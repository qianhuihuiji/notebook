### 本节说明
* 对应第 2 小节：Finding The Documentation Page

### 本节内容

通常而言，当你阅读一个 `laravel` 项目时，最初的起点应该是 `web.php`：
```
<?php

/**
 * Set the default documentation version...
 */
if (! defined('DEFAULT_VERSION')) {
    define('DEFAULT_VERSION', '5.8');
}

Route::get('/', function () {
    return view('marketing');
});
.
.
```

让我们来看看以上这段代码，第一段定义了一个常量，作用暂时不明。从第二段代码我们知道，当我们访问 [laravel.com.test](http://laravel.com.test) 时，我们看到的是 `marketing.blade.php` 视图文件渲染出来的网页：
![file](https://iocaffcdn.phphub.org/uploads/images/201903/05/19192/UfeBBjfTKZ.png!large)

我们继续看 `web.php` 的内容：

```
.
.
Route::get('docs', 'DocsController@showRootPage');
Route::get('docs/{version}/{page?}', 'DocsController@show');
.
.
```

从第一段我们可以知道，当我们访问 [laravel.com/docs](http://laravel.com.test/docs) 时，请求会由 `DocsController` 控制器的 `showRootPage` 方法处理。我么来看看这个方法：

```
public function showRootPage()
{
    return redirect('docs/'.DEFAULT_VERSION);
}
```

这里出现了一个常量 `DEFAULT_VERSION`，这正是我们在`web.php`中定义的。于是我们就知道了，当我们访问 [laravel.com/docs](http://laravel.com.test/docs) 时，`showRootPage`方法会将我们重定向到 [laravel.com/docs/5.8](http://laravel.com/docs/5.8)。而这正是第二段代码定义的路由形式，于是我们继续查看 `DocsController` 控制器的 `show`方法：

```
public function show($version, $page = null)
{
    if (! $this->isVersion($version)) {
        return redirect('docs/'.DEFAULT_VERSION.'/'.$version, 301);
    }

    if (! defined('CURRENT_VERSION')) {
        define('CURRENT_VERSION', $version);
    }

    $sectionPage = $page ?: 'installation';
    $content = $this->docs->get($version, $sectionPage);

    if (is_null($content)) {
        return response()->view('docs', [
            'title' => 'Page not found',
            'index' => $this->docs->getIndex($version),
            'content' => view('partials.doc-missing'),
            'currentVersion' => $version,
            'versions' => Documentation::getDocVersions(),
            'currentSection' => '',
            'canonical' => null,
        ], 404);
    }

    $title = (new Crawler($content))->filterXPath('//h1');

    $section = '';

    if ($this->docs->sectionExists($version, $page)) {
        $section .= '/'.$page;
    } elseif (! is_null($page)) {
        return redirect('/docs/'.$version);
    }

    $canonical = null;

    if ($this->docs->sectionExists(DEFAULT_VERSION, $sectionPage)) {
        $canonical = 'docs/'.DEFAULT_VERSION.'/'.$sectionPage;
    }

    return view('docs', [
        'title' => count($title) ? $title->text() : null,
        'index' => $this->docs->getIndex($version),
        'content' => $content,
        'currentVersion' => $version,
        'versions' => Documentation::getDocVersions(),
        'currentSection' => $section,
        'canonical' => $canonical,
    ]);
}
```

喔，长长一大段代码！但是我们粗略浏览下，大体上是做了一番操作，然后渲染了一个视图。接下来我们来逐段进行阅读：

```
if (! $this->isVersion($version)) {
    return redirect('docs/'.DEFAULT_VERSION.'/'.$version, 301);
}
```

我们知道，我们的路由形式为 `/docs/{version}/{page?}`，第二个路由参数就是 `$version` 变量。然后上面的代码会判断路由片段里面传入的 `$version` 是否是合法，我们跳到 `isVersion()` 方法：

```
protected function isVersion($version)
{
    return array_key_exists($version, Documentation::getDocVersions());
}
```

继续查看 `getDocVersions()` 方法：

```
public static function getDocVersions()
{
    return [
        'master' => 'Master',
        '5.8' => '5.8',
        '5.7' => '5.7',
        '5.6' => '5.6',
        '5.5' => '5.5',
        '5.4' => '5.4',
        '5.3' => '5.3',
        '5.2' => '5.2',
        '5.1' => '5.1',
        '5.0' => '5.0',
        '4.2' => '4.2',
    ];
}
```

现在第一段代码的含义已经明晰了，我们定义了一个有效 `$version` 的数组，并对传入的 `$version` 变量进行检查。如果包含在有效数组内，则放行；否则，进行重定向。例如，如果我们访问 `http://laravel.com.test/docs/8.6` 则会重定向到`http://laravel.com.test/docs/5.8/8.6`(当然，此时是 404 返回)。需要注意地是，这段代码还有一个作用，就是当我们不带版本进行访问时，例如 `http://laravel.com.test/docs/installation`，我们会被重定向到 `http://laravel.com.test/docs/5.8/installation` ，因为此时的 `$verson = installation`，不是一个有效的版本。

下一段代码：

```
if (! defined('CURRENT_VERSION')) {
    define('CURRENT_VERSION', $version);
}
```
这段代码一见就知，是用来定义“当前版本”，即常量 `CURRENT_VERSION`。这是为了记住我们当前浏览的版本号，我们来 `dd(CURRENT_VERSION)` 来验证：

![file](https://iocaffcdn.phphub.org/uploads/images/201903/06/19192/5MhUDAxp8g.png!large)

让我们继续前进，看下一段代码：

```
$sectionPage = $page ?: 'installation';
$content = $this->docs->get($version, $sectionPage);
```

在路由中，`$page` 参数是可选的，所以当我们不带 `$page` 参数进行访问时，我们会默认访问 `installation` 页面。然后我们根据 `$version`、`$page` 参数来获取页面内容。从构造函数我们可以知道 `$this->docs` 就是 `Documentation` 类的一个实例，让我们来看看 `get()` 方法：

```
public function get($version, $page)
{
    return $this->cache->remember('docs.'.$version.'.'.$page, 5, function () use ($version, $page) {
        $path = base_path('resources/docs/'.$version.'/'.$page.'.md');

        if ($this->files->exists($path)) {
            return $this->replaceLinks($version, markdown($this->files->get($path)));
        }

        return null;
    });
}
```

`Laravel` 的缓存系统会将我们访问的页面以 `docs.5.6.installation` 为 `key` 缓存 5 分钟，缓存的内容会在回调函数中返回。在回调函数中，我们通过 `Laravel` 的文件系统得到 `resources/docs/5.6/installation.md` 文件，然后进行缓存。我们将在下一节深入回调函数，看看到底发生了什么。
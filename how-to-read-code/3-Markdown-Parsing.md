### 本节说明
* 对应第 3 小节：Markdown Parsing

### 本节内容

我们接着上一节的进度，继续看 `get()` 函数：
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

我们来聚焦以下代码：

```
if ($this->files->exists($path)) {
    return $this->replaceLinks($version, markdown($this->files->get($path)));
}
```

首先，`$this->files` 是从构造函数注入的 `FileSystem` 的一个实例，`$this->files->exists($path)` 能够判断给定路径的文件是否存在，`$this->files->get($path)`  获取给定路径的文件的内容。然后我们注意到 `markdown()` 函数，这是一个全局辅助函数。我们全局搜索 `markdown()` 函数，就能在 `bootstrap/helpers.php` 文件中发现它的定义：

```
function markdown($text)
{
    return (new ParsedownExtra)->text($text);
}
```

看来我们需要探究 `ParsedownExtra` 了：

```
<?php

#
#
# Parsedown Extra
# https://github.com/erusev/parsedown-extra
#
# (c) Emanuil Rusev
# http://erusev.com
#
# For the full license information, view the LICENSE file that was distributed
# with this source code.
#
#

class ParsedownExtra extends Parsedown
{
    .
    .
}
```

我们可以去 GitHub 上深入研究它，但是此时我们已经知道，`markdown()` 函数的作用了：将给定的 `Markdown` 格式的文本转换为 `Html` 格式的文本。

接下来我们再看看 `replaceLinks()` 的定义：


```
public static function replaceLinks($version, $content)
{
    return str_replace('{{version}}', $version, $content);
}
```

只是简单的做了字符串替换，我们先来 `dd()` 出来替换前的文本：

![file](https://iocaffcdn.phphub.org/uploads/images/201903/12/19192/YLDkkrZxDq.png!large)

然后再 `dd()` 出来替换后的文本：

![file](https://iocaffcdn.phphub.org/uploads/images/201903/12/19192/ljIUbIEqZl.png!large)

这样我们就在页面获取到了正确版本的链接地址了。至此，我们已经知道当我们访问路由时，应用是如何处理请求，如何渲染页面了，下一节我们继续前进。
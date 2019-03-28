### 本节说明
* 对应第 4 小节：Rendering The Documentation

### 本节内容

我们接着上一节的进度，继续看 `show()` 方法：

```
.
.
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
```

接下来的一段代码很容易理解，当我们所请求的 `$version` 跟 `$sectionPage` 对应的 `markdown` 文件不存在时，`$content` 变量的值为 `null`,此时系统会渲染 `docs` 视图，并引入 `partials.doc-missing` 视图，此视图会显示内容不存在。
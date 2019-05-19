### 本节说明

* 对应第 2 小节：Counters and Namespacing

### 本节内容

上一节我们学习了 `INCR` 命令，每次递增 1。同时我们也可以使用 `INCRBY` 指定递增的数量：

```
$visits = Redis::incrBy('visits',5);
```

我们知道，`Redis` 是一个 `key-value` 的数据库，所以如何定义 `key` 是一个需要注意的问题。在 `PHP` 中我们有命名空间的概念，在定义 `key` 时我们可以参照它，利用 `.` 符号来进行命名的划分。例如我们想要统计视频的下载量：

*routes/web.php*

```
<?php

Route::get('/videos/{id}', function ($id) {
    $downloads = Redis::get("visits.$id.download");

    return view('welcome')->withDownloads($downloads);
});

Route::get('/videos/{id}/download', function ($id) {
    $downloads = Redis::incr("visits.$id.download");

    return redirect("/videos/$id");
});

```

*resources/views/welcome.blade.php*

```
<!doctype html>
<html lang="{{ str_replace('_', '-', app()->getLocale()) }}">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">

        <title>Laravel</title>

    </head>
    <body>
        <h1>Some Videos</h1>
        
        <p>
            This video has been downloaded {{ $downloads ?? 'no' }} times.
        </p>
    </body>
</html>

```

我们可以进入命令行查看这些 `key`：

```
redis-cli
keys videos.*
```

会显示如下：

```
1) "visits.20.download"
2) "visits.1.download"
```


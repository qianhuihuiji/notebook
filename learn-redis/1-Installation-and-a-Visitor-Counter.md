### 本节说明

* 对应第 1 小节：Installation and a Visitor Counter

### 本节内容

[Redis](https://redis.io) 是一个开源的，高级键值对存储数据库。由于它包含 [字符串](https://redis.io/topics/data-types#strings), [哈希](https://redis.io/topics/data-types#hashes), [列表](https://redis.io/topics/data-types#lists), [集合](https://redis.io/topics/data-types#sets), 和 [有序集合](https://redis.io/topics/data-types#sorted-sets) 这些数据类型，所以它通常被称为数据结构服务器。

本系列我们通过一个全新的 `Laravel` 项目来学习 `Redis` 的相关知识。首先我们新建项目：

```
laravel new learn-redis
```

在使用 `Laravel` 的 `Redis` 之前，你需要通过 `Composer` 安装 `predis/predis` 扩展包：

```
cd learn-redis
composer require predis/predis
```

`Laravel` 提供了 *Illuminate\Support\Facades\Redis* 的 Facades 来让我们和 `Redis` 进行交互。本节我们简单使用 `incr` 命令来统计页面访问数量：

*routes/web.php*

```
<?php

Route::get('/', function () {
    $visits = Redis::incr('visits');

    return view('welcome')->with(['visits' => $visits]);
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
        You are the #{{ $visits }} visitor.
    </body>
</html>

```

现在我们每次访问页面都会使得访问数自增。

>注1：有关 `Redis` 的命令大全，可以去 `Redis` [官网](https://redis.io/commands) 查看。

>注2：可以使用 `redis-cli` 命令行直接与 `Redis` 进行交互，但是需要注意的一点是，`Laravel` 默认会给我们定义的 `key` 加上前缀。由于我们是学习 `Redis`，所以我们来设置不加前缀：

*config/database.php*

```
.
'redis' => [

    'client' => env('REDIS_CLIENT', 'predis'),

    'options' => [
        'cluster' => env('REDIS_CLUSTER', 'predis'),
        'prefix' => null,
    ],
.
.
```



### 本节说明

* 对应第 5 小节：Caching With Redis

### 本节内容

上一节我们谈到了利用 `Redis` 做缓存的驱动，本节我们继续探索。首先我们来看下面一段代码：

*routes/web.php*

```
<?php

use Illuminate\Support\Facades\Redis;

Route::get('/',function () {
    if($value = Redis::get('articles.all')) {
        return json_decode($value);
    }

    $articles = \App\Article::all();

    Redis::setex('articles.all',60,$articles);

    return $articles;
});
```

上面的代码逻辑非常简单，访问网站时，先从 `Redis` 中尝试获取数据，如果没有，则从数据库中获取，同时储存到 `Redis` 中。`Redis::setex()` 函数可以让你设置缓存获取的时间，单位是 `s`。

访问网站之后可以从命令行查看值：

```
127.0.0.1:6379> GET articles.all
"[{\"id\":21,\"title\":\"Veritatis accusamus sit nobis quae.\",\"created_at\":\"2019-05-20 03:25:02\",\"updated_at\":\"2019-05-20 03:25:02\"},{\"id\":22,\"title\":\"Quasi recusandae odit beatae dicta debitis assumenda.\",\"created_at\":\"2019-05-20 03:25:02\",\"updated_at\":\"2019-05-20 03:25:02\"},{\"id\":23,\"title\":\"Corporis minima dolores molestiae.\",\"created_at\":\"2019-05-20 03:25:02\",\"updated_at\":\"2019-05-20 03:25:02\"},{\"id\":24,\"title\":\"Aut illum sed voluptate adipisci natus quam.\",\"created_at\":\"2019-05-20 03:25:02\",\"updated_at\":\"2019-05-20 03:25:02\"},{\"id\":25,\"title\":\"Nam asperiores quaerat quos explicabo.\",\"created_at\":\"2019-05-20 03:25:02\",\"updated_at\":\"2019-05-20 03:25:02\"},{\"id\":26,\"title\":\"Adipisci aspernatur exercitationem doloremque iusto veritatis commodi culpa.\",\"created_at\":\"2019-05-20 03:25:02\",\"updated_at\":\"2019-05-20 03:25:02\"},{\"id\":27,\"title\":\"Deserunt sint quaerat alias iusto.\",\"created_at\":\"2019-05-20 03:25:02\",\"updated_at\":\"2019-05-20 03:25:02\"},{\"id\":28,\"title\":\"Provident temporibus quos sunt ut eos.\",\"created_at\":\"2019-05-20 03:25:02\",\"updated_at\":\"2019-05-20 03:25:02\"},{\"id\":29,\"title\":\"Amet et expedita atque aperiam et aliquid.\",\"created_at\":\"2019-05-20 03:25:02\",\"updated_at\":\"2019-05-20 03:25:02\"},{\"id\":30,\"title\":\"Consectetur quas exercitationem reiciendis.\",\"created_at\":\"2019-05-20 03:25:02\",\"updated_at\":\"2019-05-20 03:25:02\"},{\"id\":31,\"title\":\"Tempora quia facilis sit labore numquam et.\",\"created_at\":\"2019-05-20 03:25:02\",\"updated_at\":\"2019-05-20 03:25:02\"},{\"id\":32,\"title\":\"Quis officia dolores id.\",\"created_at\":\"2019-05-20 03:25:02\",\"updated_at\":\"2019-05-20 03:25:02\"},{\"id\":33,\"title\":\"Eius ipsa quia dignissimos omnis.\",\"created_at\":\"2019-05-20 03:25:02\",\"updated_at\":\"2019-05-20 03:25:02\"},{\"id\":34,\"title\":\"Quas maiores nihil dignissimos quos necessitatibus.\",\"created_at\":\"2019-05-20 03:25:02\",\"updated_at\":\"2019-05-20 03:25:02\"},{\"id\":35,\"title\":\"Ut quidem repellendus eos quo.\",\"created_at\":\"2019-05-20 03:25:02\",\"updated_at\":\"2019-05-20 03:25:02\"},{\"id\":36,\"title\":\"Numquam voluptatem id quod ut iusto et repudiandae.\",\"created_at\":\"2019-05-20 03:25:02\",\"updated_at\":\"2019-05-20 03:25:02\"},{\"id\":37,\"title\":\"Commodi minus vel impedit blanditiis quisquam consequatur.\",\"created_at\":\"2019-05-20 03:25:02\",\"updated_at\":\"2019-05-20 03:25:02\"},{\"id\":38,\"title\":\"Et quia et cumque autem accusamus nostrum totam.\",\"created_at\":\"2019-05-20 03:25:02\",\"updated_at\":\"2019-05-20 03:25:02\"},{\"id\":39,\"title\":\"Voluptatibus nobis ex occaecati reprehenderit omnis dolor.\",\"created_at\":\"2019-05-20 03:25:02\",\"updated_at\":\"2019-05-20 03:25:02\"},{\"id\":40,\"title\":\"Quasi aut eligendi ut dicta eaque.\",\"created_at\":\"2019-05-20 03:25:02\",\"updated_at\":\"2019-05-20 03:25:02\"}]"
```

接下来我们做点重构，将缓存的逻辑抽取出来：

*routes/web.php*

```
 <?php

use Illuminate\Support\Facades\Redis;

function remember($key, $seconds, $callback)
{
    if ($value = Redis::get($key)) {
        return json_decode($value);
    }

    Redis::setex($key, $seconds, $value = $callback());

    return $value;
}

Route::get('/', function () {
    return remember('articles.all',60,function () {
        return \App\Article::all();
    });
});
```

如果你对 `Laravel` 的缓存机制很熟悉的话，你就会发现上面的代码的处理其实就是 `Laravel` 提供的 `Cache::remember()` 方法做的事情。现在我们深入研究下 `Cache::remember()` 方法。首先我们找到 `Cache::remember()` 方法：
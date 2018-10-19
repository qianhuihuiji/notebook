### 本节说明
* 对应第 5 小节：Testing Eloquent Models

### 本节内容
本节我们来学习如何为测试环境配置不同的数据库，以便于让我们分离开发跟测试流程。需要说明的是，在 Laravel 5.7 的版本中，默认的数据库连接方式是`mysql`，但是我们仍然可以使用`sqlite`数据库进行开发跟测试，假如你没有安装`mysql`的服务或者其他什么原因的话。首先我们需要修改`.env`文件：

```

```
我们删除了以下形如`DB_XXXX`的配置信息：
```
DB_CONNECTION=mysql
DB_HOST=127.0.0.1
DB_PORT=3306
DB_USERNAME=homestead
DB_PASSWORD=secret
```
然后我们需要修改数据库配置文件：

*config\database.php*
```
<?php

return [

    'default' => env('DB_CONNECTION', 'sqlite'),

    'connections' => [

        'sqlite' => [
            'driver' => 'sqlite',
            'database' => env('DB_DATABASE', database_path('database.sqlite')),
            'prefix' => '',
        ],

        'sqlite_testing' => [
            'driver' => 'sqlite',
            'database' => env('DB_DATABASE', database_path('database_testing.sqlite')),
            'prefix' => '',
        ],
		.
		.
]
```

我们在前面几节的数据库使用的是`mysql`进行开发，`sqlite`进行测试，现在我们统一使用`sqlite`进行开发跟测试，但是分别使用不同的数据库。所以相应地，我们需要修改测试的配置文件：

*phpunit.xml*
```
.
.
	<php>
        <env name="APP_ENV" value="testing"/>
        <env name="DB_CONNECTION" value="sqlite_testing"/>
        <env name="BCRYPT_ROUNDS" value="4"/>
        <env name="CACHE_DRIVER" value="array"/>
        <env name="SESSION_DRIVER" value="array"/>
        <env name="QUEUE_CONNECTION" value="sync"/>
        <env name="MAIL_DRIVER" value="array"/>
    </php>
</phpunit>
```
修改成`sqlite`数据库后，你需要新建`database.sqlite`跟`database_testing.sqlite`文件，用以存储数据：
```
$ touch database/database.sqlite
$ touch database/database_testing.sqlite
```
然后运行迁移：
```
$ php artisan migrate
```
运行之后你就可以打开数据库查看表了：
![file](https://lccdn.phphub.org/uploads/images/201810/19/19192/L9C5GvNnqB.png?imageView2/2/w/1240/h/0)
现在运行全部测试：
![file](https://lccdn.phphub.org/uploads/images/201810/19/19192/cm2Bo6OIKI.png?imageView2/2/w/1240/h/0)

但是，本节的目的在于仅仅了解不同的数据库是如何设置的。所以我们仍然会延续前面几节的配置进行开发跟测试，所以本节的修改需要取消。

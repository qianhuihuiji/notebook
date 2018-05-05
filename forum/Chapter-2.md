### 0.写在前面
* 本系列文章为`laracasts.com` 的系列视频教程——[Let's Build A Forum with Laravel and TDD](https://laracasts.com/series/lets-build-a-forum-with-laravel) 的学习笔记。若喜欢该系列视频，可去该网站订阅后下载该系列视频，*** 支持正版 ***。
* 视频源码地址：[https://github.com/laracasts/Lets-Build-a-Forum-in-Laravel](http://https://github.com/laracasts/Lets-Build-a-Forum-in-Laravel)
* 本项目为一个 forum（论坛）项目，与本站的第二本实战教程 [Laravel 教程 - Web 开发实战进阶 ( Laravel 5.5 )](https://laravel-china.org/courses/laravel-intermediate-training-5.5) 类似，可互相参照
* 项目开发模式为`TDD`开发，教程简介为：
> A forum is a deceptively complex thing. Sure, it's made up of threads and replies, but what else might exist as part of a forum? What about profiles, or thread subscriptions, or filtering, or real-time notifications? As it turns out, a forum is the perfect project to stretch your programming muscles. In this series, we'll work together to build one with tests from A to Z.
* 项目版本为`laravel 5.4`，教程后面会进行升级到`laravel 5.5`的教学
* 视频教程共计 102 个小节，笔记章节与视频教程一一对应

### 1.本节说明
* 对应视频第 2 小节：Testing Drving Threads

### 2.本节内容
#### 第一个功能测试

测试环境：
> 在运行测试时，Laravel 会根据 phpunit.xml 文件中设定好的环境变量自动将环境变量设置为 testing，并将 Session 及缓存以 array 的形式存储，也就是说在测试时不会持久化任何 Session 或缓存数据。
> 
你可以随意创建其它必要的测试环境配置。testing 环境的变量可以在 phpunit.xml 文件中被修改，但是在运行测试之前，请确保使用 config:clear Artisan 命令来清除配置信息的缓存。

以上是 [Laravel 5.5 中文文档](https://laravel-china.org/docs/laravel/5.5) 中对 [Laravel 测试](https://laravel-china.org/docs/laravel/5.5/testing) 的简介，详细内容参见文档 。
现在来建立第一个简单的功能测试：`a_user_can_browse_theads`。首先重命名`tests\Feature\ExampleTest.php`为`ThreadsTest.php`，修改`phpunit.xml`文件，配置测试环境：
```
.
.
<php>
	<env name="APP_ENV" value="testing"/>
	<env name="DB_CONNECTION" value="sqlite"/>
	<env name="DB_DATABASE" value=":memory:"/>
	<env name="CACHE_DRIVER" value="array"/>
	<env name="SESSION_DRIVER" value="array"/>
	<env name="QUEUE_DRIVER" value="sync"/>
	<env name="MAIL_DRIVER" value="array"/>
</php>
.
.
```
编写测试方法：
```php
<?php

namespace Tests\Feature;

use Tests\TestCase;
use Illuminate\Foundation\Testing\DatabaseMigrations;

class ThreadsTest extends TestCase
{
    use DatabaseMigrations;

    /** @test */
    public function a_user_can_browse_threads()
    {
        $response = $this->get('/threads');
        
        $response->assertStatus(200);
    }
}

```
找到`Illuminate\Foundation\Testing\DatabaseMigrations.php`文件：
```php
<?php

namespace Illuminate\Foundation\Testing;

use Illuminate\Contracts\Console\Kernel;

trait DatabaseMigrations
{
    /**
     * Define hooks to migrate the database before and after each test.
     *
     * @return void
     */
    public function runDatabaseMigrations()
    {
        $this->artisan('migrate');

        $this->app[Kernel::class]->setArtisan(null);

        $this->beforeApplicationDestroyed(function () {
            $this->artisan('migrate:rollback');
        });
    }
}

```
可以看到，每次在进行测试的时候，都会执行`php artisan migrate`命令初始化数据库；每次执行完测试，都会执行`php artisan migrate:rollback`命令重置数据库。
现在执行命令运行测试：
```
$ phpunit
```
会发现有报错，这是理所应该的，因为目前我们还未设置路由。前往`web.php`文件添加路由配置：
```php
Route::get('/threads','ThreadsController@index');
```
前往`ThreadsController.php`添加`index`方法：
```php
public function index()
{
	$threads = Thread::latest()->get();

	return view('threads.index',compact('threads'));
}
```

> 
注：`latest` 和 `oldest` 方法允许你轻松地按日期对查询结果排序。默认情况下是对 created_at 字段进行排序。或者，你可以传递你想要排序的字段名称：
```php
$user = DB::table('users')
                ->latest()
                ->first();
```

新建视图文件`resources/views/threads/index.blade.php`：
```php
@extends('layouts.app')

@section('content')
    <div class="container">
        <div class="row">
            <div class="col-md-8 col-md-offset-2">
                <div class="panel panel-default">
                    <div class="panel-heading">forum Threads</div>

                    <div class="panel-body">
                        [@foreach](https://laravel-china.org/users/5651) ($threads as $thread)
                            <article>
                                <a href="/threads/{{ $thread->id }}">
                                    <h4>{{ $thread->title }}</h4>
                                </a>
                                <div class="body">{{ $thread->body }}</div>
                            </article>

                            <hr>
                        @endforeach
                    </div>
                </div>
            </div>
        </div>
    </div>
@endsection

```
顺手生成一下`Laravel` 自带的注册登录功能：
```
$ php artisan make:auth
```
再次执行命令即可成功运行：
![file](https://lccdn.phphub.org/uploads/images/201804/19/19192/R52gnMIJlQ.png?imageView2/2/w/1240/h/0)
即意味着访问 [http://forum.test/threads](http://forum.test/threads) ，可以看到：
![file](https://lccdn.phphub.org/uploads/images/201804/19/19192/VZEy1hhfd6.png?imageView2/2/w/1240/h/0)


此时的功能测试仅仅代表可以访问该路由，并未达到功能测试的要求。接下来 编写真正的测试逻辑：
```php
public function a_user_can_browse_threads()
{
	$thread = factory('App\Thread')->create();

	$response = $this->get('/threads');

	$response->assertSee($thread->title);
}
```
运行测试：
```
$ phpunit
```
测试通过：
![file](https://lccdn.phphub.org/uploads/images/201804/19/19192/WAIw2e5sdc.png?imageView2/2/w/1240/h/0)


此时如果我们去掉`index.blade.php`视图中`title`属性，则应该测试失败：
```php
.
.
<div class="panel-body">
	[@foreach](https://laravel-china.org/users/5651)($threads as $thread)
		<article>
			// 
		</article>

		<hr>
	@endforeach
.
.
```
运行测试，发现测试失败：
![file](https://lccdn.phphub.org/uploads/images/201804/19/19192/UgbyacLKsk.png?imageView2/2/w/1240/h/0)
证明我们的测试有效。
继续编写测试，测试单个`thread`：
```php
public function a_user_can_browse_threads()
{
	$thread = factory('App\Thread')->create();

	$response = $this->get('/threads');
	$response->assertSee($thread->title);

	$response = $this->get('/threads/' . $thread->id);
	$response->assertSee($thread->title);
}
```
运行测试依然会失败，因为还未添加路由、控制器方法跟视图。
修改`web.php`：
```
.
.
Route::get('/threads','ThreadsController@index');
Route::get('/threads/{thread}','ThreadsController@show');
```
修改`ThreadsController.php`：
```php
.
.
public function show(Thread $thread)
{
    return view('threads.show',compact('thread'));
}
.
.
```
新建`resources/views/threads/show.blade.php`：
```php
@extends('layouts.app')

@section('content')
    <div class="container">
        <div class="row">
            <div class="col-md-8 col-md-offset-2">
                <div class="panel panel-default">
                    <div class="panel-heading">
                        {{ $thread->title }}
                    </div>

                    <div class="panel-body">
                        {{ $thread->body }}
                    </div>
                </div>
            </div>
        </div>
    </div>
@endsection

```
再次测试，测试成功：
![file](https://lccdn.phphub.org/uploads/images/201804/19/19192/rRxBDYnpeO.png?imageView2/2/w/1240/h/0)
此时访问 [http://forum.test/threads/1](http://forum.test/threads/1) ：
![file](https://lccdn.phphub.org/uploads/images/201804/19/19192/iQMrnoDlf6.png?imageView2/2/w/1240/h/0)

此时我们的测试是放在一个方法中的，不但可读性不高，并且在功能测试不通过时难以准确定位，于是将`a_user_can_browse_threads`测试拆分成`a_user_can_view_all_threads`和`a_user_can_read_single_thread`两个测试：
```php
use DatabaseMigrations;

/** @test */
public function a_user_can_view_all_threads()
{
	$thread = factory('App\Thread')->create();

	$response = $this->get('/threads');
	$response->assertSee($thread->title);
}

/** @test */
public function a_user_can_read_a_single_thread()
{
	$thread = factory('App\Thread')->create();

	$response = $this->get('/threads/' . $thread->id);
	$response->assertSee($thread->title);
}
```
运行测试，成功通过测试：
![file](https://lccdn.phphub.org/uploads/images/201804/19/19192/NYeBajZwDK.png?imageView2/2/w/1240/h/0)
此时在我们的视图文件中，我们采用的是使用`url`的方式给文章标题附上超链接。可是这种方法可读性差且不利于维护，现在进行修改。
首先在`app\Thread.php`模型中新增`path`方法，用来获取链接：
```php
.
.
public function path()
{
    return '/threads/'.$this->id;
}
.

```
接着修改视图文件`index.blade.php`：
```php
.
.
<article>
   <a href="{{ $thread->path() }}">
       <h4>{{ $thread->title }}</h4>
    </a>
    <div class="body">{{ $thread->body }}</div>
</article>
.
.
```
### 3.笔记心得
* `注 1 `：
页首的样式可以使用 [Laravel 教程 - Web 开发实战进阶 ( Laravel 5.5 ) ](https://laravel-china.org/courses/laravel-intermediate-training-5.5/640/base-layout) 教程中的样式，运行` Laravel Mix`可以参考参考课程下的讨论：
[https://laravel-china.org/topics/7619/resolved-laravel-mix-still-try-installing-heavy-please-help-look-laoniao-thank-you](https://laravel-china.org/topics/7619/resolved-laravel-mix-still-try-installing-heavy-please-help-look-laoniao-thank-you)
> ![file](https://lccdn.phphub.org/uploads/images/201804/19/19192/texuxJx2i4.png?imageView2/2/w/1240/h/0)

* 首次接触`TDD`实战，期待看看能带来什么 :smile:

### 4.写在后面
* 如有建议或意见，欢迎指出~
* 如果觉得文章写的不错，请点赞鼓励下哈，你的鼓励将是我的动力！
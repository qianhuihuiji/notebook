## 0.写在前面

* 本系列文章为`laracasts.com` 的系列视频教程——[Let's Build A Forum with Laravel and TDD](https://laracasts.com/series/lets-build-a-forum-with-laravel) 的学习笔记。若喜欢该系列视频，可去该网站订阅后下载该系列视频，*** 支持正版 ***。
* 视频源码地址：[https://github.com/laracasts/Lets-Build-a-Forum-in-Laravel](http://https://github.com/laracasts/Lets-Build-a-Forum-in-Laravel)
* *本项目为一个 forum（论坛）项目，与本站的第二本实战教程 [Laravel 教程 - Web 开发实战进阶 ( Laravel 5.5 )](https://laravel-china.org/courses/laravel-intermediate-training-5.5) 类似，可互相参照
* 项目开发模式为`TDD`开发，教程简介为：
> A forum is a deceptively complex thing. Sure, it's made up of threads and replies, but what else might exist as part of a forum? What about profiles, or thread subscriptions, or filtering, or real-time notifications? As it turns out, a forum is the perfect project to stretch your programming muscles. In this series, we'll work together to build one with tests from A to Z.
* 项目版本为`laravel 5.4`，教程后面会进行升级到`laravel 5.5`的教学
* 视频教程共计 102 个小节，某些小节内容较少的话会合并到一条学习笔记当中

### 1.本节说明
* 对应视频第 8 小节：The Exception Handling Conundrum

### 2.本节内容
目前我们有关`Thread`的路由为：
`web.php`
```
.
.
Route::get('/threads','ThreadsController@index');
Route::post('/threads','ThreadsController@store');
Route::get('/threads/{thread}','ThreadsController@show');
.
.
```
对于`Thread`，我们有完整的`CURD`操作，可以将`Thread`视作一个资源。
> Laravel 遵从 RESTful 架构的设计原则，将数据看做一个资源，由 URI 来指定资源。对资源进行的获取、创建、修改和删除操作，分别对应 HTTP 协议提供的 GET、POST、PATCH 和 DELETE 方法。当我们要查看一个 id 为 1 的用户时，需要向 /users/1 地址发送一个 GET 请求，当 Laravel 的路由接收到该请求时，默认会把该请求传给控制器的 show 方法进行处理。

因此我们将与`Thread`相关的路由简写为:
```
Route::resource('threads','ThreadsController');
```
以上代码等同于：
```
Route::get('/threads', 'threadsController@index')->name('threads.index');
Route::get('/threads/{thread}', 'threadsController@show')->name('threads.show');
Route::get('/threads/create', 'threadsController@create')->name('threads.create');
Route::post('/threads', 'threadsController@store')->name('threads.store');
Route::get('/threads/{thread}/edit', 'threadsController@edit')->name('threads.edit');
Route::patch('/threads/{thread}', 'threadsController@update')->name('threads.update');
Route::delete('/threads/{thread}', 'threadsController@destroy')->name('threads.destroy');
```
运行测试：
```
$ APP_ENV=testing phpunit
```
![file](https://lccdn.phphub.org/uploads/images/201804/30/19192/fIq5zqsaPh.png?imageView2/2/w/1240/h/0)
在之前的章节中，我们为新建`thread`编写了测试代码，且成功通过。现在让我们完成此功能的代码：
`\app\Http\Controllers\ThreadsController.php`
```
.
.
public function create()
{
	return view('threads.create');
}
.
.
```
`\resources\views\threads\create.blade.php`
```
@extends('layouts.app')

@section('content')
    <div class="container">
        <div class="row">
            <div class="col-md-8 col-md-offset-2">
                <div class="panel panel-default">
                    <div class="panel-heading">Create a New Thread</div>

                    <div class="panel-body">
                        <form method="post" action="/threads">
                            {{ csrf_field() }}

                            <div class="form-group">
                                <label for="title">Title</label>
                                <input type="text" class="form-control" id="title" name="title">
                            </div>

                            <div class="form-group">
                                <label for="body">Body</label>
                                <textarea name="body" id="body" class="form-control" rows="8"></textarea>
                            </div>

                            <button type="submit" class="btn btn-primary">Publish</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
@endsection
```
我们在编写代码之前就为`thread`的新建动作编写了测试，且成功通过，这意味着我们发布`thread`是不会遇到什么功能上的问题的：
![file](https://lccdn.phphub.org/uploads/images/201804/30/19192/m4s7MdU2ED.png?imageView2/2/w/1240/h/0)
![file](https://lccdn.phphub.org/uploads/images/201804/30/19192/ciD6EmqcIT.png?imageView2/2/w/1240/h/0)
![file](https://lccdn.phphub.org/uploads/images/201804/30/19192/iBKDH1DBVW.png?imageView2/2/w/1240/h/0)
在当前情况下，我们在`ThreadsController`的`__construct`方法中使用了白名单机制`only`来做权限控制，但这样不太安全。我们现在改用黑名单机制，即除了`index`,`show`方法，其他方法都需要进行登录才能操作。
`\app\Http\Controllers\ThreadsController.php`
```
class ThreadsController extends Controller
{
    public function __construct()
    {
        $this->middleware('auth')->except(['index','show']);
    }
.
.
```
我们来编写一个功能测试，用来测试未登录用户访问 [http://forum.test/threads/create](http://forum.test/threads/create) 页面。测试逻辑应为：用户访问页面，如未登录，重定向到 [登录页面](http://forum.test/login) 。
`\tests\Feature\CreateThreadsTest.php`
```
.
.
/** @test */
public function guests_may_not_see_the_create_thread_page()
{
	$this->get('/threads/create')
		->assertRedirect('/login');
}
.
.
```
让我们来运行一下测试：
```

```
![file](https://lccdn.phphub.org/uploads/images/201804/30/19192/tqj8etTG2D.png?imageView2/2/w/1240/h/0)
抛出了`Unauthenticated`的异常，这与我们的测试初衷不符。我们曾在`Handler.php`做过设置，如果`app()->environment() === 'testing'`就会抛出异常：
`\app\Exceptions\Handler.php`
```
.
.
public function render($request, Exception $exception)
{
	if (app()->environment() === 'testing') throw $exception;

	return parent::render($request, $exception);
}
.
.
```
在类似于未登录用户访问 [http://forum.test/threads/create](http://forum.test/threads/create) 页面的测试中，我们需要放行，而不是抛出异常。现在我们来进行处理：
`\forum\tests\TestCase.php`
```
<?php

namespace Tests;

use Illuminate\Foundation\Testing\TestCase as BaseTestCase;
use Illuminate\Contracts\Debug\ExceptionHandler;
use App\Exceptions\Handler;

abstract class TestCase extends BaseTestCase
{
    use CreatesApplication;

    protected function setUp()
    {
        parent::setUp();

        $this->disableExceptionHandling();
    }

    protected function signIn($user = null)
    {
        $user = $user ?: create('App\User');

        $this->actingAs($user);

        return $this;
    }

    protected function disableExceptionHandling()
    {
        $this->oldExceptionHander = $this->app->make(ExceptionHandler::class);

        $this->app->instance(ExceptionHandler::class,new class extends Handler{
           public function __construct(){}
           public function report(\Exception $e){}
           public function render($request,\Exception $e){
               throw $e;
           }
        });
    }

    protected function withExceptionHandling()
    {
        $this->app->instance(ExceptionHandler::class,$this->oldExceptionHandler);

        return $this;
    }
}

```
> 注：不要忘了头部的引用

对于继承`TestCase`基类的测试用例，我们默认先调用`disableExceptionHandling()`方法。该方法对`Handler.php`的内容进行了重写，默认抛出异常。当我们不需要抛出异常时，继续调用`withExceptionHandling()`方法即可。
现在将`\app\Exceptions\Handler.php`中的下面这行代码删掉：
```
if (app()->environment() === 'testing') throw $exception;
```
同时在编写的测试用例中调用`withExceptionHandling()`方法：
`\tests\Feature\CreateThreadsTest.php`
```
.
.
/** @test */
public function guests_may_not_see_the_create_thread_page()
{
	$this->withExceptionHandling() // 此处调用
		->get('/threads/create')
		->assertRedirect('/login');
}
.
.
```
再次测试，测试通过：
![file](https://lccdn.phphub.org/uploads/images/201804/30/19192/4pWOPVocKD.png?imageView2/2/w/1240/h/0)

### 3.笔记心得

### 4.写在后面
* 如有建议或意见，欢迎指出~
* 如果觉得文章写的不错，请点赞鼓励下哈，你的鼓励将是我的动力！
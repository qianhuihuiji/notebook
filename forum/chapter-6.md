### 0.写在前面
* 本系列文章为`laracasts.com` 的系列视频教程——[Let's Build A Forum with Laravel and TDD](https://laracasts.com/series/lets-build-a-forum-with-laravel) 的学习笔记。若喜欢该系列视频，可去该网站订阅后下载该系列视频，*** 支持正版 ***。
* 视频源码地址：[https://github.com/laracasts/Lets-Build-a-Forum-in-Laravel](http://https://github.com/laracasts/Lets-Build-a-Forum-in-Laravel)
* 本项目为一个 forum（论坛）项目，与本站的第二本实战教程 [Laravel 教程 - Web 开发实战进阶 ( Laravel 5.5 )](https://laravel-china.org/courses/laravel-intermediate-training-5.5) 类似，可互相参照
* 项目开发模式为`TDD`开发，教程简介为：
> A forum is a deceptively complex thing. Sure, it's made up of threads and replies, but what else might exist as part of a forum? What about profiles, or thread subscriptions, or filtering, or real-time notifications? As it turns out, a forum is the perfect project to stretch your programming muscles. In this series, we'll work together to build one with tests from A to Z.
* 项目版本为`laravel 5.4`，教程后面会进行升级到`laravel 5.5`的教学
* 视频教程共计 102 个小节，笔记章节与视频教程一一对应

### 1.本节说明
* 对应视频第 6 小节：A User Can Publish Threads 

### 2.本节内容

首先新建测试文件：
```
$ php artisan make:test CreateThreadsTest
```
编写测试逻辑：
`\tests\Feature\CreateThreadsTest.php`
```php
<?php

namespace Tests\Feature;

use Tests\TestCase;
use Illuminate\Foundation\Testing\WithoutMiddleware;
use Illuminate\Foundation\Testing\DatabaseMigrations;
use Illuminate\Foundation\Testing\DatabaseTransactions;

class CreateThreadsTest extends TestCase
{
    /** @test */
    public function an_authenticated_user_can_create_new_forum_threads()
    {
        // Given we have a signed in user
        // When we hit the endpoint to cteate a new thread 
        // Then,when we visit the thread
        // We should see the new thread
    }
}

```
接着填充具体代码：
```
<?php

namespace Tests\Feature;

use Tests\TestCase;
use Illuminate\Foundation\Testing\WithoutMiddleware;
use Illuminate\Foundation\Testing\DatabaseMigrations;
use Illuminate\Foundation\Testing\DatabaseTransactions;

class CreateThreadsTest extends TestCase
{
    use DatabaseMigrations;

    /** @test */
    public function an_authenticated_user_can_create_new_forum_threads()
    {
        // Given we have a signed in user
        $this->actingAs(factory('App\User')->create());  // 已登录用户

        // When we hit the endpoint to cteate a new thread
        $thread = factory('App\Thread')->make();
        $this->post('/threads',$thread->toArray());

        // Then,when we visit the thread
        // We should see the new thread
        $this->get($thread->path())
            ->assertSee($thread->title)
            ->assertSee($thread->body);
    }
}

```

> `注1`：关于`create()`，`make()`，`rw()`三种方法的比较，详见`笔记心得`处。

运行测试：
```
$ APP_ENV=testing phpunit --filter an_authenticate
```
测试未通过：
![file](https://lccdn.phphub.org/uploads/images/201804/28/19192/OGwBgIOv4u.png?imageView2/2/w/1240/h/0)
因为还未添加路由，前往添加：
`web.php`
```
.
.
Route::get('/home', 'HomeController@index')->name('home');
Route::get('/threads','ThreadsController@index');
Route::post('/threads','ThreadsController@store');  -->此处添加路由
Route::get('/threads/{thread}','ThreadsController@show');
Route::post('/threads/{thread}/replies','RepliesController@store');
```
修改`store`方法：
`\app\Http\Controllers\ThreadsController.php`
```php
.
.
public function store(Request $request)
{
	$thread = Thread::create([
		'user_id' => auth()->id(),
		'title' => request('title'),
		'body' => request('body'),
	]);

	return redirect($thread->path());
}
.
.
```
再次测试，测试通过：
![file](https://lccdn.phphub.org/uploads/images/201804/28/19192/WreYh2IG8D.png?imageView2/2/w/1240/h/0)
接下来我们可以编写测试，未登录用户不能添加`thread`：
`\tests\Feature\CreateThreadsTest.php`
```
class CreateThreadsTest extends TestCase
{
    use DatabaseMigrations;

    /** @test */
    public function guests_may_not_create_threads()
    {
        $thread = factory('App\Thread')->make();
        $this->post('/threads',$thread->toArray());
    }
	.
	.
```
此时如果我们运行测试的话：
![file](https://lccdn.phphub.org/uploads/images/201804/28/19192/ip9TRoWrzX.png?imageView2/2/w/1240/h/0)
仍然新建了`thread`，只是因为此时`auth()->id()`为`null`导致`user_id`的值为`null`，从而出现了数据库异常，这说明测试未通过。接下来修复此漏洞：
`\app\Http\Controllers\ThreadsController.php`
```
class ThreadsController extends Controller
{
    public function __construct()
    {
        $this->middleware('auth')->only('store'); // 白名单，意味着仅 store 方法需要登录
    }
```
再次测试：
![file](https://lccdn.phphub.org/uploads/images/201804/28/19192/7qzJx4U5wx.png?imageView2/2/w/1240/h/0)
正是我们希望看到的。补充完测试代码：
`\tests\Feature\CreateThreadsTest.php`
```php
/** @test */
public function guests_may_not_create_threads()
{
	$this->expectException('Illuminate\Auth\AuthenticationException'); // 在此处抛出异常即代表测试通过

	$thread = factory('App\Thread')->make();
	$this->post('/threads',$thread->toArray());
}
```
再次运行测试，测试通过：

![file](https://lccdn.phphub.org/uploads/images/201804/28/19192/3Fq2E4VAG3.png?imageView2/2/w/1240/h/0)
### 3.笔记心得
* 关于`create()`，`make()`，`raw()`三种方法的比较：`create()`方法得到一个模型实例，并保存到数据库中；`make()`方法得到一个模型实例（不保存）；`raw()`方法是得到一个模型实例转化后的数组。


### 4.写在后面
* 如有建议或意见，欢迎指出~
* 如果觉得文章写的不错，请点赞鼓励下哈，你的鼓励将是我的动力！
### 0.写在前面
* 本系列文章为`laracasts.com` 的系列视频教程——[Let's Build A Forum with Laravel and TDD](https://laracasts.com/series/lets-build-a-forum-with-laravel) 的学习笔记。若喜欢该系列视频，可去该网站订阅后下载该系列视频，*** 支持正版 ***。
* 视频源码地址：[https://github.com/laracasts/Lets-Build-a-Forum-in-Laravel](http://https://github.com/laracasts/Lets-Build-a-Forum-in-Laravel)
* *本项目为一个 forum（论坛）项目，与本站的第二本实战教程 [Laravel 教程 - Web 开发实战进阶 ( Laravel 5.5 )](https://laravel-china.org/courses/laravel-intermediate-training-5.5) 类似，可互相参照
* 项目开发模式为`TDD`开发，教程简介为：
> A forum is a deceptively complex thing. Sure, it's made up of threads and replies, but what else might exist as part of a forum? What about profiles, or thread subscriptions, or filtering, or real-time notifications? As it turns out, a forum is the perfect project to stretch your programming muscles. In this series, we'll work together to build one with tests from A to Z.
* 项目版本为`laravel 5.4`，教程后面会进行升级到`laravel 5.5`的教学
* 视频教程共计 102 个小节，某些小节内容较少的话会合并到一条学习笔记当中

### 1.本节说明
对应视频第 4 小节：A User May Response To Threads

### 2.本节内容
上节中我们的`..\views\threads\show.blade.php`视图文件回复区域的内容为：

```php
.
.
<div class="row">
	<div class="col-md-8 col-md-offset-2">
		@foreach ($thread->replies as $reply)
			<div class="panel panel-default">
				<div class="panel-heading">
					{{ $reply->owner->name }} 回复于
					{{ $reply->created_at->diffForHumans() }}
				</div>

				<div class="panel-body">
					{{ $reply->body }}
				</div>
			</div>
		@endforeach
	</div>
</div>
.
.

```
为了便于维护，我们将回复区域抽离成一个单独的视图。修改如下：
```php
.
.
<div class="row">
	<div class="col-md-8 col-md-offset-2">
		@foreach ($thread->replies as $reply)
			@include('threads.reply')
		@endforeach
	</div>
</div>
.
.
```

新建`..\views\threads\reply.blade.php`视图文件：

```php
<div class="panel panel-default">
    <div class="panel-heading">
        <a href="#">
            {{ $reply->owner->name }}
        </a>
        回复于 {{ $reply->created_at->diffForHumans() }}
    </div>

    <div class="panel-body">
        {{ $reply->body }}
    </div>
</div>
```
我们可以给话题的内容加上作者的信息：
`..\views\threads\show.blade.php`
```php
.
.
<div class="row">
	<div class="col-md-8 col-md-offset-2">
		<div class="panel panel-default">
			<div class="panel-heading">
				<a href="#">{{ $thread->creator->name }}</a> 发表了： 
				{{ $thread->title }}
			</div>

			<div class="panel-body">
				{{ $thread->body }}
			</div>
		</div>
	</div>
</div>
.
.
```
我们需要先行编写单元测试，用来测试`$thread->creator`。但是在此之前，由于上一节中我们使用了`$thread->replies`来获取回复，但并未编写单元测试。现在补上单元测试，首先移除`Unit`文件夹下的示例文件，并新建单元测试文件：
```
$ php artisan make:test ThreadTest --unit
```
修改如下：
```php
<?php

namespace Tests\Unit;

use Tests\TestCase;
use Illuminate\Foundation\Testing\DatabaseMigrations;

class ThreadTest extends TestCase
{
    use DatabaseMigrations;

    /** @test  */
    public function a_thread_has_replies()
    {
        $thread = factory('App\Thread')->create();

        $this->assertInstanceOf('Illuminate\Database\Eloquent\Collection',$thread->replies);
    }
}

```
测试一下：
```
$ phpunit tests/Unit/ThreadTest.php
```
测试通过：
![file](https://lccdn.phphub.org/uploads/images/201804/23/19192/IDjz301o7o.png?imageView2/2/w/1240/h/0)
继续编写`$thread->creator`的测试代码：
`ThreadTest.php`
```php
.
.
public function test_a_thread_has_a_creator()
{
	$this->assertInstanceOf('App\User',$this->thread->creator);
}
.
.
```
我们可以使用`--filter`来单独测试：
```
$ phpunit --filter a_thread_has_a_creator
```
![file](https://lccdn.phphub.org/uploads/images/201804/23/19192/Ec1JaDYjWp.png?imageView2/2/w/1240/h/0)
因为我们还未进行模型关联：
`app\Thread.php`
```php
.
.
public function creator()
{
	return $this->belongsTo(User::class,'user_id'); // 使用 user_id 字段进行模型关联
}
.
.
```
再次测试即可通过，刷新页面即可看到效果：
![file](https://lccdn.phphub.org/uploads/images/201804/23/19192/g0fen2R1bn.png?imageView2/2/w/1240/h/0)

接下来新建测试：
```
$ php artisan make:test ParticipateInForumTest
```
先编写测试逻辑：
```php
<?php

namespace Tests\Feature;

use Tests\TestCase;
use Illuminate\Foundation\Testing\DatabaseMigrations;

class ParticipateInForumTest extends TestCase
{
    use DatabaseMigrations;
    
	/** @test */
    function an_authenticated_user_may_participate_in_forum_threads()
    {
        // Given we have a authenticated user
        // And an existing thread
        // When the user adds a reply to the thread
        // Then their reply should be visible on the page
    }
}

```
再填充具体代码：
```php
<?php

namespace Tests\Feature;

use Tests\TestCase;
use Illuminate\Foundation\Testing\DatabaseMigrations;

class ParticipateInForumTest extends TestCase
{
    use DatabaseMigrations;

    function an_authenticated_user_may_participate_in_forum_threads()
    {
        // Given we have a authenticated user
        $this->be($user = factory('App\User')->create());

        // And an existing thread
        $thread = factory('App\Thread')->create();

        // When the user adds a reply to the thread
        $reply = factory('App\Reply')->create();
        $this->post($thread->path().'/replies',$reply->toArray());

        // Then their reply should be visible on the page
        $this->get($thread->path())
            ->assertSee($reply->body);
    }
}

```
注意到我们使用`$thread->path()`来获取 URL ，想起在`ReadThreadsTest.php`文件中可进行优化：
`tests\Feature\ReadThreadsTest.php`
```php
.
.
/** @test */
public function a_user_can_read_a_single_thread()
{
	$this->get($this->thread->path())  //此处
		->assertSee($this->thread->title);
}

/** @test */
public function a_user_can_read_replies_that_are_associated_with_a_thread()
{
	// 如果有 Thread
	// 并且该 Thread 有回复
	$reply = factory('App\Reply')
		->create(['thread_id' => $this->thread->id]);
	// 那么当我们看 Thread 时
	// 我们也要看到回复
	$this->get($this->thread->path())  //还有此处
		->assertSee($reply->body);

}
.
.
```
一般而言，当修改已通过的测试时，应该在修改之后（需注释新建的测试）再次测试，确保之前的测试逻辑未被破坏。
```
$ phpunit
```
当我们测试新写的测试时：
```
$ phpunit tests/Feature/ParticipateInForumTest.php
```
会得到一大段长长的报错信息，要定位到错误十分困难：
![file](https://lccdn.phphub.org/uploads/images/201804/23/19192/pa0yb91yJn.png?imageView2/2/w/1240/h/0)
在`app\Exceptions\Handler.php`中加上一行：
```php
.
.
public function render($request, Exception $exception)
{
	if(app()->environment() === 'local') throw $exception;  // 此处加上一行

	return parent::render($request, $exception);
}
.
.
```

> `注1`：视频教程中使用的是`app()->environment() === 'testing'`，但经过测试未生效，遂改为以上`local`。

再次运行测试：
```
$ phpunit tests/Feature/ParticipateInForumTest.php
```
现在可以十分容易地定位错误：
![file](https://lccdn.phphub.org/uploads/images/201804/23/19192/urgrjbwNdb.png?imageView2/2/w/1240/h/0)
添加路由：
```
Route::post('/threads/{thread}/replies','RepliesController@store');
```
前往`RepliesController`增加`store`方法：
```php
<?php

namespace App\Http\Controllers;

use App\Thread;
use Illuminate\Http\Request;

class RepliesController extends Controller
{
    public function store(Thread $thread)
    {
        $thread->addReply([
            'body' => request('body'),
            'user_id' => auth()->id(),
        ]);
    }
}

```
为`store`方法添加单元测试：
`ThreadTest.php`
```php
<?php

namespace Tests\Unit;

use Tests\TestCase;
use Illuminate\Foundation\Testing\DatabaseMigrations;

class ThreadTest extends TestCase
{
    use DatabaseMigrations;

    protected $thread;

    public function setUp()
    {
        parent::setUp(); // TODO: Change the autogenerated stub

        $this->thread = factory('App\Thread')->create();
    }

    /** @test  */
    public function a_thread_has_replies()
    {


        $this->assertInstanceOf('Illuminate\Database\Eloquent\Collection',$this->thread->replies);
    }

    /** @test */
    public function a_thread_has_a_creator()
    {
        $this->assertInstanceOf('App\User',$this->thread->creator);
    }

    /** @test */
    public function a_thread_can_add_a_reply()
    {
        $this->thread->addReply([
           'body' => 'Foobar',
           'user_id' => 1
        ]);

        $this->assertCount(1,$this->thread->replies);
    }
}

```
测试一下：
![file](https://lccdn.phphub.org/uploads/images/201804/27/19192/564IaKMb5m.png?imageView2/2/w/1240/h/0)
添加`addReply`方法：
`app\Thhread.php`
```php
.
.
public function addReply($reply)
{
	$this->replies()->create($reply);
}
.
.
```
再次运行测试
```
$ phpunit --filter an_authenticated_user_may_participate_in_forum_threads
```
结果报错：
![file](https://lccdn.phphub.org/uploads/images/201804/27/19192/kPxTQU7QSX.png?imageView2/2/w/1240/h/0)
按道理说不应该，根据查阅到的资料，在测试环境应该是不会检验`CsrfToken`。尝试了诸多办法仍旧无法解决，简单用以下方法临时解决：
```
$ APP_ENV=testing phpunit --filter an_authenticated_user_may_participate_in_forum_threads
```
即：在运行测试的时候将环境设为`testing`，未配合使用，应将`Hander.php`文件中代码改为如下：
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
运行测试：
![file](https://lccdn.phphub.org/uploads/images/201804/27/19192/H1OaxEZB59.png?imageView2/2/w/1240/h/0)
运行完整测试：
```
$ APP_ENV=testing phpunit
```
![file](https://lccdn.phphub.org/uploads/images/201804/27/19192/tM3zzF7s43.png?imageView2/2/w/1240/h/0)

> `注1`：此处在`笔记心得`有详细解释。

我们限制只有登录用户才能添加回复，只需利用`auth`中间件即可：
`RepliesController.php`
```php
public function __construct()
{
	$this->middleware('auth');
}
.
.
```
测试如果我们将单元测试代码更改一下：
`ParticipateInForumTest.php`
```php
<?php

namespace Tests\Feature;

use Tests\TestCase;
use Illuminate\Foundation\Testing\DatabaseMigrations;

class ParticipateInForumTest extends TestCase
{
    use DatabaseMigrations;

    /** @test */
    function an_authenticated_user_may_participate_in_forum_threads()
    {
        // Given we have a authenticated user
//        $this->be($user = factory('App\User')->create()); // 已登录用户
        $user = factory('App\User')->create(); // 未登录用户
        // And an existing thread
        $thread = factory('App\Thread')->create();

        // When the user adds a reply to the thread
        $reply = factory('App\Reply')->create();
        $this->post($thread->path() .'/replies',$reply->toArray()); // 注：此处有修改

        // Then their reply should be visible on the page
        $this->get($thread->path())
            ->assertSee($reply->body);
    }
}

```
再次运行测试：
```
$ APP_ENV=testing phpunit
```
![file](https://lccdn.phphub.org/uploads/images/201804/28/19192/AYlpQ9creD.png?imageView2/2/w/1240/h/0)
提示用户未认证，说明我们的测试有效。接下来再建立一个测试，测试未登录用户不能添加回复：
`ParticipateInForumTest.php`
```php
<?php

namespace Tests\Feature;

use Tests\TestCase;
use Illuminate\Foundation\Testing\DatabaseMigrations;

class ParticipateInForumTest extends TestCase
{
    use DatabaseMigrations;

    /** @test */
    public function unauthenticated_user_may_no_add_replies()
    {
        $this->expectException('Illuminate\Auth\AuthenticationException');

        $thread = factory('App\Thread')->create();

        $reply = factory('App\Reply')->create();
        $this->post($thread->path().'/replies',$reply->toArray());
    }

    /** @test */
    function an_authenticated_user_may_participate_in_forum_threads()
    {
        // Given we have a authenticated user
        $this->be($user = factory('App\User')->create());
        // And an existing thread
        $thread = factory('App\Thread')->create();

        // When the user adds a reply to the thread
        $reply = factory('App\Reply')->create();
        $this->post($thread->path() .'/replies',$reply->toArray());

        // Then their reply should be visible on the page
        $this->get($thread->path())
            ->assertSee($reply->body);
    }
}


```
再次测试：
```
$ APP_ENV=testing phpunit
```
成功通过：


### 笔记心得
* 关于`注1`的说明：
 第四节课经历比较坎坷，主要是遇到了一个问题：
 ![file](https://lccdn.phphub.org/uploads/images/201804/26/19192/qnZZk8G5Ko.png?imageView2/2/w/1240/h/0)
 ```
 Illuminate\Session\TokenMismatchException:
 ```
 显示的问题应该是`CSRF`令牌不符，于是定位到
 `\vendor\laravel\framework\src\Illuminate\Foundation\Http\Middleware\VerifyCsrfToken.php`的`tokensMatch`方法：
 ```php
 protected function tokensMatch($request)
{
	$token = $this->getTokenFromRequest($request);

	return is_string($request->session()->token()) &&
		   is_string($token) &&
		   hash_equals($request->session()->token(), $token);
}
 ```
 发现验证的是`$token`跟`$request->session()->token()`的值，于是将两者的值打印出来看看：
 ```php
 protected function tokensMatch($request)
    {
        $token = $this->getTokenFromRequest($request);
        var_dump($token);
        var_dump($request->session()->token());exit;
        return is_string($request->session()->token()) &&
               is_string($token) &&
               hash_equals($request->session()->token(), $token);
    }
 ```
运行：
```
$ phpunit
```
![file](https://lccdn.phphub.org/uploads/images/201804/26/19192/kfvNZp9Kae.png?imageView2/2/w/1240/h/0)
这就是问题所在，`$token`的值居然是`null`！
 这个问题困扰了三四天之久（这篇文章本来应该在 4 月 22 号发布），翻阅了超过 50 个 Google 页面，任然没有找到比较好的办法。自己有点较真，所以一直在找资料、看源码，虽然最后任然没有解决，可是体会颇多，收获也是不少。对`Laravel` 的理解也比之前透彻多了，所以千万不要怕遇到问题，只有遇到问题才会促使人更快的进步。

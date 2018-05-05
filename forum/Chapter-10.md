## 0.写在前面

* 本系列文章为`laracasts.com` 的系列视频教程——[Let's Build A Forum with Laravel and TDD](https://laracasts.com/series/lets-build-a-forum-with-laravel) 的学习笔记。若喜欢该系列视频，可去该网站订阅后下载该系列视频，*** 支持正版 ***。
* 视频源码地址：[https://github.com/laracasts/Lets-Build-a-Forum-in-Laravel](http://https://github.com/laracasts/Lets-Build-a-Forum-in-Laravel)
* 本项目为一个 forum（论坛）项目，与本站的第二本实战教程 [Laravel 教程 - Web 开发实战进阶 ( Laravel 5.5 )](https://laravel-china.org/courses/laravel-intermediate-training-5.5) 类似，可互相参照
* 项目开发模式为`TDD`开发，教程简介为：
> A forum is a deceptively complex thing. Sure, it's made up of threads and replies, but what else might exist as part of a forum? What about profiles, or thread subscriptions, or filtering, or real-time notifications? As it turns out, a forum is the perfect project to stretch your programming muscles. In this series, we'll work together to build one with tests from A to Z
* 项目版本为`laravel 5.4`，教程后面会进行升级到`laravel 5.5`的教学
* 视频教程共计 102 个小节，笔记章节与视频教程一一对应

### 1.本节说明
* 对应视频第  10 小节：How To Test Validation Errors

### 2.本节内容
在开始本节内容之前，我们首先来看一下`an_authenticated_user_can_create_new_forum_threads`测试：
`forum\tests\Feature\CreateThreadsTest.php`
```
.
.
/** @test */
public function an_authenticated_user_can_create_new_forum_threads()
{
	// Given we have a signed in user
	$this->signIn();  // 已登录用户

	// When we hit the endpoint to cteate a new thread
	$thread = create('App\Thread');
	$this->post('/threads',$thread->toArray());

	// Then,when we visit the thread
	// We should see the new thread
	$this->get($thread->path())
		->assertSee($thread->title)
		->assertSee($thread->body);
}
.
.
```
如果我们注释掉下面这行：
```
$this->post('/threads',$thread->toArray());
```
再运行测试的话依然可以通过：
![file](https://lccdn.phphub.org/uploads/images/201805/02/19192/aBFmtyqECM.png?imageView2/2/w/1240/h/0)
这是因为我们使用了`create()`方法，而`create()`方法会获取模型实例并且 **入库**，这样我们的测试就没有起到原有的作用。但是，如果我们把`create()`改成`make()`再进行测试：
```
$thread = make('App\Thread');
```
![file](https://lccdn.phphub.org/uploads/images/201805/02/19192/c23B7u2cvY.png?imageView2/2/w/1240/h/0)
测试会不通过，因为我们使用了`make()`方法，模型实例并未入库，当然也就不存在`$thread->path()`即`/threads/{channel}/{id}`这样的`url`，所以会抛出`NotFoundHttpException`的异常。
现在进行修复：
```
.
.
/** @test */
public function an_authenticated_user_can_create_new_forum_threads()
{
	// Given we have a signed in user
	$this->signIn();  // 已登录用户

	// When we hit the endpoint to cteate a new thread
	$thread = make('App\Thread');
	$response = $this->post('/threads',$thread->toArray());

	// Then,when we visit the thread
	// We should see the new thread
	$this->get($response->headers->get('Location'))
		->assertSee($thread->title)
		->assertSee($thread->body);
}
.
.
```
`$response`是`Illuminate\Foundation\Testing\TestResponse`的一个实例化对象，通过`$response->headers->get('Location')`获取到`url`，类似于：http://forum.test/threads/debitis/1 。
运行测试：
![file](https://lccdn.phphub.org/uploads/images/201805/02/19192/IH0ftz7hV9.png?imageView2/2/w/1240/h/0)
OK，现在的测试已经没问题了。接下来让我们进入本节内容。
在当前的进度下，我们已经可以发布`Thread`跟`Reply`，但是我们并没有对输入的内容做限制。本节我们要来学习**Laravel** `Validation`的测试。
首先我们来对`$thread`的`title`字段做必填校验：
`forum\tests\Feature\CreateThreadsTest.php`
```
.
.
/** @test */
public function a_thread_requires_a_title()
{
	$this->signIn();

	$thread = make('App\Thread',['title' => null]);

	$this->post('/threads',$thread->toArray())
		->assertSessionHasErrors('title');
}
.
.
```
`forum\app\Http\Controllers\ThreadsController.php`
```
.
.
public function store(Request $request)
{
	$this->validate($request,[
	   'title' => 'required'
	]);

	$thread = Thread::create([
		'user_id' => auth()->id(),
		'channel_id' => request('channel_id'),
		'title' => request('title'),
		'body' => request('body'),
	]);

	return redirect($thread->path());
}
.
.
```
如果此时运行测试：
```
$ APP_ENV=testing phpunit --filter a_thread_requires_a_title
```
![file](https://lccdn.phphub.org/uploads/images/201805/02/19192/ngkesH4dYr.png?imageView2/2/w/1240/h/0)
我们并不需要捕获此异常，因此我们需要调用`withExceptionHandling()`方法：
```
$this->withExceptionHandling()->signIn();
```
再次运行测试：
![file](https://lccdn.phphub.org/uploads/images/201805/02/19192/AaxZGd1Ig8.png?imageView2/2/w/1240/h/0)
现在让我们来想一下，我们对提交的`$thread`的内容不会仅仅检查一个`title`字段。假如我们新增加一个验证字段或者验证规则，我们就要多写一次以下代码：
```
$this->withExceptionHandling()->signIn();

$thread = make('App\Thread',['title' => null]);

$this->post('/threads',$thread->toArray())
```
这明显违背了 **DRY** 原则：Don't Repeat Youself ！作为程序员，我们想要写出高质量的代码，应时刻牢记这一基本原则，不写重复的代码。所以我们来进行重构，将以上代码片段抽离成一个新的方法，然后验证的时候去调用它即可：
```
.
.
/** @test */
public function a_thread_requires_a_title()
{
	$this->publishThread(['title' => null])
		->assertSessionHasErrors('title');
}

public function publishThread($overrides = [])
{
	$this->withExceptionHandling()->signIn();
	
	$thread = make('App\Thread',$overrides);
	
	return $this->post('/threads',$thread->toArray());
}
.
.

```
测试一下，通过：
![file](https://lccdn.phphub.org/uploads/images/201805/02/19192/WYbEkZtumH.png?imageView2/2/w/1240/h/0)
接下来我们验证 Thread 的其他信息：
* `user_id`的值为`auth()->id()`，即当前登录用户的`id`。这个值一定存在，所以我们不用验证；
* `channel_id`，需要验证为必填，并且已存在；
* `body`需要验证为必填；

修改`store()`方法的验证规则：
```
public function store(Request $request)
{
	$this->validate($request,[
	   'title' => 'required',
		'body' => 'required',
		'channel_id' => 'required|exists:channels,id'
	]);

	$thread = Thread::create([
		'user_id' => auth()->id(),
		'channel_id' => request('channel_id'),
		'title' => request('title'),
		'body' => request('body'),
	]);

	return redirect($thread->path());
}
```
> 注：exists 方法详见 [Laravel 的表单验证机制详解](https://laravel-china.org/docs/laravel/5.5/validation)

得益于我们前面抽离出来的`publishThread()`方法，我们可以方便的新建测试：
```
.
.
/** @test */
public function a_thread_requires_a_title()
{
	$this->publishThread(['title' => null])
		->assertSessionHasErrors('title');
}

/** @test */
public function a_thread_requires_a_body()
{
	$this->publishThread(['body' => null])
		->assertSessionHasErrors('body');
}

/** @test */
public function a_thread_requires_a_valid_channel()
{
	factory('App\Channel',2)->create(); // 新建两个 Channel，id 分别为 1 跟 2

	$this->publishThread(['channel_id' => null])
		->assertSessionHasErrors('channel_id');

	$this->publishThread(['channel_id' => 999])  // channle_id 为 999，是一个不存在的 Channel
		->assertSessionHasErrors('channel_id');
}
.
.
```
运行一下新建的两个测试：
![file](https://lccdn.phphub.org/uploads/images/201805/02/19192/KEEdzzVtiF.png?imageView2/2/w/1240/h/0)
如果在测试中，我们给`channel_id`赋值为已存在的`id`，那么我们的测试应该会不通过：
```
/** @test */
public function a_thread_requires_a_valid_channel()
{
	factory('App\Channel',2)->create(); // 新建两个 Channel，id 分别为 1 跟 2

	$this->publishThread(['channel_id' => null])
		->assertSessionHasErrors('channel_id');

	$this->publishThread(['channel_id' => 2])  // channle_id 为 2，是一个存在的 Channel
		->assertSessionHasErrors('channel_id');
}
```
![file](https://lccdn.phphub.org/uploads/images/201805/02/19192/2LR4ckyCkK.png?imageView2/2/w/1240/h/0)
接下来按照同样的思路，我们对添加回复进行验证：
`forum\app\Http\Controllers\RepliesController.php`
```
.
.
public function store($channelId,Thread $thread)
{
	$this->validate(request(),['body' => 'required']);

	$thread->addReply([
		'body' => request('body'),
		'user_id' => auth()->id(),
	]);

	return back();
}
.
.
```
`forum\tests\Feature\ParticipateInForumTest.php`
```
.
.
/** @test */
public function a_reply_reqiures_a_body()
{
	$this->withExceptionHandling()->signIn();

	$thread = create('App\Thread');
	$reply = make('App\Reply',['body' => null]);
	
	$this->post($thread->path() . '/replies',$reply->toArray())
		->assertSessionHasErrors('body');
}
.
.
```
运行一下新建的测试：
![file](https://lccdn.phphub.org/uploads/images/201805/02/19192/IZtP0KvhZl.png?imageView2/2/w/1240/h/0)
当然，我们全部的测试任然有效：
![file](https://lccdn.phphub.org/uploads/images/201805/02/19192/uyKNcHSltU.png?imageView2/2/w/1240/h/0)

### 3.写在后面
* 如有建议或意见，欢迎指出~
* 如果觉得文章写的不错，请点赞鼓励下哈，你的鼓励将是我的动力！
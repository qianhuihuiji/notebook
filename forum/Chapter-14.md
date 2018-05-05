### 0.写在前面
* 本系列文章为`laracasts.com` 的系列视频教程——[Let's Build A Forum with Laravel and TDD](https://laracasts.com/series/lets-build-a-forum-with-laravel) 的学习笔记。若喜欢该系列视频，可去该网站订阅后下载该系列视频，*** 支持正版 ***。
* 视频源码地址：[https://github.com/laracasts/Lets-Build-a-Forum-in-Laravel](http://https://github.com/laracasts/Lets-Build-a-Forum-in-Laravel)
* *本项目为一个 forum（论坛）项目，与本站的第二本实战教程 [Laravel 教程 - Web 开发实战进阶 ( Laravel 5.5 )](https://laravel-china.org/courses/laravel-intermediate-training-5.5) 类似，可互相参照
* 项目开发模式为`TDD`开发，教程简介为：
> A forum is a deceptively complex thing. Sure, it's made up of threads and replies, but what else might exist as part of a forum? What about profiles, or thread subscriptions, or filtering, or real-time notifications? As it turns out, a forum is the perfect project to stretch your programming muscles. In this series, we'll work together to build one with tests from A to Z.
* 项目版本为 **laravel 5.4**，教程后面会进行升级到 **laravel 5.5** 的教学
* 视频教程共计 102 个小节，笔记章节与视频教程一一对应

### 1.本节说明
* 对应视频第 14 小节：A User Can Filter Threads By Username

### 2.本节内容
本节我们来实现根据用户来筛选话题的功能。首先运行一下测试：
![file](https://lccdn.phphub.org/uploads/images/201805/03/19192/jVZsSkx7wY.png?imageView2/2/w/1240/h/0)
会发现我们所有的测试都报错了，这是因为我们在上一节使用了 [视图共享数据](https://laravel-china.org/docs/laravel/5.5/views#sharing-data-with-all-views) :
`forum\app\Providers\AppServiceProvider.php`
```
.
.
public function boot()
{
	Carbon::setLocale('zh');
	\View::share('channels',\App\Channel::all());
}
.
.
```
而`boot()`方法会在应用启动的最开始时就加载，在测试场景中，运行测试时，测试数据库中是不存在数据表的，所以就抛出了异常。我们改为使用 [视图合成器](https://laravel-china.org/docs/laravel/5.5/views#b492db) ：
> 视图合成器是在**渲染视图时调用**的回调或者类方法。如果你每次渲染视图时都要绑定视图的数据，视图合成器可以帮你将这些逻辑整理到特定的位置。

`forum\app\Providers\AppServiceProvider.php`
```
.
.
public function boot()
{
	Carbon::setLocale('zh');
	\View::composer('*',function ($view){
	   $view->with('channels',Channel::all()); 
	});
}
.
.
```
再次运行测试：
![file](https://lccdn.phphub.org/uploads/images/201805/03/19192/VD4YIVNWoj.png?imageView2/2/w/1240/h/0)
所以我们在更改了代码之后，要记得运行一下测试，确保代码无误。接下来按照惯例，建立新功能之前，先建立测试：
`forum\tests\Feature\ReadThreadsTest.php`
```
.
.
/** @test */
public function a_user_can_filter_threads_by_any_username()
{
	$this->signIn(create('App\User',['name' => 'NoNo1']));

	$threadByNoNo1 = create('App\Thread',['user_id' => auth()->id()]);
	$threadNotByNoNo1 = create('App\Thread');

	$this->get('threads?by=NoNo1')
		->assertSee($threadByNoNo1->title)
		->assertDontSee($threadNotByNoNo1->title);
}
.
.
```
运行测试会失败：
![file](https://lccdn.phphub.org/uploads/images/201805/03/19192/0KHBBkw8mE.png?imageView2/2/w/1240/h/0)
修改`index()`方法：
`forum\app\Http\Controllers\ThreadsController.php`
```
.
.
public function index(Channel $channel)
{
	if($channel->exists){
		$threads = $channel->threads()->latest();
	}else{
		$threads = Thread::latest();
	}

	if($username = request('by')){
		$user = \App\User::where('name',$username)->firstOrFail();

		$threads->where('user_id',$user->id);
	}

	$threads  = $threads->get();

	return view('threads.index',compact('threads'));
}
.
.
```
> 注：现在控制器中的代码比较粗糙，我们先让测试通过，在后面会进行重构，不要着急

运行测试：
![file](https://lccdn.phphub.org/uploads/images/201805/04/19192/Y5OLoSocWQ.png?imageView2/2/w/1240/h/0)
系统界面：
![file](https://lccdn.phphub.org/uploads/images/201805/04/19192/RhSrJac0nS.png?imageView2/2/w/1240/h/0)
修改导航栏，将这一功能显示出来：
`forum\resources\views\layouts\app.blade.php`
```
.
.
<div class="collapse navbar-collapse" id="app-navbar-collapse">
	<!-- Left Side Of Navbar -->
	<ul class="nav navbar-nav">
		<li class="dropdown">
			<a href="#" class="dropdown-toggle" data-toggle="dropdown" aria-hidden="true"
			   aria-expanded="false">Browse <span class="caret"></span> </a>

			<ul class="dropdown-menu">
				<li><a href="/threads">ALL Threads</a> </li>
				<li><a href="/threads?by={{ auth()->user()->name }}">My Threads</a> </li>
			</ul>
		</li>

		<li><a href="/threads/create">New Thread</a></li>
.
.
```
![file](https://lccdn.phphub.org/uploads/images/201805/04/19192/C58raWJiAb.png?imageView2/2/w/1240/h/0)
运行测试，看我们是否破坏了功能：
```
$ APP_ENV=testing phpunit
```
![file](https://lccdn.phphub.org/uploads/images/201805/04/19192/ZKQFfxl0L7.png?imageView2/2/w/1240/h/0)
根据报错信息可知，在此行代码中：
```
<li><a href="/threads?by={{ auth()->user()->name }}">My Threads</a> </li>
```
若未登录，`auth()->user()`的值为`null`，因此会抛出上面的异常。修改一下代码：
`forum\resources\views\layouts\app.blade.php`
```
.
.
<div class="collapse navbar-collapse" id="app-navbar-collapse">
	<!-- Left Side Of Navbar -->
	<ul class="nav navbar-nav">
		<li class="dropdown">
			<a href="#" class="dropdown-toggle" data-toggle="dropdown" aria-hidden="true"
			   aria-expanded="false">Browse <span class="caret"></span> </a>

			<ul class="dropdown-menu">
				<li><a href="/threads">ALL Threads</a> </li>
				
				@if(auth()->check())
					<li><a href="/threads?by={{ auth()->user()->name }}">My Threads</a> </li>
				@endif
				
			</ul>
		</li>

		<li><a href="/threads/create">New Thread</a></li>
.
.
```
再次测试：
![file](https://lccdn.phphub.org/uploads/images/201805/04/19192/iYQw9Ew18l.png?imageView2/2/w/1240/h/0)
### 3.写在后面
* 如有建议或意见，欢迎指出~
* 如果觉得文章写的不错，请点赞鼓励下哈，你的鼓励将是我的动力！
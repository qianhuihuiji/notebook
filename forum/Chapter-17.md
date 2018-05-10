### 0.写在前面
* 本系列文章为`laracasts.com` 的系列视频教程——[Let's Build A Forum with Laravel and TDD](https://laracasts.com/series/lets-build-a-forum-with-laravel) 的学习笔记。若喜欢该系列视频，可去该网站订阅后下载该系列视频，*** 支持正版 ***。
* 视频源码地址：[https://github.com/laracasts/Lets-Build-a-Forum-in-Laravel](http://https://github.com/laracasts/Lets-Build-a-Forum-in-Laravel)
* *本项目为一个 forum（论坛）项目，与本站的第二本实战教程 [Laravel 教程 - Web 开发实战进阶 ( Laravel 5.5 )](https://laravel-china.org/courses/laravel-intermediate-training-5.5) 类似，可互相参照
* 项目开发模式为`TDD`开发，教程简介为：
> A forum is a deceptively complex thing. Sure, it's made up of threads and replies, but what else might exist as part of a forum? What about profiles, or thread subscriptions, or filtering, or real-time notifications? As it turns out, a forum is the perfect project to stretch your programming muscles. In this series, we'll work together to build one with tests from A to Z.
* 项目版本为 **laravel 5.4**，教程后面会进行升级到 **laravel 5.5** 的教学
* 视频教程共计 102 个小节，笔记章节与视频教程一一对应

### 1.本节说明
* 对应视频第 17 小节：A User Can Filter Threads By Polularity

### 2.本节内容
在开始本节的内容之前，让我们先在`index`页加上回复数。首先更改首页：
`forum\resources\views\threads\index.blade.php`
```
.
.
<article>
	<div class="level">
		<h4 class="flex">
			<a href="{{ $thread->path() }}">
				{{ $thread->title }}
			</a>
		</h4>

		<a href="{{ $thread->path() }}">
			{{ $thread->replies_count }} {{ str_plural('reply',$thread->replies_count) }}
		</a>
	</div>

	<div class="body">{{ $thread->body }}</div>
</article>
.
.
```
接着更改页面布局的样式：
`forum\resources\views\layouts\app.blade.php`
```
.
.
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <!-- CSRF Token -->
    <meta name="csrf-token" content="{{ csrf_token() }}">

    <title>{{ config('app.name', 'Laravel') }}</title>

    <!-- Styles -->
    <link href="{{ asset('css/app.css') }}" rel="stylesheet">

    <script>
        window.Laravel = {!! json_encode([
            'csrfToken' => csrf_token(),
        ]) !!};
    </script>

    <style>
        body{ padding-bottom: 100px; }
        .level { display: flex;align-items: center; }
        .flex { flex: 1 }
    </style>
</head>
<body>
    <div id="app">
		.
		.
```
完成后页面：
![file](https://lccdn.phphub.org/uploads/images/201805/06/19192/PAJACA9qqM.png?imageView2/2/w/1240/h/0)
现在开始本节的内容：根据回复数来筛选话题。一如既往，首先新建测试:
`forum\tests\Feature\ReadThreadsTest.php`
```
.
.
/** @test */
public function a_user_can_filter_threads_by_popularity()
{
	// Given we have three threads
	// With 2 replies,3 replies,0 replies, respectively
	// When I filter all threads by popularity
	// Then they should be returned from most replies to least.
}
.
```
填充具体代码：
```
.
.
/** @test */
public function a_user_can_filter_threads_by_popularity()
{
	// Given we have three threads
	// With 2 replies,3 replies,0 replies, respectively
	$threadWithTwoReplies = create('App\Thread');
	create('App\Reply',['thread_id'=>$threadWithTwoReplies->id],2);

	$threadWithThreeReplies = create('App\Thread');
	create('App\Reply',['thread_id'=>$threadWithThreeReplies->id],3);

	$threadWithNoReplies = $this->thread;

	// When I filter all threads by popularity
	$response = $this->getJson('threads?popularity=1')->json();

	// Then they should be returned from most replies to least.
	$this->assertEquals([3,2,0],array_column($response,'replies_count'));
}
.
```

修改一下`functions.php`文件：
`C:\Users\meiyiming\Code\forum\tests\utilities\functions.php`
```
<?php

function create($class,$attributes = [],$times = null)
{
    return factory($class,$times)->create($attributes);
}

function make($class,$attributes = [],$times = null)
{
    return factory($class,$times)->make($attributes);
}

function raw($class,$attributes = [],$times = null)
{
    return factory($class,$times)->raw($attributes);
}
```
运行测试：
![file](https://lccdn.phphub.org/uploads/images/201805/09/19192/qDDUQQ6hpa.png?imageView2/2/w/1240/h/0)
我们需要修改一下`index()`方法：
`forum\app\Http\Controllers\ThreadsController.php`
```
.
.
public function index(Channel $channel,ThreadsFilters $filters)
{
	$threads = $this->getThreads($channel, $filters);

	if(request()->wantsJson()){
		return $threads;
	}

	return view('threads.index',compact('threads'));
}
.
.
```
再次运行测试，可以清楚地看到两者的顺序不匹配，所以测试未通过：
![file](https://lccdn.phphub.org/uploads/images/201805/09/19192/bIMjppZaje.png?imageView2/2/w/1240/h/0)

得益于上一节的重构，所以我们现在增加一个`popularity`筛选条件，只需在`$filter = ['by']`数组中增加一个`popularity`再增加一个相应的筛选方法即可：
`forum\app\Filters\ThreadsFilters.php`
```
<?php

namespace App\Filters;

use App\User;

class ThreadsFilters extends Filters
{
    protected $filters = ['by','popularity'];

    /**
     * @param $username
     * @return mixed
     */
    protected function by($username)
    {
        $user = User::where('name', $username)->firstOrfail();

        return $this->builder->where('user_id', $user->id);
    }

    /**
     * @return mixed
     */
    public function popularity()
    {
        return $this->builder->orderBy('replies_count','desc');
    }
}
```
再次运行，依旧失败：
![file](https://lccdn.phphub.org/uploads/images/201805/09/19192/xrsRQQU8lU.png?imageView2/2/w/1240/h/0)
我们将具体的`sql`语句打印出来：
`forum\app\Http\Controllers\ThreadsController.php`
```
.
.
protected function getThreads(Channel $channel, ThreadsFilters $filters)
{
	$threads = Thread::latest()->filter($filters);

	if ($channel->exists) {
		$threads->where('channel_id', $channel->id);
	}

	dd($threads->toSql());

	$threads = $threads->get();
	return $threads;
}
.
```
访问 [http://forum.test/threads?popularity=1](http://forum.test/threads?popularity=1) :
![file](https://lccdn.phphub.org/uploads/images/201805/09/19192/60mg3q91q3.png?imageView2/2/w/1240/h/0)
发现我们的前置排序条件是：`order by created_at desc`。这是因为我们使用了`:latest()`方法，返回的结果默认按`created_at`字段倒序：
```
$threads = Thread::latest()->filter($filters);
```
修复这个问题，我们需要在根据`popularity`筛选时，清空其他的`order by`条件即可：
`forum\app\Filters\ThreadsFilters.php`
```
.
.
public function popularity()
{
	$this->builder->getQuery()->orders = [];

	return $this->builder->orderBy('replies_count','desc');
}
.
```
再次刷新页面：
![file](https://lccdn.phphub.org/uploads/images/201805/09/19192/6OefHl7b1l.png?imageView2/2/w/1240/h/0)
去掉`dd()`语句再刷新：
![file](https://lccdn.phphub.org/uploads/images/201805/09/19192/LiXZCOcJRw.png?imageView2/2/w/1240/h/0)
运行测试：
![file](https://lccdn.phphub.org/uploads/images/201805/09/19192/vh3tM9sCyy.png?imageView2/2/w/1240/h/0)
全部的测试：
![file](https://lccdn.phphub.org/uploads/images/201805/09/19192/y8I4OA9iLL.png?imageView2/2/w/1240/h/0)
现在只剩下将排序的链接显示出来：
`forum\resources\views\layouts\app.blade.php`
```
.
.
<ul class="dropdown-menu">
	<li><a href="/threads">ALL Threads</a> </li>

	@if(auth()->check())
		<li><a href="/threads?by={{ auth()->user()->name }}">My Threads</a> </li>
	@endif

	<li><a href="/threads?popularity=1">Popular Threas</a> </li>
</ul>
.
.
```
访问应用：
![file](https://lccdn.phphub.org/uploads/images/201805/09/19192/5kNEYMBHpm.png?imageView2/2/w/1240/h/0)
### 3.写在后面
* 如有建议或意见，欢迎指出~
* 如果觉得文章写的不错，请点赞鼓励下哈，你的鼓励将是我的动力！
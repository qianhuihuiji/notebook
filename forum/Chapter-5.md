### 0.写在前面
* 本系列文章为`laracasts.com` 的系列视频教程——[Let's Build A Forum with Laravel and TDD](https://laracasts.com/series/lets-build-a-forum-with-laravel) 的学习笔记。若喜欢该系列视频，可去该网站订阅后下载该系列视频，*** 支持正版 ***。
* 视频源码地址：[https://github.com/laracasts/Lets-Build-a-Forum-in-Laravel](http://https://github.com/laracasts/Lets-Build-a-Forum-in-Laravel)
* *本项目为一个 forum（论坛）项目，与本站的第二本实战教程 [Laravel 教程 - Web 开发实战进阶 ( Laravel 5.5 )](https://laravel-china.org/courses/laravel-intermediate-training-5.5) 类似，可互相参照
* 项目开发模式为`TDD`开发，教程简介为：
> A forum is a deceptively complex thing. Sure, it's made up of threads and replies, but what else might exist as part of a forum? What about profiles, or thread subscriptions, or filtering, or real-time notifications? As it turns out, a forum is the perfect project to stretch your programming muscles. In this series, we'll work together to build one with tests from A to Z.
* 项目版本为`laravel 5.4`，教程后面会进行升级到`laravel 5.5`的教学
* 视频教程共计 102 个小节，笔记章节与视频教程一一对应

### 1.本节说明
* 对应视频第 5 小节：The Reply Form

### 2.本节内容
开始之前我们先在主布局文件中修改一下`body`的样式：
`\resources\views\layouts\app.blade.php`
```php
.
.
<body style="padding-bottom: 100px;">
.
.
```
首先我们先注册一个用户：
![file](https://lccdn.phphub.org/uploads/images/201804/28/19192/UH1vTxhONU.png?imageView2/2/w/1240/h/0)

登录之后访问 [forum.test/threads/1](http://forum.test/threads/1)
接着在话题页面增加一个回复框，并且限定只有已登录用户才能看到：
`\resources\views\threads\show.blade.php`
```php
.
.
<div class="row">
	<div class="col-md-8 col-md-offset-2">
		[@foreach](https://laravel-china.org/users/5651) ($thread->replies as $reply)
			@include('threads.reply')
		@endforeach
	</div>
</div>

@if (auth()->check())  // 已登录用户才可见
	<div class="row">
		<div class="col-md-8 col-md-offset-2">
			<form method="post" action="{{ $thread->path() . '/replies' }}">
				<div class="form-group">
					<textarea name="body" id="body" class="form-control" placeholder="说点什么吧..."rows="5"></textarea>
				</div>

				<button type="submit" class="btn btn-default">提交</button>
			</form>
		</div>
	</div>
@endif
.
.
```
刷新页面：
![file](https://lccdn.phphub.org/uploads/images/201804/28/19192/UytpUaQMmI.png?imageView2/2/w/1240/h/0)

退出登录：
![file](https://lccdn.phphub.org/uploads/images/201804/28/19192/VURa7GQjCo.png?imageView2/2/w/1240/h/0)

在上一节我们对添加回复的动作已经编写了单元测试，且都通过了：
![file](https://lccdn.phphub.org/uploads/images/201804/28/19192/hye6gi1T7y.png?imageView2/2/w/1240/h/0)

但是在我们输入回复再提交时却会报错：
![file](https://lccdn.phphub.org/uploads/images/201804/28/19192/NmwBAAkl1a.png?imageView2/2/w/1240/h/0)

因为我们忘了给`POST`动作加上`CSRF`验证，加上即可：
```
.
.
<form method="post" action="{{ $thread->path() . '/replies' }}">
                        
	{{ csrf_field() }}
.
.
```
再次测试，提交成功：
![file](https://lccdn.phphub.org/uploads/images/201804/28/19192/cbq8dCpLLF.png?imageView2/2/w/1240/h/0)

未登录用户只能浏览回复列表，无法发表回复，我们可以引导未登录用户先去登录：
```
.
.
@else
	<p class="text-center">请先<a href="{{ route('login') }}">登录</a>，然后再发表回复 </p>
@endif
.
.
```

![file](https://lccdn.phphub.org/uploads/images/201804/28/19192/qKKYaqeQCZ.png?imageView2/2/w/1240/h/0)

为页首加上导航：
`\resources\views\layouts\app.blade.php`
```php
.
.
<div class="collapse navbar-collapse" id="app-navbar-collapse">
	<!-- Left Side Of Navbar -->
	<ul class="nav navbar-nav">
		<li><a href="/threads">All Threads</a></li>
	</ul>
.
.
```
### 3.写在后面
* 如有建议或意见，欢迎指出~
* 如果觉得文章写的不错，请点赞鼓励下哈，你的鼓励将是我的动力！


## 0.写在前面

* 本系列文章为`laracasts.com` 的系列视频教程——[Let's Build A Forum with Laravel and TDD](https://laracasts.com/series/lets-build-a-forum-with-laravel) 的学习笔记。若喜欢该系列视频，可去该网站订阅后下载该系列视频，*** 支持正版 ***。
* 视频源码地址：[https://github.com/laracasts/Lets-Build-a-Forum-in-Laravel](http://https://github.com/laracasts/Lets-Build-a-Forum-in-Laravel)
* *本项目为一个 forum（论坛）项目，与本站的第二本实战教程 [Laravel 教程 - Web 开发实战进阶 ( Laravel 5.5 )](https://laravel-china.org/courses/laravel-intermediate-training-5.5) 类似，可互相参照
* 项目开发模式为`TDD`开发，教程简介为：
> A forum is a deceptively complex thing. Sure, it's made up of threads and replies, but what else might exist as part of a forum? What about profiles, or thread subscriptions, or filtering, or real-time notifications? As it turns out, a forum is the perfect project to stretch your programming muscles. In this series, we'll work together to build one with tests from A to Z.
* 项目版本为 **laravel 5.4**，教程后面会进行升级到 **laravel 5.5** 的教学
* 视频教程共计 102 个小节，笔记章节与视频教程一一对应

### 1.本节说明
* 对应视频第 12 小节：Validation Errors And Old Data

### 2.本节内容
现在我们通过访问 [http://forum.test/threads/create](http://forum.test/threads/create) 可以创建新的话题，但我们在主页面无法进入到创建页面。让我们来加上这一功能：
`forum\resources\views\layouts\app.blade.php`
```
.
.
<div class="collapse navbar-collapse" id="app-navbar-collapse">
	<!-- Left Side Of Navbar -->
	<ul class="nav navbar-nav">
		<li><a href="/threads">All Threads</a></li>

		<li><a href="/threads/create">New Thread</a></li>  // 新建页面的链接
		.
		.
```
刷新页面：
![file](https://lccdn.phphub.org/uploads/images/201805/03/19192/4fUnsY8xQ7.png?imageView2/2/w/1240/h/0)
现在我们可以点击`New Thread`进入创建页面：
![file](https://lccdn.phphub.org/uploads/images/201805/03/19192/RbaioK5wwt.png?imageView2/2/w/1240/h/0)
但是如果我们尝试不合法的提交：
![file](https://lccdn.phphub.org/uploads/images/201805/03/19192/ETtPRV5pWs.png?imageView2/2/w/1240/h/0)
点击`Publish`按钮，会发现页面重新刷新：
![file](https://lccdn.phphub.org/uploads/images/201805/03/19192/l7WL8INnCN.png?imageView2/2/w/1240/h/0)
因为不合法的提交被拒绝，页面重定向至`create`页面。接下来需要把上一次提交的内容和错误的消息显示出来：
`forum\resources\views\threads\create.blade.php`
```
.
.
<div class="panel-body">
	<form method="post" action="/threads">
		{{ csrf_field() }}

		<div class="form-group">
			<label for="title">Title</label>
			<input type="text" class="form-control" id="title" name="title" value="{{ old('title') }}">
		</div>

		<div class="form-group">
			<label for="body">Body</label>
			<textarea name="body" id="body" class="form-control" rows="8">{{ old('body') }}</textarea>
		</div>

		<div class="form-group">
			<button type="submit" class="btn btn-primary">Publish</button>
		</div>

		@if(count($errors))
			<ul class="alert alert-danger">
				@foreach($errors->all() as $error)
					<li>{{ $error }}</li>
				@endforeach
			</ul>
		@endif
	</form>

</div>
.
.
```
再次尝试不合法提交：
![file](https://lccdn.phphub.org/uploads/images/201805/03/19192/SrrZQ13L2O.png?imageView2/2/w/1240/h/0)
因为我们定义了`channel_id`为必填项，所以提交会不通过。修改创建页面：
 `forum\resources\views\threads\create.blade.php`
 ```
 .
 .
 <div class="panel-body">
	<form method="post" action="/threads">
		{{ csrf_field() }}

		<div class="form-group">
			<label for="channel_id">Choose a Channel</label>
			<select name="channel_id" id="channel_id" class="form-control" required>
				<option value="">Choose One...</option>
				@foreach(App\Channel::all() as $channel)
					<option value="{{ $channel->id }}" {{ old('channel_id') == $channel->id ? 'selected' : ''}}>
						{{ $channel->name }}
					</option>
				@endforeach
			</select>
		</div>

		<div class="form-group">
			<label for="title">Title</label>
			<input type="text" class="form-control" id="title" name="title" value="{{ old('title') }}" required>
		</div>

		<div class="form-group">
			<label for="body">Body</label>
			<textarea name="body" id="body" class="form-control" rows="8" required>{{ old('body') }}</textarea>
		</div>

		<div class="form-group">
			<button type="submit" class="btn btn-primary">Publish</button>
		</div>

		@if(count($errors))
			<ul class="alert alert-danger">
				@foreach($errors->all() as $error)
					<li>{{ $error }}</li>
				@endforeach
			</ul>
		@endif
	</form>

</div>
.
.
 ```
 尝试提交：
 ![file](https://lccdn.phphub.org/uploads/images/201805/03/19192/Rhg2e3a60x.png?imageView2/2/w/1240/h/0)
 提交成功：
 ![file](https://lccdn.phphub.org/uploads/images/201805/03/19192/wXhQWcU2go.png?imageView2/2/w/1240/h/0)
 

### 3.写在后面
* 如有建议或意见，欢迎指出~
* 如果觉得文章写的不错，请点赞鼓励下哈，你的鼓励将是我的动力！
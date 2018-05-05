### 0.写在前面

* 本系列文章为`laracasts.com` 的系列视频教程——[Let's Build A Forum with Laravel and TDD](https://laracasts.com/series/lets-build-a-forum-with-laravel) 的学习笔记。若喜欢该系列视频，可去该网站订阅后下载该系列视频，*** 支持正版 ***。
* 视频源码地址：[https://github.com/laracasts/Lets-Build-a-Forum-in-Laravel](http://https://github.com/laracasts/Lets-Build-a-Forum-in-Laravel)
* *本项目为一个 forum（论坛）项目，与本站的第二本实战教程 [Laravel 教程 - Web 开发实战进阶 ( Laravel 5.5 )](https://laravel-china.org/courses/laravel-intermediate-training-5.5) 类似，可互相参照
* 项目开发模式为`TDD`开发，教程简介为：
> A forum is a deceptively complex thing. Sure, it's made up of threads and replies, but what else might exist as part of a forum? What about profiles, or thread subscriptions, or filtering, or real-time notifications? As it turns out, a forum is the perfect project to stretch your programming muscles. In this series, we'll work together to build one with tests from A to Z.
* 项目版本为 **laravel 5.4**，教程后面会进行升级到 **laravel 5.5** 的教学
* 视频教程共计 102 个小节，笔记章节与视频教程一一对应

### 1.本节说明
* 对应视频第 13 小节：Extracting to View Composers

### 2.本节内容
本节的内容比较简单，主要是对前面章节的内容进行优化。
在前面的章节中，我们使用了`App\Channel::all()`这样简单粗暴的办法来获取所有的`channel`，现在让我们更优雅地重写这段代码。我们将使用 **Laravel** 的 [视图共享数据](https://laravel-china.org/docs/laravel/5.5/views#sharing-data-with-all-views) 功能：
> 
如果需要共享一段数据给应用程序的所有视图，你可以在服务提供器的 boot 方法中调用视图 Facade 的 share 方法。例如，可以将它们添加到 AppServiceProvider 或者为它们生成一个单独的服务提供器。

让我们来使用此功能：
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
现在我们可以在视图中直接使用`$channels`变量：
`forum\resources\views\layouts\app.blade.php`
```
.
.
<ul class="dropdown-menu">
	@foreach($channels as $channel)  -->此处使用 $channels
		<li><a href="/threads/{{ $channel->slug }}">{{ $channel->name }}</a> </li>
	@endforeach
</ul>
.
.
```
`forum\resources\views\threads\create.blade.php`
```
.
.
<div class="form-group">
	<label for="channel_id">Choose a Channel</label>
	<select name="channel_id" id="channel_id" class="form-control" required>
		<option value="">Choose One...</option>
		@foreach($channels as $channel)  -->此处使用 $channels
			<option value="{{ $channel->id }}" {{ old('channel_id') == $channel->id ? 'selected' : ''}}>
				{{ $channel->name }}
			</option>
		@endforeach
	</select>
</div>
.
.
```
访问页面，一切如常：
![file](https://lccdn.phphub.org/uploads/images/201805/03/19192/ngzE4cLVXt.png?imageView2/2/w/1240/h/0)
### 3.笔记心得
获取所有`channel`的另一个办法是在控制器里给视图传递`$thread`变量，如：
```
public function create()
{
	$channels = Channel::all();
	return view('threads.create',compact('channels'));
}
```

### 4.写在后面
* 如有建议或意见，欢迎指出~
* 如果觉得文章写的不错，请点赞鼓励下哈，你的鼓励将是我的动力！
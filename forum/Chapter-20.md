### 0.写在前面
* 本系列文章为`laracasts.com` 的系列视频教程——[Let's Build A Forum with Laravel and TDD](https://laracasts.com/series/lets-build-a-forum-with-laravel) 的学习笔记。若喜欢该系列视频，可去该网站订阅后下载该系列视频，*** 支持正版 ***。
* 视频源码地址：[https://github.com/laracasts/Lets-Build-a-Forum-in-Laravel](http://https://github.com/laracasts/Lets-Build-a-Forum-in-Laravel)
* *本项目为一个 forum（论坛）项目，与本站的第二本实战教程 [Laravel 教程 - Web 开发实战进阶 ( Laravel 5.5 )](https://laravel-china.org/courses/laravel-intermediate-training-5.5) 类似，可互相参照
* 项目开发模式为`TDD`开发，教程简介为：
> A forum is a deceptively complex thing. Sure, it's made up of threads and replies, but what else might exist as part of a forum? What about profiles, or thread subscriptions, or filtering, or real-time notifications? As it turns out, a forum is the perfect project to stretch your programming muscles. In this series, we'll work together to build one with tests from A to Z.
* 项目版本为 **laravel 5.4**，教程后面会进行升级到 **laravel 5.5** 的教学
* 视频教程共计 102 个小节，笔记章节与视频教程一一对应

### 1.本节说明
* 对应视频第 20 小节：From 56 Queries Down to 2

### 2.本节内容
此时我们的页面存在很大的 性能隐患，为了能更直观地看到问题，我们先安装 Laravel 开发者工具类 - [laravel-debugbar](https://github.com/barryvdh/laravel-debugbar)。由于我们的选择的 **Laravel** 为 5.4 版本，所以我们使用以下方式安装：
```
$ composer require barryvdh/laravel-debugbar:~2.4
```
安装完成后xu'y需要进行注册。当前环境是本地环境时才开启：
`forum\app\Providers\AppServiceProvider.php`
```
public function register()
{
	if($this->app->isLocal()){
		$this->app->register(\Barryvdh\Debugbar\ServiceProvider::class);
	}
}
```
此时刷新话题列表页面：
![file](https://lccdn.phphub.org/uploads/images/201805/13/19192/tAnguTf8qM.png?imageView2/2/w/1240/h/0)
向下滚动会发现很多`sql`都是类似下面这样的：
```
select * from `channels` where `channels`.`id` = '1' limit 1
```
问题出在导航栏获取`channnel`时，我们使用了循环语句去获取：
```
.
.
@ foreach($channels as $channel)
	<li><a href="/threads/{{ $channel->slug }}">{{ $channel->name }}</a> </li>
@endforeach
.
.
```
我们将使用 [预加载](https://laravel-china.org/docs/laravel/5.4/eloquent-relationships/1265#eager-loading) 功能解决这个问题：
`forum\app\Http\Controllers\ThreadsController.php`
```
.
.
protected function getThreads(Channel $channel, ThreadsFilters $filters)
{
	$threads = Thread::with('channel')->latest()->filter($filters);  -->注意此处

	if ($channel->exists) {
		$threads->where('channel_id', $channel->id);
	}

	$threads = $threads->get();
	return $threads;
}
.
.
```
方法`with()`提前加载了我们后面需要用到的关联属性`channel`，并做了缓存。后面即使是在遍历数据时使用到这个关联属性，数据已经被预加载并缓存，因此不会再产生多余的 SQL 查询：
![file](https://lccdn.phphub.org/uploads/images/201805/13/19192/UdKsozjfle.png?imageView2/2/w/1240/h/0)
如果你仔细观察图片显示的内容，你就会发现：我们使用了两次以下的`sql`语句：
![file](https://lccdn.phphub.org/uploads/images/201805/13/19192/FkgZNxh1PO.png?imageView2/2/w/1240/h/0)
我们可以看到，是在`app\Providers\AppServiceProvider.php`文件中发生了两次同样的查询：
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
在我们的项目中，`chanels`属于不会经常变动的数据，所以我们可以选择使用缓存机制来优化：
```
.
.
public function boot()
{
	Carbon::setLocale('zh');
	\View::composer('*',function ($view){
		$channels = \Cache::rememberForever('channels',function (){
		   return Channel::all(); 
		});
	   $view->with('channels',$channels);
	});
}
.
.
```
再次刷新页面：
![file](https://lccdn.phphub.org/uploads/images/201805/13/19192/vzSmRfdvXJ.png?imageView2/2/w/1240/h/0)
接下来我们来优化话题详情页面的性能问题。访问一个话题详情页面，可以看到：
![file](https://lccdn.phphub.org/uploads/images/201805/13/19192/3yz5nBgcfk.png?imageView2/2/w/1240/h/0)
目前我们存在两个问题需要优化：
1. 重复的`select * from users where users.id = '51' limit 1`语句；
2. 获取回复的`count(*)`语句

首先我们看一下详情页面回复区域的代码：
`forum\resources\views\threads\reply.blade.php`
```
<div class="panel panel-default">
    <div class="panel-heading">
        <div class="level">
            <h5 class="flex">
                <a href="#"> {{ $reply->owner->name }}</a>
                回复于
                {{ $reply->created_at->diffForHumans() }}
            </h5>

            <div>
                <form method="POST" action="/replies/{{ $reply->id }}/favorites">
                    {{ csrf_field() }}

                    <button type="submit" class="btn btn-default" {{ $reply->isFavorited() ? 'disabled' : '' }}>
                        {{ $reply->favorites()->count() }} {{ str_plural('Favorite',$reply->favorites()->count()) }}
                    </button>
                </form>
            </div>
        </div>
    </div>

    <div class="panel-body">
        {{ $reply->body }}
    </div>
</div>
```
导致第一个问题的代码：
```
$reply->owner->name
```
导致第二个问题的代码：
```
{{ $reply->favorites()->count() }} {{ str_plural('Favorite',$reply->favorites()->count()) }}
```
我们可以利用模型关联的 [关联数据计数](https://laravel-china.org/docs/laravel/5.4/eloquent-relationships/1265#189bfd) 功能，使用`withCount`方法，此方法会在结果集中增加一个`favorites_count `字段：
`forum\app\Thread.php`
```
.
.
public function replies()
{
	return $this->hasMany(Reply::class)
		->withCount('favorites');
}
.
.
```
在页面应用：
```
.
.
<form method="POST" action="/replies/{{ $reply->id }}/favorites">
	{{ csrf_field() }}

	<button type="submit" class="btn btn-default" {{ $reply->isFavorited() ? 'disabled' : '' }}>
		{{ $reply->favorites_count }} {{ str_plural('Favorite',$reply->favorites_count) }}
	</button>
</form>
.
.
```
再次刷新页面，可以看到`sql`语句数量已大幅减少：
![file](https://lccdn.phphub.org/uploads/images/201805/13/19192/lfGGFg80BJ.png?imageView2/2/w/1240/h/0)
使用预加载功能解决第一个问题：
```
.
.
public function replies()
{
	return $this->hasMany(Reply::class)
		->withCount('favorites')
		->with('owner');
}
.
.
```
再次刷新页面：
![file](https://lccdn.phphub.org/uploads/images/201805/13/19192/hhMZqKAER0.png?imageView2/2/w/1240/h/0)
如果你仔细观察，会发现任然有重复的`sql`语句，我们将在下一节修复它。
### 3.写在后面
* 如有建议或意见，欢迎指出~
* 如果觉得文章写的不错，请点赞鼓励下哈，你的鼓励将是我的动力！
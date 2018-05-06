### 0.写在前面
* 本系列文章为`laracasts.com` 的系列视频教程——[Let's Build A Forum with Laravel and TDD](https://laracasts.com/series/lets-build-a-forum-with-laravel) 的学习笔记。若喜欢该系列视频，可去该网站订阅后下载该系列视频，*** 支持正版 ***。
* 视频源码地址：[https://github.com/laracasts/Lets-Build-a-Forum-in-Laravel](http://https://github.com/laracasts/Lets-Build-a-Forum-in-Laravel)
* *本项目为一个 forum（论坛）项目，与本站的第二本实战教程 [Laravel 教程 - Web 开发实战进阶 ( Laravel 5.5 )](https://laravel-china.org/courses/laravel-intermediate-training-5.5) 类似，可互相参照
* 项目开发模式为`TDD`开发，教程简介为：
> A forum is a deceptively complex thing. Sure, it's made up of threads and replies, but what else might exist as part of a forum? What about profiles, or thread subscriptions, or filtering, or real-time notifications? As it turns out, a forum is the perfect project to stretch your programming muscles. In this series, we'll work together to build one with tests from A to Z.
* 项目版本为 **laravel 5.4**，教程后面会进行升级到 **laravel 5.5** 的教学
* 视频教程共计 102 个小节，笔记章节与视频教程一一对应

### 1.本节说明
* 对应视频第 16 小节：Meta Infomation and Pagination

### 2.本节内容
首先我们改变一下话题显示的布局，如下：
![file](https://lccdn.phphub.org/uploads/images/201805/06/19192/Y7gh9ztZnj.png?imageView2/2/w/1240/h/0)
修改布局文件如下：
`forum\resources\views\threads\show.blade.php`
```
@extends('layouts.app')

@section('content')
    <div class="container">
        <div class="row">
            <div class="col-md-8">
                <div class="panel panel-default">
                    <div class="panel-heading">
                        <a href="#">{{ $thread->creator->name }}</a>
                        {{ $thread->title }}
                    </div>

                    <div class="panel-body">
                        {{ $thread->body }}
                    </div>
                </div>

                @foreach ($thread->replies as $reply)
                    @include('threads.reply')
                @endforeach

                @if (auth()->check())
                    <form method="post" action="{{ $thread->path() . '/replies' }}">

                        {{ csrf_field() }}

                        <div class="form-group">
                            <textarea name="body" id="body" class="form-control" placeholder="说点什么吧..."rows="5"></textarea>
                        </div>

                        <button type="submit" class="btn btn-default">提交</button>
                    </form>
                @else
                    <p class="text-center">请先<a href="{{ route('login') }}">登录</a>，然后再发表回复 </p>
                @endif
            </div>

            <div class="col-md-4">
                <div class="panel panel-default">
                    <div class="panel-body">
                        <p>
                            <a href="#">{{ $thread->creator->name }}</a> 发布于 {{ $thread->created_at->diffForHumans() }},
                            当前共有 {{ $thread->replies()->count() }} 个回复。
                        </p>
                    </div>
                </div>
            </div>
        </div>
    </div>
@endsection
```
> 注意`$thread->replies()`跟`$thread->replies`的区别：`$thread->replies()`返回的是一个`hasMany`对象，而`$thread->replies`返回的是一个`Collection`集合。

在本项目中，我们不仅想在`show`页面显示，而在`index`页面也进行显示。我们利用 **Laravel** [全局作用域](https://laravel-china.org/docs/laravel/5.5/eloquent#global-scopes) 来实现。
`forum\app\Thread.php`
```

.
.
class Thread extends Model
{
    protected $guarded = [];

    protected static function boot()
    {
        parent::boot();

        static::addGlobalScope('replyCount',function ($builder){
           $builder->withCount('replies');
        });
    }
	.
	.
```
现在如果我们将`$thread`打印出来：
![file](https://lccdn.phphub.org/uploads/images/201805/06/19192/zGuyekd9yu.png?imageView2/2/w/1240/h/0)
可以看到`$thread`多了一个`replies_count`属性。现在可以通过`$thread->replies_count`获取属性的方式获取回复数：
`forum\resources\views\threads\show.blade.php`
```
.
.
当前共有 {{ $thread->replies_count }} 个回复。
.
.
```
最后，我们来为回复加上分页参数：
`forum\app\Http\Controllers\ThreadsController.php`
```
.
.
public function show($channelId,Thread $thread)
{
	return view('threads.show',[
		'thread' => $thread,
		'replies' => $thread->replies()->paginate(10)
	]);
}
.
.
```
前端调用：
`forum\resources\views\threads\show.blade.php`
```
.
.
@foreach ($replies as $reply)
	@include('threads.reply')
@endforeach

{{ $replies->links() }}
.
.
```
> 注：我们将每页回复数设为 10 ，所以当前无法看到分页效果。可将回复数设置为 1 查看效果。

### 3.写在后面
* 如有建议或意见，欢迎指出~
* 如果觉得文章写的不错，请点赞鼓励下哈，你的鼓励将是我的动力！
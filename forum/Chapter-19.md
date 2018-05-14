### 0.写在前面
* 本系列文章为`laracasts.com` 的系列视频教程——[Let's Build A Forum with Laravel and TDD](https://laracasts.com/series/lets-build-a-forum-with-laravel) 的学习笔记。若喜欢该系列视频，可去该网站订阅后下载该系列视频，*** 支持正版 ***。
* 视频源码地址：[https://github.com/laracasts/Lets-Build-a-Forum-in-Laravel](http://https://github.com/laracasts/Lets-Build-a-Forum-in-Laravel)
* *本项目为一个 forum（论坛）项目，与本站的第二本实战教程 [Laravel 教程 - Web 开发实战进阶 ( Laravel 5.5 )](https://laravel-china.org/courses/laravel-intermediate-training-5.5) 类似，可互相参照
* 项目开发模式为`TDD`开发，教程简介为：
> A forum is a deceptively complex thing. Sure, it's made up of threads and replies, but what else might exist as part of a forum? What about profiles, or thread subscriptions, or filtering, or real-time notifications? As it turns out, a forum is the perfect project to stretch your programming muscles. In this series, we'll work together to build one with tests from A to Z.
* 项目版本为 **laravel 5.4**，教程后面会进行升级到 **laravel 5.5** 的教学
* 视频教程共计 102 个小节，笔记章节与视频教程一一对应

### 1.本节说明
* 对应视频第 19 小节：The Favorite Button

### 2.本节内容
本节我们继续来实现对回复的 **点赞** 功能。首先我们在回复后面加上 `Favorite`按钮：
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
                    <button type="submit" class="btn btn-default">Favorites</button>
                </form>
            </div>
        </div>
    </div>

    <div class="panel-body">
        {{ $reply->body }}
    </div>
</div>
```
刷新页面即可看到效果：
![file](https://lccdn.phphub.org/uploads/images/201805/13/19192/moimVybEgQ.png?imageView2/2/w/1240/h/0)
我们还需要修改一下控制器，在做完点赞的动作后重定向至前以页面：
`forum\app\Http\Controllers\FavoritesController.php`
```
<?php

namespace App\Http\Controllers;

use App\Reply;
use Illuminate\Http\Request;

class FavoritesController extends Controller
{
    public function __construct()
    {
        $this->middleware('auth');
    }

    public function store(Reply $reply)
    {
        $reply->favorite();

         return back();
    }
}
```
运行迁移，建立表：
```
$ php artisan migrate
```
现在返回页面，刷新后点击`Favorite`按钮：
![file](https://lccdn.phphub.org/uploads/images/201805/13/19192/pUP8sFVkCI.png?imageView2/2/w/1240/h/0)
会发现页面刷新，查看数据库表中已经存在一条记录：
![file](https://lccdn.phphub.org/uploads/images/201805/13/19192/yDzUtoPbbr.png?imageView2/2/w/1240/h/0)
这说明我们的提交动作已经成功。现在要做的是将结果显示出来：
`C:\Users\meiyiming\Code\forum\resources\views\threads\reply.blade.php`
```
.
.
<form method="POST" action="/replies/{{ $reply->id }}/favorites">
	{{ csrf_field() }}

	<button type="submit" class="btn btn-default" {{ $reply->isFavorited() ? 'disabled' : '' }}>
		{{ $reply->favorites()->count() }} {{ str_plural('Favorite',$reply->favorites()->count()) }}
	</button>
</form>
.
.
```
我们使用了`isFavorited()`来判断当前登录用户是否已经进行过点赞行为：
`C:\Users\meiyiming\Code\forum\app\Reply.php`
```
.
.
public function isFavorited()
{
	return $this->favorites()->where('user_id',auth()->id())->exists();
}
.
```
再次刷新页面：
![file](https://lccdn.phphub.org/uploads/images/201805/13/19192/jcRSWIo16m.png?imageView2/2/w/1240/h/0)
### 3.写在后面
* 如有建议或意见，欢迎指出~
* 如果觉得文章写的不错，请点赞鼓励下哈，你的鼓励将是我的动力！
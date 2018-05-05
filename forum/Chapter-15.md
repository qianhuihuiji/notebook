### 0.写在前面
* 本系列文章为`laracasts.com` 的系列视频教程——[Let's Build A Forum with Laravel and TDD](https://laracasts.com/series/lets-build-a-forum-with-laravel) 的学习笔记。若喜欢该系列视频，可去该网站订阅后下载该系列视频，*** 支持正版 ***。
* 视频源码地址：[https://github.com/laracasts/Lets-Build-a-Forum-in-Laravel](http://https://github.com/laracasts/Lets-Build-a-Forum-in-Laravel)
* *本项目为一个 forum（论坛）项目，与本站的第二本实战教程 [Laravel 教程 - Web 开发实战进阶 ( Laravel 5.5 )](https://laravel-china.org/courses/laravel-intermediate-training-5.5) 类似，可互相参照
* 项目开发模式为`TDD`开发，教程简介为：
> A forum is a deceptively complex thing. Sure, it's made up of threads and replies, but what else might exist as part of a forum? What about profiles, or thread subscriptions, or filtering, or real-time notifications? As it turns out, a forum is the perfect project to stretch your programming muscles. In this series, we'll work together to build one with tests from A to Z.
* 项目版本为 **laravel 5.4**，教程后面会进行升级到 **laravel 5.5** 的教学
* 视频教程共计 102 个小节，笔记章节与视频教程一一对应

### 1.本节说明
* 对应视频第 15 小节：A Lesson in Refactoring

### 2.本节内容
本节让我们来对之前不那么优雅的代码进行重构。很多人都不喜欢进行重构，一个很重要的原因就是害怕重构了代码之后，其他地方会发生错误。可是对于我们来说，在重构的时候完全不用担心这一点。因为我们有测试来帮我们保证已有功能不会被破坏：
![file](https://lccdn.phphub.org/uploads/images/201805/05/19192/ahYRYwbNCq.png?imageView2/2/w/1240/h/0)
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
现在我们需要对上面这段代码进行重构，通常的一个方法是将获取`$threads`的代码片段抽取出来：
```
.
.
public function index(Channel $channel)
{
	$threads = $this->getThreads($channel);

	return view('threads.index',compact('threads'));
}
.
.
protected function getThreads(Channel $channel)
{
	if ($channel->exists) {
		$threads = $channel->threads()->latest();
	} else {
		$threads = Thread::latest();
	}

	if ($username = request('by')) {
		$user = \App\User::where('name', $username)->firstOrFail();

		$threads->where('user_id', $user->id);
	}

	$threads = $threads->get();
	return $threads;
}
```
运行测试，功能任然完整：
![file](https://lccdn.phphub.org/uploads/images/201805/05/19192/97Z2MGgRHb.png?imageView2/2/w/1240/h/0)
但是我们不准备这么做。因为在后面我们可能会根据不同的条件进行筛选，仅仅像上面那样，功能太单一，无法满足我们的需求。
首先新建`forum\app\Filters\ThreadsFilters.php`：
```
<?php

namespace App\Filters;

class ThreadsFilters
{

}
```
重新重构`ThreadsController.php`
```
.
.
use App\Filters\ThreadsFilters;
.
.
public function index(Channel $channel,ThreadsFilters $filters)
{
	if ($channel->exists) {
		$threads = $channel->threads()->latest();
	} else {
		$threads = Thread::latest();
	}

	$threads = $threads->filter($filters)->get();


	return view('threads.index',compact('threads'));
}
.
.
```
我们在头部引用了`ThreadsFilters`类，并且注入到`index()`方法的参数中。通过调用模型层的`filter()`方法，获取到相应筛选条件下的`$htreads`。此时`filter()`方法还不存在，接下来新建`filter()`方法：
`forum\app\Thread.php`
```
.
.
public function scopeFilter($query,$filters)
{
	return $filters->apply($query);
}
.
```
> 注：这里我们使用了 Laravel [本地作用域 ](https://laravel-china.org/docs/laravel/5.5/eloquent#local-scopes)。本地作用域允许我们定义通用的约束集合以便在应用中复用。要定义这样的一个作用域，只需简单在对应 Eloquent 模型方法前加上一个 scope 前缀，作用域总是返回 [查询构建器](https://laravel-china.org/docs/laravel/5.5/queries)。一旦定义了作用域，则可以在查询模型时调用作用域方法。在进行方法调用时不需要加上 scope 前缀。如以上代码中的 filter() 。

接下来只需补充完整`ThreadsFilters.php`即可：
```
<?php

namespace App\Filters;

use App\User;
use Illuminate\Http\Request;

class ThreadsFilters
{
    protected $request;

    public function __construct(Request $request)
    {
        $this->request = $request;
    }

    public function apply($builder)
    {
        if(! $username = $this->request->by) return $builder;

        $user = User::where('name',$username)->firstOrfail();

        return $builder->where('user_id',$user->id);
    }
}
```
运行测试：
![file](https://lccdn.phphub.org/uploads/images/201805/05/19192/CnuKL9eI7o.png?imageView2/2/w/1240/h/0)
测试全部通过，这意味着一切正常。刷新页面：
![file](https://lccdn.phphub.org/uploads/images/201805/05/19192/X43hnDjNI8.png?imageView2/2/w/1240/h/0)
让我们继续重构：
`forum\app\Filters\ThreadsFilters.php`
```
<?php

namespace App\Filters;

use App\User;
use Illuminate\Http\Request;

class ThreadsFilters
{
    protected $request;
    protected $builder;

    public function __construct(Request $request)
    {
        $this->request = $request;
    }

    public function apply($builder)
    {
        $this->builder = $builder;

        if(! $username = $this->request->by) return $builder;

        return $this->by($username);
    }

    /**
     * @param $username
     * @return mixed
     */
    protected function by($username)
    {
        $user = User::where('name', $username)->firstOrfail();

        return $this->builder->where('user_id', $user->id);
    }
}
```
运行测试：
![file](https://lccdn.phphub.org/uploads/images/201805/05/19192/I28eC3zmTC.png?imageView2/2/w/1240/h/0)
一切正常，但是我们任然可以继续重构：
 `forum\app\Filters\ThreadsFilters.php`
```
.
.
public function apply($builder)
{
	$this->builder = $builder;

	if($this->request->has('by')){
		$this->by($this->request->by);
	}

	return $this->builder;
}
.
.
```
运行测试：
![file](https://lccdn.phphub.org/uploads/images/201805/05/19192/SEbye0tmFw.png?imageView2/2/w/1240/h/0)
如果我们想在后面根据其他的条件进行筛选，那我们几乎需要重写一遍`ThreadsFilters.php`。我们不希望这么做，所以我们继续重构：
 `forum\app\Filters\ThreadsFilters.php`
 ```
 <?php

namespace App\Filters;

use App\User;

class ThreadsFilters extends Filters
{
    protected $filters = ['by'];

    /**
     * @param $username
     * @return mixed
     */
    protected function by($username)
    {
        $user = User::where('name', $username)->firstOrfail();

        return $this->builder->where('user_id', $user->id);
    }
}
 ```
 并且新建`Filters.php`基类文件：
 `forum\app\Filters\Filters.php`
 ```
 <?php

namespace App\Filters;

use Illuminate\Http\Request;

abstract class Filters
{

    protected $request,$builder;
    protected $filters = [];

    public function __construct(Request $request)
    {
        $this->request = $request;
    }

    /**
     * @param $builder
     * @return mixed
     */
    public function apply($builder)
    {
        $this->builder = $builder;

        foreach ($this->filters as $filter){
            if( ! $this->hasFilter($filter)) return;

            $this->$filter($this->request->$filter);

        }

        return $this->builder;
    }

    /**
     * @param $filter
     * @return bool
     */
    protected function hasFilter($filter)
    {
        return method_exists($this, $filter) && $this->request->has($filter);
    }
}
 ```
我们新建了基类`Filters` ，并且将可复用的代码抽取到了基类中，使得`ThreadsFilters`十分简洁。运行测试：
![file](https://lccdn.phphub.org/uploads/images/201805/05/19192/pwljOxdr0Z.png?imageView2/2/w/1240/h/0)
依然一切正常。现在访问 [http://forum.test/threads?by=NoNo1](http://forum.test/threads?by=NoNo1&bad=thing) 这样的地址已经正常，但是我们想让在用户访问 [http://forum.test/threads?by=NoNo1&bad=thing](http://forum.test/threads?by=NoNo1&bad=thing) 这样的地址的时候，将其他的请求参数过滤，只保留下`by=NoNo1`让我们进行筛选。所以继续重构，使用 [only](https://laravel-china.org/docs/laravel/5.5/collections#method-only) 方法达到我们的目的：
`forum\app\Filters\Filters.php`
```
<?php

namespace App\Filters;

use Illuminate\Http\Request;

abstract class Filters
{

    protected $request,$builder;
    protected $filters = [];

    public function __construct(Request $request)
    {
        $this->request = $request;
    }

    /**
     * @param $builder
     * @return mixed
     */
    public function apply($builder)
    {
        $this->builder = $builder;

        foreach ($this->getFilters() as $filter => $value){
            if(method_exists($this,$filter)){  // 注：此处是 hasFilter() 方法的重构
                $this->$filter($value);
            }
        }

        return $this->builder;
    }

    public function getFilters()
    {
        return $this->request->only($this->filters);
    }
}
```
> 注：我们重构之后将`hasFilter()`方法去掉了，因为我们有更好的写法。

运行测试：
![file](https://lccdn.phphub.org/uploads/images/201805/05/19192/J6lNtK8VXq.png?imageView2/2/w/1240/h/0)
发现有一个测试未通过，我们将`$this->getFilters()`的值打印出来：
```
.
.
public function getFilters()
{dd($this->request->only($this->filters));
	return $this->request->only($this->filters);
}
.
.
```
![file](https://lccdn.phphub.org/uploads/images/201805/05/19192/LDwqoP4tQ8.png?imageView2/2/w/1240/h/0)
这种情况下是正常的。 但是，当我们访问 [http://forum.test/threads](http://forum.test/threads) 这样的地址时：
![file](https://lccdn.phphub.org/uploads/images/201805/05/19192/pKtm8Vsj4R.png?imageView2/2/w/1240/h/0)
此时`by`的值为`null`，然后我们调用了`null`方法，于是抛出异常。我们希望没有`by`请求参数时，就不进行筛选。[intersect](https://laravel-china.org/docs/laravel/5.5/collections#method-intersect) 方法可以满足我们的需求。改用 intersect 方法：
```
.
.
public function getFilters()
{dd($this->request->intersect($this->filters));
	return $this->request->intersect($this->filters);
}
.
.
```
刷新页面：
![file](https://lccdn.phphub.org/uploads/images/201805/05/19192/7CaPbwKR5B.png?imageView2/2/w/1240/h/0)
去掉`dd()`后再次运行测试：
![file](https://lccdn.phphub.org/uploads/images/201805/05/19192/zpFkAZbaqO.png?imageView2/2/w/1240/h/0)
刷新页面：
![file](https://lccdn.phphub.org/uploads/images/201805/05/19192/fTDcySgLHS.png?imageView2/2/w/1240/h/0)
最后，我们将控制器代码重构：
`forum\app\Http\Controllers\ThreadsController.php`
```
.
.
public function index(Channel $channel,ThreadsFilters $filters)
{
	$threads = Thread::latest()->filter($filters);

	if ($channel->exists) {
		$threads->where('channel_id',$channel->id);
	}

	$threads = $threads->get();

	return view('threads.index',compact('threads'));
}
.
.
```
`filter()`使用的是 Laravel 的 [本地作用域 ](https://laravel-china.org/docs/laravel/5.5/eloquent#local-scopes)，作用域返回 [查询构建器](https://laravel-china.org/docs/laravel/5.5/queries)。所以我们可以很方便地链式调用`where()`方法：
```
$threads->where('channel_id',$channel->id);
```
任然，我们可以继续将获取`$threads`的代码抽取成一个新的方法。继续重构：
```
.
.
public function index(Channel $channel,ThreadsFilters $filters)
{
	$threads = $this->getThreads($channel, $filters);

	return view('threads.index',compact('threads'));
}
.
.
protected function getThreads(Channel $channel, ThreadsFilters $filters)
{
	$threads = Thread::latest()->filter($filters);

	if ($channel->exists) {
		$threads->where('channel_id', $channel->id);
	}

	$threads = $threads->get();
	return $threads;
}
```
最后，运行一下测试：
![file](https://lccdn.phphub.org/uploads/images/201805/05/19192/DwzcO0oDXw.png?imageView2/2/w/1240/h/0)
一切正常，Perfect！
### 3.写在后面
* 如有建议或意见，欢迎指出~
* 如果觉得文章写的不错，请点赞鼓励下哈，你的鼓励将是我的动力！
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
我们引入了`Channel`的概念：一个`Thread`属于一个`Channel`，一个`Channel`拥有多个`Thread`。现在我们来实现根据`Channel`筛选`Thread`的功能。
首先新建测试：
`forum\tests\Feature\ReadThreadsTest.php`
```
.
.
/** @test */
public function a_user_can_filter_threads_according_to_a_tag()
{
	$channel = create('App\Channel');
	$threadInChannel = create('App\Thread',['channel_id' => $channel->id]);
	$threadNotInChanne = create('App\Thread');

	$this->get('/threads/' . $channel->slug)
		->assertSee($threadInChannel->title)
		->assertDontSee($threadNotInChanne->title);
}
.
.
```
在测试用例中，我们新建了一个`Channel`两个`Thread`，其中一个`Thread`的`channel_id`是我们新建`Channel`的`id`。我们的测试是，当我们通过该`Channle`来筛选`Thread`，我们希望看到与该`Channel`相关的`Thread`，并且不看到与该`Channel`无关的`Thread`。
运行测试：
![file](https://lccdn.phphub.org/uploads/images/201805/02/19192/Y0DYIxsJyd.png?imageView2/2/w/1240/h/0)
让我们来修复它：
`forum\routes\web.php`
```
.
.
Route::get('/home', 'HomeController@index')->name('home');
Route::get('threads/{channel}','ThreadsController@index');  -->修改此处路由
Route::get('threads/create','ThreadsController@create');
Route::get('threads/{channel}/{thread}','ThreadsController@show');
Route::post('threads','ThreadsController@store');
Route::post('/threads/{channel}/{thread}/replies','RepliesController@store');
.
.
```
`forum\app\Http\Controllers\ThreadsController.php`
```
.
.
public function index($channelSlug = null)
{
	if($channelSlug){
		$channelId = Channel::where('slug',$channelSlug)->first()->id;

		$threads = Thread::where('channel_id',$channelId)->latest()->get();
	}else{
		$threads = Thread::latest()->get();
	}

	return view('threads.index',compact('threads'));
}
.
.
```
可以看到，我们在控制器中的代码是很粗糙的。我们这么做的原因是为了先让测试通过，稍后进行修改。现在来运行测试：
```
$ APP_ENV=testing phpunit --filter a_user_can_filter_threads_according_to_a_tag
```
测试通过：
![file](https://lccdn.phphub.org/uploads/images/201805/02/19192/9Rw1URAid3.png?imageView2/2/w/1240/h/0)
### 3.写在后面
* 如有建议或意见，欢迎指出~
* 如果觉得文章写的不错，请点赞鼓励下哈，你的鼓励将是我的动力！
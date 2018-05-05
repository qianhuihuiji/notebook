### 0.写在前面

* 本系列文章为`laracasts.com` 的系列视频教程——[Let's Build A Forum with Laravel and TDD](https://laracasts.com/series/lets-build-a-forum-with-laravel) 的学习笔记。若喜欢该系列视频，可去该网站订阅后下载该系列视频，*** 支持正版 ***。
* 视频源码地址：[https://github.com/laracasts/Lets-Build-a-Forum-in-Laravel](http://https://github.com/laracasts/Lets-Build-a-Forum-in-Laravel)
* *本项目为一个 forum（论坛）项目，与本站的第二本实战教程 [Laravel 教程 - Web 开发实战进阶 ( Laravel 5.5 )](https://laravel-china.org/courses/laravel-intermediate-training-5.5) 类似，可互相参照
* 项目开发模式为`TDD`开发，教程简介为：
> A forum is a deceptively complex thing. Sure, it's made up of threads and replies, but what else might exist as part of a forum? What about profiles, or thread subscriptions, or filtering, or real-time notifications? As it turns out, a forum is the perfect project to stretch your programming muscles. In this series, we'll work together to build one with tests from A to Z.
* 项目版本为`laravel 5.4`，教程后面会进行升级到`laravel 5.5`的教学
* 视频教程共计 102 个小节，章节内容与视频教程一一对应

### 1.本节说明
* 对应视频第 9 小节：A Thread Should Be Assigned A Channel

### 2.本节内容
在目前我们对新建`thread`的测试分成了两个：
`guests_may_not_see_the_create_thread_page`和`guests_may_not_create_threads`。
我们可以合并成一个：对于未登录用户来说，无论是访问创建的页面或者进行创建的提交，我们都重定向到登录页面：
`\tests\Feature\CreateThreadsTest.php`
```
<?php

namespace Tests\Feature;

use Tests\TestCase;
use Illuminate\Foundation\Testing\DatabaseMigrations;
class CreateThreadsTest extends TestCase
{
    use DatabaseMigrations;

    /** @test */
    public function guests_may_not_create_threads()
    {
        $this->withExceptionHandling();

        $this->get('/threads/create')
            ->assertRedirect('/login');

        $this->post('/threads')
            ->assertRedirect('/login');
    }

    /** @test */
    public function an_authenticated_user_can_create_new_forum_threads()
    {
        // Given we have a signed in user
        $this->signIn(create('App\User'));  // 已登录用户

        // When we hit the endpoint to cteate a new thread
        $thread = make('App\Thread');
        $this->post('/threads',$thread->toArray());

        // Then,when we visit the thread
        // We should see the new thread
        $this->get($thread->path())
            ->assertSee($thread->title)
            ->assertSee($thread->body);
    }
}

```
运行一下测试：
![file](https://lccdn.phphub.org/uploads/images/201804/30/19192/clXmIj9TVv.png?imageView2/2/w/1240/h/0)
本项目中，我们认为，一个`Thread`属于一个`Channel`。现在让我们来引入`Channel`的概念。
首先建立测试：
`forum\tests\Unit\ThreadTest.php`
```
.
.
/** @test */
function a_thread_belongs_to_a_channel()
{
	$thread = create('App\Thread');
	
	$this->assertInstanceOf('App\Channel',$thread->channel);
}
```
运行测试：
![file](https://lccdn.phphub.org/uploads/images/201804/30/19192/EHbSvZhBGU.png?imageView2/2/w/1240/h/0)
因为我们还没有建立`Channel`的概念。新建`Channel`：
```
$ php artisan make:model Channel -m
```
进行模型关联：
`forum\app\Thread.php`
```
.
.
public function creator()
{
	return $this->belongsTo(User::class,'user_id'); // 使用 user_id 字段进行模型关联
}

public function channel()
{
	return $this->belongsTo(Channel::class);
}
.
.
```
修改迁移文件：
`forum\database\migrations\{timestamp}_create_threads_table.php`
```
.
.
public function up()
{
	Schema::create('threads', function (Blueprint $table) {
		$table->increments('id');
		$table->unsignedInteger('user_id');
		$table->unsignedInteger('channel_id');
		$table->string('title');
		$table->text('body');
		$table->timestamps();
	});
}
.
.
```
`forum\database\migrations\{timestamp}_create_channels_table.php`
```
<?php

use Illuminate\Support\Facades\Schema;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Database\Migrations\Migration;

class CreateChannelsTable extends Migration
{
    /**
     * Run the migrations.
     *
     * @return void
     */
    public function up()
    {
        Schema::create('channels', function (Blueprint $table) {
            $table->increments('id');
            $table->string('name',50);
            $table->string('slug',50);
            $table->timestamps();
        });
    }

    /**
     * Reverse the migrations.
     *
     * @return void
     */
    public function down()
    {
        Schema::dropIfExists('channels');
    }
}

```
修改模型工厂：
`forum\database\factories\ModelFactory.php`
```
.
.
$factory->define(App\Thread::class,function ($faker){
   return [
       'user_id' => function () {
            return factory('App\User')->create()->id;
       },
       'channel_id' => function () {
            return factory('App\Channel')->create()->id;
       },
       'title' => $faker->sentence,
       'body' => $faker->paragraph,
    ];
});

$factory->define(App\Channel::class,function ($faker){
    $name = $faker->word;

    return [
        'name' => $name,
        'slug' => $name,
    ];
});
.
.
```
再次运行测试：
```
$ APP_ENV=testing phpunit --filter a_thread_belongs_to_a_channel
```
成功通过：
![file](https://lccdn.phphub.org/uploads/images/201804/30/19192/yuT2yUk14e.png?imageView2/2/w/1240/h/0)

但是当我们运行完整测试时：
```
$ APP_ENV=testing phpunit
```
会失败：
![file](https://lccdn.phphub.org/uploads/images/201804/30/19192/PdadnuirYg.png?imageView2/2/w/1240/h/0)
我们需要修改一下`ThreadsController.php`的`store()`方法：
```
.
.
public function store(Request $request)
{
	$thread = Thread::create([
		'user_id' => auth()->id(),
		'channel_id' => request('channel_id'),
		'title' => request('title'),
		'body' => request('body'),
	]);

	return redirect($thread->path());
}
.
.
```
再次测试即可成功通过：
![file](https://lccdn.phphub.org/uploads/images/201804/30/19192/C9K3SlImBy.png?imageView2/2/w/1240/h/0)
我们给`Channel`定义了`slug`字段，并且我们期望访问 [http://forum.test/threads/1](http://forum.test/threads/1) 时实际是访问并且显示 [http://forum.test/threads/{channel}/1](http://) 。 现在来编写测试：
`forum\tests\Unit\ThreadTest.php`
```
.
.
public function setUp()
{
	parent::setUp(); // TODO: Change the autogenerated stub

	$this->thread = create('App\Thread');
}

/** @test */
function a_thread_can_make_a_string_path()
{
	$thread = create('App\Thread');

	$this->assertEquals("/threads/{$thread->channel->slug}/{$thread->id}",$thread->path());
}
.
.
```
运行测试：
```
$ APP_ENV=testing phpunit --filter a_thread_can_make_a_string_path
```
测试未通过：
![file](https://lccdn.phphub.org/uploads/images/201804/30/19192/Mr5qRby1Um.png?imageView2/2/w/1240/h/0)
修改`path()`方法：
`forum\app\Thread.php`
```
.
.
public function path()
{
	return "/threads/{$this->channel->slug}/{$this->id}";
}
.
.
```
再次测试即可通过：
![file](https://lccdn.phphub.org/uploads/images/201804/30/19192/lyqu20H75N.png?imageView2/2/w/1240/h/0)
我们的数据库发生了变化，需要重新运行迁移：
```
$ php artisan migrate:refresh
```
接着重新填充数据：
```
$ php artisan tinker
```
进入`tinker`环境后，执行以下命令进行数据填充：
```
>>> factory('App\Thread',50)->create();
```
为了更好地在路由中显示`slug`，我们修改`web.php`如下：
```
<?php

/*
|--------------------------------------------------------------------------
| Web Routes
|--------------------------------------------------------------------------
|
| Here is where you can register web routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| contains the "web" middleware group. Now create something great!
|
*/

Route::get('/', function () {
    return view('welcome');
});

Auth::routes();

Route::get('/home', 'HomeController@index')->name('home');
Route::get('threads','ThreadsController@index');
Route::get('threads/create','ThreadsController@create');
Route::get('threads/{channel}/{thread}','ThreadsController@show');
Route::post('threads','ThreadsController@store');
Route::post('/threads/{thread}/replies','RepliesController@store');
//Route::resource('threads','ThreadsController');


```
修改`show()`方法：
`forum\app\Http\Controllers\ThreadsController.php`
```
.
.
public function show($channelId,Thread $thread)
{
	return view('threads.show',compact('thread'));
}
.
.
```
运行一下测试，看还有那些地方需要修改：
```
$ APP_ENV=testing phpunit
```
![file](https://lccdn.phphub.org/uploads/images/201805/01/19192/8jVzzRCHux.png?imageView2/2/w/1240/h/0)
定位到`an_authenticated_user_can_create_new_forum_threads`方法：
`forum\tests\Feature\CreateThreadsTest.php`
```
.
.
/** @test */
public function an_authenticated_user_can_create_new_forum_threads()
{
	// Given we have a signed in user
	$this->signIn();  // 已登录用户

	// When we hit the endpoint to cteate a new thread
	$thread = make('App\Thread');
	$this->post('/threads',$thread->toArray());
	
	dd($thread->path()); // 打印出路径
	
	// Then,when we visit the thread
	// We should see the new thread
	$this->get($thread->path())
		->assertSee($thread->title)
		->assertSee($thread->body);
}
.
.
```
单独测试：
```
$ APP_ENV=testing phpunit --filter an_authenticated_user_can_create_new_forum_threads
```
![file](https://lccdn.phphub.org/uploads/images/201805/01/19192/0erO42XAn8.png?imageView2/2/w/1240/h/0)
而我们期望的`$thread->path()`应该是`/threads/{channel}/{id}`，少了`id`。如果单独打印`$thread->id`的话，会发现它的值是`null`。究其原因，是因为我们使用了`make()`方法来获取模型实例，但并未将实例存入数据库中。我们改为使用`create()`方法即可：
```
.
.
/** @test */
public function an_authenticated_user_can_create_new_forum_threads()
{
	// Given we have a signed in user
	$this->signIn();  // 已登录用户

	// When we hit the endpoint to cteate a new thread
	$thread = create('App\Thread');
	$this->post('/threads',$thread->toArray());
	
	// Then,when we visit the thread
	// We should see the new thread
	$this->get($thread->path())
		->assertSee($thread->title)
		->assertSee($thread->body);
}
.
.
```
再次运行即可通过：
![file](https://lccdn.phphub.org/uploads/images/201805/01/19192/QVOuMG2LQK.png?imageView2/2/w/1240/h/0)
再次运行全部测试，看还有那些地方需要修改：
```
$ APP_ENV=testing phpunit
```
![file](https://lccdn.phphub.org/uploads/images/201805/01/19192/lbZgbHUWlX.png?imageView2/2/w/1240/h/0)
接着定位到`an_authenticated_user_may_participate_in_forum_threads`方法：
`forum\tests\Feature\ParticipateInForumTest.php`
```
.
.
/** @test */
function an_authenticated_user_may_participate_in_forum_threads()
{
	// Given we have a authenticated user
	$this->signIn();
	// And an existing thread
	$thread = create('App\Thread');

	// When the user adds a reply to the thread
	$reply = make('App\Reply');
	
	dd($thread->path() . '/replies');  // 打印出来
	
	$this->post($thread->path() .'/replies',$reply->toArray());

	// Then their reply should be visible on the page
	$this->get($thread->path())
		->assertSee($reply->body);
}
.
.
```
单独测试：
```
$ APP_ENV=testing phpunit --filter an_authenticated_user_may_participate_in_forum_threads
```
![file](https://lccdn.phphub.org/uploads/images/201805/01/19192/sA88XIwRrv.png?imageView2/2/w/1240/h/0)
会发现`$thread->path()`的形式为`/threads/{channel}/{id}/replies`。修复即可：
`web.php`
```
.
.
Route::post('/threads/{channel}/{thread}/replies','RepliesController@store');
```
`forum\app\Http\Controllers\RepliesController.php`
```
.
.
public function store($channelId,Thread $thread)
{
	$thread->addReply([
		'body' => request('body'),
		'user_id' => auth()->id(),
	]);

	return back();
}
.
.
```
再次测试即可通过：
![file](https://lccdn.phphub.org/uploads/images/201805/01/19192/hbJdB7D1MH.png?imageView2/2/w/1240/h/0)

再次运行全部测试：
```
$ APP_ENV=testing phpunit
```
![file](https://lccdn.phphub.org/uploads/images/201805/01/19192/ziHRbrNMF7.png?imageView2/2/w/1240/h/0)
定位到`unauthenticated_user_may_no_add_replies`
`forum\tests\Feature\ParticipateInForumTest.php`
```
.
.
/** @test */
public function unauthenticated_user_may_no_add_replies()
{
	$this->expectException('Illuminate\Auth\AuthenticationException');

	$this->post('threads/1/replies',[]);
}
.
.
```
在这里，我们的测试逻辑是：未登录用户抛出异常。但是我们真正的测试逻辑应该是：未登录用户试图进行此动作，我们将其重定向至登录页面：
```
.
.
/** @test */
public function unauthenticated_user_may_no_add_replies()
{
	$this->withExceptionHandling()
		->post('threads/some-channel/1/replies',[])
		->assertRedirect('/login');
}
.
.
```
再次运行测试即可成功通过：
![file](https://lccdn.phphub.org/uploads/images/201805/01/19192/H94ldqZXLI.png?imageView2/2/w/1240/h/0)
重新注册用户，然后登录即可看到效果：
![file](https://lccdn.phphub.org/uploads/images/201805/01/19192/zBG3vri7vZ.png?imageView2/2/w/1240/h/0)
### 3.写在后面
* 如有建议或意见，欢迎指出~
* 如果觉得文章写的不错，请点赞鼓励下哈，你的鼓励将是我的动力！
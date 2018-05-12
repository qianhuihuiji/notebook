### 0.写在前面
* 本系列文章为`laracasts.com` 的系列视频教程——[Let's Build A Forum with Laravel and TDD](https://laracasts.com/series/lets-build-a-forum-with-laravel) 的学习笔记。若喜欢该系列视频，可去该网站订阅后下载该系列视频，*** 支持正版 ***。
* 视频源码地址：[https://github.com/laracasts/Lets-Build-a-Forum-in-Laravel](http://https://github.com/laracasts/Lets-Build-a-Forum-in-Laravel)
* *本项目为一个 forum（论坛）项目，与本站的第二本实战教程 [Laravel 教程 - Web 开发实战进阶 ( Laravel 5.5 )](https://laravel-china.org/courses/laravel-intermediate-training-5.5) 类似，可互相参照
* 项目开发模式为`TDD`开发，教程简介为：
> A forum is a deceptively complex thing. Sure, it's made up of threads and replies, but what else might exist as part of a forum? What about profiles, or thread subscriptions, or filtering, or real-time notifications? As it turns out, a forum is the perfect project to stretch your programming muscles. In this series, we'll work together to build one with tests from A to Z.
* 项目版本为 **laravel 5.4**，教程后面会进行升级到 **laravel 5.5** 的教学
* 视频教程共计 102 个小节，笔记章节与视频教程一一对应

### 1.本节说明
* 对应视频第 18 小节：A User Can Favorite Any Reply

### 2.本节内容
本节我们来实现对回复的 **点赞** 功能。首先新建测试文件：
`forum\tests\Feature\FavoritiesTest.php`
```
<?php

namespace Tests\Feature;

use Tests\TestCase;
use Illuminate\Foundation\Testing\DatabaseMigrations;

class FavoritiesTest extends TestCase
{
    use DatabaseMigrations;

    /** @test */
    public function au_authenticated_user_can_favorite_any_reply()
    {
        // If I post a "favorite" endpoint
        // It Should be recored in the database
    }
}

```
根据测试逻辑填充代码：
```
<?php

namespace Tests\Feature;

use Tests\TestCase;
use Illuminate\Foundation\Testing\DatabaseMigrations;

class FavoritiesTest extends TestCase
{
    use DatabaseMigrations;

    /** @test */
    public function au_authenticated_user_can_favorite_any_reply()
    {
        $reply = create('App\Reply');
        
        // If I post a "favorite" endpoint
        $this->post('replies/' . $reply->id . '/favorites');
        
        // It Should be recored in the database
        $this->assertCount(1,$reply->favorites);
    }
}

```
运行测试自然会失败：
![file](https://lccdn.phphub.org/uploads/images/201805/11/19192/HbwrzNYPEy.png?imageView2/2/w/1240/h/0)
这是我们下一步的工作，我们继续。首先新增路由：
`Code\forum\routes\web.php`
```
.
.
Route::post('/replies/{reply}/favorites','FavoritesController@store');
```
接着新建控制器：
```
$ php artisan make:controller FavoritesController
```
`forum\app\Http\Controllers\FavoritesController.php`
```
<?php

namespace App\Http\Controllers;

use App\Reply;
use Illuminate\Http\Request;

class FavoritesController extends Controller
{
    public function store(Reply $reply)
    {
        return \DB::table('favorites')->insert([
           'user_id' => auth()->id(),
           'favorited_id' => $reply->id,
           'favorited_type' => get_class($reply),
        ]);
    }
}
```
再次运行测试：
![file](https://lccdn.phphub.org/uploads/images/201805/11/19192/sXbwG88dto.png?imageView2/2/w/1240/h/0)
接着新建迁移文件：
```
$ php artisan make:migration create_favorites_table --create=favorites
```
`forum\database\migrations\{timestamp}_create_favorites_table.php`
```
<?php

use Illuminate\Support\Facades\Schema;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Database\Migrations\Migration;

class CreateFavoritesTable extends Migration
{
    /**
     * Run the migrations.
     *
     * @return void
     */
    public function up()
    {
        Schema::create('favorites', function (Blueprint $table) {
            $table->increments('id');
            $table->unsignedInteger('user_id');
            $table->unsignedInteger('favorited_id');
            $table->string('favorited_type',50);
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
        Schema::dropIfExists('favorites');
    }
}

```
再次运行测试：
![file](https://lccdn.phphub.org/uploads/images/201805/11/19192/S1ylnXwg9E.png?imageView2/2/w/1240/h/0)
这是因为此时没有登录用户，所以`user_id`的值为`null`，所以我们应该增加一个测试，用来测试未登录用户的情形：
`forum\tests\Feature\FavoritiesTest.php`
```
.
.
use DatabaseMigrations;

/** @test */
public function guests_can_not_favorite_anything()
{
	$this->withExceptionHandling()
		->post('/replies/1/favorites')
		->assertRedirect('/login');
}
.
.
```
运行测试：
![file](https://lccdn.phphub.org/uploads/images/201805/11/19192/p0ES3YIhdm.png?imageView2/2/w/1240/h/0)
让我们来修复它：
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
        return \DB::table('favorites')->insert([
           'user_id' => auth()->id(),
           'favorited_id' => $reply->id,
           'favorited_type' => get_class($reply),
        ]);
    }
}

```
再次测试：
![file](https://lccdn.phphub.org/uploads/images/201805/11/19192/zxY68l5zMK.png?imageView2/2/w/1240/h/0)
成功修复。让我们接着完成未完成的测试：
```
.
.
/** @test */
public function au_authenticated_user_can_favorite_any_reply()
{
	$this->signIn();  -->先登录
	
	$reply = create('App\Reply');

	// If I post a "favorite" endpoint
	$this->post('replies/' . $reply->id . '/favorites');

	// It Should be recored in the database
	$this->assertCount(1,$reply->favorites);
}
.
```
再次运行测试：
![file](https://lccdn.phphub.org/uploads/images/201805/12/19192/8BxBnLIQYT.png?imageView2/2/w/1240/h/0)
给出的信息比较模糊，我们修改一下`Favorites.php`：
```
.
.
public function store(Reply $reply)
{
	 \DB::table('favorites')->insert([
	   'user_id' => auth()->id(),
	   'favorited_id' => $reply->id,
	   'favorited_type' => get_class($reply),
	]);
}
.
```
再次测试：
![file](https://lccdn.phphub.org/uploads/images/201805/12/19192/iDWOdtR8j4.png?imageView2/2/w/1240/h/0)
在我们的测试中，我们使用了`$this->assertCount(1,$reply->favorites)`，上面的问题就是`$reply->favorites`返回了`false`所致：因为我们还未进行模型关联。在我们的项目中，**点赞** 的动作不仅可以对`reply`进行，还能对`thread`等进行。所以我们将使用 **Laravel** 模型的 [多态关联](https://laravel-china.org/docs/laravel/5.5/eloquent-relationships/1333#polymorphic-relations) ：多态关联允许一个模型在单个关联上属于多个其他模型 。
`forum\app\Reply.php`
```
.
.
public function favorites()
{
	return $this->morphMany(Favorite::class,'favorited');
}
.
```
再次测试：
![file](https://lccdn.phphub.org/uploads/images/201805/12/19192/Lcuoq6Q1kL.png?imageView2/2/w/1240/h/0)
这是我们下一步的工作：
```
$ php artisan make:model Favorite
```
`forum\app\Favorite.php`
```
<?php

namespace App;

use Illuminate\Database\Eloquent\Model;

class Favorite extends Model
{
    protected $guarded = [];
}

```
再次测试：
![file](https://lccdn.phphub.org/uploads/images/201805/12/19192/83qZ7eEaU4.png?imageView2/2/w/1240/h/0)
我们重构`store()`方法：
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
         return $reply->favorite();
    }
}
```
我们把保存`favorite`的代码放在`favorite()`中：
`forum\app\Reply.php`
```
.
.
public function favorite()
{
	$this->favorites()->create(['user_id'=>auth()->id()]);
}
.
```
运行测试：
![file](https://lccdn.phphub.org/uploads/images/201805/12/19192/1nSGElCHVZ.png?imageView2/2/w/1240/h/0)
**点赞** 这个动作，每个用户对同一个回复只能进行一次。所以我们需要增加相关测试：
`forum\tests\Feature\FavoritiesTest.php`
```
.
.
/** @test */
public function au_authenticated_user_may_only_favorite_a_reply_once()
{
	$this->signIn();

	$reply = create('App\Reply');

	try{
		$this->post('replies/' . $reply->id . '/favorites');
		$this->post('replies/' . $reply->id . '/favorites');
	}catch (\Exception $e){
		$this->fail('Did not expect to insert the same record set twice.');
	}

	$this->assertCount(1,$reply->favorites);
}
.
```
修改迁移文件，在数据库层面进行控制：
`forum\database\migrations\{timestamp}_create_favorites_table.php`
```
.
.
public function up()
{
	Schema::create('favorites', function (Blueprint $table) {
		$table->increments('id');
		$table->unsignedInteger('user_id');
		$table->unsignedInteger('favorited_id');
		$table->string('favorited_type',50);
		$table->timestamps();

		$table->unique(['user_id','favorited_id','favorited_type']);
	});
}
.
.
```
运行测试：
![file](https://lccdn.phphub.org/uploads/images/201805/12/19192/sqwv2hTApS.png?imageView2/2/w/1240/h/0)
现在我们来修复：
`forum\app\Reply.php`
```
.
.
public function favorite()
{
	$attributes = ['user_id' => auth()->id()];

	if( ! $this->favorites()->where($attributes)->exists()){
		return $this->favorites()->create($attributes);
	}

}
.
```
运行测试：
![file](https://lccdn.phphub.org/uploads/images/201805/12/19192/KfnvIk7ZsG.png?imageView2/2/w/1240/h/0)
最后运行一下全部测试：
![file](https://lccdn.phphub.org/uploads/images/201805/12/19192/4qWB5Ox7QG.png?imageView2/2/w/1240/h/0)
我们将在下一章节完成功能代码的编写。
### 3.写在后面
* 如有建议或意见，欢迎指出~
* 如果觉得文章写的不错，请点赞鼓励下哈，你的鼓励将是我的动力！
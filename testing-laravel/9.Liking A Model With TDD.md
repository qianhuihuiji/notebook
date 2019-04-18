### 本节说明
* 对应第 9 小节：Liking A Model With TDD

### 本节内容
在一个 Laravel 应用中，对一个模型进行点赞或者是收藏是很常见的功能，例如我们可以点赞某个评论，收藏某篇文章。本节我们就来学习如何用 TDD 来开发这一功能。首先我们来对评论进行点赞，我们需要建立`Post`模型：
```
php artisan make:model Post -m
```
修改迁移文件：

*database/migrations/{timestamp}_create_posts_ts_table.php*
``*
```
<?php

use Illuminate\Support\Facades\Schema;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Database\Migrations\Migration;

class CreatePostsTable extends Migration
{
    /**
     * Run the migrations.
     *
     * @return void
     */
    public function up()
    {
        Schema::create('posts', function (Blueprint $table) {
            $table->increments('id');
            $table->unsignedInteger('user_id');
            $table->string('title');
            $table->text('body');
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
        Schema::dropIfExists('posts');
    }
}
```
然后我们来编写我们的第一个测试的思路：

*tests/Unit/it/LikesTest.php*
``*
```
<?php

namespace Tests\Unit;

use Tests\TestCase;
use Illuminate\Foundation\Testing\RefreshDatabase;

class LikesTest extends TestCase
{
    use RefreshDatabase;

    /** @test */
    public function a_user_can_like_a_post()
    {
        // given we have a post
        // and a logged user

        // when the user like a post

        // then we should see evidence in the database, and the post should be liked
    }
}

```

我们按照思路来填充代码：
```
	.
	.
	/** @test */
    public function a_user_can_like_a_post()
    {
        // given we have a post
        $post = factory('App\Post')->create();

        // and a logged user
        $user = factory('App\User')->create();
        $this->actingAs($user);

        // when the user like a post
        $post->like();

        // then we should see evidence in the database, and the post should be liked
        $this->assertDatabaseHas('likes',[
           'user_id' => $user->id,
           'likeable_id' => $post->id,
           'likeable_type' => get_class($post),
        ]);
    }
```
然后运行测试：
![file](e](https://iocaffcdn.phphub.org/uploads/images/201810/28/19192/fKyiHBCrwk.png!/fw/1240)
添加`
添加`like()`方法：

*app/Post.php*
```
<?php

namespace App;

use Illuminate\Database\Eloquent\Model;

class Post extends Model
{
    public function like()
    {

    }
}
```
再次测试：
![file](e](https://iocaffcdn.phphub.org/uploads/images/201810/28/19192/cnfB5rE7Ts.png!/fw/1240)
在我们的设定中，点赞这一行为不仅可以发生评论模型中，还可以发生在其他模型上，所以它与其他模型是 [多态关联](https://laravel-china.org/docs/laravel/5.7/eloquent-relationships/2295#polymorphic-relations) 的。所以我们新建`Like`模型并修改迁移文件如下：

*database\migrations\{timestamp}_create_likes_table.php*
再次运行测试：
![file](https://iocaffcdn.phphub.org/uploads/images/201810/30/19192/G4TSMe592z.png!/fw/1240)
我们没有在数据库存入数据，这就是我们接下来需要做的工作：

*app\Post.php*
再次测试：
![file](https://iocaffcdn.phphub.org/uploads/images/201810/30/19192/67O5upMyvw.png!/fw/1240)
仍旧是批量赋值错误：

*app\Like.php*
```
<?php

namespace App;

use Illuminate\Database\Eloquent\Model;

class Like extends Model
{
    protected $fillable = ['user_id'];
}
```
再次测试：
![file](https://iocaffcdn.phphub.org/uploads/images/201810/30/19192/M8hpMddHI9.png!/fw/1240)
我们再来补充一下我们的测试：断言该评论是`isLiked`。如下：

*tests\Unit\LikesTest.php*
```
	.
	.
	/** @test */
    public function a_user_can_like_a_post()
    {
       .
	   .

        $this->assertTrue($post->isLiked());
    }
}
```
然后添加`isLiked`方法：

*app\Post.php*
```
	.
	.
	public function isLiked()
    {
        return $this->likes()
                    ->where(['user_id' => Auth::id()])
                    ->exists();
    }
}
```
运行测试：
![file](https://iocaffcdn.phphub.org/uploads/images/201810/30/19192/xVSlUfMy3k.png!/fw/1240)
现在我们已经完成了点赞评论功能的开发，相应地，我们来开发取消点赞功能的开发。我们仍旧从测试开始：

*tests\Unit\LikesTest.php*
```
	.
	.
	/** @test */
    public function a_user_can_unlike_a_post()
    {
        $post = factory('App\Post')->create();
        $user = factory('App\User')->create();
        $this->actingAs($user);

        $post->like();
        $post->unlike();

        $this->assertDatabaseMissing('likes',[
           'user_id' => $user->id,
           'likeable_id' => $post->id,
           'likeable_type' => get_class($post),
        ]);

        $this->assertFalse($post->isLiked());
    }
}
```
然后运行测试：
![file](https://iocaffcdn.phphub.org/uploads/images/201810/30/19192/wWn0Ak2HgM.png!/fw/1240)
添加`unlike`方法：

*app\Post.php*
```
	.
	.
	public function like()
    {
        $like = new Like([
            'user_id' => Auth::id()
        ]);

        $this->likes()->save($like);
    }

    public function unlike()
    {
        $this->likes()
            ->where(['user_id' => Auth::id()])
            ->delete();
    }
	.
	.
```
再次测试：
![file](https://iocaffcdn.phphub.org/uploads/images/201810/30/19192/h3vt4vgdle.png!/fw/1240)
继续前进，我们增加一个新的测试：增加一个`toggle`方法，当前评论未被点赞时，点赞该话题；已被点赞时，取消点赞该话题。添加测试：

*tests\Unit\LikesTest.php*
```
	.
	.
	/** @test */
    public function a_user_may_toggle_a_posts_like_status()
    {
        $post = factory('App\Post')->create();

        $user = factory('App\User')->create();
        $this->actingAs($user);

        $post->toggle();
        $this->assertTrue($post->isLiked());

        $post->toggle();
        $this->assertFalse($post->isLiked());
    }
}
```
运行测试：
![file](https://iocaffcdn.phphub.org/uploads/images/201810/30/19192/9VIFQm15rr.png!/fw/1240)
添加`toggle`方法：

*app\Post.php*
```
	.
	.
	public function toggle()
    {
        if($this->isLiked()){
            return $this->unlike();
        }

        return $this->like();
    }
}
```
再次测试：
![file](https://iocaffcdn.phphub.org/uploads/images/201810/30/19192/sUmORFLj5v.png!/fw/1240)
接下来我们再添加一个测试：获取点赞该评论的总数。

*tests\Unit\LikesTest.php*
```
	.
	.
	/** @test */
    public function a_post_knows_how_many_likes_it_has()
    {
        $post = factory('App\Post')->create();

        $user = factory('App\User')->create();
        $this->actingAs($user);

        $post->toggle();
        $this->assertEquals(1,$post->likesCount);
    }
}
```
获取`likesCount`：

*app\Post.php*
```
	.
	.
	public function getLikesCountAttribute()
    {
        return $this->likes()->count();
    }
}
```
>注：我们使用了 [模型访问器](https://laravel-china.org/docs/laravel/5.7/eloquent-mutators/2297#f3b389) 来获取`likesCount`

运行测试：
![file](https://iocaffcdn.phphub.org/uploads/images/201810/30/19192/xiUo0TJXxQ.png!/fw/1240)
代码开发暂时完成，自然我们就可以来做点重构了。在文章开头说过，点赞一个模型是很常见的功能，并且我们的模型也是应用的是多态关联的。所以我们对点赞的行为的代码是可复用的，我们将其抽取成`Trait`：

*app\Likeability.php*
```
<?php

namespace App;

use Auth;

trait Likeability 
{
    public function likes()
    {
        return $this->morphMany(Like::class,'likeable');
    }

    public function like()
    {
        $like = new Like([
            'user_id' => Auth::id()
        ]);

        $this->likes()->save($like);
    }

    public function unlike()
    {
        $this->likes()
            ->where(['user_id' => Auth::id()])
            ->delete();
    }

    public function isLiked()
    {
        return $this->likes()
                    ->where(['user_id' => Auth::id()])
                    ->exists();
    }

    public function toggle()
    {
        if($this->isLiked()){
            return $this->unlike();
        }

        return $this->like();
    }

    public function getLikesCountAttribute()
    {
        return $this->likes()->count();
    }
}
```
然后我们使用`Trait`：

*app\Post.php*
```
<?php

namespace App;

use Illuminate\Database\Eloquent\Model;

class Post extends Model
{
    use Likeability;
    
}
```
然后再次测试：
![file](https://iocaffcdn.phphub.org/uploads/images/201810/30/19192/lQdMvSjYmB.png!/fw/1240)
重构很成功，但是我们的测试也是可以重构的，我们将在下一节对我们的测试进行些重构。不如你先试试？
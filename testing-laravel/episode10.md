### 本节说明
* 对应第 10 小节：Test Method Refactoring

### 本节内容
本节我们来对上一节的测试进行些重构。在上一节的测试中，对于每一个测试，我们都重复了两个动作的代码：
1. 获取`Post`实例；
2. 登录`User`；

我们可以借助`setUp`方法来进行重构：

*tests\Unit\LikesTest.php*
```
<?php

namespace Tests\Unit;

use Tests\TestCase;
use Illuminate\Foundation\Testing\RefreshDatabase;

class LikesTest extends TestCase
{
    use RefreshDatabase;

    public function setUp()
    {
        parent::setUp();

        $this->post = factory('App\Post')->create();
    }
	.
	.
}
```
这样的话，我们获取`Post`实例的代码就不必重复。对于用户登录的操作，在很多的测试中我们都将用的，所以我们可以把逻辑放到`TestCase`基类中：

*tests\TestCase.php*
```
<?php

namespace Tests;

use Illuminate\Foundation\Testing\TestCase as BaseTestCase;

abstract class TestCase extends BaseTestCase
{
    use CreatesApplication;

    protected $user;

    public function signIn($user = null)
    {
        if(! $user){
            $user = factory('App\User')->create();
        }
        
        $this->actingAs($user);

        $this->user = $user;

        return $this;
    }
}
```
最终我们重构后的代码如下：

*tests\Unit\LikesTest.php*
```
<?php

namespace Tests\Unit;

use Tests\TestCase;
use Illuminate\Foundation\Testing\RefreshDatabase;

class LikesTest extends TestCase
{
    use RefreshDatabase;

    public function setUp()
    {
        parent::setUp();

        $this->post = factory('App\Post')->create();

        $this->signIn();
    }

    /** @test */
    public function a_user_can_like_a_post()
    {
        $this->post->like();

        $this->assertDatabaseHas('likes',[
           'user_id' => $this->user->id,
           'likeable_id' => $this->post->id,
           'likeable_type' => get_class($this->post),
        ]);

        $this->assertTrue($this->post->isLiked());
    }

    /** @test */
    public function a_user_can_unlike_a_post()
    {
        $this->post->like();
        $this->post->unlike();

        $this->assertDatabaseMissing('likes',[
           'user_id' => $this->user->id,
           'likeable_id' => $this->post->id,
           'likeable_type' => get_class($this->post),
        ]);

        $this->assertFalse($this->post->isLiked());
    }

    /** @test */
    public function a_user_may_toggle_a_posts_like_status()
    {
        $this->post->toggle();
        $this->assertTrue($this->post->isLiked());

        $this->post->toggle();
        $this->assertFalse($this->post->isLiked());
    }

    /** @test */
    public function a_post_knows_how_many_likes_it_has()
    {
        $this->post->toggle();
        $this->assertEquals(1,$this->post->likesCount);
    }
}
```
然后运行测试：
![file](https://iocaffcdn.phphub.org/uploads/images/201810/31/19192/jmrDefPv79.png!/fw/1240)
当然，我们也可以创建测试用的辅助函数文件，然后将登录用户的代码放到辅助函数文件中。辅助函数文件的好处是可以帮我们简化一些很常用的代码，例如`factory('App\Post')->create()`我们就可以放到辅助函数文件的`create`方法中：

*tests\helpers\functions.php*
```
<?php

function create($class,$attributes = [])
{
    return factory($class)->create($attributes);
}
```
修改`composer.json`文件：
```
	.
	.
	"autoload-dev": {
        "psr-4": {
            "Tests\\": "tests/"
        },
        "files":[
            "tests/helpers/functions.php"
        ]
    },
	.
	.
```
然后自动加载该文件：
```
$ composer dump-autoload
```
现在我们可以像下面这样调用：

```
<?php

namespace Tests\Unit;

use Tests\TestCase;
use Illuminate\Foundation\Testing\RefreshDatabase;

class LikesTest extends TestCase
{
    use RefreshDatabase;

    public function setUp()
    {
        parent::setUp();

        $this->post = create('App\Post');

        $this->signIn();
    }
	.
	.
}
```
再次测试：
![file](https://iocaffcdn.phphub.org/uploads/images/201810/31/19192/TroJpjODy8.png!/fw/1240)
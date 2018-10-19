### 本节说明
* 对应第 6 小节：Hands On Workflow

### 本节内容
本节我们再来学习一个模型的测试流程。假设我们有一个模型`Team`，每个`Team`拥有自己的名字，并且我们可以为每个`Team`添加成员、设置最大组员人数、删除成员和清空成员。我们围绕这些来进行测试，首先我们准备模型：
```
$ php artisan make:model Team -m
```
新建测试并建立我们的第一个测试：
```
$ php artisan make:test TeamTest --unit
```

*tests\Unit\TeamTest.php*
```
<?php

namespace Tests\Unit;

use App\Team;
use Tests\TestCase;
use Illuminate\Foundation\Testing\RefreshDatabase;

class TeamTest extends TestCase
{
    /** @test */
    public function a_team_has_a_name()
    {
        $team = new Team(['name' => 'Acme']);

        $this->assertEquals('Acme',$team->name);
    }
}

```
运行测试：
![file](https://lccdn.phphub.org/uploads/images/201810/19/19192/1R0P20NB5r.png?imageView2/2/w/1240/h/0)
这就是 [批量赋值](https://laravel-china.org/docs/laravel/5.7/eloquent/2294#mass-assignment) 错误。出于安全考虑，所有的 Eloquent 模型在默认情况下都不能进行批量赋值。所以，在开始之前，你应该定义好哪些模型属性是可以被批量赋值的。你可以使用模型上的`fillable`属性来实现。

*app\Team.php*
```
<?php

namespace App;

use Illuminate\Database\Eloquent\Model;

class Team extends Model
{
    protected $fillable = ['name'];
}
```
再次测试：
![file](https://lccdn.phphub.org/uploads/images/201810/19/19192/OY4c2b7rfa.png?imageView2/2/w/1240/h/0)

接下来我们来测试添加成员：

*tests\Unit\TeamTest.php*
```
	.
	.
	/** @test */
    public function a_team_can_add_members()
    {
        $team = factory('App\Team')->create();

        $user = factory('App\User')->create();
        $userTwo = factory('App\User')->create();

        $team->add($user);
        $team->add($userTwo);

        $this->assertCount(2,$team->count());
    }
}
```
我们还需要建立模型工厂：

*database\factories\TeamFactory.php*
```
<?php

use Faker\Generator as Faker;


$factory->define(App\Team::class, function (Faker $faker) {
    return [
        'name' => $faker->sentence,
    ];
});

```
运行测试：
![file](https://lccdn.phphub.org/uploads/images/201810/19/19192/A3a5XUFzCK.png?imageView2/2/w/1240/h/0)
根据报错信息向前推进，修改迁移文件：

**
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
    protected $fillable = ['name','size'];
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

        $this->assertEquals(2,$team->count());
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
		'size' => 5
    ];
});

```
运行测试：
![file](https://lccdn.phphub.org/uploads/images/201810/19/19192/A3a5XUFzCK.png?imageView2/2/w/1240/h/0)
根据报错信息向前推进，修改迁移文件：

*database\migrations\{timestamp}_create_teams_table.php*
```
<?php

use Illuminate\Support\Facades\Schema;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Database\Migrations\Migration;

class CreateTeamsTable extends Migration
{
    /**
     * Run the migrations.
     *
     * @return void
     */
    public function up()
    {
        Schema::create('teams', function (Blueprint $table) {
            $table->increments('id');
            $table->string('name');
            $table->integer('size')->unsigned();
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
        Schema::dropIfExists('teams');
    }
}

```
再次测试：
![file](https://lccdn.phphub.org/uploads/images/201810/22/19192/s2CxQXi43V.png?imageView2/2/w/1240/h/0)
添加`add`方法：

*app\Team.php*
```
<?php

namespace App;

use Illuminate\Database\Eloquent\Model;

class Team extends Model
{
    protected $fillable = ['name','size'];

    public function add($user)
    {
        $this->members()->save($user);
    }

    public function members()
    {
        return $this->hasMany(User::class);
    }

    public function count()
    {
        return $this->members()->count();
    }
}

```

我们定义了模型关联，需要修改`users`表结构：

*database\migrations\2014_10_12_000000_create_users_table.php*
```
	.
	.
	public function up()
    {
        Schema::create('users', function (Blueprint $table) {
            $table->increments('id');
            $table->integer('team_id')->unsigned()->nullable()->index();
            $table->string('name');
            $table->string('email')->unique();
            $table->timestamp('email_verified_at')->nullable();
            $table->string('password');
            $table->rememberToken();
            $table->timestamps();
        });
    }
	.
	.
```

再次运行测试：
![file](https://lccdn.phphub.org/uploads/images/201810/22/19192/INwIUfyYs0.png?imageView2/2/w/1240/h/0)

接下来我们添加第二个测试：设置最大组员人数。当超过最大组员人数时，抛出异常：

*tests\Unit\TeamTest.php*
```
	.
	.
	/** @test */
    public function a_team_has_a_maximum_size()
    {
        $team = factory('App\Team')->create(['size' => 2]);

        $user = factory('App\User')->create();
        $userTwo = factory('App\User')->create();

        $team->add($user);
        $team->add($userTwo);

        $this->assertEquals(2,$team->count());

        $this->expectException('Exception');

        $userThree = factory('App\User')->create();

        $team->add($userThree);
    }
}
```
我们创建了一个最大人数为 2 的`Team`，当我们试图添加第三个组员时，应该抛出异常：

*app\Team.php*
```
<?php

namespace App;

use Illuminate\Database\Eloquent\Model;

class Team extends Model
{
    protected $fillable = ['name','size'];

    public function add($user)
    {
        $this->guardAgainstTooManyMembers();

        $this->members()->save($user);
    }

    public function members()
    {
        return $this->hasMany(User::class);
    }

    public function count()
    {
        return $this->members()->count();
    }

    public function guardAgainstTooManyMembers()
    {
        if($this->members()->count() >= $this->size){
            throw new \Exception;
        }
    }
}
```
运行测试：
![file](https://lccdn.phphub.org/uploads/images/201810/22/19192/5uaxdFbMk0.png?imageView2/2/w/1240/h/0)
让我们再来对添加成员做些改进：既能添加单个成员，也能添加一次性添加多个成员。新增测试：

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

        $this->assertEquals(2,$team->count());
    }

    /** @test */
    public function a_team_can_add_multiple_members_at_once()
    {
        $team = factory('App\Team')->create();

        $users = factory('App\User',2)->create();

        $team->add($users);

        $this->assertEquals(2,$team->count());
    }
	.
	.
```
运行测试当然是不会通过的：
![file](https://lccdn.phphub.org/uploads/images/201810/22/19192/nsq05Y6qLf.png?imageView2/2/w/1240/h/0)

我们来修改`add()`方法：

*app\Team.php*
```
<?php

namespace App;

use Illuminate\Database\Eloquent\Model;

class Team extends Model
{
    protected $fillable = ['name','size'];

    public function add($user)
    {
        $this->guardAgainstTooManyMembers();

        if($user instanceof User) {
            return  $this->members()->save($user);
        }

        $this->members()->saveMany($user);
    }
	.
	.
```
运行测试：
![file](https://lccdn.phphub.org/uploads/images/201810/22/19192/2SDMQyDNsA.png?imageView2/2/w/1240/h/0)
但是我们发现保存用户的两行代码仅仅是方法名有所区别，所以我们来做点重构：

```
.
.
public function add($user)
{
	$this->guardAgainstTooManyMembers();


	$method = $user instanceof User ? 'save' : 'saveMany';

	$this->members()->$method($user);
}
.
.
```
再次测试：
![file](https://lccdn.phphub.org/uploads/images/201810/22/19192/xrzc4y0omL.png?imageView2/2/w/1240/h/0)

然后我们还有两个测试：删除成员和清空成员，这个就当作个人练习，我们下节见。
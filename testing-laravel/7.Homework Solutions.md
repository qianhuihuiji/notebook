### 本节说明
* 对应第 7 小节：Homework Solutions

### 本节内容
上一节我们预留了两个个人练习，本节我们来完成它们。第一个预留的个人练习是单个删除组员，首先我们新建测试：

*tests\Unit\TeamTest.php*
```
	.
	.
	/** @test */
    public function a_team_can_remove_members()
    {
        $team = factory('App\Team')->create();

        $users = factory('App\User',2)->create();

        $team->add($users);

        $team->remove($users[0]);

        $this->assertEquals(1,$team->count());
    }

    /** @test */
    public function a_team_has_a_maximum_size()
	.
	.
```
运行测试：
![file](https://lccdn.phphub.org/uploads/images/201810/22/19192/seUhmnidCn.png?imageView2/2/w/1240/h/0)
OK，添加`remove`方法：

*app\Team.php*
```
	.
	.
	public function add($user)
    {
        .
		.
    }

    public function remove(User $user)
    {
        $user->update(['team_id' => null]);
    }
	.
	.
```
别忘了我们还要设置`team_id`字段可更新：

*app\User.php*
```
	.
	.
	protected $fillable = [
        'name', 'email', 'password','team_id'
    ];
	.
	.
```
运行测试：
![file](https://lccdn.phphub.org/uploads/images/201810/22/19192/sk6j9KlTmZ.png?imageView2/2/w/1240/h/0)
上面的这种设计当然是没问题的，但是如果我们想做到如果该用户被移出某个组时我们通知到用户之类的事情，更好的做法是将该逻辑抽取到`User.php`模型文件中：

*app\User.php*
```
	.
	.
	public function leaveTeam()
    {
        $this->team_id = null;
        $this->save();

        return $this;
    }
}
```
然后调用：

```
.
.
public function remove(User $user)
{
	$user->leaveTeam();
}
.
.
```
再次测试：
![file](https://lccdn.phphub.org/uploads/images/201810/22/19192/yICQKQNsJj.png?imageView2/2/w/1240/h/0)

接下来是第二个练习：清空成员。新建测试：

*tests\Unit\TeamTest.php*
```
	.
	.
	/** @test */
    public function a_team_can_remove_members()
    {
        $team = factory('App\Team')->create();

        $users = factory('App\User',2)->create();

        $team->add($users);

        $team->remove($users[0]);

        $this->assertEquals(1,$team->count());
    }

    /** @test */
    public function a_team_can_remove_all_members_at_once()
    {
        $team = factory('App\Team')->create();

        $users = factory('App\User',2)->create();

        $team->add($users);

        $team->restart($users);

        $this->assertEquals(0,$team->count());
    }.
	.
```
我们添加`restart`方法：

*app\Team.php*
```
.
.
public function remove(User $user)
{
	$user->leaveTeam();
}

public function restart()
{
	$this->members()->update(['team_id' => null]);
}
.
.
```
运行测试：
![file](https://lccdn.phphub.org/uploads/images/201810/22/19192/0mC4TeDZKp.png?imageView2/2/w/1240/h/0)

我们在做的练习的基础上增加一个测试：删除组中的部分成员。新建测试：

*tests\Unit\TeamTest.php*
```
	.
	.
	/** @test */
    public function a_team_can_remove_members()
    {
        $team = factory('App\Team')->create();

        $users = factory('App\User',2)->create();

        $team->add($users);

        $team->remove($users[0]);

        $this->assertEquals(1,$team->count());
    }

    /** @test */
    public function a_team_can_remove_more_than_one_members_at_once()
    {
        $team = factory('App\Team')->create(['size' => 3]);

        $users = factory('App\User',3)->create();

        $team->add($users);

        $team->remove($users->slice(0,2));

        $this->assertEquals(1,$team->count());
    }
	.
	.
```
运行测试：
![file](https://lccdn.phphub.org/uploads/images/201810/22/19192/HlIcFScAhw.png?imageView2/2/w/1240/h/0)
看上去我们需要修改`remove`方法：

*app\Team.php*
```
	.
	.
	public function add($user)
    {
        $this->guardAgainstTooManyMembers();


        $method = $user instanceof User ? 'save' : 'saveMany';

        $this->members()->$method($user);
    }

    public function remove($users)
    {
        if($users instanceof User){
            return $users->leaveTeam();
        }

        return $this->removeMany($users);
    }

    public function removeMany($users)
    {
        return $this->members()
            ->whereIn('id',$users->pluck('id'))
            ->update(['team_id' => null]);
    }
	.
	.
```
再次测试：
![file](https://lccdn.phphub.org/uploads/images/201810/22/19192/sjqkGGbjfB.png?imageView2/2/w/1240/h/0)

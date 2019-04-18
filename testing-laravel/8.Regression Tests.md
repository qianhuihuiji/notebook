### 本节说明
* 对应第 8 小节：Regression Tests

### 本节内容
之前的两小节我们建立`Team`模型，并且编写了一系列测试，测试均已通过。看上去我们的代码没有问题了，然后仍然有 Bug 暗藏于我们的代码当中。在本节我们来学习发现 Bug，然后进行回归测试解决 Bug 的流程。首先我们先看 Bug：

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


        $method = $user instanceof User ? 'save' : 'saveMany';

        $this->members()->$method($user);
    }
	.
	.
```
我们为`Team`设置了最大组员数量，并且在添加**单个**组员时，利用`guardAgainstTooManyMembers`方法确保成员数量不会溢出。然后当我们试图向一个最大组员数量为 3 的`Team`中一次性添加 10 个成员时，我们的代码也是允许的。这就是我们的 Bug。我们来为我们发现的 Bug 场景编写测试：

*tests\Unit\TeamTest.php*
```
	.
	.
	/** @test */
    public function when_adding_many_members_at_once_you_still_may_not_exceed_the_team_maximun_size()
    {
        $team = factory('App\Team')->create(['size' => 2]);

        $users = factory('App\User',3)->create();

        $this->expectException('Exception');

        $team->add($users);
    }
}

```
运行测试：
![file](https://lccdn.phphub.org/uploads/images/201810/24/19192/RCbjxUPzpH.png?imageView2/2/w/1240/h/0)
接下来我们修复 Bug：

*app\Team.php*
```
<?php

namespace App;

use Illuminate\Database\Eloquent\Model;

class Team extends Model
{
    protected $fillable = ['name','size'];

    public function add($users)
    {
        $this->guardAgainstTooManyMembers($users);

        $method = $users instanceof User ? 'save' : 'saveMany';

        $this->members()->$method($users);
    }
	.
	.
    protected function guardAgainstTooManyMembers($users)
    {
        $numUsersToAdd = $users instanceof User ? 1 : count($users);

        $newTeamCount = $this->count() + $numUsersToAdd;

        if($newTeamCount > $this->size){
            throw new \Exception;
        }
    }
}
```
`guardAgainstTooManyMembers`方法中我们先判断已有人数加上准备加入组的人数之和是否超出最大组员数，超出就会抛出异常。运行测试：
![file](https://lccdn.phphub.org/uploads/images/201810/24/19192/UgvrNHtB2a.png?imageView2/2/w/1240/h/0)
现在我们已经经历了一次完整的回归测试的流程：发现 Bug，建立该场景下的测试，修复 Bug。然后我们还需要运行一下全部测试：
![file](https://lccdn.phphub.org/uploads/images/201810/24/19192/u1F1UNVtxG.png?imageView2/2/w/1240/h/0)
在这个时候，你可以做一些重构或者继续推进的工作，只要你的测试仍然通过，我们就有把握保证我们的代码不出 Bug。
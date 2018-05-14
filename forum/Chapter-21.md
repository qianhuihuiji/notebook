### 0.写在前面
* 本系列文章为`laracasts.com` 的系列视频教程——[Let's Build A Forum with Laravel and TDD](https://laracasts.com/series/lets-build-a-forum-with-laravel) 的学习笔记。若喜欢该系列视频，可去该网站订阅后下载该系列视频，*** 支持正版 ***。
* 视频源码地址：[https://github.com/laracasts/Lets-Build-a-Forum-in-Laravel](http://https://github.com/laracasts/Lets-Build-a-Forum-in-Laravel)
* *本项目为一个 forum（论坛）项目，与本站的第二本实战教程 [Laravel 教程 - Web 开发实战进阶 ( Laravel 5.5 )](https://laravel-china.org/courses/laravel-intermediate-training-5.5) 类似，可互相参照
* 项目开发模式为`TDD`开发，教程简介为：
> A forum is a deceptively complex thing. Sure, it's made up of threads and replies, but what else might exist as part of a forum? What about profiles, or thread subscriptions, or filtering, or real-time notifications? As it turns out, a forum is the perfect project to stretch your programming muscles. In this series, we'll work together to build one with tests from A to Z.
* 项目版本为 **laravel 5.4**，教程后面会进行升级到 **laravel 5.5** 的教学
* 视频教程共计 102 个小节，笔记章节与视频教程一一对应

### 1.本节说明
* 对应视频第 21 小节：Global Scopes and Further Query Reduction

### 2.本节内容
接着上一节的内容，我们可以发现当前页面任然存在的性能问题：
![file](https://lccdn.phphub.org/uploads/images/201805/13/19192/0zPPQq15q1.png?imageView2/2/w/1240/h/0)
这是因为我们在回复区域使用了`$reply->isFavorited()`用来检查当前登录用户是否进行过**点赞**行为。我们对`isFavorited()`方法的定义为：
```
.
.
public function isFavorited()
{
	return $this->favorites()->where('user_id',auth()->id())->exists();
}
.
```
所以我们每增加一个回复，`sql`语句就会增加一条：
![file](https://lccdn.phphub.org/uploads/images/201805/13/19192/77ZQuvfpL9.png?imageView2/2/w/1240/h/0)
在我们当前的场景中，每个`reply`我们都想要获取`owner`和`favorites`关联，所以我们可以利用模型的`$with`属性来优化这个问题：
`forum\app\Reply.php`
```
<?php

namespace App;

use Illuminate\Database\Eloquent\Model;

class Reply extends Model
{
    protected $guarded = [];
    protected $with = ['owner','favorites'];  -->注意此处

    public function owner()
    {
        return $this->belongsTo(User::class,'user_id');  // 使用 user_id 字段进行模型关联
    }

    public function favorites()
    {
        return $this->morphMany(Favorite::class,'favorited');  -->注意此处
    }

    public function favorite()
    {
        $attributes = ['user_id' => auth()->id()];

        if( ! $this->favorites()->where($attributes)->exists()){
            return $this->favorites()->create($attributes);
        }

    }

    public function isFavorited()
    {
        return !! $this->favorites->where('user_id',auth()->id())->count();  -->注意此处，修改该后返回的类型是 bool 型
    }
}

```
再次刷新页面，`sql`语句数量已经减少到 8 条：
![file](https://lccdn.phphub.org/uploads/images/201805/14/19192/SDPa5RKylF.png?imageView2/2/w/1240/h/0)
如果再增加一个回复，`sql`语句数量任然是 8 条 ：
![file](https://lccdn.phphub.org/uploads/images/201805/14/19192/PvzthYJPH1.png?imageView2/2/w/1240/h/0)
相同的应用场景，每个`thread`我们都想要获取`creator`关联。同样的办法：
`forum\app\Thread.php`
```
.
.
class Thread extends Model
{
    protected $guarded = [];
    protected $with = ['creator'];
	.
	.
```
如果你对 [查询作用域](https://laravel-china.org/docs/laravel/5.4/eloquent/1264#858495) 有所了解的话，那你应该知道的是，以上我们使用的`$with`属性来获取模型的关联关系其实就是一个 [全局作用域](https://laravel-china.org/docs/laravel/5.4/eloquent/1264#6163c4)。
全局作用域允许我们为给定模型的所有查询添加条件约束，如 Laravel 自带的 [软删除功能](https://laravel-china.org/docs/laravel/5.4/eloquent/1264#ad4448) 就使用了全局作用域来从数据库中拉出所有没有被删除的模型。不过与自定义的全局作用域不同的是，对于自定义的全局作用域，我们可以使用`withoutGlobalScope`为给定查询移除指定全局作用域。而使用了`$with`属性，我们总是会获取关联的数据，并且无法移除。
好了，现在我们还剩下一个问题需要解决：
![file](https://lccdn.phphub.org/uploads/images/201805/14/19192/MZAFZLbSiI.png?imageView2/2/w/1240/h/0)
由于我们修改了代码，所以我们出现了如上的问题。我们使用`getFavoritesCountAttribute()`方法来为模型实例添加`favorites_count`属性：
`forum\app\Reply.php`
```
.
.
public function getFavoritesCountAttribute()
{
	return $this->favorites->count();
}
.
```
再次刷新页面：
![file](https://lccdn.phphub.org/uploads/images/201805/14/19192/A71E1WkxVh.png?imageView2/2/w/1240/h/0)
仔细观察一下，我们发现现在我们在最后还是使用了`select * from channels where channels.id = '1' limit 1`的语句。思考一下，每次查询`thread`时，我们也想要查询`channel`，所以我们可以像处理`creator`一样处理`channel`：
```
.
.
protected $with = ['creator'];
.
.
```
我们为了处理**点赞**这个动作以及后续的优化，使用了 4 个方法：`favorites`，`favorite`，`isFavorited`，`getFavoritesCountAttribute`。为了后期维护，我们将这 4 个方法抽取到`trait`中封装起来，再在`Reply.php`引用即可：
`forum\app\Favoritable.php`
```
<?php

namespace App;

use Illuminate\Database\Eloquent\Model;

trait Favoritable
{

    public function favorites()
    {
        return $this->morphMany(Favorite::class, 'favorited');
    }

    public function favorite()
    {
        $attributes = ['user_id' => auth()->id()];

        if (!$this->favorites()->where($attributes)->exists()) {
            return $this->favorites()->create($attributes);
        }

    }

    public function isFavorited()
    {
        return !!$this->favorites->where('user_id', auth()->id())->count();
    }

    public function getFavoritesCountAttribute()
    {
        return $this->favorites->count();
    }
}
```
再引用：
`forum\app\Reply.php`
```
<?php

namespace App;

use Illuminate\Database\Eloquent\Model;

class Reply extends Model
{
    use Favoritable;

    protected $guarded = [];
    protected $with = ['owner','favorites'];

    public function owner()
    {
        return $this->belongsTo(User::class,'user_id');  // 使用 user_id 字段进行模型关联
    }

}

```
运行一下测试，看功能是否完好：
```
$ APP_ENV=testing phpunit
```
![file](https://lccdn.phphub.org/uploads/images/201805/14/19192/cgT47H32tF.png?imageView2/2/w/1240/h/0)
### 3.写在后面
* 如有建议或意见，欢迎指出~
* 如果觉得文章写的不错，请点赞鼓励下哈，你的鼓励将是我的动力！

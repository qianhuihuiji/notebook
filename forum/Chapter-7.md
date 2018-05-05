### 0.写在前面
* 本系列文章为`laracasts.com` 的系列视频教程——[Let's Build A Forum with Laravel and TDD](https://laracasts.com/series/lets-build-a-forum-with-laravel) 的学习笔记。若喜欢该系列视频，可去该网站订阅后下载该系列视频，*** 支持正版 ***。
* 视频源码地址：[https://github.com/laracasts/Lets-Build-a-Forum-in-Laravel](http://https://github.com/laracasts/Lets-Build-a-Forum-in-Laravel)
* *本项目为一个 forum（论坛）项目，与本站的第二本实战教程 [Laravel 教程 - Web 开发实战进阶 ( Laravel 5.5 )](https://laravel-china.org/courses/laravel-intermediate-training-5.5) 类似，可互相参照
* 项目开发模式为`TDD`开发，教程简介为：
> A forum is a deceptively complex thing. Sure, it's made up of threads and replies, but what else might exist as part of a forum? What about profiles, or thread subscriptions, or filtering, or real-time notifications? As it turns out, a forum is the perfect project to stretch your programming muscles. In this series, we'll work together to build one with tests from A to Z.
* 项目版本为`laravel 5.4`，教程后面会进行升级到`laravel 5.5`的教学
* 视频教程共计 102 个小节，笔记章节与视频教程一一对应

### 1.本节说明
* 对应视频第 7 小节：Let's Make Some Testing Helpers

### 2.本节内容
本节让我们来建立一些测试时用到的辅助函数，方便我们进行编写测试：
修改`composer.json`：
```
.
.
"autoload-dev": {
	"psr-4": {
		"Tests\\": "tests/"
	},
	"files":["tests/utilities/functions.php"]  -->这里增加一行
},
.
.
```
新建`tests/utilities/functions.php`文件，执行命令将该文件加载进来：
```
$ composer dump-autoload
```
我们将获取模型实例这样的方法，例如`create()`、`make()`抽离出来，放到`functions.php`中：
`\tests\utilities\functions.php`
```
<?php

function create($class,$attributes = [])
{
    return factory($class)->create($attributes);
}

function make($class,$attributes = [])
{
    return factory($class)->make($attributes);
}

function raw($class,$attributes = [])
{
    return factory($class)->raw($attributes);
}
```
在很多测试中，我们需要测试用户是否登录。在之前的测试当中，我们使用了`be()`，`actingAs()`方法来得到一个已登录用户。现在我们在`TestCase.php`新建`signIn()`方法，将用户登录的逻辑放在基类文件中：
`\tests\TestCase.php`
```
<?php

namespace Tests;

use Illuminate\Foundation\Testing\TestCase as BaseTestCase;

abstract class TestCase extends BaseTestCase
{
    use CreatesApplication;

    protected function signIn($user = null)
    {
        $user = $user ?: create('App\User');

        $this->actingAs($user);

        return $this;
    }
}

```
现在我们需要重构之前的代码，将像`factory('App\Reply')->create()`这样的代码片段更改为`create('App\Reply')`；`be()`、`actingAs()`方法更改为`signIn()`方法。
对于使用`PHP Storm`的 programmer，我们有建立测试更加便捷的方法： 
1. 新建一个`template`:
![file](https://lccdn.phphub.org/uploads/images/201804/30/19192/cT7SymNXHw.png?imageView2/2/w/1240/h/0)
2. 在相对应的位置填充完模板，在点击`Edit variables`：
![file](https://lccdn.phphub.org/uploads/images/201804/30/19192/q5BLNX7uyR.png?imageView2/2/w/1240/h/0)
3. 定义应用的位置`everywhere`：
![file](https://lccdn.phphub.org/uploads/images/201804/30/19192/GRwjQGq853.png?imageView2/2/w/1240/h/0)
4. 勾上`Reformat according to style`：
![file](https://lccdn.phphub.org/uploads/images/201804/30/19192/O0Tjx592DQ.png?imageView2/2/w/1240/h/0)
5. 最后点击`Apply`应用设置，再点击`OK`：
![file](https://lccdn.phphub.org/uploads/images/201804/30/19192/Asn30FHehL.png?imageView2/2/w/1240/h/0)
建完模板之后，再新建一个 PHP 文件，输入`testclass`后使用`Tab`键即可补齐模板，十分便捷。

### 3.写在后面
* 如有建议或意见，欢迎指出~
* 如果觉得文章写的不错，请点赞鼓励下哈，你的鼓励将是我的动力！
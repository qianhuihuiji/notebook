### 本节说明
* 对应第 1 小节：Introduction to Application Testing

### 本节内容
本节我们来开始学习 Laravel 的测试。首先我们新建项目：
```
$ laravel new testing-laravel
```
>注1：教程中的 Laravel 版本为 5.2，此系列笔记中为 5.7，因此笔记与教程会略有不同

>注2：本笔记将不会包含 Laravel 开发环境搭建内容，如何搭建开发环境请参看 [Laravel 开发环境部署 ](https://laravel-china.org/docs/laravel-development-environment/5.5)

进行了站点关联之后，我们打开浏览器访问 [http://testing-laravel.test](http://testing-laravel.test/)：
![file](https://lccdn.phphub.org/uploads/images/201810/17/19192/QFG5PCBtMt.png?imageView2/2/w/1240/h/0)

Laravel 为我们自带了测试样例：
![file](https://lccdn.phphub.org/uploads/images/201810/17/19192/le6rqob2bi.png?imageView2/2/w/1240/h/0)

我们当然知道下面的测试为什么会成功：

*tests\Feature\ExampleTest.php*
```
.
.
public function testBasicTest()
{
	$response = $this->get('/');

	$response->assertStatus(200);
}
.
.
```
因为 Laravel 已经为我们已经定义好了路由：

*routes\web.php*
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

```
下面我们来开发我们的第一个测试。首先我们编写我们测试逻辑

*tests\Feature\ExampleTest.php*
```
.
.
public function it_goes_to_a_simple_url()
{
	// 1.Visit the url
	// 2.See "You are here."
}
.
.
```
然后我们按照测试逻辑填充测试代码：

*tests\Feature\ExampleTest.php*
```
.
.
class ExampleTest extends TestCase
{
    /** @test */
    public function it_goes_to_a_simple_url()
    {
        $this->get('/fadeback')
            ->assertSee('You are here.');
    }
}
```
定义路由：

*routes\web.php*
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

Route::get('fadeback', function () {
    return 'You are here.';
});

```
运行测试：
![file](https://lccdn.phphub.org/uploads/images/201810/17/19192/lvDM1ZYE5Z.png?imageView2/2/w/1240/h/0)
现在我们已经完成了一个简单的测试的全过程，下一节我们继续前进。
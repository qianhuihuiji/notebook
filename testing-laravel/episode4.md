### 本节说明
* 对应第 4 小节：Testing Eloquent Models

### 本节内容
本节我们来学习测试 Laravel 的`Model`。首先我们进行配置`phpunit.xml`文件：

*phpunit.xml*
```
	.
	.
	.
	<php>
        <env name="APP_ENV" value="testing"/>
        <env name="DB_CONNECTION" value="sqlite"/>
        <env name="DB_DATABASE" value=":memory:"/>
        <env name="BCRYPT_ROUNDS" value="4"/>
        <env name="CACHE_DRIVER" value="array"/>
        <env name="SESSION_DRIVER" value="array"/>
        <env name="QUEUE_CONNECTION" value="sync"/>
        <env name="MAIL_DRIVER" value="array"/>
    </php>
</phpunit>
```
我们在内存中进行测试，这样测试运行的速度会快一些，所以在 database 配置项中我们将使用 `sqlite` 和 `:memory: `选项(Sqlite的内存数据库)。

接下来我们建立`Article`模型和数据库迁移文件：
```
$ php artisan make:model Article -m
```
修改迁移文件：

*database\migrations\{timestamp}_create_articles_table.php*
```
<?php

use Illuminate\Support\Facades\Schema;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Database\Migrations\Migration;

class CreateArticlesTable extends Migration
{
    /**
     * Run the migrations.
     *
     * @return void
     */
    public function up()
    {
        Schema::create('articles', function (Blueprint $table) {
            $table->increments('id');
            $table->string('title');
            $table->integer('reads')->default(0);
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
        Schema::dropIfExists('articles');
    }
}

```
为了简单起见，我们仅定义了两个字段：`title`跟`reads`，即标题与阅读次数。然后我们新建测试：
```
$ php artisan make:test ArticleTest --unit
```
建立模型工厂：

*database\factories\ArticleFactory.php*
```
<?php

use Faker\Generator as Faker;

/*
|--------------------------------------------------------------------------
| Model Factories
|--------------------------------------------------------------------------
|
| This directory should contain each of the model factory definitions for
| your application. Factories provide a convenient way to generate new
| model instances for testing / seeding your application's database.
|
*/

$factory->define(App\Article::class, function (Faker $faker) {
    return [
        'title' => $faker->sentence,
    ];
});

```
现在我们来建立我们的第一个测试，对于我们的测试而言，我们的测试逻辑如下：

*tests\Unit\ArticleTest.php*
```
/** @test */
public function it_fetches_trending_articles()
{
    // Given
    // When
    // Then
}
```
假设我们有一些数据，当我们执行了动作之后，我们需要验证什么。我们按照这个思路来编写测试：
```
<?php

use App\Article;
use Tests\TestCase;
use Illuminate\Foundation\Testing\RefreshDatabase;

class ArticleTest extends TestCase
{
    use RefreshDatabase;

    /** @test */
    public function it_fetches_trending_articles()
    {
        factory('App\Article',2)->create();
        factory('App\Article')->create(['reads' => 10]);
        $mostPopular = factory('App\Article')->create(['reads' => 20]);

        $articles = Article::trending();

        $this->assertEquals($mostPopular->id,$articles->first()->id);
		$this->assertCount(3,$articles);
    }
}
```
现在我们可以来运行测试了：
![file](https://lccdn.phphub.org/uploads/images/201810/19/19192/G9F8ZkCbG5.png?imageView2/2/w/1240/h/0)

我们利用 [本地作用域](https://laravel-china.org/docs/laravel/5.7/eloquent/2294#local-scopes) 来获取最流行的 3 篇文章：

*app\Article.php*
```
<?php

namespace App;

use Illuminate\Database\Eloquent\Model;

class Article extends Model
{
    public function scopeTrending($query,$take = 3)
    {
        return $query->orderBy('reads','desc')->take($take)->get();
    }
}

```
再次测试：
![file](https://lccdn.phphub.org/uploads/images/201810/19/19192/bIhX32iLB6.png?imageView2/2/w/1240/h/0)

现在我们已经学习了测试`model`的一个小例子，正如这个例子而言，测试的内容单一：仅测试了能够获取流行的 3 篇文章，仅此而已。但是这样是必要的，因为你可以准确地通过你的测试定位到测试失败的位置。请记住，不要企图在一个测试中测试很多、很复杂的东西，那样会得不偿失。
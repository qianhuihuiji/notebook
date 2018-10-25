### 本节说明
* 对应第 1 小节：Prime Factors

### 本节内容
现在我们来开始学习 PHP Katas 教程系列。首先我们新建项目：
```
$ laravel new katas
```
Laravel 自带了测试的例子，我们直接运行测试：
```
$ phpunit
```
![file](https://lccdn.phphub.org/uploads/images/201810/25/19192/0eFCgP21x4.png?imageView2/2/w/1240/h/0)
>注：如果提示`No application encryption key has been specified.`错误，需要先执行`php artisan key:generate`。

然后我们来开始我们的第一个 Kata：最小质因子。我们期望获得一个正整数的最小质因子的数字，例如：对于 1，我们期望得到 []；对于 2，我们期望得到 [2]；对于 4，则是 [2,2]；对于 6，则是 [2,3]；对于 20，则是 [2,2,5]；对于 100，则是 [2,2,5,5]...我们仍旧是以 TDD 的开发模型来一步步的完成这个 Kata。首先我们新建测试：
```
$ php artisan make:test PrimeFactorsTest --unit
```
然后建立我们的第一个测试：

*tests\Unit\PrimeFactorsTest.php*
```
<?php

namespace Tests\Unit;

use Tests\TestCase;
use App\PrimeFactors;

class PrimeFactorsTest extends TestCase
{
    public function setUp()
    {
        parent::setUp();

        $this->primeFactors = new PrimeFactors();
    }

    /** @test */
    public function it_returns_an_empty_array_for_1()
    {
        $this->assertEmpty($this->primeFactors->generate(1));
    }
}

```
运行测试：
![file](https://lccdn.phphub.org/uploads/images/201810/25/19192/1r1CNko9ge.png?imageView2/2/w/1240/h/0)
为了快速让测试通过，我们首先粗糙地组织代码：

*app\PrimeFactors.php*
```
<?php

namespace App;

class PrimeFactors
{
    public function generate($number)
    {
        return [];
    }
}

```
再次运行测试：
![file](https://lccdn.phphub.org/uploads/images/201810/25/19192/lnoyIJGbXq.png?imageView2/2/w/1240/h/0)
TDD 开发的工作流为：测试不通过，然后我们尽可能编写少的代码使测试通过，然后推进（或者进行重构）。在实践中，这种工作流是尽可能小的循环。接下来让我们添加新的测试：

```
	.
	.
	/** @test */
    public function it_returns_2_for_2()
    {
        $this->assertEquals([2],$this->primeFactors->generate(2));
    }
}
```
现在运行测试肯定是不会通过的，那么我们如何写尽可能少量的代码使我们的测试通过呢？也许我们可以向下面这样：

```
<?php

namespace App;

class PrimeFactors
{
    public function generate($number)
    {
        if($number > 1) {
            return [2];
        }

        return [];
    }
}

```
然后我们运行测试：
![file](https://lccdn.phphub.org/uploads/images/201810/25/19192/afCCyGzhGy.png?imageView2/2/w/1240/h/0)
这样我们就可以进入新的工作流了。首先编写测试：

```
	.
	.
	/** @test */
    public function it_returns_3_for_3()
    {
        $this->assertEquals([3],$this->primeFactors->generate(3));
    }
}
```
运行测试会失败，然后我们修改代码：

```
<?php

namespace App;

class PrimeFactors
{
    public function generate($number)
    {
        if($number > 2) {
            return [3];
        }


        if($number > 1) {
            return [2];
        }

        return [];
    }
}

```
测试通过：
![file](https://lccdn.phphub.org/uploads/images/201810/25/19192/6X4kUbGm6Q.png?imageView2/2/w/1240/h/0)
然后进入下一个工作流：

*tests\Unit\PrimeFactorsTest.php*
```
	.
	.
	/** @test */
    public function it_returns_2_2_for_4()
    {
        $this->assertEquals([2,2],$this->primeFactors->generate(4));
    }
}
```

*app\PrimeFactors.php*
```
<?php

namespace App;

class PrimeFactors
{
    public function generate($number)
    {
        if($number == 4) {
            return [2,2];
        }

        if($number > 2) {
            return [3];
        }


        if($number > 1) {
            return [2];
        }

        return [];
    }
}

```
测试依然通过，但是我们可以来做点重构了。因为目前我们最大只测试到 4，所以我们暂时考虑用能被 2 整除的测试：

*app\PrimeFactors.php*
```
<?php

namespace App;

class PrimeFactors
{
    public function generate($number)
    {
        $primes = [];

        while($number % 2 ==0){
            $primes[] = 2;

            $number /= 2;
        }

        if($number > 1){
            $primes[] = $number;
        }

        return $primes;
    }
}

```
再次测试仍然通过，证明我们的重构是成功的。但是我们知道我们的重构是有缺陷的，因为当`$number`为 9 时，测试仍然是失败的。没关系，我们继续前进：

```
	.
	.
	/** @test */
    public function it_returns_2_2_for_4()
    {
        $this->assertEquals([2,2],$this->primeFactors->generate(4));
    }

    /** @test */
    public function it_returns_5_for_5()
    {
        $this->assertEquals([5],$this->primeFactors->generate(5));
    }

    /** @test */
    public function it_returns_2_3_for_6()
    {
        $this->assertEquals([2,3],$this->primeFactors->generate(6));
    }
	
	/** @test */
    public function it_returns_2_2_2_for_8()
    {
        $this->assertEquals([2,2,2],$this->primeFactors->generate(8));
    }
	
	/** @test */
    public function it_returns_3_3_for_9()
    {
        $this->assertEquals([3,3],$this->primeFactors->generate(9));
    }
}
```
新增的前 3 个测试仍然是通过的，然而当我们测试 9 时，测试失败了：
![file](https://lccdn.phphub.org/uploads/images/201810/25/19192/faKDCgAHnR.png?imageView2/2/w/1240/h/0)
现在我们需要修改代码了：

```
<?php

namespace App;

class PrimeFactors
{
    public function generate($number)
    {
        $primes = [];
        $candidate = 2;

        while($number > 1)
        {
            while($number % $candidate ==0)
            {
                $primes[] = $candidate;
    
                $number /= $candidate;
            }

            $candidate++;
        }

        return $primes;
    }
}

```
依次整除 2，3...，直到`$number`的值为 1，跳出循环。运行测试：
![file](https://lccdn.phphub.org/uploads/images/201810/25/19192/531L0Ghbc0.png?imageView2/2/w/1240/h/0)
并且，对于新的测试仍然时通过的：

```
	.
	.
	/** @test */
    public function it_returns_2_5_for_10()
    {
        $this->assertEquals([2,5],$this->primeFactors->generate(10));
    }


    /** @test */
    public function it_returns_5_5_for_25()
    {
        $this->assertEquals([5,5],$this->primeFactors->generate(25));
    }

    /** @test */
    public function it_returns_2_2_5_5_for_100()
    {
        $this->assertEquals([2,2,5,5],$this->primeFactors->generate(100));
    }
}
```
运行测试：
![file](https://lccdn.phphub.org/uploads/images/201810/25/19192/bp4dvBB5ON.png?imageView2/2/w/1240/h/0)
然后我们可以来进行重构：

*app\PrimeFactors.php*
```
<?php

namespace App;

class PrimeFactors
{
    public function generate($number)
    {
        $primes = [];

        for($candidate = 2;$number > 1;$candidate++)
        {
            for(;$number % $candidate == 0;$number /= $candidate)
            {
                $primes[] = $candidate;
            }
        }

        return $primes;
    }
}

```
只要我们的测试仍然通过，我们就有足够的信心我们的重构是成功的：
![file](https://lccdn.phphub.org/uploads/images/201810/25/19192/z6QOkdL5Wb.png?imageView2/2/w/1240/h/0)
棒极了，不是吗？
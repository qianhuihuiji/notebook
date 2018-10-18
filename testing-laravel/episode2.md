### 本节说明
* 对应第 2 小节：Begin Unit Test

### 本节内容
这一节我们将开始单元测试。「单元测试」的目的是用于测试函数或方法的正确性。更重要的是，我们可以轻松实现代码逻辑的正确性。首先我们新建一个`Product`类文件：

*app\Product.php*
```
<?php 

namespace App;

class Product 
{
    
}
```
然后我们新建一个单元测试文件，并编写第一个测试：

*tests\Unit\ProductTest.php*
```
<?php

use App\Product;
use Tests\TestCase;

class ProductTest extends TestCase
{
    /** @test */
    public function a_product_has_a_name()
    {
        $product = new Product('fallout 4');

        $this->assertEquals('fallout 4',$product->name());
    }
}
```
运行测试当然是不会通过的：
![file](https://lccdn.phphub.org/uploads/images/201810/17/19192/wWO3OOLAyC.png?imageView2/2/w/1240/h/0)

修改`Product.php`文件：
```
<?php 

namespace App;

class Product 
{
    protected $name;

    public function __construct($name)
    {
        $this->name = $name;
    }

    public function name()
    {
        return $this->name;
    }
}
```
再次测试：
![file](https://lccdn.phphub.org/uploads/images/201810/17/19192/vTT5Ri9r0X.png?imageView2/2/w/1240/h/0)
我们再增加一个测试：

*tests\Unit\ProductTest.php*
```
<?php

use App\Product;
use Tests\TestCase;

class ProductTest extends TestCase
{
    /** @test */
    public function a_product_has_a_name()
    {
        $product = new Product('fallout 4',59);

        $this->assertEquals('fallout 4',$product->name());
    }

    /** @test */
    public function a_product_has_a_price()
    {
        $product = new Product('fallout 4',59);

        $this->assertEquals(59,$product->price());
    }
}
```
相应地，我们要修改`Product.php`文件：
```
<?php 

namespace App;

class Product 
{
    protected $name;
    protected $price;

    public function __construct($name,$price)
    {
        $this->name = $name;
        $this->price = $price;
    }

    public function name()
    {
        return $this->name;
    }

    public function price()
    {
        return $this->price;
    }
}
```
运行测试：
![file](https://lccdn.phphub.org/uploads/images/201810/17/19192/cdCNS20bvQ.png?imageView2/2/w/1240/h/0)

测试通过了，现在我们可以做点重构了。在单元测试中，我们经常需要实例化某个类，比如：
```
$product = new Product('fallout 4',59);
```
这显然违背 DRY 原则，所以我们通常都会把实例化的代码抽取到`setUp`方法中：

*tests\Unit\ProductTest.php*
```
<?php

use App\Product;
use Tests\TestCase;

class ProductTest extends TestCase
{
    protected $product;

    public function setUp()
    {
        parent::setUp();

        $this->product = new Product('fallout 4',59);
    }

    /** @test */
    public function a_product_has_a_name()
    {
        $this->assertEquals('fallout 4',$this->product->name());
    }

    /** @test */
    public function a_product_has_a_price()
    {
        $this->assertEquals(59,$this->product->price());
    }
}
```
再次测试：
![file](https://lccdn.phphub.org/uploads/images/201810/17/19192/dIog9c1U94.png?imageView2/2/w/1240/h/0)
在本节的最后，需要说明一下测试方法的命名问题。我们之所以采用以`_`分割的命名方式，就是为了可读性。尽管这种方式不符合 PSR-2 标准，但是因为我们是在测试中命名函数方法，并不是在业务代码中，所以我们选择了可读性优先。
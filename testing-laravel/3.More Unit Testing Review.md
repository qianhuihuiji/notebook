### 本节说明
* 对应第 3 小节：More Unit Testing Review

### 本节内容
本节我们接着上一节的内容，在已有`Product`的概念上，建立对`Order`概念的测试。首先我们新建测试：
```
$ php artisan make:test OrderTest --unit
```
>注：`$`表示在虚拟机环境下

建立第一个测试：

*tests\Unit\OrderTest.php*
```
<?php

namespace Tests\Unit;

use App\Product;
use App\Order;
use Tests\TestCase;

class OrderTest extends TestCase
{
    /** @test */
    public function an_order_sonsists_of_products()
    {
        $order = new Order;

        $product = new Product('Fallout 4',59);
        $produc2 = new Product('Pillowcase',7);

        $order->add($product);
        $order->add($product2);

        $this->assertEquals(2,count($order->products()));
    }
}
```
必须要说明地是，我们先于业务代码开发了测试代码，但是没关系，这正是我们的目的所在，也是 TDD 的开发理念。我们运行测试：
![file](https://lccdn.phphub.org/uploads/images/201810/18/19192/F46iPOKlaT.png?imageView2/2/w/1240/h/0)
继续前进，新建`Order`类文件：

*app\Order.php*
```
<?php 

namespace App;

class Order 
{
    
}
```
再次运行测试：
![file](https://lccdn.phphub.org/uploads/images/201810/18/19192/WMyI3yggUX.png?imageView2/2/w/1240/h/0)
需要注意地是，我们在进行测试时，要一小步一小步地前进，只写少量的代码，确保继续推进即可。可以看到报错类型已经改变了，我们继续前进：

*app\Order.php*
```
<?php 

namespace App;

class Order 
{
    public function add()
    {
        
    }
}
```
运行测试：
![file](https://lccdn.phphub.org/uploads/images/201810/18/19192/Cl0APDoCcV.png?imageView2/2/w/1240/h/0)
继续前进：

*app\Order.php*
```
<?php 

namespace App;

class Order 
{
    public function add()
    {
        
    }

    public function products()
    {
        
    }
}
```
运行测试：
![file](https://lccdn.phphub.org/uploads/images/201810/18/19192/MCH98DiPXL.png?imageView2/2/w/1240/h/0)
继续前进：

*app\Order.php*
```
<?php 

namespace App;

class Order 
{
    protected $products = [];

    public function add(Product $product)
    {
        $this->products[] = $product;
    }

    public function products()
    {
        return $this->products;
    }
}
```
再次测试：
![file](https://lccdn.phphub.org/uploads/images/201810/18/19192/bHbkxDlrT4.png?imageView2/2/w/1240/h/0)

我们知道，每个`Product`都有单价，那么`Order`应该有总价。我们建立第二个测试：

*tests\Unit\OrderTest.php*
```
<?php

namespace Tests\Unit;

use App\Product;
use App\Order;
use Tests\TestCase;

class OrderTest extends TestCase
{
    /** @test */
    public function an_order_sonsists_of_products()
    {
        $order = new Order;

        $product = new Product('Fallout 4',59);
        $product2 = new Product('Pillowcase',7);

        $order->add($product);
        $order->add($product2);

        $this->assertCount(2,$order->products());
    }

    /** @test */
    public function an_order_can_determine_the_total_cost_of_all_its_products()
    {
        $order = new Order;

        $product = new Product('Fallout 4',59);
        $product2 = new Product('Pillowcase',7);

        $order->add($product);
        $order->add($product2);

        $this->assertEquals(66,$order->total());
    }
}

```
运行测试：
![file](https://lccdn.phphub.org/uploads/images/201810/18/19192/jZWHIeaTD5.png?imageView2/2/w/1240/h/0)
向前推进：

*app\Order.php*
```
<?php 

namespace App;

class Order 
{
    protected $products = [];
    protected $total = 0;

    public function add(Product $product)
    {
        $this->products[] = $product;
        $this->total += $product->price();
    }

    public function products()
    {
        return $this->products;
    }

    public function total()
    {
        return $this->total;
    }
}
```
运行测试：
![file](https://lccdn.phphub.org/uploads/images/201810/18/19192/z8yK7k0v7v.png?imageView2/2/w/1240/h/0)
接下来让我们来做点重构，因为我们总是在重复实例化`Order`：

*tests\Unit\OrderTest.php*
```
<?php

namespace Tests\Unit;

use App\Product;
use App\Order;
use Tests\TestCase;

class OrderTest extends TestCase
{
    /** @test */
    public function an_order_sonsists_of_products()
    {
        $order = $this->createOrderWithProducts();

        $this->assertCount(2,$order->products());
    }

    /** @test */
    public function an_order_can_determine_the_total_cost_of_all_its_products()
    {
        $order = $this->createOrderWithProducts();

        $this->assertEquals(66,$order->total());
    }

    protected function createOrderWithProducts()
    {
        $order = new Order;

        $product = new Product('Fallout 4',59);
        $product2 = new Product('Pillowcase',7);

        $order->add($product);
        $order->add($product2);

        return $order;
    }
}

```
再次运行测试，检验我们的重构是否成功：
![file](https://lccdn.phphub.org/uploads/images/201810/18/19192/wFsN0duuVE.png?imageView2/2/w/1240/h/0)
重构成功了，下一节继续。
### 本节说明

* 对应第 4 小节：Dispatchable

### 本节内容

 本节我们来探究下 `dispatch()` 辅助函数，在函数定义处我们可以看到：

 ```
if (! function_exists('dispatch')) {
    /**
     * Dispatch a job to its appropriate handler.
     *
     * @param  mixed  $job
     * @return \Illuminate\Foundation\Bus\PendingDispatch
     */
    function dispatch($job)
    {
        if ($job instanceof Closure) {
            $job = new CallQueuedClosure(new SerializableClosure($job));
        }

        return new PendingDispatch($job);
    }
}
 ```

 可以看到，函数会返回一个 `PendingDispatch` 类的实例化对象。同时我们还应该知道的是，在上一节我们生成的 `Job` 中，`Laravel` 默认为我们引入了 `Dispatchable` Trait

 ```
.
.
class ReconcileAccount implements ShouldQueue
{
    use Dispatchable, InteractsWithQueue, Queueable, SerializesModels;
    .
    .
}
 ```

 在`Dispatchable` Trait 中，`Laravel` 也提供了 `dispatch()` 方法：

 *vendor/laravel/framework/src/Illuminate/Foundation/Events/Dispatchable.php*

```
.
.
public static function dispatch()
{
    return new PendingDispatch(new static(...func_get_args()));
}
.
.
```

与全局辅助函数 `dispatch()` 的作用是一样的，所以你可以像下面这样来使用：

```
<?php

use App\Jobs\ReconcileAccount;
use App\User;

Route::get('/', function () {
    $user = User::first();
    
    ReconcileAccount::dispatch($user);

    return 'Finished!';
});
```

当然你也可以选择全局辅助函数 `dispatch()`，如果是这样的话，你就可以去掉 `Dispatchable` Trait 的引入。

下一节我们来看一看如何处理失败的队列任务。
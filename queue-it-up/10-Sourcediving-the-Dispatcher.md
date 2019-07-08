### 本节说明

* 对应第 10 小节：Sourcediving the Dispatcher

### 本节内容

本节我们来开始探究队列任务的实现原理。首先我们恢复代码到第 8 节的样子：

*routes/web.php*

```
<?php

use App\Jobs\ReconcileAccount;
use App\User;

Route::get('/', function () {
    $user = User::first();

    ReconcileAccount::dispatch($user);

    return 'Done';
});
```

*app/Jobs/ReconcileAccount.php*

```
<?php

namespace App\Jobs;

use App\User;
use Illuminate\Filesystem\Filesystem;
use Illuminate\Bus\Queueable;
use Illuminate\Queue\SerializesModels;
use Illuminate\Queue\InteractsWithQueue;
use Illuminate\Contracts\Queue\ShouldQueue;
use Illuminate\Foundation\Bus\Dispatchable;

class ReconcileAccount implements ShouldQueue
{
    use Dispatchable, InteractsWithQueue, Queueable, SerializesModels;

    protected $user;

    /**
     * Create a new job instance.
     *
     * @return void
     */
    public function __construct(User $user)
    {
        $this->user = $user;
    }

    /**
     * Execute the job.
     *
     * @return void
     */
    public function handle()
    {
        logger('Record a user:' . $this->user->name);
    }

    public function tags()
    {
        return ['accounter'];;
    }
}
```

别忘了现在的队列驱动是 `database`,并且需要执行 `php artisan queue:work` 才会执行队任务。
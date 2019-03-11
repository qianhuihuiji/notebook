### 本节说明
* 对应第 1 小节：Get it Running Locally

### 本节内容
作为开发人员, 要提高, 你必须专注三件事: 学习、阅读和写作，即
* 向比你更有经验的人学习;
* 阅读大量优秀的代码;
* 每日练习。

本系列将重点介绍关注于 **阅读代码**，我们将以 [laravel.com](https://laravel.com) 作为起点，开始我们的这个系列。[laravel.com](https://laravel.com) 本身就是一个开源项目，首先我们克隆项目到本地：

```
git clone https://github.com/laravel/laravel.com.git
```

然后进入该目录，并执行以下命令安装项目依赖：

```
composer install
npm install
```

需要注意地是，现在的项目是不包含`.env`文件的，我们需要自己添加，并且填写创建 `laravel` 项目时生成的默认配置即可：

```
APP_NAME=Laravel
APP_ENV=local
APP_KEY=
APP_DEBUG=true
APP_URL=http://localhost

LOG_CHANNEL=stack

DB_CONNECTION=mysql
DB_HOST=127.0.0.1
DB_PORT=3306
DB_DATABASE=homestead
DB_USERNAME=homestead
DB_PASSWORD=secret

BROADCAST_DRIVER=log
CACHE_DRIVER=file
QUEUE_CONNECTION=sync
SESSION_DRIVER=file
SESSION_LIFETIME=120

REDIS_HOST=127.0.0.1
REDIS_PASSWORD=null
REDIS_PORT=6379

MAIL_DRIVER=smtp
MAIL_HOST=smtp.mailtrap.io
MAIL_PORT=2525
MAIL_USERNAME=null
MAIL_PASSWORD=null
MAIL_ENCRYPTION=null

AWS_ACCESS_KEY_ID=
AWS_SECRET_ACCESS_KEY=
AWS_DEFAULT_REGION=us-east-1
AWS_BUCKET=

PUSHER_APP_ID=
PUSHER_APP_KEY=
PUSHER_APP_SECRET=
PUSHER_APP_CLUSTER=mt1

MIX_PUSHER_APP_KEY="${PUSHER_APP_KEY}"
MIX_PUSHER_APP_CLUSTER="${PUSHER_APP_CLUSTER}"
```

然后我们打开浏览器访问 [http://laravel.com.test](http://laravel.com.test) :

![file](https://iocaffcdn.phphub.org/uploads/images/201903/05/19192/wpNqtFYSWL.png!large)

>注：站点部署请参看 [Laravel 开发环境部署](https://learnku.com/docs/laravel-development-environment/5.7)

注意看报错信息，我们需要执行以下命令：

```
php artisan key:generate
```

刷新页面：

![file](https://iocaffcdn.phphub.org/uploads/images/201903/05/19192/vSxyVLIFWU.png!large)

但是当我们点击 `Documentation` 时，会提示页面不存在。这是因为我们还没有下载文档。我们去 [文档中心](https://github.com/laravel/docs) 下载 `5.8` 版本的文档到 `resources/docs/5.8` 目录下。首先我们需要创建该目录：

```
mkdir -p resources/docs/5.8
```

然后我们进入该目录：

```
cd resources/docs/5.8
```
克隆文档：

```
git clone https://github.com/laravel/docs.git
```

并且我们还需要移动文档文件到 `resources/docs/5.8` 目录下：

```
mv docs/** ./
```

再次刷新页面：

![file](https://iocaffcdn.phphub.org/uploads/images/201903/05/19192/pjIfDuPLyb.png!large)

现在我们在本地将项目运行起来了，但是我们的目标是 `Read Code`，我们需要探索项目的代码结构，路由规则，以及项目是如何运行的，所以下一节我们继续前进！

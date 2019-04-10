### 本节说明
* 对应第 16 小节：Webpack and Vue-CLI
* 因视频教程早于本文，因此本章节有较大不同，但不影响学习

### 本节内容

[Webpack](https://www.webpackjs.com/)  是一个前端资源加载/打包工具。它将根据模块的依赖关系进行静态分析，然后将这些模块按照指定的规则生成对应的静态资源：
![file](https://iocaffcdn.phphub.org/uploads/images/201902/27/19192/aij5QUGb1p.png!large)

可以使用下列任一命令安装：
```
npm install -g @vue/cli
# OR
yarn global add @vue/cli
```
安装完成后查看版本：
```
$ vue -V
```
显示如下：

![16.Webpack & Vue-CLI](https://iocaffcdn.phphub.org/uploads/images/201904/10/19192/cSEvVEkQJl.png!large)

>如果安装失败，请尝试卸载 `vue/cli` 后加上`sudo`重新安装

然后我们创建一个名为`my-app`的项目：
```
$ vue create my-app
```
然后进入`my-app`项目，进行编译：
```
$ npm run serve
```
编译成功后可以看到以下界面：
![file](https://iocaffcdn.phphub.org/uploads/images/201902/27/19192/0wyPsH8DUn.png!large)
然后我们可以在进入 [http://192.168.10.10:8080/](http://192.168.10.10:8080) 看到以下页面：
![file](https://iocaffcdn.phphub.org/uploads/images/201902/27/19192/AePNLS3WYY.png!large)

接下来我们新建`Message.vue`组件：
*src\components\Message.vue*
```
<template>
    <div class="box">
        <slot></slot>
    </div>
</template>

<script>

    export default {

    }

</script>

<style>

    .box { background: #e3e3e3; padding: 10px; border: 1px solid #c5c5c5; margin-bottom: 1em}

</style>

```
在`App.vue`组件中使用：
*src\App.vue*
```
<template>
  <div id="app">
    <message>Test Apple</message>
    <message>Test Oooo</message>
  </div>
</template>

<script>

import Message from "./components/Message.vue";

export default {
  name: 'app',

  components: {
    Message
  }
}
</script>

```
编译后你会看到如下页面：
![file](https://iocaffcdn.phphub.org/uploads/images/201902/28/19192/76Y2bRaLjN.png!large)


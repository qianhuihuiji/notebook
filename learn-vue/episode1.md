###  写在前面
* 本系列文章为`laracasts.com` 的系列视频教程——[Learn Vue 2: Step By Step](https://laracasts.com/series/learn-vue-2-step-by-step) 的学习笔记。若喜欢该系列视频，可去该网站订阅后下载该系列视频，*** 支持正版 ***。
* 视频源码地址：[https://github.com/laracasts/Vue-Forms](https://github.com/laracasts/Vue-Forms)
* 项目版本为`Vue 2.1.3`，教程还在更新中。

### 本节说明
* 对应第 1 小节：Basic Data Binding


### 本节内容
现在我们来开始学习 Vue 2 教程系列。首先我们需要安装 `Vue` 2.1.3 版本，并进行简单的数据绑定。简单起见，我们通过 cdn 的方式引入：
```
<script src="https://unpkg.com/vue@2.1.3/dist/vue.js"></script>
```
通常我们使用`JavaScript`方式绑定数据时会是如下写法：
*learn-vue\index.html*
```
<!DOCTYPE html>

<html>
    <head>
        <title></title>
    </head>

    <body>
        <input type="text" id="input">

        <script src="https://unpkg.com/vue@2.1.3/dist/vue.js"></script>

        <script>
            let data = {
                message: "Hello world!"
            };

            document.querySelector('#input').value = data.message;
        </script>
    </body>
</html>
```
我们打开浏览器：
![file](https://lccdn.phphub.org/uploads/images/201810/12/19192/utTgIoibBR.png?imageView2/2/w/1240/h/0)
数据已经绑定成功了，但是在 vue 中，我们将会是这样：
```
<!DOCTYPE html>

<html>
    <head>
        <title></title>
    </head>

    <body>
        <div id="root">
            <input type="text" id="input" v-model="message">

            <p>the value of the input is {{ message }}.</p>
        </div>

        <script src="https://unpkg.com/vue@2.1.3/dist/vue.js"></script>

        <script>
            new Vue({
                el:'#root',
                data: {
                    message: "Hello world!"
                }
            });
        </script>
    </body>
</html>
```
我们为 `id`为`root`的元素创建了一个 `Vue`实例，并将`message`绑定给`input`：
![file](https://lccdn.phphub.org/uploads/images/201810/12/19192/byu52YUOtz.png?imageView2/2/w/1240/h/0)
现在数据和 DOM 已经被建立了关联，所有东西都是**响应式**的。我们来确认一下：
```
.
.
<div id="root">
            <input type="text" id="input" v-model="message">

            <p>the value of the input is {{ message }}.</p>
        </div>
		.
		.
```
![file](https://lccdn.phphub.org/uploads/images/201810/12/19192/oydgt21GcV.gif?imageView2/2/w/1240/h/0)


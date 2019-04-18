### 本节说明
* 对应第 5 小节：Attributes and Class Name Binding


### 本节内容
我们可以传给 `v-bind:class` 一个对象，以动态地切换 class：

*index.html*
```
<!DOCTYPE html>

<html>
    <head>
        <style>
            .is-loading{
                background: red;
            }
        </style>
    </head>

    <body>
        <div id="root">
            <button :class="{ 'is-loading' : isLoading }">Click me</button>
        </div>

        <script src="https://unpkg.com/vue@2.1.3/dist/vue.js"></script>

        <script>
            var app = new Vue({
                el:'#root',

                data: {
                    isLoading:false,
                }
            });
        </script>
    </body>
</html>
```
>注：:class 为 v-bind:class 的简写方式

注意下面这行代码：
```
<button :class="{ 'is-loading' : isLoading }">Click me</button>
```
上面的语法表示 active 这个 class 存在与否将取决于数据属性`isLoading`是 `true`还是`false`。我们默认为`false`，当我们改为`true`时，class 会生效。我们查看效果：
![file](https://lccdn.phphub.org/uploads/images/201810/13/19192/QbsmEjEQdE.gif?imageView2/2/w/1240/h/0)
接下来我们点击按钮添加 class ：
```
.
.
<body>
        <div id="root">
            <button :class="{ 'is-loading' : isLoading }" @click="toggleClass">Toggle class</button>
        </div>

        <script src="https://unpkg.com/vue@2.1.3/dist/vue.js"></script>

        <script>
            var app = new Vue({
                el:'#root',

                data: {
                    isLoading:false,
                },

                methods:{
                    toggleClass() {
                        this.isLoading = true;
                    }
                }
            });
        </script>
    </body>
```
查看效果：
![file](https://lccdn.phphub.org/uploads/images/201810/13/19192/u7l32zsfoA.gif?imageView2/2/w/1240/h/0)
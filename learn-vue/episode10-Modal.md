### 本节说明
* 对应第 10 小节：Practical Component Exercise 2：Modal

### 本节内容
接下来来做第二个练习：`Modal`组件，我们的例子从 [Bulma](https://bulma.io/documentation/components/modal/) 网站选取。我们需要引入样式跟组件：

*index.html*
```
<!DOCTYPE html>

<html>
    <head>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bulma/0.2.3/css/bulma.css">
        <style type="text/css">
            body{
                padding-top: 40px;
            }
        </style>
    </head>

    <body>
        <div id="root" class="container">
            <div class="modal is-active">
                <div class="modal-background"></div>
                <div class="modal-content">
                    <div class="box">Something is happening...</div>
                </div>
                <button class="modal-close is-large" aria-label="close"></button>
            </div>
        </div>

        <script src="https://unpkg.com/vue@2.1.3/dist/vue.js"></script>

        <script src="main.js"></script>
    </body>
</html>
```
现在打开浏览器：
![file](https://lccdn.phphub.org/uploads/images/201810/15/19192/5IBguKt2bw.png?imageView2/2/w/1240/h/0)
接下来我们要做到事情是：将`Modal`抽取成组件，在页面点击按钮显示组件，点击组件中的删除图标（图片右上角）隐藏组件。我们先来完成前两个步骤：

*main.js*
```
Vue.component('modal',{
    template:`
    <div class="modal is-active">
        <div class="modal-background"></div>
        <div class="modal-content">
            <div class="box">
                <slot></slot>
            </div>
        </div>
        <button class="modal-close is-large" aria-label="close"></button>
    </div>
    `
});

new Vue({
    el:'#root',

    data:{
        showModal:false
    }
});
```
我们默认组件不显示，当点击`Show Modal`按钮之后再显示：

*index.html*
```
<!DOCTYPE html>

<html>
    <head>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bulma/0.2.3/css/bulma.css">
        <style type="text/css">
            body{
                padding-top: 40px;
            }
        </style>
    </head>

    <body>
        <div id="root" class="container">
            <modal v-if="showModal">Something is happening</modal>

            <button @click="showModal = true">show Modal</button>
        </div>

        <script src="https://unpkg.com/vue@2.1.3/dist/vue.js"></script>

        <script src="main.js"></script>
    </body>
</html>
```
现在查看效果：
![file](https://lccdn.phphub.org/uploads/images/201810/15/19192/Pk9O54HTxk.gif?imageView2/2/w/1240/h/0)
然后我们来完成第三步。我们利用`Vue`的 [$emit](https://cn.vuejs.org/v2/api/#%E5%AE%9E%E4%BE%8B%E6%96%B9%E6%B3%95-%E4%BA%8B%E4%BB%B6) 触发当前实例上的`close`事件，在`close`事件中，我们设置`showModal`属性为`false`：

*main.js*
```
Vue.component('modal',{
    template:`
    <div class="modal is-active">
        <div class="modal-background"></div>
        <div class="modal-content">
            <div class="box">
                <slot></slot>
            </div>
        </div>
        <button class="modal-close is-large" aria-label="close" @click="$emit('close')"></button>
    </div>
    `
});

new Vue({
    el:'#root',

    data:{
        showModal:false
    }
});
```
*index.html*
```
.
.
<div id="root" class="container">
	<modal v-if="showModal" @close="showModal = false">Something is happening</modal>

	<button @click="showModal = true">show Modal</button>
</div>
	.
	.
```
最终效果：
![file](https://lccdn.phphub.org/uploads/images/201810/15/19192/DsuYhOLLPL.gif?imageView2/2/w/1240/h/0)


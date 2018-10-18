### 本节说明
* 对应第 14 小节：Named Slots in Nutshell

### 本节内容
在之前的小节中，我们曾经利用`Vue`的`<slot>`元素来显示组件中的插值。但是我们并没有将插值细分成例如`title`、`content`等更细粒度的元素，本节我们来做这样的事。我们的例子从 [Bulma](https://bulma.io/documentation/components/modal/) 网站选取，引入样式跟组件：

*index.html*
```
<!DOCTYPE html>

<html>
    <head>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bulma/0.2.3/css/bulma.css">
    </head>

    <body>
        <div id="root" class="container">
            <modal></modal>
        </div>

        <script src="https://unpkg.com/vue@2.1.3/dist/vue.js"></script>

        <script src="main.js"></script>
    </body>
</html>
```
编写组件：
```
  Vue.component('modal',{
    template:`
    <div class="modal is-active">
        <div class="modal-background"></div>
        <div class="modal-card">
            <header class="modal-card-head">
                <p class="modal-card-title">Modal title</p>
                <button class="delete" aria-label="close"></button>
            </header>
            <section class="modal-card-body">
                Temparary content for now
            </section>
            <footer class="modal-card-foot">
                <button class="button is-success">Save changes</button>
                <button class="button">Cancel</button>
            </footer>
        </div>
    </div>
    `
});

new Vue({
    el:'#root'
});
```
>注：需要添加`is-active`样式类

打开浏览器查看效果：
![file](https://lccdn.phphub.org/uploads/images/201810/18/19192/S4102u66tN.png?imageView2/2/w/1240/h/0)
现在我们来动态加载不同的`<slot>`内容：

*main.js*
```
  Vue.component('modal',{
    template:`
    <div class="modal is-active">
        <div class="modal-background"></div>
        <div class="modal-card">
            <header class="modal-card-head">
                <p class="modal-card-title">
                    <slot name="title">
                        Default Title
                    </slot>
                </p>
                <button class="delete" aria-label="close"></button>
            </header>
            <section class="modal-card-body">
                <slot>
                    Default Content.
                </slot>
            </section>
            <footer class="modal-card-foot">
                <slot name="footer">
                    <button class="button is-success">Okay</button>
                </solt>
            </footer>
        </div>
    </div>
    `
});

new Vue({
    el:'#root'
});
```
我们给每个`<slot>`都给定了默认值，直接刷新页面：
![file](https://lccdn.phphub.org/uploads/images/201810/18/19192/ORSWn9GUfA.png?imageView2/2/w/1240/h/0)
下面我们来进行赋值：

*index.html*
```
<!DOCTYPE html>

<html>
    <head>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bulma/0.2.3/css/bulma.css">
    </head>

    <body>
        <div id="root" class="container">
            <modal>
                <template slot="title">My Title</template>

                something is here.

                <div slot="footer">
                    <button class="button is-success">Save changes</button>
                    <button class="button">Cancel</button>
                </div>
            </modal>
        </div>

        <script src="https://unpkg.com/vue@2.1.3/dist/vue.js"></script>

        <script src="main.js"></script>
    </body>
</html>
```
刷新页面：
![file](https://lccdn.phphub.org/uploads/images/201810/18/19192/YXbJZNNzDr.png?imageView2/2/w/1240/h/0)

值得注意的一点是，我们使用了`<template>`标签包裹`title`，`<div>`标签包裹`footer`，所以在渲染出来的页面是不同的：
![file](https://lccdn.phphub.org/uploads/images/201810/18/19192/xeeWThTo4E.png?imageView2/2/w/1240/h/0)
用`<template>`标签包裹会直接渲染内容，用`<div>`标签包裹会渲染出`<div>`标签。
### 本节说明
* 对应第 9 小节：Practical Component Exercise 1：Message

### 本节内容
接下来的几节我们来做一些练习：如何将`html`模块抽取出组件。本节我们来练习`message`组件，我们的例子从 [Bulma](https://bulma.io/documentation/components/message/) 网站选取：
![file](https://lccdn.phphub.org/uploads/images/201810/15/19192/qkhhFU07X8.png?imageView2/2/w/1240/h/0)
首先我们需要引入样式跟组件：
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
                <article class="message">
                    <div class="message-header">
                        <p>Hello World</p>
                        <button class="delete" aria-label="delete"></button>
                    </div>
                    <div class="message-body">
                        Lorem ipsum dolor sit amet, consectetur adipiscing elit. <strong>Pellentesque risus mi</strong>, tempus quis placerat ut, porta nec nulla. Vestibulum rhoncus ac ex sit amet fringilla. Nullam gravida purus diam, et dictum <a>felis venenatis</a> efficitur. Aenean ac <em>eleifend lacus</em>, in mollis lectus. Donec sodales, arcu et sollicitudin porttitor, tortor urna tempor ligula, id porttitor mi magna a neque. Donec dui urna, vehicula et sem eget, facilisis sodales sem.
                    </div>
                </article>
        </div>

        <script src="https://unpkg.com/vue@2.1.3/dist/vue.js"></script>

        <script src="main.js"></script>
    </body>
</html>
```
查看页面：
![file](https://lccdn.phphub.org/uploads/images/201810/15/19192/kHrMCY0J6o.png?imageView2/2/w/1240/h/0)
现在我们来抽取成组件：我们的组件包含三个部分，标题，内容和删除按钮，所以我们会有`title`跟`body`属性，已经删除操作。我们将通过是否显示组件可见来控制删除操作。下面建立组件：
*main.js*
```
Vue.component('message',{
    props:['title','body'],

    data() {
        return {
            isVisiable: true
        }
    },

    template:`
    <article class="message" v-show="isVisiable">
        <div class="message-header">
            <p>{{ title }}</p>
            <button class="delete" aria-label="delete" @click='isVisiable = false'></button>
        </div>
        <div class="message-body">
            {{ body }}   
        </div>
    </article>
    `
});

new Vue({
    el:'#root'
});
```
接着我们给组件的`prop`传入静态值：
*index.html*
```
.
.
<body>
	<div id="root" class="container">
		<message title="hello world" body="asdfaf asr aiunan"></message>
		<message title="hello Vue" body="ohu aeuah anfahs"></message>
		<message title="hello php" body="asdfaf asr aiunan "></message>
	</div>

	<script src="https://unpkg.com/vue@2.1.3/dist/vue.js"></script>

	<script src="main.js"></script>
</body>
.
.
```
最终的效果：
![file](https://lccdn.phphub.org/uploads/images/201810/15/19192/xUM1ToB4W8.gif?imageView2/2/w/1240/h/0)

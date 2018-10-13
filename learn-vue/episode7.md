### 本节说明
* 对应第 6 小节：Components 101

### 本节内容
本节我们开始学习组件。组件是可复用的 Vue 实例，且带有一个名字。我们可以在一个通过`new Vue`创建的 Vue 根实例中，把这个组件作为自定义元素来使用。我们来创建一个组件：
*main.js*
```
Vue.component('task',{
    template:'<li><slot></slot></li>'
});

new Vue({
    el:'#root'
})
```
Vue 自定义的`<slot>`元素可以让我们向一个组件传递内容：
*index.html*
```
<!DOCTYPE html>

<html>
    <head>
    
    </head>

    <body>
        <div id="root">
            <task>Go to work</task>
            <task>Go to bank</task>
            <task>Go to store</task>
        </div>

        <script src="https://unpkg.com/vue@2.1.3/dist/vue.js"></script>

        <script src="main.js"></script>
    </body>
</html>
```
最终效果：
![file](https://lccdn.phphub.org/uploads/images/201810/13/19192/8HUukKYqxw.png?imageView2/2/w/1240/h/0)

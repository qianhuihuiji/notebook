### 本节说明
* 对应第 13 小节：Component Communication Example 2:Event Dispatcher

### 本节内容
上一节我们学习了简单的父组件与子组件之间的通信，但是当有多个子组件，并且其中一个子组件想要通知其他子组件时，我们又该怎么做呢？

*main.js*
```
window.Event = new Vue();

Vue.component('coupon',{
    template:'<input placeholder="enter your coupon code" @blur="onCouponApplied">',

    methods: {
        onCouponApplied() {
            Event.$emit('applied');
        }
    }
});

new Vue({
    el:'#root',

    data: {
        couponApplied:false
    },

    created(){
        Event.$on('applied',() => alert('Handing it!'));
    }
});
```
请注意，我们实例化了一个共用的`Vue`实例，然后我们可以借助这个共用的实例，在任一组件上进行触发和监听事件。这样一来，组件间就可以互相通信。应用组件：

*index.html*
```
<!DOCTYPE html>

<html>
    <head>
    </head>

    <body>
        <div id="root" class="container">
            <coupon></coupon>

            <h1 v-if="couponApplied">Your coupon is applied.</h1>
        </div>

        <script src="https://unpkg.com/vue@2.1.3/dist/vue.js"></script>

        <script src="main.js"></script>
    </body>
</html>
```
查看效果：
![file](https://lccdn.phphub.org/uploads/images/201810/18/19192/c9IQm8Gw7S.gif?imageView2/2/w/1240/h/0)
我们还可以对原生的 API 进行封装：
```
window.Event = new class {
    constructor() {
        this.vue = new Vue();
    }

    fire(event,data = null){
        this.vue.$emit(event,data);
    }

    listen(event,callback){
        this.vue.$on(event,callback);
    }
}

Vue.component('coupon',{
    template:'<input placeholder="enter your coupon code" @blur="onCouponApplied">',

    methods: {
        onCouponApplied() {
            Event.fire('applied');
        }
    }
});

new Vue({
    el:'#root',

    data: {
        couponApplied:false
    },

    created(){
        Event.listen('applied',() => alert('Handing it!'));
    }
});
```
最终效果：
![file](https://lccdn.phphub.org/uploads/images/201810/18/19192/9ci4OwVGbq.gif?imageView2/2/w/1240/h/0)
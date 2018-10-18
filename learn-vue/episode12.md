### 本节说明
* 对应第 12 小节：Component Communication Example 1:Custom Event

### 本节内容
接下来的两小节我们来学习组件之间的通信。我们新建一个组件`coupon`：

*main.js*
```
Vue.component('coupon',{
    template:'<input placeholder="enter your coupon code" @blur="onCouponApplied">',

    methods: {
        onCouponApplied() {
            alert('applied!');
        }
    }
});

new Vue({
    el:'#root'
});


```
应用组件：

*index.html*
```
<!DOCTYPE html>

<html>
    <head>
    </head>

    <body>
        <div id="root" class="container">
            <coupon></coupon>
        </div>

        <script src="https://unpkg.com/vue@2.1.3/dist/vue.js"></script>

        <script src="main.js"></script>
    </body>
</html>
```
查看效果：
![file](https://lccdn.phphub.org/uploads/images/201810/18/19192/CiseactETq.gif?imageView2/2/w/1240/h/0)
接下来我们来进行父组件与子组件之间的通信：子组件触发事件，父组件监听到，然后触发动作。

*main.js*
```
Vue.component('coupon',{
    template:'<input placeholder="enter your coupon code" @blur="onCouponApplied">',

    methods: {
        onCouponApplied() {
            this.$emit('applied');
        }
    }
});

new Vue({
    el:'#root',

    data: {
        couponApplied:false
    },

    methods: {
        onCouponApplied() {
            this.couponApplied = true;
        }
    }
});


```

*index.html*
```
<!DOCTYPE html>

<html>
    <head>
    </head>

    <body>
        <div id="root" class="container">
            <coupon @applied="onCouponApplied"></coupon>

            <h1 v-if="couponApplied">Your coupon is applied.</h1>
        </div>

        <script src="https://unpkg.com/vue@2.1.3/dist/vue.js"></script>

        <script src="main.js"></script>
    </body>
</html>
```
`this.$emit('applied')`会触发在当前实例上的`applied`事件，而父组件监听到`applied`事件被触发，会运行父组件上的`onCouponApplied()`方法，从而更改`couponApplied`属性值为`true`。最终效果：
![file](https://lccdn.phphub.org/uploads/images/201810/18/19192/CtV6tOTt2I.gif?imageView2/2/w/1240/h/0)
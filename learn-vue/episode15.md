### 本节说明
* 对应第 15 小节：Inline Templates

### 本节内容
如果你使用 Laravel 进行开发，你可能会希望将`blade`模板与`Vue`组件一起配合使用，`inline-template`属性可以帮助我们做到这点：当`inline-template`这个特殊的属性出现在一个子组件上时，这个组件将会使用其里面的内容作为模板，而不是将其作为被分发的内容。例如：

*index.html*
```
<!DOCTYPE html>

<html>
    <head>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bulma/0.2.3/css/bulma.css">
    </head>

    <body>
        <div id="root" class="container">
            <process-view inline-template>
                <div>
                    <h1>Your Process is {{ processRate }}%</h1>

                    <p><button @click="processRate += 10">Update Process</button></p>
                </div>
            </process-view>
        </div>

        <script src="https://unpkg.com/vue@2.1.3/dist/vue.js"></script>

        <script src="main.js"></script>
    </body>
</html>
```

编写组件：
*main.js*
```
  Vue.component('process-view',{
      data(){
          return { processRate:50 }
      }
});

new Vue({
    el:'#root'
});
```
最终效果会是这样：
![file](https://lccdn.phphub.org/uploads/images/201810/18/19192/Mv72tyi5Up.gif?imageView2/2/w/1240/h/0)
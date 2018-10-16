### 本节说明
* 对应第 8 小节：Components Within Components

### 本节内容
本节我们开始学习在组件中引入组件。上一节我们定义了一系列任务列表，本节我们定义一个`task-list`组件来显示任务列表：
*main.js*
```
Vue.component('task-list',{
    template:`
        <div>
            <task v-for="task in tasks">{{ task.description }}</task>
        </div>
    `,

    data() {
        return {
            tasks:[
                {description:'Go to work',completed:false},
                {description:'Go to bank',completed:false},
                {description:'Go to store',completed:false},
            ]
        }
    }
})

Vue.component('task',{
    template:'<li><slot></slot></li>'
});

new Vue({
    el:'#root'
})
```
然后使用该组件：
*index.html*
```
<!DOCTYPE html>

<html>
    <head>
    
    </head>

    <body>
        <div id="root">
            <task-list>Go to work</task-list>
        </div>

        <script src="https://unpkg.com/vue@2.1.3/dist/vue.js"></script>

        <script src="main.js"></script>
    </body>
</html>
```
查看效果：
![file](https://lccdn.phphub.org/uploads/images/201810/15/19192/RU5fuyJxiT.png?imageView2/2/w/1240/h/0)
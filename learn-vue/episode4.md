### 本节说明
* 对应第 4 小节：Vue Event Listener


### 本节内容
在上一节，我们用`JavaScript`的方式将 name 添加到 names 列表当中时，我们是这么做的：首先我们注册一个 click 事件，然后我们获取 input 元素的值，然后我们再更新列表。本节我们使用`Vue`的方式来实现：
*index.html*
```
<!DOCTYPE html>

<html>
    <head>
        <title></title>
    </head>

    <body>
        <div id="root">
            <ul>
                <li v-for="name in names" v-text="name"></li>
            </ul>

            <input type="text" v-model="newName">
            <button @click="addName">Add A name</button>
        </div>

        <script src="https://unpkg.com/vue@2.1.3/dist/vue.js"></script>

        <script>
            var app = new Vue({
                el:'#root',
                data: {
                    newName:'',
                    names:['Joe','Jack','Jane','Mike']
                },

                methods: {
                    addName() {
                        this.names.push(this.newName);

                        this.newName = '';
                    }
                }
            });
        </script>
    </body>
</html>
```
在`Vue`中，我们可以用`v-on`指令监听 DOM 事件，并在触发时运行一些`JavaScript`。然而许多事件处理逻辑会更为复杂，所以直接把`JavaScript`代码写在`v-on`指令中是不可行的。` v-on` 还可以接收一个需要调用的方法名称。
> 注：@click 为 v-on:click的简写方式

我们来查看效果：
![file](https://lccdn.phphub.org/uploads/images/201810/13/19192/ESbjMKgLf4.gif?imageView2/2/w/1240/h/0)
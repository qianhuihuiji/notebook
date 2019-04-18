### 本节说明
* 对应第 3 小节：Lists


### 本节内容
本节我们来学习`v-for`循环。首先我们定义`names`，并显示出来：
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
        </div>

        <script src="https://unpkg.com/vue@2.1.3/dist/vue.js"></script>

        <script>
            var app = new Vue({
                el:'#root',
                data: {
                    names:['Joe','Jack','Jane','Mike']
                }
            });
        </script>
    </body>
</html>
```
![file](https://lccdn.phphub.org/uploads/images/201810/12/19192/5ejiObchvU.png?imageView2/2/w/1240/h/0)
接下来我们加点新东西：我们添加一个输入框跟按钮，点击按钮时将输入框中的名字添加到名字列表中。本节我们先使用常规的`JavaScript`方式：

*index.html*
```
.
.
<script>
	var app = new Vue({
		el:'#root',
		data: {
			names:['Joe','Jack','Jane','Mike']
		}
	});

	document.querySelector('#button').addEventListener('click',() => {
		let name = document.querySelector('#input');

		app.names.push(name.value);

		name.value = '';
	});
</script>
		.
		.
```
然后我们查看效果：

![file](https://lccdn.phphub.org/uploads/images/201810/12/19192/x7D6yHA751.gif?imageView2/2/w/1240/h/0)
下一节我们将采用`Vue`的方式实现此效果。
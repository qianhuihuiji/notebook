### 本节说明
* 对应第 6 小节：The Need For Computed Properties

### 本节内容
本节我们来学习 **计算属性** 。首先，我们需要了解，在`Vue`中，数据绑定最常见的形式就是使用“Mustache”语法 (双大括号) 的文本插值：
```
<h2>Message: {{ message }}</h2>
```
Mustache 标签将会被替代为对应数据对象上`message`属性的值。无论何时，绑定的数据对象上`message`属性发生了改变，插值处的内容都会更新。并且，对于所有的数据绑定，Vue.js 都提供了完全`JavaScript`表达式支持。比如，如果我们想将`message`的内容倒序输出：
```
<!DOCTYPE html>

<html>
    <head>
    
    </head>

    <body>
        <div id="root">
            <h2>{{ message.split('').reverse().join('') }}</h2>
        </div>

        <script src="https://unpkg.com/vue@2.1.3/dist/vue.js"></script>

        <script>
            var app = new Vue({
                el:'#root',

                data: {
                    message:'Hello World',
                }
            });
        </script>
    </body>
</html>
```
查看效果：
![file](https://lccdn.phphub.org/uploads/images/201810/13/19192/qDe2PY95uR.png?imageView2/2/w/1240/h/0)
模板内的表达式非常便利，但是设计它们的初衷是用于简单运算的。在模板中放入太多的逻辑会让模板过重且难以维护。在上面这个例子中，模板不再是简单的声明式逻辑。你必须看一段时间才能意识到，这里是想要显示变量`message`的翻转字符串。当你想要在模板中多次引用此处的翻转字符串时，就会更加难以处理。

所以，对于任何复杂逻辑，你都应当使用 **计算属性**：
```
.
.
<body>
	<div id="root">
		<h2>{{ reverseMessage }}</h2>
	</div>

	<script src="https://unpkg.com/vue@2.1.3/dist/vue.js"></script>

	<script>
		var app = new Vue({
			el:'#root',

			data: {
				message:'Hello World',
			},

			computed:{
				reverseMessage() {
					return this.message.split('').reverse().join('');
				}
			}
		});
	</script>
</body>
	.
	.
```
你可以像绑定普通属性一样在模板中绑定计算属性。Vue 知道`app.reversMessage`依赖于`app.message`，因此当`app.message`发生改变时，所有依赖`app.reversMessage`的绑定也会更新。如下：
![file](https://lccdn.phphub.org/uploads/images/201810/13/19192/g9AfNcOs9G.gif?imageView2/2/w/1240/h/0)
你可能已经想到，我们可以将同一函数定义为一个方法而不是一个计算属性。两种方式的最终结果确实是完全相同的：
```
<body>
	<div id="root">
		<h2>{{ reverseMessage() }}</h2>
	</div>

	<script src="https://unpkg.com/vue@2.1.3/dist/vue.js"></script>

	<script>
		var app = new Vue({
			el:'#root',

			data: {
				message:'Hello World',
			},

			methods:{
				reverseMessage() {
					return this.message.split('').reverse().join('');
				}
			}
		});
	</script>
</body>
```
然而，不同的是,**计算属性是基于它们的依赖进行缓存的** 。只在相关依赖发生改变时它们才会重新求值。这就意味着只要`message`还没有发生改变，多次访问计算`reversMessage`属性会立即返回之前的计算结果，而不必再次执行函数。

相比之下，每当触发重新渲染时，调用方法将**总会再次执行函数**。

我们为什么需要缓存？假设我们有一个性能开销比较大的计算属性 A，它需要遍历一个巨大的数组并做大量的计算。然后我们可能有其他的计算属性依赖于 A 。如果没有缓存，我们将不可避免的多次执行 A 的 getter 函数。如果你不希望有缓存，可以用方法来替代。

下面我们来看下一个例子：我们有一个任务清单，分别展示了所有任务，已完成任务，未完成任务。如下：
```
<body>
	<div id="root">
		<h2>All Tasks</h2>
		<ul>
			<li v-for="task in tasks" v-text="task.description"></li>
		</ul>

		<h2>Completed Tasks</h2>
		<ul>
			<li v-for="task in tasks" v-if="task.completed"v-text="task.description"></li>
		</ul>

		<h2>Incompleted Tasks</h2>
		<ul>
			<li v-for="task in incompletedTasks" v-text="task.description"></li>
		</ul>
	</div>

	<script src="https://unpkg.com/vue@2.1.3/dist/vue.js"></script>

	<script>
		var app = new Vue({
			el:'#root',

			data: {
				tasks:[
					{description:'Go to the store',completed:false},
					{description:'Make dinner',completed:true},
					{description:'Clean room',completed:false}
				]
			},

			computed:{
				incompletedTasks() {
					return this.tasks.filter(task => ! task.completed);
				}
			}
		});
	</script>
</body>
```
我们分别使用计算属性来展示未完成任务。现在我们有一个小练习：在每一项未完成任务清单后面加上一个按钮，点击按钮完成任务。加油吧，下一节我们继续前进。

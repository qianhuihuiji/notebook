### 本节说明
* 对应第 10 小节：Practical Component Exercise 3：Tabs

### 本节内容
接下来来做第三个练习：`Tabs`组件，我们的例子从 [Bulma](https://bulma.io/documentation/components/tabs/) 网站选取。我们期望的组件将会向下面那样使用：

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
            <tabs>
                <tab name="About us">
                    <h1>Here is the content about our tabs</h1>
                </tab>

                <tab name="About our culture">
                    <h1>Here is the content about our culture</h1>
                </tab>

                <tab name="About our vision">
                    <h1>Here is the content about our vision</h1>
                </tab>
            </tabs>
        </div>

        <script src="https://unpkg.com/vue@2.1.3/dist/vue.js"></script>

        <script src="main.js"></script>
    </body>
</html>
```
所以我们将会有两个组件：`tabs`和`tab`，`tabs`中包含多个`tab`。首先我们来注册`tabs`和`tab`组件：
`main.js`
```
Vue.component('tabs',{
    template:`
    <div>
        <div class="tabs">
            <ul>
                <li v-for="tab in tabs">
                    <a href="#">{{ tab.name }}</a>
                </li>
            </ul>
        </div>

        <div class="tabs-details">
            <slot></slot>
        </div>
    <div>
    `,

    data(){
        return { tabs:[] };
    },

    created() {
        this.tabs = this.$children;
    },
});

Vue.component('tab',{
    template:`
        <div><slot></div></div>
    `,

    props:{
        name:{ required:true },
    }
});

new Vue({
    el:'#root'
});
```
目前的页面效果：
![file](https://lccdn.phphub.org/uploads/images/201810/15/19192/HpRuzt2QoY.png?imageView2/2/w/1240/h/0)
看上去我们需要设置选中状态：
*main.js*
```
Vue.component('tabs',{
    template:`
    <div>
        <div class="tabs">
            <ul>
                <li v-for="tab in tabs" :class="{'is-active':tab.selected}">
                    <a href="#">{{ tab.name }}</a>
                </li>
            </ul>
        </div>
		.
		.
    <div>
    `,
	.
	.
});

Vue.component('tab',{
    template:`
        <div><slot></div></div>
    `,

    props:{
        name:{ required:true },
        selected:{ default:false }
    }
});

new Vue({
    el:'#root'
});
```
我们默认`tab`组件的`selected`属性为`false`，那么`:class="{'is-active':tab.selected}"`则表示我们默认`is-active`类不存在。接下来我们给第一个`tab`组件绑定`selected`属性为`true`，显示为选中状态：
`index.html`
```
.
.
<div id="root" class="container">
		<tabs>
			<tab name="About us" :selected="true">
				<h1>Here is the content about our tabs</h1>
			</tab>

			<tab name="About our culture">
				<h1>Here is the content about our culture</h1>
			</tab>

			<tab name="About our vision">
				<h1>Here is the content about our vision</h1>
			</tab>
		</tabs>
	</div>
	.
	.
```
现在来查看效果：
![file](https://lccdn.phphub.org/uploads/images/201810/15/19192/FXOi2cz9Zq.png?imageView2/2/w/1240/h/0)
下面我们来做点击标签切换选中状态。你可能会像下面这样做：
*main.js*
```
Vue.component('tabs',{
    template:`
    <div>
        <div class="tabs">
            <ul>
                <li v-for="tab in tabs" :class="{'is-active':tab.selected}">
                    <a href="#" @click="selectTab(tab)">{{ tab.name }}</a>
                </li>
            </ul>
        </div>

        <div class="tabs-details">
            <slot></slot>
        </div>
    <div>
    `,

    data(){
        return { tabs:[] };
    },

    created() {
        this.tabs = this.$children;
    },

    methods:{
        selectTab(selectedTab){
            this.tabs.forEach(tab => {
                tab.selected = (tab.name == selectedTab.name);
            });
        }
    }
});

Vue.component('tab',{
    template:`
        <div><slot></div></div>
    `,

    props:{
        name:{ required:true },
        selected:{ default:false }
    }
});

new Vue({
    el:'#root'
});
```
我们点击标签，出发`selectTab`方法，改变其他组件的`selected`的属性值。但是这样在`Vue 2`中是行不通的：
![file](https://lccdn.phphub.org/uploads/images/201810/16/19192/raORqWn8Ye.png?imageView2/2/w/1240/h/0)
组件内不能修改 props 的值，同时修改的值也不会同步到组件外层，即调用组件方不知道组件内部当前的状态是什么。

所有的 prop 都使得其父子 prop 之间形成了一个 **单向下行绑定**：父级 prop 的更新会向下流动到子组件中，但是反过来则不行。这样会防止从子组件意外改变父级组件的状态，从而导致你的应用的数据流向难以理解。

额外的，每次父级组件发生更新时，子组件中所有的 prop 都将会刷新为最新的值。这意味着你不应该在一个子组件内部改变 prop。如果你这样做了，Vue 会在浏览器的控制台中发出警告。

然而我们现在的确想做到父子组件间的双向通信，我们可以这么做：
*main.js*
```
Vue.component('tabs',{
    template:`
    <div>
        <div class="tabs">
            <ul>
                <li v-for="tab in tabs" :class="{'is-active':tab.isActive}">
                    <a href="#" @click="selectTab(tab)">{{ tab.name }}</a>
                </li>
            </ul>
        </div>

        <div class="tabs-details">
            <slot></slot>
        </div>
    <div>
    `,

    data(){
        return { tabs:[] };
    },

    created() {
        this.tabs = this.$children;
    },

    methods:{
        selectTab(selectedTab){
            this.tabs.forEach(tab => {
                tab.isActive = (tab.name == selectedTab.name);
            });
        }
    }
});

Vue.component('tab',{
    template:`
        <div><slot></div></div>
    `,

    props:{
        name:{ required:true },
        selected:{ default:false }
    },

    data() {
        return {
            isActive:false
        }
    },

    mounted() {
        this.isActive = this.selected;
    },
});

new Vue({
    el:'#root'
});
```
我们将样式关联到`isActive`数据属性中，然后进行动态切换：
![file](https://lccdn.phphub.org/uploads/images/201810/16/19192/jCAeE0dF21.gif?imageView2/2/w/1240/h/0)
接下来我们进行最后的步骤：切换标签时动态显示不同的内容，并且更新链接。对于第一个问题，我们只需设置内容显示与否由组件的`isActive`是否为`true`即可；对于第二个问题，标签的链接属于不会变化的内容，**计算属性** 可以帮我们做到：
*main.js*
```
Vue.component('tabs',{
    template:`
    <div>
        <div class="tabs">
            <ul>
                <li v-for="tab in tabs" :class="{'is-active':tab.isActive}">
                    <a :href="tab.href" @click="selectTab(tab)">{{ tab.name }}</a>
                </li>
            </ul>
        </div>

        <div class="tabs-details">
            <slot></slot>
        </div>
    <div>
    `,

    data(){
        return { tabs:[] };
    },

    created() {
        this.tabs = this.$children;
    },

    methods:{
        selectTab(selectedTab){
            this.tabs.forEach(tab => {
                tab.isActive = (tab.name == selectedTab.name);
            });
        }
    }
});

Vue.component('tab',{
    template:`
        <div v-show="isActive"><slot></div></div>
    `,

    props:{
        name:{ required:true },
        selected:{ default:false }
    },

    data() {
        return {
            isActive:false
        }
    },

    computed:{
        href(){
            return '#' + this.name.toLowerCase().replace(/ /g,'-');
        }
    },

    mounted() {
        this.isActive = this.selected;
    },
});

new Vue({
    el:'#root'
});
```
最终效果：
![file](https://lccdn.phphub.org/uploads/images/201810/16/19192/5nvV0mUPKh.gif?imageView2/2/w/1240/h/0)

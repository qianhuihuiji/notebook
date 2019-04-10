
### 本节说明
* 对应第 17 小节：HRM

### 本节内容
本节我们来介绍 Hot Reload Model，即"热重载"模式。"热重载"不是当你修改文件的时候简单重新加载页面。启用热重载后，当你修改 .vue 文件时，所有该组件的实例会被替换，**并且不需要刷新页面**。它甚至保持应用程序和被替换组件的当前状态！当你调整模版或者修改样式时，这极大的提高了开发体验。
![file](https://iocaffcdn.phphub.org/uploads/images/201903/04/19192/jZZaKzdqTf.gif!large)

>注：参见 [热重载](https://vue-loader-v14.vuejs.org/zh-cn/features/hot-reload.html)

遗憾地是，Windows 下面 Homestead 虚拟机运行`npm install`一直是个令人头痛的问题，可能导致无法使用“热重载”。关于这个问题我暂时还没找到好的方法，如果大家有好的方法，欢迎在评论区贴出。
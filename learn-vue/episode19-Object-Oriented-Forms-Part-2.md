
### 本节说明

* 对应第 20 小节：Object Oriented Forms Part-2

### 本节内容

我们接着上一节的内容。首先我们来修改下上一节成功创建的逻辑：

*public/js/app.js*

```
.
.
onSubmit() {
    axios.post('/projects',this.$data)
        .then(this.onSuccess)
        .catch(error => this.errors.record(error.response.data.errors));
},

onSuccess(response) {
    alert(response.data.message);
}
.
.
```

现在我们就可以在服务端决定我们成功创建后返回的消息。接着，我们发现，当成功创建后表单的内容没有清空，这样体验不佳，我们来建立清空的逻辑：

*public/js/app.js*

```
.

```
# 简介

具体参考https://github.com/wangliu1102/wl-guns 下的通知管理。

这里集成了文件上传和下载，富文本编辑器中集成了图片上传：

![img](../img/71c4a580-c9c9-4860-8b55-4bb808931429.png)

加入了全屏设置，需要加入如下文件：

 

```
static/js/plugins/wangEditor/wangEditor-fullscreen-plugin.js
static/css/wangEditor-fullscreen-plugin.css
```

![img](../img/9de4ffb6-d038-4c64-8b71-f2e18f9a7670.png)

使得图片可以回显，文件可以下载，返回路径如下。

若有远程服务器，将文件上传至服务器，可以通过域名设置路径，如下注释的bathPath。

![img](../img/1423061f-9e2c-44c2-b7e9-0ea067b799df.png)

同时设置路径映射：

![img](../img/93cc1888-3880-4ce2-9780-fc3ac5c83618.png)
针对没有选项卡的页面，如果使用了iframe页面来进行页面嵌套，每次点击打开一个页面就会新建一个iframe，若每次打开新页面不关闭之前的iframe页面，ifame会一直嵌套在最里层，从而会造成页面内存溢出。

**解决办法**：

  每次新建打开一个ifame页面，则关闭之前所有的iframe页面。

参考https://github.com/wangliu1102/wl-guns  中的代码，其他模板框架思想类似

进入系统首页是第一个ifame页面，代码在_right.html中

 

```
    <div class="row J_mainContent" id="content-main">
        <iframe class="J_iframe" name="iframe0" width="100%" height="100%" src="${ctxPath}/blackboard" frameborder="0" data-id="${ctxPath}/blackboard" seamless></iframe>
    </div>
```

每次新建打开一个ifame页面，就是在id="content-main"的div中新建一个iframe嵌套进去，代码在contab.js中。

在每次新建iframe之前，关闭之前打开的所有的iframe页面

 

```
        // 选项卡菜单不存在
        if (flag) {
            var str = '<a href="javascript:;" class="active J_menuTab" data-id="' + dataUrl + '">' + menuName + ' <i class="fa fa-times-circle"></i></a>';
            $('.J_menuTab').removeClass('active');

            // 添加选项卡对应的iframe
            var str1 = '<iframe class="J_iframe" name="iframe' + dataIndex + '" width="100%" height="100%" src="' + dataUrl + '" frameborder="0" data-id="' + dataUrl + '" seamless></iframe>';
            
            //---------------------修改-----------------------
            // $('.J_mainContent').find('iframe.J_iframe').hide().parents('.J_mainContent').append(str1);
            /**
             * 去掉所有layer弹框，避免内存泄漏
             * @type {jQuery}
             */
            var jframe = $('.J_mainContent').find('iframe.J_iframe');
            if(jframe.length > 0 && jframe[0].contentWindow.layer){
                jframe[0].contentWindow.layer.closeAll();
                //设置为空 垃圾回收掉
                jframe[0].contentWindow.layer = null;
                jframe[0].contentWindow.layui = null;
            }
            // * 去掉所有iframe，避免内存泄漏
            $('.J_mainContent').find('iframe.J_iframe').remove();
            $('.J_mainContent').append(str1);
            //-------------------修改--------------------------
            
            //显示loading提示
            var loading = layer.load();

            $('.J_mainContent iframe:visible').load(function () {
                //iframe加载完成后隐藏loading提示
                layer.close(loading);
            });
            
            // 添加选项卡
            $('.J_menuTabs .page-tabs-content').append(str);
            scrollToTab($('.J_menuTab.active'));
        }
```
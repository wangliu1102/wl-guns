需要引入相关js和css

在https://github.com/wangliu1102/wl-guns 中: /static/js/plugins/bootstrap-select/bootstrap-select.min.js 和 /static/css/plugins/bootstrap-select/bootstrap-select.min.css

官网地址：[Bootstrap Select中文网](https://www.bootstrapselect.cn/)

具体实现功能页面在WEB-INF/view/system/test/bootstrap_select.html中

**方式**：

给select选择框添加class="selectpicker" data-live-search="true" title="请选择" multiple(多选)

 

```
<select class="form-control selectpicker" data-style="btn-primary" id="name" name="name" data-live-search="true" title="请输入管理员" multiple>
</select>
```

并在js初始化时，查询列表数据，动态拼接成option添加到select中

 

```
$(function () {
    $.ajax({
        type: 'POST',
        url: Feng.ctxPath + "/mgr/list",
        contentType: 'application/x-www-form-urlencoded;charset=UTF-8',
        dataType: "json",
        success: function (data) {
            //获取数据成功时进行循环拼接下拉框
            for (var i = 0; i < data.length; i++) {
                var add_options = '<option value="' + data[i].id + '">' + data[i].name + '</option>';
                $('#name').append(add_options);
            }
            //更新
            $("#name").val($("#name").val());
            $('#name').selectpicker('refresh');
        }
    });
})
```

![img](../img/3b93bd34-79f3-4c22-b94d-f68bb86ce52a.png)
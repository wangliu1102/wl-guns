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

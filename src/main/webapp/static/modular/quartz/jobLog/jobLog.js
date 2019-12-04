var SysJobLog = {
    id: "sysJobLogTable",	//表格id
    seItem: null,		//选中的条目
    seItemList: null,		//选中的条目
    table: null,
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
SysJobLog.initColumn = function () {
    return [
        {field: 'selectItem', checkbox: true},
        {
            title: '日志编号', field: 'jobLogId', align: 'center', valign: 'middle', width: '100px', sortable: true,
            formatter: function (value, row, index) {
                return setTitle(value, row, index);
            }
        },
        {
            title: '任务名称', field: 'jobName', align: 'center', valign: 'middle', sortable: true,
            formatter: function (value, row, index) {
                return setTitle(value, row, index);
            }
        },
        {
            title: '任务分组', field: 'jobGroup', align: 'center', valign: 'middle', sortable: true,
            formatter: function (value, row, index) {
                var tdValue = value;
                if (value == "DEFAULT"){
                    tdValue = "默认";
                }else if (value == "SYSTEM"){
                    tdValue = "系统";
                }
                var html = '<div title="' + tdValue + '" style="width: 100%;" ><table style="table-layout: fixed"><td>' + tdValue + '</td></table></div>';
                return html;
            }
        },
        {
            title: '调用目标字符串', field: 'invokeTarget', align: 'center', width: '250px',valign: 'middle', sortable: true,
            formatter: function (value, row, index) {
                return setTitle(value, row, index);
            }
        },
        {
            title: '日志信息', field: 'jobMessage', align: 'center', valign: 'middle', sortable: true,
            formatter: function (value, row, index) {
                return setTitle(value, row, index);
            }
        },
        {
            title: '执行状态', field: 'status', align: 'center', valign: 'middle', sortable: true,
            formatter: function (value, row, index) {
                var tdValue = value;
                if (value == "0"){
                    tdValue = "成功";
                }else if (value == "1"){
                    tdValue = "失败";
                }
                var html = '<div title="' + tdValue + '" style="width: 100%;" ><table style="table-layout: fixed"><td>' + tdValue + '</td></table></div>';
                return html;
            }
        },
        {
            title: '创建时间', field: 'createTime', align: 'center', valign: 'middle', sortable: true,
            formatter: function (value, row, index) {
                return setTitle(value, row, index);
            }
        }
        ,
        {
            title: '异常信息', field: 'exceptionInfo', align: 'center', width: '300px', valign: 'middle', sortable: true,
            formatter: function (value, row, index) {
                return setTitle(value, row, index);
            }
        }

    ];
};


/**
 * 检查是否选中
 */
SysJobLog.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if (selected.length == 0) {
        Feng.info("请先选中表格中的某一记录！");
        return false;
    } else if (selected.length == 1) {
        SysJobLog.seItemList = selected;
        SysJobLog.seItem = selected[0];
        return true;
    } else {
        SysJobLog.seItemList = selected;
        return true;
    }
};

/**
 * 点击删除
 */
SysJobLog.delete = function () {
    if (this.check()) {

        var operation = function () {
            var selectedList = SysJobLog.seItemList;
            var idList = "";
            for (var i = 0; i < selectedList.length; i++) {
                if (i == selectedList.length - 1) {
                    idList += selectedList[i].jobLogId;
                } else {
                    idList += selectedList[i].jobLogId + ",";
                }
            }

            $.ajax({
                url: Feng.ctxPath + "/quartz/jobLog/remove",
                type: "POST",
                dataType: "json",
                // traditional: true,//加上这个属性，后台用 String[] 之类的参数就可以接收到了
                data: {
                    ids: idList
                },
                async: false,
                success: function (data) {
                    Feng.success("删除成功!");
                    SysJobLog.table.refresh();
                },
                error: function (data) {
                    Feng.error("删除失败!" + data.responseJSON.message + "!");
                }
            });
        };

        Feng.confirm("确认要刪除选中的调度日志吗?", operation);

    }
};

/**
 * 点击清空
 */
SysJobLog.trashLog = function () {
    var operation = function () {
        var ajax = new $ax(Feng.ctxPath + "/quartz/jobLog/clean", function (data) {
            Feng.success("清空成功!");
            SysJobLog.table.refresh();
        }, function (data) {
            Feng.error("清空失败!");
        });
        ajax.start();
    };

    Feng.confirm("确认要清空所有调度日志吗？", operation);

};

/**
 * 点击查看详情
 */
SysJobLog.detail = function () {
    if (this.check()) {
        if (SysJobLog.seItemList.length == 1) {
            var index = layer.open({
                type: 2,
                title: '日志详情',
                area: ['800px', '520px'], //宽高
                fix: false, //不固定
                maxmin: true,
                content: Feng.ctxPath + '/quartz/jobLog/detail/' + SysJobLog.seItem.jobLogId
            });
            this.layerIndex = index;
        }else {
            layer.alert("您选中了多条记录，请选中表格中的某一条记录进行刷新！");
        }
    }
};


SysJobLog.resetSearch = function () {

    $("#jobName").val("");
    $("#jobGroup").val("");
    $("#invokeTarget").val("");
    $("#status").val("");
    $("#beginTime").val("");
    $("#endTime").val("");
    SysJobLog.search();
}
/**
 * 查询列表
 */
SysJobLog.search = function () {

    var queryData = {};

    queryData['jobName'] = $("#jobName").val();
    queryData['jobGroup'] = $("#jobGroup").val();
    queryData['invokeTarget'] = $("#invokeTarget").val();
    queryData['status'] = $("#status").val();
    queryData['beginTime'] = $("#beginTime").val();
    queryData['endTime'] = $("#endTime").val();

    SysJobLog.table.refresh({query: queryData});
};

$(function () {
    var defaultColunms = SysJobLog.initColumn();
    var table = new BSTable(SysJobLog.id, "/quartz/jobLog/list", defaultColunms);
    table.setPaginationType("server");
    SysJobLog.table = table.init();
});

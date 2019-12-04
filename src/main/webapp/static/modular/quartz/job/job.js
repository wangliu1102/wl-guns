var SysJob = {
    id: "sysJobTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
SysJob.initColumn = function () {
    return [
        {field: 'selectItem', radio: true},
        {
            title: '任务编号', field: 'id', align: 'center', valign: 'middle', width: '100px', sortable: true,
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
                if (value == "DEFAULT") {
                    tdValue = "默认";
                } else if (value == "SYSTEM") {
                    tdValue = "系统";
                }
                var html = '<div title="' + tdValue + '" style="width: 100%;" ><table style="table-layout: fixed"><td>' + tdValue + '</td></table></div>';
                return html;
            }
        },
        {
            title: '调用目标字符串', field: 'invokeTarget', align: 'center', valign: 'middle', width: '250px', sortable: true,
            formatter: function (value, row, index) {
                return setTitle(value, row, index);
            }
        },
        {
            title: '执行表达式', field: 'cronExpression', align: 'center', valign: 'middle', sortable: true,
            formatter: function (value, row, index) {
                return setTitle(value, row, index);
            }
        },
        {
            title: '任务状态', field: 'status', align: 'center', valign: 'middle', sortable: true,
            formatter: function (value, row, index) {
                var tdValue = value;
                if (value == "0") {
                    tdValue = "正常";
                } else if (value == "1") {
                    tdValue = "暂停";
                }
                var html = '<div title="' + tdValue + '" style="width: 100%;" ><table style="table-layout: fixed"><td>' + tdValue + '</td></table></div>';
                return html;
            }
        },
        {
            title: '是否并发执行', field: 'concurrent', align: 'center', valign: 'middle', sortable: true,
            formatter: function (value, row, index) {
                var tdValue = value;
                if (value == "0") {
                    tdValue = "允许";
                } else if (value == "1") {
                    tdValue = "禁止";
                }
                var html = '<div title="' + tdValue + '" style="width: 100%;" ><table style="table-layout: fixed"><td>' + tdValue + '</td></table></div>';
                return html;
            }
        },
        {
            title: '执行策略', field: 'misfirePolicy', align: 'center', valign: 'middle', sortable: true,
            formatter: function (value, row, index) {
                var tdValue = value;
                if (value == "1") {
                    tdValue = "立即执行";
                } else if (value == "2") {
                    tdValue = "执行一次";
                } else if (value == "3") {
                    tdValue = "放弃执行";
                }
                var html = '<div title="' + tdValue + '" style="width: 100%;" ><table style="table-layout: fixed"><td>' + tdValue + '</td></table></div>';
                return html;
            }
        }
        ,
        {
            title: '创建时间', field: 'createTime', align: 'center', valign: 'middle', sortable: true,
            formatter: function (value, row, index) {
                return setTitle(value, row, index);
            }
        }
        ,
        {
            title: '备注', field: 'remark', align: 'center', width: '300px', valign: 'middle', sortable: true,
            formatter: function (value, row, index) {
                return setTitle(value, row, index);
            }
        }

    ];
};


/**
 * 检查是否选中
 */
SysJob.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if (selected.length == 0) {
        Feng.info("请先选中表格中的某一记录！");
        return false;
    } else {
        SysJob.seItem = selected[0];
        return true;
    }
};

/**
 * 点击添加
 */
SysJob.openAddSysJob = function () {
    var index = layer.open({
        type: 2,
        title: '添加定时任务',
        area: ['800px', '520px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/quartz/job/job_add'
    });
    this.layerIndex = index;

};

/**
 * 点击编辑
 */
SysJob.openEditSysJob = function () {
    if (this.check()) {
        var index = layer.open({
            type: 2,
            title: '修改定时任务',
            area: ['800px', '520px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/quartz/job/job_edit/' + SysJob.seItem.id
        });
        this.layerIndex = index;
    }
};

/**
 * 点击启用
 */
SysJob.enableJob = function () {
    if (this.check()) {
        if (this.seItem.status === "1") { // 暂停状态
            var operation = function () {
                var jobId = SysJob.seItem.id;
                var ajax = new $ax(Feng.ctxPath + "/quartz/job/changeStatus", function (data) {
                    Feng.success(data.message);
                    SysJob.table.refresh();
                }, function (data) {
                    Feng.error(data.message);
                });
                ajax.set("id", jobId);
                ajax.set("status", "0");
                ajax.start();
            };

            Feng.confirm("确认要启用该定时任务吗？", operation);
        } else {
            layer.alert("该定时任务处于启用状态");
        }
    }
};

/**
 * 点击暂停
 */
SysJob.pauseJob = function () {
    if (this.check()) {
        if (this.seItem.status === "0") { // 启用状态
            var operation = function () {
                var jobId = SysJob.seItem.id;
                var ajax = new $ax(Feng.ctxPath + "/quartz/job/changeStatus", function (data) {
                    Feng.success(data.message);
                    SysJob.table.refresh();
                }, function (data) {
                    Feng.error(data.message);
                });
                ajax.set("id", jobId);
                ajax.set("status", "1");
                ajax.start();
            };

            Feng.confirm("确认要暂停该定时任务吗？", operation);
        } else {
            layer.alert("该定时任务处于暂停状态");
        }
    }
};

/**
 * 点击执行
 */
SysJob.runJob = function () {
    if (this.check()) {
        var operation = function () {
            var jobId = SysJob.seItem.id;
            var ajax = new $ax(Feng.ctxPath + "/quartz/job/run", function (data) {
                Feng.success(data.message);
                SysJob.table.refresh();
            }, function (data) {
                Feng.error(data.message);
            });
            ajax.set("id", jobId);
            ajax.start();
        }
        Feng.confirm("确认要立即执行一次该定时任务吗？", operation);
    }
};

/**
 * 点击删除
 */
SysJob.removeJob = function () {
    if (this.check()) {
        var operation = function () {
            var jobId = SysJob.seItem.id;
            var ajax = new $ax(Feng.ctxPath + "/quartz/job/remove", function (data) {
                Feng.success(data.message);
                SysJob.table.refresh();
            }, function (data) {
                Feng.error(data.message);
            });
            ajax.set("id", jobId);
            ajax.start();
        }
        Feng.confirm("确认要删除该定时任务吗？", operation);
    }
};

/**
 * 点击查看详情
 */
SysJob.detail = function () {
    if (this.check()) {

        var index = layer.open({
            type: 2,
            title: '任务详情',
            area: ['800px', '520px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/quartz/job/detail/' + SysJob.seItem.id
        });
        this.layerIndex = index;

    }
};


SysJob.resetSearch = function () {

    $("#jobName").val("");
    $("#invokeTarget").val("");
    $("#jobGroup").val("");
    $("#status").val("");
    SysJob.search();
}
/**
 * 查询列表
 */
SysJob.search = function () {

    var queryData = {};

    queryData['jobName'] = $("#jobName").val().trim();
    queryData['invokeTarget'] = $("#invokeTarget").val().trim();
    queryData['jobGroup'] = $("#jobGroup").val().trim();
    queryData['status'] = $("#status").val().trim();

    SysJob.table.refresh({query: queryData});
};

$(function () {
    var defaultColunms = SysJob.initColumn();
    var table = new BSTable(SysJob.id, "/quartz/job/list", defaultColunms);
    table.setPaginationType("client");
    SysJob.table = table.init();
});

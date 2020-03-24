/**
 * 信息管理初始化
 */
var Notice = {
    id: "NoticeTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
Notice.initColumn = function () {
    return [
        {field: 'selectItem', radio: true},
        {title: 'id', field: 'id', visible: false, align: 'center', valign: 'middle'},
        {
            title: '标题',
            formatter: function (value, row, index) {
                return "<a style='color: blue;' href='javascript:void(0)' onclick='Notice.viewDetail(" + row.id + ")' >" + row.title + "</a>";
            },
            align: 'center', valign: 'middle', sortable: true
        },
        {title: '简介', field: 'simpleDes', align: 'center', valign: 'middle', visible: false, sortable: true},
        {title: '发布者', field: 'creator', align: 'center', valign: 'middle', sortable: true},
        {title: '所属机构', field: 'deptName', align: 'center', valign: 'middle', sortable: true},
        {title: '发布时间', field: 'createTime', align: 'center', valign: 'middle', sortable: true},
        {
            title: '操作',
            formatter: function (value, row, index) {
                return "<a style='color: blue;' href='javascript:void(0)' onclick='Notice.viewDetail(" + row.id + ")' >详情</a>";
            },
            align: 'center', valign: 'middle', sortable: false
        }
    ];
};

/**
 * 检查是否选中
 */
Notice.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if (selected.length == 0) {
        Feng.info("请先选中表格中的某一记录！");
        return false;
    } else {
        Notice.seItem = selected[0];
        return true;
    }
};

/**
 * 点击添加信息
 */
Notice.openAddNotice = function () {
    var index = layer.open({
        type: 2,
        title: '添加信息',
        area: ['1000px', '650px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/notice/notice_add'
    });
    this.layerIndex = index;
};

/**
 * 打开编辑信息
 */
Notice.openNoticeDetail = function () {
    if (this.check()) {
        var index = layer.open({
            type: 2,
            title: '信息详情',
            area: ['1000px', '650px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/notice/notice_update/' + Notice.seItem.id
        });
        this.layerIndex = index;
    }
};

/**
 * 删除信息
 */
Notice.delete = function () {
    if (this.check()) {
        var operation = function () {
            var ajax = new $ax(Feng.ctxPath + "/notice/delete", function (data) {
                Feng.success("删除成功!");
                Notice.table.refresh();
            }, function (data) {
                Feng.error("删除失败!" + data.responseJSON.message + "!");
            });
            ajax.set("noticeId", Notice.seItem.id);
            ajax.start();
        };
        Feng.confirm("是否删除信息 " + Notice.seItem.title + "?", operation);
    }
};

/**
 * 查询信息列表
 */
Notice.search = function () {
    var queryData = {};
    queryData['condition'] = $("#condition").val();
    Notice.table.refresh({query: queryData});
};

Notice.resetSearch = function () {
    $("#condition").val("");
    Notice.search();
};

$(function () {
    var defaultColunms = Notice.initColumn();
    var table = new BSTable(Notice.id, "/notice/list", defaultColunms);
    table.setPaginationType("client");
    Notice.table = table.init();
});

Notice.viewDetail = function (noticeId) {
    window.open(Feng.ctxPath + '/notice/detail/' + noticeId, '_blank');
    // location.href = Feng.ctxPath + '/notice/detail/' + noticeId;
};

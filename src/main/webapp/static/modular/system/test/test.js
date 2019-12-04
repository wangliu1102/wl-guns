
function chosenJquery() {
    var index = layer.open({
        type: 2,
        title: 'chosen.jquery.js测试',
        area: ['800px', '560px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/test/chosen_test'
    });
    this.layerIndex = index;
}

function bootstrapSelect() {
    var index = layer.open({
        type: 2,
        title: 'bootstrap_select测试',
        area: ['800px', '560px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/test/bootstrap_select'
    });
    this.layerIndex = index;
}

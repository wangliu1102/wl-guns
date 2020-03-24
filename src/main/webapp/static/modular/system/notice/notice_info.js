/**
 * 初始化通知详情对话框
 */
var NoticeInfoDlg = {
    noticeInfoData: {},
    editor: null,
    validateFields: {
        title: {
            validators: {
                notEmpty: {
                    message: '标题不能为空'
                }
            }
        },
        simpleDes: {
            validators: {
                notEmpty: {
                    message: '简介不能为空'
                }
            }
        }
    }
};

/**
 * 清除数据
 */
NoticeInfoDlg.clearData = function () {
    this.noticeInfoData = {};
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
NoticeInfoDlg.set = function (key, value) {
    this.noticeInfoData[key] = (typeof value == "undefined") ? $("#" + key).val() : value;
    return this;
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
NoticeInfoDlg.get = function (key) {
    return $("#" + key).val();
}

/**
 * 关闭此对话框
 */
NoticeInfoDlg.close = function () {
    parent.layer.close(window.parent.Notice.layerIndex);
}

/**
 * 收集数据
 */
NoticeInfoDlg.collectData = function () {
    this.noticeInfoData['content'] = NoticeInfoDlg.editor.txt.html();
    this.set('id').set('title').set('simpleDes').set('fileName');
}

/**
 * 验证数据是否为空
 */
NoticeInfoDlg.validate = function () {
    $('#noticeInfoForm').data("bootstrapValidator").resetForm();
    $('#noticeInfoForm').bootstrapValidator('validate');
    return $("#noticeInfoForm").data('bootstrapValidator').isValid();
};

/**
 * 提交添加
 */
NoticeInfoDlg.addSubmit = function () {

    this.clearData();
    this.collectData();

    if (!this.validate()) {
        return;
    }
    if (NoticeInfoDlg.editor.txt.html() === '<p><br></p>') {
        layer.alert("请在文本编辑器中输入内容！")
        return;
    }
    var formData = new FormData();
    var files = $('#file')[0].files;
    for (var i = 0; i < files.length; i++) {
        formData.append('files', files[i]);
    }

    formData.append('notice', JSON.stringify(this.noticeInfoData));

    //加载层
    var index = layer.load(0, {shade: [0.1, '#f5f5f5']}); //0代表加载的风格，支持0-2
    $.ajax({
        url: Feng.ctxPath + "/notice/add",
        type: "POST",
        cache: false,
        data: formData,
        processData: false,
        contentType: false,
        timeout: 20000, // 超时20s
        success: function (data) {
            layer.close(index);
            Feng.success("添加成功!");
            window.parent.Notice.table.refresh();
            NoticeInfoDlg.close();
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            layer.close(index);
            if (textStatus === 'error') {
                Feng.error("添加失败!" + XMLHttpRequest.responseJSON.message + "!");
            } else if (textStatus === 'timeout') {
                Feng.info("添加超时，请几分钟后查看该文件是否上传成功!");
                window.parent.Notice.table.refresh();
                NoticeInfoDlg.close();
            }
        }
    });
}

/**
 * 提交修改
 */
NoticeInfoDlg.editSubmit = function () {

    this.clearData();
    this.collectData();

    if (!this.validate()) {
        return;
    }
    console.log(NoticeInfoDlg.editor.txt.html())
    if (NoticeInfoDlg.editor.txt.html() === '<p><br></p>') {
        layer.alert("请在文本编辑器中输入内容！")
        return;
    }
    var formData = new FormData();
    var files = $('#file')[0].files;
    for (var i = 0; i < files.length; i++) {
        formData.append('files', files[i]);
    }

    formData.append('notice', JSON.stringify(this.noticeInfoData));

    //加载层
    var index = layer.load(0, {shade: [0.1, '#f5f5f5']}); //0代表加载的风格，支持0-2
    $.ajax({
        url: Feng.ctxPath + "/notice/update",
        type: "POST",
        cache: false,
        data: formData,
        processData: false,
        contentType: false,
        timeout: 20000, // 超时20s
        success: function (data) {
            layer.close(index);
            Feng.success("修改成功!");
            window.parent.Notice.table.refresh();
            NoticeInfoDlg.close();
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            layer.close(index);
            if (textStatus === 'error') {
                Feng.error("修改失败!" + XMLHttpRequest.responseJSON.message + "!");
            } else if (textStatus === 'timeout') {
                Feng.info("修改超时，请几分钟后查看该文件是否上传成功!");
                window.parent.Notice.table.refresh();
                NoticeInfoDlg.close();
            }
        }
    });
}

NoticeInfoDlg.uploadFile = function () {
    $("#file").click();
}

NoticeInfoDlg.changeFile = function () {
    var files = $('#file')[0].files;
    var filesLength = files.length;
    if (filesLength > 0) {
        var reg = /[`%^()';{}=]/;
        var fileNames = '';
        var fileSizes = 0;
        var maxSingleSize = 20 * 1024 * 1024;
        var maxSize = 100 * 1024 * 1024;
        for (var i = 0; i < filesLength; i++) {
            var fileName = files[i].name;
            var fileSize = files[i].size;
            var fileType = fileName.substr(fileName.lastIndexOf(".")).toUpperCase();
            if (fileType !== ".PDF" && fileType !== ".DOC" && fileType !== ".DOCX" && fileType !== ".XLS"
                && fileType !== ".XLSX" && fileType !== ".PPT" && fileType !== ".PPTX") {
                layer.alert("上传文件限于pdf,doc,docx,xls,xlsx,ppt,pptx格式");
                $("#file").val("");
                fileNames = '';
                fileSizes = 0;
                break;
            }
            if (fileSize > maxSingleSize) {
                layer.alert('单个文件大小不能超过 20MB!');
                $("#file").val("");
                fileNames = '';
                fileSizes = 0;
                break;
            } else {
                fileSizes += fileSize;
            }
            if (reg.test(fileName)) {
                layer.alert("上传文件的名称中含有 ` % ^ ( ) ' ; { } = 等非法字符,请修改文件名重新上传");
                $("#file").val("");
                fileNames = '';
                fileSizes = 0;
                break;
            } else {
                if (i !== filesLength - 1) {
                    fileNames += fileName + ',';
                } else {
                    fileNames += fileName;
                }
            }

        }
        if (fileSizes < maxSize) {
            $("#fileName").val(fileNames);
        } else {
            layer.alert('批量上传文件总大小不能超过 100MB!');
            $("#file").val("");
        }
    }

}

$(function () {
    Feng.initValidator("noticeInfoForm", NoticeInfoDlg.validateFields);

    //初始化编辑器
    var E = window.wangEditor;
    var editor = new E('#editor');
    //开启配置
    editor.customConfig.debug = true
    // 隐藏“网络图片”tab
    editor.customConfig.showLinkImg = false
    // 开启粘贴样式的过滤
    editor.customConfig.pasteFilterStyle = true
    // 自定义菜单配置
    editor.customConfig.menus = [
        'head',  // 标题
        'bold',  // 粗体
        'fontSize',  // 字号
        'fontName',  // 字体
        'italic',  // 斜体
        'underline',  // 下划线
        'strikeThrough',  // 删除线
        'foreColor',  // 文字颜色
        'backColor',  // 背景颜色
        'link',  // 插入链接
        'list',  // 列表
        'justify',  // 对齐方式
        'quote',  // 引用
        'emoticon',  // 表情
        'image',  // 插入图片
        'table',  // 表格
        // 'video',  // 插入视频
        'code',  // 插入代码
        'undo',  // 撤销
        'redo'  // 重复
    ]
    // 忽略粘贴内容中的图片
    editor.customConfig.pasteIgnoreImg = true
    // 将图片大小限制为 5M
    editor.customConfig.uploadImgMaxSize = 5 * 1024 * 1024
    // 限制一次最多上传 5 张图片
    editor.customConfig.uploadImgMaxLength = 5
    editor.customConfig.uploadImgServer = '/notice/uploadImg'
    editor.customConfig.uploadFileName = 'myFileName'

    // 使用 base64 保存图片
    // editor.customConfig.uploadImgShowBase64 = true
    // 将 timeout 时间改为 3s
    editor.customConfig.uploadImgTimeout = 3000

    // 自定义处理粘贴的文本内容
    editor.customConfig.pasteTextHandle = function (content) {
        // content 即粘贴过来的内容（html 或 纯文本），可进行自定义处理然后返回
        var myDiv = $("#myDiv").html(content)
        $("#myDiv img").each(function () {
            var src = $(this).attr("src");
            console.log(src);
            // 上传至服务器并返回url
            // todo
        });
        return $("#myDiv").html()
    }

    editor.customConfig.uploadImgHooks = {
        before: function (xhr, editor, files) {
            // 图片上传之前触发
            // xhr 是 XMLHttpRequst 对象，editor 是编辑器对象，files 是选择的图片文件

            // 如果返回的结果是 {prevent: true, msg: 'xxxx'} 则表示用户放弃上传
            // return {
            //     prevent: true,
            //     msg: '放弃上传'
            // }
        },
        success: function (xhr, editor, result) {
            // 图片上传并返回结果，图片插入成功之后触发
            // xhr 是 XMLHttpRequst 对象，editor 是编辑器对象，result 是服务器端返回的结果
            console.log("image url = " + result.data)
        },
        fail: function (xhr, editor, result) {
            // 图片上传并返回结果，但图片插入错误时触发
            // xhr 是 XMLHttpRequst 对象，editor 是编辑器对象，result 是服务器端返回的结果
            layer.alert("图片插入错误")
        },
        error: function (xhr, editor) {
            // 图片上传出错时触发
            // xhr 是 XMLHttpRequst 对象，editor 是编辑器对象
            layer.alert("图片上传出错")
        },
        timeout: function (xhr, editor) {
            // 图片上传超时时触发
            // xhr 是 XMLHttpRequst 对象，editor 是编辑器对象
            layer.alert("图片上传超时")
        },
        // 如果服务器端返回的不是 {errno:0, data: [...]} 这种格式，可使用该配置
        // （但是，服务器端返回的必须是一个 JSON 格式字符串！！！否则会报错）
        customInsert: function (insertImg, result, editor) {
            console.log(result)
            // 举例：假如上传图片成功后，服务器端返回的是 {url:'....'} 这种格式，即可这样插入图片：
            // var url = result.url
            // console.log("image url = " + url)

            // 图片上传并返回结果，自定义插入图片的事件（而不是编辑器自动插入图片！！！）
            // insertImg 是插入图片的函数，editor 是编辑器对象，result 是服务器端返回的结果
            // insertImg(url)

            // result 必须是一个 JSON 格式字符串！！！否则报错
            var data = result.data;
            for (var j = 0, len = data.length; j < len; j++) {
                insertImg(data[j])
            }
        }
    };

    editor.create();
    E.fullscreen.init('#editor');
    editor.txt.html($("#contentVal").val());
    NoticeInfoDlg.editor = editor;
});

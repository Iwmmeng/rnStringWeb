<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page isELIgnored="false" %>
<!DOCTYPE HTML>
<html>
<head>
    <meta charset="UTF-8"/>
    <title>Getting Started: Serving Web Content</title>
</head>
<body>
<div>
    <form method="post" id="file" action="" enctype="multipart/form-data">
        选择文件：<input id="excelFile" type="file" name="uploadFile"/>
        <br/><br/>
        <input type="button" value="上传" onclick="uploadFiles();"/>
    </form>
    <br/>
    <button id="button_id" disabled="disabled" onclick="downloadFile()">下载</button>
</div>
</body>
<script type="text/javascript" src="/assets/jquery.min.js"></script>
<script>
    var downloadFileName;
    function uploadFiles() {
//  var uploadFile = $('#excelFile').get(0).files[0];
        var uploadFile = new FormData($("#file")[0]);
        if ("undefined" != typeof(uploadFile) && uploadFile != null && uploadFile != "") {
            $.ajax({
                url: '/mvc/upload',
                type: 'POST',
                data: uploadFile,
                async: false,
                cache: false,
                contentType: false, //不设置内容类型
                processData: false, //不处理数据
                success: function (data) {
                    downloadFileName = "12345";
                    // document.getElementById("button_id").disabled = false;
                    $("#button_id").attr('disabled', false);
                },
                error: function () {
                    alert("解析失败！");
                }
            })
        } else {
            alert("选择的文件无效！请重新选择");
        }
    }

    function downloadFile() {
        var url = "/mvc/download";
// 构造隐藏的form表单
        var $form = $("<form id='download' class='hidden' method='post'></form>");
        $form.attr("action",url);
        $('body').append($form);
// 添加提交参数
        var $input1 = $("<input name='aaa' type='hidden'></input>");
        $input1.attr("value",downloadFileName);
        $("#download").append($input1);
// 提交表单
        $form.submit();
    }
</script>
</html>
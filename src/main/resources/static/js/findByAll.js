$(function () {
    $('#btSearch').click(function () {
        // var keyName = document.forms["myForm"]["keyName"].value; // js 获取文件对象
        // var fileName = document.forms["myForm"]["keyName"].value;
        // var productName = document.forms["myForm"]["keyName"].value;

        var keyName = $("#keyName").val();
        var fileName = $("#fileName").val();
        var productName = $("#productName").val();
        var tbody = window.document.getElementById("tbody-result");
        console.log("keyName"+keyName)
        console.log("keyName"+fileName)
        console.log("keyName"+productName)
        console.log("keyName"+tbody)


        var jsonDataString = {
            "keyName": keyName,
            "fileName": fileName,
            "product": productName
        }
        var urlLink ;
        if(isEmpty(keyName)){
            //todo 提示报错
            console.log("keyName is:"+keyName);
        }else {
            if(isEmpty(fileName) && isEmpty(productName)){
                urlLink = "http://10.38.163.192:9090/info";
            }else if((isEmpty(fileName)) && (!isEmpty(productName))){
                urlLink="http://10.38.163.192:9090/info/product";
            }else if((!isEmpty(fileName)) && (!isEmpty(productName))){
                urlLink= "http://10.38.163.192:9090/info/fileName";
            }else {
                //todo 提示报错
                console.log("keyName is:"+keyName);
            }
        }
        var jsonData = JSON.stringify(jsonDataString);
        $.ajax({
            url: urlLink,//后台请求的数据，用的是PHP
            dataType: "json",//数据格式
            data:
                {
                keyName: keyName,
                fileName: fileName,
                product: productName
            },
            type: "get",//请求方式
            async: false,//是否异步请求
            success: function (data) {   //如果请求成功，返回数据。
                var str = "";
                for (var i = 0; i < data.length; i++) {    //遍历data数组
                    var ls = data[i];
                    str += "<tr>" +
                        "<td>" + data[i].keyName + "</td>" +
                        "<td>" + data[i].en + "</td>" +
                        "<td>" + data[i].zh + "</td>" +
                        "<td>" + data[i].zh_Hant + "</td>" +
                        "<td>" + data[i].ru + "</td>" +
                        "<td>" + data[i].es + "</td>" +
                        "<td>" + data[i].pl + "</td>" +
                        "<td>" + data[i].ko + "</td>" +
                        "<td>" + data[i].it + "</td>" +
                        "<td>" + data[i].fr + "</td>" +
                        "<td>" + data[i].de + "</td>" +
                        "<td>" + data[i].fileName + "</td>" +
                        "<td>" + data[i].product + "</td>" +
                        "</tr>";
                    tbody.innerHTML = str;
                }
                // html += "<span>测试：" + ls.name + "</span>";
            }
        })
    })
})


function isEmpty(v) {
    switch (typeof v) {
        case 'undefined':
            return true;
        case 'string':
            if (v.replace(/(^[ \t\n\r]*)|([ \t\n\r]*$)/g, '').length == 0) return true;
            break;
        case 'boolean':
            if (!v) return true;
            break;
        case 'number':
            if (0 === v || isNaN(v)) return true;
            break;
        case 'object':
            if (null === v || v.length === 0) return true;
            for (var i in v) {
                return false;
            }
            return true;
    }
    return false;
}

// $("#test").html(html); //在html页面id=test的标签里显示html内容

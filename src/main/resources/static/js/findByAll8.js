$(function () {
    $('#btSearch').click(function () {
        var keyName = $("#keyName").val();
        var fileName = $("#fileName").val();
        var myselect = document.getElementById("mySelect");
        var index = myselect.selectedIndex;
        var productName = myselect.options[index].value;
        if(index==0 || productName.indexOf("select")>-1){
            productName="";
        }
        console.log("productName is:"+productName);
        console.log("keyName" + keyName)
        console.log("fileName" + fileName)
        console.log("productName" + productName)
        var urlLink = "http://10.234.22.121:9090/info";
        if (isEmpty(keyName)) {
            //todo 提示报错
            console.log("keyName is:" + keyName);
            if((!isEmpty(productName)) && (isEmpty(fileName))){
                urlLink = urlLink + "/onlyproduct";
            }else if((!isEmpty(productName)) && (!isEmpty(fileName))){
                urlLink = urlLink + "/fileproduct";
            }
            console.log(" keyName is null，urlLink is:" + urlLink);
        } else {
            if (isEmpty(fileName) && isEmpty(productName)) {
                urlLink = urlLink + "";
            } else if ((isEmpty(fileName)) && (!isEmpty(productName))) {
                urlLink = urlLink + "/product";
            } else if ((!isEmpty(fileName)) && (!isEmpty(productName))) {
                urlLink = urlLink + "/all";
            } else if ((!isEmpty(fileName)) && (isEmpty(productName))) {
                urlLink = urlLink + "/fileName";
            } else {
                //todo 提示报错

                console.log("keyName is:" + keyName);
            }
            console.log("urlLink is " + urlLink)
        }

        $('#table').bootstrapTable('showLoading');

        $.ajax({
            // 后台请求的数据，用的是PHP
            url: urlLink,
            // 数据格式
            dataType: "json",
            data: {
                keyName: keyName.trim(),
                fileName: fileName.trim(),
                product: productName.trim()
            },
            // 请求方式
            type: "get",
            // 是否异步请求
            async: false,
            // 如果请求成功，返回数据
            success: function (data) {
                // 返回的对象都是List<keyInfo>,对其进行遍历
                console.log("data is: ", data);

                var i, j;


                console.log("json is : ", json);

                var dynamicHeader = [];

                var json = data[0];
                for (var key in json) {
                    console.log("key: ", key);

                    dynamicHeader.push({
                        field: key,
                        title: key
                    });
                }

                console.log(data, json, dynamicHeader);

                $('#table').bootstrapTable('destroy').bootstrapTable({
                    // 得到的json数据，会根据columns参数进行对应赋值配置
                    data: data,
                    // Bstable工具导航条
                    toolbar: '#toolbar',
                    // 浏览器缓存，默认为true，设置为false避免页面刷新调用浏览器缓存
                    cache: false,
                    // 是否显示行间隔色
                    striped: true,
                    // 分页方式：client客户端分页，server服务端分页
                    sidePagination: "client",
                    // 排序方式
                    sortOrder: "desc",
                    // 每页记录行数
                    pageSize: 25,
                    // 初始化加载第一页
                    pageNumber: 1,
                    // 可供选择的每页行数
                    pageList: "[25, 50, 100, All]",
                    // 是否显示切换按钮
                    showToggle: true,
                    // 是否显示所有的列
                    showColumns: true,
                    // 是否显示导出按钮(下篇文章将会提到)
                    showExport: true,
                    // 导出数据类型(下篇文章将会提到)
                    exportDataType: "basic",
                    // 是否显示分页
                    pagination: true,
                    // 是否启用全匹配搜索，否则为模糊搜索
                    strictSearch: true,
                    // 开启搜索
                    search: true,
                    // 自定义所生成的动态表头放入，结合上述json数据，实现表格数据内容的构建
                    columns: dynamicHeader
                });

                $('#table').bootstrapTable('hideLoading');
            },
            error: function () {
              alert("查询错误！");

              setTimeout(() => {
                location.reload()
              }, 1000)
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

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
        $.ajax({
            url: urlLink,//后台请求的数据，用的是PHP
            dataType: "json",//数据格式
            data:
                {
                    keyName: keyName.trim(),
                    fileName: fileName.trim(),
                    product: productName.trim()
                },
            type: "get",//请求方式
            async: false,//是否异步请求
            success: function (data) {   //如果请求成功，返回数据。
                //返回的对象都是List<keyInfo>,对其进行遍历
                console.log("data is:" + data);
                var i, j;
                var json = data[0];
                console.log("json is :" + json);
                var rowSize = data.length;
                var keyArray = new Array();
                var keyColumns = [];
                var tmp = 0;
                var str = "";
                for(var k=0;k<rowSize;k++){
                    console.log("current data is"+data[k]);
                }
                for (var key in json) {
                    console.log("key" + key);
                    keyArray[tmp] = key;
                    console.log("keyArray[i] is:" + tmp + "  " + keyArray[tmp]);
                    keyColumns.push({
                        field: "field" + tmp,
                        title: key
                    });
                    tmp++;
                }
                $('#table').bootstrapTable({
                    // data: data,
                    toolbar: '#toolbar',
                    cache: false,
                    striped: true,
                    sidePagination: "client",
                    sortOrder: "desc",
                    pageSize: 25,
                    pageNumber: 1,
                    pageList: "[25, 50, 100, All]",
                    showToggle: true,
                    showColumns: true,
                    showExport: true,
                    exportDataType: "basic",
                    pagination: true,
                    strictSearch: true,
                    search: true,
                    columns: keyColumns
                });
                $('#table').bootstrapTable("load",data);
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
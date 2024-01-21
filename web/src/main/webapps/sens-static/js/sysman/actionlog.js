$(document).ready(function() {
    $('#start_date').datepicker({
        inline: false,
        showOn: focus(),
        changeMonth: true,
        changeYear:true,
        dayNames: ['','','','','','',''],
        dayNamesMin: [message_calendar.C0002,message_calendar.C0003,message_calendar.C0004,message_calendar.C0005,message_calendar.C0006,message_calendar.C0007,message_calendar.C0008],
        dayNamesShort: [message_calendar.C0002,message_calendar.C0003,message_calendar.C0004,message_calendar.C0005,message_calendar.C0006,message_calendar.C0007,message_calendar.C0008],
        monthNames: ['','','','','','','','','','','',''],
        monthNamesShort: ['01','02','03','04','05','06','07','08','09','10','11','12'],
        dateFormat: 'yy-mm-dd',
        minDate: "-12m",    // 시작일 (현재부터 1년 전)
        maxDate: new Date(),// 종료일 (오늘까지)
        duration: 10
    });

    $("#start_date").datepicker('setDate', new Date); // 오늘 날짜 넣기

    $('#end_date').datepicker({
        inline: false,
        showOn: focus(),
        changeMonth: true,
        changeYear:true,
        dayNames: ['','','','','','',''],
        dayNamesMin: [message_calendar.C0002,message_calendar.C0003,message_calendar.C0004,message_calendar.C0005,message_calendar.C0006,message_calendar.C0007,message_calendar.C0008],
        dayNamesShort: [message_calendar.C0002,message_calendar.C0003,message_calendar.C0004,message_calendar.C0005,message_calendar.C0006,message_calendar.C0007,message_calendar.C0008],
        monthNames: ['','','','','','','','','','','',''],
        monthNamesShort: ['01','02','03','04','05','06','07','08','09','10','11','12'],
        dateFormat: 'yy-mm-dd',
        minDate: "-12m",	// 시작일 (현재부터 1년 전)
        maxDate: new Date(),// 종료일 (오늘까지)
        duration: 10
    });

    $("#end_date").datepicker('setDate', new Date); // 오늘 날짜 넣기

    $("#pageInfo").hide();

});

var actionlog = {

    search:function(cpage){
        if(cpage){
            $("#cpage").val(cpage);
        }else {
            $("#cpage").val('1');
        }
        var param = $("#actionLogListForm").serialize();
        $.ajax( {
            url : "../actionlog/search.json",
            data : param,
            type : "POST",
            dataType : "json",
            async : false,
            success : function(data) {
                $("#tbody_noData").empty();
                $("#firstPage").empty();

                //console.log(data.result);
                var result = data.result;
                var str = [], n = -1;
                var col = ['log_date','ip','userid','menu','param'];
                for (var i = 0; i < result.length; i++){
                    str[++n] = "<tr>";
                    for(var j = 0; j < col.length; j++){
                        if(col[j] == 'log_date'){
                            str[++n] = "<td>" + new Date(result[i][col[j]].time).format("yyyy-mm-dd hh:nn") + "</td>";
                        }else if(col[j] == 'menu'){
                            var code = result[i].imbActionMenu.menu;
                            str[++n] = "<td>"+code+"</td>";
                        }
                        else {
                            str[++n] = "<td>" + util.html2ascii(result[i][col[j]]) + "</td>";
                        }
                    }
                    str[++n] = "</tr>";
                }

                $("#log_result").html(str.join(''));
                var page = data.pageInfo;
                if(result.length == 0) {
                    $("#tbody_noData").append('<td colspan="6">' +'<p class="txt center">' + message_sysman.O00520001 + '<p>'+'</td>');
                }

                if(result.length > 0){

                    var pageHtml = pageInfo(page.cpage, page.pageSize, page.total, '', 'actionlog.search(');
                    $("#pageInfo").html(pageHtml);
                    $("#pageInfo").show();
                    $("#result_count").text(page.total + message_sysman.O0038);
                }else{
                    var pageHtml = pageInfo(1, 1, 1, '', 'actionlog.search(');
                    $("#pageInfo").html(pageHtml);
                    $("#result_count").text(page.total + message_sysman.O0038);
                    $("#pageInfo").show();
                    alert(message_sysman.O0037);
                }
            },
            error:function(xhr){
               AjaxUtil.error(xhr);
            }
        });

    },

    listDown:function() {
        if($("#log_result tr").length == 0){
            alert(message_sysman.O0037);
            return;
        }
        var param = $("#actionLogListForm").serialize();
        var url = "listDownload.do?" + param;
        jDownload(url);
    },
    allListDown:function() {
        if($("#log_result tr").length == 0){
            alert(message_sysman.O0037);
            return;
        }
        var param = $("#actionLogListForm").serialize();
        var url = "allListDownload.do?" + param;
        jDownload(url);
    },

    change_pagesize:function (pagesize) {
        var url="/common/changePagesize.json";
        var param = {
            "pagesize":pagesize
        }
        $.ajax({
            url: url,
            type: "get",
            data: param,
            dataType: "json",
            async: false,
            success: function (result) {
                if (result.code == JSONResult.SUCCESS) {
                    $(".drop_menu").toggle();
                    if($("#result_count").text()!="0"+message_sysman.O0038){
                        actionlog.search();
                    }
                } else {
                    alert(result.message);
                }
            }
        });
    },
    change_cpage:function(){
        $("#cpage").val('1');
    },
    list:function(para){
        $("#cpage").val(para);
        $("#actionLogListForm").submit();
    },


}



var report = {
    //인쇄
    print:function () {
        window.print();
    },
    htmlSave:function (msgid) {
        var html = $("#printBody").html();
        $("#html").val(html);
        var param = $("#saveForm").serialize();
        var csrfToken = $("meta[name='_csrf']").attr("content");

        //var url = "/mail/result/htmlDownload.json"+"?_csrf="+csrfToken;
        var url = "/mail/result/htmlDownload.json";

        //document.saveForm.submit();

        jDownload(url, "POST", param);
    },
    /** 레포트 팝업페이지를 닫는다. */
    closeReport:function () {
        window.close();
    }
}

var resultList = {
    list:function(para){
        $("#cpage").val(para);
        $("#emsListForm").submit();
    },

    listSend : function(listcpage, srch_key){
        location.href ="list.do?cpage="+listcpage+"&srch_keyword="+srch_key;
    },
    viewAll:function(){
        var bview = document.getElementById("allview");
        bview.style.display = "none";
        location.href = "list.do";
    },

    /** 새로고침 비동기식으로 리스트를 갱신한다. */
    reLoad: function () {
        $("#emsListForm").submit();
    },
    /** 검색 */
    search: function () {
        $("#cpage").val('1');
        var chk_keyword = $("#srch_keyword").val();
        if(chk_keyword == "") {
            alert("검색어를 입력해주세요.");
           return false;
        }else {
            $("#emsListForm").submit();

        }

        doDisplay();

    },
    /** 카테고리 열기 **/
    moveCategoryLayreOpen: function () {
        var msgids = resultList.checkedList();

        var selCategory = $("#moveLayerCategory option:selected").val();

        if (msgids == 0) {
            alert(message_sendManage.S0031); // 선택된 항목이 없습니다.
            return false;
        }

        $("#moveCategoryLayer").show();
    },
    /** 카테고리 닫기 **/
    moveCategoryLayerClose: function () {
        $("#moveCategoryLayer").hide();
    },
    /** 카테고리 이동 **/
    moveCategoryConfirm: function () {
        var msgids = resultList.checkedList();
        var selCategory = $("#moveLayerCategory option:selected").val();

        if (msgids == 0) {
            alert(message_sendManage.S0031); // 선택된 항목이 없습니다.
            return false;
        }

        if (selCategory == '' || selCategory == null) {
            alert(message_sendManage.S0031) // 선택된 항목이 없습니다.
        }

        var param = {
            "msgids": msgids,
            "categoryid": selCategory,
        };

        var url = "/mail/result/categoryMove.json";
        $.ajax({
            url: url,
            type: "post",
            data: param,
            dataType: "json",
            cache: false,
            async: false,
            success: function (data) {
                if (data.result) {
                    alert(data.message); // 카테고리 이동을 완료하였습니다.
                    $("#moveCategoryLayer").hide();
                    resultList.reLoad();
                    //return;
                } else {
                    alert(data.message); // 작업중 오류가 발생하였습니다.
                    return;
                }
            },
            error: function (xhr, txt) {
                AjaxUtil.error(xhr);
            },
        });
    },
    /** 카테고리 선택에 따른 카테고리 정렬*/
    categorySort: function () {
        $("#emsListForm").submit();
    },
    /**
     * 리스트의에서 선택된 목록의 ukey값을 리턴
     * @returns {any[]}
     */
    checkedList: function () {
        var ukeys = new Array();
        $("input[name='msgid']:checkbox:checked").each(function () {
            ukeys.push($(this).val());
        });
        return ukeys;
    },
    /** 발송상태에 맞게 소팅한다.**/
    stateListSort: function () {
        var sel = $("#stateSort option:selected").val();
        $("#state").val(sel);
        $("#emsListForm").submit();

    },
    /** 선택된 목록을 삭제한다. */
    delete: function () {
        var msgids = resultList.checkedList();

        if (msgids.length == 0) {
            alert(message_sendManage.S0031); //선택된 항목이 없습니다.
            return false;
        }

        var param = {
            "msgids": msgids
        }
        var url = "/mail/result/delete.json";

        if (confirm(message_sendManage.S0032)) { //"선택한 메일을 정말로 삭제 하시겠습니까? 삭제된 메일은 복원할 수 없습니다."
            $.ajax({
                url: url,
                type: "post",
                data: param,
                dataType: "json",
                async: false,
                error: function (xhr, txt) {
                    AjaxUtil.error(xhr);
                },
                success: function (result) {
                    if (result.code == JSONResult.FAIL) {
                        alert(result.message);
                    } else {
                        alert(result.message);
                        location.reload();
                    }
                }
            });
        }
    },
    /** 재발송을 시도한다.
     * 031 전송중지(발송중) , 011 전송중지(수신자추출중), 100 전송중지(대기중)가 아니면 재발신을 할 수 없다.
     * */
    doResend: function (msgid,msg_name,is_reserve) {
        if(is_reserve != ''){/** 예약발송 메일일 경우 예약일이 아닌 즉시 재발송되기 때문에 confirm 창을 띄운다. **/
            if(!confirm((message_sendManage.S0042))) return;
        }

        /** 031 전송중지(발송중) , 011 전송중지(수신자추출중), 100 전송중지(대기중) 인 경우만 발송을 할 수 있다.*/
        var url = "/mail/result/resend.json";
        var pram = {
            "msgid": msgid,
            "msg_name": msg_name
        }
        $.ajax({
            url: url,
            type: "post",
            data: pram,
            dataType: "json",
            async: false,
            error: function (xhr, txt) {
                AjaxUtil.error(xhr);
            },
            success: function (result) {
                if (result.code == JSONResult.FAIL) {
                    alert(result.message);
                    location.reload();
                } else {
                    alert(result.message);
                    location.reload();
                }

            }
        });

    },

    /** 선택된 항목의 발송중지를 시도한다.
     * */
    doStop: function () {
        var msgids = resultList.checkedList();

        if (msgids.length > 1) {
            alert(message_sendManage.S0036); // 발송중지는 한개만 가능합니다. 한개만 선택해 주세요.
            return false;
        } else if (msgids.length == 0) {
            alert(message_sendManage.S0037); //선택된 메일이 없습니다. 중지할 메일을 선택해 주세요.
        }

        // 현재 선택된 값의 요소에 해당하는 state 값을 가져온다
        var state = $("input[name='msgid']:checkbox:checked").attr("state").trim();
        var msgid = $("input[name='msgid']:checkbox:checked").val();

        /** 000 발송대기 , 010 수신자 추출중, 030 발송중, 032 재전송중, +10 수신자 추출완료 상태일때만 정지가 가능하다. */
        if (state == '000' || state == '010' || state == '030' || state == '032' || state == '+10') {
            var url = "/mail/result/stop.json";
            var pram = {"msgid": msgid}
            $.ajax({
                url: url,
                type: "post",
                data: pram,
                dataType: "json",
                async: false,
                error: function (xhr, txt) {
                    AjaxUtil.error(xhr);
                },
                success: function (result) {
                    if (result.code == JSONResult.FAIL) {
                        alert(result.message);
                        location.reload();
                    } else {
                        alert(result.message);
                        location.reload();
                    }

                }
            });
        } else {
            alert(message_sendManage.S0038); // 현재는 중지가 불가능한 상태입니다.
        }

    },
    /** 리포팅 팝업 페이지를 출력한다. */
    openReport:function (msgid) {
        var url = "/mail/result/reporting.do?msgid="+msgid;
        var w = window.open(url,'리포팅 ','width=800px,height=750px,location=no,status=no,scrollbars=yes,menubar=no,resizable=yes')
        w.window.focus();

    },
    /** 수신자목록 팝업 페이지를 출력한다. */
    openReceiverList:function (msgid) {
        var url = "/mail/result/receiverList.do?msgid="+msgid;
        var w =  window.open(url,'수신자목록 ','width=1060px,height=750px,location=no,status=no,scrollbars=yes,menubar=no,resizable=yes');
        w.window.focus();
    },
    /** 발송통계 페이지를 출력한다. */
    goStaticSend:function (msgid,listcpage,srch_key) {
        location.href = "staticSend.do?msgid="+msgid+"&listcpage="+listcpage+"&srch_key="+srch_key;
    },
    /** 에러통계 페이지를 출력한다. */
    goStaticError:function (msgid,listcpage,srch_key) {
        location.href = "staticError.do?msgid="+msgid+"&listcpage="+listcpage+"&srch_key="+srch_key;
    },
    /** 수신확인 통계 페이지를 출력한다. */
    goStaticReceipt:function (msgid,listcpage,srch_key){
        location.href = "staticReceipt.do?msgid="+msgid+"&listcpage="+listcpage+"&srch_key="+srch_key;

    },
    /** 링크 통계 페이지를 출력한다. */
    goStaticLink:function (msgid,listcpage,srch_key){
        location.href = "staticLink.do?msgid="+msgid+"&listcpage="+listcpage+"&srch_key="+srch_key;

    },
    /** 상세보기 수신확인 통계 목록 다운로드를 진행한다. */
    downReceiptList:function (msgid, recvdate){
        if(msgid != null && recvdate != null){
            var url = "/mail/result/downReceiptList.do?msgid="+ msgid + "&recvdate=" + recvdate;
            jDownload(url);
        }else{
            if (confirm(message_common.CM0017)) {
                var param = $("#receiverListForm").serialize()
                var url = "/mail/result/downReceiptList.do?"+param;
                jDownload(url);
            }
        }
    },
    listDownReceiptList:function (msgid, receipt){
        if(msgid != null && receipt != null){
            var url = "/mail/result/listDownReceiptList.do?msgid="+ msgid + "&receipt=" + receipt;
            jDownload(url);
        }else{
            var url = "/mail/result/listDownReceiptList.do?msgid="+ msgid;
            jDownload(url);
        }
    },
    nodataList:function (){
        alert("저장할 목록이 없습니다.");
    },

    goStaticSends:function (msgid) {

        location.href = "/mail/result/staticSend.do?msgid="+msgid;

    },

    goStaticPage:function (msgid,listcpage,srch_key) {
        location.href = "/mail/result/staticPage.do?msgid="+msgid+"&listcpage="+listcpage+"&srch_key="+srch_key;
    },

    downLinkClickList:function (msgid, linkid){
        var url = "/mail/result/downLinkClickList.do?msgid="+ msgid + "&linkid=" + linkid;
        jDownload(url);
    }
    ,
    /** 임시보관 메일의 쓰기페이지를 출력한다. */
    goLayoutWriteForm:function (msgid) {
        location.href = "/mail/write/form.do?msgid="+msgid;
    },
    /**
     * 재발신 기능
     * TODO 발신기능으로 기존 발송된 데이터의 가공, 비가공 유무가 판단이 필요한 부분으로 발송 기능 완료 후 추후 구현 예정
     */
    reSend: function () {
        var Dmail = [];
        var state;
        $("input[name='msgid']:checkbox:checked").each( function() {
            Dmail.push( $(this).attr("value").split(",")[0] );

        });
        state = $("input[name='msgid']:checkbox:checked").attr("state");

        if(Dmail.length == 0){
            alert( message_common.CM0036 );
            return;
        }
        if(Dmail.length > 1){
            alert( message_common.CM0035 );
            return;
        }
        // 발송 완료된 메일만 재작성 가능
        if(state != '+30'){
            alert(message_common.CM0037 );
            return;
        }
        var msgid = $("input[name='msgid']:checkbox:checked").val();
        var url = "/mail/result/permissionCheck.json";
        var pram = {"msgid": msgid}
        $.ajax({
            url: url,
            type: "post",
            data: pram,
            dataType: "json",
            async: false,
            error: function (xhr, txt) {
                AjaxUtil.error(xhr);
            },
            success: function (result) {
                if (result.code == JSONResult.FAIL) {
                    alert(result.message);
                    location.href="/mail/result/list.do";
                } else {
                    location.href = "/mail/write/resendform.do?msgid="+Dmail;
                }
            }
        });
    },
}
var receiverListPopup = {
    /**
     * 하단 페이지 목록
     */
    pagesize:function(obj){
        $("#pagesize").val( obj.value );
        $("#receiverListForm").submit();
    },
    /**
     * 선택한 페이징을 처리한다
     * @param para
     */
    list:function(para){
        if($("#cpage").val(para) == 0){
            $("#cpage").val(1);
        }
        $("#cpage").val(para);
        $("#receiverListForm").submit();
    },
    /**
     * 현재창을 닫는다.
     */
    closeReceiverList:function () {
        window.close();
    },
    /**
     * 수신자 목록에서 검색을 수행
     */
    search: function () {
        var email = $("#srch_keyword").val();
        var srch_type =$("#srch_type").val();
        if( !EmailAddressUtil.checkAddress(email)&& srch_type =="01"){
            alert("이메일 형식이 올바르지 않습니다.\n" +
                "이메일 검색 시, 정확한 이메일을 입력해주세요.\n" +
                "ex) user@domain.com");
        }
        $("#cpage").val('1');
        $("#recv_count").val();
        $("#receiverListForm").submit();
    },

    recvsearch: function () {
        var email = $("#srch_keyword").val();
        var srch_type =$("#srch_type").val();
        $("#cpage").val('1');
        $("#recv_count").val();
        $("#receiverListForm").submit();
    },
    viewAll:function(){
        var bview = document.getElementById("allview");
        bview.style.display = "none";
        $("#srch_keyword").val('');
        $("#cpage").val('1');
        $("#receiverListForm").submit();
    },
    /**
     * 재발신 기능
     * TODO 발신기능으로 기존 발송된 데이터의 가공, 비가공 유무가 판단이 필요한 부분으로 발송 기능 완료 후 추후 구현 예정
     */
    reSend: function () {
        var msgid = $("#msgid").val();
        var recvid = $('input:checkbox[name="recvid"]:checked').val();

        if(recvid == null){
            alert(message_sendManage.S0034)
        }else{

            var url = "/mail/write/clickresendform.do?msgid="+msgid+"&recvid="+recvid;
            var w =  window.open(url,'메일쓰기 ','width=1060px,height=750px,location=no,status=no,scrollbars=yes,menubar=no,resizable=yes');
            w.window.focus();
        }
    },
}

var linkPopupList ={
    /**
     * 하단 페이지 목록
     */
    pagesize:function(obj){
        $("#pagesize").val( obj.value );
        $("#LinkListForm").submit();
    },
    /**
     * 선택한 페이징을 처리한다
     * @param para
     */
    list:function(para){
        $("#cpage").val(para);
        $("#LinkListForm").submit();
    },
    /**
     * 전체 목록으로 돌아간다.
     */
    viewAll:function(){
        var bview = document.getElementById("allview");
        bview.style.display = "none";
        $("#srch_keyword").val('');
        $("#cpage").val('1');
        $("#LinkListForm").submit();
    },
    /**
     * 현재창을 닫는다.
     */
    closeReceiverList:function () {
        window.close();
    },
    /**
     * 수신자 목록에서 검색을 수행
     */
    search: function () {
        $("#cpage").val('1');
        $("#LinkListForm").submit();
    },
    /**
     * 재발신 기능
     * TODO 발신기능으로 기존 발송된 데이터의 가공, 비가공 유무가 판단이 필요한 부분으로 발송 기능 완료 후 추후 구현 예정
     */
    reSend: function () {
        alert("메일쓰기 기능으로 기존 발송된 데이터의 가공, 비가공 유무가 판단이 필요한 부분으로 발송 기능 완료 후 추후 구현 예정");
    },

    downloadLinkList: function () {
        if (confirm(message_common.CM0017)) {
            var param = $("#LinkListForm").serialize();
            var url = "/mail/result/listDownlinkList.do?" + param;
            jDownload(url);
        }
    },

}

var staticsendHsList ={
    /**
     * 하단 페이지 목록
     */
    pagesize:function(obj){
        $("#pagesize").val( obj.value );
        $("#StatSendForm").submit();
    },
    /**
     * 선택한 페이징을 처리한다
     * @param para
     */
    list:function(para){
        $("#cpage").val(para);
        $("#StatSendForm").submit();
        this.reloadFolder();
    },
    /** 새로고침 비동기식으로 리스트를 갱신한다. */
    reLoad: function () {
        $("#StatSendForm").submit();
    },
    staticHcbar: function (hcinfo) {
        var eration = hcinfo.eration;
        if(eration>100){
            eration=100
        }
        /* 메일 사용량 변경 */
        try {
            //$("#mail_curr_size").html(eration);
            $("#hc_bar").css("width",eration+"%");
        } catch (e) {
            log.error("staticHcbar error");
        }

    },
    reloadFolder:function() {
        var url = app.contextPath + "staticSend.json";
        $.ajax({
            url: url,
            type: "get",
            dataType: "json",
            async: true,
            cache: false,
            timeout: 10000,
            success: function (mboxinfo) {
                try {
                    /*   mailinfo = mboxinfo.mailboxlist;
                       if (typeof(mailinfo) != 'undefined') {
                           sensmail.mbox_mailcount_list = mailinfo;
                       }*/
                    /* 메일 사용량 변경 */
                    try {
                        //$("#mail_curr_size").html(eration);
                        $("#hc_bar").css("width",eration+"%");
                    } catch (e) {
                        log.error("hc_bar css error");
                    }

                    // 결재메일 건수 표시
                } catch (e) {
                    log.error("reloadfolder error");
                }
            },
            error: function (xhr, txt) {
                AjaxUtil.error(xhr);
            },
        });
    },
    /*  changeRespTime:function () {
          var url = app.contextPath + "resptimeUpdate.json";
          var param = "resp_date="+$('#resp_date').val()+"&resp_hour="+$('#resp_hour').val() + "&resp_min=" + $('#resp_min');
          $.ajax({
              url: url,
              type: "get",
              dataType: "json",
              param : param,
              cache: false,
              async: false,
              error: function (xhr, txt) {
                  AjaxUtil.error(xhr);
              },
              success: function (result) {
                  if (result.code == JSONResult.FAIL) {
                      alert(result.message);
                      location.reload();
                  } else {
                      alert(result.message);
                      location.reload();
                  }
          }
      });
      },*/
    changeDatesub: function(time){
        return time.substring(0,4)+time.substring(5,7)+time.substring(8,10);
    },
    editChanegResp_time: function () {
        var response_date =  $('#resp_time').val();
        var resp_date = staticsendHsList.changeDatesub(response_date);
        var resp_hour = $('#resp_hour').val();
        var resp_min = $('#resp_min').val();
        var msgid = $('#msgid').val();
        var now = staticsendHsList.dateFormatDate(new Date()); // 현재 날짜시간 yyyymmddhhmm 로 변환
        var resp_day = resp_date + $('#resp_hour').val() + $('#resp_min').val(); // 반응분석 종료일 yymmddhhmm 로 변환

        if(Number(resp_day) <= Number(now)){ // 반응분석 종료일이 현재 시간보다 이전이면 return
            alert(message_common.CM0031);
            return ;
        }
        var url = "/mail/result/resptimeUpdate.json";
        var param = {
            "resp_date": resp_date,
            "resp_hour": resp_hour,
            "resp_min": resp_min,
            "msgid": msgid
        }
        $.ajax({
            url: url,
            data : param,
            type : "post",
            dataType: "json",
            async: false,
            cache: false,
            error:function(xhr) {
                AjaxUtil.error(xhr);
            },
            success:function(result){
                if (result.code == JSONResult.FAIL) {
                    alert(result.message);
                } else {
                    alert(result.message);
                    location.reload();
                }
            }
        });
    },
    dateFormatHour(date) {
        let hour = date;
        hour = hour >= 10 ? hour : '0' + hour;

        return  hour;
    },
    dateFormatMin(date) {
        let minute = date;
        minute = minute >= 10 ? minute : '0' + minute;

        return minute;
    },
    dateFormatDate(date) {
        let month = date.getMonth() + 1;
        let day = date.getDate();
        let hour = date.getHours();
        let minute = date.getMinutes();
        let second = date.getSeconds();

        month = month >= 10 ? month : '0' + month;
        day = day >= 10 ? day : '0' + day;
        hour = hour >= 10 ? hour : '0' + hour;
        minute = minute >= 10 ? minute : '0' + minute;
        second = second >= 10 ? second : '0' + second;

        return date.getFullYear() + month + day + hour + minute;
    }

}

var send_resultList ={
    list:function(para){
        $("#cpage").val(para);
        $("#testSendListForm").submit();
    },

    viewAll:function(){
        var bview = document.getElementById("allview");
        bview.style.display = "none";
        location.href = "sendList.do";
    },

    /** 검색 */
    search: function () {
        $("#cpage").val('1');
        if ($("#srch_keyword").val() == "") {
            alert("검색어를 입력해주세요.");
            return;
        }
        $("#testSendListForm").submit();
    },

    /**
     * 리스트에서 선택된 목록의 ukey값을 리턴
     * @returns {any[]}
     */
    checkedList: function () {
        var ukeys = new Array();
        $("input[name='ukey']:checkbox:checked").each(function () {
            ukeys.push($(this).val());
        });
        return ukeys;
    },

    /** 선택된 목록을 삭제한다. */
    delete: function () {
        var ukeys = send_resultList.checkedList();

        if (ukeys.length == 0) {
            alert(message_sendManage.S0031); //선택된 항목이 없습니다.
            return false;
        }

        var param = {
            "ukeys": ukeys
        }
        var url = "/mail/result/testDelete.json";

        if (confirm(message_sendManage.S0032)) { //"선택한 메일을 정말로 삭제 하시겠습니까? 삭제된 메일은 복원할 수 없습니다."
            $.ajax({
                url: url,
                type: "post",
                data: param,
                dataType: "json",
                async: false,
                error: function (xhr, txt) {
                    AjaxUtil.error(xhr);
                },
                success: function (result) {
                    if (result.code == JSONResult.FAIL) {
                        alert(result.message);
                    } else {
                        alert(result.message);
                        location.reload();
                    }
                }
            });
        }
    },
    /** 개별발송 목록에서 선택한 목록의 상세내용 출력 */
    view:function(traceid,serverid,rcptto){

        var param = {
            "traceid": traceid,
            "serverid": serverid,
            "rcptto": rcptto,
        }

        var url =  "/mail/result/testSend.json";
        $.ajax({
            url : url,
            type: "get",
            data : param,
            dataType : "json",
            cache : false,
            async : false,
            success : function(data) {

                var transmitData = data.transmitData;
                $("#view_subject").text(transmitData.subject);
                $("#view_date").text($.formatDate(new Date(transmitData.logdate.time), "yyyy-MM-dd HH:mm"));
                $("#view_from").text( transmitData.mailfrom);
                $("#view_to").text( transmitData.rcptto );
                $("#view_serverid").text( transmitData.serverid );
                $("#view_sendip").text( transmitData.ip );
                $("#view_traceid").text( transmitData.traceid );

                if(transmitData.result == 0){
                    // $("#view_result").html(data.msg +'('+ transmitData.errcode +')' +'('+ data.Errmsg+')');
                    $("#view_result").html(data.msg +' : '+ data.Errmsg);
                }else{
                    $("#view_result").text(data.msg);
                }

                if(transmitData.authid != '' && transmitData.authid != null){
                    $("#view_smtpid").text( transmitData.authid );
                } else {
                    $("#view_smtpid").text('');
                }

                if( transmitData.readdate != '' && transmitData.readdate != null){
                    $("#view_readdate").text($.formatDate(new Date(transmitData.readdate.time), "yyyy-MM-dd HH:mm") + " ("+"수신확인 횟수 : "+transmitData.readcount+")");
                } else{
                    $("#view_readdate").text('');
                }

                $("#modal_view").show();
                $("#popup_content").show();
            },
            error : function(xhr, txt) {
                AjaxUtil.error( xhr );
            },
        });
    },
    view_close:function(){
        $("#view_subject").text("");
        $("#view_date").text("");
        $("#view_from").text("");
        $("#view_to").html( "" );


        $("#modal_view").hide();
        $("#popup_content").hide();

    }
    ,doresend:function(msgid,flag,linkid){
        var url = "/mail/result/clickResend.json";
        var returnUrl = "/mail/write/resendform.do?msgid=" + msgid + "&flag=" + flag;
        var param = {
            "msgid": msgid,
            "flag":flag,
            "linkid":linkid
        };
        if(linkid != undefined) returnUrl += "&linkid=" + linkid;
        $.ajax({
            url: url,
            type: "post",
            data: param,
            dataType: "json",
            async: false,
            error: function (xhr, txt) {
                AjaxUtil.error(xhr);
            },
            success: function (result) {
                if (result.code == JSONResult.FAIL) {
                    alert(result.message);
                } else {
                    location.href=returnUrl;
                }
            }
        });

    }
    ,sendTypeSort:function (){
        var sel=$("#sendTypesort option:selected").val();
        console.log("sel : {}", sel);
        $("#send_type").val(sel);
        console.log("sendtype :", $("#send_type").val(sel));
        $("#testSendListForm").submit();
    }

}

var statisticsError ={
    list:function(msgid, errtype){
        window.open('/mail/result/statErrorList.do?msgid=' + msgid + '&errcode=' + errtype,'에러 상세 목록','width=1200px,height=600px,location=no,status=no,scrollbars=yes,menubar=no,resizable=yes');
    },
    /** 검색 */
    search: function () {
        $("#cpage").val('1');
        if ($("#srch_keyword").val() == "") {
            alert("검색어를 입력해주세요.");
            return;
        }
        $("#StatErrorForm").submit();
    },
    download:function (msgid, code) {
        if(msgid != null && code != null){
            var url = "/mail/result/statErrorDownload.do?msgid=" + msgid + "&code=" + code;
            jDownload(url);
        }else {
            if (confirm(message_common.CM0017)) {
                var param = $("#StatErrorForm").serialize()
                var url = "/mail/result/statErrorDownload.do?" + param;
                jDownload(url);
            }
        }
    },

    closePopup:function () {
        window.close();
    },
    /**
     * 전체 목록으로 돌아간다.
     */
    viewAll:function(){
        var bview = document.getElementById("allview");
        bview.style.display = "none";
        $("#srch_keyword").val('');
        $("#cpage").val('1');
        $("#StatErrorForm").submit();
    },
    listpage:function(param){
        if($("#cpage").val(param) == 0){
            $("#cpage").val(1);
        }
        $("#cpage").val(param);
        $("#StatErrorForm").submit();
    },doerrorresend:function (param){
        var url = "/mail/result/errorResend.json";
        var result = confirm(message_sendManage.S0039);
        var param = {
            "msgid": param,
        };
        if(result){
            $.ajax({
                url: url,
                type: "post",
                data: param,
                dataType: "json",
                async: false,
                error: function (xhr, txt) {
                    AjaxUtil.error(xhr);
                },
                success: function (result) {
                    if (result.code == JSONResult.FAIL) {
                        alert(result.message);
                    } else {
                        alert(result.message);
                        location.href="/mail/result/list.do";
                    }
                }
            });
        }}

}

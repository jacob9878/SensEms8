$(document).ready(function() {

    writeform.editorInit();

    /**
     * 발송 시작일 달력 선언
     */
    $('#start_time').datepicker({
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
        minDate: new Date(),	// 시작일
        maxDate: "12m",			// 종료일(현재부터 1년까지만)
        duration: 10
    });

    $("#start_time").datepicker('setDate', new Date); // 오늘 날짜 넣기

    /**
     * 발송 종료일 달력 선언
     */
    $('#end_time').datepicker({
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
        minDate: new Date(),	// 시작일
        maxDate: "24m",			// 종료일(현재부터 2년까지만)
        duration: 10
    });

    $("#end_time").datepicker('setDate', new Date); // 오늘 날짜 넣기

});
var writeform= {
    editorInit:function(){
        tinyMce.init();
    }
};

/** 발송시 처리 이벤트 */
var reserveEvent = {

    /**
     * 반복설정값에 따라 메뉴를 숨기고 활성화 처리를 한다.
     * 매일, 매주, 매월 파라메터를 보이고 숨김 
     * @param pram
     */
    viewRepeat:function(pram){

        if(pram == 0){
            $("#repeat_day").hide();
            $("#repeat_month").hide();
        }else if(pram == 1){
            $("#repeat_day").show();
            $("#repeat_month").hide();
        }else if(pram == 2){
            $("#repeat_month").show();
            $("#repeat_day").hide();
        }
    },

    /** 정기예약발송 등록 처리 */
    write:function(){
        var chk = false;

        /** 제목, 수신자, 반복일정을 체크 */
        chk = reserveEvent.validation();

        if(!chk){ return; }

        /** timeSetter = 시간 값을 추출해서 조합 및 설정 */
        chk =  reserveEvent.timeSetter();

        if(!chk){ return; }

        /** doSave = 발송 */
        reserveEvent.doSave();

    },
    /** doSave = 발송 */
    doSave: function () {
        var f = document.reserveSendForm;

        var update_flag = $("#update_flag").val();
        var ishtml = $("input[name='ishtml']:checked").val() ;

        if(ishtml == '1'){
            var content = $("#editHtml").editor("getHtml");
        }else{
            var content = $("#editHtml").editor("getText");
        }

        $("#content").val(content);

        if(update_flag != 1){ /// update flag가 1이 아니면 write.do를 호출한다.
            f.submit();
        }else{ // target을 modify.do를 호출하게 한다.
            f.action = "/send/reserve/modify.do";
            f.submit();
        }
    },

    /** timeSetter = 시간 값을 추출해서 조합 및 설정 */
    timeSetter: function(){
        var f = document.reserveSendForm;

        //반복일정을 선택한게 있는지 없는지 체크
        if(f.rot_flag[0].checked == false && f.rot_flag[1].checked == false && f.rot_flag[2].checked == false){
            alert(message_sendManage.S0024)
            return false;
        }

        if(f.rot_flag[1].checked == true){  // rot_flag1 = 매주
            var nWCnt = 0;
            var rot_point = "";
            //
            for (var k = 0 ; k < f.elements.length ; k++) {
                if (f.elements[k].name == 'send_week[]' && f.elements[k].checked == true) {
                    rot_point+= f.elements[k].value+",";
                    nWCnt++;
                }
            }

            if(nWCnt < 1){
                alert(message_sendManage.S0025); // 발송요일을 선택해주세요.
                return false;
            }

            var n = rot_point.lastIndexOf(",");
            rot_point = rot_point.substring(0, n);
            $("#rot_point").val(rot_point); // 선택 값을 세팅

        } else if(f.rot_flag[2].checked == true){ // rot_flag2 = 매월
            var wDCnt = 0;
            var rot_point = "";
            for (var k = 0 ; k < f.elements.length ; k++) {
                if (f.elements[k].name == 'send_date[]' && f.elements[k].checked == true) {
                    rot_point+= f.elements[k].value+",";
                    wDCnt++;
                }
            }

            if(wDCnt < 1){
                alert(message_sendManage.S0025); // 발송요일을 선택해주세요.
                return false;
            }

            var n = rot_point.lastIndexOf(",");
            rot_point = rot_point.substring(0, n);
            $("#rot_point").val(rot_point); // 선택 값을 세팅
        }

        // 현재시간과 비교 하여 발송 예약시간이 지금 시간보다 늦은 시간인지 체크
        var now = new Date();

        //시작일
        var start_time = $("#start_time").val();
        start_time = start_time.replace(/([\d]{4})([\d]{2})([\d]{2})/,"$1$2$3");

        // 시작일
        var year = start_time.substring(0,4);
        var month = start_time.substring(5,7);
        var day = start_time.substring(8,10);

        // 종료일
        var end_time = $("#end_time").val();
        end_time = end_time.replace(/([\d]{4})([\d]{2})([\d]{2})/,"$1$2$3");

        var e_year = end_time.substring(0,4);
        var e_month = end_time.substring(5,7);
        var e_day = end_time.substring(8,10);



        // 시간
        var hour = $("#reserve_hour").val();
        var minute = $("#reserve_minute").val();

        var reservDate = new Date( year , month -1 , day , hour , minute , 0);
        var e_reservDate = new Date( e_year , e_month -1 , e_day , hour , minute , 0);

        toNow = now.getTime();
        var toReserveDate = reservDate.getTime();
        var e_toReserveDate = e_reservDate.getTime();

        if( toNow > toReserveDate ){
            alert(message_sendManage.S0026); //발송기간중 발송시작일은 현재 시간 이후로 설정하셔야 합니다.
            return false;
        }

        if( e_toReserveDate < toReserveDate ){
            alert(message_sendManage.S0029); //발송기간중 발송시작일은 현재 시간 이후로 설정하셔야 합니다.
            return false;
        }

        return true;
    },

    /** 제목, 수신자, 반복일정을 체크 */
    validation: function () {
        var subject = $("#msg_name").val();
        var reid = $("#recid").val();

        // 제목체크
        if(subject == null || subject == ''){
            alert(message_sendManage.S0014); // 제목을 입력해주세요.
            return false;
        }

        // 주소록 or 수신그룹 체크 (둘다 선택안되었다면 선택하라고 안내
        if(reid == null || reid == ''){
            alert(message_sendManage.S0027); // 수신그룹이 선택되지 않았습니다. 수신그룹을 선택해주세요.
            return false;
        }

        return true;
    },
    // 미리보기 창 닫기
    previewClose: function(){
        $("#previewLayer").hide();
    },
    // 템플릿 추가 창 닫기
    templateAddClose: function(){
        $("#addTemplateLayer").hide();
    },
    // 템플릿 추가 창 보이기 이벤트
    templateAddShow:function () {
        $("#addTemplateLayer").show();
    },
    // 템플릿 추가 데이터 입력 여부 검증
    templateAdd:function () {
        var subject = $("#temp_name").val();
        var flag = $("#flag").val();
        var contents = EditorUtil.getBody();

        if(!subject){
            alert(message_sendManage.S0014);
            return false;
        }

        var param ={"subject":subject, "flag":flag, "contents":contents };

        var url =  "/send/template/templateAdd.json";
        $.ajax({
            url : url,
            type : "post",
            data : param,
            dataType : "json",
            cache : false,
            async : false,
            success : function(data) {
                if(data.result){
                    alert(message_common.CM0014);
                    $("#addTemplateLayer").hide();
                    var subject = $("#temp_name").val('');
                    return;

                }else{
                    alert(message_common.CM0016);
                }
            },
            error : function(xhr, txt) {
                AjaxUtil.error( xhr );
            },
        });


        // 에이작스로 추가하는 이벤트 추가


    },
    /** 에디터 본문 업데이트 */
    updateContent:function(send_time, rot_point, rot_flag, start_time, end_time, receiver_name){
        EditorUtil.setContent();

        /** Rserve write Form 데이터 setting start  */

        /** 수신그룹명 세팅 */
        $("#receiverGroupText").val(receiver_name);


        /** 선택된 flag에 따라서 show이벤트 설정  */
        if(rot_flag == 0){   /** 매일이면 rot_point 아무것도 선택 안함 */
            $("#repeat_day").hide();
            $("#repeat_month").hide();
        }else if(rot_flag == 1){ /** rot_point 매주면  send_week[] 선택  */
            $("#repeat_day").show();
            $("#repeat_month").hide();

            var sel_day =  rot_point.split(",");
            var length = sel_day.length;

            for(var i=0; i < length; i++){
                $('input[name="send_week[]"]').eq(sel_day[i]-1).prop("checked", true);
            }
        }else if(rot_flag == 2){   /** rot_point 매월이면 send_date[] 선택  */
            $("#repeat_month").show();
            $("#repeat_day").hide();

            var sel_day =  rot_point.split(",");
            var length = sel_day.length;

            for(var i=0; i < length; i++){
                $('input[name="send_date[]"]').eq(sel_day[i]-1).prop("checked", true);
            }

            /** send_time, start_time, end_time  setting */
            var hour = send_time.substring(0, 2);
            var mintue = send_time.substring(3, 5);

            $("#reserve_hour").val(hour);
            $("#reserve_minute").val(mintue);

            $("#start_time").val(start_time);
            $("#end_time").val(end_time);

        }
        /** write Form 데이터 setting end  */

    },


    /** test send start **/
    testPopupShow : function () {
        /** 테스트 발송 수행 전 수신그룹이 선택된 부분이 있는지 제목이 입력되어있는지 체크 **/
        var receiver = $("#recid").val();
        var subject = $("#msg_name").val();

        if(receiver == '' || receiver == null){
            alert(message_sendManage.S0027); // 수신그룹이 선택되지 않았습니다. 수신그룹을 선택해주세요.
            return false;
        }

        if(subject == '' || subject == null){
            alert(message_sendManage.S0014); // 제목을 입력해주세요.
            return false;
        }

        /** 테스트 사용자 목록을 불러온다. **/
        reserveEvent.getTestAccountListDraw();

        /** 테스트 발송 레이아웃을 보여준다. **/
        $("#testSendPopup").show();
    },

    /** 테스트 발송 레이아웃을 숨긴다. **/
    closeTestPopup: function () {
        $("#testSendPopup").hide();
        $("#testSendAccount").children('option').remove();
        $("#testAccount").children('option').remove();
    },

    /** 테스트 발송을 수행한다.  **/
    doTestSend : function () {
        /** 테스트 발송 수신자 목록이 존재하는지 체크하여 입력값이 없으면 retrun **/
        var count = 0;
        var to_emails = new Array();

        $("#testSendAccount option").each(function (idx) {
            to_emails.push($(this).val());
            count ++;
        })

        if(count == 0){
            alert(message_sendManage.S0030); //테스트 발송 계정을 추가해주시기 바랍니다.
            return false;
        }

        /** 테스트 발송 데이터 setting **/
        //TODO isAttach값은 테스트 발송에서 필요없으므로 TestSendController에 isAttach 디폴트 값을 0으로 처리하였으므로 발송단에서는 해당 값을 추출해서 param으로 전달 필요

        var subject = $("#msg_name").val();
        var charset = $("#charset").val();
        var ishtml = $("input[name='ishtml']:checked").val() ;
        var from_email = $("#mail_from").val();
        var recid = $("#recid").val();
        //TODO 수신자 그룹 선택 유형 처리 필요 rectype = 1(주소록) rectype = 2(수신그룹) - 정기예약발송은 수신유형이 수신그룹만 존재함. 정기예약발송은 2로 설정해서 전달
        var rectype = "2";
        
        var f = document.reserveSendForm;

        if(ishtml == '1'){
            var content = $("#editHtml").editor("getHtml");
        }else{
            var content = $("#editHtml").editor("getText");
        }
        //TODO 첨부파일 처리 방안 1.첨부파일이 존재하는 경우 editor 본문에 첨부파일 내용을 append 해서 전달하여 처리 하거나 아래와 같이 Param에 isAttach를 1로 전달하여 비지니스 로직에서 처리를 하는 케이스로 처리 혹은 아래와 같이 처리
        //TODO 첨부파일 처리 방안 2.첨부파일이 존재하는 경우 testSendController에서 아래 pram값에 isAttach 1을 포함하여 처리
         var param = {
            "to_emails[]": to_emails,
            "subject": subject,
            "charset": charset,
            "ishtml": ishtml,
            "from_email": from_email,
            "content": content,
            //"isAttach": isAttach,
            "recid" : recid,
            "rectype" : rectype
        };
        // var param = $("#testSendForm").serialize();

        var url = "/send/test/testSend.json";

        $.ajax( {
            url : url,
            data : param,
            type : "POST",
            dataType : "json",
            async : false,
            success : function(data) {
                if(data.result){
                    alert(data.message);
                    $("#testSendPopup").hide();

                }else{
                    alert(data.message);
                }
            },
            error:function(xhr){
                AjaxUtil.error(xhr);
            }
        });
        /** 테스트 발송을 한다. **/
        
    },

    /** 테스트 발송에서 선택된 option의 테스트 계정을 주입한다. **/
    testAccountInsert : function () {
        var sel_account =$("#testAccount option:selected").val();

        if(sel_account== '' || sel_account == null){
            return false;
        }
        $("#testAccount option:selected").remove();
        $("#testSendAccount").append('<option value="'+sel_account + '">'+sel_account+'</option>');
    },

    /** 테스트 발송에서 선택된 option의 테스트 계정을 삭제한다. **/
    testAccountDelete : function () {
        var sel_account = $("#testSendAccount option:selected").val();
        if(sel_account== '' || sel_account == null){
            return false;
        }
        $("#testSendAccount option:selected").remove();
        $("#testAccount").append('<option value="'+sel_account + '">'+sel_account+'</option>');
    },

    /** 테스트 계정 목록을 그린다.
     * 테스트 계정 수신자 및 테스트 계정 기본등록자 영역으로 나뉨
     * */
    getTestAccountListDraw : function () {
        var url = "/send/demoaccount/list.json";
        $.ajax({
            url: url,
            type: "get",
            dataType: "json",
            async: false,
            error:function(xhr, txt){
                AjaxUtil.error( xhr );
            },
            success: function (data) {
                if(data.result){
                        var demoAccountList = data.demoAccountList;
                        $.each(demoAccountList,function (index, item) {
                            if(item.flag != '1'){
                                $("#testAccount").append('<option value="'+ item.email +'">'+ item.email +'</option>');
                            }else{
                                $("#testSendAccount").append('<option value="'+ item.email +'">'+ item.email +'</option>');
                            }

                        })
                }
            }
        });
    },
    /** 테스트 레이어에서 입력한 테스트계정을 추가**/
    demoAccountInsert:function () {
        var url ="/send/demoaccount/add.json";
        var email = $("#accountInsertText").val();
        var flag = '0';

        if(email == ''){
            alert(message_sendManage.S0009);
            return;
        }
        if(!ImStringUtil.validateEmail(email)){
            alert(message_sendManage.S0008);
            $("#accountInsertText").focus();
            return;
        }
        var param = {
            "email":email,
            "flag":flag
        }
        $.ajax( {
            url : url,
            data : param,
            type : "POST",
            dataType : "json",
            async : false,
            success : function(data) {
                if(data.result){
                    alert(message_sendManage.S0007);
                    $("#testAccount").append('<option value="'+ email +'">'+ email +'</option>');
                }else{
                    alert(data.message);
                    $("#accountInsertText").val("");
                }
            },
            error:function(xhr){
                AjaxUtil.error(xhr);
            }
        });
    }

    /** test send end **/

};


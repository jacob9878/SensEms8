function removecontent() {
    console.log("removecontent");
    var field = document.getElementById("field");
    var condition = document.getElementById("condition");
    var query = $("#query").val();
    for (var i = 0; i < field.children.length; i++) {
        if (field.children.length >= 1 && field.firstChild) {
            field.removeChild(field.lastChild);
            i--
        }
    }
    for (var j=0; j<condition.children.length; i++){
        if(condition.children.length >=1 && condition.firstChild){
            condition.removeChild(condition.lastChild);
            i--
        }
    }

    if(query != '' || query != null){
        $("#query").val('');
    }

// }
}
var receiverList = {

    doList : function(cpage) {
        receiverEvent.getAddrGroupList(cpage, $("#groupKey").val(), 'N', $("#groupName").val());
    },
    doList2 : function(cpage) {
        receiverEvent.getReceiverGroupListDraw(cpage);
    },
    /**
     * 페이지 검색
     */
    search : function () {
        $("#cpage").val('1');
        var chk_keyword = $("#srch_keyword").val();

        if(chk_keyword == "") {
            alert("검색어를 입력해주세요.");

        }else {
            $("#ReceiverGroupListForm").submit();
        }

    },
    /**
     * 페이지 목록 선택
     * @param para
     */
    list:function(para){
        $("#cpage").val(para);
        $("#ReceiverGroupListForm").submit();

    },
    /**
     * 전체 목록으로 돌아간다.
     */
    viewAll:function(){
        var bview = document.getElementById("allview");
        bview.style.display = "none";
        $("#srch_keyword").val('');
        $("#cpage").val('1');
        $("#ReceiverGroupListForm").submit();
    },
    /**
     * 추가 페이지로 이동
     */
    add:function(){
        location.href="add.do?cpage="+$("#cpage").val()+"&srch_key="+$("#srch_key").val()+"&srch_type="+$("#srch_type").val();
    },
    /**
     * 수정 페이지로 이동
     */
    edit:function(ukey,srch_key,srch_type){
        location.href="edit.do?ukey=" + ukey+"&cpage="+$("#cpage").val()+"&srch_key="+srch_key+"&srch_type="+srch_type;
    },
    /**
     * 쿼리 실행하여 수신그룹 미리 보기
     * @param ukey
     */
    previewGroup: function (ukey) {
        window.open("/receiver/group/preview.do?ukey="+ukey,"_blank","toolbar=no,width=600,height=500,directories=no,status=no,scrollbars=yes,resizable=yes,menubar=no")
    },
    /**
     * 목록으로 이동
     */
    receiverList: function () {
        location.href="list.do?cpage="+$("#cpage").val()+"&srch_keyword="+$("#srch_key").val()+"&srch_type="+$("#srch_type").val();
    },
    /**
     * 필드추가, 조건추가 시 모달 레이어 보이기.
     * type - field : 필드추가 , condition : 조건추가
     * @param type
     */
    openPopup: function (type) {
        if(!$("#dbkey").val()){
            alert(message_group.G0002);
            return;
        }
        //팝업 데이터 초기화
        if(type == 'field'){
            $("#chooseTable").html('<option value="">'+message_group.G0006+'</option>');
            $("#chooseField").html('<option value="">'+message_group.G0006+'</option>');
        }else if (type == 'condition'){
            $("#chooseTableConditionA").html('<option value="">'+message_group.G0006+'</option>');
            $("#chooseFieldConditionA").html('<option value="">'+message_group.G0006+'</option>');
            $("#chooseTableConditionB").html('<option value="">'+message_group.G0006+'</option>');
            $("#chooseFieldConditionB").html('<option value="">'+message_group.G0006+'</option>');
            $("#compval1").val("");
            $("#compval2").val("");
            $("#conQuery").val("");
            $("#sel_opt").val("0").prop("selected",true);
            $("#sel_same_opt").val("").prop("selected",true);
            $("#sel_another_opt").val("").prop("selected",true);
            $("#another_table").hide();
            $("#same_table").show();


            var c = document.getElementById("condition");
            if(c.length > 0){
                $("#another_multiConCheck").attr("disabled",true);
                $("#same_multiConCheck").attr("disabled",true);
                $("input:radio[name='same_join']:radio[value='AND']").prop('checked', true);
                $("input:radio[name='another_join']:radio[value='AND']").prop('checked', true);
            }else {
                $("#another_multiConCheck").removeAttr("disabled");
                $("#same_multiConCheck").removeAttr("disabled");
                $("input:radio[name='same_join']:radio[value='']").prop('checked', true);
                $("input:radio[name='another_join']:radio[value='']").prop('checked', true);
            }

        }


        var url = "/receiver/group/getTables.json";
        var param = {
            "dbkey" : $("#dbkey").val()
        }
        $.ajax({
            url: url,
            type: "post",
            data: param,
            dataType: "json",
            async: false,
            error:function(xhr, txt){
                AjaxUtil.error( xhr );
            },
            success: function (data) {
                if(data.result){
                    var tableList = data.tableList;
                    $.each(tableList,function (index, item) {
                        if(type == 'field'){
                            $("#chooseTable").append('<option value="'+ item.tableName +'">'+ item.tableName +'</option>');
                            $("#addFieldPopup").show();
                        }else if(type == 'condition'){
                            $("#chooseTableConditionA").append('<option value="'+ item.tableName +'">'+ item.tableName +'</option>');
                            $("#chooseTableConditionB").append('<option value="'+ item.tableName +'">'+ item.tableName +'</option>');
                            $("#addConditionPopup").show();
                        }
                    })
                }else {
                    if(type == 'field'){
                        $("#chooseTable").html('<option value="">'+message_group.G0006+'</option>');
                    }else if(type == 'condition'){
                        $("#chooseTableConditionA").html('<option value="">'+message_group.G0006+'</option>');
                        $("#chooseTableConditionB").html('<option value="">'+message_group.G0006+'</option>');
                    }
                    alert(data.message);
                }
            }
        });
    },
    /**
     * 필드 선택 목록에서 선택한 데이터 삭제 처리
     */
    deleteField: function () {
        var field = document.getElementById("field");
        var i;
        var flag = false;
        for(i=0; i < field.length; i++){
            if(field.options[i].selected == true){
                flag = true;
                break;
            }
        }
        if(!flag){
            alert(message_group.G0010);
        }
        field.remove(i);
    },
    /**
     * 조건 선택 목록에서 선택한 데이터 삭제 처리
     */
    deleteCondition: function () {
        var condition = document.getElementById("condition");
        var i;
        var flag = false;
        for(i=0; i < condition.length; i++){
            if(condition.options[i].selected == true){
                flag = true;
                break;
            }
        }
        if(!flag){
            alert(message_group.G0011);
        }
        condition.remove(i);
    },
    /**
     * 선택된 필드와 조건에 따라 쿼리 생성
     */
    makeQuery : function () {
        var field = document.getElementById("field");
        if(field.length < 1){
            alert(message_group.G0019);
            return;
        }
        //이전에 만들어진 테이블 목록 비우기
        $("#selectedTables").html("");
        //필드와 조건에서 선택된 테이블 목록 생성
        doMakeSelTabs();

        var strSelect = doMakeSelect();
        var strFrom = doMakeFrom();
        var strWhere = doMakeWhere();

        if(strWhere.length > 0){
            strWhere = " WHERE " + strWhere;
        }

        var strQuery = "SELECT " + strSelect + " FROM " + strFrom + strWhere;
        $("#query").val(strQuery);
    },
    /**
     * 추가 창에서 생성쿼리 결과 보기
     */
    previewQuery : function () {
        var dbkey = $("#dbkey").val();
        var query = $("#query").val();
        $("#chk_result").val("1");
        //유효성 체크
        if(!dbkey){
            alert(message_group.G0002);
            return;
        }
        if(!query){
            alert(message_group.G0003);
            return;
        }

        var url = "/receiver/group/previewQuery.do";
        $('#ReceiverGroupForm').attr("action", url);
        $('#ReceiverGroupForm').attr("method", "post");
        window.open("", "previewQuery", "toolbar=no,width=900,height=500,directories=no,status=no,scrollbars=yes,resizable=yes,menubar=no");
        $('#ReceiverGroupForm').attr("target", "previewQuery");

        $('#ReceiverGroupForm').submit();
    },
    /**
     * 필드 추가창 닫기
     */
    closeFieldPopup: function () {
        $("#addFieldPopup").hide();
    },
    /**
     * 조건 추가창 닫기
     */
    closeConditionPopup: function () {
        $("#addConditionPopup").hide();
    },
    /**
     * 필드 추가 창 선택 후 확인 처리
     * @param column
     */
    fieldSelect : function() {
        var tableName = $("#chooseTable").val();
        var column = $("#chooseField").val();

        if(!tableName){
            alert(message_group.G0008);
            return;
        }
        if(!column){
            alert(message_group.G0009);
            return;
        }
        for(var i=0; i<column.length;i++){
            var option = $("<option value='" + column[i] +"'>"+ tableName + "." + column[i]+"</option>");
            $("#field").append(option);
        }
        $("#addFieldPopup").hide();
    },
    /**
     * 테이블 변경 시 필드 목록 획득
     * type - '': 필드 추가창 , 'ConditionA' : 조건 선택창의 comval1 , 'ConditionB' : 조건 선택창의 comval2
     * @param tableName, type
     */
    getFields: function (tableName,type) {
        if(!tableName){
            $("#chooseField"+type).html('<option value="">'+message_group.G0006+'</option>');
            return;
        }

        if(!$("#dbkey").val()){
            alert(message_group.G0007);
        }

        var url = "/receiver/group/getFieldsByTableName.json";
        var param = {
            "dbkey" : $("#dbkey").val(),
            "tableName" : tableName
        }
        $.ajax({
            url: url,
            type: "post",
            data: param,
            dataType: "json",
            async: false,
            error:function(xhr, txt){
                AjaxUtil.error( xhr );
            },
            success: function (data) {
                if(data.result){
                    $("#chooseField"+type).empty();
                    var columnList = data.columnList;
                    $.each(columnList,function (index, item) {
                        $("#chooseField"+type).append('<option value="'+ item.columnName +'">'+ item.columnName +'</option>');
                    })
                }else {
                    $("#chooseField"+type).html('<option value="">'+message_group.G0006+'</option>');
                    alert(data.message);
                }

            }
        });

    },
    /**
     * 조건 추가 창, 선택 필드 값 변경 동작 - 같은 테이블에서 비교, 다른테이블에서 비교
     * @param opt
     */
    selOptionChange: function (opt) {
        if(opt=='1'){
            $("#another_table").show();
            $("#same_table").hide();
        }else{
            $("#another_table").hide();
            $("#same_table").show();
        }
    },
    /**
     * 같은 테이블에서 비교 시, [BETWEEN, IN] 연산자에 대해서만 비교값2(comval2) input 태그 활성화
     * @param same_option
     */
    selSameOptionChange : function (same_option) {
        if(same_option == "9" || same_option == "10"){
            $("#compval2").removeAttr("disabled");
        }else {
            $("#compval2").attr("disabled","disabled");
        }
    },
    /**
     * 조건 추가 창에서 적용 클릭 시, 조건 쿼리 생성
     */
    doMakeConQuery : function () {
        var tableA = $("#chooseTableConditionA").val();
        var fieldA = $("#chooseFieldConditionA").val();
        var sel_opt = $("#sel_opt").val();

        if(!tableA) {
            alert(message_group.G0008);
            return;
        }
        if(!fieldA){
            alert(message_group.G0009);
            return;
        }

        if(sel_opt=='0'){ // 같은 테이블에서 비교 선택 시 동작
            var compval1 = $("#compval1").val();
            var sel_same_opt = $("#sel_same_opt").val();
            var compval2 = $("#compval2").val();

            if(!compval1){
                alert(message_group.G0012);
                return;
            }
            if(!sel_same_opt) {
                alert(message_group.G0013);
                return;
            }else if(sel_same_opt == '9' || sel_same_opt == '10'){
                if(!compval2){
                    alert(message_group.G0014);
                    return;
                }
            }

            //쿼리 만들기 시작
            var conQuery; // text값
            var hiddenQuery; // value 값

            if(sel_same_opt == '0'){
                conQuery = tableA + "." + fieldA + "='" + compval1 + "'";
                hiddenQuery = fieldA + "='" + compval1 + "'";
            }else if(sel_same_opt == '1'){
                conQuery = tableA + "." + fieldA + "!='" + compval1 + "'";
                hiddenQuery = fieldA + "!='" + compval1 + "'";
            }else if(sel_same_opt == '2'){
                conQuery = tableA + "." + fieldA + ">'" + compval1 + "'";
                hiddenQuery = fieldA + ">'" + compval1 + "'";
            }else if(sel_same_opt == '3'){
                conQuery = tableA + "." + fieldA + "<'" + compval1 + "'";
                hiddenQuery = fieldA + "<'" + compval1 + "'";
            }else if(sel_same_opt == '4'){
                conQuery = tableA + "." + fieldA + ">='" + compval1 + "'";
                hiddenQuery = fieldA + ">='" + compval1 + "'";
            }else if(sel_same_opt == '5'){
                conQuery = tableA + "." + fieldA + "<='" + compval1 + "'";
                hiddenQuery = fieldA + "<='" + compval1 + "'";
            }else if(sel_same_opt == '6'){
                conQuery = tableA + "." + fieldA + " LIKE '%" + compval1 + "'";
                hiddenQuery = fieldA + " LIKE '%" + compval1 + "'";
            }else if(sel_same_opt == '7'){
                conQuery = tableA + "." + fieldA + " LIKE '%" + compval1 + "%'";
                hiddenQuery = fieldA + " LIKE '%" + compval1 + "%'";
            }else if(sel_same_opt == '8'){
                conQuery = tableA + "." + fieldA + " LIKE '" + compval1 + "%'";
                hiddenQuery = fieldA + " LIKE '" + compval1 + "%'";
            }else if(sel_same_opt == '9'){
                conQuery = tableA + "." + fieldA + " BETWEEN " + compval1 + " AND " + compval2;
                hiddenQuery = fieldA + " BETWEEN " + compval1 + " AND " + compval2;
            }else if(sel_same_opt == '10'){
                conQuery = tableA + "." + fieldA + " IN ('" + compval1 + "', '" + compval2 + "')";
                hiddenQuery = fieldA + " IN ('" + compval1 + "', '" + compval2 + "')";
            }
        }else { //다른 테이블에서 비교 선택 시 동작
            var tableB = $("#chooseTableConditionB").val();
            var fieldB = $("#chooseFieldConditionB").val();
            var sel_another_opt = $("#sel_another_opt").val();

            if(!tableB) {
                alert(message_group.G0015);
                return;
            }
            if(!fieldB){
                alert(message_group.G0016)
                return;
            }
            if(!sel_another_opt){
                alert(message_group.G0017);
                return;
            }

            //쿼리 만들기 시작
            var conQuery;

            if(sel_another_opt == '0'){
                conQuery = tableA + "." + fieldA + "=" + tableB + "." + fieldB;
            }else if(sel_another_opt == '1') {
                conQuery = tableA + "." + fieldA + "!=" + tableB + "." + fieldB;
            }
        }
        $("#conQuery").val(conQuery);
    },
    /**
     * 조건 선택 후 조건 선택창에 삽입
     */
    conSubmit : function () {
        var conQuery = $("#conQuery").val();
        var sel_opt = $("#sel_opt").val();
        var join;
        var option;
        if(sel_opt=='0'){
            join = $('input:radio[name="same_join"]:checked').val();
            option = "one";
        }else {
            join = $('input:radio[name="another_join"]:checked').val();
            option = "two";
        }

        if(!conQuery){
            alert(message_group.G0018);
            return;
        }
        var option = $('<option value="' + conQuery + ';'+ option + ';' + join +'">'+ conQuery +'</option>');
        $("#condition").append(option);

        $("#addConditionPopup").hide();
    }

}
var receiverSave = {
    /**
     * 수신 그룹 추가/수정 실행
     */

    save : function (type) {
        var regExp = /[\{\}\[\]\/?;:|\)*~,`!^+<>@\#$%&\\\=\(\'\"]/gi;

        if(!$("#recv_name").val()){
            alert(message_group.G0001);
            return;
        }else {
            if(regExp.test($("#recv_name").val())){
                alert(message_group.G0020);
                return;
            }
        }
        if(!$("#dbkey").val()){
            alert(message_group.G0002);
            return;
        }
        if(!$("#query").val()){
            alert(message_group.G0003);
            return;
        }

        if ($("#chk_result").val() != "1") {
            alert("결과보기를 진행 후 추가해주세요.");
            return;
        }

        var url = "/receiver/group/" + type + ".do";
        var ReceiverGroupForm = $("#ReceiverGroupForm").serialize();
        // ajax 처리를 위해 주석
        /*$('#ReceiverGroupForm').attr("action", url);
        $('#ReceiverGroupForm').attr("method", "post");
        $("#ReceiverGroupForm").submit();*/

        $.ajax({
            url : url,
            type : 'POST',
            cache : false ,
            data : ReceiverGroupForm,
            dataType: "json",
            async: true,
            error:function(xhr){
                AjaxUtil.error(xhr);
            },
            success:function (result) {
                if(result.code==JSONResult.FAIL){
                    alert(result.message);
                    return;
                }else {
                    location.href = "/receiver/group/list.do";
                }

            }
        });
    }
}


var receiverDel = {

    delete : function () {
        var ukeys = this.checkedList();

        if (ukeys == 0) {
            alert(message_group.G0051);
            return;
        }
        var param = {
            "ukeys": ukeys
        }
        var url ="/receiver/group/deleteReceiverGroups.json";


        if (confirm(message_group.G0005)) {
            $.ajax({
                url: url,
                type: "post",
                data: param,
                dataType: "json",
                async: false,
                error:function(xhr, txt){
                    AjaxUtil.error( xhr );
                },
                success: function (result) {
                    if(result.code==JSONResult.FAIL){
                        alert(result.message);
                    }else{
                        alert(result.message);
                        location.reload();
                    }
                }
            });
        }
    },
    checkedList: function () {
        var ukeys = new Array();
        $("input[name='ukeys']:checkbox:checked").each(function () {
            ukeys.push($(this).val());
        });
        return ukeys;
    }
}


var receiverEvent = {

    getReceiverList : function (type) {
        if(!type){
            alert(message_common.CM0018);
            return;
        }
        // if(type=='0'){
        //     $("#recid").val("");
        //     $("#recname").val("");
        // }
        $("#grouplist").empty();
        receiverEvent.getReceiverGroupListDraw();
        $("#groupListLayer").show();

        //수신자 그룹 목록을 불러온다.
        // window.open('/receiver/group/popupReceiver.do?type='+type,'receiverSelect','toolbar=no,width=1050,height=900,top=0,left=0;directories=no,status=no,scrollbars=yes,resizable=yes,menubar=no');

    },

    getAddrGroupList : function () {
        $("#recid").val("");
        //$("#recname").val("");
        $("#addresslist").empty();
        //주소록 그룹 목록을 불러온다.
        // window.open('/receiver/group/popupAddr.do','receiverSelect','toolbar=no,width=1050,height=900,top=0,left=0;directories=no,status=no,scrollbars=yes,resizable=yes,menubar=no');
        receiverEvent.getAddressGroupListDraw();
        $("#addressListLayer").show();

    },

    /**
     * 리시버 리스트에서 수신자를 선택한다.
     * 선택한 라디오버튼이나 이름 클릭시 창을 닫으면서
     * 수신자 그룹 명을 부모창 receiverGroupText에 넣는다.
     * 부모창 recid에 ukey를 넣는다.
     */
    receiverGroupSel : function (ukey, groupName, opener_type) {
        try{
            var key;
            if(opener_type =='1'){
                key = String(ukey);
                document.reserveSendForm.recid.value = key; // 수신 키값 입력
                document.reserveSendForm.receiverGroupText.value = groupName; // 이름값 입력
                getFieldListAndDraw(key,opener_type);
            } else{
                key = String(ukey);
                document.MailWriteForm.recid.value = key; // 수신 키값 입력
                document.MailWriteForm.recname.value = groupName; // 이름값 입력
                getFieldListAndDraw(key,opener_type);
            }

        } catch(e){
            log.error("receiverGroupSel error");
        }
        $("#groupListLayer").hide();
        //window.close();

        //TODO 필드 삽입 기능 구현 필요.

    },
    /**
     * 리시버 리스트에서 라디오 버튼을 선택한 후, 확인을 누른경우 사용하는 이벤트
     * 수신자 그룹 명을 부모창 receiverGroupText에 넣는다.
     * 부모창 recid에 ukey를 넣는다.
     */
    receiverGroupConfirm : function (opener_type) {
        // 확인을 눌렀을때 선택된 라디오 버튼이 있어야한다.
        var checked = $("input[name='ukey']:checked").val();

        if(!checked){
            alert(message_sendManage.S0028); // 수신그룹을 선택해주시기 바랍니다.
            return;
        }

        var check_count = document.getElementsByName("ukey").length;

        for (var i=0; i < check_count; i++) {
            if (document.getElementsByName("ukey")[i].checked == true) {
                var ukey = document.getElementsByName("ukey")[i].value;
                var groupName = document.getElementsByName("ukey")[i].getAttribute("gname_value");
            }
        }

        try{ 	// 전체발송수 미리 입력...
            var key; // 형변환 용 key 변수 선언, 부모창으로 데이터를 넣을때 실수형으로 주입됨
            if( opener_type == '1'){
                key = String(ukey);
                document.reserveSendForm.recid.value = key; // 수신 키값 입력
                document.reserveSendForm.receiverGroupText.value = groupName; // 이름값 입력
                getFieldListAndDraw(key,opener_type);
            } else{
                key = String(ukey);
                document.MailWriteForm.recid.value = key; // 수신 키값 입력
                document.MailWriteForm.recname.value = groupName; // 이름값 입력
                getFieldListAndDraw(key,opener_type);
            }
        } catch(e){
            log.error("receiverGroupConfirm error");
        }

        //TODO 필드 삽입 기능 구현 필요.
        $("#groupListLayer").hide();
        // $("#grouplist").empty();
        // window.close();

    },

    selAddrGroupList : function () {
        var count = document.getElementsByName("gkeys").length;
        var selCount = 0;
        var gkeys = "";
        var gnames = "";
        var opener = window.dialogArguments;
        $("input:checkbox[id='all_check']").prop("checked", false);
        for (var i=0; i < count; i++) {
            if (document.getElementsByName("gkeys")[i].checked == true) {
                if(selCount > 0){
                    gkeys += ",";
                    gnames += ",";
                }
                selCount ++;
                gkeys += document.getElementsByName("gkeys")[i].value;
                gnames += document.getElementsByName("gkeys")[i].getAttribute("gname_value");
            }
        }

        if (selCount <= 0) {
            alert(message_mail.M0011);
            return;
        }


        // window.opener.document.MailWriteForm.recid.value = gkeys; // 수신 키값 입력
        // window.opener.document.MailWriteForm.recname.value = gnames; // 이름값 입력
        // window.opener.getAddrFieldListDraw();


        document.MailWriteForm.recid.value = gkeys; // 수신 키값 입력
        document.MailWriteForm.recname.value = gnames; // 이름값 입력
        getAddrFieldListDraw();


//		window.opener.getFieldListAndDraw(key,opener_type); TODO : 주소록 필드삽입 구현 필요

        $("#addressListLayer").hide();
        //  $("#addresslist").empty();



        //window.close();
    },

    /**
     * 창 닫기
     */
    close : function () {
        // window.close();
        $("#addresslist").empty();
        $("#addressListLayer").hide();

        $("#grouplist").empty();
        $("#groupListLayer").hide();


    },

    /**
     * 미리보기
     * 선택된 수신자 그룹의 ukey 값을 기준으로 수신자들의 정보를 구한다.
     */
    preview:function(ukey){

        var checked = $("input[name='ukey']:checked").val();

        if(!checked){
            alert(message_sendManage.S0028); // 수신그룹을 선택해주시기 바랍니다.
            return;
        }

        var check_count = document.getElementsByName("ukey").length;

        for (var i=0; i<check_count; i++) {
            if (document.getElementsByName("ukey")[i].checked == true) {
                var ukey = document.getElementsByName("ukey")[i].value;
            }
        }
        window.open('/receiver/group/preview.do?ukey='+ukey,'_blank','toolbar=no,width=1050,height=900,top=0,left=0;directories=no,status=no,scrollbars=yes,resizable=yes,menubar=no');
    },

    getAddressGroupListDraw : function () {
        var url = "/receiver/group/addresslist.do";

        $.ajax({
            url: url,
            type: "get",
            dataType: "json",
            async: false,
            error:function(xhr, txt){
                alert("에러가 발생했습니다!");
                AjaxUtil.error( xhr );
            },
            success: function (data) {
                if(data.result){
                    var groupList = data.groupList;
                    $.each(groupList,function (index, item) {
                        $("#addresslist").append('<tr>' +'<td class="check_ico">'  +'<input type="checkbox" name= "gkeys" id =' + item.gkey +' value=' + item.gkey +' +  gname_value=' + item.gname + '>'  + '</td>'  + '<td>' + '<label for=' + item.gkey + '>' + item.gname + '</label>'  +'</td>' +  '<td>' + item.count + '</td>'  + '</tr>');
                    })
                }
            }
        });
    },


    getAddrGroupListDraw : function () {
        var url = "/receiver/group/grouplist.do";

        $.ajax({
            url: url,
            type: "get",
            dataType: "json",
            async: false,
            error:function(xhr, txt){
                alert("에러가 발생했습니다!");
                AjaxUtil.error( xhr );
            },
            success: function (data) {
                if(data.result){
                    var receiverGroupList = data.receiverGroupList;
                    var opener_type = data.type;
                    var pageInfo = data.pageInfo;
                    var totalsize = data.totalsize;
                    $.each(receiverGroupList,function (index, item) {
                        // $("#grouplist").append('<tr>' +'<td>' + '<input type="radio" name= "ukey" id="' + item.ukey + '" value="' + item.ukey + '" gname_value="' + item.recv_name + '">' + '</td>' +
                        //     '<td class="over_text" >' + '<a href="#" onclick="receiverEvent.receiverGroupSel(' +item.ukey +','+ item.recv_name +','+ opener_type +');">' + item.recv_name + '</a>' + '</td>' +
                        //     '<td>' + '<a href="#">' +item.userid+ '</a>' + '</td>' + '</tr>');

                        $("#grouplist").append('<tr>' +'<td>' + '<input type="radio" name= "ukey" id="' + item.ukey + '" value="' + item.ukey + '" gname_value="' + item.recv_name + '">' + '</td>' +
                            '<td class="over_text" >' + '<label for=' + item.recv_name + '</a>' + '</label>' + '</td>' +
                            '<td>' + '<a href="javascript:;">' +item.userid+ '</a>' + '</td>' + '</tr>');

                    })

                    // $("#page_nav").append('<pt:page><pt:cpage>'+pageInfo.cpage+'</pt:cpage>'+'<pt:pageSize>'+pageInfo.pageSize+'</pt:pageSize>'+'<pt:total>'+pageInfo.total +'</pt:total>'+'<pt:jslink>'+ 'receiverList.list' +'</pt:jslink>'+'</pt:page>');

                }

            }
        });
    },

    getReceiverGroupListDraw : function (cpage) {
        var url = "/receiver/group/grouplist.do";
        var param = {
            "cpage": cpage
        };
        $.ajax({
            url: url,
            type: "get",
            data: param,
            dataType: "json",
            async: false,
            error:function(xhr, txt){
                alert("에러가 발생했습니다!");
                AjaxUtil.error( xhr );
            },
            success: function (data) {
                if(data.result){
                    $("#grouplist").empty();
                    var receiverGroupList = data.receiverGroupList;
                    var opener_type = data.type;
                    var page = data.pageInfo;
                    if(data.receiverGroupList != null) {
                        var pageHtml = pageInfo(page.cpage, page.pageSize, page.total, '', 'receiverList.doList2(');
                    }else {
                         pageHtml = pageInfo(1, 1, 1, '', 'receiverList.doList2(');
                    }

                    $("#pageInfo_group").html(pageHtml);
                    $.each(receiverGroupList,function (index, item) {
                        // $("#grouplist").append('<tr>' +'<td>' + '<input type="radio" name= "ukey" id="ukey" value="' + item.ukey + '" gname_value="' + item.recv_name + '">' + '</td>' +
                        //     '<td class="over_text" >' + '<a href="#" onclick="receiverEvent.receiverGroupSel(' +item.ukey +','+ item.recv_name +','+ '1' +');">' + item.recv_name + '</a>' + '</td>' +
                        //     '<td>' + '<a href="#">' +item.userid+ '</a>' + '</td>' + '</tr>');
                        $("#grouplist").append('<tr>' +'<td>' + '<input type="radio" name= "ukey" id="' + item.ukey + '" value="' + item.ukey + '" gname_value="' + item.recv_name + '">' + '</td>' +
                            '<td class="over_text" >' + '<label for='+ item.ukey + '>' + item.recv_name + '</a>' + '</label>' + '</td>' +
                            '<td>' + '<a href="javascript:;">' +item.userid+ '</a>' + '</td>' + '</tr>');

                    })

                    //$("#page_nav").append('<pt:page>' + '<pt:cpage>' + pageInfo.cpage +'</pt:cpage>'+'<pt:pageSize>'+ pageInfo.pageSize +'</pt:pageSize>'+'<pt:total>'+ pageInfo.total + '</pt:total>' +'<pt:jslink>' + receiverList.list + '</pt:jslink>' + '</pt:page>');

                }

            }
        });
    },







}



/**
 * 필드,조건에서 선택한 테이블 목록 생성
 */
function doMakeSelTabs(){
    var f = document.getElementById("field");
    var s = document.getElementById("selectedTables");
    var c = document.getElementById("condition");

    var nRet,nRet2;

    /**
     * 필드선택에서 선택된 테이블 목록 추출
     */
    for(var i=0; i<f.length;i++){
        var table = f.options[i].text.split(".")[0];

        if(i==0){
            var option = $("<option value='" + table +"'>"+ table +"</option>");
            $("#selectedTables").append(option);
        }else {
            nRet=0;
            //테이블 목록에 등록이 되어있다면 다음으로 넘어간다.
            for(var j=0;j<s.length;j++){
                if(s.options[j].text == table){
                    nRet=1;
                    break;
                }
            }
            // 등록된 테이블이 아니라면 등록한다.
            if(nRet != 1){
                var option = $("<option value='" + table +"'>"+ table +"</option>");
                $("#selectedTables").append(option);
            }
        }
    }

    /**
     * 조건 선택에서 선택된 테이블 목록 추출
     */
    for(var i=0; i<c.length;i++){
        var tableA = c.options[i].value.split(".")[0];
        var tableB = "";
        if(c.options[i].value.split(";")[1] == 'two'){
            tableB = (c.options[i].value.split("=")[1]).split(".")[0];
        }

        nRet = 0;
        nRet2 = 0;

        for(var j=0;j<s.length;j++){
            //비교할 테이블이 목록에 존재하면 플래그값 변경
            if(s.options[j].text == tableA){
                nRet=1;
                //break;
            }
            if(tableB != ""){
                if(s.options[j].text == tableB){
                    nRet2=1;
                    //break;
                }
            }
        }
        //등록된 테이블이 아니라면 등록한다.
        if(nRet == 0){
            var option = $("<option value='" + tableA +"'>"+ tableA +"</option>");
            $("#selectedTables").append(option);
        }
        if((nRet2!=1) && (tableB!="")){
            var option = $("<option value='" + tableB +"'>"+ tableB +"</option>");
            $("#selectedTables").append(option);
        }
    }

}

/**
 * select 쿼리문 생성
 * @returns {string}
 */
function doMakeSelect() {
    var f = document.getElementById("field");
    var s = document.getElementById("selectedTables");
    var strSelectedCols = "";
    var strMultiSelectedCols = "";

    for(var i=0; i < f.length; i++){
        if (i==0) {
            strSelectedCols = f.options[i].value;
            strMultiSelectedCols = f.options[i].text;
        } else {
            strSelectedCols = strSelectedCols + "," + f.options[i].value;
            strMultiSelectedCols = strMultiSelectedCols + "," + f.options[i].text;
        }
    }
    if(s.length == 1){
        return strSelectedCols;
    }else {
        return strMultiSelectedCols;
    }
}

/**
 * from 쿼리문 생성
 * @returns {string}
 */
function doMakeFrom() {
    var s = document.getElementById("selectedTables");
    var strFrom = "";

    if(s.length == 1){
        strFrom = s.options[0].value;
    }else {
        for(var i=0; i < s.length; i++){
            if(i==0){
                strFrom = s.options[i].value;
            }else {
                strFrom = strFrom + "," + s.options[i].value;
            }
        }
    }
    return strFrom;
}

/**
 * where 쿼리문 생성
 * @returns {string}
 */
function doMakeWhere() {
    var c = document.getElementById("condition");
    var s = document.getElementById("selectedTables");

    var strWhere = "";

    for(var i=0; i < c.length; i++){
        var strSelectedRows = c.options[i].value;
        if(i == 0){
            strWhere = (strSelectedRows.split(";"))[0];
        }else {
            strWhere = strWhere + " " + (strSelectedRows.split(";"))[2] + " " + (strSelectedRows.split(";"))[0];
        }
    }
    return strWhere;
}

/**
 * 주소록 선택시 필드 리스트
 * */
function getAddrFieldListDraw() {
    var item = ["email","name","company","dept","grade","office_tel","mobile","etc1","etc2"];
    var str = "<option value=\"\">"+message_group.G0021+"</option>";
    if(receiverFieldArray != '') receiverFieldArray = new Array;

    var i = 1;
    $.each(item,function (index,field) {
        str += "<option value=\"[#FIELD"+ i +"#]\">" + field + "</option>";
        receiverFieldArray.push({'key':'[#FIELD' + i + '#]','value':field});
        i++;
    })

    $(".send_fieldSelect").html(str);
}



/**
 * 수신그룹 선택 후, 필드 목록 획득
 * @param type
 */
function getFieldListAndDraw(ukey,type) {
    //ukey를 이용하여 필드 목록 획득
    var url = "/receiver/group/getFieldsByUkey.json";
    var param = {
        "ukey" : ukey,
    }
    $.ajax({
        url: url,
        type: "post",
        data: param,
        dataType: "json",
        async: false,
        error:function(xhr, txt){
            AjaxUtil.error( xhr );
        },
        success: function (data) {
            if(data.result){
                if(receiverFieldArray != '') receiverFieldArray = new Array;
                if(type == '1') {
                    $(".reserveFieldSelect").html('<option value="">'+message_group.G0021+'</option>');
                    var fieldList = data.fieldList;
                    var i=1;
                    $.each(fieldList,function (index, item) {
                        $(".reserveFieldSelect").append('<option value="[#FIELD'+ i +'#]">'+ item.fieldName +'</option>');
                        i++;
                    })
                }else {
                    $(".send_fieldSelect").html('<option value="">'+message_group.G0021+'</option>');
                    var fieldList = data.fieldList;
                    var i=1;
                    $.each(fieldList,function (index, item) {
                        $(".send_fieldSelect").append('<option value="[#FIELD'+ i +'#]">'+ item.fieldName +'</option>');
                        receiverFieldArray.push({'key':'[#FIELD' + i + '#]','value':item.fieldName});
                        i++;
                    })
                }

            }else {
                if(type == '1') {
                    $(".reserveFieldSelect").html('<option value="">'+message_group.G0021+'</option>');
                    alert(data.message);
                }else {
                    $(".send_fieldSelect").html('<option value="">'+message_group.G0021+'</option>');
                    alert(data.message);
                }


            }

        }
    });
}
var userList = {
    search:function(){
        $("#cpage").val('1');
        var chk_keyword = $("#srch_keyword").val();

        if(chk_keyword == "") {
            alert("검색어를 입력해주세요.");

        }else {
            $("#UserListForm").submit();
        }

    },
    list:function(para){
        $("#cpage").val(para);

        $("#UserListForm").submit();
        // $("#srch_keyword").clear();

    },
        userList:function(){
            location.href="list.do?cpage="+$("#cpage").val();

    },
    add:function(){
        location.href="add.do?cpage="+$("#cpage").val();
    },
    changeOption:function(){
      if($("#srch_keyword").val() == null){
        $("#srch_keyword").val("");
      }
        $("#cpage").val(1);
        $("#UserListForm").submit();
    },
    edit: function (userid) {

        location.href="edit.do?userid="+userid+"&cpage="+$("#cpage").val()+"&srch_keyword="+$("#srch_keyword").val()+"&srch_type="+$("#srch_type").val();
    },
    viewAll:function(){
        var bview = document.getElementById("allview");
        bview.style.display = "none";
        location.href = "list.do";
    },

}
var userAdd = {

    list:function(){
        location.href="list.do?cpage="+$("#cpage").val();
    },
    /**
     * 아이디 중복 확인
     */
    idCheck: function () {
        var userid = $.trim($("#userid").val());
        if (!userid) {
            alert(message_sysman.O0004);
            return;
        }
        var param = {
            "userid": userid
        };
        $.ajax({
            url: "/sysman/user/json/idCheck.json",
            type: "get",
            data: param,
            dataType: "json",
            async: false,
            error:function(xhr, txt){
                AjaxUtil.error( xhr );
            },
            success: function (result) {
                if (result.code == JSONResult.SUCCESS) {
                    alert($("#userid").val() + message_sysman.O0003);
                    $("#isCheck").val("1");
                } else {
                    alert(result.message);
                }
            }
        });
    },
    active:function(){
        var param = {
            "userid":  $("#userid").val(),
            "use_smtp": $("#use_smtp").val(),
        };
        var url =  "use.json";
        $.ajax({
            url : url,
            type : "get",
            data : param,
            dataType : "json",
            cache : false,
            async : false,
            success : function(result) {
                if (result.code == "1") {
                    var tagName = domain+"_use";
                    if(use_sign == "1"){
                        tagName = tagName + "_on";
                    }else{
                        tagName = tagName + "_off";
                    }

                    $('#'+tagName).prop('checked', true);

                }
                alert (result.resultMsg);
            },
            error : function(xhr, txt) {
                AjaxUtil.error( xhr );
            },
        });
    },
    add:function(){
        var userid = $.trim($("#userid").val());
        var passwd = $.trim($("#passwd").val());
        var passwd_confirm = $.trim($("#passwd_confirm").val());
        var name = $("#uname").val();
        var email = $.trim($("#email").val());
        var approve_email = $.trim($("#approve_email").val());
        var tel = $.trim($("#tel").val());
        var mobile = $.trim($("#mobile").val());
        var access_ip = $.trim($("#access_ip").val());

        if (!userid) {
            alert(message_sysman.O0004);
            $("#userid").focus();
            return;
        }else {
            if($("#isCheck").val() !='1' ){
                alert(message_sysman.O0006);
                $("#userid").focus();
                return;
            }
        }

        if(!passwd){
            alert(message_sysman.O0007);
            $("#passwd").focus();
            return;
        }else if(!passwd_confirm){
            alert(message_sysman.O0008);
            $("#passwd_confirm").focus();
            return;
        }else if(passwd != passwd_confirm) {
            alert(message_sysman.O0009);
            $("#passwd_confirm").focus();
            return;
        }

        if(!name){
            alert(message_sysman.O0010);
            $("#uname").focus();
            return;
        }

        if(!email){
            alert(message_sysman.O0015);
            $("#email").focus();
            return;
        }

        if(email){
            if(!ImStringUtil.validateEmail(email)){
                alert(message_sysman.O0013);
                $("#email").focus();
                return;
            }
        }
        if(approve_email){
            if(!ImStringUtil.validateEmail(approve_email)){
                alert(message_sysman.O0034);
                $("#approve_email").focus();
                return;
            }
        }

        var regExp = /^[0-9]+$/;;
        if(tel){
            if(tel.indexOf('-')>-1){
                tel = tel.replaceAll('-','');
            }

            if(!regExp.test(tel)){
                alert(message_sysman.O0035);
                $("#tel").focus();
                return;
            }
        }
        if(mobile){
            if(mobile.indexOf('-')>-1){
                mobile = mobile.replaceAll('-','');
            }

            if(!regExp.test(mobile)){
                alert(message_sysman.O0036);
                $("#mobile").focus();
                return;
            }
        }

        if(access_ip){

            if(access_ip.indexOf(",") > -1){
                var ips = access_ip.split(",");
                for(var i=0; i < ips.length ; i++){
                    if(!ImStringUtil.validateIPaddressCidr(ips[i].trim())){
                        alert(message_sysman.O0014);
                        $("#access_ip").focus();
                        return;
                    }
                }
            }else {
                access_ip = access_ip.trim();
                if(!ImStringUtil.validateIPaddressCidr(access_ip)){
                    alert(message_sysman.O0014);
                    $("#access_ip").focus();
                    return;
                }
            }

        }
        encrypt.enCryptInit();
        encryptData(); // 사용자 데이터 암호화


        $("#UserForm").submit();


    },

}
var userEdit = {
    list:function(){
        location.href="list.do?cpage="+$("#cpage").val()+"&srch_type="+$("#srch_type").val()+"&srch_keyword="+$("#srch_keyword").val();
    },
    edit: function () {
        var name = $("#uname").val();
        var email = $.trim($("#email").val());
        var approve_email = $.trim($("#approve_email").val());
        var access_ip = $.trim($("#access_ip").val());
        var tel = $.trim($("#access_ip").val());
        var mobile = $.trim($("#access_ip").val());
        var use_smtp = $("#use_smtp").val();

        if(!name){
            alert(message_sysman.O0010);
            $("#uname").focus();
            return;
        }
        if(!email){
            alert(message_sysman.O0015);
            $("#email").focus();
            return;
        }

        if(email){
            if(!ImStringUtil.validateEmail(email)){
                alert(message_sysman.O0013);
                $("#email").focus();
                return;
            }
        }
        if(approve_email){
            if(!ImStringUtil.validateEmail(approve_email)){
                alert(message_sysman.O0034);
                $("#approve_email").focus();
                return;
            }
        }

        var regExp = /[0-9]/gi;
        if(tel){
            if(tel.indexOf('-')>-1){
                tel = tel.replaceAll('-','');
            }

            if(!regExp.test(tel)){
                alert(message_sysman.O0035);
                $("#tel").focus();
                return;
            }
        }
        if(mobile){
            if(mobile.indexOf('-')>-1){
                mobile = mobile.replaceAll('-','');
            }

            if(!regExp.test(mobile)){
                alert(message_sysman.O0036);
                $("#mobile").focus();
                return;
            }
        }

        if(access_ip){

            if(access_ip.indexOf(",") > -1){
                var ips = access_ip.split(",");
                for(var i=0; i < ips.length ; i++){
                    if(!ImStringUtil.validateIPaddressCidr(ips[i].trim())){
                        alert(message_sysman.O0014);
                        $("#access_ip").focus();
                        return;
                    }
                }
            }else {
                access_ip = access_ip.trim();
                if(!ImStringUtil.validateIPaddressCidr(access_ip)){
                    alert(message_sysman.O0014);
                    $("#access_ip").focus();
                    return;
                }
            }

        }
        encrypt.enCryptInit();
        encryptData(); // 사용자 데이터 암호화
        $("#UserForm").submit();
        window.close();
    },
    changePwdPopup : function () {
        $("#changePwdPopup").show();
    },
    close : function () {
        $("#passwd").val("");
        $("#passwd_confirm").val("");
        $("#changePwdPopup").hide();
    },
    changePassword : function () {
        var userid = $("#userid").val();
        var passwd = $.trim($("#passwd").val());
        var passwd_confirm = $.trim($("#passwd_confirm").val());

        if(!passwd){
            alert(message_sysman.O0007);
            $("#passwd").focus();
            return;
        }else if(!passwd_confirm){
            alert(message_sysman.O0008);
            $("#passwd_confirm").focus();
            return;
        }else if(passwd != passwd_confirm) {
            alert(message_sysman.O0009);
            $("#passwd_confirm").focus();
            return;
        }

        encrypt.enCryptInit();
        var enpass = encrypt.enCrypt($("#passwd").val());
        var enpass_con = encrypt.enCrypt($("#passwd_confirm").val());
        $("#passwd").val(enpass);
        $("#passwd_confirm").val(enpass_con);
        $("#encAESKey").val(encryptKey);

        var param = {
            "userid" : userid,
            "passwd" : $("#passwd").val(),
            "passwd_confirm":$("#passwd_confirm").val(),
            "encAESKey":$("#encAESKey").val()
        }

        var url = "/sysman/user/changePwd.json";

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
                if (result.code == JSONResult.SUCCESS) {
                    alert(result.message);
                    $("#passwd").val("");
                    $("#passwd_confirm").val("");
                    $("#encAESKey").val("");
                    $("#changePwdPopup").hide();
                } else {
                    alert(result.message);
                    $("#passwd").val("");
                    $("#passwd_confirm").val("");
                    $("#encAESKey").val("");
                }
            }
        });

    }
}

var userDel = {
    delete : function () {
        var userList = this.checkedList();

        if (userList == 0) {
            alert(message_sysman.O0043);
            return;
        }
        var param = {
            "userid": userList
        }
        var url ="/sysman/user/deleteUsers.json";


        if (confirm(message_sysman.O0017)) {
            $.ajax({
                url: url,
                type: "post",
                traditional:true,
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
        var userList = new Array();
        $("input[name='userIds']:checkbox:checked").each(function () {
            userList.push($(this).val());
        });
        return userList;
    }
}

var receiverGroup ={

    /**
     * 페이지 목록 선택
     * @param para
     */
    list:function(para){
        $("#cpage").val(para);
        $("#ReceiverGroupListForm").submit();
    },
}

function encryptData() {
    var pwEncrypt = encrypt.enCrypt($("#passwd").val()); // 패스워드 암호화
    var pwEncrypt_new = encrypt.enCrypt($("#passwd_confirm").val()); // 패스워드 확인 암호화
    var emEncrypt = encrypt.enCrypt($("#email").val()); // 이메일 암호화
    $("#passwd").val(pwEncrypt);
    $("#passwd_confirm").val(pwEncrypt_new);
    $("#email").val(emEncrypt);
    var mobile = $.trim($("#mobile").val());
    var tel = $.trim($("#tel").val());
    var approve_email = $.trim($("#approve_email").val());

    if(isNotEmpty(mobile)){ // 모바일이 null이 아니면
        var moEncrypt = encrypt.enCrypt($("#mobile").val()); // 모바일 암호화
        $("#mobile").val(moEncrypt);
    }
    if(isNotEmpty(tel)){ // 전화번호가 null이 아니면
        var telEncrypt = encrypt.enCrypt($("#tel").val()); // 전화번호 암호화
        $("#tel").val(telEncrypt);
    }
    if(isNotEmpty(approve_email)){ // 승인메일주소가 null이 아니면
        var apEncrypt = encrypt.enCrypt($("#approve_email").val()); // 승인메일주소 암호화
        $("#approve_email").val(apEncrypt);
    }
    var oriemEncrypt = encrypt.enCrypt($("#ori_email").val()); // 변경 전 이메일 암호화
    $("#ori_email").val(oriemEncrypt);

    $("#encAESKey").val(encryptKey);
}
/* 체크박스변경스크립트 시작 */
/**
 * 전체선택 버튼
 * @param chkId
 */
function onSelectAll(chkId) {
    if ($("#" + chkId).is(":checked")) {
        $("input:checkbox").each(function() {
            $(this).attr("checked", true);
        });
    } else {
        $("input:checkbox").each(function() {
            $(this).attr("checked", false);
        });
    }
}

function isNotEmpty(str){
    obj = String(str);
    if(obj == null || obj == undefined || obj == 'null' || obj == 'undefined' || obj == '' ) return false;
    else return true;
}


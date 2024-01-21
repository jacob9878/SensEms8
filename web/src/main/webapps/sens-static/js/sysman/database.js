$(document).ready(function () {
    var dbtype = $("#dbtype").val() ;
    if(dbtype == "mysql" || dbtype == "mssql" || dbtype == "tibero"){
        $("#oracle_svc_div").hide();
        $("#host_div").show();
        $("#port_div").show();
        $("#real_dbname_div").show();
    }else if(dbtype == "oracle"){
        $("#host_div").hide();
        $("#port_div").hide();
        $("#real_dbname_div").hide();
        $("#oracle_svc_div").show();
    }
    changePort.init();
});
/*연결테스트 진행여부*/
var connectTest = false;

var database = {
    /* 페이징 */
    list:function(para) {
        $("#cpage").val(para);
        $("#databaseForm").submit();
    },
    /* 목록으로 이동 */
    goList:function() {
        location.href="list.do?cpage="+$("#cpage").val();
    },
    /* 추가화면 */
    addForm:function(){
      location.href="add.do?cpage="+$("#cpage").val();
    },
    /* 추가처리 */
    add:function(){
        //유효성 체크
        if(!validator.validData()) return ;

        if(!connectTest){
            alert(message_sysman.O0041); //DB 연결테스트를 진행해주세요
            return false;
        }

        var dbtype = $("#dbtype").val();

        if(dbtype == "mysql" || dbtype == "mssql" || dbtype == "tibero"){
            var enHost = encrypt.enCrypt( $("#dbhost").val() );
            var enDbname = encrypt.enCrypt( $("#real_dbname").val() );
            $("#dbhost").val(enHost);
            $("#real_dbname").val(enDbname);
        } else if(dbtype == "oracle"){
            var enHost = encrypt.enCrypt( $("#oracle_svc").val() );
            $("#oracle_svc").val(enHost);
        }

        var enDbuser = encrypt.enCrypt( $("#dbuser").val() );
        var enDbpass = encrypt.enCrypt( $("#dbpasswd").val() );

        $("#dbuser").val(enDbuser);
        $("#dbpasswd").val(enDbpass);
        $("#encAESKey").val(encryptKey);

        $("#databaseForm").attr("action", "add.do");
        $("#databaseForm").attr("method", "post");
        $("#databaseForm").submit();
    },
    /* 수정화면 */
    editForm:function (ukey) {
        location.href="edit.do?ukey="+ukey+"&cpage="+$("#cpage").val();
    },
    /* 수정처리 */
    edit:function () {
        //유효성 체크

        if(!validator.validData()) return;

        if(!connectTest){
            alert(message_sysman.O0041); //DB 연결테스트를 진행해주세요
            return false;
        }

        var dbtype = $("#dbtype").val();

        if(dbtype == "mysql" || dbtype == "mssql" || dbtype == "tibero"){
            var enHost = encrypt.enCrypt( $("#dbhost").val() );
            var enDbname = encrypt.enCrypt( $("#real_dbname").val() );
            $("#dbhost").val(enHost);
            $("#real_dbname").val(enDbname);
        } else if(dbtype == "oracle"){
            var enHost = encrypt.enCrypt( $("#oracle_svc").val() );
            $("#oracle_svc").val(enHost);
        }

        var enDbuser = encrypt.enCrypt( $("#dbuser").val() );
        var enDbpass = encrypt.enCrypt( $("#dbpasswd").val() );

        $("#dbuser").val(enDbuser);
        $("#dbpasswd").val(enDbpass);
        $("#encAESKey").val(encryptKey);

        $("#databaseForm").attr("action", "edit.do");
        $("#databaseForm").attr("method", "post");
        $("#databaseForm").submit();
    },
    /* 삭제처리 */
    delete:function () {
        var selCount = 0; // 선택된 갯수
        var ukeys = [];
        $("input[name=ukey]:checked").each(function(idx){
            ukeys.push($(this).val());
            selCount++;
        });

        if (selCount <= 0) { // 선택된 것이 없으면
            alert(message_sysman.O0031); // 삭제할 항목을 선택해주세요.
            return;
        }
        if(confirm(message_sysman.O0032)){ // 선택하신 항목을 삭제하시겠습니까?
            var param = {
                "ukeys[]":ukeys
            };

            var url = "delete.json";
            $.ajax({
                url : url,
                type : "post",
                data : param,
                dataType : "json",
                cache : false,
                async : false,
                error : function(xhr, txt){
                    AjaxUtil.error( xhr );
                },
                success : function(data) {
                    if(data.result){
                        alert(data.message);
                        location.reload();
                    } else{
                        alert(data.message);
                    }
                }
            });
        }
    },
    /**
     * 데이터베이스 연결테스트
     */
    doConnectTest:function () {

        //유효성 체크
        if(!validator.validData()) return;

        var param = $("#databaseForm").serialize();
        //alert(param);
        var url = "connectTest.json";
        $.ajax({
            url : url,
            type : "post",
            data : param,
            dataType : "json",
            cache : false,
            async : false,
            error : function(xhr, txt) {
                AjaxUtil.error( xhr );
            },
            success : function(data) {
                if(data.result){
                    connectTest = true;
                    alert(data.message);// 데이터베이스 연결에 성공했습니다.
                    //location.reload();
                } else{
                    connectTest = false;
                    alert(data.message); //데이터베이스 연결이 실패하였습니다.
                }
            }
        });
    }
};

/**
 * 데이터베이스 유형에 따른 포트 번호 세팅
 * 오라클 서비스 포트 div 숨김, 보임처리
 */
var changePort = {
    init:function () {
        $("#dbtype").on( "change", function() {
            var dbtype = $(this).val();
            if(dbtype == "mysql" || dbtype == "mssql" || dbtype == "tibero"){
                $("#oracle_svc_div").hide();
                $("#host_div").show();
                $("#port_div").show();
                $("#real_dbname_div").show();
                if(dbtype == "mysql"){
                    $("#dbport").val("3306");
                }else if(dbtype == "mssql"){
                    $("#dbport").val("1433");
                }else if(dbtype == "tibero"){
                    $("#dbport").val("8629");
                }
            }else if(dbtype == "oracle"){
                $("#host_div").hide();
                $("#port_div").hide();
                $("#real_dbname_div").hide();
                $("#oracle_svc_div").show();
                $("#oracle_port").val("1521");
            }
        });
    }
};

var validator = {
    validData: function () {
        var dbname = $.trim($("#dbname").val());
        var dbtype = $.trim($("#dbtype").val());
        var dbhost = $.trim($("#dbhost").val());
        var dbport = $.trim($("#dbport").val());
        var real_dbname = $.trim($("#real_dbname").val());
        var oracle_svc = $.trim($("#oracle_svc").val());
        var oracle_port = $.trim($("#oracle_port").val());
        var oracle_sid = $.trim($("#oracle_sid").val());
        var dbuser = $.trim($("#dbuser").val());
        var dbpasswd = $.trim($("#dbpasswd").val());
        var dbcharset = $.trim($("#dbcharset").val());
        var datacharset = $.trim($("#datacharset").val());

        if (dbname == '' || dbname == null) {
            alert(message_sysman.O0021);//데이터베이스 이름을 입력해주세요.
            $("#dbname").focus();
            return false;
        }
        if (dbtype == '' || dbtype == null) {
            alert(message_sysman.O0022);//데이터베이스 유형을 선택해주세요.
            return false;
        } else if (dbtype == 'oracle') { // oracle 선택 했을 경우에만 체크
            if (oracle_svc == '' || oracle_svc == null) {
                alert(message_sysman.O0024);//접속 호스트를 입력해주세요.
                $("#oracle_svc").focus();
                return false;
            } else if (oracle_port == '' || oracle_port == null) {
                alert(message_sysman.O0025);//포트번호를 입력해주세요.
                $("#oracle_port").focus();
                return false;
            } else if (oracle_sid == '' || oracle_sid == null) {
                alert(message_sysman.O0029);//SID 를 입력해주세요.
                $("#oracle_sid").focus();
                return false;
            }
        } else if (dbtype == 'mysql' || dbtype == 'mssql' || dbtype == 'tibero') {
            if (dbhost == '' || dbhost == null) {
                alert(message_sysman.O0024);//접속 호스트를 입력해주세요.
                $("#dbhost").focus();
                return false;
            }
            if (dbport == '' || dbport == null) {
                alert(message_sysman.O0025);//포트번호를 입력해주세요.
                $("#dbport").focus();
                return false;
            }
            if (real_dbname == '' || real_dbname == null) {
                alert(message_sysman.O0033);//DB명을 입력해주세요.
                $("#real_dbname").focus();
                return false;
            }
        } else {
            log.debug("dbtype value is '" + dbtype + "'");
            alert(message_sysman.O0030);//지원하지 않는 DB유형입니다.
            return false;
        }
        if (dbuser == '' || dbuser == null) {
            alert(message_sysman.O0026);//접속 아이디를 입력해주세요.
            $("#dbuser").focus();
            return false;
        }
        if (dbpasswd == '' || dbpasswd == null) {
            alert(message_sysman.O0027);//접속 비밀번호를 입력해주세요.
            $("#dbpasswd").focus();
            return false;
        }
        if (dbcharset == '' || dbcharset == null) {
            alert(message_sysman.O0028);//DB의 캐릭터셋을 지정해주세요.
            $("#dbcharset").focus();
            return false;
        }
        /*
        //datacharset 공백인 경우 dbcharset 과 같은 값으로 세팅
        if(datacharset == '' || datacharset == null){
            datacharset == dbcharset;
            $("#datacharset").val(dbcharset);
        }*/
        return true;
    },
    dbChange: function () {
    }
};

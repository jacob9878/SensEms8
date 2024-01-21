var demoaccount_list = {
    /* list, search */
    list:function(para){
        $("#cpage").val(para);
        $("#demoAccount").submit();
    },
    search:function(){
        $("#cpage").val('1');
        if ($("#srch_keyword").val() == "") {
            alert("검색어를 입력해주세요.");
            return;
        }
        $("#demoAccount").submit();
    },
    viewAll:function(){
        var bview = document.getElementById("allview");
        bview.style.display = "none";
        location.href = "list.do";
    },

    /* add, edit */

    add:function(){
        var email = $("#email").val();
        var flag = $("input[name=testAccount]:checked").val();

        if(email == ''){
            alert(message_sendManage.S0009);
            return;
        }
        if(!ImStringUtil.validateEmail(email)){
            alert(message_sendManage.S0008);
            $("#email").focus();
            return;
        }

        var param = {
            "email":$("#email").val(),
            "flag":flag
        }
        var url = "add.json";
        $.ajax( {
            url : url,
            data : param,
            type : "POST",
            dataType : "json",
            async : false,
            success : function(data) {
                if(data.result){
                    alert(message_sendManage.S0007);
                    demoaccount_list.close_popup();
                    location.reload();
                }else{
                    alert(data.message);
                    $("#email").val("");
                }
            },
            error:function(xhr){
                AjaxUtil.error(xhr);
            }
        });


    },
    edit:function(){
        var ukey = $("#edit_ukey").val();
        var email = $("#edit_email").val();
        var flag =  $("input[name=editTestAccount]:checked").val();
        var ori_email = $("#ori_email").val();

        var param = {
            "ukey":ukey,
            "email":email,
            "flag":flag,
            "ori_email":ori_email
        };

        if(email == ''){
            alert(message_sendManage.S0009);
            return;
        }
        if(!ImStringUtil.validateEmail(email)){
            alert(message_sendManage.S0008);
            $("#edit_email").focus();
            return;
        }

        var url = "edit.json";
        $.ajax({
            url : url,
            type : "post",
            data : param,
            dataType : "json",
            async : false,
            success : function(data) {
                if(data.result){
                    alert(data.message);
                    location.reload();
                } else{
                    alert(data.message);
                }
            }
        });

    },

    /* delete */
    delete:function(){
        var selCount = 0;
        var ukeys = [];
        $("input[name=ukeys]:checked").each(function(idx){
            ukeys.push($(this).val());
            selCount++;
        });

        if (selCount <= 0) {
            alert(message_sendManage.S0041);
            return;
        }

        if(confirm(message_sendManage.S0023)){
            var param = {
                "ukeys[]":ukeys
            };

            var url = "delete.json";
            $.ajax({
                url : url,
                type : "post",
                data : param,
                dataType : "json",
                async : false,
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

    /* popup */
    open_addPopup:function(){
        $("#email").val("");
        $("#addDemoAccountLayer").show();
    },
    open_editPopup:function(ukey,flag,email){
        $("#edit_ukey").val(ukey);
        $("#edit_email").val(email);
        $("#ori_email").val(email);
        ($("input[name=editTestAccount]").each(function(){
            if($(this).val()==flag){
                $(this).prop("checked",true);
            }
        }));
        $("#editDemoAccountLayer").show();

        var flag = $("input[name=testAccount]:checked").val();
    },
    close_popup:function (param) {
        $("#" + param + "DemoAccountLayer").hide();
    }



}
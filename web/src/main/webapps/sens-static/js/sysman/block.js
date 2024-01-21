var block_list = {

    pagesize:function(obj){
        $("#pagesize").val( obj.value );
        $("#BlockListForm").submit();
    },
    search:function(){
        $("#cpage").val('1');
        if ($("#srch_keyword").val() == "") {
            alert("검색어를 입력해주세요.");
            return;
        }
        $("#BlockListForm").submit();
    },
    list:function(para){
        $("#cpage").val(para);
        $("#BlockListForm").submit();
    },
    addForm:function(){
        $("#addBlockLayer").show();
    },
    viewAll:function(){
        var bview = document.getElementById("allview");
        bview.style.display = "none";
        location.href = "list.do";
    },


    deleteClick : function() {
        var selCount = ImFormUtils.selected_item_count("ips");
        if (selCount <= 0) {
            alert(message_common.CM0039); // 선택된 항목이 없습니다.
            return;
        }
        var ips = [];
        $("input[name=ips]:checkbox:checked").each(function () {
            var t = $(this).attr("value");
            ips.push(t);
        });
        if(confirm("삭제하겠습니까?")){
            var param = {
                "ips[]":  ips
            };
            var url = "delete.json";

            $.ajax( {
                url : url,
                data : param,
                type : "POST",
                dataType : "json",
                async : false,
                success : function(data) {
                    if(data.result){
                        alert(data.message);
                        location.reload();
                    }else{
                    }
                },
                error:function(xhr){
                    AjaxUtil.error(xhr);
                }
            });

        }

    },
    close_editPopup:function() {
        $("#editBlockLayer").hide();
    },

    open_editPopup:function(ip,memo){

        $("#editBlockLayer").show();
        $("#edit_ip").val(ip);
        $("#edit_memo").val(memo);
        $("#ori_ip").val(ip);


    },


    /**
     * 저장
     */

    edit:function(){
        var ip = $("#edit_ip").val();
        var memo = $("#edit_memo").val();
        var ori_ip = $("#ori_ip").val();

        var param = {
            "ip":ip,
            "memo":memo,
            "ori_ip":ori_ip
        }

        if(ori_ip == ip){
            alert("같은 ip입니다. 확인해주시고 다시 입력해주세요.");
            $("#edit_ip").focus();
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
                }else{
                    alert(data.message);
                }
                if(param.ip == ''){
                    alert("ip를 입력해주세요.");
                }
                if(!ImStringUtil.validateIPaddressCidr(param.ip)){
                    alert("ip형식에 맞지 않습니다.");
                    $("#edit_ip").focus();
                }

                // else{
                //     alert(data.message);
                // }
            }
        });

    }

}

var block_add = {
        ipChk:function(){
            var ip = $("#ip").val();
            var memo = $("#memo").val();
            var param ={"ip":ip, "memo":memo};
            var url =  "ipChk.json";
            if(ip == ''){
                alert("ip를 입력해주세요.");
                $("#ip").focus();
                return;
            }

            $.ajax({
                url : url,
                type : "post",
                data : param,
                dataType : "json",
                cache : false,
                async : false,
                success : function(data) {
                    if(!data.result) {
                        alert("이미 존재하는 ip입니다");
                        $("#ip").focus();
                        return;

                    }if(!ImStringUtil.validateIPaddressCidr(param.ip)){
                        alert("ip형식에 맞지 않습니다.");
                        $("#ip").focus();
                        return ;
                    }
                    else{
                        block_add.add();
                    }
                },
                error : function(xhr, txt) {
                    AjaxUtil.error( xhr );
                },
            });

        },
        add:function(){
            var param = {
                "ip":  $("#ip").val(),
                "memo":  $("#memo").val()
            };
            var url = "add.json";
            $.ajax( {
                url : url,
                data : param,
                type : "POST",
                dataType : "json",
                async : false,
                success : function(data) {
                    if(data.result){
                        alert(data.message);
                        block_add.close();
                        location.reload();
                    }else{
                        alert(data.message);
                    }
                },
                error:function(xhr){
                    AjaxUtil.error(xhr);
                }
            });
        },
        close:function(){
            $("#ip").val('');
            $("#memo").val('');
            $("#addBlockLayer").hide();

        }

    }
;

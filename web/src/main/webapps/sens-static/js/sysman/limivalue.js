var limitValue_list={

    edit:function(type,descript,value){
        $("#limit_type").val(type);
        $("#descript").text(descript);
        $("#limit_value").val(value);
        $("#ord_limit_value").val(value);

        $("#limitValueLayer").show();

    },
    close:function(){
        $("#limit_type").val('');
        $("#descript").empty();
        $("#limit_value").val('');
        $("#limitValueLayer").hide();
    }
};

var limitValue_edit={
    edit:function(){

        var value = $('#limit_value').val();
        var ord_limit_value = $('#ord_limit_value').val();
        var descript = $('#descript').text();
        if(value == ""){
            alert(message_sysman.O0039);
            return;
        }else if(!ImStringUtil.checkNumber(value)){
            alert(message_sysman.O0040);
            return;
        }

        var param = {
            "limit_value" : value,
            "limit_type" : $('#limit_type').val(),
            "ord_limit_value" : ord_limit_value,
            "descript" : descript
        }
        $.ajax({
            type: "POST",
            url : "/sysman/limit/edit.do",
            data : param,
            dataType:"json",
            success : function (json) {
                if(json.result){
                    limitValue_list.close();
                    location.reload();
                }else{
                    alert(json.message);
                    $('#limit_value').val("");
                }
            },
            error:function(xhr){
                AjaxUtil.error(xhr)
            }
        })
        return false;
    }
};
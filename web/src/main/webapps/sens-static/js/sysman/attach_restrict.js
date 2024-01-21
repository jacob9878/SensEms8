var attach_restrict={
    save:function () {
        var restrict_ext = $("#restrict_ext").val();

        // 특수문자 , 한글 체크
        if(checkSpecialChar(restrict_ext)){
            alert(message_common.CM0006);
            return ;
        }

        var param = {
            "restrict_ext": restrict_ext
        };

        var url = "/sysman/attach/restrict.json";
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
                    alert(data.message);
                    location.reload();
                } else{
                    alert(data.message);
                }
            }
        });
    }
};

/**
 * 특수문자 및 한글 체크
 * - _ , . $ 만 허용
 */
function checkSpecialChar(restrict_ext) {
    var regExp = /[\{\}\[\]\/?;:|\)*~`!^+<>@\#%&\\\=\(\'\"ㄱ-ㅎㅏ-ㅣ가-힣]/gi;
    if (regExp.test(restrict_ext)) {
        return true;
    } else{
        return false;
    }
};
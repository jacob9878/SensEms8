var templateEditor = {
    editorInit:function(){
        tinyMce.toolbar = 'fontfamily fontsize lineheight forecolor backcolor bold italic underline strikethrough alignment outdent indent table image link code';

        var content = $("#content").val();
        tinyMce.content = content;
        tinyMce.init();
    }
};
var templateList = {
    /**
     * 하단 페이지 목록 처리
     * @param para
     */
    list:function(para){
        $("#cpage").val(para);
        $("#TemplateListForm").submit();
    },
    viewAll:function(){
        var bview = document.getElementById("allview");
        bview.style.display = "none";
        location.href = "list.do";
    },

    search: function () {
        $("#cpage").val('1');
        var chk_keyword = $("#srch_keyword").val();

        if(chk_keyword == "") {
            alert("검색어를 입력해주세요.");

        }else {
            $("#TemplateListForm").submit();
        }


    },
    /**
     * 추가페이지로 이동
     */
    add:function (flag,srch_key,srch_type) {
        location.href="add.do?flag="+flag+"&cpage="+$("#cpage").val()+"&srch_keyword="+srch_key+"&srch_type="+srch_type;
    },
    /**
     * 수정페이지로 이동
     */
    edit:function (ukey,srch_key,srch_type,cpage) {
        var param = {
            "ukey" : ukey
        }
        var url = "/send/template/checkPermission.json";

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
                    location.href="edit.do?ukey="+ukey+"&srch_keyword="+srch_key+"&srch_type="+srch_type+"&cpage="+cpage;
                }
            }
        });


    },
    /**
     * 템플릿 분류 선택
     */
    changeOption : function (srch_type) {
        $("#srch_type").val(srch_type);
        $("#srch_keyword").val("");
        $("#TemplateListForm").submit();
    },
    /**
     * 템플릿 이미지 새창으로 보기
     */
    previewImage : function (ukey) {
        window.open("/send/image/preview.do?type=template&ukey="+ukey,"_blank","toolbar=no,width=900,height=500,directories=no,status=no,scrollbars=yes,resizable=yes,menubar=no")
    },

    /**
     * 템플릿 새창으로 미리보기
     */
    previewTemplate : function (ukey) {
        window.open("/send/template/preview.do?ukey="+ukey,"_blank","toolbar=no,width=900,height=650,directories=no,status=no,scrollbars=yes,resizable=yes,menubar=no")
    },
}
var templateAdd = {
    /**
     * 템플릿 추가 실시
     */
    save : function () {
        if(!$("#temp_name").val()){
            alert(message_sendManage.S0014);
            return;
        }

        var fileName = $("#file_upload").val();
        if(fileName){
            var fileExt = fileName.substring(fileName.lastIndexOf(".")+1).toLowerCase();
            if(fileExt != "jpg" && fileExt != "jpeg" && fileExt != "gif"){
                alert(message_sendManage.S0015);
                $("#file_upload").val("");
                return false;
            }
        }

        var content = tinymce.activeEditor.getContent();

        $("#content").val(content);
        alert(message_sendManage.S0045);
        $("#TemplateForm").submit();
    },
    /**
     * 목록 페이지로 이동
     */
    templateList : function () {
        location.href="list.do?cpage="+$("#cpage").val()+"&srch_keyword="+$("#srch_keyword").val()+"&srch_type="+$("#srch_type").val();

    }
};
var templateEdit = {
    /**
     * 이미지 파일 존재할 경우 파일 선택에 따른 파일 삭제여부 체크
     */
    changeFile : function () {
        var filename = $("#file_upload").val();

        if(!filename) {
            $("input:checkbox[name='isDeleteImage']").attr('disabled',false);
            $("input:checkbox[name='isDeleteImage']").prop('checked',false);
        }else{
            $("input:checkbox[name='isDeleteImage']").prop('checked',true);
            $("input:checkbox[name='isDeleteImage']").attr('disabled', true);
        }
    },
    /**
     * 템플릿 수정 실시
     */
    edit : function () {
        if(!$("#temp_name").val()){
            alert(message_sendManage.S0014);
            return;
        }

        var fileName = $("#file_upload").val();
        if(fileName){
            var fileExt = fileName.substring(fileName.lastIndexOf(".")+1).toLowerCase();
            if(fileExt != "jpg" && fileExt != "jpeg" && fileExt != "gif"){
                alert(message_sendManage.S0015);
                $("#file_upload").val("");
                return false;
            }
        }

        var content = tinymce.activeEditor.getContent();
        $("#content").val(content);
        $("#TemplateForm").submit();
    }
}
var templateDel = {
    /**
     * 템플릿 삭제
     */
    delete : function () {
        var ukeys = this.checkedList();

        if (ukeys == 0) {
            alert(message_sendManage.S0041);
            return;
        }
        var param = {
            "ukeys": ukeys
        }
        var url ="/send/template/deleteTemplates.json";


        if (confirm(message_sendManage.S0017)) {
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
    checkDelete : function () {
        var ukeys = this.checkedList();

        if (ukeys == 0) {
            alert(message_sendManage.S0041);
            return;
        }
        var param = {
            "ukeys": ukeys
        }
        var url ="/send/template/checkdeleteTemplates.json";


        if (confirm(message_sendManage.S0017)) {
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

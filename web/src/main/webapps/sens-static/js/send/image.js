var imageList = {
    /**
     * 하단 페이지 목록 처리
     * @param para
     */
    list:function(para){
        $("#cpage").val(para);
        $("#ImageListForm").submit();
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
            event.preventDefault();
        }else {
            $("#ImageListForm").submit();
        }
    },
    /**
     * 추가 팝업창 열기
     * */
    open_addPopup : function(){
        var srch_type = $("#srch_type").val();
        $("#flag").val(srch_type);
        $("#add_imagePopup").show();
    },
    /**
     * 팝업창 닫기
     * */
    close_popup : function(){
        $("#add_imagePopup").hide();
    },
    /**
     * 이미지 새창으로 보기
     */
    previewImage : function (ukey) {
        window.open("/send/image/preview.do?type=image&ukey="+ukey,"_blank","toolbar=no,width=900,height=500,directories=no,status=no,scrollbars=yes,resizable=yes,menubar=no")
    },
    /**
     * 이미지 분류 선택
     */
    changeOption : function (srch_type) {
        $("#srch_type").val(srch_type);
        $("#srch_keyword").val("");
        $("#ImageListForm").submit();
    },
    /**
     * 이미지 추가
     */
    addImage : function () {

        var fileName = $("#file_upload").val();

        if(!$("#image_name").val()){
            alert(message_sendManage.S0014);
            return false;
        }
        if (!fileName) {
            alert(message_sendManage.S0011);
            return false;
        }

        var fileExt = fileName.substring(fileName.lastIndexOf(".")+1).toLowerCase();
        if(fileExt != "jpg" && fileExt != "jpeg" && fileExt != "gif" && fileExt != "png" && fileExt != "bmp"){
            alert(message_sendManage.S0015);
            $("#file_upload").val("");
            return false;
        }

        var form = $("#image_upload");
        var formData = new FormData(form[0]);
        formData.append("file", $("#file_upload")[0].files[0]);
        formData.append("image_name",$("#image_name").val());
        formData.append("flag",$("#flag").val());
        $.ajax({
            url: '/send/image/add.json',
            processData: false,
            contentType: false,
            data: formData,
            type: 'POST',
            dataType : "json",
            async : false,
            error : function(xhr, txt) {
                AjaxUtil.error( xhr );
            },
            success: function(result){
                if (result.code==JSONResult.FAIL){
                    alert(result.message);
                }else{
                    alert(result.message);
                    location.reload();
                }
            }
        });
    }

}
var imageDel = {
    /**
     * 이미지 삭제
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
        var url ="/send/image/deleteImages.json";


        if (confirm(message_sendManage.S0013)) {
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
        var url ="/send/image/checkdeleteImages.json";


        if (confirm(message_sendManage.S0013)) {
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
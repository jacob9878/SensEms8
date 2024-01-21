$(document).ready(function() {
});

var reserveList = {
    /**
     * 하단 페이지 목록
     */
    pagesize:function(obj){
        $("#pagesize").val( obj.value );
        $("#rejectListForm").submit();
    },
    /**
     * 페이징을 처리를 위한 리스트 함수
     * @param para
     */
    list:function(para){
        $("#cpage").val(para);
        $("#reserveSendListForm").submit();
    },
    /**
     * 페이징을 처리를 위한 리스트 함수
     * @param para
     */
    list2:function(para){
        $("#cpage").val(para);
        $("#ReceiverAddrListForm").submit();
    },
    /**
     * 예약메일을 삭제한다.
     */
    reserveDel:function () {
        var ukeys = reserveList.checkedList();

        if (ukeys == 0) {
            alert(message_sendManage.S0004);
            return;
        }
        var param = {
            "ukeys": ukeys
        }
        var url ="/send/reserve/reserveDelete.json";

        if (confirm(message_sendManage.S0020)) {
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
    /**
     * 선택한 예약메일 페이지로 이동
     * @param msgid
     */
    view : function(msgid){
        var url ="/send/reserve/view.do?msgid="+msgid;
        location.href = url;

    },
    /**
     * 리스트의에서 선택된 목록의 ukey값을 리턴
     * @returns {any[]}
     */
    checkedList: function () {
        var ukeys = new Array();
        $("input[name='ukeys']:checkbox:checked").each(function () {
            ukeys.push($(this).val());
        });
        return ukeys;
    }
};

var reserveView = {
    /**
     * list 페이지로 이동
     */
    list : function(){
        var url ="/send/reserve/list.do";
        location.href = url;
    },
    /**
     * 수정 페이지로 이동
     * @param msgid
     */
    modify : function(){
        var msgid = $("#msgid").val();
        var url ="/send/reserve/modify.do?msgid="+msgid;
        location.href = url;
    },
}
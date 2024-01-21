/* 체크박스변경스크립트 시작 */
$(document).ready(function() {
  $('[name="expire_date"]').each(function() {
    $('[name="expire_date"]').datepicker({
      inline         : false,
      showOn         : focus(),
      changeMonth    : true,
      changeYear     : true,
      dayNames       : ['', '', '', '', '', '', ''],
      dayNamesMin    : ['일', '월', '화', '수', '목', '금', '토'],
      dayNamesShort  : ['일', '월', '화', '수', '목', '금', '토'],
      monthNames     : ['', '', '', '', '', '', '', '', '', '', '', ''],
      monthNamesShort: ['01', '02', '03', '04', '05', '06', '07', '08', '09',
        '10', '11', '12'],
      dateFormat     : 'yy-mm-dd',
      minDate        : new Date(),	// 시작일
      maxDate        : "12m"			// 종료일(현재부터 1년까지만)
    });
  });
});
/**
 * 전체선택 버튼
 * @param chkId
 */
function onSelectAll(chkId) {
    var checked = $("#" + chkId).is(":checked");
    $("input:checkbox").each(function() {
        if( $(this).attr("id") == "notice_today"){
            return true;
        }
        $(this).prop("checked", !checked);
    });
}

var attachList ={

    /**
     * 하단 페이지 목록
     */
    pagesize:function(obj){
        $("#pageSize").val( obj.value );
        $("#attachListForm").submit();
    },
    /**
     * 선택한 페이징을 처리한다
     * @param cpage
     */
    list:function(cpage){
        $("#cpage").val(cpage);
        $("#attachListForm").submit();
        this.reLoad();
    },
    searchFile : function() {
        if ($("#searchText").val() == "") {
            alert(message_sysman.O0049);
            return;
        }
        $("#cpage").val('1');
        $("#attachListForm").submit();
    },
    doAllList : function(url) {
        var url2 =url+".do";
        location.href = "/sysman/attach/" + url+".do";
    },
    /** 새로고침 비동기식으로 리스트를 갱신한다. */
    reLoad: function () {
        $("#attachListForm").submit();
    },


    /** 여러개 삭제 */
    deleteClick : function(ekey) {
        var url = "/sysman/attach/attachDel.do";

        var ekeyA = new Array();
        if (ekey == '') {
            // 여러개 삭제 시
            $("input[name='ekey[]']:checkbox:checked").each(function() {
                ekeyA.push($(this).val());
            });

        } else {
            // 한개만 삭제 시
            ekeyA.push(ekey);
        }

        if ( ekeyA.length == 0 ){
            alert(message_common.CM0039); // 선택된 항목이 없습니다.
            return;
        }

        // jConfirm("삭제", "삭제하시겠습니까?", function() {
        if ( window.confirm("정말 삭제하시겠습니까?")) {
            var param = {
                "ekey": ekeyA
            };
            $.ajax({
                url: url,
                data: param,
                type: "POST",
                dataType: "json",
                async: false,
                error: function (xhr, txt) {
                    AjaxUtil.error(xhr);
                },
                success: function (data) {
                    if (data.result == 1) {
                        location.reload();
                        // jInfo(data.message);
                        alert("삭제 되었습니다.");
                    } else {
                        /*jAlert(message_config.O0148);*/
                        alert("삭제 실패 했습니다.");
                    }
                }
            });
            //});
        }
    },

    /** 첨부파일 목록에서 개별 다운로드*/
    downLoad : function (ekey) {
        var url = "/sysman/attach/attachDownload.do?ekey=" + ekey;
        jDownloadSub(url);
    },

    expireDateUpdate : function(ekey, date){
      if ( window.confirm("만료일을 수정 하시겠습니까?")) {
        var url = "/sysman/attach/expireDateUpdate.json"
        var param = {
          "ekey": ekey,
          "date": date
        };
        $.ajax({
          url: url,
          data: param,
          type: "POST",
          dataType: "json",
          async: false,
          error: function (xhr, txt) {
            AjaxUtil.error(xhr);
          },
          success: function (data) {
            if (data.result == 1) {
              location.reload();
              alert("만료일이 수정 되었습니다.");
            } else {
              alert("만료일 수정 실패 했습니다.");
            }
          }
        });
      }else{
        location.reload();
      }
    }

}
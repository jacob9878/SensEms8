var sendfilter = {
    /* 페이지 목록 이동 */
    list:function(para){
        $("#cpage").val(para);
        $("#sendFilterForm").submit();
    },
    /* 검색 */
    doSearch:function(){
        $("#cpage").val('1'); // 공백 입력시 첫페이지 목록으로 이동하도록 처리
        if ($("#srch_keyword").val() == "") {
            alert("검색어를 입력해주세요.");
            return;
        }
        $("#sendFilterForm").submit();
    },
    viewAll:function(){
        var bview = document.getElementById("allview");
        bview.style.display = "none";
        location.href = "list.do";
    },
    /* 발송차단 도메인 추가(저장) */
    save:function () {
        var hostname = $.trim($("#hostname").val());

        if(!hostname){
            alert(message_sysman.O0020);
            return ;
        }
        // 특수문자 , 한글 체크
        if(checkHostname(hostname)){
            alert(message_common.CM0006);
            return ;
        }
        var param = {
            "hostname":hostname
        };

        var url = "add.json";
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
                    alert(data.message);//추가되었습니다.
                    location.reload();
                } else{
                    alert(data.message); // 작업중 오류가 발생하였습니다.이미 존재하는 도메인입니다.발송차단할 도메인을 입력해주세요.
                }
            }
        });
    },
    /* 발송차단 도메인 삭제 */
    delete:function () {
        var selCount = 0; // 선택된 갯수
        var hostnames = [];
        $("input[name=hostname]:checked").each(function(idx){
            hostnames.push($(this).val());
            selCount++;
        });

        if (selCount <= 0) { // 선택된 것이 없으면
            alert(message_sysman.O0018); // 선택된 도메인이 없습니다. 도메인을 선택해 주세요.
            return;
        }
        if(confirm(message_sysman.O0019)){
            var param = {
                "hostnames[]":hostnames
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
    /* 추가 모달창 팝업 열기 수행 */
    openAddPopup:function(){
        $("#hostname").val(''); // 도메인 입력값 비우고, 팝업 열기
        $("#sendFilterPopup").show();
    },
    /* 추가 모달창 팝업 닫기 수행 */
    doCancel:function () {
        $("#sendFilterPopup").hide();
    }
};

/**
 * 특수문자 및 한글 체크
 * - _ , . 만 허용
*/
function checkHostname(hostname) {
    var regExp = /[\{\}\[\]\/?;:|\)*~`!^+<>@\#$%&\\\=\(\'\"ㄱ-ㅎ가-힣]/gi;
    if (regExp.test(hostname)) {
        return true;
    } else{
        return false;
    }
};
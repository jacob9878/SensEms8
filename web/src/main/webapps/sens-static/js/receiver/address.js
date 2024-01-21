var addressList = {
    search: function () {
        $("#cpage").val('1');

        var email = $("#srch_keyword").val();
        var srch_type =$("#srch_type").val();

        if(email == ""){
            alert(message_common.CM0019);
            return ;
        }
        if( !EmailAddressUtil.checkAddress(email)&& srch_type =="email"){
            alert("이메일 형식이 올바르지 않습니다.\n" +
                "이메일 검색 시, 정확한 이메일을 입력해주세요.\n" +
                "ex) user@domain.com");
            return ;
        }else {
            $("#AddressListForm").submit();
        }

    },
    list: function (para) {
        $("#cpage").val(para);
        $("#AddressListForm").submit();
    },
    viewAll:function(){
        var bview = document.getElementById("allview");
        bview.style.display = "none";
        location.href = "list.do";
    },
    addressList: function () {
        location.href = "list.do?cpage="+$("#cpage").val();
    },
    add: function () {
        var gkey = $("#gkey").val();
        location.href = "add.do?gkey=" + gkey +"&cpage="+$("#cpage").val();
    },
    edit: function (ukey) {
        location.href = "edit.do?ukey=" + ukey + "&cpage=" + $("#cpage").val();
    },
    /**
     * 그룹 선택 변경
     * @param gkey
     */
    changeGkey: function (gkey) {
        $("#cpage").val('1');
        $("#srch_keyword").val("");
        $("#gkey").val(gkey);
        $("#AddressListForm").submit();
    },
    /**
     * 그룹 우측 펼치기 클릭시 동작
     * @param gkey
     */
    grpOptionToggle: function (gkey) {
        $("#grpOption" + gkey).toggle();
    },
    /**
     * 주소록 그룹 추가 / 수정 팝업 열기
     * @param gkey - null일경우 추가/ 값이 있을 경우 수정
     */
    addrGrpPopup: function (gkey) {
        if (!gkey) {
            $("#popupTitle").html(message_group.G0022);
            $("#grpName").val("");
            $("#grpMemo").val("");
        } else {
            $("#popupTitle").html(message_group.G0023);
            $("#group_key").val(gkey);

            var url = "/receiver/address/addrGrpPopupData.json";
            //추가일 경우 gkey = null, 수정일 경우 값 있음.
            var param = {
                "gkey": gkey
            };

            $.ajax({
                url: url,
                type: "post",
                data: param,
                dataType: "json",
                async: false,
                error: function (xhr, txt) {
                    AjaxUtil.error(xhr);
                },
                success: function (data) {
                    if (data.result) {
                        $("#grpName").val(data.grpInfo.gname);
                        $("#grpMemo").val(data.grpInfo.memo);
                    } else {
                        alert(data.message);
                    }
                }
            });
        }

        $("#addrGrpPopup").show();
    },
    /**
     * 그룹 팝업 닫기
     */
    closePopup: function () {
        $("#addrGrpPopup").hide();
    },
        /**
     * 주소록 그룹 추가/수정 동작
     */
    saveGrp: function () {
        var gname = $("#grpName").val();
        var memo = $("#grpMemo").val();
        var gkey = $("#group_key").val();

        if (!gname) {
            alert(message_group.G0027);
            return;
        }

        var url = "/receiver/address/saveAddressGrpInfo.json";
        var param = {
            "gkey": gkey,
            "gname": gname,
            "memo": memo,
        };

        $.ajax({
            url: url,
            type: "post",
            data: param,
            dataType: "json",
            async: false,
            error: function (xhr, txt) {
                AjaxUtil.error(xhr);
            },
            success: function (data) {
                if (data.result) {
                    alert(data.message);
                    location.reload();
                } else {
                    alert(data.message);
                }
            }
        });
    },
    /**
     * 그룹 삭제 팝업 열기
     * @param gkey
     */
    delGrpPopup: function (gkey) {
        if (gkey == '0' || gkey == '-1') {
            $("#deleteGrpOption").html('<option value="1">' + message_group.G0041 + '</option>');
            $("#deleteGrpOption").val('1');
            $("#msg").html('<span class="care_txt txt red block mg_t10"><strong>' + message_group.G0049 + '</strong></span>');
        } else {
            $("#deleteGrpOption").html('<option value="0">' + message_group.G0042 + '</option>');
            $("#deleteGrpOption").append('<option value="1">' + message_group.G0041 + '</option>');
            $("#deleteGrpOption").append('<option value="2">' + message_group.G0043 + '</option>');
            $("#deleteGrpOption").val('0');
            $("#msg").html('<span class="care_txt txt red block mg_t10"><strong>' + message_group.G0050 + '</strong></span>');
        }
        $("#deleteGkey").val(gkey)
        $("#deletegrpPopup").show();
    },
    /**
     * 주소록 그룹 삭제 팝업 닫기
     */
    closeDelete: function () {
        $("#deletegrpPopup").hide();
    },
    /**
     * 주소록 그룹 삭제하기
     */
    deleteAddrGrp: function () {
        var gkey = $("#deleteGkey").val();
        var delOpt = $("#deleteGrpOption").val();

        if (!gkey) {
            alert(message_group.G0028);
            return;
        }
        if (!delOpt) {
            alert(message_group.G0029);
            return;
        }

        var url = "/receiver/address/deleteAddrGrp.json";
        var param = {
            "gkey": gkey,
            "delOpt": delOpt
        };
        if (confirm(message_group.G0030)) {
            $.ajax({
                url: url,
                type: "post",
                data: param,
                dataType: "json",
                async: false,
                error: function (xhr, txt) {
                    AjaxUtil.error(xhr);
                },
                success: function (data) {
                    if (data.result) {
                        alert(data.message);
                        location.reload();
                    } else {
                        alert(data.message);
                    }
                }
            });
        }
    },
    /**
     * 주소록 데이터 추가 /수정 동작
     */
    saveAddr: function () {

        var name = $("#name").val();
        var gkey = $("#gkey").val();
        var email = $("#email").val();

        //필수값 체크
        if (!name) {
            alert(message_group.G0031);
            return;
        }
        if (!gkey) {
            alert(message_group.G0028);
            return;
        }
        if (!email) {
            alert(message_group.G0032);
            return;
        }

        // 형식 체크
        if (email) {
            if (!ImStringUtil.validateEmail(email)) {
                alert(message_group.G0035);
                $("#email").focus();
                return;
            }
        }

        var regExp = /^[0-9]+$/;

        var home_tel = $("#home_tel").val();
        var office_tel = $("#office_tel").val();
        var mobile = $("#mobile").val();
        var fax = $("#fax").val();
        var zipcode = $("#zipcode").val();

        if (home_tel) {
            if (home_tel.indexOf('-') > -1) {
                home_tel = home_tel.replaceAll('-', '');
            }

            if (!regExp.test(home_tel)) {
                alert(message_group.G0033);
                $("#home_tel").focus();
                return;
            }
        }
        if (office_tel) {
            if (office_tel.indexOf('-') > -1) {
                office_tel = office_tel.replaceAll('-', '');
            }

            if (!regExp.test(office_tel)) {
                alert(message_group.G0034);
                $("#office_tel").focus();
                return;
            }
        }
        if (mobile) {
            if (mobile.indexOf('-') > -1) {
                mobile = mobile.replaceAll('-', '');
            }

            if (!regExp.test(mobile)) {
                alert(message_group.G0036);
                $("#mobile").focus();
                return;
            }
        }
        if (fax) {
            if (fax.indexOf('-') > -1) {
                fax = fax.replaceAll('-', '');
            }

            if (!regExp.test(fax)) {
                alert(message_group.G0037);
                $("#fax").focus();
                return;
            }
        }
        if (zipcode) {
            if (zipcode.indexOf('-') > -1) {
                zipcode = zipcode.replaceAll('-', '');
            }

            if (!regExp.test(zipcode)) {
                alert(message_group.G0038);
                $("#zipcode").focus();
                return;
            }
        }
        $("#AddressForm").submit();

    },
    /**
     * 주소록 팝업 열기
     */
    movePopup: function () {
        var ukeys = checkedList();
        if (ukeys == 0) {
            alert(message_group.G0051);
            return;
        }
        $("#MoveAddrPopup").show();
    },
    /**
     * 주소록 이동 팝업 닫기
     */
    closeMove: function () {
        $("#MoveAddrPopup").hide();
    },
    /**
     * 체크된 주소록을 선택한 주소록 그룹으로 이동
     */
    moveAddr: function () {
        var ukeys = checkedList();
        var gkey = $("#MoveAddrOption").val();
        if (ukeys == 0) {
            alert(message_group.G0040);
            return;
        }
        var param = {
            "ukeys": ukeys,
            "gkey": gkey
        }
        var url = "/receiver/address/moveAddress.json";
        $.ajax({
            url: url,
            type: "post",
            data: param,
            dataType: "json",
            async: false,
            error: function (xhr, txt) {
                AjaxUtil.error(xhr);
            },
            success: function (result) {
                if (result.code == JSONResult.FAIL) {
                    alert(result.message);
                } else {
                    alert(result.message);
                    location.reload();
                }
            }
        });
    },
    /**
     * 주소록 목록 저장(다운로드)
     */
    downloadAddrList: function () {
        if (confirm(message_common.CM0017)) {
            var param = $("#AddressListForm").serialize();
            var url = "/receiver/address/saveList.do?" + param;
            jDownload(url);
        }
    },
    /**
     * 주소록 그룹 저장(다운로드)
     */
    downloadAddrGrp: function (gkey) {
        if (confirm(message_common.CM0017)) {
            var url = "/receiver/address/saveGrp.do?gkey=" + gkey;
            jDownload(url);
        }
    },
    /**
     * 파일 가져오기 실행
     */
    doImport: function () {
        window.open("/receiver/address/doImport1.do", "FileImport", "height=300,width=700,scrollbars=no,left=100,top=100,resizable");
    },
    /**
     * 파일로 가져오기 1단계 submit
     */
    doFileUp: function () {
        var file = $("#im_file").val();

        if (!file) {
            alert(message_common.CM0020);
            return;
        }

        var index = file.lastIndexOf('.');
        if (index == -1) {
            alert();
            return;
        }
        var strFileExt = file.substring(file.lastIndexOf('.'));
        strFileExt = strFileExt.toLowerCase();

        if (strFileExt != ".csv" && strFileExt != ".txt") {
            alert(message_common.CM0021);
            return;
        }

        return $("#importForm").submit();
    },
    /**
     * 샘플 파일 다운로드
     */
    addrSampleDownload: function () {
        jDownload("/receiver/address/sampleDownload.do");
    },
    /**
     * 파일로 가져오기 2단계 미리보기 실행
     */
    doPreview: function () {
        var div = -1;
        var header = 0;

        for (var i = 0; i < importForm.div.length; i++) {
            if (importForm.div[i].checked == true) {
                div = i;
            }
        }

        if (importForm.isheader.checked == true) {
            header = 1;
        }

        var param = {
            "fileKey": importForm.fileKey.value,
            "div": div,
            "header": header
        };

        $.ajax({
            url: "/receiver/address/import/preview.json",
            data: param,
            type: "post",
            dataType: "json",
            async: false,
            success: function (data) {
                if (!data.result) {
                    alert(data.message);
                } else {
                    $("#frmPreview").html(data.previewHtml);
                }
            },
            error: function (xhr, txt) {
                AjaxUtil.error(xhr);
            }
        });
    },
    /**
     * 파일로 가져오기 2단계 submit
     */
    doFileSubmit: function () {
        for (var i = 0; i < importForm.div.length; i++) {
            if (importForm.div[i].checked == true) {
                importForm.divMethod.value = i;
            }
        }

        if (importForm.divMethod.value == '-1') {
            alert(message_group.G0044);
            return false;
        }

        if (importForm.isheader.checked == true) {
            importForm.header.value = 1;
        }

        return importForm.submit();
    },
    /**
     * 주소록 그룹 선택하기
     */
    doSelect: function () {
        if ($("#gkey").val() == '-1') {
            $("#newGrpName").val("");
            $("#newGrpName").show();
            $("#newGrpName").focus();
        } else if ($("#gkey").val() == '0') {
            $("#newGrpName").hide();
        } else {
            $("#newGrpName").hide();
        }
    },
    /**
     * 파일로 가져오기 2단계로 넘어가기
     */
    doPrev: function () {
        importForm.action = "doImportPrev.do?";
        importForm.submit();
    },
    /**
     * 파일로 가져오기 3단계 submit
     */
    doInsertAddr: function () {

        if (importForm.name.value == "-1") {
            alert(message_group.G0045);
            return;
        }
        if (importForm.email.value == "-1") {
            alert(message_group.G0046);
            return;
        }

        if(importForm.gkey.value == "-1"){
            if (!$("#newGrpName").val()) {
                alert(message_group.G0047);
                return;
            }
            $("#gname").val($("#newGrpName").val());
        }

        return importForm.submit();
    },
    addressSetting: function (){
      if (confirm(message_group.G0052)) {
        $.ajax({
          url    : "/receiver/address/addressSetting.json",
          type   : "post",
          dataType: "json",
          async  : false,
          success: function (data) {
            if (data.result) {
              alert(data.message);
              location.reload();
            } else {
              alert(data.message);
            }
          },
          error  : function (xhr, txt) {
            AjaxUtil.error(xhr);
          }
        });
      }
    }
}
var addressDel = {
    delete : function () {
        var ukeys = checkedList();

        if (ukeys == 0) {
            alert(message_group.G0051);
            return;
        }
        var param = {
            "ukeys": ukeys
        }
        var url ="/receiver/address/deleteAddress.json";


        if (confirm(message_group.G0030)) {
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
    }
}
function checkedList() {
    var ukeys = new Array();
    $("input[name='ukeys']:checkbox:checked").each(function () {
        ukeys.push($(this).val());
    });
    return ukeys;
}


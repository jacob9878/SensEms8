$(document).ready(function () {
  writeform.editorInit();

  var default_date = new Date();
  default_date.setDate(default_date.getDate()+7); // 일주일이 기본값
  var default_date2 = new Date();
  default_date2.setHours(default_date2.getHours()+1) // 예약발송 기본값 1시간후
  $('#resp_day').datepicker({
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
    maxDate        : "12m",			// 종료일(현재부터 1년까지만)
    duration       : 10
  });
  $("#resp_day").datepicker("setDate", default_date);

  $('#reserv_day').datepicker({
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
    maxDate        : "12m",			// 종료일(현재부터 1년까지만)
    duration       : 10
  });
  $("#reserv_day").datepicker("setDate", new Date());
  $("#reserv_hour").val(default_date2.getHours());

});

var writeform = {
  sending         : false,
  editorInit      : function () {
    tinyMce.init();
  },
  insertTitleField: function () {

    var field = $("#titleFieldSelect").val();

    if (field == "필드삽입") {
      alert("수신그룹을 선택하세요.");
      return;
    }

    var msg_name = document.getElementById("msg_name") //제목

    var scroll = msg_name.scrollTop;
    var start = msg_name.selectionStart;
    var end = msg_name.selectionEnd;
    msg_name.value = msg_name.value.substring(0, start)+field
        +msg_name.value.substring(end, msg_name.value.length);
    msg_name.setSelectionRange(start+field.length, start+field.length);
    msg_name.scrollTop = scroll;
  },
  change_sendmode : function (type) {
    if (!type) {
      $("#reserv_time_area").css("display", "none");
    } else {
      if (type == '0') {
        $("#reserv_time_area").css("display", "none");
      } else {
        $("#reserv_time_area").css("display", "inline-block");
      }
    }
  },
  previewClose    : function () {
    $("#previewLayer").hide();
  },
  testPopupShow   : function () {
    /** 테스트 발송 수행 전 수신그룹이 선택된 부분이 있는지 제목이 입력되어있는지 체크 **/
    $("#testAccount").empty();
    $("#testSendAccount").empty();
    var receiver = $("#recid").val();
    var subject = $("#msg_name").val();

    if (receiver == '' || receiver == null) {
      alert(message_sendManage.S0027); // 수신그룹이 선택되지 않았습니다. 수신그룹을 선택해주세요.
      return false;
    }

    if (subject == '' || subject == null) {
      alert(message_sendManage.S0014); // 제목을 입력해주세요.
      return false;
    }

    /** 테스트 사용자 목록을 불러온다. **/
    writeform.getTestAccountListDraw();

    /** 테스트 발송 레이아웃을 보여준다. **/

    $("#testSendPopup").show();
  },

  closeTestPopup: function () {
    /** 테스트 발송 레이아웃을 숨긴다. **/

    $("#testSendPopup").hide();

  },

  /** 테스트 발송을 수행한다.  **/
  doTestSend            : function () {
    /** 테스트 발송 수신자 목록이 존재하는지 체크하여 입력값이 없으면 retrun **/
    var count = 0;
    var to_emails = new Array();

    $("#testSendAccount option").each(function (idx) {
      to_emails.push($(this).val());
      count++;
    })

    if (count == 0) {
      alert("테스트 발송 계정을 추가해주시기 바랍니다.");
      return false;
    }

    /** 테스트 발송 데이터 setting **/
        //TODO isAttach값은 테스트 발송에서 필요없으므로 TestSendController에 isAttach 디폴트 값을 0으로 처리하였으므로 발송단에서는 해당 값을 추출해서 param으로 전달 필요

    var subject = $("#msg_name").val();
    var charset = $("#charset").val(); // charset은 utf-8을 기본값으로 지정 2022-09-13
    var ishtml = $("#ishtml").val(); // Mail Type은 HTML을 기본값으로 지정 2022-09-13
    var from_email = $("#mail_from").val();
    var recid = $("#recid").val();
    //TODO 수신자 그룹 선택 유형 처리 필요 rectype = 1(주소록) rectype = 3(수신그룹) - 정기예약발송은 수신유형이 수신그룹만 존재함. 정기예약발송은 2로 설정해서 전달
    var rectype = $("input:radio[name='rectype']:checked").val();

    var f = document.reserveSendForm;
    var content = tinymce.activeEditor.getContent();

    /*if(ishtml == '1'){
        var content = $("#editHtml").editor("getHtml");
    }else{
        var content = $("#editHtml").editor("getText");
    }*/
    //TODO 첨부파일 처리 방안 1.첨부파일이 존재하는 경우 editor 본문에 첨부파일 내용을 append 해서 전달하여 처리 하거나 아래와 같이 Param에 isAttach를 1로 전달하여 비지니스 로직에서 처리를 하는 케이스로 처리 혹은 아래와 같이 처리
    //TODO 첨부파일 처리 방안 2.첨부파일이 존재하는 경우 testSendController에서 아래 pram값에 isAttach 1을 포함하여 처리

    var isAttach = '0';
    var fileName = $("#file_upload").val();
    if (data_array[0] != null) {
      isAttach = '1';
      if (!writeform.addAttach(fileName)) {
        isAttach = '0';
        writeform.sending = false;
        return;
      }
    }
    var att_keys = $("#att_keys").val();

    var param = {
      "to_emails[]": to_emails,
      "subject"    : subject,
      "charset"    : charset,
      "ishtml"     : ishtml,
      "from_email" : from_email,
      "content"    : content,
      "isAttach"   : isAttach,
      "recid"      : recid,
      "rectype"    : rectype,
      "att_keys"   : att_keys
    };

    var url = "/send/test/testSend.json";

    $.ajax({
      url     : url,
      data    : param,
      type    : "POST",
      dataType: "json",
      async   : false,
      success : function (data) {
        if (data.result) {
          alert(data.message);

          $("#testSendPopup").hide();

        } else {
          alert(data.message);
        }
      },
      error   : function (xhr) {
        AjaxUtil.error(xhr);
      }
    });
    /** 테스트 발송을 한다. **/

  },
  testAccountInsert     : function () {

    /** 테스트 발송에서 선택된 option의 테스트 계정을 주입한다. **/

    var sel_account = document.getElementById("testAccount");

    if (sel_account === '' || sel_account == null) {
      return false;
    }

    for (i = 0; i < sel_account.length; i++) {
      if (sel_account[i].selected) {
        var account = sel_account[i].value;
        $("#testSendAccount").append(
            '<option value="'+account+'">'+account+'</option>');
      }
    }
    $("#testAccount option:selected").remove();

  },
  testAccountDelete     : function () {
    /** 테스트 발송에서 선택된 option의 테스트 계정을 삭제한다. **/
    var sel_account = document.getElementById("testSendAccount");

    if (sel_account == '' || sel_account == null) {
      return false;
    }

    for (i = 0; i < sel_account.length; i++) {
      if (sel_account[i].selected) {
        var account = sel_account[i].value;
        $("#testAccount").append(
            '<option value="'+account+'">'+account+'</option>');
      }
    }
    $("#testSendAccount option:selected").remove();
  },
  getTestAccountListDraw: function () {
    var url = "/send/demoaccount/list.json";
    $.ajax({
      url     : url,
      type    : "get",
      dataType: "json",
      async   : false,
      error   : function (xhr, txt) {
        AjaxUtil.error(xhr);
      },
      success : function (data) {
        if (data.result) {
          var demoAccountList = data.demoAccountList;
          $.each(demoAccountList, function (index, item) {
            if (item.flag != '1') {
              $("#testAccount").append(
                  '<option value="'+item.email+'">'+item.email+'</option>');
            } else {
              $("#testSendAccount").append(
                  '<option value="'+item.email+'">'+item.email+'</option>');
            }

          })
        }
      }
    });
  },
  /** 테스트 레이어에서 입력한 테스트계정을 추가**/
  demoAccountInsert     : function () {
    var url = "/send/demoaccount/add.json";
    var email = $("#accountInsertText").val();
    var flag = '0';

    if (email == '') {
      alert(message_sendManage.S0009);
      return;
    }
    if (!ImStringUtil.validateEmail(email)) {
      alert(message_sendManage.S0008);
      $("#accountInsertText").focus();
      return;
    }
    var param = {
      "email": email,
      "flag" : flag
    }
    $.ajax({
      url     : url,
      data    : param,
      type    : "POST",
      dataType: "json",
      async   : false,
      success : function (data) {
        if (data.result) {
          alert(message_sendManage.S0007);
          $("#testAccount").append(
              '<option value="'+email+'">'+email+'</option>');
        } else {
          alert(data.message);
          $("#accountInsertText").val("");
        }
      },
      error   : function (xhr) {
        AjaxUtil.error(xhr);
      }
    });
  },
  // 템플릿 추가 창 닫기
  templateAddClose      : function () {
    $("#addTemplateLayer").hide();
  },
  // 템플릿 추가 창 보이기 이벤트
  templateAddShow       : function () {
    $("#addTemplateLayer").show();
  },
  // 템플릿 추가 데이터 입력 여부 검증
  templateAdd           : function () {
    var subject = $("#temp_name").val();
    var flag = $("#flag").val();
    var contents = EditorUtil.getBody();

    if (!subject) {
      alert(message_sendManage.S0014);
      return false;
    }

    var param = {"subject": subject, "flag": flag, "contents": contents};

    var url = "/send/template/templateAdd.json";
    $.ajax({
      url     : url,
      type    : "post",
      data    : param,
      dataType: "json",
      cache   : false,
      async   : false,
      success : function (data) {
        if (data.result) {
          alert(message_common.CM0014);
          $("#addTemplateLayer").hide();
          var subject = $("#temp_name").val('');
          return;

        } else {
          alert(message_common.CM0016);
        }
      },
      error   : function (xhr, txt) {
        AjaxUtil.error(xhr);
      },
    });

    // 에이작스로 추가하는 이벤트 추가
  },
  changeDatesub         : function (time) {
    return time.substring(0, 4)+time.substring(5, 7)+time.substring(8, 10);
  },

  sendProcess: function (type) {

    // 발송 두번 방어
    if (writeform.sending) {
      return;
    }

    writeform.sending = true;

    var mail_from = $("#mail_from").val();
    var replyto = $("#replyto").val();
    var recname = $("#recname").val();
    var subject = $("#msg_name").val();
    var response_time = $("#resp_day").val();
    var resp_day = writeform.changeDatesub(response_time);
    var is_reserve = $(":radio[name='is_reserve']:checked").val();
    var resp_date = resp_day+$("#resp_hour").val(); // 반응분석일 yyyymmddhh 로 변환
    var now = writeform.dateFormatHour(new Date(), 1); // 현재 날짜시간 yyyymmdd 로 변환
    var now2 = writeform.dateFormatHour(new Date(), 2); // 현재 날짜시간 yyyymmddhhmm 로 변환
    var reserv_day = writeform.changeDatesub($("#reserv_day").val())
    var reserv_date = reserv_day+$("#reserv_hour").val()+$("#reserv_min").val(); // 예약발송일 yymmddhhmm 로 변환
    var rectype = $("#rectype").val();

    if (!mail_from) {
      alert(message_mail.M0003);
      writeform.sending = false;
      return;
    }

    if (!replyto) {
      alert(message_mail.M0004);
      writeform.sending = false;
      return;
    }

    if (!validateFromEmail(mail_from)) {
      alert(message_mail.M0005);
      writeform.sending = false;
      return;
    }

    if (!ImStringUtil.validateEmail(replyto)) {
      alert(message_mail.M0006);
      writeform.sending = false;
      return;
    }

    if (!recname) {
      if (rectype != 4) {
        alert(message_mail.M0007);
        writeform.sending = false;
        return;
      }
    }

    if (!subject) {
      alert(message_mail.M0008);
      writeform.sending = false;
      return;
    }

    if (!resp_day) {
      alert(message_mail.M0009);
      writeform.sending = false;
      return;
    }

    if (is_reserve == "1") {
      if (!$("#reserv_day").val()) {
        alert(message_mail.M0010);
        writeform.sending = false;
        return;
      }
    }

    if (Number(resp_date) <= Number(now)) { // 반응분석 종료일이 현재 시간보다 이전이면 return
      alert(message_mail.M0012);
      writeform.sending = false;
      return;
    }
    if (is_reserve == "1") {
      if (Number(reserv_date) < Number(now2)) { // 예약 발송일이 현재 시간보다 이전이면 return
        alert(message_mail.M0013);
        writeform.sending = false;
        return;
      }
    }

    var reserv_year = reserv_day.substring(0, 4);
    var reserv_month = reserv_day.substring(4, 6);
    var reserv_days = reserv_day.substring(6, 8);
    var reserv_hour = $("#reserv_hour").val();
    var reserv_minute = $("#reserv_min").val();

    var exDays = new Date(reserv_year, reserv_month, reserv_days, reserv_hour,
        reserv_minute);
    exDays.setMinutes(exDays.getMinutes()-5);
    var exHours = exDays.getHours();
    if (exHours < 10) {
      exHours = "0"+exHours;
    }
    var exDay = reserv_year+reserv_month+exDays.getDate()+exHours
        +exDays.getMinutes();
    if (is_reserve == "1") {
      if (exDay < now2) {
        alert(message_mail.M0015);
        writeform.sending = false;
        return;
      }
    }

    $("#state").val(type);

    /*var content = "";
    var ishtml = $("#ishtml").val(); // Mail Type은 HTML을 기본값으로 지정 2022-09-13
    if(ishtml == '1'){
            content = $("#editHtml").editor("getHtml");
        }else{
            content = $("#editHtml").editor("getText");
        }*/

    var content = tinymce.activeEditor.getContent();
    $("#content").val(content);

    var fileName = $("#file_upload").val();
    if (data_array[0] != null && type != "1") {
      if (!writeform.addAttach(fileName)) {
        writeform.sending = false;
        return;
      }
    }

    $.ajax({
      url     : "/mail/write/write.json",
      type    : "POST",
      data    : $("#MailWriteForm").serialize(),
      dataType: "json",
      async   : true,
      error   : function (xhr) {
        AjaxUtil.error(xhr);
        writeform.sending = false;
      },
      success : function (data) {
        if (data.result) {
          location.href = "/mail/result/list.do";
        } else {
          writeform.sending = false;
          alert(data.message);
        }
      }
    });
  },

  dateFormatHour(date, ck) { // ck=1 반응분석종료일, ck2=예약발송일
    let month = date.getMonth()+1;
    let day = date.getDate();
    let hour = date.getHours();
    let minute = date.getMinutes();
    let second = date.getSeconds();

    month = month >= 10 ? month : '0'+month;
    day = day >= 10 ? day : '0'+day;
    hour = hour >= 10 ? hour : '0'+hour;
    minute = minute >= 10 ? minute : '0'+minute;
    second = second >= 10 ? second : '0'+second;

    if (ck == 1) {
      return date.getFullYear()+month+day+hour;
    } else if (ck == 2) {
      return date.getFullYear()+month+day+hour+minute;
    }
  },

  addAttach: function (fileName) {
    var success = true;

    /*var form = $("#uploadForm");
    var formData = new FormData(form[0]);
    formData.append("file", $("#file_upload")[0].files[0]);*/
    var form_data = new FormData();
    for (i in data_array) {
      form_data.append("file", data_array[i][Object.keys(data_array[i])]);
    }
    $.ajax({
      url        : '/mail/write/uploadFile.json',
      processData: false,
      contentType: false,
      data       : form_data,
      type       : 'POST',
      dataType   : "json",
      async      : false,
      error      : function (xhr, txt) {
        AjaxUtil.error(xhr);
      },
      success    : function (data) {
        if (!data.result) {
          alert(data.message);
          success = false;
        } else {
          $("#att_keys").val(data.att_keys);
        }
      }
    });

    return success;
  },
  doResend : function () {

    if (writeform.sending) {
      return;
    }

    writeform.sending = true;

    var content = tinymce.activeEditor.getContent();
    $("#content").val(content);

    var to_mail = $("#mail_to").val();
    $("#recname").val(to_mail);

    var url = "/mail/write/rewrite.json";

    $.ajax({
      url     : url,
      data    : $("#MailWriteForm").serialize(),
      type    : "POST",
      dataType: "json",
      async   : false,
      success : function (data) {
        if (data.result) {
          alert(data.message);
          window.opener.closeWin();   //리스트 팝업 닫기
          window.close();             //작성 팝업 닫기
        } else {
          alert(data.message);
        }
      },
      error   : function (xhr) {
        AjaxUtil.error(xhr);
      }
    });
  }
}

/**
 * 보내는 사람 주소 검사
 * 이름<이메일>
 * */
function validateFromEmail(str) {
  //보내는 사람에 이름이 빠졌을 떄 발송이 안되게 하는 로직
  // var word = str.substring(0, str.indexOf('<'));
  var s = str.indexOf("<");
  var e = str.indexOf(">");

  // if (word == "") {
  //     return false;
  // }

  if (s > -1 && e > -1) {
    str = str.substring(s-1, e);
    if (ImStringUtil.validateEmail(str)) {
      return true;
    } else {
      return false;
    }
  } else {
    return false;
  }
}

$(document).ready(function () {
  var upload_box = document.querySelector('#filelist');
  upload_box.addEventListener('dragover', function (e) {
    e.preventDefault();
  });

  upload_box.addEventListener('drop', function (e) {
    e.preventDefault();
    var data = e.dataTransfer.files;
    addFile(data);
  });

  $("#chk_all").click(function () {
    if ($("#chk_all").prop("checked")) {
      $(".chk_attach").prop("checked", true);
    } else {
      $(".chk_attach").prop("checked", false);
    }
  });

  $(".chk_attach").click(function () {
    if (!$(this).is(":checked")) {
      $("#chk_all").prop("checked", false);
    }
  });
});

var data_array = [];
var nfile_size = 0;
var nfile_count = 0;

function readFile() {
  var file_arr = document.getElementById('file_upload').files;
  if (file_arr) {
    addFile(file_arr);
  }
}

function addFile(file_arr) {
  for (var i = 0; i < file_arr.length; i++) {
    var temp_count = nfile_count+1;
    if (temp_count > max_nfile_count) {
      alert("첨부 가능한 최대 개수를 초과하였습니다."); //TODO 메시지 설정 필요S0045
      break;
    }
    var temp_size = nfile_size+file_arr[i].size;
    if (temp_size > max_nfile_size) {
      alert("첨부 가능한 최대 크기를 초과하였습니다."); //TODO 메시지 설정 필요 S0046

      break;
    }

    var fkey = Math.floor(Math.random() * 100000);
    var data_arr = {};
    data_arr[fkey] = file_arr[i];

    var check = false;
    for (var j = 0; j < list.length; j++) {
      if (file_arr[i].name.toLowerCase().indexOf(list[j]) > -1) {
        alert("제한된 확장자 입니다.");
        check = true;

      }
    }
    if (!check) {
      data_array.push(data_arr);
      $('#attach_file').append('<tr id="'+fkey
          +'" class="attachfile"><td scope="col" class="" style="text-align: center"><input type="checkbox" name="att" class="chk_attach" value="'
          +fkey+'"></td><td scope="col" class=""><span>'+file_arr[i].name
          +'</span></td>'+
          '<td scope="col" class=""><span>'+file_arr[i].size.byteFormat(2)
          +'</span></td><td scope="col" class=""><span><input type="button" class="file_delete" value="삭제" name="'
          +i+'" onclick="delFile('+fkey+');"></span></td></tr>');
      $('#attach_message').css({"display": "none"});

      nfile_size = temp_size;
      nfile_count++;

      $("#attach_size").html(nfile_size.byteFormat(2));
      $("#curr_uploadfile_cnt").html(data_array.length);
      $("#filelist").show();
    }
  }

}

function chkDelFile() {
  $("input:checkbox[name='att']").each(function () {
    if ($(this).is(":checked") == true) {
      delFile($(this).val());
    }
  });
}

function delFile(fkey) {
  $("#"+fkey).remove();
  for (var i = 0; i < data_array.length; i++) {
    if (data_array[i][fkey]) {
      nfile_size -= data_array[i][fkey].size;
      nfile_count--;
      data_array.splice(i, 1);
    }
  }
  $("#attach_size").html(nfile_size.byteFormat(2));
  $("#curr_uploadfile_cnt").html(data_array.length);
  if ($('.attachfile').length == 0) {
    $('#attach_message').css({"display": ""});
  }
}

function openUploadArea() {
  if ($("#filelist").css("display") == "none") {
    $("#filelist").show();
  } else {
    $("#filelist").hide();
  }
}
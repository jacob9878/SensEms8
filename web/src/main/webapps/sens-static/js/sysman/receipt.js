/*
$(document).ready(function() {
		$("#recv_date").datepicker({
			showOn : focus(),
			changeMonth : true,
			changeYear : true,
			dayNames : [ '', '', '', '', '', '', '' ],
			dayNamesMin : [ msg.get(message_common.CM0024),	msg.get(message_common.CM0025),	msg.get(message_common.CM0026),	msg.get(message_common.CM0027),	msg.get(message_common.CM0028),	msg.get(message_common.CM0029),	msg.get(message_common.CM0030) ],
			dayNamesShort : [ msg.get(message_common.CM0024),	msg.get(message_common.CM0025),	msg.get(message_common.CM0026),	msg.get(message_common.CM0027),	msg.get(message_common.CM0028),	msg.get(message_common.CM0029),	msg.get(message_common.CM0030) ],
			monthNames : [ '', '', '', '', '', '', '', '', '', '', '', '' ],
			monthNamesShort : [ '01', '02', '03', '04', '05', '06', '07', '08', '09', '10','11', '12' ],
			dateFormat : 'yy-mm-dd',
			maxDate:'+0d',			// 종료일
			options:{
				disabled:true
			}
		});
});
*/

var receiptLog ={

	doSearch : function(cpage) {
		if (cpage == null || cpage == '') {
			cpage = 1;
		}

		var recv_date = $.trim($("#ReceiptForm input#recv_date").val());
		var searchKeywordMsgid = $.trim($("#ReceiptForm input#searchKeywordMsgid").val());
		var searchKeywordRcode = $.trim($("#ReceiptForm input#searchKeywordRcode").val());

		if (searchKeywordMsgid == "") {
			alert("메세지 아이디를 입력해주세요.");

		}
		else if (searchKeywordRcode == ""){
			alert("고유 아이디를 입력해 주세요.");
		}else if  (searchKeywordMsgid == "" && searchKeywordRcode == "") {
			alert("메세지 아이디를 입력해주세요.");
		}


		$("#ReceiptForm input#searchKeywordMsgid").val(searchKeywordMsgid);
		$("#ReceiptForm input#searchKeywordRcode").val(searchKeywordRcode);
		$("#ReceiptForm input#recv_date").val(recv_date);
		$("#ReceiptForm input#cpage").val(cpage);

		this.search();
	},

	search:function() {
		var param = $("#ReceiptForm").serialize();
		$.ajax({
			url: "../receipt/search.json",
			data: param,
			type: "POST",
			dataType: "json",
			async: false,
			success: function (data) {
				if(data == null || data.result == null || data.result.length == 0 ){
					receiptLog.drawNoTable();
				} else {
					receiptLog.drawTable(data);
				}
			},
			error: function (xhr) {
				AjaxUtil.error(xhr);
			}
		});
	},
	drawNoTable : function () {
		//페이징
		$("#pageInfo").hide();

		$("#tbody_logList").empty();
		$("#tbody_logList").hide();
		$("#tbody_noData").show();
	},
	drawTable : function (data) {
		var dataList = data.result;
		var page = data.pageInfo;
		var msg_name = data.msg_name;
		var mail_from = data.mailfrom;
		var item = [];

		for (var i = 0; i<dataList.length; i++) {
			item[i] = receiptLog.make_data(dataList[i],msg_name, mail_from);
		}

		var pageHtml = pageInfo(page.cpage, page.pageSize, page.total, '', 'receipt_log.doSearch');
		$("#pageInfo").html(pageHtml);
		$("#pageInfo").show();
		$("#tbody_noData").hide();
		$("#tbody_logList").empty();
		$("#tbody_logList").append(item.join(''));

		$("#tbody_logList").show();
	},

	make_data : function (data, msg_name, mail_from) {
		var strList = [], n = -1;

		var subject = msg_name;

		var fromadress = mail_from;

		strList[++n] ="<tr>";
		strList[++n] = "<th>" + message_sysman.O0044 + "</th>";
		strList[++n] = "<td>"+ data.recv_time +"</td>";
		strList[++n] ="</tr>";
		strList[++n] ="<tr>";
		strList[++n] = "<th>" + message_sysman.O0045 + "</th>";
		strList[++n] = "<td>"+ subject +"</td>";
		strList[++n] ="</tr>";
		strList[++n] ="<tr>";
		strList[++n] = "<th>" + message_sysman.O0046 + "</th>";
		strList[++n] = "<td>"+ data.field2 +"</td>";
		strList[++n] ="</tr>";
		strList[++n] ="<tr>";
		strList[++n] = "<th>" + message_sysman.O0047 + "</th>";
		strList[++n] = "<td>"+ data.field1 +"</td>";
		strList[++n] ="</tr>";
		strList[++n] ="<tr>";
		strList[++n] = "<th>" + message_sysman.O0048 + "</th>";
		strList[++n] = "<td>"+ fromadress +"</td>";
		strList[++n] ="</tr>";

		return strList.join('');
	}
}
/**
 * 보안설정 메뉴에서 사용하는 스크립트
 */
/* 체크박스변경스크립트 시작 */
function onSelectAll(chkId) {
	if ($("#" + chkId).is(":checked")) {
		$("input:checkbox").each(function() {
			$(this).attr("checked", false);
		});
	} else {
		$("input:checkbox").each(function() {
			$(this).attr("checked", true);
		});
	}
}
function selectAllChk(chkId){
	if ($("#" + chkId).is(":checked")) {
		$("input:checkbox").each(function() {
			$(this).attr("checked", true);
		});
	} else {		
		$("input:checkbox").each(function() {
			$(this).attr("checked", false);
		});
	}
}

// 보안설정 > 해킹메일 신고함
var hackingmail_report = {
	preview_width : 600,

	init : function() {
		$("#preView").dialog({
			bgiframe : false,
            autoOpen : false,
            height : 600,
            width : this.preview_width,
            modal : true,
            title : message_security.SO0005,
            buttons : [
                {
                    text : msg.get(message_security.SO0006),
                    click : function() {
                    	$("#preView").dialog('close');
                    }
                } ]
		});
		
		$("#commentView").dialog({
			bgiframe : false,
            autoOpen : false,
            height : 300,
            width : 500,
            modal : true,
            title : message_security.SO0022,
            buttons : [
                {
                    text : msg.get(message_security.SO0006),
                    click : function() {
                    	$("#commentView").dialog('close');
                    }
                } ]
		});		
	},
	doKeyPress : function() {
		if (event.keyCode != 13) {
			return;
		}
		this.doSearch();
	},
	doSearch : function() {
		var searchSelect = $.trim($("#HackingMailReportForm select#searchSelect").val());
		var searchText = $.trim($("#HackingMailReportForm input#searchText").val());
		$("#HackingMailReportForm input#searchText").val(searchText);
		
		if (searchText == "") {
			jAlert(message_security.SO0001);
			return;
		}
		
		this.doMailListFormReset();	// 기존 검색 조건을 초기화 한다.
		
		$("#HackingMailListForm input#cmd").val("search");
		$("#HackingMailListForm input#searchSelect").val(searchSelect);
		$("#HackingMailListForm input#searchText").val(searchText);
		
		this.doMailListFormSubmit();
	},
	doAllList : function() {
		this.doMailListFormReset();
		this.doMailListFormSubmit();
	},
	doList : function(cpage) {
        $("#HackingMailListForm input#cpage").val(cpage);
        this.doMailListFormSubmit();
	},
	doMailListFormSubmit : function() {		
		$("#HackingMailListForm").attr("method", "get");
		$("#HackingMailListForm").attr("action", app.contextPath +"/security/hackingreport/list.do");
		$("#HackingMailListForm").submit();
	},
	doView : function(ukey) {
		var preview_div_width = this.preview_width - 50;
		$("#preView div#viewArea").attr("style", "width:"+ preview_div_width +"px;");
		$("#preView #viewArea").html("<img src='"+ app.contextPath +"/security/hackingreport/image/view.do?ukey="+ ukey +"' style='max-width: 100%; display: block;'/>");
		
		$("#preView").dialog('open');
	},
	// 수신거부
	deny : function() {
		var DMail = new Array();
		$("input[name='ukey[]']:checkbox:checked").each(function() {
			DMail.push($(this).attr("value"));
		});
		
		if (DMail.length == 0) {
			jAlert(message_security.SO0002);
			return;
		}
		
		jConfirm( message_security.SO0003 , message_security.SO0004 ,function(){
			var param = {
				"ukey" : DMail
			};
			var async = true;
			var url = app.contextPath +"/security/json/hackingreport/deny/add.do";
			$.ajax({
				url: url ,
				data : param ,
				type : "POST",
				dataType:"json",
				async: async,
				error:function(xhr, txt){
					AjaxUtil.error( xhr );					
				},
				success:function( result ){
                    jAlert( result.message , function(){
                        if($("#allCheck").is(":checked")) {
                        	onSelectAll('allCheck');
                        } else {
                        	$("input:checkbox").each(function() {
                    			$(this).attr("checked", false);
                    		});
                        }
                    });
				}
			});
		});
	},
	// 목록저장
	listDownload : function() {
		var DMail = new Array();
		$("input[name='ukey[]']:checkbox:checked").each(function() {
			DMail.push($(this).attr("value"));
		});
		
		if (DMail.length == 0) {
			jAlert(message_security.SO0007);
			return;
		}
		
		var param = {
			"ukey" : DMail
		};
		
		jDownload( app.contextPath + "/security/hackingreport/list/download.do", "GET", param );
	},
	viewComment : function(obj) {
		var comment = $(obj).parent().find("textarea#comment").val();
		$("#commentView #viewArea").html(comment);		
		$("#commentView").dialog('open');
	},
	doMailListFormReset : function() {
		$("#HackingMailListForm input#cmd").val("");
		$("#HackingMailListForm input#searchSelect").val("");
		$("#HackingMailListForm input#searchText").val("");
		$("#HackingMailListForm input#searchSdate").val("");
		$("#HackingMailListForm input#searchEdate").val("");
		$("#HackingMailListForm input#searchState").val("");
		$("#HackingMailListForm input#searchMailtype").val("");
		$("#HackingMailListForm input#cpage").val("1");
	},
	sort : function(sort) {
		var order = "desc";
		
		var currentSort = $("#HackingMailListForm input#sort").val();
		if ( currentSort == sort ) {
			if( $("#order_"+ sort ).hasClass("desc") ){
				order = "asc";
			}
		}
		
		$("#HackingMailListForm input#sort").val( sort );
		$("#HackingMailListForm input#order").val( order );
		$("#HackingMailListForm input#cpage").val(1);
		
		this.doMailListFormSubmit();
	}
};

// 보안설정 > 해킹메일 관리함
var hackingmail_manager = {
	doMailListFormReset : function() {
		$("#HackingMailListForm input#ukey").val("");
		$("#HackingMailListForm input#mhost").val("");
		$("#HackingMailListForm input#userid").val("");
		
		$("#HackingMailListForm input#cmd").val("");
		$("#HackingMailListForm input#searchSelect").val("");
		$("#HackingMailListForm input#searchText").val("");
		$("#HackingMailListForm input#searchSdate").val("");
		$("#HackingMailListForm input#searchEdate").val("");
		$("#HackingMailListForm input#searchState").val("");
		$("#HackingMailListForm input#searchMailtype").val("");
		$("#HackingMailListForm input#cpage").val("1");
	},
	doKeyPress : function() {
		if (event.keyCode != 13) {
			return;
		}
		this.doSearch();
	},	
	doSearch : function() {
		var searchSelect = $.trim($("#HackingMailManagerForm select#searchSelect").val());
		var searchText = $.trim($("#HackingMailManagerForm input#searchText").val());
		$("#HackingMailManagerForm input#searchText").val(searchText);
		
		if (searchText == "") {
			jAlert(message_security.SO0001);
			return;
		}
		
		this.doMailListFormReset();	// 기존 검색 조건을 초기화 한다.
		
		$("#HackingMailListForm input#cmd").val("search");
		$("#HackingMailListForm input#searchSelect").val(searchSelect);
		$("#HackingMailListForm input#searchText").val(searchText);
		
		this.doMailListFormSubmit();
	},
	doList : function(cpage) {
		$("#HackingMailListForm input#cpage").val(cpage);
		this.doMailListFormSubmit();
	},
	doAllList : function() {
		this.doMailListFormReset();
		this.doMailListFormSubmit();
		
	},
	doDetailSearchOpen : function() {
		hackingmail_detailsearch.open();
	},
	doMailListFormSubmit : function() {
		$("#HackingMailListForm").attr("method", "get");
		$("#HackingMailListForm").attr("action", app.contextPath +"/security/hackingmanager/list.do");
		$("#HackingMailListForm").submit();
	},
	doView : function(obj) {
		var ukey = $(obj).parents("tr").data("key");
		var mhost = $(obj).parents("tr").data("mhost");
		var userid = $(obj).parents("tr").data("userid");
		
		$("#HackingMailListForm input#mhost").val(mhost);
		$("#HackingMailListForm input#userid").val(userid);
		$("#HackingMailListForm input#ukey").val(ukey);		
		
		$("#HackingMailListForm").attr("method", "get");
		$("#HackingMailListForm").attr("action", app.contextPath +"/security/hackingmanager/view.do");
		$("#HackingMailListForm").submit();
	},
	// 수신거부
	deny : function() {
		var DMail = new Array();
		$("input[name='ukey[]']:checkbox:checked").each(function() {
			var ukey = $(this).parents("tr").data("key");
			var mhost = $(this).parents("tr").data("mhost");
			var userid = $(this).parents("tr").data("userid");
			var mailid = $(this).parents("tr").data("mailid");
			
			var val = mhost +"||"+ userid +"||"+ ukey +"||"+ mailid;
			DMail.push(val);
		});
		
		if (DMail.length == 0) {
			jAlert(message_security.SO0002);
			return;
		}
		
		jConfirm( message_security.SO0003 , message_security.SO0004 ,function(){
			var param = {
				"ukey" : DMail
			};
			var async = true;
			var url = app.contextPath +"/security/json/hackingmanager/deny/add.do";
			$.ajax({
				url: url ,
				data : param ,
				type : "POST",
				dataType:"json",
				async: async,
				error:function(xhr, txt){
					AjaxUtil.error( xhr );
				},
				success:function( result ){
                    jAlert( result.message , function(){
                        if($("#allCheck").is(":checked")) {
                        	onSelectAll('allCheck');
                        } else {
                        	$("input:checkbox").each(function() {
                    			$(this).attr("checked", false);
                    		});
                        }
                    });
				}
			});
		});
	},
	// 목록저장
	listDownload : function() {
		var DMail = new Array();
		$("input[name='ukey[]']:checkbox:checked").each(function() {
			var ukey = $(this).parents("tr").data("key");
			
			DMail.push(ukey);
		});
		
		if (DMail.length == 0) {
			jAlert(message_security.SO0007);
			return;
		}
		
		var param = {
			"ukey" : DMail
		};
		
		jDownload( app.contextPath + "/security/hackingmanager/list/download.do", "GET", param );
	},
	// 원문 저장 > 개별 다운로드
	doEmlDownload : function(obj) {
		var ukey = $(obj).parents("tr").data("key");
		var mhost = $(obj).parents("tr").data("mhost");
		var userid = $(obj).parents("tr").data("userid");
		
		var param = {
			"mhost" : mhost,
			"userid" : userid,
			"ukey" : ukey
		};
		
		jDownload( app.contextPath + "/security/hackingmanager/mail/download.do", "GET", param );
	},
	sort : function(sort) {
		var order = "desc";
		
		var currentSort = $("#HackingMailListForm input#sort").val();
		if ( currentSort == sort ) {
			if( $("#order_"+ sort ).hasClass("desc") ){
				order = "asc";
			}
		}
		
		$("#HackingMailListForm input#sort").val( sort );
		$("#HackingMailListForm input#order").val( order );
		$("#HackingMailListForm input#cpage").val(1);
		
		this.doMailListFormSubmit();
	}
};

//보안설정 > 해킹메일 신고, 관리함 > 상세검색
var hackingmail_detailsearch = {
	form : "",

	init : function() {
		if ($("#HackingMailManagerForm").length == 0 && $("#HackingMailReportForm").length > 0) {
			this.form = "HackingMailReportForm";
		} else {
			this.form = "HackingMailManagerForm";
		}
		
	},

	initDatePicker : function() {
		$('.search_term').datepicker({
			showOn : focus(),
			changeMonth : true,
			changeYear : true,
			dayNames : [ '', '', '', '', '', '', '' ],
			dayNamesMin : [ msg.get(message_common.CM0018),	msg.get(message_common.CM0019),	msg.get(message_common.CM0020),	msg.get(message_common.CM0021),	msg.get(message_common.CM0022),	msg.get(message_common.CM0023),	msg.get(message_common.CM0024) ],
			dayNamesShort : [ msg.get(message_common.CM0018),	msg.get(message_common.CM0019),	msg.get(message_common.CM0020),	msg.get(message_common.CM0021),	msg.get(message_common.CM0022),	msg.get(message_common.CM0023),	msg.get(message_common.CM0024) ],
			monthNames : [ '', '', '', '', '', '', '', '', '', '', '', '' ],
			monthNamesShort : [ '01', '02', '03', '04', '05', '06', '07', '08', '09', '10','11', '12' ],
			dateFormat : 'yymmdd',
			maxDate:'+0d',			// 종료일
			options:{
			   disabled:true
			}
		});
	},
	// 상세검색 창 OPEN
	open : function() {
		if ($("#detail_search").is(":visible")) {
			this.close();    
		} else {
			this.initDatePicker();
			$("#"+ this.form +" select#searchSelect").attr("disabled", true);
			$("#"+ this.form +" input#searchText").attr("disabled", true);
			$("#"+ this.form +" button#search").attr("disabled", true);
			$("#detail_search").show();
		}
	},
	// 상세검색 창 CLOSE
	close : function() {
		$("#"+ this.form +" select#searchSelect").attr("disabled", false);
		$("#"+ this.form +" input#searchText").attr("disabled", false);
		$("#"+ this.form +" button#search").attr("disabled", false);
		$("#detail_search").hide();
	},
	term : function(obj) {
		var term_type = $(obj).val();
	
		// 설정한 날짜를 텍스트 입력창에 출력
		if (term_type == 'S' || term_type == 'A') {
			$("#"+ this.form +" input#sdate").val("");
			$("#"+ this.form +" input#edate").val("");
			if (term_type == 'A') {
				$("#"+ this.form +" .search_term").datepicker("option",{disabled:true});
			} else {
				$("#"+ this.form +" .search_term").datepicker("option",{disabled:false});
			}
		} else {
			var startdate = new Date();
			var enddate = new Date();
			// 설정한 일자 만큼 뒤로 이동
			var processTime = startdate.getTime() - (parseInt(term_type) * 24 * 60 * 60 * 1000);
			if (processTime != null) {
				startdate.setTime(processTime);
				var sd = startdate.format("yyyymmdd");
				var ed = enddate.format("yyyymmdd");
				if (sd != null && ed != null) {
					$("#"+ this.form +" input#sdate").val(sd);
					$("#"+ this.form +" input#edate").val(ed);
				}
			}
			
			$("#"+ this.form +" .search_term").datepicker("option",{disabled:true});
		}
	},
	search : function() {
		// 일반검색 데이터 초기화
		if (this.form == "HackingMailReportForm") {
			hackingmail_report.doMailListFormReset(); // 기존 검색 조건을 초기화 한다.
		} else {
			hackingmail_manager.doMailListFormReset(); // 기존 검색 조건을 초기화 한다.
		}		
		
		var searchSelect = $.trim($("#"+ this.form +" select#searchDetailSelect").val());
		var searchText = $.trim($("#"+ this.form +" input#searchDetailText").val());
		var sdate = $.trim($("#"+ this.form +" input#sdate").val());
		var edate = $.trim($("#"+ this.form +" input#edate").val());
		var searchState = $.trim($("#"+ this.form +" select#searchState").val());
		var searchMailtype = $.trim($("#"+ this.form +" select#searchMailtype").val());
		
		if (sdate != '' || edate != '') {
			if (sdate == '') {
				jAlert(message_security.SO0009);
				$("#"+ this.form +" input#sdate").focus();
				return;
			}
			
			if (edate == '') {
				jAlert(message_security.SO0009);
				$("#"+ this.form +" input#edate").focus();
				return;
			}
			
			if (sdate > edate) {
				jAlert(message_security.SO0008);
				$("#"+ this.form +" input#sdate").focus();
				return;
			}
		}
		
		$("#HackingMailListForm input#cmd").val("detailsearch");
		$("#HackingMailListForm input#searchSelect").val(searchSelect);
		$("#HackingMailListForm input#searchText").val(searchText);
		$("#HackingMailListForm input#searchSdate").val(sdate);
		$("#HackingMailListForm input#searchEdate").val(edate);
		$("#HackingMailListForm input#searchState").val(searchState);
		$("#HackingMailListForm input#searchMailtype").val(searchMailtype);
		
		if (this.form == "HackingMailReportForm") {
			hackingmail_report.doMailListFormSubmit();
		} else {
			hackingmail_manager.doMailListFormSubmit();
		}		
	},
	doAllList : function() {
		this.close();
		
		if (this.form == "HackingMailReportForm") {
			hackingmail_report.doAllList();
		} else {
			hackingmail_manager.doAllList();
		}		
	}
};

// 보안설정 > 해킹메일 관리함 > 메일 상세 보기 및 보안담당자 해킹메일 처리
var hackingmail_view = {
	goToList : function() {
		hackingmail_manager.doMailListFormSubmit();
	},
	viewHeader : function() {
		if ($("#report_mail_header").css("display") == 'none') {
			$("#report_mail_header").show();
			$("#view_header").attr("title", message_security.SO0011);
		} else {
			$("#report_mail_header").hide();
			$("#view_header").attr("title", message_security.SO0010);
		}
	},
	doSave : function() {
		var comment_limit = 1000;
		
		var state = $("#HackingMailForm input:radio[name=sel_state]:checked").val();
		var mailtype = $("#HackingMailForm input:radio[name=sel_mailtype]:checked").val();		
		var comment = $("#HackingMailForm textarea[name='comment']").val();
		
		if (state == "3") {	// 처리완료
			if (mailtype == null || typeof(mailtype) == 'undefined' || mailtype == "") {
				jAlert( message_security.SO0013 );
				return;
			}
			
			if (comment == '') {
				jAlert( message_security.SO0014, function() {
					$("#HackingMailForm textarea[name='comment']").focus();
				});
				
				return;
			} else if (comment.length > comment_limit) {
				jAlert( msg.get( message_security.SO0012, comment_limit ), function() {
					$("#HackingMailForm textarea[name='comment']").focus();
				});
				return;
			}
			
			jConfirm( message_security.SO0015 , message_security.SO0016 ,function(){
				$("#HackingMailForm input#cmd").val('S');
				$("#HackingMailForm input#state").val(state);
				$("#HackingMailForm input#mailtype").val(mailtype);
				
				hackingmail_view.callSaveProcess();
			});
			
		} else {	// 접수 또는 처리중
			if (mailtype == null || typeof(mailtype) == 'undefined' || mailtype == "") {
				mailtype = "0";
			}
			
			jConfirm( message_security.SO0017 , message_security.SO0018 ,function() {
				$("#HackingMailForm input#cmd").val('U');
				$("#HackingMailForm input#state").val(state);
				$("#HackingMailForm input#mailtype").val(mailtype);
				
				hackingmail_view.callSaveProcess();
			});
		}
	},
	callSaveProcess : function() {
		var url = app.contextPath +"/security/json/hackingmanager/result/save.do";
		
		$.ajax({
			url: url ,
			data : $("#HackingMailForm").serialize(),
			type : "POST",
			dataType:"json",
			async: browser.type == browser.IE && browser.version > 6 ? true : false,
			error:function(xhr, txt){
				AjaxUtil.error( xhr );
			},
			success:function( result ){
                jAlert( result.message , function(){
                	//hackingmail_view.goToList();
                });
			}
		});
	},
	doSendMail : function() {
		var state = $("#HackingMailForm input:radio[name=sel_state]:checked").val();
		
		if (state != "3") {
			jAlert(message_security.SO0019);
			return;
		}
		
		jConfirm( message_security.SO0020 , message_security.SO0021 ,function(){
			var url = app.contextPath +"/security/json/hackingmanager/result/send.do";
			
			var param = {
				"mhost" : $("#HackingMailForm input#mhost").val(),
				"userid" : $("#HackingMailForm input#userid").val(),
				"ukey" : $("#HackingMailForm input#ukey").val()
			};
			
			$.ajax({
				url: url ,
				data : param,
				type : "POST",
				dataType:"json",
				async: browser.type == browser.IE && browser.version > 6 ? true : false,
				error:function(xhr, txt){
					AjaxUtil.error( xhr );
				},
				success:function( result ){
	                jAlert( result.message , function(){
	                	//hackingmail_view.goToList();
	                });
				}
			});
		});
	},
	addFilter : function() {
		$("#BlockMailViewForm input#add_type").val(block_mailview.REGTYPE__HACKINGMAIL_INPUT);
		$("#BlockMailViewLayer").dialog('open');
	},
};

// 보안설정 > 수신확인 코드 조회
var receipt_log = {		
	initDatePicker : function() {
		$("#date").datepicker({
			showOn : focus(),
			changeMonth : true,
			changeYear : true,
			dayNames : [ '', '', '', '', '', '', '' ],
			dayNamesMin : [ msg.get(message_common.CM0018),	msg.get(message_common.CM0019),	msg.get(message_common.CM0020),	msg.get(message_common.CM0021),	msg.get(message_common.CM0022),	msg.get(message_common.CM0023),	msg.get(message_common.CM0024) ],
			dayNamesShort : [ msg.get(message_common.CM0018),	msg.get(message_common.CM0019),	msg.get(message_common.CM0020),	msg.get(message_common.CM0021),	msg.get(message_common.CM0022),	msg.get(message_common.CM0023),	msg.get(message_common.CM0024) ],
			monthNames : [ '', '', '', '', '', '', '', '', '', '', '', '' ],
			monthNamesShort : [ '01', '02', '03', '04', '05', '06', '07', '08', '09', '10','11', '12' ],
			dateFormat : 'yy-mm-dd',
			maxDate:'+0d',			// 종료일
			options:{
			   disabled:true
			}
		});
	},		
	init : function() {
		$("#receiptInfoView").dialog({
			bgiframe : false,
            autoOpen : false,
            height : 450,
            width : 500,
            modal : true,
            title : message_security.SO0025,
            buttons : [
                {
                    text : msg.get(message_security.SO0006),
                    click : function() {
                    	$("#receiptInfoView").dialog('close');
                    }
                } ]
		});
		
		this.initDatePicker();
	},
	doKeyPress : function() {
		if (event.keyCode != 13) {
			return;
		}
		this.doSearch(1);
	},
	doSearch : function(cpage) {
		if (cpage == null || cpage == '') {
			cpage = 1;
		}
		
		var date = $.trim($("#ReceiptLogForm input#date").val());
		var searchKeyword = $.trim($("#ReceiptLogForm input#searchKeyword").val());
		
		if (date == "") {
			jAlert(message_security.SO0024);
			return;
		}
		if (searchKeyword == "") {
			jAlert(message_security.SO0023);
			return;
		}
		
		$("#ReceiptLogForm input#searchKeyword").val(searchKeyword);
		$("#ReceiptLogForm input#cpage").val(cpage);
		
		this.execSearch();
	},	
	execSearch : function() {
		var url = app.contextPath +"/security/json/receipt/log/search.do";
		
		$.ajax({
			url: url ,
			data : $("#ReceiptLogForm").serialize(),
			type : "get",
			dataType:"json",
			async: browser.type == browser.IE && browser.version > 6 ? true : false,
			error:function(xhr, txt){
				AjaxUtil.error( xhr );
			},
			success:function( result ){
				if (result == null || result.data == null || result.data.length == 0) {
					receipt_log.drawNoLogTable();
				} else {
					receipt_log.drawLogTable(result);
				}				
			}
		});
	},	
	drawNoLogTable:function(){
    	// 페이징
        $("#pagenation").html("");
        
        $("#tbody_logList").empty();       
        $("#tbody_logList").hide();
        $("#tbody_noData").show();
        
    },    
	drawLogTable : function(result) {
		var dataList = result.data;
		var page = result.pageInfo;
		
		var item = [];
		for (var i = 0; i < dataList.length; i++) {
			item[i] = this.make_logdata(dataList[i]);
		}
		
		var pageHtml = pageInfo(page.cpage, page.pageSize, page.total, '', 'receipt_log.doSearch');
		$("#pagenation").html(pageHtml);
		
		$("#tbody_noData").hide();        
        $("#tbody_logList").empty();
        $("#tbody_logList").append(item.join(''));
        $("#tbody_logList").show();
	},	
	make_logdata : function(data) {
		var strList = [], n = -1;
		
		var subject = util.escapeXml(data.SUBJECT);
		var recv_info = util.escapeXml(data.RECV_INFO);
		
		var receipt_result = message_common.CM0013;
		if (data.RESULT == "SUCCESS") {
			receipt_result = message_common.CM0012;
		}
		
		var keyInfo = "data-date=\""+ data.DATE +"\" data-ip=\""+ data.IP +"\" ";
		keyInfo += 	"data-mhost=\""+ data.MHOST +"\" data-userid=\""+ data.USERID +"\" data-mailid=\""+ data.MAILID +"\" ";
		keyInfo += "data-ukey=\""+ data.UKEY +"\" data-recvinfo=\""+ recv_info +"\" data-senddate=\""+ data.SENDDATE +"\" data-subject=\""+ subject +"\" ";
		keyInfo += "data-param=\""+ data.PARAM +"\" data-result=\""+ receipt_result +"\" data-desc=\""+ data.DESCRIPTION +"\" ";
		
		strList[++n] = "<tr "+ keyInfo +" onClick=\"receipt_log.detailInfo(this);\">\n";
		strList[++n] = "<td><span class=\"txt\">"+ data.DATE +"</span></td>";
		strList[++n] = "<td><span class=\"txt\">"+ data.IP +"</span></td>";
		strList[++n] = "<td><span class=\"txt\">"+ data.MAILID +"</span></td>";		
		strList[++n] = "<td><span class=\"txt\">"+ subject +"</span></td>";
		strList[++n] = "<td><span class=\"txt\">"+ recv_info +"</span></td>";		
		strList[++n] = "<td><span class=\"txt\">"+ receipt_result +"</span></td>";
		strList[++n] = "</tr>\n";		
		
		return strList.join('');
	},	
	detailInfo : function(obj) {
		var ukey = $(obj).data("ukey");
		
		$("#receiptInfoView #date").html($(obj).data("date"));
		$("#receiptInfoView #ip").html($(obj).data("ip"));
		$("#receiptInfoView #mhost").html($(obj).data("mhost"));
		$("#receiptInfoView #userid").html($(obj).data("userid"));
		$("#receiptInfoView #mailid").html($(obj).data("mailid"));
		$("#receiptInfoView #ukey").html($(obj).data("ukey"));
		$("#receiptInfoView #recv_info").html(util.escapeXml($(obj).data("recvinfo")));
		$("#receiptInfoView #senddate").html($(obj).data("senddate"));
		$("#receiptInfoView #subject").html(util.escapeXml($(obj).data("subject")));
		$("#receiptInfoView #param").html($(obj).data("param"));
		$("#receiptInfoView #result").html($(obj).data("result"));
		
		$("#receiptInfoView").dialog('open');
	},
	
};

//보안설정 > 열람차단 필터 관리
var block_mailview = {
	REGTYPE__NO_INFO : "0",						// 정보 없음
	REGTYPE__DIRECT_INPUT : "1",				// 직접입력
	REGTYPE__HACKINGMAIL_INPUT : "2",			// 해킹메일 관리함
	REGTYPE__SEARCHSIMILARMAIL_INPUT : "3",		// 유사메일 검색

	initDialog : function() {
		$("#BlockMailViewLayer").dialog({
			bgiframe : false,
            autoOpen : false,
            height : 250,
            width : 600,
            modal : true,
            title : message_common.CM0070,
            buttons : [
            	{
                    text : msg.get(message_common.CM0069),
                    click : function() {
                    	block_mailview.execFilter();
                    }
                },
                {
                    text : msg.get(message_common.CM0028),
                    click : function() {
                    	$("#BlockMailViewLayer").dialog('close');
                    }
                } 
            ]
		});
	},
	doKeyPress : function() {
		if (event.keyCode != 13) {
			return;
		}
		
		this.doSearch();
	},
	doSearch : function() {
		var searchText = $.trim($("#BlockMailViewListForm input#searchText").val());
		$("#BlockMailViewListForm input#searchText").val(searchText);
		
		if (searchText == "") {
			jAlert(message_security.SO0001, function() {
				$("#denyListForm input#searchText").focus();
			});
			
			return;
		}
				
		this.doList(1);
	},
	doAllList : function() {
		location.href = app.contextPath +"/security/block/mailview/list.do";
	},
	doList : function(cpage) {
		$("#BlockMailViewListForm input#cpage").val(cpage);
		
		$("#BlockMailViewListForm").attr("method", "get");
		$("#BlockMailViewListForm").attr("action", app.contextPath +"/security/block/mailview/list.do");
		
		document.BlockMailViewListForm.submit();
	},
	doBlockMailViewFormReset : function() {
		$("#BlockMailViewForm input#cmd").val("");
		$("#BlockMailViewForm input#ukey").val("");
		
		$("#BlockMailViewForm input#target_ip").val("");
		$("#BlockMailViewForm input#target_from").val("");
		$("#BlockMailViewForm input#target_subject").val("");
		
		$("#BlockMailViewForm select#cond_ip").val("0");
		$("#BlockMailViewForm select#cond_from").val("0");
		$("#BlockMailViewForm select#cond_subject").val("0");
		
		$("#BlockMailViewForm span#target_ip_err").html("");
		$("#BlockMailViewForm span#target_from_err").html("");
		$("#BlockMailViewForm span#target_subject_err").html("");
		
	},
	addFilter : function() {
		this.doBlockMailViewFormReset();
		
		$("#BlockMailViewLayer table").attr("summary", message_security.SO0026);
		$("#BlockMailViewLayer caption").html(message_security.SO0026);
		
		$("#BlockMailViewForm input#cmd").val("ADD");
		$("#BlockMailViewForm input#add_type").val(this.REGTYPE__DIRECT_INPUT);
		
		$("#BlockMailViewLayer").dialog('open');
	},
	editFilter : function(obj) {
		this.doBlockMailViewFormReset();
		
		$("#BlockMailViewLayer table").attr("summary", message_security.SO0027);
		$("#BlockMailViewLayer caption").html(message_security.SO0027);
		
		$("#BlockMailViewForm input#cmd").val("MOD");
		$("#BlockMailViewForm input#ukey").val($(obj).parents("tr").data("key"));
				
		$("#BlockMailViewForm input#target_ip").val($(obj).parents("tr").data("ip"));
		$("#BlockMailViewForm input#target_from").val($(obj).parents("tr").data("from"));
		$("#BlockMailViewForm input#target_subject").val($(obj).parents("tr").data("subject"));
		
		$("#BlockMailViewForm select#cond_ip").val($(obj).parents("tr").data("cond_ip"));
		$("#BlockMailViewForm select#cond_from").val($(obj).parents("tr").data("cond_from"));
		$("#BlockMailViewForm select#cond_subject").val($(obj).parents("tr").data("cond_subject"));
		
		$("#BlockMailViewLayer").dialog('open');
	},
	execFilter : function() {
		var target_ip = $.trim($("#BlockMailViewForm input#target_ip").val());
		var target_from = $.trim($("#BlockMailViewForm input#target_from").val());
		var target_subject = $.trim($("#BlockMailViewForm input#target_subject").val());
		
		var cond_ip = $.trim($("#BlockMailViewForm select#cond_ip").val());
		var cond_from = $.trim($("#BlockMailViewForm select#cond_from").val());
		var cond_subject = $.trim($("#BlockMailViewForm select#cond_subject").val());
		
		if (target_ip == "" && target_from == "" && target_subject == "") {
			jAlert(message_security.SO0029, function() {
				$("#BlockMailViewForm input#target_ip").focus();
			});
			
			return;
		}
		
		if (target_ip != "" && cond_ip == "0") {
			jAlert(message_security.SO0030, function() {
				$("#BlockMailViewForm select#cond_ip").focus();
			});
			return;
		}
		
		if (target_from != "" && cond_from == "0") {
			jAlert(message_security.SO0030, function() {
				$("#BlockMailViewForm select#cond_from").focus();
			});
			return;
		}
		
		if (target_subject != "" && cond_subject == "0") {
			jAlert(message_security.SO0030, function() {
				$("#BlockMailViewForm select#cond_subject").focus();
			});
			return;
		}		
		
		var url = app.contextPath +"/security/json/block/mailview/add.do";
		if ($("#BlockMailViewForm input#cmd").val() == "MOD") {
			url = app.contextPath +"/security/json/block/mailview/modify.do";
		}
		
		$.ajax({
			url: url ,
			data : $("#BlockMailViewForm").serialize(),
			type : "post",
			dataType:"json",
			async: browser.type == browser.IE && browser.version > 6 ? true : false,
			error:function(xhr, txt){
				AjaxUtil.error( xhr );
			},
			success:function( result ) {
				if ( result.code == JSONResult.SUCCESS ) {
					jAlert( result.message, function() {
						if (location.href.indexOf("/security/block/mailview/list.do") > -1) {
							location.reload();
						} else {
							$("#BlockMailViewLayer").dialog('close');
						}
					});                    
                } else {
                    jAlert( result.message );
                }
			}
		});		
	},
	delFilter : function(obj) {
		var ukeyList = new Array();
		
		if (typeof obj == 'undefined') {
			$("input[name='ukey[]']:checkbox:checked").each(function() {
				ukeyList.push($(this).attr("value"));
			});
		} else {
			ukeyList.push($(obj).parents("tr").data("key"));
		}
		
		if (ukeyList.length == 0) {
			jAlert(message_security.SO0031);
			return;
		}		
		
		jConfirm( message_common.CM0027 , message_security.SO0032 ,function(){
			var url = app.contextPath +"/security/json/block/mailview/delete.do";
			var param = {
				"ukeys" : ukeyList
			}
			
			$.ajax({
				url: url ,
				data : param,
				type : "post",
				dataType:"json",
				async: browser.type == browser.IE && browser.version > 6 ? true : false,
				error:function(xhr, txt){
					AjaxUtil.error( xhr );
				},
				success:function( result ) {
					if ( result.code == JSONResult.SUCCESS ) {
						jAlert( result.message, function() {
							location.reload();
						});
						
	                } else {
	                    jAlert( result.message );
	                }
				}
			});
		});
	}	
};

// 보안설정 > 유사메일 검색
var similarmail = {
	initDatePicker : function() {
		$("#sdate").datepicker({
			showOn : focus(),
			changeMonth : true,
			changeYear : true,
			dayNames : [ '', '', '', '', '', '', '' ],
			dayNamesMin : [ msg.get(message_common.CM0018),	msg.get(message_common.CM0019),	msg.get(message_common.CM0020),	msg.get(message_common.CM0021),	msg.get(message_common.CM0022),	msg.get(message_common.CM0023),	msg.get(message_common.CM0024) ],
			dayNamesShort : [ msg.get(message_common.CM0018),	msg.get(message_common.CM0019),	msg.get(message_common.CM0020),	msg.get(message_common.CM0021),	msg.get(message_common.CM0022),	msg.get(message_common.CM0023),	msg.get(message_common.CM0024) ],
			monthNames : [ '', '', '', '', '', '', '', '', '', '', '', '' ],
			monthNamesShort : [ '01', '02', '03', '04', '05', '06', '07', '08', '09', '10','11', '12' ],
			dateFormat : 'yymmdd',
			maxDate:'+0d',			// 종료일
			options:{
			   disabled:true
			}
		});
		
		$("#edate").datepicker({
			showOn : focus(),
			changeMonth : true,
			changeYear : true,
			dayNames : [ '', '', '', '', '', '', '' ],
			dayNamesMin : [ msg.get(message_common.CM0018),	msg.get(message_common.CM0019),	msg.get(message_common.CM0020),	msg.get(message_common.CM0021),	msg.get(message_common.CM0022),	msg.get(message_common.CM0023),	msg.get(message_common.CM0024) ],
			dayNamesShort : [ msg.get(message_common.CM0018),	msg.get(message_common.CM0019),	msg.get(message_common.CM0020),	msg.get(message_common.CM0021),	msg.get(message_common.CM0022),	msg.get(message_common.CM0023),	msg.get(message_common.CM0024) ],
			monthNames : [ '', '', '', '', '', '', '', '', '', '', '', '' ],
			monthNamesShort : [ '01', '02', '03', '04', '05', '06', '07', '08', '09', '10','11', '12' ],
			dateFormat : 'yymmdd',
			maxDate:'+0d',			// 종료일
			options:{
			   disabled:true
			}
		});
	},	
	init : function() {
		this.initDatePicker();
	},
	doSimilarMailListFormReset : function() {
		$("#SimilarMailListForm input#sdate").val("");
		$("#SimilarMailListForm input#edate").val("");
		$("#SimilarMailListForm input#tbl_no").val("");
		$("#SimilarMailListForm input#fromaddr").val("");
		$("#SimilarMailListForm input#subject").val("");
		$("#SimilarMailListForm input#cpage").val("1");
	},
	doKeyPress : function() {
		if (event.keyCode != 13) {
			return;
		}
		
		this.doSearch();
	},
	doSearch : function() {
		var sdate = $.trim($("#SimilarMailSearchForm input#sdate").val());
		var edate = $.trim($("#SimilarMailSearchForm input#edate").val());
		var tbl_no = $.trim($("#SimilarMailSearchForm select[name='tbl_no']").val());
		var fromaddr = $.trim($("#SimilarMailSearchForm input#fromaddr").val());
		var subject = $.trim($("#SimilarMailSearchForm input#subject").val());
		
		if (sdate == '') {
			jAlert(message_security.SO0009);
			$("#SimilarMailSearchForm input#sdate").focus();
			return;
		}
		
		if (edate == '') {
			jAlert(message_security.SO0009);
			$("#SimilarMailSearchForm input#edate").focus();
			return;
		}
		
		if (sdate > edate) {
			jAlert(message_security.SO0008);
			$("#SimilarMailSearchForm input#sdate").focus();
			return;
		}
		
		if (fromaddr == "") {
			jAlert(message_security.SO0033, function() {
				$("#SimilarMailSearchForm input#fromaddr").focus();
			});
			
			return;
		}
		
		if (subject == "") {
			jAlert(message_security.SO0034, function() {
				$("#SimilarMailSearchForm input#subject").focus();
			});
			
			return;
		}
		
		this.doSimilarMailListFormReset();
		
		$("#SimilarMailListForm input#sdate").val(sdate);
		$("#SimilarMailListForm input#edate").val(edate);
		$("#SimilarMailListForm input#tbl_no").val(tbl_no);
		$("#SimilarMailListForm input#fromaddr").val(fromaddr);
		$("#SimilarMailListForm input#subject").val(subject);
		$("#SimilarMailListForm input#cpage").val("1");
		
		this.doList();
	},
	doPageList : function(cpage) {
		$("#SimilarMailListForm input#cpage").val(cpage);
		this.doList();
	},
	doList : function() {
		var url = app.contextPath +"/security/json/similarmail/search.do";
		
		$.ajax({
			url: url ,
			data : $("#SimilarMailListForm").serialize(),
			type : "get",
			dataType:"json",
			async: browser.type == browser.IE && browser.version > 6 ? true : false,
			error:function(xhr, txt){
				AjaxUtil.error( xhr );
			},
			success:function( result ){
				if (result == null || result.maillist == null || result.maillist.length == 0) {
					similarmail.drawNoLogTable();
				} else {
					similarmail.drawLogTable(result);
				}				
			}
		});		
	},
	drawNoLogTable:function(){
    	// 페이징
        $("#pagenation").html("");
        
        $("#tbody_logList").empty();       
        $("#tbody_logList").hide();
        $("#tbody_noData").show();
        
    },    
	drawLogTable : function(result) {
		var dataList = result.maillist;
		var page = result.pageInfo;
		
		var item = [];
		for (var i = 0; i < dataList.length; i++) {
			item[i] = this.make_logdata(dataList[i]);
		}
		
		var pageHtml = pageInfo(page.cpage, page.pageSize, page.total, '', 'similarmail.doPageList');
		$("#pagenation").html(pageHtml);
		
		$("#tbody_noData").hide();        
        $("#tbody_logList").empty();
        $("#tbody_logList").append(item.join(''));
        $("#tbody_logList").show();
	},	
	make_logdata : function(data) {
		var strList = [], n = -1;
		
		var subject = util.escapeXml(data.subject);
		
		strList[++n] = "<tr data-subject=\""+ subject +"\" data-from=\""+ data.fromaddr +"\" data-ip=\""+ data.fromip +"\">\n";
		strList[++n] = "<td><span class=\"txt\">"+ data.sender +"</span></td>";
		strList[++n] = "<td><span class=\"txt\">"+ data.to +"</span></td>";
		strList[++n] = "<td><a href=\"javascript:;\" onClick=\"similarmail.viewMail('"+ data.userid +"', '"+ data.ukey +"');\">";
		strList[++n] = "<span class=\"txt\">"+ subject +"</span>";
		strList[++n] = "</a></td>";
		strList[++n] = "<td><span class=\"txt\">"+ data.fromip +"</span></td>";
		strList[++n] = "<td><span class=\"txt\">"+ data.senddate +"</span></td>";		
		strList[++n] = "<td><span class=\"txt\">";
		strList[++n] = "<button type=\"button\" class=\"btn_bgtxt bk\" onclick=\"similarmail.addFilter(this)\">"+ message_security.SO0035+"</button>";
		strList[++n] = "</span></td>";
		strList[++n] = "</tr>\n";
		
		return strList.join('');
	},
	viewMail : function(userid, ukey) {
		var url = app.contextPath +"/security/similarmail/popup/view.do?userid="+ userid +"&ukey="+ ukey;
		
		var dummy = sensmail.dummy();		
		window.open( url , dummy ,'toolbar=no,width=900,height=700,directories=no,status=yes,scrollbars=yes,resizable=yes,menubar=no');		
	},
	resetAddFilter : function() {
		$("#BlockMailViewForm input#target_ip").val("");
		$("#BlockMailViewForm input#target_from").val("");
		$("#BlockMailViewForm input#target_subject").val("");
		
		$("#BlockMailViewForm select#cond_ip").val("2");
		$("#BlockMailViewForm select#cond_from").val("2");
		$("#BlockMailViewForm select#cond_subject").val("2");
	},
	addFilter : function(obj) {
		this.resetAddFilter();
		
		$("#BlockMailViewForm input#target_ip").val($(obj).parents("tr").data("ip"));
		$("#BlockMailViewForm input#target_from").val($(obj).parents("tr").data("from"));
		$("#BlockMailViewForm input#target_subject").val($(obj).parents("tr").data("subject"));
		
		$("#BlockMailViewForm input#add_type").val(block_mailview.REGTYPE__SEARCHSIMILARMAIL_INPUT);
		$("#BlockMailViewLayer").dialog('open');
	}
	
}

var similarmail_view = {
	init : function() {
		// 받는 사람,참조,숨은참조 글줄임 폈다 줄였다..토글 설정 : start
	    $("#toggle_view_to").click(function(){
	    	if( $(this).hasClass("not") ){
				$(this).removeClass("not");
				$(".recvlist").hide();
			}else{
				$(this).addClass("not");
				$(".recvlist").show();				
			}
	    });        	
		// 받는 사람,참조,숨은참조 글줄임 폈다 줄였다..토글 설정 : end
		
		// 헤더보기 토글
		$("#view_header").toggle(
			function(){
				$("#mail_header").show();				
			},
			function(){
				$("#mail_header").hide();				
			}			
		);
	},
	/**
	 * 첨부파일 다운로드
	 * @param depth
	 * @param attach_key
	 * @param mindex
	 * @param aindex
	 */
	attachDownload:function( mail_key , mindex , aindex ){
		var userid = $("#MailViewForm input#userid").val();
        var url = app.contextPath +"/security/attach/download.do?userid="+ userid +"&mail_key="+mail_key+"&mindex="+mindex+"&aindex="+aindex;
		jDownload( url );
		
	}		
}

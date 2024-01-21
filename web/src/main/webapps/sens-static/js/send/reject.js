var reject_list = {
	doImport: function () {
		window.open("/send/reject/doImport1.do", "FileImport", "height=300,width=700,scrollbars=no,left=100,top=100,resizable");
	},
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
		jDownload("/send/reject/sampleDownload.do");
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
			url: "/send/reject/import/preview.json",
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
	doPrev: function () {
		importForm.action = "reject/doImportPrev.do?";
		importForm.submit();
	},
	/**
	 * 파일로 가져오기 3단계 submit
	 */
	doInsertAddr: function () {

		if (importForm.name.value == "-1") {
			alert(message_sendManage.S0043);
			return;
		}
		if (importForm.email.value == "-1") {
			alert(message_sendManage.S0044);
			return;
		}

		// if(importForm.gkey.value == "-1"){
		// 	if (!$("#newGrpName").val()) {
		// 		alert(message_group.G0047);
		// 		return;
		// 	}
		// 	$("#gname").val($("#newGrpName").val());
		// }

		return importForm.submit();
	},
	pagesize:function(obj){
		$("#pagesize").val( obj.value );
		$("#rejectListForm").submit();
	},
	search:function(){
		$("#cpage").val('1');
		if ($("#srch_keyword").val() == "") {
			alert("검색어를 입력해주세요.");
			return;
		}
		$("#rejectListForm").submit();
	},
	list:function(para){
		$("#cpage").val(para);
		$("#rejectListForm").submit();
	},
	addForm:function(){
		$("#addRejectLayer").show();
	},
	viewAll:function(){
		var bview = document.getElementById("allview");
		bview.style.display = "none";
		location.href = "list.do";
	},


	deleteClick : function() {
		var selCount = ImFormUtils.selected_item_count("emails");
		if (selCount <= 0) {
			alert(message_sendManage.S0041);
			return;
		}
		var emails = [];
		$("input[name=emails]:checkbox:checked").each(function () {
			var t = $(this).attr("value");
			emails.push(t);
		});
		if(confirm("삭제하겠습니까?")){
			var param = {
				"emails[]":  emails
			};
			var url = "delete.json";

			$.ajax( {
				url : url,
				data : param,
				type : "POST",
				dataType : "json",
				async : false,
				success : function(data) {
					if(data.result){
						alert(data.message);
						location.reload();
					}else{
					}
				},
				error:function(xhr){
					AjaxUtil.error(xhr);
				}
			});

		}

	},
	close_editPopup:function() {
		$("#editRejectLayer").hide();
	},

	open_editPopup:function(email){

		$("#editRejectLayer").show();
		$("#edit_email").val(email);
		$("#ori_email").val(email);


	},


	/**
	 * 저장
	 */
	doSave: function () {

		if(confirm("저장하겠습니까?")){
			var param = $("#rejectListForm").serialize();
			var url = "/send/reject/save.do?" + param;
			jDownload(url);
		}

	},
	edit:function(){
		var email = $("#edit_email").val();
		var ori_email = $("#ori_email").val();



		var param = {
			"email":email,
			"ori_email":ori_email
		}

		if(email == ''){
			alert(message_sendManage.S0009);
			return;
		}
		if(!ImStringUtil.validateEmail(email)){
			alert(message_sendManage.S0008);
			$("#edit_email").focus();
			$("#ori_email").focus();
			return;
		}

		var url = "edit.json";
		$.ajax({
			url : url,
			type : "post",
			data : param,
			dataType : "json",
			async : false,
			success : function(data) {
				if(data.result){
					alert(data.message);
					location.reload();
				} else{
					alert(data.message);
				}
			}
		});

	},

	fileupload: function (){

		var strFileName = $("#file_upload").val();
		var nStringSize = $("#file_upload").val().length;

		if (nStringSize <= 0) {
			alert(message_sendManage.S0011);
			return false;
		}

		var strFileExt = strFileName.substring(nStringSize - 4, nStringSize);
		strFileExt = strFileExt.toLowerCase();


		if (strFileExt != ".csv" && strFileExt != ".txt") {
			alert(message_sendManage.S0010);
			$("#file_upload").val("");
			return false;
		}


		var form = $("#uploadForm");
		var formData = new FormData(form[0]);
		formData.append("file", $("#file_upload")[0].files[0]);
		$.ajax({
			url: 'uploadfile.json',
			processData: false,
			contentType: false,
			data: formData,
			type: 'POST',
			dataType : "json",
			async : false,
			success: function(data){
				if (data.result){
					alert(message_sendManage.S0005 +"[성공:"+data.success+"건 / 실패:"+data.fail+"건]");
					location.reload();
				}else{
					alert(data.message);
				}


			}
		});
	}

}

var reject_add = {

	emailChk:function(){

		var email = $("#email").val();
		if(email == ''){
			alert(message_sendManage.S0009);
			return false;
		}
		if(!ImStringUtil.validateEmail(email)){
			alert(message_sendManage.S0008);
			$("#email").focus();
			return false;
		}

		var param ={"email":email };

		var url =  "emailChk.json";
		$.ajax({
			url : url,
			type : "post",
			data : param,
			dataType : "json",
			cache : false,
			async : false,
			success : function(data) {
				if(!data.result){
					alert(message_sendManage.S0006);
					$("#email").focus();
					return;

				}else{
					reject_add.add();
				}
			},
			error : function(xhr, txt) {
				AjaxUtil.error( xhr );
			},
		});

	},
	add:function(){
		var param = {
			"email":  $("#email").val()
		};
		var url = "add.json";
		$.ajax( {
			url : url,
			data : param,
			type : "POST",
			dataType : "json",
			async : false,
			success : function(data) {
				if(data.result){
					alert(message_sendManage.S0007);
					reject_add.close();
					location.reload();
				}else{
					alert(data.message);
				}
			},
			error:function(xhr){
				AjaxUtil.error(xhr);
			}
		});
	},
	close:function(){
		$("#email").val('');
		$("#addRejectLayer").hide();

	}

}
;

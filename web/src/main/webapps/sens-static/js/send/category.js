$(document).ready(function(){
	$("#add_popup").hide();
	$("#edit_popup").hide();
});

var category = {
	/**
	 * 목록 이동
	 * */
	list:function(para){
		$("#cpage").val(para);
	    $("#categoryListForm").submit();
	},

	viewAll:function(){
		var bview = document.getElementById("allview");
		bview.style.display = "none";
		location.href = "list.do";
	},
	/**
	 * 검색
	 * */
	search: function () {
		$("#cpage").val('1');
		if ($("#srch_keyword").val() == "") {
			alert("검색어를 입력해주세요.");
			return;
		}
			$("#categoryListForm").submit();



	},
	/**
	 * 발송분류 추가
	 * */
	add:function(){
		var name = $("#add_name").val();
	
		if(!name){
			alert(message_sendManage.S0001);
			return ;
		}
		
		// 특수문자 체크
		if(check_name(name)){
			alert(message_common.CM0004);
			return;
		}
		
		var param = {
			"name":name
		};
		
		var url = "add.json";
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
	/**
	 * 발송분류 수정
	 * */
	edit:function(){
		var ukey = $("#edit_ukey").val();
		var name = $("#edit_name").val();
		var userid = $("#reg_userid").text();
		var ori_name = $("#ori_name").val();
		
		if(!name){
			alert(message_sendManage.S0001);
			return ;
		}
		
		// 특수문자 체크
		if(check_name(name)){
			alert(message_common.CM0004);
			return;
		}
		
		var param = {
			"name":name,
			"ukey":ukey,
			"userid":userid,
			"ori_name":ori_name
		};
		
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
	/**
	 * 발송분류 삭제
	 * */
	deleteClick : function() {
		var selCount = 0;
		var ukeys = [];
		$("input[name=ukeys]:checked").each(function(idx){
			ukeys.push($(this).val());
			selCount++;
		});
		
		if (selCount <= 0) {
			alert(message_sendManage.S0041);
			return;
		}
		
		if(confirm(message_sendManage.S0003)){
			var param = {
				"ukeys[]":ukeys
			};
			
			var url = "delete.json";
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
		}
	},
	/**
	 * 추가 팝업창 열기
	 * */
	open_addPopup : function(){
		$("#add_name").val("");
		$("#add_popup").show();
	},
	/**
	 * 수정 팝업창 열기
	 * */
	open_editPopup : function(ukey,name,userid){
		$("#reg_userid").text(userid);
		$("#edit_name").val(name);
		$("#edit_ukey").val(ukey);
		$("#ori_name").val(name);
				
		$("#edit_popup").show();
	},
	/**
	 * 팝업창 닫기
	 * */
	close_popup : function(type){
		$("#"+type+"_popup").hide();
	}
}

/**
 * 특수문자 체크
 * - _ , . 만 허용
 * */
function check_name(str){
	var pattern = /[\{\}\[\]\/?;:|\)*~`!^+<>@\#$%&\\\=\(\'\"]/gi;
	if(pattern.test(str)){
		return true;
	} else{
		return false;
	}
}
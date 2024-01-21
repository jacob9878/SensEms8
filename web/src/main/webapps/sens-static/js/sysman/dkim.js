var dkimList={
	pagesize:function(obj){
		$("#pagesize").val( obj.value );
		$("#dkimListForm").submit();
	},
	search:function(){
		$("#cpage").val('1');
		if ($("#srch_keyword").val() == "") {
			alert("검색어를 입력해주세요.");
			return;
		}
		$("#dkimListForm").submit();
	},


	viewAll:function(){
		var bview = document.getElementById("allview");
		bview.style.display = "none";
		location.href = "/sysman/dkim/list.do";
	},

	del:function(){
		// 	   if( !confirm( "DKIM 도메인 키를 삭제하시겠습니까?"  ) ){
	    //     return;
	    // }
	    // var param = {
	    //     "domain":domain
	    // };
	    // $.get( "/config/dkim/del.json", param, function (result) {
		// 	alert (result.resultMsg);
	    //     if (result.code == "1") {
	    //         location.reload();
	    //     }
	    // }, "json");
		var selCount = ImFormUtils.selected_item_count("dkim");
		if (selCount <= 0) {
			alert(message_sysman.O0050); // 선택된 항목이 없습니다.
			return;
		}
		var dkim = [];
		$("input[name=dkim]:checkbox:checked").each(function () {
			var t = $(this).attr("value");
			dkim.push(t);
		});
		if(confirm("삭제하겠습니까?")){
			var param = {
				"dkim[]":  dkim
			};
			var url = "delete.json";

			$.ajax( {
				url : url,
				data : param,
				dataType : "json",
				type : "POST",
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
	list:function(para){
		$("#cpage").val(para);
		$("#dkimListForm").submit();
	},
	add:function(){
		if($("#domain").val() == ''){
			alert(bundle.getString("14"));
			return;
		}
		location.href= "add.do?cpage="+$("#cpage").val();
	},
	active:function(domain,use_sign){
		var param = {
			"domain":domain,
			"use_sign":use_sign
		};
		var url =  "use.json";
		$.ajax({
			url : url,
			type : "get",
			data : param,
			dataType : "json",
			cache : false,
			async : false,
			success : function(result) {
				if(result.code==JSONResult.SUCCESS) {
					var tagName = domain + "_use";
					if (use_sign == "1") {
						tagName = tagName + "_on";
						alert(result.message);
					} else {
						tagName = tagName + "_off";
						alert(result.message);
					}

					$('#' + tagName).prop('checked', true);
					return;

				}else {
					alert(result.message);
				}
			},
			error : function(xhr, txt) {
				AjaxUtil.error( xhr );
			},
		});
	}
}
var dkimAdd={
		confirm:function(){
			if($("#domain").val() == ''){
				alert(message_sysman.O0051);
				return;
			}

			$("#dkimForm").submit();
		},

		list:function(){
			location.href="list.do?cpage="+$("#cpage").val()+"&srch_keyword="+$("#srch_key").val();
		}
};

var dkimView={
	list:function(){
		location.href="list.do?cpage="+$("#cpage").val()+"&srch_keyword="+$("#srch_keyword").val();
		}
}



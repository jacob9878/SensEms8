$(document).ready(function(){
	$("#password").focus();
});
function update(){
	var userid = $.trim( $("#userid").val() );
	var password = $.trim( $("#ImaUserinfo input[name='password']").val() );	
	var new_password = $.trim( $("#ImaUserinfo input[name='new_password']").val() );
	var new_password1 = $.trim( $("#ImaUserinfo input[name='new_password1']").val() );
	
	if( password.length == 0 ){
		alert( bundle.getString("3") );
		$("#ImaUserinfo input[name='password']").focus();
		return;
	}
	if( new_password.length == 0 ){
		alert( bundle.getString("4") );
		$("#ImaUserinfo input[name='new_password']").focus();
		return;
	}
	if( new_password1.length == 0 ){
		alert(bundle.getString("5"));
		$("#ImaUserinfo input[name='new_password1']").focus();
		return;
	}
	if( !common.fnCheckPassword( userid , new_password ) ){		
		$("#ImaUserinfo input[name='new_password']").focus();
		return;
	}
	if( new_password != new_password1 ){
		alert( bundle.getString("6") );
		$("#ImaUserinfo input[name='new_password1']").focus();
		return;
	}
	var url = "/account/myinfo.do";
	var param = {
		"isAdmin" : $("#ImaUserinfo input[name='isAdmin']").val() ,
		"password" : password ,
		"new_password" : new_password
	};
	$.ajax({
		url: url ,
		data : param ,
		type : "POST",
		cache : false ,
		dataType:"json",
		async:true,
		timeout:15000,
		error:function(xhr){
			alert( xhr.status );
		},
		success:function(responseData){
			if( responseData.result ){
				alert( bundle.getString("111") );
				window.close();
			}else{
				alert( responseData.message );
			}
		}
	});

}




var myinfo = {
		infoPage:function(){
			window.open("/account/infoedit.do","내 정보 수정","height=550,width=650,scrollbars=no,left=100,top=100,resizable");
		},
		edit: function () {

				var name = $("#uname").val();
				var email = $.trim($("#email").val());
				var tel = $.trim($("#tel").val());


				if (!name) {
					alert(message_sysman.O0010);
					return ;
				}
				if(name.indexOf("<") !== -1 || name.indexOf(">") !== -1) {
					alert("이름에 \"<\" 또는 \">\"를 사용할 수 없습니다.");
					return;
				}
				if (!email) {
					alert(message_sysman.O0015);
					$("#email").focus();
					return ;
				}
				if (email) {
					if (!ImStringUtil.validateEmail(email)) {
						alert(message_sysman.O0013);
						$("#email").focus();
						return ;
					}
				}
				var regExp = /[0-9]/gi;
				if (tel) {
					if (tel.indexOf('-') > -1) {
						tel = tel.replaceAll('-', '');
					}

					if (!regExp.test(tel)) {
						alert(message_sysman.O0035);
						$("#tel").focus();
						return;
					}
				}
			// 	alert("수정되었습니다.");
			// $("#UserForm").serialize();
			// window.close();

		var url = "/account/infoedit.do"

		$.ajax({
			url: url,
			data : $("#UserForm").serialize(),
			type : "POST",
			dataType:"json",
			async:false,
			error:function(xhr){
				alert(xhr);
			},
			success:function(result){
				if(result.code == JSONResult.SUCCESS){
					alert("수정되었습니다.");
					opener.location.reload();
					window.close();
				}else{
					alert(result.message);
				}
			}
		});
	},

	changePwdPopup : function () {
		$("#changePwdPopup").show();
	},
	close : function () {
		$("#passwd").val("");
		$("#passwd_confirm").val("");
		$("#changePwdPopup").hide();
	},
	changePassword : function () {
		var userid = $("#userid").val();
		var passwd = $.trim($("#passwd").val());
		var passwd_confirm = $.trim($("#passwd_confirm").val());

		if(!passwd){
			alert(message_sysman.O0007);
			$("#passwd").focus();
			return;
		}else if(!passwd_confirm){
			alert(message_sysman.O0008);
			$("#passwd_confirm").focus();
			return;
		}else if(passwd != passwd_confirm) {
			alert(message_sysman.O0009);
			$("#passwd_confirm").focus();
			return;
		}

		encrypt.enCryptInit();
		encryptDatas();


		var param = {
			"userid" : userid,
			"passwd" : $("#passwd").val(),
			"passwd_confirm":$("#passwd_confirm").val(),
			"encAESKey":$("#encAESKey").val()
		}

		var url = "/account/changePwd.json";

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
				if (result.code == JSONResult.SUCCESS) {
					alert(result.message);
					$("#passwd").val("");
					$("#passwd_confirm").val("");
					$("#encAESKey").val("");
					$("#changePwdPopup").hide();
					window.close();
				} else {
					alert(result.message);
					$("#passwd").val("");
					$("#passwd_confirm").val("");
					$("#encAESKey").val("");
				}
			}
		});

	}


}

function encryptDatas() {
	//mailid_encrypt
	var pwEncrypt = encrypt.enCrypt($("#passwd").val());
	var pwEncrypt_new = encrypt.enCrypt($("#passwd_confirm").val());
	$("#passwd").val(pwEncrypt);
	$("#passwd_confirm").val(pwEncrypt_new);
	$("#encAESKey").val(encryptKey);
}


var JSONResult = {
	SUCCESS:1,
	FAIL:-1
};




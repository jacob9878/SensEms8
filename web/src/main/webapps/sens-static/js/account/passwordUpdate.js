$(document).ready(function(){
	$("#password").focus();
});
var updatePassword = {

	encryptData : function () {
		var pwEncrypt = encrypt.enCrypt($("#password").val());
		var newPwEncrypt = encrypt.enCrypt($("#newPassword").val());
		var confirmPwEncrypt = encrypt.enCrypt($("#confirmPassword").val());
		$("#password").val(pwEncrypt);
		$("#newPassword").val(newPwEncrypt);
		$("#confirmPassword").val(confirmPwEncrypt);
		$("#encAESKey").val(encryptKey);
	},
	updatePassword : function () {
		if($("#password").val() != '' && $("#newPassword").val() != '' && $("#confirmPassword").val() != '') {
			encrypt.enCryptInit();
			updatePassword.encryptData();
		}
		$("#UpdatePasswordForm").submit();
	},
	changeNext : function () {
		$("#UpdatePasswordForm").attr("action", "/account/updatePasswordNext.do");
		$("#UpdatePasswordForm").submit();
	}
}
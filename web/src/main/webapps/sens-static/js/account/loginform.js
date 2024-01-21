var use_captcha= false;
$(document).ready(function(){
	if( $("#userid").val() != "" ){
		$("#password").focus();
	}else{
		$("#userid").focus();
	}
	if(use_captcha){
		changeCaptcha();
	}
});
function changeCaptcha(){
	$("#li_captcha").html("<img id=\"captchaImg\"  name=\"captchaImg\"  alt=\"Captcha Image\" src=\"/account/captcha.do?dummy="+Math.random()+" \"   height=\"49px\">" +
		"<button type=\"button\" class=\"btn themes_bg2\" tabindex=\"-1\" onclick=\"changeCaptcha();\"><span>새로고침</span></button>");
}
function encryptData() {
    //mailid_encrypt
    var pwEncrypt = encrypt.enCrypt($("#password").val());
    $("#password").val(pwEncrypt);
    $("#encAESKey").val(encryptKey);
}
function login() {
	if($("#userid").val() != '' && $("#password").val() != '') {
		encrypt.enCryptInit();
		encryptData();
	}
	$("#mode").val("LOGIN");
}
function langChange(language){
	var f = document.LoginForm;
	f.mode.value = "LANGUAGE";
	f.submit();
}
function login_check(){
	if( event.keyCode == 13 ){
		login();
	}
}
function change_language(){
	//$.cookie('language',$("#languageList").val(),{path:'/'});
	$("#mode").val("LANGUAGE");
	var f = document.LoginForm;
	f.submit();
}
// function imgCbox(N, tabstop)
// {
// 	var objs, cboxes, Img, Span, A;
//
// 	objs = document.getElementsByTagName("INPUT");
// 	if (N == undefined) return false;
// 	if (tabstop == undefined) tabstop = true;
//
// 	for (var i=0; i < objs.length; i++) {
// 		if (objs[i].type != "checkbox" || objs[i].name != N) continue;
//
// 		if (imgCbox.Objs[N] == undefined) {
// 			imgCbox.Objs[N] = [];
// 			imgCbox.Imgs[N] = [];
// 			imgCbox.ImgObjs[N] = [];
// 		}
//
// 		var len = imgCbox.Objs[N].length;
// 		imgCbox.Objs[N][len] = objs[i];
// 		imgCbox.Imgs[N][len] = {};
//
// 		// for image cache
// 		(Img = new Image()).src = $(objs[i]).data("onsrc");
//
// 		imgCbox.Imgs[N][len]["on"] = Img;
//
// 		(Img = new Image()).src = $(objs[i]).data("offsrc");
// 		imgCbox.Imgs[N][len]["off"] = Img;
//
// 		// image element
// 		Img = document.createElement("IMG");
// 		Img.src = $(objs[i]).is(":checked") ? $(objs[i]).data("onsrc") : $(objs[i]).data("offsrc");
// 		Img.style.borderWidth = "0px";
// 		Img.onclick = new Function("imgCbox.onclick('"+N+"','"+len+"')");
// 		imgCbox.ImgObjs[N][len] = Img;
//
// 		// anchor element for tab stop
// 		A = document.createElement("A");
// 		if (tabstop) {
// 			A.href = "javascript:;";
// 			A.onkeypress = new Function("evt", "if(evt==undefined)evt=event;if(evt.keyCode==32||evt.keyCode==0){ imgCbox.onclick('"+N+"','"+len+"'); }");
// 		}
// 		A.style.borderWidth = "0px";
// 		A.appendChild(Img);
//
// 		// insert object
// 		Span = objs[i].parentNode;
// 		Span.style.display = "none";
// 		Span.parentNode.insertBefore(A, Span);
// 	}
// }
// imgCbox.onclick = function(N, idx) {
// 	var C = imgCbox.Objs[N][idx];
// 	var I = imgCbox.ImgObjs[N][idx];
//
// 	C.checked = !C.checked;
// 	I.src = imgCbox.Imgs[N][idx][C.checked?"on":"off"].src;
//
// 	// fire event
// 	if (C.onclick != undefined || C.onclick != null) C.onclick();
// }
// imgCbox.Objs = {};
// imgCbox.Imgs = {};
// imgCbox.ImgObjs = {};
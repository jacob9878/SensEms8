/* 자바스크립트의 메세지 파일 호출*/
var bundle = {
	getString:function(key){
		return resoucebundle[key];
	}
};

function jConfirm( title, message, callback , option ) {

	if ($.trim(message).length == 0) {
		alert(message_common.CM0022);
		return;
	}
}
/**
 * HTTP Error Code Define
 */
var errorcode = {
	session_expire:451, // 세션 종료
	not_allow:405, // 리소스를 허용안함
	internal_error:500, // 서버 에러
	notfound:404 // 페이지 없음
};

var AjaxUtil = {
		error:function( xhr ){

			if( xhr.status == errorcode.session_expire ){
				if(confirm(message_common.CM0002)){
					location.href="/account/login.do";
				}
			}else if( xhr.status == errorcode.internal_error ){
			    var json = JSON.parse( xhr.responseText );
			    if( json && json.message ){
			        alert( json.message );
	            }else{
	            	alert( message_common.CM0001);
	            }
	        }else if( xhr.status == errorcode.notfound ){
	        	alert( message_common.CM0003  );
			}else{
				alert( message_common.CM0001 );
			}
		}
	};
var common = {
	/**
	 * menu_top의 active를 변경한다.
	 *
	 *
	 */
	logout:function(){
		location.href="/account/logout.do";
	},
	change_main_active:function(index){
		var obj="";
		for(var i=1; i<=4; i++){
			var id="top_"+i;
			obj = document.getElementById(id);
			if(index==0){
				obj.className="";
			}
			if(index==i){
				obj.className ="active";
			}
			else{
				obj.className="";
			}
		}
	},

	change_sub_active:function(sub_index, manager_index){
		$($('#sub_tit'+sub_index)).css("display", "block");
		var obj="";
		for(var i=1; i<=5; i++){
			var id="m"+sub_index+"_"+i;
			obj = document.getElementById(id);
			if(manager_index==i){
				obj.className ="active";
			}
			else{
				if(obj){
				obj.className="";
				}
			}
		}

	},

	/**
	 * checkbox 를 모두 선택 또는 모두 해제 한다.
	 * @author SSG911
	 * @parameter - checkbox name
	 */
	select_all:function( name ){
		var c = $("#all_check").is(":checked");
		$("input:checkbox[name='"+name+"']").each( function(){
			$(this).prop("checked",c);
		});
	},
	/**
	 * 선택되어있는 아이템만 가져온다.
	 */
	checked_list:function( name ){
		var item = new Array();
		$("input[name='"+name+"']:checkbox:checked").each( function() {
			item.push( $(this).attr("value") );
		});
		return item;
	},
	/**
	 * select option 객체 이동
	 * s 의 option 을 t 로 이동한다.
	 * s 와 t 는 id 값이여만 한다.
	 * ex) id="s"
	 */
	option_move:function( s , t ){
		$("#"+s+" option:selected").each(function(){
			var s_value = $(this).attr("value");
			var exist = false;
			$("#"+t+" option").each( function(){
				if( $(this).attr("value") == s_value ){
					exist = true;
					return false;
				}
			});
			if( !exist ){
				$("#"+t).append("<option value="+$(this).attr("value")+">"+$(this).attr("text")+"</option>");
				$(this).remove();
			}
		});
	},
	/**
	 * 문자열에 공백을 제거한다.
	 */
	ignoreSpace:function(s){
	    var temp = "";
	    s = '' + s;
	    splitstring = s.split(" ");
	    for(i = 0; i < splitstring.length; i++)
	        temp += splitstring[i];
	    return temp;
	},
	/**
	 * 문자열이 숫자만으로 되어 있는지 확인한다.
	 */
	checkNum:function(obj){
		var ip = common.ignoreSpace(obj.value);
		if(ip == ""){
			alert( bundle.getString("7") );
			//obj.value = "0";
			obj.focus();
			return false;
		}

		if (isNaN(ip)){
			alert( bundle.getString("7") );
			//obj.value = "0";
			obj.focus();
			return false;
		}

		obj.value=ip;
		return true;
	},
	/**
	 * (obj, type, special)
	 * 문자열이 특정문자(한글, 영문, 숫자)들로 구성 되어 있는지 확인한다.
	 * type
	 * 0: 숫자, 1: 영문자, 2: 숫자 + 영문자, 3: 한글
	 * special = 0: 특수키 포함 안됨, 1: 특수키 포함
	 * 특수키 = -.,_
	 */
	checkString:function(obj, type, special){
		var str = common.ignoreSpace(obj.value);

		//var chk_num = ip.search(/0-9/g);	// 숫자가 아닌 문자 검토[0-9]
		//var chk_eng = ip.search(/[a-zA-Z]/g);	// 영문이 아닌 문자 검토[a-zA-Z]
		//var chk_engnum = ip.search(/\W/g);	// 영문과 숫자 조합이 아닌 문자 검토[a-zA-Z0-9]

		if(str == ""){
			alert( bundle.getString("52") );
			//obj.value = "0";
			obj.focus();
			return false;
		}

		var pattern = /[0-9]/g;
		var err_str = bundle.getString("53");

		switch( type ){
		case '0':
			pattern = /[^0-9]/g;
			err_str = bundle.getString("53");
			if( special == '1' ) {
				pattern = /[^0-9.]/g;
				err_str = bundle.getString("54");
			}

			break;
		case '1':
			pattern = /[^a-zA-Z]/g;
			err_str = bundle.getString("55");
			if( special == '1' ) {
				pattern = /[^a-zA-Z-._,/\\]/g;
				err_str = bundle.getString("56");
			}

			break;
		case '2':
			pattern = /[^a-zA-Z0-9]/g;
			err_str = bundle.getString("57");
			if( special == '1' ) {
				pattern = /[^a-zA-Z0-9-._,/\\]/g;
				err_str = bundle.getString("58");
			}
			break;
		case '3':
			pattern = /[^가-힣]/g;
			err_str = bundle.getString("59");
			if( special == '1' ) {
				pattern = /[^가-힣-._,/\\]/g;
				err_str = bundle.getString("60");
			}
			break;
		}
		if ( str.search(pattern) > -1){
			alert( err_str );
			obj.focus();
			return false;
		}
		return true;
	},
	/**
	 * (obj, type, special)
	 * 문자열이 특정문자(한글, 영문, 숫자)들로 구성 되어 있는지 확인한다.
	 * type
	 * 0: 숫자, 1: 영문자, 2: 숫자 + 영문자, 3: 한글
	 * special = 0: 특수키 포함 안됨, 1: 특수키 포함
	 * 특수키 = -.,_
	 */
	checkStringJquery:function(obj, type, special){
		var str = common.ignoreSpace(obj.val());

		//var chk_num = ip.search(/0-9/g);	// 숫자가 아닌 문자 검토[0-9]
		//var chk_eng = ip.search(/[a-zA-Z]/g);	// 영문이 아닌 문자 검토[a-zA-Z]
		//var chk_engnum = ip.search(/\W/g);	// 영문과 숫자 조합이 아닌 문자 검토[a-zA-Z0-9]

		if(str == ""){
			alert( bundle.getString("52") );
			//obj.value = "0";
			obj.focus();
			return false;
		}

		var pattern = /[0-9]/g;
		var err_str = bundle.getString("53");

		switch( type ){
		case '0':
			pattern = /[^0-9]/g;
			err_str = bundle.getString("53");
			if( special == '1' ) {
				pattern = /[^0-9.]/g;
				err_str = bundle.getString("54");
			}

			break;
		case '1':
			pattern = /[^a-zA-Z]/g;
			err_str = bundle.getString("55");
			if( special == '1' ) {
				pattern = /[^a-zA-Z-._,/\\]/g;
				err_str = bundle.getString("56");
			}

			break;
		case '2':
			pattern = /[^a-zA-Z0-9]/g;
			err_str = bundle.getString("57");
			if( special == '1' ) {
				pattern = /[^a-zA-Z0-9-._,/\\]/g;
				err_str = bundle.getString("58");
			}
			break;
		case '3':
			pattern = /[^가-힣]/g;
			err_str = bundle.getString("59");
			if( special == '1' ) {
				pattern = /[^가-힣-._,/\\]/g;
				err_str = bundle.getString("60");
			}
			break;
		}
		if ( str.search(pattern) > -1){
			alert( err_str );
			obj.focus();
			return false;
		}
		return true;
	},
	/**
	 * 비밀번호가 유효한 비밀번호인지 체크한다.
	 */
	fnCheckPassword:function(uid,upw){
		if( upw.length < 8 || upw.length > 20 ){
			alert( bundle.getString("8") );
			return false;
		}
		/*
		if(!/{8,20}$/.test(upw)){
			alert( bundle.getString("8") );
			return false;
		}
		*/
		/*
		var chk_num = upw.search(/[0-9]/g);
		var chk_eng = upw.search(/[a-z]/ig);

		if(chk_num < 0 || chk_eng <0){
			alert( bundle.getString("9") );
			return false;
		}
		*/
		/*
		if(/(\w)\1\1\1/.test(upw)){
			alert!('비밀번호에 같은 문자를 4번 이상 사용하실 수 없습니다.');
			return false;
		}
		*/
		if(upw.search(uid) > -1){
			alert( bundle.getString("10") );
			return false;
		}

		return true;
	},
	/* 윈도우 파일시스템에서 사용할수 없는 문자를 제거한다.*/
	windowFileNameCheck:function(s){
		s = s.replace(/\:/g,'');
		s = s.replace(/\\/g,'');
		s = s.replace(/\</g,'');
		s = s.replace(/\>/g,'');
		s = s.replace(/\?/g,'');
		s = s.replace(/\|/g,'');
		s = s.replace(/\"|/g,'');
		s = s.replace(/\//g,'');
		return s;
	},
	// myinfo:function(){
	// 	window.open("/account/infoedit.do?page","account","height=550,width=650,scrollbars=no,left=100,top=100,resizable");
	// },

	change_language:function(language){
		var param = {
				"language" : language
			};
			var url = "/common/json/changeLanguage.do";

			$.get( url , param , function( data, textStatus ){
				if( textStatus == 'success' ){
					location.reload();
				}
			});
	},
	/**
	 * 목록 설정 open/close
	 * */
	open_dropMenu:function(){
		$(".drop_menu").toggle();
	},
	/**
	 * 세션이 유지되는 동안 목록 개수 설정
	 * @param pagesize
	 */
	change_pagesize:function (pagesize) {
		var url="/common/changePagesize.json";
		var param = {
			"pagesize":pagesize
		}
		$.ajax({
			url: url,
			type: "get",
			data: param,
			dataType: "json",
			async: false,
			success: function (result) {
				if (result.code == JSONResult.SUCCESS) {
					location.reload();
				} else {
					alert(result.message);
				}
			}
		});
	},
	/**
	 *  좌측 메뉴바 선택시 해당 메뉴 펼치기 / 접기
	 */
	select_menu:function(menu) {

		if(menu == 'send_result_menu'){
			if($("#send_result_menu_li").css('display') == 'none' || $("#send_result_menu_li").css('display') == ''){
				$("#send_result_menu_li").show();
			}else {
				$("#send_result_menu_li").hide();
			}
		}
		if(menu == 'receive_group_menu'){
			if($("#receive_group_menu_li").css('display') == 'none' || $("#receive_group_menu_li").css('display') == ''){
				$("#receive_group_menu_li").show();
			}else {
				$("#receive_group_menu_li").hide();
			}
		}
		if(menu == 'send_menu'){
			if($("#send_menu_li").css('display') == 'none' || $("#send_menu_li").css('display') == ''){
				$("#send_menu_li").show();
			}else {
				$("#send_menu_li").hide();
			}
		}
		if(menu == 'sysman_menu'){
			if($("#sysman_menu_li").css('display') == 'none' || $("#sysman_menu_li").css('display') == ''){
				$("#sysman_menu_li").show();
				$("#individual_menu_li").show();
			}else {
				$("#sysman_menu_li").hide();
				$("#individual_menu_li").hide();
			}
		}
		/*if(menu == 'individual_menu'){ 개별발송 설정 메뉴탭
			if($("#individual_menu_li").css('display') == 'none' || $("#individual_menu_li").css('display') == ''){
				$("#individual_menu_li").show();
			}else {
				$("#individual_menu_li").hide();
			}
		}*/

	}
};

function MM_preloadImages() { //v3.0
  var d=document; if(d.images){ if(!d.MM_p) d.MM_p=new Array();
    var i,j=d.MM_p.length,a=MM_preloadImages.arguments; for(i=0; i<a.length; i++)
    if (a[i].indexOf("#")!=0){ d.MM_p[j]=new Image; d.MM_p[j++].src=a[i];}}
}

function MM_swapImgRestore() { //v3.0
  var i,x,a=document.MM_sr; for(i=0;a&&i<a.length&&(x=a[i])&&x.oSrc;i++) x.src=x.oSrc;
}

function MM_findObj(n, d) { //v4.01
  var p,i,x;  if(!d) d=document; if((p=n.indexOf("?"))>0&&parent.frames.length) {
    d=parent.frames[n.substring(p+1)].document; n=n.substring(0,p);}
  if(!(x=d[n])&&d.all) x=d.all[n]; for (i=0;!x&&i<d.forms.length;i++) x=d.forms[i][n];
  for(i=0;!x&&d.layers&&i<d.layers.length;i++) x=MM_findObj(n,d.layers[i].document);
  if(!x && d.getElementById) x=d.getElementById(n); return x;
}

function MM_swapImage() { //v3.0
  var i,j=0,x,a=MM_swapImage.arguments; document.MM_sr=new Array; for(i=0;i<(a.length-2);i+=3)
   if ((x=MM_findObj(a[i]))!=null){document.MM_sr[j++]=x; if(!x.oSrc) x.oSrc=x.src; x.src=a[i+2];}
}
//-->
if (!String.prototype.format) {
  String.prototype.format = function() {
    var args = arguments;
    return this.replace(/{(\d+)}/g, function(match, number) {
  return typeof args[number] != 'undefined'
        ? args[number]
        : match
      ;
    });
  };
}
String.prototype.zf = function(l) { return '0'.string(l - this.length) + this; };
String.prototype.string = function(l) { var s = '', i = 0; while (i++ < l) { s += this; } return s; };
Number.prototype.zf = function(l) { return this.toString().zf(l); };
Number.prototype.byteFormat = function(dot){
	var kb = 1024; // Kilobyte
    var mb = 1024 * kb; // Megabyte
    var gb = 1024 * mb; // Gigabyte
    var tb = 1024 * gb; // Terabyte
    var size = this;
    if (size == 0)
        return "0 Byte";
    if (size < kb) {
    	var nf = new NumberFormat(size);
    	nf.setPlaces(0);
        return nf.toFormatted() + " Byte";
    } else if (size < mb) {
    	var nf = new NumberFormat(size / kb );
    	nf.setPlaces(dot);
        return nf.toFormatted() + " KB";
    } else if (size < gb) {
    	var nf = new NumberFormat(size / mb );
    	nf.setPlaces(dot);
        return nf.toFormatted() + " MB";
    } else if (size < tb) {
    	var nf = new NumberFormat(size / gb);
    	nf.setPlaces(dot);
        return nf.toFormatted() + " GB";
    } else {
    	var nf = new NumberFormat(size /tb );
    	nf.setPlaces(dot);
        return nf.toFormatted() + " TB";
    }
};


function jDownload( url , method , param ){

	if( !method ){
		method = "GET";
	}

	$.fileDownload( url , {
	    popupWindowTitle : "File Download",
	    httpMethod : method,
	    data : param,
		sucessCallback : function( url ){
		},
		failCallback : function( responseHtml , url ){
			if(responseHtml != ''){
				alert( responseHtml );
			}else{
				alert( bundle.getString("170"));
			}
			return false;
		}
	});
}
/**
 JSON 호출(JSONResult 이용)에 대한 성공/실패에 대한 리턴값 정의
 */
var JSONResult = {
	SUCCESS:1,
	FAIL:-1
};




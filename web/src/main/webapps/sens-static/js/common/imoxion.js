var log;
var tab;

jQuery.ajaxSettings.traditional = true;
jQuery.support.scriptEval = true;
jQuery.curCSS = function(element, prop, val) {
    return jQuery(element).css(prop, val);
};
$.ajaxSetup({
	cache : false,
	timeout : 60000,
    beforeSend : function(xhr, opts) {
    	AjaxUtil.setCsrfHeader(xhr);
    	// ajax no-cache일때 _={timestamp} 13자리가 붙어서 주민번호처럼 웹방화벽에서 걸릴수 있음
		try {
			if(opts.url.indexOf("_=") > 0) {
				opts.url += Math.random().toString().substr(2, 11);
			}
		}catch(e){
			log.error("ajaxSetup error");
		}
    },
	error:function(xhr){
		AjaxUtil.error(xhr);
	}
});
//Array Remove - By John Resig (MIT Licensed)
Array.prototype.remove = function(from, to) {
  var rest = this.slice((to || from) + 1 || this.length);
  this.length = from < 0 ? this.length + from : from;
  return this.push.apply(this, rest);
};

Array.prototype.indexOf = function( str ){
	var pos = -1;
	for(var i = 0 ; i < this.length ; i++ ){
		if(this[i] == str){ 
			pos = i;
			break;
		}
	}
	return pos;
};

/**
 * 자바스크립트 replaceAll 메서드 추가
 * @param regex
 * @param replacement
 * @returns
 */
String.prototype.replaceAll = function(regex, replacement) {	
    return this.split(regex).join(replacement);   
};

/**
 * 자바스크립트 between 메서드 추가
 * startChar 와 endChar 사이의 문자열을 추출한다.
 * @param startChar
 * @param endChar
 */
String.prototype.between = function( startChar , endChar ){
	try{
		var s = this.indexOf( startChar );
		var e = this.indexOf( endChar );
		return this.substring( s + 1 , e );
	}catch(e){
		return this;
	}	
};

/**
 * 첫문자열이 char 와 같다면 true , 아니면 false
 * @param char
 * @returns {Boolean}
 */
String.prototype.startWith = function( char ){
	if( this.substring(0,1) == char ){
		return true;
	}else{
		return false;
	}
};

/**
 * HTTP Error Code Define
 */
var errorcode = {
	session_expire:401 // 세션 종료
	,not_allow:405 // 리소스를 허용안함
	,internal_error:500 // 서버 에러
	,notfound:404 // 페이지 없음
	,duplicate_login:452 // 중복로그인
	,forbidden:400 // CSRF 위반
};

function setCookie(cKey, cValue)  // name,pwd
{
    var date = new Date(); // 오늘 날짜
    // 만료시점 : 오늘날짜+10 설정
    var validity = 10;
    date.setDate(date.getDate() + validity);
    // 쿠키 저장
    document.cookie = cKey + '=' + escape(cValue) + ';path=/;expires=' + date.toGMTString();
}

function delCookie(cKey) {
    // 동일한 키(name)값으로
    // 1. 만료날짜 과거로 쿠키저장
    // 2. 만료날짜 설정 않는다. 
    //    브라우저가 닫힐 때 제명이 된다    

    var date = new Date(); // 오늘 날짜 
    var validity = -1;
    date.setDate(date.getDate() + validity);
    document.cookie =
          cKey + "=;expires=" + date.toGMTString();
}

function getCookie( name ){
	var nameOfCookie = name + "=";
	var x = 0;
	while ( x <= document.cookie.length )
	{
		var y = (x+nameOfCookie.length);
		if ( document.cookie.substring( x, y ) == nameOfCookie ) {
			if ( (endOfCookie=document.cookie.indexOf( ";", y )) == -1 )
			endOfCookie = document.cookie.length;
			return unescape( document.cookie.substring( y, endOfCookie ) );
		}
		
		x = document.cookie.indexOf( " ", x ) + 1;
		
		if ( x == 0 ) break;
	}
			return "";
}

/**
 * OS 에 따라서 다운로드를 처리한다.
 * @param url
 * @param method - GET , POST
 * @param param - 파라미터
 */
function jDownload( url , method , param ){
	// for iphone/ipad
//    var agent = navigator.userAgent.toLowerCase();
//    if(agent.indexOf('ipad') > -1 || agent.indexOf('iphone') > -1){
//        window.open( url , '' , '' );
//        return;
//	}
	var filter = "win16|win32|win64|mac|macintel";
	if( navigator.platform  ){
	        if( filter.indexOf(navigator.platform.toLowerCase())<0 ){
	        	var add_param="";
	        	var i=0;
	        	for(var objVarName in param){
	        		if(i > 0){	        		
	                	add_param +="&";
	                }else{
	                	add_param="?";
	                }
	        		add_param += objVarName;
	        		add_param += "=";
	        		add_param += encodeURIComponent(param[objVarName]);
	        		i++;
	        	}
	        	if(add_param != ""){
	        		url = url +add_param;
	        	}
	        	window.open( url , '' , '' );
		          return;
	        }
	 }


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
				alert( "다운로드 요청 중 오류가 발생하였습니다.");
			}			
			return false;
		}
	});
}

/**
 * OS 에 따라서 다운로드를 처리한다.(시스템 관리자 > 첨부파일 관리 > 다운로드)
 * @param url
 * @param method - GET , POST
 * @param param - 파라미터
 */
function jDownloadSub( url , method , param ){
	// for iphone/ipad
//    var agent = navigator.userAgent.toLowerCase();
//    if(agent.indexOf('ipad') > -1 || agent.indexOf('iphone') > -1){
//        window.open( url , '' , '' );
//        return;
//	}
	var filter = "win16|win32|win64|mac|macintel";
	if( navigator.platform  ){
		if( filter.indexOf(navigator.platform.toLowerCase())<0 ){
			var add_param="";
			var i=0;
			for(var objVarName in param){
				if(i > 0){
					add_param +="&";
				}else{
					add_param="?";
				}
				add_param += objVarName;
				add_param += "=";
				add_param += encodeURIComponent(param[objVarName]);
				i++;
			}
			if(add_param != ""){
				url = url +add_param;
			}
			window.open( url , '' , '' );
			return;
		}
	}


	if( !method ){
		method = "GET";
	}
	if( !param ){
		param = {
			"jdownload":"true"
		};
	}else{
		param.jdownload = "true";
	}
	$.fileDownload( url , {
		popupWindowTitle : "File Download",
		httpMethod : method,
		data : param,
		failCallback : function( responseHtml , url ){
			if(responseHtml != ''){
				/*jAlert( responseHtml.trim() );*/
				alert(responseHtml.trim());
			}else{
				/*jAlert( message_common.CM0032 );*/
				alert( message_common.CM0032 );
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

/**
 * AJX 호출 Error 발생시 에러코드에 따라 처리 방법이 달라진다.
 * 세션이 종료되었을경우에는 세션 만료 페이지로 이동한다.
 * @type {{error: AjaxUtil.error}}
 */
var AjaxUtil = {
    HTML:"HTML",
    JSON:"JSON",
	error:function( xhr ){
		log.debug( "status : "+ xhr.status );
		if( xhr.status == errorcode.session_expire ){
			location.href="error/session-expire.do";
        }else if( xhr.status == errorcode.internal_error ){
		    var json = JSON.parse( xhr.responseText );
		    if( json && json.message ){
		        alert( json.message );
            }else{
                alert( message_common.CM0001 );
            }
        }else if( xhr.status == errorcode.notfound ) {
            alert(message_common.CM0003);
        }else if( xhr.status == errorcode.forbidden ){
        	//사용자에게 csrf 토큰 누락 메세지가 이해하기 어려우므로 
        	//페이지를 리로딩 해주는것으로 한다.(2018-03-02)
        	location.reload();
			//alert(message_common.CM0083);
        }else{
            alert( message_common.CM0001 );
        }
    },
    getContentType:function(xhr){
        if( xhr.getResponseHeader("Content-Type").indexOf("text/html") > -1 ){
            return AjaxUtil.HTML;
        }else if( xhr.getResponseHeader("Content-Type").indexOf("application/json") > -1 ){
            return AjaxUtil.JSON;
        }
    },
	setCsrfHeader:function(xhr){
    	var token = CSRFWrapper.getToken();
		if( token ) {
            var header = CSRFWrapper.getHeaderName();
            xhr.setRequestHeader(header, token);
        }
	}
};

/* IE에서 FLASH에 의해 타이틀이 변경되는 현상 방지 : START */
// if( browser.type == browser.IE ){
//     try {
//         var originalTitle = document.title.split("#")[0];
//         document.attachEvent('onpropertychange', function (evt) {
//             if (evt.propertyName === 'title' && document.title !== originalTitle) {
//                 setTimeout(function () {
//                     document.title = originalTitle;
//                 }, 1);
//             }
//         });
//     }catch(e){}
// }
/* IE에서 FLASH에 의해 타이틀이 변경되는 현상 방지 : END */

/**
 * 페이지를 이동한다.
 * @param url
 * @param target (Option)
 */
function goto(url,target){
	if( target ) {
		target.location.href = url;
	}else{
		location.href = url;
	}
}

/**
 * CSRF 토큰 관련 wrapper
 * @type {{getToken: csrfWrapper.getToken, getParameterName: csrfWrapper.getParameterName, getHeaderName: csrfWrapper.getHeaderName, setToken: csrfWrapper.setToken}}
 */
var CSRFWrapper = {
	getToken:function(){
        return $("meta[name='_csrf']").attr("content");
	},
	getParameterName:function(){
        return "_csrf";
	},
	getHeaderName:function(){
        return $("meta[name='_csrf_header']").attr("content");
	},
	setToken:function(token){
        $("form input[name='_csrf']").each(function(){
            $(this).val(token);
        });
        $("meta[name='_csrf']").attr("content", token);
	}
}

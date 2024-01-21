var ImStringUtil = {
	encode:function(sStr){
		return escape(sStr).replace(/\+/g,'%2C').replace(/\"/g,'%22').replace(/\'/g,'%27');
	},
	emailHandler:function(sStr){
		return sStr.replace(/\+/g,'%2C').replace(/\"/g,'%22').replace(/\'/g,'%27');
	},
	decode:function(sStr){
		return unescape(sStr);
	},	
	safeString:function(str){
		str = str.replaceAll("'","");
		return str;
	},
	dateFormat:function(str){
		if( str.length < 12 ) return str;
	//	var str = str;
		return str.substring(0,4)+"-"+str.substring(4,6)+"-"+str.substring(6,8) + " " + str.substring(8,10)+":"+str.substring(10,12);
	},
	isAlphaIncluded:function(str){
		var pattern = /[a-zA-Z]+/g;
		if(str != ""){
            if(pattern.test(str)){
            	return true;
            }
		}
		return false;
	},
    numberCheck:function( obj ){
        var pattern = /^[0-9]+$/;
        if(obj.value != ""){
            if(!pattern.test(obj.value)){
                alert( bundle.getString("166"));
                obj.value = ''; 
                obj.focus();
            }
        }
    },
    numberCheck2:function( obj ){
        var pattern = /^[1-9]+$/;
        if(obj.value != ""){
            if(!pattern.test(obj.value)){
                //  jAlert( msg.get( cmessage.CM0001 ) );
                obj.value = ''; 
                obj.focus();
            }
        }
    },
    numberCheck3:function( chkValue ){
    	if( chkValue != '-1'){
    		var pattern = /^[0-9]+$/;
            if(chkValue != ""){
                if(!pattern.test(chkValue)){
                	return false;
                }else{
                	return true;
                }
            }else{
            	return false;
            }
    	}else{
    		return false;
    	}
    	return true;
    },
    doTrimString:function(str){
        // trim string
        var strRet = str.replace(/(\n+$)/gi,"");
        strRet =  strRet.replace(/(^\n*)/gi,"");
        strRet = str.replace(/(\s+$)/gi,"");
        strRet =  strRet.replace(/(^\s*)/gi,"");
        
        return strRet;
    },
    checkSpecialChar:function(ele){
        var pattern = /[~,`!@\#$%^&*\()\-=+'"]/gi;
       
        if(pattern.test(ele.value)){
        	
            ele.value = ele.value.replace(pattern,"");
            ele.focus();    
            return false;
        } 
        return true;
    },
    checkNumber:function(val){
        var pattern = /^[0-9]+$/;
        if(val == ""){
            return true;
        }
        if(!pattern.test(val)){
            return false;
        }
    
        return true;
    },
    /**
     * IP4 어드레스 체크
     * @returns {boolean}
     */
    validateIPaddress:function(ip){
        if (/^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/.test(ip))
        {
            return true;
        }
        return false;
    },
	validateIPaddressCidr:function(ip){
		if (/^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)(\/(3[0-2]|[012]?[0-9]))?$/.test(ip)) {
			return true;
		}
		return false;
	},
	validateEmail : function(email) {
		var regEmail = /([\w-\.]+)@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.)|(([\w-]+\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\]?)$/;
		if (email == '') {

			return false;

		} else {

			if (!regEmail.test(email)) {

				
				return false;

			}

		}
		
		return true;
	}
};

/**
 * form 사용시 공통적으로 사용 가능한 스크립트 
 * 번들은 js/messages/common_lang.js 사용
 */
var ImFormUtils = {
    
    // 전체 체크박스 선택
    onclick_allchk:function( key_name ){
        try{
            // id가 all_check인 체크박스의 checked를 체크하여 해당 값의 반대로 설정
            if( !$("#all_check").attr("checked") ){
                $("#all_check").attr("checked", true);
            } else {
                $("#all_check").attr("checked", false);
            }
        }catch(e){
			console.error("onclick_allchk error");
        }

        /**
         * 목록의 체크박스 선택
         * key_name : 목록 체크박스의 name 속성 값
         */
        ImFormUtils.select_all(key_name);
    },
    
    // onclick_allchk와 동일하나 목록의 체크박스 선택시 disable된 체크박스는 선택 대상에서 제외
    onclick_allchk2:function( key_name ){
        try{
            if( !$("#all_check").attr("checked") ){
                $("#all_check").attr("checked", true);
            } else {
                $("#all_check").attr("checked", false);
            }
        }catch(e){
			console.error("onclick_allchk2 error");
        }

        ImFormUtils.select_all2(key_name);
    },
    
    /**
     * 화면상 모든 체크박스 중 name속성 값이 key_name인 체크박스는 모두 선택 또는 해제
     */
    select_all:function( key_name ){
        if( key_name == null || key_name == '' ){
            return;
        }       
        
//        // checkbox 타입의 객체는 모두 loop
//        $(":checkbox").each(function(){
//            if($.trim($(this).html()) == ""){
//                // disabled 속성 무시
//                if( $(this).attr("disabled") ){
//                    return false;
//                } 
//                
//                /*
//                 * name 속성값이 key_name 파라미터와 일치하는 경우
//                 * id가 btn_all 버튼의 html 코드 변경 전체선택 <-> 선택해제
//                 */
//                if($(this).attr("name") == key_name){
//                    if( !$(this).attr("checked") ){
//                        $(this).attr("checked", true);
//                        // $("#btn_all").text(msg.get( cmessage.CM0003 ) );
//                    } else {
//                        $(this).attr("checked", false);
//                        // $("#btn_all").text(msg.get( cmessage.CM0002) );
//                    }
//                }
//                
//            }
//        });
        var isChecked = $('#all_check').is(':checked');
        
        var obj = document.getElementsByName(key_name);
        for (var i =0 ; i < obj.length ; i++ ) {
        	if(!obj[i].disabled){
        		obj[i].checked = isChecked;
        	}
        }
    },
    
    // select_all과 동일하나 목록의 체크박스 선택시 disable된 체크박스는 선택 대상에서 제외
    select_all2:function( key_name ){
        if( key_name == null || key_name == '' ){
            return;
        }
        
        //"<img src=\"/sens-static/images/common/bt_ic_selec.gif\"/>"+ 
        $(":checkbox").each(function(){
            if($.trim($(this).html()) == ""){
                if( $(this).attr("disabled") ){
                    return true;
                }
                
                if($(this).attr("name") == key_name){
                    if( !$(this).attr("checked") ){
                        $(this).attr("checked", true);
                        // $("#btn_all").text(msg.get( cmessage.CM0003) );
                    } else {
                        $(this).attr("checked", false);
                        // $("#btn_all").text(msg.get( cmessage.CM0002) );
                    }
                }
                
            }
        });
    },

    // 체크박스의 name 속성값이 파라미터 값과 같은 경우 선택된 갯수 리턴
    selected_item_count:function( name ){
        var checked = 0;
        $(":checkbox:checked").each(function(){
           if($.trim($(this).html()) == ""){
               if($(this).attr("name") == name){
                   checked++;
               }
               
           } 
        });

        return checked;
    },

    
    /**
     * 
     */
    searchZip:function() {
//        var z = window.open(app.contextPath + "/common/popup/searchZip.do",'SearchZip','width=350,height=300,scrollbars=no,status=no');
//        z.window.focus();
    	
    	new daum.Postcode({ 
    		
//	        oncomplete: function(data) {
//	            // 팝업에서 검색결과 항목을 클릭했을때 실행할 코드를 작성하는 부분. 우편번호와 주소 정보를 해당 필드에 넣고, 커서를 상세주소 필드로 이동한다.
//	            var post1 = data.postcode1;
//	            var post2 = data.postcode2;
//	            var address = data.address;
//	           
//	            $("#zipcode").val(post1+"-"+post2);
//	            $("#address").val(address);
//	        }
    		
    		oncomplete: function(data) {
    			// 팝업에서 검색결과 항목을 클릭했을때 실행할 코드를 작성하는 부분.

                // 각 주소의 노출 규칙에 따라 주소를 조합한다.
                // 내려오는 변수가 값이 없는 경우엔 공백('')값을 가지므로, 이를 참고하여 분기 한다.
                var fullAddr = ''; // 최종 주소 변수
                var extraAddr = ''; // 조합형 주소 변수

                // 사용자가 선택한 주소 타입에 따라 해당 주소 값을 가져온다.
                if (data.userSelectedType === 'R') { // 사용자가 도로명 주소를 선택했을 경우
                    fullAddr = data.roadAddress;

                } else { // 사용자가 지번 주소를 선택했을 경우(J)
                    fullAddr = data.jibunAddress;
                }

                // 사용자가 선택한 주소가 도로명 타입일때 조합한다.
                if(data.userSelectedType === 'R'){
                    //법정동명이 있을 경우 추가한다.
                    if(data.bname !== ''){
                        extraAddr += data.bname;
                    }
                    // 건물명이 있을 경우 추가한다.
                    if(data.buildingName !== ''){
                        extraAddr += (extraAddr !== '' ? ', ' + data.buildingName : data.buildingName);
                    }
                    // 조합형주소의 유무에 따라 양쪽에 괄호를 추가하여 최종 주소를 만든다.
                    fullAddr += (extraAddr !== '' ? ' ('+ extraAddr +')' : '');
                }

                // 우편번호와 주소 정보를 해당 필드에 넣는다.
                document.getElementById('zipcode').value = data.zonecode; //5자리 새우편번호 사용
                document.getElementById('address').value = fullAddr;

                // 커서를 상세주소 필드로 이동한다.
                document.getElementById('address').focus();
            }
	    }).open();
    },
    
    /**
     *      
    checkZip:function(id){
        var pattern = /^[0-9-]+$/;
        if($("#"+id).val() != ""){
            if(!pattern.test($("#"+id).val())){
                alert( msg.get( cmessage.CM0006) );
                $("#"+id).val("");
                return; 
            }
        }
    },
    */
    
    /**
     * 전화번호 양식 체크 
     */
    checkPhone:function(id){
        var pattern = /^[0-9-]+$/;
        if($("#"+id).val() != ""){
            if(!pattern.test($("#"+id).val())){
            	alert( bundle.getString("168") );
                $("#"+id).val("");
                return; 
            }
        }
    },
    
    /**
     * 
     */
    idcheck:function() {
        var url = app.contextPath + "/manager/user/popup/userRegistIdCheckForm.do";
        var w = window.open(url,'existID','width=300,height=150');  
        w.window.focus();
    },
    
    /**
     * 페이지 리로딩(1page로 이동)
     * formName : form id
     * actionUrl : 삭제가 처리될 form action id
     */
    doReload:function(formName, actionUrl) {
        $("#cpage").val('1');
        $("#pageSize").val($("#selpagesize").val());
        $("#"+formName).attr("action", actionUrl);
        $("#"+formName).submit();
    },
    
    /**
     * 페이지 리로딩(pageNum로 이동)
     * pageNum : 이동될 대상 페이지 숫자
     * formName : form id
     * actionUrl : 삭제가 처리될 form action id
     */
    doReloadGetPage:function(pageNum, formName, actionUrl) {
        $("#cpage").val(pageNum);
        $("#pageSize").val($("#selpagesize").val());
        $("#"+formName).attr("action", actionUrl);
        $("#"+formName).submit();
    },
    
    /**
     * 검색 목록에서 전체목록 으로 전환
     * formName : form id
     * actionUrl : 삭제가 처리될 form action id
     */
    doAllList:function(formName, actionUrl){
        $("#cpage").val('1');
        $("#searchText").val('');
        $("#"+formName).attr("action", actionUrl);
        $("#"+formName).submit();
    }
};

var ImUtils = {
	/**
	 * @param userid : 사용자의 아이디 혹은 아이디로 사용할 스트링
	 * @param passid : 패스워드를 기입하는 텍스트박스의 아이디 값
	 * @param minLen : 패스워드 최소 사이즈
	 * 번들은 원래 환경설정에 있던것이라 그냥 그거 썼음
	 */
	passInit:function(userid, passid, minLen){
		// 강도 체크 로직 초기화 (10이하 : 낮음, 10~20 : 보통 , 20~35 : 높음 , 45 이상 : 매우높음) -> 이하/이상인지 미만/초과인지는 잘 모르겠음.
		// 애초에 초기점수는 몇점인지 모르겠음
		$("#"+passid).pstrength({ minChar: minLen, verdicts: [msg.get(message.O0011), msg.get(message.O0012), msg.get(message.O0013), msg.get(message.O0056)], scores: [10, 20, 35, 45] , colors: ['#ed1c24', '#0072bc', '#197b30'], minCharText: msg.get(message.O0054,minLen)});

		// 강도 체크 로직 추가 (본인의 아이디 포함 여부) // 아이디가 포함되면 토탈스코어에서 50점을 깐다.
		$("#"+passid).pstrength.addRule('except_string', function (word, score) {return word.match(userid) && score;}, -50, true);
		
		//강도체크 로직 추가 
		$("#"+passid).pstrength.addRule('eng_and_num', function (word, score) {
			//숫자 입력이 없을경우
			var exp1 = new RegExp("^[^\d]+$");
			//문자 입력이 없을경우		
			var exp2 = new RegExp(/^[^A-Za-z]+$/g);
			
			//특수문자 입력이 없을경우는 현재 체크하지 않습니다.
			var exp3 = new RegExp("^[a-zA-Z0-9]+$");
			
			return ( (word.match(exp1) || word.match(exp2))) && score; 	},  -30, true
		);

	},
	placeHolder:function(){
		$('input[placeholder], textarea[placeholder]').placeholder();
	},
	getFileExtension:function( filePath ){
	    var lastIndex = -1;
	    lastIndex = filePath.lastIndexOf('.');
	    var extension = "";

		if ( lastIndex != -1 )
		{
		    extension = filePath.substring( lastIndex+1, filePath.len );
		    extension = extension.toLowerCase();
		} else {
		    extension = "";
		}
	    return extension;
	},
	/**
	 * subject 에서 FW: RE: 을 제외한 순수 제목만을 뽑아온다.
	 */
	getOriginalSubject:function( subject ){
		var reg_pattern = /\FW\s+:\s+/gi;
		subject = subject.replace( reg_pattern , "" );
		reg_pattern = /\RE\s+:\s+/gi;
		subject = subject.replace( reg_pattern , "" );
		return subject;
	},
	/**
	 * 
	 * @param url
	 * @param parm : unserialize 된 param (array형태) - 폼값을 시리얼라이즈
	 * @param target : target 프레임
	 * @param target_form_id : 생성될 폼의 아이디
	 * @param src_form_id : 폼데이타로부터 넘겨받을 소스 폼 아이디
	 * @return
	 */
	submitByPost: function(url, parm, target, target_form_id, src_form_id) {
		var f = document.createElement('form');
		var objs, value;
		if(src_form_id && src_form_id != ''){
			var fields = $('#'+src_form_id).serializeArray();
			// 띄어쓰기가 + 로 표시되는 것을 다시 빈칸으로 변경한다.			
			var params = {};
			$.each(fields, function(i, field){
				var val = field.value;
				objs = document.createElement('input');
				objs.setAttribute('type', 'hidden');
				objs.setAttribute('name', field.name);
				objs.setAttribute('value', val);
				f.appendChild(objs);
			});
		} 
		for (var key in parm) {
			value = parm[key];	
			objs = document.createElement('input');
			objs.setAttribute('type', 'hidden');
			objs.setAttribute('name', key);
			objs.setAttribute('value', value);
			f.appendChild(objs);
		}
		
		if (target) f.setAttribute('target', target);
		if (target_form_id) f.setAttribute('id', target_form_id);
	
		f.setAttribute('method', 'post');
		f.setAttribute('action', url);
		document.body.appendChild(f);
		//alert(f.innerHTML);
		f.submit();
	}
};

(function($){	
	function parseValue(strVal) {
		return ( strVal.match(/^[0-9]+$/) ) ? parseInt(strVal) : (strVal == 'true') ? true : (strVal == 'false') ? false : strVal.replace(/[+]/g, " ");
	}

	$.unserialize = function(serializedString){
		
		var str = decodeURIComponent(serializedString);
		var pairs = str.split('&');
		var n = pairs.length;
		var obj = {}, p, idx, val, match, key, val;
		for (var i=0; i < n; i++) {
			p = pairs[i].split('=');
			idx = p[0];
			if (idx.indexOf("[]") > -1 && idx.indexOf("[]") == (idx.length - 2)) {
				var ind = idx.substring(0, idx.length-2);
				if (obj[ind] === undefined) {
					obj[ind] = [];
				}
				obj[ind].push(p[1]);
			}
			else if (match = idx.match(/([^\[]+)\[([^\]]+)\]$/)) {
				key = match[1];
				val = match[2];
				if (!obj[key]) {
					obj[key] = {};
				}
				obj[key][val] = parseValue(p[1]);
			}
			else {
				obj[idx] = parseValue(p[1]);
			}
		}

		return obj;
	};
})(jQuery);


String.prototype.zf = function(l) { return '0'.string(l - this.length) + this; };
String.prototype.string = function(l) { var s = '', i = 0; while (i++ < l) { s += this; } return s; };
String.prototype.byteFormat = function(dot){
	return parseInt( this ).byteFormat(dot);
};
Number.prototype.zf = function(l) { return this.toString().zf(l); };
Number.prototype.byteFormat = function(dot){
	var kb = 1024; // Kilobyte
    var mb = 1024 * kb; // Megabyte
    var gb = 1024 * mb; // Gigabyte
    var tb = 1024 * gb; // Terabyte
    var size = this;
    if (size == 0)
        return "0 KB";    
    if (size < mb) {
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
var gsMonthNames = new Array('January','February','March','April','May','June','July','August','September','October','November','December');

//a global day names array
var gsDayNames = new Array('Sunday', 'Monday', 'Tuesday', 'Wednesday','Thursday', 'Friday', 'Saturday');
//the date format prototype
Date.prototype.format = function(f) {
	if (!this.valueOf())
		return '&nbsp;';
	var d = this;
	return f.replace(/(yyyy|yy|mmmm|mmm|mm|dddd|ddd|dd|hh|nn|ss|ns|a\/p)/gi,
		function($1) {
			switch ($1.toLowerCase()) {
			case 'yyyy':			   
				return d.getFullYear();
			case "yy": 
				return (d.getFullYear() % 1000).zf(2);
			case 'mmmm':
				return gsMonthNames[d.getMonth()];
			case 'mmm':
				return gsMonthNames[d.getMonth()].substr(0, 3);
			case 'mm':
				return (d.getMonth() + 1).zf(2);
			case 'dddd':
				return gsDayNames[d.getDay()];
			case 'ddd':
				return gsDayNames[d.getDay()].substr(0, 3);
			case 'dd':
				return d.getDate().zf(2);
			case 'hh':
				return ((h = d.getHours() % 24) ? h : 0).zf(2);
				//return ((h = d.getHours() % 12) ? h : 12).zf(2);				
			case 'nn':
				return d.getMinutes().zf(2);
			case 'ss':
				return d.getSeconds().zf(2);
			case 'ns':
				return d.getMilliseconds();
			case 'a/p':
				return d.getHours() < 12 ? 'a' : 'p';
			}
		}
	);
};

function all_checked( key_name ){
	var c = $("#all_check").is(":checked");
	var eles = document.getElementsByName(key_name);
	for( var i=0; i< eles.length ; i++ ) {
		var ele = eles[i];			
		if (!ele.disabled){		
			if( c ){
				ele.checked = true;
			} else {
				ele.checked = false;
			}
		}
	}
}

var msg = {
	get:function( message , arg1 , arg2 , arg3 ){
		if( arg1 ){
			message = message.replace("{0}" , arg1 );
		}
		if( arg2 ){
			message = message.replace("{1}" , arg2 );
		}		
		if( arg3 ){
			message = message.replace("{2}" , arg3 );
		}		
		return message;
	}
};

/**
 * 드롭다운 메뉴
 * author: sunggyu
 */
var ddmenu = {
    timeout:50,
    closetimer:null,
    ddmenuitem:null,
	duplicate:null,
    /**
     * 메뉴를 보여준다.
     * @param 클릭한 객체
     * @param 메뉴 아이디
     */
    show:function(obj,id){
	    this.cancelclosetime(); //자동으로 닫는 것을 중지한다.

		if(id == this.duplicate){ // 이전에 클릭한 버튼과 동일하다면 display = 'block' 처리
			toggle(id);
		}else{
			// 기존에 떠있던 레이어가 있으면 기존 레이어를 닫아버린다.
			if(this.ddmenuitem) this.ddmenuitem.style.display = 'none';
			this.ddmenuitem = document.getElementById(id);
			this.ddmenuitem.style.display = 'block';
			this.ddmenuitem.style.zIndex = 1;
		}

		this.duplicate = id; // 동일 버튼 클릭 체크 목적

        // 클릭한 객체에서 마우스오버시 창 닫기
        $( obj ).mouseover(function(){
            ddmenu.cancelclosetime();
        });
        
        /*$( obj ).mouseout(function(){
        	ddmenu.closetime();
        });*/
        
        $("#"+id).mouseover(function(){
            ddmenu.cancelclosetime();
        });
        $("#"+id).focus(function(){
            ddmenu.cancelclosetime();
        });
        /*$("#"+id).mouseout(function(){
            ddmenu.closetime();
        }); */
        $("#"+id).blur(function(){
            ddmenu.closetime();
        });
    },
    close:function(){
        if( this.ddmenuitem) this.ddmenuitem.style.display = 'none';
        this.closetimer = null;
    },
    closetime:function(){
        if( !this.closetimer ){
            this.closetimer = window.setTimeout(function(){
                ddmenu.close();
            }, this.timeout );
        };
    },
    cancelclosetime:function()
    {
        if(this.closetimer)
        {
            window.clearTimeout( this.closetimer );
            this.closetimer = null;
        }
    }
};

function toggle( id ){
	if( $("#"+id).css("display") == 'none' ){
		$("#"+id).show();
	}else{
		$("#"+id).hide();
	}
}


function fixDate(d, check) { // force d to be on check's YMD, for daylight savings purposes
	if (+d) { // prevent infinite looping on invalid dates
		while (d.getDate() != check.getDate()) {
			d.setTime(+d + (d < check ? 1 : -1) * HOUR_MS);
		}
	}
}

function parseDate(s) {
	if (typeof s == 'object') { // already a Date object
		return s;
	}
	if (typeof s == 'number') { // a UNIX timestamp
		return new Date(s * 1000);
	}
	if (typeof s == 'string') {
		if (s.match(/^\d+$/)) { // a UNIX timestamp
			return new Date(parseInt(s) * 1000);
		}
		return parseISO8601(s, true) || (s ? new Date(s) : null);
	}
	// TODO: never return invalid dates (like from new Date(<string>)), return null instead
	return null;
}

function parseISO8601(s, ignoreTimezone) {
	// derived from http://delete.me.uk/2005/03/iso8601.html
	// TODO: for a know glitch/feature, read tests/issue_206_parseDate_dst.html
	var m = s.match(/^([0-9]{4})(-([0-9]{2})(-([0-9]{2})([T ]([0-9]{2}):([0-9]{2})(:([0-9]{2})(\.([0-9]+))?)?(Z|(([-+])([0-9]{2}):([0-9]{2})))?)?)?)?$/);
	if (!m) {
		return null;
	}
	var date = new Date(m[1], 0, 1),
		check = new Date(m[1], 0, 1, 9, 0),
		offset = 0;
	if (m[3]) {
		date.setMonth(m[3] - 1);
		check.setMonth(m[3] - 1);
	}
	if (m[5]) {
		date.setDate(m[5]);
		check.setDate(m[5]);
	}
	fixDate(date, check);
	if (m[7]) {
		date.setHours(m[7]);
	}
	if (m[8]) {
		date.setMinutes(m[8]);
	}
	if (m[10]) {
		date.setSeconds(m[10]);
	}
	if (m[12]) {
		date.setMilliseconds(Number("0." + m[12]) * 1000);
	}
	fixDate(date, check);
	if (!ignoreTimezone) {
		if (m[14]) {
			offset = Number(m[16]) * 60 + Number(m[17]);
			offset *= m[15] == '-' ? 1 : -1;
		}
		offset -= date.getTimezoneOffset();
	}
	return new Date(+date + (offset * 60 * 1000));
}

var util = {
    /**
     * txt 문자열에 특수문자( " , < , > )를 ascii(unicode)로 변환한다.
     * @param txt
     * @returns
     */ 
    html2ascii:function( txt ){
        txt = txt.replaceAll("\"","&quot;");
        txt = txt.replaceAll("<","&lt;");
        txt = txt.replaceAll (">","&gt;");      
        return txt;
    },
	/**
	 * 용량이 무제한인경우 숫자 입력창을 보이고 안보이게 하기
	 * @param id
	 */
	sizecheck:function(id){
		var val = $("select[name='"+id +".unit']").val();
		//console.log(id+":"+val);
		if( val == '0' ){
			$("input[name='"+id+".size']").hide();
		}else if( val == '-1'){
			$("input[name='"+id+".size']").hide();
		}else{
			$("input[name='"+id+".size']").show();
		}
	},
	/**
	 * 문자열을 XSS FILTERING 처리를 한다.
	 * @필수조건 - js-xss.js 필요
	 * @param txt
	 * @returns {*}
	 */
	escapeXml:function( txt ){
		return this.html2ascii(filterXSS(txt));
	},

	escapeXml1:function (txt){
		return String(txt).replace(/[&<>"'`=\/]/g, function (s) { return util.entityMap[s]; });
	},
	entityMap:{ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;', '/': '&#x2F;', '`': '&#x60;', '=': '&#x3D;' }
};

var help = {
    toggle:function(id){
        var layer = document.getElementById(id);
        if( layer.style.display=='block' ){            
            layer.style.display='none'
        }
        else{
            layer.style.display='block'
        }
    }   
};
var EmailAddressUtil = {
	// 변수를 선언한다.
	regExp:/^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$/i,
	/**
	 * 이메일 형식인지 확인
	 * @param email
	 * @returns {boolean}
	 */
	checkAddress:function(email){
		return this.regExp.test(email);
	}
};

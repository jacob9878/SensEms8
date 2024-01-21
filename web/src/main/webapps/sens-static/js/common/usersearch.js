/*
 * 간단검색 추천
 * 사용법 
 * 입력을 받는 객체(input 또는 textarea 에 simpleSearch.init(this) 를 선언한다.
 */
var usersearch = {
    KEY_ENTER:13
	,KEY_TAB:9
	,KEY_PAGEUP:33
	,KEY_PAGEDOWN:34
	,KEY_END:35
	,KEY_HOME:36	
	,KEY_UP:38
	,KEY_DOWN:40
	,KEY_ESC:27
	,type:null
    ,directInput:false
	/* 자동완성 레이어가 붙을 위치 */
	,areaAppendId:null
	,areaWidth:270
	,areaHeight:340	
	,curr:''
	,listcount:4
	,delay_hide:0
	,limit:20
	,pos:0
	,delimiter:","
	,timeOut:3000
	,old_page_no:1
	,delay_time:0
	,chkLimit:1
	//,my_search:new Array()
	//,myaddrDelimiter:"&lt;"
	//,servletURL:app.contextPath + "/autocomplete/mail/write.do"
	
	/**
	 * 자동완성을 하기 위해 입력한 두글자를 저장
	 */
	,chkFirstStr:''
	
	,init:function(obj, event) {
        if (obj) usersearch.curr = obj;

		usersearch.areaWidth = usersearch.curr.offsetWidth;

        if(!usersearch.getCurrentInput()){
            $("#ssArea").remove();
            $("#ssFrame").remove();
			usersearch.ssArea = null;
			usersearch.ssFrame = null;
        }

        if (usersearch.ssArea) {
        	//simpleSearch.updateList();
			usersearch.showSelectList();
        } else {
			// check browser (IE 5.0)
			if (navigator.userAgent.indexOf("MSIE 5.0") != -1) {
				return;
			}

			usersearch.curr.onkeydown = usersearch.moveSelect;
			usersearch.curr.onkeyup = usersearch.checkInput;

		}
	}
	
	,moveSelect:function(e){
	    //log.debug("move_select");

		var code;
		if( browser.type == browser.IE ){
			code = event.keyCode;
		}else{
			code = e.which;
		}
		
		if ( code == usersearch.KEY_ENTER ) {
			if( browser.type != browser.IE ){
				e.preventDefault();
			}
		}
		
		var showArea = $("#ssArea").is(":visible");		
		if( showArea ){
			// simpleSearch.pos 값 구하기 : start
			if ( code == usersearch.KEY_UP || code == usersearch.KEY_DOWN || code == usersearch.KEY_TAB || code == usersearch.KEY_ENTER ) {
			
				if( browser.type == browser.IE ){
					event.returnValue=false;
                } else {
					e.preventDefault();
				}
			}
			//console.log("keycode: " + code);
			if( code == usersearch.KEY_UP ){
			    if( usersearch.pos > 0 ){
					usersearch.pos--;
			    }
			    else{
					usersearch.pos = usersearch.listcount - 1;
			    }
			}
			else if ( code == usersearch.KEY_TAB || code == usersearch.KEY_DOWN ) {
				
				if( usersearch.listcount == 1 && code == usersearch.KEY_TAB ) {
					usersearch.applySelectSearch();
					return ;
				}
				if ( usersearch.pos < usersearch.listcount-1){
					usersearch.pos++;
				} else {
					usersearch.pos = 0;
				}
			}
            // simpleSearch.pos 값 구하기 : end
			usersearch.updateSelectList();
		}
	}

	/**
	 * 키보드 입력 이벤트 
	 */
	,checkInput:function(e){
		var code;
		if( browser.type == browser.IE ){
			code = event.keyCode;
		} else {
			code = e.which;
		}

		//console.log("checkInput : "+ code );
		
		if( code == usersearch.KEY_ESC ){
			usersearch.close();
		    return;
		}
				
		var showArea = $("#ssArea").is(":visible");
		
		var currInput = usersearch.getCurrentInput();
		// 입력값을 없을 경우, 자동완성 결과창을 닫는다.
		if (currInput.length == 0) {
			usersearch.close();
            return;
		}

        if( code != usersearch.KEY_UP && code != usersearch.KEY_DOWN && code != usersearch.KEY_ENTER ){
            simpleSearch.updateList();
        }
		if ( code == usersearch.KEY_ENTER ) {
			usersearch.applySelectSearch();
		}
	}
	,showSelectList:function() {
		//$(simpleSearch.curr).data('autocomplete-open', true);
		$("#ssArea").show();
		$("#ssFrame").show();			
	}
	,selectSearchOver:function(obj){
		$(obj).addClass("highlight");
	}
	,selectSearchOut:function(obj){
		$(obj).removeClass("highlight");
	}
	,selectSearchMouseOver:function(obj){
        $(obj).addClass("highlight");
		usersearch.pos = parseInt(obj.id.substr(14, obj.id.length));
		usersearch.updateSelectList();
	}
	,updateSelectList:function(){
        //log.debug("simpleSearch.pos : " + simpleSearch.pos);
		// 현재 선택된 리스트를 강조표시한다.
		for(var i=0 ; i < usersearch.listcount ; i++ ) {
			if ( i != usersearch.pos ) {
				usersearch.selectSearchOut( document.getElementById('SearchListItem'+i) );
			} else {
				usersearch.selectSearchOver( document.getElementById('SearchListItem'+simpleSearch.pos) );
			}
		}
	}
	/**
		자동완성 데이터 목록을 갱신
	*/
	,updateList:function(){
		
		//log.debug("updateList");
        var lastWord = usersearch.getCurrentInput();
        if(lastWord.length == 0) return;

    //    simpleSearch.pos = 0;
		usersearch.simpleSearchArea = document.getElementById("simpleSearchArea");
		//simpleSearch.simpleSearchArea.innerHTML = simpleSearch.createSelectList();
		//alert(simpleSearch.simpleSearchArea.innerHTML);
		usersearch.updateSelectList();
		usersearch.showSelectList();

	}

	,getAllList:function(){
		var rcpt = simpleSearch.getCurrentInput();		       
		simpleSearch.getActbList( rcpt );
	}

	,setCaret:function(obj,l){
		obj.focus();
		if (obj.setSelectionRange){
			obj.setSelectionRange(l,l);
		}else if(obj.createTextRange){
			m = obj.createTextRange();
			m.moveStart('character',l);
			m.collapse();
			m.select();
		}
	}	
	/**
		현재 선택된 값을 수신자창에 반영
	*/
	,applySelectSearch:function(){
        //alert("------");
		//$(simpleSearch.curr).data('autocomplete-open', false);

		var curInput = usersearch.getCurrentInput();

        var bShow = $("#ssArea").is(":visible");

        var searchKey = "all";
	    if( bShow ){
	    	searchKey = $("#SearchListItemHidden"+usersearch.pos).val();
	        //searchKey = document.getElementById('SearchListItemHidden'+simpleSearch.pos ).value;
	    }

		// var tmpObj = simpleSearch.curr;
		// var tmpObj_value = $.trim( tmpObj.value );

		//alert("tmpObj : "+ tmpObj.value );
		usersearch.pos = 0;

		usersearch.hideSelectList();
        $(usersearch.curr).blur();

		// 검색필터
        $("#ss_filter").val(searchKey);
        //log.debug("searchKey: " + searchKey);
		userList.search();
	}
	,getCurrentInput:function(){
		return $.trim( util.escapeXml1(usersearch.curr.value) );
	}
	,hideSelectList:function() {
		//$(simpleSearch.curr).data('autocomplete-open', false);
		$("#ssArea").hide();
		$("#ssFrame").hide();
	}

	/**
	 * 검색창을 닫는다.
	 */
	,close : function() {
		usersearch.chkFirstStr = '';
		usersearch.hideSelectList();
	}
};
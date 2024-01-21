/*
 * 간단검색 추천
 * 사용법 
 * 입력을 받는 객체(input 또는 textarea 에 simpleSearch.init(this) 를 선언한다.
 */
var relaysearch = {
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
        if (obj) relaysearch.curr = obj;

		relaysearch.areaWidth = relaysearch.curr.offsetWidth;

        if(!relaysearch.getCurrentInput()){
            $("#ssArea").remove();
            $("#ssFrame").remove();
			relaysearch.ssArea = null;
			relaysearch.ssFrame = null;
        }

        if (relaysearch.ssArea) {
        	//simpleSearch.updateList();
			relaysearch.showSelectList();
        } else {
			// check browser (IE 5.0)
			if (navigator.userAgent.indexOf("MSIE 5.0") != -1) {
				return;
			}

			relaysearch.curr.onkeydown = relaysearch.moveSelect;
			relaysearch.curr.onkeyup = relaysearch.checkInput;
			//simpleSearch.curr.onblur = simpleSearch.close;

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
		
		if ( code == relaysearch.KEY_ENTER ) {
			if( browser.type != browser.IE ){
				e.preventDefault();
			}
		}
		
		var showArea = $("#ssArea").is(":visible");		
		if( showArea ){
			// simpleSearch.pos 값 구하기 : start
			if ( code == relaysearch.KEY_UP || code == relaysearch.KEY_DOWN || code == relaysearch.KEY_TAB || code == relaysearch.KEY_ENTER ) {
			
				if( browser.type == browser.IE ){
					event.returnValue=false;
                } else {
					e.preventDefault();
				}
			}
			//console.log("keycode: " + code);
			if( code == relaysearch.KEY_UP ){
			    if( relaysearch.pos > 0 ){
					relaysearch.pos--;
			    }
			    else{
					relaysearch.pos = relaysearch.listcount - 1;
			    }
			}
			else if ( code == relaysearch.KEY_TAB || code == relaysearch.KEY_DOWN ) {
				
				if( relaysearch.listcount == 1 && code == relaysearch.KEY_TAB ) {
					relaysearch.applySelectSearch();
					return ;
				}
				if ( relaysearch.pos < relaysearch.listcount-1){
					relaysearch.pos++;
				} else {
					relaysearch.pos = 0;
				}
			}
            // simpleSearch.pos 값 구하기 : end
			relaysearch.updateSelectList();
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
		
		if( code == relaysearch.KEY_ESC ){
			relaysearch.close();
		    return;
		}
				
		var showArea = $("#ssArea").is(":visible");
		
		var currInput = relaysearch.getCurrentInput();
		// 입력값을 없을 경우, 자동완성 결과창을 닫는다.
		if (currInput.length == 0) {
			relaysearch.close();
            return;
		}

        if( code != relaysearch.KEY_UP && code != relaysearch.KEY_DOWN && code != relaysearch.KEY_ENTER ){
			relaysearch.updateList();
        }
		if ( code == relaysearch.KEY_ENTER ) {
			relaysearch.applySelectSearch();
		}
	}
	,showSelectList:function() {
		//$(simpleSearch.curr).data('autocomplete-open', true);
		$("#ssArea").show();
		$("#ssFrame").show();			
	}
	,hideSelectList:function() {
		//$(simpleSearch.curr).data('autocomplete-open', false);
		$("#ssArea").hide();
		$("#ssFrame").hide();
	}
	,selectSearchOver:function(obj){
		$(obj).addClass("highlight");
	}
	,selectSearchOut:function(obj){
		$(obj).removeClass("highlight");
	}
	,selectSearchMouseOver:function(obj){
        $(obj).addClass("highlight");
		relaysearch.pos = parseInt(obj.id.substr(14, obj.id.length));
		relaysearch.updateSelectList();
	}
	,updateSelectList:function(){
        //log.debug("simpleSearch.pos : " + simpleSearch.pos);
		// 현재 선택된 리스트를 강조표시한다.
		for(var i=0 ; i < relaysearch.listcount ; i++ ) {
			if ( i != relaysearch.pos ) {
				relaysearch.selectSearchOut( document.getElementById('SearchListItem'+i) );
			} else {
				relaysearch.selectSearchOver( document.getElementById('SearchListItem'+relaysearch.pos) );
			}
		}
	}
	/**
		자동완성 데이터 목록을 갱신
	*/
	,updateList:function(){
		
		//log.debug("updateList");
        var lastWord = relaysearch.getCurrentInput();
        if(lastWord.length == 0) return;

    //    simpleSearch.pos = 0;
		relaysearch.simpleSearchArea = document.getElementById("simpleSearchArea");
		//simpleSearch.simpleSearchArea.innerHTML = simpleSearch.createSelectList();
		//alert(simpleSearch.simpleSearchArea.innerHTML);
		relaysearch.updateSelectList();
		relaysearch.showSelectList();

	}
	,getAllList:function(){
		var rcpt = relaysearch.getCurrentInput();
		relaysearch.getActbList( rcpt );
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

		var curInput = relaysearch.getCurrentInput();

        var bShow = $("#ssArea").is(":visible");

        var searchKey = "all";
	    if( bShow ){
	    	searchKey = $("#SearchListItemHidden"+relaysearch.pos).val();
	        //searchKey = document.getElementById('SearchListItemHidden'+simpleSearch.pos ).value;
	    }

		// var tmpObj = simpleSearch.curr;
		// var tmpObj_value = $.trim( tmpObj.value );

		//alert("tmpObj : "+ tmpObj.value );
		relaysearch.pos = 0;

		relaysearch.hideSelectList();
        $(relaysearch.curr).blur();

		// 검색필터
        $("#ss_filter").val(searchKey);
        //log.debug("searchKey: " + searchKey);
		relay_list.search();
	}
	,getCurrentInput:function(){
		return $.trim( util.escapeXml1(relaysearch.curr.value) );
	}

	/**
	 * 검색창을 닫는다.
	 */
	,close : function() {
		relaysearch.chkFirstStr = '';
		relaysearch.hideSelectList();
	}
};
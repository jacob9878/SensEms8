/*
 * 간단검색 추천
 * 사용법 
 * 입력을 받는 객체(input 또는 textarea 에 simpleSearch.init(this) 를 선언한다.
 */
var rejectsearch = {
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
        if (obj) rejectsearch.curr = obj;

		rejectsearch.areaWidth = rejectsearch.curr.offsetWidth;

        if(!rejectsearch.getCurrentInput()){
            $("#ssArea").remove();
            $("#ssFrame").remove();
			rejectsearch.ssArea = null;
			rejectsearch.ssFrame = null;
        }

        if (rejectsearch.ssArea) {
        	//simpleSearch.updateList();
			rejectsearch.showSelectList();
        } else {
			// check browser (IE 5.0)
			if (navigator.userAgent.indexOf("MSIE 5.0") != -1) {
				return;
			}

			rejectsearch.curr.onkeydown = rejectsearch.moveSelect;
			rejectsearch.curr.onkeyup = rejectsearch.checkInput;
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
		
		if ( code == rejectsearch.KEY_ENTER ) {
			if( browser.type != browser.IE ){
				e.preventDefault();
			}
		}
		
		var showArea = $("#ssArea").is(":visible");		
		if( showArea ){
			// simpleSearch.pos 값 구하기 : start
			if ( code == rejectsearch.KEY_UP || code == rejectsearch.KEY_DOWN || code == rejectsearch.KEY_TAB || code == rejectsearch.KEY_ENTER ) {
			
				if( browser.type == browser.IE ){
					event.returnValue=false;
                } else {
					e.preventDefault();
				}
			}
			//console.log("keycode: " + code);
			if( code == rejectsearch.KEY_UP ){
			    if( rejectsearch.pos > 0 ){
					rejectsearch.pos--;
			    }
			    else{
					rejectsearch.pos = rejectsearch.listcount - 1;
			    }
			}
			else if ( code == rejectsearch.KEY_TAB || code == rejectsearch.KEY_DOWN ) {
				
				if( rejectsearch.listcount == 1 && code == rejectsearch.KEY_TAB ) {
					rejectsearch.applySelectSearch();
					return ;
				}
				if ( rejectsearch.pos < rejectsearch.listcount-1){
					rejectsearch.pos++;
				} else {
					rejectsearch.pos = 0;
				}
			}
            // simpleSearch.pos 값 구하기 : end
			rejectsearch.updateSelectList();
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
		
		if( code == rejectsearch.KEY_ESC ){
			rejectsearch.close();
		    return;
		}
				
		var showArea = $("#ssArea").is(":visible");
		
		var currInput = rejectsearch.getCurrentInput();
		// 입력값을 없을 경우, 자동완성 결과창을 닫는다.
		if (currInput.length == 0) {
			rejectsearch.close();
            return;
		}

        if( code != rejectsearch.KEY_UP && code != rejectsearch.KEY_DOWN && code != rejectsearch.KEY_ENTER ){
			rejectsearch.updateList();
        }
		if ( code == rejectsearch.KEY_ENTER ) {
			rejectsearch.applySelectSearch();
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
		rejectsearch.pos = parseInt(obj.id.substr(14, obj.id.length));
		rejectsearch.updateSelectList();
	}
	,updateSelectList:function(){
        //log.debug("simpleSearch.pos : " + simpleSearch.pos);
		// 현재 선택된 리스트를 강조표시한다.
		for(var i=0 ; i < rejectsearch.listcount ; i++ ) {
			if ( i != rejectsearch.pos ) {
				rejectsearch.selectSearchOut( document.getElementById('SearchListItem'+i) );
			} else {
				rejectsearch.selectSearchOver( document.getElementById('SearchListItem'+rejectsearch.pos) );
			}
		}
	}
	/**
		자동완성 데이터 목록을 갱신
	*/
	,updateList:function(){
		
		//log.debug("updateList");
        var lastWord = rejectsearch.getCurrentInput();
        if(lastWord.length == 0) return;

    //    simpleSearch.pos = 0;
		rejectsearch.simpleSearchArea = document.getElementById("simpleSearchArea");
		//simpleSearch.simpleSearchArea.innerHTML = simpleSearch.createSelectList();
		//alert(simpleSearch.simpleSearchArea.innerHTML);
		rejectsearch.updateSelectList();
		rejectsearch.showSelectList();

	}
	,getAllList:function(){
		var rcpt = rejectsearch.getCurrentInput();
		rejectsearch.getActbList( rcpt );
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

		var curInput = rejectsearch.getCurrentInput();

        var bShow = $("#ssArea").is(":visible");

        var searchKey = "all";
	    if( bShow ){
	    	searchKey = $("#SearchListItemHidden"+rejectsearch.pos).val();
	        //searchKey = document.getElementById('SearchListItemHidden'+simpleSearch.pos ).value;
	    }

		// var tmpObj = simpleSearch.curr;
		// var tmpObj_value = $.trim( tmpObj.value );

		//alert("tmpObj : "+ tmpObj.value );
		rejectsearch.pos = 0;

		rejectsearch.hideSelectList();
        $(rejectsearch.curr).blur();

		// 검색필터
        $("#ss_filter").val(searchKey);
        //log.debug("searchKey: " + searchKey);
		reject_list.search();
	}
	,getCurrentInput:function(){
		return $.trim( util.escapeXml1(rejectsearch.curr.value) );
	}

	/**
	 * 검색창을 닫는다.
	 */
	,close : function() {
		rejectsearch.chkFirstStr = '';
		rejectsearch.hideSelectList();
	}
};
/*
 * 간단검색 추천
 * 사용법 
 * 입력을 받는 객체(input 또는 textarea 에 simpleSearch.init(this) 를 선언한다.
 */
var testsendsearch = {
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
        if (obj) testsendsearch.curr = obj;

		testsendsearch.areaWidth = testsendsearch.curr.offsetWidth;

        if(!testsendsearch.getCurrentInput()){
            $("#ssArea").remove();
            $("#ssFrame").remove();
			testsendsearch.ssArea = null;
			testsendsearch.ssFrame = null;
        }

        if (testsendsearch.ssArea) {
        	//simpleSearch.updateList();
			testsendsearch.showSelectList();
        } else {
			// check browser (IE 5.0)
			if (navigator.userAgent.indexOf("MSIE 5.0") != -1) {
				return;
			}

			testsendsearch.curr.onkeydown = testsendsearch.moveSelect;
			testsendsearch.curr.onkeyup = testsendsearch.checkInput;
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
		
		if ( code == testsendsearch.KEY_ENTER ) {
			if( browser.type != browser.IE ){
				e.preventDefault();
			}
		}
		
		var showArea = $("#ssArea").is(":visible");		
		if( showArea ){
			// simpleSearch.pos 값 구하기 : start
			if ( code == testsendsearch.KEY_UP || code == testsendsearch.KEY_DOWN || code == testsendsearch.KEY_TAB || code == testsendsearch.KEY_ENTER ) {
			
				if( browser.type == browser.IE ){
					event.returnValue=false;
                } else {
					e.preventDefault();
				}
			}
			//console.log("keycode: " + code);
			if( code == testsendsearch.KEY_UP ){
			    if( testsendsearch.pos > 0 ){
					testsendsearch.pos--;
			    }
			    else{
					testsendsearch.pos = testsendsearch.listcount - 1;
			    }
			}
			else if ( code == testsendsearch.KEY_TAB || code == testsendsearch.KEY_DOWN ) {
				
				if( testsendsearch.listcount == 1 && code == testsendsearch.KEY_TAB ) {
					testsendsearch.applySelectSearch();
					return ;
				}
				if ( testsendsearch.pos < testsendsearch.listcount-1){
					testsendsearch.pos++;
				} else {
					testsendsearch.pos = 0;
				}
			}
            // simpleSearch.pos 값 구하기 : end
			testsendsearch.updateSelectList();
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
		
		if( code == testsendsearch.KEY_ESC ){
			testsendsearch.close();
		    return;
		}
				
		var showArea = $("#ssArea").is(":visible");
		
		var currInput = testsendsearch.getCurrentInput();
		// 입력값을 없을 경우, 자동완성 결과창을 닫는다.
		if (currInput.length == 0) {
			testsendsearch.close();
            return;
		}

        if( code != testsendsearch.KEY_UP && code != testsendsearch.KEY_DOWN && code != testsendsearch.KEY_ENTER ){
			testsendsearch.updateList();
        }
		if ( code == testsendsearch.KEY_ENTER ) {
			testsendsearch.applySelectSearch();
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
		testsendsearch.pos = parseInt(obj.id.substr(14, obj.id.length));
		testsendsearch.updateSelectList();
	}
	,updateSelectList:function(){
        //log.debug("simpleSearch.pos : " + simpleSearch.pos);
		// 현재 선택된 리스트를 강조표시한다.
		for(var i=0 ; i < testsendsearch.listcount ; i++ ) {
			if ( i != testsendsearch.pos ) {
				testsendsearch.selectSearchOut( document.getElementById('SearchListItem'+i) );
			} else {
				testsendsearch.selectSearchOver( document.getElementById('SearchListItem'+testsendsearch.pos) );
			}
		}
	}
	/**
		자동완성 데이터 목록을 갱신
	*/
	,updateList:function(){
		
		//log.debug("updateList");
        var lastWord = testsendsearch.getCurrentInput();
        if(lastWord.length == 0) return;

    //    simpleSearch.pos = 0;
		testsendsearch.simpleSearchArea = document.getElementById("simpleSearchArea");
		//simpleSearch.simpleSearchArea.innerHTML = simpleSearch.createSelectList();
		//alert(simpleSearch.simpleSearchArea.innerHTML);
		testsendsearch.updateSelectList();
		testsendsearch.showSelectList();

	}
	,getAllList:function(){
		var rcpt = testsendsearch.getCurrentInput();
		testsendsearch.getActbList( rcpt );
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

		var curInput = testsendsearch.getCurrentInput();

        var bShow = $("#ssArea").is(":visible");

        var searchKey = "all";
	    if( bShow ){
	    	searchKey = $("#SearchListItemHidden"+testsendsearch.pos).val();
	        //searchKey = document.getElementById('SearchListItemHidden'+simpleSearch.pos ).value;
	    }

		// var tmpObj = simpleSearch.curr;
		// var tmpObj_value = $.trim( tmpObj.value );

		//alert("tmpObj : "+ tmpObj.value );
		testsendsearch.pos = 0;

		testsendsearch.hideSelectList();
        $(testsendsearch.curr).blur();

		// 검색필터
        $("#ss_filter").val(searchKey);
        //log.debug("searchKey: " + searchKey);
		send_resultList.search();
	}
	,getCurrentInput:function(){
		return $.trim( util.escapeXml1(testsendsearch.curr.value) );
	}

	/**
	 * 검색창을 닫는다.
	 */
	,close : function() {
		testsendsearch.chkFirstStr = '';
		testsendsearch.hideSelectList();
	}
};
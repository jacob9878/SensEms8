/*
 * 간단검색 추천
 * 사용법 
 * 입력을 받는 객체(input 또는 textarea 에 simpleSearch.init(this) 를 선언한다.
 */
var imagesearch = {
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
        if (obj) imagesearch.curr = obj;

		imagesearch.areaWidth = imagesearch.curr.offsetWidth;

        if(!imagesearch.getCurrentInput()){
            $("#ssArea").remove();
            $("#ssFrame").remove();
			imagesearch.ssArea = null;
			imagesearch.ssFrame = null;
        }

        if (imagesearch.ssArea) {
        	//simpleSearch.updateList();
			imagesearch.showSelectList();
        } else {
			// check browser (IE 5.0)
			if (navigator.userAgent.indexOf("MSIE 5.0") != -1) {
				return;
			}

			imagesearch.curr.onkeydown = imagesearch.moveSelect;
			imagesearch.curr.onkeyup = imagesearch.checkInput;
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
		
		if ( code == imagesearch.KEY_ENTER ) {
			if( browser.type != browser.IE ){
				e.preventDefault();
			}
		}
		
		var showArea = $("#ssArea").is(":visible");		
		if( showArea ){
			// simpleSearch.pos 값 구하기 : start
			if ( code == imagesearch.KEY_UP || code == imagesearch.KEY_DOWN || code == imagesearch.KEY_TAB || code == imagesearch.KEY_ENTER ) {
			
				if( browser.type == browser.IE ){
					event.returnValue=false;
                } else {
					e.preventDefault();
				}
			}
			//console.log("keycode: " + code);
			if( code == imagesearch.KEY_UP ){
			    if( imagesearch.pos > 0 ){
					imagesearch.pos--;
			    }
			    else{
					imagesearch.pos = imagesearch.listcount - 1;
			    }
			}
			else if ( code == imagesearch.KEY_TAB || code == imagesearch.KEY_DOWN ) {
				
				if( imagesearch.listcount == 1 && code == imagesearch.KEY_TAB ) {
					imagesearch.applySelectSearch();
					return ;
				}
				if ( imagesearch.pos < imagesearch.listcount-1){
					imagesearch.pos++;
				} else {
					imagesearch.pos = 0;
				}
			}
            // simpleSearch.pos 값 구하기 : end
			imagesearch.updateSelectList();
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
		
		if( code == imagesearch.KEY_ESC ){
			imagesearch.close();
		    return;
		}
				
		var showArea = $("#ssArea").is(":visible");
		
		var currInput = imagesearch.getCurrentInput();
		// 입력값을 없을 경우, 자동완성 결과창을 닫는다.
		if (currInput.length == 0) {
			imagesearch.close();
            return;
		}

        if( code != imagesearch.KEY_UP && code != imagesearch.KEY_DOWN && code != imagesearch.KEY_ENTER ){
			imagesearch.updateList();
        }
		if ( code == imagesearch.KEY_ENTER ) {
			imagesearch.applySelectSearch();
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
		imagesearch.pos = parseInt(obj.id.substr(14, obj.id.length));
		imagesearch.updateSelectList();
	}
	,updateSelectList:function(){
        //log.debug("simpleSearch.pos : " + simpleSearch.pos);
		// 현재 선택된 리스트를 강조표시한다.
		for(var i=0 ; i < imagesearch.listcount ; i++ ) {
			if ( i != imagesearch.pos ) {
				imagesearch.selectSearchOut( document.getElementById('SearchListItem'+i) );
			} else {
				imagesearch.selectSearchOver( document.getElementById('SearchListItem'+imagesearch.pos) );
			}
		}
	}
	/**
		자동완성 데이터 목록을 갱신
	*/
	,updateList:function(){
		
		//log.debug("updateList");
        var lastWord = imagesearch.getCurrentInput();
        if(lastWord.length == 0) return;

    //    simpleSearch.pos = 0;
		imagesearch.simpleSearchArea = document.getElementById("simpleSearchArea");
		//simpleSearch.simpleSearchArea.innerHTML = simpleSearch.createSelectList();
		//alert(simpleSearch.simpleSearchArea.innerHTML);
		imagesearch.updateSelectList();
		imagesearch.showSelectList();

	}
	,getAllList:function(){
		var rcpt = imagesearch.getCurrentInput();
		imagesearch.getActbList( rcpt );
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

		var curInput = imagesearch.getCurrentInput();

        var bShow = $("#ssArea").is(":visible");

        var searchKey = "all";
	    if( bShow ){
	    	searchKey = $("#SearchListItemHidden"+imagesearch.pos).val();
	        //searchKey = document.getElementById('SearchListItemHidden'+simpleSearch.pos ).value;
	    }

		// var tmpObj = simpleSearch.curr;
		// var tmpObj_value = $.trim( tmpObj.value );

		//alert("tmpObj : "+ tmpObj.value );
		imagesearch.pos = 0;

		imagesearch.hideSelectList();
        $(imagesearch.curr).blur();

		// 검색필터
        $("#ss_filter").val(searchKey);
        //log.debug("searchKey: " + searchKey);
		imageList.search();
	}
	,getCurrentInput:function(){
		return $.trim( util.escapeXml1(imagesearch.curr.value) );
	}

	/**
	 * 검색창을 닫는다.
	 */
	,close : function() {
		imagesearch.chkFirstStr = '';
		imagesearch.hideSelectList();
	}
};
/*
 * 간단검색 추천
 * 사용법 
 * 입력을 받는 객체(input 또는 textarea 에 simpleSearch.init(this) 를 선언한다.
 */
var simpleSearch = {
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
        if (obj) simpleSearch.curr = obj;

        simpleSearch.areaWidth = simpleSearch.curr.offsetWidth;

        if(!simpleSearch.getCurrentInput()){
            $("#ssArea").remove();
            $("#ssFrame").remove();
            simpleSearch.ssArea = null;
            simpleSearch.ssFrame = null;
        }

        if (simpleSearch.ssArea) {
        	//simpleSearch.updateList();
			simpleSearch.showSelectList();
        } else {
			// check browser (IE 5.0)
			if (navigator.userAgent.indexOf("MSIE 5.0") != -1) {
				return;
			}

			simpleSearch.curr.onkeydown = simpleSearch.moveSelect;
			simpleSearch.curr.onkeyup = simpleSearch.checkInput;
			//simpleSearch.curr.onblur = simpleSearch.close;

			simpleSearch.createSelectListLayer();
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
		
		if ( code == simpleSearch.KEY_ENTER ) {
			if( browser.type != browser.IE ){
				e.preventDefault();
			}
		}
		
		var showArea = $("#ssArea").is(":visible");		
		if( showArea ){
			// simpleSearch.pos 값 구하기 : start
			if ( code == simpleSearch.KEY_UP || code == simpleSearch.KEY_DOWN || code == simpleSearch.KEY_TAB || code == simpleSearch.KEY_ENTER ) {
			
				if( browser.type == browser.IE ){
					event.returnValue=false;
                } else {
					e.preventDefault();
				}
			}
			//console.log("keycode: " + code);
			if( code == simpleSearch.KEY_UP ){			    
			    if( simpleSearch.pos > 0 ){
			        simpleSearch.pos--;
			    }
			    else{
			        simpleSearch.pos = simpleSearch.listcount - 1;			        
			    }
			}
			else if ( code == simpleSearch.KEY_TAB || code == simpleSearch.KEY_DOWN ) {
				
				if( simpleSearch.listcount == 1 && code == simpleSearch.KEY_TAB ) {
					simpleSearch.applySelectSearch();
					return ;
				}
				if ( simpleSearch.pos < simpleSearch.listcount-1){
					simpleSearch.pos++;
				} else {
					simpleSearch.pos = 0;
				}
			}
            // simpleSearch.pos 값 구하기 : end
            simpleSearch.updateSelectList();
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
		
		if( code == simpleSearch.KEY_ESC ){
		    simpleSearch.close();
		    return;
		}
				
		var showArea = $("#ssArea").is(":visible");
		
		var currInput = simpleSearch.getCurrentInput();
		// 입력값을 없을 경우, 자동완성 결과창을 닫는다.
		if (currInput.length == 0) {
            simpleSearch.close();
            return;
		}

        if( code != simpleSearch.KEY_UP && code != simpleSearch.KEY_DOWN && code != simpleSearch.KEY_ENTER ){
            simpleSearch.updateList();
        }
		if ( code == simpleSearch.KEY_ENTER ) {
			simpleSearch.applySelectSearch();
		}
	}
	/**
	 * 자동완성 레이어 생성 
	 */
	,createSelectListLayer:function(){
		
		var autoCompleteDiv = $(simpleSearch.curr).parent();
		/* 자동완성 레이어가 붙을 위치 */
		if(simpleSearch.areaAppendId){
			autoCompleteDiv = $('#'+simpleSearch.areaAppendId).parent();
		}
		
		// backgound frame
		simpleSearch.ssFrame = document.createElement('iframe');
		simpleSearch.ssFrame.id = "ssFrame";
		//simpleSearch.ssFrame.style.top = ( simpleSearch.curr.offsetHeight - 1 ) + 'px';
		simpleSearch.ssFrame.style.left = '0px';
		simpleSearch.ssFrame.style.width = '100%';
		simpleSearch.ssFrame.setAttribute("frameBorder","0");
		
		// addr
		simpleSearch.ssArea = document.createElement("div");
		simpleSearch.ssArea.id = 'ssArea';
		//simpleSearch.ssArea.style.top = ( simpleSearch.curr.offsetHeight - 1 ) + 'px';
		simpleSearch.ssArea.style.left = '0px';
		simpleSearch.ssArea.style.width = '100%';
        //simpleSearch.ssArea.setAttribute("style", "text-align:left; color:red; border: 1px solid blue;");
        //simpleSearch.ssArea.setAttribute("style", "color:red; border: 1px solid blue;");

		
		// create selectlist area
		simpleSearch.simpleSearchArea = document.createElement('div');
		simpleSearch.simpleSearchArea.id = 'simpleSearchArea';
				
		// append to body
		simpleSearch.ssArea.appendChild(simpleSearch.simpleSearchArea);
		//$(autoCompleteDiv).append(simpleSearch.ssFrame);
		$(autoCompleteDiv).append(simpleSearch.ssArea);

		// $("#ssArea").append('<div id="ssMenu"></div>');
		// $("#ssMenu").append('<span class="btn_close"><button type="button" class="btn_bgtxt bk right" onclick="simpleSearch.close();return false;">'+ message_common.CM0033 +'</button></span>');
		
		// add events	자동으로 닫히는 이벤트틑 삭제.(닫기 버튼 클릭으로 변경 )
		ssArea.onmouseover = function(){
			window.clearTimeout(simpleSearch.delay_hide);
		};
		ssArea.onmouseout = function(){
			simpleSearch.delay_hide = window.setTimeout('simpleSearch.close()',simpleSearch.timeOut);
		};

		simpleSearch.hideSelectList();
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
		simpleSearch.pos = parseInt(obj.id.substr(14, obj.id.length));
		simpleSearch.updateSelectList();
	}
	,updateSelectList:function(){
        //log.debug("simpleSearch.pos : " + simpleSearch.pos);
		// 현재 선택된 리스트를 강조표시한다.
		for(var i=0 ; i < simpleSearch.listcount ; i++ ) {
			if ( i != simpleSearch.pos ) {
				simpleSearch.selectSearchOut( document.getElementById('SearchListItem'+i) );
			} else {
				simpleSearch.selectSearchOver( document.getElementById('SearchListItem'+simpleSearch.pos) );
			}
		}
	}
	/**
		자동완성 데이터 목록을 갱신
	*/
	,updateList:function(){
		
		//log.debug("updateList");
        var lastWord = simpleSearch.getCurrentInput();
        if(lastWord.length == 0) return;

    //    simpleSearch.pos = 0;
		simpleSearch.simpleSearchArea = document.getElementById("simpleSearchArea");
		//simpleSearch.simpleSearchArea.innerHTML = simpleSearch.createSelectList();
		//alert(simpleSearch.simpleSearchArea.innerHTML);
		simpleSearch.updateSelectList();
		simpleSearch.showSelectList();

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

		var curInput = simpleSearch.getCurrentInput();

        var bShow = $("#ssArea").is(":visible");

        var searchKey = "all";
	    if( bShow ){
	    	searchKey = $("#SearchListItemHidden"+simpleSearch.pos).val();
	        //searchKey = document.getElementById('SearchListItemHidden'+simpleSearch.pos ).value;
	    }

		// var tmpObj = simpleSearch.curr;
		// var tmpObj_value = $.trim( tmpObj.value );

		//alert("tmpObj : "+ tmpObj.value );
		simpleSearch.pos = 0;

		simpleSearch.hideSelectList();
        $(simpleSearch.curr).blur();

		// 검색필터
        $("#ss_filter").val(searchKey);
        //log.debug("searchKey: " + searchKey);
		resultList.search();
	}
	,getCurrentInput:function(){
		return $.trim( util.escapeXml1(simpleSearch.curr.value) );
	}

	/**
	 * 검색창을 닫는다.
	 */
	,close : function() {
		simpleSearch.chkFirstStr = '';
		simpleSearch.hideSelectList();
	}
};
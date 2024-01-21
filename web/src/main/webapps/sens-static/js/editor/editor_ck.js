;(function($, window, document, undefined){
    $.widget( "custom.editor" , {
        editor:null,
        options:defaultEditorConfig,
        config:{
            tabIndex: 5,
            language: 'ko',
            //extraPlugins : 'clipboard,tableresize,template,paper,uploadimage,notification',
            extraPlugins: 'template,images,pastefromword,pastefromexcel,insertfield,rejectcode',
            removePlugins: 'elementspath,autosave,autogrow',
            //contentsCss :  '/sens-static/css/editor/content.css',
            skin: 'moono-lisa',
            //skin : 'office2013',
            width: '100%',
            height: '490px',
            enterMode: CKEDITOR.ENTER_P,
            shiftEnterMode: CKEDITOR.ENTER_P,
            //enterMode:CKEDITOR.ENTER_DIV,
            //shiftEnterMode : CKEDITOR.ENTER_DIV,
            resize_enabled: false,
            disableNativeSpellChecker: false,
            magicline_color: '#66ccff',
            startupShowBorders: false,
            ignoreEmptyParagraph: false,
            pasteFromWordCleanupFile: false,
            pasteFromWordPromptCleanup: false,
            pasteFromWordRemoveFontStyles: false,
            pasteFromWordNumberedHeadingToList: false,
            pasteFromWordRemoveStyles: false,
            removeFormatAttributes: false,
            forcePasteAsPlainText: false,
            removeDialogTabs : 'image:Link;image:advanced;link:advanced',
            basicEntities: true,
            entities_latin: false,
            entities_greek: false,
            entities_processNumerical: false,
            fillEmptyBlocks: true,
            //*/
            entities: false,
            allowedContent: true,
            autoParagraph: false,
            /*allowedContent : {
             $1: {
             // Use the ability to specify elements as an object.
             elements: CKEDITOR.dtd,
             attributes: true,
             styles: true,
             classes: true
             }
             },*/
            disallowedContent: 'script; *[on*]',
            font_names: '굴림/굴림;굴림체/굴림체;돋움/돋움;돋움체/돋움체;바탕/바탕;바탕체/바탕체;궁서/궁서;맑은 고딕/맑은 고딕;Arial/arial;Courier New/courier new;Georgia/georgia;Tahoma/tahoma;Times New Roman/times new roman;Verdana/verdana;メイリオUI/Meiryo UI;ＭＳ Ｐゴシック/ＭＳ Ｐゴシック;ＭＳ Ｐ明朝/ＭＳ Ｐ明朝;MS UI Gothic/MS UI Gothic;ＭＳ ゴシック/ＭＳ ゴシック;ＭＳ 明朝/ＭＳ 明朝;宋体/Simsun;宋体-18030/宋体-18030;仿宋体/Simfang;黑体/Simhei;楷体/Simhei;隶书/Simli',
            font_defaultLabel: '돋움',
            fontSize_defaultLabel: '10pt',
            fontSize_sizes: '8/8pt;9/9pt;10/10pt;11/11pt;12/12pt;14/14pt;18/18pt;24/24pt;36/36pt',
            line_height: '100%;150%;200%;250%;300%',
            line_height_defaultLabel:'150%',
            filebrowserImageUploadUrl: null,
            filebrowserUploadUrl: null,
          //  filebrowserUploadMethod : 'form',
            imageUploadUrl: null,
            notification_duration: 1,
            startupFocus: false,	// 로딩시 에디터에 포커스 처리
            auto_focus: '',
            qtRows: 10, // Count of rows
            qtColumns: 10, // Count of columns
            qtBorder: '1', // Border of inserted table
            qtWidth: '500px', // Width of inserted table
            //qtStyle: { 'border-collapse' : 'collapse' },
            qtCellPadding: '0', // Cell padding table
            qtCellSpacing: '0', // Cell spacing table
            qtPreviewBorder: '1px double #bbbbbb', // preview table border
            qtPreviewSize: '10px', // Preview table cell size
            qtPreviewBackground: '#c8def4', // preview table background (hover)
            insertpre_style: 'background-color:#F8F8F8;border:1px solid #DDD;padding:10px;',
            toolbar_Full:
                [
                    ['RejectCode','InsertField','Undo', 'Redo'],
                    ['Font', 'FontSize','lineheight'],
                    ['Cut', 'Copy', 'Paste', 'PasteText', 'PasteFromWord', '-'],
                    ['Bold', 'Italic', 'Underline', 'Strike', '-', 'TextColor', 'BGColor'],
                    ['NumberedList', 'BulletedList', '-', 'Outdent', 'Indent', '-', 'HorizontalRule', 'SpecialChar'],
                    ['JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock'],
                    ['Link', 'Unlink', 'Image', 'Smiley', 'Table', 'Maximize'],
                    ['ShowBlocks', 'InsertPre', 'Source', '-', 'Template', 'Images']
                ],
            toolbar: 'Full',
            toolbarCanCollapse: false,
            uiColor: '#F5F5F5'
        },
        _create:function(){
            log.debug("editor_ck._create");
            /*
            -- PC에서 이미지 업로드 하는 부분 제거 - SJY
            if (this.options.imageupload_url != null) {
                if (this.options.imageupload_url.indexOf("?") > -1) {
                    this.config.imageUploadUrl = this.options.imageupload_url + "&type=quick";
                } else {
                    this.config.imageUploadUrl = this.options.imageupload_url + "?type=quick";
                }
            }
            this.config.filebrowserImageUploadUrl = this.config.imageUploadUrl;
            log.debug("this.config.imageUploadUrl : "+ this.config.imageUploadUrl);
            log.debug("this.config.filebrowserImageUploadUrl : "+ this.config.filebrowserImageUploadUrl);*/

            if (browser.type != browser.SAFARI) {
                this.config.removePlugins += ",pastefromword";
            }

            if( this.options.type == "simple" ||  this.options.type == "hosting"){ //?
                this.config.toolbarGroups = [
                    { name: 'font',items: [ 'Font','FontSize' ] },
                    { name: 'editing',items: [ 'Bold','Italic','Underline','Strike','-','TextColor','BGColor' ] },
                    { name: 'source', items: ['ShowBlocks','-','Source'] },
                    { name: 'inserts', 	items: [ 'Image']}
                ];
                this.config.extraPlugins = null;
            }

            // 폰트 크기,종류 설정
            this.config.font_defaultLabel = this.options.defaultFont;
            this.config.fontSize_defaultLabel = this.options.defaultFontSize + "pt";

            if( this.options.focus ){
                this.config.startupFocus = true;
                this.config.auto_focus = this.element.context.id;
            }

            var editor = CKEDITOR.replace( this.element.context.id , this.config );
            var _widget = this;
            var totalsize = 0;
            var imagecnt = 0;
            var totalcnt = 0;

            /**
             * 소스/에디터 모드 변환 시
             */
            editor.on('mode', function () {
                log.debug("ckeditor event - mode");

                if (this.mode == 'source') return;

                // mode 변경이 되면 body style 이 사라지는 문제가 있음.
                this.document.getBody().setStyles(
                {
                    "font-family" : _widget.config.font_defaultLabel,
                    "font-size" : _widget.config.fontSize_defaultLabel,
                    "line-height" : _widget.config.line_height_defaultLabel
                });

                // if (_widget.options.imageBody) {
                //     log.debug("imageBody process");
                //     this.document.getBody().setStyle('background', _widget.options.imageBody);
                // }
                // IE8 호환성보기에서 소스<->에디터 이동간에 <p>&nbsp;</p> 가 <p></p> 로 변경됨
                // <p></p>로 변경되면 에디터 화면에서 해당 코드가 보이지 않아 줄바뀜어 없어져보여 다음과 같이 처리함.
                /*
                 var content = ckeditor.document.getBody().getHtml();
                 content = content.replace(/<p><\/p>/ig,"<p>&nbsp;</p>");
                 content = content.replace(/<p> <\/p>/ig,"<p>&nbsp;</p>");
                 ckeditor.document.getBody().setHtml(content);
                 */
            });

            // br to p on paste
            editor.on('paste', function (e) {
                try {
                    if (e.data.dataValue !== 'undefined') {
                        // pastefromexcel에서 받은 내용이 있으면 추가(크롬/파이어폭스 해당) : 엑셀문서 복사/붙여넣기
                        // 이미지 태그가 있으면 바꾸지 않음
                        if(e.data.dataTransfer.getData('text/html').toLowerCase().indexOf('<img ') < 0) {
                            if (e.data.dataTransfer.getData('text/html')) {
                                e.data.dataValue = e.data.dataTransfer.getData('text/html');
                            }
                        }
                        //alert(e.data.dataTransfer.getData('text/html'));
                        // span left: -5000px 제거
                        //e.data.dataValue = e.data.dataValue.replace(/(\<br ?\/?\>)+/gi, '<p>').replace(/<span .*?left: -5000px.*?>/i, "");
                        e.data.dataValue = e.data.dataValue.replace(/<span .*?left: -5000px.*?>/i, "");
                        // font tag to span
                        e.data.dataValue = _widget.convertFontTagToSpanTag(e.data.dataValue);
                    }
                } catch (ee) {
                    console.error("editor change error");
                }
            });

            editor.on("instanceReady",function(e){
                log.debug("ckeditor event - instanceReady");
                this.document.getBody().setStyles(
                    {
                        "font-family" : _widget.config.font_defaultLabel,
                        "font-size" : _widget.config.fontSize_defaultLabel,
                        "line-height" : _widget.config.line_height_defaultLabel
                    });
            });
            if( this.options.type == "simple" ){
            editor.on('fileUploadRequest', function (e) {
            	// log.debug("ckeditor event - fileUploadRequest");
            	// var fileLoader = e.data.fileLoader;
                //
            	// imagecnt = uploadComponent.image_file_count;
            	// imagecnt++;
                //
            	// //일반 첨부파일 용량
            	// uploadComponent.nfile_size = 0; // 설정된 값 초기화
            	// uploadComponent.bfile_size = 0;// 설정된 값 초기화
                //
            	// var attachCount = uploadComponent.attachList.length;
            	// for(var i = 0 ; i < attachCount ; i++ ){
            	// 	var attach = uploadComponent.attachList[i];
            	// 	var islink = attach.islink;
                //
            	// 	if(attach.size == undefined){
            	// 		uploadComponent.nfile_size = undefined;
                //      	uploadComponent.bfile_size = undefined;
                //     }else{
                //     	if (islink == 1) {
                //     		uploadComponent.bfile_size += parseInt(attach.size);
                //     	} else {
                //     		uploadComponent.nfile_size += parseInt(attach.size);
                //     	}
                //     }
            	// }
                //
            	// //임베디드 이미지 용량 + 일반 첨부 파일 용량
            	// totalsize = fileLoader.file.size + uploadComponent.nfile_size + uploadComponent.image_file_size;
            	// totalcnt = imagecnt + attachCount;
            	// uploadComponent.image_file_size += fileLoader.file.size;
            	// uploadComponent.image_file_count = imagecnt;
                // log.debug("fileUploadRequest totalsize :" + totalsize);
                // log.debug("fileUploadRequest totalcnt :" + totalcnt);
                //
             	// if(totalsize >= uploadComponent.max_nfile_size){
             	// 	jAlert(msg.get( uploaderMessage.CM0106, uploadComponent.max_nfile_size.byteFormat(0)));
             	// 	e.cancel();
             	// }
                //
             	// if(totalcnt > uploadComponent.max_file_count){
             	// 	jAlert(msg.get( uploaderMessage.CM0104, uploadComponent.max_file_count));
             	// 	e.cancel();
             	// }
            }, null, null, 4);
            }
            editor.fire( 'load' );

            //전역변수에 dialogDefinition이벤트가 등록되어 재기호출하는 문제점관련하여 listener가 존재시 제거함.
            if (CKEDITOR.hasListeners('dialogDefinition')) {
                CKEDITOR.removeListener('dialogDefinition');
            }
            this.editor = editor;

            if( this.options.content ){
                this.content(this.options.content);
            }
            log.debug("CKEDITOR init");
        },
        _destroy:function(){
            log.debug("editor destroy : " + this.element.context.id);
            this.editor.destroy();
            this.editor = null;
            try {
                // 에디터가 destroy 된후 focus 가 이동안되는 issue 처리
                try {
                    $("<input type='text' id='focusIssue' style='width:1px;height:1px;'/>").appendTo("body").focus().remove();
                } catch (e) {
                    log.error("html input error - " + e);
                }
            } catch (e) {
                log.error("editor destroy error - " + e);
            }
            //$.Widget.prototype.destroy.call(this);
        },
        load:function(contents){
            try {
                var defaultBlock = (this.config.enterMode == CKEDITOR.ENTER_DIV) ? 'div' : 'p';
                // 본문 삽입
                // font tag to span
                contents = this.convertFontTagToSpanTag(contents);

                // 서명 입력 때문에 메일 쓰기 페이지에서 <p> 태그로 간격을 벌려줌
                contents += "<" + defaultBlock + ">&nbsp;</" + defaultBlock + ">";
                this.content(contents);
                log.debug("loadContent ok " + contents);
            } catch (e) {
                log.error("load error - " + e);
            }
        },
        content:function(content) {
            // setData는 body에 정의한 데이터가 사라진다.
            var _widget = this;
            this.editor.setData(content,function(){
                this.document.getBody().setStyles(
                    {
                        "font-family" : _widget.config.font_defaultLabel,
                        "font-size" : _widget.config.fontSize_defaultLabel,
                        "line-height" : _widget.config.line_height_defaultLabel
                    });
            });
        },
        convertFontTagToSpanTag:function(str) {
            var startIndex = str.indexOf('<font');
            while (startIndex >= 0) {

                var endIndex = str.substring(startIndex).indexOf('>');
                var subString1 = str.substring(startIndex, (startIndex + endIndex) + 1);
                var subString2 = subString1;
                var mapObj = {
                    size: "font-size:",
                    face: "font-family:",
                    color: "color:"
                };
                subString2 = subString2.replace(/font-size:/gi, 'size');
                subString2 = subString2.replace(/size|face|color/gi, function (matched) {
                    return mapObj[matched];
                });
                subString2 = subString2.replace(/style=/g, '=');
                subString2 = subString2.replace(/['"]/g, ';');
                subString2 = subString2.replace(/=;/g, '');
                subString2 = subString2.replace('font', 'span');
                if (subString2.length > 6) {
                    subString2 = [subString2.slice(0, 6), 'style=\"', subString2.slice(6)].join('');
                    subString2 = [subString2.slice(0, subString2.length - 1), '\"', subString2.slice(subString2.length - 1)].join('');
                }
                //Converting Font-size
                var sizeIndex = subString2.indexOf('font-size:');

                if (sizeIndex >= 0) {
                    var sizeEndIndex = subString2.substring(sizeIndex).indexOf(';');
                    var size = subString2.substring(sizeIndex + 10, (sizeIndex + sizeEndIndex));

                    if (Number(size) > 0) {
                        //Removing Font size
                        subString2 = subString2.slice(0, (sizeIndex + sizeEndIndex) - 1) + subString2.slice((sizeIndex + sizeEndIndex));
                        //Adding Font Size
                        subString2 = [subString2.slice(0, (sizeIndex + sizeEndIndex) - 1), editor.convertSpanFontSize(size), subString2.slice((sizeIndex + sizeEndIndex) - 1)].join('');
                    }
                }
                //end
                str = str.replace(subString1, subString2);
                startIndex = str.indexOf('<font');

            }
            str = str.replace(/font>/g, 'span>');
            return str;
        },
        /**
         * 현재 에디터에 있는 순수 데이터만 제공
         * @returns {*}
         */
        getContent:function(){
            return this.editor.getData();
        },
        /**
         * 에디터 본문에 서식유지를 위한 틀을 포함
         * @returns {string}
         */
        getBody:function(){
            var color = "#000";
            // 에디터의 기본폰트 설정 내용을 메일 보낼때 div 로 감싸서 적용함
            var fontFamily = this.config.font_defaultLabel;
            var fontSize = this.config.fontSize_defaultLabel;
            var lineHeight = this.config.line_height_defaultLabel;

            var content = this.editor.getData();
            content = content.replaceAll("\n\n", "\n");
            content = EditorUtil.removeTag(content);

            var body = "";
            var sensEdContentAreaCls = "<div class=\"sensEdContentAreaCls\" data-id=\"sensEdContentArea\" style=\"color:" + color + ";line-height:"+ lineHeight + ";font-size:" + fontSize + ";font-family:" + fontFamily + " !important;padding:0;margin:0;\">\n";;
            var isAppendEdContentCls = false;
            // 게시판 및 서명,부재중응답 및 기존 데이터가 있는 경우에는 기존에 감싼은 전체를 감싸는 태그를 한번만 사용하도록 한다.
            if( this.options.type == "simple" || this.options.content ) {
                // 소스를 div로 자꾸 감싸는거 방지
                var tmp = content.substring(0, 100);
                if (tmp.indexOf("<div class=\"sensEdContentAreaCls\"") < 0) {
                    isAppendEdContentCls = true;
                    body += sensEdContentAreaCls;
                } else {
                    var dom = document.getElementById("div_Text");
                    dom.innerHTML = content;
                    $(dom).filter(".sensEdContentAreaCls").css("color", color).css("font-size", fontSize).css("font-family", fontFamily);
                    content = dom.innerHTML;
                }
            }else{
                // 메일은 저장할때마다 무조건 DIV 로 감싸도록 한다.
                body += sensEdContentAreaCls
            }

            // 편지지
            if (this.options.imageTop != '' || this.options.imageBody != '' || this.options.imageBottom != '') {
                body += '<table style="border:0;border-spacing:0;border-collapse:collapse;width:' + options.imageWidth + 'px;">';
                body += '<tbody>';
                if (this.options.imageTop != '') {
                    body += '<tr><td valign="top" style="padding:0px;">';
                    body += '<img src="' + this.options.imageTop + '" style="border:0;display:block;" alt="image_top" />';
                    body += '</td></tr>';
                }
                body += '<tr>';
                body += '<td style="min-height:300px;background:' + this.options.imageBody + ';word-break:break-all;padding:0 50px;vertical-align:top;">';
            }


            // 에디터에선 <p></p> 가 한칸 띄어쓰기로 보이지만 실제 메일을 읽을 때에는 줄간격이 떨어지지 않는다.
            // 그래더 <p></p> 사이에 &nbsp; 를 추가한다.
            content = content.replace(/<p><\/p>/g, "<p>&nbsp;</p>");
            content = content.replace(/<p> <\/p>/g, "<p>&nbsp;</p>");
            content = content.replace(/<div><\/div>/g, "<div>&nbsp;</div>");
            content = content.replace(/<div> <\/div>/g, "<div>&nbsp;</div>");

            body += content;
            body += "\n";
            // 편지지
            if (this.options.imageTop != '' || this.options.imageBody != '' || this.options.imageBottom != '') {
                body += '</td></tr>';
                if (this.options.imageBottom != '') {
                    body += '<tr><td style="padding:0px;"><img src="' + this.options.imageBottom + '" style="border:0;display:block;" alt="image_bottom" /><td></tr>';
                }
                body += '</tbody></table>';
            }
            if( isAppendEdContentCls ) {
                body += "</div>";
            }
            return body;
        },
        /**
         * 에디터내용을 HTML 구조로 제공
         * <html><head><body>...</body></head></html>
         * @returns {string}
         */
        getHtml:function(){
            var body;
            if( this.options.type == 'simple'){
                body = this.getBody();
            }else{
                body = "<html>";
                body += "<head>";
                body += "<style type=\"text/css\">\n";
                body += "p {padding:0;margin:0;}\n";
                body += "</style>\n";
                body += "</head>";
                body += "<body>";
                body += this.getBody();
                body += "</body>";
                body += "</html>";
            }
            return body;
        },
        getText:function(){
            try {
                var content = this.editor.getData();
                content = content.replace(/\n\n/g, "\n");//줄바꿈이 textarea 에서는 2칸으로 보이는걸 한칸으로 보이도록 수정
                // content = content.replace(/\/g, "");//엔터제거

                var dom = document.getElementById("div_Text");
                dom.innerHTML = content;
                // 본문의 content에 스타일을 제거하기 위한 작업 추가 by sunggyu
                $(dom).find("head").remove();
                $(dom).find("script").remove();
                $(dom).find("style").remove();
                $(dom).find("iframe").remove();
                $(dom).find("object").remove();
                $(dom).find("bgsound").remove();
                $(dom).find("base").remove();
                $(dom).find("background").remove();
                return (dom.innerText || dom.textContent);
            } catch (e) {
                return '';
            }
        },
        images:function(view_url, image_width, image_height){
            log.debug("images");
            var doc = this.editor.document.getById("editHtml");
            var bodyContent;
            if( doc ) {
                bodyContent = doc.getHtml();
            }
            var body = "";
            body += '<img src="'+ view_url +'" style="width:'+ image_width +'px; height:'+image_height+'px; border:0;display:block;" alt="top" />';
            body += "<div id='editorContent'>";

            if( bodyContent ){
                body += bodyContent;
            }else{
                body += this.getContent();
            }
            body += "</div>";
            this.content(body);
        },
        reject:function(reject){
            log.debug("reject");
            var doc = this.editor.document.getById("editHtml");
            var bodyContent;
            if( doc ) {
                bodyContent = doc.getHtml();
            }
            var body = "";

            body += "<div id='editorContent'>";
            if( bodyContent ){
                body += bodyContent;
            }else{
                body += this.getContent();
            }
            body += '<span>'+reject+'</span>';
            body += "</div>";
            this.content(body);
        },
        preview:function(){
            var color = "#000";
            // 에디터의 기본폰트 설정 내용을 메일 보낼때 div 로 감싸서 적용함
            var fontSize = this.config.fontSize_defaultLabel;
            var fontFamily = this.config.font_defaultLabel;
            var lineHeight = this.config.line_height_defaultLabel;

            var body = "";
            var content = this.editor.getData();
            // 소스를 div로 자꾸 감싸는거 방지
            var tmp = content.substring(0,100);
            if(tmp.indexOf("<div class=\"sensEdContentAreaCls\"") < 0){
                body += "<div class=\"sensEdContentAreaCls\" data-id=\"sensEdContentArea\" style=\"color:"+ color+";line-height:"+lineHeight+";font-size:"+ fontSize +";font-family:"+ fontFamily +" !important;background-color: transparent;word-break: break-all;\">\n";
            } else {
                var dom = document.getElementById("div_Text");
                dom.innerHTML = content;
                $(dom).filter(".sensEdContentAreaCls").css("color", color);
                $(dom).filter(".sensEdContentAreaCls").css("font-size", fontSize);
                $(dom).filter(".sensEdContentAreaCls").css("font-family", fontFamily);
                $(dom).filter(".sensEdContentAreaCls").css("line-height", lineHeight);

                content = dom.innerHTML;
            }

            // 편지지
            if(this.options.imageTop != '' || this.options.imageBody != '' || this.options.imageBottom != ''){
                body += '<table style="border:0;border-spacing:0;border-collapse:collapse;width:'+this.options.imageWidth+'px;">';
                body += '<tbody>';
                if(this.options.imageTop != ''){
                    body += '<tr><td valign="top" style="padding:0px;">';
                    body += '<img src="'+this.options.imageTop+'" style="border:0;display:block;" alt="image_top" />';
                    body += '</td></tr>';
                }
                body += '<tr>';
                body += '<td style="min-height:300px;background:'+this.options.imageBody+';word-break:break-all;padding:0 50px;vertical-align:top;">';
            }


            // 에디터에선 <p></p> 가 한칸 띄어쓰기로 보이지만 실제 메일을 읽을 때에는 줄간격이 떨어지지 않는다.
            // 그래더 <p></p> 사이에 &nbsp; 를 추가한다.
            content = content.replace(/<p><\/p>/g,"<p>&nbsp;</p>");
            content = content.replace(/<p> <\/p>/g,"<p>&nbsp;</p>");

            body += content;
            body += "\n";
            // 편지지
            if(this.options.imageTop != '' || this.options.imageBody != '' || this.options.imageBottom != ''){
                body += '</td></tr>';
                if(this.options.imageBottom != ''){
                    body += '<tr><td style="padding:0px;"><img src="'+this.options.imageBottom+'" style="border:0;display:block;" alt="image_bottom" /><td></tr>';
                }
                body += '</tbody></table>';
            }
            body += "</div>";
            return body;
        },
        imageDisable:function(){
            log.debug("imageDisable");
            var doc = this.editor.document.getById("editorContent");
            //var bodyContent = doc.getHtml();
            // this.content("bodyContent");
            this.content("");
        },
        append:function(content){
            if( this.editor.mode != 'wysiwyg') return;
            this.editor.insertHtml(content);
        },
        /**
         * 에디터가 생성되어있는가?
         * @returns {boolean}
         */
        instance:function(){
            return this.editor != null;
        },
        /**
         * 에디터에 내용이 있는가?
         * @returns {boolean}
         */
        hasData:function() {
            return this.editor.getData().length > 0;
        },
        style:function( config ){
            for(var key in config ){
                this.editor.document.getBody().setStyle(key , config[key]);
            }
        },
        /**
         * 해당 아이디 노드를 제거한다.
         * @param id
         */
        removeById:function(id){
            var element = this.editor.document.getById(id);
            element.remove();
        },
        /**
    	 * 에디터 본문에 덧붙이는 거 없이 순수 태그만 가져온다.
    	 * @returns {*|string}
    	 */
        getData:function(){
            return this.editor.getData();
    	},
    	/**
    	 * 본문안에 HTML 소스를 넣는다.(템플릿 선택)
    	 */
    	setContent:function( content ){
    	    this.editor.setData(content);
    	},
        removeSign:function(){
        	var tempDiv = document.createElement("div");
        	tempDiv.innerHTML = this.editor.getData();

	 		$(tempDiv).find("#div_imo_sign").remove();
            this.setContent( tempDiv.innerHTML );

	        $(tempDiv).remove();
	 	},
        expandEditor:function(bExpand){
    	    if(bExpand) {
                //$("#div_Html").css({'position':'fixed', 'top':'160px', 'width':'100%', 'z-index':200, 'background-color':'#ffffff'});
                $(".write_header").hide();
                this.editor.resize('100%', '560', true);
            } else {
                $(".write_header").show();
                this.editor.resize('100%', '490', true);
            }
        },
        /**
         * 에디터로 focus를 이동한다.
         */
        focus:function(){
            this.editor.focus();
        },
    })
})( jQuery , window, document);
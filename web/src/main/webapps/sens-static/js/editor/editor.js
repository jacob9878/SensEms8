var defaultEditorConfig = {
    template_url: null,
    imageupload_url: null,
    imageupload_basepath: null,
    language: 'ko',
    imageBody: '',
    imageTop: '',
    imageBottom: '',
    imageWidth: '800',
    type: "normal",
    defaultFont: "돋음",
    defaultFontSize: "10",
    content: null, // 에디터가 로딩된후 에디터에안에 삽입할 글
    css: null, // css file 경로
    useImage: false, // 이미지 사용여부
    useTemplate: false, // 서식 사용여부
    focus: false, // 에디터 로딩 후 포커스 지정,
    websource: "default" // 웹소스 경로(나모에디터 전용),
};
/**
 * 에디터 이미지 플러그인
 */
var EditorImagePlugin = {
    open: function () {
        // 서식창이 떠있을 경우 서식창을 닫는다.
        if ($("#send_template").is(':visible')) {
            EditorTemplatePlugin.close();
        }
        var _this = this;
        if (!$("#send_image").is(':visible')) {
            $.get("/editor/image.do", function (body) {
                $("#editor").before(body);
                $("#send_image").show();
                _this.drawItem();
            });
        }
    },
    apply: function (view_url, img_width, img_height) {
        $("#editHtml").editor("images", view_url, img_width, img_height);
    },
    close: function () {
        $("#send_image").remove();
    },
    list: function (cpage) {
        $("#image_cpage").val(cpage);
        this.drawItem();
    },
    change_categoryImage: function () {
        $("#image_cpage").val('1');
        this.drawItem();
    },
    drawItem: function () {
        // image_inner_div에 이미 불러온 image가 있는지 확인해서 없으면 불러온다.
        var url = "/editor/json/imageList.do";
        var param = {
            "imageCategory": $("#selectImage").val(),
            "currentPage": $("#image_cpage").val()
        };
        $.ajax({
            url: url,
            data: param,
            type: "POST",
            dataType: "json",
            async: false,
            success: function (encoded) {
                var imageList = encoded.imageList;
                var imPage = encoded.imPage;
                var weburl = encoded.weburl;

                var pageHtml = pageInfoForPrevNext(imPage.cpage, imPage.pageSize, imPage.total, '', 'EditorImagePlugin.list');
                $("#image_cpage").val(imPage.cpage);
                $("#imageInfo_image").html(pageHtml); // 페이징 들어감

                var strList = [], n = -1;
                strList[++n] = "<ul>";
                if (imageList.length == 0) {
                    strList[++n] = "<li>";
                    strList[++n] = message_common.CM0012;
                    strList[++n] = "</li>";
                }
                for (var i = 0; i < imageList.length; i++) {
                    var ukey = imageList[i].ukey;
                    var view_url = "/send/image/view.do?ukey="+ ukey;
                    strList[++n] = "<li>";
                    strList[++n] = "<span style=\"cursor:pointer\" onclick=\"EditorImagePlugin.apply("
                    strList[++n] = "'"+weburl+view_url+"'";
                    strList[++n] = ",";
                    strList[++n] = "'"+imageList[i].image_width+"'";
                    strList[++n] = ",";
                    strList[++n] = "'"+imageList[i].image_height+"'";
                    strList[++n] = ");\">";
                    strList[++n] = "<img src=\"/send/image/view.do?ukey=" + ukey + "\"style=\"width:63px;height:78px;border:1px solid black;\" title=\"" + imageList[i].image_name + "\"/>";
                    strList[++n] = "</span>";
                    strList[++n] = "</li>";
                }

                strList[++n] = "</ul>";
                $("#imageList").html(strList.join(''));
            },
            error: function (xhr, txt) {
                AjaxUtil.error(xhr);
            }
        });
    },
    /*
	 * 이미지 제거
	 */
    imageRemove: function () {
        log.debug("EditorImagePlugin.paerRemove");
        $("#editHtml").editor("imageDisable");
    }
}

/**
 * 에디터 템플릿 플러그인
 * @type {{open: EditorTemplatePlugin.open, close: EditorTemplatePlugin.close, list: EditorTemplatePlugin.list, category: EditorTemplatePlugin.category, drawTemplate: EditorTemplatePlugin.drawTemplate, setContent: EditorTemplatePlugin.setContent}}
 */
var EditorTemplatePlugin = {
    open: function () {
        // 이미지창이 떠있을경우 이미지창을 닫는다.
        if ($("#send_image").is(':visible')) {
            EditorImagePlugin.close();
        }
        if (!$("#send_template").is(':visible')) {
            $.get("/editor/template.do", function (body) {
                $("#editor").before(body);
                $("#send_template").show();
                EditorTemplatePlugin.drawTemplate();
            });
        }
    },
    close: function () {
        $("#send_template").remove();
    },
    list: function (cpage) {
        $("#template_cpage").val(cpage);
        this.drawTemplate();
    },
    category: function () {
        $("#template_cpage").val('1');
        this.drawTemplate();
    },
    drawTemplate: function () {
        var url = "/editor/json/templateList.do";
        var param = {
            "templateCategory": $("#selectTemplate").val(),
            "currentPage": $("#template_cpage").val()
        };
        $.ajax({
            url: url,
            data: param,
            type: "POST",
            dataType: "json",
            async: false,
            success: function (encoded) {
                var templateList = encoded.templateList;
                var imPage = encoded.imPage;

                var pageHtml = pageInfoForPrevNext(imPage.cpage, imPage.pageSize, imPage.total, '', 'EditorTemplatePlugin.list');
                $("#template_cpage").val(imPage.cpage);
                $("#pageInfo_template").html(pageHtml); // 페이징 들어감


                var strList = [], n = -1;
                /*strList[++n] = "<ul>";*/
                if (templateList.length == 0) {
                    strList[++n] = "<li>";
                    strList[++n] = message_common.CM0013;
                    strList[++n] = "</li>";
                }
                for (var i = 0; i < templateList.length; i++) {
                    var ukey = templateList[i].ukey;
                    var date = new Date(templateList[i].regdate).format("yyyy-MM-dd HH:mm")
                    console.log("date>> " + date);
                    var preview_img = templateList[i].imageKey;

                    /*strList[++n] = "<span style=\"cursor:pointer\" onclick=\"EditorTemplatePlugin.setContent('" + ukey + "');\">";
                    if (preview_img == '') {
                        strList[++n] = "<img src=\"/sens-static/images/ko/noimg.png\" style=\"width:63px;height:78px;border:1px solid black;\" title=\"\"/>";
                    } else {
                        strList[++n] = "<img src=\"/send/template/view.do?ukey=" + ukey + "\" style=\"width:63px;height:78px;border:1px solid black;\" title=\"\"/>";
                    }
                    strList[++n] = "</span>";*/
                    strList[++n] = "<tr>";
                    strList[++n] = "<td style=\"padding-left: 10px;\">";
                    strList[++n] = "<span style=\"cursor:pointer\" onclick=\"EditorTemplatePlugin.setContent('" + ukey + "');\">";
                    strList[++n] = "<div class=\"image_name\" style=\"text-overflow: ellipsis;overflow:hidden;\" title="+templateList[i].temp_name +">" + templateList[i].temp_name + "</div>";
                    strList[++n] = "</span>";
                    strList[++n] = "</td>";
                    strList[++n] = "<td>" + date+ "</td>";
                    strList[++n] = "<td style=\"text-align: center;\">";
                    strList[++n] = "<span style=\"cursor:pointer\" onclick=\"templateList.previewTemplate('" + ukey + "');\">";
                    strList[++n] = "<span class=\"icon_public preview\" title=\"미리보기\"/>";
                    strList[++n] = "</td>";
                    strList[++n] = "</tr>";
                }
                /*strList[++n] = "</ul>";*/
                $("#templateList").html(strList.join(''));
            },
            error: function (xhr, txt) {
                AjaxUtil.error(xhr);
            }
        });
    },
    /**
     * 본문안에 HTML 소스를 넣는다.(템플릿 선택)
     */
    setContent: function (ukey) {
        var url = "/editor/json/template/content.do";
        var param = {
            "ukey": ukey,
            "templateCategory": $("#selectTemplate").val()
        };
        $.ajax({
            url: url,
            data: param,
            type: "POST",
            dataType: "json",
            async: false,
            success: function (jsonData) {
                var content = jsonData.data.content;
                $("#editHtml").editor("content", content);
                $("#template_ukey").val(ukey);
                if ($("#selectTemplate").val() == '1') {
                    $("#myTemplateKey").val(ukey);
                    if (document.editForm.subject.value == '') {
                        document.editForm.subject.value = jsonData.data.subject;
                    }
                }
            },
            error: function (xhr, txt) {
                AjaxUtil.error(xhr);
            }
        });
    },
}

var EditorUtil = {
    removeTag: function (content) {
        var tempDiv = document.createElement("div");
        tempDiv.innerHTML = content;
        $(tempDiv).find("*").removeAttr('onunload');
        $(tempDiv).find("*").removeAttr('onload');
        $(tempDiv).find("*").removeAttr('onsubmit');
        $(tempDiv).find("*").removeAttr('onreset');
        $(tempDiv).find("*").removeAttr('onselect');
        $(tempDiv).find("*").removeAttr('onresize');
        $(tempDiv).find("*").removeAttr('onmouseup');
        $(tempDiv).find("*").removeAttr('onmouseover');
        $(tempDiv).find("*").removeAttr('onmouseout');
        $(tempDiv).find("*").removeAttr('onmousemove');
        $(tempDiv).find("*").removeAttr('onmousedown');
        $(tempDiv).find("*").removeAttr('onkeyup');
        $(tempDiv).find("*").removeAttr('onkeypress');
        $(tempDiv).find("*").removeAttr('onkeydown');
        $(tempDiv).find("*").removeAttr('onfocus');
        $(tempDiv).find("*").removeAttr('onerror');
        $(tempDiv).find("*").removeAttr('ondblclick');
        $(tempDiv).find("*").removeAttr('onclick');
        $(tempDiv).find("*").removeAttr('onchange');
        $(tempDiv).find("*").removeAttr('onblur');
        $(tempDiv).find("*").removeAttr('onabort');
        return tempDiv.innerHTML;
    },
    backgroundimageSrc: function (src, repeat) {
        if (!src) {
            return '';
        }
        src = this.imageSrc(src);
        repeat = repeat == "1" ? "repeat" : "no-repeat";
        return "url('" + src + "') " + repeat;
    },
    imageSrc: function (src) {
        if (!src) {
            return '';
        }
        if (src.toLowerCase().indexOf('http') < 0) {
            var hostname = document.location.protocol + '//' + document.location.host;
            if (src.indexOf('/') == 0) src = hostname + src;
            else src = hostname + '/' + src;
        }
        return src;
    },
    rejectCodeInsert: function(){
        var rejectCode = "[REJECT]수신거부[/REJECT]";
        $("#editHtml").editor("reject", rejectCode);
    },
    fieldCodeInsert : function(field,type){
        if(type == 'editor'){
            $("#editHtml").editor("reject", field);
        }else if(type == 'title'){
            field = $("#titleFieldSelect").val();
            var msg_name = document.getElementById("msg_name") //제목

            var scroll = msg_name.scrollTop;
            var start = msg_name.selectionStart;
            var end = msg_name.selectionEnd;
            msg_name.value = msg_name.value.substring(0, start) + field
                + msg_name.value.substring(end, msg_name.value.length);
            msg_name.setSelectionRange(start+field.length, start+field.length);
            msg_name.scrollTop = scroll;

            /**
             * 이전 소스인데 if문 IE여도 타지 않아 주석 처리.
             */
            // if (document.selection) { // for IE
            //     var selection = document.selection.createRange();
            //     if (selection.text) document.selection.clear();
            //     selection.text = field;
            //     selection.select();
            // }
            // else { // for FF
            //     var scroll = msg_name.scrollTop;
            //     var start = msg_name.selectionStart;
            //     var end = msg_name.selectionEnd;
            //     msg_name.value = msg_name.value.substring(0, start) + field
            //         + msg_name.value.substring(end, msg_name.value.length);
            //     msg_name.setSelectionRange(start+field.length, start+field.length);
            //     msg_name.scrollTop = scroll;
            // }
        }
    },
    preview:function () {
        var subject = $("#msg_name").val();
        var body = $("#editHtml").editor("getBody");
        body = body.replace(/<(no)?script[^>]*>(.|\r|\n)*?<\/(no)?script>/igm, "");
        body = body.replace(/<style>(.*?)<(\/?)style>/gi,"");
        $("#preview_subject").html(subject);
        $("#preview_content").html(body);
        $("#previewLayer").show();
    },
    getBody:function () {
        var body = $("#editHtml").editor("getBody");
        body = body.replace(/<(no)?script[^>]*>(.|\r|\n)*?<\/(no)?script>/igm, "");
        body = body.replace(/<style>(.*?)<(\/?)style>/gi,"");
        return body;
    },

    /** 에디터 본문 업데이트 */
    setContent:function () {
        var content = $("#content").val();
        $("#editHtml").editor("content", content);

    }
}
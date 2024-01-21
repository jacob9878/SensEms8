<%@ page pageEncoding="UTF-8"%>
<%@ include file = "/WEB-INF/jsp/inc/taglib.jspf" %>
<%--<script type="text/javascript" src="${staticURL}/sens-static/js/util/page.js"></script>
<script type="text/javascript" src="${staticURL}/sens-static/js/messages/sendmanage_${UserInfo.language}.js"></script>
<script type="text/javascript" src="${staticURL}/sens-static/js/messages/common_${UserInfo.language}.js"></script>
<script type="text/javascript" src="${staticURL}/sens-static/plugin/jquery/jquery.json-2.4.js"></script>
<script type="text/javascript" src="${staticURL}/sens-static/plugin/jquery/ui/jquery-ui.min.js"></script>

<script type="text/javascript" src="${staticURL}/sens-static/plugin/tinymce/tinymce.min.js"></script>
<script type="text/javascript" src="${staticURL}/sens-static/js/editor/editor_tiny.js?dummy=${constant.dummy}"></script>--%>

<%--<script type="text/javascript" src="${staticURL}/sens-static/js/editor/editor.js?dummy=${constant.dummy}"></script>
<script type="text/javascript" src="${staticURL}/sens-static/plugin/ckeditor/ckeditor.js"></script>
<script type="text/javascript" src="${staticURL}/sens-static/js/editor/editor_ck.js?dummy=${constant.dummy}"></script>--%>
<div id="send_image" class="write_image">
    <input type="hidden" name="image_cpage" id="image_cpage">
    <div id="image_inner_div" class="image_inner_div">
        <p class="image_inner_btn">
<%--            style="display: block; text-align: right; padding-right: 5px;"--%>
<%--            <button type="button" class="btn_bgtxt wt pd5" onclick="EditorImagePlugin.imageRemove();">--%>
<%--                <span class="txt"><spring:message code="E0540" text="사용안함" /></span>--%>
<%--            </button>--%>
            <button type="button" class="btn_bgtxt wt pd5" onclick="EditorImagePlugin.close();">
                <span class="txt"><spring:message code="E0282" text="닫기" /></span>
            </button>
        </p>
        <select name="selectImage" id="selectImage" class="write_image_select" onchange="EditorImagePlugin.change_categoryImage();" title="<spring:message code="" text="이미지 선택" />">
            <option value="0"><spring:message code="E0098" text="전체" /></option>
            <option value="01"><spring:message code="E0541" text="공용" /></option>
            <option value="02"><spring:message code="E0542" text="개인" /></option>
        </select>
        <div class="write_image_item" id="imageList">
        </div>
        <!-- 페이징영역 -->
        <div class="list_nav_simple" id="imageInfo_image">
        </div>
    </div>
</div>
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
<script type="text/javascript" src="${staticURL}/sens-static/js/send/template.js"></script>
<!-- 서식 선택 옵션 -->
<div id="send_template" class="write_paper">
    <input type="hidden" name="paper_cpage" id="template_cpage">
    <div id="template_inner_div" class="template_inner_div">
        <p style="display: block; text-align: right; padding-right: 5px;">
            <%--<button type="button" class="" onclick="EditorImagePlugin.imageRemove();">사용안함</button>--%>
            <button type="button" class="btn_bgtxt wt pd5" onclick="EditorTemplatePlugin.close();">
                <span class="txt"><spring:message code="E0282" text="닫기" /></span>
            </button>
        </p>
        <select name="selectTemplate" id="selectTemplate" class="write_template_select" onchange="EditorTemplatePlugin.category();" title="<spring:message code="" text="템플릿 선택" />">
            <option value="0"><spring:message code="E0098" text="전체" /></option>
            <option value="01"><spring:message code="E0541" text="공용" /></option>
            <option value="02"><spring:message code="E0542" text="개인" /></option>
        </select>

        <table  width ="100%" height="auto" style="margin-top: 10px;">
            <colgroup>
                <col style="width:220px;">
                <col style="width:120px;">
                <col style="width:auto;">
            </colgroup>
            <thead class="fixed">
            <tr>
                <th class="txt"><spring:message code="E0284" text="템플릿 제목"/></th>
                <th><spring:message code="E0288" text="등록일자"/></th>
                <th><spring:message code="E0104" text="미리보기"/></th>
            </tr>
            </thead>
            <tbody id="templateList">
            </tbody>
        </table>
        <%--<div class="write_paper_item" id="templateList"></div>--%>
        <div class="list_nav_simple" id="pageInfo_template"></div>
    </div>
</div>
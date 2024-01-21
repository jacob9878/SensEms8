<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file = "../inc/common.jsp" %>
<c:if test="${ !empty infoMessage }">
	<script>
		alert( '${infoMessage}' );
	</script>
</c:if>
<%-- 에디터 구분 : start --%>
<script type="text/javascript" src="${staticURL}/sens-static/plugin/tinymce/tinymce.min.js"></script>
<script type="text/javascript" src="${staticURL}/sens-static/js/editor/editor_tiny.js?dummy=${constant.dummy}"></script>
<%--<script type="text/javascript" src="${staticURL}/sens-static/js/editor/editor.js"></script>
<script type="text/javascript" src="${staticURL}/sens-static/plugin/ckeditor/ckeditor.js"></script>
<script type="text/javascript" src="${staticURL}/sens-static/js/editor/editor_ck.js"></script>--%>
<script type="text/javascript" src="${staticURL}/sens-static/js/send/template.js?dummy=${constant.dummy}"></script>
<script type="text/javascript" src="${staticURL}/sens-static/js/uploader/plupload.js?dummy=${constant.dummy}"></script>
<script type="text/javascript" src="${staticURL}/sens-static/js/messages/sendmanage_${UserInfo.language}.js"></script>

<script type="text/javascript">
	templateEditor.editorInit();
</script>
<div class="template_add">
	<input type="hidden" id="cpage" value="${cpage}">
    <input type="hidden" id="srch_keyword" value="${srch_keyword}">
    <input type="hidden" id="srch_type" value="${srch_type}">
	<!-- top area start -->
	<div class="title_box fixed">
		<div class="article top_area">
			<div class="title">
				<h1><spring:message code="E0118" text="템플릿 관리"/></h1>
				<p><spring:message code="E0123" text="템플릿 추가"/></p>
			</div>

		</div>
	</div>
	<!-- top area end -->

	<!-- content top start -->
	<div class="content_top fixed">
		<ul class="content_top_btn">
			<li><a class="btn1" href="javascript:;" onclick="templateAdd.save()"><spring:message code="E0029" text="추가"/></a></li>
			<li><a class="btn2" href="javascript:;" onclick="templateAdd.templateList()"><spring:message code="E0047" text="목록"/></a></li>
		</ul>
	</div>
	<!-- content top end -->v

	<div class="section pd_l30">
		<!-- content start -->

		<div class="article content">

			<!-- composer area start -->
			<div class="composer_area">
				<table width="100%" border="0" cellpadding="0" cellspacing="1" >
					<colgroup>
						<col style="width:150px;">
						<col style="width:auto;">
					</colgroup>
					<tbody>
					<form:form method="post" modelAttribute="TemplateForm" name="TemplateForm" action="add.do?${_csrf.parameterName}=${_csrf.token}" enctype="multipart/form-data">
					<tr>
						<th ><spring:message code="E0101" text="분류"/></th>
						<td>
							<form:select path="flag">
								<form:option value="02"><spring:message code="E0120" text="개인템플릿"/></form:option>
								<form:option value="01"><spring:message code="E0121" text="공용템플릿"/></form:option>
							</form:select>
						</td>
					</tr>
					<tr>
						<th><spring:message code="E0103" text="제목"/></th>
						<td>
							<form:input path="temp_name" maxlength="50"/>
							<div class="care_txt">
								<span class="txt red"><spring:message code="E0297" text="사용가능한 특수 문자 : ’_’ , ’.’, ‘-’"/></span>
							</div>
						</td>
					</tr>
					<!-- 에디터 start-->
					<tr class="composer_edit">
						<td colspan="2">
							<div id="div_Html" style="position:relative;margin:0;padding:10px 10px 0px 10px;height:100%;">
								<textarea id="editHtml" name="editHtml" wrap="hard" style="width:100%;height:100%;margin:0;box-sizing:border-box; -moz-box-sizing:border-box; -webkit-box-sizing:border-box;"></textarea>

							</div>
						</td>
					</tr>
					<!-- 에디터 end-->
                        <form:textarea path="content" id="content" htmlEscape="true" cssStyle="display:none;" title="content"/>
					<%--<tr>
						<th><spring:message code="E0166" text="미리보기 이미지"/></th>
						<td>
							<input type="file" id="file_upload" name="file_upload"/>
							<span class="care_txt" style="font-size: 12px">
					<strong class="txt red"><spring:message code="E0110" text="이미지파일은 JPG 또는 GIF 확장자만 등록가능합니다."/></strong>
				</span>
						</td>
					</tr>--%>
					</form:form>
					</tbody>
				</table>

			</div>
			<!-- composer area end -->
		</div>
		<!-- content end -->

	</div>
	<!-- section end -->
</div>

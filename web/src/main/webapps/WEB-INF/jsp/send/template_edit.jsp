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
<script type="text/javascript" src="${staticURL}/sens-static/js/messages/sendmanage_${UserInfo.language}.js"></script>

<div class="template_edit">
	<input type="hidden" id="cpage" value="${cpage}">
    <input type="hidden" id="srch_keyword" value="${srch_keyword}">
    <input type="hidden" id="srch_type" value="${srch_type}">
	<!-- top area start -->
	<div class="title_box fixed">
		<div class="article top_area">
			<div class="title">
				<h1><spring:message code="E0118" text="템플릿 관리"/></h1>
				<p><spring:message code="E0131" text="템플릿 수정"/></p>
			</div>

		</div>
	</div>
	<!-- top area end -->

	<!-- content top start -->
	<div class="content_top fixed">
		<ul class="content_top_btn">
			<li><a class="btn1" href="javascript:;" onclick="templateEdit.edit()"><spring:message code="E0069" text="저장"/></a></li>
			<li><a class="btn2" href="javascript:;" onclick="templateAdd.templateList()"><spring:message code="E0065" text="취소"/></a></li>
		</ul>
	</div>
	<!-- content top end -->

	<div class="section pd_l30">
		<!-- content start -->

		<div class="article content">

			<!-- composer area start -->
			<div class="composer_area">
				<table width="100%" border="0" cellpadding="0" cellspacing="1" >
					<colgroup>
						<col style="width: 150px;">
						<col style="width: auto;">
					</colgroup>
					<tbody>
					<form:form method="post" modelAttribute="TemplateForm" name="TemplateForm" action="edit.do?${_csrf.parameterName}=${_csrf.token}" enctype="multipart/form-data">
					<form:hidden path="ukey"/>
					<form:hidden path="ori_name" value="${TemplateForm.temp_name}"/>
					<tr>
						<th><spring:message code="E0101" text="분류"/></th>
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
                        <form:textarea path="content" htmlEscape="true" cssStyle="display:none;" title="content"/>
					<%--<tr>
						<th><spring:message code="E0166" text="미리보기 이미지"/></th>
						<td>
							<input type="file" id="file_upload" name="file_upload" <c:if test="${TemplateForm.image_path ne ''}"> onchange="templateEdit.changeFile()" </c:if>/>
							<c:if test="${TemplateForm.image_path ne ''}">
								<button type="button" class="btn3" onclick="templateList.previewImage('${TemplateForm.ukey}')"><spring:message code="E0104" text="기존 이미지 보기"/></button>
								<span><form:checkbox path="isDeleteImage" value="1"/><spring:message code="E0167" text="파일 삭제"/></span>
								<span class="care_txt" style="font-size: 12px"><strong class="txt red"><spring:message code="E0110" text="이미지파일은 JPG 또는 GIF 확장자만 등록가능합니다."/></strong></span>
							</c:if>
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
<script type="text/javascript">
	templateEditor.editorInit();
</script>
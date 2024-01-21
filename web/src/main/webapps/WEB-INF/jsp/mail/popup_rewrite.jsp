<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file = "../inc/common.jsp" %>
<%@ include file = "/WEB-INF/jsp/inc/taglib.jspf" %>

<%-- 에디터 구분 : start --%>
<script type="text/javascript" src="${staticURL}/sens-static/js/messages/sendmanage_${UserInfo.language}.js"></script>
<script type="text/javascript" src="${staticURL}/sens-static/js/messages/mail_${UserInfo.language}.js"></script>
<script type="text/javascript" src="${staticURL}/sens-static/js/messages/group_${UserInfo.language}.js"></script>
<script type="text/javascript" src="${staticURL}/sens-static/js/receiver/receiver.js"></script>
<script type="text/javascript" src="${staticURL}/sens-static/js/util/NumberFormat154.js"></script>
<script type="text/javascript" src="${staticURL}/sens-static/js/mail/write.js"></script>

<script type="text/javascript" src="${staticURL}/sens-static/plugin/tinymce/tinymce.min.js"></script>
<script type="text/javascript" src="${staticURL}/sens-static/js/editor/editor_tiny.js?dummy=${constant.dummy}"></script>
<script type="text/javascript">
	var receiverFieldArray = new Array();
</script>
<!-- top area start -->
<div class="title_box fixed" style="left:0; ">
	<div class="article top_area">
		<div class="title">
			<h1><spring:message code="E0393" text="메일쓰기"/></h1>
			<p><spring:message code="E0394" text="보내실 메일을 작성해주세요."/></p>
		</div>

	</div>
</div>
<!-- top area end -->

<!-- content top start -->
<div class="content_top fixed" style="border: none; top:80px;">
	<ul class="content_top_btn">
		<li><a class="btn1" href="javascript:;" onclick="writeform.doResend();"><spring:message code="E0392" text="발송"/></a></li>
		<li><a class="btn2" href="javascript:;" onclick="EditorUtil.preview();"><spring:message code="E0104" text="미리보기"/></a></li>
	</ul>
</div>
<!-- content top end -->

<div class="section pd_l30" style="top:110px; padding-left:0!important;">
	<!-- content start -->

	<div class="article content">
		<div class="composer_area">
			<form:form modelAttribute="MailWriteForm" id="MailWriteForm" name="MailWriteForm" method="post" enctype="multipart/form-data">
				<form:hidden path="msgid"/>
				<form:hidden path="recid"/>
				<form:hidden path="recname"/>
				<form:hidden path="rectype"/>
				<form:hidden path="state"/>
				<form:hidden path="content"/>
				<form:hidden path="att_keys"/>
				<form:hidden path="resend_flag"/>
				<form:hidden path="old_msgid"/>
				<form:hidden path="dbkey"/>
				<form:hidden path="recvid"/>
				<form:hidden path="charset" value="utf-8"/>
				<form:hidden path="ishtml" value="1"/>
				<!-- composer area start -->
				<table width="100%" border="0" cellpadding="0" cellspacing="1" >
					<colgroup>
						<col style="width: 150px">
						<col style="width: auto">
					</colgroup>
					<tbody>
					<tr class="table_sender">
						<th><spring:message code="E0246" text="보내는 사람"/></th>
						<td><form:input path="mail_from"  size="30" cssClass="w400" maxlength="100" htmlEscape="true"/>
							<span class="care_txt" style="font-size:12px;">
									<strong class="txt red"><spring:message code="E0257" text="(주의)"/></strong>
									<strong><spring:message code="E0259" text="홍길동&lt;master@mydomain.com&gt;"/></strong><spring:message code="E0258" text="과 같은 형식으로 입력해주세요."/>
								</span>
						</td>
					</tr>
					<tr class="table_sender">
						<th><spring:message code="E0451" text="수신자"/></th>
						<td><input type="text" id="mail_to" value="${mail_to}" readonly="readonly" size="30" cssClass="w400" maxlength="100" htmlEscape="true"/></td>
					</tr>

					<tr>
						<th height="46px" align="left"><spring:message code="E0103"  text="제목"/></th>
						<td><form:input path="msg_name" size="50" maxlength="100" cssClass="w400" htmlEscape="true"/>
							<select class="send_fieldSelect" id="titleFieldSelect">
								<option selected=""><spring:message code="E0261" text="필드삽입"/></option>
							</select>
							<button type="button" class="btn3" onclick="writeform.insertTitleField()"><spring:message code="E0262" text="삽입"/></button>
						</td>
					</tr>

					<!-- 에디터 start-->
					<tr class="composer_edit" >
						<td colspan="2">
							<div id="editor">
								<div id="div_Html" style="position:relative;margin:0;padding:10px 10px 0px 10px;height:100%;">
									<textarea id="editHtml" name="editHtml" wrap="hard" style="width:100%;height:100%;margin:0;box-sizing:border-box; -moz-box-sizing:border-box; -webkit-box-sizing:border-box;"></textarea>
								</div>
							</div>
						</td>
					</tr>

					<!-- 에디터 end-->
					</tbody>
				</table>
				<!-- composer area end -->
			</form:form>
		</div>
	</div>
	<!-- content end -->

</div>
<!-- section end -->
<c:if test="${MailWriteForm.rectype eq '4'}">
	<script>
		if("${MailWriteForm.dbkey}" != '' ){
			getFieldListAndDraw('${MailWriteForm.recid}','');
		}else{
			getAddrFieldListDraw();
			$("#recid").val("0")
		}
	</script>
</c:if>
<!-- preview popup start-->
<div id="previewLayer" style="display:none;" title="<spring:message code="E0104" text="미리보기"/>" >
	<div class="popup">
		<div class="popup_content" style="height: auto; width: 600px; overflow:auto;">
			<div class="popup_title">
				<span class="close_button" onclick="writeform.previewClose();"></span>
				<h3 class="title"><spring:message code="E0104" text="미리보기"/></h3>
			</div>
			<div class="popup_body_wrap">
			<div class="popup_body" >
				<table width="100%" border="0" cellpadding="0" cellspacing="1" >
					<colgroup>
						<col style="width:150px;">
						<col style="width:auto;">
					</colgroup>
					<tbody>
					<tr>
						<th style="width:80px; min-width:80px;"><spring:message code="E0103" text="제목"/></th>
						<td id="previewArea" >
							<div id="preview_subject" class="prev_subject"></div>
						</td>
					</tr>
					<tr>

						<td colspan="2">
							<div class="prev_content" id="preview_content"></div>
						</td>
					</tr>
					</tbody>
				</table>
			</div>
			</div>
				<div class="pop_footer">
					<div class="btn_div">
						<button type="button" class="btn2" onclick="writeform.previewClose();"><spring:message code="E0282" text="닫기"/></button>
					</div>
				</div>
			</div>
	</div>
</div>
<div id="filelist" style="display: none;"></div>
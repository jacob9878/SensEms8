<%@ page contentType="text/html;charset=utf-8" %>
<%@ page language = "java" %>
<%@ include file = "../inc/common.jsp" %>
<%@ include file = "/WEB-INF/jsp/inc/taglib.jspf" %>

<%-- 에디터 구분 : start --%>
<script type="text/javascript" src="${staticURL}/sens-static/js/messages/sendmanage_${UserInfo.language}.js"></script>
<script type="text/javascript" src="${staticURL}/sens-static/js/messages/mail_${UserInfo.language}.js"></script>
<script type="text/javascript" src="${staticURL}/sens-static/js/messages/group_${UserInfo.language}.js"></script>
<script type="text/javascript" src="${staticURL}/sens-static/js/receiver/receiver.js"></script>
<script type="text/javascript" src="${staticURL}/sens-static/js/util/NumberFormat154.js"></script>

<script type="text/javascript" src="${staticURL}/sens-static/plugin/tinymce/tinymce.min.js"></script>
<script type="text/javascript" src="${staticURL}/sens-static/js/editor/editor_tiny.js?dummy=${constant.dummy}"></script>
<%--<script type="text/javascript" src="${staticURL}/sens-static/js/editor/editor.js"></script>
<script type="text/javascript" src="${staticURL}/sens-static/plugin/ckeditor/ckeditor.js"></script>
<script type="text/javascript" src="${staticURL}/sens-static/js/editor/editor_ck.js"></script>--%>

<script type="text/javascript">
	//주소록, 수신그룹 선택 시 필드 데이터 집어넣는 배열변수

	var receiverFieldArray = new Array();

	var max_nfile_size = 1024*1024*${maxAttachSize};
	var max_nfile_count = ${maxAttachCount};
	var list = new Array();
	<c:forEach var ="restrict" items="${restrict}">
	list.push("${restrict.ext}");
	</c:forEach>
	$(document).ready(function(){
		if("${isDraft}" == "1"){
			EditorUtil.setContent();
			if("${MailWriteForm.is_reserve}" == "1"){
				$("#reserv_time_area").css("display","inline-block");
			}
		}
	});
</script>

<!-- top area start -->
<div class="title_box fixed">
	<div class="article top_area">
		<div class="title">
			<h1><spring:message code="E0393" text="메일쓰기"/></h1>
			<p><spring:message code="E0394" text="보내실 메일을 작성해주세요."/></p>
		</div>

	</div>
</div>
<!-- top area end -->

<!-- content top start -->
<div class="content_top fixed">
	<ul class="content_top_btn">
		<li><a class="btn1" href="javascript:;" onclick="writeform.sendProcess('0');"><spring:message code="E0392" text="발송"/></a></li>
		<li><a class="btn2" href="javascript:;" onclick="EditorUtil.preview();"><spring:message code="E0104" text="미리보기"/></a></li>
		<c:if test="${MailWriteForm.rectype ne '4'}"><li><a class="btn2" href="javascript:;" onclick="writeform.sendProcess('1');"><spring:message code="E0395" text="임시보관"/></a></li></c:if>
		<li><a class="btn2" href="javascript:;" onclick="writeform.testPopupShow()";><spring:message code="E0253" text="테스트발송"/></a></li>
		<c:if test="${MailWriteForm.rectype ne '4'}"><li><a class="btn2" href="javascript:;" onclick="writeform.templateAddShow()";><spring:message code="E0254" text="템플릿으로 저장"/></a></li></c:if>
		<%--<li><a class="btn2" href="#" onclick="writeform.sendProcess('2');"><spring:message code="E0396" text="발송 및 사본저장"/></a></li>--%>
	</ul>
</div>
<!-- content top end -->

<div class="section pd_l30">
	<!-- content start -->

	<div class="article content">
		<div class="composer_area">
			<form:form modelAttribute="MailWriteForm" id="MailWriteForm" name="MailWriteForm" method="post" enctype="multipart/form-data">
				<form:hidden path="msgid"/>
				<form:hidden path="recid"/>
				<form:hidden path="state"/>
				<form:hidden path="content"/>
				<form:hidden path="att_keys"/>
				<form:hidden path="resend_flag"/>
				<form:hidden path="old_msgid"/>
				<form:hidden path="charset" value="utf-8"/>
				<form:hidden path="ishtml" value="1"/>
				<form:hidden path="linkid"/>
				<!-- composer area start -->
				<table width="100%" border="0" cellpadding="0" cellspacing="1" >
					<colgroup>
						<col style="width: 150px">
						<col style="width: auto">
					</colgroup>
					<tbody>
					<tr>
						<th><spring:message code="E0255" text="발송분류"/></th>
						<td width="auto" >
							<form:select path="categoryid">
								<form:option value="0"><spring:message code="E0256" text="분류없음"/></form:option>
								<c:forEach items="${categoryList}" var="category">
									<form:option value="${category.ukey}">${category.name}</form:option>
								</c:forEach>
							</form:select>
						</td>
					</tr>

					<tr class="table_sender">
						<th><spring:message code="E0246" text="보내는 사람"/></th>
						<td><form:input path="mail_from"  size="30" cssClass="w400" maxlength="100" htmlEscape="true"/>
							<span class="care_txt" style="font-size:12px;">
									<strong class="txt red"><spring:message code="E0257" text="(주의)"/></strong>
									<strong><spring:message code="E0259" text="홍길동&lt;master@mydomain.com&gt;"/></strong><spring:message code="E0258" text="과 같은 형식으로 입력해주세요."/>
								</span>
						</td>
					</tr>
					<tr>
						<th><spring:message code="E0247" text="회신주소"/></th>
						<td><form:input path="replyto" size="30" cssClass="w400" maxlength="100" htmlEscape="true"/></td>
					</tr>

					<tr class="table_recipient">
						<th height="46px" align="left"><spring:message code="E0260" text="수신그룹"/></th>
						<td>
							<label class="getList" onclick="receiverEvent.getAddrGroupList();"><form:radiobutton path="rectype" value="1"/><spring:message code="개인주소록" text="개인주소록"/></label>
							<label class="getList" onclick="receiverEvent.getReceiverList('0');"><form:radiobutton path="rectype" value="3"/><spring:message code="E0260" text="수신그룹"/></label>
							<c:if test="${MailWriteForm.rectype eq '4'}"><form:radiobutton path="rectype" value="4" cssStyle="display: none"/></c:if>
							<label><form:input path="recname" readonly="true"/></label>
						</td>
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

					<%--<tr>
						<th height="46px" align="left">Charset</th>
						<td>
							<form:select path="charset">
								<form:option value="euc-kr"><spring:message code="" text="Korean (EUC)"/></form:option>
								<form:option value="ks_c_5601-1987"><spring:message code="" text="Korean (KS_C_5601-1987)"/></form:option>
								<form:option value="utf-8"><spring:message code="" text="Unicode(UTF-8)"/></form:option>
								&nbsp;
							</form:select>
							&nbsp;<spring:message code="" text="Mail Type : "/> &nbsp;
							<form:radiobutton path="ishtml" value="0"/><spring:message code="" text="TEXT"/>
							<form:radiobutton path="ishtml" value="1"/><spring:message code="" text="HTML"/>
						</td>
					</tr>--%>
						<%--<tr class="b_none bg_gray">
							<td colspan="2">
								<select class="w200" onchange="EditorUtil.fieldCodeInsert(this.value,'editor')">
									<option><spring:message code="E0261" text="필드삽입"/></option>
								</select>
								<button class="btn2 f_none" onclick="EditorUtil.rejectCodeInsert();"><spring:message code="E0270" text="수신거부코드삽입"/></button>
								이 부분이 제일 문제
							</td>
						</tr>--%>
						<%--<tr>
							<td colspan="2">
								<select class="send_fieldSelect" onchange="EditorUtil.fieldCodeInsert(this.value,'editor')">
									<option><spring:message code="E0261" text="필드삽입"/></option>
								</select>
								<button type="button" class="btn_small" onclick="EditorUtil.rejectCodeInsert();"><spring:message code="E0270" text="수신거부코드삽입"/></button>
								<div>
									<ul>
										<li><select class="send_fieldSelect" onchange="EditorUtil.fieldCodeInsert(this.value,'editor')">
												<option><spring:message code="E0261" text="필드삽입"/></option>
										</select></li>
										<li><button type="button" class="btn_small" onclick="EditorUtil.rejectCodeInsert();"><spring:message code="E0270" text="수신거부코드삽입"/></button></li>
									</ul>
								</div>
							</td>
						</tr>--%>

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
					<tr>
						<th height="46px" align="left"><spring:message code="E0398" text="반응분석 종료일"/></th>
						<td>
								<%--<form:input path="resp_day" title="반응분석종료일" cssStyle="vertical-align: middle;border:1px solid #dcdcdc;width: 78px;" cssClass="hide_x_button hasDatepicker w100" htmlEscape="true"/>--%>
								<form:input path="resp_day" cssClass="w100" title="반응분석종료일"/>
								<form:select path="resp_hour" cssStyle="vertical-align: middle;width:56px;" cssClass="w100" title="시" items="${hourList}"/>
					</tr>

					<tr>
						<th><spring:message code="E0279" text="발송시간"/></th>
						<td>
							<label><form:radiobutton path="is_reserve" value="0" onclick="writeform.change_sendmode(this.value)"/><spring:message code="E0399" text="즉시발송"/></label>
							<label><form:radiobutton path="is_reserve" value="1" onclick="writeform.change_sendmode(this.value)"/><spring:message code="E0400" text="예약발송"/></label>

							<div id="reserv_time_area" style="display: none;">
								<%--<form:input path="reserv_day" title="예약시간" cssStyle="vertical-align: middle;border:1px solid #dcdcdc;width: 78px;" cssClass="hide_x_button hasDatepicker w100" htmlEscape="true"/>--%>
								<form:input path="reserv_day" cssClass="w100" title="예약시간"/>
								<form:select path="reserv_hour" cssStyle="vertical-align: middle;width:56px;" cssClass="w100" title="시" items="${hourList}"/>
								<form:select path="reserv_min" cssStyle="vertical-align: middle;width:56px;" cssClass="w100" title="분" items="${minuteList}"/>
							</div>
						</td>
					</tr>

					<tr>
						<th height="46px" align="left"><spring:message code="E0401" text="링크 추적"/></th>
						<td>
							<label><form:radiobutton path="islink" value="1"/><spring:message code="E0402" text="추적함"/></label>
							<label><form:radiobutton path="islink" value="0"/><spring:message code="E0403" text="추적안함"/></label>
						</td>
					</tr>

					<tr>
						<th height="46px" align="left"><spring:message code="E0404" text="이메일 중복 허용여부"/></th>
						<td>
							<label><form:radiobutton path="is_same_email" value="1"/><spring:message code="E0405" text="중복허용"/></label
							<label><form:radiobutton path="is_same_email" value="0"/><spring:message code="E0406" text="중복허용 안함"/></label>
						</td>
					</tr>
					<tr>
						<th height="46px" align="left"><spring:message code="E0479" text="파일첨부"/></th>
						<td>
							<form:form id="uploadForm" method="post" enctype="multipart/form-data" >
								<div class="uploadArea">
									<ul class="btn_add_file">
										<li class="f_lft">
											<label for="file_upload" id="file_upload_"><spring:message code="E0559" text="파일첨부"/></label><input type="file" multiple="multiple" name="attachment" id="file_upload" onchange="readFile();"/>
											<input type="button" class="file_delete" value="삭제" onclick="chkDelFile();">
										</li>
										<li class="f_rt"><span>[파일 : <span id="curr_uploadfile_cnt">0</span> / <span id="max_uploadfile_cnt">${maxAttachCount}</span> ] [일반 : <span id="attach_size">0 KB</span> / <span id="max_attachfile_size">${maxAttachSize} MB</span> ] </span></li>
									</ul>
									<div id="filelist" style="display: block;">
										<table>
											<colgroup>
												<col style="width:35px">
												<col style="width:auto">
												<col style="width:100px">
												<col style="width:100px">

											</colgroup>
											<thead>
											<tr>
												<th scope="col" class="check" style="text-align:center;"><input type="checkbox" id="chk_all" title="전체선택"></th>
												<th scope="col" class="name"><span><spring:message code="E0560" text="파일이름"/></span></th>
												<th scope="col" class="size"><span><spring:message code="E0561" text="크기"/></span></th>
												<th scope="col" class="del"><span><spring:message code="E0030" text="삭제"/></span></th>
											</tr>
											</thead>
											<tbody id="attach_file">
											<tr id="attach_message"><td colspan="4"><spring:message code="E0562" text="파일을 이곳으로 끌어오세요."/></td></tr>
											</tbody>
										</table>

									</div>
								</div>
							</form:form>
						</td>
					</tr>

					<!-- <tr class="upload" >
                        <th align="left" rowspan="3">첨부파일</th>
                        <td  >
                            <button type="button">내 PC</button>
                            <button type="button">삭제</button>
                            <button type="button">미리보기</button>
                            <span style="font-size:12px;" class="txt">
                                수신메일 시스템에 따라 ‘제한 초과’로 거부 또는 스팸판정 될 수 있습니다.
                                <strong class="txt red">(10MB 이내 파일 권장)</strong>

                            </span>
                        </td>
                    </tr> -->

					</tbody>
				</table>
				<!-- composer area end -->
			</form:form>
		</div>
	</div>
	<!-- content end -->

</div>
<c:if test="${MailWriteForm.rectype eq '4'}">
	<script>
		$(".getList").attr("onclick","return false;");
		if("${MailWriteForm.recid}" != '' ){
			getFieldListAndDraw('${MailWriteForm.recid}','');
			$("input:radio[name=rectype]:input[value='3']").attr("checked",true);
		}else{
			getAddrFieldListDraw();
			$("input:radio[name=rectype]:input[value='1']").attr("checked",true);
			$("#recid").val("0")
		}
		$("input:radio[name=rectype]").val('4');
	</script>
</c:if>
<!-- section end -->

<!-- preview popup start-->
<div id="previewLayer" style="display:none;" title="<spring:message code="E0104" text="미리보기"/>" >
	<div class="popup">
		<div class="popup_content" style="height: auto; width: 800px; overflow:auto;">
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
						</tbody>
					</table>
					<div class="prev_content" id="preview_content"></div>
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
<!-- template add popup start-->
<div id="addTemplateLayer" style="display:none; " title="<spring:message code="E0283" text="템플릿"/> <spring:message code="E0029" text="추가"/>" >
	<div class="popup">
		<div class="popup_content" style="height: 225px; width: 600px;">
			<div class="popup_title">
				<span class="close_button" onclick="writeform.templateAddClose();"></span>
				<h3 class="title"><spring:message code="E0283" text="템플릿"/>&nbsp;<spring:message code="E0029" text="추가"/> </h3>
			</div>
			<div class="popup_body_wrap" style="/* height: 157px;*/">
			<div class="popup_body">
				<!-- <p> 설명 텍스트 </p> -->
				<table width="100%" border="0" cellpadding="0" cellspacing="1" >
					<tbody>
					<tr>
						<th style="width:150px; min-width:150px;"><spring:message code="E0101" text="분류"/></th>
						<td>
							<select id="flag" name="flag">
								<option value="02"><spring:message code="E0120" text="개인템플릿"/></option>
								<option value="01"><spring:message code="E0121" text="공용템플릿"/></option>
							</select>
						</td>
					</tr>
					<tr>
						<th><spring:message code="E0284" text="템플릿 제목"/></th>
						<td><input type="text" id="temp_name" name="temp_name" class="input" ></td>
					</tr>
					</tbody>
				</table>

				</div>

			</div>
			<div class="pop_footer">
				<div class="btn_div">
					<button type="button" class="btn2" onclick="writeform.templateAdd();"><spring:message code="E0029" text="추가"/></button>
					<button type="button" class="btn2" onclick="writeform.templateAddClose();"><spring:message code="E0282" text="닫기"/></button>
				</div>
			</div>
		</div>
	</div>
</div>

<!-- popup end-->

<!-- Add Field popup start-->
<div class="popup" id="testSendPopup" style="display: none">
	<div class="popup_content" style="width: 900px; height: 585px;">
		<div class="popup_title">
			<span class="close_button" onclick="writeform.closeTestPopup();"></span>
			<h3 class="title"><spring:message code="E0253" text="테스트발송"/></h3>
		</div>
		<div class="popup_body_wrap">
			<div class="popup_body">
				<!-- <p> 설명 텍스트 </p> -->
				<table  class="queryBox" border="0"  cellpadding="0" cellspacing="0" >
					<colgroup>
						<col style="width: auto;">
						<col style="width: 90px;">
						<col style="width: auto;">
					</colgroup>
					<tbody  id ="testlist">
					<tr>
						<th><spring:message code="E0407" text="테스트 계정"/></th>
						<th></th>
						<th><spring:message code="E0408" text="테스트 발송 명단"/></th>
					</tr>
					<tr class="table_field">
						<td>
							<select id="testAccount" name="testAccount" multiple="multiple" size="3" style="height: 320px;">
							</select>
						</td>
						<td>
							<button type="button" class="shuttle_btn shuttle_rt" onclick="writeform.testAccountInsert();"><spring:message code="E0029" text="추가"/></button>
							<button type="button" class="shuttle_btn shuttle_let" onclick="writeform.testAccountDelete();"><spring:message code="E0320" text="제거"/></button>
						</td>
						<td>
							<select id="testSendAccount" name="testSendAccount" multiple="multiple" size="3"style="height: 320px;">

							</select>
						</td>
					</tr>
					<tr class="queryBox_btn">
						<td colspan="3">
							<strong><spring:message code="E0407" text="테스트 계정 "/></strong><input type="text" id="accountInsertText"><button type="button" class="btn1" onclick="writeform.demoAccountInsert();"><spring:message code="E0029" text="추가"/></button>
						</td>
					</tr>
					</tbody>
				</table>

		</div>
		</div>
        <div class="pop_footer">
            <div class="btn_div">
                <button type="button" class="btn2" onclick="writeform.doTestSend()"><spring:message code="E0253" text="테스트발송"/></button>
                <button type="button" class="btn2" onclick="writeform.closeTestPopup()"><spring:message code="E0065" text="취소"/></button>
            </div>
        </div>
	</div>
</div>


<!-- 개인주소록 모달레이어 팝업으로 변경 -->

<div id="addressListLayer" style="display:none;" >
	<div class="section pd_l30">
		<div class="popup">
			<div class="popup_content" style="height: 659px;">
				<div class="popup_title">
					<span class="close_button" onclick="receiverEvent.close();"></span>
					<h3 class="title"><spring:message code="E0710" text="개인주소록"/> <spring:message code="E0175" text="선택"/></h3>
				</div>
				<div class="popup_body_wrap footer_nav" style="height: 499px;">
					<div class="popup_body" >
						<p class="top_explain"><spring:message code="E0759" text="주소록을 선택해 주세요"/></p>
						<div class="footer_nav_table">
							<div class="table">
								<table width="100%" border="0" cellpadding="0" cellspacing="1" >
									<colgroup>
										<col style="width: 30px;">
										<col style="width: auto;">
										<col style="width: 80px;">
									</colgroup>
									<thead class="fixed">
										<tr>
											<th class="check_ico">
												<input type="checkbox" id="all_check" onclick="common.select_all('gkeys')" title="전체선택">
											</th>
											<th title="주소록 그룹"><spring:message code="E0319" text="주소록 그룹명"/></th>
											<th  title="인원"><spring:message code="E0386" text="인원"/></th>
										</tr>
									</thead>
								<tbody id="addresslist" style="align:center;">
								</tbody>
								</table>
							</div>
<%--							<div class="page_nav" id="pageInfo_addr"></div>--%>
						</div>
					</div>
				</div>
				<div class="pop_footer">
					<div class="btn_div">
						<button class="btn2" onclick="receiverEvent.selAddrGroupList();"><spring:message code="E0064" text="확인"/></button>
						<button class="btn2" onclick="receiverEvent.close();"><spring:message code="E0282" text="닫기"/></button>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>



<!-- 수신그룹 팝업을 모달레이어팝업으로 변경 -->

<div id="groupListLayer" style="display:none;" title="<spring:message code="E0343" text="주소록 그룹"/> <spring:message code="E0386" text="인원"/>" >
	<div class="section pd_l30">
		<div class="popup">
			<div class="popup_content" style="height: 659px;">
				<div class="popup_title">
					<span class="close_button" onclick="receiverEvent.close();"></span>
					<h3 class="title"><spring:message code="E0285" text="수신그룹 목록"/></h3>
				</div>
				<div class="popup_body_wrap footer_nav">
					<div class="popup_body" >
						<p class="top_explain">수신그룹을 선택해 주세요</p>
						<div class="footer_nav_table">
							<div class="table">
								<table width="100%" border="0" cellpadding="0" cellspacing="1" >
									<colgroup>
										<col style="width:50px" />
										<col style="width:auto;" />
										<col style="width:150px" />
									</colgroup>
									<thead class="fixed">
										<tr>
											<th>
												<spring:message code="E0175" text="선택"/>
											</th>
											<th><spring:message code="E0171" text="수신그룹명"/></th>
											<th><spring:message code="E0066" text="작성자"/></th>
										</tr>
									</thead>
									<tbody id="grouplist">
									</tbody>
								</table>
							</div>
						<div class="page_nav" id="pageInfo_group"></div>
						</div>
					</div>
				</div>
				<div class="pop_footer">
					<div class="btn_div">
						<button class="btn2" onclick="receiverEvent.receiverGroupConfirm('${opener_type}');"><spring:message code="E0064" text="확인"/></button>
						<button class="btn2" onclick="receiverEvent.preview();"><spring:message code="E0104" text="미리보기"/></button>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
	<!-- content end -->
<!-- popup end-->


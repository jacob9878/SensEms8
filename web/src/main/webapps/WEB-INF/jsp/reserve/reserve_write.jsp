<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file = "../inc/common.jsp" %>


<%-- 에디터 구분 : start --%>
<script type="text/javascript" src="${staticURL}/sens-static/js/editor/editor.js?dummy=${constant.dummy}"></script>
<script type="text/javascript" src="${staticURL}/sens-static/plugin/ckeditor/ckeditor.js"></script>
<script type="text/javascript" src="${staticURL}/sens-static/js/editor/editor_ck.js?dummy=${constant.dummy}"></script>
<script type="text/javascript" src="${staticURL}/sens-static/js/reserve/reserve_write.js"></script>
<script type="text/javascript" src="${staticURL}/sens-static/js/receiver/receiver.js"></script>
<script type="text/javascript" src="${staticURL}/sens-static/js/reserve/setday.js"></script>
<script type="text/javascript" src="${staticURL}/sens-static/plugin/moment/moment.min.js"></script>
<script type="text/javascript" src="${staticURL}/sens-static/plugin/jquery/jquery.json-2.4.js"></script>
<script type="text/javascript" src="${staticURL}/sens-static/plugin/jquery/ui/jquery-ui.min.js"></script>

<script>
	$(document).ready(function() {
		if("${reserveSendForm.update_flag}" != null && "${reserveSendForm.update_flag}" == "1"){

			/** 정기예약발송 수정인 경우 발송폼에 데이터를 세팅 */
			reserveEvent.updateContent('${reserveSendForm.send_time}','${reserveSendForm.rot_point}', '${reserveSendForm.rot_flag}', '${reserveSendForm.start_time}', '${reserveSendForm.end_time}','${recv_name}');
		}
	});
</script>

<style type="text/css">
	.repeat_schedule tr {border: none; height: 20px; }
	.repeat_schedule tr td{padding: 5px; }
	.repeat_schedule tr td input{margin-right: 3px; }
</style>
<!-- top area start -->
<div class="title_box fixed">
	<div class="article top_area">
		<div class="title">
			<h1><spring:message code="E0251" text="정기예약발송 작성"/></h1>
			<p><spring:message code="E0252" text="메일을 작성해주세요."/></p>
		</div>

	</div>
</div>
<!-- top area end -->

<!-- content top start -->
<div class="content_top fixed">
	<ul class="content_top_btn">
		<li>
			<c:choose>
				<c:when test="${reserveSendForm.update_flag != '1'}"><a class="btn1" href="javascript:;" onclick="reserveEvent.write();">
					<spring:message code="E0069" text="저장"/></a>
				</c:when>
				<c:otherwise><a class="btn1" href="javascript:;" onclick="reserveEvent.write();"><spring:message code="E0128" text="수정"/></a></c:otherwise>
			</c:choose>
		</li>
		<li><a class="btn2" href="javascript:;" onclick="EditorUtil.preview();"><spring:message code="E0104" text="미리보기"/></a></li>
		<li><a class="btn2" href="javascript:;" onclick="reserveEvent.testPopupShow()";><spring:message code="E0253" text="테스트발송"/></a></li>
		<li><a class="btn2" href="javascript:;" onclick="reserveEvent.templateAddShow()";><spring:message code="E0254" text="템플릿으로 저장"/></a></li>
	</ul>
</div>
<!-- content top end -->v

<div class="section pd_l30">
<!-- content start -->

	<div class="article content">
    	 <form:form modelAttribute="reserveSendForm"  name="reserveSendForm" method="post">
			 <form:hidden path="send_time"/>
			 <form:hidden path="content"/>
			 <form:hidden path="recid"/>
			 <form:hidden path="rot_point"/>
			 <form:hidden path="update_flag"/>
			 <form:hidden path="msgid"/>
			 <form:hidden path="userid"/>

				<!-- composer area start -->
				<div class="composer_area">
					<table width="100%" border="0" cellpadding="0" cellspacing="1">
							<tbody>
							<tr>
								<th ><spring:message code="E0255" text="발송분류"/></th>
								<td width="auto" >
									<form:select path="categoryid">
										<form:option value="0"><spring:message code="E0256" text="분류없음"/></form:option>

										<c:forEach items="${categoryList}" var="category">
											<form:option value="${category.ukey}">${category.name}</form:option>
										</c:forEach>
									</form:select>
								</td>
							</tr>

							<tr>
								<th ><spring:message code="E0246" text="보내는 사람"/></th>
								<td><form:input path="mail_from"  size="30" maxlength="100"/>
									<span style="font-size:12px;">
										<strong class="txt red"><spring:message code="E0257" text="(주의)"/></strong>
										<strong><spring:message code="E0259" text="홍길동&lt;master@mydomain.com&gt;"/></strong><spring:message code="E0258" text="과 같은 형식으로 입력해주세요."/>
									</span>
								</td>
							</tr>
							<tr>
								<th> <spring:message code="E0247" text="회신 주소"/></th>
								<td><form:input path="replyto"  size="30" maxlength="100"/></td>
							</tr>

							<tr>
								<th ><spring:message code="E0260" text="수신그룹"/></th>
								<td>
									<%--<label><input name="recieverGroup" id="address" type="radio" value="주소록"><spring:message code="" text="주소록"/></label>--%>
									<label onclick="receiverEvent.getReceiverList('1');">
										<%--<input name="recieverGroup" id="receiver" type="radio" value="수신그룹" >--%>
										<input type="text" name="receiverGroupText" id="receiverGroupText"></label>
								<%--	:&nbsp;<input type="text" id="recieverAddr" class="input" size="40">--%>
								</td>
							</tr>

							<tr>
								<th><spring:message code="E0103" text="제 목"/></th>
								<td><form:input path="msg_name" size="50" maxlength="100" cssClass="f_lft"/>
									<select class="reserveFieldSelect f_lft" id="titleFieldSelect">
										<option selected=""><spring:message code="E0261" text="필드삽입"/></option>
									</select>
									<button type="button" class="btn3 f_lft mg_l5" onclick="EditorUtil.fieldCodeInsert('','title')"><spring:message code="E0262" text="삽입"/></button>
								</td>
							</tr>


							<tr>
								<th><spring:message code="E0263" text="Charset"/></th>
								<td>
									<form:select path="charset">
										<form:option value="euc-kr"><spring:message code="E0264" text="Korean (EUC)"/></form:option>
										<form:option value="ks_c_5601-1987"><spring:message code="E0265" text="Korean (KS_C_5601-1987)"/></form:option>
										<form:option value="utf-8"><spring:message code="E0266" text="Unicode(UTF-8)"/></form:option>
									&nbsp;</form:select>

									&nbsp;<spring:message code="E0267" text="Mail Type : "/> &nbsp;
									<form:label path="ishtml"><form:radiobutton path="ishtml" value="0"/><spring:message code="E0268" text="TEXT"/></form:label>
									<form:label path="ishtml"><form:radiobutton path="ishtml" value="1"/><spring:message code="E0269" text="HTML"/></form:label>
								</td>
							</tr>

							<tr class="b_none bg_gray">
								<td colspan="2">
										<select class="reserveFieldSelect f_lft"  onchange="EditorUtil.fieldCodeInsert(this.value,'editor')"><option><spring:message code="E0261" text="필드삽입"/></option></select>
										<button type="button" class="btn2 mg_l5" onclick="EditorUtil.rejectCodeInsert();"><spring:message code="E0270" text="수신거부코드삽입"/></button>
									</ul>
								</td>
							</tr>


					<!-- 에디터 start-->
						<tr class="composer_edit" >
							<td colspan="2">
                                <div id="editor">
                                    <div id="div_Html" style="position:relative;margin:0;padding:10px 10px 0px 10px;height:100%;">
                                        <textarea id="editHtml" name="editHtml" wrap="hard" style="width:100%;height:100%;margin:0;box-sizing:border-box; -moz-box-sizing:border-box; -webkit-box-sizing:border-box;"></textarea>
                                    <!--		<label for="editHtml" class="blind">editHtml</label>-->
                                    </div>
                                    <div id="div_Text" style="display:none">
                                        <textarea id="editText" name="editText" wrap="hard" style="width:100%;height:400px;margin:0;" ></textarea>
                                        <label for="editText" class="blind">editText</label>
                                    </div>
                                </div>
							</td>
						</tr>
					<!-- 에디터 end-->
							<tr>
								<th ><spring:message code="E0248" text="반복일정"/></th>

								<td class="">
									<label><form:radiobutton onclick="reserveEvent.viewRepeat(this.value)" path="rot_flag" value="0"/> <spring:message code="E0242" text="매일"/></label>
									<label><form:radiobutton onclick="reserveEvent.viewRepeat(this.value)" path="rot_flag" value="1"/> <spring:message code="E0243" text="매주"/></label>
									<label><form:radiobutton onclick="reserveEvent.viewRepeat(this.value)" path="rot_flag" value="2"/> <spring:message code="E0244" text="매월"/></label>
								</td>
							</tr>

								<%--매주 반복요일 start --%>
							<tr id="repeat_day" style="display: none;">
								<th><spring:message code="E0513" text="발송요일"/></th>

								<td class="">
											<label><input type="checkbox" name="send_week[]" value="1"><spring:message code="E0271" text="일"/></label>
											<label><input type="checkbox" name="send_week[]" value="2"><spring:message code="E0272" text="월"/></label>
											<label><input type="checkbox" name="send_week[]" value="3"><spring:message code="E0273" text="화"/></label>
											<label><input type="checkbox" name="send_week[]" value="4"><spring:message code="E0274" text="수"/></label>
											<label><input type="checkbox" name="send_week[]" value="5"><spring:message code="E0275" text="목"/></label>
											<label><input type="checkbox" name="send_week[]" value="6"><spring:message code="E0276" text="금"/></label>
											<label><input type="checkbox" name="send_week[]" value="7"><spring:message code="E0277" text="토"/></label>
								</td>
							</tr>
								<%--매주 반복요일 end --%>

								<%--매월 날짜 start --%>
							<tr id="repeat_month" style="display: none;">
								<th height="46px" align="left"><spring:message code="E0368" text="발송일"/></th>


								<td>
									<table class="repeat_schedule">
										<tr >
											<td><label><input type="checkbox" name="send_date[]" value="1">1<spring:message code="E0271" text="일"/></label></td>
											<td><label><input type="checkbox" name="send_date[]" value="2">2<spring:message code="E0271" text="일"/></label></td>
											<td><label><input type="checkbox" name="send_date[]" value="3">3<spring:message code="E0271" text="일"/></label></td>
											<td><label><input type="checkbox" name="send_date[]" value="4">4<spring:message code="E0271" text="일"/></label></td>
											<td><label><input type="checkbox" name="send_date[]" value="5">5<spring:message code="E0271" text="일"/></label></td>
											<td><label><input type="checkbox" name="send_date[]" value="6">6<spring:message code="E0271" text="일"/></label></td>
											<td><label><input type="checkbox" name="send_date[]" value="7">7<spring:message code="E0271" text="일"/></label></td>
											<td><label><input type="checkbox" name="send_date[]" value="8">8<spring:message code="E0271" text="일"/></label></td>
											<td><label><input type="checkbox" name="send_date[]" value="9">9<spring:message code="E0271" text="일"/></label></td>
											<td><label><input type="checkbox" name="send_date[]" value="10">10<spring:message code="E0271" text="일"/></label></td>
										</tr>
										<tr>
											<td><label><input type="checkbox" name="send_date[]" value="12">12<spring:message code="E0271" text="일"/></label></td>
											<td><label><input type="checkbox" name="send_date[]" value="11">11<spring:message code="E0271" text="일"/></label></td>
											<td><label><input type="checkbox" name="send_date[]" value="13">13<spring:message code="E0271" text="일"/></label></td>
											<td><label><input type="checkbox" name="send_date[]" value="14">14<spring:message code="E0271" text="일"/></label></td>
											<td><label><input type="checkbox" name="send_date[]" value="15">15<spring:message code="E0271" text="일"/></label></td>
											<td><label><input type="checkbox" name="send_date[]" value="16">16<spring:message code="E0271" text="일"/></label></td>
											<td><label><input type="checkbox" name="send_date[]" value="17">17<spring:message code="E0271" text="일"/></label></td>
											<td><label><input type="checkbox" name="send_date[]" value="18">18<spring:message code="E0271" text="일"/></label></td>
											<td><label><input type="checkbox" name="send_date[]" value="19">19<spring:message code="E0271" text="일"/></label></td>
											<td><label><input type="checkbox" name="send_date[]" value="20">20<spring:message code="E0271" text="일"/></label></td>
										</tr>
										<tr>
											<td><label><input type="checkbox" name="send_date[]" value="21">21<spring:message code="E0271" text="일"/></label></td>
											<td><label><input type="checkbox" name="send_date[]" value="22">22<spring:message code="E0271" text="일"/></label></td>
											<td><label><input type="checkbox" name="send_date[]" value="23">23<spring:message code="E0271" text="일"/></label></td>
											<td><label><input type="checkbox" name="send_date[]" value="24">24<spring:message code="E0271" text="일"/></label></td>
											<td><label><input type="checkbox" name="send_date[]" value="25">25<spring:message code="E0271" text="일"/></label></td>
											<td><label><input type="checkbox" name="send_date[]" value="26">26<spring:message code="E0271" text="일"/></label></td>
											<td><label><input type="checkbox" name="send_date[]" value="27">27<spring:message code="E0271" text="일"/></label></td>
											<td><label><input type="checkbox" name="send_date[]" value="28">28<spring:message code="E0271" text="일"/></label></td>
											<td><label><input type="checkbox" name="send_date[]" value="29">29<spring:message code="E0271" text="일"/></label></td>
											<td><label><input type="checkbox" name="send_date[]" value="30">30<spring:message code="E0271" text="일"/></label></td>
										</tr>
										<tr>
											<td colspan="10"><label><input type="checkbox" name="send_date[]" value="31">31<spring:message code="E0271" text="일"/></label></td>
										</tr>
									</table>

								</td>
							</tr>
								<%--매월 날짜 end --%>

							<%-- 발송기간 start --%>
							<tr>
								<th ><spring:message code="E0238" text="발송기간"/></th>

								<td>
									<form:input path="start_time" title="예약시작일"  cssClass="hide_x_button w200"/> ~
									<form:input path="end_time" title="예약종료일"  cssClass="hide_x_button w200"/>
								</td>

							</tr>
							<%-- 발송기간 end --%>

								<%-- 발송시간 end --%>
							<tr>
								<th><spring:message code="E0279" text="발송시간"/></th>

								<td>
									<form:select path="reserve_hour" cssClass="w100" title="시" items="${hourList}"/>
									<form:select path="reserve_minute" cssClass="w100" title="분" items="${minuteList}"/>
								</td>

							</tr>
								<%-- 발송시간 end --%>

							</tbody>
						</table>
					</div>
				<!-- composer area end -->
				 </form:form>
			</div>
			<!-- content end -->

		</div>
	<!-- section end -->

<!-- preview popup start-->
<div id="previewLayer" style="display:none;" title="<spring:message code="E0104" text="미리보기"/>" >
		<div class="popup">
			<div class="popup_content" style="height: auto; width: 1200px; overflow:auto;">
				<div class="popup_title">
					<span class="close_button" onclick="reserveEvent.previewClose();"></span>
					<h3 class="title"><spring:message code="E0280" text="정기예약발송 미리보기"/></h3>
				</div>
				<div class="popup_body" >
					<table width="100%" border="0" cellpadding="0" cellspacing="1" >
						<tbody>
						<tr>
								<th><spring:message code="E0103" text="제목"/></th>
							<td id="previewArea" >
								<div id="preview_subject" class="prev_subject"></div>
							</td>
						</tr>
						<tr>
							<th><spring:message code="E0281" text="본문"/></th>
							<td>
								<div class="prev_content" id="preview_content"></div>
							</td>
						</tr>
						</tbody>
					</table>
					<div class="btn_div">
						<button type="button" class="btn2" onclick="reserveEvent.previewClose();"><spring:message code="E0282" text="닫기"/></button>
					</div>
				</div>
			</div>
		</div>
</div>
<!-- preview popup end-->

<!-- template add popup start-->
<div id="addTemplateLayer" style="display:none;" title="<spring:message code="E0283" text="템플릿"/> <spring:message code="E0029" text="추가"/>" >
		<div class="popup">
			<div class="popup_content" style="height: 250px;">
				<div class="popup_title">
					<span class="close_button" onclick="reserveEvent.templateAddClose();"></span>
					<h3 class="title"><spring:message code="E0283" text="템플릿"/>&nbsp;<spring:message code="E0029" text="추가"/> </h3>
				</div>
				<div class="popup_body">
					<!-- <p> 설명 텍스트 </p> -->
					<table width="100%" border="0" cellpadding="0" cellspacing="1" >
						<tbody>
						<tr>
							<th><spring:message code="E0101" text="분류"/></th>
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
					<div class="btn_div">
						<button type="button" class="btn1" onclick="reserveEvent.templateAdd();"><spring:message code="E0029" text="추가"/></button>
						<button type="button" class="btn2" onclick="reserveEvent.templateAddClose();"><spring:message code="E0282" text="닫기"/></button>
					</div>
				</div>
			</div>
		</div>
</div>

<!-- popup end-->

<!-- Add Field popup start-->
<div class="popup" id="testSendPopup" style="display: none">
	<div class="popup_content" style="width: 850px;height: 600px;" >
		<div class="popup_title">
			<span class="close_button" onclick="reserveEvent.closeTestPopup();"></span>
			<h3 class="title"><spring:message code="E0514" text="테스트 발송"/></h3>
		</div>
		<div class="popup_body_wrap">
			<div class="popup_body">
				<!-- <p> 설명 텍스트 </p> -->
				<table  class="queryBox" border="0"  cellpadding="0" cellspacing="0" >
					<colgroup>
						<col style="width:300px;">
						<col style="width:100px">
						<col style="width:300px;">
					</colgroup>
					<tbody>
					<tr>
						<th><spring:message code="E0407" text="테스트 계정"/></th>
						<th></th>
						<th><spring:message code="E0408" text="테스트 발송 명단"/></th>
					</tr>
					<tr class="table_field">
						<td>
							<select id="testAccount" name="testAccount" size="3" style="height: 320px;">
							</select>
						</td>
						<td>
							<button type="button" class="shuttle_btn shuttle_rt" onclick="reserveEvent.testAccountInsert();"></button>
							<button type="button" class="shuttle_btn shuttle_let" onclick="reserveEvent.testAccountDelete();"></button>
						</td>
						<td>
							<select id="testSendAccount" name="testSendAccount" size="3"style="height: 320px;">

							</select>
						</td>
					</tr>
					<tr class="queryBox_btn">
						<td colspan="3">
							<strong><spring:message code="E0407" text="테스트 계정 "/></strong><input type="text" id="accountInsertText"><button type="button" class="btn1" onclick="reserveEvent.demoAccountInsert();"><spring:message code="E0029" text="추가"/></button>
						</td>
					</tr>
					</tbody>
				</table>
				<div class="btn_div">
					<button type="button" class="btn1" onclick="reserveEvent.doTestSend()"><spring:message code="E0253" text="테스트발송"/></button>
					<button type="button" class="btn2" onclick="reserveEvent.closeTestPopup()"><spring:message code="E0065" text="취소"/></button>
				</div>
			</div>
		</div>
	</div>
</div>
<!-- popup end-->


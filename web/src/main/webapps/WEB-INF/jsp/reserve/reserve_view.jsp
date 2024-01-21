<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file = "/WEB-INF/jsp/inc/taglib.jspf" %>
<%@ include file="../common/encrypt.jsp" %>
<c:if test="${ !empty infoMessage }">
	<script>
		$("#editHtml").editor("content", content);
	</script>
</c:if>
<div class="reserveSend_view">
	<!-- section start -->
	<!-- top area start -->
	<div class="title_box fixed">
		<div class="article top_area">
			<div class="title">
				<h1><spring:message code="E0230" text="정기예약발송 관리"/></h1>
				<p><spring:message code="E0245" text="정기예약발송 확인"/></p>
			</div>
		</div>
	</div>
	<!-- top area end -->

	<!-- content top start -->
	<div class="content_top fixed">
		<ul class="content_top_btn">
			<li><a class="btn2" href="javascript:;" onclick="reserveView.modify();"><spring:message code="E0128" text="수정"/></a></li>
			<li><a class="btn2" href="javascript:;" onclick="reserveView.list();"><spring:message code="E0047" text="목록"/></a></li>
		</ul>
	</div>
	<!-- content top end -->

	<div class="section pd_l30">
		<form:form method="get" name="reserveSendForm" modelAttribute="reserveSendForm" action="edit.do">
			<form:hidden path="msgid"/>
			<!-- content start -->
			<div class="article content">

				<!-- composer area start -->
				<div class="composer_area">
					<table width="100%" border="0" cellpadding="0" cellspacing="1" >
						<tbody>
						<tr height="46px" align="left">
							<th style="width:150px; min-width:150px;"><spring:message code="E0246" text="보내는 사람"/></th>
							<td>${reserveSendForm.mail_from}</td>
						</tr>
						<tr height="46px" align="left">
							<th><spring:message code="E0247" text="회신주소"/></th>
							<td>${reserveSendForm.replyto}</td>
						</tr>
						<tr height="46px" align="left">
							<th><spring:message code="E0103" text="제목"/></th>
							<td>${reserveSendForm.msg_name}</td>
						</tr>
						<tr height="46px" align="left">
							<th><spring:message code="E0067" text="등록일"/></th>
							<td>${reserveSendForm.regdate}</td>
						</tr>

						<tr height="46px" align="left">
							<th><spring:message code="E0248" text="반복일정"/></th>
							<td>
							<c:choose>
								<c:when test="${reserveSendForm.rot_flag == 0}"><spring:message code="E0242" text="매일"/></c:when>
								<c:when test="${reserveSendForm.rot_flag == 1}"><spring:message code="E0243" text="매주"/></c:when>
								<c:when test="${reserveSendForm.rot_flag == 2}"><spring:message code="E0244" text="매월"/></c:when>
								<c:otherwise></c:otherwise>
							</c:choose>
							</td>
						</tr>

						<tr height="46px" align="left">
							<th><spring:message code="E0240" text="발송시간"/></th>
							<td>${reserveSendForm.send_time}</td>
						</tr>

						<tr height="46px" align="left">
							<th><spring:message code="E0238" text="발송기간"/></th>
							<td>${reserveSendForm.start_time} - ${reserveSendForm.end_time}
							</td>
						</tr>

						<tr height="46px" align="left">
							<th><spring:message code="E0249" text="최근발송일"/></th>

							<td>
							<c:choose>
								<c:when test="${reserveSendForm.last_send == null}">
									<spring:message code="E0250" text="최근 발송된 내역이 없습니다."/>
								</c:when>
								<c:otherwise>
									${reserveSendForm.last_send }
								</c:otherwise>
							</c:choose>
							</td>
						</tr>


						<!-- 에디터 start-->
						<tr class="composer_edit" >
							<td colspan="2">
								<div id="editor">

										${reserveSendForm.content}
								</div>
							</td>
						</tr>
						<!-- 에디터 end-->
						</tbody>
					</table>
				</div>
				<!-- composer area end -->
			</div>
			<!-- content end -->
		</form:form>


	</div>
	<!-- section end -->
</div>
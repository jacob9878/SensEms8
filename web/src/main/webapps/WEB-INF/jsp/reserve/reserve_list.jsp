<%@ page contentType="text/html;charset=utf-8" %>
<%@ page language = "java" %>
<%@ include file = "../inc/common.jsp" %>

	<!-- section start -->
	<!-- top area start -->
	<form:form method="get" name="reserveSendListForm" modelAttribute="reserveSendListForm" action="list.do">
	<form:hidden path="cpage" value="${pageInfo.cpage }" />
	<div class="title_box fixed">
		<div class="article top_area">
			<div class="title">
				<h1><spring:message code="E0230" text="정기예약발송 관리"/></h1>
				<p><spring:message code="E0231" text="정기예약발송관리를 관리 할 수 있습니다."/></p>
			</div>
		</div>
	</div>
	<!-- top area end -->
	<!-- content top start -->
	<div class="content_top fixed">
		<ul class="content_top_btn">
			<li><a class="btn2" href="javascript:;" onclick="location.href='${contextPath}/send/reserve/write.do'"><spring:message code="E0232" text="추가"/></a></li>
			<li><a class="btn2" href="javascript:;" onclick="reserveList.reserveDel();"><spring:message code="E0030" text="삭제"/></a></li>
		</ul>
		<div class="content_top_opt">
			<button type="button" onclick="common.open_dropMenu();"><img src="${StaticURL}/sens-static/images/top_opt_btn.png" alt="" /></button>
			<div class="drop_menu" style="display:none;" >
				<table>
					<tbody>
					<tr height="30px">
						<th scope="row"><span><spring:message code="" text="목록 개수"/></span></th>
						<td>
							<select onchange="common.change_pagesize(this.value)">
								<option ${f:isSelected('15',UserInfo.pagesize)} value="15"><spring:message code="E0233" text="15"/></option>
								<option ${f:isSelected('30',UserInfo.pagesize)} value="30"><spring:message code="E0234" text="30"/></option>
								<option ${f:isSelected('50',UserInfo.pagesize)} value="50"><spring:message code="E0235" text="50"/></option>
								<option ${f:isSelected('100',UserInfo.pagesize)} value="100"><spring:message code="E0236" text="100"/></option>
							</select>
						</td>
					</tr>
					<tr height="30px">
						<th scope="row"><span><spring:message code="E0041" text="목록 간격"/></span></th>
						<td>
							<select>
								<option><spring:message code="E0043" text="좁게"/></option>
								<option><spring:message code="E0045" text="보통"/></option>
								<option><spring:message code="E0044" text="넓게"/></option>
							</select>
						</td>
					</tr>
					</tbody>
				</table>
			</div>
		</div>
	</div>
	<!-- content top end -->



	<div class="section pd_l30">
		<!-- content start -->

		<div class="article content">
			<!-- content area start -->
			<div class="content_area">
				<table width ="100%" height="auto" >
					<colgroup>
						<col style="width:35px" />
						<col style="width:auto" />
						<col style="width:200px" />
						<col style="width:100px" />
						<col style="width:100px" />
					</colgroup>
					<thead>
					<tr>
						<th class="txt center">
							<input id="all_check" type="checkbox" onclick="common.select_all('ukeys');">
						</th>
						<th><spring:message code="E0103" text="제목"/></th>
						<th class="txt center"><spring:message code="E0238" text="발송기간"/></th>
						<th class="txt center"><spring:message code="E0239" text="발송주기"/></th>
						<th class="txt center"><spring:message code="E0240" text="발송시간"/></th>
					</tr>
					</thead>
					<tbody>
					<c:if test="${ empty reserveList }">
						<td class="txt center"><spring:message code="E0241" text="등록된 정기예약발송이 없습니다."/></td>
					</c:if>
					<c:forEach var="reserveInfo" items="${reserveList}">
						<fmt:formatDate value="${reserveInfo.regdate}" pattern="yyyy-MM-dd" var="regdate" />
						<fmt:formatDate value="${reserveInfo.regdate}" pattern="HH:mm" var="time" />
						<tr>
							<td class="txt center">
								<input type="checkbox" name="ukeys" value="${reserveInfo.msgid}">
							</td>
							<td><a href="javascript:;" onclick="reserveList.view('${reserveInfo.msgid}')">${reserveInfo.msg_name}</a></td>
							<td class="txt center ">${reserveInfo.start_time}&nbsp;~&nbsp;${reserveInfo.end_time}</td>
							<c:choose>
							<c:when test="${reserveInfo.rot_flag == 0}"><td class="txt center"><spring:message code="E0242" text="매일"/></td></c:when>
							<c:when test="${reserveInfo.rot_flag == 1}"><td class="txt center"><spring:message code="E0243" text="매주"/></td></c:when>
							<c:when test="${reserveInfo.rot_flag == 2}"><td class="txt center"><spring:message code="E0244" text="매월"/></td></c:when>
							<c:otherwise><td class="txt center"></td></c:otherwise>
							</c:choose>
							<td class="txt center list_2line">${regdate}<span>${time}</span></td>
						</tr>
					</c:forEach>
					</tbody>
				</table>

			</div>
			<!-- content area end -->
		</div>
		<!-- content end -->
		<!-- nav start -->
		<div class="page_nav">
			<pt:page>
				<pt:cpage>
					${ pageInfo.cpage }
				</pt:cpage>
				<pt:pageSize>
					${ pageInfo.pageSize }
				</pt:pageSize>
				<pt:total>
					${ pageInfo.total }
				</pt:total>
				<pt:jslink>reserveList.list</pt:jslink>
			</pt:page>
		</div>
		<!-- nav end -->
	</form:form>
	</div>
	<!-- section end -->
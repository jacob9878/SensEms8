<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file = "/WEB-INF/jsp/inc/taglib.jspf" %>
<script type="text/javascript" src="/sens-static/js/sysman/relay.js"></script>
<script type="text/javascript" src="${staticURL}/sens-static/js/common/relaysearch.js"></script>
	<!-- section start -->
	<!-- top area start -->
<div class="relaylist">
	<form:form  name="RelayListForm" modelAttribute="relayListForm" id="RelayListForm" action="list.do"  method="get">
	<form:hidden path="cpage" id="cpage" value="${ pageInfo.cpage }" />

	<div class="title_box fixed">
		<div class="article top_area">
			<div class="title">
				<h1><spring:message code="E0564" text="연동IP 관리 "/></h1>
				<p><spring:message code="E0565" text="연동IP를 관리할 수 있습니다."/></p>
			</div>
			<div class="search_box">
				<div class="inner">
					<div class="select_box">
						<form:select path="srch_type" cssClass="search_opt">
							<form:option value="ip"><spring:message code="E0001" text="IP"/></form:option>
							<form:option value="memo"><spring:message code="E0322" text="설명(메모)"/></form:option>
						</form:select>
					</div>
					<form:input path="srch_keyword" onfocus="relaysearch.init(this)" placeholder="검색어를 입력해주세요."/>
					<button type="button" class="btn1" onclick="relay_list.search()" >검색</button>
					<c:if test="${issearch}"><button type="button" id="allview" class="btn2" onclick="relay_list.viewAll();">전체목록</button></c:if>
				</div>
			</div>
		</div>

	</div>
	<!-- top area end -->


	<!-- content top start -->
	<div class="content_top fixed">
		<ul class="content_top_btn">
			<li><a class="btn2" href="javascript:;" onclick="relay_list.addForm()"><spring:message code="E0029" text="추가"/></a></li>
			<li><a class="btn2" href="javascript:;" onclick="relay_list.deleteClick()"><spring:message code="E0030" text="삭제"/></a></li>

		</ul>

		<div class="select_box">
			<select class="list_select" onchange="common.change_pagesize(this.value)">
				<option ${f:isSelected('15',UserInfo.pagesize)} value="15"><spring:message code="E0233" text="15"/></option>
				<option ${f:isSelected('30',UserInfo.pagesize)} value="30"><spring:message code="E0234" text="30"/></option>
				<option ${f:isSelected('50',UserInfo.pagesize)} value="50"><spring:message code="E0235" text="50"/></option>
				<option ${f:isSelected('100',UserInfo.pagesize)} value="100"><spring:message code="E0236" text="100"/></option>
			</select>
		</div>
	</div>
	<!-- content top end -->



	<div class="section pd_l30">
		<!-- content start -->

		<div class="article content">
			<!-- content area start -->
			<div class="content_area">
				<table  width ="100%" height="auto" >
					<colgroup>
						<col style="width:35px" />
						<col style="width:150px" />
						<col style="width:auto" />
						<col style="width:150px" />
					</colgroup>
					<thead class="fixed">
					<tr>
						<th  class="check_ico">
							<input id="all_check" type="checkbox" title="전체선택" onclick="common.select_all('ips');">
						</th>
						<th><spring:message code="E0566" text="IP"/></th>
						<th><spring:message code="E0322" text="설명(메모)"/></th>
						<th><spring:message code="E0067" text="등록일"/></th>
					</tr>
					</thead>
					<tbody>

					<c:if test="${ empty relayList }">
						<tr>
							<td colspan="3" class="txt center">
								<spring:message code="E0586" text="데이터가 없습니다." />
							</td>
						</tr>
					</c:if>
					<c:forEach varStatus="i" var="relayInfo" items="${ relayList }">
						<fmt:formatDate value="${relayInfo.regdate}" pattern="yyyy-MM-dd" var="regdate" />
						<fmt:formatDate value="${relayInfo.regdate}" pattern="HH:mm" var="time" />

						<tr>
							<td class="check_ico">
								<input type="checkbox" name="ips" value="${relayInfo.ip},${relayInfo.memo}" title="선택">
							</td>

							<td style="cursor: pointer;" onclick="relay_list.open_editPopup('${relayInfo.ip}','${relayInfo.memo}')">${relayInfo.ip}</td>
							<td>${relayInfo.memo}</td>
							<td>${regdate} ${time}</td>
						</tr>
					</c:forEach>

					</tbody>
				</table>
			</div>
			<!-- content area end -->
		</div>
		<!-- content end -->
		<div class="page_nav">
			<c:if test="${ empty relayList }">
			<pt:page>
				<pt:cpage>
					1
				</pt:cpage>
				<pt:pageSize>
					1
				</pt:pageSize>
				<pt:total>
					1
				</pt:total>
				<pt:jslink>relay_list.list</pt:jslink>
			</pt:page>
			</c:if>

			<c:if test="${ !empty relayList }">
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
					<pt:jslink>relay_list.list</pt:jslink>
				</pt:page>
			</c:if>
		</div>

		</form:form>
	</div>
	<!-- section end -->



	<!-- popup start-->
	<div id="addRelayLayer" style="display:none;" title="<spring:message code="E0566" text="IP"/> <spring:message code="E0029" text="추가"/>" >
		<form:form name="relayForm" id="relayForm" action="add.json" method="post" >
			<div class="popup">
				<div class="popup_content">
					<div class="popup_title">
						<span class="close_button" onclick="relay_add.close();"></span>
						<h3 class="title"><spring:message code="E0566" text="IP"/> <spring:message code="E0029" text="추가"/> </h3>
					</div>
					<div class="popup_body_wrap">
						<div class="popup_body">
							<!-- <p> 설명 텍스트 </p> -->
							<table width="100%" border="0" cellpadding="0" cellspacing="1" >
								<colgroup>
									<col style="width:150px;">
									<col style="width:auto;">
								</colgroup>
								<tbody>
								<tr>
									<th><spring:message code="E0566" text="IP"/></th>
									<td ><input type="text" id="ip" /></td>
								</tr>
								<tr>
									<th><spring:message code="E0322" text="설명(메모)"/></th>
									<td ><input type="text" id="memo" /></td>
								</tr>
								</tbody>
							</table>
						</div>
					</div>
					<div class="pop_footer">
						<div class="btn_div">
							<button type="button" class="btn2" onclick="relay_add.ipChk();"><spring:message code="E0029" text="추가"/></button>
							<button type="button" class="btn2" onclick="relay_add.close();"><spring:message code="E0065" text="취소"/></button>
						</div>
					</div>
				</div>
			</div>

		</form:form>
	</div>
	<!-- popup end-->


	<div id="editRelayLayer" style="display:none;" title="<spring:message code="E0564" text="연동IP 관리"/> <spring:message code="E0128" text="수정"/>" >
		<form:form name="relayForm" id="relayForm" action="edit.json" method="post" >
			<input type="hidden" id="ori_ip" value="">
		<div class="popup">
			<div class="popup_content">
				<div class="popup_title">
					<span class="close_button" onclick="relay_list.close_editPopup('edit');"></span>
					<h3 class="title"><spring:message code="E0564" text="연동IP 관리"/> <spring:message code="E0128" text="수정"/></h3>
				</div>
				<div class="popup_body_wrap">
				<div class="popup_body">
					<table width="100%" border="0" cellpadding="0" cellspacing="1" >
						<colgroup>
							<col style="width:150px;">
							<col style="width:auto;">
						</colgroup>
						<tbody>
						<tr>
							<th><spring:message code="E0566" text="IP"/></th>
							<td><input type="text" id="edit_ip" value=""></td><br>
						</tr>
						<tr>
							<th><spring:message code="E0322" text="설명(메모)"/></th>
							<td>
								<input type="text" id="edit_memo" value="">
							</td>
						</tr>
						</tbody>
					</table>
				</div>
				</div>
				<div class="pop_footer">
					<div class="btn_div">
						<button type="button" class="btn2" onclick="relay_list.edit();"><spring:message code="E0128" text="수정"/></button>
						<button type="button" class="btn2"  onclick="relay_list.close_editPopup('edit');"><spring:message code="E0065" text="취소"/></button>
					</div>
				</div>
			</div>
		</div>

	</div>
	</form:form>
</div>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file = "/WEB-INF/jsp/inc/taglib.jspf" %>
<script type="text/javascript" src="${staticURL}/sens-static/js/common/dkimsearch.js"></script>
<script type="text/javascript" src="${staticURL}/sens-static/js/sysman/dkim.js"></script>
<c:if test="${ !empty infoMessage }">
	<script>
		alert( '${infoMessage}' );
	</script>
</c:if>

	<!-- section start -->
	<!-- top area start -->
<div class="block">
    <input type="hidden" name="srch_key" id="srch_key" value="${srch_key}">
	<form:form  name="dkimListForm" modelAttribute="dkimListForm" id="dkimListForm" action="list.do"  method="post">
		<form:hidden path="cpage" id="cpage" value="${ pageInfo.cpage }" />
	<div class="title_box fixed">
		<div class="article top_area">
			<div class="title">
				<h1><spring:message code="E0617" text="DKIM 관리"/></h1>
				<p><spring:message code="E0618" text="DKIM을 관리할 수 있습니다."/></p>
			</div>
			<div class="search_box">
				<div class="inner">
					<form:input path="srch_keyword" onfocus="dkimsearch.init(this)" placeholder="검색어를 입력해주세요."/>
					<button type="button" class="btn1" onclick="dkimList.search()" >검색</button>
					<c:if test="${issearch}"><button type="button" id="allview" class="btn2" onclick="dkimList.viewAll();">전체목록</button></c:if>
				</div>
			</div>
		</div>

	</div>
	<!-- top area end -->


	<!-- content top start -->
	<div class="content_top fixed">
		<ul class="content_top_btn">
			<li><a class="btn2" href="javascript:;" onclick="dkimList.add()"><spring:message code="E0029" text="추가"/></a></li>
			<li><a class="btn2" href="javascript:;" onclick="dkimList.del()"><spring:message code="E0030" text="삭제"/></a></li>

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
						<col style="width:auto" />
						<col style="width:200px" />
						<col style="width:250px" />
						<col style="width:150px" />
					</colgroup>
					<thead class="fixed">
					<tr>
						<th  class="check_ico">
							<input id="all_check" type="checkbox" title="전체선택" onclick="common.select_all('dkim');">
						</th>
						<th><spring:message code="E0109" text="도메인명"/></th>
						<th><spring:message code="E0598" text="지정자"/></th>
						<th><spring:message code="E0599" text="사용여부"/></th>
						<th><spring:message code="E0067	" text="등록일"/></th>
					</tr>
					</thead>
					<tbody>

					<c:if test="${ empty dkimList }">
						<tr>
							<td colspan="5" class="txt center">
								<spring:message code="E0586" text="데이터가 없습니다." />
							</td>
						</tr>
					</c:if>
					<c:forEach varStatus="i" var="dkimInfo" items="${ dkimList }">
						<fmt:formatDate value="${dkimInfo.regdate}" pattern="yyyy-MM-dd HH:mm" var="regdate" />
<%--						<fmt:formatDate value="${blockInfo.regdate}" pattern="HH:mm" var="time" />--%>
						<tr>
							<td class="check_ico">
								<input type="checkbox" name="dkim" value="${dkimInfo.domain}" title="선택">
							</td>

							<td> <a href="view.do?domain=${dkimInfo.domain}&cpage=${cpage}&srch_keyword=${srch_key}">${ dkimInfo.domain }</a></td>
							<td>${ dkimInfo.selector }</td>
							<td>
								<span style="padding-right:10px;"><input type="radio" class="dkim_use_radio"  name="${dkimInfo.domain}_use" id="${dkimInfo.domain}_use_on" value="1" onclick="dkimList.active('${dkimInfo.domain}','1');"  ${f:isChecked('1',dkimInfo.use_sign)}><label for="${dkimInfo.domain}_use_on"> <spring:message code="88" text="사용"/></label></span>
								<input type="radio" class="dkim_use_radio"  name="${dkimInfo.domain}_use" id="${dkimInfo.domain}_use_off" value="0" onclick="dkimList.active('${dkimInfo.domain}','0');" ${f:isChecked('0',dkimInfo.use_sign)}><label for="${dkimInfo.domain}_use_off"> <spring:message code="89" text="사용 안함"/></label>
							</td>
							<td>${regdate}</td>
						</tr>
					</c:forEach>

					</tbody>
				</table>
			</div>
			<!-- content area end -->
		</div>
		<!-- content end -->
		<div class="page_nav">
			<c:if test="${ empty dkimList }">
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
					<pt:jslink>dkimList.list</pt:jslink>
				</pt:page>
			</c:if>

			<c:if test="${ !empty dkimList }">
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
					<pt:jslink>dkimList.list</pt:jslink>
				</pt:page>
			</c:if>
		</div>
		</form:form>
		<!-- nav start -->

		<!-- nav end -->
	<!-- section end -->



	<!-- popup start-->
</div>
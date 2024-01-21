<%@ page contentType="text/html;charset=utf-8" %>
<%@ page language = "java" %>
<%@ include file = "../inc/common.jsp" %>
<script type="text/javascript" src="${staticURL}/sens-static/js/send/category.js"></script>
<script type="text/javascript" src="${staticURL}/sens-static/js/messages/sendmanage_${UserInfo.language}.js"></script>
<script>
	function enterkey() {
		if (window.event.keyCode == 13) {
			event.preventDefault();
			// 엔터키가 눌렸을 때 실행할 내용
			category.search();
		}
	}
</script>

<div class="categoryList">

<form:form modelAttribute="categoryListForm" id="categoryListForm" action="list.do" method="get">
	<form:hidden path="cpage" id="cpage" value="${ pageInfo.cpage }" />
	<input type="hidden" id="ori_name" value="">
	<div class="title_box fixed">
		<div class="article top_area">
			<div class="title">
				<h1><spring:message code="E0010" text="발송분류 관리"/></h1>
				<p><spring:message code="E0011" text="발송하는 메일에 대한 카테고리를 관리할 수 있습니다."/></p>
			</div>
			<div class="search_box">
				<div class="inner">
					<div class="select_box">
						<form:select path="srch_type" class="search_opt">
							<form:option value="name"><spring:message code="E0255" text="발송분류"/></form:option>
							<form:option value="userid"><spring:message code="E0066" text="작성자"/></form:option>
						</form:select>
					</div>
					<form:input path="srch_keyword" id="srch_keyword" onkeypress="enterkey()" value="" placeholder="검색어를 입력하세요." />
					<button type="button" class="btn1" onclick="category.search();">검색</button>
					<c:if test="${issearch}"><button type="button" id="allview" class="btn2" onclick="category.viewAll();">전체목록</button></c:if>
				</div>
			</div>
		</div>
	</div>
	<!-- top area end -->
	<!-- content top start -->
	<div class="content_top fixed">
		<ul class="content_top_btn">
			<li><a class="btn2" href="javascript:;" onclick="category.open_addPopup();"><spring:message code="E0029" text="추가"/></a></li>
			<li><a class="btn2" href="javascript:;" onclick="category.deleteClick();"><spring:message code="E0030" text="삭제"/></a></li>
		</ul>
		<%--<div class="content_top_opt">
			<button type="button" onclick="common.open_dropMenu();"><img src="../../sens-static/images/top_opt_btn.png"/></button>
			<div class="drop_menu" style="display:none;" >
				<table>
					<tbody>
						<tr>
							<th scope="row"><span><spring:message code="E0040" text="목록 개수"/></span></th>
							<td>
								<select onchange="common.change_pagesize(this.value)">
		                            <option ${f:isSelected('15',UserInfo.pagesize)} value="15">15</option>
		                            <option ${f:isSelected('30',UserInfo.pagesize)} value="30">30</option>
		                            <option ${f:isSelected('50',UserInfo.pagesize)} value="50">50</option>
		                            <option ${f:isSelected('100',UserInfo.pagesize)} value="100">100</option>
		                        </select>
							</td>
						</tr>
						<tr>
							<th scope="row"><span><spring:message code="E0041" text="목록 간격"/></span></th>
							<td>
								<select onchange="">
									<option><spring:message code="E0043" text="좁게"/></option>
									<option><spring:message code="E0045" text="보통"/></option>
									<option><spring:message code="E0044" text="넓게"/></option>
								</select>
							</td>
						</tr>
					</tbody>
				</table>
			</div>
		</div>--%>
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
						<col style="width:120px" />
						<col style="width:150px" />
					</colgroup>
					<thead>
						<tr>
							<th class="check_ico" >
								<input type="checkbox" id="all_check" onclick="common.select_all('ukeys')" title="전체선택">
							</th>
							<th><spring:message code="E0255" text="발송분류"/></th>
							<th><spring:message code="E0066" text="작성자"/></th>
							<th><spring:message code="E0067" text="등록일"/></th>
						</tr>
					</thead>
					<tbody>
						<c:choose>
							<c:when test="${empty categoryList}">
								<tr><td colspan="4" class="txt center">
									<spring:message code="E0586" text="데이터가 없습니다."/>
								</td></tr>
							</c:when>
							<c:otherwise>
								<c:forEach var="category" items="${categoryList}">
									<fmt:formatDate value="${category.regdate}" pattern="yyyy-MM-dd" var="regdate"/>
									<fmt:formatDate value="${category.regdate}" pattern="HH:mm" var="time"/>
									<tr>
										<td class="check_ico"><input type="checkbox" name="ukeys" value="${category.ukey},${category.name}" title="선택"></td>
										<td style="cursor: pointer;" onclick="category.open_editPopup('${category.ukey}','${category.name}','${category.userid}')"><c:out value="${category.name}"></c:out></td>
										<td><c:out value="${category.userid }"></c:out></td>
										<td><c:out value="${regdate}"></c:out> <c:out value="${time}"></c:out></td>
									</tr>
								</c:forEach>
							</c:otherwise>
						</c:choose>
					</tbody>
				</table>
			</div>
			<!-- content area end -->
		</div>

		<div class="page_nav">
				<c:if test="${empty categoryList}">
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
						<pt:jslink>category.list</pt:jslink>
					</pt:page>
				</c:if>

				<c:if test="${!empty categoryList}">
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
					<pt:jslink>category.list</pt:jslink>
				</pt:page>
				</c:if>
		</div>
	</div>
		</form:form>

		<!-- popup start-->
		<div class="popup" id="add_popup">
			 <div class="popup_content" style="height:205px;">
				<div class="popup_title">
					<span class="close_button" onclick="category.close_popup('add');"></span>
					<h3 class="title"><spring:message code="E0013" text="발송분류 추가"/></h3>
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
									<th><spring:message code="E0255" text="발송분류"/></th>
									<td><input type="text" id="add_name" maxlength="30">
									<div class="care_txt"><span class="txt strong red">&nbsp; <spring:message code="E074-" text="※ (-) , (_) ,(.) , (,) 를 제외한 특수문자는 사용할 수 없습니다."/></span></div>
									</td>
								</tr>
							</tbody>
						</table>
					</div>
				</div>
				 <div class="pop_footer">
					 <div class="btn_div">
						 <button type="button" class="btn2" onclick="category.add();")><spring:message code="E0064" text="확인"/></button>
						 <button type="button" class="btn2" onclick="category.close_popup('add');"><spring:message code="E0065" text="취소"/></button>
					 </div>
				 </div>
			 </div>
		 </div>

		 <div class="popup" id="edit_popup">
			 <div class="popup_content" style="height:245px;">
				<div class="popup_title">
					<span class="close_button" onclick="category.close_popup('edit');"></span>
					<h3 class="title"><spring:message code="E0014" text="발송분류 수정"/></h3>
				</div>
				<div class="popup_body_wrap">
					<div class="popup_body">
						<!-- <p> 설명 텍스트 </p> -->
						<input type="hidden" id="edit_ukey"/>
						<table width="100%" border="0" cellpadding="0" cellspacing="1">
							<colgroup>
								<col style="width:150px;">
								<col style="width:auto;">
							</colgroup>
							<tbody>
								<tr>
									<th><spring:message code="E0066" text="작성자"/></th>
									<td><span id="reg_userid"></span></td>
								</tr>
								<tr>
									<th ><spring:message code="E0012" text="카테고리"/></th>
									<td><input type="text" id="edit_name" maxlength="30">
										<div class="care_txt"><span class="txt strong red">&nbsp; <spring:message code="E0740" text="※ (-) , (_) ,(.) , (,) 를 제외한 특수문자는 사용할 수 없습니다."/></span></div>

									</td>
								</tr>
							</tbody>
						</table>
					</div>
				</div>
				<div class="pop_footer">
					<div class="btn_div">
						 <button type="button" class="btn2" onclick="category.edit();"><spring:message code="E0064" text="확인"/></button>
						 <button type="button" class="btn2" onclick="category.close_popup('edit');"><spring:message code="E0065" text="취소"/></button>
					</div>
				</div>
			 </div>
		 </div>
 		 <!-- popup end-->
	</div>


</div>
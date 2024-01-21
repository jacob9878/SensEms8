<%@ page contentType="text/html;charset=utf-8" %>
<%@ page language = "java" %>
<%@ include file = "../inc/common.jsp" %>
<c:if test="${ !empty infoMessage }">
    <script>
        alert( '${infoMessage}' );
    </script>
</c:if>
<script type="text/javascript" src="${staticURL}/sens-static/js/send/template.js"></script>
<script type="text/javascript" src="${staticURL}/sens-static/js/common/templateSearch.js"></script>
<script type="text/javascript" src="${staticURL}/sens-static/js/messages/sendmanage_${UserInfo.language}.js"></script>

<div class="templatemanager">
	<!-- section start -->
	<!-- top area start -->
    <input type="hidden" name="srch_key" id="srch_key" value="${srch_key}">
	<form:form method="get" name="TemplateListForm" modelAttribute="TemplateListForm" action="list.do">
	<form:hidden path="cpage" value="${ pageInfo.cpage }" />
	<form:hidden path="srch_type" id="srch_type" name="srch_type"/>
	<div class="title_box fixed">
		<div class="article top_area">
			<div class="title">
				<h1><spring:message code="E0118" text="템플릿 관리"/></h1>
				<p><spring:message code="E0119" text="발송하는 메일에 삽입하는 템플릿을 관리할 수 있습니다."/></p>
			</div>
			<div class="search_box">
				<div class="inner">
					<form:input path="srch_keyword" name="srch_keyword" onfocus="templateList.init(this)" placeholder="검색어를 입력해주세요."/>
					<button class="btn1" onclick="templateList.search();">검색</button>
					<c:if test="${issearch}"><button type="button" id="allview" class="btn2" onclick="templateList.viewAll();">전체목록</button></c:if>
				</div>
			</div>
		</div>
	</div>
	<!-- top area end -->


	<!-- tab wrap start -->
	<div class="tab_area">
		<ul>
			<li <c:if test="${TemplateListForm.srch_type == '02'}">class="on"</c:if> onclick="templateList.changeOption('02')"><a href="javascript:;" ><spring:message code="E0120" text="개인템플릿"/></a></li>
			<li <c:if test="${TemplateListForm.srch_type == '01'}">class="on"</c:if> onclick="templateList.changeOption('01')"><a href="javascript:;" ><spring:message code="E0121" text="공용템플릿"/></a></li>
		</ul>
	</div>
	<!-- tab wrap end -->

	<!-- content top start -->
	<div class="content_top fixed tab">
		<ul class="content_top_btn">
			<li><a class="btn2" href="javascript:;" onclick="templateList.add('${TemplateListForm.srch_type}','${srch_key}','${srch_type}','${cpage}')"><spring:message code="E0029" text="추가"/></a></li>
			<c:if test="${TemplateListForm.srch_type == '02'}"><li><a class="btn2" href="javascript:;" onclick="templateDel.delete()"><spring:message code="E0030" text="삭제"/></a></li></c:if>
			<c:if test="${TemplateListForm.srch_type == '01'}"><li><a class="btn2" href="javascript:;" onclick="templateDel.checkDelete()"><spring:message code="E0030" text="삭제"/></a></li></c:if>

		</ul>
		<%--<div class="content_top_opt">
			<button type="button" onclick="common.open_dropMenu();"><img src="../../sens-static/images/top_opt_btn.png" alt="" /></button>
			<div class="drop_menu" style="display:none;" >
				<table>
					<tbody>
					<tr height="30px">
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



	<div class="section pd_l30 tab">
		<!-- content start -->

		<div class="article content">
			<!-- content area start -->
			<div class="content_area">
				<table width ="100%" height="auto" >
					<colgroup>
						<col style="width:35px" />
						<col style="width:120px" />
						<col style="width:auto" />
						<col style="width:120px" />
						<col style="width:150px" />
						<col style="width:120px" />
					</colgroup>
					<thead class="fixed">
					<tr>
						<th class="check_ico">
							<input id="all_check" type="checkbox" onclick="common.select_all('ukeys');">
						</th>
						<th><spring:message code="E0101" text="분류"/></th>
						<th ><spring:message code="E0103" text="제목"/></th>
						<th><spring:message code="E0066" text="작성자"/></th>
						<th><spring:message code="E0067" text="등록일"/></th>
						<th class="txt center" ><spring:message code="E0104" text="미리보기"/></th>
					</tr>
					</thead>
					<tbody>
					<c:if test="${ empty templateList }">
						<td colspan="6" class="txt center"><spring:message code="E0586" text="데이터가 없습니다."/></td>
                    </c:if>
					<c:forEach var="templateInfo" items="${templateList}">
                        <fmt:formatDate value="${templateInfo.regdate}" pattern="yyyy-MM-dd" var="regdate" />
                        <fmt:formatDate value="${templateInfo.regdate}" pattern="HH:mm" var="time" />
                        <tr>
                            <td class="check_ico">
                                <input type="checkbox" name="ukeys" value="${templateInfo.ukey},${templateInfo.temp_name}">
                            </td>
                            <c:if test="${templateInfo.flag eq '02'}">
                                <td><spring:message code="E0120" text="개인템플릿"/></td>
                            </c:if>
                            <c:if test="${templateInfo.flag eq '01'}">
                                <td><spring:message code="E0121" text="공용템플릿"/></td>
                            </c:if>
							<td><a href="javascript:;" onclick="templateList.edit('${templateInfo.ukey}','${srch_key}','${srch_type}','${cpage}')"><c:out value="${templateInfo.temp_name}"></c:out></a></td>
                            <td><c:out value="${templateInfo.userid}"></c:out></td>
                            <td><c:out value="${regdate}"></c:out> <c:out value="${time}"></c:out></td>
							<td class="txt center">
								<c:if test="${templateInfo.contents != ''}">
									<button type="button" class="btn3" onclick="templateList.previewTemplate('${templateInfo.ukey}')"><spring:message code="E0104" text="미리보기"/></button>
								</c:if>
							</td>
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
			<c:if test="${ empty templateList }">
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
				<pt:jslink>templateList.list</pt:jslink>
			</pt:page>
			</c:if>

			<c:if test="${ !empty templateList }">
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
					<pt:jslink>templateList.list</pt:jslink>
				</pt:page>
			</c:if>
		</div>
		<!-- nav end -->
	</form:form>
	</div>
	<!-- section end -->
</div>
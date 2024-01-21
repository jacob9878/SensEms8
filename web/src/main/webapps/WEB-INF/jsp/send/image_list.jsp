<%@ page contentType="text/html;charset=utf-8" %>
<%@ page language = "java" %>
<%@ include file = "../inc/common.jsp" %>
<script type="text/javascript" src="${staticURL}/sens-static/js/send/image.js"></script>
<script type="text/javascript" src="${staticURL}/sens-static/js/messages/sendmanage_${UserInfo.language}.js"></script>
<script type="text/javascript" src="${staticURL}/sens-static/js/common/imagesearch.js"></script>

<div class="imgmanager">
	<!-- section start -->
	<!-- top area start -->
	<form:form method="get" name="ImageListForm" modelAttribute="ImageListForm" action="list.do">
	<form:hidden path="cpage" value="${ pageInfo.cpage }" />
	<form:hidden path="srch_type"/>
	<div class="title_box fixed">
		<div class="article top_area">
			<div class="title">
				<h1><spring:message code="E0096" text="이미지 관리"/></h1>
				<p><spring:message code="E0097" text="발송하는 메일에 첨부할 이미지를 관리할 수 있습니다."/></p>
			</div>
			<div class="search_box">
				<div class="inner">
					<form:input path="srch_keyword" name="srch_keyword" onfocus="imagesearch.init(this)" placeholder="검색어를 입력해주세요."/>
					<button class="btn1" onclick="imageList.search();">검색</button>
					<c:if test="${issearch}"><button type="button" id="allview" class="btn2" onclick="imageList.viewAll();">전체목록</button></c:if>
				</div>
			</div>
		</div>
	</div>
	<!-- top area end -->


	<!-- tab wrap start -->
	<div class="tab_area">
		<ul>
			<li <c:if test="${ImageListForm.srch_type == '02'}">class="on"</c:if> onclick="imageList.changeOption('02')"><a href="javascript:;" ><spring:message code="E0099" text="개인이미지"/></a></li>
			<li <c:if test="${ImageListForm.srch_type == '01'}">class="on"</c:if> onclick="imageList.changeOption('01')"><a href="javascript:;" ><spring:message code="E0100" text="공용이미지"/></a></li>
		</ul>
	</div>
	<!-- tab wrap end -->

	<!-- content top start -->
	<div class="content_top fixed tab">
		<ul class="content_top_btn">
			<li><a class="btn2" href="javascript:;" onclick="imageList.open_addPopup()"><spring:message code="E0029" text="추가"/></a></li>
			<c:if test="${ImageListForm.srch_type == '02'}"><li><a class="btn2" href="javascript:;" onclick="imageDel.delete()"><spring:message code="E0030" text="삭제"/></a></li></c:if>
			<c:if test="${ImageListForm.srch_type == '01'}"><li><a class="btn2" href="javascript:;" onclick="imageDel.checkDelete()"><spring:message code="E0030" text="삭제"/></a></li></c:if>


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
						<col style="width:80px" />
						<col style="width:auto" />
						<col style="width:120px" />
						<col style="width:150px" />
						<col style="width:95px" />
					</colgroup>
					<thead class="fixed">
					<tr>
						<th class="check_ico" >
							<input id="all_check" type="checkbox" onclick="common.select_all('ukeys');">
						</th>
						<th><spring:message code="E0101" text="분류"/></th>
						<th><spring:message code="E0102" text="이미지"/></th>
						<th><spring:message code="E0103" text="제목"/></th>
						<th><spring:message code="E0066" text="작성자"/></th>
						<th><spring:message code="E0067" text="등록일"/></th>
						<th class="txt center" ><spring:message code="E0104" text="미리보기"/></th>
					</tr>
					</thead>
					<tbody>
					<c:if test="${ empty imageList }">
						<td colspan="7" class="txt center"><spring:message code="E0586" text="데이터가 없습니다."/></td>
                    </c:if>
					<c:forEach var="imageInfo" items="${imageList}">
                        <fmt:formatDate value="${imageInfo.regdate}" pattern="yyyy-MM-dd" var="regdate" />
                        <fmt:formatDate value="${imageInfo.regdate}" pattern="HH:mm" var="time" />
                        <tr >
                            <td class="check_ico">
                                <input type="checkbox" name="ukeys" value="${imageInfo.ukey},${imageInfo.image_name}">
                            </td>
                            <c:if test="${imageInfo.flag eq '02'}">
                                <td><spring:message code="E0099" text="개인이미지"/></td>
                            </c:if>
                            <c:if test="${imageInfo.flag eq '01'}">
                                <td><spring:message code="E0100" text="공용이미지"/></td>
                            </c:if>
                            <td class="none_img txt center"><img src="/send/image/view.do?ukey=${imageInfo.ukey}" width="60px" height="60px" alt="" onclick="imageList.previewImage('${imageInfo.ukey}');"/></td>
                            <td><c:out value="${imageInfo.image_name}"></c:out></td>
                            <td><c:out value="${imageInfo.userid}"></c:out></td>
                            <td><c:out value="${regdate}"></c:out> <c:out value="${time}"></c:out></td>
                            <td class="txt center"><button type="button" class="btn3" onclick="imageList.previewImage('${imageInfo.ukey}');"><spring:message code="E0104" text="미리보기"/></button></td>
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
			<c:if test="${ empty imageList }">
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
				<pt:jslink>imageList.list</pt:jslink>
			</pt:page>
			</c:if>

			<c:if test="${ !empty imageList }">
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
					<pt:jslink>imageList.list</pt:jslink>
				</pt:page>
			</c:if>
		</div>
		<!-- nav end -->
	</form:form>
		<!-- popup start-->
		<div class="popup image-add" id="add_imagePopup" style="display: none">
			<div class="popup_content">
				<div class="popup_title">
					<span class="close_button" onclick="imageList.close_popup();"></span>
					<h3 class="title"><spring:message code="E0106" text="이미지 추가"/></h3>
				</div>
				<div class="popup_body_wrap">
					<div class="popup_body">
						<!-- <p> 설명 텍스트 </p> -->
						<table width="100%" border="0" cellpadding="0" cellspacing="1" >
							<colgroup>
								<col width="150px"/>
								<col width="auto"/>
							</colgroup>
							<tbody>
							<form:form id="image_upload" method="post" enctype="multipart/form-data" >
								<tr >
									<th><spring:message code="E0101" text="분류"/></th>
									<td>
										<select id="flag">
											<option value = "02"><spring:message code="E0099" text="개인이미지"/></option>
											<option value = "01"><spring:message code="E0100" text="공용이미지"/></option>
										</select>
									</td>
								</tr>
								<tr>
									<th><spring:message code="E0103" text="제목"/></th>
									<td>
										<input type="text" id="image_name" maxlength="50"/>
                                        <input type="text" style="display:none"/>
									</td>
								</tr>
								<tr>
									<th><spring:message code="E0102" text="이미지"/></th>
									<td>
										<input type="file" id="file_upload"/>
										<div class="care_txt">
										<span class="txt red"><spring:message code="E0110" text="이미지파일은 JPG, GIF, PNG, BMP 확장자만 등록가능합니다."/></span>
										</div>
									</td>
								</tr>
							</form:form>
							</tbody>
						</table>

					</div>
				</div>
				<div class="pop_footer">
					<div class="btn_div">
						<button type="button" class="btn2" onclick="imageList.addImage();"><spring:message code="E0029" text="추가"/></button>
						<button type="button" class="btn2" onclick="imageList.close_popup();"><spring:message code="E0065" text="취소"/></button>
					</div>
				</div>
			</div>
		</div>
		<!-- popup end-->
	</div>
	<!-- section end -->
</div>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file = "/WEB-INF/jsp/inc/taglib.jspf" %>
<script type="text/javascript" src="${staticURL}/sens-static/js/common/rejectsearch.js"></script>
<script type="text/javascript" src="/sens-static/js/send/reject.js"></script>

<div class="rejectlist">
<!-- section start -->
<!-- top area start -->
<form:form  name="rejectListForm" modelAttribute="rejectListForm" id="rejectListForm" action="list.do"  method="get">
    <form:hidden path="cpage" id="cpage" value="${ pageInfo.cpage }" />
<div class="title_box fixed">
    <div class="article top_area">
        <div class="title">
            <h1><spring:message code="E0521" text="수신거부 관리"/></h1>
            <p><spring:message code="E0072" text="수신거부 이메일을 관리할 수 있습니다."/></p>
        </div>
        <div class="search_box">
            <div class="inner">
                <form:input path="srch_keyword" onfocus="rejectsearch.init(this)" name="srch_keyword" value="" placeholder="검색어를 입력하세요."/>
                <button type="button" class="btn1" onclick="reject_list.search();" >검색</button>
                <c:if test="${issearch}"><button type="button" id="allview" class="btn2" onclick="reject_list.viewAll();">전체목록</button></c:if>

            </div>
        </div>
    </div>
</div>
<!-- top area end -->


<!-- content top start -->
<div class="content_top fixed">
    <ul class="content_top_btn">
        <li><a class="btn2" href="javascript:;" onclick="reject_list.addForm()"><spring:message code="E0029" text="추가"/></a></li>
        <li><a class="btn2" href="javascript:;" onclick="reject_list.deleteClick()"><spring:message code="E0030" text="삭제"/></a></li>
        <li><a class="btn2" href="javascript:;" onclick="reject_list.doSave()"><spring:message code="E0073" text="목록저장"/></a></li>
        <li><a class="btn2" href="javascript:;" onclick="reject_list.doImport()"><spring:message code="E0074" text="가져오기"/></a></li>

    </ul>

    <%--<div class="content_top_opt">
        <button type="button" onclick="common.open_dropMenu();"><img src="../../sens-static/images/top_opt_btn.png" alt="" /></button>
        <div class="drop_menu" style="display:none;" >
        <table>
                <tbody>
                <tr>
                    <th scope="row"><span><spring:message code="E0040" text="목록개수"/></span></th>
                    <td>
                        <select onchange="common.change_pagesize(this.value)">
                            <option ${f:isSelected( '15' , UserInfo.pagesize ) } value="15">15</option>
                            <option ${f:isSelected( '30' , UserInfo.pagesize ) } value="30">30</option>
                            <option ${f:isSelected( '50' , UserInfo.pagesize ) } value="50">50</option>
                            <option ${f:isSelected( '100' , UserInfo.pagesize ) } value="100">100</option>
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
                    <col style="width:150px" />
                </colgroup>
                <thead class="fixed">
                    <tr>
                        <th class="check_ico">
                        <input id="all_check" type="checkbox" title="전체선택" onclick="common.select_all('emails');">
                        </th>
                        <th><spring:message code="E0022" text="E-Mail"/></th>
                        <th><spring:message code="E0067" text="등록일"/></th>
                    </tr>
                </thead>
                <tbody>

            <c:if test="${ empty rejectList }">
                <tr>
                    <td colspan="3" class="txt center">
                    <spring:message code="E0586" text="데이터가 없습니다." />
                    </td>
                </tr>
            </c:if>

            <c:forEach varStatus="i" var="rejectInfo" items="${ rejectList }">
            <fmt:formatDate value="${rejectInfo.regdate}" pattern="yyyy-MM-dd" var="regdate" />
            <fmt:formatDate value="${rejectInfo.regdate}" pattern="HH:mm" var="time" />

                <tr>
                    <td class="check_ico">
                        <input type="checkbox" name="emails" value="${rejectInfo.email }" title="선택">
                    </td>

                    <td style="cursor: pointer;" onclick="reject_list.open_editPopup('${rejectInfo.email}')"><c:out value="${rejectInfo.email}"></c:out></td>
                    <td><c:out value="${regdate}"></c:out> <c:out value="${time}"></c:out></td>
                </tr>
            </c:forEach>
                </tbody>
            </table>
        </div>
        <!-- content area end -->
    </div>

    <!-- content end -->
    <div class="page_nav">
        <c:if test="${ empty rejectList }">
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
            <pt:jslink>reject_list.list</pt:jslink>
        </pt:page>
        </c:if>

        <c:if test="${ !empty rejectList }">
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
                <pt:jslink>reject_list.list</pt:jslink>
            </pt:page>
        </c:if>
    </div>

</form:form>
</div>
<!-- section end -->
<div style="display: none;">
    <form:form id="uploadForm" method="post" enctype="multipart/form-data" >
        <input type="file" id="file_upload" onchange="reject_list.fileupload()"/>
        <button id="button_upload" type="button">submit</button>
    </form:form>
</div>



<!-- popup start-->
<div id="addRejectLayer" style="display:none;" title="<spring:message code="E0071" text="수신거부"/> <spring:message code="E0029" text="추가"/>" >

        <div class="popup">
            <div class="popup_content" style="height: 200px">
                <div class="popup_title">
                    <span class="close_button" onclick="reject_add.close();"></span>
                    <h3 class="title"><spring:message code="E0071" text="수신거부"/> <spring:message code="E0029" text="추가"/> </h3>
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
                                <th><spring:message code="E0022" text="E-Mail"/></th>
                                <td ><input type="text" id="email" ></td>
                            </tr>
                            </tbody>
                        </table>

                    </div>
                </div>
                <div class="pop_footer">
                    <div class="btn_div">
                        <button type="button" class="btn2" onclick="reject_add.emailChk();"><spring:message code="E0029" text="추가"/></button>
                        <button type="button" class="btn2" onclick="reject_add.close();"><spring:message code="E0065" text="취소"/></button>
                    </div>
                </div>
            </div>
        </div>


</div>
<!-- popup end-->


    <div id="editRejectLayer" style="display:none;" title="<spring:message code="E0521" text="수신거부 관리"/> <spring:message code="E0128" text="수정"/>" >

    <div class="popup">
        <div class="popup_content" style="height: 200px">
            <div class="popup_title">
                <span class="close_button" onclick="reject_list.close_editPopup('edit');"></span>
                <h3 class="title"><spring:message code="E0521" text="수신거부 관리"/> <spring:message code="E0128" text="수정"/></h3>
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
                        <th><spring:message code="E0022" text="E-Mail"/></th>

                        <td><input type="text" id="edit_email" value=""></td>
                        <td width="30px"><input type="hidden" id="ori_email" value=""></td>
                    </tr>
                    </tbody>
                </table>
            </div>
            </div>
            <div class ="pop_footer">
                <div class="btn_div">
                    <button type="button" class="btn2" onclick="reject_list.edit();"><spring:message code="E0128" text="수정"/></button>
                    <button type="button" class="btn2" onclick="reject_list.close_editPopup('edit');"><spring:message code="E0065" text="취소"/></button>
                </div>
            </div>
        </div>

    </div>

    </div>

</div>
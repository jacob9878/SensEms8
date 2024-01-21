<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file = "/WEB-INF/jsp/inc/taglib.jspf" %>
<script type="text/javascript" src="/sens-static/js/sysman/user.js"></script>
<script type="text/javascript" src="${staticURL}/sens-static/js/common/usersearch.js"></script>
<c:if test="${ !empty infoMessage }">
    <script>
        alert( '${infoMessage}' );
    </script>
</c:if>
<div class="adminuser">
<!-- section start -->
<!-- top area start -->
    <input type="hidden" id="srch_key" value="${srch_key}">
    <input type="hidden" id="srch_type" value="${srch_type}">
<form:form method="get" name="UserListForm" modelAttribute="UserListForm" action="list.do">
    <form:hidden path="cpage" value="${ pageInfo.cpage }" />
    <input type="hidden" id="search_key" value="${ check_keyword }">

<div class="title_box fixed">
    <div class="article top_area">
        <div class="title">
            <h1><spring:message code="E0016" text="사용자 관리"/>&nbsp;<span style="color:#3366ff">${totalsize}</span></h1>
            <p><spring:message code="E0017" text="사용자 정보를 관리할 수 있습니다."/></p>
        </div>
        <div class="search_box">
            <div class="inner">
                <div class="select_box">
                    <form:select path="srch_type" cssClass="search_opt">
                        <form:option value="userid"><spring:message code="E0001" text="아이디"/></form:option>
                        <form:option value="uname"><spring:message code="E0018" text="이름"/></form:option>
                    </form:select>
                </div>
                <form:input path="srch_keyword" name="srch_keyword" onfocus="usersearch.init(this)" placeholder="검색어를 입력해주세요."/>
                <button type="button" class="btn1"  onclick="userList.search()" >검색</button>
                <c:if test="${issearch}"><button type="button" id="allview" class="btn2" onclick="userList.viewAll();">전체목록</button></c:if>
            </div>
        </div>
    </div>
</div>
<!-- top area end -->


<!-- content top start -->
<div class="content_top fixed">
    <ul class="content_top_btn">
        <li><a class="btn2" href="javascript:;" onclick="userList.add();"><spring:message code="E0029" text="추가"/></a></li>
        <li><a class="btn2" href="javascript:;" onclick="userDel.delete();"><spring:message code="E0030" text="삭제"/></a></li>
    </ul>
    <%--<div class="content_top_opt">
        <button type="button" onclick="common.open_dropMenu();" ><img src="../../sens-static/images/top_opt_btn.png" alt="" /></button>
        <div class="drop_menu" style="display: none">
            <table>
                <tbody>
                <tr>
                    <th scope="row"><span><spring:message code="E0040" text="목록 개수"/></span></th>
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
                    <th scope="row"><span><spring:message code="E0041" text="목록간격"/></span></th>
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
        <form:select path="permission" cssClass="w80" onchange="userList.changeOption();">
            <form:option value=""><spring:message code="E0023" text="권한"/></form:option>
            <form:option value="A"><spring:message code="E0025" text="관리자"/></form:option>
            <form:option value="U"><spring:message code="E0026" text="사용자"/></form:option>
        </form:select>
        <form:select path="isStop" cssClass="w80" onchange="userList.changeOption();">
            <form:option value=""><spring:message code="E0024" text="상태"/></form:option>
            <form:option value="0"><spring:message code="E0028" text="정상"/></form:option>
            <form:option value="1"><spring:message code="E0027" text="사용정지"/></form:option>
        </form:select>
        <%--<form:select path="use_smtp" cssClass="w80" onchange="userList.changeOption();">
            <form:option value=""><spring:message code="E0611" text="SMTP"/></form:option>
            <form:option value="0"><spring:message code="E0597" text="미인증"/></form:option>
            <form:option value="1"><spring:message code="E0610" text="인증"/></form:option>
        </form:select>--%>

        <form:select path="" cssClass="w80" onchange="common.change_pagesize(this.value)" >
            <option ${f:isSelected('15',UserInfo.pagesize)} value="15"><spring:message code="E0233" text="15"/></option>
            <option ${f:isSelected('30',UserInfo.pagesize)} value="30"><spring:message code="E0234" text="30"/></option>
            <option ${f:isSelected('50',UserInfo.pagesize)} value="50"><spring:message code="E0235" text="50"/></option>
            <option ${f:isSelected('100',UserInfo.pagesize)} value="100"><spring:message code="E0236" text="100"/></option>
        </form:select>
    </div>
    <div class="select_box">
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
                    <col style="width:130px" />
                    <col style="width:130px" />
                    <col style="width:130px" />
                    <col style="width:120px" />
                    <col style="width:auto" />
                    <col style="width:auto" />
                    <col style="width:90px" />
                    <col style="width:90px" />
                    <%-- gs 인증 끝난 후 다시 주석해제 예정--%>
                    <%--<col style="width:120px" />--%>
                </colgroup>
                <thead class="fixed">
                <tr>
                    <th class="check_ico">
                        <input id="all_check" type="checkbox" onclick="common.select_all('userIds');">
                    </th>
                    <th><spring:message code="E0001" text="아이디"/></th>
                    <th><spring:message code="E0018" text="이름"/></th>
                    <th><spring:message code="E0020" text="소속"/></th>
                    <th><spring:message code="E0021" text="직책"/></th>
                    <th><spring:message code="E0022" text="E-Mail"/></th>
                    <th><spring:message code="E0051" text="승인메일 주소"/></th>
                    <th><spring:message code="E0023" text="권한"/></th>
                    <th><spring:message code="E0024" text="상태"/></th>

                    <%-- gs 인증 끝난 후 다시 주석해제 예정--%>
                    <%--<th><spring:message code="E0730" text="SMTP 인증 권한"/></th>--%>
                </tr>
                </thead>
                <tbody>
                <c:if test="${ empty userList }">
                    <td colspan="9" class="txt center"><spring:message code="E0586" text="데이터가 없습니다."/></td>
                </c:if>
                <c:forEach var="userInfo" items="${userList}">
                    <tr>
                        <td class="check_ico" >
                            <input type="checkbox" name="userIds" value="${userInfo.userid}">
                        </td>
                        <td>
                            <a href="javascript:;" onclick="userList.edit('${userInfo.userid}');"><c:out value="${userInfo.userid}"></c:out></a>
                        </td>
                        <td>
                            <a href="javascript:;" onclick="userList.edit('${userInfo.userid}');"><c:out value="${userInfo.uname}"></c:out></a>
                        </td>
                        <td><c:out value="${userInfo.dept}"></c:out></td>
                        <td><c:out value="${userInfo.grade}"></c:out></td>
                        <td><c:out value="${userInfo.email}"></c:out></td>
                        <td><c:out value="${userInfo.approve_email}"></c:out></td>
                        <c:if test="${userInfo.permission eq 'A'}">
                            <td><spring:message code="E0025" text="관리자"/></td>
                        </c:if>
                        <c:if test="${userInfo.permission eq 'U'}">
                            <td><spring:message code="E0026" text="사용자"/></td>
                        </c:if>
                        <c:if test="${userInfo.isstop eq '1'}">
                            <td class="txt red"><spring:message code="E0027" text="사용정지"/></td>
                        </c:if>
                        <c:if test="${userInfo.isstop eq '0'}">
                            <td><spring:message code="E0028" text="정상"/></td>
                        </c:if>
                            <%-- gs 인증 끝난 후 다시 주석해제 예정--%>
                      <%--  <c:if test="${userInfo.use_smtp eq '1'}">
                            <td><spring:message code="E0597" text="인증"/></td>
                        </c:if>
                        <c:if test="${userInfo.use_smtp eq '0'}">
                            <td><spring:message code="E0610" text="미인증"/></td>
                        </c:if>--%>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
        <!-- content area end -->
    </div>
    <!-- content end -->
    <div class="page_nav">
        <c:if test="${ empty userList }">
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
            <pt:jslink>userList.list</pt:jslink>
        </pt:page>
        </c:if>

        <c:if test="${ !empty userList }">
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
                <pt:jslink>userList.list</pt:jslink>
            </pt:page>
        </c:if>
    </div>

</form:form>
</div>
<!-- section end -->
</div>
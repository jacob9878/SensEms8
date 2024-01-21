<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file = "/WEB-INF/jsp/inc/taglib.jspf" %>
<script type="text/javascript" src="/sens-static/js/sysman/database.js"></script>

<div class="admindatabase">

<!-- section start -->
<!-- top area start -->
<form:form modelAttribute="databaseForm" id="databaseForm" method="get">
    <form:hidden path="cpage" value="${ pageInfo.cpage }" />
    <div class="title_box fixed">
        <div class="article top_area">
            <div class="title">
                <h1><spring:message code="E0126" text="데이터베이스 관리"/></h1>
                <p><spring:message code="E0127" text="수신자 항목을 가져올 외부 데이터베이스를 관리합니다."/></p>
            </div>
        </div>
    </div>
    <!-- top area end -->

    <!-- content top start -->
    <div class="content_top fixed">
        <ul class="content_top_btn">
            <li><a class="btn2" href="javascript:;" onclick="database.addForm();"><spring:message code="E0029" text="추가"/></a></li>
            <li><a class="btn2" href="javascript:;" onclick="database.delete();"><spring:message code="E0030" text="삭제"/></a></li>
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
                    </colgroup>
                    <thead class="fixed">
                        <tr>
                            <th class="check_ico">
                                <input id="all_check" type="checkbox" onclick="common.select_all('ukey');">
                            </th>
                            <th ><spring:message code="E0129" text="데이터베이스"/></th>
                            <th><spring:message code="E0130" text="DB유형"/></th>
                            <th><spring:message code="E0067" text="등록일"/></th>
                        </tr>
                    </thead>
                    <tbody>
                    <c:choose>
                        <c:when test="${empty dbInfoList}">
                            <tr>
                                <td colspan="4" class="txt center">
                                    <spring:message code="E0586" text="데이터가 없습니다." />
                                </td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="dbInfo" items="${ dbInfoList }">
                                <fmt:formatDate value="${dbInfo.regdate}" pattern="yyyy-MM-dd" var="regdate" />
                                <fmt:formatDate value="${dbInfo.regdate}" pattern="HH:mm" var="time" />
                                <tr>
                                    <td class="check_ico" >
                                        <input type="checkbox" name="ukey"  value="${dbInfo.ukey},${dbInfo.dbname}">
                                    </td>
                                    <td><a href="javascript:;" onclick="database.editForm('${dbInfo.ukey}');"><c:out value="${dbInfo.dbname}"></c:out></a></td>
                                    <td><a href="javascript:;" onclick="database.editForm('${dbInfo.ukey}');"><c:out value="${ dbInfo.dbtype}"></c:out></a></td>
                                    <td><c:out value="${regdate}"></c:out> <c:out value="${time}"></c:out></td>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                    </tbody>
                </table>
            </div> <!-- content area end -->
        </div>

        <!-- content end -->
        <div class="page_nav">
            <c:if test="${empty dbInfoList}">
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
                    <pt:jslink>database.list</pt:jslink>
                </pt:page>
            </c:if>
            <c:if test="${!empty dbInfoList}">
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
                    <pt:jslink>database.list</pt:jslink>
                </pt:page>
            </c:if>
        </div>
    </div>
</form:form>
<!-- section end -->

</div>


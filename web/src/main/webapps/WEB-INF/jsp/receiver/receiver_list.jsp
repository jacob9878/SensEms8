<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file = "/WEB-INF/jsp/inc/taglib.jspf" %>
<script type="text/javascript" src="${staticURL}/sens-static/js/receiver/receiver.js"></script>
<script type="text/javascript" src="${staticURL}/sens-static/js/common/receiversearch.js"></script>
<c:if test="${ !empty infoMessage }">
    <script>
        alert( '${infoMessage}' );
    </script>
</c:if>
<div class="receivergroupmanager">
<!-- section start -->
<!-- top area start -->
<input type="hidden" name="srch_key" id="srch_key" value="${srch_key}">
<input type="hidden" name="srch_type" id="srch_type" value="${srch_type}">
<form:form name="ReceiverGroupListForm" modelAttribute="ReceiverGroupListForm" method="get" action="list.do">
    <form:hidden path="cpage" value="${ pageInfo.cpage }" />
<div class="title_box fixed">
    <div class="article top_area">
        <div class="title">
            <h1><spring:message code="E0169" text="수신그룹 관리"/></h1>
            <p><spring:message code="E0170" text="발송하는 메일에 수신그룹을 관리할 수 있습니다."/></p>
        </div>
        <div class="search_box">
            <div class="inner">
                <div class="select_box">
                    <c:choose>
                        <c:when test="${permission eq 'A'}">
                         <form:select path="srch_type" cssClass="search_opt">
                            <form:option value="recv_name"><spring:message code="E0171" text="수신그룹명"/></form:option>
                            <form:option value="userid"><spring:message code="E0066" text="작성자"/></form:option>
                         </form:select>
                        </c:when>
                        <c:otherwise>
                            <form:select path="srch_type" cssClass="search_opt">
                                <form:option value="recv_name"><spring:message code="E0171" text="수신그룹명"/></form:option>
                            </form:select>
                        </c:otherwise>
                    </c:choose>
                </div>
                <form:input path="srch_keyword" name="srch_keyword" onfocus="receiversearch.init(this)" placeholder="검색어를 입력해주세요."/>
                <button type="button" class="btn1" onclick="receiverList.search()">검색</button>
                <c:if test="${issearch}"><button type="button" id="allview" class="btn2" onclick="receiverList.viewAll();">전체목록</button></c:if>
            </div>
        </div>
    </div>
</div>
<!-- top area end -->


<!-- content top start -->
<div class="content_top fixed">
    <ul class="content_top_btn">
        <li><a class="btn2" href="javascript:;" onclick="receiverList.add();"><spring:message code="E0029" text="추가"/></a></li>
        <li><a class="btn2" href="javascript:;" onclick="receiverDel.delete();"><spring:message code="E0030" text="삭제"/></a></li>
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
                    <col style="width:150px" />
                    <col style="width:150px" />
                </colgroup>
                <thead class="fixed">
                <tr >
                    <th class="check_ico">
                        <input id="all_check" type="checkbox" onclick="common.select_all('ukeys');">
                    </th>
                    <th ><spring:message code="E0171" text="수신그룹 명"/></th>
                    <th><spring:message code="E0066" text="작성자"/></th>
                    <th><spring:message code="E0067" text="등록일"/></th>
                    <th class="txt center"><spring:message code="E0104" text="미리보기"/></th>
                </tr>
                </thead>
                <tbody>
                <c:if test="${ empty receiverGroupList }">
                    <td colspan="5" class="txt center"><spring:message code="E0586" text="데이터가 없습니다."/></td>
                </c:if>
                <c:forEach var="receiverGroup" items="${receiverGroupList}">
                    <fmt:formatDate value="${receiverGroup.regdate}" pattern="yyyy-MM-dd" var="regdate" />
                    <fmt:formatDate value="${receiverGroup.regdate}" pattern="HH:mm" var="time" />
                    <tr>
                        <td class="check_ico" >
                            <input type="checkbox" name="ukeys" value="${receiverGroup.ukey},${receiverGroup.recv_name}">
                        </td>
                        <td>
                            <a href="javascript:;" onclick="receiverList.edit('${receiverGroup.ukey}','${srch_key}','${srch_type}');"><c:out value="${receiverGroup.recv_name}"></c:out></a>
                        </td>
                        <td>
                            <c:out value="${receiverGroup.userid}"></c:out>
                        </td>
                        <td><c:out value="${regdate}"></c:out> <c:out value="${time}"></c:out></td>
                        <td class="txt center"><button type="button" class="btn3" onclick="receiverList.previewGroup('${receiverGroup.ukey}')"><spring:message code="E0104" text="미리보기"/></button></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
        <!-- content area end -->
    </div>
    <!-- content end -->
    <div class="page_nav">
        <c:if test="${ empty receiverGroupList }">
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
                <pt:jslink>receiverList.list</pt:jslink>
            </pt:page>
        </c:if>
        <c:if test="${ !empty receiverGroupList }">
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
            <pt:jslink>receiverList.list</pt:jslink>
        </pt:page>
        </c:if>
    </div>

</form:form>
</div>
<!-- section end -->
</div>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file = "../inc/common.jsp" %>
<%@ include file = "/WEB-INF/jsp/inc/taglib.jspf" %>
<script type="text/javascript" src="${staticURL}/sens-static/js/sysman/user.js"></script>
<div class="statisticsError w_content">
<div class="popup_title over_text">
    <div>
        <h3 class="title">수신그룹 목록</h3>
    </div>
</div>
<div>
    <form:form method="post" id="ReceiverGroupListForm" modelAttribute="ReceiverGroupListForm" action="groupDetail.do">
        <form:hidden path="recid" id="recid" value="${recid}"/>

        <!-- content start -->
        <div class="content_top fixed">
        <ul class="content_top_btn">
            <li class="mg_t10">총 데이터수 : <span>${totalCount} (데이터는 최대 100개까지만 조회됩니다.)</span></li>
        </ul>
        </div>
        <div class="section add_btn">
        <div class="article content">
            <c:if test="${ErrorMessage != null}">
                <p class="strong txt">${ErrorMessage}</p>
            </c:if>
            <div class="content_area ">
                <table width ="100%" height="auto">
                    <colgroup>
                        <c:forEach var="field" items="${fieldInfoList}">
                            <col style="width:200px" />
                        </c:forEach>
                    </colgroup>
                    <tbody>
                    <tr>
                        <c:forEach var="field" items="${fieldInfoList}">
                            <th>${field}</th>
                        </c:forEach>
                    </tr>
                    <c:forEach var="result" items="${resultList}">
                        <tr >
                            <c:forEach var="value" items="${result}">
                                <td class="over_text">${value}</td>
                            </c:forEach>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    </form:form>
        </div>
</div>
</div>
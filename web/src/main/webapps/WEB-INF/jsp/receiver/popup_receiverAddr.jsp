<%--
  Created by IntelliJ IDEA.
  User: 홍준기
  Date: 2022-06-02
  Time: 오전 11:42
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file = "../inc/common.jsp" %>
<%@ include file = "/WEB-INF/jsp/inc/taglib.jspf" %>
<script type="text/javascript" src="${staticURL}/sens-static/js/reserve/reserve.js"></script>
<div class="statisticsError w_content">
<div class="popup_title over_text">
    <div>
        <h3 class="title"><spring:message code="E0640" text="주소록 목록"/></h3>
    </div>
</div>
<div>
    <form:form method="post" id="ReceiverAddrListForm" modelAttribute="ReceiverAddrListForm" action="addrDetail.do">
        <form:hidden path="cpage" id="cpage" value="${ pageInfo.cpage }" />
        <form:hidden path="msgid" id="msgid" value="${msgid}"/>
        <!-- content start -->
        <div class="content_top fixed">
        <ul class="content_top_btn">
            <li class="mg_t10"><spring:message code="E0641" text="총 데이터수 :"/> <span>${totalsize}</span></li>
        </ul>
        </div>
        <div class="section add_btn">
        <div class="article content">
            <div class="content_area ">
                <table width="100%" height="auto">
                    <colgroup>
                        <col style="width:auto;"/>
                        <col style="width:200px;"/>
                        <col style="width:200px;"/>
                        <col style="width:150px;"/>
                    </colgroup>
                    <thead>
                    <th>field1</th>
                    <th>field2</th>
                    <th>field3</th>
                    <th>field4</th>
                    </thead>
                    <c:choose>
                        <c:when test="${empty recvMessageIDBean}">
                            <tr>
                                <td colspan="4" class="txt center">
                                    <spring:message code="E0586" text="데이터가 없습니다." />
                                </td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="recvList" items="${recvMessageIDBean}">
                                <tr>
                                    <td>${recvList.field1}</td>
                                    <td>${recvList.field2}</td>
                                    <td>${recvList.field3}</td>
                                    <td>${recvList.field4}</td>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </table>
            </div>
        </div>
        <div class="page_nav">
            <pt:page>
                <pt:cpage>
                    ${pageInfo.cpage }
                </pt:cpage>
                <pt:pageSize>
                    ${ pageInfo.pageSize }
                </pt:pageSize>
                <pt:total>
                    ${ pageInfo.total }
                </pt:total>
                <pt:jslink>reserveList.list2</pt:jslink>
            </pt:page>
        </div>
    </form:form>
        </div>
</div>
</div>
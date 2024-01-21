<%--
  Created by IntelliJ IDEA.
  User: moonc
  Date: 2021-02-25
  Time: 오후 4:05
  To change this template use File | Settings | File Templates.
--%>
<%@ include file = "../inc/common.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file = "/WEB-INF/jsp/inc/taglib.jspf" %>
<script type="text/javascript" src="${staticURL}/sens-static/js/receiver/receiver.js"></script>
<!-- popup start-->
<form:form method="get" name="ReceiverGroupListForm" modelAttribute="ReceiverGroupListForm" action="popupReceiver.do">
    <form:hidden path="cpage" value="${ pageInfo.cpage }" />
<body class="skin1">
    <div class="w_pop">
        <div class="w_content">
        <div class="popup_title over_text">
            <div>
                <a href="javascript:;" onclick="receiverEvent.close();"><span class="close_button" ></span></a>
                <h3 class="title"><spring:message code="E0285" text="수신그룹 목록"/></h3>
            </div>
        </div>
        <div class="section">
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
                    </colgroup>
                    <thead class="fixed">
                    <tr>
                        <th>
                            <spring:message code="E0175" text="선택"/>
                        </th>
                        <th><spring:message code="E0171" text="수신그룹명"/></th>
                        <th class="txt center"><spring:message code="E0066" text="작성자"/></th>
                        <th class="txt center"><spring:message code="E0067" text="등록일"/></th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:if test="${ empty receiverGroupList }">
                        <td colspan="4" class="txt center"><spring:message code="E0586" text="데이터가 없습니다."/></td>
                    </c:if>
                    <c:forEach var="receiverGroup" items="${receiverGroupList}">
                        <fmt:formatDate value="${receiverGroup.regdate}" pattern="yyyy-MM-dd" var="regdate" />
                        <fmt:formatDate value="${receiverGroup.regdate}" pattern="HH:mm" var="time" />
                        <tr>
                            <td>
                                <input type="radio" name="ukey" value="${receiverGroup.ukey}" gname_value="${receiverGroup.recv_name}">
                            </td>
                            <td class="over_text">
                                <a href="javascript:;" onclick="receiverEvent.receiverGroupSel('${receiverGroup.ukey}', '${receiverGroup.recv_name}','${opener_type}');">${receiverGroup.recv_name}</a>
                            </td>
                            <td class="txt center">
                                <a href="javascript:;">${receiverGroup.userid}</a>
                            </td>
                            <td class="list_2line txt center">${regdate} ${time}</td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
                <%--<div class="btn_div">
                    <button class="btn2" onclick="receiverEvent.preview();"><spring:message code="E0104" text="미리보기"/></button>
                    <button class="btn2" onclick="receiverEvent.receiverGroupConfirm('${opener_type}');"><spring:message code="E0064" text="확인"/></button>
                </div>--%>
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
                        1e
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
        </div>
        <div class="close_area">
            <button class="close_btn" onclick="receiverEvent.preview();"><spring:message code="E0104" text="미리보기"/></button>
            <button class="close_btn" onclick="receiverEvent.receiverGroupConfirm('${opener_type}');"><spring:message code="E0064" text="확인"/></button>
        </div>
    </div>
</form:form>
<!-- popup end-->
</div>
</body>
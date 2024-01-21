<%@ include file = "../inc/common.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!-- popup start-->

<script type="text/javascript" src="${staticURL}/sens-static/js/messages/mail_${UserInfo.language}.js"></script>
<script type="text/javascript" src="${staticURL}/sens-static/js/receiver/receiver.js"></script>
<body class="skin1">
<div class="w_pop">
    <div class="w_content">
    <div class="popup_title over_text">
        <div>
            <a href="javascript:;" onclick="receiverEvent.close();"><span class="close_button" ></span></a>
            <h3 class="title"><spring:message code="E0343" text="주소록 그룹"/></h3>
        </div>
    </div>
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
                        <th>
                            <input type="checkbox" id="all_check" onclick="common.select_all('gkeys')" title="전체선택">
                        </th>
                        <th><spring:message code="E0319" text="주소록 그룹명"/></th>
                        <th class="txt center"><spring:message code="E0386" text="인원"/></th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:if test="${ empty groupList }">
                        <td colspan="3"><spring:message code="E0317" text="등록된 개인주소록이 없습니다."/></td>
                    </c:if>
                    <c:forEach var="group" items="${groupList}">
                        <tr>
                            <td>
                                <input type="checkbox" name="gkeys" value="${group.gkey}" gname_value="${group.gname}">
                            </td>
                            <td class="over_text">${group.gname}</td>
                            <td class="txt center">${group.count}</td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        <!-- content area end -->
        </div>
        <!-- content end -->
    </div>
        <div class="close_area">
            <button class="close_btn" onclick="receiverEvent.selAddrGroupList();"><spring:message code="E0064" text="확인"/></button>
        </div>
    </div>
</div>
</body>
<!-- popup end-->

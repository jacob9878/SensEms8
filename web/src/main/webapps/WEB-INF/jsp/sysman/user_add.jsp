<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file = "/WEB-INF/jsp/inc/taglib.jspf" %>
<%@ include file="../common/encrypt.jsp" %>
<script type="text/javascript" src="/sens-static/js/sysman/user.js"></script>
<c:if test="${ !empty infoMessage }">
    <script>
        alert( '${infoMessage}' );
    </script>
</c:if>
<div class="adminuser_add">
    <input type="hidden" id="cpage" value="${cpage}">

    <!-- section start -->
<!-- top area start -->
<div class="title_box fixed">
    <div class="article top_area">
        <div class="title">
            <h1><spring:message code="E0016" text="사용자 관리"/></h1>
            <p><spring:message code="E0032" text="사용자 정보 추가"/></p>
        </div>
    </div>
</div>
<!-- top area end -->

<!-- content top start -->
<div class="content_top fixed">
    <ul class="content_top_btn">
        <li><a class="btn1" href="javascript:;" onclick="userAdd.add()"><spring:message code="E0029" text="추가"/></a></li>
        <li><a class="btn2" href="javascript:;" onclick="userAdd.list()"><spring:message code="E0047" text="목록"/></a></li>
    </ul>
</div>
<!-- content top end -->


<form:form method="post" name="UserForm" modelAttribute="UserForm" action="add.do">
    <form:hidden path="isCheck"/>
    <form:hidden path="encAESKey"/>
    <div class="section pd_l30">
    <!-- content start -->

    <div class="article content">

        <!-- composer area start -->
        <div class="composer_area">
            <table width="100%" border="0" cellpadding="0" cellspacing="1" >
                <colgroup>
                    <col style="width: 150px">
                    <col style="width: auto">
                </colgroup>
                <tbody>
                <tr>
                    <th><spring:message code="E0001" text="아이디"/><span><strong> *</strong></span></th>
                    <td>
                        <form:input path="userid" maxlength="15" cssClass="input" onkeypress="isCheck.value='';" onblur="isCheck.value='';"/>
                        <button type="button" class="btn3" onclick="userAdd.idCheck()"><spring:message code="E0033" text="중복확인"/></button>
                    </td>
                </tr>
                <tr>
                    <th><spring:message code="E0002" text="비밀번호"/><span><strong> *</strong></span></th>
                    <td>
                        <form:password path="passwd" maxlength="20" cssClass="input" autocomplete="false"/>
                        <span class="pw-info">
                            <ul style="padding-top: 5px;">
                                <c:forEach var="required" items="${passwordRequired}">
                                    <li <c:if test="${required.error}">style="color:#fc4c50;font-weight: bold;"</c:if>>${required.message} </li>
                                </c:forEach>
                            </ul>
                        </span>
                    </td>
                </tr>
                <tr>
                    <th><spring:message code="E0005" text="비밀번호 확인"/><span><strong> *</strong></span></th>
                    <td>
                        <form:password path="passwd_confirm" maxlength="20" cssClass="input" autocomplete="false"/>
                    </td>
                </tr>
                <tr>
                    <th><spring:message code="E0018" text="이름"/><span><strong> *</strong></span></th>
                    <td>
                        <form:input path="uname" maxlength="20" cssClass="input"/>
                    </td>
                </tr>
                <tr>
                    <th><spring:message code="E0022" text="E-Mail"/><span><strong> *</strong></span></th>
                    <td>
                        <form:input path="email" maxlength="50" cssClass="input"/>
                    </td>
                </tr>
                <%--<tr>
                    <th><spring:message code="E0051" text="승인메일 주소" /></th>
                    <td>
                        <form:input path="approve_email" maxlength="50" cssClass="input"/>
                    </td>
                </tr>--%>
                <tr>
                    <th><spring:message code="E0020" text="소속"/></th>
                    <td>
                        <form:input path="dept" maxlength="50" cssClass="input"/>
                    </td>
                </tr>
                <tr>
                    <th><spring:message code="E0021" text="직책"/></th>
                    <td>
                        <form:input path="grade" maxlength="50" cssClass="input"/>
                    </td>
                </tr>

                <tr>
                    <th><spring:message code="E0035" text="전화번호"/></th>
                    <td>
                        <form:input path="tel" maxlength="50" cssClass="input"/>
                    </td>
                </tr>
                <tr>
                    <th><spring:message code="E0037" text="휴대폰번호"/></th>
                    <td>
                        <form:input path="mobile" maxlength="50" cssClass="input"/>
                    </td>
                </tr>
                <tr>
                    <th><spring:message code="E0023" text="권한"/></th>
                    <td>
                        <form:select path="permission">
                            <form:option value="A"><spring:message code="E0025" text="관리자"/></form:option>
                            <form:option value="U"><spring:message code="E0026" text="사용자"/></form:option>
                        </form:select>
                        <span class="pw-info">
                            <ul style="padding-top: 5px;">
                                <li><spring:message code="E0046" text="','를 이용하여 여러 IP를 등록할 수 있습니다."/></li>

                            </ul>
                        </span>
<%--                        <span class="ucare_txt">--%>
<%--                            <li class="help_list"><spring:message code="E0046" text="관리자는 모든 메일/수신그룹/발송관리 항목 등을 관리 할 수 있습니다."/></li>--%>
<%--                        </span>--%>
                    </td>
                </tr>
                    <%-- gs 인증 끝난 후 다시 주석해제 예정--%>
                <%--<tr>
                    <th><spring:message code="E0730" text="SMTP 인증 권한"/></th>
                    <td>--%>
                        <input type="hidden" name="use_smtp" id="use_smtp" value="0"><%--<spring:message code="E0731" text="권한있음"/>--%>
                        <input type="hidden" name="use_smtp" id="use_smtp" value="1"><%--<spring:message code="E0732" text="권한없음"/>
                    &lt;%&ndash;</td>
                </tr>--%>
                <tr>
                    <th><spring:message code="E0036" text="접근 허용 IP"/></th>
                    <td>
                        <form:input path="access_ip"  maxlength="1000" cssClass="input"/>
                        <span class="pw-info">
                            <ul style="padding-top: 5px;">
                                <li><spring:message code="E0735" text="쉼표(,)를 이용하여 여러개의 IP를 등록할 수 있습니다."/></li>
                                <li><spring:message code="E0736" text="접근허용 IP를 지정하지 않을 경우, 모든 IP의 접근을 허용합니다."/></li>
                            </ul>
                        </span>

<%--                        <span class="care_txt">--%>
<%--                            <strong><spring:message code="E0046" text="','를 이용하여 여러 IP를 등록할 수 있습니다."/></strong>--%>
<%--                        </span>--%>
<%--                        <span class="care_txt">--%>
<%--                            <strong><spring:message code="E0046" text="접근허용 IP를 지정하지 않을 시 모든 IP의 접근을 허용합니다."/></strong>--%>
<%--                        </span>--%>
                    </td>
                </tr>
                </tbody>
            </table>
            <%--<div class="pw-info">
                <h5><spring:message code="E0539" text="비밀번호 안내"/></h5>
                <ul>
                    <c:forEach var="required" items="${passwordRequired}">
                        <li <c:if test="${required.error}">style="color:#fc4c50;font-weight: bold;"</c:if>>${required.message} </li>
                    </c:forEach>
                </ul>
            </div>--%>
        </div>
        </div>
        <!-- composer area end -->

    <!-- content end -->

</div>
<!-- section end -->
</form:form>

</div>
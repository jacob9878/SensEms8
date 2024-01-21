<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file = "/WEB-INF/jsp/inc/taglib.jspf" %>
<%@ include file="../common/encrypt.jsp" %>
<script type="text/javascript" src="/sens-static/js/sysman/user.js"></script>
<c:if test="${ !empty infoMessage }">
    <script>
        alert( '${infoMessage}' );
    </script>
</c:if>
<div class="adminuser_edit">
    <input type="hidden" id="cpage" value="${cpage}">
    <input type="hidden" id="srch_keyword" value="${srch_keyword}">
    <input type="hidden" id="srch_type" value="${srch_type}">

    <!-- section start -->
<!-- top area start -->
<div class="title_box fixed">
    <div class="article top_area">
        <div class="title">
            <h1><spring:message code="E0016" text="사용자 관리"/></h1>
            <p><spring:message code="E0055" text="사용자 정보 수정"/></p>
        </div>
    </div>
</div>
<!-- top area end -->

<!-- content top start -->
<div class="content_top fixed">
    <ul class="content_top_btn">
        <li><a class="btn1" href="javascript:;" onclick="userEdit.edit();"><spring:message code="E0069" text="저장"/></a></li>
        <li><a class="btn2" href="javascript:;" onclick="userEdit.list()"><spring:message code="E0065" text="취소"/></a></li>
    </ul>
</div>
<!-- content top end -->

<div class="section pd_l30">
    <form:form method="post" name="UserForm" modelAttribute="UserForm" action="edit.do">
        <form:hidden path="userid"/>
        <form:hidden path="encAESKey"/>
        <form:hidden path="ori_email" value="${UserForm.email}"/>
        <form:hidden path="ori_name" value="${UserForm.uname}"/>

        <!-- content start -->

    <div class="article content">

        <!-- composer area start -->
        <div class="composer_area">
            <table width="100%" border="0" cellpadding="0" cellspacing="1" >
                <colgroup>
                    <col style="width:150px;">
                    <col style="width:auto;">
                </colgroup>
                <tbody>
                <tr>
                    <th><spring:message code="E0001" text="아이디"/></th>
                    <td>${UserForm.userid}</td>
                </tr>
                <tr>
                    <th><spring:message code="E0002" text="비밀번호"/></th>
                    <td ><button type="button" class="btn3" onclick="userEdit.changePwdPopup()"><spring:message code="E0006" text="비밀번호 변경"/></button></td>
                </tr>
                <tr>
                    <th><spring:message code="E0018" text="이름"/><span><strong> *</strong></span></th>
                    <td><form:input path="uname" maxlength="20" /></td>
                </tr>
                <tr>
                    <th><spring:message code="E0022" text="E-Mail"/><span><strong> *</strong></span></th>
                    <td><form:input path="email" maxlength="50" /></td>
                </tr>
                <%--<tr>
                    <th><spring:message code="E0051" text="승인메일 주소"/></th>
                    <td><form:input path="approve_email" maxlength="50" /></td>
                </tr>--%>
                <tr>
                    <th><spring:message code="E0020" text="소속"/></th>
                    <td><form:input path="dept" maxlength="50" /></td>
                </tr>
                <tr>
                    <th><spring:message code="E0021" text="직책"/></th>
                    <td><form:input path="grade" maxlength="50" /></td>
                </tr>
                <tr>
                    <th><spring:message code="E0035" text="전화번호"/></th>
                    <td><form:input path="tel" maxlength="50" /></td>
                </tr>
                <tr>
                    <th><spring:message code="E0037" text="휴대폰번호"/></th>
                    <td><form:input path="mobile" maxlength="50"/></td>
                </tr>
                <tr>
                    <th><spring:message code="E0023" text="권한"/></th>
                    <td>
                        <form:select path="permission">
                            <form:option value="A"><spring:message code="E0025" text="관리자"/></form:option>
                            <form:option value="U"><spring:message code="E0026" text="사용자"/></form:option>
                        </form:select>
                        <span class="care_txt">
                            <strong><spring:message code="E0046" text="','를 이용하여 여러 IP를 등록할 수 있습니다."/></strong>
                        </span>
                    </td>
                </tr>
                    <%-- gs 인증 끝난 후 다시 주석해제 예정--%>
                <%--<tr>
                    <th><spring:message code="E0730" text="SMTP 인증 권한"/></th>
                    <td>--%>
                        <input type="hidden" name="use_smtp" id="use_smtp" value="0" <c:if test="${UserForm.use_smtp == '0'}"/> <%--checked</c:if>/><spring:message code="E0731" text="권한없음"/>--%>
                        <input type="hidden" name="use_smtp" id="use_smtp" value="1" <c:if test="${UserForm.use_smtp == '1'}"/> <%--checked</c:if>/><spring:message code="E0732" text="권한있음"/>--%>
                    <%--</td>
                </tr>--%>
                <tr>
                    <th><spring:message code="E0036" text="접근 허용 IP"/></th>
                    <td>
                        <form:input path="access_ip"  maxlength="1000"/>
                        <span class="care_txt">
                            <strong><spring:message code="E0046" text="','를 이용하여 여러 IP를 등록할 수 있습니다."/></strong>
                        </span>
                        <span class="care_txt">
                            <strong><spring:message code="E0736" text="접근허용 IP를 지정하지 않을 시 모든 IP의 접근을 허용합니다."/></strong>
                        </span>
                    </td>
                </tr>
                <tr>
                    <th><spring:message code="E0024" text="상태"/></th>
                    <td>
                        <form:select path="isstop">
                            <form:option value="0"><spring:message code="E0028" text="정상"/></form:option>
                            <form:option value="1"><spring:message code="E0027" text="사용정지"/></form:option>
                        </form:select>
                    </td>
                </tr>
                </tbody>
        </div>
        <!-- composer area end -->
    </div>
    <!-- content end -->
    </form:form>

    </table>
    <!-- popup start-->

    <!-- popup end-->
</div>
    <!-- section end -->
</div>

<div class="popup" id="changePwdPopup" style="display: none">
    <div class="popup_content" style="height: 420px">
        <div class="popup_title">
            <span class="close_button" onclick="userEdit.close()" ></span>
            <h3 class="title"><spring:message code="E0006" text="비밀번호 변경"/></h3>
        </div>
        <div class="popup_body_wrap">
            <div class="popup_body">
                <form>
                    <!-- <p> 설명 텍스트 </p> -->
                    <table width="100%" border="0" cellpadding="0" cellspacing="1" >
                        <colgroup>
                            <col style="width: 150px;">
                            <col style="width: auto;">
                        </colgroup>
                        <tbody>
                        <tr>
                            <th><spring:message code="E0002" text="비밀번호"/></th>
                            <td>
                                <input type="password" name="passwd" id="passwd" maxlength="20" autocomplete="false"/>
                            </td>
                        </tr>
                        <tr>
                            <th><spring:message code="E0005" text="비밀번호 확인"/></th>
                            <td>
                                <input type="password" name="passwd_confirm" id="passwd_confirm"maxlength="20" autocomplete="false"/>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="2">
                                <div class="pw-info">
                                    <h5><spring:message code="E0539" text="비밀번호 안내"/></h5>
                                    <ul>
                                        <c:forEach var="required" items="${passwordRequired}">
                                            <li <c:if test="${required.error}">style="color:#fc4c50;font-weight: bold;"</c:if>>${required.message} </li>
                                        </c:forEach>
                                    </ul>
                                </div>
                            </td>
                        </tr>
                        </tbody>
                    </table>
                </form>
            </div>

        </div>
        <div class="pop_footer">
            <div class="btn_div">
                <button type="button" class="btn4" onclick="userEdit.changePassword()"><spring:message code="E0069" text="저장"/></button>
                <button type="button" class="btn4" onclick="userEdit.close()"><spring:message code="E0065" text="취소"/></button>
            </div>
        </div>
    </div>
</div>
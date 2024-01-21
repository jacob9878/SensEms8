<%@ include file = "../inc/common.jsp" %>
<%@ include file="../common/encrypt.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<script type="text/javascript" src="/sens-static/js/account/myinfo.js"></script>
<script type="text/javascript" src="/sens-static/js/messages/sysman_${UserInfo.language}.js"></script>


<%--<c:if test="${ !empty infoMessage }">--%>
<%--    <script>--%>
<%--        alert( '${infoMessage}' );--%>
<%--    </script>--%>
<%--</c:if>--%>
<!-- popup start-->
<div class="popup_title over_text">
    <div>
        <h3 class="title"><spring:message code="E0055" text="사용자 정보 수정"/></h3>
    </div>
</div>

<!-- content top start -->

<!-- content top end -->

<div class="section ">

    <!-- content start -->
    <div class="article content">
        <!-- content area start -->
        <div class="composer_area" >
            <form:form  name="UserForm" modelAttribute="UserForm" action="infoedit.do" method="post" >
            <form:hidden path="userid"/>

                <table  width ="100%" height="auto" >
                    <colgroup>
                        <col style="width:150px" />
                        <col style="width:auto" />
                    </colgroup>
                    <tbody>
                    <tr>
                        <th><spring:message code="E0001" text="아이디"/></th>
                        <td>${UserForm.userid}</td>
                    </tr>
                    <tr>
                        <th ><spring:message code="E0002" text="비밀번호"/><span class="txt red"><strong> *</strong></span></th>
                        <td ><button type="button" class="btn3" onclick="myinfo.changePwdPopup()"><spring:message code="E0006" text="비밀번호 변경"/></button></td>
                    </tr>
                    <tr>
                        <th><spring:message code="E0018" text="이름"/><span class="txt red"><strong> *</strong></span></th>
                        <td><form:input path="uname" maxlength="20" /></td>

                    </tr>
                    <tr>
                        <th ><spring:message code="E0020" text="소속"/></th>
                        <td><form:input path="dept" maxlength="20" /></td>
                    </tr>
                    <tr>
                        <th ><spring:message code="E0021" text="직책"/></th>
                        <td><form:input path="grade" maxlength="20" /></td>
                    </tr>
                    <tr>
                        <th ><spring:message code="E0022" text="E-Mail"/><span class="txt red"><strong> *</strong></span></th>
                        <td><form:input path="email" maxlength="30" /></td>
                    </tr>
                    <tr>
                        <th ><spring:message code="E0035" text="전화번호"/></th>
                        <td><form:input path="tel" maxlength="20" /></td>
                    </tr>
                    </tbody>
                </table>


        </div>
        <!-- content area end -->
    </div>
    </form:form>
    <!-- content end -->
</div>
<div class="pop_footer">
    <div class="btn_div ">
        <button class="btn1" onclick="myinfo.edit();"><spring:message code="E0069" text="저장"/></button>
        <%--<button class="close_btn" onclick="window.close();"><spring:message code="E0282" text="닫기"/></button>--%>
    </div>
</div>

<div class="popup" id="changePwdPopup" style="display: none">
    <div class="popup_content" style="height: 420px">
        <div class="popup_title">
            <span class="close_button" onclick="myinfo.close()" ></span>
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
                        <input type="hidden" id="encAESKey"/>
                        <input type="hidden" id="userid"/>
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
            <div class="pop_footer">
                <div class="btn_div">
                    <button type="button" class="btn4" onclick="myinfo.changePassword()"><spring:message code="E0069" text="저장"/></button>
                    <button type="button" class="btn4" onclick="myinfo.close()"><spring:message code="E0065" text="취소"/></button>
                </div>
            </div>
        </div>
    </div>
</div>
<!-- popup end-->



<%--<div class="close_area">--%>
<%--    <button class="close_btn" onclick="window.close();"><spring:message code="E0282" text="닫기"/></button>--%>
<%--</div>--%>

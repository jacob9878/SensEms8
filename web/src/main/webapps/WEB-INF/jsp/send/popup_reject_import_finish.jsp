<%@ include file = "../inc/common.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<script type="text/javascript" src="${staticURL}/sens-static/js/send/reject.js"></script>
<script>
    $(document).ready(function(){
        window.resizeTo( 900 , 650 );
        opener.location.reload();
    });
</script>
<c:if test="${ !empty infoMessage }">
    <script>
        alert( '${infoMessage}' );
    </script>
</c:if>
<!-- popup start-->
<div class="popup_title over_text">
    <div>
        <h3 class="title"><spring:message code="E0436" text="주소록 가져오기 결과"/></h3>
    </div>
</div>
<div class="section">
    <!-- content start -->
    <div class="article content">
        <!-- content area start -->
        <div class="composer_area" >
                   <table  width ="100%" height="auto" >
                    <colgroup>
                        <%--<col style="width:220px" />--%>
                        <col style="width:auto" />
                    </colgroup>
                    <tbody>
                        <c:if test="${!empty errorMessage}">
                            <tr>
                                <td colspan="2">
                                    ${errorMessage}
                                </td>
                            </tr>
                        </c:if>
                        <c:if test="${empty errorMessage}">
                            <tr>
                                <th scope="row">
                                    <c:choose>
                                        <c:when test="${ nRowCount < nSuccess }">
                                            <c:set var="nResult" value="0" />
                                        </c:when>
                                        <c:otherwise>
                                            <c:set var="nResult" value="${nRowCount - nSuccess}" />
                                        </c:otherwise>
                                    </c:choose>
                                    <c:if test="${nResult != 0}">
                                        <spring:message code ="E0713" arguments="${nRowCount},${nResult}" text='{0} 개의 열을 넣는데 {1} 개의 에러가 발생 했습니다.'/> <br />
                                    </c:if>
                                    <c:if test="${nSuccess != 0}">
                                        <spring:message code="E0714" text='총'/> ${nSuccess}<spring:message code="A0112" text=' 개의 주소를 임포트했습니다.'/>
                                    </c:if>
                                </th>
                            </tr>
                            <c:if test="${nColumnCountNotMatch != 0}">
                            <tr>
                                <th>
                                    <spring:message code="E0446" text="필드 개수 불일치"/> : ${nColumnCountNotMatch}
                                </th>
                            </tr>
                            </c:if>
                            <%--<tr>
                                <th>
                                    <spring:message code="E0437" text="시도 횟수"/>
                                </th>
                                <td>
                                        ${nRowCount}
                                </td>
                            </tr>--%>
                            <%--<tr>
                                <th>
                                    <spring:message code="E0438" text="성공 횟수"/>
                                </th>
                                <td>
                                        ${nSuccess}
                                </td>
                            </tr>--%>
                            <c:if test="${!empty nBlankList}">
                            <tr>
                                <th>
                                    <spring:message code="E0439" text="이름 누락 오류 횟수"/>: ${nBlankCount}
                                </th>
                            </tr>
                                <c:forEach var="addr" items="${nBlankList}">
                                    <tr>
                                        <td>${addr.name} / ${addr.email}</td>
                                    </tr>
                                </c:forEach>
                            </c:if>
                            <c:if test="${!empty nEmailCheckList}">
                            <tr>
                                <th>
                                    <spring:message code="E0440" text="이메일 누락/형식/중복 오류 횟수"/>: ${nEmailCheckCount}
                                </th>
                            </tr>
                                <c:forEach var="addr" items="${nEmailCheckList}">
                                    <tr>
                                        <td>${addr.name} / ${addr.email}</td>
                                    </tr>
                                </c:forEach>
                            </c:if>
                            <c:if test="${!empty nNumberErrorList}">
                            <tr>
                                <th>
                                    <spring:message code="E0441" text="번호 형식 오류 횟수"/>: ${nNumberErrorCount}
                                </th>
                            </tr>
                                <c:forEach var="addr" items="${nNumberErrorList}">
                                    <tr>
                                        <td>${addr.name} / ${addr.email}</td>
                                    </tr>
                                </c:forEach>
                            </c:if>
                        </c:if>
                    </tbody>
                </table>
            <%--<div class="mg_t20 composer_area graph ">
                <div class="mini_table">
                <table width="100%" height="auto">
                </table>
                </div>
            </div>--%>
        </div>
        <!-- content area end -->
    </div>
    <!-- content end -->
</div>
<!-- popup end-->
<div class="pop_footer" >
    <div class="btn_div">
        <%--<button class="close_btn btn2" onclick="opener.location.reload();self.close();"><spring:message code="E0282" text="닫기"/></button>--%>
        <button class="close_btn btn2" onclick="self.close();"><spring:message code="E0282" text="닫기"/></button>
    </div>
</div>
<%--<div class="close_area">--%>
<%--    <button class="close_btn" onclick="opener.location.reload();self.close();"><spring:message code="E0282" text="닫기"/></button>--%>
<%--</div>--%>

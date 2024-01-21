<%@ include file = "../inc/common.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<script type="text/javascript" src="${staticURL}/sens-static/js/receiver/address.js"></script>
<script>
    $(document).ready(function(){
        window.resizeTo( 900 , 750 );
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
        <h3 class="title"><spring:message code="E0432" text="주소록 가져오기 3단계"/></h3>
    </div>
</div>

<!-- content top start -->
<%--<div class="content_top fixed">
    <ul class="content_top_btn">

    </ul>
</div>--%>
<!-- content top end -->

<div class="section ">
    <!-- content start -->
    <div class="article content">
        <!-- content area start -->
        <div class="composer_area" >
            <form:form name="importForm" modelAttribute="importForm" method="post" action="doImportFinish.do">
                <form:hidden path="fileKey" />
                <form:hidden path="header" />
                <form:hidden path="divMethod" />
                <form:hidden path="gname"/>

                <table  width ="100%" height="auto" >
                    <colgroup>
                        <col style="width:150px" />
                        <col style="width:auto" />
                    </colgroup>
                    <tbody>
                        <tr>
                            <th>
                                <spring:message code="E0319" text="주소록 그룹명"/>
                            </th>
                            <td colspan="3">
                                <form:select path="gkey" onchange="addressList.doSelect();" cssClass="w200">
                                    <option value="0"><spring:message code="E0311" text="미분류"/></option>
                                    <c:forEach var="group" items="${addrGrpList}">
                                        <option value="${group.gkey}">${group.gname}</option>
                                    </c:forEach>
                                    <option value="-1">[<spring:message code="E0433" text="새그룹"/>]</option>
                                </form:select>
                                <input type="text" id="newGrpName" maxlength="50" style="display: none"/>
                            </td>

                        </tr>
                        <tr>
                            <th>
                                <span class="txt strong red">*</span><spring:message code="E0018" text="이름"/>
                            </th>
                            <td>
                                <form:select path="name" cssClass="w200" >
                                    <option value="-1"><spring:message code="E0224" text="없음"/></option>
                                    <form:options items="${importForm.strColumn }" />
                                </form:select>
                            </td>
                        </tr>
                        <tr>
                            <th>
                                <span class="txt strong red">*</span><spring:message code="E0022" text="E-Mail"/>
                            </th>
                            <td>
                                <form:select path="email"  cssClass="w200">
                                    <option value="-1"><spring:message code="E0224" text="없음"/></option>
                                    <form:options items="${importForm.strColumn }" />
                                </form:select>
                            </td>

                        </tr>
                        <tr>
                            <th>
                                <spring:message code="E0312" text="회사"/>
                            </th>
                            <td>
                                <form:select path="company" cssClass="w200">
                                    <option value="-1"><spring:message code="E0224" text="없음"/></option>
                                    <form:options items="${importForm.strColumn }" />
                                </form:select>
                            </td>

                        </tr>
                        <tr>
                            <th>
                                <spring:message code="E0313" text="부서"/>
                            </th>
                            <td>
                                <form:select path="dept" cssClass="w200">
                                    <option value="-1"><spring:message code="E0224" text="없음"/></option>
                                    <form:options items="${importForm.strColumn }" />
                                </form:select>
                            </td>

                        </tr>
                        <tr>
                            <th>
                                <spring:message code="E0021" text="직책"/>
                            </th>
                            <td>
                                <form:select path="grade" cssClass="w200">
                                    <option value="-1"><spring:message code="E0224" text="없음"/></option>
                                    <form:options items="${importForm.strColumn }" />
                                </form:select>

                        </tr>

                        <tr>
                            <th>
                                <spring:message code="E0314" text="회사전화"/>
                            </th>
                            <td>
                                <form:select path="office_tel" cssClass="w200">
                                    <option value="-1"><spring:message code="E0224" text="없음"/></option>
                                    <form:options items="${importForm.strColumn }" />
                                </form:select>
                            </td>

                        </tr>
                        <tr>
                            <th>
                                <spring:message code="E0037" text="휴대폰번호"/>
                            </th>
                            <td>
                                <form:select path="mobile" cssClass="w200">
                                    <option value="-1"><spring:message code="E0224" text="없음"/></option>
                                    <form:options items="${importForm.strColumn }" />
                                </form:select>
                            </td>

                        </tr>
                        <tr>
                            <th>
                                <spring:message code="E0316" text="기타정보1"/>
                            </th>
                            <td>
                                <form:select path="etc1" cssClass="w200">
                                    <option value="-1"><spring:message code="E0224" text="없음"/></option>
                                    <form:options items="${importForm.strColumn }" />
                                </form:select>
                            </td>
                        </tr>
                        <tr>
                            <th>
                                <spring:message code="E0349" text="기타정보2"/>
                            </th>
                            <td>
                                <form:select path="etc2" cssClass="w200">
                                    <option value="-1"><spring:message code="E0224" text="없음"/></option>
                                    <form:options items="${importForm.strColumn }" />
                                </form:select>
                            </td>
                        </tr>

<%--                        <tr>--%>
<%--                            <td colspan="4">--%>
<%--                                --%>
<%--                            </td>--%>
<%--                        </tr>--%>
                    </tbody>
                </table>
            </form:form>
            <span class="bottom_explain"><spring:message code="E0434" text="*표시는 필수 입력 사항입니다."/></span>
        </div>
        <!-- content area end -->
    </div>
    <!-- content end -->

</div>
<!-- popup end-->
<div class="pop_footer">
    <div class="btn_div">
        <button class="btn2" href="javascript:;" onclick="addressList.doPrev();"><spring:message code="E0435" text="이전"/></button>
        <button class="btn2" href="javascript:;" onclick="addressList.doInsertAddr()"><spring:message code="E0419" text="다음"/></button>
        <%--<button class="close_btn btn2" onclick="window.close();"><spring:message code="E0282" text="닫기"/></button>--%>
    </div>
</div>

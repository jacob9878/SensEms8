<%@ include file = "../inc/common.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<script type="text/javascript" src="${staticURL}/sens-static/js/send/reject.js"></script>
<script>
    $(document).ready(function(){
        window.resizeTo( 900 , 400 );
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
        <h3 class="title"><spring:message code="E0718" text="수신거부목록 가져오기 3단계"/></h3>
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

                <table  width ="100%" height="auto" >
                    <colgroup>
                        <col style="width:150px" />
                        <col style="width:auto" />
                    </colgroup>
                    <tbody>
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
        <button class="btn2" href="javascript:;" onclick="reject_list.doPrev();"><spring:message code="E0435" text="이전"/></button>
        <button class="btn2" href="javascript:;" onclick="reject_list.doInsertAddr()"><spring:message code="E0419" text="다음"/></button>
        <%--<button class="close_btn btn2" onclick="window.close();"><spring:message code="E0282" text="닫기"/></button>--%>
    </div>
</div>

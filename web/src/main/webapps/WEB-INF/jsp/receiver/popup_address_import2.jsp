<%@ include file = "../inc/common.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<script type="text/javascript" src="${staticURL}/sens-static/js/receiver/address.js"></script>
<script>
    $(document).ready(function(){
        window.resizeTo( 900 , 700 );
        addressList.doPreview();
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
        <h3 class="title"><spring:message code="E0420" text="주소록 가져오기 2단계"/></h3>
    </div>
</div>

<!-- content top start -->
<%--<div class="content_top fixed">
    <ul class="content_top_btn">
        <li><a class="btn2" href="#" onclick="addressList.doFileSubmit()"><spring:message code="E0419" text="다음"/></a></li>
    </ul>
</div>--%>
<!-- content top end -->

<div class="section">
    <!-- content start -->
    <div class="article content">
        <!-- content area start -->
        <div class="composer_area" >
            <form:form name="importForm" modelAttribute="importForm" method="post" action="doImport3.do">
<%--            <form name="importForm" id="importForm" method="post" action="/receiver/address/doImport3.do?${_csrf.parameterName}=${_csrf.token}">--%>
                <input type="hidden" name="fileKey" value="${fileKey}"/>
                <input type="hidden" name="header" value="0"/>
                <input type="hidden" name="divMethod" value="-1"/>
                <table  width ="100%" height="auto" >
                    <colgroup>
                        <col style="width:150px" />
                        <col style="width:auto" />
                    </colgroup>
                    <tbody>
                        <tr>
                            <th>
                                <spring:message code="E0423" text="구분자"/>
                            </th>
                            <td>
                                <input type="radio" name="div" value="0" onclick="addressList.doPreview();" checked/><spring:message code="E0421" text="콤마(,)로 구분"/>
                                <input type="radio" name="div" value="1" onclick="addressList.doPreview();"/><spring:message code="E0422" text="세미콜론(;)으로 구분"/>
                            </td>
                        </tr>
                        <tr>
                            <th>
                                <spring:message code="E0424" text='첫행의 헤더 여부'/>
                            </th>
                            <td>
                                <span><input type="checkbox" name="isheader" value="true" onclick="addressList.doPreview();"/><spring:message code="E0425" text='첫행이 헤더'/></span>
                            </td>
                        </tr>
                        <tr>
                            <th>
                                <spring:message code="E0426" text="필드값"/>
                            </th>
                            <td class="graph ">
                                <span class="care_txt txt red"><spring:message code="E0427" text="※ 미리보기 화면은 실제 파일 내용의 10열까지만 보여줍니다."/></span>
                                <div id="frmPreview"></div>
                            </td>
                        </tr>
                    </tbody>
                </table>
<%--            </form>--%>
            </form:form>

        </div>
        <!-- content area end -->
    </div>
    <!-- content end -->

</div>
<!-- popup end-->
<div class="pop_footer">
    <div class="btn_div">
        <button class="btn2" href="javascript:;" onclick="addressList.doFileSubmit()"><spring:message code="E0419" text="다음"/></button>
        <%--<button class="close_btn btn2" onclick="window.close();"><spring:message code="E0282" text="닫기"/></button>--%>
    </div>
</div>
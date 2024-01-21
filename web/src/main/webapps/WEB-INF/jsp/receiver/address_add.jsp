<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file = "/WEB-INF/jsp/inc/taglib.jspf" %>
<script type="text/javascript" src="${staticURL}/sens-static/js/receiver/address.js"></script>
<c:if test="${ !empty infoMessage }">
    <script>
        alert( '${infoMessage}' );
    </script>
</c:if>
<div class="address_add">
    <input type="hidden" id="cpage" value="${cpage}">

    <!-- section start -->
<!-- top area start -->
<div class="title_box fixed">
    <div class="article top_area">
        <div class="title">
            <h1><spring:message code="E0306" text="개인주소록 관리"/></h1>
            <p><spring:message code="E0342" text="개인주소록 정보 추가"/></p>
        </div>
    </div>
</div>
<!-- top area end -->

<!-- content top start -->
<div class="content_top fixed">
    <ul class="content_top_btn">
        <li><a class="btn1" href="javascript:;" onclick="addressList.saveAddr()"><spring:message code="E0029" text="추가"/></a></li>
        <li><a class="btn2" href="javascript:;" onclick="addressList.addressList()"><spring:message code="E0047" text="목록"/></a></li>
    </ul>
</div>
<!-- content top end -->


    <div class="section pd_l30">
    <!-- content start -->
    <div class="article content">
        <!-- composer area start -->
        <div class="composer_area">
            <form:form method="post" name="AddressForm" modelAttribute="AddressForm" action="add.do">
            <table width="100%" border="0" cellpadding="0" cellspacing="1" >
                <colgroup>
                    <col style="width:150px" />
                    <col style="width:500px" />
                    <col style="width:150px" />
                    <col style="width:auto" />
                </colgroup>
                <tbody>
                <tr>
                    <th><spring:message code="E0018" text="이름"/><span class="txt red"><strong> *</strong></span></th>
                    <td>
                        <form:input path="name" maxlength="25"/>
                    </td>

                </tr>
                <tr>
                    <th><spring:message code="E0343" text="주소록 그룹"/></th>
                    <td>
                        <form:select path="gkey">
                            <form:option value="0"><spring:message code="E0311" text="미분류"/></form:option>
                            <form:options items="${addrGrpList}" itemLabel="gname" itemValue="gkey"/>
                        </form:select>
                    </td>


                </tr>
                <tr>
                    <th><spring:message code="E0022" text="E-Mail"/><span class="txt red"><strong> *</strong></span></th>
                    <td>
                        <form:input path="email" maxlength="100"  autocomplete="false"/>
                        <span class="txt strong red"><spring:message code="E0487" text="같은 그룹 내의 E-MAIL 중복만 허용합니다."/></span>
                    </td>

                </tr>
                <tr>
                    <th><spring:message code="E0312" text="회사"/></th>
                    <td>
                        <form:input path="company" maxlength="40" />
                    </td>

                </tr>
                <tr>
                    <th><spring:message code="E0313" text="부서"/></th>
                    <td>
                        <form:input path="dept" maxlength="40" />
                    </td>

                </tr>
                <tr>
                    <th><spring:message code="E0021" text="직책"/></th>
                    <td>
                        <form:input path="grade" maxlength="40" />
                    </td>

                </tr>
                <tr>

                </tr>
                <tr>
                    <th><spring:message code="E0314" text="회사전화"/></th>
                    <td>
                        <form:input path="office_tel" maxlength="50" />
                    </td>

                </tr>
                <tr>
                    <th><spring:message code="E0037" text="휴대폰번호"/></th>
                    <td>
                        <form:input path="mobile" maxlength="50" />
                    </td>

                </tr>
                <tr>
                    <th><spring:message code="E0316" text="기타정보1"/></th>
                    <td>
                        <form:input path="etc1" maxlength="100" />
                    </td>
                </tr>
                <tr>
                    <th><spring:message code="E0349" text="기타정보2"/></th>
                    <td>
                        <form:input path="etc2" maxlength="100" />
                    </td>
                </tr>


                </tbody>
            </table>
            </form:form>
        </div>
        <!-- composer area end -->
    </div>
    <!-- content end -->
</div>
<!-- section end -->

</div>
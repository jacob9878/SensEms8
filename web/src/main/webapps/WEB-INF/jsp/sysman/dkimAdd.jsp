<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file = "/WEB-INF/jsp/inc/taglib.jspf" %>
<script type="text/javascript" src="${staticURL}/sens-static/js/sysman/dkim.js"></script>
<c:if test="${ !empty infoMessage }">
    <script>
        alert( '${infoMessage}' );
    </script>
</c:if>

<!-- section start -->
<!-- top area start -->
<div class="dkim">
    <input type="hidden" id="cpage" value="${cpage}">
    <input type="hidden" id="srch_key" value="${srch_key}">
<form:form modelAttribute="dkimForm" id="dkimForm"  action="add.do" onsubmit="dkimAdd">
    <div class="title_box fixed">
        <div class="article top_area">
            <div class="title">
                <h1><spring:message code="E0619" text="DKIM 추가"/></h1>
                <p><spring:message code="E0620" text="DKIM을 추가할 수 있습니다."/></p>
            </div>
        </div>
    </div>
    <!-- top area end -->

    <!-- content top start -->
    <div class="content_top fixed">
        <ul class="content_top_btn">
            <li><button type="button" class="btn1" href="javascript:;" onclick="dkimAdd.confirm();"><spring:message code="E0069" text="저장"/></button></li>
            <li><a class="btn2" href="javascript:;" onclick="dkimAdd.list()"><spring:message code="E0047" text="목록"/></a></li>
        </ul>
    </div>
    <!-- content top end -->


        <!-- section start -->
        <div class="section pd_l30">
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
                                <th rowspan="2"><spring:message code="E0109" text="도메인명"/>
                                    </th>
                                <td class="h80">
                                    <form:input path="domain" type="text" size="80" maxlength="100" cssClass="w500" /><br>
                                    <span class="care_txt">
                                        <strong  class="txt red"><span><spring:message code="E0621" text="(추가할 도메인을 입력 후 추가해주세요.)"/></span></strong><br>
<%--                                        <spring:message code="E0205" text="ex)엑셀파일과 실행파일을 제한할 경우, 해당파일의 확장자만 입력해주세요."/><strong class="txt red"><spring:message code="E0206" text="(.xls, .exe)"/></strong></span>--%>
                                    <div class="error_msg"><form:errors path="domain" cssClass="error_msg"/></div>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
                <!-- composer area end -->

            </div>
            <!-- content end -->
        </div>
        <!-- section end -->
</form:form>

</div>
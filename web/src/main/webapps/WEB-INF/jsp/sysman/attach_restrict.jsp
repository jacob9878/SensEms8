<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file = "/WEB-INF/jsp/inc/taglib.jspf" %>
<script type="text/javascript" src="/sens-static/js/sysman/attach_restrict.js"></script>
<!-- section start -->
<!-- top area start -->
<div class="attach">
<form:form modelAttribute="attachRestrictForm" id="attachRestrictForm" method="post" action="restrict.do">
    <div class="title_box fixed">
        <div class="article top_area">
            <div class="title">
                <h1><spring:message code="E0201" text="첨부파일 확장자 관리"/></h1>
                <p><spring:message code="E0202" text="제한할 첨부파일 확장자를 관리합니다."/></p>
            </div>
        </div>
    </div>
    <!-- top area end -->

    <!-- content top start -->
    <div class="content_top fixed">
        <ul class="content_top_btn">
            <li><button type="button" class="btn1" href="javascript:;" onclick="attach_restrict.save()"><spring:message code="E0069" text="저장"/></button></li>
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
                                <th rowspan="2"><spring:message code="E0203" text="제한 확장자"/>
                                    </th>
                                <td class="h80">
                                    <form:input path="restrict_ext" type="text" size="80" maxlength="100" cssClass="w500" /><br>
                                    <span class="care_txt">
                                        <strong  class="txt red"><span><spring:message code="E0734" text="제한할 확장자를 입력한 후 저장을 클릭해주세요."/></span></strong><br>
                                        <spring:message code="E0205" text="ex)엑셀파일과 실행파일을 제한할 경우, 해당파일의 확장자만 입력해주세요."/><strong class="txt red"><spring:message code="E0206" text="(.xls, .exe)"/></strong></span>
                                    <div class="error_msg"><form:errors path="restrict_ext" cssClass="error_msg"/></div>
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
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
    <input type="hidden" id="srch_keyword" value="${srch_keyword}">
    <form:form modelAttribute="dkimForm" id="dkimForm"  action="view.do" onsubmit="dkimView">
    <form:hidden path="domain"/>
    <div class="title_box fixed">
        <div class="article top_area">
            <div class="title">
                <h1><spring:message code="E0622" text="DKIM 상세보기"/></h1>
                <p><spring:message code="E0623" text="DKIM설정에 대한 내용을 확인할 수 있습니다."/></p>
            </div>
        </div>
    </div>
    <!-- top area end -->

    <!-- content top start -->
    <div class="content_top fixed">
        <ul class="content_top_btn">
            <td colspan="4"><button type="button" class="btn2" onclick="dkimView.list();"><span><spring:message code="E0047" text="목록"/></span></button></td>
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
                            <th scope="row"><label><spring:message code="E0109" text="도메인명"/></label></th>
                            <td>${dkim.domain}</td>
                        </tr>
                        <tr>
                            <th scope="row"><label><spring:message code="E0598" text="지정자"/></label></th>
                            <td>${dkim.selector}</td>
                        </tr>
                        <tr>
                            <th scope="row"><label><spring:message code="E0600" text="공개키"/></label></th>
                            <td style="white-space: normal">
                                <div style=" word-break: break-all;border:1px solid #ccc;padding:3px;">
                                        ${dkim.public_key}
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <th scope="row"><label><spring:message code="E0288" text="등록일자"/></label></th>
                            <td><fmt:formatDate value="${dkim.regdate}" pattern="yyyy-MM-dd HH:mm"/></td>
                        </tr>
                        <tr>
                            <th scope="row"><label><spring:message code="E0601" text="DNS 등록방법"/></label></th>
                            <td style="white-space: normal">
                                <p><spring:message code="E0602" text="{0} 도메인의 DNS에 다음과 같이 추가합니다." arguments="${dkim.domain}"/></p>
                                <div style="word-break: break-all; background-color: #EEE;border:1px dotted #AAA;padding:2px;">
                                        ${dkim.selector}._domainkey IN TXT "v=DKIM1; g=*; k=rsa; p=${dkim.public_key}"
                                </div>
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
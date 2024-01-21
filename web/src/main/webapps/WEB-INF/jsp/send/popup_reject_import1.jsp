<%@ include file = "../inc/common.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<script type="text/javascript" src="${staticURL}/sens-static/js/send/reject.js"></script>
<c:if test="${ !empty infoMessage }">
    <script>
        alert( '${infoMessage}' );
    </script>
</c:if>
<!-- popup start-->
<div class="popup_title over_text">
    <div>
        <h3 class="title"><spring:message code="E0715" text="수신거부목록 가져오기 1단계"/></h3>
    </div>
</div>

<!-- content top start -->
<%--<div class="content_top fixed">
    <ul class="content_top_btn">
        <li><a class="btn1" href="#" onclick="addressList.addrSampleDownload();"><spring:message code="E0418" text="샘플 다운로드"/></a></li>
        <li><a class="btn2" href="#" onclick="addressList.doFileUp()"><spring:message code="E0419" text="다음"/></a></li>
    </ul>
</div>--%>
<!-- content top end -->

<div class="section">
    <!-- content start -->
    <div class="article content">
        <!-- content area start -->
        <div class="composer_area" >
            <form name="importForm" id="importForm" method="post" action="/send/reject/doImport2.do?${_csrf.parameterName}=${_csrf.token}" enctype="multipart/form-data" accept-charset="UTF-8">
                <table  width="100%" border="0" cellpadding="0" cellspacing="1" class="table">
                    <colgroup>
                        <col style="width:150px;">
                        <col style="width:auto;">
                    </colgroup>
                    <tbody>
                        <tr>
                            <th><spring:message code="E0479" text="파일첨부"/></th>
                            <td>
                                <input type="file" id="im_file" name="im_file" size="35" onkeydown="return false;" /><br/>
                            </td>
                        </tr>
                    </tbody>
                </table>
                <span class="care_txt block mg_t10"><spring:message code="E0417" text="CSV 파일 또는 쉼표나 세미콜론으로 구분된 TXT 파일만 가능합니다."/></span><br/>
            </form>
        </div>
        <!-- content area end -->
    </div>
    <!-- content end -->

</div>
<!-- popup end-->
<div class="pop_footer">
<div class="btn_div ">
    <button class="btn1" href="javascript:;" onclick="reject_list.addrSampleDownload();"><spring:message code="E0418" text="샘플 다운로드"/></button>
    <button class="btn2" href="javascript:;" onclick="reject_list.doFileUp()"><spring:message code="E0419" text="다음"/></button>
    <%--<button class="close_btn btn2" onclick="window.close();"><spring:message code="E0282" text="닫기"/></button>--%>
</div>
</div>

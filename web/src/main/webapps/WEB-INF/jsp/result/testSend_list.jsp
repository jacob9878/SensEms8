<%@ page contentType="text/html;charset=utf-8" %>
<%@ page language="java" %>
<%@ include file="../inc/common.jsp" %>
<!-- section start -->
<!-- top area start -->
<form:form modelAttribute="testSendListForm" name="testSendListForm" method="get" action="sendList.do">
    <form:hidden path="cpage" value="${pageInfo.cpage }"/>
    <form:hidden path="send_type"/>
    <div class="title_box fixed">
        <div class="article top_area">
            <div class="title">
                <h1><spring:message code="E0572" text="개별발송 결과"/></h1>
                <p><spring:message code="E0573" text="개별발송 결과를 볼 수 있습니다."/></p>
            </div>
            <div class="search_box">
                <div class="inner">
                    <div class="select_box">
                        <form:select path="srch_type" class="search_opt">
                            <form:option value="mailfrom"><spring:message code="E0574" text="발신주소"/></form:option>
                            <form:option value="rcptto"><spring:message code="E0575" text="수신주소"/></form:option>
                            <form:option value="errcode"><spring:message code="E0578" text="에러코드"/></form:option>
                        </form:select>
                    </div>
                    <form:input path="srch_keyword" id="srch_keyword" value="" onfocus="testsendsearch.init(this)" placeholder="검색어를 입력하세요."/>
                    <button type="button" class="btn1" onclick="send_resultList.search();">검색</button>
                    <c:if test="${issearch}"><button type="button" id="allview" class="btn2" onclick="send_resultList.viewAll();">전체목록</button></c:if>
                </div>
            </div>
        </div>
    </div>
    <!-- top area end -->
    <!-- content top start -->
    <div class="content_top fixed">
        <ul class="content_top_btn">
            <li><a class="btn2" href="javascript:;" onclick="send_resultList.delete();"><spring:message code="E0030" text="삭제"/></a></li>
        </ul>

        <div class="select_box">
            <%--<select class="send_type w200" id="sendTypesort" onchange="send_resultList.sendTypeSort()">
                <option value="4"<c:if test="${testSendListForm.send_type eq '4'}">selected</c:if>><spring:message code="E0371" text="전체 선택"/></option>
                <option value="0"<c:if test="${testSendListForm.send_type eq '0'}">selected</c:if>><spring:message code="E0371" text="테스트 발송"/></option>
                <option value="1"<c:if test="${testSendListForm.send_type eq '1'}">selected</c:if>><spring:message code="E0371" text="DB연동"/></option>
                <option value="2"<c:if test="${testSendListForm.send_type eq '2'}">selected</c:if>><spring:message code="E0371" text="SMTP 인증"/></option>
                <option value="3"<c:if test="${testSendListForm.send_type eq '3'}">selected</c:if>><spring:message code="E0371" text="릴레이"/></option>
            </select>--%>
            <select class="list_select" onchange="common.change_pagesize(this.value)">
                <option ${f:isSelected('15',UserInfo.pagesize)} value="15"><spring:message code="E0233" text="15"/></option>
                <option ${f:isSelected('30',UserInfo.pagesize)} value="30"><spring:message code="E0234" text="30"/></option>
                <option ${f:isSelected('50',UserInfo.pagesize)} value="50"><spring:message code="E0235" text="50"/></option>
                <option ${f:isSelected('100',UserInfo.pagesize)} value="100"><spring:message code="E0236" text="100"/></option>
            </select>
        </div>
    </div>
    <!-- content top end -->

    <div class="section pd_l30">
        <!-- content start -->
        <div class="article content">
            <!-- content area start -->
            <div class="content_area">
                <table width="100%" height="auto">
                    <colgroup>
                        <col style="width:25px">
                        <col style="width:auto">
                        <col style="width:auto">
                        <col style="width:auto">
                        <col style="width:auto">
                        <col style="width:100px">
                        <%--<col style="width:160px">--%>
                        <col style="width:150px">
                    </colgroup>
                    <thead class="fixed">
                    <tr>
                        <th class="txt center">
                            <input type="checkbox" id="all_check" onclick="common.select_all('ukey');" title="<spring:message code="E0391" text="전체선택"/>">
                        </th>
                        <th><spring:message code="E0103" text="제목"/></th>
                        <th><spring:message code="E0574" text="발신주소"/></th>
                        <th><spring:message code="E0575" text="수신주소"/></th>
                        <th><spring:message code="E0453" text="발송일자"/></th>
                        <th><spring:message code="E0454" text="발송결과"/></th>
                        <%--<th><spring:message code="E0455" text="발송구분"/></th>--%>
                        <th><spring:message code="E0576" text="수신확인"/></th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:if test="${ empty testSendList }">
                        <td height="46px" colspan="7" align="center"><spring:message code="E0586" text="데이터가 없습니다."/></td>
                    </c:if>
                    <c:forEach items="${testSendList}" var="result">
                        <tr>
                            <td class="check_ico">
                                <input type="checkbox" name="ukey" value="${result.traceid},${result.subject}" title="<spring:message code="E0175" text="선택"/>">
                            </td>
                            <td>
                                <a href ="javascript:;" onclick="send_resultList.view('${result.traceid}', '${result.serverid}','${result.rcptto}')"><c:out value="${result.subject}"></c:out></a>
                            </td>
                            <td><c:out value="${result.mailfrom}"></c:out></td>
                            <td><c:out value="${result.rcptto}"></c:out></td>
                            <td>
                                <fmt:formatDate value="${result.logdate}" pattern="YYYY-MM-dd HH:mm"/>
                            </td>
                            <td>
                                <c:if test="${result.result eq '0'}">
                                    <spring:message code="E0457" text="실패(${result.errcode})"/>
                                </c:if>
                                <c:if test="${result.result eq '1'}">
                                    <spring:message code="E0456" text="성공"/>
                                </c:if>
                                <c:if test="${result.result eq '2'}">
                                    <spring:message code="E0412" text="대기중"/>
                                </c:if>
                            </td>
                            <%--<td>
                                <c:choose>
                                    <c:when test="${result.send_type == 'T'}"><span title="테스트 발송"><spring:message code="E0371" text="테스트 발송"/></span></c:when>
                                    <c:when test="${result.send_type == 'D'}"><span title="DB연동"><spring:message code="E0371" text="DB연동"/></span></c:when>
                                    <c:when test="${result.send_type == 'A'}"><span title="SMTP 인증"><spring:message code="E0371" text="SMTP 인증"/></span></c:when>
                                    <c:when test="${result.send_type == 'R'}"><span title="릴레이"><spring:message code="E0371" text="릴레이"/></span></c:when>
                                    <c:when test="${result.send_type == 'C'}"><span title="개별 재발신"><spring:message code="E0371" text="개별 재발신"/></span></c:when>
                                </c:choose>
                            </td>--%>
                            <td>
                                <c:if test="${ result.readdate ne null  }">
                                <fmt:formatDate value="${result.readdate}" pattern="yyyy-MM-dd HH:mm"/>(<c:out value="${result.readcount}"></c:out>)
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
            <!-- content area end -->
        </div>
        <!-- content end -->
        <!-- nav start -->
        <div class="page_nav">
            <c:if test="${ empty testSendList }">
                <pt:page>
                    <pt:cpage>
                        1
                    </pt:cpage>
                    <pt:pageSize>
                        1
                    </pt:pageSize>
                    <pt:total>
                        1
                    </pt:total>
                    <pt:jslink>send_resultList.list</pt:jslink>
                </pt:page>
            </c:if>
            <c:if test="${ !empty testSendList }">
                <pt:page>
                    <pt:cpage>
                        ${ pageInfo.cpage }
                    </pt:cpage>
                    <pt:pageSize>
                        ${ pageInfo.pageSize }
                    </pt:pageSize>
                    <pt:total>
                        ${ pageInfo.total }
                    </pt:total>
                    <pt:jslink>send_resultList.list</pt:jslink>
                </pt:page>
            </c:if>
        </div>
        <!-- nav end -->

    <!-- section end -->

</form:form>
</div>
</div>
<!-- popup start-->
<div id="popup_content" style="display: none">
    <div class="popup">
        <div class="popup_content"  style="height: 450px; width: 800px;">
            <div class="popup_title">
                <span class="close_button" onclick="send_resultList.view_close();"></span>
                <h3 class="title" id="view_subject"></h3>
            </div>
            <div class="popup_body ">
                <table table width="100%" border="0" cellpadding="0" cellspacing="1">
                    <colgroup>
                        <col style="width:150px;">
                        <col style="width:auto;">
                    </colgroup>
                    <tbody>
                    <tr>
                        <th class="view_header">TraceID</th>
                        <td><span id="view_traceid" ></span></td>
                    </tr>
                    <tr>
                        <th class="view_header"><spring:message code="119" text="날짜"/></th>
                        <td><span id="view_date" ></span></td>
                    </tr>
                    <tr>
                        <th class="view_header"><spring:message code="65" text="발신자"/></th>
                        <td><span id="view_from"></span></td>
                    </tr>
                    <tr>
                        <th class="view_header"><spring:message code="64" text="수신자"/></th>
                        <td ><span  id="view_to" ></span></td>
                    </tr>
                    <tr>
                        <th class="view_header"><spring:message code="116" text="발신아이피"/></th>
                        <td ><span  id="view_sendip" ></span></td>
                    </tr>
<%--                    <tr>--%>
<%--                        <th class="view_header"><spring:message code="117" text="SMTP 인증아이디"/></th>--%>
<%--                        <td ><span  id="view_smtpid" ></span></td>--%>
<%--                    </tr>--%>
<%--                    <tr>--%>
<%--                        <th class="view_header"><spring:message code="118" text="서버아이디"/></th>--%>
<%--                        <td ><span  id="view_serverid" ></span></td>--%>
<%--                    </tr>--%>
                    <tr >
                        <th  class="view_header"><spring:message code="30" text="발송결과"/></th>
                        <td><span id="view_result"  class="text_tag"></span></td>
                    </tr>
                    <tr>
                        <th class="view_header"><spring:message code="136" text="수신확인 시간"/></th>
                        <td ><span  id="view_readdate" ></span></td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>
<!-- popup end-->



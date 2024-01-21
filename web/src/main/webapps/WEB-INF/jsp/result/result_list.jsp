<%@ page contentType="text/html;charset=utf-8" %>
<%@ page language="java" %>
<%@ include file="../inc/common.jsp" %>
<!-- section start -->
<!-- top area start -->
<input type="hidden" id="srch_key" value="<c:out value="${srch_key}" escapeXml="true"></c:out>">
<form:form modelAttribute="emsListForm" name="emsListForm" method="get" action="list.do">
    <form:hidden path="cpage" value="${pageInfo.cpage }"/>
    <form:hidden path="state"/>
    <div class="statistics">
    <div class="title_box fixed">
        <div class="article top_area">
            <div class="title">
                <h1><spring:message code="E0361" text="메일발송 결과"/></h1>
                <p><spring:message code="E0362" text="메일발송 결과나 통계를 볼 수 있습니다."/></p>
            </div>
            <div class="search_box">
                <div class="inner">
                    <div class="select_box">
                        <!-- <select class="search_opt" id="" name="search_opt">
                            <option value=""><spring:message code="E0103" text="제목"/></option>
                        </select> -->
                    </div>
<%--                    <form:input path="srch_keyword" id="srch_keyword" value="" onsubmit="return resultList.search();" onkeypress="enterEvent('13')" placeholder="검색어를 입력하세요."/>--%>
                    <form:input path="srch_keyword" id="srch_keyword" htmlEscape="true" value="" onfocus="simpleSearch.init(this)" placeholder="검색어를 입력하세요."/>
                    <button type="button"  class="btn1" onclick="resultList.search();"><spring:message code="E0321" text="검색"/></button>
                    <c:if test="${issearch}"><button type="button" id="allview" class="btn2" onclick="resultList.viewAll();"><spring:message code="E0649" text="전체목록"/></button></c:if>
                </div>
            </div>
        </div>
    </div>
    <!-- top area end -->
    <!-- content top start -->
    <div class="content_top fixed">
        <ul class="content_top_btn">
            <li><a class="btn2" href="javascript:;" onclick="resultList.reLoad();"><spring:message code="E0363" text="새로고침"/></a></li>
            <li><a class="btn2" href="javascript:;" onclick="resultList.moveCategoryLayreOpen();"><spring:message code="E0309" text="이동"/></a></li>
            <li><a class="btn2" href="javascript:;" onclick="resultList.delete();"><spring:message code="E0030" text="삭제"/></a></li>
            <li><a class="btn2" href="javascript:;" onclick="resultList.doStop();"><spring:message code="E0364" text="중지"/></a></li>
            <li><a class="btn2" href="javascript:;" onclick="resultList.reSend();"><spring:message code="E0742" text="재작성"/></a></li>
        </ul>
        <div class="select_box">
            <form:select path="categoryid" class="category w200" onchange="resultList.categorySort();">
                <c:choose>
                    <c:when test="${categoryList != null}">
                        <option selected value=""><spring:message code="E0429" text="발송분류(전체보기)"/></option>
                        <c:choose>
                            <c:when test="${emsListForm.categoryid eq ''}">
                                <c:forEach items="${categoryList}" var="category">
                                    <option value="${category.ukey}"><c:out value="${category.name}"></c:out></option>
                                </c:forEach>
                            </c:when>
                            <c:otherwise> <%-- 카테고리가 null이 아니면 선택을 해준다.--%>
                                <c:set var="categoryid" value="${emsListForm.categoryid}"/>
                                <c:forEach items="${categoryList}" var="category">
                                    <option value="${category.ukey}"
                                            <c:if test="${category.ukey eq categoryid}">selected</c:if> ><c:out value="${category.name}"></c:out></option>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </c:when><%-- 카테고리가 null이 아니면 선택을 해준다.--%>
                    <c:otherwise>
                        <option selected value=""><spring:message code="E0255" text="발송분류"/></option>
                    </c:otherwise>
                </c:choose>
            </form:select>

            <select class="state w200" id="stateSort" onchange="resultList.stateListSort()">
                <option value="0"<c:if test="${emsListForm.state eq '0'}">selected</c:if>><spring:message code="E0428" text="발송상태(전체보기)"/></option>
                <option value="1"<c:if test="${emsListForm.state eq '1'}">selected</c:if>><spring:message code="E0379" text="발송완료"/></option>
                <option value="2"<c:if test="${emsListForm.state eq '2'}">selected</c:if>><spring:message code="E0412" text="대기 중"/></option>
                <option value="3"<c:if test="${emsListForm.state eq '3'}">selected</c:if>><spring:message code="E0374" text="발송중"/></option>
                <option value="4"<c:if test="${emsListForm.state eq '4'}">selected</c:if>><spring:message code="E0372" text="임시보관중"/></option>
            </select>

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
                        <col style="width:25px"/>
                        <col style="width:auto"/>
                        <col style="width:120px"/>
                        <col style="width:200px"/>
                        <col style="width:150px"/>
                        <col style="width:130px"/>
                        <col style="width:130px"/>
                        <col style="width:65px"/>
                    </colgroup>
                    <thead class="fixed">
                    <tr>
                        <th class="check_ico">
                            <input type="checkbox" id="all_check" onclick="common.select_all('msgid');" title="<spring:message code="E0391" text="전체선택"/>">
                        </th>
                        <th><spring:message code="E0103" text="제목"/></th>
                        <th><spring:message code="E0066" text="작성자"/></th>
                        <th><spring:message code="E0366" text="발송상태"/></th>
                        <th><spring:message code="E0367" text="발송수"/></th>
                        <th><spring:message code="E0067" text="등록일"/></th>
                        <th><spring:message code="E0368" text="발송일"/></th>
                        <th><spring:message code="E0369" text="리포팅"/></th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:if test="${ empty emsList }">
                        <td height="46px" colspan="8" align="center"><spring:message code="E0586" text="데이터가 없습니다."/></td>
                    </c:if>
                    <c:forEach items="${emsList}" var="result">
                        <c:set var="processRate" value="${( result.cur_send * 100 ) / result.total_send}"/>
                        <fmt:formatNumber value="${processRate}" type="number" var="val" pattern="##.#"/>
                        <tr>
                            <td class="check_ico">
                                <input type="checkbox" name="msgid" value="${result.msgid},${result.msg_name}" userid="${result.userid}" state="${result.state}" title="<spring:message code="E0175" text="선택"/>">
                            </td>
                            <td>
                                <c:if test="${result.resend_num ne 0}">
                                    <c:forEach begin="1" end="${result.resend_step}">　</c:forEach><spring:message code="E0628" text="└ [재발송]"/>
                                    <%--<c:if test="${result.resend_step ne 1}"><c:forEach begin="1" end="${result.resend_step}">　</c:forEach></c:if><spring:message code="E0628" text="└[재발송]"/>--%>
                                </c:if>
                                    <%--  <a href="design/html/02002_statisticsSend.html">${result.msg_name}</a>--%>
                                <c:choose>
                                    <c:when test="${result.state eq '007'}"><a href ="javascript:;" onclick="resultList.goLayoutWriteForm('${result.msgid}')"><c:out value="${result.msg_name}"></c:out></a></c:when>
                                    <c:otherwise><a href ="javascript:;" onclick="resultList.goStaticSend('${result.msgid}','${cpage}','${srch_key}')"><c:out value="${result.msg_name}"></c:out></a></c:otherwise>
                                </c:choose>

                            </td>
                            <td><c:out value="${result.userid}"></c:out></td> <!-- userid -->
                            <td>
                                <c:choose>
                                    <c:when test="${result.state == '000'}"><span title="발송대기"><spring:message code="E0371" text="발송대기"/><fmt:parseDate value="${result.reserv_time}" pattern="yyyyMMddHHmm" var="reserv_time"/><c:if test="${ !empty result.reserv_time }">(<fmt:formatDate value="${reserv_time}" pattern="yyyy-MM-dd HH:mm"/>)</c:if></span></c:when>
                                    <c:when test="${result.state == '007'}"><span title="임시보관중"><spring:message code="E0372" text="임시보관중"/></c:when>
                                    <c:when test="${result.state == '010'}"><span title="수신자 추출중"><spring:message code="E0373" text="수신자 추출중"/></c:when>
                                    <c:when test="${result.state == '030'}"><span title="발송중">
                                         <c:if test="${result.total_send eq '0'}"><spring:message code="E0374" text="발송중"/><c:out value="( 0% )"></c:out></c:if>
                                         <c:if test="${result.total_send > '0'}"><spring:message code="E0374" text="발송중"/><c:out value="( ${val}% )"></c:out></c:if>
                                    </c:when>
                                    <c:when test="${result.state == '031'}"><span title="발송중지"><spring:message code="E0375" text="발송중지"/></c:when>
                                    <c:when test="${result.state == '040'}"><span title="통계데이터 정리중"><spring:message code="E0376" text="통계데이터 정리중"/></c:when>
                                    <c:when test="${result.state == '-00'}"><span title="수신대상자 없음"><spring:message code="E0377" text="수신대상자 없음"/></c:when>
                                    <c:when test="${result.state == '-10'}"><span title="수신자목록 생성실패"><spring:message code="E0378" text="수신자목록 생성실패"/></c:when>
                                    <c:when test="${result.state == '+10'}"><span title="수신자 추출완료"><spring:message code="E0627" text="수신자 추출완료"/></span></c:when>
                                    <c:when test="${result.state == '+30'}"><span title="발송완료"><spring:message code="E0379" text="발송완료"/></c:when>
                                    <c:when test="${result.state == '011'}"><span title="전송중지(수신자추출중)"><spring:message code="E0413" text="전송중지(수신자추출중)"/></c:when>
                                    <c:when test="${result.state == '100'}"><span title="전송중지"><spring:message code="E0414" text="전송중지"/></c:when>
                                </c:choose>
                                <c:if test="${result.state == '031' || result.state == '011' || result.state == '100'}"><a class="btn2" href="javascript:;" onclick="resultList.doResend('${result.msgid}','${result.msg_name}','${result.reserv_time}');"><spring:message code="E0365" text="재발신"/></a></c:if>
                            </td>

                            <td>
                                <c:out value="${result.cur_send}"></c:out> / <c:out value="${result.total_send}"></c:out>
                                <button title="<spring:message code="E0370" text="발송수 목록"/>" class="read btn4"  onclick="resultList.openReceiverList('${result.msgid}')"type="button">
                                        <%--onclick="window.open('${StaticURL}/design/html/02001_statistics_list.html','발송수 목록','width=860px,height=750px,location=no,status=no,scrollbars=yes,menubar=no,resizable=yes');" type="button">--%>
                                    <img src="${StaticURL}/sens-static/images/icon_read.png" alt=""/>
                                </button>
                            </td>
                            <td>
                                <fmt:parseDate value="${result.regdate}" pattern="yyyyMMddHHmm" var="regdate" />
                                <fmt:formatDate value="${regdate}" pattern="yyyy-MM-dd"/> <fmt:formatDate value="${regdate}" pattern="HH:mm"/>
                            </td>
                            <td>
                                <c:if test="${result.state == '030' || result.state == '+30'}">
                                <fmt:parseDate value="${result.start_time}" pattern="yyyyMMddHHmmss" var="start_regdate"/>
                                <fmt:formatDate value="${start_regdate}" pattern="yyyy-MM-dd HH:mm"/>
                                </c:if>
                            </td>
                            <td class="txt center">
                                <button title="<spring:message code="E0369" text="리포팅"/>" class="btn3" onclick="resultList.openReport('${result.msgid}')" type="button"><spring:message code="E0390" text="보기"/></button>
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
            <c:if test="${ empty emsList }">
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
                    <pt:jslink>resultList.list</pt:jslink>
                </pt:page>
            </c:if>
            <c:if test="${ !empty emsList }">
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
                    <pt:jslink>resultList.list</pt:jslink>
                </pt:page>
            </c:if>
        </div>
        <!-- nav end -->
    </div>
    <!-- section end -->

</form:form>
</div>
<!-- template add popup start-->
<div id="moveCategoryLayer" style="display:none;" title="<spring:message code="E0711" text="발송분류 이동"/>">
    <div class="popup">
        <div class="popup_content" style="height: 180px;">
            <div class="popup_title">
                <span class="close_button" onclick="resultList.moveCategoryLayerClose();"></span>
                <h3 class="title"><spring:message code="E0711" text="발송분류 이동"/></h3>
            </div>
            <div class="popup_body">
                <!-- <p> 설명 텍스트 </p> -->
                <table width="100%" border="0" cellpadding="0" cellspacing="1">
                    <tbody>
                    <tr>
                        <th style="width:150px; min-width:150px;"><spring:message code="E0712" text="발송분류 선택"/></th>
                        <td>
                            <select id="moveLayerCategory" name="moveLayerCategory" class="w_full">
                                <c:if test="${empty categoryList}">
                                    <option value=""><spring:message code="E0389" text="카테고리가 존재하지 않습니다."/></option>
                                </c:if>
                                <c:forEach items="${categoryList}" var="category">
                                    <option value="${category.ukey}"><c:out value="${category.name}"></c:out></option>
                                </c:forEach>
                            </select>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </div>
            <div class="pop_footer">
                <div class="btn_div">
                    <button type="button" class="btn2" onclick="resultList.moveCategoryConfirm();"><spring:message
                            code="E0309" text="이동"/></button>
                    <button type="button" class="btn2" onclick="resultList.moveCategoryLayerClose();"><spring:message
                            code="E0282" text="닫기"/></button>
                </div>
            </div>
        </div>
    </div>
</div>
<!-- popup end-->
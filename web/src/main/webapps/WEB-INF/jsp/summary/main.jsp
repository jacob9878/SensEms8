<%@ page contentType="text/html;charset=utf-8" %>
<%@ page language="java" %>
<%@ include file="../inc/common.jsp" %>

<script type="text/javascript" src="/sens-static/js/result/result.js"></script>
<!-- section start -->
<!-- top area start -->
<form:form modelAttribute="emsListForm" name="emsListForm" method="post" action="main.do">
    <form:hidden path="cpage" value="${pageInfo.cpage }"/>
    <form:hidden path="userid"/>
    <form:hidden path="state"/>
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
                    <form:input path="srch_keyword" id="srch_keyword" value="" placeholder="검색어를 입력하세요."/>
                    <button type="button" onclick="resultList.search();"><img src="${StaticURL}/sens-static/images/search_btn.png" alt=""/></button>
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
            <li><a class="btn2" href="javascript:;" onclick="resultList.doResend();"><spring:message code="E0365" text="재발신"/></a></li>
        </ul>
            <%--<div class="content_top_opt">
                <button type="button" onclick="common.open_dropMenu();"><img
                        src="${StaticURL}/sens-static/images/top_opt_btn.png" alt=""/></button>
                <div class="drop_menu" style="display:none;">
                    <table>
                        <tbody>
                        <tr height="30px">
                            <th scope="row"><span><spring:message code="" text="목록 개수"/></span></th>
                            <td>
                                <select onchange="common.change_pagesize(this.value)">
                                    <option ${f:isSelected('15',UserInfo.pagesize)} value="15"><spring:message code="E0233" text="15"/></option>
                                    <option ${f:isSelected('30',UserInfo.pagesize)} value="30"><spring:message code="E0234" text="30"/></option>
                                    <option ${f:isSelected('50',UserInfo.pagesize)} value="50"><spring:message code="E0235" text="50"/></option>
                                    <option ${f:isSelected('100',UserInfo.pagesize)} value="100"><spring:message code="E0236" text="100"/></option>
                                </select>
                            </td>
                        </tr>
                        <tr height="30px">
                            <th scope="row"><span><spring:message code="E0041" text="목록 간격"/></span></th>
                            <td>
                                <select>
                                    <option><spring:message code="E0043" text="좁게"/></option>
                                    <option><spring:message code="E0045" text="보통"/></option>
                                    <option><spring:message code="E0044" text="넓게"/></option>
                                </select>
                            </td>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </div>--%>
        <div class="select_box">
            <form:select path="categoryid" class="category w200 list_select" onchange="resultList.categorySort();">
                <c:choose>
                    <c:when test="${categoryList != null}">
                        <option selected value=""><spring:message code="E0429" text="발송분류(전체보기)"/></option>
                        <c:choose>
                            <c:when test="${emsListForm.categoryid eq ''}">
                                <c:forEach items="${categoryList}" var="category">
                                    <option value="${category.ukey}">${category.name}</option>
                                </c:forEach>
                            </c:when>
                            <c:otherwise> <%-- 카테고리가 null이 아니면 선택을 해준다.--%>
                                <c:set var="categoryid" value="${emsListForm.categoryid}"/>
                                <c:forEach items="${categoryList}" var="category">
                                    <option value="${category.ukey}"
                                            <c:if test="${category.ukey eq categoryid}">selected</c:if> >${category.name}</option>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </c:when><%-- 카테고리가 null이 아니면 선택을 해준다.--%>
                    <c:otherwise>
                        <option selected value=""><spring:message code="E0255" text="발송분류"/></option>
                    </c:otherwise>
                </c:choose>
            </form:select>

            <select class="state w200 list_select" id="stateSort" onchange="resultList.stateListSort()">
                <option value="0" <c:if test="${emsListForm.state eq '0'}">selected</c:if>><spring:message code="E0428" text="발송상태(전체보기)"/></option>
                <option value="1"<c:if test="${emsListForm.state eq '1'}">selected</c:if>><spring:message code="E0379" text="발송완료"/></option>
                <option value="2"<c:if test="${emsListForm.state eq '2'}">selected</c:if>><spring:message code="E0412" text="대기 중"/></option>
                <option value="3"<c:if test="${emsListForm.state eq '3'}">selected</c:if>><spring:message code="E0374" text="발송중"/></option>
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
                        <col style="width:35px"/>
                        <col style="width:100px"/>
                        <col style="width:150px"/>
                        <col style="width:auto"/>
                        <col style="width:140px"/>
                        <col style="width:130px"/>
                        <col style="width:130px"/>
                        <col style="width:65px"/>
                    </colgroup>
                    <thead class="fixed">
                    <tr>
                        <th class="txt center">
                            <input type="checkbox" id="all_check" onclick="common.select_all('msgid');" title="<spring:message code="E0391" text="전체선택"/>">
                        </th>
                        <th><spring:message code="E0066" text="작성자"/></th>
                        <th><spring:message code="E0366" text="발송상태"/></th>
                        <th><spring:message code="E0103" text="제목"/></th>
                        <th><spring:message code="E0367" text="발송수"/></th>
                        <th class="txt center"><spring:message code="E0067" text="등록일"/></th>
                        <th class="txt center"><spring:message code="E0368" text="발송일"/></th>
                        <th class="txt center"><spring:message code="E0369" text="리포팅"/></th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:if test="${ empty emsList }">
                        <td height="46px" colspan="5" align="center"><spring:message code="E0380"
                                                                                     text="메일발송 결과가 없습니다."/></td>
                    </c:if>
                    <c:forEach items="${emsList}" var="result">
                        <tr>
                            <td class="txt center">
                                <input type="checkbox" name="msgid" value="${result.msgid}" state="${result.state}"
                                       title="<spring:message code="E0175" text="선택"/>">
                            </td>
                            <td>${result.userid}</td> <!-- userid -->
                            <td>
                                <c:choose>
                                    <c:when test="${result.state == '000'}"><spring:message code="E0371" text="발송대기"/></c:when>
                                    <c:when test="${result.state == '007'}"><spring:message code="E0372" text="임시보관중"/></c:when>
                                    <c:when test="${result.state == '010'}"><spring:message code="E0373" text="수신자 추출중"/></c:when>
                                    <c:when test="${result.state == '030'}"><spring:message code="E0374" text="발송중"/></c:when>
                                    <c:when test="${result.state == '031'}"><spring:message code="E0375" text="발송중지"/></c:when>
                                    <c:when test="${result.state == '040'}"><spring:message code="E0376" text="통계데이터 정리중"/></c:when>
                                    <c:when test="${result.state == '-00'}"><spring:message code="E0377" text="수신대상자 없음"/></c:when>
                                    <c:when test="${result.state == '-10'}"><spring:message code="E0378" text="수신자목록 생성실패"/></c:when>
                                    <c:when test="${result.state == '+10'}"><spring:message code="E0627" text="수신자 추출완료"/></c:when>
                                    <c:when test="${result.state == '+30'}"><spring:message code="E0379" text="발송완료"/></c:when>
                                    <c:when test="${result.state == '011'}"><spring:message code="E0413" text="전송중지(수신자추출중)"/></c:when>
                                    <c:when test="${result.state == '100'}"><spring:message code="E0414" text="전송중지"/></c:when>
                                </c:choose>
                            </td>
                            <td class="over_text">
                                    <%--  <a href="design/html/02002_statisticsSend.html">${result.msg_name}</a>--%>
                                <a href ="javascript:;" onclick="resultList.goStaticSend2('${result.msgid}')">${result.msg_name}</a>
                            </td>
                            <td>
                                    ${result.cur_send} / ${result.total_send}
                                <button title="<spring:message code="E0370" text="발송수 목록"/>" class="read btn4"  onclick="resultList.openReceiverList('${result.msgid}')"type="button">
                                        <%--onclick="window.open('${StaticURL}/design/html/02001_statistics_list.html','발송수 목록','width=860px,height=750px,location=no,status=no,scrollbars=yes,menubar=no,resizable=yes');" type="button">--%>
                                    <img src="${StaticURL}/sens-static/images/icon_read.png" alt=""/>
                                </button>
                            </td>
                            <td class="list_2line txt center">
                                <fmt:parseDate value="${result.regdate}" pattern="yyyyMMddHHmm" var="regdate" />
                                <fmt:formatDate value="${regdate}" pattern="yyyy-MM-dd"/> <fmt:formatDate value="${regdate}" pattern="HH:mm"/>
                            </td>
                            <td class="list_2line txt center">
                                <fmt:parseDate value="${result.start_time}" pattern="yyyyMMddHHmm" var="start_regdate"/>
                                <fmt:formatDate value="${start_regdate}" pattern="yyyy-MM-dd HH:mm"/>
                            </td>
                            <td class="txt center">
                                <button title="<spring:message code="E0369" text="리포팅"/>" class="btn_small" onclick="resultList.openReport('${result.msgid}')" type="button"><spring:message code="E0390" text="보기"/></button>
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

<!-- template add popup start-->
<div id="moveCategoryLayer" style="display:none;" title="<spring:message code="E0387" text="카테고리 이동"/>">
    <div class="popup">
        <div class="popup_content" style="height: 180px;">
            <div class="popup_title">
                <span class="close_button" onclick="resultList.moveCategoryLayerClose();"></span>
                <h3 class="title"><spring:message code="E0387" text="카테고리 이동"/></h3>
            </div>
            <div class="popup_body">
                <!-- <p> 설명 텍스트 </p> -->
                <table width="100%" border="0" cellpadding="0" cellspacing="1">
                    <tbody>
                    <tr>
                        <th style="width:150px; min-width:150px;"><spring:message code="E0388" text="변경할 분류"/></th>
                        <td>
                            <select id="moveLayerCategory" name="moveLayerCategory">
                                <c:if test="${empty categoryList}">
                                    <option value=""><spring:message code="E0389" text="카테고리가 존재하지 않습니다."/></option>
                                </c:if>
                                <c:forEach items="${categoryList}" var="category">
                                    <option value="${category.ukey}">${category.name}</option>
                                </c:forEach>
                            </select>
                        </td>
                    </tr>
                    </tbody>
                </table>
                <div class="pop_footer">
                    <div class="btn_div">
                        <button type="button" class="btn1" onclick="resultList.moveCategoryConfirm();"><spring:message
                                code="E0309" text="이동"/></button>
                        <button type="button" class="btn2" onclick="resultList.moveCategoryLayerClose();"><spring:message
                                code="E0282" text="닫기"/></button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<!-- popup end-->
























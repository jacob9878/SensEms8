<%@ include file = "../inc/common.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<script type="text/javascript" src="/sens-static/js/result/result.js"></script>
<script type="text/javascript" src="${staticURL}/sens-static/js/common/errpopupsearch.js"></script>
<form:form modelAttribute="StatErrorForm" name="StatErrorForm" method="get" action="statErrorList.do">
    <form:hidden path="msgid"/>
    <form:hidden path="cpage" value="${pageInfo.cpage }"/>
<div class="w_pop ">
    <div class="statisticsError w_content">
        <div class="popup_title ">
            <div >
                <h3 class="title"><spring:message code="E0647" text="에러 통계 목록보기"/></h3>
            </div>

            <div class="search_box">
                <div class="inner">
                    <div class="select_box">
                        <form:select path="srch_type" class="search_op" >
                            <form:option value="01">field1(E-mail)</form:option>
                            <form:option value="02"><spring:message code="E0648" text="에러내용"/></form:option>
                        </form:select>
                    </div>
                    <form:input path="srch_keyword" id="srch_keyword" onfocus="errpopupsearch.init(this)" value="" placeholder="검색어를 입력하세요."/>
                    <button class="search_btn btn1" onclick="statisticsError.search()"><span><spring:message code="E0321" text="검색"/></span></button>
                    <c:if test="${issearch}"><button type="button" id="allview" class="btn2" onclick="statisticsError.viewAll();"><spring:message code="E0649" text="전체목록"/></button></c:if>
                </div>
            </div>
            <div  style="text-align: right; color: #999999; "><spring:message code="E0650" text="E-mail 검색 시 정확한 이메일을 입력해주세요."/></div>
        </div>

        <!-- content top start -->
        <div class="content_top fixed">
            <ul class="content_top_btn">
                <li><a class="btn2" onclick="statisticsError.download()"><spring:message code="E0073" text="목록저장"/></a></li>
            </ul>
            <div class="select_box">
                <form:select path="errcode" class="errorcode w200" onchange="statisticsError.list('${msgid}', this.options[this.selectedIndex].value)">
                    <form:option value=""><spring:message code="E0098" text="전체"/></form:option>
                    <form:option value="901"><spring:message code="E0676" text="불확실한 도메인"/></form:option>
                    <form:option value="902"><spring:message code="E0498" text="서버연결 에러"/></form:option>
                    <form:option value="903"><spring:message code="E0499" text="DNS 에러"/></form:option>
                    <form:option value="904"><spring:message code="E0504" text="네트워크 에러"/></form:option>
                    <form:option value="905"><spring:message code="E0509" text="시스템 에러"/></form:option>
                    <form:option value="906"><spring:message code="E0507" text="서버 에러"/></form:option>
                    <form:option value="907"><spring:message code="E0508" text="명령어 에러"/></form:option>
                    <form:option value="908"><spring:message code="E0497" text="불확실한 이메일주소"/></form:option>
                    <form:option value="909"><spring:message code="E0503" text="메일박스 FULL"/></form:option>
                    <form:option value="910"><spring:message code="E0502" text="기타"/></form:option>
                    <form:option value="911"><spring:message code="E0501" text="이메일형식 에러"/></form:option>
                    <form:option value="912"><spring:message code="E0071" text="수신거부"/></form:option>
                    <form:option value="913"><spring:message code="E0506" text="중복에러"/></form:option>
                    <form:option value="914"><spring:message code="E0500" text="차단 도메인"/></form:option>
                    <form:option value="915"><spring:message code="E0496" text="이메일주소 공백"/></form:option>
                </form:select>
            </div>

        </div>
        <!-- content top end -->

        <div class="section add_btn">
            <!-- content start -->
            <div class="article content">
                <!-- content area start -->
                <div class="content_area">
                    <table  width ="100%" height="auto" >
                        <colgroup>
                            <col style="width:80px" />
                            <col style="width:300px" />
                            <col style="width:100%" />
                        </colgroup>
                        <thead >
                        <tr>
                            <th>No</th>
                            <th>field1(E-mail)</th>
                            <th><spring:message code="E0648" text="에러내용"/></th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="list" items="${errorList}">
                            <tr>
                                <td class="">${list.id}</td>
                                <td class="">${list.field1}</td>
                                <td class="">${list.err_exp}</td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty errorList}">
                            <tr>
                                <td colspan="5" align="center"><spring:message code="E0586" text="데이터가 없습니다."/></td>
                            </tr>
                        </c:if>
                        </tbody>
                    </table>
                </div>
                <!-- content area end -->
                <!-- nav start -->
                <div class="page_nav">
                    <c:if test="${empty errorList}">
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
                            <pt:jslink>statisticsError.listpage</pt:jslink>
                        </pt:page>
                    </c:if>
                    <c:if test="${!empty errorList}">
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
                            <pt:jslink>statisticsError.listpage</pt:jslink>
                        </pt:page>
                    </c:if>
                </div>
                <!-- nav end -->
            </div>
            <!-- content end -->
</form:form>
        </div>

    </div>
    <%--<div class="pop_footer">
        <div class="btn_div">
            <button class="close_btn btn2" onclick="statisticsError.closePopup()"><spring:message code="E0282" text="닫기"/></button>
        </div>
    </div>--%>
</div>



























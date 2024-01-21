<%@ page contentType="text/html;charset=utf-8" %>
<%@ page language = "java" %>
<%@ include file = "../inc/common.jsp" %>
<%@ include file="/WEB-INF/jsp/inc/taglib.jspf"%>

<script type="text/javascript" src="/sens-static/js/sysman/actionlog.js"></script>
<script type="text/javascript" src="/sens-static/js/messages/calendar_ko.js"></script><!--calendar, page 꼭 적어야 하는지-->
<script type="text/javascript" src="/sens-static/js/util/page.js"></script>
<script type="text/javascript" src="${staticURL}/sens-static/plugin/jquery/jquery.json-2.4.js"></script>
<script type="text/javascript" src="${staticURL}/sens-static/plugin/jquery/ui/jquery-ui.min.js"></script>

<div class="actionlog">
<!-- section start -->
<!-- top area start -->
<form:form name="actionLogListForm" modelAttribute="actionLogListForm" method="get" id="actionLogListForm">
    <form:hidden path="cpage" id="cpage" value="${ pageInfo.cpage }" />

    <div class="title_box fixed">
        <div class="article top_area">
            <div class="title">
                <h1><spring:message code="E0324" text="사용자 활동로그 조회"/></h1>
                <p><spring:message code="E0325" text="사용자의 활동로그를 조회합니다."/></p>
            </div>
        </div>
    </div>
    <!-- top area end -->
    <div class="content_top fixed">
        <ul class="content_top_btn">
            <li>
                <button type="button" onclick="actionlog.listDown();" class="btn2">
                    <span><spring:message code="E0384" text="검색결과 다운로드"/></span>
                </button>
            </li>

            <li>
                <button type="button" onclick="actionlog.allListDown();" class="btn2" style="margin-left: 5px;">
                    <span><spring:message code="E0723" text="전체 검색결과 다운로드"/></span>
                </button>
            </li>

            <li class="count">
                <span><spring:message code="E0329" text="검색결과 :"/> </span>
                <span class="txt strong red" style="display:inline-block; width: 70px;" id="result_count">0<spring:message code="E0385" text="건"/></span>
            </li>

        </ul>
    </div>

    <div class="section">
    <!-- content start -->
    <div class="article content">
            <!-- content area start -->
            <div class="composer_area">
                <!-- search parameter select start-->
                <table width="100%" border="0" cellpadding="0" cellspacing="1" >
                    <colgroup>
                        <col style="width: 150px">
                        <col style="width: 300px">
                        <col style="width: 150px;"/>
                        <col style="width: auto">
                    </colgroup>
                    <tbody>
                    <tr>
                        <th><spring:message code="E0326" text="검색기간"/></th>
                        <td colspan="3">
                            <form:input path="start_date" title="검색시작일" cssStyle="vertical-align: middle;border:1px solid #dcdcdc;width: 78px;height: 32px" cssClass="hide_x_button w200"/> ~
                            <form:input path="end_date" title="검색종료일" cssStyle="vertical-align: middle;border:1px solid #dcdcdc;width: 78px;height: 32px" cssClass="hide_x_button w200"/>
                        </td>
                    </tr>

                    <tr>
                        <th><spring:message code="E0327" text="메뉴"/></th>
                        <td colspan="3">
                            <form:select path="menu_key" cssStyle="width:455px;" onclick="actionlog.change_cpage();">
                                <form:option value="" selected="selected"><spring:message code="E0256" text="분류없음"/></form:option>
                                <c:forEach items="${actionMenuList}" var="actionMenu">
                                    <form:option value="${actionMenu.menu_key}"><c:out value="${actionMenu.menu}"></c:out></form:option>
                                </c:forEach>
                            </form:select>
                        </td>
                    </tr>


                    <tr>
                        <th><spring:message code="E0001" text="아이디"/></th>
                        <td>
                            <form:input path="userid" title="아이디"/>
                        </td>
                        <th width="150px"><spring:message code="E0328" text="내용"/></th>
                        <td>
                            <form:input path="srch_keyword" title="내용"/>
                        </td>
                    </tr>

                    </tbody>
                </table>
                <!-- search parameter select end-->
                <ul class="txt center mg_t10">
                    <li><button type="button" class="btn1 tbl_ft_btn" onclick="actionlog.search();"><spring:message code="E0321" text="검색"/></button></li>
                </ul>
            </div>
    </div>



<%--                        <div class="select_box">--%>
<%--                            <select class="list_select" onchange="actionlog.change_pagesize(this.value)">--%>
<%--                                <option ${f:isSelected('15',UserInfo.pagesize)} value="15"><spring:message code="E0233" text="15"/></option>--%>
<%--                                <option ${f:isSelected('30',UserInfo.pagesize)} value="30"><spring:message code="E0234" text="30"/></option>--%>
<%--                                <option ${f:isSelected('50',UserInfo.pagesize)} value="50"><spring:message code="E0235" text="50"/></option>--%>
<%--                                <option ${f:isSelected('100',UserInfo.pagesize)} value="100"><spring:message code="E0236" text="100"/></option>--%>
<%--                            </select>--%>
<%--                        </div>--%>

                <!-- content top end -->

        <div class="article content">
            <!-- content area start -->
            <div class="content_area">
                <table  width ="100%" height="auto">
                    <colgroup>
                        <col style="width:150px">
                        <col style="width:130px">
                        <col style="width:130px">
                        <col style="width:250px">
                        <col style="width:auto">
                    </colgroup>
                    <thead class="fixed">
                        <tr>
                            <th class="txt"><spring:message code="E0383" text="로그시간"/></th>
                            <th>IP</th>
                            <th><spring:message code="E0001" text="아이디"/></th>
                            <th><spring:message code="E0327" text="메뉴"/></th>
                            <th><spring:message code="E0431" text="활동내역"/></th>
                        </tr>
                    </thead>
                    <tbody class="search_no" id="tbody_noData">
                    <tr>
                        <c:if test="${ empty logData }">
                        <td colspan="5">
                            <p style="text-align: center"><spring:message code="E0586" text="데이터가 없습니다."/></p>
                        </td>
                        </c:if>
                    </tr>
                    </tbody>
                    <tbody id="log_result">
                    </tbody>
                </table>
            </div>
            <!-- content area end -->

    </div>
    <!-- content end -->
        <div class="page_nav" id="firstPage">
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
                <pt:jslink>actionlog.list</pt:jslink>
            </pt:page>
        </div>
    <div class="page_nav" id="pageInfo">

    </div>

    <div>
        <input type="hidden" name="_csrf" value="768bffd0-cc52-41ff-a2be-c2228b33e3bb" />
    </div>

</form:form>
</div>
</div>
<!-- section end -->

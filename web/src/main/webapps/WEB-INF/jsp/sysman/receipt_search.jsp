<%@ page contentType="text/html;charset=utf-8" %>
<%@ include file = "../inc/common.jsp" %>
<%@ page language = "java" %>
<%@ include file = "/WEB-INF/jsp/inc/taglib.jspf" %>
<script type="text/javascript" src="/sens-static/js/messages/calendar_ko.js"></script><!--calendar, page 꼭 적어야 하는지-->
<script type="text/javascript" src="/sens-static/js/util/page.js"></script>
<script type="text/javascript" src="${staticURL}/sens-static/plugin/jquery/jquery.json-2.4.js"></script>
<script type="text/javascript" src="${staticURL}/sens-static/plugin/jquery/ui/jquery-ui.min.js"></script>
<script type="text/javascript" src="/sens-static/js/sysman/receipt.js"></script>
<link rel="stylesheet" href="/sens-static/css/style.css" />

<div class="receipt">
    <form:form name="ReceiptForm" modelAttribute="ReceiptForm" id="ReceiptForm">
    <form:hidden path="cpage" id="cpage" value="${ pageInfo.cpage}" />
    <form:hidden path="pagesize" />

    <div class="title_box fixed">
        <div class="article top_area">
            <div class="title">
                <h1><spring:message code="E0571" text="수신확인 코드 조회"/></h1>
                <p><spring:message code="E0750" text="수신확인 코드 URL의 msgid와 rcode값을 입력하세요."/></p>
            </div>
        </div>
    </div>

    <div class="content_top fixed">
    </div>

    <div class="section ">
        <div class="article content">
            <div class="composer_area">
                <table width="100%" border="0" cellpadding="0" cellspacing="1">
                    <colgroup>
                        <col style="width:150px;"/>
                        <col style="width: auto;">
                    </colgroup>
                    <tbody>
                    <tr>
                        <th><label for="searchKeywordMsgid"><spring:message code="E0580" text="메세지 아이디"/></label></th>
                        <td colspan="3">
                            <form:input path="searchKeywordMsgid" cssClass="input" id="searchKeywordMsgid"  title="검색 내용" style="width:400px"/>
                        </td>
                    </tr>
                    <tr>
                        <th><label for="searchKeywordRcode"><spring:message code="E0581" text="고유 아이디"/></label></th>
                        <td colspan="3">
                            <form:input path="searchKeywordRcode" cssClass="input" id="searchKeywordRcode" title="검색 내용"/>
                        </td>
                    </tr>
                    </tbody>
                </table>
                <ul class="txt center mg_t10">
                    <button type="button" class="btn1 tbl_ft_btn" onclick="receiptLog.doSearch(1);"><spring:message code="E0321" text="검색"/></button>
                </ul>
            </div>
        </div>

        <div class="article content">
            <!-- content area start -->
            <div class="composer_area">
                <table width="100%" border="0" cellpadding="0" cellspacing="1">
                    <colgroup>
                        <col style="width:150px;">
                        <col style="width:auto;">
                    </colgroup>
                    <tbody id="tbody_noData">
                    <tr>
                        <td colspan="5">
                            <p style="text-align: center"><spring:message code="E0586" text="데이터가 없습니다."/></p>
                        </td>
                    </tr>
                    </tbody>
                    <tbody id="tbody_logList">
                    </tbody>
                </table>
            </div>
        </div>
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
                <pt:jslink>receiptLog.list</pt:jslink>
            </pt:page>
        </div>
        <!-- 페이징 : start -->
        <div class="page_nav" id="pageInfo"></div>
        <!-- 페이징 : end -->
        </form:form>
    </div>
</div>
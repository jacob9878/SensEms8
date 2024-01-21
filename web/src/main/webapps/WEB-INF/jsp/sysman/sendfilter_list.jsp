<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file = "/WEB-INF/jsp/inc/taglib.jspf" %>
<script type="text/javascript" src="/sens-static/js/sysman/sendfilter.js"></script>
<script type="text/javascript" src="${staticURL}/sens-static/js/common/sendfiltersearch.js"></script>
<!-- section start -->
<!-- top area start -->
<form:form modelAttribute="sendFilterForm" id="sendFilterForm" method="get">
    <form:hidden path="cpage" id="cpage" value="${ pageInfo.cpage }" />
    <div class="title_box fixed">
        <div class="article top_area">
            <div class="title">
                <h1><spring:message code="E0523" text="발송차단 설정"/></h1>
                <p><spring:message code="E0108" text="발송차단할 도메인을 관리합니다."/></p>
            </div>
            <div class="search_box">
                <div class="inner">
<%--                    <div class="select_box">--%>
<%--                        <select class="search_opt">--%>
<%--                            <option><spring:message code="E0109" text="도메인명"/></option>--%>
<%--                        </select>--%>
<%--                    </div>--%>
                    <form:input path="srch_keyword" onfocus="sendfiltersearch.init(this)" placeholder="검색어를 입력하세요."/>
                    <button type="button" class="btn1" onclick="sendfilter.doSearch()" >검색</button>
                    <c:if test="${issearch}"><button type="button" id="allview" class="btn2" onclick="sendfilter.viewAll();">전체목록</button></c:if>
                </div>
            </div>
        </div>
    </div>
    <!-- top area end -->

    <!-- content top start -->
    <div class="content_top fixed">
        <ul class="content_top_btn">
            <li><a class="btn2" href="javascript:;" onclick="sendfilter.openAddPopup()"><spring:message code="E0029" text="추가"/></a></li>
            <li><a class="btn2" href="javascript:;" onclick="sendfilter.delete()"><spring:message code="E0030" text="삭제"/></a></li>
        </ul>
        <%--<div class="content_top_opt">
            <button type="button" onclick="common.open_dropMenu();"><img src="../../sens-static/images/top_opt_btn.png" alt="" /></button>
            <div class="drop_menu" style="display:none;" >
                <table>
                    <tbody>
                    <tr>
                        <th scope="row"><span><spring:message code="E0040" text="목록개수"/></span></th>
                        <td>
                            <select onchange="common.change_pagesize(this.value)">
                                <option ${f:isSelected( '15' , UserInfo.pagesize ) } value="15">15</option>
                                <option ${f:isSelected( '30' , UserInfo.pagesize ) } value="30">30</option>
                                <option ${f:isSelected( '50' , UserInfo.pagesize ) } value="50">50</option>
                                <option ${f:isSelected( '100' , UserInfo.pagesize ) } value="100">100</option>
                            </select>
                        </td>
                    </tr>
                    <tr>
                        <th scope="row"><span><spring:message code="E0041" text="목록간격"/></span></th>
                        <td>
                            <select onchange="">
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
            <table  width ="100%" height="auto" >
                <colgroup>
                    <col style="width:35px" />
                    <col style="width:auto" />
                    <col style="width:250px" />
                </colgroup>
                <thead>
                    <tr >
                        <th >
                            <input id="all_check" type="checkbox" class="check_ico" onclick="common.select_all('hostname');"></th>
                        <th ><spring:message code="E0109" text="도메인명"/></th>
                        <th ><spring:message code="E0067" text="등록일"/></th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty sendFilterList}">
                            <tr>
                                <td colspan="2" class="txt center" >
                                    <spring:message code="E0586" text="데이터가 없습니다." />
                                </td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="sendFilterInfo" items="${ sendFilterList }">
                                <tr>
                                    <td class="txt center" >
                                        <input type="checkbox" name="hostname" class="check_ico"  value="${ sendFilterInfo.hostname }">
                                    </td>
                                    <td><c:out value="${ sendFilterInfo.hostname}"></c:out></td>
                                    <td><fmt:formatDate value="${ sendFilterInfo.regdate }" pattern="yyyy-MM-dd HH:mm" /></td>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div> <!-- content area end -->
    </div>

    <!-- content end -->
    <div class="page_nav">
        <c:if test="${empty sendFilterList}">
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
                <pt:jslink>sendfilter.list</pt:jslink>
            </pt:page>
        </c:if>

        <c:if test="${!empty sendFilterList}">
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
                <pt:jslink>sendfilter.list</pt:jslink>
            </pt:page>
        </c:if>
    </div>
</form:form>
<!-- section end -->

<!-- popup start-->
<div id="sendFilterPopup" style="display:none;" title="<spring:message code="E0107" text="발송차단"/> <spring:message code="E0029" text="추가"/>" >
    <form:form modelAttribute="sendFilterForm" method="post" >
        <div class="popup">
            <div class="popup_content" style="height: 200px">
                <div class="popup_title">
                    <span class="close_button" onclick="sendfilter.doCancel();"></span>
                    <h3 class="title"><spring:message code="E0107" text="발송차단"/> <spring:message code="E0029" text="추가"/> </h3>
                </div>
                <div class="popup_body_wrap">
                <div class="popup_body">
                    <table width="100%" border="0" cellpadding="0" cellspacing="1" >
                        <colgroup>
                            <col style="width:150px;">
                            <col style="width:auto;">
                        </colgroup>
                        <tbody>
                        <tr >
                            <th ><spring:message code="E0109" text="도메인명"/></th>
                            <td><form:input path="hostname" id="hostname"/></td>
                            <input type="text" style="display:none"/>
                        </tr>
                        </tbody>
                    </table>

                </div>
                </div>
                <div class="pop_footer">
                    <div class="btn_div">
                        <button type="button" class="btn2" onclick="sendfilter.save()"><spring:message code="E0029" text="추가"/></button>
                        <button type="button" class="btn2" onclick="sendfilter.doCancel();"><spring:message code="E0065" text="취소"/></button>
                    </div>
                </div>
            </div>
        </div>
    </form:form>
</div>
<!-- popup end-->


<%
    /*
        @program : 시스템 관리자 - 첨부파일관리
        @author : jooyoung.LEE
        @since : 2022-03-30
    */
%>
<%@ page pageEncoding="UTF-8"%>
<%@ page import = "com.imoxion.sensems.web.common.*" %>
<%-- TAGLIB INCLUDE --%>
<%@ include file = "/WEB-INF/jsp/inc/taglib.jspf" %>
<script type="text/javascript" src="/sens-static/js/sysman/attach.js"></script>
<script type="text/javascript" src="${staticURL}/sens-static/js/common/attachsearch.js"></script>
<script type="text/javascript" src="${staticURL}/sens-static/plugin/jquery/jquery.json-2.4.js"></script>
<script type="text/javascript" src="${staticURL}/sens-static/plugin/jquery/ui/jquery-ui.min.js"></script>
<form:form id="attachListForm" name="attachListForm" modelAttribute="attachlistForm" action="list.do" method="get">
    <div class="attach_list">

    <div class="title_box fixed">

        <div class="article top_area">
            <div class="title">
                <h1><spring:message code="E0743" text="첨부파일 관리"/></h1>
                <p><spring:message code="E0744" text="첨부파일을 관리할 수 있습니다."/></p>
            </div>
            <div class="search_box">
                <input name="searchText" id="searchText" class="searchText" onfocus="attachsearch.init(this)" type="text" value="${ fileHistory.searchText }" title="<spring:message code='E0321' text='검색'/>" />
                <button type="button" class="btn1" onclick="attachList.searchFile();"><spring:message code="E0321" text="검색"/></button>
                <c:if test="${!empty fileHistory.searchText }">
                    <button type="button" class="btn_bgtxt wt pd5" onclick="attachList.doAllList('list')"><span><spring:message code="E0649" text="전체목록"/></span></button>
                </c:if>
            </div>
        </div>
    </div>
    <form:hidden path="currentPage" id="cpage" value="${imPage.cpage}"/>
    <form:hidden path="pageSize" id="pageSize" value="${ imPage.pageSize }"/>
    <!-- content top start -->
    <div class="content_top fixed">
       <%-- <div class="main_content_top_bt left <c:if test="${ UserSessionInfo.useTextBtn }">textbtn</c:if>">--%>
            <ul class="content_top_btn">
<%--                <li><button type="button" class="btn2" title="<spring:message code="M0023" text="전체선택"/>" onclick="onSelectAll('allCheck')"><span class="bt_caption select"><spring:message code="M0023" text="전체선택"/></span></button></li>--%>
<%--    <li><button type="button" class="btn2" title="<spring:message code="M0023" text="전체선택"/>" onclick="common.select_all('ekey')"><span class="bt_caption select"><spring:message code="M0023" text="전체선택"/></span></button></li>--%>

    <li><a class="btn2" href="javascript:;" onclick="attachList.deleteClick('')"><spring:message code="E0030" text="삭제"/></a></li>
            </ul>
           <div class="select_box">
               <select class="list_select" onchange="common.change_pagesize(this.value)">
                   <option ${f:isSelected('15',UserInfo.pagesize)} value="15"><spring:message code="E0233" text="15"/></option>
                   <option ${f:isSelected('30',UserInfo.pagesize)} value="30"><spring:message code="E0234" text="30"/></option>
                   <option ${f:isSelected('50',UserInfo.pagesize)} value="50"><spring:message code="E0235" text="50"/></option>
                   <option ${f:isSelected('100',UserInfo.pagesize)} value="100"><spring:message code="E0236" text="100"/></option>
               </select>
           </div>
        </div>
    </div>

    <!-- content top end -->

    <div class="section pd_l30">
        <!-- content start -->
        <div class="article content">
            <div class="content_area ">
                <table width ="100%" height="auto"  id="list"  summary="<spring:message code="E0743" text="첨부파일 관리"/>">
                    <colgroup>
                        <col style="width:35px;"/>
                        <col style="width:auto;"/>
                        <col style="width:130px;"/>
                        <col style="width:110px;"/>
                        <col style="width:110px;"/>
                        <col style="width:110px;"/>
                        <col style="width:80px;"/>
                    </colgroup>
                    <thead>
                        <tr>
                            <th scope="col" class="check_ico"><input type="checkbox" name="all_check" id="all_check" class="check_ico" onclick="common.select_all('ekey[]')" title="<spring:message code="M0023" text="전체선택"/>" ></th>
                            <th scope="col"><span class="txt strong title"><spring:message code="E0745" text="파일명"/></span></th>
                        <%--  <th scope="col"><span class="txt strong title">URL</span></th>--%>
                            <th scope="col"><span class="txt strong title"><spring:message code="E0746" text="파일크기"/></span></th>
                            <th scope="col"><span class="txt strong title"><spring:message code="E0747" text="시작일"/></span></th>
                            <th scope="col"><span class="txt strong title"><spring:message code="E0748" text="만료일"/></span></th>
                            <th scope="col"><span class="txt strong title"><spring:message code="E0749" text="다운횟수"/></span></th>
                            <th scope="col" class="txt center" ><span><spring:message code="E0030" text="삭제"/></span></th>
                        </tr>
                    </thead>
                    <c:choose>
                        <c:when test="${ empty fileHistory.fileList }">
                            <tbody  class="search_no">
                                <tr>
                                    <td colspan="6" class="txt center">
                                         <spring:message code="E0586" text="데이터가 없습니다."/>
                                    </td>
                                </tr>
                            </tbody>
                        </c:when>
                    <c:otherwise>
                        <tbody>
                        <c:forEach var="weblink" items="${ fileHistory.fileList }" varStatus="i">
                            <tr>
                                <td class="check_ico">
                                    <input type="checkbox" name="ekey[]"  value="${ weblink.ekey }" title="<spring:message code="E0175" text="선택"/>" >
                                </td>
                                <td>
                                    <a href="javascript:;" onclick="attachList.downLoad('${ weblink.ekey }')">
                                    <span class="txt">${ weblink.file_name }</span>
                                    </a>
                                </td>
<%--                                <td><span title="${ weblink.w_url }">${ weblink.w_url }</span></td>--%>
                                <td class="size">${f:myByteFormat(weblink.file_size,2)}</td>
                                <td><fmt:formatDate value="${ weblink.regdate }" pattern="yyyy-MM-dd" /></td>
                                <td><input style="width:80px" name="expire_date" value="<fmt:formatDate value="${ weblink.expire_date }" pattern="yyyy-MM-dd"/>" onchange="attachList.expireDateUpdate('${ weblink.ekey }', this.value)"/></td>
                                <td><span class="txt">${weblink.down_count}</span></td>
                                <td class="txt center">
                                    <button type="button" class="btn3" title="<spring:message code="E0030" text="삭제"/>" onclick="attachList.deleteClick('${ weblink.ekey }')"><span class="blind"><spring:message code="E0030" text="삭제"/></span></button>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </c:otherwise>
                </c:choose>

            </table>
        </div>
    </div>
    <div class="page_nav">
        <c:if test="${ empty fileHistory.fileList }">
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
            <pt:jslink>attachList.list</pt:jslink>
        </pt:page>
        </c:if>

        <c:if test="${ !empty fileHistory.fileList }">
            <pt:page>
                <pt:cpage>
                    ${imPage.cpage }
                </pt:cpage>
                <pt:pageSize>
                    ${ imPage.pageSize }
                </pt:pageSize>
                <pt:total>
                    ${ imPage.total }
                </pt:total>
                <pt:jslink>attachList.list</pt:jslink>
            </pt:page>
        </c:if>
    </div>
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    <!-- 메인 컨텐츠 : end -->
<%--
</form>--%>
</form:form>
</div>
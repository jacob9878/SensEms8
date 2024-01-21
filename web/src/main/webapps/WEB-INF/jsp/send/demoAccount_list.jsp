<%@ page contentType="text/html;charset=utf-8" %>
<%@ page language = "java" %>
<%@ include file = "../inc/common.jsp" %>
<script type="text/javascript" src="/sens-static/js/send/demoaccount.js"></script>
<script type="text/javascript" src="${staticURL}/sens-static/js/common/demoaccountsearch.js"></script>
<!-- section start -->
<!-- top area start -->
<form:form  name="demoAccountForm" modelAttribute="demoAccountForm" id="demoAccount" action="list.do"  method="get">
    <form:hidden path="cpage" id="cpage" value="${ pageInfo.cpage }" />
    <input type="hidden" id="ori_email" value="">
<div class="title_box fixed">
    <div class="article top_area">
        <div class="title">
            <h1><spring:message code="E0161" text="테스트계정 관리"/></h1>
            <p><spring:message code="E0162" text="테스트계정을 관리할 수 있습니다."/></p>
        </div>
        <div class="search_box">
            <div class="inner">
                <form:input path="srch_keyword" onfocus="demoaccountsearch.init(this)" name="srch_keyword" placeholder="검색어를 입력하세요."/>
                <button type="button" class="btn1" onclick="demoaccount_list.search()" >검색</button>
                <c:if test="${issearch}"><button type="button" id="allview" class="btn2" onclick="demoaccount_list.viewAll();">전체목록</button></c:if>

            </div>
        </div>
    </div>
</div>
<!-- top area end -->

<!-- content top start -->
<div class="content_top fixed">
    <ul class="content_top_btn">
        <li><a class="btn2" href="javascript:;" onclick="demoaccount_list.open_addPopup()"><spring:message code="E0029" text="추가"/></a></li>
        <li><a class="btn2" href="javascript:;" onclick="demoaccount_list.delete()"><spring:message code="E0030" text="삭제"/></a></li>
    </ul>

    <%--<div class="content_top_opt">
        <button type="button" onclick="common.open_dropMenu();"><img src="../../sens-static/images/top_opt_btn.png" alt="" /></button>
        <div class="drop_menu" style="display:none;" >
            <table summary="목록화면입니다">
                <caption>목록화면입니다</caption>
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
                    <th scope="row"><span><spring:message code="E0041" text="목록 간격"/></span></th>
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
                    <col style="width:150px" />
                </colgroup>

                <thead>
                <tr >
                    <th class="check_ico" >
                        <input id="all_check" type="checkbox" title="전체선택" onclick="common.select_all('ukeys');">
                    </th>
                    <th ><spring:message code="E0022" text="E-Mail"/></th>
                    <th ><spring:message code="E0067" text="등록일"/></th>
                </tr>
                </thead>
                <tbody>

                <c:if test="${ empty demoAccountList }">
                    <tr>
                        <td colspan="3" class="txt center">
                            <spring:message code="E0075" text="등록된 데이터가 없습니다." />
                        </td>
                    </tr>
                </c:if>

                <c:forEach varStatus="i" var="demoAccount" items="${ demoAccountList }">
                    <fmt:formatDate value="${demoAccount.regdate}" pattern="yyyy-MM-dd" var="regdate" />
                    <fmt:formatDate value="${demoAccount.regdate}" pattern="HH:mm" var="time" />
                    <tr>
                        <td class="check_ico" >
                            <input type="checkbox" name="ukeys" value="${demoAccount.ukey},${demoAccount.email}" >
                        </td>
                        <td style="cursor: pointer;" onclick="demoaccount_list.open_editPopup('${demoAccount.ukey}',${demoAccount.flag},'${demoAccount.email}')"><c:out value="${demoAccount.email}"></c:out></td>
                        <td><c:out value="${regdate}"></c:out> <c:out value="${time}"></c:out></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
        <!-- content area end -->
    </div>
    <!-- content end -->

    <div class="page_nav">
        <c:if test="${ empty demoAccountList }">
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
            <pt:jslink>demoaccount_list.list</pt:jslink>
        </pt:page>
        </c:if>
        <c:if test="${ !empty demoAccountList }">
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
                <pt:jslink>demoaccount_list.list</pt:jslink>
            </pt:page>
        </c:if>
    </div>

</form:form>
</div>
<!-- section end -->


<!-- popup start-->
<div id="addDemoAccountLayer" style="display:none;" title="<spring:message code="E0161" text="테스트 계정관리"/> <spring:message code="E0029" text="추가"/>" >

        <div class="popup">
            <div class="popup_content" style="height: 240px">
                <div class="popup_title">
                    <span class="close_button" onclick="demoaccount_list.close_popup('add');"></span>
                    <h3 class="title"><spring:message code="E0161" text="테스트 계정관리"/> <spring:message code="E0029" text="추가"/> </h3>
                </div>
                <div class="popup_body_wrap">
                <div class="popup_body">
                    <table width="100%" border="0" cellpadding="0" cellspacing="1" >
                        <colgroup>
                            <col style="width:150px;">
                            <col style="width:auto;">
                        </colgroup>
                        <tbody>
                        <tr>
                            <th><spring:message code="E0022" text="E-Mail"/></th>
                            <td ><input type="text" id="email"></td>
                        </tr>
                        <tr>
                            <th><spring:message code="E0163" text="기본 테스트 계정으로 설정하시겠습니까?"/></th>
                            <td><div>
                                <label><input type="radio" name="testAccount" value="1"><spring:message code="E0164" text="예"/></label>
                                <label><input type="radio" name="testAccount" value="0" checked="checked"><spring:message code="E0165" text="아니오"/></label>
                            </div>
                                <span class="care_txt"><strong class="txt red">테스트 메일 발송 시 테스트 발송 명단에 기본 선택됩니다.</strong></span>
                            </td>
                        </tr>
                        </tbody>
                    </table>
<%--                    <div style="text-align:center; padding-top:15px;">--%>
<%--                        <p><spring:message code="E0163" text="기본 테스트 계정으로 설정하시겠습니까?"/></p>--%>
<%--                        <label><input type="radio" name="testAccount" value="1"><spring:message code="E0164" text="예"/></label>--%>
<%--                        <label><input type="radio" name="testAccount" value="0" checked="checked"><spring:message code="E0165" text="아니오"/></label>--%>
<%--                    </div>--%>


                </div>
                </div>
                <div class="pop_footer">
                    <div class="btn_div">
                        <button type="button" class="btn4" onclick="demoaccount_list.add();"><spring:message code="E0029" text="추가"/></button>
                        <button type="button" class="btn4" onclick="demoaccount_list.close_popup('add');"><spring:message code="E0065" text="취소"/></button>
                    </div>
                </div>
            </div>
        </div>

</div>



<div id="editDemoAccountLayer" style="display:none;" title="<spring:message code="E0161" text="테스트 계정관리"/> <spring:message code="E0128" text="수정"/>" >

        <div class="popup">
            <div class="popup_content" style="height: 240px">
                <div class="popup_title">
                    <span class="close_button" onclick="demoaccount_list.close_popup('edit');"></span>
                    <h3 class="title"><spring:message code="E0161" text="테스트 계정관리"/> <spring:message code="E0128" text="수정"/></h3>
                </div>
                <div class="popup_body_wrap">
                <div class="popup_body">
                    <input type="hidden" id="edit_ukey">
                    <table width="100%" border="0" cellpadding="0" cellspacing="1" >
                        <colgroup>
                            <col style="width:150px;">
                            <col style="width:auto;">
                        </colgroup>
                        <tbody>
                        <tr>
                            <th><spring:message code="E0022" text="E-Mail"/></th>
                            <td><input type="text" id="edit_email" value=""></td>
                        </tr>
                        <tr>
                            <th><spring:message code="E0163" text="기본 테스트 계정으로 설정하시겠습니까?"/></th>
                            <td><div>
                                <label><input type="radio" name="editTestAccount" value="1"><spring:message code="E0164" text="예"/></label>
                                <label><input type="radio" name="editTestAccount" value="0" checked="checked"><spring:message code="E0165" text="아니오"/></label>
                            </div>
                                <span class="care_txt"><strong class="txt red">테스트 메일 발송 시 테스트 발송 명단에 기본 선택됩니다.</strong></span>
                            </td>
                        </tr>
                        </tbody>
                    </table>
<%--                    <div style="text-align:center; padding-top:15px;">--%>
<%--                        <p><spring:message code="E0163" text="기본 테스트 계정으로 설정하시겠습니까?"/></p>--%>
<%--                        <label><input type="radio" name="editTestAccount" value="1"><spring:message code="E0164" text="예"/></label>--%>
<%--                        <label><input type="radio" name="editTestAccount" value="0"><spring:message code="E0165" text="아니오"/></label>--%>
<%--                    </div>--%>

                </div>
                </div>
                <div class="pop_footer">
                    <div class="btn_div">
                        <button type="button" class="btn4" onclick="demoaccount_list.edit();"><spring:message code="E0128" text="수정"/></button>
                        <button type="button" class="btn4" onclick="demoaccount_list.close_popup('edit');"><spring:message code="E0065" text="취소"/></button>
                    </div>
                </div>
            </div>
        </div>

</div>

<!-- popup end-->

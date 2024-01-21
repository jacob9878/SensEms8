<%@ include file = "../inc/common.jsp" %>
<script type="text/javascript">
    $(document).ready(function(){
        $(document).on("change","input[name='recvid']",function(){
            $("input[name='recvid']").each(function(){
                $(this).prop("checked",false);
            });
            $(this).prop("checked",true);
        })
    })
</script>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<form:form modelAttribute="receiverListForm" name="receiverListForm" method="get" action="receiverList.do">
    <form:hidden path="msgid"/>
    <form:hidden path="cpage" value="${pageInfo.cpage }"/>
<div class="w_pop wrap">
    <div class="w_content">
        <div class="popup_title over_text">
            <div >
                <h3 class="title"><spring:message code="E0526" text="수신자 목록보기"/></h3>
            </div>

            <div class="search_box">
                <div class="inner">
                    <div class="select_box">
                        <form:select path="srch_type" class="search_opt" >
                            <form:option value="01">field1(E-mail)</form:option>
                            <form:option value="02">field2</form:option>
                        </form:select>
                    </div>
                    <form:input path="srch_keyword" id="srch_keyword"  onfocus="recvpopupsearch.init(this)" value="" placeholder="검색어를 입력하세요."/>
                    <button type="button" class="search_btn btn1" onclick="receiverListPopup.search();">검색</button>
                    <c:if test="${issearch}"><button type="button" id="allview" class="btn2" onclick="receiverListPopup.viewAll();">전체목록</button></c:if>
                </div>
            </div>
            <div  style="text-align: right; color: #999999; ">E-mail 검색 시 정확한 이메일을 입력해주세요.</div>
        </div>
    </div>

        <!-- content top start -->
        <div class="content_top fixed">
            <ul class="content_top_btn">
                <li><a class="btn2" href="javascript:;" onclick="receiverListPopup.reSend();"><spring:message code="E0742" text="재작성"/></a></li>
            </ul>
            <div class="content_top_opt">
<%--                <button type="button" onclick="common.open_dropMenu();"><img src="${StaticURL}/sens-static/images/top_opt_btn.png"></button>--%>
                <div class="select_box" >

<%--                    <table summary="<spring:message code="E0527" text="수신자 목록화면"/>">--%>
<%--                        <tbody>--%>
<%--                        <tr height="30px">--%>
<%--                            <th scope="row"><span><spring:message code="" text="목록 개수"/></span></th>--%>
<%--                            <td>--%>
                                <select class="list_select" onchange="common.change_pagesize(this.value)">
                                    <option ${f:isSelected('15',UserInfo.pagesize)} value="15"><spring:message code="E0233" text="15"/></option>
                                    <option ${f:isSelected('30',UserInfo.pagesize)} value="30"><spring:message code="E0234" text="30"/></option>
                                    <option ${f:isSelected('50',UserInfo.pagesize)} value="50"><spring:message code="E0235" text="50"/></option>
                                    <option ${f:isSelected('100',UserInfo.pagesize)} value="100"><spring:message code="E0236" text="100"/></option>
                                </select>
<%--                            </td>--%>
<%--                        </tr>--%>
<%--                        <tr height="30px">--%>
<%--                            <th scope="row"><span><spring:message code="E0041" text="목록 간격"/></span></th>--%>
<%--                            <td>--%>
<%--                                <select>--%>
<%--                                    <option><spring:message code="E0043" text="좁게"/></option>--%>
<%--                                    <option><spring:message code="E0045" text="보통"/></option>--%>
<%--                                    <option><spring:message code="E0044" text="넓게"/></option>--%>
<%--                                </select>--%>
<%--                            </td>--%>
<%--                        </tr>--%>
<%--                        </tbody>--%>
<%--                    </table>--%>
                </div>
            </div>
        </div>
        <!-- content top end -->

        <div class="section add_btn">
            <!-- content start -->
            <!-- <p class="over_text">데이터수 : 10개 (미리보기 데이터는 최대 100개까지만 조회됩니다.)</p> -->
            <div class="article content">
                <!-- content area start -->
                <div class="content_area">
                    <table  width ="100%" height="auto" >
                        <colgroup>
                            <col style="width:35px" />
                            <col style="width:80px" />
                            <col style="width:auto" />
                            <col style="width:120px" />
                            <col style="width:150px" />
                            <col style="width:150px" />
                            <col style="width:50px" />
                            <col style="width:150px" />
                            <col style="width:60px" />
                        </colgroup>
                        <thead >
                        <tr>
                            <th class="txt center">
                                <%--<input type="checkbox" id="all_check" onclick="common.select_all('recvid');" title="<spring:message code="E0391" text="전체선택"/>">--%>
                            </th>
                            <th class="txt center">No</th>
                            <th>field1(E-mail)</th>
                            <th>field2</th>
                            <th>field3</th>
                            <th>field4</th>
                            <th class="txt center"><spring:message code="E0528" text="결과"/></th>
                            <th class="txt center"><spring:message code="E0529" text="확인 시간"/></th>
                            <th class="txt center"><spring:message code="E0530" text="재발송"/></th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="list" items="${recvList}">
                            <tr>
                                <td class="txt center">
                                    <input type="checkbox" name="recvid" value="${list.id}" title="선택">
                                </td>
                                <td class="txt center">${list.id}</td>
                                <td class="over_text">${list.field1}</td>
                                <td class="over_text">${list.field2}</td>
                                <td class="over_text">${list.field3}</td>
                                <td class="over_text">${list.field4}</td>
                                <td class="txt center">
                                    <c:choose>
                                        <c:when test="${list.success eq '1'}">
                                            <spring:message code="E0456" text="성공"/>
                                        </c:when>
                                        <c:when test="${list.success eq '2'}">
                                            <spring:message code="E0763" text="-"/>
                                        </c:when>
                                        <c:otherwise>
                                            <spring:message code="E0457" text="실패"/>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="list_2line txt center">
                                    <c:if test="${list.recv_count ne '0'}">
                                    <fmt:parseDate value="${list.recv_time}" pattern="yyyyMMddHHmmss" var="regdate"/>
                                    <fmt:formatDate value="${regdate}" pattern="yyyy-MM-dd"/>
                                    <span><fmt:formatDate value="${regdate}" pattern="HH:mm"/></span>
                                    </c:if>
                                </td>
                                <td class="txt center">
                                    <c:if test="${list.is_resend eq '1'}">
                                        <span style="color: green">v</span>
                                    </c:if>
                                    </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty recvList}">
                            <tr>
                                <td colspan="9" align="center"><spring:message code="E0586" text="데이터가 없습니다."/></td>
                            </tr>
                        </c:if>
                        </tbody>
                    </table>
                </div>
                <!-- content area end -->
            </div>
            <!-- content end -->

            <!-- nav start -->
            <div class="page_nav">
                <c:if test="${empty recvList}">
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
                        <pt:jslink>receiverListPopup.list</pt:jslink>
                    </pt:page>
                </c:if>
                <c:if test="${!empty recvList}">
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
                    <pt:jslink>receiverListPopup.list</pt:jslink>
                </pt:page>
                </c:if>
            </div>
            <!-- nav end -->
        </div>
        <!-- section end -->
</form:form>
        </div>
    </div>
<%--<div class="pop_footer">
    <div class="btn_div">
        <button class="close_btn btn2" type="button" onclick="receiverListPopup.closeReceiverList();" ><spring:message code="E0282" text="닫기"/></button>
    </div>
</div>--%>
</div>
<script type="text/javascript">
    function closeWin() {
        top.opener.location.href='/mail/result/sendList.do';
        top.window.opener = top;
        top.window.open('','_parent','');
        top.window.close();
    }
</script>

<%--
  Created by IntelliJ IDEA.
  User: 신주현
  Date: 2022-03-28
  Time: 오전 10:29
  To change this template use File | Settings | File Templates.
--%>
<%@ include file = "../inc/common.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
        <div class="w_pop wrap">
            <div class="w_content">
                <div class="popup_title over_text">
                    <div >
                        <h3 class="title"><spring:message code="E0664" text="수신확인 통계 목록보기"/></h3>
                    </div>
                    <form:form method="get" name="ReceiverListForm" modelAttribute="receiverListForm" action="statisticsReceiptList.do">
                    <form:hidden path="msgid"/>
                    <form:hidden path="cpage" value="${pageInfo.cpage }"/>
                    <div class="search_box">
                        <div class="inner">
                            <div class="select_box">
                                <form:select path="srch_type" class="search_opt" >
                                    <form:option value="01">field1(E-mail)</form:option>
                                    <form:option value="02">field2</form:option>
                                </form:select>
                            </div>
                            <form:input path="srch_keyword" id="srch_keyword" onfocus="receiptpopupsearch.init(this)" value="" placeholder="검색어를 입력하세요."/>
                            <button type="button" class="btn1" onclick="receiverListPopup.search();"><spring:message code="E0321" text="검색"/></button>
                            <c:if test="${issearch}"><button type="button" id="allview" class="btn2" onclick="receiverListPopup.viewAll();"><spring:message code="E0649" text="전체목록"/></button></c:if>
                        </div>
                    </div>
                    <div  style="text-align: right; color: #999999; "><spring:message code="E0650" text="E-mail 검색 시 정확한 이메일을 입력해주세요."/></div>
                </div>

                <!-- content top start -->
                <div class="content_top fixed">
                    <ul class="content_top_btn">
                        <c:if test="${recvDate ne null}">
                        <form:hidden path="recv_date" value="${form.recv_date}"/>
                        </c:if>
                        <form:hidden path="receipt" value="${form.receipt}"/>
                        <form:hidden path="unreceipt" value="${form.unreceipt}"/>
                        <c:if test="${!empty recvMessageIDBean}">
                            <li><a class="btn2" href="javascript:;" onclick="resultList.listDownReceiptList('${form.msgid}','${form.receipt}')"><spring:message code="E0073" text="목록저장"/></a></li>
                        </c:if>
                        <c:if test="${empty recvMessageIDBean}">
                            <li><a class="btn2" href="javascript:;" onclick="resultList.nodataList()"><spring:message code="E0073" text="목록저장"/></a></li>
                        </c:if>
<%--                        <li><a class="btn2" href="">재발신</a></li>--%>
                    </ul>
                    <div class="select_box">
<%--                        <select class="errorcode w150" name="recv_count" id="recv_count" onchange="receiverListPopup.search(this.value);">>--%>
<%--                            <option selected value="">전체</option>--%>
<%--                            <option value="4">수신확인 4회이상</option>--%>
<%--                            <option value="3">수신확인 3회이상</option>--%>
<%--                            <option value="2">수신확인 2회이상</option>--%>
<%--                            <option value="1">수신확인 1회이상</option>--%>
<%--                        </select>--%>
                        <form:select path="recv_count" class="search_opt" name="recv_count" id="recv_count" onchange="receiverListPopup.recvsearch(this.value);">
                            <form:option value=""><spring:message code="E0098" text="전체"/></form:option>
                            <c:if test="${recvDate eq null}"><form:option value="0"><spring:message code="E0639" text="수신 미확인"/></form:option></c:if>
                            <form:option value="1"><spring:message code="E0665" text="수신확인 1회이상"/></form:option>
                            <form:option value="2"><spring:message code="E0666" text="수신확인 2회이상"/></form:option>
                            <form:option value="3"><spring:message code="E0667" text="수신확인 3회이상"/></form:option>
                            <form:option value="4"><spring:message code="E0668" text="수신확인 4회이상"/></form:option>
                        </form:select>

<%--                        <button type="button" onclick="common.open_dropMenu();"><img src="${StaticURL}/sens-static/images/top_opt_btn.png"></button>  <!-- <img src="${StaticURL}/sens-static/images/top_opt_btn.png"> -->--%>
                            <table summary="<spring:message code="E0527" text="수신자 목록화면"/>">
                                <tbody>
<%--                                    <tr height="30px">--%>
<%--                                        <th scope="row"><span><spring:message code="" text="목록 개수"/></span></th>--%>
<%--                                        <td>--%>
                                            <select class="list_select" onchange="common.change_pagesize(this.value)">
                                                <option ${f:isSelected('15',UserInfo.pagesize)} value="15"><spring:message code="E0233" text="15"/></option>
                                                <option ${f:isSelected('30',UserInfo.pagesize)} value="30"><spring:message code="E0234" text="30"/></option>
                                                <option ${f:isSelected('50',UserInfo.pagesize)} value="50"><spring:message code="E0235" text="50"/></option>
                                                <option ${f:isSelected('100',UserInfo.pagesize)} value="100"><spring:message code="E0236" text="100"/></option>
                                            </select>
<%--                                        </td>--%>
<%--                                    </tr>--%>
<%--                                    <tr height="30px">--%>
<%--                                        <th scope="row"><span><spring:message code="E0041" text="목록 간격"/></span></th>--%>
<%--                                        <td>--%>
<%--                                            <select>--%>
<%--                                                <option><spring:message code="E0043" text="좁게"/></option>--%>
<%--                                                <option><spring:message code="E0045" text="보통"/></option>--%>
<%--                                                <option><spring:message code="E0044" text="넓게"/></option>--%>
<%--                                            </select>--%>
<%--                                        </td>--%>
<%--                                    </tr>--%>
                                </tbody>
                            </table>
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
                                    <col style="width:80px" />
                                    <col style="width:auto" />
                                    <col style="width:180px;" />
                                    <col style="width:180px" />
                                    <col style="width:180px" />
                                    <col style="width:180px" />
                                    <col style="width:80px" />
                                </colgroup>
                                <thead >
                                    <tr>
                                        <th><spring:message code="E0669" text="No"/></th>
                                        <th>field1(E-mail)</th>
                                        <th>field2</th>
                                        <th>field3</th>
                                        <th>field4</th>
                                        <th class="txt center"><spring:message code="E0529" text="확인 시간"/></th>
                                        <th><spring:message code="E0593" text="확인 횟수"/></th>
                                    </tr>
                                </thead>
                                <tbody>
                                <c:if test="${empty recvMessageIDBean}">
                                    <tr>
                                        <td colspan="7" align="center"><spring:message code="E0586" text="데이터가 없습니다."/></td>
                                    </tr>
                                </c:if>

                                    <c:forEach var = "recvMessageIDBean" items="${recvMessageIDBean}">
                                        <c:set var = "i" value="${i+1}"/>
                                    <tr>
                                        <td class="over_text">${recvMessageIDBean.id}</td>
                                        <td class="over_text">${recvMessageIDBean.field1}</td>
                                        <td class="over_text">${recvMessageIDBean.field2}</td>
                                        <td class="over_text">${recvMessageIDBean.field3}</td>
                                        <td class="over_text">${recvMessageIDBean.field4}</td>
                                        <td class="list_2line txt center">
                                            <fmt:parseDate value="${recvMessageIDBean.recv_time}" pattern="yyyyMMddHHmmss" var="recv_time"/>
                                            <fmt:formatDate value="${recv_time}" pattern="yyyy-MM-dd HH:mm"/>
                                        </td>
                                        <td style="text-align: center">${recvMessageIDBean.recv_count}</td>
                                    </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                        <!-- content area end -->
                    <!-- content end -->
                        </form:form>
                    <!-- nav start -->
                    <div class="page_nav">
                        <c:if test="${ empty recvMessageIDBean }">
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

                        <c:if test="${ !empty recvMessageIDBean }">
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
                </div>

            </div>

            <%--<div class="pop_footer">
            <div class="btn_div">
                <button class="close_btn btn2" onclick="window.close();"><spring:message code="E0282" text="닫기"/></button>
            </div>
            </div>--%>
        </div>
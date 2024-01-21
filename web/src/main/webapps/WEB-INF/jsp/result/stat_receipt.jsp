<%@ page contentType="text/html;charset=utf-8" %>
<%@ page language="java" %>
<%@ include file="../inc/common.jsp" %>
<!-- Resources -->
<script src="${staticURL}/sens-static/plugin/amchart4/core.js"></script>
<script src="${staticURL}/sens-static/plugin/amchart4/charts.js"></script>
<script src="${staticURL}/sens-static/plugin/amchart4/themes/material.js"></script>
<script src="${staticURL}/sens-static/plugin/amchart4/themes/animated.js"></script>
<script>
</script>
<%--
  Created by IntelliJ IDEA.

  User: 신주현
  Date: 2022-03-22
  Time: 오전 10:45
  To change this template use File | Settings | File Templates.
--%>
                    <div class="graph_top">
                        <c:if test="${empty message}">
                            <ul>
                                <li><button class="btn2" onclick="send_resultList.doresend('${msgid}','rcpt');"><spring:message code="E0672" text="수신 확인자에게 다시 발송"/></button></li>
                                <li><button class="btn2" onclick="send_resultList.doresend('${msgid}','norcpt');"><spring:message code="E0673" text="수신 미확인자에게 다시 발송"/></button></li>
                            </ul>
                        </c:if>
                    </div>

                    <!-- content start -->
                    <div class="article content  mg_b30 t_br2">
                        <!-- content area start -->
                        <div class="graph">
                        <div class="content_area">
                            <table  width ="100%" height="auto" >
                                <colgroup>
                                    <col style="width:100px" />
                                    <col style="width:auto" />
                                    <col style="width:90px" />
                                    <col style="width:90px" />
                                    <col style="width:90px" />
                                    <col style="width:80px" />
                                </colgroup>
                                <thead >
                                    <tr>
                                        <th class="txt center"><spring:message code="E0634" text="날짜"/></th>
                                        <th class="txt"><spring:message code="E0633" text="수신확인율"/></th>
                                        <th class="txt "><spring:message code="E0576" text="수신확인"/></th>
                                        <th class="txt center"><spring:message code="E0452" text="상세보기"/></th>
                                        <th class="txt center"><spring:message code="E0635" text="목록보기"/></th>
                                        <th class="txt center"><spring:message code="E0636" text="목록다운"/></th>
                                    </tr>
                                </thead>
                                <tbody>
                                <c:choose>
                                    <c:when test="${!empty message}">
                                        <td colspan="6" align="center"><spring:message code="E0586" text="데이터가 없습니다."/></td>
                                    </c:when>
                                    <c:otherwise>
                                    <c:forEach var="recvMessageIDBean" items="${recvMessageIDBean}">
                                        <fmt:parseDate value="${recvMessageIDBean.recv_date}" pattern="yyyyMMdd" var="recv_date"/>
                                    <tr>
                                        <td class="txt center"><span><fmt:formatDate value="${recv_date}" pattern="yyyy-MM-dd"/></span></td>
                                        <td class="graph_Wlong"><span class="stick" style="width:${(100*recvMessageIDBean.recv_count)/totalcount}%"></span></td>
                                        <td class="txt"><span>${recvMessageIDBean.recv_count}</span>건</td>
                                        <td class="txt center">
                                            <button title="수신확인 통계 상세" class="btn3" onclick="window.open('/mail/result/statisticsReceiptDetail.do?msgid=${emsbean.msgid}&recv_date=${recvMessageIDBean.recv_date}','수신확인 상세보기','width=800px,height=520px,location=no,status=no,scrollbars=yes,menubar=no,resizable=yes');" type="button">
                                                <span><spring:message code="E0452" text="상세보기"/></span>
                                            </button>
                                        </td>
                                        <td class="txt center">
                                            <button title="수신확인 통계 목록" class="btn3" onclick="window.open('/mail/result/statisticsReceiptList.do?msgid=${emsbean.msgid}&recv_date=${recvMessageIDBean.recv_date}','수신확인 목록보기','width=1200,height=600px,location=no,status=no,scrollbars=yes,menubar=no,resizable=yes');" type="button">
                                                <span><spring:message code="E0635" text="목록보기"/></span>
                                            </button>
                                        </td>
                                        <td class="txt center"><button class="btn3" onclick="resultList.downReceiptList('${emsbean.msgid}','${recvMessageIDBean.recv_date}')"><spring:message code="E0638" text="다운"/></button></td>
                                    </tr>
                                    </c:forEach>
                                        <tr class="graph_total">
                                            <td class="txt center now"><span><spring:message code="E0637" text="총 확인"/></span></td>
                                            <td class="graph_Wlong"><span class="stick" style="width:${(100*receipt)/totalcount}%"></span></td>
                                            <td class="txt"><span>${receipt}</span><spring:message code="E0385" text="건"/></td>
                                            <td class="txt center">
                                                <button title="수신확인 통계 상세" class="btn4" onclick="window.open('/mail/result/statisticsReceiptDetail.do?msgid=${emsbean.msgid}&receipt=${receipt}','수신확인 상세보기','width=800px,height=520px,location=no,status=no,scrollbars=yes,menubar=no,resizable=yes');" type="button">
                                                    <span><spring:message code="E0452" text="상세보기"/></span>
                                                </button>
                                            </td>
                                            <td class="txt center">
                                                <button title="수신확인 통계 목록" class="btn4" onclick="window.open('/mail/result/statisticsReceiptList.do?msgid=${emsbean.msgid}&receipt=${receipt}&recv_count=1','수신확인 목록보기','width=1200px,height=600px,location=no,status=no,scrollbars=yes,menubar=no,resizable=yes');" type="button">
                                                    <span><spring:message code="E0635" text="목록보기"/></span>
                                                </button>
                                            </td>
                                            <c:if test="${unreceipt ne 0}">
                                            <td class="txt center"><button class="btn4" onclick="resultList.listDownReceiptList('${emsbean.msgid}','${receipt}')"><spring:message code="E0638" text="다운"/></button></td>
                                            </c:if>
                                            <c:if test="${unreceipt eq 0}">
                                                <td class="txt center"><button class="btn4" onclick="resultList.nodataList()"><spring:message code="E0638" text="다운"/></button></td>
                                            </c:if>
                                        </tr>
                                        <tr class="graph_total2">
                                            <td class="txt center now"><span><spring:message code="E0639" text="수신 미확인"/></span></td>
                                            <td class="graph_Wlong"><span class="stick" style="width:${(100*unreceipt)/totalcount}%"></span></td>
                                            <td class="txt"><span>${unreceipt}</span><spring:message code="E0385" text="건"/></td>
                                            <td class="txt center">
                                            </td>
                                            <td class="txt center">
                                                <button title="수신확인 통계 목록" class="btn4" onclick="window.open('/mail/result/statisticsReceiptList.do?msgid=${emsbean.msgid}&unreceipt=${unreceipt}&recv_count=0','수신미확인 목록','width=1200px,height=600px,location=no,status=no,scrollbars=yes,menubar=no,resizable=yes');" type="button">
                                                    <span><spring:message code="E0635" text="목록보기"/></span>
                                                </button>
                                            </td>
                                            <c:if test="${unreceipt ne 0}">
                                            <td class="txt center"><button class="btn4" onclick="resultList.listDownReceiptList('${emsbean.msgid}')"><spring:message code="E0638" text="다운"/></button></td>
                                            </c:if>
                                            <c:if test="${unreceipt eq 0}">
                                                <td class="txt center"><button class="btn4" onclick="resultList.nodataList()"><spring:message code="E0638" text="다운"/></button></td>
                                            </c:if>
                                        </tr>
                                    </c:otherwise>
                                </c:choose>
                                </tbody>
                            </table>
                        </div>
                        <!-- content area end -->
                    </div>
                    <!-- content end -->

                    </div>

                </div>
                <!-- section end -->
            </div>
        </div>
    </body>
</html>
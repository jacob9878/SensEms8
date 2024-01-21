<%@ page contentType="text/html;charset=utf-8" %>
<%@ page language="java" %>
<%@ include file="../inc/common.jsp" %>
<%--
  Created by IntelliJ IDEA.

  User: 신주현
  Date: 2022-03-22
  Time: 오전 10:45
  To change this template use File | Settings | File Templates.
--%>
                <form:form method="post" name="LinkListForm" id="LinkListForm" modelAttribute="linkListForm" action="staticLink.do">
                    <form:hidden path="msgid"/>
                    <form:hidden path="cpage" value="${pageInfo.cpage }"/>
                    <input type="hidden" name="srch_key" id="srch_key" value="${srch_key}" />
                    <div class="graph_top">
                        <c:if test="${empty message}">
                            <ul>
                                <li><button class="btn2" type="button" onclick="send_resultList.doresend('${emsbean.msgid}','link');"><spring:message code="E0657" text="링크를 클릭한 사람에게 다시 보내기"/></button></li>
                            </ul>
                        </c:if>
                    </div>

                    <!-- content start -->
                    <div class="article content mg_b30 t_br2">
                        <!-- content area start -->
                        <div class="graph">
                            <div class="content_area">
                                <table  width ="100%" height="auto" >
                                    <colgroup>
                                        <col style="width:150px" />
                                        <col style="width:auto" />
                                        <col style="width:90px" />
                                        <col style="width:90px" />
                                        <col style="width:90px" />
                                        <col style="width:80px" />
                                    </colgroup>
                                    <thead >
                                        <tr>
                                            <th><spring:message code="E0671" text="링크이름"/></th>
                                            <th class="txt"><spring:message code="E0670" text="URL"/></th>
                                            <th class=""><spring:message code="E0663" text="클릭 횟수"/></th>
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
                                        <c:forEach var="linkMessageIDBean" items="${linkMessageIDBean}">
                                        <tr>
                                            <td>
                                                <span class="linkImg"><c:out value="${linkMessageIDBean.link_name}"></c:out></span>
                                            </td>
                                            <td class=""><span><c:out value="${linkMessageIDBean.link_url}"></c:out></span></td>
                                            <td class=""><span>${linkMessageIDBean.count}</span>회</td>
                                            <td class="txt center">
                                            <button title="링크 통계 상세" class="btn3" onclick="window.open('/mail/result/statisticsLinkDetailList.do?msgid=${emsbean.msgid}&linkid=${linkMessageIDBean.linkid}','링크통계 상세보기','width=800px,height=520px,location=no,status=no,scrollbars=yes,menubar=no,resizable=yes');" type="button">
                                                <span><spring:message code="E0452" text="상세보기"/></span>
                                            </button>
                                            </td>
                                            <td class="txt center">
                                                <button title="링크 통계 목록" class="btn3" onclick="window.open('/mail/result/statisticsLinkList.do?msgid=${emsbean.msgid}&linkid=${linkMessageIDBean.linkid}','링크통계 목록보기','width=1200px,height=600px,location=no,status=no,scrollbars=yes,menubar=no,resizable=yes');" type="button">
                                                    <span><spring:message code="E0635" text="목록보기"/></span>
                                                </button>
                                            </td>
                                            <td class="txt center"><button class="btn3" onclick="resultList.downLinkClickList('${emsbean.msgid}','${linkMessageIDBean.linkid}')"><spring:message code="E0638" text="다운"/></button></td>
                                        </tr>
                                        </c:forEach>
                                        </c:otherwise>
                                    </c:choose>
                                    </tbody>
                                </table>
                            </div>
                            <!-- nav start -->
                            <div class="page_nav">
                                <c:if test="${ empty linkMessageIDBean }">
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
                                        <pt:jslink>linkPopupList.list</pt:jslink>
                                    </pt:page>
                                </c:if>
                                <c:if test="${ !empty linkMessageIDBean }">
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
                                        <pt:jslink>linkPopupList.list</pt:jslink>
                                    </pt:page>
                                </c:if>
                            </div>
                            <!-- nav end -->
                        </div>
                        <!-- content area end -->
                    </div>
                        <!-- content end -->
                </form:form>
            </div>
                <!-- section end -->
            </div>
        </div>
    </body>
</html>



























<%@ page pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/jsp/inc/taglib.jspf" %>
<script type="text/javascript" src="/sens-static/js/result/result.js"></script>
<script type="text/javascript"></script>

<script>


    <%--<c:if test="${on} == receipt ">--%>
    <%--$('#ul_list li').removeClass('on');--%>
    <%--$('#receipt').addClass('on');--%>
    <%--</c:if>--%>

    <%--<c:if test="${on} == page ">--%>
    <%--$('#ul_list li').removeClass('on');--%>
    <%--$('#page').addClass('on');--%>
    <%--</c:if>--%>

    <%--<c:if test="${on} == link ">--%>
    <%--$('#ul_list li').removeClass('on');--%>
    <%--$('#link').addClass('on');--%>
    <%--</c:if>--%>
</script>

<div class="article content">
    <div class="composer_area">
        <div>
            <table width="100%" border="0" cellpadding="0" cellspacing="1" class="b_b2">
                <colgroup>
                    <col style="width: 150px">
                    <col style="width: 300px">
                    <col style="width: 150px">
                    <col style="width: auto">
                </colgroup>
                <tbody>
                <tr>
                    <th><spring:message code="E0103" text="제목"/></th>
                    <td colspan="3">${emsbean.msg_name}</td>
                </tr>
                <tr>
                    <th><spring:message code="E0255" text="발송분류"/></th>
                    <td>
                        <c:choose>
                            <c:when test="${empty categoryName}">
                                [<spring:message code="E0256" text="분류없음"/>]
                            </c:when>
                            <c:otherwise>
                                ${categoryName}
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <th><spring:message code="E0733" text="예약일자"/></th>
                    <td>
                        <fmt:parseDate value="${emsbean.reserv_time}" pattern="yyyyMMddHHmm" var="reserv_time"/>
                        <fmt:formatDate value="${reserv_time}" pattern="yyyy-MM-dd HH:mm"/>
                    </td>
                </tr>
                <tr>
                    <th><spring:message code="E0449" text="발송자 계정"/></th>
                    <td><span>${emsbean.userid}</span></td>
                    <th><spring:message code="E0288" text="등록일자"/></th>
                    <td>
                        <fmt:parseDate value="${emsbean.regdate}" pattern="yyyyMMddHHmm" var="regdate"/>
                        <fmt:formatDate value="${regdate}" pattern="yyyy-MM-dd HH:mm"/>
                    </td>
                </tr>
                <tr>
                    <th><spring:message code="E0450" text="보낸사람"/></th>
                    <td>${emsbean.mail_from}</td>
                    <th><spring:message code="E0451" text="수신자"/></th>
                    <td>
                        <c:if test="${emsbean.rectype == 1}">
                            <span><spring:message code="E0397" text="[주소록] ${emsbean.recname}"/></span>
                        </c:if>
                        <c:if test="${emsbean.rectype == 1 && emsbean.state == '+30'}">
                            <button title="수신자 상세보기" class="btn3"
                                    onclick="window.open('/receiver/group/addrDetail.do?msgid=${emsbean.msgid}','_blank','toolbar=no,width=900,height=500,directories=no,status=no,scrollbars=yes,resizable=yes,menubar=no');"
                            <span><spring:message code="E0452" text="상세보기"/></span>
                            </button>
                        </c:if>
                        <c:choose>
                            <c:when test="${emsbean.rectype == 3}">
                                <span><spring:message code="E0260" text="[수신그룹] ${emsbean.recname}"/></span>
                                <button title="수신자 상세보기" class="btn3"
                                        onclick="window.open('/receiver/group/groupDetail.do?recid=${emsbean.recid}','_blank','toolbar=no,width=900,height=500,directories=no,status=no,scrollbars=yes,resizable=yes,menubar=no');"
                                <span><spring:message code="E0452" text="상세보기"/></span>
                                </button>
                            </c:when>
                        </c:choose>
                    </td>
                </tr>

                </tbody>
            </table>
        </div>
        <%--    </div>--%>


        <!-- tab wrap start -->
        <div class="tab_area02">

            <ul class="list" id="ul_list">
                <li id="send" <c:if test="${on eq 'send'}">class="on"</c:if>><a href="javascript:;" onclick="resultList.goStaticSend('${emsbean.msgid}','${listcpage}','${srch_key}')"><spring:message code="E0753" text="발송 통계"/></a></li>
                <li id="error" <c:if test="${on eq 'error'}">class="on"</c:if>><a href="javascript:;" onclick="resultList.goStaticError('${emsbean.msgid}','${listcpage}','${srch_key}')"><spring:message code="E0754" text="에러 통계"/></a></li>
                <li id="receipt" <c:if test="${on eq 'receipt'}">class="on"</c:if>><a href="javascript:;" onclick="resultList.goStaticReceipt('${emsbean.msgid}','${listcpage}','${srch_key}')"><spring:message code="E0755" text="수신확인 통계"/></a></li>
                <li id="link" <c:if test="${on eq 'link'}">class="on"</c:if>><a href="javascript:;" onclick="resultList.goStaticLink('${emsbean.msgid}','${listcpage}','${srch_key}')"><spring:message code="E0756" text="링크 통계"/></a></li>
                <li id="page" <c:if test="${on eq 'page'}">class="on"</c:if> ><a href="javascript:;" onclick="resultList.goStaticPage('${emsbean.msgid}','${listcpage}','${srch_key}')"><spring:message code="E0757" text="페이지 보기"/></a></li>
            </ul>
        </div>

        <!-- tab wrap end -->


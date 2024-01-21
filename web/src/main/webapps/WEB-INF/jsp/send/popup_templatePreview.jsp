<%@ page contentType="text/html;charset=utf-8" %>
<%@ page language="java" %>
<%@ include file="../inc/common.jsp" %>
<%@ include file="/WEB-INF/jsp/inc/taglib.jspf" %>
<%--
  Created by IntelliJ IDEA.
  User: 홍준기
  Date: 2022-09-19
  Time: 오후 2:43
  To change this template use File | Settings | File Templates./send/image/preview.do
--%>
<div class="w_pop wrap">
    <div class="w_content">
        <div class="popup_title over_text">
            <div>
                <h3 class="title"><spring:message code="E0720" text="템플릿 미리보기"/></h3>
            </div>
        </div>
        <div class="section graph" id="printBody">
            <div class="article content">
                <div class="composer_area ">
                    <table width="100%" border="0" cellpadding="0" cellspacing="1" class="b_b2">
                        <colgroup>
                            <col style="width: 100px">
                            <col style="width: auto">
                        </colgroup>
                        <tbody>
                        <tr>
                            <th><spring:message code="E0103" text="제목"/></th>
                            <td class="over_text"><span>${subject}</span></td>
                        </tr>
                        </tbody>
                    </table>
                    <div class="prev_content" id="preview_content">
                        <c:choose>
                            <c:when test="${!empty message}">
                                <td colspan="2" class="over_text"><span>${message}</span></td>
                            </c:when>
                            <c:otherwise>
                                <td colspan="2" class="over_text"><span>${content}</span></td>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <%--<div class="pop_footer">
        <div class="btn_div">
            <button class="close_btn btn2" onclick="window.close();"><spring:message code="E0282" text="닫기"/></button>
        </div>
    </div>--%>
</div>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file = "../inc/common.jsp" %>
<script type="text/javascript" src="${staticURL}/sens-static/js/receiver/receiver.js"></script>

<div class="popup_title over_text">
    <span class="close_button" ></span>
    <h3 class="title"><spring:message code="E0104" text="미리보기"/></h3>
</div>

<div class="section">
    <!-- content start -->


    <div class="article content">
        <!-- content area start -->
        <c:if test="${ErrorMessage == null}">

        </c:if>
        <c:if test="${ErrorMessage != null}">
            <p class="strong txt">${ErrorMessage}</p>
        </c:if>
        <div class="composer_area">
            <c:if test="${ErrorMessage == null}">
                <p class="top_explain"><spring:message code="E0478" arguments="${totalCount}" text="데이터 수 : {0}개 (미리보기 데이터는 최대 100개까지만 조회됩니다.)"/></p>
                <table  width ="100%" height="auto" >
                    <colgroup>
                        <c:forEach var="field" items="${fieldInfoList}">
                            <col style="width:200px" />
                        </c:forEach>
                    </colgroup>
                    <tbody>
                    <tr>
                        <c:forEach var="field" items="${fieldInfoList}">
                            <th>${field}</th>
                        </c:forEach>
                    </tr>
                    <c:forEach var="result" items="${resultList}">
                        <tr >
                            <c:forEach var="value" items="${result}">
                                <td class="over_text">${value}</td>
                            </c:forEach>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:if>
        </div>
        <!-- content area end -->
        <%--<div class="pop_footer">
            <div class="btn_div">
                <button class="btn2" onclick="window.close()"><spring:message code="E0064" text="확인"/></button>
            </div>
        </div>--%>
    </div>
    <!-- content end -->

</div>
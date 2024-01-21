<%@ page contentType="text/html;charset=utf-8" %>
<%@ page language = "java" %>
<%@ include file = "../inc/common.jsp" %>
<script type="text/javascript" src="${staticURL}/sens-static/js/sysman/limivalue.js"></script>

<div class="contents_area">
    <form:form modelAttribute="relayLimitValueForm" id="relayLimitValueForm"  action="list.do" method="post">
        <div class="title_box fixed">
            <div class="article top_area">
                <div class="title">
                    <h1><spring:message code="E0608" text="발송제한 설정"/></h1>
                    <p><spring:message code="E0609" text="발송제한 설정을 할 수 있습니다."/></p>
                </div>
            </div>
        </div>
        <!-- content top start -->
        <!-- content top end -->
        <div class="section top_nobtn pagenav_no">
            <div class="article content">
                <div class="content_area">
                    <table width ="100%" height="auto" >
                        <colgroup>
                            <col style="width:auto;/"/>
                            <col style="width:200px;"/>
                            <col style="width:150px;"/>
                        </colgroup>
                        <thead class="fixed">
                        <tr>
                            <th><spring:message code="E0322" text="설명(메모)"/></th>
                            <th><spring:message code="E0607" text="한계값"/></th>
                            <th><spring:message code="E0128" text="수정"/><!--<spring:message code="E0030" text="삭제"/> --></th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:if test="${ empty relayLimitValueForm.limitValueList }">
                            <tr>
                                <td colspan="3" class="none"><spring:message code="60" text="등록된 정보가 없습니다."/></td>
                            </tr>
                        </c:if>
                        <c:forEach varStatus="i" var="limitValue" items="${ relayLimitValueForm.limitValueList }">
                            <tr>
                                <td>${ limitValue.descript }</td>
                                <td>${ limitValue.limit_value }</td>
                                <td>
                                    <button onclick="limitValue_list.edit('${ limitValue.limit_type }','${ limitValue.descript }','${ limitValue.limit_value }');" class="btn3" type="button"><span><spring:message code="E0128" text="수정"/></span></button><!-- <button onclick="limitValue_list.deleteOne('${ limitValue.limit_type }');" class="btn text white_gray" type="button"><span><spring:message code="12" text="삭제"/></span></button> -->
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </form:form>
    <!-- 송신제한값 설정 list End -->
</div>

<div id="limitValueLayer" style="display:none;" title="<spring:message code="E0608" text="발송제한 설정"/> <spring:message code="E0128" text="수정"/>" >>
    <%--<form:form modelAttribute="relayLimitValueForm" action="edit.do" onsubmit="limitValue_edit">--%>
        <input type="hidden" id="limit_type" value="limit_type"/>
        <input type="hidden" id="ord_limit_value" value="">
    <div class="section pd_l30">
        <!-- popup start-->
        <div class="popup">
            <div class="popup_content" style=" height: 250px;">
                <div class="popup_title">
                    <span class="close_button" onclick="limitValue_list.close();"></span>
                    <h3 class="title"><spring:message code="E0608" text="발송제한 설정"/> <spring:message code="E0128" text="수정"/></h3>
                </div>
                <div class="popup_body_wrap">
                    <div class="popup_body">
                        <!-- <p> 설명 텍스트 </p> -->
                        <table width="100%" border="0" cellpadding="0" cellspacing="1" >
                            <colgroup>
                                <col style="width: 150px">
                                <col style="width: auto">
                            </colgroup>
                            <tbody>
                            <tr>
                                <th><spring:message code="E0653" text="설명"/></th>
                                <td id="descript"></td>
                            </tr>
                            <tr>
                                <th><spring:message code="E0607" text="한계값"/></th>
                                <td>
                                    <input type="text" class="input" id="limit_value">
                                </td>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
                <div class="pop_footer">
                    <div class="btn_div">
                        <button class="btn2" onclick="limitValue_edit.edit();"><spring:message code="E0128" text="수정"/></button>
                        <button class="btn2" onclick="limitValue_list.close();"><spring:message code="E0065" text="취소"/></button>
                    </div>
                </div>
            </div>
        </div>
        <!-- popup end-->
    </div>
    <%--</form:form>--%>
<!-- section end -->
</div>

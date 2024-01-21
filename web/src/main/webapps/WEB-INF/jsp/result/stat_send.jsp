<%@ page contentType="text/html;charset=utf-8" %>
<%@ page language="java" %>
<%@ include file="../inc/common.jsp" %>
<!-- Resources -->
<script src="${staticURL}/sens-static/plugin/amchart4/core.js"></script>
<script src="${staticURL}/sens-static/plugin/amchart4/charts.js"></script>
<script src="${staticURL}/sens-static/plugin/amchart4/themes/material.js"></script>
<script src="${staticURL}/sens-static/plugin/amchart4/themes/animated.js"></script>

<script language="javascript">
    $(document).ready(function() {

        $("#resp_time").datepicker({
            inline: false,
            changeMonth: true,
            changeYear:true,
            dayNames: ['','','','','','',''],
            dayNamesMin: ['Su','Mo','Tu','We','Th','Fr','Sa'],
            dayNamesShort: ['Su','Mo','Tu','We','Th','Fr','Sa'],
            monthNames: ['','','','','','','','','','','',''],
            monthNamesShort: ['01','02','03','04','05','06','07','08','09','10','11','12'],
            dateFormat: 'yy-mm-dd',
            autosize: true,
            minDate: new Date(),	// 시작일
            maxDate: "12m"          // 종료일(현재부터 1년까지)
        });

        <%--$("#resp_time").datepicker("setDate", ${resp_time});--%>

        <%--$("#resp_time").val(${resp_time});--%>
        var resp_hour = staticsendHsList.dateFormatHour(${resp_hour});
        var resp_min = staticsendHsList.dateFormatMin(${resp_min});
        $("#resp_hour").val(resp_hour);
        $("#resp_min").val(resp_min);

    });

</script>


<!-- section start -->
<!-- top area start -->
<%--
  Created by IntelliJ IDEA.

  User: 이주영
  Date: 2022-03-08
  Time: 오전 11:49
  To change this template use File | Settings | File Templates.
--%>
<%--<form name="statForm" action="statisticsSend.do" method="post">--%>
    <!-- section start -->
    <!-- top area start -->

    <form:form method="post" id="StatSendForm" name="StatSendForm" modelAttribute="StatSendForm" action="staticSend.do">
        <form:hidden path="cpage" value="${ pageInfo.cpage }" />
        <form:hidden path="msgid" id="msgid" value="${emsbean.msgid}" />
        <input type="hidden" name="srch_key" id="srch_key" value="${srch_key}" />
        <input type="hidden" name="listcpage" id="listcpage" value="${listcpage}" />
        <input type="hidden" name="response_time" id="response_time" value="${response_time}" />
        <div class="graph">
            <!-- content area start -->
            <div class="composer_area ">
                <table>
                    <tbody>
                    <tr>
                        <td class="graph_lft">
                            <table width="100%" border="0" cellpadding="0" cellspacing="1" >
                                <tbody>
                                <tr>
                                    <td class="txt center">
                                                    <c:if test="${empty emsMap}">
                                                        <span><spring:message code="E0642" text="데이터가 존재하지 않습니다."/></span>
                                                    </c:if>
                                                    <div id="chartdiv"></div>
                                                </td>
                                            </tbody>
                                        </table>
                                    </td>
                                        <td class="mini_table" width="auto">
                                            <table width="100%" border="0" cellpadding="0" cellspacing="1" >
                                                <colgroup>
                                                    <col style="width:150px">
                                                    <col style="width:auto">
                                                </colgroup>
                                                <tbody>
                                                <c:choose>
                                                    <c:when test="${total_count == 0 && success_count == 0 &&  fail_count == 0}">
                                                        <tr>
                                                            <td colspan="4" align="center" class="none_data"><span><spring:message code="E0642" text="데이터가 존재하지 않습니다."/></span></td>
                                                        </tr>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <tr>
                                                            <th><spring:message code="E0455" text="발송메일수"/></th>
                                                            <td>${total_count}<spring:message code="E0385" text="건"/></td>
                                                        </tr>
                                                        <tr>
                                                            <th><spring:message code="E0456" text="성공"/></th>
                                                            <td><span>${success_count}<spring:message code="E0385" text="건"/></span></td>
                                                        </tr>
                                                        <tr>
                                                            <th><spring:message code="E0457" text="실패"/></th>
                                                            <td><span>${fail_count}<spring:message code="E0385" text="건"/></span></td>
                                                        </tr>
                                                        <tr>
                                                            <th><spring:message code="E0071" text="수신거부"/></th>
                                                            <td><span><spring:message code="E0675" text="현재 :"/> ${reject_recentcount}<spring:message code="E0385" text="건"/> / <spring:message code="E0098" text="전체"/>: ${reject_totalcount}<spring:message code="E0385" text="건"/> </span></td>
                                                        </tr>
                                                        <tr>
                                                            <th><spring:message code="E0459" text="시작시간"/></th>
<%--                                                            <td><span>${emsbean.start_time}</span></td>--%>
                                                          <td>  <fmt:parseDate value="${emsbean.start_time}" pattern="yyyyMMddHHmmss" var="start_time"/>
                                                            <fmt:formatDate value="${start_time}" pattern="yyyy-MM-dd"/>
                                                            <span><fmt:formatDate value="${start_time}" pattern="HH:mm"/></span></td>
                                                        </tr>
                                                        <tr>
                                                            <th><spring:message code="E0460" text="종료시간"/></th>
<%--                                                            <td><span>${emsbean.end_time}</span></td>--%>
                                                            <td>  <fmt:parseDate value="${emsbean.end_time}" pattern="yyyyMMddHHmm" var="end_time"/>
                                                                <fmt:formatDate value="${end_time}" pattern="yyyy-MM-dd"/>
                                                                <span><fmt:formatDate value="${end_time}" pattern="HH:mm"/></span></td>
                                                        </tr>
<%--                                                        <fmt:parseDate value="${resp_time}" pattern="yyyyMMddHHmm" var="resp_time"/>--%>
<%--                                                        <fmt:formatDate value="${resp_time}" pattern="yyyy-MM-dd"/>--%>
                                                        <tr class="">
                                                            <th><spring:message code="E0398" text="반응분석 종료일"/></th>
                                                            <td>
                                                                <%--<input type="text" name="resp_date" id="resp_date" class="w100" size="10" value="${resp_date}" readonly="">--%>
                                                                <form:input path="resp_time" cssClass="w100" value="${resp_time}" title="반응분석종료일"/>
                                                                    <%--<input type="text" name="resp_hourmin" id="resp_hourmin" class="w50" size="5" value="${resp_hourmin}" readonly="">--%>
                                                                <form:select path="resp_hour" cssStyle="vertical-align: middle;width:56px;" cssClass="w100" title="시" items="${hourList}" value="${resp_hour}"/>
                                                                <form:select path="resp_min" cssStyle="vertical-align: middle;width:56px;" cssClass="w100" title="분" items="${minuteList}" value="${resp_min}"/>
                                                                <button class="btn3 btn_small" onclick="staticsendHsList.editChanegResp_time()"/><spring:message code="E0128" text="수정"/></button>
                                                            </td>

                                                        </tr>
                                                    </c:otherwise>
                                                </c:choose>
                                                </tbody>
                                            </table>
                                        </td>

                                </tr>
                                </tbody>

                            </table>



            </div>

            <!-- content area end -->
        </div>
        <!-- content end -->


        <!-- content start -->
        <div class="graph">
            <!-- content area start -->
            <div class="content_area ">
                <table  width ="100%" height="auto">
                    <colgroup>
                        <col style="width:auto" />
                        <col style="width:80px" />
                        <col style="width:80px" />
                        <col style="width:80px" />
                        <col style="width:400px" />
                    </colgroup>
                    <thead>
                    <tr>
                        <th class="over_txt"><spring:message code="E0109" text="도메인명"/></th>
                        <th class="txt"><spring:message code="E0469" text="발송건수"/></th>
                        <th class="txt"><spring:message code="E0470" text="실패건수"/></th>
                        <th class="txt"><spring:message code="E0458" text="성공율"/></th>
                        <th></th>
                    </tr>
                    </thead>
                    <tbody>

                    <c:forEach var="hcResult" items="${hcList}">
                        <c:set var="eration" value="${(100*hcResult.ecount)/hcResult.scount}"/>
                        <fmt:formatNumber value="${eration}" type="number" var="val" pattern="########"/>
                        <tr>
                            <td class="over_text"><span>${hcResult.hostname}</span></td>
                            <td class="txt"><span>${hcResult.scount}</span><spring:message code="E0385" text="건"/></td>
                            <td class="txt"><span>${hcResult.ecount}</span><spring:message code="E0385" text="건"/></td>
                            <td class="txt"><span >${100-val}%</span>
                            </td>
                            <td class="txt">
                               <%-- <div id="hc_bar" class="current_percent" style="width:${hcResult.eration<100?hcResult.eration:100}%;top:0;"></div>--%>
                                <div class="graph_Wlong"><span class="stick" style="width:${100-val}%;"></span></div>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${ empty hcList}">
                        <td height="46px" colspan="5" align="center"><spring:message code="E0481" text="도메인 결과분석 데이터가 존재하지 않습니다"/></td>
                    </c:if>
                    </tbody>
                </table>

            <!-- content area end -->
        </div>
        <!-- content end -->

        <!-- nav start -->
        <!-- nav start -->
        <div class="page_nav">
            <c:if test="${ empty hcList }">
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
            <c:if test="${ !empty hcList }">
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
                <pt:jslink>staticsendHsList.list</pt:jslink>
            </pt:page>
            </c:if>
        </div>
    </form:form>
</div>
</div>
</div>
</div>

<script>
    am4core.ready(function() {
        var emsDate = '${emsMap}';

        if(emsDate != null && emsDate != ''){
            // Themes begin
            am4core.useTheme(am4themes_material);
            am4core.useTheme(am4themes_animated);
            // Themes end

            // Create chart instance
            var chart = am4core.create("chartdiv", am4charts.PieChart);

            var list = new Array() ;
            var i= 0;
            <c:forEach items="${emsMap}" var="i" >
            // 객체 생성
            var data = {
                "ems_info" : "${i.key}",
                "value" : "${i.value}"
            };
            // 리스트에 생성된 객체 삽입
            list.push(data) ;
            </c:forEach>

            chart.data = [{
                "ems_type": list[0].ems_info,
                "litres": list[0].value
            }, {
                "ems_type": list[1].ems_info,
                "litres": list[1].value
            }];

            // Add and configure Series
            var pieSeries = chart.series.push(new am4charts.PieSeries());
            pieSeries.dataFields.value = "litres";
            pieSeries.dataFields.category = "ems_type";
            pieSeries.slices.template.stroke = am4core.color("#fff");
            pieSeries.slices.template.strokeWidth = 2;
            pieSeries.slices.template.strokeOpacity = 1;

            // This creates initial animation
            pieSeries.hiddenState.properties.opacity = 1;
            pieSeries.hiddenState.properties.endAngle = -90;
            pieSeries.hiddenState.properties.startAngle = -90;

        }
    })

</script>

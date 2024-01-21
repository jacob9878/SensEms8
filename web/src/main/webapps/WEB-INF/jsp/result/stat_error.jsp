<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<script src="${staticURL}/sens-static/plugin/amchart4/core.js"></script>
<script src="${staticURL}/sens-static/plugin/amchart4/charts.js"></script>
<script src="${staticURL}/sens-static/plugin/amchart4/themes/material.js"></script>
<script src="${staticURL}/sens-static/plugin/amchart4/themes/animated.js"></script>
<script type="text/javascript" src="/sens-static/js/messages/sendmanage_ko.js"></script>
<div class="graph_top">
    <c:if test="${empty message}">
        <ul>
            <li><button class="btn2" onclick="statisticsError.doerrorresend('${msgid}');"><spring:message code="E0643" text="에러메일 재발신"/></button></li>
        </ul>
    </c:if>
</div>

<div class="graph mg_b30 t_br2">
    <!-- content area start -->
    <div class="">
        <table width="100%">
            <tbody>
            <tr>
                <td class="graph_lft" >
                    <table width="100%" border="0" cellpadding="0" cellspacing="0" >
                        <tbody>
                        <tr>
                            <c:choose>
                                <c:when test="${empty errDataMap}">
                                    <td class="txt center">
                                        <span><spring:message code="E0642" text="데이터가 존재하지 않습니다."/></span>
                                    </td>
                                </c:when>
                                <c:otherwise>
                                    <div id="chartdiv" style="height: 430px"></div>
                                </c:otherwise>
                            </c:choose>
                        </tr>
                        </tbody>
                    </table>
                </td>
                <td class="mini_table" width="auto">
                    <table  width ="100%" height="auto" >
                        <colgroup>
                            <col style="width:auto" />
                            <col style="width:80px" />
                            <col style="width:90px" />
                            <col style="width:80px" />
                        </colgroup>
                        <thead >
                        <c:if test="${empty message}">
                        <tr>
                            <th class="txt"><spring:message code="E0644" text="에러종류"/></th>
                            <th class="txt"><spring:message code="E0646" text="에러건수"/></th>
                            <th class="txt center"><spring:message code="E0635" text="목록보기"/></th>
                            <th class="txt center"><spring:message code="E0636" text="목록다운"/></th>
                        </tr>
                        </c:if>
                        </thead>
                        <tbody>
                        <c:choose>
                            <c:when test="${!empty message}">
                                <tr>
                                    <td colspan="4" align="center" style="height: 215px;"><span><spring:message code="E0642" text="데이터가 존재하지 않습니다."/></span></td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                            <c:forEach var="err" items="${errDataMap}" varStatus="status" >
                            <tr>
                                <td class="over_text" title='${err.type}'><span class="dot color_${status.index}"></span><c:out value="${err.type}"></c:out></td>
                                <td class="txt"><span>${err.count}</span>건</td>
                                <td class="txt center"><button class="btn3" onclick="statisticsError.list('${msgid}', '${err.code}');" type="button"><spring:message code="E0635" text="목록보기"/></button></td>
                                <td class="txt center"><button class="btn3" onclick="statisticsError.download('${msgid}', '${err.code}');" type="button" ><spring:message code="E0638" text="다운"/></button></td></tr>
                            </tr>
                            </c:forEach>
                            </c:otherwise>
                        </c:choose>
                        </tbody>
                    </table>
                </td>
            </tr>
            </tbody>
        </table>
    </div>

    <script>
        am4core.ready(function() {
            var errData = '${errDataMap}';

            if(errData != null && errData != ''){
                // Themes begin
                am4core.useTheme(am4themes_material);
                am4core.useTheme(am4themes_animated);
                // Themes end

                // Create chart instance
                var chart = am4core.create("chartdiv", am4charts.PieChart);

                var list = new Array() ;
                var i= 0;
                var tt = 0;
                <c:forEach items="${errDataMap}" var="i" >
                // 객체 생성
                tt +=  ${i.count};
                var data = {
                    "err_name" : "${i.type}",
                    "value" : "${i.count}",
                    "color" : "${i.color}"
                };
                // 리스트에 생성된 객체 삽입
                list.push(data)
                </c:forEach>
                if(tt==0){
                    document.getElementById("chartdiv").innerHTML="<div style='text-align: center; line-height: 430px;'>데이터가 존재하지 않습니다.</div>";
                }

                for (var i = 0; i < 15; i++) {
                    chart.data.push({
                        "error_type": list[i].err_name,
                        "litres": list[i].value,
                        "color": am4core.color(list[i].color)
                    });
                }

                chart.innerRadius = am4core.percent(10);

                var pieSeries = chart.series.push(new am4charts.PieSeries());
                pieSeries.dataFields.value = "litres";
                pieSeries.dataFields.category = "error_type";
                pieSeries.slices.template.propertyFields.fill = "color";
                pieSeries.slices.template.stroke = am4core.color("#fff");
                pieSeries.slices.template.strokeWidth = 2;
                pieSeries.slices.template.strokeOpacity = 1;
                pieSeries.labels.template.disabled = true;

                // This creates initial animation
                pieSeries.hiddenState.properties.opacity = 1;
                pieSeries.hiddenState.properties.endAngle = -90;
                pieSeries.hiddenState.properties.startAngle = -90;
                pieSeries.tooltip.background.fill = am4core.color("#000000");
            }
        }); // end am4core.ready()

    </script>
    <!-- content area end -->
</div>
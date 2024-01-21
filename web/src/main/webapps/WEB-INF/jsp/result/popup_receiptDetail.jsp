<%@ page contentType="text/html;charset=utf-8" %>
<%@ page language="java" %>
<%@ include file="../inc/common.jsp" %>
<!-- Resources -->
<script src="//cdn.amcharts.com/lib/4/core.js"></script>
<script src="//cdn.amcharts.com/lib/4/charts.js"></script>
<script src="//cdn.amcharts.com/lib/4/themes/animated.js"></script>
<script src="//cdn.amcharts.com/lib/4/themes/kelly.js"></script>
<script src="${staticURL}/sens-static/plugin/amchart4/core.js"></script>
<script src="${staticURL}/sens-static/plugin/amchart4/charts.js"></script>
<script src="${staticURL}/sens-static/plugin/amchart4/themes/material.js"></script>
<script src="${staticURL}/sens-static/plugin/amchart4/themes/animated.js"></script>
<script src="https://cdn.amcharts.com/lib/5/index.js"></script>
<script src="https://cdn.amcharts.com/lib/5/xy.js"></script>
<script src="https://cdn.amcharts.com/lib/5/themes/Animated.js"></script>
<script>
    am4core.ready(function() {
        var emsDate = '${emsMap}';

        if(emsDate != null && emsDate != ''){


            am4core.useTheme(am4themes_animated);
            am4core.useTheme(am4themes_kelly);
            // Themes end

            // Create chart instance
            var chart = am4core.create("chartdiv", am4charts.XYChart);

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
                "ems_type": 1,
                "litres": list[0].value
            }, {
                "ems_type": 2,
                "litres": list[1].value
            },{
                "ems_type": 3,
                "litres": list[2].value
            },{
                "ems_type": 4,
                "litres": list[3].value
            },{
                "ems_type": 5,
                "litres": list[4].value
            },{
                "ems_type": 6,
                "litres": list[5].value
            },{
                "ems_type": 7,
                "litres": list[6].value
            },{
                "ems_type": 8,
                "litres": list[7].value
            },{
                "ems_type": 9,
                "litres": list[8].value
            },{
                "ems_type": 10,
                "litres": list[9].value
            },{
                "ems_type": 11,
                "litres": list[10].value
            },{
                "ems_type": 12,
                "litres": list[11].value
            },{
                "ems_type": 13,
                "litres": list[12].value
            },{
                "ems_type": 14,
                "litres": list[13].value
            },{
                "ems_type": 15,
                "litres": list[14].value
            },{
                "ems_type": 16,
                "litres": list[15].value
            },{
                "ems_type": 17,
                "litres": list[16].value
            },{
                "ems_type": 18,
                "litres": list[17].value
            },{
                "ems_type": 19,
                "litres": list[18].value
            },{
                "ems_type": 20,
                "litres": list[19].value
            },{
                "ems_type": 21,
                "litres": list[20].value
            },{
                "ems_type": 22,
                "litres": list[21].value
            },{
                "ems_type": 23,
                "litres": list[22].value
            },{
                "ems_type": 24,
                "litres": list[23].value
            },];


            var categoryAxis = chart.xAxes.push(new am4charts.CategoryAxis());
            categoryAxis.dataFields.category = "ems_type";

            categoryAxis.renderer.grid.template.location = 0;
            categoryAxis.renderer.minGridDistance = 25;

            var  valueAxis = chart.yAxes.push(new am4charts.ValueAxis());
            // valueAxis.title.text = "건 수";

            var series = chart.series.push(new am4charts.ColumnSeries());
            series.dataFields.valueY = "litres";
            series.dataFields.categoryX = "ems_type";
            series.name = "건 수";
            series.tooltipText = "{name}: [bold]{valueY}[/]";


            chart.cursor = new am4charts.XYCursor();


            chart.legend = new am4charts.Legend();

        }
    });
</script>
<%--
  Created by IntelliJ IDEA.
  User: 신주현
  Date: 2022-03-24
  Time: 오후 6:24
  To change this template use File | Settings | File Templates.
--%>
<!DOCTYPE HTML>
<html lang="ko-KR">
    <head>
        <meta charset="UTF-8">
        <title>SensEMS</title>
        <link rel="stylesheet" href="../../sens-static/css/style.css" />
        <link rel="stylesheet" href="../../sens-static/css/skin.css" />

    </head>
<%--<c:forEach var="emsMap2" items="${emsMap2}" begin="0" end="11" step="1" varStatus="status">--%>
<%--    <input type="text" value="${emsMap2.value}">--%>
<%--</c:forEach>--%>
    <body class="skin1">
        <div class="w_pop wrap">
            <div class="statisticsReceipt w_content">
                <div class="popup_title over_text">
                    <div >
                        <h3 class="title"><spring:message code="E0654" text="수신확인 통계 상세보기"/></h3>
                    </div>
                </div>

                <!-- content top start -->
                <!-- <div class="content_top fixed">
                    <ul class="content_top_btn">
                        <li><a class="btn2" href="">이전으로</a></li>
                    </ul>

                </div> -->
                <!-- content top end -->

                <div class="section graph min_graph">
                    <!-- content start -->
                    <!-- <p class="over_text">데이터수 : 10개 (미리보기 데이터는 최대 100개까지만 조회됩니다.)</p> -->
                    <!-- content area start -->
                    <div class="article content">
                    <div class="composer_area">
                        <table  width ="100%" height="auto" >
                            <tbody>
                                <tr>
                                    <td class="graph_lft ">
                                        <table width="100%" border="0" cellpadding="0" cellspacing="1" >
                                            <tbody>
                                                <tr>
                                                    <td>
                                                        <c:if test="${empty emsMap}">
                                                            <span><spring:message code="E0642" text="데이터가 존재하지 않습니다."/></span>
                                                        </c:if>
                                                        <div id="chartdiv"></div>
                                                    </td>
                                                </tr>
                                            </tbody>
                                        </table>
                                    </td>
                                    <td class="mini_table pd_l10">
                                        <table width ="100%" height="auto">
                                            <colgroup>
                                                <col style="width:80px">
                                                <col style="width:70px">
                                                <col style="width:80px">
                                                <col style="width:auto">
                                            <tbody>
                                                <tr>
                                                    <th colspan="4" class="min_graph_head"><span>${recvDate}</span></th>
                                                </tr>
                                                <c:forEach var="emsMap" items="${emsMap}" begin="0" end="11" step="1" varStatus="status">
                                                    <c:set var ="i" value="${i+1}"/>

                                                <tr>
                                                    <th>${i-1} - ${i}시</th>
                                                    <td>${emsMap.value}<span>건</span></td>

                                                    <th>${i+11} - ${i+12}시</th>
                                                    <td>${data[i-1]}<span>건</span></td>
                                                </tr>
                                                </c:forEach>
                                            </tbody>
                                        </table>
<%--                                        <table width ="50%" height="auto" style="float:right">--%>
<%--                                            <colgroup>--%>
<%--                                                <col style="width:70px" />--%>
<%--                                                <col style="width:70px" />--%>
<%--                                            <tbody>--%>
<%--                                                <tr>--%>
<%--                                                    <th colspan="4" class="min_graph_head"><span></span></th>--%>
<%--                                                </tr>--%>
<%--                                                <c:forEach var="emsMap" items="${emsMap}" begin="12" end="23" step="1">--%>
<%--                                                    <c:set var = "t" value="${t+1}"/>--%>
<%--                                                    <tr>--%>
<%--                                                        <th>${t + 11} - ${emsMap.key}</th>--%>
<%--                                                        <td>${emsMap.value}<span>건</span></td>--%>
<%--                                                    </tr>--%>
<%--                                                </c:forEach>--%>
<%--                                            </tbody>--%>
<%--                                        </table>--%>
                                    </td>
                            </tbody>
                        </table>
                    </div>
                    </div>
                    <!-- content area end -->
                    <!-- content end -->

                </div>
            </div>
            <div class="pop_footer">
            <div class="btn_div">
                <button class="close_btn btn2" onclick="window.close();"><spring:message code="E0282" text="닫기"/></button>
            </div>
            </div>
        </div>
    </body>
</html>



























<%--
  Created by IntelliJ IDEA.
  User: 신주현
  Date: 2022-04-17
  Time: 오후 10:46
  To change this template use File | Settings | File Templates.
--%>
<%@ include file = "../inc/common.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

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
        var emsDate = '${linkLogMessageIDBean}';

        if(emsDate != null && emsDate != ''){


            am4core.useTheme(am4themes_animated);
            am4core.useTheme(am4themes_kelly);
            // Themes end

            // Create chart instance
            var chart = am4core.create("chartdiv", am4charts.XYChart);



            var list = new Array() ;

            var i= 0;
            <c:forEach var="linkLogMessageIDBean" items="${linkLogMessageIDBean}">
            // 객체 생성
            var data = {
                "ems_info" : "${linkLogMessageIDBean.link_date}",
                "value" : ${linkLogMessageIDBean.click_count}
            };


            // 리스트에 생성된 객체 삽입
            list.push(data) ;

            </c:forEach>
            chart.dateFormatter.dateFormat = "yyyy-MM-dd";
            chart.data = [
               <c:forEach items="${linkLogMessageIDBean}" var="linkLogMessageIDBean" >

                {
                "ems_type": "${linkLogMessageIDBean.link_date}",
                "litres": ${linkLogMessageIDBean.click_count}
            },

               </c:forEach>
            ];


            var categoryAxis = chart.xAxes.push(new am4charts.CategoryAxis());
            categoryAxis.dataFields.category = "ems_type";

            categoryAxis.renderer.grid.template.location = 1;
            categoryAxis.renderer.minGridDistance = 20;
            var  valueAxis = chart.yAxes.push(new am4charts.ValueAxis());
            // valueAxis.title.text = "건 수";
            valueAxis.renderer.line.strokeWidth = 2;
            var series = chart.series.push(new am4charts.ColumnSeries());
            series.dataFields.valueY = "litres";
            series.dataFields.categoryX = "ems_type";
            series.name = "클릭 수";
            // series.tooltipText = "{name}: [bold]{valueY}[/]";


            chart.cursor = new am4charts.XYCursor();


             chart.legend = new am4charts.Legend();

        }
    });
</script>



        <div class="w_pop">
            <div class="statisticsLink_detail w_content">
                <div class="popup_title over_text">
                    <div >
                        <h3 class="title"><spring:message code="E0656" text="링크 통계 상세보기"/></h3>
                    </div>
                </div>

                <div class="section graph">
                    <div class="article content">
                        <!-- content start -->

                        <div class="composer_area">
                            <div class="graph_top">
                                <ul>
                                    <li><button class="btn2" type="button" onclick="self.close(); parent.opener.send_resultList.doresend('${msgid}','link','${linkid}')"><spring:message code="E0657" text="링크를 클릭한 사람에게 다시 보내기"/></button></li>
                                </ul>
                            </div>

                            <c:choose>
                                <c:when test="${empty linkLogMessageIDBean}">

                                </c:when>
                                <c:otherwise>
                                <div class="content_area ">
                                    <div class="top_chart_area">
                                        <div id="chartdiv"></div>
                                    </div>
                                </div>
                                </c:otherwise>
                            </c:choose>
                                <div class="content_area ">
                                    <table  width ="100%" height="auto" >
                                        <colgroup>
                                            <col style="width:100px" />
                                            <col style="width:110px" />
                                            <col style="width:auto" />
                                            <col style="width:100px" />
                                        </colgroup>
                                        <thead >
                                            <tr>
                                                <th class="txt center"><spring:message code="E0658" text="클릭한 날짜"/></th>
                                                <th class="txt" colspan="2"><spring:message code="E0659" text="총 확인자수"/></th>

                                                <th class="txt center"><spring:message code="E0452" text="상세보기"/></th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                        <c:choose>
                                            <c:when test="${empty linkLogMessageIDBean}">
                                                <tr>
                                                    <td colspan="4" class="txt center"><spring:message code="E0586" text="데이터가 없습니다."/></td>
                                                </tr>
                                            </c:when>
                                            <c:otherwise>
                                                <c:forEach var="linkLogMessageIDBean" items="${linkLogMessageIDBean}">
                                                    <tr>
                                                        <td class="txt center"><span>${linkLogMessageIDBean.link_date}</span></td>
                                                        <td>
                                                            <span> <fmt:formatNumber value="${(linkLogMessageIDBean.click_count* 100)/ totalcount }" type="PERCENT" pattern="#.#" />%(${linkLogMessageIDBean.click_count}명)</span>
                                                        </td>
                                                        <td class="graph_Wlong"><span class="stick" style="width:${(100*linkLogMessageIDBean.click_count)/totalcount}%"></span></td>

                                                        <td class="txt center">
                                                            <button title="링크확인 통계 상세" class="btn4" onclick="window.open('/mail/result/statisticsLinkDetail.do?msgid=${msgid}&click_date=${linkLogMessageIDBean.link_date}&linkid=${linkLogMessageIDBean.adid}','링크통계 상세보기','width=800px,height=550px,location=no,status=no,scrollbars=yes,menubar=no,resizable=yes');" type="button">
                                                                <span><spring:message code="E0452" text="상세보기"/></span>
                                                            </button>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                            </c:otherwise>
                                        </c:choose>
                                        </tbody>
                                    </table>
                                </div>
                        </div>
                    </div>
                </div>
                    <!-- section end -->
                    <div class="pop_footer">
                        <div class="btn_div">
                            <button class="close_btn btn2" onclick="window.close();"><spring:message code="E0282" text="닫기"/></button>
                        </div>
                    </div>
            </div>
        </div>
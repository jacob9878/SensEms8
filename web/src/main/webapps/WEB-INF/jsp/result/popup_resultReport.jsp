<%--
  Created by IntelliJ IDEA.
  User: moonc
  Date: 2021-02-25
  Time: 오후 4:05
  To change this template use File | Settings | File Templates.
--%>
<!-- Resources -->
<script src="${staticURL}/sens-static/plugin/amchart4/core.js"></script>
<script src="${staticURL}/sens-static/plugin/amchart4/charts.js"></script>
<script src="${staticURL}/sens-static/plugin/amchart4/themes/material.js"></script>
<script src="${staticURL}/sens-static/plugin/amchart4/themes/animated.js"></script>
<%@ include file = "../inc/common.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<script>
    am4core.ready(function() {
        var errDate = '${errDataMap}';

        if(errDate != null && errDate != ''){

            // Themes begin
            am4core.useTheme(am4themes_material);
            am4core.useTheme(am4themes_animated);
            // Themes end

            // Create chart instance
            var chart = am4core.create("chartdiv", am4charts.PieChart);

            var list = new Array() ;
            var i= 0;
            <c:forEach items="${errDataMap}" var="i" >
            // 객체 생성
            var data = {
                "err_name" : "${i.key}",
                "value" : "${i.value}"
            };
            // 리스트에 생성된 객체 삽입
            list.push(data) ;
            </c:forEach>

            chart.data = [{
                "error_type": list[0].err_name,
                "litres": list[0].value
            }, {
                "error_type": list[1].err_name,
                "litres": list[1].value
            }, {
                "error_type": list[2].err_name,
                "litres": list[2].value
            }, {
                "error_type": list[3].err_name,
                "litres": list[3].value
            }, {
                "error_type": list[4].err_name,
                "litres": list[4].value
            }, {
                "error_type": list[5].err_name,
                "litres": list[5].value
            }];

            // Add and configure Series
            var pieSeries = chart.series.push(new am4charts.PieSeries());
            pieSeries.dataFields.value = "litres";
            pieSeries.dataFields.category = "error_type";
            pieSeries.slices.template.stroke = am4core.color("#fff");
            pieSeries.slices.template.strokeWidth = 2;
            pieSeries.slices.template.strokeOpacity = 1;

            // This creates initial animation
            pieSeries.hiddenState.properties.opacity = 1;
            pieSeries.hiddenState.properties.endAngle = -90;
            pieSeries.hiddenState.properties.startAngle = -90;

        }
    }); // end am4core.ready()

</script>



    <div class="w_content">
        <div class="popup_title over_text">
            <div >
                <h3 class="title"><spring:message code="E0447" text="메일 리포팅"/></h3>
            </div>
        </div>

        <!-- content top start -->
        <%--<div class="content_top fixed">
            <ul class="content_top_btn">
                <li><a class="btn1" href="#" onclick="report.htmlSave('${emsBean.msgid}');"><spring:message code="E0069" text="저장"/></a></li>
                <li><a class="btn2" href="#" onclick="report.print();"><spring:message code="E0448" text="인쇄"/></a></li>
            </ul>
        </div>--%>
        <!-- content top end -->

        <div class="section graph" id="printBody">
            <!-- content start -->
            <div class="article content">
                <!-- content area start -->
                <div class="composer_area ">
                    <table width="100%" border="0" cellpadding="0" cellspacing="1" class="b_b2">
                        <colgroup>
                            <col style="width: 115px">
                            <col style="width: 160px">
                            <col style="width: 115px">
                            <col style="width: auto">
                        </colgroup>
                        <tbody>
                            <tr>
                                <th><spring:message code="E0103" text="제목"/></th>
                                <td colspan="3" class="over_text"><span>${emsBean.msg_name}</span></td>
                            </tr>
                            <tr>
                                <th><spring:message code="E0255" text="발송분류"/></th>
                                <td colspan="3"><span>
                                <c:choose>
                                    <c:when test="${empty categoryName}">
                                        [<spring:message code="E0256" text="분류없음"/>]
                                    </c:when>
                                    <c:otherwise>
                                        ${categoryName}
                                    </c:otherwise>
                                </c:choose>
                                    </span></td>
                            </tr>
                            <tr>
                                <th><spring:message code="E0449" text="발송자 계정"/></th>
                                <td><span>${emsBean.userid}</span></td>
                                <th><spring:message code="E0451" text="수신자"/></th>
                                <td>
                                    <span>${emsBean.recname}</span>
                                    <%--TODO 구 ems에서는 존재하지 않던 버튼으로 구현이 필요한지 검토 필요 - 리스트에서는 recv_massageID 목록을 출력하는데 하기 버튼을 클릭 시 수신그룹목록을 불러오고 있음 데이터를 불러오는 부분이 일관성이 없다 판단되어 우선 주석 조치--%>
                                    <%--
    <button title="수신자 상세보기" class="btn4" onclick="window.open('02002_statisticsSend_address.html','에러통계 목록','width=1000px,height=550px,location=no,status=no,scrollbars=yes,menubar=no,resizable=yes');" type="button">
                                        <span><spring:message code="E0452" text="상세보기"/></span>
                                    </button>--%>
                                </td>
                            </tr>
                            <tr>
                                <th><spring:message code="E0450" text="보낸사람"/></th>
                                <td><span>${emsBean.mail_from}</span></td>
                                <th><spring:message code="E0453" text="발송일자"/></th>
                                <td><span>${emsBean.regdate}</span></td>
                            </tr>

                        </tbody>
                    </table>


                    <div class="">
                        <div class="graph_area mg_t30">
                            <h3><spring:message code="E0454" text="발송결과"/></h3>
                            <table>
                            <tbody>
                                <tr>
                                    <td class="mini_table" >
                                        <table width="100%" height="auto" border="0" cellpadding="0" cellspacing="1" class="t_br2 b_br2">
                                            <colgroup>
                                                <col style="width:115px" />
                                                <col style="width:120px" />
                                                <col style="width:115px" />
                                                <col style="width:auto" />
                                            </colgroup>
                                            <tbody>
                                            <c:choose>
                                                <c:when test="${emsBean.total_send == 0 && success_count == 0 &&  fail_count == 0}">
                                                    <tr>
                                                        <td colspan="4" align="center">데이터가 존재하지 않습니다.</td>
                                                    </tr>
                                                </c:when>
                                                <c:otherwise>
                                                    <tr>
                                                        <th colspan="1"><spring:message code="E0455" text="발송메일수"/></th>
                                                        <td colspan="3">
                                                <span>
                                                        ${emsBean.total_send}<spring:message code="E0385" text="건"/>
                                                </span></td>
                                                    </tr>
                                                    <tr>
                                                        <th><spring:message code="E0456" text="성공"/></th>
                                                        <td><span>${success_count}<spring:message code="E0385" text="건"/></span></td>
                                                        <th><spring:message code="E0457" text="실패"/></th>
                                                        <td><span>${fail_count}<spring:message code="E0385" text="건"/></span></td>
                                                    </tr>
<%--                                                    <tr>--%>
<%--                                                      --%>
<%--                                                    </tr>--%>
                                                    <tr>
                                                        <th colspan="1"><spring:message code="E0458" text="성공율"/></th>
                                                        <td colspan="3"><span>
                                                <%--<c:if test="${not empty hcBean.eration}"> <!--hcBean? 미구현 예상 -->
                                                    ${hcBean.eration}%
                                                </c:if> TODO Controller에 hcBean 항목이 없으므로 계산이 불가함, 추후 해당 주석 소스 제거할지 판단 필요
                                                --%>
                                                            <!--성공율 임시계산-->
                                                <fmt:formatNumber value="${(success_count/emsBean.total_send ) * 100}" type="PERCENT" pattern="#" />%
                                            </span></td>
                                                    </tr>
                                                    <tr>
                                                        <th colspan="1"><spring:message code="E0459" text="시작시간"/></th>
                                                        <td colspan="3"><span>${emsBean.start_time}</span></td>
                                                    </tr>
                                                    <tr>
                                                        <th><spring:message code="E0460" text="종료시간"/></th>
                                                        <td><span>${emsBean.end_time}</span></td>
                                                        <th><spring:message code="E0461" text="소요시간"/></th>
                                                        <td><span>
                                                    <c:choose>
                                                        <c:when test="${empty emsBean.end_time}">
                                                            <spring:message code="E0763" text="-"/>
                                                        </c:when>
                                                        <c:when test="${time_diff == 0}">
                                                            <spring:message code="E0764" text="1분 이내"/>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <fmt:formatNumber value="${time_diff}" type="number" var="val" pattern="########"/>
                                                            ${val}<spring:message code="E0302" text="분"/>
                                                            </c:otherwise>
                                                    </c:choose>
<%--                                                             <c:if test="${time_diff == 0}">--%>
<%--                                                                 <c:if test="${empty emsBean.end_time}">--%>
<%--                                                                     <spring:message code="E0302" text="-"/>--%>
<%--                                                                 </c:if>--%>
<%--                                                                 <spring:message code="E0302" text="1분 이내"/>--%>
<%--                                                             </c:if>--%>
<%--                                                <c:if test="${time_diff != 0}">--%>
<%--                                                    ${time_diff}<spring:message code="E0302" text="분"/>--%>
<%--                                                </c:if>--%>
                                            </span></td>
                                                    </tr>
                                                </c:otherwise>
                                            </c:choose>
                                            </tbody>
                                        </table>
                                    </td>
                                    <td class="mini_table pd_l10">
                                        <table width="100%" height="auto" border="0" cellpadding="0" cellspacing="1" class="t_br2 b_br2">
                                            <colgroup>
                                                <col style="width:115px" />
                                                <col style="width:auto" />
                                            </colgroup>
                                            <tbody>
                                            <c:choose>
                                                <c:when test="${total_count == 0 &&  receiptCount  == 0 }">
                                                    <tr>
                                                        <td colspan="2" align="center">데이터가 존재하지 않습니다.</td>
                                                    </tr>
                                                </c:when>
                                                <c:otherwise>
                                                    <tr>
                                                        <th><spring:message code="E0462" text="전체메일발송 수"/></th>
                                                        <td><span>${total_count}<spring:message code="E0385" text="건"/></span></td>
                                                    </tr>
                                                    <tr>
                                                        <th><spring:message code="E0463" text="수신확인자 수"/></th>
                                                        <td><span>
                                                <c:if test="${not empty receiptCount}">
                                                    ${receiptCount}
                                                </c:if>
                                                <spring:message code="E0468" text="명"/></span></td>
                                                    </tr>
                                                    <tr>
                                                        <th><spring:message code="E0464" text="개봉율"/></th>
                                                        <td>
                                                <span>
                                                    <c:if test="${not empty openRate}">
                                                        ${openRate}%
                                                    </c:if>
                                                </span>
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <th><spring:message code="E0465" text="링크클릭 수"/></th>
                                                        <td><span>
                                                            <c:if test="${empty clickCount}">-</td></c:if>
                                                        <c:if test="${!empty clickCount}">
                                                                ${clickCount}<spring:message code="E0468" text="명"/></span></td></c:if>
                                                    </tr>
                                                    <tr>
                                                        <th><spring:message code="E0466" text="클릭율"/></th>
                                                        <td>
                                                <span>
                                                    ${clickRate}%
                                                </span>
                                                        </td>
                                                    </tr>
<%--                                                    <tr>--%>
<%--                                                        <th>&lt;%&ndash; 공백으로 두기로 함  &ndash;%&gt;</th>--%>
<%--                                                        <td><span>&lt;%&ndash; 공백으로 두기로 함  &ndash;%&gt;</span></td>--%>
<%--                                                    </tr>--%>
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
                    <!-- graph end -->

                    <div class="graph_area">
                        <div class="graph_area mg_t30">
                            <h3><spring:message code="E0480" text="주요에러"/></h3>
                            <table  width ="100%" height="auto" >
                                <tbody>
                                <tr>
                                    <td class="graph_lft ">
                                        <table width="100%" border="0" cellpadding="0" cellspacing="1" >
                                            <tbody>
                                            <tr>
                                                <td>
                                                    <c:if test="${empty errDataMap}">
                                                        <span>데이터가 존재하지 않습니다.</span>
                                                    </c:if>
                                                    <div id="chartdiv"></div>
                                                </td>
                                            </tr>
                                            </tbody>
                                        </table>
                                    </td>
                                    <td class="mini_table pd_l10">
                                        <table width ="100%" height="auto" >
                                            <colgroup>
                                                <col style="width:115px" />
                                                <col style="width:auto" />
                                            </colgroup>
                                            <tbody>
                                            <c:forEach var="entry" items="${errDataMap}" varStatus="status">
                                                <tr>
                                                    <th>${entry.key}
                                                        <%--<c:choose>
                                                            <c:when test="${entry.key eq '이메일주소 공백'}"><spring:message code="E0496" text="이메일주소 공백"/></c:when>
                                                            <c:when test="${entry.key eq 'User Unknown Error'}">User<br>Unknown Error</c:when>
                                                            <c:when test="${entry.key eq '서버연결 에러'}"><spring:message code="E0498" text="서버연결 에러"/></c:when>
                                                            <c:when test="${entry.key eq 'DNS 에러'}"><spring:message code="E0499" text="DNS 에러"/></c:when>
                                                            <c:when test="${entry.key eq '차단 도메인'}"><spring:message code="E0500" text="차단 도메인"/></c:when>
                                                            <c:when test="${entry.key eq '이메일형식 에러'}"><spring:message code="E0501" text="이메일형식 에러"/></c:when>
                                                            <c:when test="${entry.key eq '기타'}"><spring:message code="E0502" text="기타"/></c:when>
                                                            <c:when test="${entry.key eq '메일박스 FULL'}"><spring:message code="E0503" text="메일박스 FULL"/></c:when>
                                                            <c:when test="${entry.key eq '네트워크 에러'}"><spring:message code="E0504" text="네트워크 에러"/></c:when>
                                                            <c:when test="${entry.key eq '수신거부'}"><spring:message code="E0505" text="수신거부"/></c:when>
                                                            <c:when test="${entry.key eq '중복에러'}"><spring:message code="E0506" text="중복에러"/></c:when>
                                                            <c:when test="${entry.key eq '서버 에러'}"><spring:message code="E0507" text="서버 에러"/></c:when>
                                                            <c:when test="${entry.key eq '명령어 에러'}"><spring:message code="E0508" text="명령어 에러"/></c:when>
                                                            <c:when test="${entry.key eq '시스템 에러'}"><spring:message code="E0509" text="시스템 에러"/></c:when>
                                                            <c:when test="${entry.key eq 'Unknown Host'}"><spring:message code="E0510" text="Unknown Host"/></c:when>
                                                        </c:choose>--%>
                                                    </th>
                                                    <td><span>${entry.value}<spring:message code="E0385" text="건"/></span> / <span>
                                                <c:choose>
                                                    <c:when test="${entry.value > 0}">
                                                        <fmt:formatNumber value="${(entry.value/total_error_count ) * 100}" type="PERCENT" pattern="#.##" />%
                                                    </c:when>
                                                    <c:otherwise>0%</c:otherwise>
                                                </c:choose>
                                            </span></td>
                                                </tr>
                                            </c:forEach>

                                            <c:if test="${empty errDataMap}">
                                                <tr><td colspan="2" align="center">데이터가 존재하지않습니다.</td></tr>
                                            </c:if>
                                            </tbody>
                                        </table>
                                    </td>
                                </tr></tbody>
                            </table>
                        </div>
                    </div>
                    <div class="mg_t30 graph">
                        <!-- content area start -->
                        <div class="mini_table mg_t30">
                            <h3 class="f_lft"><spring:message code="E0471" text="도메인 결과분석"/></h3>
                            <p class="care_txt mg_t5 f_lft"><spring:message code="E0472" text="기타도메인"/>: ${hcCount}<spring:message code="E0473" text="개"/></p>
                            <table  width ="100%" height="auto" class="t_br2 b_br2">
                                <colgroup>
                                    <col style="width:auto" />
                                    <col style="width:80px" />
                                    <col style="width:80px" />
                                    <col style="width:80px" />
                                </colgroup>
                                <thead>
                                <tr>
                                    <th class="txt"><spring:message code="E0109" text="도메인명"/></th>
                                    <th class=""><spring:message code="E0469" text="발송건수"/></th>
                                    <th class=""><spring:message code="E0470" text="실패건수"/></th>
                                    <th class=""><spring:message code="E0458" text="성공율"/></th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach var="hcResult" items="${hcList}">
                                    <c:set var="eration" value="${(100*hcResult.ecount)/hcResult.scount}"/>
                                    <fmt:formatNumber value="${eration}" type="number" var="val" pattern="########"/>
                                    <tr>
                                        <td class="over_text"><span>${hcResult.hostname}</span></td>
                                        <td class=""><span>${hcResult.scount}</span><spring:message code="E0385" text="건"/></td>
                                        <td class=""><span>${hcResult.ecount}</span><spring:message code="E0385" text="건"/></td>
                                        <td class=""><span >${100-val}%</span></td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${ empty hcList}">
                                    <td height="46px" colspan="4" align="center"><spring:message code="E0481" text="도메인 결과분석 데이터가 존재하지 않습니다"/></td>
                                </c:if>
                                </tbody>
                            </table>
                        </div>
                        <!-- content area end -->
                    </div>
                </div>
                <!-- content area end -->
            </div>

        </div>
        <%--<div class="pop_footer">
        <div class="btn_div">
            <button class="close_btn btn2" type="button" onclick="report.closeReport();"><spring:message code="E0282" text="닫기"/></button>
        </div>
        </div>--%>
    </div>
</div>
<form:form id="saveForm" name="saveForm" method="post" action="htmlDownload.json">
    <input type="hidden" name="html" id="html">
    <input type="hidden" name="msgid" value="${ emsBean.msgid }">
</form:form>

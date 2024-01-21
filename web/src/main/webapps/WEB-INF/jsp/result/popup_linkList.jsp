<%--
  Created by IntelliJ IDEA.
  User: 신주현
  Date: 2022-04-17
  Time: 오후 11:49
  To change this template use File | Settings | File Templates.
--%>
<script type="text/javascript" src="/sens-static/js/result/result.js"></script>
<script type="text/javascript" src="${staticURL}/sens-static/js/common/linkpopupsearch.js"></script>
<%@ include file = "../inc/common.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
        <div class="w_pop ">
            <div class="statisticsLink_list w_content">
                <div class="popup_title over_text">
                    <div >
                        <h3 class="title"><spring:message code="E0660" text="링크 통계 목록보기"/></h3>
                    </div>
                    <form:form method="get" name="LinkListForm" id="LinkListForm" modelAttribute="linkListForm" action="statisticsLinkList.do">
                    <form:hidden path="msgid"/>
                    <form:hidden path="cpage" value="${pageInfo.cpage }"/>
                    <form:hidden path="linkid" value="${linkid}"/>
                    <div class="search_box">
                        <div class="inner">
                            <div class="select_box">
                                <form:select path="srch_type" class="search_opt" >
                                    <form:option value="01">field1(E-mail)</form:option>
                                    <form:option value="02">field2</form:option>
                                </form:select>
                            </div>
                            <form:input path="srch_keyword" id="srch_keyword" onfocus="linkpopupsearch.init(this)" value="" placeholder="검색어를 입력하세요."/>
                            <button type="button" class="btn1" onclick="linkPopupList.search();"><spring:message code="E0321" text="검색"/></button>
                            <c:if test="${issearch}"><button type="button" id="allview" class="btn2" onclick="linkPopupList.viewAll();"><spring:message code="E0649" text="전체목록"/></button></c:if>

                        </div>

                    </div>
                    <div  style="text-align: right; color: #999999; "><spring:message code="E0650" text="E-mail 검색 시 정확한 이메일을 입력해주세요."/></div>
                </div>

                </div>

                <!-- content top start -->
                <div class="content_top fixed">
                    <!-- 	<ul class="content_top_btn">
                             <li><a class="btn2" href="">목록저장</a></li>
                            <li><a class="btn2" href="">재발신</a></li>
                        </ul> -->
                    <div class="content_top_opt">
<%--                        <button ><img src="../../sens-static/images/top_opt_btn.png" alt="" /></button>--%>
<%--                        <button >설정</button>--%>
                        <a class="btn2" href="javascript:;" onclick="linkPopupList.downloadLinkList()"><spring:message code="E0073" text="목록저장"/></a>
                        <div class="drop_menu" style="display:none;" >
                            <table summary="목록화면입니다">
                                <caption><spring:message code="E0661" text="목록화면입니다"/></caption>
                                <tbody>
                                    <tr height="30px">
                                        <th scope="row"><span><spring:message code="" text="목록 개수"/></span></th>
                                        <td>
                                            <select onchange="common.change_pagesize(this.value)">
                                                <option ${f:isSelected('15',UserInfo.pagesize)} value="15"><spring:message code="E0233" text="15"/></option>
                                                <option ${f:isSelected('30',UserInfo.pagesize)} value="30"><spring:message code="E0234" text="30"/></option>
                                                <option ${f:isSelected('50',UserInfo.pagesize)} value="50"><spring:message code="E0235" text="50"/></option>
                                                <option ${f:isSelected('100',UserInfo.pagesize)} value="100"><spring:message code="E0236" text="100"/></option>
                                            </select>
                                        </td>
                                    </tr>
                                    <tr height="30px">
                                        <th scope="row"><span><spring:message code="E0041" text="목록 간격"/></span></th>
                                        <td>
                                            <select>
                                                <option><spring:message code="E0043" text="좁게"/></option>
                                                <option><spring:message code="E0045" text="보통"/></option>
                                                <option><spring:message code="E0044" text="넓게"/></option>
                                            </select>
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>


                </div>
                <!-- content top end -->

                <div class="section add_btn">
                    <!-- content start -->
                    <!-- <p class="over_text">데이터수 : 10개 (미리보기 데이터는 최대 100개까지만 조회됩니다.)</p> -->
                    <div class="article content">
                        <!-- content area start -->
                        <div class="content_area">
                            <table  width ="100%" height="auto" >
                                <colgroup>
                                    <col style="width:80px" />
                                    <col style="width:auto" />
                                    <col style="width:120px" />
                                    <col style="width:170px" />
                                    <col style="width:170px" />
                                    <col style="width:auto" />
                                    <col style="width:80px" />
                                </colgroup>
                                <thead >
                                    <tr>
                                        <th>No</th>
                                        <th>field1(E-mail)</th>
                                        <th>field2</th>
                                        <th>field3</th>
                                        <th>field4</th>
                                        <th class="txt center"><spring:message code="E0662" text="클릭 시간"/></th>
                                        <th><spring:message code="E0663" text="클릭 횟수"/></th>
                                    </tr>
                                </thead>
                                <tbody>
                                <c:if test="${ empty linkListUserForm }">
                                    <tr>
                                        <td colspan="7" class="txt center">
                                            <spring:message code="E0586" text="데이터가 없습니다." />
                                        </td>
                                    </tr>
                                </c:if>

                                    <c:forEach var="linkListUserForm" items="${linkListUserForm}">
                                    <tr>
                                        <td class="over_text">${linkListUserForm.userid}</td>
                                        <td class="over_text">${linkListUserForm.field1}</td>
                                        <td class="over_text">${linkListUserForm.field2}</td>
                                        <td class="over_text">${linkListUserForm.field3}</td>
                                        <td class="over_text">${linkListUserForm.field4}</td>
                                        <td class="list_2line txt center">
                                            <fmt:parseDate value="${linkListUserForm.click_time}" pattern="yyyyMMddHHmm" var="regdate"/>
                                            <fmt:formatDate value="${regdate}" pattern="yyyy-MM-dd"/>
                                            <span><fmt:formatDate value="${regdate}" pattern="HH:mm"/></span>
                                        </td>
                                        <td class=""><span>${linkListUserForm.click_count}</span><spring:message code="E0674" text="회"/></td>
                                    </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                        <!-- content area end -->
                    </div>
                    <!-- content end -->

                    <!-- nav start -->
                    <div class="page_nav">
                        <c:if test="${ empty linkListUserForm }">
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

                        <c:if test="${ !empty linkListUserForm }">
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
                </form:form>
            </div>
            <%--<div class="pop_footer">
            <div class="btn_div">
                <button class="close_btn btn2" onclick="window.close();"><spring:message code="E0282" text="닫기"/></button>
            </div>
            </div>--%>
        </div>

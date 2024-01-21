<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file = "/WEB-INF/jsp/inc/taglib.jspf" %>
<script type="text/javascript" src="${staticURL}/sens-static/js/receiver/address.js"></script>
<script type="text/javascript" src="${staticURL}/sens-static/js/common/addresssearch.js"></script>
<c:if test="${ !empty infoMessage }">
    <script>
        alert( '${infoMessage}' );
    </script>
</c:if>
<div class="address">
<!-- section start -->
<!-- top area start -->
<form:form name="AddressListForm" modelAttribute="AddressListForm" method="get" action="list.do">
    <form:hidden path="cpage" value="${ pageInfo.cpage }" />
    <form:hidden path="gkey" value="${AddressListForm.gkey}"/>
<div class="title_box fixed">
    <div class="article top_area">
        <div class="title">
            <h1><spring:message code="E0306" text="개인주소록 관리"/></h1>
            <p><spring:message code="E0307" text="발송하는 메일에 포함될 주소록을 관리할 수 있습니다."/></p>
        </div>
        <div class="search_box" style="margin-top: 5px;margin-bottom: 5px;">
            <div class="inner">
                <div class="select_box">
                    <form:select path="srch_type" cssClass="search_opt">
                        <form:option value="name"><spring:message code="E0018" text="이름"/></form:option>
                        <form:option value="email"><spring:message code="E0022" text="E-Mail"/></form:option>
                    </form:select>
                </div>
                <form:input path="srch_keyword" name="srch_keyword" onfocus="addresssearch.init(this)" placeholder="검색어를 입력해주세요."/>
                <button type="button" class="btn1" onclick="addressList.search()" >검색</button>
                <c:if test="${issearch}"><button type="button" id="allview" class="btn2" onclick="addressList.viewAll();">전체목록</button></c:if>
            </div>
           <%-- <div style="margin-left: 7px; color: #999999; ">E-mail 검색 시 정확한 이메일을 입력해주세요. ex) test@imoxion.com</div>--%>
        </div>
    </div>
</div>
<!-- top area end -->
    <div class="section top_nobtn">
<div class="content_top fixed2">
    <ul class="content_top_btn">
        <li><a class="btn2" href="javascript:;" onclick="addressList.addrGrpPopup();"><spring:message code="E0308" text="그룹추가"/></a></li>
    </ul>
</div>

    <!-- content top start -->
<div class="content_top fixed">
    <ul class="content_top_btn">
        <li><a class="btn2" href="javascript:;" onclick="addressList.add()"><spring:message code="E0029" text="추가"/></a></li>
        <li><a class="btn2" href="javascript:;" onclick="addressDel.delete()"><spring:message code="E0030" text="삭제"/></a></li>
        <li><a class="btn2" href="javascript:;" onclick="addressList.movePopup()"><spring:message code="E0309" text="이동"/></a></li>
        <li><a class="btn2" href="javascript:;" onclick="addressList.downloadAddrList()"><spring:message code="E0073" text="목록저장"/></a></li>
        <li><a class="btn2" href="javascript:;" onclick="addressList.doImport()"><spring:message code="E0074" text="가져오기"/></a></li>
        <li><a class="btn2" href="javascript:;" onclick="addressList.addressSetting()"><spring:message code="E0726" text="주소록 정보 복구"/></a></li>
        <c:if test="${!empty gname}">
        <div style="padding-top: 5px; text-align: right; color: #000000; font-size: 15px;" >그룹명 : "<b>${gname}</b>"</div>
        </c:if>
        <c:if test="${form.gkey == '0'}">
        <div style="padding-top: 5px; text-align: right; color: #000000; font-size: 15px;" >그룹명 : "<b>미분류</b>"</div>
        </c:if>
        <c:if test="${form.gkey == '-1'}">
            <div style="padding-top: 5px; text-align: right; color: #000000; font-size: 15px;" >그룹명 : "<b>전체주소록</b>"</div>
        </c:if>
    </ul>

<%--    <div class="content_top_opt">--%>
<%--        <select class="list_select" onchange="common.change_pagesize(this.value)">--%>
<%--            <option ${f:isSelected( '15' , UserInfo.pagesize ) } value="15">15</option>--%>
<%--            <option ${f:isSelected( '30' , UserInfo.pagesize ) } value="30">30</option>--%>
<%--            <option ${f:isSelected( '50' , UserInfo.pagesize ) } value="50">50</option>--%>
<%--            <option ${f:isSelected( '100' , UserInfo.pagesize ) } value="100">100</option>--%>
<%--        </select>--%>
<%--    </div>--%>
    </div>


<!-- content top end -->

    <div class="address_left_menu">
        <div class="article content add-menu-area" style="overflow-x:hidden;">
            <ul>
                <li class="add_group">
                    <div class="group_txt">
                        <a href="javascript:;" onclick="addressList.changeGkey('-1');" class="group-name txt strong"><spring:message code="E0310" text="전체주소록"/></a>
<%--                        <span class="btn_util" onclick="ddmenu.show(this,'grpOption-1')"></span>--%>
                    </div>
                    <span class="txt blue strong">${totalCount}</span>
                    <ul class="add_group_opt " id="grpOption-1"  style="display: none;">
                        <li><a href="javascript:;" onclick="addressList.delGrpPopup('-1');"><spring:message code="E0030" text="삭제"/></a></li>
                        <li><a href="javascript:;" onclick="addressList.downloadAddrGrp('-1')"><spring:message code="E0069" text="저장"/></a></li>
                    </ul>
                </li>
                <li class="add_group <c:if test="${AddressListForm.gkey == '0'}">active</c:if>">
                    <div class="group_txt">
                        <span class="icon_public"></span>
                        <a href="javascript:;" onclick="addressList.changeGkey('0');" class="group-name"><spring:message code="E0311" text="미분류"/></a>
                        <span class="btn_util" onclick="ddmenu.show(this,'grpOption0')"></span>
                    </div>
                    <span class="txt blue strong">${defaultCount}</span>
                    <ul class="add_group_opt " id="grpOption0"  style="display: none">
                        <li><a href="javascript:;" onclick="addressList.delGrpPopup('0');"><spring:message code="E0030" text="삭제"/></a></li>
                        <li><a href="javascript:;" onclick="addressList.downloadAddrGrp('0')"><spring:message code="E0069" text="저장"/></a></li>
                    </ul>
                </li>
                <c:forEach var="addressGroup" items="${addrGrpList}">
                    <li class="add_group <c:if test="${AddressListForm.gkey == addressGroup.gkey}">active</c:if>">
                        <div class="group_txt">
                            <span class="icon_public"></span>
                            <a href="javascript:;" id="name" onclick="addressList.changeGkey('${addressGroup.gkey}','${addressGroup.gname}');" class="group-name"><c:out value="${addressGroup.gname}"></c:out></a>
                            <span class="btn_util" onclick="ddmenu.show(this,'grpOption${addressGroup.gkey}')"></span>
                        </div>
                        <span class="txt blue strong">${addressGroup.count}</span>
                        <ul class="add_group_opt " id="grpOption${addressGroup.gkey}" style="display: none">
                            <li><a href="javascript:;" onclick="addressList.addrGrpPopup('${addressGroup.gkey}');"><spring:message code="E0128" text="수정"/></a></li>
                            <li><a href="javascript:;" onclick="addressList.delGrpPopup('${addressGroup.gkey}');"><spring:message code="E0030" text="삭제"/></a></li>
                            <li><a href="javascript:;" onclick="addressList.downloadAddrGrp('${addressGroup.gkey}')"><spring:message code="E0069" text="저장"/></a></li>
                        </ul>
                    </li>
                </c:forEach>
            </ul>
        </div>
    </div>

    <div class="address_body">
        <div class="article content">
            <!-- content area start -->
            <div class="content_area">
                <table  width ="100%" height="auto" style="border-bottom:1px solid #e5e5e5">
                    <colgroup>
                        <col style="width:25px" />
                        <col style="width:120px" />
                        <col style="width:auto" />
                        <col style="width:120px" />
                        <col style="width:120px" />
                        <col style="width:120px" />
                    </colgroup>
                    <thead class="fixed">
                    <tr >
                        <th class="check_ico">
                            <input id="all_check" type="checkbox"  onclick="common.select_all('ukeys');">
                        </th>
                        <th ><spring:message code="E0018" text="이름"/></th>
                        <th ><spring:message code="E0022" text="E-Mail"/></th>
                        <th ><spring:message code="E0315" text="휴대폰번호"/></th>
                        <th ><spring:message code="E0312" text="회사"/></th>
                        <th ><spring:message code="E0316" text="기타정보1"/></th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:if test="${ empty addrList }">
                        <td colspan="6" class="no_data"><spring:message code="E0586" text="데이터가 없습니다."/></td>
                    </c:if>
                    <c:forEach var="address" items="${addrList}">
                        <tr>
                            <td class="check_ico">
                                <input type="checkbox" name="ukeys" value="${address.ukey}">
                            </td>
                            <td>
                                <a href="javascript:;" onclick="addressList.edit('${address.ukey}')"><c:out value="${address.name}"></c:out></a>
                            </td>
                            <td>
                                <c:out value="${address.email}"></c:out>
                            </td>
                            <td>
                                <c:out value="${address.mobile}"></c:out>
                            </td>
                            <td>
                                <c:out value="${address.company}"></c:out>
                            </td>
                            <td>
                                <c:out value="${address.etc1}"></c:out>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
            <!-- content area end -->
        </div>
        <div class="page_nav">
            <c:if test="${ empty addrList }">
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
                <pt:jslink>addressList.list</pt:jslink>
            </pt:page>
            </c:if>
            <c:if test="${!empty addrList }">
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
                    <pt:jslink>addressList.list</pt:jslink>
                </pt:page>
            </c:if>
        </div>
        </form:form>
    <!-- content start -->
    </div>

    <!-- content end -->

</div>
<!-- section end -->

    <!-- Address group popup start-->
    <div class="popup" id="addrGrpPopup" style="display: none">
         <input type="hidden" id="group_key" />
        <div class="popup_content" style="height: 240px">
            <div class="popup_title">
                <span class="close_button" onclick="addressList.closePopup();"></span>
                <h3 class="title" id="popupTitle"><spring:message code="E0308" text="그룹 추가"/></h3>
            </div>
            <div class="popup_body_wrap">
                <div class="popup_body">
                    <!-- <p> 설명 텍스트 </p> -->
                    <table  border="0"  cellpadding="0" cellspacing="0" >
                        <colgroup>
                            <col style="width: 150px;">
                            <col style="width: auto;" >
                        </colgroup>
                        <thead>
                        <tr>
                            <th>
                                <spring:message code="E0319" text="주소록 그룹명"/>
                            </th>
                            <td>
                                <input id="grpName" maxlength="50" type="text">
                            </td>
                        </tr>
                        <tr>
                            <th>
                                <spring:message code="E0322" text="설명(메모)"/>
                            </th>
                            <td>
                                <input id="grpMemo" maxlength="100" type="text">
                            </td>
                        </tr>
                        </thead>
                    </table>

                </div>

            </div>
            <div class="pop_footer">
                <div class="btn_div">
                    <button type="button" class="close_btn btn2" onclick="addressList.saveGrp()"><spring:message code="E0064" text="확인"/></button>
                    <button type="button" class="close_btn btn2" onclick="addressList.closePopup();"><spring:message code="E0065" text="취소"/></button>
                </div>
            </div>
        </div>
    </div>
    <!-- address group popup end-->

    <!-- Delete Group popup start-->
    <div class="popup" id="deletegrpPopup" style="display: none;">
        <input type="hidden" id="deleteGkey" />
        <div class="popup_content" style="height: 250px" >
            <div class="popup_title">
                <span class="close_button" onclick="addressList.closeDelete();" ></span>
                <h3 class="title"><spring:message code="E0331" text="그룹삭제"/></h3>
            </div>
            <div class="popup_body_wrap">
                <div class="popup_body">
                    <!-- <p> 설명 텍스트 </p> -->
                    <table width="100%" border="0" cellpadding="0" cellspacing="1" >
                        <colgroup>
                            <col style="width:150px">
                            <col style="width:auto">
                        </colgroup>
                        <tbody>
                        <tr>
                            <th><spring:message code="E0332" text="삭제 옵션"/></th>
                            <td>
                                <select id="deleteGrpOption">
                                    <option value="0"><spring:message code="E0333" text="주소록 그룹만 삭제"/></option>
                                    <option value="1"><spring:message code="E0334" text="주소록 데이터만 삭제"/></option>
                                    <option value="2"><spring:message code="E0335" text="주소록 그룹, 데이터 모두 삭제"/></option>
                                </select>
                            </td>
                        </tr>
                        <tr >
                            <td colspan="2" class="care_txtAdd" id="msg">
                                <%--<span class="care_txt txt red block mg_t10">
                                    <strong><spring:message code="E0336" text="주소록 그룹만 삭제시 그룹에 포함되어 있는 주소록 데이터는 미분류 폴더로 이동합니다."/></strong>
                                </span>--%>
                            </td>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </div>
            <div class="pop_footer">
                <div class="btn_div">
                    <button class="btn2" type="button" onclick="addressList.deleteAddrGrp();"><spring:message code="E0030" text="삭제"/></button>
                    <button class="btn2" type="button" onclick="addressList.closeDelete();"><spring:message code="E0065" text="취소"/></button>
                </div>
            </div>
        </div>
    </div>
    <!--Delete Group popup end-->


    <!-- Move Address popup start-->
    <div class="popup" id="MoveAddrPopup" style="display: none;">
        <div class="popup_content" style="height: 200px" >
            <div class="popup_title">
                <span class="close_button" onclick="addressList.closeMove();" ></span>
                <h3 class="title"><spring:message code="E0415" text="주소록 이동"/></h3>
            </div>
            <div class="popup_body_wrap">
                <div class="popup_body">
                    <!-- <p> 설명 텍스트 </p> -->
                    <table width="100%" border="0" cellpadding="0" cellspacing="1" >
                        <colgroup>
                            <col style="width:150px;">
                            <col style="width:auto;">
                        </colgroup>
                        <tbody>
                        <tr>
                            <th><spring:message code="E0319" text="주소록 그룹명"/></th>
                            <td>
                                <select id="MoveAddrOption">
                                    <option value="0"><spring:message code="E0311" text="미분류"/></option>
                                    <c:forEach var="addressGroup" items="${addrGrpList}">
                                        <option value="${addressGroup.gkey}">${addressGroup.gname}</option>
                                    </c:forEach>
                                </select>
                            </td>
                        </tr>
                        </tbody>
                    </table>

                </div>
            </div>
            <div class="pop_footer">
                <div class="btn_div">
                    <button class="btn2" type="button" onclick="addressList.moveAddr();"><spring:message code="E0309" text="이동"/></button>
                    <button class="btn2" type="button" onclick="addressList.closeMove();"><spring:message code="E0065" text="취소"/></button>
                </div>
            </div>
        </div>
    </div>
    <!--Move Address popup end-->
</div>
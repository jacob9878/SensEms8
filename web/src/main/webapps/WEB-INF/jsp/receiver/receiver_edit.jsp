<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file = "/WEB-INF/jsp/inc/taglib.jspf" %>
<%@ include file="../common/encrypt.jsp" %>
<script type="text/javascript" src="${staticURL}/sens-static/js/receiver/receiver.js"></script>
<c:if test="${ !empty infoMessage }">
    <script>
        alert( '${infoMessage}' );
    </script>
</c:if>
<body class="skin1">
<div class="receivergroup_edit">
    <input type="hidden" id="cpage" value="${cpage}">
    <input type="hidden" id="srch_key" value="${srch_key}">
    <input type="hidden" id="srch_type" value="${srch_type}">
    <!-- section start -->
<!-- top area start -->
<div class="title_box fixed">
    <div class="article top_area">
        <div class="title">
            <h1><spring:message code="E0169" text="수신그룹 관리"/></h1>
            <p><spring:message code="E0293" text="수신그룹을 수정할 수 있습니다."/></p>
        </div>
    </div>
</div>
<!-- top area end -->
    <input type="hidden" id="chk_result" value="0">
<!-- content top start -->
<div class="content_top fixed">
    <ul class="content_top_btn">
        <li><a class="btn1" href="javascript:;" onclick="receiverSave.save('edit')"><spring:message code="E0069" text="저장"/></a></li>
        <li><a class="btn2" href="javascript:;" onclick="receiverList.receiverList()"><spring:message code="E0047" text="목록"/></a></li>
    </ul>
</div>
<!-- content top end -->

    <div class="section pd_l30">
    <!-- content start -->

    <div class="article content">
        <form:form method="post" name="ReceiverGroupForm" id="ReceiverGroupForm" modelAttribute="ReceiverGroupForm" action="edit.do">
        <form:hidden path="ukey"/>
        <form:hidden path="ori_name" value="${ReceiverGroupForm.recv_name}"/>
        <!-- composer area start -->
        <div class="composer_area">
            <table width="100%" border="0" cellpadding="0" cellspacing="1" >
                <colgroup>
                    <col style="width: 150px">
                    <col style="width: auto">
                </colgroup>
                <tbody>
                    <tr>
                        <th><spring:message code="E0171" text="수신그룹명"/></th>
                        <td>
                            <form:input path="recv_name" maxlength="50" />
                            <span class="txt red" style="font-size: 12px;">
                                <strong><spring:message code="E0297" text="사용가능한 특수 문자 : ’_’ , ’.’, ‘-’"/></strong>
                            </span>
                        </td>
                    </tr>
                    <tr class="table_database">
                        <th><spring:message code="E0174" text="데이터베이스 선택"/></th>
                        <td>
                        <form:select path="dbkey">
                            <form:option value=""><spring:message code="E0175" text="선택"/></form:option>
                            <form:options items="${DBList}" itemLabel="dbname" itemValue="ukey"/>
                        </form:select>
                        </td>
                    </tr>
                </tbody>
            </table>
            <table  class="queryBox" border="0"  cellpadding="0" cellspacing="0" >
                <tbody>
                    <tr>
                        <th class="b_r1"><spring:message code="E0180" text="필드선택"/></th>
                        <th><spring:message code="E0181" text="조건선택"/></th>
                    </tr>
                    <tr class="table_field">
                        <td class="b_r1">
                            <select id="field" name="field" size="3">

                            </select>
                        </td>
                        <td>
                            <select id="condition" name="condition" size="3">

                            </select>
                        </td>
                    </tr>
                    <tr class="table_field_btn">
                        <td class="b_r1">
                            <button type="button" id="addField" name ="addField" class="btn2" onclick="receiverList.openPopup('field')"><spring:message code="E0182" text="필드추가"/></button>
                            <button type="button" id="delField" name ="delField" class="btn2" onclick="receiverList.deleteField()"><spring:message code="E0183" text="필드삭제"/></button>
                        </td>
                        <td>
                            <button type="button" id="addCondition" name ="addCondition" class="btn2" onclick="receiverList.openPopup('condition')"><spring:message code="E0184" text="조건추가"/></button>
                            <button type="button" id="delCondition" name ="delCondition" class="btn2" onclick="receiverList.deleteCondition()"><spring:message code="E0185" text="조건삭제"/></button>
                        </td>
                    </tr>
                    <tr class="queryBox_btn">
                        <td colspan="2" align="center">
                            <button type="button" id="createQuery" class="btn1 tbl_ft_btn" onclick="receiverList.makeQuery()"><spring:message code="E0186" text="쿼리 생성하기"/><span class="add_query_icon"></button>
                        </td>
                    </tr>
                </tbody>
            </table>
            <table class="query_view b_b2"  border="0" cellpadding="0" cellspacing="1">
                <colgroup>
                    <col style="width: 720px">
                    <%--<col style="width: auto">--%>
                </colgroup>
                <tbody>
                    <tr>
                        <td>
                            <span><spring:message code="E0228" text="쿼리는 직접 작성할 수 있습니다."/></span>
                            <form:textarea path="query" cols="110" rows="9" cssStyle="resize: none;"/>
                            <button type="button" id="previewQuery" class="btn1 tbl_ft_btn" onclick="receiverList.previewQuery();"><spring:message code="E0583" text="결과보기"/></button>
                        </td>
                    </tr>
                </tbody>
            </table>

            <div id="hiddenTables" style="display: none">
                <select id="selectedTables">
                </select>
            </div>
        </div>
        <!-- composer area end -->
        </form:form>
    </div>
    <!-- content end -->

        <!-- Add Field popup start-->
        <div class="popup" id="addFieldPopup" style="display: none">
            <div class="popup_content" style="height:350px;">
                <div class="popup_title">
                    <span class="close_button" onclick="receiverList.closeFieldPopup();"></span>
                    <h3 class="title"><spring:message code="E0192" text="추출 필드 선택"/></h3>
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
                                <th>
                                    <spring:message code="E0193" text="테이블 선택"/>
                                </th>
                                <td>
                                    <select id="chooseTable" onchange="receiverList.getFields(this.value,'');">
                                        <option value=""><spring:message code="E0198" text="--선택--"/></option>
                                    </select>
                                </td>
                            </tr>
                            <tr>
                                <th>
                                    <spring:message code="E0194" text="필드 선택"/>
                                </th>
                                <td class="table_field pd_t10 pd_b10">
                                    <span class="block care_txt mg_b10">
									    <spring:message code="E0195" text="(필드 선택시에 이메일주소에 해당하는 필드를 선택하세요)"/>
								    </span>
                                    <select id="chooseField" multiple="multiple">
                                        <option value=""><spring:message code="E0198" text="--선택--"/></option>
                                    </select>
                                </td>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
                <div class="pop_footer">
                    <div class="btn_div">
                        <button type="button" class="btn2" onclick="receiverList.fieldSelect()"><spring:message code="E0064" text="확인"/></button>
                        <button type="button" class="btn2" onclick="receiverList.closeFieldPopup()"><spring:message code="E0065" text="취소"/></button>
                    </div>
                </div>
            </div>
        </div>
        <!-- popup end-->

        <!-- Add Condition popup start-->
            <div class="popup" id="addConditionPopup" style="display: none">
                <div class="popup_content">
                    <div class="popup_title">
                        <span class="close_button" onclick="receiverList.closeConditionPopup();"></span>
                        <h3 class="title"><spring:message code="E0181" text="조건선택"/></h3>
                    </div>
                    <div class="popup_body_wrap">
                        <div class="popup_body">
                            <!-- <p> 설명 텍스트 </p> -->
                            <table width="100%" border="0" cellpadding="0" cellspacing="1" >
                                <colgroup>
                                    <col style="width: 170px;">
                                    <col style="width: auto;">
                                </colgroup>
                                <tbody>
                                    <tr>
                                        <th>
                                            <spring:message code="E0193" text="테이블 선택"/>
                                        </th>
                                        <td>
                                            <select id="chooseTableConditionA" onchange="receiverList.getFields(this.value,'ConditionA');">
                                                <option value=""><spring:message code="E0198" text="--선택--"/></option>
                                            </select>
                                        </td>
                                    </tr>
                                    <tr>
                                        <th>
                                            <spring:message code="E0194" text="필드 선택"/>
                                        </th>
                                        <td>
                                            <select id="chooseFieldConditionA">
                                                <option value=""><spring:message code="E0198" text="--선택--"/></option>
                                            </select>
                                        </td>
                                    </tr>
                                    <tr>
                                        <th>
                                            <spring:message code="E0175" text="선택"/>
                                        </th>
                                        <td>
                                            <select id="sel_opt" onchange="receiverList.selOptionChange(this.value)">
                                                <option value="0"><spring:message code="E0207" text="같은 테이블에서 비교"/></option>
                                                <option value="1"><spring:message code="E0208" text="다른 테이블에서 비교"/></option>
                                            </select>
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                            <table id="same_table" style="display:block">
                                <colgroup>
                                    <col style="width: 170px;">
                                    <col style="width: auto;">
                                </colgroup>
                                <tbody>
                                    <tr>
                                        <th>
                                            <spring:message code="E0209" text="비교값1"/>
                                        </th>
                                        <td>
                                            <input id="compval1" type="text" />
                                        </td>
                                    </tr>
                                    <tr>
                                        <th>
                                            <spring:message code="E0210" text="조건"/>
                                        </th>
                                        <td>
                                            <select id="sel_same_opt" onchange="receiverList.selSameOptionChange(this.value)">
                                                <option value=""><spring:message code="E0198" text="--선택--"/></option>
                                                <option value="0"><spring:message code="E0211" text="같다(=)"/></option>
                                                <option value="1"><spring:message code="E0212" text="같지않다(!=)"/></option>
                                                <option value="2"><spring:message code="E0213" text="초과(>)"/></option>
                                                <option value="3"><spring:message code="E0214" text="미만(<)"/></option>
                                                <option value="4"><spring:message code="E0215" text="이상(>=)"/></option>
                                                <option value="5"><spring:message code="E0216" text="이하(<=)"/></option>
                                                <option value="6"><spring:message code="E0217" text="뒤에포함(LIKE '%A')"/></option>
                                                <option value="7"><spring:message code="E0218" text="중간에포함(LIKE '%A%')"/></option>
                                                <option value="8"><spring:message code="E0219" text="앞에포함(LIKE 'A%')"/></option>
                                                <option value="9"><spring:message code="E0220" text="범위의숫자(BETWEEN)"/></option>
                                                <option value="10"><spring:message code="E0221" text="문자열포함(IN)"/></option>
                                            </select>
                                        </td>
                                    </tr>
                                    <tr>
                                        <th>
                                            <spring:message code="E0222" text="비교값2"/>
                                        </th>
                                        <td>
                                            <input id="compval2" type="text"  disabled/>
                                        </td>
                                    </tr>
                                    <tr>
                                        <th>
                                            <spring:message code="E0223" text="이전조건과의 연결조건"/>
                                        </th>
                                        <td>
                                            <input id="same_multiConCheck" type="radio" name="same_join" value="" checked><spring:message code="E0224" text="없음"/>
                                            <input type="radio" name="same_join" value="AND" ><spring:message code="E0225" text="그리고(AND)"/>
                                            <input type="radio" name="same_join" value="OR" ><spring:message code="E0226" text="또는(OR)"/>
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                            <table id="another_table" style="display: none">
                                <colgroup>
                                    <col style="width: 170px;">
                                    <col style="width: auto;">
                                </colgroup>
                                <tbody>
                                <tr>
                                    <th>
                                        <spring:message code="E0193" text="테이블 선택"/>
                                    </th>
                                    <td>
                                        <select id="chooseTableConditionB" onchange="receiverList.getFields(this.value,'ConditionB');">
                                            <option value=""><spring:message code="E0198" text="--선택--"/></option>
                                        </select>
                                    </td>
                                </tr>
                                <tr>
                                    <th>
                                        <spring:message code="E0194" text="필드 선택"/>
                                    </th>
                                    <td>
                                        <select id="chooseFieldConditionB">
                                            <option value=""><spring:message code="E0198" text="--선택--"/></option>
                                        </select>
                                    </td>
                                </tr>
                                <tr>
                                    <th>
                                        <spring:message code="E0210" text="조건"/>
                                    </th>
                                    <td>
                                        <select id="sel_another_opt">
                                            <option value=""><spring:message code="E0198" text="--선택--"/></option>
                                            <option value="0"><spring:message code="E0211" text="같다(=)"/></option>
                                            <option value="1"><spring:message code="E0212" text="같지않다(!=)"/></option>
                                        </select>
                                    </td>
                                </tr>
                                <tr>
                                    <th >
                                        <spring:message code="E0223" text="이전조건과의 연결조건"/>
                                    </th>
                                    <td>
                                        <input id="another_multiConCheck" type="radio" name="another_join" value="" checked><spring:message code="E0224" text="없음"/>
                                        <input type="radio" name="another_join" value="AND" ><spring:message code="E0225" text="그리고(AND)"/>
                                        <input type="radio" name="another_join" value="OR" ><spring:message code="E0226" text="또는(OR)"/>
                                    </td>
                                </tr>
                                </tbody>
                            </table>
                            <div class="queryBox_btn">
                                <button class="btn1" type="button" onclick="receiverList.doMakeConQuery()"><spring:message code="E0227" text="적용"/></button>
                            </div>
                            <table style="display: inline-table">
                                <colgroup>
                                    <col style="width: 170px;">
                                    <col style="width: auto;">
                                </colgroup>
                                <tbody>
                                    <tr>
                                        <th >
                                            <spring:message code="E0187" text="생성쿼리"/>
                                        </th>
                                        <td class="pd_5">
                                            <textarea id="conQuery" cols="70" rows="9" style="resize: none;" readonly></textarea>
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                    <div class="pop_footer">
                        <div class="btn_div">
                            <button type="button" class="btn2" onclick="receiverList.conSubmit()"><spring:message code="E0064" text="확인"/></button>
                            <button type="button" class="btn2" onclick="receiverList.closeConditionPopup();"><spring:message code="E0065" text="취소"/></button>
                        </div>
                    </div>
                </div>
            </div>
            <!-- popup end-->
</div>
<!-- section end -->


</div>
</body>
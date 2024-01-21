<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file = "/WEB-INF/jsp/inc/taglib.jspf" %>
<script type="text/javascript" src="/sens-static/js/sysman/database.js"></script>
<script type="text/javascript">
    $(document).ready(function(){
        encrypt.enCryptInit();
        $("#encAESKey").val(encryptKey);

        /* input 값이 변경되면 연결테스트 재진행 */
        $("input").change(function() {
            connectTest = false;
        });
    });
</script>

<div class="admindatabase_add">
    <input type="hidden" id="cpage" value="${cpage}">


<!-- section start -->
<!-- top area start -->
<form:form modelAttribute="databaseForm" id="databaseForm" method="post" action="add.do">
    <form:hidden path="encAESKey" />
    <div class="title_box fixed">
        <div class="article top_area">
            <div class="title">
                <h1><spring:message code="E0126" text="데이터베이스 관리"/></h1>
                <p><spring:message code="E0132" text="데이터베이스 정보 추가"/></p>
            </div>
        </div>
    </div>
    <!-- top area end -->

    <!-- content top start -->
    <div class="content_top fixed">
        <ul class="content_top_btn">
            <li><a class="btn1" href="javascript:;" onclick="database.add();"><spring:message code="E0029" text="추가"/></a></li>
            <li><a class="btn2" href="javascript:;" onclick="database.goList();"><spring:message code="E0047" text="목록"/></a></li>
            <li><a class="btn2" href="javascript:;" onclick="database.doConnectTest();"><spring:message code="E0146" text="연결테스트"/></a></li>
        </ul>
    </div>
    <!-- content top end -->



    <div class="section pd_l30">
    <!-- content start -->

        <div class="article content">
            <!-- content area start -->
            <div class="composer_area">
                <table width="100%" border="0" cellpadding="0" cellspacing="1" >
                    <colgroup>
                        <col width="200px"/>
                        <col width="auto"/>
                    </colgroup>
                    <tbody>
                        <tr>
                            <th><spring:message code="E0134" text="데이터베이스 이름"/></th>
                            <td><form:input path="dbname" maxlength="20" cssErrorClass="error_textbox"/>
                                <div class="error_msg"><form:errors path="dbname" cssClass="error_msg"/></div>
                                <div class="care_txt">
                                    <span class="txt red"><spring:message code="E0297" text="사용가능한 특수 문자 : ’_’ , ’.’, ‘-’"/></span>
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <th><spring:message code="E0135" text="데이터베이스 유형"/></th>
                            <td>
                                <form:select path="dbtype">
                                    <form:option value="mysql"><spring:message code="E0136" text="MYSQL"/></form:option>
                                    <form:option value="oracle"><spring:message code="E0137" text="ORACLE"/></form:option>
                                    <form:option value="mssql"><spring:message code="E0138" text="MSSQL"/></form:option>
                                    <form:option value="tibero"><spring:message code="E0139" text="TIBERO"/></form:option>
                                </form:select>
                            </td>
                        </tr>
                        <tr id="host_div">
                            <th><spring:message code="E0140" text="DB 호스트"/></th>
                            <td><form:input path="dbhost" maxlength="20" size="15" cssErrorClass="error_textbox"/>
                                <div class="error_msg"><form:errors path="dbhost" cssClass="error_msg"/></div></td>
                        </tr>
                        <tr id="port_div">
                            <th><spring:message code="E0141" text="포트"/></th>
                            <td><form:input path="dbport" maxlength="20"  size="15" cssErrorClass="error_textbox"/>
                                <div class="error_msg"><form:errors path="dbport" cssClass="error_msg"/></div></td>
                        </tr>
                        <tr  id="real_dbname_div">
                            <th><spring:message code="E0196" text="DB명"/></th>
                            <td><form:input path="real_dbname" maxlength="20" size="15" cssErrorClass="error_textbox"/>
                                <div class="error_msg"><form:errors path="real_dbname" cssClass="error_msg"/></div></td>
                        </tr>
                        <tr id="oracle_svc_div" style="display: none">
                            <th><spring:message code="E0147" text="서비스 (IP : 포트 : SID)"/></th>
                            <td><form:input path="oracle_svc" maxlength="20" size="15" cssErrorClass="error_textbox"/>&nbsp;:
                            <form:input path="oracle_port" maxlength="20" size="5" cssErrorClass="error_textbox" cssClass="w100"/>&nbsp;:
                            <form:input path="oracle_sid" maxlength="20" size="10" cssErrorClass="error_textbox" cssClass="w100"/>&nbsp;<span>ex) 192.168.1.1 : 1521 : testdb</span>
                                <div class="error_msg">
                                    <form:errors path="oracle_svc" cssClass="error_msg"/>
                                    <form:errors path="oracle_port" cssClass="error_msg"/>
                                    <form:errors path="oracle_sid" cssClass="error_msg"/>
                                </div></td>
                        </tr>
                        <tr >
                            <th><spring:message code="E0142" text="접속 아이디"/></th>
                            <td><form:input path="dbuser" maxlength="20" size="15" cssErrorClass="error_textbox"/>
                                <div class="error_msg"><form:errors path="dbuser" cssClass="error_msg"/></div></td>
                        </tr>
                        <tr>
                            <th><spring:message code="E0143" text="접속 비밀번호"/></th>
                            <td><form:password path="dbpasswd" maxlength="20" autocomplete="false" size="15" cssErrorClass="error_textbox"/>
                                <div class="error_msg"><form:errors path="dbpasswd" cssClass="error_msg"/></div></td>
                        </tr>
                        <tr >
                            <th><spring:message code="E0144" text="DB CHARSET"/></th>
                            <td><form:input path="dbcharset" maxlength="20" size="15" cssErrorClass="error_textbox"/>
                                <div class="error_msg"><form:errors path="dbcharset" cssClass="error_msg"/></div></td>
                        </tr>
                        <%--<tr>
                            <th><spring:message code="E0145" text="DATA CHARSET"/></th>
                            <td><form:input path="datacharset" maxlength="20" size="15" cssErrorClass="error_textbox"/>
                                <div class="error_msg"><form:errors path="datacharset" cssClass="error_msg"/></div></td>
                        </tr>--%>
                    </tbody>
                </table>
            </div> <!-- content area end -->
        </div>
    </div>
</form:form>
<!-- section end -->

</div>
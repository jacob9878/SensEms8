<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page language="java"%>
<%@ include file="/WEB-INF/jsp/inc/taglib.jspf"%>
<!DOCTYPE HTML>
<html lang="${ language }">
<head>
    <title><spring:message code="title" text="SensEMS"/></title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=Edge"/>
    <meta id="_csrf" name="_csrf" content="${_csrf.token}"/>
    <meta id="_csrf_header" name="_csrf_header" content="${_csrf.headerName}"/>

    <link rel="stylesheet" type="text/css" href="${staticURL}/sens-static/css/style.css"/>
    <link rel="stylesheet" type="text/css" href="${staticURL}/sens-static/css/skin.css"/>
     <link rel="stylesheet" type="text/css" href="${staticURL}/sens-static/plugin/jquery/ui/theme/black/jquery.ui.all.css"/>

    <script type="text/javascript" src="${staticURL}/sens-static/js/messages/common_${UserInfo.language}.js"></script>
    <script type="text/javascript" src="${staticURL}/sens-static/plugin/jquery/jquery-2.2.2.min.js"></script>
    <script type="text/javascript" src="${staticURL}/sens-static/plugin/jquery/jquery.json-2.4.js"></script>
    <script type="text/javascript" src="${staticURL}/sens-static/plugin/jquery/ui/jquery-ui.min.js"></script>
    <script type="text/javascript" src="${staticURL}/sens-static/plugin/jquery/jquery.fileDownload.js"></script>
    <script type="text/javascript" src="${staticURL}/sens-static/plugin/jquery/jquery.formatdate.js"></script>
    <script type="text/javascript" src="${staticURL}/sens-static/js/common/common.js"></script>
    <script type="text/javascript" src="${staticURL}/sens-static/js/common/browsercheck.js"></script>
    <script type="text/javascript" src="${staticURL}/sens-static/js/common/imutil.js"></script>
    <script type="text/javascript" src="${staticURL}/sens-static/js/common/imoxion.js"></script>
    <script type="text/javascript" src="${staticURL}/sens-static/js/common/recvpopupsearch.js"></script>
    <script type="text/javascript" src="${staticURL}/sens-static/js/common/receiptpopupsearch.js"></script>
    <tiles:insertAttribute name="static" ignore="true"/>
</head>
<body class="skin1">
	<div class="w_pop">
        <div class="w_content">
            <tiles:insertAttribute name="body" />
        </div>
 	</div>
</body>
</html>
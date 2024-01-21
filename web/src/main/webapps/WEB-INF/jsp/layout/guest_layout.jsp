<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<!DOCTYPE HTML>
<html lang="${ language }">
<head>
 <title><spring:message code="title" text="SensEMS"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=Edge"/>
    <meta id="_csrf" name="_csrf" content="${_csrf.token}"/>
    <meta id="_csrf_header" name="_csrf_header" content="${_csrf.headerName}"/>
    <link rel="stylesheet" type="text/css" href="${staticURL}/sens-static/plugin/jquery/ui/theme/black/jquery.ui.all.css"/>
    <script type="text/javascript" src="${staticURL}/sens-static/js/messages/common_${language}.js"></script>
    <script type="text/javascript" src="${staticURL}/sens-static/plugin/jquery/jquery-2.2.2.min.js"></script>
    <script type="text/javascript" src="${staticURL}/sens-static/plugin/jquery/jquery.json-2.4.js"></script>
    <script type="text/javascript" src="${staticURL}/sens-static/plugin/jquery/ui/jquery-ui.min.js"></script>
	<script type="text/javascript" src="${staticURL}/sens-static/js/common/imoxion.js"></script>
</head>
<body>
<tiles:insertAttribute name="body"/>
</body>
</html>
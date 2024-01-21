<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page language = "java" %>
<%@ include file = "../inc/common.jsp" %>
<head>
	<title><spring:message code="title" text="SensEMS"/></title>
	<meta charset="utf-8" />
	<link rel="stylesheet" href="../../sens-static/css/style.css" />
	<link rel="stylesheet" href="../../sens-static/css/skin.css" />
	<script src="/sens-static/js/serial.js" type="text/javascript"></script>
       <script src="/sens-static/js/themes/light.js" type="text/javascript"></script>
	<!--[if lt IE 10]>
	<link type="text/css" rel="stylesheet" href="/sens-static/css/ie.css" />
	<![endif]-->

	<!--[if lt IE 9]>
	<script src="/sens-static/js/common/html5shiv.min.js"></script>
	<script src="/sens-static/js/common/ie9.min.js"></script>
	<![endif]-->

</head>
<%--<body>--%>
<%--	<div class="wrap error themes_border2">--%>
<%--		<p class="bi"><img src="/sens-static/images/common/top_bi.gif"></img></p>--%>
<%--		<div class="error_area">--%>
<%--			<span class="icon error"></span>--%>
<%--			<span><spring:message code="E0090" text="페이지를 찾을 수 없습니다."/></span>--%>
<%--			<button type="button" onclick="history.back();" class="btn themes_bg2"><span><spring:message code="E0091" text="돌아가기"/></span></button>--%>
<%--		</div>--%>
<%--	</div>--%>
<%--</body>--%>

<body class="skin1">
<div class="error-body">

	<div class="error-area txt center">
		<img src="../../sens-static/images/common/error.png" class="mg_b30" alt="" />
		<h1 class="mg_b10">서비스 이용에 불편을 드려 죄송합니다.</h1>
		<p class="mg_b30 txt gray">
			연결하려는 페이지에 문제가 있어 페이지를 표시할 수 없습니다.<br />
			이용에 불편을 드려 대단히 죄송합니다.<br />
		</p>
		<button type="button" onclick="history.back();" class="btn1"><span><spring:message code="E0091" text="돌아가기"/></span></button>
	</div>
</div>
</body>
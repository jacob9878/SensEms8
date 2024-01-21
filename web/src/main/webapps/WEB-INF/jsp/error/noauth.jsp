<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page language = "java" %>
<%@ include file = "../inc/common.jsp" %>
<head>
	<title><spring:message code="title" text="SensEMS"/></title>
	<meta charset="utf-8" />
	<link type="text/css" rel="stylesheet" href="/sens-static/css/common.css" />
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
<body>
	<div class="wrap error themes_border2">
		<p class="bi"><img src="/sens-static/images/common/top_bi.gif"></img></p>
		<div class="error_area">
			<span class="icon error"></span>
			<span><spring:message code="E0094" text="해당 프로그램을 사용할 권한이 없습니다."/></span>
			<button type="button" onclick="history.back();" class="btn themes_bg2"><span><spring:message code="E0091" text="돌아가기"/></span></button>
		</div>
	</div>
</body>

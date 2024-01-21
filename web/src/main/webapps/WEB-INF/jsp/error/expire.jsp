<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page language = "java" %>
<%@ include file = "../inc/common.jsp" %>
<head>
	<title><spring:message code="title" text="SensEMS"/></title>
	<meta charset="utf-8" />
<%--	<link type="text/css" rel="stylesheet" href="/sens-static/css/common.css" />--%>
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
<%--			<span><spring:message code="E0093" text="세션이 종료되었습니다."/></span>--%>
<%--			<button type="button" onclick="location.href='/account/login.do';" class="btn themes_bg2"><span><spring:message code="E0004" text="로그인"/></span></button>--%>
<%--		</div>--%>
<%--	</div>--%>
<%--</body>--%>
<body class="skin1">
<div class="error-body">

	<div class="error-area txt center">
		<img src="../../sens-static/images/common/error02.png" class="mg_b30" alt="">
		<h1 class="mg_b10">세션이 종료되었습니다.</h1>
		<p class="mg_b30 txt gray">로그인페이지로 이동하시겠습니까?
		</p>
		<button type="button"  onclick="location.href='/account/login.do';" class="btn1"><span><spring:message code="E0004" text="로그인"/></span></button>
	</div>
</div>
</body>
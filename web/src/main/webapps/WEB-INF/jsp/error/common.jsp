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

<body class="skin1">
<div class="error-body">

    <div class="error-area txt center">
        <img src="../../sens-static/images/common/error.png" class="mg_b30" alt="" />
        <h1 class="mg_b10">서비스 이용에 불편을 드려 죄송합니다.</h1>
        <c:choose>
            <c:when test="${empty message}">
                <p><span class="txt size16 strong"><spring:message code="E0060" text="작업중 오류가 발생하였습니다."/></span></p>
                <p><span class="txt lightgray"><spring:message code="E0524" text="관리자에게 문의해주세요."/></span></p>
            </c:when>
            <c:otherwise>
                <p><span class="mg_b30 txt gray">${message}</span></p>
                <p><span class="mg_b30 txt gray"><spring:message code="E0524" text="관리자에게 문의해주세요."/></span></p>
            </c:otherwise>
        </c:choose>
    </div>
</div>
</body>
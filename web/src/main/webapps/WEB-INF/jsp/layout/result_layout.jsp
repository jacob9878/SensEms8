<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/inc/taglib.jspf"%>
<!DOCTYPE HTML>
<html lang="${ language }">
<head>
    <title><spring:message code="title" text="SensEMS"/></title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=Edge"/>
    <meta id="_csrf" name="_csrf" content="${_csrf.token}"/>
    <meta id="_csrf_header" name="_csrf_header" content="${_csrf.headerName}"/>

	<link rel="stylesheet" type="text/css" href="${staticURL}/sens-static/css/style.css" />
	<link rel="stylesheet" type="text/css" href="${staticURL}/sens-static/css/skin.css" />
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
    <script type="text/javascript" src="${staticURL}/sens-static/js/result/result.js"></script>
    <script type="text/javascript" src="${staticURL}/sens-static/js/common/simplesearch.js"></script>
    <script type="text/javascript" src="${staticURL}/sens-static/js/common/testsendsearch.js"></script>

    <tiles:insertAttribute name="static" ignore="true"/>
   		<!--[if lt IE 10]>
		<link type="text/css" rel="stylesheet" href="/sens-static/css/ie.css" />
		<![endif]-->

		<!--[if lt IE 9]>
		<script src="/sens-static/js/common/html5shiv.min.js"></script>
		<script src="/sens-static/js/common/ie9.min.js"></script>
		<![endif]-->

</head>
<body class="skin1">
 	<div class="wrap">
            <tiles:insertAttribute name="menu" />
        <div class="statisticsSend"><!-- 왼쪽 class에 디자인값을 가변적으로 주는경우 주입을 해줄 필요가 있음  -->

    <div class="title_box fixed">
    <div class="article top_area">
    <div class="title">
    <h1><spring:message code="E0361" text="메일발송 결과"/></h1>
    <p><spring:message code="E0362" text="메일발송 결과나 통계를 볼 수 있습니다."/></p>
    </div>
    </div>
    </div>

    <!-- content top start -->
    <div class="content_top fixed">
        <input type="hidden" id="listcpage" value="${listcpage}">
        <input type="hidden" id="srch_key" value="${srch_key}">
    <ul class="content_top_btn">
    <%--  <li><a class="btn2" href="02001_statistics.html">목록으로</a></li>--%>
    <li><a class="btn2" href="javascript:;" onclick="resultList.listSend('${listcpage}','${srch_key}')"><spring:message code="E0570" text="목록으로"/></a></li>
    </ul>
    </div>
    <!-- content top end -->

    <div class="section pd_l30">
            <tiles:insertAttribute name="composer_area"/>
            <tiles:insertAttribute name="body" />
    </div>
        </div>
 	</div>
</body>
</html>
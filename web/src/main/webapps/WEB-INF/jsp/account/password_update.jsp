<%@ page contentType="text/html;charset=utf-8"%>
<%@ page language="java"%>
<%@ page import = "com.imoxion.sensems.web.common.*" %>
<%@ page import = "com.imoxion.sensems.web.beans.*" %>
<%@ page import = "com.imoxion.sensems.web.form.LoginForm" %>
<%@ page import = "java.util.*" %>
<%@ taglib prefix="c" uri="/WEB-INF/tld/c.tld"%>
<%@ taglib prefix="fmt" uri="/WEB-INF/tld/fmt.tld"%>
<%@ taglib prefix="fn" uri="/WEB-INF/tld/fn.tld"%>
<%@ taglib prefix="f" uri="/WEB-INF/tld/f.tld"%>
<%@ taglib prefix="pt" uri="/WEB-INF/tld/page-taglib.tld"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<!DOCTYPE HTML>
<html lang="ko-KR">
<head>
	<meta charset="UTF-8">
	<title>SensEMS</title>
	<link rel="stylesheet" href="/sens-static/css/style.css" />
	<link rel="stylesheet" href="/sens-static/css/login.css" />
	<link rel="stylesheet" href="/sens-static/css/skin.css" />	
	<script type="text/javascript" src="/sens-static/plugin/jquery/jquery-2.2.2.min.js"></script>
	    <script type="text/javascript" src="${staticURL}/sens-static/plugin/jquery/jquery.json-2.4.js"></script>
	     <script type="text/javascript" src="${staticURL}/sens-static/js/account/passwordUpdate.js"></script>
	     <script type="text/javascript" src="${staticURL}/sens-static/js/messages/sysman_${LoginForm.language}.js"></script>
	     <%@ include file="../common/encrypt.jsp" %>
</head>
<body class="skin1">
<form:form method="post" name="UpdatePasswordForm" modelAttribute="UpdatePasswordForm" action="updatePassword.do">
	<div class="login_form">
			<form:hidden path="encAESKey"/>
			<fieldset>
			<legend class="screen_out"><img src="/sens-static/images/login_logo.png" alt="" /></legend>
				<div class="box_login">
					<div class="inp_text">
					<label class="screen_out"><spring:message code="E0086" text="기존 비밀번호"/></label>
						<form:password path="password" value="" cssClass="loginId" autocomplete="off" placeholder="Password"/>
					</div>
					<div class="inp_text">
					<label class="screen_out"><spring:message code="E0087" text="새로운 비밀번호"/></label>
						<form:password path="newPassword" value="" cssClass="loginId" autocomplete="off" placeholder="New Password" />
					</div>
					<div class="inp_text">
						<label class="screen_out"><spring:message code="E0005" text="비밀번호 확인"/></label>
						<form:password path="confirmPassword" value="" cssClass="loginPw" autocomplete="off" placeholder="Confirm Password" />
					</div>
				</div>
				<ul class="login_append help_list">
					<spring:bind path="*">
						<c:if test="${ status.error }">
							<span style="color:#e65b5b">${status.errorMessage}</span></br>
						</c:if>
					</spring:bind>
					<c:forEach var="required" items="${passwordRequired}">
						<li<c:if test="${required.error}">style="color:#e65b5b;font-weight: bold;"</c:if>>${required.message}</li>
					</c:forEach>
				</ul>
			<button type="button" class="btn1 btn_login" onclick="updatePassword.updatePassword()"><spring:message code="E0088" text="변경하기"/></button>
			<button type="button" class="btn1 btn_login" onclick="updatePassword.changeNext()" ><spring:message code="E0089" text="다음에 변경하기"/></button>
			</fieldset>
		</form:form>
		<div class="copyright">
			<address>Copyright © IMOXION. All Rights Reserved.</address>
		</div>		
	</div>	

<c:if test="${ !empty error }">
	<script>alert("${ error }");</script>
</c:if>
</body>
</html>


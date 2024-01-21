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
<html lang="ko">
<head>
	<meta charset="UTF-8">
	<title>SensEMS</title>
	<link rel="stylesheet" href="/sens-static/css/style.css" />
	<link rel="stylesheet" href="/sens-static/css/login.css" />
	<link rel="stylesheet" href="/sens-static/css/skin.css" />
	<script type="text/javascript" src="/sens-static/plugin/jquery/jquery-2.2.2.min.js"></script>
	<script type="text/javascript" src="${staticURL}/sens-static/plugin/jquery/jquery.json-2.4.js"></script>
	<script type="text/javascript" src="${staticURL}/sens-static/js/common/browsercheck.js"></script>
	<script type="text/javascript" src="${staticURL}/sens-static/js/common/imoxion.js"></script>
	<script type="text/javascript" src="${staticURL}/sens-static/js/account/loginform.js"></script>
	<script type="text/javascript" src="${staticURL}/sens-static/js/messages/sysman_${LoginForm.language}.js"></script>
	<script type="text/javascript" src="${staticURL}/sens-static/js/messages/common_${LoginForm.language}.js"></script>
	<script>
		use_captcha=<%= ImbConstant.USE_CAPTCHA %>;
	</script>
	<%@ include file="../common/encrypt.jsp" %>
</head>
<body class="skin1">
<form:form method="post" name="LoginForm" modelAttribute="LoginForm" action="login.do" onsubmit="return login();">
<div class="right_menu">
	<c:if test="${ lang eq 1 }">
	<span class="icon_public"></span>
	<form:select path="language" cssClass="lang_list2" onchange="change_language()">
		<form:option value="ko"  label="KOR" />
		<form:option value="en"  label="ENG"/>
		<form:option value="ja"  label="JPN"/>
		<form:option value="zh"  label="CHN"/>
	</form:select>
		</c:if>



</div>
<div class="login_form">
	<form:hidden path="mode"/>
		<%--			<input type="hidden" id="mode" name="mode"/>--%>
	<form:hidden path="encAESKey"/>
	<fieldset>
		<legend class="screen_out"><img src="/sens-static/images/login_logo.png" alt="" /></legend>
		<div class="box_login">
			<div class="inp_text">
				<label class="screen_out"><spring:message code="E0001" text="아이디"/></label>
				<form:input path="userid" value="" cssClass="loginId" placeholder="ID"/>
			</div>
			<div class="inp_text">
				<label class="screen_out"><spring:message code="E0002" text="비밀번호"/></label>
				<form:password path="password" value="" cssClass="loginPw" autocomplete="off" placeholder="Password" />
			</div>
			<spring:bind path="userid">
				<c:if test="${ status.error }">
					<div class="pw_error" style="display:block;"><span class="txt red">${status.errorMessage}</span></div>
				</c:if>
			</spring:bind>
			<spring:bind path="password">
				<c:if test="${ status.error }">
					<div class="pw_error" style="display:block;"><span class="txt red">${status.errorMessage}</span></span>
				</c:if>
			</spring:bind>
		</div>
<%--		<div class="login_append">--%>
<%--			<div class="inp_chk"> <!-- 체크시 checked 추가 -->--%>
<%--				<form:checkbox path="isSave" value="1" cssClass="inp_radio" />--%>
<%--				<label class="lab_g">--%>
<%--					<span class="img_top ico_check"></span>--%>
<%--					<span class="txt_lab"><spring:message code="E0003" text="아이디 저장하기"/></span>--%>
<%--				</label>--%>
<%--			</div>--%>
			<c:if test="${useCaptcha}">
			<div class="captcha_wrap">
				<ul>
					<li id="li_captcha" class="captcha_area">
<%--					<button type="button" class="btn themes_bg2"  onclick="changeCaptcha();"><span>새로고침</span></button>--%>
					</li>

					<li>
						<label>
						<form:input path="answer" value="" cssClass="login_text" autocomplete="off"  placeholder="보안문자 입력"  maxlength="6"   tabindex="0"/>

					</label>
					</li>
					<spring:bind path="answer">
						<c:if test="${ status.error }" >
							<span style="color:#fc4c50;font-size:13px;margin: 3px 0 0 0;display: inline-block;">${status.errorMessage}</span>
						</c:if>
					</spring:bind>
				</ul>


			</div>
			</c:if>
			<button type="submit" class="btn1 btn_login" ><spring:message code="E0004" text="로그인"/></button>
			<div class="login_append">
				<div class="inp_chk"> <!-- 체크시 checked 추가 -->
					<form:checkbox path="isSave" value="1" cssClass="inp_radio" />
					<label class="lab_g">
						<span class="img_top ico_check"></span>
						<span class="txt_lab"><spring:message code="E0003" text="아이디 저장하기"/></span>
					</label>
				</div>
			</div>

	</fieldset>
	<div class="copyright">
		<address>Copyright © IMOXION. All Rights Reserved.</address>
	</div>
</div>
	</form:form>

</div>

<c:if test="${ !empty error }">
	<script>alert("${ error }");</script>
</c:if>

</body>
</html>


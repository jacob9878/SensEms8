<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<%@ include file = "../inc/common.jsp" %>
<script type="text/javascript" src="/sens-static/js/account/myinfo.js"></script>
<!-- 컨텐츠 영역 Start -->
<h1 class="themes_text2"><span class="themes_border2"><spring:message code="4" text="회원 정보  변경"/></span></h1>
<div class="contents_area">
	<!-- 회원정보 변경 (비밀번호 변경) 영역 Start -->
	<div class="mody_pass_area step2">
		<p class="icon user"></p>
		<p class="caption step1 themes_text2"><spring:message code="E0751" text="비밀번호 변경을 위해 기존 비밀번호를 확인합니다."/></p>
		<p class="caption step2 themes_text2"><spring:message code="E0752" text="현재 비밀번호와<br/>변경할 비밀번호를 입력해 주세요."/></p>
		<form:form modelAttribute="ImaUserinfo" class="edit_form step2" action="myyinfo.do" method="post">
		<input type="hidden" name="userid" id="userid" value="${userid}"/>
		<input type="hidden" name="isAdmin" id="isAdmin" value="${isAdmin}"/>
			<p class="pass"><label><input type="password" name="password" onfocus="pass_txt.style.display='none';" onblur="if (!this.value) pass_txt.style.display='inline-block'" maxlength="20" /><span id="pass_txt"><spring:message code="5" text="현재 패스워드"/></span></label></p>
			<p class="pass"><label><input type="password" name="new_password" onfocus="newpass_txt.style.display='none';" onblur="if (!this.value) newpass_txt.style.display='inline-block'" maxlength="20" /><span id="newpass_txt"><spring:message code="6" text="새로운 패스워드"/></span></label></p>
			<p class="pass"><label><input type="password" name="new_password1" onfocus="new1pass_txt.style.display='none';" onblur="if (!this.value) new1pass_txt.style.display='inline-block'" maxlength="20" /><span id="new1pass_txt"><spring:message code="7" text="새로운 패스워드 확인"/></span></label></p>
			<p class="error"><span class="w​arning_text"><form:errors path="password"/></span></p>
			<button type="button" onclick="update();" class="themes_bg2"><span><spring:message code="E0128" text="수정"/></span></button>
		</form:form>
	</div>
	<!-- 회원정보 변경 (비밀번호 변경) 영역 End -->
</div>
<!-- 컨텐츠 영역 End -->

<%@ page pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/inc/taglib.jspf"%>
<!-- 헤더 영역 Start -->
<p class="bi"><a href="<c:choose><c:when test="${UserInfo.admin }">/mail/result/list.do</c:when><c:otherwise>/search/search.do</c:otherwise></c:choose>"><img src="${staticURL}/sens-static/images/common/top_bi.gif" alt="SensArchiving"/></a></p>
<div class="header_menu">
	<ul>
	<li class="login"><span class="icon user"></span><span class="text">${UserInfo.name}<c:if test="${!empty UserInfo.email}">(${UserInfo.email})</c:if></span>&nbsp;&nbsp;|&nbsp;&nbsp;</li>
	<li class="login"><a href="javascript:;" onclick="common.myinfo();"><span><spring:message code="12" text="정보수정"/></span></a>&nbsp;&nbsp;|&nbsp;&nbsp;</li>
	<li class="login"><a href="javascript:;" onclick="common.logout();"><span><spring:message code="13" text="로그아웃"/></span></a></li>
	</ul>

	<p class="language">
		<label>
			<span class="text"></span><span class="arrow"></span>
			<select name="language" id="language" onchange="common.change_language(this.value);">
				<option value="ko" ${f:isSelected( 'ko' , language ) }>한국어</option>
				<option value="en" ${f:isSelected( 'en' , language ) }>English</option>
				<option value="ja" ${f:isSelected( 'ja' , language ) }>日本語</option>
				<option value="zh" ${f:isSelected( 'zh' , language ) }>中文</option>
			</select>
		</label>
	</p>

</div>
<!--	 헤더 영역 End -->

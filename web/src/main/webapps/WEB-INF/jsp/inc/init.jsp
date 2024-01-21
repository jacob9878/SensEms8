<%@ page pageEncoding = "UTF-8" %>
<%@ page import = "com.imoxion.sensems.web.common.*" %>
<%@ page import = "com.imoxion.sensems.web.beans.*" %>
<%@ page import = "java.util.*" %>
<%@ taglib prefix="c" uri="/WEB-INF/tld/c.tld"%>
<%@ taglib prefix="fmt" uri="/WEB-INF/tld/fmt.tld"%>
<%@ taglib prefix="fn" uri="/WEB-INF/tld/fn.tld"%>
<%@ taglib prefix="f" uri="/WEB-INF/tld/f.tld"%>
<%@ taglib prefix="pt" uri="/WEB-INF/tld/page-taglib.tld"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%	
	UserInfoBean userInfoBean = (UserInfoBean)session.getAttribute("UserInfo");
	String language = userInfoBean != null ? userInfoBean.getLanguage() : "ko";
%>
<c:set var="lang" value="${ UserInfo.language }" />

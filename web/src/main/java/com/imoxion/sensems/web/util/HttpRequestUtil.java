/**
 * 
 */
package com.imoxion.sensems.web.util;

import com.imoxion.sensems.web.common.ImbConstant;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author : sunggyu
 * @date : 2013. 2. 20.
 * @desc : request 관련 UTIL
 * 
 */
public class HttpRequestUtil {

	/**
	 * @Method Name : isAjaxCall
	 * @Method Comment : request가 ajax 호출인지 체크한다.
	 * @since 2013.02.20
	 * 
	 * @param request
	 * @return
	 */
	public static boolean isAjaxCall(HttpServletRequest request) {

		boolean isAjaxCall = false;
		String req_header = request.getHeader("x-requested-with");
		if (req_header != null && req_header.equalsIgnoreCase("XMLHttpRequest")) {
			isAjaxCall = true;
		}
		return isAjaxCall;
	}

	/**
	 * requestURI 에 contextPath 를 제외한 URI 를 제공한다.
	 * @param request
	 * @return
	 */
	public static String getRequestURI(HttpServletRequest request) {
		return request.getRequestURI().substring(request.getContextPath().length());
	}

	public static String getRequestURI(String requestURI, String contentPath) {
		return requestURI.substring(contentPath.length());
	}


	/**
	 * 환경에 따라 remoteAddr 로 client ip 를 구하지 못하는 경우가 있음.
	 * 해당 로직을 수정하여 환경에 맞게 client ip 를 구하는 로직을 수정하면 됨.
	 * @param request
	 * @return
	 */
	public static String getRemoteAddr(HttpServletRequest request) {
		return request.getRemoteAddr();
	}

    public static String getWebUrl(HttpServletRequest request) {
		String webUrl = "";
		String serverName = "";
		String protocol = "http://";
		if (ImbConstant.SSL_TYPE == ImbConstant.SSL_TYPE_TOTAL || request.getScheme().equalsIgnoreCase("https")) {
			protocol = "https://";
		}
		serverName = request.getServerName();
		webUrl = protocol + serverName;
		if (request.getServerPort() != 80 && request.getServerPort() != 443) {
			webUrl += ":" + request.getServerPort();
		}
		if (StringUtils.isNotEmpty(request.getContextPath())) {
			webUrl += request.getContextPath();
		}
		return webUrl;
    }
}
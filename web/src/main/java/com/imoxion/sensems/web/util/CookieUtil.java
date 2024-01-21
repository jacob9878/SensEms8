/**
 *
 */
package com.imoxion.sensems.web.util;

import com.imoxion.sensems.web.common.ImbConstant;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : jhpark
 * @date : 2021. 02. 16.
 * @desc :
 *
 */
public class CookieUtil {
	Map cookie = null;

	HttpServletResponse response = null;

	public CookieUtil(HttpServletRequest request, HttpServletResponse response) {
		this.response = response;
		Cookie[] cookies = request.getCookies();
		cookie = new HashMap();
		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				cookie.put(cookies[i].getName(), cookies[i].getValue());
			}
		}
	}

	public String get(String name) {
		return (String) cookie.get(name);
	}

	public void set(String key, String value) {
		Cookie ck = new Cookie(key, value);
        boolean secureType = false;
        if (ImbConstant.getInstance().SSL_TYPE == ImbConstant.SSL_TYPE_TOTAL) {
            secureType = true;
        }
		ck.setSecure(secureType);
		response.addCookie(ck);
	}
}

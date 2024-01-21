/**
 * 
 */
package com.imoxion.sensems.web.authentication;

import com.imoxion.common.util.ImCheckUtil;
import com.imoxion.sensems.web.beans.UserInfoBean;
import com.imoxion.sensems.web.common.SessionAttributeNames;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author : zpqdnjs
 * @date : 2021. 2. 16.
 * @desc : 사용자 인증관련 서비스
 * 
 */
@Service
public class AuthenticationUtil {

	/**
	 * 기본 세션정보가 있는지 존재하는지 체크한다.
	 * 
	 * @Method Name : hasUserSessionInfo
	 * @Method Comment :
	 * 
	 * @param request
	 * @return
	 */
	public boolean hasUserSessionInfo(HttpServletRequest request) {

		// 세션검사
		HttpSession session = request.getSession(false);
		if (session == null) {
			return false;
		}

		UserInfoBean userInfo = (UserInfoBean) session.getAttribute(SessionAttributeNames.USER_SESSION_INFO);
		if (userInfo == null || !ImCheckUtil.isNotEmpty(userInfo.getUserid())) {
			return false;
		}
		return true;
	}
}
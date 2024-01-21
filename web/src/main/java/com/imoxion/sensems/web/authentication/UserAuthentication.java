package com.imoxion.sensems.web.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author : zpqdnjs
 * @date : 2021. 02. 17
 * @desc : role은 있고 session이 없는경우 로그아웃 처리 ( was 재기동 등 )
 * 
 */
public class UserAuthentication extends HandlerInterceptorAdapter {

	@Autowired
    private RoleService roleService;
	
	@Autowired
	private AuthenticationUtil authenticationUtil;
	
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if(roleService.hasRole("ROLE_USER") || roleService.hasRole("ROLE_ADMIN")){
			if (!authenticationUtil.hasUserSessionInfo(request)) {
				SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
                logoutHandler.setClearAuthentication(true);
                logoutHandler.setInvalidateHttpSession(true);
                logoutHandler.logout(request, null, null);
                // 세션 무효화 및 권한 제거 후 세션 만료 페이지로 이동
                throw new ModelAndViewDefiningException(new ModelAndView("error/expire"));
	        }
		}
		return super.preHandle(request, response, handler);
	}
}
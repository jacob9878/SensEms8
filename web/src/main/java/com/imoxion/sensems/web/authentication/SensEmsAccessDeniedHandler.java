package com.imoxion.sensems.web.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 접근 권한이 없는 경우에 대한 처리
 * Created by zpqdnjs on 2021-02-16.
 */
public class SensEmsAccessDeniedHandler implements AccessDeniedHandler {
	private Logger logger = LoggerFactory.getLogger(SensEmsAccessDeniedHandler.class);

    private String notAllowPageUrl;

    public void setNotAllowPageUrl(String notAllowPageUrl) {
        this.notAllowPageUrl = notAllowPageUrl;
    }

    @Autowired
    public SensEmsAuthenticationEntryPoint sensEmsAuthenticationEntryPoint;

    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
        
        // 현재 사용자의 인증 정보가 없는 경우는 세션종료 페이지로 이동
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            logger.info("SensEmsAccessDeniedHandler Handlering - not allow");
            httpServletRequest.getRequestDispatcher(notAllowPageUrl).forward(httpServletRequest, httpServletResponse);
        } else {
            AuthenticationException authenticationException = new CredentialsExpiredException("Session Expire");
            sensEmsAuthenticationEntryPoint.commence(httpServletRequest, httpServletResponse, authenticationException);
        }
    }
}

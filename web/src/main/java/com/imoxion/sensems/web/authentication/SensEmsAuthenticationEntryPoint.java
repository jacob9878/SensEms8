package com.imoxion.sensems.web.authentication;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.imoxion.sensems.web.util.HttpRequestUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 인증되지 않은 사용자가 접근했을 때 수행되는 Entry Point.
 * Created by zpqdnjs on 2021-02-16.
 */
public class SensEmsAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private Logger logger = LoggerFactory.getLogger(SensEmsAuthenticationEntryPoint.class);

    /**
     * 로그인 페이지 URL
     */
    private String loginPageUrl;

    private String logoutUrl;

    /**
     * 세션 종료 안내 페이지 URL
     */
    private String sessionExpireInfoUrl;

    public void setLoginPageUrl(String loginPageUrl) {
        this.loginPageUrl = loginPageUrl;
    }

    public void setLogoutUrl(String logoutUrl) {
        this.logoutUrl = logoutUrl;
    }

    public void setSessionExpireInfoUrl(String sessionExpireInfoUrl) {
        this.sessionExpireInfoUrl = sessionExpireInfoUrl;
    }

    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        if( HttpRequestUtil.isAjaxCall(httpServletRequest) ){
            httpServletResponse.setContentType("application/json");
            httpServletResponse.sendError(401);
        }else{
            /*
                로그아웃 URL 을 호출했거나 URL 을 직접 입력하고 들어왔을 경우에는 로그인 페이지로 바로 이동한다.
             */
            String contextPath = httpServletRequest.getContextPath();
            if( httpServletRequest.getRequestURI().equalsIgnoreCase( contextPath + logoutUrl ) || StringUtils.isEmpty(httpServletRequest.getHeader("referer"))){
                httpServletResponse.sendRedirect( contextPath + loginPageUrl);
            }else {
                // 그 외에는 세션이 종료되었다고 안내페이지로 이동한다.
                httpServletResponse.setStatus(401);
                httpServletRequest.getRequestDispatcher(sessionExpireInfoUrl).forward(httpServletRequest, httpServletResponse);
            }
        }
        logger.debug("CustomAuthenticationEntryPoint check");
    }
}
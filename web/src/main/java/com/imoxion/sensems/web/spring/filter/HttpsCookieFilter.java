package com.imoxion.sensems.web.spring.filter;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.imoxion.sensems.web.common.ImbConstant;

import java.io.IOException;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by zpqdnjs on 2021-02-17
 * 일부 SSL 적용시 https <-> http 간의 세션 공유가 되지 않는 현상 해결을 위한 Filter.
 */
public class HttpsCookieFilter extends OncePerRequestFilter {

	private Logger logger = LoggerFactory.getLogger(HttpsCookieFilter.class);

	private String sessionId = "JSESSIONID";

	private List<AntPathRequestMatcher> applyUrl;

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public void setApplyUrl(List<AntPathRequestMatcher> applyUrl) {
		this.applyUrl = applyUrl;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
		if(ImbConstant.SSL_TYPE == ImbConstant.SSL_TYPE_SPECIFIC || ImbConstant.SSL_TYPE == ImbConstant.SSL_TYPE_ANY ) {
			if (applyUrl != null) {
				for (AntPathRequestMatcher matcher : applyUrl) {
					if (matcher.matches(httpServletRequest)) {
						HttpSession session = httpServletRequest.getSession();
						String contextPath = httpServletRequest.getContextPath();
						if(StringUtils.isEmpty(contextPath)){
							contextPath = "/";
						}
						httpServletResponse.setHeader("SET-COOKIE",  sessionId + "="+ session.getId() +";MaxAge=-1;Path="+contextPath+";HttpOnly");
						logger.debug("Secure Session Cookie Create Ok");
						break;
					}
				}
			}
		}
		filterChain.doFilter(httpServletRequest,httpServletResponse);
	}
}
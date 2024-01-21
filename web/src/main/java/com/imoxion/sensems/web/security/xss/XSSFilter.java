package com.imoxion.sensems.web.security.xss;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * XSS Filter
 * - web.xml에 다음과 같이 설정하여 사용한다.
 *
 * Created by sunggyu on 2015-04-28.
 */
public class XSSFilter implements Filter {
    Logger logger = LoggerFactory.getLogger(XSSFilterWrapper.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String config_file = filterConfig.getInitParameter("config-file");
        if(StringUtils.isNotEmpty( config_file ) ){
            XSSFilterConfig.init(filterConfig.getServletContext().getRealPath(config_file));
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;

        //logger.info("XssFilter URL: {}", request.getRequestURI());
        // XSS Filter Ignore URL인경우 패스한다.
        if( XSSFilterConfig.isIgnoreURL(request.getRequestURI() ) ){
            filterChain.doFilter( servletRequest , servletResponse );
        }else{
            filterChain.doFilter( new XSSFilterWrapper( request ) , servletResponse );
        }
    }

    @Override
    public void destroy() {

    }
}

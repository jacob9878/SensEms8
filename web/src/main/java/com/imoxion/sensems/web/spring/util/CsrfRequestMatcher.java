package com.imoxion.sensems.web.spring.util;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by zpqdnjs on 2021-02-15.
 * CSRF 토큰 검사 예외처리
 */
public class CsrfRequestMatcher implements RequestMatcher {

    private Pattern allowedMethods = Pattern.compile("^(GET|HEAD|TRACE|OPTIONS)$");

    /**
     * CSRF 예외처리 Pattern
     */
    private List<AntPathRequestMatcher> requestIgnoreMatchers;

    public CsrfRequestMatcher(String[] ignoreUrlPattern){
        requestIgnoreMatchers = new ArrayList<>();
        if( ignoreUrlPattern != null ) {
            for(String pattern : ignoreUrlPattern){
                requestIgnoreMatchers.add(new AntPathRequestMatcher(pattern));
            }
        }
    }

    @Override
    public boolean matches(HttpServletRequest httpServletRequest) {
        if( allowedMethods.matcher( httpServletRequest.getMethod() ).matches() ){
            return false;
        }
        for(AntPathRequestMatcher matcher : requestIgnoreMatchers){
            if( matcher.matches(httpServletRequest) ){
                return false;
            }
        }
        return true;
    }
}

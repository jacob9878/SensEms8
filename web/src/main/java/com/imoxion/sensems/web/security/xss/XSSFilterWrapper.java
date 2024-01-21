package com.imoxion.sensems.web.security.xss;

import com.imoxion.sensems.web.util.HttpRequestUtil;
import com.nhncorp.lucy.security.xss.XssPreventer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * Created by sunggyu on 2015-04-28.
 */
public class XSSFilterWrapper extends HttpServletRequestWrapper {

    Logger logger = LoggerFactory.getLogger(XSSFilterWrapper.class);

    private HttpServletRequest request = null;

    public XSSFilterWrapper(HttpServletRequest request) {
        super(request);
        this.request = request;
    }

    @Override
    public String getParameter(String name) {
        String uri = super.getRequestURI();
        String value = super.getParameter(name);
        
        if (value == null) {
        	return null;
        }

        if( XSSFilterConfig.isExceptionUri(uri, name) ) {
            return value;
        }else{
            return XssPreventer.escape( value );
        }
    }

    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        //logger.debug("XSSFilter Header - {}:{}",name,value);
        return XssPreventer.escape(value);
    }

    @Override
    public String[] getParameterValues(String parameter) {
        String[] values = super.getParameterValues(parameter);

        if (values == null) {
            return null;
        }

        String uri = HttpRequestUtil.getRequestURI(request);

        int count = values.length;
        String[] encodedValues = new String[count];
        for (int i = 0; i < count; i++) {
            //logger.debug("XSSFilter ParameterValues :: {} - {} : {}",uri, parameter,values[i]);
            if( XSSFilterConfig.isExceptionUri( uri , parameter ) ){
                encodedValues[i] = values[i];
            }else {
                encodedValues[i] = XssPreventer.escape(values[i]);
//                logger.debug("XSSFilter Parameter Escape - {}:{}",uri,parameter);
            }
        }
        return encodedValues;
    }
}
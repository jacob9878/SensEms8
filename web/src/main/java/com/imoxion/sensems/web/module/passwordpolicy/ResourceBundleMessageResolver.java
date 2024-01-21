package com.imoxion.sensems.web.module.passwordpolicy;

import org.passay.AbstractMessageResolver;
import org.passay.RuleResultDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.Map;
import java.util.ResourceBundle;

public class ResourceBundleMessageResolver extends AbstractMessageResolver {

    private Logger logger = LoggerFactory.getLogger(ResourceBundleMessageResolver.class);

    private ResourceBundle resourceBundle;

    public ResourceBundleMessageResolver(ResourceBundle resourceBundle){
        this.resourceBundle = resourceBundle;
    }

    @Override
    protected String getMessage(String s) {
        return this.resourceBundle.getString(s);
    }

    @Override
    public String resolve(RuleResultDetail detail) {
        String key = detail.getErrorCode();
        String message = this.getMessage(key);
        String format;
        if (message != null) {
            logger.debug(key);
            logger.debug(message);
            logger.debug(detail.getParameters().toString());
            Object[] arg = new Object[detail.getParameters().size()];
            int i = 0;
            for( Map.Entry<String, Object> entry : detail.getParameters().entrySet() ){
                arg[i++] = entry.getValue();
            }
            return MessageFormat.format(message,arg);
        } else if (!detail.getParameters().isEmpty()) {
            format = String.format("%s:%s", key, detail.getParameters());
        } else {
            format = String.format("%s", key);
        }
        return format;
    }
}
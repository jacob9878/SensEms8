package com.imoxion.sensems.server.util;

import org.apache.commons.lang.StringUtils;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class ServerMessageSource {

    static ResourceBundle resourceBundle;
    static{
        resourceBundle = ResourceBundle.getBundle("server");
    }

    public static String getMessage(String code , String defaultMessages , Object...objects){
        String message;
        if( resourceBundle.containsKey(code) ){
            message = resourceBundle.getString( code );
        }else{
            if(StringUtils.isEmpty(defaultMessages) ){
                return "";
            }
            message = defaultMessages;
        }
        return MessageFormat.format(message, objects );
    }

    public static String getMessage(String code , String defaultMessages ){
        if( resourceBundle.containsKey(code) ){
            return resourceBundle.getString( code );
        }else{
            if(StringUtils.isEmpty(defaultMessages) ){
                return "";
            }
            return defaultMessages;
        }
    }

    public static String getMessage(String code , Object...objects){
        String message;
        if( resourceBundle.containsKey(code) ){
            message = resourceBundle.getString( code );
        }else{
            return "";
        }
        return MessageFormat.format( message, objects );
    }

    public static String getMessage(String code ){
        if( resourceBundle.containsKey(code) ){
            return resourceBundle.getString( code );
        }else{
            return "";
        }
    }
}
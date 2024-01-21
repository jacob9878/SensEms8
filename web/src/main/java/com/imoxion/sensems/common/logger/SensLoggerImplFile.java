package com.imoxion.sensems.common.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Administrator on 2014-12-19.
 */
public class SensLoggerImplFile implements SensLogger {

    private Logger logger = null;

    public SensLoggerImplFile(String loggerName){
        logger = LoggerFactory.getLogger( loggerName );
    }

    @Override
    public void info(Object logData) {
    	/*String s = toLogString(logData);
    	if(StringUtils.isEmpty(s)) return;
    	
        logger.info( s );*/
    	return;
    }

    @Override
    public void error(Object logData) {
    	/*String s = toLogString(logData);
    	if(StringUtils.isEmpty(s)) return;
    	
        logger.error( s );*/
    	return;
    }

    @Override
    public void debug(Object logData) {
    	/*String s = toLogString(logData);
    	if(StringUtils.isEmpty(s)) return;
    	
        logger.debug( s );*/
    	return;
    }

    private String toLogString(Object logData){
        StringBuffer logBuffer = new StringBuffer();
//        logBuffer.append("DOMAIN:").append( domain ).append(",").append("USERID:").append( userid ).append(",");
//        if( data != null ){
//            Iterator<String> keys = data.getKeyList();
//            while( keys.hasNext() ){
//                String key = keys.next();
//                logBuffer.append( key ).append(":").append( data.get( key ) ).append(",");
//            }
//        }
        return logBuffer.toString();
    }
}
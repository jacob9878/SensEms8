package com.imoxion.sensems.common.logger;

import com.imoxion.common.util.ImConfLoaderEx;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Administrator on 2014-12-19.
 */
public class SensLoggerFactory {

    private static Logger logger = LoggerFactory.getLogger( SensLoggerFactory.class );

    private static final String SENSLOGGER_TYPE_SQLITE = "sqlite";

    private static final String SENSLOGGER_TYPE_MONGODB = "mongodb";

    private static final String SENSLOGGER_TYPE_FILE = "file";

    private static Map<String,SensLogger> sensLoggerMap;

    static{
        sensLoggerMap = new ConcurrentHashMap<String,SensLogger>();
    }

    public static SensLogger getLogger(String loggerName ){
        if( !sensLoggerMap.containsKey( loggerName ) ){

            // sensmail.xml에서 로그 방식이 대해서 구한다.
       	 	ImConfLoaderEx conf = new ImConfLoaderEx("sensems.home","sensems.xml","utf-8");
            String senslogger_type = conf.getProfileString("senslogger.type");
            // 로그 방식이 설정되어있지 않을 경우 기본 파일로 지정한다.
            if(StringUtils.isEmpty( senslogger_type ) ){
                senslogger_type = SENSLOGGER_TYPE_FILE;
            }
            logger.info("SensLogger Type : {}",senslogger_type);

            SensLogger sensLogger = null;
            if( senslogger_type.equalsIgnoreCase( SENSLOGGER_TYPE_SQLITE ) ){

            }else if( senslogger_type.equalsIgnoreCase( SENSLOGGER_TYPE_MONGODB ) ){
                sensLogger = new SensLoggerImplMongoDB(loggerName);
            }else if( senslogger_type.equalsIgnoreCase( SENSLOGGER_TYPE_FILE ) ){
                sensLogger = new SensLoggerImplFile(loggerName);
            }
            sensLoggerMap.put( loggerName , sensLogger );
        }
        return sensLoggerMap.get( loggerName );
    }
}
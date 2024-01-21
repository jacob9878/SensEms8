package com.imoxion.sensems.common.logger;


import org.apache.log4j.Logger;

/**
 * Created by Administrator on 2014-12-19.
 */
public class SensLoggerImplMongoDB implements SensLogger {

    private Logger logger = null;

    public SensLoggerImplMongoDB(String loggerName){
        logger = Logger.getLogger( loggerName );
    }

    @Override
    public void info(Object logData){
        logger.info(logData);
    }

    @Override
    public void error(Object logData) {
        logger.error(logData);
    }

    @Override
    public void debug(Object logData) {
        logger.debug(logData);
    }
}
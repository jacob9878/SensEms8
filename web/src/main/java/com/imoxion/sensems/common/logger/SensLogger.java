package com.imoxion.sensems.common.logger;

/**
 * Created by Administrator on 2014-12-19.
 */
public interface SensLogger {

    public void info(Object logData);

    public void error(Object logData);

    public void debug(Object logData);
}
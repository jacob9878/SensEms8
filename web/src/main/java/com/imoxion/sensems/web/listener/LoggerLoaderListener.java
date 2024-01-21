package com.imoxion.sensems.web.listener;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class LoggerLoaderListener implements ServletContextListener{

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        //Log4jWebConfigurer.initLogging(servletContextEvent.getServletContext());

        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        try {
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(loggerContext);
            String logConfigFilePath = servletContextEvent.getServletContext().getRealPath("/WEB-INF/config/logback/logback.xml");
            loggerContext.reset();
            configurator.doConfigure(logConfigFilePath);
        } catch (JoranException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}

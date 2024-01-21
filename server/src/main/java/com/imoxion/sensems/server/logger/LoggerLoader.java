package com.imoxion.sensems.server.logger;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import org.slf4j.LoggerFactory;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;

import java.io.File;

public class LoggerLoader {
    
    public static void initLog(String env, String sPropFile) {
        String prefix = "";
        String sPath = System.getProperty( env );
        prefix = sPath + File.separator + "conf";
        // String file = getInitParameter("log4j-init-file");
        String file = sPropFile;
System.out.println("log file = " + prefix + "/" + file);

        if (file != null) {
            LoggerContext ic = (LoggerContext) LoggerFactory.getILoggerFactory();
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(ic);
            ic.reset();

            try{
                configurator.doConfigure(prefix + "/" + file);
            }catch(Exception e){}

            // System.out.println 로그를 logback 의 CONSOLE 로그에 찍히도록 수정
            SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
        }
    }

    public static void initLog(String sPropFile) {
        initLog("sensems.home", sPropFile);
    }
}
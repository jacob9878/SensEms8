package com.imoxion.sensems.common.logger;

import org.apache.log4j.xml.DOMConfigurator;

import java.io.File;

/**
 * Created by sunggyu on 2016-03-23.
 */
public class Log4jLoader {

    public static void initLog(String sPropFile) {
        File propFile = new File(sPropFile);
        if (sPropFile != null) {
            if (propFile.exists() && propFile.isFile()) {
                DOMConfigurator.configure(sPropFile);
            } else {
                String env = "sensems.home";
                initLog(env,sPropFile);
            }
        }
    }

    public static void initLog(String env , String sPropFile) {
        String sPath = System.getProperty(env);
        String prefix = sPath + File.separator + "conf";
        if (sPropFile != null) {
            DOMConfigurator.configure(prefix + File.separator + sPropFile);
        }
    }
}
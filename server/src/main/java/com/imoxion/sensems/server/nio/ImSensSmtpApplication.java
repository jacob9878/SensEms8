package com.imoxion.sensems.server.nio;

import com.imoxion.common.database.ImDatabaseConnectionEx;
import com.imoxion.sensems.server.config.ImSmtpConfig;
import com.imoxion.sensems.server.environment.SensEmsEnvironment;
import com.imoxion.sensems.server.logger.ErrorTraceLogger;
import com.imoxion.sensems.server.logger.LoggerLoader;
import com.imoxion.sensems.server.nio.define.ServiceType;
import com.imoxion.sensems.server.nio.smtp.ImSmtpServer;
import com.imoxion.sensems.server.nio.ssl.ImSSLContext;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;

import java.io.IOException;
import java.security.GeneralSecurityException;


public class ImSensSmtpApplication {
    public static Logger smtpLogger = LoggerFactory.getLogger("SMTP");
    public static Logger smailLogger = LoggerFactory.getLogger("SMAIL");
   // public static ImSmtpConfig m_config = ImSmtpConfig.getInstance();
   public static final String SMTP_SERVER_VERSION = "ESMTP imoxion SensEmsSmtpServer 8.0";

    public static void main(String[] args) throws Exception {
    	SensEmsEnvironment.init();

		SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();

		ImSmtpConfig m_config = ImSmtpConfig.getInstance();
		LoggerLoader.initLog("emslog.xml");

        ImDatabaseConnectionEx.init("sensems.home", "mybatis-config.xml");

        // 인증키체크(sensproxy.lic: 서버맥어드레스를 가지고 com.imoxion.sensproxy.common.service.LicenseService에서 생성하면 됨)
//        LicenseService licenseService = LicenseService.getInstance();
//        try {
//            licenseService.licenseCheck();
//            smtpLogger.info("License key is valid");
//        } catch (LicenseException e) {
//            if(e.getErrorCode() == LicenseException.NOT_AVAILABLE){
//                smtpLogger.error("License key is unavailable");
//            } else if(e.getErrorCode() == LicenseException.NOT_FOUND){
//                smtpLogger.error("License key file not found");
//            } else if (e.getErrorCode() == LicenseException.EXPIRE) {
//                smtpLogger.error("License key expired");
//            }
//            System.exit(0);
//        }

        smtpLogger.info( ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        smtpLogger.info( "SMTP CONFIG: {}", m_config.toString());
        smtpLogger.info( "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");        
        
        ImSSLContext sslContext = new ImSSLContext();
        ImSmtpServer imSmtpServer = ImSmtpServer.getInstance(sslContext);

        // shutdownhook
        Runtime.getRuntime().addShutdownHook(new SmtpShutdownHook(Thread.currentThread()));

        Thread smtpThread = new Thread(
            () -> {
                try {
                    imSmtpServer.start(ServiceType.SVC_DEFAULT);
                } catch (IOException e) {
                    String errorId = ErrorTraceLogger.log(e);
                    smtpLogger.error("[{}] - ImSensSmtpApplication.run IOException", errorId);
                } catch (GeneralSecurityException e) {
                    String errorId = ErrorTraceLogger.log(e);
                    smtpLogger.error("[{}] - ImSensSmtpApplication.run GeneralSecurityException", errorId);
                }
            }
        );
        smtpThread.setDaemon(true);
        smtpThread.start();

        Thread smtpSslThread = null;
        if( m_config.isUseSSL() ) {
            if (StringUtils.isNotEmpty(m_config.getCertPath()) && StringUtils.isNotEmpty(m_config.getCertPass())) {
                smtpSslThread = new Thread(
                    () -> {
                        try {
                            imSmtpServer.start(ServiceType.SVC_SSL);
                        } catch (IOException e) {
                            String errorId = ErrorTraceLogger.log(e);
                            smtpLogger.error("[{}] - ImSensSmtpApplication.run IOException", errorId);
                        } catch (GeneralSecurityException e) {
                            String errorId = ErrorTraceLogger.log(e);
                            smtpLogger.error("[{}] - ImSensSmtpApplication.run GeneralSecurityException", errorId);
                        }
                    }
                );
                smtpSslThread.setDaemon(true);
                smtpSslThread.start();
            }
        }

        Thread smtpIspThread = null;
        if( m_config.getIsUseIsp() ==  1) {
            smtpIspThread = new Thread(
                    () -> {
                        try {
                            imSmtpServer.start(ServiceType.SVC_ISP);
                        } catch (IOException e) {
                            String errorId = ErrorTraceLogger.log(e);
                            smtpLogger.error("[{}] - ImSensSmtpApplication.run IOException", errorId);
                        } catch (GeneralSecurityException e) {
                            String errorId = ErrorTraceLogger.log(e);
                            smtpLogger.error("[{}] - ImSensSmtpApplication.run GeneralSecurityException", errorId);
                        }
                    }
            );
            smtpIspThread.setDaemon(true);
            smtpIspThread.start();
        }

        try {
            smtpThread.join();
            if(smtpSslThread != null) smtpSslThread.join();
            if(smtpIspThread != null) smtpIspThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * 서비스 중지 체크
     */
    private static class SmtpShutdownHook extends Thread {
        private Thread mainThread;
        public SmtpShutdownHook(Thread thread) {
            this.mainThread = thread;
        }

        @Override
        public void run() {
            smtpLogger.info("+++++++++++++++++++++ Shutdown Called +++++++++++++++++++++");
            ImSmtpServer.getInstance().stop();
            smtpLogger.info("+++++++++++++++++++++ Shutdown Complete +++++++++++++++++++++");
        }
    }
    
    
}


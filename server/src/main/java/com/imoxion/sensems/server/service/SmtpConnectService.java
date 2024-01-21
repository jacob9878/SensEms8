package com.imoxion.sensems.server.service;

import com.imoxion.sensems.server.config.ImSmtpConfig;
import com.imoxion.sensems.server.logger.ErrorTraceLogger;
import com.imoxion.sensems.server.smtp.ImSmtpSession;
import com.imoxion.sensems.server.util.HAProxyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

public class SmtpConnectService {
    private static Logger smtpLogger = LoggerFactory.getLogger("SMTP");
    private static final SmtpConnectService smtpConnectService = new SmtpConnectService();
    private final static ConcurrentHashMap<String, Integer> m_sSmtpConnectServer = new ConcurrentHashMap<>();

    public static SmtpConnectService getInstance() {
        return smtpConnectService;
    }
    private SmtpConnectService(){}


    public ConcurrentHashMap<String, Integer> getSmtpConnectServer(){
        return m_sSmtpConnectServer;
    }

    /**
     * SMTP 서버에 동시 접속 허용 수 제한
     * @param smtps 접속한 서버의 IP
     * @return 성공 실패 여부
     */
    public boolean addSmtpConnectServer(ImSmtpSession smtps){
        boolean bRet = true;
        try{
            String mapKey = smtps.getPeerIP() + "_" + smtps.getTraceID();
            ImSmtpConfig smtpConfig = ImSmtpConfig.getInstance();
            // L4 스위치 아이피
            // sensproxy.xml : proxy.servers.server
            HAProxyService proxyService = HAProxyService.getInstance();
            boolean isHealthCheck = proxyService.isProxyServer(smtps.getPeerIP());
            // L4 스위치 아이피
            if(isHealthCheck){
                return true;
            }

//smtpLogger.info("getListLogIgnoreIP: " + smtpConfig.getListLogIgnoreIP());
//smtpLogger.info("ip: " + sRServer);

            Integer num = m_sSmtpConnectServer.compute(mapKey, (k, v) -> v == null ? 1 : v + 1 );
            int totalCount = m_sSmtpConnectServer.values().stream().reduce(0, Integer::sum);
//            if(smtpConfig.getIsLogConn() == 1) smtpLogger.debug( "addSmtpConnectServer: " + smtps.getPeerIP() + " - "+
//                    num +"  / IPs: " + m_sSmtpConnectServer.keySet().size() + " / total: " + totalCount);
            if(smtpConfig.getIsLogConn() == 1) smtpLogger.debug( "[{}] [{}] addSmtpConnectServer: {} - {}  / IPs: {} / total: {}", smtps.getTraceID(), mapKey, smtps.getPeerIP(), num, m_sSmtpConnectServer.keySet().size(), totalCount);

            int maxConnection = smtpConfig.getMaxConnection();
            if (totalCount > maxConnection) {
                smtpLogger.error("addSmtpconnectServer: Max concurrent connections Exceeded.({})", maxConnection);
                return false;
            }

            // 예외아이피인지 확인한다.
            if(smtpConfig.getMaxConcurrentConnectNoLimit().indexOf(smtps.getPeerIP()+",") >=0 ) {
                // 예외아이피라도 smtp 최대 커넥션의 80% 보다 많으면 제한
                if(num > smtpConfig.getMaxConnection() * 80/100){
                    smtpLogger.error( "addSmtpConnectServer : Too many concurrent connections from " + smtps.getPeerIP() + " (" + m_sSmtpConnectServer.get(mapKey) + ")");
                    bRet = false;
                }
            } else {
                // 접속 제한 설정 보다 많으면
                if(num >= smtpConfig.getMaxConcurrentConnect()){
                    smtpLogger.error( "addSmtpConnectServer : Too many concurrent connections from " + smtps.getPeerIP() + " (" + m_sSmtpConnectServer.get(mapKey) + ")");
                    bRet = false;
                }
            }

        }catch(Exception ex){
            String errorId = ErrorTraceLogger.log(ex);
            smtpLogger.error("{} - [SmtpServer] ", errorId );
        }
        return bRet;
    }


    public void delSmtpConnectServer(ImSmtpSession smtps){
        try{
            String sRServer = smtps.getPeerIP();
            String mapKey = smtps.getPeerIP() + "_" + smtps.getTraceID();
            // L4 스위치 아이피
            // sensproxy.xml : proxy.servers.server
            HAProxyService proxyService = HAProxyService.getInstance();
            boolean isHealthCheck = proxyService.isProxyServer(smtps.getPeerIP());
            // L4 스위치 아이피
            if(isHealthCheck){
                return;
            }

            Integer num = m_sSmtpConnectServer.compute(mapKey, (k, v) -> v == null ? 0 : v - 1 );
            int totalCount = m_sSmtpConnectServer.values().stream().reduce(0, Integer::sum);
//            if( num <= 0){
//                m_sSmtpConnectServer.remove(mapKey);
//                if(ImSmtpConfig.getConfig().getIsLogConn() == 1) smtpLogger.debug( "delSmtpConnectServer : " + sRServer + " - 0"+ " / total: " + totalCount);
//            } else {
//                if(ImSmtpConfig.getConfig().getIsLogConn() == 1) smtpLogger.debug( "delSmtpConnectServer : " + sRServer + " - " + num+ " / total: " + totalCount);
//            }
            if( num <= 0){
                m_sSmtpConnectServer.remove(mapKey);
                if(ImSmtpConfig.getInstance().getIsLogConn() == 1) smtpLogger.debug( "[{}] [{}] delSmtpConnectServer : {} - 0"+ " / total: {}", smtps.getTraceID(), mapKey, sRServer, totalCount);
            } else {
                if(ImSmtpConfig.getInstance().getIsLogConn() == 1) smtpLogger.debug( "[{}] [{}] delSmtpConnectServer : {} - {} / total: {}", smtps.getTraceID(), mapKey, sRServer, num, totalCount);
            }


        }catch(Exception ex){
            String errorId = ErrorTraceLogger.log(ex);
            smtpLogger.error("{} - [SmtpServer] ", errorId );
        }
    }
}

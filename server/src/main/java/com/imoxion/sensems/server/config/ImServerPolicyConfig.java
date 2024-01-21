package com.imoxion.sensems.server.config;

import com.imoxion.sensems.server.logger.ErrorTraceLogger;
import com.imoxion.sensems.server.repository.SmtpRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 서버 송수신 정책(imp_limit_info) 데이터 제공 서비스
 */
public class ImServerPolicyConfig {
    public Logger smtpLogger = LoggerFactory.getLogger("SMTP");
    private Logger logger = LoggerFactory.getLogger(ImServerPolicyConfig.class);

    public static final String LIMIT_BULK_MAIL_SIZE  = "004";   // 대용량큐로 전환되는 건당 메일 크기(MB)
    public static final String LIMIT_BULK_MAIL_COUNT  = "005";   // 대용량큐로 전환되는 From의 시간당 메일 건수
    public static final String LIMIT_BULK_MAIL_TOTAL_SIZE  = "006";   // 대용량큐로 전화되는 From의 시간당 메일 총 용량(MB)
    public static final String LIMIT_MAIL_COUNT  = "012";   // 1회 발송 시 최대 동보 수신자 수
    public static final String LIMIT_MAIL_SIZE  = "016";   // 1회 발송 시 최대 메일 크기(MB)
    public static final String LIMIT_MAIL_TOTAL_SIZE  = "017";   // 1회 발송 시 최대 총 메일 크기(수신자수 * 메일크기, MB)

    private static ImServerPolicyConfig serverPolicyConfig;

    public synchronized static ImServerPolicyConfig getInstance(){
        if( serverPolicyConfig == null ){
            serverPolicyConfig = new ImServerPolicyConfig();
        }
        return serverPolicyConfig;
    }

    private ConcurrentHashMap<String, String> mapLimitConf = null;

    private ImServerPolicyConfig(){
        reload();
    }

    public String get(String key){
        return mapLimitConf.get(key);
    }
    public long getLong(String key){
        return Long.parseLong(mapLimitConf.get(key));
    }
    public int getInt(String key){
        return Integer.parseInt(mapLimitConf.get(key));
    }


    /**
     * 서버의 정책 설정 값을 reload 한다.
     */
    public void reload(){
        try {
            SmtpRepository smtpDatabaseService = SmtpRepository.getInstance();
            this.mapLimitConf = smtpDatabaseService.getLimitInfo();
            smtpLogger.info("ImServerPolicyConfig.reload");
        }catch(Exception e){
            String errorId = ErrorTraceLogger.log(e);
            //e.printStackTrace();
            smtpLogger.error("{} - ImServerPolicyConfig reload error",errorId);
        }
    }
}
package com.imoxion.sensems.server.util;

import com.imoxion.common.util.ImIpUtil;
import com.imoxion.sensems.server.config.ImSmtpConfig;
import com.imoxion.sensems.server.logger.ErrorTraceLogger;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageHeaderUtil {

    private final static Logger logger = LoggerFactory.getLogger(MessageHeaderUtil.class);

    /**
     * 헤더의 received 값에서 등록된 앞단의 서버 IP를 제외한 최종 IP를 구한다.
     * @param received
     * @return
     */
    public static String getMailHeaderSendIp(String[] received){
        if(received == null){
            return null;
        }
        try{
            int receivedIpLength = received.length;
            for(int i=0; i<receivedIpLength; i++){
                boolean bCheck = true;
                String receivedIp = received[i];
                String[] recvIPs = getIP(receivedIp);
                String checkIP = null;
                if(recvIPs != null && recvIPs.length > 0){
                    checkIP = recvIPs[0];
                }
                // 내부 ip , 스팸 ip Skep
                if(StringUtils.isEmpty(checkIP)){
                    bCheck = false;
                }

                // 사설아이피 체크
                if( bCheck && !ImIpUtil.isPublicIP(checkIP) ){
                    bCheck = false;
                }
                if( bCheck && ImSmtpConfig.getInstance().getSpamServerList() != null ){
                    if( ImSmtpConfig.getInstance().getSpamServerList().contains(checkIP) ){
                        bCheck = false;
                    }
                }
//				smtpLogger.debug("FROM IP :{} - {}",checkIP, bCheck);
                if( !bCheck ){
                    continue;
                }
                return checkIP;
            }
            return null;
        }catch(Exception e){
            String errorId = ErrorTraceLogger.log(e);
            logger.error("{} - RECEIVED IP PARSING ERROR({})",errorId,e.getMessage());
            return null;
        }
    }

    public static String getMailHeaderSendIp(String[] received, String peerIP){

        List<String> ipList = getReceivedIPList(peerIP, received);

        for(String checkIP : ipList){
            boolean bCheck = true;

            // 내부 ip , 스팸 ip Skep
            if(StringUtils.isEmpty(checkIP)){
                bCheck = false;
            }
            // 사설아이피 체크
            if( bCheck && !ImIpUtil.isPublicIP(checkIP)){
                bCheck = false;
            }
            if( bCheck && ImSmtpConfig.getInstance().getSpamServerList() != null ){
                if( ImSmtpConfig.getInstance().getSpamServerList().contains(checkIP) ){
                    bCheck = false;
                }
            }
            if( !bCheck ){
                continue;
            }
            return checkIP;
        }

        return null;
    }

    private static List<String> getReceivedIPList(String peerIP, String[] received){
        List<String> ipList = new ArrayList<String>();
        ipList.add(peerIP);
        if(received != null){
            try{
                int receivedIpLength = received.length;
                for(int i=0; i<receivedIpLength; i++){
                    String receivedIp = received[i];
                    String[] recvIPs = getIP(receivedIp);
                    if(recvIPs != null && recvIPs.length > 0){
                        ipList.add(recvIPs[0]);
                    }
                }
            }catch(Exception e){
                String errorId = ErrorTraceLogger.log(e);
                logger.error("{} - getReceivedIPList({})",errorId,e.getMessage());
            }
        }
        return ipList;
    }

    private static String[] getIP(String str){
        Pattern p =
                Pattern.compile("((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[0-9]{1,2})\\.){3}(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[0-9]{1,2})");
        Matcher m = p.matcher(str);

        StringBuffer sb = new StringBuffer();
        while(m.find()){
            sb.append(m.group()+ " ");
        }
        return m.reset().find() ? sb.toString().split(" ") : new String[0];
    }
}

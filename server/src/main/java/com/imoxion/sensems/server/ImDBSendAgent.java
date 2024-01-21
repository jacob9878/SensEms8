package com.imoxion.sensems.server;

import com.imoxion.common.database.ImDatabaseConnectionEx;
import com.imoxion.common.mail.ImMessage;
import com.imoxion.common.net.ImSmtp;
import com.imoxion.common.util.ImConfLoaderEx;
import com.imoxion.common.util.ImLoadBalance;
import com.imoxion.common.util.ImStringUtil;
import com.imoxion.common.util.ImTimeUtil;
import com.imoxion.sensems.server.config.ImSmtpConfig;
import com.imoxion.sensems.server.domain.SmtpTempMain;
import com.imoxion.sensems.server.domain.SmtpTempRcpt;
import com.imoxion.sensems.server.environment.SensEmsEnvironment;
import com.imoxion.sensems.server.exception.SensemsException;
import com.imoxion.sensems.server.logger.ErrorTraceLogger;
import com.imoxion.sensems.server.logger.LoggerLoader;
import com.imoxion.sensems.server.repository.SmtpRepository;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeUtility;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * imp_temp_main, imp_temp_rcpt 테이블을 이용한 메일 발송
 */
public class ImDBSendAgent {
    public static Logger logger = LoggerFactory.getLogger("DBSEND");
    private static String g_sPlatform = "unix";
    private static int AGENT_FIRST_WAIT = 30 * 1000;
    private static int AGENT_SCAN_INTERVAL = 60 * 1000;
    public static final String MERGE_TO = "<[SENS-RCPT-TO]>";
    public static final String MERGE_RCPTKEY = "<[SENS-RCPT-KEY]>";

    public static final String MAIL_CHARSET = "UTF-8";
    private static ImLoadBalance loadBalanceSender = null;

    public static ImConfLoaderEx confSmtp = ImConfLoaderEx.getInstance("sensems.home","smtp.xml");

    static class SendAgent extends Thread {
        public SendAgent() {
            setDaemon(true);
        }

        @Override
        public void run() {
            try {
                Thread.sleep(AGENT_FIRST_WAIT);
                while (!isInterrupted()) {
                    logger.info("checkMailToSend start...");
                    // 발송 대상 메일을 체크하여 메일을 발송
                    processMailToSend();
                    logger.info("checkMailToSend end...");
                    // sleep
                    Thread.sleep(AGENT_SCAN_INTERVAL);
                }
            }catch (Exception e){
                logger.error( "SendAgent error : " + e.getMessage());
            }
        }

        private void processMailToSend() {
            SmtpRepository dbService = SmtpRepository.getInstance();

            try {
                if(!checkSmtpConnect()){
                    logger.error( "checkSmtpConnect smtp connect error, will try again later");
                    return;
                }

                // 발송 대상 메일 목록을 뽑는다(10개씩만)
                List<SmtpTempMain> tempMainList = dbService.getTempMainList();
                for(SmtpTempMain tempMain : tempMainList){
                    File messageFile = File.createTempFile("sensems", ".msg", new File(ImSmtpConfig.getInstance().getTempDir()));
                    try {
                        logger.info("checkMailToSend(1) Start : {}", tempMain.toString());
                        // 수신자 목록을 뽑는다(천개씩 뽑으면서 loop)
                        int tryCount = 0;
                        int resultCount = 0;
                        while (true) {
                            //logger.info("checkMailToSend 111 : {}", tempMain.getMainkey());
                            try {
                                //logger.info("checkMailToSend 222 : {}", tempMain.getMainkey());
                                List<SmtpTempRcpt> tempRcptList = dbService.getTempRcptList(tempMain.getMainkey());
                                //logger.info("checkMailToSend 333 : {} - size: {}", tempMain.getMainkey(), tempRcptList.size() );
                                if (tempRcptList == null || tempRcptList.size() == 0) {
                                    break;
                                }
                                //logger.info("checkMailToSend 444 : {}", tempMain.getMainkey());
                                //logger.info("checkMailToSend 555 : {}", tempRcptList.size());
                                tryCount += tempRcptList.size();
                                //logger.info("tempRcptList size: {} /  tryCount: {}", tempRcptList.size(), tryCount);
                                // 수신자에게 메일을 발송한다.
                                logger.info("checkMailToSend : try RcptCount: {}", tempRcptList.size());
                                List<SmtpTempRcpt> resultList = sendMail(tempMain, tempRcptList, messageFile);
                                resultCount += resultList.size();
                                logger.info("checkMailToSend : result size {} / resultCount: {}", resultList.size(), resultCount);
                                // 발송한 수신자는 삭제처리
                                if(resultList.size() > 0) {
                                    dbService.deleteTempRcptByIdxList(resultList);
                                } else { // 발송결과 건수가 0이면 break
                                    break;
                                }
                                Thread.sleep(200);
                            } catch (Exception e) {
                                logger.error("checkMailToSend(1.1) error: {}", tempMain.getMainkey());
                            }
                        }
                        logger.info("checkMailToSend(2): Finish : mainkey={}, tryCount: {}, resultCount: {}",
                                tempMain.getMainkey(), tryCount, resultCount);

                        if(tryCount != 0 && tryCount == resultCount) {
                            // imp_temp_main 에서 삭제
                            dbService.deleteTempMainByMainkey(tempMain.getMainkey());
                            dbService.deleteTempRcptByMainkey(tempMain.getMainkey());
                        } else if(tryCount == 0){
                            Date curDate = new Date();
                            if(ImTimeUtil.getDiffOfDate(tempMain.getRegdate(), curDate) > 0){
                                // 하루이상 지나면 imp_temp_main 에서 삭제
                                dbService.deleteTempMainByMainkey(tempMain.getMainkey());
                                dbService.deleteTempRcptByMainkey(tempMain.getMainkey());
                            }
                        }
                    }catch(SensemsException se){
                        String errorId = ErrorTraceLogger.log(se);
                        logger.error("SendAgent.checkMailToSend error: {}", errorId);
                    }catch(Exception e){
                        String errorId = ErrorTraceLogger.log(e);
                        logger.error("SendAgent.checkMailToSend error: {}", errorId);
                    }finally {
                        // 임시파일 삭제
                        if(messageFile.exists()){
                            messageFile.delete();
                        }
                    }
                    Thread.sleep(500);
                }
            } catch (Exception e) {
                String errorId = ErrorTraceLogger.log(e);
                logger.error("SendAgent.checkMailToSend error: {}", errorId);
            }
        }

        private boolean checkSmtpConnect(){
            boolean bResult = true;

            ImSmtp smtp = new ImSmtp();
            smtp.setConnectTimeout(120000);
            smtp.setSocketTimeout(120000);

            // send_host가 여러개면 한개씩 돌아가면서 쏜다.
            int senderCount = ImSmtpConfig.getInstance().getSendHostArray().length;
            boolean isConnect = false;
            String msg = null;
            for(int i=0; i<senderCount; i++) {
                String sendHost = ImSmtpConfig.getInstance().getSendHostArray()[i];
                if (!smtp.connect(sendHost, ImSmtpConfig.getInstance().getSmtpPortIn())) {
                    logger.error("checkSmtpConnect smtp connect error: {}:{}", sendHost, ImSmtpConfig.getInstance().getSmtpPortIn());
                    continue;
                } else {
                    isConnect = true;
                    logger.info("checkSmtpConnect smtp connect ok: {}:{}", sendHost, ImSmtpConfig.getInstance().getSmtpPortIn());
                    break;
                }
            }
            if(!isConnect){
                return false;
            }

            // helo
            if(!smtp.helo(ImSmtpConfig.getInstance().getHeloHost())){
                logger.error( "checkSmtpConnect smtp helo error");
                try {
                    smtp.close();
                } catch (Exception e){}
                return false;
            }

            smtp.quit();
            if(smtp != null){
                try {
                    smtp.close();
                } catch (Exception e){}
            }

            return bResult;
        }

        private List<SmtpTempRcpt> sendMail(SmtpTempMain tempMain, List<SmtpTempRcpt> tempRcptList, File messageFile) {
            List<SmtpTempRcpt> resultList = new ArrayList<>();

            // eml 파일을 생성
            try {
                ImMessage message = getMessage(tempMain);
                message.makeMimeFile(messageFile.getAbsolutePath());

                int k = 0;
                // 수신자별로 돌면서 메일 발송
                for(SmtpTempRcpt tempRcpt : tempRcptList) {
                    String[] arrMergeTag = new String[2];
                    String[] arrMergeValue = new String[2];

                    String encName = "";
                    String email = "";
                    try {
                        InternetAddress[] ia = InternetAddress.parse(tempRcpt.getRcptto());
                        email = ia[0].getAddress();
                        if (StringUtils.isNotEmpty(ia[0].getPersonal())) {
                            encName = MimeUtility.encodeText(ia[0].getPersonal(), ImDBSendAgent.MAIL_CHARSET, ImMessage.MIME_BASE64) + " ";
                        }
                    } catch (AddressException e) {
                        email = tempRcpt.getRcptto();
                    }

                    String rcptKey = tempRcpt.getRcpt_key();

                    arrMergeTag[0] = "To: " + ImDBSendAgent.MERGE_TO;
                    arrMergeValue[0] = "To: " + encName+"<"+email+">";

                    arrMergeTag[1] = ImSmtpConfig.getInstance().getHeaderRcptKey() + ": " + ImDBSendAgent.MERGE_RCPTKEY;
                    arrMergeValue[1] = ImSmtpConfig.getInstance().getHeaderRcptKey() + ": " + rcptKey;


                    // 메일을 발송한다.
                    //ImSmtp smtp = new ImSmtp();
                    ImSmtp smtp = null;
                    String errMsg = "";

                    // send_host가 여러개면 한개씩 돌아가면서 쏜다.
                    boolean isConnect = false;
                    int senderCount = ImSmtpConfig.getInstance().getSendHostArray().length;
                    String prevSender = null;
                    for(int i=0; i<senderCount; i++) {
                        String sendHost = loadBalanceSender.getValue();
                        while(true){
                            if(sendHost.equals(prevSender)){
                                sendHost = loadBalanceSender.getValue();
                                continue;
                            }
                            break;
                        }
                        //logger.info("{} - sendHost: {}", i, sendHost);

                        prevSender = sendHost;
                        smtp = new ImSmtp();
                        if (!smtp.connect(sendHost, ImSmtpConfig.getInstance().getSmtpPortIn())) {
                            errMsg = String.format("Smtp Connect Error: %s : %s : %s : %s", tempMain.getMainkey(), sendHost, ImSmtpConfig.getInstance().getSmtpPortIn(), smtp.getError());
                            logger.error(errMsg);
                            continue;
                        } else {
                            isConnect = true;
                            logger.info("Smtp Connect Ok: {}:{}", sendHost, ImSmtpConfig.getInstance().getSmtpPortIn());
                            break;
                        }

                    }

                    if(!isConnect){
                        smtp.close();
                        //throw new Exception(errMsg);
                        return resultList;
                    }
                    /*if(!smtp.connect(ImSmtpConfig.getInstance().getSendHost(), ImSmtpConfig.getInstance().getSmtpPortIn())){
                        smtp.close();
                        errMsg = String.format("Smtp Connect Error: %s : %s : %s : %s", tempMain.getMainkey(), ImSmtpConfig.getInstance().getSendHost(), ImSmtpConfig.getInstance().getSmtpPortIn(), smtp.getError());
                        logger.error(errMsg);
                        throw new Exception(errMsg);
                    }*/

                    if(!smtp.helo(ImSmtpConfig.getInstance().getHeloHost())){
                        smtp.close();
                        errMsg = String.format("Smtp Helo Error: %s: %s : %s : %s", tempMain.getMainkey(), ImSmtpConfig.getInstance().getSendHost(), ImSmtpConfig.getInstance().getSmtpPortIn(), smtp.getError());
                        logger.error(errMsg);
                        //throw new Exception(errMsg);
                        return resultList;
                    }

                    if(!smtp.mail(tempMain.getFromEmail())){
                        smtp.close();
                        errMsg = String.format("Smtp From Error: %s : %s : %s", tempMain.getMainkey(), tempMain.getFromEmail(), smtp.getError());
                        logger.error(errMsg);
                        //throw new Exception(errMsg);
                        return resultList;
                    }

                    if(!smtp.rcpt(email)){
                        smtp.close();
                        errMsg = String.format("Smtp Rcpt Error: %s : %s : %s", tempMain.getMainkey(), email, smtp.getError());
                        logger.error(errMsg);
                        // db에 넣어야 하나?

                        resultList.add(tempRcpt);

                        continue;
                    }

                    if(!smtp.dataFile(messageFile.getAbsolutePath(), arrMergeTag, arrMergeValue)){
                        smtp.close();
                        errMsg = String.format("Smtp Data Error: %s : %s : %s", tempMain.getMainkey(), email, smtp.getError());
                        logger.error(errMsg);
                        // db에 넣어야 하나?
                        //throw new Exception(errMsg);
                        return resultList;
                        //continue;
                    }

                    smtp.close();
                    resultList.add(tempRcpt);
                    logger.info("{} - Smtp Send OK: {} : {} -> {}, {}", k++, tempMain.getMainkey(), tempMain.getFromEmail(), email, tempMain.getSubject());
                }
            } catch (Exception e) {
                logger.error("sendMail error: mainKey : {}, {}", tempMain.getMainkey(), e.toString());
            }

            return resultList;
        }

        private ImMessage getMessage(SmtpTempMain tempMain) throws Exception {
            Properties props = new Properties();
            Session session = Session.getDefaultInstance(props);
            ImMessage message = new ImMessage(session);

            String from = tempMain.getMailfrom();
            String fromEmail = ImStringUtil.getStringBetween(from, "<", ">");
            String fromDomain = ImStringUtil.getStringAfter(from, "@");
            String fromName = ImStringUtil.getStringBefore(from, "<", true);

            message.setContentEncoding(ImMessage.ENC_QP);
            message.setCharset(ImDBSendAgent.MAIL_CHARSET);

            message.setFrom(fromName, fromEmail, ImDBSendAgent.MAIL_CHARSET);
            message.setSubject(tempMain.getSubject(), ImDBSendAgent.MAIL_CHARSET);
            message.setMessageID("<" + tempMain.getMainkey() + "@" + fromDomain + ">");
            // To: <[SENS-RCPT-TO]>
            message.setRecipientsEx(Message.RecipientType.TO, MERGE_TO, ImDBSendAgent.MAIL_CHARSET);
            message.setSentDate(tempMain.getRegdate());

            // 커스텀헤더
            // T: 테스트메일, D: DB연동외부메일(WEB API이용), A: Smtp 인증, R: 릴레이연동, C:건별재발신
            message.setHeader(ImSmtpConfig.HEADER_SEND_TYPE, tempMain.getSend_type());
            message.setHeader(ImSmtpConfig.getInstance().getHeaderGroupKey(), tempMain.getGroup_key());
            message.setHeader(ImSmtpConfig.getInstance().getHeaderRcptKey(), ImDBSendAgent.MERGE_RCPTKEY);
            //logger.info(ImSmtpConfig.HEADER_SEND_TYPE + ": {}", tempMain.getSend_type());
            // 발신아이피
            message.setHeader("X-ORIGINATING-SPRXY-IP", tempMain.getIp());

            message.setHtml(tempMain.getBody());

            // from email주소만 tempMain에 넣는다(smtp 발송시 from으로 쓰기 위해)
            tempMain.setFromEmail(fromEmail);

            return message;
        }

    }

    public static void main(String[] args) {
        SensEmsEnvironment.init();
        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();

        ImSmtpConfig m_config = ImSmtpConfig.getInstance();

        LoggerLoader.initLog("emslog.xml");
        logger.info( "ImDBSendAgent Start");

        ImDatabaseConnectionEx.init("sensems.home", "mybatis-config.xml");

        AGENT_SCAN_INTERVAL = m_config.getDbSendAgentInterval() * 1000;

        try {
            // 발송서버: 이중화 되어 있으면 번갈아가면서 던진다.
            loadBalanceSender = new ImLoadBalance();
            loadBalanceSender.init(m_config.getSendHostArray());

            Thread th = new SendAgent();
            th.setName("SendAgentTh");
            th.start();

            //logger.info( "ImDBSendAgent Start...");

            if(g_sPlatform.equalsIgnoreCase("unix")){
                th.join();
            }

        } catch(Exception e){
            logger.error( "ImDBSendAgent Start " + e.getMessage());
        }

        logger.info( "ImDBSendAgent Stop");
    }

}

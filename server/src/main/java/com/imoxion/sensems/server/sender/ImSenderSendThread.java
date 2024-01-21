package com.imoxion.sensems.server.sender;

import com.imoxion.common.mail.ImMessage;
import com.imoxion.common.util.ImFileUtil;
import com.imoxion.common.util.ImStringUtil;
import com.imoxion.sensems.server.beans.ImEmsMailData;
import com.imoxion.sensems.server.beans.ImSenderQueryData;
import com.imoxion.sensems.server.config.ImEmsConfig;
import com.imoxion.sensems.server.domain.ImbEmsMain;
import com.imoxion.sensems.server.exception.ImMessQueueException;
import com.imoxion.sensems.server.logger.ErrorTraceLogger;
import com.imoxion.sensems.server.repository.MsgidRecvRepository;
import com.imoxion.sensems.server.service.ConnectErrorDomainService;
import com.imoxion.sensems.server.service.DnsSearchService;
import com.imoxion.sensems.server.service.ImDkimSigner;
import com.imoxion.sensems.server.smtp.ImBizemsSmtp;
import com.imoxion.sensems.server.util.ImEmsUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

public class ImSenderSendThread extends Thread {
    private static Logger logger = LoggerFactory.getLogger("SENDER");

    private final static String LANG_KO = "ko";
    private final static String LANG_EN = "en";
    private final static String LANG_JA = "ja";
    private final static String LANG_ZH = "zh";

    private static int connectTimeout = 60000;
    private static int socketTimeout = 60000;
    private Object ImMessQueueException;


    public ImSenderSendThread() {
        setDaemon(true);
    }

    public static ArrayList getSpoolMailObjectData(String p_sQueuePath) throws IOException, ClassNotFoundException {
        ArrayList<ImEmsMailData> arrMail = null;
        File f = new File(p_sQueuePath);
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));) {
            arrMail = (ArrayList<ImEmsMailData>) ois.readObject();
        } catch(Exception e){
            logger.error("getSpoolMailObjectData error : {}", e);
        }

        return arrMail;
    }

    private static long getTimeInMilis(String time, String format) {
        DateFormat df = new SimpleDateFormat(format);
        long mili = -1;
        try {
            Date dt = df.parse(time);
            mili = dt.getTime();
        } catch (Exception e) {
            System.out.println(e);
        }

        return mili;
    }

    private String getIPByName(String host) throws UnknownHostException {
        String ip = host;

        InetAddress ia = InetAddress.getByName(host);
        ip = ia.getHostAddress();
        // ip가 15자리를 넘으면 그냥 host명을 사용한다.
        if (ip.length() > 16) ip = host;

        return ip;
    }

    private ImBizemsSmtp initSmtp(ImBizemsSmtp smtp) {
        if (smtp != null) {
            smtp.close();
            smtp = null;
        }
        smtp = new ImBizemsSmtp();
        smtp.setUseTLS(true);
        smtp.setLogger("SENDER");
        smtp.setConnectTimeout(connectTimeout);
        smtp.setSocketTimeout(socketTimeout);

        return smtp;
    }

    @Override
    public void run() {
        ArrayList<ImEmsMailData> arrMail = null;
        int nResponse = 0;
        int nSuccess = 1;
        String sErrMsg = "";
        ImBizemsSmtp smtp = null;

        try {
            ImDkimSigner dkimSign = ImDkimSigner.getInstance();
            //logger.info("ImSenderSendThread.run 111");
            while (!isInterrupted()) {
                //logger.info("ImSenderSendThread.run 222");
                ArrayList<ImSenderQueryData> arrQuery = new ArrayList<>();
                String queuePath = (String) ImSenderServer.getInstance().extractQueue();

                //logger.info("ImSenderSendThread.run 333 {}", queuePath);
                try {
                    if (StringUtils.isEmpty(queuePath)) {
                        logger.error("queuePath is null");
                        continue;
                    }

                    logger.info("queuePath: {}", queuePath);
                    arrMail = getSpoolMailObjectData(queuePath);

                    if (arrMail == null) {
                        nSuccess = 0;
                        nResponse = 910;
                        sErrMsg = "Spool Error: arrMail is null";
                        logger.error("arrMail is null");
                        throw new ImMessQueueException(sErrMsg);
                    }

                    logger.info("arrMail size: {}, queuePath: {}", arrMail.size(), queuePath);

                    int nSameDomainCount = 1;
                    String prevDomain_1 = "";
                    String prevDomain_2 = "";
                    ConnectErrorDomainService connectErrorDomainService = ConnectErrorDomainService.getInstance();
                    for (ImEmsMailData mailData : arrMail) {
                        nSuccess = 1;
                        boolean bDeleted = false;
                        try {
                            if (mailData == null) {
                                nSuccess = 0;
                                nResponse = 910;
                                sErrMsg = "Spool Error: ImEmsMailData is null";
                                logger.error("ImEmsMailData is null");
                                throw new ImMessQueueException(sErrMsg);
                            }

                            String emlPath = ImEmsConfig.getInstance().getMsgPath() + File.separator + mailData.getEmsMain().getMsg_path();
                            File emlFile = new File(emlPath);
                            if(!emlFile.exists()){
                                nSuccess = 0;
                                nResponse = 910;
                                sErrMsg = "No message file";
                                logger.error("No message file: {}", emlPath);
                                throw new ImMessQueueException(sErrMsg);
                            }

                            logger.debug("Send start - msgid: {}, from: {}, to: {}", mailData.getEmsMain().getMsgid(), mailData.getMailFrom(), mailData.getRcptTo());
                            ImbEmsMain emsMainFromTraceMap = ImSenderServer.getInstance().getSending(mailData.getEmsMain().getMsgid());
                            if (emsMainFromTraceMap != null) {
                                if (!mailData.getEmsMain().getStart_time().equals(emsMainFromTraceMap.getStart_time())) {
                                    logger.debug("Sender Check Stop: {} : {} - {} = {}", mailData.getEmsMain().getMsgid(), mailData.getEmsMain().getStart_time(), emsMainFromTraceMap.getStart_time(), bDeleted);
                                    bDeleted = true;
                                }
                            } else {
                                long curTimeInMillis = Calendar.getInstance().getTimeInMillis();
                                if (StringUtils.isNotEmpty(mailData.getEmsMain().getStart_time())) {
                                    long startTimeInMillies = getTimeInMilis(mailData.getEmsMain().getStart_time(), "yyyyMMddHHmmss");
                                    if ((curTimeInMillis - startTimeInMillies) > 120000) {
                                        bDeleted = true;
                                    }
                                }/* else {
                                    long startTimeInMillies = getTimeInMilis(mailData.getEmsMain().getRegdate() + "59", "yyyyMMddHHmmss");
                                    if ((curTimeInMillis - startTimeInMillies) > 120000) {
                                        bDeleted = true;
                                    }
                                }*/
                            }

                            // 삭제된 메일
                            if (bDeleted) {
                                logger.debug("Sender.IsStop : msgid: {} -  send stop (regdate {}/ starttime {}), queuePath: {}",
                                        mailData.getEmsMain().getMsgid(), mailData.getEmsMain().getRegdate(), mailData.getEmsMain().getStart_time(), queuePath);
                                ImFileUtil.deleteFile(queuePath);
                                break;
                            }

                            if (StringUtils.isEmpty(mailData.getDomain())) {
                                nSuccess = 0;
                                nResponse = 910;
                                sErrMsg = "Domain Error: No Data";
                                logger.debug("send error - msgid: {}, from: {}, to: {}, result: {}-{}", mailData.getEmsMain().getMsgid(),
                                        mailData.getMailFrom(), mailData.getRcptTo(), nResponse, sErrMsg);
                                throw new ImMessQueueException(sErrMsg);
                            }

                            // 연결실패 도메인맵에 있으면서 3회이상 실패시/10분이내 연결시도 안하고 실패 처리
                            int errorConnCount = connectErrorDomainService.getDomainCount(mailData.getDomain());
                            if (errorConnCount >= ConnectErrorDomainService.MIN_ERROR_COUNT) {
                                nSuccess = 0;
                                nResponse = 900;
                                sErrMsg = "cmd.connect: connetion not stable: " + mailData.getDomain();
                                logger.debug("send error-connect - msgid: {}, from: {}, to: {}, result: {}-{}", mailData.getEmsMain().getMsgid(),
                                        mailData.getMailFrom(), mailData.getRcptTo(), nResponse, sErrMsg);
                                throw new ImMessQueueException(sErrMsg);
                            }

                            // 메일을 실제 발송하는 부분
                            DnsSearchService ids = DnsSearchService.getInstance();
                            Iterator<String> mx = ids.findMXRecords(mailData.getDomain()).iterator();
                            String sHost = null;
                            boolean bConnect = false;
                            if (!mx.hasNext()) {
                                try {
                                    sHost = getIPByName(mailData.getDomain());
                                } catch (UnknownHostException e) {
                                    nSuccess = 0;
                                    nResponse = 901;
                                    sErrMsg = "Host unknown";
                                    logger.debug("send error - msgid: {}, from: {}, to: {}, result: {}-{}", mailData.getEmsMain().getMsgid(),
                                            mailData.getMailFrom(), mailData.getRcptTo(), nResponse, sErrMsg);
                                    throw new ImMessQueueException(sErrMsg);
                                }

                                smtp = initSmtp(smtp);
                                if (smtp.connect(sHost)) {
                                    bConnect = true;
                                }
                            } else {
                                while (mx.hasNext()) {
                                    sHost = StringUtils.trim(mx.next());
                                    if (sHost.endsWith(".")) {
                                        sHost = sHost.substring(0, (sHost.length() - 1));
                                    }

                                    try {
                                        sHost = getIPByName(sHost);
                                    } catch (UnknownHostException e) {
                                        nSuccess = 0;
                                        nResponse = 901;
                                        sErrMsg = "Host unknown: "+sHost;
                                    }

                                    smtp = initSmtp(smtp);
//                                    logger.info("send host: {}", sHost);
                                    if (smtp.connect(sHost)) {
                                        nSuccess = 1;
                                        nResponse = 0;
                                        sErrMsg = "";
                                        bConnect = true;
                                        break;
                                    }
                                }
                            }
                            if (!bConnect) {
                                nSuccess = 0;
                                nResponse = smtp.getBizemsErrorCode();
                                sErrMsg = "cmd.connect: " + smtp.getError() + " / " + sHost + " / " + smtp.getResponse();
                                logger.debug("send error-connect - msgid: {}, from: {}, to: {}, result: {}-{}", mailData.getEmsMain().getMsgid(),
                                        mailData.getMailFrom(), mailData.getRcptTo(), nResponse, sErrMsg);
                                throw new ImMessQueueException(sErrMsg);
                            }

                            if (!smtp.ehlo(ImEmsConfig.getInstance().getHeloDomain())) {
                                nSuccess = 0;
                                nResponse = smtp.getBizemsErrorCode();
                                sErrMsg = "cmd.helo: " + smtp.getError() + " / " + sHost;
                                prevDomain_2 = prevDomain_1;
                                prevDomain_1 = "";
                                logger.debug("send error-helo - msgid: {}, from: {}, to: {}, result: {}-{}", mailData.getEmsMain().getMsgid(),
                                        mailData.getMailFrom(), mailData.getRcptTo(), nResponse, sErrMsg);
                                throw new ImMessQueueException(sErrMsg);
                            }

                            if (prevDomain_1.equalsIgnoreCase(mailData.getDomain())) {
                                nSameDomainCount++;
                            }

                            prevDomain_1 = mailData.getDomain();

                            // from email
                            String sFrom = mailData.getMailFrom();
                            String fromEmail = null;
                            String fromDomain = null;
                            if (sFrom.indexOf("[#") > -1) {
                                //sFrom = ImStringUtil.getStringBetween(sFrom, "<", ">");
                                sFrom = ImEmsUtil.doMapPage(mailData.getRecordData(), sFrom);
                            }
                            try {
                                InternetAddress[] ia = InternetAddress.parse(sFrom);
                                fromEmail = ia[0].getAddress();
                            } catch (AddressException e) {
                                fromEmail = ImStringUtil.getStringBetween(sFrom, "<", ">");
                            }
                            fromDomain = ImStringUtil.getStringAfter(fromEmail, "@");

                            // mail from
                            if (!smtp.mail(fromEmail)) {
                                nSuccess = 0;
                                nResponse = smtp.getBizemsErrorCode();
                                sErrMsg = "cmd.mail: " + smtp.getError() + "/" + sFrom + " / " + sHost;
                                logger.debug("send error-mail - msgid: {}, from: {}, to: {}, result: {}-{}", mailData.getEmsMain().getMsgid(),
                                        mailData.getMailFrom(), mailData.getRcptTo(), nResponse, sErrMsg);
                                throw new ImMessQueueException(sErrMsg);
                            }

                            // rcpt to
                            if (!smtp.rcpt(mailData.getRcptTo())) {
                                nSuccess = 0;
                                nResponse = smtp.getBizemsErrorCode();
                                sErrMsg = "cmd.rcpt: " + smtp.getError() + "/" + sFrom + " / " + mailData.getRcptTo() + " / " + sHost;
                                logger.debug("send error-rcpt - msgid: {}, from: {}, to: {}, result: {}-{}", mailData.getEmsMain().getMsgid(),
                                        mailData.getMailFrom(), mailData.getRcptTo(), nResponse, sErrMsg);
                                throw new ImMessQueueException(sErrMsg);
                            }

                            // data
                            String subject = ImEmsUtil.doMapPage(mailData.getRecordData(), mailData.getEmsMain().getMsg_name());
                            String from = ImEmsUtil.doMapPage(mailData.getRecordData(), mailData.getMailFrom());
                            String fromName = ImStringUtil.getStringBefore(from, "<");
                            String charset = "utf-8";
                            if (StringUtils.isNotEmpty(mailData.getEmsMain().getCharset())) {
                                subject = ImEmsUtil.encodeUniText(subject, mailData.getEmsMain().getCharset(), false);
                                if (StringUtils.isNotEmpty(fromName))
                                    fromName = ImEmsUtil.encodeUniText(fromName, mailData.getEmsMain().getCharset(), false);
                            } else {
                                if (LANG_KO.equalsIgnoreCase(ImEmsConfig.getInstance().getDefaultLang())) {
                                    charset = "euc-kr";
                                } else if (LANG_JA.equalsIgnoreCase(ImEmsConfig.getInstance().getDefaultLang())) {
                                    charset = "iso-2022-jp";
                                } else if (LANG_ZH.equalsIgnoreCase(ImEmsConfig.getInstance().getDefaultLang())) {
                                    charset = "gb2312";
                                }
                                subject = ImEmsUtil.encodeUniText(subject, charset, false);
                                if (StringUtils.isNotEmpty(fromName))
                                    fromName = ImEmsUtil.encodeUniText(fromName, charset, false);
                            }
                            if (StringUtils.isNotEmpty(fromName)) {
                                sFrom = fromName + "<" + fromEmail + ">";
                            }

                            subject = ImStringUtil.replace(subject, "?= =?", "?=\n\t=?");

                            //String emlPath = ImEmsConfig.getInstance().getMsgPath() + File.separator + mailData.getEmsMain().getMsg_path();
                            String sContent = ImFileUtil.getFileToString(emlPath, "UTF-8");

                            sContent = ImStringUtil.replace(sContent,"[$SUBJECT$]", subject);
                            sContent = ImStringUtil.replace(sContent,"[$FROM$]", sFrom);

                            sContent = ImEmsUtil.mergeContents(mailData.getRecordData(), charset, mailData.getEmsMain().getMsgid(),
                                    mailData.getRcptTo(), mailData.getId(), sContent);

                            sContent = ImEmsUtil.changeMimeEncoding(sContent, charset, ImMessage.ENC_QP);

                            sContent = dkimSign.doDKIMSign(sContent, fromDomain);
                            if (!smtp.data(sContent)) {
                                nSuccess = 0;
                                nResponse = smtp.getBizemsErrorCode();
                                sErrMsg = "cmd.data: " + smtp.getError() + "/" + sFrom + " / " + mailData.getRcptTo() + " / " + sHost;
                                logger.debug("send error-data - msgid: {}, from: {}, to: {}, result: {}-{}", mailData.getEmsMain().getMsgid(),
                                        mailData.getMailFrom(), mailData.getRcptTo(), nResponse, sErrMsg);
                                throw new ImMessQueueException(sErrMsg);
                            }

                            // connectErrorDomainMap에서 제거
                            if(errorConnCount > 0){
                                connectErrorDomainService.removeDomain(mailData.getDomain());
                            }
                            logger.info("Send OK - msgid: {}, from: {}, to: {}, mx:{}, useTLS:{}, result: {}", mailData.getEmsMain().getMsgid(), mailData.getMailFrom(),
                                    mailData.getRcptTo(), sHost, smtp.getIsTlsHandshakeOK(), smtp.getResponse());
                            smtp.rset();

                            prevDomain_2 = prevDomain_1;
                            prevDomain_1 = mailData.getDomain();
                        } catch (ImMessQueueException ex) {
                            if (sErrMsg.length() <= 0) {
                                sErrMsg = smtp.getResponse();
                                if (sErrMsg.length() <= 0) {
                                    nSuccess = 0;
                                    nResponse = 910;
                                    sErrMsg = "Unknown error";
                                }
                            }
                            if (smtp != null) {
                                smtp.close();
                                smtp = null;
                            }
                        } finally {
                            if(nResponse == 902  || nResponse == 904){
                                connectErrorDomainService.putDomainAndCount(mailData.getDomain());
                            }
                            if(nResponse == 900) nResponse = 902;
                            ImSenderQueryData qData = new ImSenderQueryData();
                            qData.setQuery(mailData.getUpdateQuery());
                            qData.setId(mailData.getId());
                            qData.setRData(mailData.getRecordData());
                            qData.setReponse(nResponse);
                            qData.setSuccess(nSuccess);
                            qData.setField1(mailData.getDomain());
                            if (nSuccess == 0 && sErrMsg.length() <= 0) {
                                qData.setReponse(910);
                                qData.setErrStr("Unknown error");
                                //logger.info("(sending2 - " + mailData.getRcptTo() + "):Unknown error:" + sErrMsg);
                            } else {
                                qData.setErrStr(sErrMsg);
                            }

                            if(nResponse != 0) {
                                logger.error("Send Fail - msgid: {}, from: {}, to: {}, result: {}-{}", mailData.getEmsMain().getMsgid(),
                                        mailData.getMailFrom(), mailData.getRcptTo(), nResponse, sErrMsg);
                            }
                            arrQuery.add(qData);
                        }
                    }
                } catch (ImMessQueueException | IOException | ClassNotFoundException ex) {
                    String errorId = ErrorTraceLogger.log(ex);
                    logger.error("[{}] ImSenderSendThread.run error: {}", errorId, queuePath);
                } catch (Exception e) {
                    String errorId = ErrorTraceLogger.log(e);
                    logger.error("[{}] ImSenderSendThread.run2 error: {}", errorId, queuePath);
                    nSuccess = 0;
                    nResponse = 910;
                } finally {
                    logger.info("finally ImSenderSendThread arrQuery.size: {}", arrQuery.size());
                    if ((arrQuery != null) && (arrQuery.size() > 0))
                        setResult(arrQuery);

                    arrMail = null;
                    arrQuery = null;

                    if (smtp != null) {
                        smtp.close();
                        smtp = null;
                    }

                    // queuePath 파일 삭제
                    ImFileUtil.deleteFile(queuePath);
                }
            }
        } catch (Exception e1) {
            String errorId = ErrorTraceLogger.log(e1);
            logger.error("[{}] ImSenderSendThread error", errorId);
        }
    }

    private void setResult(ArrayList p_arrQuery)	{

        if(p_arrQuery.size() <= 0) {
            return;
        }

        MsgidRecvRepository recvRepository = MsgidRecvRepository.getInstance();

        //
        try{
            recvRepository.excuteUpdateResult(p_arrQuery);
        }catch(SQLException ex){
            logger.error("setResult execute update :"+  ex.getMessage());
        }finally {
            //setSendCount(false);
            p_arrQuery.clear();
        }
    }


}

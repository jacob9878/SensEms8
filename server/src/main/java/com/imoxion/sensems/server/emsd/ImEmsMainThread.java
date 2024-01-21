package com.imoxion.sensems.server.emsd;

import com.imoxion.common.mail.ImMessage;
import com.imoxion.common.util.ImStringUtil;
import com.imoxion.common.util.ImTimeUtil;
import com.imoxion.common.util.ImUtils;
import com.imoxion.security.ImSecurityLib;
import com.imoxion.sensems.server.ImEmsServer;
import com.imoxion.sensems.server.beans.SendMailData;
import com.imoxion.sensems.server.config.ImEmsConfig;
import com.imoxion.sensems.server.define.ImStateCode;
import com.imoxion.sensems.server.domain.*;
import com.imoxion.sensems.server.logger.ErrorTraceLogger;
import com.imoxion.sensems.server.sender.ImSenderServer;
import com.imoxion.sensems.server.service.AddressService;
import com.imoxion.sensems.server.service.EmsMainService;
import com.imoxion.sensems.server.service.LinkService;
import com.imoxion.sensems.server.service.ReceiverService;
import com.imoxion.sensems.server.util.ImEmsUtil;
import com.imoxion.sensems.server.util.ImFilePermission;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ImEmsMainThread extends Thread {
    private static Logger logger = LoggerFactory.getLogger("EMSD");

    public static boolean bIsRecv = false;
    public static boolean isPossibleSend = true;
    public static boolean bIsCheckSendMail = false;

    // 현재 처리중인 태스크를 관리하기 위한 맵
    public static Map<String, Future> taskExtractMap = new ConcurrentHashMap<String, Future>();
    public static Map<String, Future> taskTransferMap = new ConcurrentHashMap<String, Future>();
    // 태스크 멀티쓰레딩(설정파일에 설정된 쓰레드 숫자만큼 풀링)
    ExecutorService executorServiceExtract = Executors.newFixedThreadPool(ImEmsConfig.getInstance().getExtractThreadCount());
    ExecutorService executorServiceTransfer = Executors.newFixedThreadPool(ImEmsConfig.getInstance().getTransferThreadCount());

    public ImEmsMainThread() {
        setDaemon(true);
    }


    /**
     * 수신자 추출 쓰레드 생성
     */
    private void createExtractRecvThread(ImbEmsMain emsMain){
        try {
            ImExtractRecvThread extractRecvThread = new ImExtractRecvThread(emsMain);
            Future<?> future = executorServiceExtract.submit(extractRecvThread);
            taskExtractMap.putIfAbsent(emsMain.getMsgid(), future);
/*
            try {
                future.get();
                logger.info("ExtractRecvThread Finish: {}", emsMain.getMsgid() );

            } catch (InterruptedException e) {
                String errorId = ErrorTraceLogger.log(e);
                logger.error("{} - createExtractRecvThread Error", errorId );
            } catch (ExecutionException e) {
                String errorId = ErrorTraceLogger.log(e);
                logger.error("{} - createExtractRecvThread Error", errorId );
            } catch (Exception e) {
                String errorId = ErrorTraceLogger.log(e);
                logger.error("{} - createExtractRecvThread Error", errorId );
            } finally {
                if(taskExtractMap.containsKey(emsMain.getMsgid())) {
                    taskExtractMap.remove(emsMain.getMsgid());
                }
            }*/
        } catch (Exception e) {
            String errorId = ErrorTraceLogger.log(e);
            logger.error("{} - createExtractRecvThread Error", errorId );
        }

    }

    /**
     * 수신자추출 쓰레드 중지
     */
    public void removeExtractRecvThread(ImbEmsMain emsMain){
        try {
            // remove 된 Future가 나옴
            Future<?> future = taskExtractMap.remove(emsMain.getMsgid());
            if(future != null) {
                future.cancel(true);
                logger.info("removeExtractRecvThread: {} Stop Called", emsMain.getMsgid());
            }
        } catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            logger.error("{} - removeExtractRecvThread Error", errorId );
        }
    }


    /**
     * 수신자 전달 쓰레드 생성
     */
    private void createTransferRecvThread(ImbEmsMain emsMain){
        try {
            if(!isPossibleSend) {
                logger.error("createTransferRecvThread Skip: isPossibleSend - {}", isPossibleSend );
                logger.error("### Sender service Not started. You have to start sender service first and Restart emsd service!! ###");
                return;
            }
            ImTransferRecvThread transferRecvThread = new ImTransferRecvThread(emsMain);
            Future<?> future = executorServiceTransfer.submit(transferRecvThread);
            taskTransferMap.putIfAbsent(emsMain.getMsgid(), future);

            // 수신자 생성 중임을 입력한다.
            bIsRecv = true;
            /*try {
                future.get();
                logger.info("TransferRecvThread Finish: {}", emsMain.getMsgid() );

            } catch (InterruptedException e) {
                String errorId = ErrorTraceLogger.log(e);
                logger.error("{} - createTransferRecvThread Error", errorId );
            } catch (ExecutionException e) {
                String errorId = ErrorTraceLogger.log(e);
                logger.error("{} - createTransferRecvThread Error", errorId );
            } catch (Exception e) {
                String errorId = ErrorTraceLogger.log(e);
                logger.error("{} - createTransferRecvThread Error", errorId );
            } finally {
                if(taskTransferMap.containsKey(emsMain.getMsgid())) {
                    taskTransferMap.remove(emsMain.getMsgid());
                }
            }*/
        } catch (Exception e) {
            String errorId = ErrorTraceLogger.log(e);
            logger.error("{} - createTransferRecvThread Error", errorId );
        }
    }

    /**
     * 수신자 전달 쓰레드 중지
     */
    public void removeTransferRecvThread(ImbEmsMain emsMain){
        try {
            // remove 된 Future가 나옴
            Future<?> future = taskTransferMap.remove(emsMain.getMsgid());
            if(future != null) {
                future.cancel(true);
                logger.info("removeTransferRecvThread: {} Stop Called", emsMain.getMsgid());
            }
            bIsRecv = false;
        } catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            logger.error("{} - removeTransferRecvThread Error", errorId );
        }
    }

    /**
     * 수신자 추출을 중단할 쓰레드가 있는지 확인
     */
    private void checkStopMailStop(){
        // 정지 플래그가 있으면 정지 플래그를 설정한다.
        try{
            EmsMainService emsMainService = EmsMainService.getInstance();
            List<ImbEmsMain> listToStop = emsMainService.getListToStop();
            if(listToStop.size() > 0) logger.info("listToStop size: {}", listToStop.size());
            for (ImbEmsMain imbEmsMain : listToStop) {
                // 중지 상태값 업데이트
                emsMainService.stopSendEms(imbEmsMain);

                // 수신자 추출 및 전달 쓰레드에서 해당 태스크 제거
                removeExtractRecvThread(imbEmsMain);
                removeTransferRecvThread(imbEmsMain);
            }
        }catch(Exception e){
            String errorId = ErrorTraceLogger.log(e);
            logger.error("{} - checkStopMail Error", errorId );
        }
    }


    /**
     * 수신자 추출이 필요한 메일이 있는지 확인
     */
    private void checkSendMail(){
        AddressService addressService = AddressService.getInstance();
        //Map<String, Future> taskMap = new ConcurrentHashMap<String, Future>();
       // ExecutorService execService = Executors.newFixedThreadPool(ImEmsConfig.getInstance().getExtractThreadCount());

        try {
            bIsCheckSendMail = true;
            EmsMainService emsMainService = EmsMainService.getInstance();
            ReceiverService receiverService = ReceiverService.getInstance();
            //List<ImbEmsMain> listToResend = emsMainService.getListToResend();
            List<ImbEmsMain> listToSend = emsMainService.getListToSend();
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
            if(listToSend.size() > 0) logger.info("getListToSend size: {}", listToSend.size());
            for (ImbEmsMain emsMain : listToSend) {
                String msgid = emsMain.getMsgid();
                String state = emsMain.getState();
                String currTime = df.format(new Date());

                // 수신그룹 타입에 따른 수신자 추출 쿼리
                if(ImbEmsMain.RECTYPE_ADDR.equals(emsMain.getRectype())){
                    String query = addressService.getRecvQueryAddr(emsMain.getUserid(), msgid);
                    emsMain.setQuery(query);
                } else if(ImbEmsMain.RECTYPE_RESEND.equals(emsMain.getRectype())){
//                    emsMain.setQuery(ImEmsUtil.getDecryptString(emsMain.getQuery()));
                    emsMain.setQuery(emsMain.getQuery());
                } else if(ImbEmsMain.RECTYPE_RECV.equals(emsMain.getRectype())){
                    ImbReceiver receiverInfo = receiverService.getReceiverInfo(emsMain.getRecid());
                    String query = ImEmsUtil.getDecryptString(receiverInfo.getQuery());
                    emsMain.setQuery(query);
                }

                if(ImStateCode.ST_WAIT_0.equals(state) || ImStateCode.ST_WAIT.equals(state) || ImStateCode.ST_EXTRACTING_RECV.equals(state) ){    // 수신자 추출
                    // taskExtractMap에 있으면 추출중인 상태
                    if(!taskExtractMap.containsKey(emsMain.getMsgid())) {
                        emsMainService.updateStateStartTime(emsMain, currTime);
                        // 수신자 추출 쓰레드
                        createExtractRecvThread(emsMain);
                        if(!ImStateCode.ST_EXTRACTING_RECV.equals(emsMain.getState())) {
                            emsMainService.updateState(emsMain, ImStateCode.ST_EXTRACTING_RECV);
                        }
                        logger.info("createTransferRecvThread 00 - {} : {}: {}", emsMain.getMsgid(), emsMain.getStart_time(), state);
                    }
                } else if(ImStateCode.ST_FINISH_RECV.equals(state)){     // 발송
                    emsMain = emsMainService.getEmsInfoWithContents(emsMain);
                    emsMainService.updateStateSendStartTime(emsMain, System.currentTimeMillis());
                    logger.info("createTransferRecvThread 11 - {} : {} : state {}", emsMain.getMsgid(), emsMain.getStart_time(), state);
                    ImSenderServer.getInstance().putSending(msgid, emsMain);
                    // 수신자 transfer 쓰레드
                    createTransferRecvThread(emsMain);
                } else if(ImStateCode.ST_RESENDING.equals(state)){     // 발송
                    logger.info("createTransferRecvThread 22 - {} : {} : state {}", emsMain.getMsgid(), emsMain.getStart_time(), state);
                    ImSenderServer.getInstance().putSending(msgid, emsMain);
                    emsMainService.updateStateSendStartTime(emsMain, System.currentTimeMillis());
                    // 수신자 transfer 쓰레드
                    createTransferRecvThread(emsMain);
                }
            }
        }catch(Exception e){
            String errorId = ErrorTraceLogger.log(e);
            logger.error("{} - checkSendMailSend Error", errorId );
        }finally{
            bIsCheckSendMail = false;
        }
    }

    /**
     * 재발신할 메일이 있는지 체크
     */
    private void checkRetry(){
        try{
            EmsMainService emsMainService = EmsMainService.getInstance();

            String curTime = ImTimeUtil.getDateFormat(new Date(), "yyyyMMddHHmm");
            int interval = ImEmsConfig.getInstance().getErrorResendInterval();

            List<ImbEmsMain> listToResend = emsMainService.getListToResend(interval);

            if(listToResend != null){
                logger.info("checkRetry.listToResend : {}", listToResend.size());
                for(ImbEmsMain emsMain : listToResend){

                    // 에러재발신: 이때 error_resend 값은 부모 error_resend 값 -1 로 해서 넣음
                    // 부모의 cur_resend = error_resend 로 업데이트 한다.
                    mailResend(emsMain);
                    logger.info("mailResend: msgid-{}, subject-{}", emsMain.getMsgid(), emsMain.getMsg_name());
                }
            }
        } catch(Exception ex){
            String errorId = ErrorTraceLogger.log(ex);
            logger.error("{} - checkRetry Error", errorId );
        }
    }

    private void mailResend( ImbEmsMain emsMain ){
        try {
            EmsMainService emsMainService = EmsMainService.getInstance();
            LinkService linkService = LinkService.getInstance();
            ReceiverService receiverService = ReceiverService.getInstance();

            String msgid = ImUtils.makeKeyNum(24);
            String encrypt_msgid = ImSecurityLib.encryptAESUrlSafe(ImEmsConfig.getInstance().getUrlEncryptKey(), msgid);
            String weburl = ImEmsConfig.getInstance().getWebUrl();

            String old_msgid = emsMain.getMsgid();
            int old_error_resend = emsMain.getError_resend();

            String mbox_path = ImEmsConfig.getInstance().getMsgPath();

            // 발송대상자가 존재하는지 확인
            boolean existResendTarget = receiverService.existResendTarget(old_msgid);
            if(!existResendTarget){
                // 여기서 종료
                emsMainService.updateMailResend(old_msgid);
                logger.info("mailResend Target Count is 0, Finish Resend: msgid-{}", old_msgid);
                return;
            }

            //get ims_msg_info 해서 다시 insert - 첨부파일, 링크추적, 수신확인, 수신거부...
            ImbEmsContents OldEmsContents = emsMainService.getEmsContents(old_msgid);
            String dbContent = OldEmsContents.getContents();

            String oldMsgPath = mbox_path + File.separator + emsMain.getMsg_path();
            String mailContent = emsMainService.getMailBody(oldMsgPath);

            int index = mailContent.indexOf("msgid=");
            if(index > 0) {
                String result = mailContent.substring(index + 6, index + 49);
                mailContent = mailContent.replace(result, encrypt_msgid);
            }

            //수신자 query
            String fetchFileds = receiverService.getResendQueryColumn(old_msgid);
            emsMain.setQuery("select "+fetchFileds+" from recv_" + old_msgid + " where errcode IN ('902','903','904','906','910') order by field1");

            //링크추적 링크 테이블...
            //imb_link_info, linklog_msgid 가져와서 msgid만 바꾸기 insert, create / imb_link_count 새로 insert
            List<ImbLinkInfo> linkList = linkService.getLinkList(old_msgid);

            //첨부파일일 경우 getAttachInfo 해서 거기 있는 fkey를 암호화, link url에 넣어주기

            if (linkList != null) {
                int i = 0;
                for (ImbLinkInfo linkInfo : linkList) {
                    linkInfo.setMsgid(msgid);
                    String oldLink = linkInfo.getLink_url();

                    if (StringUtils.isNotEmpty(oldLink) && oldLink.contains(ImEmsConfig.getInstance().getDownloadUrl())) {
                        //첨부파일 처리
                        //fkey와 msgid로 imb_emsattach에 존재하는지 확인, 없다면 continue
                        index = oldLink.indexOf("fkey=");
                        String ekey = oldLink.substring(index + 5, index + 48);
                        ekey = ImSecurityLib.decryptAES(ImEmsConfig.getInstance().getUrlEncryptKey(), ekey);
                        String encrypt_old_url = ImSecurityLib.encryptAESUrlSafe(ImEmsConfig.getInstance().getUrlEncryptKey(), oldLink);
                        ImbEmsAttach attachBean = emsMainService.getAttachInfo(ekey, old_msgid);
                        if (attachBean == null) {
                            String error_url = ImSecurityLib.encryptAESUrlSafe(ImEmsConfig.getInstance().getUrlEncryptKey(), weburl + "/error/no-resource.do");
                            dbContent = ImStringUtil.replace(mailContent, encrypt_old_url, error_url);
                            continue;
                        }
                        //파일 복제 로직, 서버에서 파일이 삭제되어 없다면 continue
                        String file_path = emsMainService.copyAttach(attachBean);

                        if (file_path == null) {
                            String error_url = ImSecurityLib.encryptAESUrlSafe(ImEmsConfig.getInstance().getUrlEncryptKey(), weburl + "/error/no-resource.do");
                            dbContent = ImStringUtil.replace(mailContent, encrypt_old_url, error_url);
                            continue;
                        }

                        //파일 복제 성공했으면 imb_emsattach에 insert
                        String fkey = ImUtils.makeKeyNum(24);
                        attachBean.setEkey(fkey);
                        attachBean.setMsgid(msgid);
                        attachBean.setFile_path(file_path);
                        Date now = new Date();
                        attachBean.setRegdate(now);
                        emsMainService.insertAttachInfo(attachBean);

                        //content에서 이전 다운로드 url을 새로운 fkey와 msgid가 적용된 다운로드 url로 수정
                        String encrypt_fkey = ImSecurityLib.encryptAESUrlSafe(ImEmsConfig.getInstance().getUrlEncryptKey(), attachBean.getEkey());
                        linkInfo.setLink_url(weburl + ImEmsConfig.getInstance().getDownloadUrl()+ "?fkey=" + encrypt_fkey + "&msgid=" + encrypt_msgid);
                        String encrypt_url = ImSecurityLib.encryptAESUrlSafe(ImEmsConfig.getInstance().getUrlEncryptKey(), linkInfo.getLink_url());

                        mailContent = ImStringUtil.replace(mailContent, encrypt_old_url, encrypt_url);
                    }
                    linkService.insertLinkInfo(linkInfo);
                    i++;
                }
                //imb_link_info에 1개 이상 insert 했다면 linklog_msgid 테이블 생성
                if (i >= 1) linkService.createLinkLogTable(msgid);
            }

            //수신확인 url imb_receipt_count insert
            emsMainService.insertReceiptCount(msgid);

            //기존 emsmain 처리
            emsMainService.updateMailResendNum(emsMain.getParentid(), emsMain.getResend_num());
            emsMainService.updateMailResend(old_msgid);


            //save mail - eml 저장
            Date now = new Date();
            String year = ImTimeUtil.getDateFormat(now, "yyyy");
            String month = ImTimeUtil.getDateFormat(now, "MM");
            String day = ImTimeUtil.getDateFormat(now, "dd");
            String msg_path = File.separator + year + File.separator + month + File.separator + day;

            emsMain.setMsg_path(msg_path + File.separator + msgid + ".eml");
            emsMain.setMsgid(msgid);
            emsMain.setRegdate(ImTimeUtil.getDateFormat(new Date(), "yyyyMMddHHmm"));
            emsMain.setRectype("4");
            emsMain.setExtended("999");
            emsMain.setState("000");
            emsMain.setResend_num(emsMain.getResend_num() + 1);
            emsMain.setResend_step(emsMain.getResend_step() + 1);
            emsMain.setError_resend( old_error_resend -1 );
            this.saveMail(emsMain, weburl, mailContent, msg_path);

            //imb_emsmail 기존 data update & 새 data insert
            emsMainService.insertMsgInfo(msgid, dbContent);
            emsMainService.insertMailData(emsMain);

            //hc 테이블 생성
            receiverService.createHostCount(msgid);

            logger.info("mailResend OK: msgid {} => {}", old_msgid, msgid);
        }catch(Exception ex){
            String errorId = ErrorTraceLogger.log(ex);
            logger.error("{} - mailResend Error", errorId );
        }
    }



    private void saveMail(ImbEmsMain emsMain, String weburl, String content, String msg_path) throws Exception{

        String mailFrom = emsMain.getMail_from();
        String reply_to = emsMain.getReplyto();

        String mbox_path = ImEmsConfig.getInstance().getMsgPath();
        String fileName = emsMain.getMsgid() + ".eml";

        String filePath = mbox_path + msg_path;
        File fileDir = new File(filePath);
        if(!fileDir.exists()){
            fileDir.mkdirs();
            ImFilePermission.setFilePermission(filePath);
        }

        String fromName = "";
        String fromEmail = "";
        String from_domain = "";
        String[] arrFrom = mailFrom.split("<");
        if (arrFrom.length > 1) {
            fromName = arrFrom[0];
            fromEmail = arrFrom[1].substring(0, arrFrom[1].length() - 1);
        } else {
            fromEmail = mailFrom;
        }
        String tempArr[] = fromEmail.split("@");
        if(tempArr != null){
            from_domain = tempArr[1];
        }

        String sReplytoAddr = null;
        String[] arrReplyto = reply_to.split("<");
        if(arrReplyto.length > 1){
            sReplytoAddr = arrReplyto[1].substring(0, arrReplyto[1].length()-1);
        } else {
            sReplytoAddr = reply_to;
        }

        SendMailData sendMailData = new SendMailData();
        sendMailData.setCharset(emsMain.getCharset());
        sendMailData.setFromName(fromName);
        sendMailData.setFromEmail(fromEmail);
        sendMailData.setReply_to(sReplytoAddr);
        sendMailData.setTo("[$TO$]");
        sendMailData.setSubject(emsMain.getMsg_name());
        if("0".equals(emsMain.getIshtml())){
            sendMailData.setTextBody(content);
        } else{
            sendMailData.setHtmlBody(content);
        }
        sendMailData.setMessageId("<"+emsMain.getMsgid()+"@"+from_domain+">"); // <msgid@from_domain> 형식

        String emlPath = filePath + File.separator + fileName;
        logger.info("saveMail - {}", emlPath);
        ImMessage mimeMessage = sendMailData.getMessage();
        //this.writeMimeFile(mimeMessage, emlPath);
        mimeMessage.makeMimeFile(emlPath);
        ImFilePermission.setFilePermission(emlPath);
    }

    private void writeMimeFile(ImMessage message, String emlPath) throws Exception{
        BufferedOutputStream bos = null;
        try {
            logger.info("writeMimeFile: {}", emlPath);
            //bos =  new BufferedOutputStream(ImIOCipherUtil.getFileOutputStream(emlPath),4096);
            bos =  new BufferedOutputStream(new FileOutputStream(emlPath));
            //message.setContentEncoding(ImMessage.ENC_7BIT);
            message.makeMimeFile(bos);
            message.writeTo(bos);
        }finally{
            if (bos != null) {
                try {
                    bos.close();
                }catch (IOException fe) {

                }
                catch (Exception e) {
                }
            }
        }
    }

    @Override
    public void run() {
        try {
            int j = 0;
            while (!isInterrupted() && ImEmsServer.isAlive) {
                j++;
                // checkStopMailStop
                checkStopMailStop();

                // checkSendMailSend
                if(!bIsRecv && !bIsCheckSendMail) {
                    checkSendMail();
                }

                if(j % 2 == 0){
                    // 정기메일 체크
                    //checkRotMain();

                    // 임시 테이블 체크
                    //checkTempMain();

                    // 에러재발신 체크(발송완료된 메일중에서 에러재발신 설정이 되어 있는 메일을 재발송한다.)
                    checkRetry();

                    j = 0;
                }

                for(int i=0;i<10;i++)
                    sleep(1000);
            }
        }catch(Exception e){
            String errorId = ErrorTraceLogger.log(e);
            logger.error("{} - ImbEmsMainThread run error", errorId);
        }

        executorServiceExtract.shutdown();
        executorServiceTransfer.shutdown();

        logger.info("ImbEmsMainThread Shutdown");
    }


}

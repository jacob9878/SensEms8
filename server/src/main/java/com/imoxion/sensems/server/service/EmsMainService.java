package com.imoxion.sensems.server.service;

import com.imoxion.common.mail.ImMessage;
import com.imoxion.common.util.ImFileUtil;
import com.imoxion.common.util.ImTimeUtil;
import com.imoxion.common.util.ImUtils;
import com.imoxion.sensems.server.config.ImEmsConfig;
import com.imoxion.sensems.server.define.ImStateCode;
import com.imoxion.sensems.server.domain.ImbEmsAttach;
import com.imoxion.sensems.server.domain.ImbEmsContents;
import com.imoxion.sensems.server.domain.ImbEmsMain;
import com.imoxion.sensems.server.logger.ErrorTraceLogger;
import com.imoxion.sensems.server.repository.AttachRepository;
import com.imoxion.sensems.server.repository.EmsMainRepository;
import com.imoxion.sensems.server.repository.ReceiptCountRepository;
import com.imoxion.sensems.server.util.ImFilePermission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Session;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class EmsMainService {
    private Logger logger = LoggerFactory.getLogger("EMSD");

    private static final EmsMainService emsMainService = new EmsMainService();
    public static EmsMainService getInstance() {
        return emsMainService;
    }
    private EmsMainService() {}
    /////////////////////////////////////////////////

    public List<ImbEmsMain> getListToSend() throws Exception {
        EmsMainRepository emsMainRepository = EmsMainRepository.getInstance();

        return emsMainRepository.getListToSend();
    }

    public List<ImbEmsMain> getListToStop() throws Exception {
        EmsMainRepository emsMainRepository = EmsMainRepository.getInstance();

        return emsMainRepository.getListToStop();
    }

    public List<ImbEmsMain> getListRecentSendingMail() throws Exception {
        EmsMainRepository emsMainRepository = EmsMainRepository.getInstance();

        return emsMainRepository.getListRecentSendingMail();
    }

    public List<ImbEmsMain> getListSending() throws Exception {

        EmsMainRepository emsMainRepository = EmsMainRepository.getInstance();

        return emsMainRepository.getListSending();
    }

    public List<ImbEmsMain> getListToResend(int interval) throws Exception {
        EmsMainRepository emsMainRepository = EmsMainRepository.getInstance();

        return emsMainRepository.getListToResend(interval);
    }


    public ImbEmsMain getEmsInfo(String msgid) throws Exception {
        EmsMainRepository emsMainRepository = EmsMainRepository.getInstance();

        return emsMainRepository.getEmsInfo(msgid);
    }

    public ImbEmsMain getEmsInfoWithContents(ImbEmsMain emsMain) throws Exception {
        EmsMainRepository emsMainRepository = EmsMainRepository.getInstance();

        ImbEmsContents emsContents = emsMainRepository.getContents(emsMain.getMsgid());
        emsMain.setContents(emsContents.getContents());

        return emsMain;
    }

    public ImbEmsAttach getAttachInfo(String ekey, String msgid) throws Exception {
        AttachRepository attachRepository = AttachRepository.getInstance();

        ImbEmsAttach emsAttach = attachRepository.getAttachInfo(ekey, msgid);

        return emsAttach;
    }

    public void insertAttachInfo(ImbEmsAttach emsAttach) {
        try {
            AttachRepository attachRepository = AttachRepository.getInstance();
            attachRepository.insertAttach(emsAttach);
        }catch(Exception e){
            String errorId = ErrorTraceLogger.log(e);
            logger.error("{} - insertAttachInfo error : {}", errorId, e.getMessage());
        }
    }

    public void insertReceiptCount(String msgid) {
        try {
            ReceiptCountRepository receiptCountRepository = ReceiptCountRepository.getInstance();
            receiptCountRepository.insertReceiptCount(msgid);
        }catch(Exception e){
            String errorId = ErrorTraceLogger.log(e);
            logger.error("{} - insertReceiptCount error : {}", errorId, e.getMessage());
        }
    }

    public void  insertMsgInfo(String msgid, String content) {
        try {
            EmsMainRepository emsMainRepository = EmsMainRepository.getInstance();

            emsMainRepository.insertMsgInfo(msgid, content);
        }catch(Exception e){
            String errorId = ErrorTraceLogger.log(e);
            logger.error("{} - insertMsgInfo error : {}", errorId, e.getMessage());
        }
    }

    public void  insertMailData(ImbEmsMain emsMain) {
        try {
            EmsMainRepository emsMainRepository = EmsMainRepository.getInstance();

            emsMainRepository.insertMailData(emsMain);
        }catch(Exception e){
            String errorId = ErrorTraceLogger.log(e);
            logger.error("{} - insertMailData error : {}", errorId, e.getMessage());
        }
    }



    public void updateMailResend(String msgid) {
        try {
            EmsMainRepository emsMainRepository = EmsMainRepository.getInstance();

            emsMainRepository.updateMailResend(msgid);
        }catch(Exception e){
            String errorId = ErrorTraceLogger.log(e);
            logger.error("{} - updateMailResend error : {}", errorId, e.getMessage());
        }
    }

    public void updateMailResendNum(String msgid, int resend_num) {
        try {
            EmsMainRepository emsMainRepository = EmsMainRepository.getInstance();

            emsMainRepository.updateMailResendNum(msgid, resend_num);
        }catch(Exception e){
            String errorId = ErrorTraceLogger.log(e);
            logger.error("{} - updateMailResendNum error : {}", errorId, e.getMessage());
        }
    }

    public String getMailBody(String emlPath) throws Exception {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props);
        ImMessage message = new ImMessage(session);
        message.setDefaultCharset("euc-kr");
        message.parseMimeFile(emlPath);

        return message.getHtml();
    }

    public String copyAttach(ImbEmsAttach emsAttach) throws Exception{
        Date now = new Date();
        String year = ImTimeUtil.getDateFormat(now, "yyyy");
        String month = ImTimeUtil.getDateFormat(now, "MM");
        String day = ImTimeUtil.getDateFormat(now, "dd");

        String dayPath = year + File.separator + month + File.separator + day;
        String fkey = ImUtils.makeKeyNum(24);
        String inFilePath = ImEmsConfig.getInstance().getAttachPath() + File.separator + emsAttach.getFile_path();
        String outFilePath = ImEmsConfig.getInstance().getAttachPath() + File.separator + dayPath;

        try{
            File realFile = new File(inFilePath);
            if(realFile.exists()) {
                // 실제 저장할 경로 생성
                realFile = new File(outFilePath);
                if (!realFile.exists()) {
                    if (!realFile.mkdirs()) {
                        logger.info("Make Attach Directory Fail");
                    }
                }
                ImFileUtil.copyFile(inFilePath, outFilePath + File.separator + fkey);
                ImFilePermission.setFilePermission(outFilePath + File.separator + fkey);
            }else{
                return null;
            }
        }catch (NullPointerException ne) {
            logger.error("readfile error: {}", emsAttach.toString());
        }
        catch(Exception e){
            logger.error("readfile error: {} - {}", emsAttach.toString(), e.toString());
        }
        return dayPath + File.separator + fkey;
    }

    public ImbEmsContents getEmsContents(String msgid) throws Exception {
        EmsMainRepository emsMainRepository = EmsMainRepository.getInstance();

        return emsMainRepository.getContents(msgid);
    }

    public void stopSendEms(ImbEmsMain emsMain) throws Exception {
        EmsMainRepository emsMainRepository = EmsMainRepository.getInstance();

        String currentState = emsMain.getState();
        String targetState = "";
        // 대기중
        if(ImStateCode.ST_WAIT_0.equals(currentState) || ImStateCode.ST_WAIT.equals(currentState) || ImStateCode.ST_STOP_WAIT.equals(currentState)) {
            targetState = ImStateCode.ST_STOP_WAIT;
        // 수신자추출중
        } else if(ImStateCode.ST_EXTRACTING_RECV.equals(currentState) ){
            targetState = ImStateCode.ST_STOP_RECV;
        // 발송중
        } else if(ImStateCode.ST_FINISH_RECV.equals(currentState) || ImStateCode.ST_SENDING.equals(currentState) || ImStateCode.ST_RESENDING.equals(currentState)){
            targetState = ImStateCode.ST_STOP_SENDING;
        }

        emsMainRepository.stopSendEms(emsMain.getMsgid(), targetState);
    }

    public void updateStateStartTime(ImbEmsMain emsMain, String currentTime) throws Exception {
        EmsMainRepository emsMainRepository = EmsMainRepository.getInstance();

        emsMainRepository.updateStateStartTime(emsMain.getMsgid(), ImStateCode.ST_EXTRACTING_RECV, currentTime);
    }

    public void updateState(ImbEmsMain emsMain) throws Exception {
        EmsMainRepository emsMainRepository = EmsMainRepository.getInstance();

        emsMainRepository.updateState(emsMain.getMsgid(), ImStateCode.ST_FINISH_RECV);
    }
    public void updateState(ImbEmsMain emsMain, String state) throws Exception {
        EmsMainRepository emsMainRepository = EmsMainRepository.getInstance();

        emsMainRepository.updateState(emsMain.getMsgid(), state);
    }

    public void updateStateSendStartTime(ImbEmsMain emsMain, long currentTime) throws Exception {
        EmsMainRepository emsMainRepository = EmsMainRepository.getInstance();

        emsMainRepository.updateStateSendStartTime(emsMain.getMsgid(), ImStateCode.ST_SENDING, currentTime);
    }

    public void updateStateAndCount(ImbEmsMain emsMain) throws Exception {
        EmsMainRepository emsMainRepository = EmsMainRepository.getInstance();

        emsMainRepository.updateStateAndCount(emsMain);
    }

    public void updateCurSend(int curSend, String msgid) throws Exception {
        EmsMainRepository emsMainRepository = EmsMainRepository.getInstance();

        emsMainRepository.updateCurSend(curSend, msgid);
    }

    public void updateToLoggingState( String msgid) throws Exception {
        EmsMainRepository emsMainRepository = EmsMainRepository.getInstance();

        emsMainRepository.updateToLoggingState(msgid);
    }

    public void updateMainEndEx(String msgid, String currState, String currDate) throws Exception {
        EmsMainRepository emsMainRepository = EmsMainRepository.getInstance();

        emsMainRepository.updateMainEndEx(msgid, currState, currDate);
    }

    /**
     * 서비스를 재기동하면서 모든 발송 중 메일을 중지시킴
     */
    public void stopAllSending() {
        EmsMainRepository emsMainRepository = EmsMainRepository.getInstance();

        try {
            List<ImbEmsMain> listSending = emsMainRepository.getListSendingToStop();
            for (ImbEmsMain emsMain : listSending) {
                String curState = emsMain.getState();
                String targetState = curState;
                if (ImStateCode.ST_WAIT_0.equals(curState)) {
                    targetState = ImStateCode.ST_STOP_WAIT;
                } else if (ImStateCode.ST_WAIT.equals(curState)) {
                    targetState = ImStateCode.ST_STOP_WAIT;
                } else if (ImStateCode.ST_EXTRACTING_RECV.equals(curState)) {
                    targetState = ImStateCode.ST_STOP_RECV;
                } else if (ImStateCode.ST_SENDING.equals(curState)) {
                    targetState = ImStateCode.ST_STOP_SENDING;
                } else if (ImStateCode.ST_RESENDING.equals(curState)) {
                    targetState = ImStateCode.ST_STOP_SENDING;
                }
                emsMainRepository.stopSendEms(emsMain.getMsgid(), targetState);
                logger.info("stopAllSending - msgid: {}, targetState:{}, subject: {}", emsMain.getMsgid(), targetState, emsMain.getMsg_name());
            }
        } catch (Exception e) {
            String errorId = ErrorTraceLogger.log(e);
            logger.error("{} - stopAllSending error : {}", errorId, e.getMessage());
        }
    }

}

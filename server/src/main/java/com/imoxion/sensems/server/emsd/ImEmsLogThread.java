package com.imoxion.sensems.server.emsd;

import com.imoxion.sensems.server.ImEmsServer;
import com.imoxion.sensems.server.define.ImStateCode;
import com.imoxion.sensems.server.domain.ImbEmsMain;
import com.imoxion.sensems.server.logger.ErrorTraceLogger;
import com.imoxion.sensems.server.service.EmsMainService;
import com.imoxion.sensems.server.service.ReceiverService;
import org.apache.commons.collections4.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

public class ImEmsLogThread extends Thread {

    private static Logger logger = LoggerFactory.getLogger("EMSD");

    public ImEmsLogThread() {
        setDaemon(true);
    }

    private int getRecvSendingCount(String msgid){
        ReceiverService receiverService = ReceiverService.getInstance();
        int sendingCount = 0;

        sendingCount = receiverService.getRecvSendingCount(msgid);
        logger.info("getRecvSendingCount: msgid: {}, count: {}", msgid, sendingCount);

        return sendingCount;
    }

//    private void updateDomainCount(String msgid){
//        new ImEMSDomainCountThread(msgid).start();
//    }
    private void updateDomainCount(String msgid, String state){
        new ImEMSDomainCountThread(msgid, state).start();
    }

    private void checkMainCount() {
        EmsMainService emsService = EmsMainService.getInstance();
        ReceiverService receiverService = ReceiverService.getInstance();
        try {
            List<ImbEmsMain> emsMainList = emsService.getListSending();
            for(ImbEmsMain emsMain : ListUtils.emptyIfNull(emsMainList)) {
                String msgid = emsMain.getMsgid();
                int totalSend = emsMain.getTotal_send();
                int sendingCount = getRecvSendingCount(emsMain.getMsgid());
                String state = emsMain.getState();

                try {
                    // 수신대상자 없음
                    if (ImStateCode.ST_NO_RECV.equals(state)) {
                        logger.info("{} : {}", emsMain.getMsgid(), state);

                        //emsService.updateCurSend(sendingCount, msgid);
                        updateDomainCount(emsMain.getMsgid(), state);
                    } else {
                        if (sendingCount > 0) {
                            if ((totalSend != 0) && (sendingCount != 0) && (totalSend == sendingCount)) {

                                emsService.updateToLoggingState(msgid);

                                updateDomainCount(emsMain.getMsgid(), ImStateCode.ST_LOGGING);
                            } else {
                                emsService.updateCurSend(sendingCount, msgid);
                            }
                        }
                        logger.info( "Log Thread. checkMainLog : " + sendingCount + " - " + totalSend);
                    }
                } catch (Exception exCount) {
                    String errorId = ErrorTraceLogger.log(exCount);
                    logger.error("[{}] checkMainCount error", errorId);
                }
            }
        } catch (SQLException se){
            String errorId = ErrorTraceLogger.log(se);
            logger.error("[{}] - getListSending error", errorId);
        } catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            logger.error("[{}] - getListSending error", errorId);
        }
    }

    @Override
    public void run() {
        try {
            int j = 0;
            while (!isInterrupted() && ImEmsServer.isAlive) {
                checkMainCount();
                for (int i = 0; i < 10; i++) {
                    sleep(1000);
                }
            }

        }catch(InterruptedException ie){
            logger.info("ImEmsLogThread.run interrupted - {}", ie.toString());
        }catch(Exception e){
            logger.info("ImEmsLogThread.run exception - {}", e.toString());
        }
    }
}



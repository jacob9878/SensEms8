package com.imoxion.sensems.server.sender;

import com.imoxion.sensems.server.define.ImStateCode;
import com.imoxion.sensems.server.domain.ImbEmsMain;
import com.imoxion.sensems.server.service.EmsMainService;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

public class ImSenderTraceThread extends Thread {
    private static Logger transferLogger = LoggerFactory.getLogger("TRANSFER");
    private static Logger senderLogger = LoggerFactory.getLogger("SENDER");

    public ImSenderTraceThread() {
        setDaemon(true);
    }

    public void checkSendingState(){
        EmsMainService emsMainService = EmsMainService.getInstance();

        try {
            List<ImbEmsMain> listSendingMail = emsMainService.getListRecentSendingMail();

            for(ImbEmsMain emsMain : ListUtils.emptyIfNull(listSendingMail)){
                String msgid = emsMain.getMsgid();
                String startTime = emsMain.getStart_time();
                transferLogger.info("SenderTraceThread - msgid {} : state {} : starttime{}", msgid, emsMain.getState(), startTime);

                // map에 있는지 조회
                ImbEmsMain mapMain = ImSenderServer.getInstance().getSending(msgid);

                // 030 발송중
                if (ImStateCode.ST_SENDING.equals(emsMain.getState())) {
                    // map 에 존재하지 않으므로 map 추가
                    if (mapMain == null) {
                        if (emsMain != null) {
                            ImSenderServer.getInstance().putSending(msgid, emsMain);
                            transferLogger.info("SenderTraceThread 00: {} : {}", msgid, emsMain.getStart_time());
                        }
                    } else { // map 에 이미 존재
                        // 동일한 msgid 인데 시작시간이 다르면 기존 맵 제거 후 새로 추가
                        if(StringUtils.isNotEmpty(mapMain.getStart_time()) && !emsMain.getStart_time().equals(mapMain.getStart_time())){
                            ImSenderServer.getInstance().removeSending(msgid);
                            ImSenderServer.getInstance().putSending(msgid, emsMain);
                            transferLogger.info("SenderTraceThread 11: {} : {} = {}", msgid, emsMain.getStart_time(), mapMain.getStart_time());
                        }
                    }
                // 030 이 아니면 발송중인 상태가 아님
                } else {
                    if(mapMain != null){
                        ImSenderServer.getInstance().removeSending(msgid);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while(true){
            try {
                // 10초 간격으로 체크함
                checkSendingState();

                // 10sec
                Thread.sleep(10000);
            } catch(InterruptedException e){
                transferLogger.error("ImSenderTraceThread interrupted, Stop.");
            }
        }
    }
}

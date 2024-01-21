package com.imoxion.sensems.server.emsd;

import com.imoxion.sensems.server.service.EmsMainService;
import com.imoxion.sensems.server.service.ReceiverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * **********************************************
 * ImEMSDomainCountThread
 * **********************************************
 */
public class ImEMSDomainCountThread extends Thread {
    private static Logger logger = LoggerFactory.getLogger("EMSD");
    private String msgid = "";
    private String currState = "";

//    public ImEMSDomainCountThread(String msgid) {
//        this.msgid = msgid.trim();
//    }

    public ImEMSDomainCountThread(String msgid, String cur_state) {
        this.msgid = msgid.trim();
        this.currState = cur_state.trim();
    }

    private void setDomainCount(){
        ReceiverService receiverService = ReceiverService.getInstance();
        try {
            logger.debug("setDomainCount: msgid: {}", this.msgid);
            receiverService.insertHostCountByDomain(this.msgid);
        } catch (Exception e){}
    }

    private void setErrorCount(){
        ReceiverService receiverService = ReceiverService.getInstance();
        try {
            logger.debug("setErrorCount: msgid: {}", this.msgid);
            receiverService.updateErrorCountByDomain(this.msgid);
        } catch (Exception e){}
    }

    private void setMainEndEx(){
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
        String currDate = df.format(new Date());

        EmsMainService emsMainService = EmsMainService.getInstance();
        try {
            logger.debug("setMainEndEx: msgid: {}", this.msgid);
            emsMainService.updateMainEndEx(this.msgid, this.currState, currDate);
        } catch (Exception e){}
    }

    public void run() {
        logger.info("ImEMSDomainCountThread.run: msgid: {}, curState: {}", this.msgid, this.currState);

        // 도메인별 발송/성공/실패 카운트
        setDomainCount();

        //setErrorCount
        setErrorCount();

        // setMainEndEx
        setMainEndEx();

        logger.info("SendComplete - msgid: {}", this.msgid);
    }

}

/*
 * �ۼ��� ��¥: 2005. 4. 13.
 *
 * TODO ��� ���Ͽ� ���� ���ø�Ʈ�� �����Ϸx� ��=8�� �̵��Ͻʽÿ�.
 * â - ȯ�� ��d - Java - �ڵ� ��Ÿ�� - �ڵ� ���ø�Ʈ
 */
package com.imoxion.sensems.server.emsd;


import com.imoxion.common.thread.ImBlockingQueue;
import com.imoxion.sensems.server.ImEmsServer;
import com.imoxion.sensems.server.beans.ImEmsMailData;
import com.imoxion.sensems.server.config.ImEmsConfig;
import com.imoxion.sensems.server.logger.ErrorTraceLogger;
import com.imoxion.sensems.server.smtp.ImBizemsSmtp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 *
 */
public class ImEmsDistThread extends Thread {
    private static Logger logger = LoggerFactory.getLogger("EMSD");

    private ImBizemsSmtp smtp = null;
    public static ImBlockingQueue bqMail = new ImBlockingQueue();

    public ImEmsDistThread() {
        smtp = new ImBizemsSmtp();
        smtp.setSocketTimeout(0);

        setDaemon(true);
    }


    public void run() {
        try {
            logger.info("ImDistThread Start");
            while (true && ImEmsServer.isAlive) {
                ArrayList<ImEmsMailData> arrMail = (ArrayList<ImEmsMailData>) bqMail.dequeue();
                logger.info("ImDistThread dequeue: {}", arrMail.size());
                if (smtp == null) {
                    smtp = null;
                    smtp = new ImBizemsSmtp();
                }

                if (!smtp.connect(ImEmsConfig.getInstance().getSenderHost(), ImEmsConfig.getInstance().getSenderInboundPort())){
                    logger.info("ImDistThread : connect sender failed.. " + ImEmsConfig.getInstance().getSenderHost() + ":" + ImEmsConfig.getInstance().getSenderInboundPort());
                    // 5초간 대기
                    Thread.sleep(5000);
                    if (!smtp.connect(ImEmsConfig.getInstance().getSenderHost(), ImEmsConfig.getInstance().getSenderInboundPort())){
                        break;
                    }
                }

                if(!smtp.helo2("emsd.localhost")){
                    logger.info("ImDistThread : helo failed.. " + ImEmsConfig.getInstance().getSenderHost() + ":" + ImEmsConfig.getInstance().getSenderInboundPort());
                    break;
                }

                if (arrMail != null) {
                    if (!smtp.dataObject(arrMail)) {
                        logger.info("ImDistThread Error Data : {}", smtp.getError());

                        // 발송 실패시 재연결해서 재발송 시도...
                        try {
                            if (smtp != null) smtp.close();
                            smtp = null;
                            smtp = new ImBizemsSmtp();
                            if (!smtp.connect(ImEmsConfig.getInstance().getSenderHost(), ImEmsConfig.getInstance().getSenderInboundPort())){
                                logger.info("ImDistThread : connect sender failed.. " + ImEmsConfig.getInstance().getSenderHost() + ":" + ImEmsConfig.getInstance().getSenderInboundPort());
                                break;
                            }

                            logger.info("ImDistThread retry send");
                            if(!smtp.helo2("emsd.localhost")){
                                logger.info("ImDistThread : helo failed.. " + ImEmsConfig.getInstance().getSenderHost() + ":" + ImEmsConfig.getInstance().getSenderInboundPort());
                                break;
                            }

                            if (!smtp.dataObject(arrMail)) {
                                logger.info("ImDistThread Error Data, GiveUp. : {}", smtp.getError());
                            }
                        } catch (Exception e) {
                            if (smtp != null) smtp.close();
                        }
                    }

                    logger.info("ImEmsDistThread Transfer to Sender OK - {}", arrMail.size());
                    try { if (smtp != null) smtp.close(); smtp = null; } catch (Exception e) {}
                }

                arrMail.clear();
                arrMail = null;
            }
        }catch(InterruptedException ex) {
            //ex.printStackTrace();
            logger.info("<" +  Thread.currentThread().getName() + "> Interrupted & Stopped.");
        } catch (Exception ex) {
            String errorId = ErrorTraceLogger.log(ex);
            logger.error("{} - ImDistThread Error - {}", errorId );
        } finally {
            try { if (smtp != null) smtp.close(); } catch (Exception e) {}
        }

        // ImTransferRecvThread 를 자꾸 띄우지 않게 한다.
        ImEmsMainThread.isPossibleSend = false;
        logger.info("--- ImDistThread END ---");
    }
}

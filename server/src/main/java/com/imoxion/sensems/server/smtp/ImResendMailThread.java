package com.imoxion.sensems.server.smtp;

import com.imoxion.common.thread.ImBlockingQueue;
import com.imoxion.common.util.ImFileUtil;
import com.imoxion.sensems.server.config.ImSmtpConfig;
import com.imoxion.sensems.server.logger.ErrorTraceLogger;
import com.imoxion.sensems.server.logger.event.TransmitLogger;
import com.imoxion.sensems.server.service.MessageQueueService;
import com.imoxion.sensems.server.util.ImSmtpSendData;
import com.imoxion.sensems.server.util.ImSmtpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class ImResendMailThread extends Thread {

	private static Logger smailLogger = LoggerFactory.getLogger("SMAIL");

	private static ImBlockingQueue m_bqrsnd = new ImBlockingQueue();

	static int QUE_RSND_SCAN_WAIT = 1 * 1000;
	static int QUE_RSND_SCAN_INTERVAL = 5 * 1000;
	private boolean alive = false;

	public ImResendMailThread(){
		setDaemon(true);
	}
	/*
	public static void addResendMailQueue(ImSmtpSendData sd){
		m_bqrsnd.enqueue(sd);
	}

	public static ImSmtpSendData extractResendMailQueue(){
		ImSmtpSendData sd = null;
		try{
			sd = (ImSmtpSendData)m_bqrsnd.dequeue();
		}catch(Exception ex){
			
		}
		
		return sd;
	}
	*/
	public static void addResendMailQueue(String queuePath){
		m_bqrsnd.enqueue(queuePath);
	}
	
	public boolean isThAlive() {
		return alive;
	}

	
	public static void addResendMailQueue(ImSmtpSendData issd, String queuePath) {
        ObjectOutputStream objS = null;
        BufferedOutputStream fos = null;
        try {
            
            // info
            File f = new File(queuePath); 
			objS = new ObjectOutputStream(new FileOutputStream(f));
        	objS.writeObject(issd);
        		
        	m_bqrsnd.enqueue(queuePath);

        } catch (Exception ex) {
    		String errorId = ErrorTraceLogger.log(ex);
    		smailLogger.error("{} - [ResendMail] " , errorId );
        } finally {
        	try{
    			if(objS != null)objS.close();
    		}catch(IOException ex1){}
            try {
                fos.close();
            } catch (Exception ex) {
            }
        }
    }

	public static String extractResendMailQueue(){
		String sQueuePath = null;
		try {
			sQueuePath = (String)m_bqrsnd.dequeue();
		} catch(Exception e){
    		String errorId = ErrorTraceLogger.log(e);
			smailLogger.error("{} - [ResendMail] " , errorId );
		}
		
		return sQueuePath;
	}


	public void run()
	{	
		ImSmtpSendData sd = null;
		int nElapsedTime = 0;
		try{
			alive = true;
			
			while(!isInterrupted()){
				sleep(QUE_RSND_SCAN_WAIT);
				nElapsedTime += QUE_RSND_SCAN_WAIT;

				if(nElapsedTime > QUE_RSND_SCAN_INTERVAL){	
//					boolean bLocal = false;
					/*
					 * 0: Normal Inbound
					 * 1: Normal Outbound
					 * 2: Bulk Inbound
					 * 3: Bulk Outbound
					 */
					int spoolType = 0; // 0:Normail inbound
//					boolean bBulk = false;
					int queueIndex = 0;

					nElapsedTime = 0;
					
					/*
					sd = (ImSmtpSendData) m_bqrsnd.dequeue();
					
					String sRsndPath = sd.getQueueDir() + File.separator + "rsnd" + File.separator + sd.getQueueFile();

					if(!sd.getSendDataInfo(sRsndPath)){
						continue;
						//throw new ImMessQueueException();
					}*/
					String sRsndPath = (String)m_bqrsnd.dequeue();
					sd = ImSmtpUtil.getSendData(sRsndPath);
					if(sd == null) {
						ImSmtpUtil.cleanQueue(sRsndPath);
						continue;
					}
					
					// 경로를 확인해서 대용량, 외부 메일 여부를 확인한다.
					if((queueIndex = sRsndPath.indexOf("queue")) >= 0){
						// 일반 메일일 경우 큐인덱스를 가져온다.
						String queueNum = sRsndPath.substring(queueIndex+5, queueIndex+6);
						queueIndex = Integer.parseInt(queueNum);
						spoolType = 0;
					}else if(sRsndPath.indexOf("bulk") >= 0){
						// 내부 대용량 큐  메일
						spoolType = 2;
					}

					
					// slog analyze (resend queue에 넣을때 updateRetryInfo 실행하므로 여기선 제외)
					//String sSlogFile = sd.getQueueDir() + File.separator + "slog" + File.separator + sd.getQueueFile();
					//sd.updateRetryInfo(sSlogFile);
//ImLoggerEx.error("SMAIL", "resend : " + ImSmtpServer.m_config.getMaxRetry() + " / " + sd.getTryNumbers() + " / " + sd.getLastTryTime());
					try{
						// 
						if(ImSmtpConfig.getInstance().getMaxRetry() <= sd.getTryNumbers()){
							TransmitLogger transmitLogger = new TransmitLogger();

							if(!sd.isDSN()){
								if(ImSmtpConfig.getInstance().isUseDsn()) {
									ImSmtpUtil.sendNotifyErrorMessage(sd);
								}else {
									smailLogger.info("[{}] DSN Not Send: {} -> {}, SUBJ:{}", sd.getTraceID(), sd.getFrom(), sd.getRcptto(), sd.getSubject());
								}
			                }
			                
							smailLogger.info("[{}] Too many Retry, Remove From Queue: {}/{}/{}, SUBJ:{}", sd.getTraceID(), sd.getFrom(), sd.getRcptto(), sd.getQueueFile(), sd.getSubject());
							transmitLogger.setErrcode(sd.getSendResultCode());
							transmitLogger.setErrmsg(sd.getSendResultErrMsg());
							transmitLogger.setDescription("delivery.remote.resend.error");
							transmitLogger.setResultState(TransmitLogger.STATE_FAIL);
							ImSmtpUtil.doTransmitLog(transmitLogger, sd);

							//
							ImSmtpUtil.cleanQueue(sd.getQueueDir(), sd.getQueueFile());
							continue;
						}
						
						// 재발송 시간이 아니면 다시 큐에 넣음
						if(System.currentTimeMillis() < (Long.parseLong(sd.getLastTryTime()) + 
								(ImSmtpConfig.getInstance().getRetryInterval() * sd.getTryNumbers()))){
							//m_bqrsnd.enqueue(sd);
							m_bqrsnd.enqueue(sRsndPath);
						} else {
							// 재발송함
							String sQueuePath = sd.getQueueDir() + File.separator + "mess" + File.separator + sd.getQueueFile();
							String sBodyPath = sQueuePath + ".body";
							ImFileUtil.moveFile(sRsndPath, sQueuePath);
							ImFileUtil.moveFile(sRsndPath + ".body", sBodyPath);
							
							smailLogger.info("[{}] Move to send - {}: {}/{}/{}, SUBJ:{}", sd.getTraceID(), sd.getTryNumbers(), sd.getFrom(), sd.getRcptto(), sd.getQueueFile(), sd.getSubject());
							
							MessageQueueService queueService = MessageQueueService.getInstance();
							switch(spoolType){
							case 0:
								queueService.addMessageQueue(sQueuePath,queueIndex);
								break;
							case 2:
								queueService.addBulkMessageQueue(sQueuePath);
								break;
							}
						}
					} catch(Exception ex){
			    		String errorId = ErrorTraceLogger.log(ex);
			    		smailLogger.error("{} - [ResendMail] " , errorId );
						ImSmtpUtil.cleanQueue(sd.getQueueDir(), sd.getQueueFile());
					}
				}
			}
		} catch( InterruptedException ie){
			smailLogger.debug("ImResendMailThread Interrupted");
		} catch (Exception e){
    		String errorId = ErrorTraceLogger.log(e);
    		smailLogger.error("{} - [ResendMail] " , errorId );
		}
		
		alive = false;
		/**
		 * Author : jungyc
		 * Date : 2007.01.04
		 * Content : 스레드가 종료될 때 로그를 남김
		 */
		smailLogger.info("Resend Thread End : {}",this.toString());
		/* 2007.01.04 jungyc */
	}

	public static int getQueueCount(){
		return m_bqrsnd.getSize();
	}
}

package com.imoxion.sensems.server.smtp;

import com.imoxion.common.util.ImStringUtil;
import com.imoxion.sensems.server.config.ImSmtpConfig;
import com.imoxion.sensems.server.logger.event.TransmitLogger;
import com.imoxion.sensems.server.nio.ImSensSmtpApplication;
import com.imoxion.sensems.server.util.ImSmtpSendData;
import com.imoxion.sensems.server.util.ImSmtpSendingInfo;
import com.imoxion.sensems.server.util.ImSmtpUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class ImNetlinkSpoolThread extends Thread {

	private static Logger logger = LoggerFactory.getLogger(ImNetlinkSpoolThread.class);
	private static Logger smtpLogger = LoggerFactory.getLogger("SMTP");
	private static Logger smailLogger = LoggerFactory.getLogger("SMAIL");
	
	// 스풀 스캔 간격(기본 3초)
	private static int SCAN_INTERVAL = 3000; 

	private String queuePath = "";
	
	public ImNetlinkSpoolThread(String queuePath){
		this.queuePath = queuePath;
	}
	
	public ImNetlinkSpoolThread(){
		if(this.queuePath.equals("")){
			this.queuePath = System.getProperty("sensems.home") + File.separator + "netlink_spool";
		}
	}	
	
	private void checkLocalSpoolDir(String queueDir){
		try{
			if(StringUtils.isEmpty(queueDir)) return;
			
			String sLocalSpoolDir = queueDir;
			// accept spool이 있는지 체크해서 없으면 return
			File localSpoolDir = new File(sLocalSpoolDir);

			//smtpLogger.info( "ImLocalSpoolThread.checkLocalSpoolDir : " + sLocalSpoolDir);

			if(!localSpoolDir.exists() && !localSpoolDir.mkdirs()) {
				return;
			}
			File[] spoolDirs = localSpoolDir.listFiles();
			// 잠시 대기
			Thread.sleep(100);
			
			int count = 0;
			
			if( spoolDirs != null ) {
				for (File f : spoolDirs) {
					if (f.isDirectory()) {
						continue;
					}
					// 한번에 천통씩 처리
					if(count > 999) {
						break;
					}

					// log 파일을 읽어들인다.
					if (f.getName().endsWith("body")) {
						try {
							smtpLogger.info("<<<<<< checkLocalSpoolDir: {}", f.getAbsolutePath() );
							ImSmtpSendingInfo sendInfo = ImSmtpUtil.getSendingInfoEx(f.getAbsolutePath());
							
							long lMsgSize = f.length();
							
							StringBuffer bfHeader = new StringBuffer();
			    			String sRcpt = sendInfo.getRcptto();
			    			String sMsgId = ImStringUtil.getStringBefore(f.getName(), ".");
			    			
			    			ImSmtpSendData issd = new ImSmtpSendData();
			    			issd.setMsgID(sMsgId);
			    			issd.setTraceID(sendInfo.getTraceid());
			    			issd.setFrom(sendInfo.getMailfrom());
			    			issd.setRcptto(sRcpt);
			    			issd.setPeerIP(sendInfo.getFromIP());
			    			issd.setDomain(sendInfo != null ? sendInfo.getDomain():"");
			    			issd.setMailKey(sendInfo != null ? sendInfo.getMailKey():"");
			    			issd.setReserveTime(sendInfo != null ? sendInfo.getReserveTime():"");
//			    			issd.setLocalMsgid(smtps.getLocalMsgid());
//			    			issd.setLocalDomain(smtps.getLocalDomain());
//			    			issd.setLocalUserid(smtps.getLocalUserid());
//			    			issd.setIsRelay(nIsRelay);
//			    			issd.setBulk(smtps.isBulk());
//			    			issd.setLogonDomain(smtps.getLogonDomain());
//			    			issd.setLogonUser(smtps.getLogonUser());
							issd.setSubject(sendInfo.getSubject());
							issd.setSenddate(sendInfo.getSenddate());
							issd.setMailsize(lMsgSize);
	
							// fromIP 를 구하지 못한 메일은 smtp 에 접속한 IP 를 사용한다.
							issd.setFromIP(sendInfo.getFromIP());
							
			    			if(sendInfo != null ){
								if( sendInfo.getReciptKey() != null )
									issd.setReceiptKey(sendInfo.getReciptKey());
	
								if(sendInfo.getAhost() != null)
									issd.setAhost(sendInfo.getAhost());
	
								if(sendInfo.getUserid() != null)
									issd.setUserid(sendInfo.getUserid());
	
								if(sendInfo.getTbl_no() != null)
									issd.setTbl_no(sendInfo.getTbl_no());
	
								if(sendInfo.getPart_no() != null)
									issd.setPart_no(sendInfo.getPart_no());
	
								if(sendInfo.getXmailer() != null)
									issd.setXmailer(sendInfo.getXmailer());
	
								if(sendInfo.getFromIP() != null){
									issd.setFromIP(sendInfo.getFromIP());
								}
							}
	
							bfHeader.append("Received: from ")
								.append(sendInfo.getDomain())
								.append("(in)")
								.append(" (")
								.append(sendInfo.getFromIP())
								.append(")\r\n")
								.append("\tby ")
								.append(ImSmtpConfig.getInstance().getRootDomain())
								.append(" with ")
								.append(ImSensSmtpApplication.SMTP_SERVER_VERSION)
								.append("\r\n")
								.append("\tid <")
								.append(sMsgId)
								.append(">");
							//if(smtps.getCurrRcpt() == 1){
								bfHeader.append(" for <")
									.append(sendInfo.getRcptto())
									.append(">");
							//} 
							bfHeader.append(" from <")
								.append(sendInfo.getMailfrom())
								.append(">")
								.append("\r\n");
							
							String sHeader = bfHeader.toString();
			                
							smtpLogger.info("In Normal Queue ( {} / {} -> {} / {} ) OK",  sendInfo.getFromIP(), sendInfo.getMailfrom(), sRcpt, lMsgSize);
							String sUid = ImSmtpUtil.addQueueNetlink( issd, sHeader, sMsgId, f.getAbsolutePath());
							
							TransmitLogger transmitLogger = new TransmitLogger();
//							transmitLogger.setServerid(ImSmtpConfig.getInstance().getServerID());
//		            		transmitLogger.setTraceid(issd.getTraceID());
//		                    transmitLogger.setWork(TransmitLogger.WORK_DELIVERY);
//		                	transmitLogger.setIp(issd.getPeerIP());
//							transmitLogger.setFrom(issd.getFrom());
//							transmitLogger.setTo(issd.getRcptto());
//							transmitLogger.setSubject(issd.getSubject());
//							transmitLogger.setSize(issd.getMailsize());
							transmitLogger.setDescription("delivery.load.spool.success");
							transmitLogger.setEtc(f.getName());
//							transmitLogger.setDomain(issd.getDomain());
//							transmitLogger.info();
							transmitLogger.setResultState(TransmitLogger.STATE_SUCCESS);
							ImSmtpUtil.doTransmitLog(transmitLogger, issd);
							
				        	smtpLogger.info("LocalSpool / Data ( {} / {} -> {} / {} ) OK", sendInfo.getFromIP(), sendInfo.getMailfrom(), sRcpt , lMsgSize );
						}catch(Exception ee) {
							smtpLogger.error("checkLocalSpoolDir error: {}", ee);
						}finally{
				    		// delete local file
							if (f.exists()) {
								f.delete();
							}							
						}		
						
						count++;
					}
					// 통당 처리 간격
					Thread.sleep(20);
				}
			}
		}catch(Exception ex){
			smtpLogger.error( "ImNetlinkSpoolThread.checkLocalSpoolDir : " + ex.getMessage());
		}
	}
	
	
	public void run(){
		try{
			while(!isInterrupted()){
				// 
				checkLocalSpoolDir(ImSmtpConfig.getInstance().getNetLinkSpoolPath());
				
				// netlink_dir2
				/*if(ImSensProxyServer.isSendNetLinkDir2) {
					checkLocalSpoolDir(ImSensProxyServer.g_sNetLinkDir2);
				}*/
				// 대기한다.
				sleep(SCAN_INTERVAL);
			}
		}catch(Exception ex){
			smtpLogger.error( "ImNetlinkSpoolThread.run : {}", ex.getMessage());
		}
	}
}

package com.imoxion.sensems.server.smtp;

import com.imoxion.common.mail.ImMessage;
import com.imoxion.common.net.ImSmtp;
import com.imoxion.common.thread.ImBlockingQueue;
import com.imoxion.common.util.ImFileUtil;
import com.imoxion.common.util.ImUtils;
import com.imoxion.security.ImSecurityLib;
import com.imoxion.sensems.server.config.ImSmtpConfig;
import com.imoxion.sensems.server.logger.ErrorTraceLogger;
import com.imoxion.sensems.server.logger.event.TransmitLogger;
import com.imoxion.sensems.server.service.DnsSearchService;
import com.imoxion.sensems.server.service.ImDkimSigner;
import com.imoxion.sensems.server.service.MessageQueueService;
import com.imoxion.sensems.server.util.ImSmtpSendData;
import com.imoxion.sensems.server.util.ImSmtpUtil;
import com.imoxion.sensems.server.util.UUIDService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Session;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

public class ImSendMailThread extends Thread {

	private static Logger logger = LoggerFactory.getLogger(ImSendMailThread.class);
	private static Logger smtpLogger = LoggerFactory.getLogger("SMTP");
	private static Logger smailLogger = LoggerFactory.getLogger("SMAIL");

	private ImBlockingQueue mailQueue = null;
	private int queueIndex = 0;
	private boolean alive = false;
	private boolean bulk = false;
	
	
   	public ImSendMailThread(){
		setDaemon(true);
	}

   	public ImSendMailThread(boolean isBulk){
		setDaemon(true);
		
		bulk = isBulk;
	}

   	public ImSendMailThread(int nIndex, ImBlockingQueue queue){
   		queueIndex = nIndex;
   		mailQueue = queue;
   		
		setDaemon(true);
	}

   	public boolean isThAlive() {
		return alive;
	}
	
	private int doLocalRemoteSend(String sRcptDomain, ImSmtpSendData sd, String sSlogFile, String sQueuePath){
		int ret = -1;
		// 게이트웨이가 설정되어 있지 않다면 도메인 정보로 MX 레코드를 가져온다.
		DnsSearchService ids = DnsSearchService.getInstance();
		Iterator<String> mx = ids.findMXRecords(sRcptDomain).iterator();

		TransmitLogger transmitLogger = new TransmitLogger();
		// 외부로 메일을 발송한다.
		try{
			int nSendResult = doRemoteSend(mx, sd);
			if(nSendResult == 1){	// 메일 발송 성공
				// Queue 메시지 파일을 지운다.
				ImSmtpUtil.cleanQueue(sd.getQueueDir(), sd.getQueueFile());
				ret = 1;
				// 발송중인 상태를 업데이트한다.(성공)
			} else if(nSendResult == -1){	// 재시도가 불가능한 발송 실패
				// 리턴 메일을 발송하고 큐에서 메시지를 지운다.
				// 발송실패알림 메일인 경우 발송하지 않음,
				if(!sd.isDSN()){
					if(ImSmtpConfig.getInstance().isUseDsn()) {
						ImSmtpUtil.sendNotifyErrorMessage(sd);
					}else {
						smailLogger.info("[{}] DSN Not Send: {} -> {}, SUBJ:{}", sd.getTraceID(), sd.getFrom(), sd.getRcptto(), sd.getSubject());
					}
				} else {
                	smailLogger.info("[{}] DSN Send fatal error: {} -> {}, SUBJ:{}, SIZE:{}", 
                			sd.getTraceID(), sd.getFrom(), sd.getRcptto(), sd.getSubject(), ImUtils.byteFormat(sd.getMailsize(), 2));

					transmitLogger.setDescription("delivery.remote.dsn.error");
					transmitLogger.setErrcode(sd.getSendResultCode());
					transmitLogger.setErrmsg("DSN Send fatal error");
					sd.setSendResultErrMsg("DSN Send fatal error");
					transmitLogger.setResultState(TransmitLogger.STATE_FAIL);
					ImSmtpUtil.doTransmitLog(transmitLogger, sd);
                }
                
                ImSmtpUtil.cleanQueue(sd.getQueueDir(), sd.getQueueFile());

			} else {	// 재시도 가능한 메일
				// 발송실패 리턴메일은 재발송 시도하지 않는다.
				if(sd.isDSN()){
					ImSmtpUtil.cleanQueue(sd.getQueueDir(), sd.getQueueFile());
                	smailLogger.info("[{}] DSN Send fatal error: {} -> {}({}), SUBJ:{}, SIZE:{}",
                			sd.getTraceID(), sd.getFrom(), sd.getRcptto(), nSendResult, sd.getSubject(), ImUtils.byteFormat(sd.getMailsize(), 2));

					transmitLogger.setDescription("delivery.remote.dsn.resend.error");
					transmitLogger.setErrcode(sd.getSendResultCode());
					transmitLogger.setErrmsg("DSN Send fatal error");
					sd.setSendResultErrMsg("DSN Send fatal error");
					transmitLogger.setResultState(TransmitLogger.STATE_FAIL);
					ImSmtpUtil.doTransmitLog(transmitLogger, sd);

                	return ret;
                }
				
				// 재발송 메일 큐로 옮긴다.
				String sRsndPath = sd.getQueueDir() + File.separator + "rsnd" + File.separator + sd.getQueueFile();
				
				String sRsndBodyPath = sRsndPath + ".body";

				sd.updateRetryInfo(sSlogFile);
				sd.setRetryNow(true);
				if(ImFileUtil.moveFile(sQueuePath, sRsndPath)){
					if(ImFileUtil.moveFile(sQueuePath + ".body", sRsndBodyPath)){
						ImResendMailThread.addResendMailQueue(sd, sRsndPath);

						transmitLogger.setDescription("delivery.remote.resend");
						transmitLogger.setErrcode(sd.getSendResultCode());
						transmitLogger.setErrmsg("Remote resend");
						sd.setSendResultErrMsg("Remote resend");
						transmitLogger.setResultState(TransmitLogger.STATE_ING);
						ImSmtpUtil.doTransmitLog(transmitLogger, sd);

						smailLogger.info("[{}] Remote resend : {}/{}, SUBJ:[{}], SIZE:{}", sd.getTraceID(), sd.getFrom(), sd.getRcptto(), sd.getSubject(), ImUtils.byteFormat(sd.getMailsize(), 2) );
						//smailLogger.info("Remote Resend : " + sd.getFrom() + " / " + sd.getRcptto());
					}
				} else {

					transmitLogger.setDescription("delivery.remote.resend.error");
					transmitLogger.setErrcode(sd.getSendResultCode());
					transmitLogger.setErrmsg("Remote Resend file move error");
					sd.setSendResultErrMsg("Remote Resend file move error");
					transmitLogger.setResultState(TransmitLogger.STATE_FAIL);
					ImSmtpUtil.doTransmitLog(transmitLogger, sd);
					
					smailLogger.info("Remote Resend file move error");
				}
			}
		} catch (Exception ee){
    		String errorId = ErrorTraceLogger.log(ee);
    		smailLogger.error("{} - [SendMail] " , errorId );
		}
		
		return ret;
	}
	
	private boolean prependToFile(String sAddHeader, String srcPath, String destPath){
		BufferedInputStream bis = null;
		BufferedOutputStream fos = null;
		try {
			bis = new BufferedInputStream(new FileInputStream(srcPath));
			// header + body
			fos = new BufferedOutputStream(new FileOutputStream(destPath));

			if(sAddHeader !=null && !sAddHeader.equals("")){
				fos.write(sAddHeader.getBytes());
			}

			byte[] buffer = new byte[8192];
			int read = -1;
			while ((read = bis.read(buffer)) != -1) {
				fos.write(buffer, 0, read);
			}
			fos.flush();
		} catch (Exception ex) {
			String errorId = ErrorTraceLogger.log(ex);
			smailLogger.error("[{}] - prependToFile error" , errorId );
			return false;
		} finally {
			try { if(bis != null) bis.close(); } catch (Exception ex) {}
			try { if(fos != null) fos.close(); } catch (Exception ex) {}
		}

		return true;
	}
	
	/**
	 * 망연계용 디렉토리에 파일 저장
	 * @param sd
	 * @param sQueuePath
	 * @return
	 */
	private boolean doGatewaySavePath(ImSmtpSendData sd, String sQueuePath) {
		boolean result = false;
		
		File srcFile = new File(sd.getContentsFile());
		String destPath = ImSmtpConfig.getInstance().getGatewaySendPath() + File.separator + srcFile.getName();
		File destFile = new File(destPath);
		
		try {
			FileUtils.copyFile(srcFile, destFile);
			result = true;
			smailLogger.info(">>>>>> doGatewaySavePath OK: {}", destFile);
		} catch(Exception e) {
			String errorId = ErrorTraceLogger.log(e);
    		smailLogger.error("{} - [doGatewaySavePath] " , errorId );
    	}
		
		return result;
	}
	
	private int doGatewayRemoteSend(String sRcptDomain,ImSmtpSendData sd,String sSlogFile,String sQueuePath){
		TransmitLogger transmitLogger = new TransmitLogger();
        
		int ret = 1;
		int nSendResult = -1;
		String sHost = "";
		String[] errMsg = new String[] {""};
		// 게이트웨이가 설정되어 있다면 게이트웨이 서버 정보를 가져온다.(IP)
		if( ImSmtpConfig.getInstance().isUseGateway() ) {
			String[] arrMx = new String[ImSmtpConfig.getInstance().getGatewayServerIp().size()];
			arrMx = ImSmtpConfig.getInstance().getGatewayServerIp().toArray(arrMx);
			for (int i = 0; i < arrMx.length; i++) {
				sHost = arrMx[i];
				
				nSendResult = doSmtpSend(sHost, ImSmtpConfig.getInstance().getSmtpPortOut(), sd, errMsg);

				if (nSendResult >= 500 && nSendResult < 600) {
					// fatal error
					transmitLogger.setDescription("delivery.remote.gateway.error");
					transmitLogger.setErrcode(nSendResult);
	                transmitLogger.setErrmsg(errMsg[0]);
					sd.setSendResultErrMsg(errMsg[0]);
					transmitLogger.setResultState(TransmitLogger.STATE_FAIL);
					ImSmtpUtil.doTransmitLog(transmitLogger, sd);
		                
					smailLogger.info("[{}] Remote send Gateway fatal error : {}/{} -> {}({}), SUBJ:[{}], SIZE:{}",
							sd.getTraceID(), sHost, sd.getFrom(), sd.getRcptto(), nSendResult, sd.getSubject(), ImUtils.byteFormat(sd.getMailsize(), 2));
					nSendResult = -1;
					break;
				}

				if (nSendResult != 1) {
					transmitLogger.setDescription("delivery.remote.gateway.error");
					transmitLogger.setErrcode(nSendResult);
	                transmitLogger.setErrmsg(errMsg[0]);
					sd.setSendResultErrMsg(errMsg[0]);

					smailLogger.info( "[{}] Remote send Gateway error : {}/{} -> {}({}), SUBJ:[{}], SIZE:{}", 
							sd.getTraceID(), sHost, sd.getFrom(), sd.getRcptto(), nSendResult, sd.getSubject(), ImUtils.byteFormat(sd.getMailsize(), 2));
					
					// 재시도가 불필요한 메일은 재시도 큐에 넣지 않는다.
					if ((sd.getTryNumbers() + 1) > ImSmtpConfig.getInstance().getMaxRetry()) {
						// 다음 재시도 회수가 설정된 회수보다 크면 재시도하지 않는다.
						transmitLogger.setResultState(TransmitLogger.STATE_FAIL);
						ImSmtpUtil.doTransmitLog(transmitLogger, sd);
						nSendResult = -1;
						break;
					} else {
						transmitLogger.setResultState(TransmitLogger.STATE_ING);
						ImSmtpUtil.doTransmitLog(transmitLogger, sd);
					}
					nSendResult = 0;
				} else {
					break;
				}
			}
		}
		
		if(nSendResult == 0){	// 재발송이 가능한 메일 발송 실패
			transmitLogger.setDescription("delivery.remote.gateway.error.resend");
			transmitLogger.setArgument(sHost);
			ImSmtpUtil.doTransmitLog(transmitLogger, sd);
			
			// 재발신 큐로 메시지를 옮긴다.
			String sRsndPath = sd.getQueueDir() + File.separator + "rsnd" + File.separator + sd.getQueueFile();
			String sRsndBodyPath = sRsndPath + ".body";

			sd.updateRetryInfo(sSlogFile);
			sd.setRetryNow(true);
			if(ImFileUtil.moveFile(sQueuePath, sRsndPath)){
				if(ImFileUtil.moveFile(sQueuePath + ".body", sRsndBodyPath)){
					ImResendMailThread.addResendMailQueue(sd, sRsndPath);
					
					// 발송중인 상태를 업데이트한다.(재시도)
					transmitLogger.setDescription("delivery.remote.gateway.resend");
					transmitLogger.setArgument(sHost);
					transmitLogger.setErrmsg("Remote send Gateway Resend");
					sd.setSendResultErrMsg("Remote send Gateway Resend");
					transmitLogger.setResultState(TransmitLogger.STATE_ING);
					ImSmtpUtil.doTransmitLog(transmitLogger, sd);

					smailLogger.info( "Remote send Gateway Resend : {}/{} -> {}, SUBJ:[{}], SIZE:{}", 
							sHost, sd.getFrom(), sd.getRcptto(), sd.getSubject(), ImUtils.byteFormat(sd.getMailsize(), 2) );
				}
			}
		} else if(nSendResult == -1){	// 재발송이 불가능한 메일 발송 실패
			// 메일링리스트일경우 발송하지 않음,
			if(!sd.isDSN()){
				if(ImSmtpConfig.getInstance().isUseDsn()) {
					ImSmtpUtil.sendNotifyErrorMessage(sd);
				}else {
					smailLogger.info("[{}] DSN Not Send: {} -> {}, SUBJ:{}", sd.getTraceID(), sd.getFrom(), sd.getRcptto(), sd.getSubject());
				}
			}

			ImSmtpUtil.cleanQueue(sd.getQueueDir(), sd.getQueueFile());
			
			transmitLogger.setDescription("delivery.remote.gateway.error");
			transmitLogger.setErrcode(nSendResult);
			transmitLogger.setErrmsg("Remote send Gateway fatal error");
			sd.setSendResultErrMsg("Remote send Gateway fatal error");
			transmitLogger.setResultState(TransmitLogger.STATE_FAIL);
			ImSmtpUtil.doTransmitLog(transmitLogger, sd);

			
			smailLogger.info("Remote send Gateway fatal error : {}/{} -> {}({}), SUBJ:[{}], SIZE:{}", 
					sHost, sd.getFrom(), sd.getRcptto(), nSendResult, sd.getSubject(), ImUtils.byteFormat(sd.getMailsize(), 2));
			ret = -1;
		} else {	// 발송 성공
			// 큐에서 메시지를 지운다.
			ImSmtpUtil.cleanQueue(sd.getQueueDir(), sd.getQueueFile());

			transmitLogger.setDescription("delivery.remote.gateway.send.success");
			transmitLogger.setResultState(TransmitLogger.STATE_SUCCESS);
			ImSmtpUtil.doTransmitLog(transmitLogger, sd);
			
			smailLogger.info( "Remote send Gateway : {}/{} -> {}, SUBJ:[{}], SIZE:{} Send OK", 
					sHost, sd.getFrom(), sd.getRcptto(), sd.getSubject(), ImUtils.byteFormat(sd.getMailsize(), 2));
		}
		
		return ret;
	}

	/**
	 * @Method Name : getIPByName
	 * @Method Comment : mx레코드의 호스트명을 이용하여 ip를 뽑는다.
	 *
	 * @param host
	 * @return
	 */
	public String getIPByName(String host) throws UnknownHostException {
		String ip = host;

		//InetAddress ia = ImSensProxyServer.ids.getByName(host);
		InetAddress ia = InetAddress.getByName(host);
		ip = ia.getHostAddress();
		// ip가 15자리를 넘으면 그냥 host명을 사용한다.
		if(ip.length() > 16) ip = host;

		return ip;
	}
	
	private String getLocalIp() throws Exception {
		// 자기자신의 ip
		InetAddress ia = InetAddress.getLocalHost();
		String localIp = ia.getHostAddress();

		return localIp;
	}
	
	
	public int doSmtpSend(String sHost, int hostPort , ImSmtpSendData sd, String[] errMsg){
		// mx 레코드의 호스트명을 이용하여 ip를 뽑는다.

		String hostIP = null;
		try {
			hostIP = getIPByName(sHost);
		} catch (UnknownHostException e) {
			String errorId = ErrorTraceLogger.log(e);
			errMsg[0] = "Unknown host(ip find error) - host: "+ sHost;
			smailLogger.error("{} - getIPByName: Unknown host(ip find error) : {}", errorId, sHost);
			return 901;
		}

		return doSmtpSend(sHost, hostIP, hostPort, sd, errMsg);
	}

	public int doSmtpSend(String sHost, String hostIP, int hostPort, ImSmtpSendData sd, String[] errMsg){
		ImSmtp smtp = null;
		boolean bRemote = true;
		String sSlogFile = sd.getQueueDir() + File.separator + "slog" + File.separator + sd.getQueueFile();
		try{
			smtp = new ImSmtp();
			smtp.setLogger("SMAIL");
			// start tls 를 사용할 수 있으면 사용해라
			// retry가 2번째일때는 TLS 사용안함, 맨처음과 그 이후에는 가능하면 해라
			if(sd.getTryNumbers() != 2){
				smtp.setUseTLS(true);
			}
			smtp.setConnectTimeout(ImSmtpConfig.getInstance().getConnTime());
			smtp.setSocketTimeout(ImSmtpConfig.getInstance().getRWTime());
			
			/*bRemote = ImSensProxyServer.addRemoteServer(sHost);
			if(!bRemote){
				errMsg[0] = smtp.getResponse();
				smailLogger.info("smtp send error-connect: " + sHost + ": Remote Server Busy");
				return 902;
			}*/
			
			// connect
			if(!smtp.connect(hostIP, hostPort)){
				ImSmtpSLog.doSmtpErrorLog(sSlogFile, smtp, sHost, sd);

				smailLogger.info("smtp send error-connect: " + sHost + "(" + hostIP + ")" + ":" + smtp.getErrorCode() + ":" + smtp.getResponse());
				errMsg[0] = smtp.getResponse().trim();
				if(StringUtils.isEmpty(errMsg[0])) errMsg[0] = ("smtp send error-connect: " + sHost);
				return smtp.getErrorCode();
				//return false;
			}
			
			// helo
			if(!smtp.helo(ImSmtpConfig.getInstance().getHeloHost())){
				ImSmtpSLog.doSmtpErrorLog(sSlogFile, smtp, sHost, sd);
				//System.out.println(sSlogFile + "- helo");
				//return false;
				errMsg[0] = smtp.getResponse();
				smailLogger.info("smtp send error-helo: " + sHost + ":" + smtp.getErrorCode() + ":" + smtp.getResponse());
				return smtp.getErrorCode();
			}
			//smailLogger.info( "smtp send TLS : " + smtp.getIsTlsHandshakeOK() + " - " + sHost );
			
			String mailFrom = sd.getFrom();			
			String defaultDomain = ImSmtpConfig.getInstance().getDefaultDomain();
			// 발송주소 proxy 사용여부
			if(ImSmtpConfig.getInstance().isSendDomainProxy() 
					&& StringUtils.isNotEmpty(defaultDomain)) {
				
				String fromDomain = StringUtils.substringAfter(mailFrom, "@").toLowerCase();
				// 도메인이 같으면 그냥 발송
				if(!fromDomain.equals(defaultDomain)) {
					// 이메일아이디+rel=이메일도메인@디폴트도메인
					mailFrom = StringUtils.replace(mailFrom, "@", "+prx=") + "@" + ImSmtpConfig.getInstance().getDefaultDomain();
				}
			} 
			
			// mail
			if(!smtp.mail(mailFrom, sd.getContentsFile())){
				ImSmtpSLog.doSmtpErrorLog(sSlogFile, smtp, sHost, sd);
				//System.out.println(sSlogFile + "- mail");
				//return false;
				errMsg[0] = smtp.getResponse();
				smailLogger.info("smtp send error-mail: " + sHost + ":" + smtp.getErrorCode() + ":" + smtp.getResponse());
				return smtp.getErrorCode();
			}
			// rcpt
			if(!smtp.rcpt(sd.getRcptto())){
				ImSmtpSLog.doSmtpErrorLog(sSlogFile, smtp, sHost, sd);
				//System.out.println(sSlogFile + "- rcpt");
				//return false;
				errMsg[0] = smtp.getResponse();
				smailLogger.info("smtp send error-rcpt: " + sHost + ":" + smtp.getErrorCode() + ":" + smtp.getResponse());
				return smtp.getErrorCode();
			}
			smailLogger.info("[{}] doSmtpSend: sd.getFrom: {}, mailFrom: {}", sd.getTraceID(), sd.getFrom(), mailFrom);
			// dkim 
			//String from_domain = sd.getFrom().substring(sd.getFrom().indexOf("@") + 1);
			String from_domain = mailFrom.substring(mailFrom.indexOf("@") + 1);
			ImDkimSigner dkimSigner = ImDkimSigner.getInstance();

			smailLogger.info( "[{}] dkim check: {}:{}", sd.getTraceID(), from_domain, dkimSigner.has(from_domain));

			if(dkimSigner.has(from_domain)){
				dkimSigner.doDKIMSign(sd,from_domain);
			}
			
			// data
			if(!smtp.dataFile(sd.getContentsFile())){
				ImSmtpSLog.doSmtpErrorLog(sSlogFile, smtp, sHost, sd);
				//System.out.println(sSlogFile + "- data");
				//return false;
				errMsg[0] = smtp.getResponse();
				smailLogger.info("smtp send error-data: " + sHost + ":" + smtp.getErrorCode() + ":" + smtp.getResponse());
				return smtp.getErrorCode();
			}
			
			errMsg[0] = smtp.getResponse();

			smailLogger.info("smtp send ok : {}, use TLS : {}", sHost, smtp.getIsTlsHandshakeOK() );
			smtp.quit();
		} catch( Exception e ){ /* ignore */ 
    		String errorId = ErrorTraceLogger.log(e);
			smailLogger.error("{} - [SendMail] " , errorId );
			return 902;
    	}finally{
			/*if(bRemote)
				ImSensProxyServer.delRemoteServer(sHost);*/

			if(smtp != null){
    			try{
    				smtp.close();
    			} catch(Exception ex){
    				//
    			}
    			smtp = null;
    		}
    	}
		
		//return true;
    	return 1;
	}
	
	/**
	 * 외부로 메일을 발송한다.
	 * @Method Name : doRemoteSend
	 * @Method Comment : 
	 *
	 * @param mx
	 * @param sd
	 * @return
	 */
	public int doRemoteSend(Iterator<String> mx, ImSmtpSendData sd){
		TransmitLogger transmitLogger = new TransmitLogger();
        
		int nSuccess = 0;
		int nRet = 0;
		try{
			String sHost = "";
			String hostIP = "";
			String localIp = "127.0.0.1";
			
			//if(mx == null){
			if(!mx.hasNext()){
				// mx
				String[] sEmail = sd.getRcptto().split("@");
				if(sEmail.length > 1){
					sHost = sEmail[1];
		    	}
		    	
		    	if(sHost.length() <= 0){
					nRet = 911;
					transmitLogger.setDescription("delivery.remote.error");
					transmitLogger.setErrcode(nRet);
					sd.setSendResultCode(nRet);
					transmitLogger.setErrmsg("Invalid email format: "+ sd.getRcptto());
					sd.setSendResultErrMsg("Invalid email format: "+ sd.getRcptto());
					transmitLogger.setResultState(TransmitLogger.STATE_FAIL);
					ImSmtpUtil.doTransmitLog(transmitLogger, sd);

					smailLogger.info("[{}] Remote send fatal error : Invalid recipient's email format: {}/{}, SUBJ:[{}]", sd.getTraceID(), sd.getFrom(), sd.getRcptto(), sd.getSubject() );
					//smailLogger.info("[{}] Remote send fatal error : Localhost Loop : " + sHost + "("+ hostIP + ")/" + sd.getFrom() +"/"+sd.getRcptto() );
					return -1;
				}

				EmailValidator emailValidator = EmailValidator.getInstance();
		    	if(!emailValidator.isValid(sd.getRcptto())){
					nRet = 911;
					transmitLogger.setDescription("delivery.remote.error");
					transmitLogger.setErrcode(nRet);
					sd.setSendResultCode(nRet);
					transmitLogger.setErrmsg("Invalid email format: "+ sd.getRcptto());
					sd.setSendResultErrMsg("Invalid email format: "+ sd.getRcptto());
					transmitLogger.setResultState(TransmitLogger.STATE_FAIL);
					ImSmtpUtil.doTransmitLog(transmitLogger, sd);

					smailLogger.info("[{}] Remote send fatal error : Invalid recipient's email format: {}/{}, SUBJ:[{}]", sd.getTraceID(), sd.getFrom(), sd.getRcptto(), sd.getSubject() );
					//smailLogger.info("[{}] Remote send fatal error : Localhost Loop : " + sHost + "("+ hostIP + ")/" + sd.getFrom() +"/"+sd.getRcptto() );
					return -1;
				}
		    	
		    	// mx 레코드의 호스트명을 이용하여 ip를 뽑는다.
	    		try {
					hostIP = getIPByName(sHost);
				} catch (UnknownHostException e) {
					nRet = 901;
					String errorId = ErrorTraceLogger.log(e);
					smailLogger.error("{} - getIPByName: Unknown host(ip find error) : {}", errorId, sHost);
					transmitLogger.setDescription("delivery.remote.error");
					transmitLogger.setErrcode(nRet);
					sd.setSendResultCode(nRet);
					transmitLogger.setErrmsg("Unknown host(ip find error) - host: "+ sHost);
					sd.setSendResultErrMsg("Unknown host(ip find error) - host: "+ sHost);
					transmitLogger.setResultState(TransmitLogger.STATE_FAIL);
					ImSmtpUtil.doTransmitLog(transmitLogger, sd);

					smailLogger.info("[{}] Remote send fatal error : Unknown host(ip find error) - host: {}/{}/{}, SUBJ:[{}]", sd.getTraceID(), sHost, sd.getFrom(), sd.getRcptto(), sd.getSubject() );
					//smailLogger.info("[{}] Remote send fatal error : Localhost Loop : " + sHost + "("+ hostIP + ")/" + sd.getFrom() +"/"+sd.getRcptto() );
					return -1;
				}
	    		
	    		// 자기자신의 ip
	    		/*try {
					localIp = getLocalIp();
					smailLogger.info("localIp : {}", localIp);
				}catch(Exception e){}
	    		
				// 내부서버에에서 무한루프에 빠짐
				// (dadum.net이라는 도메인의 mx가 localhost)
				if("127.0.0.1".equals(hostIP) || "::1".equals(hostIP) || localIp.equals(hostIP)){
					smailLogger.info("Remote send fatal error : Localhost Loop : " + sHost + "("+ hostIP + ")/" + sd.getFrom() +"/"+sd.getRcptto() );
					return -1;
				}*/
				// 내부서버에에서 무한루프에 빠짐
				if(ImSmtpConfig.getInstance().getLocalIpList().contains(hostIP)) {
	    			String sSlogFile = sd.getQueueDir() + File.separator + "slog" + File.separator + sd.getQueueFile();
					ImSmtpSLog.doSmailErrorLog(sSlogFile, sd, "Recipient's email domain is not correct: "+hostIP);

					transmitLogger.setDescription("delivery.remote.error");
					if(nRet == 0) {
						transmitLogger.setErrcode(901);
						sd.setSendResultCode(901);
					} else {
						transmitLogger.setErrcode(nRet);
						sd.setSendResultCode(nRet);
					}
					transmitLogger.setResultState(TransmitLogger.STATE_FAIL);
					transmitLogger.setErrmsg("Invalid MX Record: "+ hostIP + " : localhost loop");
					sd.setSendResultErrMsg("Invalid MX Record: "+ hostIP + " : localhost loop");
					ImSmtpUtil.doTransmitLog(transmitLogger, sd);

					smailLogger.info("[{}] Remote send fatal error : Invalid MX Record - Localhost Loop : {}({})/{}/{}, SUBJ:[{}]", sd.getTraceID(), sHost, hostIP, sd.getFrom(), sd.getRcptto(), sd.getSubject() );
					//smailLogger.info("[{}] Remote send fatal error : Localhost Loop : " + sHost + "("+ hostIP + ")/" + sd.getFrom() +"/"+sd.getRcptto() );
					return -1;
				}

	    		String[] errMsg = new String[]{""};
		    	nRet = doSmtpSend( sHost, hostIP, ImSmtpConfig.getInstance().getSmtpPortOut(), sd, errMsg);
//				smailLogger.info("nRet: " + nRet + " / errMsg: " + errMsg[0]);
		    	if (nRet >= 500 && nRet < 600){
					// fatal error
					transmitLogger.setDescription("delivery.remote.error");
					transmitLogger.setErrcode(nRet);
					sd.setSendResultCode(nRet);
					transmitLogger.setErrmsg(errMsg[0]);
					sd.setSendResultErrMsg(errMsg[0]);
					transmitLogger.setResultState(TransmitLogger.STATE_FAIL);
					ImSmtpUtil.doTransmitLog(transmitLogger, sd);

					smailLogger.info("[{}] Remote send fatal error : {}({})/{}/{}({}), SUBJ:[{}], SIZE:{}", 
							sd.getTraceID(), sHost, hostIP, sd.getFrom(), sd.getRcptto(), nRet, sd.getSubject(), ImUtils.byteFormat(sd.getMailsize(), 2) );
					return -1;
				} 
		    	
				if(nRet != 1){
					transmitLogger.setDescription("delivery.remote.error");
					if(nRet == 0) {
						transmitLogger.setErrcode(901);
						sd.setSendResultCode(901);
					} else {
						transmitLogger.setErrcode(nRet);
						sd.setSendResultCode(nRet);
					}
					transmitLogger.setErrmsg(errMsg[0]);
					sd.setSendResultErrMsg(errMsg[0]);

					smailLogger.info("[{}] Remote send error : {}({})/{}/{}({}), SUBJ:[{}], SIZE:{}", 
							sd.getTraceID(), sHost, hostIP, sd.getFrom(), sd.getRcptto(), nRet, sd.getSubject(), ImUtils.byteFormat(sd.getMailsize(), 2) );
					/*
					 * Author : jungyc
					 * Date : 2007.03.15
					 * Comment : 재시도가 불필요한 메일은 재시도 큐에 넣지 않는다.
					 */
					if((sd.getTryNumbers()+1) > ImSmtpConfig.getInstance().getMaxRetry()){
						// 다음 재시도 회수가 설정된 회수보다 크면 재시도하지 않는다.
						transmitLogger.setResultState(TransmitLogger.STATE_FAIL);
						ImSmtpUtil.doTransmitLog(transmitLogger, sd);
						return -1;
					}
					transmitLogger.setResultState(TransmitLogger.STATE_ING);
					ImSmtpUtil.doTransmitLog(transmitLogger, sd);
					/* 2007.03.15 jungyc */
					return 0;
				}
				
			} else {
				String[] errMsg = new String[] {""};
				while(mx.hasNext()){
		        	sHost = StringUtils.trim(mx.next());
		        	if(sHost.endsWith(".")){
		        		sHost = sHost.substring(0, (sHost.length() - 1));
		        	}
		        	// mx 레코드의 호스트명을 이용하여 ip를 뽑는다.
		    		try {
						hostIP = getIPByName(sHost);
					} catch (UnknownHostException e) {
		    			nRet = 901;
						nSuccess = -1;
						String errorId = ErrorTraceLogger.log(e);
						smailLogger.error("{} - getIPByName: Unknown host(ip find error) : {}", errorId, sHost);
						transmitLogger.setDescription("delivery.remote.error");
						transmitLogger.setErrcode(nRet);
						sd.setSendResultCode(nRet);
						transmitLogger.setErrmsg("Unknown host(ip find error) - host: "+ sHost);
						sd.setSendResultErrMsg("Unknown host(ip find error) - host: "+ sHost);
						transmitLogger.setResultState(TransmitLogger.STATE_FAIL);
						ImSmtpUtil.doTransmitLog(transmitLogger, sd);

						smailLogger.info("[{}] Remote send fatal error : Unknown host(ip find error) - host: {}/{}/{}, SUBJ:[{}]", sd.getTraceID(), sHost, sd.getFrom(), sd.getRcptto(), sd.getSubject() );
						break;
					}
		    		
		    		// 자기자신의 ip
		    		/*try {
						localIp = getLocalIp();
						smailLogger.info("localIp2 : {}", localIp);
					}catch(Exception e){
						smailLogger.error("localIp2 : {}", e);
					}
		    		
		    		// 내부서버에에서 무한루프에 빠짐
					// (dadum.net이라는 도메인의 mx가 localhost)
					if("127.0.0.1".equals(hostIP) || "::1".equals(hostIP) || localIp.equals(hostIP)){
						smailLogger.info("Remote send fatal error : Localhost Loop : " + sHost + "("+ hostIP + ")/" + sd.getFrom() +"/"+sd.getRcptto() );
						return -1;
					}*/
					
					if(ImSmtpConfig.getInstance().getLocalIpList().contains(hostIP)) {
						String sSlogFile = sd.getQueueDir() + File.separator + "slog" + File.separator + sd.getQueueFile();
						ImSmtpSLog.doSmailErrorLog(sSlogFile, sd, "Recipient's email domain is not correct: "+hostIP);

						transmitLogger.setDescription("delivery.remote.error");
						if(nRet == 0) {
							transmitLogger.setErrcode(901);
							sd.setSendResultCode(901);
						} else {
							transmitLogger.setErrcode(nRet);
							sd.setSendResultCode(nRet);
						}
						transmitLogger.setErrmsg("Invalid MX Record: "+ hostIP + " : localhost loop");
						sd.setSendResultErrMsg("Invalid MX Record: "+ hostIP + " : localhost loop");
						transmitLogger.setResultState(TransmitLogger.STATE_FAIL);
						ImSmtpUtil.doTransmitLog(transmitLogger, sd);
						
						//smailLogger.info("Remote send fatal error : Localhost Loop : " + sHost + "("+ hostIP + ")/" + sd.getFrom() +"/"+sd.getRcptto() );
						smailLogger.info("[{}] Remote send fatal error : Invalid MX Record - Localhost Loop : {}({})/{}/{}, SUBJ:[{}]", sd.getTraceID(), sHost, hostIP, sd.getFrom(), sd.getRcptto(), sd.getSubject() );
						nSuccess = -1;
						break;
					}
//smailLogger.info("errMsg: " + errMsg[0]);		
		        	nRet = doSmtpSend( sHost, hostIP, ImSmtpConfig.getInstance().getSmtpPortOut(), sd, errMsg);
//smailLogger.info("nRet: " + nRet + " / errMsg: " + errMsg[0]);		        	
					if(nRet == 1){
						//System.out.println(sSlogFile);
						nSuccess = 1;
    					break;
    				} else if (nRet >= 500 && nRet < 600){
    					// fatal error
    					nSuccess = -1;
    					break;
    				}
				}
    			
    			if(nSuccess == 0){

					if((sd.getTryNumbers()+1) > ImSmtpConfig.getInstance().getMaxRetry()){
						transmitLogger.setDescription("delivery.remote.error");
						transmitLogger.setResultState(TransmitLogger.STATE_FAIL);
					} else {
						transmitLogger.setDescription("delivery.remote.error.resend");
						transmitLogger.setResultState(TransmitLogger.STATE_ING);
					}
					if(nRet == 0) {
						transmitLogger.setErrcode(901);
						sd.setSendResultCode(901);
					} else {
						transmitLogger.setErrcode(nRet);
						sd.setSendResultCode(nRet);
					}
					transmitLogger.setErrmsg(errMsg[0]);
					sd.setSendResultErrMsg(errMsg[0]);
					ImSmtpUtil.doTransmitLog(transmitLogger, sd);
    				
					smailLogger.info("[{}] Remote resend : {}({})/{}/{}({}), SUBJ:[{}], SIZE:{}", 
							sd.getTraceID(), sHost, hostIP, sd.getFrom(), sd.getRcptto(), nRet, sd.getSubject(), ImUtils.byteFormat(sd.getMailsize(), 2) );
					return 0;
				} else if(nSuccess == -1){
					// fatal error
					transmitLogger.setDescription("delivery.remote.error");
					if(nRet == 0) {
						transmitLogger.setErrcode(901);
						sd.setSendResultCode(901);
					} else {
						transmitLogger.setErrcode(nRet);
						sd.setSendResultCode(nRet);
					}
					if(StringUtils.isEmpty(transmitLogger.getErrmsg())) {
						transmitLogger.setErrmsg(errMsg[0]);
						sd.setSendResultErrMsg(errMsg[0]);
					}
					transmitLogger.setResultState(TransmitLogger.STATE_FAIL);
					ImSmtpUtil.doTransmitLog(transmitLogger, sd);
					
					smailLogger.info("[{}] Remote send fatal error : {}({})/{}/{}({}), SUBJ:[{}], SIZE:{}", 
							sd.getTraceID(), sHost, hostIP, sd.getFrom(), sd.getRcptto(), nRet, sd.getSubject(), ImUtils.byteFormat(sd.getMailsize(), 2) );
					return -1;
				}
			}

			transmitLogger.setDescription("delivery.remote.send.success");
			transmitLogger.setResultState(TransmitLogger.STATE_SUCCESS);
			ImSmtpUtil.doTransmitLog(transmitLogger, sd);

			smailLogger.info("[{}] Remote send MX: {}({}) : {} -> {}, SUBJ:[{}], SIZE:{} Send OK", 
					sd.getTraceID(), sHost, hostIP, sd.getFrom(), sd.getRcptto(), sd.getSubject(), ImUtils.byteFormat(sd.getMailsize(), 2));
		} catch (Exception e){
    		String errorId = ErrorTraceLogger.log(e);
			smailLogger.error("{} - [SendMail] " , errorId );
		} 
		    		
		return 1;
	}

	/**
	 * 스팸메일 여부 확인
	 * @param sd
	 * @return @return  -1: 스팸메일, -2: 스팸아이피, 0:정상
	 */
	public int isSpam(ImSmtpSendData sd){
		return ImSmtpUtil.isSpam(sd);
	}

	/**
	 * 수신확인url 이미지 태그
	 * @param sd
	 * @return
	 * @throws Exception
	 */
	private String makeReceiptTag(ImSmtpSendData sd) throws Exception {
		String serverId = ImSmtpConfig.getInstance().getServerID();

		String openUrl = ImSmtpConfig.getInstance().getWebUrl() + ImSmtpConfig.getInstance().getReceiptNotifyUrl();

		StringBuffer receiptTagBuff = new StringBuffer();
		receiptTagBuff.append("\r\n<img id='mailexp' width=0 height=0 border=0 loading='auto' src='");
		receiptTagBuff.append(openUrl);
		receiptTagBuff.append("?");

		String encryptKey = ImSmtpConfig.getInstance().getEncrypt_key();

		if (StringUtils.isNotEmpty(encryptKey)) {
			// 암복호화 키가 존재할 경우 수신확인 파라메터는 암호화 되어서 발송된다.
			String receipt_param = "tid=" + sd.getTraceID() + "&to=" + sd.getRcptto() + "&sid="+serverId;
			String enc_param = ImSecurityLib.encryptAriaHexString(encryptKey, receipt_param, true);

			receiptTagBuff.append("p=").append(enc_param).append(".gif");
		} else {
			receiptTagBuff.append("tid=").append(sd.getTraceID());
			receiptTagBuff.append("&").append("to=").append(sd.getRcptto());
			receiptTagBuff.append("&").append("sid=").append(serverId);
		}

		receiptTagBuff.append("' />");

		return receiptTagBuff.toString();
	}


	/**
	 * 수신확인 url 처리
	 * @param sd
	 * @param msgPath
	 */
	private void doProcessReceiptNotify(ImSmtpSendData sd, String msgPath){
		try {
			if(!ImSmtpConfig.getInstance().isUseReceiptNotify() ||
					StringUtils.isEmpty(ImSmtpConfig.getInstance().getReceiptNotifyUrl())){
				return;
			}

			Properties props = System.getProperties();
			Session session = Session.getInstance(props);

			ImMessage im = new ImMessage(session);
			im.setDefaultCharset("euc-kr");
			im.parseMimeFile(msgPath);
			im.setLogger("SMAIL");

			Date senddate = im.getSentDateEx();
			senddate = senddate != null ? senddate : im.getReceivedDateEx();
			if(senddate == null){
				senddate = new Date();
			}
			String messageId = im.getMessageID();

			// eml 을 다시 만든다.
			String outPath = msgPath + ".tmp";
			im.setContentEncoding(ImMessage.ENC_QP);

			// ----- setText, setHtml, setMessageID 는 해줘야 한다 ----
			if(StringUtils.isNotEmpty(im.getText())) {
				im.setText(im.getText());
			}
			if(StringUtils.isNotEmpty(im.getHtml())) {
				String receitNotiTag = makeReceiptTag(sd);
				im.setHtml(im.getHtml()+"\r\n" + receitNotiTag);
			}

			// message-id 가 없으면 만든다.
			String messageID = im.getMessageID();
			if(StringUtils.isEmpty(messageID)){
				messageID = UUIDService.getMessageID(sd.getDomain());
			}
			im.setMessageID(messageID);
			im.makeMimeFile(outPath);

			// 테스트이면 둘다 보관(헤더의 from 주소가 내부 사용자여야 여기까지 도달함)
			// 실제 스풀에서 삭제는 crontab에 등록된 delete_dev_spool.sh 에서 처리
			smailLogger.info("ImSmtpConfig.getInstance().isTest(): {}", ImSmtpConfig.getInstance().isTest());
			if(ImSmtpConfig.getInstance().isTest()){
				// 원본
				ImFileUtil.copyFile(msgPath, msgPath+".origin");
				smailLogger.info("[{}] doProcessReceiptNotify Original eml: {}", sd.getTraceID(), msgPath+".origin");
			}

			if(ImFileUtil.copyFile(outPath, msgPath)){
				// 테스트이면 삭제 안함
				if(!ImSmtpConfig.getInstance().isTest()) {
					ImFileUtil.deleteFile(outPath);
				}
				//smailLogger.info("[{}] doProcessReceiptNotify New eml: {}", sd.getTraceID(), outPath);
			}
			smailLogger.info("[{}] doProcessReceiptNotify OK: {}", sd.getTraceID(), msgPath);
		} catch(Exception e){
			String errorId = ErrorTraceLogger.log(e);
			smailLogger.error("[{}]- doProcessReceiptNotify error : {}", errorId, msgPath);
		}
	}

	/** Must be public, but do not override this method */
	public void run()
	{	
		try{
			alive = true;
			while(!isInterrupted()){
				int nErrCode = 0;
				String sErrMsg = "";
				String sRcptDomain = "";
				String sRcptUserID = "";
				String sQueuePath = "";
				
//				if(bulk){
//					sQueuePath = ImSensProxyServer.extractInBulkMessageQueue();
//				}else{
//					sQueuePath = ImSensProxyServer.extractMessageQueue(queueIndex);
//				}
				MessageQueueService messageQueueService = MessageQueueService.getInstance();
				if(bulk){
					sQueuePath = messageQueueService.extractInBulkMessageQueue();
				}else{
					sQueuePath = messageQueueService.extractMessageQueue(queueIndex);
				}
				
				if(sQueuePath == null || sQueuePath.equals("")) continue;
				
				ImSmtpSendData sd = ImSmtpUtil.getSendData(sQueuePath);
				if(sd == null) {
					smailLogger.error("Message Data NULL : " + sQueuePath);
					ImSmtpUtil.cleanQueue(sQueuePath);
					continue;
				}
				smailLogger.debug("[{}] load queue file - path:{}",sd.getTraceID(), sd.getQueueDir() + File.separator + "mess" + File.separator + sd.getQueueFile() );
				
				try{
					String bodyPath = sQueuePath + ".body";
					File bodyFile = new  File(bodyPath);
					if(!bodyFile.exists()){
						smailLogger.error("bodyPath does not exist - path: {}, from:{}, to:{}", bodyPath, sd.getFrom(), sd.getRcptto());
						ImSmtpUtil.cleanQueue(sQueuePath);
						continue;
					}
					//smailLogger.debug("[{}] load eml file before: {}", sd.getTraceID(), ImUtils.byteFormat(sd.getMailsize(),2));
					sd.setMailsize(bodyFile.length());
					smailLogger.debug("[{}] load queue eml file - path:{}, size:{}",sd.getTraceID(), bodyPath, ImUtils.byteFormat(sd.getMailsize(),2) );


					TransmitLogger transmitLogger = new TransmitLogger();

					// 2008-07-29 스팸체크
					int nIsSpam = isSpam(sd);

					// DenyIP 에 있는지 체크
					boolean isDenyIp = ImSmtpUtil.isDenyIp(sd.getPeerIP());
					if(isDenyIp){
						transmitLogger.setSize(0L);
						transmitLogger.setDescription("error.spam_email");
						transmitLogger.setErrmsg("Sender ip in reject list");
						transmitLogger.setResultState(TransmitLogger.STATE_FAIL);
						ImSmtpUtil.doTransmitLog(transmitLogger, sd);

						smailLogger.info("[{}] From or To is blocked email : skipped - from:{}, to:{}",sd.getTraceID(), sd.getFrom(), sd.getRcptto());

						ImSmtpUtil.cleanQueue(sd.getQueueDir(), sd.getQueueFile());
						continue;
					}

					// 수신거부자 목록에 있는지 체크(일단 제외 - 대량발송만 적용)

					// 발신차단 도메인에 있는지 체크(일단 제외 - 대량발송만 적용)

					if (nIsSpam == -1) { // 발신자 또는 수신자 메일주소가 스팸 이메일주소에 포함됨
						transmitLogger.setSize(0L);
						transmitLogger.setDescription("error.spam_email");
						transmitLogger.setErrmsg("From or To is blocked email");
						sd.setSendResultErrMsg("From or To is blocked email");
						transmitLogger.setResultState(TransmitLogger.STATE_FAIL);
						ImSmtpUtil.doTransmitLog(transmitLogger, sd);

						smailLogger.info("[{}] From or To is blocked email : skipped - from:{}, to:{}",sd.getTraceID(), sd.getFrom(), sd.getRcptto());

						ImSmtpUtil.cleanQueue(sd.getQueueDir(), sd.getQueueFile());
						continue;

					} else if (nIsSpam == -2) { // 발신자 IP가 스팸 HOST 에 등록되어있음

						transmitLogger.setSize(0L);
						transmitLogger.setDescription("error.spam_host");
						transmitLogger.setErrmsg("From is blocked ip");
						sd.setSendResultErrMsg("From is blocked ip");
						transmitLogger.setResultState(TransmitLogger.STATE_FAIL);
						ImSmtpUtil.doTransmitLog(transmitLogger, sd);

						smailLogger.info("[{}] From is blocked ip : skipped - from:{}, to:{}",sd.getTraceID(), sd.getFrom(),sd.getRcptto());
						ImSmtpUtil.cleanQueue(sd.getQueueDir(), sd.getQueueFile());
						continue;
					}

					// 핑퐁치는 경우 방지
					if(ImSmtpUtil.doCountLoop(bodyPath,  "Received:") >= ImSmtpConfig.getInstance().getMaxMsgLoops()){
						transmitLogger.setSize(sd.getMailsize());
						transmitLogger.setDescription("error.too_many_loop");
						transmitLogger.setErrmsg("too many loop");
						sd.setSendResultErrMsg("too many loop");
						transmitLogger.setResultState(TransmitLogger.STATE_FAIL);
						ImSmtpUtil.doTransmitLog(transmitLogger, sd);

						smailLogger.info("[{}] Too many loop : skipped - from:{}, to:{}",sd.getTraceID(), sd.getFrom(), sd.getRcptto());

						ImSmtpUtil.cleanQueue(sd.getQueueDir(), sd.getQueueFile());
						continue;
					}

					/**
					 * 수신확인 데이터를 넣어야 함
					 */
					smailLogger.info("[{}] isUseReceiptNotify : {}", sd.getTraceID(), ImSmtpConfig.getInstance().isUseReceiptNotify());
					smailLogger.info("[{}] sd.isRetryNow(): {}", sd.getTraceID(), sd.isRetryNow());
					if(ImSmtpConfig.getInstance().isUseReceiptNotify()){
						// 재발송 메일이 아니어야 함
						if(!sd.isRetryNow()){
							doProcessReceiptNotify(sd, bodyPath);
						}
					}
					
//smailLogger.debug("OrgTraceID: "+sd.getOrgTraceID());	
					// slog 메일 발송 로그 경로를 생성하고 발송 로그를 업데이트한다.
					String sSlogFile = sd.getQueueDir() + File.separator + "slog" + File.separator + sd.getQueueFile();
        			ImSmtpSLog.doPeekLog(sSlogFile, sd);
        			
        			// 아이디와 도메인을 분리한다.
					String[] sEmail = sd.getRcptto().split("@");
			    	if(sEmail.length > 1){
			    		sRcptUserID = sEmail[0];
			    		sRcptDomain = sEmail[1].toLowerCase();
			    	}
			    	
			    	if(sRcptDomain.length() <= 0){
			    		// 수신자의 도메인 정보가 없다면 수신자의 도메인이 없다고 에러 발생
						nErrCode = 910;
						sErrMsg = "[" + sd.getTraceID()+"] Domain Error ( " + sd.getFrom() + " / " + sd.getRcptto()+" )";

						ImSmtpSLog.doSmtpLocalErrorLog(sSlogFile, sRcptDomain, nErrCode, sErrMsg, sd.getFrom(), sd.getRcptto());
					
						// 메일링리스트일경우 발송하지 않음,
						if(!sd.isDSN()){
							if(ImSmtpConfig.getInstance().isUseDsn()) {
								ImSmtpUtil.sendNotifyErrorMessage(sd);
							}else {
								smailLogger.info("[{}] DSN Not Send: {} -> {}, SUBJ:{}", sd.getTraceID(), sd.getFrom(), sd.getRcptto(), sd.getSubject());
							}
                        }
						
						ImSmtpUtil.cleanQueue(sd.getQueueDir(), sd.getQueueFile());

                        transmitLogger.setDescription("error.unknown.domain");
						transmitLogger.setErrcode(nErrCode);
                        transmitLogger.setErrmsg("No Rcpt Domain");
						sd.setSendResultErrMsg("No Rcpt Domain");
						transmitLogger.setResultState(TransmitLogger.STATE_FAIL);
						ImSmtpUtil.doTransmitLog(transmitLogger, sd);
                        
						smailLogger.info( "[" + sd.getTraceID()+"] No Rcpt Domain ( " + sd.getFrom() + " / " + sd.getRcptto()+" )");
						continue;
					}
			    	
			    	if(sRcptUserID.length() <= 0){
						nErrCode = 908;
						sErrMsg = "Unknown User";
						//throw new ImMessQueueException();
						ImSmtpSLog.doSmtpLocalErrorLog(sSlogFile, sRcptDomain, nErrCode, sErrMsg, sd.getFrom(), sd.getRcptto());

						// 메일링리스트일경우 발송하지 않음,
						if(!sd.isDSN()){
							if(ImSmtpConfig.getInstance().isUseDsn()) {
								ImSmtpUtil.sendNotifyErrorMessage(sd);
							}else {
								smailLogger.info("[{}] DSN Not Send: {} -> {}, SUBJ:{}", sd.getTraceID(), sd.getFrom(), sd.getRcptto(), sd.getSubject());
							}
						}

						ImSmtpUtil.cleanQueue(sd.getQueueDir(), sd.getQueueFile());

                        transmitLogger.setDescription("error.unknown_user");
						transmitLogger.setErrcode(nErrCode);
						transmitLogger.setErrmsg(sErrMsg);
						sd.setSendResultErrMsg(sErrMsg);
						transmitLogger.setResultState(TransmitLogger.STATE_FAIL);
						ImSmtpUtil.doTransmitLog(transmitLogger, sd);
                        
						smailLogger.info( "[" + sd.getTraceID()+"] No Rcpt To( " + sd.getFrom() + " / " + sd.getRcptto()+" )");
						continue;
					}
					
			    	// 도메인 정보를 가져온다. 
	        		boolean bLocalDomain;
	        		/*// 메일 발송 프로세스전의 전역 필터들을 실행한다.
	            	for(ISmailProcessFilter filter : ImSensProxyServer.m_arrGlobalFilter){
	            		if(!filter.doProcess(sd,sQueuePath)){
	                		continue;
	            		}
	            	}*/
			    	
	            	// DefaultGateway가 설정되어 있는지 확인한다.
					if(!ImSmtpConfig.getInstance().isUseGateway()){
						doLocalRemoteSend(sRcptDomain, sd, sSlogFile, sQueuePath);
					} else {
						// gateway send_path가 있으면 send_path에 저장, gateway server ip가 있으면 smtp로 전송
						if(ImSmtpConfig.getInstance().getGatewayServerIp().size() > 0) {
							if(doGatewayRemoteSend(sRcptDomain, sd, sSlogFile, sQueuePath) == -1){
								// Gateway로 전송이 실패하면 그냥 발송한다.
								doLocalRemoteSend(sRcptDomain, sd, sSlogFile, sQueuePath);
							}
						} else if(StringUtils.isNotEmpty(ImSmtpConfig.getInstance().getGatewaySendPath())){
							if(!doGatewaySavePath(sd, sQueuePath)) {
								// 메일링리스트일경우 발송하지 않음,
								if(!sd.isDSN()){
									if(ImSmtpConfig.getInstance().isUseDsn()) {
										ImSmtpUtil.sendNotifyErrorMessage(sd);
									}else {
										smailLogger.info("[{}] DSN Not Send: {} -> {}, SUBJ:{}", sd.getTraceID(), sd.getFrom(), sd.getRcptto(), sd.getSubject());
									}
								}

								ImSmtpUtil.cleanQueue(sd.getQueueDir(), sd.getQueueFile());
								transmitLogger.setDescription("delivery.remote.gateway.savefile.error");
								transmitLogger.setArgument("Remote send SaveFile fatal error");
								transmitLogger.setResultState(TransmitLogger.STATE_FAIL);
								ImSmtpUtil.doTransmitLog(transmitLogger, sd);

								smailLogger.info("[{}] Remote send SaveFile fatal error : {} -> {}, SUBJ:[{}], SIZE:{}",
										sd.getTraceID(), sd.getFrom(), sd.getRcptto(), sd.getSubject(), ImUtils.byteFormat(sd.getMailsize(), 2));
							} else {
								transmitLogger.setDescription("delivery.remote.gateway.savefile.success");
								transmitLogger.setArgument(new File(sd.getContentsFile()).getName());
								transmitLogger.setResultState(TransmitLogger.STATE_SUCCESS);
								ImSmtpUtil.doTransmitLog(transmitLogger, sd);

								smailLogger.info("[{}] Remote send SaveFile OK : {} -> {}, SUBJ:[{}], {}, SIZE:{}",
										sd.getTraceID(), sd.getFrom(), sd.getRcptto(), sd.getSubject(), sd.getQueueFile(), ImUtils.byteFormat(sd.getMailsize(), 2));
								ImSmtpUtil.cleanQueue(sd.getQueueDir(), sd.getQueueFile());
							}
						} else {
							smailLogger.info("WARNING: isUseGateway setting is true, but setting not exist");
							doLocalRemoteSend(sRcptDomain, sd, sSlogFile, sQueuePath);
						}
					}

					//smailLogger.info( sd.getTraceID()+ " Local : " + sd.getFrom() + " -> " + sd.getRcptto() + " Send OK");
					//ImSmtpUtil.cleanQueue(sd.getQueueDir(), sd.getQueueFile());

				} catch (Exception e){
		    		String errorId = ErrorTraceLogger.log(e);
					smailLogger.error("[{}] - [SendMail] "+sd.getTraceID()+" send error : ( " + sd.getFrom() + " / " + sd.getRcptto() + " ) "  , errorId );

//					smtpDAO.updateMailQueue(sd.getTraceID(),sd.getQueueFile(), "30", "Send Error");
//					ImSmtpUtil.moveErrorQueue(sd.getQueueDir(), sd.getQueueFile());
					ImSmtpUtil.cleanQueue(sd.getQueueDir(), sd.getQueueFile());
				}
			}
		}catch(Exception e1){
			//System.out.println(e1);
    		String errorId = ErrorTraceLogger.log(e1);
    		smailLogger.error("{} - [SendMail] ", errorId );
		}

		alive = false;
		/**
		 * Author : jungyc
		 * Date : 2007.01.04
		 * Content : 스레드가 종료될 때 로그를 남김
		 */
		smailLogger.info("Send Thread End : {}",this.toString());
		/* 2007.01.04 jungyc */
	}

	

}

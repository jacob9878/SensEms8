package com.imoxion.sensems.server.util;

import com.imoxion.common.mail.ImMessage;
import com.imoxion.common.mail.ImMimeHeader;
import com.imoxion.common.util.ImFileUtil;
import com.imoxion.common.util.ImIpUtil;
import com.imoxion.common.util.ImStringUtil;
import com.imoxion.common.util.ImUtils;
import com.imoxion.sensems.server.beans.ErrorCode;
import com.imoxion.sensems.server.beans.ImQueueObj;
import com.imoxion.sensems.server.config.ImSmtpConfig;
import com.imoxion.sensems.server.daemon.job.service.TransmitDataLogMonitoringService;
import com.imoxion.sensems.server.domain.DenyIp;
import com.imoxion.sensems.server.domain.RelayIp;
import com.imoxion.sensems.server.domain.TransmitStatisticsData;
import com.imoxion.sensems.server.logger.ErrorTraceLogger;
import com.imoxion.sensems.server.logger.event.TransmitLogger;
import com.imoxion.sensems.server.repository.SmtpRepository;
import com.imoxion.sensems.server.service.MessageQueueService;
import com.imoxion.sensems.server.smtp.ImSmtpSession;
import com.maxmind.geoip2.record.Country;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.ContentType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.InternetHeaders;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImSmtpUtil {

	private static Logger smailLogger = LoggerFactory.getLogger("SMAIL");

	private static Logger smtpLogger = LoggerFactory.getLogger("SMTP");

	private static Pattern p =
			Pattern.compile("(.*?)\\s+for\\s+<(.*?)>\\s+from\\s+<(.*?)>",
					Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

	//
	public static boolean doFindHeaderTag(String p_sIn, String sTag) {
		boolean bRet = false;
		ArrayList<String> arraylist = ImFileUtil.readTextByLine(p_sIn);

		for (int i = 0; i < arraylist.size(); i++) {
			if (ImStringUtil.strINComp(arraylist.get(i).toString(), sTag)) {
				bRet = true;
				break;
			}
		}

		return bRet;
	}

	/**
	 * X-DELIVER-TO에 입력받은 주소가 포함됐는지 체크
	 * @param p_sPath
	 * @param sTo
	 * @return
	 */
	public static boolean doCheckDeliverHeader(String p_sPath, String sTo) {
		boolean bRet = false;

		BufferedReader br = null;
		String sTag = "X-DELIVER-TO:";

		try {
			String sLine = "";
			br = new BufferedReader(new FileReader(p_sPath));
			while ((sLine = br.readLine()) != null) {
				if(sLine.length() > 0){
					if (sLine.startsWith(sTag)) {
						String sDeliver = sLine.substring(13).trim();
						if (sDeliver.equalsIgnoreCase(sTo)) {
							return true;
						}
					}
				}
			}
		} catch (Exception ex) {
			String errorId = ErrorTraceLogger.log(ex);
			smtpLogger.error("{} - doCheckDeliverHeader error" , errorId );
		} finally {
			try { br.close();} catch (Exception e) {}
		}

		return bRet;
	}

	/**
	 * 부재중 메일인지 체크
	 * @param p_sPath
	 * @return
	 */
	public static boolean doCheckAbsentMail(String p_sPath) {
		boolean bRet = false;

		BufferedReader br = null;
		String sTag = "X-ABSENT:";

		try {
			String sLine = "";
			br = new BufferedReader(new FileReader(p_sPath));
			while ((sLine = br.readLine()) != null) {
				if(sLine.length() > 0){
					if (sLine.startsWith(sTag)) {
						if (sLine.startsWith(sTag)) {
							return true;
						}
					}
				}
			}
		} catch (Exception ex) {
			String errorId = ErrorTraceLogger.log(ex);
			smtpLogger.error("{} - doCheckAbsentMail" , errorId );
		} finally {
			try { if( br != null ){br.close();} } catch (Exception e) {}
		}

		return bRet;
	}

	/**
	 * Received 헤더의 갯수를 체크
	 * @param p_sPath
	 * @param sTag
	 * @return
	 */
	public static int doCountLoop(String p_sPath, String sTag) {
		int nCount = 0;

		BufferedReader br = null;
		if (sTag.equals("")) {
			sTag = "Received:";
		}
		try {
			br = new BufferedReader(new FileReader(p_sPath));
			String sLine = "";
			while ((sLine = br.readLine()) != null) {
				if(sLine.length() > 0){
					if (sLine.startsWith(sTag)) {
						nCount++;
					}
				}
			}
		} catch (Exception ex) {
			String errorId = ErrorTraceLogger.log(ex);
			smtpLogger.error("{} - doCountLoop" , errorId );
		} finally {
			try { br.close(); } catch (Exception e) {}
		}

		return nCount;
	}

	/**
	 * ip로부터 국가 정보 추출
	 * @param ip
	 * @return
	 */
	public static Map<String, String> getIpCountryMap(String ip){
		Map<String, String> countryMap = new HashMap<>();

		if( StringUtils.isNotEmpty(ip) ) {
			try {
				Country country = ImGeoIPLookup2.getInstance().getCountry(ip);
				if (country != null) {
					countryMap.put("country", country.getIsoCode());
					countryMap.put("country_name", country.getName());
				} else {
					if (ImIpUtil.isPublicIP(ip)) {
						countryMap.put("country", "UNKNOWN");
						countryMap.put("country_name", "UNKNOWN");
					} else {
						countryMap.put("country", "--");
						countryMap.put("country_name", "--");
					}
				}
			} catch (Exception e) {
				if (ImIpUtil.isPublicIP(ip)) {
					countryMap.put("country", "UNKNOWN");
					countryMap.put("country_name", "UNKNOWN");
				} else {
					countryMap.put("country", "--");
					countryMap.put("country_name", "--");
				}
			}
		}

		return countryMap;
	}

	/**
	 * Received 헤더의 갯수를 체크
	 * @param p_sPath
	 * @param sTag
	 * @return
	 */
	public static boolean doForwardLoop(String p_sPath, String sTag) {
		int nCount = 0;

		BufferedInputStream fis = null;
		if (sTag.equals("")) {
			sTag = "Received:";
		}
		try {
			fis = new BufferedInputStream( new FileInputStream(p_sPath) );
			InternetHeaders ihdrs = new InternetHeaders(fis);
			String[] arrInfo = ihdrs.getHeader("X-DELIVER-TO");

			if(arrInfo.length > 5){
				return true;
			}

			for(int i=0;i<arrInfo.length;i++){
				String email = arrInfo[i].trim();
				email = email.toLowerCase();

				if(email.equalsIgnoreCase(sTag)){
					return true;
				}
			}
		} catch (Exception ex) {
			String errorId = ErrorTraceLogger.log(ex);
			smtpLogger.error("{} - [ImSmtpUtil] " , errorId );
		} finally {
			try { fis.close(); } catch (Exception e) {}
		}

		return false;
	}

	/**
	 *
	 * @param sd
	 * @return  -1: 스팸메일, -2: 스팸아이피, 0:정상
	 */
	public static int isSpam(ImSmtpSendData sd) {
		String sDomain = "";
		String sWhereIp = "";
		int nCnt = 0;
		int nRet = 0;

		try{
			// 이메일 체크
			String[] arrAddr = ImStringUtil.getTokenizedString(sd.getFrom(), "@");
			if(arrAddr.length > 1){
				sDomain = arrAddr[1];
			}

			SmtpRepository smtpDatabaseService = SmtpRepository.getInstance();
			int blockEmailCount = smtpDatabaseService.getBlockEmailCountEx(sd.getFrom(), "*@"+sDomain, sd.getRcptto());

			if(blockEmailCount > 0){
				return -1;
			}

			// 발신자 IP 가 없는 경우 정상처리
			if( StringUtils.isEmpty(sd.getPeerIP()) ){
				return 0;
			}

			// 아이피 체크
			List<DenyIp> denyIpList = smtpDatabaseService.getDenyIpList();

			String[] arrInIP = ImStringUtil.getTokenizedString(sd.getPeerIP(), ".");
			if(arrInIP.length > 1){
				sWhereIp = arrInIP[0] + "." + arrInIP[1];
			}

			if(denyIpList == null) {
				return 0;
			}

			for(DenyIp denyIp : denyIpList){
				if(ImIpUtil.matchIPbyCIDR(denyIp.getIp(), sd.getPeerIP())){
					return -2;
				}
			}
		}catch(Exception ex){
			String errorId = ErrorTraceLogger.log(ex);
			smailLogger.error("[{}] {} - check spool spam email or ip (" + sd.getFrom() + " / " + sd.getPeerIP() + ")" , sd.getTraceID(), errorId );
		}
		return nRet;
	}

	// 긴급 메일(서버에서 파일 저장 실패했을 경우 발생)
	public static void sendNotifyErrorEmergency(String sTo){
		if(sTo == null || sTo.equals("")) return;
		String sDefQueuePath = ImSmtpConfig.getInstance().getQueuePath();
		String sDefaultDomain = ImSmtpConfig.getInstance().getDefaultDomain();
		String sMailerDaemon = "mailer-daemon@"+sDefaultDomain;
		String sMailServerExplain = "";

		if(StringUtils.isEmpty(sTo) ) {
			return;
		}

		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props);
		try{
			String sMsgKey = UUIDService.getUID();//ImUtils.makeKey(24);

			StringBuffer sbfInput = new StringBuffer();
			String sText = sbfInput.append("This is an emergency mail.\r\n")
					.append("Please check the mail server as soon as possible!!")
					.append("\r\n")
					.toString();

			ResourceBundle bundle = ResourceBundle.getBundle("messages.smtp", new Locale(ImSmtpConfig.getInstance().getDefaultLang()));

			ImMessage message = new ImMessage(session);
			message.setCharset("utf-8");
			message.setFrom(sMailerDaemon);
			message.setTo(sTo);
			message.setSubject("[Emergency]["+bundle.getString("1")+"] " + sMailServerExplain + " has a problem.("+bundle.getString("2")+")");
			message.setPriority(1);
			message.setText(sText);
			//message.setHtml(sText);
			String sMime = message.makeMimeData();

			String sQueueDir = ImMessQueue.createQueueDir(ImUtils.stripDirSlash(sDefQueuePath));
			String sFileName = UUIDService.getUID()+".sml";//ImUtils.makeKey(24) + ".sml";

			ImSmtpSendData issd = new ImSmtpSendData();
			issd.setTraceID(UUIDService.getTraceID());
			issd.setMsgID(sMsgKey);
			issd.setFrom(sMailerDaemon);
			issd.setRcptto(sTo);
			issd.setPeerIP("127.0.0.1");
			issd.setIsRelay(1);
			issd.setSubject("[Emergency]["+bundle.getString("1")+"] " + sMailServerExplain + " has a problem.("+bundle.getString("2")+")");
			issd.setDSN(true);

			addQueue( issd, sMime, sQueueDir, sFileName);
		} catch(Exception e){
			String errorId = ErrorTraceLogger.log(e);
			smailLogger.error("{} - [ImSmtpUtil] " , errorId );

		}
	}

	public static String sendNotifyErrorMessage(ImSmtpSendData sd) {
		String sPostmaster = ImSmtpConfig.getInstance().getPostmaster();
		//smailLogger.debug("sErrorAdmin : {}", sErrorAdmin);
		String sDefaultDomain = ImSmtpConfig.getInstance().getDefaultDomain();
		String sMailerDaemon = "mailer-daemon@"+sDefaultDomain;
		String sErrString = "Connect Error";

		if(StringUtils.isEmpty(sPostmaster)){
			sPostmaster = sMailerDaemon;
		}

		if(sPostmaster.equals(sd.getFrom()) || sMailerDaemon.equals(sd.getFrom()) ){
			return "";
		}

		sErrString = sendNotifyErrorMessage(sd, sPostmaster, sd.getFrom());


//		if(sd.getFrom() != null && !sd.getFrom().equals("")){
//			sErrString = sendNotifyErrorMessage(sd,sMailerDaemon,sd.getFrom());
//		}
		smailLogger.info("[{}] DSN Send: {} -> {}, SUBJ:{}", sd.getTraceID(), sPostmaster, sd.getFrom(), sd.getSubject());

		return sErrString;
	}

	public static String sendNotifyErrorMessage(ImSmtpSendData sd,String p_sFrom,String p_sRcptTo) {
		ResourceBundle bundle = ResourceBundle.getBundle("messages.smtp", new Locale(ImSmtpConfig.getInstance().getDefaultLang()));
		String sMailKey = UUIDService.getUID();//ImUtils.makeKey(24);
		//		String sQueueDir = ImMessQueue.createQueueDir(ImUtils.stripDirSlash(sDefQueuePath));
		String sFileName = sMailKey + ".sml";

		String sErrString = "Connect Error";

		if(StringUtils.isEmpty(p_sFrom) || StringUtils.isEmpty(p_sRcptTo)) {
			return "";
		}



		String[] arrFrom = ImStringUtil.getEmailAddress(p_sFrom);
		String fromEmail = arrFrom[1];
		String fromName = arrFrom[0];
		if(StringUtils.isEmpty(fromName)) {
			fromName = bundle.getString("8");
		}

		// 큐정보를 먼저 가져온다.
		//ImQueueObj objQueue = ImSensProxyServer.getMessageQueue();
		MessageQueueService messageQueueService = MessageQueueService.getInstance();
		ImQueueObj objQueue = messageQueueService.getMessageQueue();

		String sQueueDir = ImMessQueue.createQueueDir(ImUtils.stripDirSlash(ImSmtpConfig.getInstance().getQueuePath())+File.separator+"queue"+objQueue.getIndex());


		// postmaster
		if (fromEmail.equalsIgnoreCase(p_sRcptTo)) {
			return "";
		}

		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props);
		try {
			String sRcptUserID = "";
			String sRcptDomain = "";
			String[] sEmail = p_sRcptTo.split("@");
			if(sEmail.length > 1){
				sRcptUserID = sEmail[0];
				sRcptDomain = sEmail[1];
			}
			if (StringUtils.isEmpty(sRcptDomain)) {
				return "";
			}

			String sSlogFile = sd.getQueueDir() + File.separator + "slog" + File.separator + sd.getQueueFile();
			String sMsgFile = sd.getQueueDir() + File.separator + "mess" + File.separator + sd.getQueueFile();

			List<String> listErrStr = new ArrayList<String>();
			BufferedReader bufferedreader = null;
			try {
				String str = null;
				bufferedreader = new BufferedReader(new FileReader(sSlogFile));
				while((str = bufferedreader.readLine()) != null){
					//smailLogger.error("sLine: {}" , str );
					if( str.indexOf("ErrString =") >= 0 ){
						sErrString = str.substring("ErrString =".length()+1);
						listErrStr.add(sErrString);
					}
				}
			} catch (Exception ee){
			} finally {
				try { bufferedreader.close(); } catch(Exception e){e.printStackTrace();}
			}

			// 에러코드로 회신메세지를 작성할 내용을 만든다.
			ReturnMailCodeMatcher returnMailCodeMatcher = ReturnMailCodeMatcher.getInstance();
			ErrorCode errorCode = null;
			try {
				errorCode = returnMailCodeMatcher.matcher( listErrStr.get(0) );
			}catch(Exception e) {}

			// 에러 문구-------
			StringBuilder sb = new StringBuilder();
			for (String s : listErrStr){
				sb.append(">> ").append(s).append("\r\n\r\n");
			}

			String errString = sb.toString();

			// 에러메시지에서 링크를 추출한다.
//			String urlRegex = "\\b((https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|])";
//			Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
//			Matcher urlMatcher = pattern.matcher(errString);
//			StringBuilder sbUrl = new StringBuilder();
//			while (urlMatcher.find()){
//				sbUrl.append(errString.substring(urlMatcher.start(0), urlMatcher.end(0))).append("\r\n");
//			}
//
//			if(sbUrl.length() > 0){
//				errorCode.setDescription(errorCode.getDescription() + "\r\n(" + sbUrl.toString()+")");
//			}
			//----------

			ImSmtpSendingInfo sendingInfo = ImSmtpUtil.getSendingInfo(sMsgFile+ ".body");
			//smailLogger.info( sendingInfo.getMailKey() + " / " + sendingInfo.getReciptKey() + " / " + di.getAHost() + " / " + di.getHost());

			//* 원본을 첨부로 넣었으나 1M 이상은 첨부로 넣지 않기로 함(2011-07-12)
			String sAttName = "";
			String sAttPath = "";
			String strOriginalMessage = "----- Original message -----";
			boolean bOrgMailAttach = false;
			if(ImFileUtil.getFileSize(sMsgFile + ".body") <= 1048576){
				sAttName = sd.getQueueFile() + ".eml";
				sAttPath = sd.getQueueDir() + File.separator + "temp" + File.separator + sAttName;
				ImFileUtil.copyFile(sMsgFile + ".body", sAttPath);
				bOrgMailAttach = true;
				strOriginalMessage = "----- Original message - included as attachment -----";
			}
			//*/
			ImMimeHeader imHdr= new ImMimeHeader();
			imHdr.parseHeader(sMsgFile + ".body");
			String sHeader = imHdr.getHeader();



			StringBuffer sbfInput = new StringBuffer();
			sbfInput.append("1. "+bundle.getString("3")+"\r\n")
					.append("   (This is an automatically generated Delivery Status Notification (Failure))\r\n\r\n")
					.append("- "+bundle.getString("4")+": ")
					.append(sd.getFrom())
					.append("\r\n")
					.append("- "+bundle.getString("5")+": ")
					.append(sd.getRcptto())
					.append("\r\n\r\n")
					.append("2. "+bundle.getString("6")+" \r\n")
					.append("   (The reason of the delivery failure was) \r\n\r\n");
			if(errorCode != null) {
				sbfInput.append(" : ").append(errorCode.getDescription()).append("\r\n\r\n");
			}
			//.append(sErrString)
			sbfInput.append(sb.toString())
					.append("\r\n\r\n\r\n")
					.append(strOriginalMessage +"\r\n\r\n")
					.append(sHeader);
			String sText = sbfInput.toString();
			ImMessage message = new ImMessage(session);
			message.setCharset("UTF-8");
			/*if (p_sFrom.trim().equals("")) {
				message.setFrom("PostMaster");
			} else {
//				message.setFrom("PostMaster<" + p_sFrom + ">");
				message.setFrom(p_sFrom);
			}*/
			message.setFrom(fromName, fromEmail, "UTF-8");
			message.setTo(p_sRcptTo);
			message.setSubject("Delivery Status Notification - Failure ("+bundle.getString("7")+")", "UTF-8");
			message.setHeader("X-NOTIFY-ERR", fromEmail);
			//message.setHeader("X-SensTrace", sd.getTraceID());
			message.setHeader("X-SensTrace", UUIDService.getTraceID());
			message.setText(sText,"UTF-8");
			// message.setHtml(sText);


			// 원본을 첨부하는 대신 헤더만 본문에 같이 넣어준다.
			if(bOrgMailAttach){
				message.addAttachWithContentType(sAttPath, sAttName, "message/rfc822");
			}
			String mimeFilePath = sQueueDir + File.separator + "temp" + File.separator + sFileName + ".body";
			message.makeMimeFile(mimeFilePath);
			//String sMime = message.makeMimeData();

			ImSmtpSendData issd = new ImSmtpSendData();
			issd.setMsgID(sMailKey);
			// 기존 TraceID에 1을 덧붙인다.
			issd.setTraceID(sd.getTraceID()+"1");
			issd.setOrgTraceID(sd.getTraceID());
			issd.setFrom(ImStringUtil.getStringBetween(fromEmail, "<", ">"));
			issd.setRcptto(ImStringUtil.getStringBetween(p_sRcptTo, "<",">"));
			issd.setPeerIP("127.0.0.1");
			issd.setIsRelay(1);
			issd.setMailsize(new File(mimeFilePath).length());
			issd.setSubject("Delivery Status Notification - Failure ("+bundle.getString("7")+")");
			// 발송실패 리턴메일임
			issd.setDSN(true);

			addQueue( issd, sQueueDir, sFileName);

			// attach 로 넣었던 원본 제거
			if(bOrgMailAttach){
				ImFileUtil.deleteFile(sAttPath);
			}
		} catch (Exception e) {
			String errorId = ErrorTraceLogger.log(e);
			smailLogger.error("{} - [ImSmtpUtil] " , errorId );
		}

		return sErrString;
	}

	/**
	 *
	 *
	 * @param sDefQueuePath
	 * @return
	 */
	public static String createMessDataFile(String sDefQueuePath) {
		String sQueueDir = ImMessQueue.createQueueDir(ImUtils
				.stripDirSlash(sDefQueuePath));
		String sFileName = UUIDService.getUID()+".sml.body";//ImUtils.makeKey(24) + ".sml.body";

		String sPath = sQueueDir + File.separator + "temp" + File.separator + sFileName;

		return sPath;
	}

	/**
	 *
	 * @param p_sQueuePath
	 * @return
	 */
	public static ImSmtpSendData getSendData(String p_sQueuePath){
		ObjectInputStream in = null;
		ImSmtpSendData issd = null;

		File f = new File(p_sQueuePath);
		try{
			in = new ObjectInputStream(new FileInputStream(f));

			issd = (ImSmtpSendData)in.readObject();

			// slog
			//String sSlogFile = issd.getQueueDir() + File.separator + "slog" + File.separator + issd.getQueueFile();
			//issd.updateRetryInfo(sSlogFile);
		}catch(Exception ex){
			String errorId = ErrorTraceLogger.log(ex);
			smailLogger.error("{} - getSendData " , errorId );
		}finally{
			try{
				if(in != null) in.close();
				//f.delete();
			}catch(Exception fileex){fileex.printStackTrace();}
		}

		return issd;
	}

	public static void addQueue( ImSmtpSendData issd, String sQueueDir, String sFileName) {

		ObjectOutputStream objS = null;
		try {
			String sTempFilePath = sQueueDir + File.separator + "temp" + File.separator + sFileName + ".body";
			//			String sQuePath = sQueueDir + File.separator + "mess" + File.separator + sFileName;
			//			String sBodyPath = sQuePath + ".body";

			// 큐정보를 먼저 가져온다.
			//ImQueueObj objQueue = ImSensProxyServer.getMessageQueue();
			MessageQueueService messageQueueService = MessageQueueService.getInstance();
			ImQueueObj objQueue = messageQueueService.getMessageQueue();

			if(objQueue != null){
				String sQuePath = ImMessQueue.createQueueDir(ImUtils.stripDirSlash(ImSmtpConfig.getInstance().getQueuePath())+File.separator+"queue"+objQueue.getIndex())
						+ File.separator + "mess" + File.separator + sFileName;
				String sBodyPath = sQuePath + ".body";

				ImFileUtil.moveFile(sTempFilePath, sBodyPath);

				// info
				/*
    			String[] sSendInfo = getSendingInfo(sBodyPath);
    			issd.setDomain(sSendInfo != null ? sSendInfo[0]:"");
    			issd.setMailKey(sSendInfo != null ?sSendInfo[1]:"");
    			issd.setReserveTime(sSendInfo != null ?sSendInfo[2]:"");
				 */

				issd.setQueuePath(sQuePath);
				issd.setSendResultCode(0);
				File f = new File(sQuePath);
				objS = new ObjectOutputStream(new FileOutputStream(f));
				objS.writeObject(issd);

				objQueue.getQueue().enqueue(sQuePath);
			}


		} catch (Exception ex) {
			String errorId = ErrorTraceLogger.log(ex);
			smailLogger.error("{} - addQueue" , errorId );
		} finally {
			try{
				if(objS != null)objS.close();
			}catch(Exception ex1){ex1.printStackTrace();}
		}
	}

	public static String addQueue2( ImSmtpSendData issd,
									String p_sHeader,  String sKey) {
		String sTempFilePath = ImUtils.stripDirSlash(ImSmtpConfig.getInstance().getQueuePath()) + File.separator + "temp" + File.separator + sKey + ".sml.body";
		String sResult = sKey;

		ObjectOutputStream objS = null;
		BufferedOutputStream fos = null;
		try {
			sResult = UUIDService.getUID();//ImUtils.makeKey(24);
			String sFileName = sResult + ".sml";

			// 큐정보를 먼저 가져온다.
			//ImQueueObj objQueue = ImSensProxyServer.getMessageQueue();
			MessageQueueService messageQueueService = MessageQueueService.getInstance();
			ImQueueObj objQueue = messageQueueService.getMessageQueue();

			if(objQueue != null){
				String sQuePath = ImMessQueue.createQueueDir(ImUtils.stripDirSlash(ImSmtpConfig.getInstance().getQueuePath())+File.separator+"queue"+objQueue.getIndex())
						+ File.separator + "mess" + File.separator + sFileName;
				String sBodyPath = sQuePath + ".body";

				// info
				issd.setQueuePath(sQuePath);
				File f = new File(sQuePath);
				objS = new ObjectOutputStream(new FileOutputStream(f));
				objS.writeObject(issd);

				// header + body
				fos = new BufferedOutputStream( new FileOutputStream(sBodyPath, true) );
				fos.write(p_sHeader.getBytes());
				fos.close();

				ImFileUtil.catFile(sBodyPath, sTempFilePath);

				objQueue.getQueue().enqueue(sQuePath);
			}

			//            ImSensProxyServer.addMessageQueue(sQuePath);
			//          bq.enqueue(sd);

		} catch (Exception ex) {
			String errorId = ErrorTraceLogger.log(ex);
			smtpLogger.error("{} - addQueue2" , errorId );
		} finally {
			/*
            try {
                // ImLoggerEx.log("sender.error.log","(commit - "+ ex + sQueuePath);
                ImFileUtil.deleteFile(sTempFilePath);
            } catch (Exception ex1) {
            }
			 */
			try{
				if(objS != null)objS.close();
			}catch(IOException ex1){}
			try {
				fos.close();
			} catch (Exception ex) {
			}
		}

		return sResult;
	}

	public static String addQueueNetlink( ImSmtpSendData issd, String p_sHeader, String sKey, String sTempFilePath) {

		String sResult = sKey;

		ObjectOutputStream objS = null;
		BufferedOutputStream fos = null;
		try {
			//sResult = UUIDService.getUID();
			String sFileName = sResult + ".sml";

			// 큐정보를 먼저 가져온다.
			//ImQueueObj objQueue = ImSensProxyServer.getMessageQueue();
			MessageQueueService messageQueueService = MessageQueueService.getInstance();
			ImQueueObj objQueue = messageQueueService.getMessageQueue();

			if(objQueue != null){
				String sQuePath = ImMessQueue.createQueueDir(ImUtils.stripDirSlash(ImSmtpConfig.getInstance().getQueuePath())+File.separator+"queue"+objQueue.getIndex())
						+ File.separator + "mess" + File.separator + sFileName;
				String sBodyPath = sQuePath + ".body";

				// info
				issd.setQueuePath(sQuePath);
				issd.setSendResultCode(0);
				File f = new File(sQuePath);
				objS = new ObjectOutputStream(new FileOutputStream(f));
				objS.writeObject(issd);

				// header + body
				fos = new BufferedOutputStream( new FileOutputStream(sBodyPath, true) );
				fos.write(p_sHeader.getBytes());
				fos.close();

				ImFileUtil.catFile(sBodyPath, sTempFilePath);

				objQueue.getQueue().enqueue(sQuePath);
			}

		} catch (Exception ex) {
			String errorId = ErrorTraceLogger.log(ex);
			smtpLogger.error("{} - addQueueNetlink" , errorId );
		} finally {
			try{
				if(objS != null)objS.close();
			}catch(IOException ex1){}
			try {
				fos.close();
			} catch (Exception ex) {
			}
		}

		return sResult;
	}

	public static String addQueueLocal( ImSmtpSendData issd, String sLocalPath, String sKey) {
		String sTempFilePath = ImUtils.stripDirSlash(ImSmtpConfig.getInstance().getQueuePath()) + File.separator + "local" + File.separator + sKey + ".sml.body";
		String sResult = sKey;

		ObjectOutputStream objS = null;
		BufferedOutputStream fos = null;
		try {
			sResult = UUIDService.getUID();//ImUtils.makeKey(24);
			String sFileName = sResult + ".sml";

			// 큐정보를 먼저 가져온다.
			MessageQueueService messageQueueService = MessageQueueService.getInstance();
			ImQueueObj objQueue = messageQueueService.getMessageQueue();

			if(objQueue != null){
				String sQuePath = ImMessQueue.createQueueDir(ImUtils.stripDirSlash(ImSmtpConfig.getInstance().getQueuePath())+File.separator+"queue"+objQueue.getIndex())
						+ File.separator + "mess" + File.separator + sFileName;
				String sBodyPath = sQuePath + ".body";

				// info
				issd.setQueuePath(sQuePath);
				issd.setSendResultCode(0);
				File f = new File(sQuePath);
				objS = new ObjectOutputStream(new FileOutputStream(f));
				objS.writeObject(issd);

				ImFileUtil.moveFile(sTempFilePath, sBodyPath);

				objQueue.getQueue().enqueue(sQuePath);
				smtpLogger.info("LocalSpool Add Queue : {}", sTempFilePath);
			}

		} catch (Exception ex) {
			String errorId = ErrorTraceLogger.log(ex);
			smtpLogger.error("{} - addQueueLocal " , errorId );
		} finally {
			try{
				if(objS != null)objS.close();
			}catch(IOException ex1){}
			try {
				fos.close();
			} catch (Exception ex) {
			}
		}

		return sResult;
	}

	public static void addQueue( ImSmtpSendData issd, String p_sMsg, String sQueueDir,String sFileName) {
		ObjectOutputStream objS = null;
		BufferedOutputStream fos = null;
		try {
			String sQuePath = sQueueDir + File.separator + "mess" + File.separator + sFileName;
			String sBodyPath = sQuePath + ".body";

			smailLogger.debug("queue path: " + sQuePath);
			smailLogger.debug("sBodyPath path: " + sBodyPath);
			// info
			/*
			String[] sSendInfo = getSendingInfo(sBodyPath);
			issd.setDomain(sSendInfo != null ? sSendInfo[0]:"");
			issd.setMailKey(sSendInfo != null ?sSendInfo[1]:"");
			issd.setReserveTime(sSendInfo != null ?sSendInfo[2]:"");
			 */

			issd.setQueuePath(sQuePath);
			issd.setSendResultCode(0);
			File f = new File(sQuePath);
			objS = new ObjectOutputStream(new FileOutputStream(f));
			objS.writeObject(issd);

			// body
			fos = new BufferedOutputStream(new FileOutputStream(sBodyPath, true) );
			fos.write(p_sMsg.getBytes());
			fos.close();

			/*
    		if(isLocalDomain(issd)){
    			ImSensProxyServer.addLocalMessageQueue(sQuePath);
    		}else{
    			ImSensProxyServer.addRemoteMessageQueue(sQuePath);
    		}
			 */
			//ImSensProxyServer.addMessageQueue(sQuePath);
			MessageQueueService messageQueueService = MessageQueueService.getInstance();
			messageQueueService.addMessageQueue(sQuePath);
		} catch (Exception ex) {
			String errorId = ErrorTraceLogger.log(ex);
			smailLogger.error("{} - addQueue" , errorId );

		} finally {
			try{
				if(objS != null)objS.close();
			}catch(Exception ex1){}
			try {
				fos.close();
			} catch (Exception ex) {
			}
		}
	}

	public static void addQueue( ImSmtpSendData issd, String p_sHeader, String sQueueDir, String sKey,String[] sSendingInfo) {
		String sTempFilePath = sQueueDir + File.separator + "temp" + File.separator + sKey + ".sml.body";
		ObjectOutputStream objS = null;
		BufferedOutputStream fos = null;
		try {
			String sFileName = UUIDService.getUID()+".sml";//ImUtils.makeKey(24) + ".sml";
			//String sFileName = sKey + ".sml";
			String sQuePath = sQueueDir + File.separator + "mess" + File.separator + sFileName;
			String sBodyPath = sQuePath + ".body";

			// info
			issd.setQueuePath(sQuePath);
			issd.setSendResultCode(0);
			File f = new File(sQuePath);
			objS = new ObjectOutputStream(new FileOutputStream(f));
			objS.writeObject(issd);

			// header + body
			fos = new BufferedOutputStream( new FileOutputStream(sBodyPath, true) );
			fos.write(p_sHeader.getBytes());
			fos.close();

			ImFileUtil.catFile(sBodyPath, sTempFilePath);

			MessageQueueService messageQueueService = MessageQueueService.getInstance();
			messageQueueService.addMessageQueue(sQuePath);
			/*
    		if(isLocalDomain(issd)){
    			ImSensProxyServer.addLocalMessageQueue(sQuePath);
    		}else{
    			ImSensProxyServer.addRemoteMessageQueue(sQuePath);
    		}
			 */
		} catch (Exception ex) {
			String errorId = ErrorTraceLogger.log(ex);
			smailLogger.error("{} - [ImSmtpUtil] " , errorId );
			try {
				// ImLoggerEx.log("sender.error.log","(commit - "+ ex + sQueuePath);
				ImFileUtil.deleteFile(sTempFilePath);
			} catch (Exception ex1) {
			}
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

	public static String addQueueBulk( ImSmtpSendData issd,
									   String p_sHeader, String sKey, ImSmtpSendingInfo sendInfo) {
		String sTempFilePath = ImUtils.stripDirSlash(ImSmtpConfig.getInstance().getQueuePath()) + File.separator + "temp" + File.separator + sKey + ".sml.body";
		String sResult = sKey;

		ObjectOutputStream objS = null;
		BufferedOutputStream fos = null;
		try {
			sResult = UUIDService.getUID();//ImUtils.makeKey(24);
			String sFileName = sResult + ".sml";

			String sQuePath = ImMessQueue.createQueueDir(ImUtils.stripDirSlash(ImSmtpConfig.getInstance().getQueuePath())+File.separator+"bulk")
					+ File.separator + "mess" + File.separator + sFileName;
			String sBodyPath = sQuePath + ".body";

			// info
			issd.setQueuePath(sQuePath);
			issd.setSendResultCode(0);
			File f = new File(sQuePath);
			objS = new ObjectOutputStream(new FileOutputStream(f));
			objS.writeObject(issd);

			// header + body
			fos = new BufferedOutputStream( new FileOutputStream(sBodyPath, true) );
			fos.write(p_sHeader.getBytes());
			fos.close();

			ImFileUtil.catFile(sBodyPath, sTempFilePath);

			smtpLogger.debug("In Bulk :"+sQuePath);

			MessageQueueService messageQueueService = MessageQueueService.getInstance();
			messageQueueService.addBulkMessageQueue(sQuePath);
			//ImSensProxyServer.addBulkMessageQueue(sQuePath);

		} catch (Exception ex) {
			String errorId = ErrorTraceLogger.log(ex);
			smailLogger.error("{} - addQueueBulk" , errorId );
			try {
				// ImLoggerEx.log("sender.error.log","(commit - "+ ex + sQueuePath);
				ImFileUtil.deleteFile(sTempFilePath);
			} catch (Exception ex1) {
			}
		} finally {
			try{
				if(objS != null)objS.close();
			}catch(IOException ex1){}
			try {
				fos.close();
			} catch (Exception ex) {
			}
		}

		return sResult;
	}

	public static ArrayList<String> getPureEmailList(String addr){
		ArrayList<String> arrAddr = new ArrayList<String>();

		if(StringUtils.isEmpty(addr)){
			return arrAddr;
		}

		addr = ImStringUtil.replace(addr, ";", ",");

		try {
			InternetAddress[] ia = InternetAddress.parse(addr);

			for(int i=0; i<ia.length; i++){
				String fullemail = ia[i].toString();
				String name = ia[i].getPersonal();
				String email = ia[i].getAddress();

				if(email.contains("@")){
					arrAddr.add(email);
				} else {
					// 주소록 그룹, 공용주소록, 조직도 등의 특수한 이름
					if(email.startsWith("*") || email.startsWith("~") || email.startsWith("#") || email.startsWith("$") || email.startsWith("!")){
						if(StringUtils.isNotEmpty(name)){
							email = email+"("+name+")";
						}
					}
					arrAddr.add(email);
				}
			}
		} catch (AddressException e) {
			String[] splitAddr = addr.split(",");
			for(int i=0; i<splitAddr.length; i++){
				String email = ImStringUtil.getStringBetween(splitAddr[i], "<", ">");
				arrAddr.add(splitAddr[i]);
			}
		}

		return arrAddr;
	}

	public static ImSmtpSendingInfo getSendingInfo(String p_sFile, String peerIP) {
		ImSmtpSendingInfo sendingInfo = new ImSmtpSendingInfo();
		String sDomain = "";
		String sMailKey = "";
		String sSubject ="";
		String sCharset = "";
		String sReciptKey = "";
		//String sDeliverTo = "";
		String sDeliverFrom = "";
		String sFrom = "";
		String userid = "";
		String ahost = "";
		String tbl_no = "";
		String part_no = "";
		String xmailer = "";
		String sSenddate = "";
		String xWebSend = "";
		// 그룹키
		String groupKey = "";
		// 수신자키
		String rcptKey = "";
		List<String> listRcpt = new ArrayList<String>();
		boolean isLocalMail = false;
		String as[] = null;
		BufferedInputStream fis = null;
		try {
			fis = new BufferedInputStream( new FileInputStream(p_sFile) );
			ImMimeHeader ihdrs = new ImMimeHeader();
			ihdrs.parseHeader(fis);

//				InternetHeaders ihdrs = new InternetHeaders(fis);
			String arrInfo = ihdrs.getHeader("X-Sensmail-Info");
			if ( StringUtils.isNotEmpty(arrInfo) ){
				String[] arrSubInfo = ImStringUtil.getTokenizedString(arrInfo, ";");
				if (arrSubInfo != null && (arrSubInfo.length > 0)) {
					sDomain = arrSubInfo[0];
					sMailKey = arrSubInfo[1];

					if(arrSubInfo.length > 2){
						userid = arrSubInfo[2];

						if(arrSubInfo.length > 3){
							ahost = arrSubInfo[3];

							if(arrSubInfo.length > 4){
								tbl_no = arrSubInfo[4];

								if(arrSubInfo.length > 5){
									part_no = arrSubInfo[5];
								}
							}
						}
					}
				}
			}

			sSubject = ihdrs.getHeader("subject");

			String sContentType = ihdrs.getHeader("Content-Type");
			if(sContentType != null){
				try {
					ContentType ct = new ContentType(sContentType);
					if (ct != null) {
						sCharset = ct.getParameter("charset");
					}
				}catch(Exception e){}
			}

			sFrom = ihdrs.getHeader("from");

			sReciptKey = ihdrs.getHeader("X-Sensmail-Rcptkey");

			//sDeliverTo = ihdrs.getHeader("X-DELIVER-TO");
			sDeliverFrom = ihdrs.getHeader("X-Forwarded-For");
			sDeliverFrom = ImStringUtil.getStringBefore(sDeliverFrom, " ");
			xmailer = ihdrs.getHeader("X-Mailer");

			sSenddate = ihdrs.getHeader("Date");


			String[] receiveds = ihdrs.getHeaders("Received");
			if( receiveds != null ){
				if( receiveds != null ){
					String fromIP = MessageHeaderUtil.getMailHeaderSendIp( receiveds, peerIP );
					sendingInfo.setFromIP( fromIP );
				}
			}else{

				//String clientIp = ihdrs.getHeader("X-ClientIP");
				String clientIp = ihdrs.getHeader("X-ORIGINATING-SPRXY-IP");
				if(StringUtils.isEmpty(clientIp)) {
					clientIp = ihdrs.getHeader("X-ClientIP");
				}
				if( StringUtils.isNotEmpty(clientIp) ){
					sendingInfo.setFromIP( clientIp );
				}
			}

			String to = ihdrs.getHeader("To");
			String cc = ihdrs.getHeader("Cc");
			String bcc = ihdrs.getHeader("Bcc");

			List<String> listTo = getPureEmailList(to);
			List<String> listCc = getPureEmailList(cc);
			List<String> listBcc = getPureEmailList(bcc);
			listRcpt.addAll(listTo);
			listRcpt.addAll(listCc);
			listRcpt.addAll(listBcc);
			sendingInfo.setListRcpt(listRcpt);

			String send_type = ihdrs.getHeader(ImSmtpConfig.HEADER_SEND_TYPE);
			sendingInfo.setSend_type(send_type);

			xWebSend = ihdrs.getHeader("X-WebSend");
			if( StringUtils.isNotEmpty(xWebSend)) xWebSend = ImUtils.decodeBase64(xWebSend, "utf-8");

			// 그룹 키 정보(Base64 encode)
			// ==> 그냥 평문 처리
			groupKey = ihdrs.getHeader(ImSmtpConfig.getInstance().getHeaderGroupKey());
			rcptKey = ihdrs.getHeader(ImSmtpConfig.getInstance().getHeaderRcptKey());
			//if( StringUtils.isNotEmpty(groupKey)) groupKey = ImUtils.decodeBase64(groupKey, "utf-8");

			sendingInfo.setDomain(sDomain);
			sendingInfo.setMailKey(sMailKey);
			sendingInfo.setSubject(sSubject);
			sendingInfo.setCharset(sCharset);
			sendingInfo.setReciptKey(sReciptKey);
			//sendingInfo.setDeliverTo(sDeliverTo);
			sendingInfo.setDeliverFrom(sDeliverFrom);
			sendingInfo.setFrom(sFrom);
			sendingInfo.setUserid(userid);
			sendingInfo.setAhost(ahost);
			sendingInfo.setTbl_no(tbl_no);
			sendingInfo.setPart_no(part_no);
			sendingInfo.setXmailer(xmailer);
			sendingInfo.setSenddate(sSenddate);
			sendingInfo.setXWebSend(xWebSend);
			sendingInfo.setGroupKey(groupKey);
			sendingInfo.setRcptKey(rcptKey);
		}  catch (Exception ex){
			String errorId = ErrorTraceLogger.log(ex);
			smtpLogger.error("{} - getSendingInfo error" , errorId );
		} finally {
			try { if(fis != null) fis.close(); } catch (Exception e1) {}
		}
		return sendingInfo;
	}

	public static ImSmtpSendingInfo getSendingInfo(String p_sFile) {
		ImSmtpSendingInfo sendingInfo = new ImSmtpSendingInfo();
		String sReserveTime = "";
		String sDomain = "";
		String sMailKey = "";
		String sSubject ="";
		String sCharset = "";
		String sReciptKey = "";
		String sDeliverFrom = "";
		String sFrom = "";
		String userid = "";
		String ahost = "";
		String tbl_no = "";
		String part_no = "";
		String xmailer = "";
		String sSenddate = "";
		// 그룹키
		String groupKey = "";
		String rcptKey = "";
		String as[] = null;
		BufferedInputStream fis = null;
		try {
			fis = new BufferedInputStream( new FileInputStream(p_sFile) );
			ImMimeHeader ihdrs = new ImMimeHeader();
			ihdrs.parseHeader(fis);
//				InternetHeaders ihdrs = new InternetHeaders(fis);
			String arrInfo = ihdrs.getHeader("X-Sensmail-Info");
			if ( StringUtils.isNotEmpty(arrInfo) ){
				String[] arrSubInfo = ImStringUtil.getTokenizedString(arrInfo, ";");
				if (arrSubInfo != null && (arrSubInfo.length > 0)) {
					sDomain = arrSubInfo[0];
					sMailKey = arrSubInfo[1];

					if(arrSubInfo.length > 2){
						userid = arrSubInfo[2];

						if(arrSubInfo.length > 3){
							ahost = arrSubInfo[3];

							if(arrSubInfo.length > 4){
								tbl_no = arrSubInfo[4];

								if(arrSubInfo.length > 5){
									part_no = arrSubInfo[5];
								}
							}
						}
					}
				}
			}
			sReserveTime = ihdrs.getHeader("X-Sensmail-Reserve");

			sSubject = ihdrs.getHeader("subject");

			String sContentType = ihdrs.getHeader("Content-Type");
			if(sContentType != null){
				try {
					ContentType ct = new ContentType(sContentType);
					if (ct != null) {
						sCharset = ct.getParameter("charset");
					}
				}catch(Exception e){}
			}

			sFrom = ihdrs.getHeader("from");

			sReciptKey = ihdrs.getHeader("X-Sensmail-Rcptkey");

			//sDeliverTo = ihdrs.getHeader("X-DELIVER-TO");
			sDeliverFrom = ihdrs.getHeader("X-Forwarded-For");

			xmailer = ihdrs.getHeader("X-Mailer");

			sSenddate = ihdrs.getHeader("Date");


			String[] receiveds = ihdrs.getHeaders("Received");
			if( receiveds != null ){
				if( receiveds != null ){
					String fromIP = getMailHeaderSendIp( receiveds );
					sendingInfo.setFromIP( fromIP );
				}
			}else{

				String clientIp = ihdrs.getHeader("X-ClientIP");
				if( StringUtils.isNotEmpty(clientIp) ){
					sendingInfo.setFromIP( clientIp );
				}
			}
			// 그룹 키 정보(Base64 encode)
			// ==> 그냥 평문 처리
			groupKey = ihdrs.getHeader(ImSmtpConfig.getInstance().getHeaderGroupKey());
			rcptKey = ihdrs.getHeader(ImSmtpConfig.getInstance().getHeaderRcptKey());
			//if( StringUtils.isNotEmpty(groupKey)) groupKey = ImUtils.decodeBase64(groupKey, "utf-8");

			sendingInfo.setDomain(sDomain);
			sendingInfo.setMailKey(sMailKey);
			sendingInfo.setReserveTime(sReserveTime);
			sendingInfo.setSubject(sSubject);
			sendingInfo.setCharset(sCharset);
			sendingInfo.setReciptKey(sReciptKey);
			sendingInfo.setDeliverFrom(sDeliverFrom);
			sendingInfo.setFrom(sFrom);
			sendingInfo.setUserid(userid);
			sendingInfo.setAhost(ahost);
			sendingInfo.setTbl_no(tbl_no);
			sendingInfo.setPart_no(part_no);
			sendingInfo.setXmailer(xmailer);
			sendingInfo.setSenddate(sSenddate);
			sendingInfo.setGroupKey(groupKey);
			sendingInfo.setRcptKey(rcptKey);
		}  catch (Exception ex){
			String errorId = ErrorTraceLogger.log(ex);
			smtpLogger.error("{} - [ImSmtpUtil.getSendingInfo] " , errorId );
		} finally {
			try {
				fis.close();
			} catch (Exception e1) {
			}
		}
		return sendingInfo;
	}

	public static ImSmtpSendingInfo getSendingInfoEx(String p_sFile) {
		ImSmtpSendingInfo sendingInfo = new ImSmtpSendingInfo();
		String sReserveTime = "";
		String sDomain = "";
		String sMailKey = "";
		String sSubject ="";
		String sCharset = "";
		String sReciptKey = "";
		String sDeliverFrom = "";
		String sFrom = "";
		String userid = "";
		String ahost = "";
		String tbl_no = "";
		String part_no = "";
		String xmailer = "";
		String sSenddate = "";
		String groupKey = "";
		String rcptKey = "";
		String as[] = null;
		String sTraceId = "";

		BufferedInputStream fis = null;
		try {
			fis = new BufferedInputStream( new FileInputStream(p_sFile) );
			ImMimeHeader ihdrs = new ImMimeHeader();
			ihdrs.parseHeader(fis);
//				InternetHeaders ihdrs = new InternetHeaders(fis);
			String arrInfo = ihdrs.getHeader("X-Sensmail-Info");
			if ( StringUtils.isNotEmpty(arrInfo) ){
				String[] arrSubInfo = ImStringUtil.getTokenizedString(arrInfo, ";");
				if (arrSubInfo != null && (arrSubInfo.length > 0)) {
					sDomain = arrSubInfo[0];
					sMailKey = arrSubInfo[1];

					if(arrSubInfo.length > 2){
						userid = arrSubInfo[2];

						if(arrSubInfo.length > 3){
							ahost = arrSubInfo[3];

							if(arrSubInfo.length > 4){
								tbl_no = arrSubInfo[4];

								if(arrSubInfo.length > 5){
									part_no = arrSubInfo[5];
								}
							}
						}
					}
				}
			}
			sReserveTime = ihdrs.getHeader("X-Sensmail-Reserve");


			sSubject = ihdrs.getHeader("subject");

			String sContentType = ihdrs.getHeader("Content-Type");
			if(sContentType != null){
				try {
					ContentType ct = new ContentType(sContentType);
					if (ct != null) {
						sCharset = ct.getParameter("charset");
					}
				}catch(Exception e){}
			}

			sFrom = ihdrs.getHeader("from");
			sFrom = ImStringUtil.getStringBetween(sFrom, "<", ">");
			String sFromDomain = ImStringUtil.getStringAfter(sFrom, "@");
			if(StringUtils.isEmpty(sDomain)) sDomain = sFromDomain;

			sReciptKey = ihdrs.getHeader("X-Sensmail-Rcptkey");

			sDeliverFrom = ihdrs.getHeader("X-Forwarded-For");

			xmailer = ihdrs.getHeader("X-Mailer");

			sSenddate = ihdrs.getHeader("Date");

			sTraceId = ihdrs.getHeader("X-SensTrace");

			String[] receiveds = ihdrs.getHeaders("Received");
			if( receiveds != null ){
				String fromIP = getMailHeaderSendIp( receiveds );
				sendingInfo.setFromIP( fromIP );

				// 메일 발신자/수신자 추출
				try {
					Matcher m = p.matcher(receiveds[0]);
					if(m.find()) {
						sendingInfo.setRcptto( m.group(2).trim() );
						sendingInfo.setMailfrom( m.group(3).trim() );
					}
				}catch(Exception e) {
					String errorId = ErrorTraceLogger.log(e);
					smtpLogger.error("{} - [ImSmtpUtil.getSendingInfoEx] " , errorId );
				}
			}else{

				String clientIp = ihdrs.getHeader("X-ClientIP");
				if( StringUtils.isNotEmpty(clientIp) ){
					sendingInfo.setFromIP( clientIp );
				}
			}
			// 그룹 키 정보(Base64 encode)
			// ==> 그냥 평문 처리
			groupKey = ihdrs.getHeader(ImSmtpConfig.getInstance().getHeaderGroupKey());
			rcptKey = ihdrs.getHeader(ImSmtpConfig.getInstance().getHeaderRcptKey());
			//if( StringUtils.isNotEmpty(groupKey)) groupKey = ImUtils.decodeBase64(groupKey, "utf-8");

			sendingInfo.setDomain(sDomain);
			sendingInfo.setMailKey(sMailKey);
			sendingInfo.setReserveTime(sReserveTime);
			sendingInfo.setSubject(sSubject);
			sendingInfo.setCharset(sCharset);
			sendingInfo.setReciptKey(sReciptKey);
			sendingInfo.setDeliverFrom(sDeliverFrom);
			sendingInfo.setFrom(sFrom);
			sendingInfo.setUserid(userid);
			sendingInfo.setAhost(ahost);
			sendingInfo.setTbl_no(tbl_no);
			sendingInfo.setPart_no(part_no);
			sendingInfo.setXmailer(xmailer);
			sendingInfo.setSenddate(sSenddate);
			sendingInfo.setTraceid(sTraceId);
			sendingInfo.setGroupKey(groupKey);
			sendingInfo.setRcptKey(rcptKey);
		}  catch (Exception ex){
			String errorId = ErrorTraceLogger.log(ex);
			smtpLogger.error("{} - [ImSmtpUtil.getSendingInfoEx] " , errorId );
		} finally {
			try {
				fis.close();
			} catch (Exception e1) {
			}
		}
		return sendingInfo;
	}

	public static boolean catContentsToFile(String p_sFilePath,
											ImSmtpSendData sd, boolean bAppend) {
		String sQueuePath = sd.getQueueDir() + File.separator + "mess"
				+ File.separator + sd.getQueueFile();
		boolean bContents = false;
		String sBuff = null;
		byte[] bCrLf = "\r\n".getBytes();

		try {
			BufferedOutputStream fos = new BufferedOutputStream(
					new FileOutputStream(p_sFilePath, bAppend) );

			File fp = new File(sQueuePath);
			if (!fp.exists()) {
				sQueuePath = sd.getQueueDir() + File.separator + "rsnd"
						+ File.separator + sd.getQueueFile();
				File fp2 = new File(sQueuePath);
				if (!fp2.exists()) {
					return false;
				}
			}

			BufferedReader bufferedreader = null;
			try {
				bufferedreader = new BufferedReader(new FileReader(sQueuePath));
				while ((sBuff = bufferedreader.readLine()) != null) {
					if (bContents) {
						fos.write(sBuff.getBytes());
						fos.write(bCrLf);
					} else {
						if (sBuff.trim().equals("<<MAIL-DATA>>")) {
							bContents = true;
						}
					}
				}
				// bufferedreader.close();
			} catch (Exception ex) {
				String errorId = ErrorTraceLogger.log(ex);
				smtpLogger.error("{} - [ImSmtpUtil] " , errorId );
				return false;
			} finally {
				try {
					bufferedreader.close();
					fos.close();
				} catch (Exception ex) {
				}
			}

		} catch (Exception e) {
			String errorId = ErrorTraceLogger.log(e);
			smtpLogger.error("{} - [ImSmtpUtil] " , errorId );
			return false;
		}

		return true;
	}

	/**
	 * 큐에 메일을 지운다.
	 * @param p_sQueueDir 큐 경로
	 * @param p_sQueueFile 지울 메일 파일 이름
	 */
	public static void cleanQueue(String p_sQueueDir, String p_sQueueFile) {

		try {
			// mess
			String sQueuePath = p_sQueueDir + File.separator + "mess" + File.separator + p_sQueueFile;
			String sBodyPath = sQueuePath + ".body";
			ImFileUtil.deleteFile(sQueuePath);
			ImFileUtil.deleteFile(sBodyPath);

			// rsnd
			sQueuePath = p_sQueueDir + File.separator + "rsnd" + File.separator + p_sQueueFile;
			sBodyPath = sQueuePath + ".body";
			ImFileUtil.deleteFile(sQueuePath);
			ImFileUtil.deleteFile(sBodyPath);

			// temp
			sQueuePath = p_sQueueDir + File.separator + "temp" + File.separator + p_sQueueFile;
			sBodyPath = sQueuePath + ".body";
			ImFileUtil.deleteFile(sQueuePath);
			ImFileUtil.deleteFile(sBodyPath);

			// slog
			sQueuePath = p_sQueueDir + File.separator + "slog" + File.separator + p_sQueueFile;
			ImFileUtil.deleteFile(sQueuePath);
		} catch (Exception e) {
			String errorId = ErrorTraceLogger.log(e);
			smtpLogger.error("{} - [ImSmtpUtil] " , errorId );

		}
	}

	public static void cleanQueue(String p_sQueuePath) {

		try {
			String sSplit = File.separator + "mess" + File.separator;
			String sQueueDir = "";
			String sQueFileName = "";

			int nPos = p_sQueuePath.indexOf(sSplit);
			if(nPos > 0){
				sQueueDir = p_sQueuePath.substring(0, nPos);
				sQueFileName = p_sQueuePath.substring(nPos + sSplit.length());
			} else {
				sSplit = File.separator + "rsnd" + File.separator;
				nPos = p_sQueuePath.indexOf(sSplit);
				if(nPos > 0){
					sQueueDir = p_sQueuePath.substring(0, nPos);
					sQueFileName = p_sQueuePath.substring(nPos + sSplit.length());
				} else {
					sSplit = File.separator + "rsrv" + File.separator;
					nPos = p_sQueuePath.indexOf(sSplit);
					if(nPos > 0){
						sQueueDir = p_sQueuePath.substring(0, nPos);
						sQueFileName = p_sQueuePath.substring(nPos + sSplit.length());
					}
				}
			}
			cleanQueue(sQueueDir, sQueFileName);
		} catch (Exception e) {
			String errorId = ErrorTraceLogger.log(e);
			smtpLogger.error("{} - [ImSmtpUtil] " , errorId );

		}
	}

	/**
	 * 에러 메시지를 에러 큐로 옮긴다.
	 * @param p_sQueueDir
	 * @param p_sQueueFile
	 */
	public static void moveErrorQueue(String p_sQueueDir, String p_sQueueFile) {

		try {
			String sTargetPath = p_sQueueDir + File.separator + "serr"
					+ File.separator + p_sQueueFile;
			String sTargetBody = sTargetPath + ".body";

			// mess
			String sQueuePath = p_sQueueDir + File.separator + "mess"
					+ File.separator + p_sQueueFile;
			String sBodyPath = sQueuePath + ".body";
			File fp = new File(sQueuePath);
			if (fp.exists()) {
				// info 파일을 옮긴다.
				ImFileUtil.moveFile(sQueuePath, sTargetPath);

				// 본문 파일을 옮긴다.
				fp = new File(sBodyPath);
				if (fp.exists()) {
					ImFileUtil.moveFile(sBodyPath, sTargetBody);
				}

				return;

			}


			// mess 큐에 없다면 rsnd 에서 찾는다.
			sQueuePath = p_sQueueDir + File.separator + "rsnd" + File.separator
					+ p_sQueueFile;
			sBodyPath = sQueuePath + ".body";
			fp = new File(sQueuePath);
			if (fp.exists()) {
				// info 파일을 옮긴다.
				ImFileUtil.moveFile(sQueuePath, sTargetPath);

				// 본문 파일을 옮긴다.
				fp = new File(sBodyPath);
				if (fp.exists()) {
					ImFileUtil.moveFile(sBodyPath, sTargetBody);
				}

				return;
			}

		} catch (Exception e) {
			String errorId = ErrorTraceLogger.log(e);
			smtpLogger.error("{} - [ImSmtpUtil] " , errorId );

		}

	}

	/**
	 * 에러 큐에서 에러 메시지를 지운다..
	 * @param p_sQueueDir
	 * @param p_sQueueFile
	 */
	public static void deleteErrorQueue(String p_sQueueDir, String p_sQueueFile) {

		try {
			// mess
			String sQueuePath = p_sQueueDir + File.separator + "serr"
					+ File.separator + p_sQueueFile;
			String sBodyPath = sQueuePath + ".body";
			File fp = new File(sQueuePath);
			if (fp.exists()) {
				// System.out.println(sQueuePath);
				if (!fp.delete()) {
					// System.out.println("del err");
				}
			}
			fp = new File(sBodyPath);
			if (fp.exists()) {
				if (!fp.delete()) {
					// System.out.println("del err");
				}
			}

			// slog
			sQueuePath = p_sQueueDir + File.separator + "slog" + File.separator
					+ p_sQueueFile;
			fp = new File(sQueuePath);
			if (fp.exists()) {
				// System.out.println(sQueuePath);
				if (!fp.delete()) {
					// System.out.println("del err");
				}
			}
		} catch (Exception e) {
			String errorId = ErrorTraceLogger.log(e);
			smtpLogger.error("{} - [ImSmtpUtil] " , errorId );

		}

	}

	/**
	 * 헤더의 received 값에서 등록된 앞단의 서버 IP를 제외한 최종 IP를 구한다.
	 * @param received
	 * @return
	 */
	public static String getMailHeaderSendIp(String[] received){
		if(received == null){
			return null;
		}
		try{
			int receivedIpLength = received.length;
			for(int i=0; i<receivedIpLength; i++){
				boolean bCheck = true;
				String receivedIp = received[i];
				String[] recvIPs = getIP(receivedIp);
				String checkIP = null;
				if(recvIPs != null && recvIPs.length > 0){
					checkIP = recvIPs[0];
				}
				// 내부 ip , 스팸 ip Skep
//                if(StringUtils.isEmpty(checkIP)){
//                	bCheck = false;
//				}
//                if( bCheck && ( checkIP.startsWith("127.") || checkIP.startsWith("10.") || checkIP.startsWith("172.") || checkIP.startsWith("192.") ) ){
//					bCheck = false;
//                }
//                if( bCheck && ImSensProxyServer.localServerList != null ){
//                    if( ImSensProxyServer.localServerList.contains(checkIP) ){
//						bCheck = false;
//                    }
//                }
//				ImLoggerEx.debug("SMTP","FROM IP :{} - {}",checkIP, bCheck);
				if( !bCheck ){
					continue;
				}
				return checkIP;
			}
			return null;
		}catch(Exception e){
			String errorId = ErrorTraceLogger.log(e);
			smtpLogger.error("{} - FROM IP PARSING ERROR({})",errorId,e.getMessage());
			return null;
		}
	}

	private static String[] getIP(String str){
		Pattern p =
				Pattern.compile("((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[0-9]{1,2})\\.){3}(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[0-9]{1,2})");
		Matcher m = p.matcher(str);

		StringBuffer sb = new StringBuffer();
		while(m.find()){
			sb.append(m.group()+ " ");
		}
		return m.reset().find() ? sb.toString().split(" ") : new String[0];
	}

	public static String sendNotifyReceiverErrorMessage(ImSmtpSendData sd,String sMsgFile,
														String p_sFrom,String p_sRcptTo,String strMsg,String sErrString) {
		String sDefQueuePath = ImSmtpConfig.getInstance().getQueuePath();
		//String sPostmaster = ImSensProxyServer.confSmtp.getProfileString("general", "postmaster");
		String sMailKey = UUIDService.getUID();//ImUtils.makeKey(24);
		//		String sQueueDir = ImMessQueue.createQueueDir(ImUtils.stripDirSlash(sDefQueuePath));
		String sFileName = sMailKey + ".sml";
		File pTemp = null;

		if(StringUtils.isEmpty(p_sFrom) || StringUtils.isEmpty(p_sRcptTo)) {
			return "";
		}

		String[] arrFrom = ImStringUtil.getEmailAddress(p_sFrom);
		String fromEmail = arrFrom[1];
		String fromName = arrFrom[0];
		if(StringUtils.isEmpty(fromName)) {
			fromName = "메일관리자";
		}

		// postmaster
		if (fromEmail.equalsIgnoreCase(p_sRcptTo)) {
			return "";
		}

		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props);
		try {
			//			String sAttName = "";
			String strOriginalMessage = "----- Original message -----";
			//*/
			ImMimeHeader imHdr= new ImMimeHeader();
			imHdr.parseHeader(sMsgFile );
			String sHeader = imHdr.getHeader();

			StringBuffer sbfInput = new StringBuffer();
			String sText = sbfInput
					.append("1. This is an automatically generated received Status Notification (Failure).\r\n\r\n")
					.append("Sender address : ")
					.append(sd.getFrom())
					.append("\r\n")
					.append("To address: ")
					.append(sd.getRcptto())
					.append("\r\n\r\n")
					.append("2. The received status was : \r\n\r\n")
					.append(sErrString)
					.append("\r\n\r\n\r\n")
					.append(strOriginalMessage +"\r\n\r\n")

					.append(sHeader)
					.toString();

			ImMessage message = new ImMessage(session);
			message.setCharset("UTF-8");
			/*if (p_sFrom.trim().equals("")) {
				message.setFrom("PostMaster");
			} else {
				message.setFrom("PostMaster<" + p_sFrom + ">");
			}*/
			message.setFrom(fromName, fromEmail, "UTF-8");

			message.setTo(p_sRcptTo);
			message.setSubject("Received Status Notification : "+strMsg, "UTF-8");
			message.setHeader("X-NOTIFY-ERR", fromEmail);
			message.setHeader("X-SensTrace", sd.getTraceID());
			message.setText(sText,"UTF-8");
			// message.setHtml(sText);

			// 원본을 첨부하는 대신 헤더만 본문에 같이 넣어준다.

			String mimeFilePath = ImUtils.stripDirSlash(sDefQueuePath) + File.separator + "temp" + File.separator + sFileName + ".body";
			message.makeMimeFile(mimeFilePath);
			//String sMime = message.makeMimeData();

			ImSmtpSendData issd = new ImSmtpSendData();
			issd.setMsgID(sMailKey);
			issd.setTraceID(sd.getTraceID());
			issd.setFrom(ImStringUtil.getStringBetween(fromEmail, "<", ">"));
			issd.setRcptto(ImStringUtil.getStringBetween(p_sRcptTo, "<",">"));
			issd.setPeerIP("127.0.0.1");
			issd.setIsRelay(1);
			issd.setSubject("Received Status Notification : "+strMsg);
			issd.setDSN(true);


			/*
			String[] sSendingInfo = new String[5];
			sSendingInfo[0] = "";
			sSendingInfo[1] = sMailKey;
			sSendingInfo[2] = "";
			sSendingInfo[3] = "Delivery Status Notification (Failure)";
			sSendingInfo[4] = "";

			// 데이터베이스에 메일 정보를 입력한다.
			smtpDAO.addMailQueue(sMailKey,sFileName,sQueueDir,sSendingInfo,
                    ImStringUtil.getStringBetween(p_sRcptTo, "<",">"),
                    "127.0.0.1",ImStringUtil.getStringBetween(p_sFrom, "<", ">"));
			 */

			addQueue2( issd, "", sMailKey);

			pTemp = new File(mimeFilePath);
			// addQueue(bq, sContents, message.makeMimeData(),
			// sDefQueuePath);
		} catch (Exception e) {
			String errorId = ErrorTraceLogger.log(e);
			smailLogger.error("{} - [ImSmtpUtil] " , errorId );
		}finally{
			if(pTemp != null)
				pTemp.delete();
		}

		return sErrString;
	}

	/**
	 * true이면 차단아이피, false이면 정상
	 */
	public static boolean isDenyIp(ImSmtpSession smtps){
		boolean bRet = false;
		String sInIP = smtps.getPeerIP();
		String sWhereIp = "";

		try{
			SmtpRepository smtpDatabaseService = SmtpRepository.getInstance();
			List<DenyIp> denyIpList = smtpDatabaseService.getDenyIpList();

//            String[] arrInIP = ImStringUtil.getTokenizedString(sInIP, ".");
//            if(arrInIP.length > 1){
//                sWhereIp = arrInIP[0] + "." + arrInIP[1];
//            }

			if(denyIpList == null) {
				smtpLogger.trace("[{}] ImSmtpServerHandler checkDenyIP is NULL", smtps.getTraceID());
				return false;
			}

			for(DenyIp denyIp : denyIpList){
				if(ImIpUtil.matchIPbyCIDR(denyIp.getIp(), smtps.getPeerIP())){
					bRet = true;
					break;
				}
			}
		}catch(Exception ex){
			String errorId = ErrorTraceLogger.log(ex);
			smtpLogger.error("{} - [checkDenyIP] Check Deny IP (" + sInIP +")", errorId );
			bRet = false;
		}
		return bRet;
	}

	public static boolean isDenyIp(String ip){
		boolean bRet = false;
		String sWhereIp = "";

		try{
			SmtpRepository smtpDatabaseService = SmtpRepository.getInstance();
			List<DenyIp> denyIpList = smtpDatabaseService.getDenyIpList();

			if(denyIpList == null) {
				smtpLogger.trace("checkDenyIP is NULL");
				return false;
			}

			for(DenyIp denyIp : denyIpList){
				if(ImIpUtil.matchIPbyCIDR(denyIp.getIp(), ip)){
					bRet = true;
					break;
				}
			}
		}catch(Exception ex){
			String errorId = ErrorTraceLogger.log(ex);
			smtpLogger.error("{} - [checkDenyIP] Check Deny IP (" + ip +")", errorId );
			bRet = false;
		}
		return bRet;
	}

	public static boolean isRelayIP(ImSmtpSession smtps){
		boolean bRet = false;
		try{
			SmtpRepository smtpDatabaseService = SmtpRepository.getInstance();
			List<RelayIp> relayIpList = smtpDatabaseService.getRelayIpList();

			if(relayIpList == null) {
				smtpLogger.trace("[{}] ImSmtpServerHandler checkRelayIP is NULL", smtps.getTraceID());
				return false;
			}

			for(RelayIp relayIp : relayIpList){
				if(ImIpUtil.matchIPbyCIDR(relayIp.getIp(), smtps.getPeerIP())){
					// 발신서버 그룹키
//					if(StringUtils.isEmpty(smtps.getSender_group())) smtps.setSender_group(relayIp.getSender_group());
					bRet = true;
					break;
				}
			}
		}catch(Exception ex){
			String errorId = ErrorTraceLogger.log(ex);
			smtpLogger.error("{} - [checkRelayIP] Check IP (" + smtps.getPeerIP() +")", errorId );
			bRet = false;
		}

		return bRet;
	}


	public static void doTransmitLog(TransmitLogger transmitLogger, ImSmtpSession smtps){
		if(transmitLogger == null) return;

		transmitLogger.setServerid(ImSmtpConfig.getInstance().getServerID());
		transmitLogger.setTraceid(smtps.getTraceID());
		transmitLogger.setWork(TransmitLogger.WORK_RECEIVE);
		if(StringUtils.isNotEmpty(smtps.getFromIP())){
			transmitLogger.setIp(smtps.getFromIP());
		} else {
			transmitLogger.setIp(smtps.getPeerIP());
		}
		// 발신서버 그룹키
//		if(transmitLogger.getSender_group() == null) {
//			if(StringUtils.isNotEmpty(smtps.getSender_group())){
//				transmitLogger.setSender_group(smtps.getSender_group());
//			}
//		}
		if(transmitLogger.getAuthid() == null) transmitLogger.setAuthid(smtps.getLogonUser());
		transmitLogger.setFrom(smtps.getFrom());
		if(transmitLogger.getSize() == null) transmitLogger.setSize(smtps.getMsgSize());

		transmitLogger.info();
	}
	public static void doTransmitLogIns(TransmitLogger transmitLogger, ImSmtpSession smtps){
		if(transmitLogger == null) return;

		transmitLogger.setServerid(ImSmtpConfig.getInstance().getServerID());
		transmitLogger.setTraceid(smtps.getTraceID());
		transmitLogger.setWork(TransmitLogger.WORK_RECEIVE);
		if(StringUtils.isNotEmpty(smtps.getFromIP())){
			transmitLogger.setIp(smtps.getFromIP());
		} else {
			transmitLogger.setIp(smtps.getPeerIP());
		}
		// 발신서버 그룹키
//		if(transmitLogger.getSender_group() == null) {
//			if(StringUtils.isNotEmpty(smtps.getSender_group())){
//				transmitLogger.setSender_group(smtps.getSender_group());
//			}
//		}
		if(transmitLogger.getAuthid() == null) transmitLogger.setAuthid(smtps.getLogonUser());
		transmitLogger.setFrom(smtps.getFrom());
		if(transmitLogger.getSize() == null) transmitLogger.setSize(smtps.getMsgSize());
		transmitLogger.setResultState(TransmitLogger.STATE_ING);
		transmitLogger.info();

		try {
			SmtpRepository dbService = SmtpRepository.getInstance();
			TransmitDataLogMonitoringService transmitDataLogMonitoringService = new TransmitDataLogMonitoringService();
			TransmitStatisticsData data = transmitDataLogMonitoringService.parseTransmitLog(transmitLogger, new HashMap<String,String>());
			dbService.insertTransmitLogData(data);
		} catch (Exception e) {
			smtpLogger.error("ImSmtpUtil.doTransmitLogIns error: {}", e.toString());
		}
		smtpLogger.info("ImSmtpUtil.doTransmitLogIns OK");
	}
	public static void doTransmitLog(TransmitLogger transmitLogger, ImSmtpSendData sd){
		if(transmitLogger == null) return;

		transmitLogger.setServerid(ImSmtpConfig.getInstance().getServerID());
		transmitLogger.setTraceid(sd.getTraceID());
		transmitLogger.setOrg_traceid(sd.getOrgTraceID());
		transmitLogger.setWork(TransmitLogger.WORK_DELIVERY);
		transmitLogger.setIp(sd.getPeerIP());
		transmitLogger.setAuthid(sd.getLogonUser());
		transmitLogger.setFrom(sd.getFrom());
		transmitLogger.setTo(sd.getRcptto());
		if(StringUtils.isNotEmpty(sd.getSubject())) transmitLogger.setSubject(sd.getSubject());
		if(transmitLogger.getSize() == null) transmitLogger.setSize(sd.getMailsize());
		transmitLogger.setDomain(sd.getDomain());
		if(transmitLogger.getGroupkey() == null) transmitLogger.setGroupkey(sd.getGroupKey());
		if(transmitLogger.getRcptkey() == null) transmitLogger.setRcptkey(sd.getRcptKey());
		transmitLogger.info();
	}
}

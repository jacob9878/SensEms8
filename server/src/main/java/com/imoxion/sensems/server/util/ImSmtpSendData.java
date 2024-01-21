/**
 * 
 * @author: OYoung, Kwon<realkoy@imoxion.com>
 *
 * SmtpServer
 * 
 */
package com.imoxion.sensems.server.util;

import com.imoxion.common.util.ImStringUtil;
import com.imoxion.sensems.server.logger.ErrorTraceLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Serializable;

/**
 * @author realkoy
 *
 *
 */
public class ImSmtpSendData implements Serializable {

	private Logger smtpLogger = LoggerFactory.getLogger("SMTP");
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String m_sOrgTraceID;
	private String m_sTraceID;
	private String m_sMsgID = "";
	private String m_sFrom = "";
	private String m_sOriginFrom = "";
	private String m_sRcptto = "";
	private String m_sQueueDir = "";
	private String m_sQueFileName = "";
	private String m_sBodyFileName = "";
	private String m_sLastTry = "0";
	private String m_sPeerIP = "";
	private String m_sDomain = "";
	private String m_sMailKey = "";
	private String m_sMailingList = "";
	private String m_sMailBoxKey = "";

	private int m_nTryNumbers = 0;
	private int m_nIsRelay = 0;
	private String m_sLogonUser = "";
	private String m_sLogonDomain = "";

	private String logonEmail = "";

	/*
	 * Author : jungyc
	 * Date : 2007.03.15
	 * Comment : 내부 메일에서 발신된 메일과 외부에서 발신된 메일을 구분하기 위한 변수 추가
	 */
	private String m_sLocalMsgid = "";	// 내부 메시지 아이디
	private String m_sLocalDomain = "";	// 내부 도메인
	private String m_sLocalUserid = "";	// 내부 사용자 아이디
	
	private boolean bulk = false;
	private String receiptKey = null;
	private String userid = null;
	private String ahost = null;
	private String tbl_no = null;
	private String part_no = null;
	private boolean redirect = false;
	private Integer reserv_no;
	private String m_sReserveTime = null;

	private String xmailer = "";
	
	/// 확장용도..
	private String mailUkey = "";
	private String dirKey = "";
	private long mailsize = 0;
	private String subject;
	private String senddate;
	private String fromIP;
	private String headerFrom = "";
	private boolean isBccAudit = false;

	private boolean isJournalSend = false;
	// 발송실패 리턴메일인지 여부
	private boolean isDSN = false;

	// 수신자 수(동보일때)
	private int rcptCount = 1;
	// from이 로컬주소인지 여부
	private boolean m_bFromLocalUser = false;

	private int sendResultCode = 0;

	private String sendResultErrMsg = "";

	// 동보메일 그룹키
	private String groupKey = null;

	// 수신자별 고유키
	private String rcptKey = null;

	// 현재 재발송인지 여부
	private boolean retryNow = false;

	/** 발송 구분 - T: 테스트메일, D: DB연동외부메일(WEB API이용), A: Smtp 인증, R: 릴레이연동, C:건별재발신 **/
	private String send_type;

	public boolean isRetryNow() {
		return retryNow;
	}

	public void setRetryNow(boolean retryNow) {
		this.retryNow = retryNow;
	}


	public String getRcptKey() {
		return rcptKey;
	}

	public void setRcptKey(String rcptKey) {
		this.rcptKey = rcptKey;
	}

	public String getGroupKey() {
		return groupKey;
	}

	public void setGroupKey(String groupKey) {
		this.groupKey = groupKey;
	}

	public int getRcptCount() {
		return rcptCount;
	}

	public void setRcptCount(int rcptCount) {
		this.rcptCount = rcptCount;
	}

	public boolean isBccAudit() {
		return isBccAudit;
	}

	public void setBccAudit(boolean bccAudit) {
		isBccAudit = bccAudit;
	}

	public boolean isDSN() {
		return isDSN;
	}

	public void setDSN(boolean DSN) {
		isDSN = DSN;
	}
	public boolean isJournalSend() {
		return isJournalSend;
	}

	public void setJournalSend(boolean journalSend) {
		isJournalSend = journalSend;
	}

	public int getSendResultCode() {
		return sendResultCode;
	}

	public void setSendResultCode(int sendResultCode) {
		this.sendResultCode = sendResultCode;
	}

	public String getSendResultErrMsg() {
		return sendResultErrMsg;
	}

	public void setSendResultErrMsg(String sendResultErrMsg) {
		this.sendResultErrMsg = sendResultErrMsg;
	}

	public String getAhost() {
		return ahost;
	}

	public void setAhost(String ahost) {
		this.ahost = ahost;
	}

	public long getMailsize() {
		return mailsize;
	}

	public void setMailsize(
		long mailsize) {
		this.mailsize = mailsize;
	}

	public String getMailUkey() {
        return mailUkey;
    }

    public void setMailUkey(String mailUkey) {
        if (mailUkey != null)
            this.mailUkey = mailUkey;
    }
    
    public boolean isFromLocalUser(){
		return m_bFromLocalUser;
	}
	public void setFromLocalUser(boolean isFromLocalUser){
		m_bFromLocalUser = isFromLocalUser;
	}

    public String getLogonUser() {
		return m_sLogonUser;
	}

	public void setLogonUser(String m_sLogonUser) {
		this.m_sLogonUser = m_sLogonUser;
	}

	public String getLogonDomain() {
		return m_sLogonDomain;
	}

	public void setLogonDomain(String m_sLogonDomain) {
		this.m_sLogonDomain = m_sLogonDomain;
	}

	public String getDirKey() {
        return dirKey;
    }

    public void setDirKey(String dirKey) {
        if (dirKey != null)
            this.dirKey = dirKey;
    }

	public boolean isRedirect() {
		return redirect;
	}
	public void setRedirect(boolean redirect) {
		this.redirect = redirect;
	}
	public boolean isBulk() {
		return bulk;
	}
	public void setBulk(boolean bulk) {
		this.bulk = bulk;
	}
	public String getLocalDomain() {
		return m_sLocalDomain;
	}
	public void setLocalDomain(String localDomain) {
		if (localDomain != null)
			m_sLocalDomain = localDomain;
	}

	public String getLocalMsgid() {
		return m_sLocalMsgid;
	}
	public void setLocalMsgid(String localMsgid) {
		if (localMsgid != null)
			m_sLocalMsgid = localMsgid;
	}

	public String getLocalUserid() {
		return m_sLocalUserid;
	}
	public void setLocalUserid(String localUserid) {
		if (localUserid != null)
			m_sLocalUserid = localUserid;
	}

	public String getTraceID() {
		return m_sTraceID;
	}

	public void setTraceID(String m_sTraceID) {
		this.m_sTraceID = m_sTraceID;
	}

	public String getFrom() { return m_sFrom; }
	public void setFrom(String p_sFrom) { m_sFrom = p_sFrom; }

	public String getOriginFrom() { return m_sOriginFrom; }
	public void setOriginFrom(String p_sOriginFrom) { m_sOriginFrom = p_sOriginFrom; }
		
	public String getPeerIP() { return m_sPeerIP; }
	public void setPeerIP(String p_sPeerIP){ m_sPeerIP = p_sPeerIP; }
	
	public String getDomain() { return m_sDomain; }
	public void setDomain(String p_sDomain){ m_sDomain = p_sDomain; }
	
	public String getMailKey() { return m_sMailKey; }
	public void setMailKey(String p_sMailKey){ m_sMailKey = p_sMailKey; }
	
	public String getMsgID() { return m_sMsgID; }
	public void setMsgID(String p_sMsgid) { m_sMsgID = p_sMsgid; }
	
	public String getRcptto() { return m_sRcptto; }
	public void setRcptto(String p_sRcptto) { m_sRcptto = p_sRcptto; }
	
	public String getMList() { return m_sMailingList; }
	public void setMList(String p_sMList) { m_sMailingList = p_sMList; }
	
	public void setQueuePath(String p_sQueuePath) {
		splitQueuePath(p_sQueuePath);
	}
	public String getQueueDir() { return m_sQueueDir; }
	public void setQueueDir(String p_sQueueDir) { m_sQueueDir = p_sQueueDir; }
	
	public String getQueueFile() { return m_sQueFileName; }
	public void setQueueFile(String p_sQueFileName) { m_sQueFileName = p_sQueFileName; }
	
	public String getQueueBodyFile() { return m_sBodyFileName; }
	public void setQueueBodyFile(String p_sQueBodyFileName) { m_sBodyFileName = p_sQueBodyFileName; }
	
	public String getLastTryTime() { return m_sLastTry; }
	public void setLastTryTime(String p_sLastTry) { m_sLastTry = p_sLastTry; }
	
	public int	getTryNumbers() { return m_nTryNumbers; }
	
	
	public int getIsRelay() {
		return m_nIsRelay;
	}
	public void setIsRelay(int isRelay) {
		m_nIsRelay = isRelay;
	}

	public String getLogonEmail() {
		return logonEmail;
	}

	public void setLogonEmail(String logonEmail) {
		this.logonEmail = logonEmail;
	}

	public String getSend_type() {
		return send_type;
	}

	public void setSend_type(String send_type) {
		this.send_type = send_type;
	}

	public ImSmtpSendData(){}
	
	public ImSmtpSendData(String p_sQueuePath){
		splitQueuePath(p_sQueuePath);
	}
	
	public ImSmtpSendData(String p_sQueueDir, String p_sFileName){
		m_sQueueDir = p_sQueueDir;
		m_sQueFileName = p_sFileName;
		m_sBodyFileName = m_sQueFileName + ".body";
	}
	
	
	public void increaseTryNumbers() { m_nTryNumbers++; }
	public void decreaseTryNumbers() { 
		if(m_nTryNumbers > 0) {
			m_nTryNumbers--;
		} else {
			m_nTryNumbers = 0;
		}
	}
	
	public void splitQueuePath(String p_sQueuePath){
		try{
			String sSplit = File.separator + "mess" + File.separator;
			//String[] arrQueue = ImStringHandler.getTokenizedString(p_sQueuePath, sSplit);
			int nPos = p_sQueuePath.indexOf(sSplit);
			if(nPos > 0){
				m_sQueueDir = p_sQueuePath.substring(0, nPos);
				m_sQueFileName = p_sQueuePath.substring(nPos + sSplit.length());
				m_sBodyFileName = m_sQueFileName + ".body";
			} else {
				sSplit = File.separator + "rsnd" + File.separator;
				nPos = p_sQueuePath.indexOf(sSplit);
				if(nPos > 0){
					m_sQueueDir = p_sQueuePath.substring(0, nPos);
					m_sQueFileName = p_sQueuePath.substring(nPos + sSplit.length());
					m_sBodyFileName = m_sQueFileName + ".body";
				} else {
					sSplit = File.separator + "rsrv" + File.separator;
					nPos = p_sQueuePath.indexOf(sSplit);
					if(nPos > 0){
						m_sQueueDir = p_sQueuePath.substring(0, nPos);
						m_sQueFileName = p_sQueuePath.substring(nPos + sSplit.length());
						m_sBodyFileName = m_sQueFileName + ".body";
					} 
				}
			}
		} catch (Exception e){
    		String errorId = ErrorTraceLogger.log(e);
    		smtpLogger.error("[{}] {} - [ImSmtpSendData]   " , m_sTraceID, errorId );

		}
	}
	
	public String getContentsFile() { 
		
		String sQueuePath = m_sQueueDir + File.separator + "mess" + File.separator + m_sBodyFileName;
		
		try{
			File fp = new File(sQueuePath);
        	if(!fp.exists()){
        		sQueuePath = m_sQueueDir + File.separator + "rsnd" + File.separator + m_sBodyFileName;
        		File fp2 = new File(sQueuePath);
        		if(!fp2.exists()){
        			return "";
        		}
        	}

        	return sQueuePath;
		}catch (Exception ex){
    		String errorId = ErrorTraceLogger.log(ex);
			smtpLogger.error("[{}] {} - [ImSmtpSendData]   " , m_sTraceID, errorId );
			return "";
		}
	}
	/*
	public String getContents() { 
		
		String sQueuePath = m_sQueueDir + File.separator + "mess" + File.separator + m_sBodyFileName;
		
		try{
			File fp = new File(sQueuePath);
        	if(!fp.exists()){
        		sQueuePath = m_sQueueDir + File.separator + "rsnd" + File.separator + m_sBodyFileName;
        		File fp2 = new File(sQueuePath);
        		if(!fp2.exists()){
        			return "";
        		}
        	}

        	return ImFileUtil.readFile(sQueuePath);
		}catch (Exception ex){
			return "";
		}
	}
	
	
	
	public void addSendInfo(String p_sInfo) {
		String sQueuePath = getQueueDir() + File.separator + "mess" + File.separator + getQueueFile();
		try {
			BufferedOutputStream fos = null;
			try{
				fos = new BufferedOutputStream( new FileOutputStream(sQueuePath, true) );
				fos.write(p_sInfo.getBytes());
			} catch(Exception ex) {
				//
			} finally {
				try{
					fos.close();
				} catch(Exception e) {}
			}
		} catch (Exception ex) {
			//
		}
	}*/
	
	// 
	public void updateRetryInfo(String p_sSlogPath){
		File fp = new File(p_sSlogPath);
    	if(!fp.exists()){
    		return;
    	}
    	
    	m_nTryNumbers = 0;
    	BufferedReader bufferedreader = null;
        try {
        	String str = null;
            bufferedreader = new BufferedReader(new FileReader(p_sSlogPath));
            while((str = bufferedreader.readLine()) != null){
            	if( str.indexOf("[PeekTime]") >= 0 ){
    				increaseTryNumbers();
    			
    				String[] arrStr = ImStringUtil.getTokenizedString(str, " ");
    	    		if(arrStr.length < 2){
    	    			return;
    	    		}
    	    		m_sLastTry = arrStr[1];
            	}
            }
        } catch (Exception ee){
    		String errorId = ErrorTraceLogger.log(ee);
			smtpLogger.error("[{}] {} - [ImSmtpSendData]   " , m_sTraceID, errorId );
        } finally {
            try { bufferedreader.close(); } catch(Exception e){}
        }
    	
    	/*
		ArrayList<String> arraylist = ImFileUtil.readFileByLine(p_sSlogPath);
		String sBuff = "";
		
		m_nTryNumbers = 0;
		
		for(int i=0; i<arraylist.size();i++){
			sBuff = arraylist.get(i).toString();
			
			if( (ImStringUtil.strINComp(sBuff, "[PeekTime]")) ){
				increaseTryNumbers();
			
				String[] arrStr = ImStringUtil.getTokenizedString(sBuff, " ");
	    		if(arrStr.length < 2){
	    			return;
	    		}
	    		m_sLastTry = arrStr[1];
        	}
		}*/
	}
	/*
	public boolean getSendDataInfo(String sQueuePath){
		ObjectInputStream in = null;
		ImSmtpSendData issd = null;
		
		File fp = new File(sQueuePath);
    	if(!fp.exists()){
    		return false;
    	}
    	
    	splitQueuePath(sQueuePath);
    	
		try{
			in = new ObjectInputStream(new FileInputStream(fp));
			
			issd = (ImSmtpSendData)in.readObject();
			
			// slog
			String sSlogFile = m_sQueueDir + File.separator + "slog" + File.separator + m_sQueFileName;
			getRetryInfo(sSlogFile);
		}catch(Exception ex){
			System.out.println(ex);
		}finally{
			try{
				if(in != null) in.close();
				fp.delete();
			}catch(Exception fileex){}
		}

		return true;
	}*/

	/**
	 * @return the m_sMailBoxKey
	 */
	public String getMailBoxKey() {
		return m_sMailBoxKey;
	}
	/**
	 * @param sMailBoxKey the m_sMailBoxKey to set
	 */
	public void setMailBoxKey(String sMailBoxKey) {
		this.m_sMailBoxKey = sMailBoxKey;
	}
	/**
	 * @return the receiptKey
	 */
	public String getReceiptKey() {
		return receiptKey;
	}
	/**
	 * @param receiptKey the receiptKey to set
	 */
	public void setReceiptKey(String receiptKey) {
		this.receiptKey = receiptKey;
	}
	/**
	 * @return the userid
	 */
	public String getUserid() {
		return userid;
	}
	/**
	 * @param userid the userid to set
	 */
	public void setUserid(String userid) {
		this.userid = userid;
	}
	/**
	 * @return the tbl_no
	 */
	public String getTbl_no() {
		return tbl_no;
	}
	/**
	 * @param tbl_no the tbl_no to set
	 */
	public void setTbl_no(String tbl_no) {
		this.tbl_no = tbl_no;
	}
	/**
	 * @return the part_no
	 */
	public String getPart_no() {
		return part_no;
	}
	/**
	 * @param part_no the part_no to set
	 */
	public void setPart_no(String part_no) {
		this.part_no = part_no;
	}
	/**
	 * @return the xmailer
	 */
	public String getXmailer() {
		return xmailer;
	}
	/**
	 * @param xmailer the xmailer to set
	 */
	public void setXmailer(String xmailer) {
		this.xmailer = xmailer;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getSenddate() {
		return senddate;
	}

	public void setSenddate(String senddate) {
		this.senddate = senddate;
	}

	public String getFromIP() {
		return fromIP;
	}

	public void setFromIP(String fromIP) {
		this.fromIP = fromIP;
	}

	public void setHeaderFrom(String headerFrom) { this.headerFrom = headerFrom; }
	public String getHeaderFrom(){ return this.headerFrom; }

	public Integer getReserv_no() {
		return reserv_no;
	}

	public void setReserv_no(Integer reserv_no) {
		this.reserv_no = reserv_no;
	}
	
	public String getOrgTraceID() {
		return m_sOrgTraceID;
	}

	public void setOrgTraceID(String m_sOrgTraceID) {
		this.m_sOrgTraceID = m_sOrgTraceID;
	}
	public String getReserveTime() { return m_sReserveTime; }
	public void setReserveTime(String p_sReserveTime){ m_sReserveTime = p_sReserveTime; }
}

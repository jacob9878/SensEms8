/*
 * �wα׷��� : ImSmtpConfig.java
 * �ۼ��� : d����
 * Email : jungyc@imoxion.com
 * �Ҽ� : (��) ���̸�� ���߽�
 * �ۼ��� ��¥: 2006. 2. 23
 *
 * �wα׷� ����
 * 
 */
package com.imoxion.sensems.server.config;

import com.imoxion.common.util.ImConfLoaderEx;
import com.imoxion.common.util.ImConvertUtil;
import com.imoxion.sensems.server.environment.SensEmsEnvironment;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;


public class ImSmtpConfig {
	private Logger smtpLogger = LoggerFactory.getLogger("SMTP");
	public static final int DEFAULT_SMTP_PORT = 25;
	public static final int DEFAULT_SMTP_SSL_PORT = 465;
	public static final String SMTP_SERVER_NAME = "imoxion SensMail";

	public static final String HEADER_SEND_TYPE = "X-SNDTYPE";

	private String m_sDnsServer = "";
	private String m_sHeloHost = "";
	private String m_sRootDomain = "";
	private String m_sQueuePath = "";
	private String m_sTempDir = "/tmp";
	private int m_nSmtpPortIn = 25;
	private int m_nSmtpPortOut = 25;

	private int m_nMaxSendTh = 20;
	private int m_nReSendTh = 1;
	private int m_nReserveSendTh = 1;
	private int m_nDefaultConnection = 16;
	private int m_nMaxConnection = 64;
	private int m_nMaxRetry = 3;
	private int m_nRetryInterval = 10 * 1000 * 60;
	private int m_nConnTime = 60000;
	private int m_nRWTime = 60000;
	private int m_nMaxRcpt = 100;
	private String m_sMaxRcptErrors = "";
	private int m_nMaxRsetCount = 100;

	private long m_lMaxMsgSize = 20971520; //20MB
	private long m_lTotMaxMsgSize = 1024 * 1024 * 1024;
	private String m_sMaxMsgSizeErrors = "";

	private int m_nMaxMsgLoops = 16;

	private int m_nIsCheckFromDom = 0;
	// from 주소 null 허용 여부
    private int m_nAllowNullFrom = 0;

	// relay to domain
	private String m_sRelayToDomain = "";

	// SSL
	private String m_sCertPath = "";
	private String m_sCertPass = "";
	private String m_sSensmailHome = "";
	private String m_sDefaultDomain = "";
	private boolean useTLS = false;
	private String postmaster;

	// db type
	private int m_nDbType = 2;
	private int isBlockNotRelay = 0;
	private String FromMobile = "";
	private String absent_subject = "";

	// 동시접속 수 제한없음
	private String m_sMaxConcurrentConnectNoLimit = "127.0.0.1";
	private int m_nMaxConcurrentConnect = 0;

	// 비정상 접속에 대한 응답 지연
	private int useTarpit = 2000;

	// smtp 인증아이디와 보내는메일주소가 다를때 처리 방법(1이면 from주소와 logon user가 다르면 거부)
	private int m_nStrictFromUser = 1;

	private int maxBlukThread = 8;

	private int isForceAuth = 0;

	private int useFileDB = 1;

	private boolean useJournaling = false;

	private String journalingSpoolPath = null;

	/**
	 * 망연계 디렉토리
	 */
	private String netLinkSpoolPath = null;

    /**
     * 게이트웨이 기능을 사용할것인가?
     */
    private boolean useGateway = false;

    /**
     * 게이트웨이 smtp 가 아니고 망연계용 디렉토리에 eml을 저장함
     * gatewayServerIp 또는 gatewaySendPath 둘중 하나만 설정해야 함
     */
    private String gatewaySendPath = null;

    /**
     * 게이트웨이를 이용한 메일 발송 IP 목록
     * gatewayServerIp 또는 gatewaySendPath 둘중 하나만 설정해야 함
     */
    private List<String> gatewayServerIp = new ArrayList<>();
    /**
     * 게이트웨이를 이용한 메일발송시 제외할 IP 목록
     */
    private List<String> exceptGatewaysendIpList = new ArrayList<>();


    /**
     * 게이트웨이 기능을 사용할 때 내부간 메일도 적용할것인가?
     */
    private boolean useGatewayForLocalMail = false;

	/**
	 * ylmf-pc brute force attack
	 * EHLO 명령에 ylmf-pc로 무작위시도를 하는 것에 대한 차단 여부
	 */
	private int ylmfpcBlock = 1;

	/**
	 * Dmarc/dkim/spf 체크후 헤더에 결과를 기록함
	 */
	private boolean useDmarcCheck = true;

//	private Map<String, ProxyDomain> proxyDomainMap = new HashMap<>();
//
//	private List<String> relayIpList;
//
//	private List<String> denyIpList;
//
//	// smtp인증용 사용자 정보
//	private Map<String, String> userMap = new HashMap<>();

	private List<String> localIpList;

	private int m_nSuccessLog = 0;

	private String productName;

	private int m_nSmtpAuthIpFailCount = 5;

	private int m_nIsLogConn = 0;

	private int m_nIsUseIsp =0;
	private int m_nIspPort = 587;
	private int m_nUseIspAuth = 1;

	private String serverID = "0";

	private int queueSize = 1;

	private String m_sBlockCountry;

	private List<String> excludeTlsCipherList = new ArrayList<>();

	private boolean useSSL = false;
	private int m_nSslPort = DEFAULT_SMTP_SSL_PORT;

	private List<String> listLogIgnoreIP = new ArrayList<>();

	// 로컬 send_host
	private String sendHost = "localhost";
	private String[] sendHostArray;

	// smtp auth 기능 사용여부
	private boolean useSmtpAuth = true;

	private int m_nRemoveQuotes = 0;

	private List<String> spamServerList;

	private String geoipPath;
	private String geoipDatabase;

	// 발송실패 알림 메일
	private boolean useDsn = true;
	// db사용 여부
	private boolean useDB = true;

	//
	private int transmitLogDeleteDays = 7;

	// 동보 그룹 메일 발송 시 그룹키
	private String headerGroupKey;

	// 수신자별 고유키
	private String headerRcptKey;

	// 기본 언어 설정
	private String defaultLang = "ko";

	/**
	 * smtp 서버가 릴레이하면서 수신확인 코드를 삽입해 줄지 여부
	 */
	private boolean useReceiptNotify = false;

	private String webUrl;
	private String receiptNotifyUrl;

	private boolean test = false;
	private String encrypt_key;

	private int dbSendAgentInterval = 60;


	// 발신할때 mail from 의 도메인을 default domain 으로 처리할지 여부
	// 이메일아이디+rel=이메일도메인@디폴트도메인
	private boolean isSendDomainProxy = false;

	///////////////////=========================
	private static ImSmtpConfig smtpConfig;

	public static ImSmtpConfig getInstance(){
		if( smtpConfig == null ){
			smtpConfig = new ImSmtpConfig();
		}
		return smtpConfig;
	}

	private ImSmtpConfig(){
		load();
	}
	public void clear(){
		smtpConfig = null;
	}


	public String getWebUrl() {
		return webUrl;
	}

	public void setWebUrl(String webUrl) {
		this.webUrl = webUrl;
	}

	public int getDbSendAgentInterval() {
		return dbSendAgentInterval;
	}

	public void setDbSendAgentInterval(int dbSendAgentInterval) {
		this.dbSendAgentInterval = dbSendAgentInterval;
	}

	public String getSendHost() {
		return sendHost;
	}

	public void setSendHost(String sendHost) {
		this.sendHost = sendHost;
		this.sendHostArray = sendHost.split(",");
	}

	public String[] getSendHostArray() {
		return sendHostArray;
	}

	public void setSendHostArray(String[] sendHostArray) {
		this.sendHostArray = sendHostArray;
	}

	public String getDefaultLang() {
		return defaultLang;
	}

	public void setDefaultLang(String defaultLang) {
		this.defaultLang = defaultLang;
	}

	public String getHeaderGroupKey() {
		return headerGroupKey;
	}

	public void setHeaderGroupKey(String headerGroupKey) {
		this.headerGroupKey = headerGroupKey;
	}

	public String getHeaderRcptKey() {
		return headerRcptKey;
	}

	public void setHeaderRcptKey(String headerRcptKey) {
		this.headerRcptKey = headerRcptKey;
	}

	public int getTransmitLogDeleteDays() {
		return transmitLogDeleteDays;
	}

	public void setTransmitLogDeleteDays(int transmitLogDeleteDays) {
		this.transmitLogDeleteDays = transmitLogDeleteDays;
	}

	public boolean isUseDB() {
		return useDB;
	}

	public void setUseDB(boolean useDB) {
		this.useDB = useDB;
	}

	public String getPostmaster() {
		return postmaster;
	}

	public void setPostmaster(String postmaster) {
		this.postmaster = postmaster;
	}

	public int getUseIspAuth() {
		return m_nUseIspAuth;
	}

	public void setUseIspAuth(int m_nUseIspAuth) {
		this.m_nUseIspAuth = m_nUseIspAuth;
	}

	public String getServerID() {
		return serverID;
	}

	public void setServerID(String serverID) {
		this.serverID = serverID;
	}

	public boolean isSendDomainProxy() {
		return isSendDomainProxy;
	}

	public void setSendDomainProxy(boolean isSendDomainProxy) {
		this.isSendDomainProxy = isSendDomainProxy;
	}

	public int getUseTarpit(){ return useTarpit; }
	public void setUseTarpit(int tarpit){ useTarpit = tarpit; }

	public boolean isUseDsn() {
		return useDsn;
	}

	public void setUseDsn(boolean useDsn) {
		this.useDsn = useDsn;
	}

	//	public int getIsCheckSmtp2() {
//		return m_nIsCheckSmtp2;
//	}
//
//	public void setIsCheckSmtp2(int m_nIsCheckSmtp2) {
//		this.m_nIsCheckSmtp2 = m_nIsCheckSmtp2;
//	}
//
//	public int getCheckPort2() {
//		return m_nCheckPort2;
//	}
//
//	public void setCheckPort2(int m_nCheckPort2) {
//		this.m_nCheckPort2 = m_nCheckPort2;
//	}
//
//	public String getCheckServer2() {
//		return m_sCheckServer2;
//	}
//
//	public void setCheckServer2(String m_sCheckServer2) {
//		this.m_sCheckServer2 = m_sCheckServer2;
//	}
//
//	public int getCheckLimitFailCount2() {
//		return m_nCheckLimitFailCount2;
//	}
//
//	public void setCheckLimitFailCount2(int m_nCheckLimitFailCount2) {
//		this.m_nCheckLimitFailCount2 = m_nCheckLimitFailCount2;
//	}
//
//	public String getNetLinkDir2() {
//		return m_sNetLinkDir2;
//	}
//
//	public void setNetLinkDir2(String m_sNetLinkDir2) {
//		this.m_sNetLinkDir2 = m_sNetLinkDir2;
//	}
//
//	public boolean getIsSendNetLinkDir2() {
//		return isSendNetLinkDir2;
//	}
//
//	public void setIsSendNetLinkDir2(boolean isSendNetLinkDir2) {
//		this.isSendNetLinkDir2 = isSendNetLinkDir2;
//	}

	public String getGeoipPath() {
		return geoipPath;
	}

	public void setGeoipPath(String geoipPath) {
		this.geoipPath = geoipPath;
	}

	public String getGeoipDatabase() {
		return geoipDatabase;
	}

	public void setGeoipDatabase(String geoipDatabase) {
		this.geoipDatabase = geoipDatabase;
	}

	public List<String> getSpamServerList() {
		return spamServerList;
	}

	public void setSpamServerList(List<String> spamServerList) {
		this.spamServerList = spamServerList;
	}

	public int getRemoveQuotes() {
		return m_nRemoveQuotes;
	}

	public void setRemoveQuotes(int mNRemoveQuotes) {
		m_nRemoveQuotes = mNRemoveQuotes;
	}
	public boolean isUseTLS() {
		return useTLS;
	}

	public void setUseTLS(boolean useTLS) {
		this.useTLS = useTLS;
	}

	public boolean isUseSmtpAuth() {
		return useSmtpAuth;
	}

	public void setUseSmtpAuth(boolean useSmtpAuth) {
		this.useSmtpAuth = useSmtpAuth;
	}

	public List<String> getListLogIgnoreIP() {
		return listLogIgnoreIP;
	}

	public void setListLogIgnoreIP(List<String> listLogIgnoreIP) {
		this.listLogIgnoreIP = listLogIgnoreIP;
	}

	public int getQueueSize() {
		return queueSize;
	}

	public void setQueueSize(int queueSize) {
		this.queueSize = queueSize;
	}

	public boolean isUseSSL() {
		return useSSL;
	}

	public void setUseSSL(boolean useSSL) {
		this.useSSL = useSSL;
	}



	public int getIsUseIsp() {
		return m_nIsUseIsp;
	}

	public void setIsUseIsp(int m_nIsUseIsp) {
		this.m_nIsUseIsp = m_nIsUseIsp;
	}

	public int getIspPort() {
		return m_nIspPort;
	}

	public void setIspPort(int m_nIspPort) {
		this.m_nIspPort = m_nIspPort;
	}

	public int getSslPort() {
		return m_nSslPort;
	}

	public void setSslPort(int m_nSslPort) {
		this.m_nSslPort = m_nSslPort;
	}

	public List<String> getExcludeTlsCipherList() {
		return excludeTlsCipherList;
	}

	public void setExcludeTlsCipherList(List<String> excludeTlsCipherList) {
		this.excludeTlsCipherList = excludeTlsCipherList;
	}

	public String getBlockCountry() {
		return m_sBlockCountry;
	}

	public void setBlockCountry(String m_sBlockCountry) {
		this.m_sBlockCountry = m_sBlockCountry;
	}

	public int getSmtpAuthIpFailCount() {
		return m_nSmtpAuthIpFailCount;
	}

	public void setSmtpAuthIpFailCount(int m_nSmtpAuthIpFailCount) {
		this.m_nSmtpAuthIpFailCount = m_nSmtpAuthIpFailCount;
	}

	public int getIsLogConn() {
		return m_nIsLogConn;
	}

	public void setIsLogConn(int m_nIsLogConn) {
		this.m_nIsLogConn = m_nIsLogConn;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public void setSuccessLog(int successLog) {
		m_nSuccessLog = successLog;
	}
	public int getSuccessLog() {
		return m_nSuccessLog;
	}

	public String getNetLinkSpoolPath() {
		return netLinkSpoolPath;
	}

	public void setNetLinkSpoolPath(String netLinkSpoolPath) {
		this.netLinkSpoolPath = netLinkSpoolPath;
	}

	public String getJournalingSpoolPath() {
		return journalingSpoolPath;
	}

	public void setJournalingSpoolPath(String journalingSpoolPath) {
		this.journalingSpoolPath = journalingSpoolPath;
	}

	public List<String> getLocalIpList() {
		return localIpList;
	}

	public void setLocalIpList(List<String> localIpList) {
		this.localIpList = localIpList;
	}

	public boolean isUseDmarcCheck() {
		return useDmarcCheck;
	}

	public void setUseDmarcCheck(boolean useDmarcCheck) {
		this.useDmarcCheck = useDmarcCheck;
	}

	public int getYlmfpcBlock() {
		return ylmfpcBlock;
	}

	public void setYlmfpcBlock(int ylmfpcBlock) {
		this.ylmfpcBlock = ylmfpcBlock;
	}

	public int getUseFileDB(){
		return this.useFileDB;
	}

	public void setUseFileDB(int useFileDB){
		this.useFileDB = useFileDB;
	}


	public int getIsForceAuth() {
		return isForceAuth;
	}

	public void setIsForceAuth(int isForceAuth) {
		this.isForceAuth = isForceAuth;
	}

	public int getMaxBlukThread() {
		return maxBlukThread;
	}

	public void setMaxBlukThread(
		int maxBlukThread) {
		this.maxBlukThread = maxBlukThread;
	}

	public int getStrictFromUser(){
		return m_nStrictFromUser;
	}

	public void setStrictFromUser(int strictFromUser){
		m_nStrictFromUser = strictFromUser;
	}

	public int getMaxConcurrentConnect(){
		return m_nMaxConcurrentConnect;
	}

	public void setMaxConcurrentConnect(int maxConcurrentConnect){
		m_nMaxConcurrentConnect = maxConcurrentConnect;
	}

	public String getMaxConcurrentConnectNoLimit() {
		return m_sMaxConcurrentConnectNoLimit;
	}

	public void setMaxConcurrentConnectNoLimit(String maxConcurrentConnectNoLimit) {
		m_sMaxConcurrentConnectNoLimit = maxConcurrentConnectNoLimit;
	}

	public int getMaxRsetCount(){
		return m_nMaxRsetCount;
	}

	public void setMaxRsetCount(int rsetCount){
		m_nMaxRsetCount = rsetCount;
	}

	public String getAbsent_subject() {
		return absent_subject;
	}

	public void setAbsent_subject(String absentSubject) {
		absent_subject = absentSubject;
	}

	public String getFromMobile() {
		return FromMobile;
	}

	public void setFromMobile(String fromMobile) {
		FromMobile = fromMobile;
	}

	public long getMaxMsgSize() {
		return m_lMaxMsgSize;
	}

	public void setMaxMsgSize(long maxMsgSize) {
		m_lMaxMsgSize = maxMsgSize;
	}

	public long getTotMaxMsgSize() {
		return m_lTotMaxMsgSize;
	}

	public void setTotMaxMsgSize(long totMaxMsgSize) {
		m_lTotMaxMsgSize = totMaxMsgSize;
	}

	public String getQueuePath() {
		return m_sQueuePath;
	}

	public void setQueuePath(String queuePath) {
		m_sQueuePath = queuePath;
	}

	public int getConnTime() {
		return m_nConnTime;
	}

	public void setConnTime(int connTime) {
		m_nConnTime = connTime;
	}

	public int getDefaultConnection() {
		return m_nDefaultConnection;
	}

	public void setDefaultConnection(int defaultConnection) {
		m_nDefaultConnection = defaultConnection;
	}

	public int getIsCheckFromDom() {
		return m_nIsCheckFromDom;
	}

	public void setIsCheckFromDom(int isCheckFromDom) {
		m_nIsCheckFromDom = isCheckFromDom;
	}

	public int getAllowNullFrom() {
        return m_nAllowNullFrom;
    }

    public void setAllowNullFrom(int allowNullFrom) {
        m_nAllowNullFrom = allowNullFrom;
    }

	public int getMaxConnection() {
		return m_nMaxConnection;
	}

	public void setMaxConnection(int maxConnection) {
		m_nMaxConnection = maxConnection;
	}

	public int getMaxMsgLoops() {
		return m_nMaxMsgLoops;
	}

	public void setMaxMsgLoops(int maxMsgLoops) {
		m_nMaxMsgLoops = maxMsgLoops;
	}

	public int getMaxRcpt() {
		return m_nMaxRcpt;
	}

	public void setMaxRcpt(int maxRcpt) {
		m_nMaxRcpt = maxRcpt;
	}

	public int getMaxRetry() {
		return m_nMaxRetry;
	}

	public void setMaxRetry(int maxRetry) {
		m_nMaxRetry = maxRetry;
	}

	public int getMaxSendTh() {
		return m_nMaxSendTh;
	}

	public void setMaxSendTh(int maxSendTh) {
		m_nMaxSendTh = maxSendTh;
	}

	public int getReSendTh() {
		return m_nReSendTh;
	}

	public void setReSendTh(int reSendTh) {
		m_nReSendTh = reSendTh;
	}

	public int getReserveSendTh() {
		return m_nReserveSendTh;
	}

	public void setReserveSendTh(int reserveSendTh) {
		m_nReserveSendTh = reserveSendTh;
	}

	public int getRetryInterval() {
		return m_nRetryInterval;
	}

	public void setRetryInterval(int retryInterval) {
		m_nRetryInterval = retryInterval;
	}

	public int getRWTime() {
		return m_nRWTime;
	}

	public void setRWTime(int time) {
		m_nRWTime = time;
	}

	public int getSmtpPortIn() {
		return m_nSmtpPortIn;
	}

	public void setSmtpPortIn(int smtpPortIn) {
		m_nSmtpPortIn = smtpPortIn;
	}

	public int getSmtpPortOut() {
		return m_nSmtpPortOut;
	}

	public void setSmtpPortOut(int smtpPortOut) {
		m_nSmtpPortOut = smtpPortOut;
	}

	public String getCertPass() {
		return m_sCertPass;
	}

	public void setCertPass(String certPass) {
		m_sCertPass = certPass;
	}

	public String getCertPath() {
		return m_sCertPath;
	}

	public void setCertPath(String certPath) {
		m_sCertPath = certPath;
	}

	public String getDefaultDomain() {
		return m_sDefaultDomain;
	}

	public void setDefaultDomain(String defaultDomain) {
		m_sDefaultDomain = defaultDomain;
	}

	public String getDnsServer() {
		return m_sDnsServer;
	}

	public void setDnsServer(String dnsServer) {
		m_sDnsServer = dnsServer;
	}

	public String getHeloHost() {
		return m_sHeloHost;
	}

	public void setHeloHost(String heloHost) {
		m_sHeloHost = heloHost;
	}

	public String getMaxMsgSizeErrors() {
		return m_sMaxMsgSizeErrors;
	}

	public void setMaxMsgSizeErrors(String maxMsgSizeErrors) {
		m_sMaxMsgSizeErrors = maxMsgSizeErrors;
	}

	public String getMaxRcptErrors() {
		return m_sMaxRcptErrors;
	}

	public void setMaxRcptErrors(String maxRcptErrors) {
		m_sMaxRcptErrors = maxRcptErrors;
	}

	public String getRelayToDomain() {
		return m_sRelayToDomain;
	}

	public void setRelayToDomain(String relayToDomain) {
		m_sRelayToDomain = relayToDomain;
	}

	public String getRootDomain() {
		return m_sRootDomain;
	}

	public void setRootDomain(String rootDomain) {
		m_sRootDomain = rootDomain;
	}

	public String getSensmailHome() {
		return m_sSensmailHome;
	}

	public void setSensmailHome(String sensmailHome) {
		m_sSensmailHome = sensmailHome;
	}

	public String getTempDir() {
		return m_sTempDir;
	}

	public void setTempDir(String sTempDir) {
		this.m_sTempDir = sTempDir;
	}

	public void setDbType(int dbType){
	    m_nDbType = dbType;
	}

	public int getDbType(){
	    return m_nDbType;
	}

	public int getIsBlockNotRelay() {
		return isBlockNotRelay;
	}

	public void setIsBlockNotRelay(int isBlockNotRelay) {
		this.isBlockNotRelay = isBlockNotRelay;
	}


    public String getGatewaySendPath() {
		return gatewaySendPath;
	}

	public void setGatewaySendPath(String gatewaySendPath) {
		this.gatewaySendPath = gatewaySendPath;
	}

	public List<String> getGatewayServerIp() {
        return gatewayServerIp;
    }

    public void setGatewayServerIp(List<String> gatewayServerIp) {
        this.gatewayServerIp = gatewayServerIp;
    }

    public List<String> getExceptGatewaysendIpList() {
        return exceptGatewaysendIpList;
    }

    public void setExceptGatewaysendIpList(List<String> exceptGatewaysendIpList) {
        this.exceptGatewaysendIpList = exceptGatewaysendIpList;
    }

    public boolean isUseGateway() {
        return useGateway;
    }

    public void setUseGateway(boolean useGateway) {
        this.useGateway = useGateway;
    }

    public boolean isUseGatewayForLocalMail() {
        return useGatewayForLocalMail;
    }

    public void setUseGatewayForLocalMail(boolean useGatewayForLocalMail) {
        this.useGatewayForLocalMail = useGatewayForLocalMail;
    }

	public boolean isUseJournaling() {
		return useJournaling;
	}

	public void setUseJournaling(boolean useJournaling) {
		this.useJournaling = useJournaling;
	}

	public boolean isUseReceiptNotify() {
		return useReceiptNotify;
	}

	public void setUseReceiptNotify(boolean useReceiptNotify) {
		this.useReceiptNotify = useReceiptNotify;
	}

	public String getReceiptNotifyUrl() {
		return receiptNotifyUrl;
	}

	public void setReceiptNotifyUrl(String receiptNotifyUrl) {
		this.receiptNotifyUrl = receiptNotifyUrl;
	}

	public String getEncrypt_key() {
		return encrypt_key;
	}

	public void setEncrypt_key(String encrypt_key) {
		this.encrypt_key = encrypt_key;
	}

	public boolean isTest() {
		return test;
	}

	public void setTest(boolean test) {
		this.test = test;
	}


	/*public Map<String, String> getUserMap() {
		return userMap;
	}

	public void setUserMap(Map<String, String> userMap) {
		this.userMap = userMap;
	}

	public Map<String, ProxyDomain> getProxyDomainMap() {
		return proxyDomainMap;
	}

	public void setProxyDomainMap(Map<String, ProxyDomain> proxyDomainMap) {
		this.proxyDomainMap = proxyDomainMap;
	}

	public List<String> getRelayIpList() {
		return relayIpList;
	}
	public void setRelayIpList(List<String> relayIpList) {
		this.relayIpList = relayIpList;
	}
	public boolean hasRelayIp(String ip) {
		boolean bRet = false;
		if(this.relayIpList != null) {
			for(String relayip : this.relayIpList) {
				bRet = ImIpUtil.matchIPbyCIDR(relayip, ip);
				if(bRet) break;
			}
		}
		//return this.relayIpList.contains(ip);
		return bRet;
	}

	public List<String> getDenyIpList() {
		return denyIpList;
	}
	public void setDenyIpList(List<String> denyIpList) {
		this.denyIpList = denyIpList;
	}
	public boolean hasDenyIp(String ip) {
		boolean bRet = false;
		if(this.denyIpList != null) {
			for(String denyip : this.denyIpList) {
				bRet = ImIpUtil.matchIPbyCIDR(denyip, ip);
				if(bRet) break;
			}
		}
		//return this.relayIpList.contains(ip);
		return bRet;
	}*/

	public void load() {
		String sPath = SensEmsEnvironment.getSensEmsServerHome();
		//System.out.println("---------- SensProxyServerHome: " + sPath);
		ImConfLoaderEx confSmtp = ImConfLoaderEx.getInstance("sensems.home","sensems.xml");

		this.setDnsServer(confSmtp.getProfileString("smtp","nameserver"));
		this.setHeloHost(confSmtp.getProfileString("smtp","helo_domain"));
		this.setRootDomain(confSmtp.getProfileString("general","default_domain"));

		this.setQueuePath(confSmtp.getProfileString("smtp","queue_path"));
		this.setTempDir(confSmtp.getProfileString("general","tempfile"));
		this.setSendHost(confSmtp.getProfileString("smtp","send_host"));
		this.setSmtpPortIn(confSmtp.getProfileInt("smtp","port.inbound", DEFAULT_SMTP_PORT));
		this.setSmtpPortOut( confSmtp.getProfileInt("smtp","port.outbound", DEFAULT_SMTP_PORT));
		this.setMaxSendTh(confSmtp.getProfileInt("smtp","sendthread",20));
		this.setReSendTh(confSmtp.getProfileInt("smtp","resendthread",5));
		//this.setReserveSendTh(confSmtp.getProfileInt("smtp","reservethread",1));
		this.setMaxBlukThread(confSmtp.getProfileInt("general","bulkthread",5));

		this.setDefaultConnection(confSmtp.getProfileInt("smtp","connect.default",20));
		this.setMaxConnection(confSmtp.getProfileInt("smtp","connect.max",128));
		this.setMaxRetry(confSmtp.getProfileInt("smtp","retry",3));
		this.setRetryInterval(confSmtp.getProfileInt("smtp","retry_interval",10) * 1000 * 60);
		this.setConnTime(confSmtp.getProfileInt("timeout", "conn", 60) * 1000);
		this.setRWTime(confSmtp.getProfileInt("timeout", "read", 60) * 1000);
		this.setMaxRcpt(confSmtp.getProfileInt("smtp","max_rcpt",100));
		this.setMaxRcptErrors(confSmtp.getProfileString("smtp","maxrcpterr"));
		this.setMaxRcptErrors(ImConvertUtil.convertCharset("8859_1", "EUC_KR", this.getMaxRcptErrors()));
		this.setUseTarpit(confSmtp.getProfileInt("smtp", "use_tarpit", 2)*1000);
		// 혹시 핑퐁치는 경우 중간에 멈추게
		this.setMaxMsgLoops(confSmtp.getProfileInt("smtp", "max_msgloop", 5));

		// RSET count
		this.setMaxRsetCount(confSmtp.getProfileInt("smtp","max_rset",100));

		// 메일 한건당 최대 메시지 크기
		this.setMaxMsgSize(confSmtp.getProfileInt("smtp","max_size",20)*1024*1024);
		// 메시지 사이즈크기 * 수신자 수 했을 때 최대 크기
		this.setTotMaxMsgSize((long)confSmtp.getProfileInt("smtp","total_max_size",1024)*1024*1024);

		this.setMaxMsgSizeErrors(confSmtp.getProfileString("smtp","maxmsgsizeerr"));
		this.setMaxMsgSizeErrors(ImConvertUtil.convertCharset("8859_1", "EUC_KR", this.getMaxMsgSizeErrors()));

		// 무한루프 빠짐 방지(Received 헤더를 몇개까지 허용할지 설정)
		this.setMaxMsgLoops(confSmtp.getProfileInt("smtp","max_msgloop",10));

		this.setIsCheckFromDom(confSmtp.getProfileInt("smtp","checkfromdomain",0));

		this.setGeoipPath(confSmtp.getProfileString("geoip", "path"));
		this.setGeoipDatabase(confSmtp.getProfileString("geoip", "data"));

		// 발송실패시 리턴메일 전송 여부(기본은 1 = true)
		this.setUseDsn(confSmtp.getProfileInt("smtp", "use_dsn_send", 1) == 1);

		this.setUseDB(confSmtp.getProfileInt("general", "use_db", 1) == 1);

        // 망연계 파일 전송
        boolean isUseNetLink = confSmtp.getProfileInt("smtp", "netlink.use", 0) == 1;
        boolean isSender = confSmtp.getProfileInt("smtp", "netlink.is_sender", 0) == 1;
        String netLinkDir = confSmtp.getProfileString("smtp", "netlink.netlink_dir");

        if(isUseNetLink) {
        	if(isSender) {
        		this.setUseGateway(true);
        		// gateway send_path (내부망에서 망연계용 디렉토리에 메일을 저장할때)
                this.setGatewaySendPath(netLinkDir);
        	} else {
        		// 메일을 읽어들일 망연계 디렉토리(내부에서 발송된(망연계 디렉토리에 저장된) 메일을 외부로 발송할때)
             	this.setNetLinkSpoolPath(netLinkDir);
        	}
        }

        // gateway 서버 목록
		// 게이트웨이를 이용한 메일 발송을 할것인가?
		this.setUseGateway(confSmtp.getProfileInt("smtp", "gateway.use", 0) == 1);
		String gatewayServer = confSmtp.getProfileString("smtp","gateway.server");
		if(StringUtils.isNotEmpty(gatewayServer)){
			String[] gatewayServers = gatewayServer.split(",");
			this.setGatewayServerIp(new ArrayList(Arrays.asList(gatewayServers)));
		}

        // 게이트웨이로 안보낼 IP 목록
        String exceptGatewayIp = confSmtp.getProfileString("smtp","gateway.except");
        if(StringUtils.isNotEmpty(exceptGatewayIp)){
            String[] exceptServers = exceptGatewayIp.split(",");
            this.setExceptGatewaysendIpList(new ArrayList(Arrays.asList(exceptServers)));
        }
		// relay to domain
		this.setRelayToDomain(confSmtp.getProfileString("smtp","smtprelayto"));

		// 접속 기록 로그에 제외시킬 아이피 목록
		String logIgnoreIp = confSmtp.getProfileString("smtp","log_ignore_ip");
		if(StringUtils.isNotEmpty(logIgnoreIp)){
			this.setListLogIgnoreIP(Arrays.asList(logIgnoreIp.split(",")));
		}

		// SSL
		this.setCertPath(confSmtp.getProfileString("general","certpath"));
		this.setCertPass(confSmtp.getProfileString("general","certpass"));
		// certpass , certpath 가 있을 경우 STARTTLS 명령어가 가능하다.
		if( StringUtils.isNotEmpty(this.m_sCertPath) && StringUtils.isNotEmpty(this.m_sCertPass) ){
			this.useTLS = true;
		}

		this.setSensmailHome( SensEmsEnvironment.getSensEmsServerHome() );
		this.setDefaultDomain(confSmtp.getProfileString("general","default_domain"));

		this.setSuccessLog(confSmtp.getProfileInt("smtp","successlog",0));

		String prodName = confSmtp.getProfileString("smtp","product");
		if(prodName == null || prodName.equals("")){
			prodName = SMTP_SERVER_NAME;
		}
		this.setProductName(prodName);

		this.setIsBlockNotRelay(confSmtp.getProfileInt("smtp","block_norelay",0));

		//this.setAbsent_subject(confSmtp.getProfileString("general","absent_subject"));

		// 동시접속 수 제한(0보다 크면 제한한다.)
		this.setMaxConcurrentConnect(confSmtp.getProfileInt("smtp","max_concurrent_connect",0));
		// 동시접속수가 많아도 제한하지 않는 예외 아이피
		this.setMaxConcurrentConnectNoLimit(confSmtp.getProfileString("smtp","no_concurrent_limit"));
		if(!this.getMaxConcurrentConnectNoLimit().equals("")){
			this.setMaxConcurrentConnectNoLimit(this.getMaxConcurrentConnectNoLimit()+",");
		}

		// 특정IP가 서로 다른 여러 아이디로 인증 실패를 여러번 할 경우 해당 ip 차단
		this.setSmtpAuthIpFailCount(confSmtp.getProfileInt("smtp","max_smtpauth_ip_fail",0));

		// smtp 접속 아이피 기록 여부(debug일때) - addSmtpConnectServer/delSmtpConnectServer 기록
		this.setIsLogConn(confSmtp.getProfileInt("smtp", "is_debug_log_connect", 1));

		// smtp 인증아이디와 보내는메일주소가 다를때 처리 방법(1이면 from주소와 logon user가 다르면 거부)
		this.setStrictFromUser(confSmtp.getProfileInt("smtp","strict_from_user",0));

		// 저널링 사용여부
		this.setUseJournaling( confSmtp.getProfileInt("journaling.use",0) == 1 );

		// 587포트 사용여부
		m_nIsUseIsp = confSmtp.getProfileInt("smtp", "use_isp_smtp",0);
		m_nIspPort = confSmtp.getProfileInt("smtp", "use_isp_port",587);
		m_nUseIspAuth = confSmtp.getProfileInt("smtp", "use_isp_auth",1);

		serverID = confSmtp.getProfileString("general", "serverid");

		queueSize = confSmtp.getProfileInt("smtp", "queue_size",1);

		useSSL = confSmtp.getProfileInt("smtp","ssl.use",0) == 1;
		m_nSslPort = confSmtp.getProfileInt("smtp","ssl.port", DEFAULT_SMTP_SSL_PORT);

		this.setUseSmtpAuth(confSmtp.getProfileInt("smtp", "use_smtp_auth", 1) == 1);

		//String bindAddr = confSmtp.getProfileString("general", "bind_address");
		//ImSmtpConnectClient.g_sCurMonth = ImSensProxyServer.confSmtp.getProfileString("general","logmonth");

		this.setIsForceAuth(confSmtp.getProfileInt("smtp", "is_force_auth",0));

		this.setYlmfpcBlock(confSmtp.getProfileInt("smtp", "ylmf_pc_block",1));

		this.setBlockCountry(confSmtp.getProfileString("smtp", "access_allow_country.smtp_force_block_country"));
		if(StringUtils.isNotEmpty(m_sBlockCountry)) m_sBlockCountry = m_sBlockCountry + ",";

		// 외부로 발송할때 mail from을 id+rel=domain@default_domain 으로 처리 (id@domain)
		this.setSendDomainProxy(confSmtp.getProfileInt("smtp", "sendproxy.use",0) == 1);

		this.setTransmitLogDeleteDays(confSmtp.getProfileInt("smtp", "transmit_log_delete_day", 7));

		// 동보 그룹 키
		this.setHeaderGroupKey(confSmtp.getProfileString("smtp","group_key_header"));
		// 수신자별 고유 키
		this.setHeaderRcptKey(confSmtp.getProfileString("smtp","rcpt_key_header"));

		// dbsend agent
		this.setDbSendAgentInterval(confSmtp.getProfileInt("smtp", "dbsend_agent_interval", 60));

		String lang = confSmtp.getProfileString("smtp","default_lang");
		if(StringUtils.isEmpty(lang)) lang = "ko";
		this.setDefaultLang(lang);

		// 다른 서버 체크
//		m_nIsCheckSmtp2 =  confSmtp.getProfileInt("smtp","check_smtp2.check", 0);
//		m_nCheckPort2 =  confSmtp.getProfileInt("smtp","check_smtp2.check_port", 25);
//		m_sCheckServer2 = confSmtp.getProfileString("smtp","check_smtp2.check_host");
//		m_nCheckLimitFailCount2 = confSmtp.getProfileInt("smtp","check_smtp2.check_limit_fail_count", 10);
//		m_sNetLinkDir2 = confSmtp.getProfileString("netlink", "netlink_dir2");
//		isSendNetLinkDir2 = confSmtp.getProfileInt("netlink.use_netlink_dir2",0) == 1;

		postmaster = confSmtp.getProfileString("general", "postmaster");

		List<String> localIpList = new ArrayList<String>();
		localIpList.add("127.0.0.1");
		localIpList.add("::1");
		try {
			String localIp = getHost4Address();
			if(StringUtils.isNotEmpty(localIp)) {
				localIpList.add(localIp);
			}
		}catch(Exception e) {}
		this.setLocalIpList(localIpList);

		// 수신확인 url 삽입여부
		this.useReceiptNotify = confSmtp.getProfileInt("smtp", "receipt_notify.use", 0) == 1;
		this.receiptNotifyUrl = confSmtp.getProfileString("url", "rcpt_url_ex");
		this.webUrl = confSmtp.getProfileString("url", "web_url");
		// 스풀 삭제여부(test 면 삭제안하게)
		this.setTest(confSmtp.getProfileInt("general", "is_test", 0) == 1);
		this.encrypt_key = confSmtp.getProfileString("url", "aes_key");

		// TLS 통신을 할때 잘 안되는 ciphersuite 들이 있음
		String excludeTlsCipher = confSmtp.getProfileString("tls","exclude.cipher");
		if(StringUtils.isNotEmpty(excludeTlsCipher)){
			String[] excludeCipherArray = excludeTlsCipher.split(",");
			excludeTlsCipherList = Arrays.asList(excludeCipherArray);
			this.setExcludeTlsCipherList(excludeTlsCipherList);
		}

	}


	@Override
	public String toString() {
		return "ImSmtpConfig{" +
				"m_sDnsServer='" + m_sDnsServer + '\'' +
				", m_sHeloHost='" + m_sHeloHost + '\'' +
				", m_sRootDomain='" + m_sRootDomain + '\'' +
				", m_sQueuePath='" + m_sQueuePath + '\'' +
				", m_nSmtpPortIn=" + m_nSmtpPortIn +
				", m_nSmtpPortOut=" + m_nSmtpPortOut +
				", m_nMaxSendTh=" + m_nMaxSendTh +
				", m_nReSendTh=" + m_nReSendTh +
				", m_nReserveSendTh=" + m_nReserveSendTh +
				", m_nDefaultConnection=" + m_nDefaultConnection +
				", m_nMaxConnection=" + m_nMaxConnection +
				", m_nMaxRetry=" + m_nMaxRetry +
				", m_nRetryInterval=" + m_nRetryInterval +
				", m_nConnTime=" + m_nConnTime +
				", m_nRWTime=" + m_nRWTime +
				", m_nMaxRcpt=" + m_nMaxRcpt +
				", m_sMaxRcptErrors='" + m_sMaxRcptErrors + '\'' +
				", m_nMaxRsetCount=" + m_nMaxRsetCount +
				", m_lMaxMsgSize=" + m_lMaxMsgSize +
				", m_lTotMaxMsgSize=" + m_lTotMaxMsgSize +
				", m_sMaxMsgSizeErrors='" + m_sMaxMsgSizeErrors + '\'' +
				", m_nMaxMsgLoops=" + m_nMaxMsgLoops +
				", m_nIsCheckFromDom=" + m_nIsCheckFromDom +
				", m_nAllowNullFrom=" + m_nAllowNullFrom +
				", m_sRelayToDomain='" + m_sRelayToDomain + '\'' +
				", m_sCertPath='" + m_sCertPath + '\'' +
				", m_sCertPass='" + m_sCertPass + '\'' +
				", m_sSensmailHome='" + m_sSensmailHome + '\'' +
				", m_sDefaultDomain='" + m_sDefaultDomain + '\'' +
				", useTLS=" + useTLS +
				", postmaster='" + postmaster + '\'' +
				", m_nDbType=" + m_nDbType +
				", isBlockNotRelay=" + isBlockNotRelay +
				", FromMobile='" + FromMobile + '\'' +
				", absent_subject='" + absent_subject + '\'' +
				", m_sMaxConcurrentConnectNoLimit='" + m_sMaxConcurrentConnectNoLimit + '\'' +
				", m_nMaxConcurrentConnect=" + m_nMaxConcurrentConnect +
				", useTarpit=" + useTarpit +
				", m_nStrictFromUser=" + m_nStrictFromUser +
				", maxBlukThread=" + maxBlukThread +
				", isForceAuth=" + isForceAuth +
				", useFileDB=" + useFileDB +
				", useJournaling=" + useJournaling +
				", journalingSpoolPath='" + journalingSpoolPath + '\'' +
				", netLinkSpoolPath='" + netLinkSpoolPath + '\'' +
				", useGateway=" + useGateway +
				", gatewaySendPath='" + gatewaySendPath + '\'' +
				", gatewayServerIp=" + gatewayServerIp +
				", exceptGatewaysendIpList=" + exceptGatewaysendIpList +
				", useGatewayForLocalMail=" + useGatewayForLocalMail +
				", ylmfpcBlock=" + ylmfpcBlock +
				", useDmarcCheck=" + useDmarcCheck +
				", localIpList=" + localIpList +
				", m_nSuccessLog=" + m_nSuccessLog +
				", productName='" + productName + '\'' +
				", m_nSmtpAuthIpFailCount=" + m_nSmtpAuthIpFailCount +
				", m_nIsLogConn=" + m_nIsLogConn +
				", m_nIsUseIsp=" + m_nIsUseIsp +
				", m_nIspPort=" + m_nIspPort +
				", m_nUseIspAuth=" + m_nUseIspAuth +
				", serverID='" + serverID + '\'' +
				", queueSize=" + queueSize +
				", m_sBlockCountry='" + m_sBlockCountry + '\'' +
				", excludeTlsCipherList=" + excludeTlsCipherList +
				", useSSL=" + useSSL +
				", m_nSslPort=" + m_nSslPort +
				", listLogIgnoreIP=" + listLogIgnoreIP +
				", useSmtpAuth=" + useSmtpAuth +
				", m_nRemoveQuotes=" + m_nRemoveQuotes +
				", spamServerList=" + spamServerList +
				", geoipPath='" + geoipPath + '\'' +
				", geoipDatabase='" + geoipDatabase + '\'' +
				", useDsn=" + useDsn +
				", useDB=" + useDB +
				", transmitLogDeleteDays=" + transmitLogDeleteDays +
				", headerGroupKey='" + headerGroupKey + '\'' +
				", headerRcptKey='" + headerRcptKey + '\'' +
				", defaultLang='" + defaultLang + '\'' +
				", useReceiptNotify=" + useReceiptNotify +
				", webUrl=" + webUrl +
				", receiptNotifyUrl='" + receiptNotifyUrl + '\'' +
				", test=" + test +
				", encrypt_key='" + encrypt_key + '\'' +
				", isSendDomainProxy=" + isSendDomainProxy +
				'}';
	}

	/**
	 * Returns this host's non-loopback IPv4 addresses.
	 */
	private static List<Inet4Address> getInet4Addresses() throws SocketException {
	    List<Inet4Address> ret = new ArrayList<Inet4Address>();

	    Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
	    for (NetworkInterface netint : Collections.list(nets)) {
	        Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
	        for (InetAddress inetAddress : Collections.list(inetAddresses)) {
	            if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress()) {
	                ret.add((Inet4Address)inetAddress);
	            }
	        }
	    }

	    return ret;
	}

	/**
	 * Returns this host's first non-loopback IPv4 address string in textual
	 * representation.
	 * 
	 * @return
	 * @throws SocketException
	 */
	private static String getHost4Address() throws SocketException {
	    List<Inet4Address> inet4 = getInet4Addresses();
	    return !inet4.isEmpty()
	            ? inet4.get(0).getHostAddress()
	            : null;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
	}

}

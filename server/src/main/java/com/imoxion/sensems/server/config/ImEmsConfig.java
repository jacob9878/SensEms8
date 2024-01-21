package com.imoxion.sensems.server.config;

import com.imoxion.common.util.ImConfLoaderEx;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ImEmsConfig {
    private Logger logger = LoggerFactory.getLogger("EMSD");
    private static ImEmsConfig emsConfig;

    public static final int DEFAULT_SMTP_PORT = 25;
    public static final int DEFAULT_SMTP_SSL_PORT = 465;

    private boolean useEncryptDB = true;
    private String webUrl;
    private String aesKey;
    private Map<String, String> aesKeyMap;

    private int extractThreadCount = 1;
    private int transferThreadCount = 1;
    private int senderThreadInCount = 8;
    private int maxRecvCount = 20;

    private String senderHost = "localhost";
    private int senderInboundPort = 9090;
    private int senderOutbountPort = 25;
    private int senderThreadOutCount = 8;
    private String dnsServer;
    private String heloDomain;
    private int connTime = 60000;
    private int readTime = 60000;

    private int errorResendInterval = 60; // 분단위 기본 60분

    // SSL
    private String certPath = "";
    private String certPass = "";
    private String defaultDomain = "";
    private List<String> excludeTlsCipherList = new ArrayList<String>();
    private String[] tlsProtocolArray = null;

    private String queuePath = "";
    // eml 파일 저장 경로(msgPath + YYYY/MM/DD/MSGID.eml )
    private String msgPath = "";

    private String attachPath = "";

    private String defaultLang = "ko";
    private String tempfile;

    private String rejectUrl = "";
    private String downloadUrl = "";

    private String urlEncryptKey = "";

    private String geoipDatabase;

    private String sensdataPath;

    public static ImEmsConfig getInstance() {
        if (emsConfig == null) {
            emsConfig = new ImEmsConfig();
        }

        return emsConfig;
    }

    private ImEmsConfig() {
        load();
    }
  //////////////


    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public int getErrorResendInterval() {
        return errorResendInterval;
    }

    public void setErrorResendInterval(int errorResendInterval) {
        this.errorResendInterval = errorResendInterval;
    }

    public String getRejectUrl() {
        return rejectUrl;
    }

    public void setRejectUrl(String rejectUrl) {
        this.rejectUrl = rejectUrl;
    }

    public String getUrlEncryptKey() {
        return urlEncryptKey;
    }

    public void setUrlEncryptKey(String urlEncryptKey) {
        this.urlEncryptKey = urlEncryptKey;
    }

    public String getDefaultLang() {
        return defaultLang;
    }

    public void setDefaultLang(String defaultLang) {
        this.defaultLang = defaultLang;
    }

    public String getHeloDomain() {
        return heloDomain;
    }

    public void setHeloDomain(String heloDomain) {
        this.heloDomain = heloDomain;
    }

    public String getMsgPath() {
        return msgPath;
    }

    public void setMsgPath(String msgPath) {
        this.msgPath = msgPath;
    }

    public String getDnsServer() {
        return dnsServer;
    }

    public void setDnsServer(String dnsServer) {
        this.dnsServer = dnsServer;
    }

    public int getSenderThreadOutCount() {
        return senderThreadOutCount;
    }

    public void setSenderThreadOutCount(int senderThreadOutCount) {
        this.senderThreadOutCount = senderThreadOutCount;
    }

    public String[] getTlsProtocolArray() {
        return tlsProtocolArray != null ? tlsProtocolArray.clone() : null;
    }

    public void setTlsProtocolArray(String[] tlsProtocolArray) {
        if(tlsProtocolArray != null){
            this.tlsProtocolArray = new String[tlsProtocolArray.length];
            for ( int i = 0; i < tlsProtocolArray.length; ++i){
                this.tlsProtocolArray[i] = tlsProtocolArray[i];
            }
        }else {
            this.tlsProtocolArray = null;
        }
    }

    public String getQueuePath() {
        return queuePath;
    }

    public void setQueuePath(String queuePath) {
        this.queuePath = queuePath;
    }

    public int getConnTime() {
        return connTime;
    }

    public void setConnTime(int connTime) {
        this.connTime = connTime;
    }

    public int getReadTime() {
        return readTime;
    }

    public void setReadTime(int readTime) {
        this.readTime = readTime;
    }

    public String getCertPath() {
        return certPath;
    }

    public void setCertPath(String certPath) {
        this.certPath = certPath;
    }

    public String getCertPass() {
        return certPass;
    }

    public void setCertPass(String certPass) {
        this.certPass = certPass;
    }

    public String getDefaultDomain() {
        return defaultDomain;
    }

    public void setDefaultDomain(String defaultDomain) {
        this.defaultDomain = defaultDomain;
    }

    public String getAesKey() {
        return aesKey;
    }

    public void setAesKey(String aesKey) {
        this.aesKey = aesKey;
    }

    public boolean isUseEncryptDB() {
        return useEncryptDB;
    }

    public void setUseEncryptDB(boolean useEncryptDB) {
        this.useEncryptDB = useEncryptDB;
    }

    public String getTempfile() {
        return tempfile;
    }

    public void setTempfile(String tempfile) {
        this.tempfile = tempfile;
    }

    public int getExtractThreadCount() {
        return extractThreadCount;
    }

    public void setExtractThreadCount(int extractThreadCount) {
        this.extractThreadCount = extractThreadCount;
    }

    public int getTransferThreadCount() {
        return transferThreadCount;
    }

    public void setTransferThreadCount(int transferThreadCount) {
        this.transferThreadCount = transferThreadCount;
    }

    public int getMaxRecvCount() {
        return maxRecvCount;
    }

    public void setMaxRecvCount(int maxRecvCount) {
        this.maxRecvCount = maxRecvCount;
    }

    public String getSenderHost() {
        return senderHost;
    }

    public void setSenderHost(String senderHost) {
        this.senderHost = senderHost;
    }

    public int getSenderInboundPort() {
        return senderInboundPort;
    }

    public void setSenderInboundPort(int senderInboundPort) {
        this.senderInboundPort = senderInboundPort;
    }

    public int getSenderOutbountPort() {
        return senderOutbountPort;
    }

    public void setSenderOutbountPort(int senderOutbountPort) {
        this.senderOutbountPort = senderOutbountPort;
    }

    public int getSenderThreadInCount() {
        return senderThreadInCount;
    }

    public void setSenderThreadInCount(int senderThreadInCount) {
        this.senderThreadInCount = senderThreadInCount;
    }

    public String getGeoipDatabase() {
        return geoipDatabase;
    }

    public void setGeoipDatabase(String geoipDatabase) {
        this.geoipDatabase = geoipDatabase;
    }

    public String getSensdataPath() {
        return sensdataPath;
    }

    public void setSensdataPath(String sensdataPath) {
        this.sensdataPath = sensdataPath;
    }

    public String getAttachPath() {
        return attachPath;
    }

    public void setAttachPath(String attachPath) {
        this.attachPath = attachPath;
    }

    public void load(){
        ImConfLoaderEx confEms = new ImConfLoaderEx("sensems.home", "sensems.xml");

        aesKey = confEms.getProfileString("database.encryption", "aes_key");
        //confEms.getProfileStringArray("database.encryption", "aes_key");
        useEncryptDB = confEms.getProfileString("database.encryption", "use").equals("1");
        extractThreadCount = confEms.getProfileInt("emsd", "thread.extract", 1);
        transferThreadCount = confEms.getProfileInt("emsd", "thread.transfer", 1);
        maxRecvCount = confEms.getProfileInt("emsd", "max_recv", 10);
        senderInboundPort = confEms.getProfileInt("sender", "port.inbound", 9090);
        senderOutbountPort = confEms.getProfileInt("sender", "port.outbound", 25);
        senderThreadOutCount = confEms.getProfileInt("sender", "thread_out", 8);
        senderThreadInCount = confEms.getProfileInt("sender", "thread_in", 8);
        connTime = confEms.getProfileInt("timeout", "conn", 30000);
        readTime = confEms.getProfileInt("timeout", "read", 30000);
        errorResendInterval = confEms.getProfileInt("general", "error_resend_interval", 60);

        certPath = confEms.getProfileString("general","certpath");
        certPass = confEms.getProfileString("general","certpass");
        defaultDomain = confEms.getProfileString("general","default_domain");
        // default: TLSv1, TLSv1.1, TLSv1.2
        String tlsProtocol = confEms.getProfileString("tls","protocol");
        if(StringUtils.isNotEmpty(tlsProtocol)){
            this.setTlsProtocolArray(tlsProtocol.split(","));
        }

        queuePath = confEms.getProfileString("sender", "queue_path");
        msgPath = confEms.getProfileString("general", "msg_path");
        attachPath = confEms.getProfileString("attach", "path");
        dnsServer = confEms.getProfileString("general", "nameserver");

        heloDomain =  confEms.getProfileString("sender", "helo_domain");
        defaultLang =  confEms.getProfileString("lang", "default");

        rejectUrl = confEms.getProfileString("url", "reject_url");
        urlEncryptKey = confEms.getProfileString("url", "aes_key");
        webUrl = confEms.getProfileString("url", "web_url");
        downloadUrl = confEms.getProfileString("url", "download");

        geoipDatabase = confEms.getProfileString("geoip", "database");
        sensdataPath = confEms.getProfileString("sensdata","path");
    }

    public static void main(String[] args) {
        ImConfLoaderEx confEms = new ImConfLoaderEx("sensems.home", "sensems.xml");
        String[] arrKey = confEms.getProfileStringArray("database.encryption", "aes_key.v1");
        System.out.println("arrKey = " + arrKey[0]);
    }
}

package com.imoxion.sensems.web.beans;

public class ImbErrorCount {
    /** 메일 메시지 아이디 */
    private String msgid;

    /** HOST UNKNOWN */
    private int unknownhost;

    /** 연결 에러 */
    private int connect_error;

    /** DNS 에러 */
    private int dns_error;

    /** 네트워크 에러 */
    private int network_error;

    /** 시스템 에러 */
    private int system_error;

    /** 서버연결 에러 */
    private int server_error;

    /** 명령어 에러 */
    private int syntax_error;

    /** USER UNKNOWN */
    private int userunknown;

    /** 메일박스 FULL */
    private int mboxfull;

    /** 기타 에러 */
    private int etc_error;

    /** 이메일 주소 형식 에러 */
    private int emailaddr_error;

    /** 수신거부 */
    private int reject_error;

    /** 중복 에러 */
    private int repeat_error;

    /** 차단 도메인 */
    private int domain_error;

    /** 이메일 주소 없음 */
    private int blankemail_error;

    /** 기타 에러 */
    private String extended;

    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }

    public int getUnknownhost() {
        return unknownhost;
    }

    public void setUnknownhost(int unknownhost) {
        this.unknownhost = unknownhost;
    }

    public int getConnect_error() {
        return connect_error;
    }

    public void setConnect_error(int connect_error) {
        this.connect_error = connect_error;
    }

    public int getDns_error() {
        return dns_error;
    }

    public void setDns_error(int dns_error) {
        this.dns_error = dns_error;
    }

    public int getNetwork_error() {
        return network_error;
    }

    public void setNetwork_error(int network_error) {
        this.network_error = network_error;
    }

    public int getSystem_error() {
        return system_error;
    }

    public void setSystem_error(int system_error) {
        this.system_error = system_error;
    }

    public int getServer_error() {
        return server_error;
    }

    public void setServer_error(int server_error) {
        this.server_error = server_error;
    }

    public int getSyntax_error() {
        return syntax_error;
    }

    public void setSyntax_error(int syntax_error) {
        this.syntax_error = syntax_error;
    }

    public int getUserunknown() {
        return userunknown;
    }

    public void setUserunknown(int userunknown) {
        this.userunknown = userunknown;
    }

    public int getMboxfull() {
        return mboxfull;
    }

    public void setMboxfull(int mboxfull) {
        this.mboxfull = mboxfull;
    }

    public int getEtc_error() {
        return etc_error;
    }

    public void setEtc_error(int etc_error) {
        this.etc_error = etc_error;
    }

    public int getEmailaddr_error() {
        return emailaddr_error;
    }

    public void setEmailaddr_error(int emailaddr_error) {
        this.emailaddr_error = emailaddr_error;
    }

    public int getReject_error() {
        return reject_error;
    }

    public void setReject_error(int reject_error) {
        this.reject_error = reject_error;
    }

    public int getRepeat_error() {
        return repeat_error;
    }

    public void setRepeat_error(int repeat_error) {
        this.repeat_error = repeat_error;
    }

    public int getDomain_error() {
        return domain_error;
    }

    public void setDomain_error(int domain_error) {
        this.domain_error = domain_error;
    }

    public int getBlankemail_error() {
        return blankemail_error;
    }

    public void setBlankemail_error(int blankemail_error) {
        this.blankemail_error = blankemail_error;
    }

    public String getExtended() {
        return extended;
    }

    public void setExtended(String extended) {
        this.extended = extended;
    }
}

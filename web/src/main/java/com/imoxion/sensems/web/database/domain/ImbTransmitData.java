package com.imoxion.sensems.web.database.domain;

import org.apache.ibatis.type.Alias;

import java.util.Date;

@Alias("transmitData")
public class ImbTransmitData {

    /** smtp traceid */
    private String traceid;

    /** 서버 아이디 */
    private String serverid;

    /** 수신시간 */
    private Date logdate;

    /** 제목 */
    private String subject;

    /** 송신자 이메일 */
    private String mailfrom;

    /** 수신자 이메일 */
    private String rcptto;

    /** 송신자 도메인 */
    private String from_domain;

    /** 수신자 도메인 */
    private String rcpt_domain;

    /** 송신자 아이피 */
    private String ip;

    /** 메일 크기 */
    private long mailsize;

    /** 송/수신 내부간 구분 */
    private String transmit_fl;

    /** */
    private String local_fl;

    /** 메일 발송 성공 여부 판단 */
    private String result;

    /** 메세지 코드 */
    private String description;

    /**  오류코드 */
    private int errcode;

    /** 기타 */
    private String etc;

    /**  에러메세지 */
    private String errmsg;

    /** original traceid */
    private String original_traceid;

    /** 인증 아이디 */
    private String authid;

    /** 발송 시스템에서 동보로 묶어서 보내기위한 그룹키 */
    private String group_key;

    /** 수신자별 고유 키 */
    private String rcpt_key;

    /** 수신확인일 */
    private Date readdate;

    private int readcount;

    /** 발송 구분 - T: 테스트메일, D: DB연동외부메일(WEB API이용), A: Smtp 인증, R: 릴레이연동 **/
    private String send_type;

    public String getTraceid() {
        return traceid;
    }

    public void setTraceid(String traceid) {
        this.traceid = traceid;
    }

    public String getServerid() {
        return serverid;
    }

    public void setServerid(String serverid) {
        this.serverid = serverid;
    }

    public Date getLogdate() {
        return logdate;
    }

    public void setLogdate(Date logdate) {
        this.logdate = logdate;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMailfrom() {
        return mailfrom;
    }

    public void setMailfrom(String mailfrom) {
        this.mailfrom = mailfrom;
    }

    public String getRcptto() {
        return rcptto;
    }

    public void setRcptto(String rcptto) {
        this.rcptto = rcptto;
    }

    public String getFrom_domain() {
        return from_domain;
    }

    public void setFrom_domain(String from_domain) {
        this.from_domain = from_domain;
    }

    public String getRcpt_domain() {
        return rcpt_domain;
    }

    public void setRcpt_domain(String rcpt_domain) {
        this.rcpt_domain = rcpt_domain;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public long getMailsize() {
        return mailsize;
    }

    public void setMailsize(long mailsize) {
        this.mailsize = mailsize;
    }

    public String getTransmit_fl() {
        return transmit_fl;
    }

    public void setTransmit_fl(String transmit_fl) {
        this.transmit_fl = transmit_fl;
    }

    public String getLocal_fl() {
        return local_fl;
    }

    public void setLocal_fl(String local_fl) {
        this.local_fl = local_fl;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getErrcode() {
        return errcode;
    }

    public void setErrcode(int errcode) {
        this.errcode = errcode;
    }

    public String getEtc() {
        return etc;
    }

    public void setEtc(String etc) {
        this.etc = etc;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public String getOriginal_traceid() {
        return original_traceid;
    }

    public void setOriginal_traceid(String original_traceid) {
        this.original_traceid = original_traceid;
    }

    public String getAuthid() {
        return authid;
    }

    public void setAuthid(String authid) {
        this.authid = authid;
    }

    public String getGroup_key() {
        return group_key;
    }

    public void setGroup_key(String group_key) {
        this.group_key = group_key;
    }

    public String getRcpt_key() {
        return rcpt_key;
    }

    public void setRcpt_key(String rcpt_key) {
        this.rcpt_key = rcpt_key;
    }

    public Date getReaddate() {
        return readdate;
    }

    public void setReaddate(Date readdate) {
        this.readdate = readdate;
    }

    public int getReadcount() {
        return readcount;
    }

    public void setReadcount(int readcount) {
        this.readcount = readcount;
    }

    public String getSend_type() {
        return send_type;
    }

    public void setSend_type(String send_type) {
        this.send_type = send_type;
    }
}

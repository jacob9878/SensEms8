/*
 * FileName : PEmsBean.java
 *
 * 작성자 : Administrator
 * 이메일 : Administrator@imoxion.com
 * Company : (주)아이모션
 * 날짜 : 2006. 11. 14
 *
 * 설명:
 * 
 */
package com.imoxion.sensems.web.beans;

public class PEmsBean {
    private String skey = "";
    private String extkey = "";
    private String authid = "";
    private String subject = "";
    private String from_email = "";
    private String replyto = "";
    private String regdate = "";
    private String send_date = "";
    private int total_send = 0;
    private String state = "";
    private String tbl_name = "";
    private String contents = "";
    private int attach_cnt = 0;
    
    public int getAttach_cnt() {
        return attach_cnt;
    }
    public void setAttach_cnt(int attach_cnt) {
        this.attach_cnt = attach_cnt;
    }
    public String getAuthid() {
        return authid;
    }
    public void setAuthid(String authid) {
        if (authid != null)
            this.authid = authid;
    }
    public String getContents() {
        return contents;
    }
    public void setContents(String contents) {
        if (contents != null)
            this.contents = contents;
    }
    public String getExtkey() {
        return extkey;
    }
    public void setExtkey(String extkey) {
        if (extkey != null)
            this.extkey = extkey;
    }
    public String getFrom_email() {
        return from_email;
    }
    public void setFrom_email(String from_email) {
        if (from_email != null)
            this.from_email = from_email;
    }
    public String getRegdate() {
        return regdate;
    }
    public void setRegdate(String regdate) {
        if (regdate != null)
            this.regdate = regdate;
    }
    public String getReplyto() {
        return replyto;
    }
    public void setReplyto(String replyto) {
        if (replyto != null)
            this.replyto = replyto;
    }
    public String getSend_date() {
        return send_date;
    }
    public void setSend_date(String send_date) {
        if (send_date != null)
            this.send_date = send_date;
    }
    public String getSkey() {
        return skey;
    }
    public void setSkey(String skey) {
        if (skey != null)
            this.skey = skey;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        if (state != null)
            this.state = state;
    }
    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        if (subject != null)
            this.subject = subject;
    }
    public String getTbl_name() {
        return tbl_name;
    }
    public void setTbl_name(String tbl_name) {
        if (tbl_name != null)
            this.tbl_name = tbl_name;
    }
    public int getTotal_send() {
        return total_send;
    }
    public void setTotal_send(int total_send) {
        this.total_send = total_send;
    }
    
    
    
    
}

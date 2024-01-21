/*
 * FileName : TempMainBean.java
 *
 * 작성자 : Administrator
 * 이메일 : Administrator@imoxion.com
 * Company : (주)아이모션
 * 날짜 : 2008. 12. 17
 *
 * 설명:
 * 
 */
package com.imoxion.sensems.web.beans;

import java.util.Date;

public class TempMainBean {
    private int idx = 0;
    private String campid = null;
    private String userid = null;
    private String mail_from = null;
    private String recname = null;
    private String templateid = null;
    private String title = null;
    private String body = null;
    private String reserve_date = null;
    private Date regdate = null;
    private String resp_time = null;
    private String isattach = null;
    private String islink = null;
    private String charset = "euc-kr";
    public int getIdx() {
        return idx;
    }
    public void setIdx(int idx) {
        this.idx = idx;
    }
    public String getCampid() {
        return campid;
    }
    public void setCampid(String campid) {
        if (campid != null)
            this.campid = campid;
    }
    public String getUserid() {
        return userid;
    }
    public void setUserid(String userid) {
        if (userid != null)
            this.userid = userid;
    }
    public String getMail_from() {
        return mail_from;
    }
    public void setMail_from(String mail_from) {
        if (mail_from != null)
            this.mail_from = mail_from;
    }
    public String getRecname() {
        return recname;
    }
    public void setRecname(String recname) {
        if (recname != null)
            this.recname = recname;
    }
    public String getTemplateid() {
        return templateid;
    }
    public void setTemplateid(String templateid) {
        if (templateid != null)
            this.templateid = templateid;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        if (title != null)
            this.title = title;
    }
    public String getBody() {
        return body;
    }
    public void setBody(String body) {
        if (body != null)
            this.body = body;
    }
    public String getReserve_date() {
        return reserve_date;
    }
    public void setReserve_date(String reserve_date) {
        if (reserve_date != null)
            this.reserve_date = reserve_date;
    }
    public Date getRegdate() {
        return regdate;
    }
    public void setRegdate(Date regdate) {
        if (regdate != null)
            this.regdate = regdate;
    }
    public String getResp_time() {
        return resp_time;
    }
    public void setResp_time(String resp_time) {
        if (resp_time != null)
            this.resp_time = resp_time;
    }
    public String getIsattach() {
        return isattach;
    }
    public void setIsattach(String isattach) {
        if (isattach != null)
            this.isattach = isattach;
    }
    public String getIslink() {
        return islink;
    }
    public void setIslink(String islink) {
        if (islink != null)
            this.islink = islink;
    }
    public String getCharset() {
        return charset;
    }
    public void setCharset(String charset) {
        if (charset != null)
            this.charset = charset;
    }
    
    
}

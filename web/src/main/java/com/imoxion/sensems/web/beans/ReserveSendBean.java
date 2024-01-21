package com.imoxion.sensems.web.beans;

import org.apache.ibatis.type.Alias;

public class ReserveSendBean {
    private String categoryid;
    private String msgid;
    private String userid;
    private String recid;
    private String mail_from;
    private String replyto;
    private String templateid;
    private String msg_name;
    private String regdate;
    private String start_time;
    private String end_time;
    private String rot_flag;
    private String rot_point;
    private String send_time;

    /** 최근발송일  */
    private String last_send;
    private String charset;
    private String islink;
    private String ishtml;
    private String extended;

    public String getCategoryid() {
        return categoryid;
    }

    public void setCategoryid(String categoryid) {
        this.categoryid = categoryid;
    }

    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getRecid() {
        return recid;
    }

    public void setRecid(String recid) {
        this.recid = recid;
    }

    public String getMail_from() {
        return mail_from;
    }

    public void setMail_from(String mail_from) {
        this.mail_from = mail_from;
    }

    public String getReplyto() {
        return replyto;
    }

    public void setReplyto(String replyto) {
        this.replyto = replyto;
    }

    public String getTemplateid() {
        return templateid;
    }

    public void setTemplateid(String templateid) {
        this.templateid = templateid;
    }

    public String getMsg_name() {
        return msg_name;
    }

    public void setMsg_name(String msg_name) {
        this.msg_name = msg_name;
    }

    public String getRegdate() {
        return regdate;
    }

    public void setRegdate(String regdate) {
        this.regdate = regdate;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getRot_flag() {
        return rot_flag;
    }

    public void setRot_flag(String rot_flag) {
        this.rot_flag = rot_flag;
    }

    public String getRot_point() {
        return rot_point;
    }

    public void setRot_point(String rot_point) {
        this.rot_point = rot_point;
    }

    public String getSend_time() {
        return send_time;
    }

    public void setSend_time(String send_time) {
        this.send_time = send_time;
    }

    public String getLast_send() {
        return last_send;
    }

    public void setLast_send(String last_send) {
        this.last_send = last_send;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getIslink() {
        return islink;
    }

    public void setIslink(String islink) {
        this.islink = islink;
    }

    public String getIshtml() {
        return ishtml;
    }

    public void setIshtml(String ishtml) {
        this.ishtml = ishtml;
    }

    public String getExtended() {
        return extended;
    }

    public void setExtended(String extended) {
        this.extended = extended;
    }
}

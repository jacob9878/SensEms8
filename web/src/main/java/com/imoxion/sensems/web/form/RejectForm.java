package com.imoxion.sensems.web.form;

import java.util.Date;

public class RejectForm {

    private String email; //이메일
    private String msgid ; // 메일메시지 아이디
    private Date regdate; // 등록일


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }

    public Date getRegdate() {
        return regdate;
    }

    public void setRegdate(Date regdate) {
        this.regdate = regdate;
    }
}

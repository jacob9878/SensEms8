package com.imoxion.sensems.web.database.domain;

import org.apache.ibatis.type.Alias;

import java.util.Date;


@Alias("rejectInfo")
public class ImbReject {

    private String email; //이메일
    private String ori_email; //변경전 이메일
    private String msgid ; // 메일메시지 아이디
    private Date regdate; // 등록일

    public String getOri_email() {
        return ori_email;
    }

    public void setOri_email(String ori_email) {
        this.ori_email = ori_email;
    }

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

    public Date getRegdate() {       return regdate;    }

    public void setRegdate(Date regdate) {        this.regdate = regdate;    }
}

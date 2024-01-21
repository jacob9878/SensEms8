package com.imoxion.sensems.web.form;

import java.util.Date;

public class RelayForm {

    private String ip; //ip
    private String memo ; // 메모
    private Date regdate; // 등록일


    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }


    public Date getRegdate() {
        return regdate;
    }

    public void setRegdate(Date regdate) {
        this.regdate = regdate;
    }
}

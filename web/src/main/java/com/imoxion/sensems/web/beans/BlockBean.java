package com.imoxion.sensems.web.beans;

public class BlockBean {

    private String ip;
    private String userid;
    private String memo;
    private String regdate;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        if (userid != null)
            this.userid = userid;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getRegdate() {
        return regdate;
    }

    public void setRegdate(String regdate) {
        this.regdate = regdate;
    }
}

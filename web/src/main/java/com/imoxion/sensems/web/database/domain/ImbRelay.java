package com.imoxion.sensems.web.database.domain;

import org.apache.ibatis.type.Alias;

import java.util.Date;


@Alias("relayInfo")
public class ImbRelay {

    private String ip; // 아이피
    private String ori_ip; //변경전 아이피
    private String memo ; // 아이피 설명
    private Date regdate; // 등록일

    public String getOri_ip() {
        return ori_ip;
    }

    public void setOri_ip(String ori_ip) {
        this.ori_ip = ori_ip;
    }

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

    public Date getRegdate() {     return regdate;    }

    public void setRegdate(Date regdate) {      this.regdate = regdate;    }
}

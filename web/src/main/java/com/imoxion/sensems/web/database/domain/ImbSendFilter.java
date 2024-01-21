package com.imoxion.sensems.web.database.domain;

import java.util.Date;
import org.apache.ibatis.type.Alias;

@Alias("sendfilterInfo")
public class ImbSendFilter {

    private String hostname; // 발송 차단할 도메인명

    private Date regdate; // 차단 등록 일자

    public Date getRegdate() {
        return regdate;
    }

    public void setRegdate(Date regdate) {
        this.regdate = regdate;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }
}

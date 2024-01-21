package com.imoxion.sensems.server.domain;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.type.Alias;

import java.util.Date;

@Getter
@Setter
@Alias("SmtpTempMain")
public class SmtpTempMain {
    private String mainkey;
    private String subject;
    private String mailfrom;
    private String ip;
    private String group_key;
    private Date regdate;
    private String body;

    private String fromEmail;

    /** T: 테스트메일, D: DB연동외부메일(WEB API이용), A: Smtp 인증, R: 릴레이연동, C:건별재발신 **/
    private String send_type;

    @Override
    public String toString() {
        return "TempMain{" +
                "mainkey='" + mainkey + '\'' +
                ", subject='" + subject + '\'' +
                ", mailfrom='" + mailfrom + '\'' +
                ", group_key='" + group_key + '\'' +
                ", ip='" + ip + '\'' +
                ", regdate=" + regdate +
                ", send_type=" + send_type +
                ", body='" + StringUtils.substring(body, 0, 255) + '\'' +
                '}';
    }
}

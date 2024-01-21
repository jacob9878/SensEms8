package com.imoxion.sensems.server.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.ibatis.type.Alias;

import java.util.Date;

@Setter
@Getter
@ToString(exclude = "passwd")
@Alias("SmtpAccount")
public class SmtpAccount {
    private String sid;
    private String passwd;
    private String pwd_type;    // default sha-256
    private String st_data;     // salt
    private String description;
    private Date regdate;
}

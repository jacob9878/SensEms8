package com.imoxion.sensems.server.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.ibatis.type.Alias;

@Getter
@Setter
@ToString
@Alias("SmtpTempRcpt")
public class SmtpTempRcpt {
    private Long idx;
    private String mainkey;
    private String rcptto;
    private String rcpt_key;
}

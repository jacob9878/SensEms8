package com.imoxion.sensems.server.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.ibatis.type.Alias;

import java.util.Date;

@Getter
@Setter
@ToString
@Alias("Dkim")
public class Dkim {
    private String domain;
    private String selector;
    private String filename;
    private String public_key;
    private Date regdate;
    private String use_sign;
    private byte[] private_key;
}

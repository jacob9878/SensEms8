package com.imoxion.sensems.server.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.ibatis.type.Alias;

import java.util.Date;

@Setter
@Getter
@ToString
@Alias("DenyIp")
public class DenyIp {
    private String ip;
    private String etc;
    private Date regdate;
}

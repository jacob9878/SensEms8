package com.imoxion.sensems.server.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Setter
@Getter
@ToString
public class ImbActionLog {
    private String log_key;
    private Date log_date;
    private String ip;
    private String userid;
    private String menu_key;
    private String param;

}

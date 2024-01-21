package com.imoxion.sensems.server.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Setter
@Getter
@ToString
public class ImbEmsAttach {
    private String ekey;
    private String msgid;
    private String file_name;
    private String file_size;
    private String file_path;
    private Date regdate;
    private Date expire_date;
    private int down_count;
}

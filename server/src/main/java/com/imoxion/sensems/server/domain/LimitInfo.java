package com.imoxion.sensems.server.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.ibatis.type.Alias;

@Getter
@Setter
@ToString
@Alias("LimitInfo")
public class LimitInfo {
    private String limit_type;
    private String limit_value;
    private String descript;
}

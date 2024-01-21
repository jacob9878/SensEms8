package com.imoxion.sensems.server.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.ibatis.type.Alias;

import java.util.Date;

@Setter
@Getter
@ToString
@Alias("BlockEmail")
public class BlockEmail {
    private String email;
    private String description;
    private Date regdate;
}

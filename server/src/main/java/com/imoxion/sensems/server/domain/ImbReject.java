package com.imoxion.sensems.server.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.ibatis.type.Alias;

import java.util.Date;

@Setter
@Getter
@ToString
public class ImbReject {

    private String email; //이메일
    private String msgid ; // 메일메시지 아이디
    private Date regdate; // 등록일

}

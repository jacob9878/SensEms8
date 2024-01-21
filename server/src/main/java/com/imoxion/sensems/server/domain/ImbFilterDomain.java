package com.imoxion.sensems.server.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.ibatis.type.Alias;

@Setter
@Getter
@ToString
public class ImbFilterDomain {

    private String hostname; // 발송 차단할 도메인명

}

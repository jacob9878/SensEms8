package com.imoxion.sensems.server.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class ImbHostInfo {
    private String hostname;    // sender 서버 아이피
    private int port = 0;       // 9090, 9091, 9092…등
    private int target = 0;     // 0: 대량+개별, 1:개별
    private int iscative = 1;

}

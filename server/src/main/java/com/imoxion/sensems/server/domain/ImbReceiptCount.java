package com.imoxion.sensems.server.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class ImbReceiptCount {
    private String msgid;
    private int recv_count = 0;
}

package com.imoxion.sensems.server.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class ImbLinkCount {
    private String msgid;
    private int linkid;
    private int link_count;
}

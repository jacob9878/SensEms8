package com.imoxion.sensems.server.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class ImbLinkInfo {
    private String msgid;
    private int linkid;
    private String link_name;
    private String link_url;
    private String link_img;
}

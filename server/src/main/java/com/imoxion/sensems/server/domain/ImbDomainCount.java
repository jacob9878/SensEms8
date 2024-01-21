package com.imoxion.sensems.server.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class ImbDomainCount {
    private String domain;
    private int successCount = 0;
    private int failCount = 0;
}

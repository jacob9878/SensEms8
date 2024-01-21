package com.imoxion.sensems.server.domain;

import lombok.*;

@RequiredArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class ImbMsgidHostCount {
    private String hostname;
    private int scount; // send count
    private int ecount; // error count
    private int eration;

}

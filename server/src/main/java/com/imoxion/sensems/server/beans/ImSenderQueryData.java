package com.imoxion.sensems.server.beans;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@RequiredArgsConstructor
public class ImSenderQueryData {
    private String query;
    private int id;
    private String field1;
    private String errStr;
    private int success = 0;
    private int reponse = 0;
    private ImRecvRecordData rData = null;
}

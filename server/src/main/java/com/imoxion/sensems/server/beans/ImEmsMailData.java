package com.imoxion.sensems.server.beans;

import com.imoxion.sensems.server.domain.ImbEmsMain;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class ImEmsMailData implements Serializable {
    private ImbEmsMain emsMain;
    private int id;
    private String mailFrom;
    private String replyTo;
    private String domain;
    private String rcptTo;
    private ImRecvRecordData recordData;
    private String updateQuery;

}

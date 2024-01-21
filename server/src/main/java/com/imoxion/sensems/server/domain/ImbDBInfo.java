package com.imoxion.sensems.server.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * yeji
 * 2021. 03. 03
 * 데이터베이스관리 관련 bean 객체
 */
@Setter
@Getter
@ToString
public class ImbDBInfo {

    private String ukey; //고유키

    private String dbname; //DB 이름

    private String dbtype; //DB 유형

    private String userid; //userid

    private String dbhost; //DB 호스트

    private String dbuser; //DB user

    private String dbpasswd; //DB pw

    private Date regdate; //등록일

    private String dbcharset; //캐릭터셋

    private String datacharset; //데이터캐릭터셋

    private String address; //jdbc url

    private String dbport; //DB포트

    public String getDbport() {
        return dbport;
    }

    public void setDbport(String dbport) {
        this.dbport = dbport;
    }



}

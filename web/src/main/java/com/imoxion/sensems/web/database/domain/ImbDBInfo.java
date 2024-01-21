package com.imoxion.sensems.web.database.domain;

import org.apache.ibatis.type.Alias;

import java.util.Date;

/**
 * yeji
 * 2021. 03. 03
 * 데이터베이스관리 관련 bean 객체
 */
@Alias("dbinfo")
public class ImbDBInfo {

    public static final String DB_TYPE__MYSQL = "mysql";

    public static final String DB_TYPE__ORACLE = "oracle";

    public static final String DB_TYPE__MSSQL = "mssql";

    public static final String DB_TYPE__TIBERO = "tibero";

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

    public String getUkey() {
        return ukey;
    }

    public void setUkey(String ukey) {
        this.ukey = ukey;
    }

    public String getDbname() {
        return dbname;
    }

    public void setDbname(String dbname) {
        this.dbname = dbname;
    }

    public String getDbtype() {
        return dbtype;
    }

    public void setDbtype(String dbtype) {
        this.dbtype = dbtype;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getDbhost() {
        return dbhost;
    }

    public void setDbhost(String dbhost) {
        this.dbhost = dbhost;
    }

    public String getDbuser() {
        return dbuser;
    }

    public void setDbuser(String dbuser) {
        this.dbuser = dbuser;
    }

    public String getDbpasswd() {
        return dbpasswd;
    }

    public void setDbpasswd(String dbpasswd) {
        this.dbpasswd = dbpasswd;
    }

    public Date getRegdate() {
        return regdate;
    }

    public void setRegdate(Date regdate) {
        this.regdate = regdate;
    }

    public String getDbcharset() {
        return dbcharset;
    }

    public void setDbcharset(String dbcharset) {
        this.dbcharset = dbcharset;
    }

    public String getDatacharset() {
        return datacharset;
    }

    public void setDatacharset(String datacharset) {
        this.datacharset = datacharset;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "dbinfo{" +
                ", ukey:" + ukey +
                ", dbname:" + dbname +
                ", dbtype:" + dbtype +
                ", userid:" + userid +
                ", dbhost:" + dbhost +
                ", dbport:" + dbport +
                ", dbuser:" + dbuser +
                ", dbpasswd:" + dbpasswd +
                ", dbcharset:" + dbcharset +
                ", datacharset:" + datacharset +
                ", address:" + address +
                "}";
    }

}

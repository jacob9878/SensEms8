package com.imoxion.sensems.web.database.domain;

import org.apache.ibatis.type.Alias;

import java.util.Date;

@Alias("receiverInfo")
public class ImbReceiver {

    /** 수신그룹 고유 키 24자리 */
    private String ukey;

    /** 수신그룹 작성자 */
    private String userid;

    /** 수신 그룹명 */
    private String recv_name;

    /** Imb_dbinfo의 DB 고유 키 24자리 */
    private String dbkey;

    /** 수신그룹 추출 SQL 쿼리문 */
    private String query;

    /** 등록 날짜 */
    private Date regdate;

    /** 기타 */
    private String extended;

    public String getUkey() {
        return ukey;
    }

    public void setUkey(String ukey) {
        this.ukey = ukey;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getRecv_name() {
        return recv_name;
    }

    public void setRecv_name(String recv_name) {
        this.recv_name = recv_name;
    }

    public String getDbkey() {
        return dbkey;
    }

    public void setDbkey(String dbkey) {
        this.dbkey = dbkey;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Date getRegdate() {
        return regdate;
    }

    public void setRegdate(Date regdate) {
        this.regdate = regdate;
    }

    public String getExtended() {
        return extended;
    }

    public void setExtended(String extended) {
        this.extended = extended;
    }
}

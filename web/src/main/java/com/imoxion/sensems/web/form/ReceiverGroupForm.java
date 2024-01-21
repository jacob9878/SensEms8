package com.imoxion.sensems.web.form;

public class ReceiverGroupForm {

    /** 수신그룹 고유 키 24자리 */
    private String ukey;

    /** 작성자 아이디 */
    private String userid;

    /** 수신그룹명 */
    private String recv_name;

    /** Imb_dbinfo 고유키 24자리 */
    private String dbkey;

    /** Imb_dbinfo의 DB 고유 키 24자리 */
    private String query;

    /** 수정 전 수신그룹명*/
    private String ori_name;

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

    public String getOri_name() {
        return ori_name;
    }

    public void setOri_name(String ori_name) {
        this.ori_name = ori_name;
    }
}

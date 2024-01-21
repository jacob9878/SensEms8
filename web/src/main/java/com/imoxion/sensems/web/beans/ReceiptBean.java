package com.imoxion.sensems.web.beans;

import org.apache.ibatis.type.Alias;

import java.util.Date;

@Alias("receipt")
public class ReceiptBean {
    String srch_keyword;
    String searchKeywordRcode;
    String searchKeywordMsgid;
    String msgid;
    String field1;
    String field2;
    String msg_name;
    String mailfrom;
    String recv_time;
    String id;
//    int start;
//    int end;


    public String getRecv_time() {
        return recv_time;
    }

    public String getField1() {
        return field1;
    }

    public String getField2() {
        return field2;
    }

    public String getMsg_name() {
        return msg_name;
    }

    public void setField1(String field1) {
        this.field1 = field1;
    }


    public void setRecv_time(String recv_time) {
        this.recv_time = recv_time;
    }

    public void setField2(String field2) {
        this.field2 = field2;
    }

    public void setMsg_name(String msg_name) {
        this.msg_name = msg_name;
    }

    public String getSearchKeywordMsgid() {
        return searchKeywordMsgid;
    }

    public String getSearchKeywordRcode() {
        return searchKeywordRcode;
    }

    public void setSearchKeywordMsgid(String searchKeywordMsgid) {
        this.searchKeywordMsgid = searchKeywordMsgid;
    }

    public void setSearchKeywordRcode(String searchKeywordRcode) {
        this.searchKeywordRcode = searchKeywordRcode;
    }


    public String getSrch_keyword() {
        return srch_keyword;
    }

//    public int getStart() {
//        return start;
//    }
//
//    public int getEnd() {
//        return end;
//    }


    public void setSrch_keyword(String srch_keyword) {
        this.srch_keyword = srch_keyword;
    }

//    public void setStart(int start) {
//        this.start = start;
//    }
//
//    public void setEnd(int end) {
//        this.end = end;
//    }

    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMailfrom() {return mailfrom;}

    public void setMailfrom(String mailfrom) {this.mailfrom = mailfrom;}

}

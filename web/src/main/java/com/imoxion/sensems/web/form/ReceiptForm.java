package com.imoxion.sensems.web.form;

import java.util.Date;

public class ReceiptForm {
    String recv_date;

    String msgid;

    private String searchKeywordRcode;

    private String searchKeywordMsgid;

    private int cpage = 1;

    private int pagesize = 15;

    int start;

    int end;


    public void setSearchKeywordRcode(String searchKeywordRcode) {
        this.searchKeywordRcode = searchKeywordRcode;
    }

    public void setSearchKeywordMsgid(String searchKeywordMsgid) {
        this.searchKeywordMsgid = searchKeywordMsgid;
    }

    public String getSearchKeywordRcode() {
        return searchKeywordRcode;
    }

    public String getSearchKeywordMsgid() {
        return searchKeywordMsgid;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public void setStart(int start) {
        this.start = start;
    }


    public int getCpage() {
        return cpage;
    }

    public void setCpage(int cpage) {
        this.cpage = cpage;
    }

    public int getPagesize() {
        return pagesize;
    }

    public void setPagesize(int pagesize) {
        this.pagesize = pagesize;
    }

    public String getRecv_date() {return recv_date;}

    public void setRecv_date(String recv_date) {this.recv_date = recv_date;}

    public String getMsgid() {return msgid;}

    public void setMsgid(String msgid) {this.msgid = msgid;}
}

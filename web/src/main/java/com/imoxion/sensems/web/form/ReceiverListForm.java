package com.imoxion.sensems.web.form;

public class ReceiverListForm {

    private String cpage = "1";

    private String pagegroupsize = "5";

    private String srch_type;

    private String srch_keyword;

    private String msgid;

    private String recv_date;

    private String receipt;

    private String unreceipt;

    public String recv_count;

    public String getRecv_count() {
        return recv_count;
    }

    public void setRecv_count(String recv_count) {
        this.recv_count = recv_count;
    }

    public String getRecv_date() {
        return recv_date;
    }

    public void setRecv_date(String recv_date) {
        this.recv_date = recv_date;
    }

    public String getReceipt() {
        return receipt;
    }

    public void setReceipt(String receipt) {
        this.receipt = receipt;
    }

    public String getUnreceipt() {
        return unreceipt;
    }

    public void setUnreceipt(String unreceipt) {
        this.unreceipt = unreceipt;
    }

    public String getCpage() {
        return cpage;
    }

    public void setCpage(String cpage) {
        this.cpage = cpage;
    }

    public String getPagegroupsize() {
        return pagegroupsize;
    }

    public void setPagegroupsize(String pagegroupsize) {
        this.pagegroupsize = pagegroupsize;
    }

    public String getSrch_type() {
        return srch_type;
    }

    public void setSrch_type(String srch_type) {
        this.srch_type = srch_type;
    }

    public String getSrch_keyword() {
        return srch_keyword;
    }

    public void setSrch_keyword(String srch_keyword) {
        this.srch_keyword = srch_keyword;
    }

    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }
}

package com.imoxion.sensems.web.form;

public class ReceiverAddrListForm {

    private String cpage = "1";
    private String pagegroupsize = "5";
    private String msgid;

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

    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }
}

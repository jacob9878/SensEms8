package com.imoxion.sensems.web.form;

public class RejectListForm {
    private String cpage = "1";

    private String pagegroupsize = "5";

    private String srch_type;

    private String srch_keyword;

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

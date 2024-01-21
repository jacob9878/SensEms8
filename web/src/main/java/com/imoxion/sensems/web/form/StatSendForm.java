package com.imoxion.sensems.web.form;

public class StatSendForm {

    private String cpage = "1";

    private String pagegroupsize = "5";

    private String msgid;

    private String resp_time;

    private String resp_hour;

    private String resp_min;

    public String getResp_time() {
        return resp_time;
    }

    public void setResp_time(String resp_time) {
        this.resp_time = resp_time;
    }

    public String getCpage() {
        return cpage;
    }

    public String getPagegroupsize() {
        return pagegroupsize;
    }

    public String getMsgid() {
        return msgid;
    }

    public void setCpage(String cpage) {
        this.cpage = cpage;
    }

    public void setPagegroupsize(String pagegroupsize) {
        this.pagegroupsize = pagegroupsize;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }

    public String getResp_hour() {return resp_hour;}

    public void setResp_hour(String resp_hour) {this.resp_hour = resp_hour; }

    public String getResp_min() {return resp_min; }

    public void setResp_min(String resp_min) {this.resp_min = resp_min; }
}

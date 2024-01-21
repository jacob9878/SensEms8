package com.imoxion.sensems.web.form;

public class ReserveSendListForm {
    private String cpage = "1";

    private String pagegroupsize = "5";

    private String msgid;

    private String rot_flag;

    private String msg_name;

    private String start_time;

    private String end_time;

    private String regdate;

    public String getRot_flag() {
        return rot_flag;
    }

    public void setRot_flag(String rot_flag) {
        this.rot_flag = rot_flag;
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

    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }

    public String getMsg_name() {
        return msg_name;
    }

    public void setMsg_name(String msg_name) {
        this.msg_name = msg_name;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getRegdate() {
        return regdate;
    }

    public void setRegdate(String regdate) {
        this.regdate = regdate;
    }
}

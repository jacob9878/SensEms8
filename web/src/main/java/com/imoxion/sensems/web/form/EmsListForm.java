package com.imoxion.sensems.web.form;

public class EmsListForm {
    private String msgid;
    private String msg_name;
    private String userid;
    private String contents;
    private String cpage = "1";
    private String pagegroupsize = "5";
    private String srch_keyword="";

    /**
     * state 값 참조
     * 000: 발송대기
     * 007: 임시보관중
     * 010: 수신자 추출중
     * 030: 발송중
     * 031: 발송중지
     * 032: 재전송중
     * 040: 로그 정리중
     * -00: 수신대상자 없음
     * -10: 수신자목록 생성실패
     * +10: 수신자 추출완료
     * -20: 수신리스트 생성실패(제한초과)
     * +30: 발송완료
     * 100: 전송중지
     * 111: 승인대기중
     */
    private String state;

    private String cur_send;
    private String total_send;
    private String regdate;
    private String regdate_date;
    private String regdate_time;
    private String start_time;
    private String start_time_date;
    private String start_time_time;
    private String categoryid;

    public String getCategoryid() {
        return categoryid;
    }

    public void setCategoryid(String categoryid) {
        this.categoryid = categoryid;
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

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCur_send() {
        return cur_send;
    }

    public void setCur_send(String cur_send) {
        this.cur_send = cur_send;
    }

    public String getTotal_send() {
        return total_send;
    }

    public void setTotal_send(String total_send) {
        this.total_send = total_send;
    }

    public String getRegdate() {
        return regdate;
    }

    public void setRegdate(String regdate) {
        this.regdate = regdate;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getSrch_keyword() {
        return srch_keyword;
    }

    public void setSrch_keyword(String srch_keyword) {
        this.srch_keyword = srch_keyword;
    }

    public String getRegdate_date() {
        return regdate_date;
    }

    public void setRegdate_date(String regdate_date) {
        this.regdate_date = regdate_date;
    }

    public String getRegdate_time() {
        return regdate_time;
    }

    public void setRegdate_time(String regdate_time) {
        this.regdate_time = regdate_time;
    }

    public String getStart_time_date() {
        return start_time_date;
    }

    public void setStart_time_date(String start_time_date) {
        this.start_time_date = start_time_date;
    }

    public String getStart_time_time() {
        return start_time_time;
    }

    public void setStart_time_time(String start_time_time) {
        this.start_time_time = start_time_time;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }
}

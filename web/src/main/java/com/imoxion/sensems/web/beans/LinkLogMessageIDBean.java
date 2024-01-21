package com.imoxion.sensems.web.beans;

public class LinkLogMessageIDBean {
    private String msgid;

    /** 링크 아이디 */
    private int adid;

    /** RECV_메시지아이디 테이블의 ID */
    private int userid;

    /** 클릭 횟수 */
    private int click_count;

    /** 클릭 시간 */
    private String click_time;

    /** 링크 년월일 */
    private String link_date;

    /** 링크 시간  */
    private String link_hour;

    /** 52주차중 몇주차인지  */
    private int link_week;

    /** 기타 */
    private String extended;

    /** 수신자 이메일 */
    private String field1;
    private String field2;
    private String field3;
    private String field4;

    public String getField1() {
        return field1;
    }

    public void setField1(String field1) {
        this.field1 = field1;
    }

    public String getField2() {
        return field2;
    }

    public void setField2(String field2) {
        this.field2 = field2;
    }

    public String getField3() {
        return field3;
    }

    public void setField3(String field3) {
        this.field3 = field3;
    }

    public String getField4() {
        return field4;
    }

    public void setField4(String field4) {
        this.field4 = field4;
    }

    public String getMsgid() { return msgid; }

    public void setMsgid(String msgid) { this.msgid = msgid; }

    public String getLink_date() {
        return link_date;
    }

    public void setLink_date(String link_date) {
        this.link_date = link_date;
    }

    public String getLink_hour() {
        return link_hour;
    }

    public void setLink_hour(String link_hour) {
        this.link_hour = link_hour;
    }

    public int getAdid() {
		return adid;
	}

	public void setAdid(int adid) {
		this.adid = adid;
	}

	public int getLink_week() {
		return link_week;
	}

	public void setLink_week(int link_week) {
		this.link_week = link_week;
	}

	public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getClick_count() {
        return click_count;
    }

    public void setClick_count(int click_count) {
        this.click_count = click_count;
    }

    public String getClick_time() {
        return click_time;
    }

    public void setClick_time(String click_time) {
        this.click_time = click_time;
    }

    public String getExtended() {
        return extended;
    }

    public void setExtended(String extended) {
        this.extended = extended;
    }
}

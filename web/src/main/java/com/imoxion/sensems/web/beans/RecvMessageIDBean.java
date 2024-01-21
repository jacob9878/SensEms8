package com.imoxion.sensems.web.beans;

public class RecvMessageIDBean {
	
	/** 메세지 아이디 */
	private String msgid;

    /** 고유 아이디 */
    private String id;

    /** 도메인 */
    private String domain;

    /** 성공여부 */
    private String success;

    /** 에러코드 */
    private String errcode;

    /** 에러 원인 */
    private String err_exp;

    /** 발송일 */
    private String send_time;

    /** 수신확인 일 yyyyMMddHHmmss */
    private String recv_time;

    /** 수신확인 횟수 */
    private int recv_count;
    
    /** 수신확인 날짜 yyyyMMdd*/
    private String recv_date;
    
    /** 수신확인 시간 HH */
    private String recv_hour;
    
    /** 52주중 몇번째 주인지 */
    private int recv_week;

    /** 재발송 **/
    private int is_resend;

    /** 수신자 이메일 */
    private String field1;
    private String field2;
    private String field3;
    private String field4;
    private String field5;
    private String field6;
    private String field7;
    private String field8;
    private String field9;

    public int getIs_resend() {
        return is_resend;
    }

    public void setIs_resend(int is_resend) {
        this.is_resend = is_resend;
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

    public String getField5() {
        return field5;
    }

    public void setField5(String field5) {
        this.field5 = field5;
    }

    public String getField6() {
        return field6;
    }

    public void setField6(String field6) {
        this.field6 = field6;
    }

    public String getId() {
        return id;
    }

    public String getMsgid() {
		return msgid;
	}

	public void setMsgid(String msgid) {
		this.msgid = msgid;
	}

	public String getRecv_date() {
		return recv_date;
	}

	public void setRecv_date(String recv_date) {
		this.recv_date = recv_date;
	}

	public String getRecv_hour() {
		return recv_hour;
	}

	public void setRecv_hour(String recv_hour) {
		this.recv_hour = recv_hour;
	}

	public int getRecv_week() {
		return recv_week;
	}

	public void setRecv_week(int recv_week) {
		this.recv_week = recv_week;
	}

	public void setId(String id) {
        this.id = id;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getErrcode() {
        return errcode;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }

    public String getErr_exp() {
        return err_exp;
    }

    public void setErr_exp(String err_exp) {
        this.err_exp = err_exp;
    }

    public String getSend_time() {
        return send_time;
    }

    public void setSend_time(String send_time) {
        this.send_time = send_time;
    }

    public String getRecv_time() {
        return recv_time;
    }

    public void setRecv_time(String recv_time) {
        this.recv_time = recv_time;
    }

    public int getRecv_count() {
        return recv_count;
    }

    public void setRecv_count(int recv_count) {
        this.recv_count = recv_count;
    }

    public String getField1() {
        return field1;
    }

    public void setField1(String field1) {
        this.field1 = field1;
    }

    public String getField7() { return field7; }

    public void setField7(String field7) { this.field7 = field7; }

    public String getField8() { return field8; }

    public void setField8(String field8) { this.field8 = field8; }

    public String getField9() { return field9; }

    public void setField9(String field9) { this.field9 = field9; }
}

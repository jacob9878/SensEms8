package com.imoxion.sensems.web.beans;

public class IndividualBean {

	private String id = null;
	private String userid = null;
	private String ukey = null;
	private String f_key = null;
	private String tempid = null;
	private String from_email = null;
	private String to_email = null;
	private String field1 = null;
	private String field2 = null;
	private String field3 = null;
	private String field4 = null;
	private String field5 = null;
	private String success = null;
	private String errcode = null;
	private String err_exp = null;
	private String send_time = null;
	private String recv_time = null;
    private String subject = null;
    private String body = null;
    private String charset = null;
	private int recv_count = 0;
	private String state = "0";
	private int ishtml = 1;
	

	public String getErr_exp() {
		return err_exp;
	}
	public void setErr_exp(String err_exp) {
		this.err_exp = err_exp;
	}
	public String getErrcode() {
		return errcode;
	}
	public void setErrcode(String errcode) {
		this.errcode = errcode;
	}
	public String getF_key() {
		return f_key;
	}
	public void setF_key(String f_key) {
		this.f_key = f_key;
	}
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
	public String getFrom_email() {
		return from_email;
	}
	public void setFrom_email(String from_email) {
		this.from_email = from_email;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getRecv_count() {
		return recv_count;
	}
	public void setRecv_count(int recv_count) {
		this.recv_count = recv_count;
	}
	public String getRecv_time() {
		return recv_time;
	}
	public void setRecv_time(String recv_time) {
		this.recv_time = recv_time;
	}
	public String getSend_time() {
		return send_time;
	}
	public void setSend_time(String send_time) {
		this.send_time = send_time;
	}
	public String getSuccess() {
		return success;
	}
	public void setSuccess(String success) {
		this.success = success;
	}
    public String getTo_email() {
        return to_email;
    }
    public void setTo_email(String to_email) {
        this.to_email = to_email;
    }
    public String getBody() {
        return body;
    }
    public void setBody(String body) {
        this.body = body;
    }
    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }
    public String getCharset() {
        return charset;
    }
    public void setCharset(String charset) {
        this.charset = charset;
    }
    public String getUserid() {
        return userid;
    }
    public void setUserid(String userid) {
        if (userid != null)
            this.userid = userid;
    }
    public String getUkey() {
        return ukey;
    }
    public void setUkey(String ukey) {
        if (ukey != null)
            this.ukey = ukey;
    }
    public String getTempid() {
        return tempid;
    }
    public void setTempid(String tempid) {
        if (tempid != null)
            this.tempid = tempid;
    }
    public String getField4() {
        return field4;
    }
    public void setField4(String field4) {
        if (field4 != null)
            this.field4 = field4;
    }
    public String getField5() {
        return field5;
    }
    public void setField5(String field5) {
        if (field5 != null)
            this.field5 = field5;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        if (state != null)
            this.state = state;
    }
    public int getIshtml() {
        return ishtml;
    }
    public void setIshtml(int ishtml) {
        this.ishtml = ishtml;
    }
}

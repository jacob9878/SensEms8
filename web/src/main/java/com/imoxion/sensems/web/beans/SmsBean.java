package com.imoxion.sensems.web.beans;

public class SmsBean {

	private String skey = null;
	private String categoryid = null;
	private String authid = null;
	private String callback_number = null;
	private String send_method = null;
    private String rectype = null;
	private String recid = null;
	private String recname = null;
	private String conn_str = null;
	private String query = null;
	private String ftype = null;
	private String msg = null;
	private String regtime = null;
    private String start_time = null;
    private String end_time = null;
	private String reserv_time = null;
	private int total_send;
	private int cur_send;
	private String state = null;
	private String state_info = null;
    private String extended = null;
    
	public String getExtended() {
        return extended;
    }
    public void setExtended(String extended) {
        if (extended != null)
            this.extended = extended;
    }
    public String getAuthid() {
		return authid;
	}
	public String getCallback_number() {
		return callback_number;
	}
	
	public String getConn_str() {
		return conn_str;
	}
	public int getCur_send() {
		return cur_send;
	}
	public String getFtype() {
		return ftype;
	}
	public String getMsg() {
		return msg;
	}
	public String getQuery() {
		return query;
	}
	public String getRectype() {
        return rectype;
    }
    public void setRectype(String rectype) {
        if (rectype != null)
            this.rectype = rectype;
    }
    public String getRecid() {
		return recid;
	}
	
	public String getRegtime() {
		return regtime;
	}
	public String getReserv_time() {
		return reserv_time;
	}
	public String getSend_method() {
		return send_method;
	}
	public String getSkey() {
		return skey;
	}
	public String getState() {
		return state;
	}
	public int getTotal_send() {
		return total_send;
	}
	public void setAuthid(String authid) {
		this.authid = authid;
	}
	public void setCallback_number(String callback_number) {
		this.callback_number = callback_number;
	}
	
	public String getCategoryid() {
        return categoryid;
    }
    public void setCategoryid(String categoryid) {
        if (categoryid != null)
            this.categoryid = categoryid;
    }
    public void setConn_str(String conn_str) {
		this.conn_str = conn_str;
	}
	public void setCur_send(int cur_send) {
		this.cur_send = cur_send;
	}
	public void setFtype(String ftype) {
		this.ftype = ftype;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public void setRecid(String recid) {
		this.recid = recid;
	}
	
	public void setRegtime(String regtime) {
		this.regtime = regtime;
	}
	public void setReserv_time(String reserv_time) {
		this.reserv_time = reserv_time;
	}
	public void setSend_method(String send_method) {
		this.send_method = send_method;
	}
	public void setSkey(String skey) {
		this.skey = skey;
	}
	public void setState(String state) {
		this.state = state;
	}
	public void setTotal_send(int total_send) {
		this.total_send = total_send;
	}
    public String getRecname() {
        return recname;
    }
    public void setRecname(String recname) {
        if (recname != null)
            this.recname = recname;
    }
    public String getEnd_time() {
        return end_time;
    }
    public void setEnd_time(String end_time) {
        if (end_time != null)
            this.end_time = end_time;
    }
    public String getStart_time() {
        return start_time;
    }
    public void setStart_time(String start_time) {
        if (start_time != null)
            this.start_time = start_time;
    }
	public String getState_info() {
		return state_info;
	}
	public void setState_info(String state_info) {
		if (state_info != null)
			this.state_info = state_info;
	}
}

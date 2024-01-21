package com.imoxion.sensems.web.form;

import java.util.Date;

public class ActionLogForm {

	private String log_key;
	private Date log_date;
	private String ip;
	private String userid;
	private String menu_key;
	private String param;

	public String getLog_key() {
		return log_key;
	}

	public void setLog_key(String log_key) {
		this.log_key = log_key;
	}

	public Date getLog_date() {
		return log_date;
	}

	public void setLog_date(Date log_date) {
		this.log_date = log_date;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getMenu_key() { return menu_key; }

	public void setMenu_key(String menu_key) { this.menu_key = menu_key; }

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}
}

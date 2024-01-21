package com.imoxion.sensems.web.beans;

public class FilterBean {

	private String hostname;
	private String f_flag;
	private String e_data;
	private String extended;
	
	public String getE_data() {
		return e_data;
	}
	public void setE_data(String e_data) {
		this.e_data = e_data;
	}
	public String getExtended() {
		return extended;
	}
	public void setExtended(String extended) {
		this.extended = extended;
	}
	public String getF_flag() {
		return f_flag;
	}
	public void setF_flag(String f_flag) {
		this.f_flag = f_flag;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	
}

package com.imoxion.sensems.web.model;

public class LoginModel {

	private String userid = null;
	private String password = null;
	private String isSave = "0";
	private String language = "";
	private String encAESKey;
	
	public String getEncAESKey() {
		return encAESKey;
	}

	public void setEncAESKey(String encAESKey) {
		this.encAESKey = encAESKey;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}



	public String getIsSave() {
		return isSave;
	}

	public void setIsSave(String isSave) {
		this.isSave = isSave;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
}

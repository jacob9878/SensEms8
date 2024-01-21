package com.imoxion.sensems.web.beans;

public class TestAccountBean {

	private String ukey = null;
	private String userid = null;
	private String email = null;
	private String flag = null;
	private String[] ukeys = null;
	
	public String[] getUkeys() {
		String[] safeArray = null;
		if (this.ukeys != null) {
			safeArray = new String[this.ukeys.length];
			for (int i = 0; i < this.ukeys.length ; i++) {
				safeArray[i] = this.ukeys[i];
			}
		}
		return safeArray;
	}
	public void setUkeys(String[] ukeys) {
		this.ukeys = new String[ukeys.length];
		for (int i = 0; i < ukeys.length; ++i)
			this.ukeys[i] = ukeys[i];
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public String getUkey() {
		return ukey;
	}
	public void setUkey(String ukey) {
		this.ukey = ukey;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
}

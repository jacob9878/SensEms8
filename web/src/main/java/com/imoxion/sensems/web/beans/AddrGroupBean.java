package com.imoxion.sensems.web.beans;

public class AddrGroupBean {

	private String userid = null;
	private int gkey;
	private String gname = null;

	private int count;
	

	public String getGname() {
		return gname;
	}
	public void setGname(String gname) {
		this.gname = gname;
	}

	public int getGkey() {
		return gkey;
	}

	public void setGkey(int gkey) {
		this.gkey = gkey;
	}

	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
	
}

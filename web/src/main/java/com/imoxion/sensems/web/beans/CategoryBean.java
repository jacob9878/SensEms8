package com.imoxion.sensems.web.beans;

public class CategoryBean {

	private String sid = null;
	private String name = null;
	private String userid = null;
	private String regdate = null;
	private String[] shares = null;
	private String[] sids = null;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRegdate() {
		return regdate;
	}
	public void setRegdate(String regdate) {
		this.regdate = regdate;
	}
	public String getSid() {
		return sid;
	}
	public void setSid(String sid) {
		this.sid = sid;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String[] getShares() {
		String[] safeArray = null;
		if (this.shares != null) {
			safeArray = new String[this.shares.length];
			for (int i = 0; i < this.shares.length ; i++) {
				safeArray[i] = this.shares[i];
			}
		}
		return safeArray;
	}
	public void setShares(String[] shares) {
		this.shares = new String[shares.length];
		for (int i = 0; i < shares.length; ++i)
			this.shares[i] = shares[i];
	}

	public String[] getSids() {
		String[] safeArray = null;
		if (this.sids != null) {
			safeArray = new String[this.sids.length];
			for (int i = 0; i < this.sids.length ; i++) {
				safeArray[i] = this.sids[i];
			}
		}
		return safeArray;
	}
	public void setSids(String[] sids) {
		this.sids = new String[sids.length];
		for (int i = 0; i < sids.length; ++i)
			this.sids[i] = sids[i];
	}
	
	
}

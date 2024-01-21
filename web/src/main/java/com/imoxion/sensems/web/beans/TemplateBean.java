package com.imoxion.sensems.web.beans;

public class TemplateBean {

	private String sid = null;
	private String[] sids = null;
	private String[] shares = null;
	private String userid = null;
	private String temp_name = null;
    private String safe_temp_name = null;
	private String regdate = null;
	private String contents = null;
	private String category = null;	
	private String extended = null;
	private String image_path = null;
	
	public String getImage_path() {
		return image_path;
	}
	public void setImage_path(String image_path) {
		this.image_path = image_path;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}

	public String getContents() {
		return contents;
	}
	public void setContents(String contents) {
		this.contents = contents;
	}
	public String getExtended() {
		return extended;
	}
	public void setExtended(String extended) {
		this.extended = extended;
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
	public String getTemp_name() {
		return temp_name;
	}
	public void setTemp_name(String temp_name) {
		this.temp_name = temp_name;
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
    public String getSafe_temp_name() {
        return safe_temp_name;
    }
    public void setSafe_temp_name(String safe_temp_name) {
        this.safe_temp_name = safe_temp_name;
    }
	
	
}

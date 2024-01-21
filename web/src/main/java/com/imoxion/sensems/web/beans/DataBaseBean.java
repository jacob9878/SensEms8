package com.imoxion.sensems.web.beans;

public class DataBaseBean {

	private String ukey = null;	
	private String dbname = null;	
	private String dbtype = null;
	private String userid = null;
	private String passwd = null;
	private String host = null;
	private String authid = null;
	private String isshare = null;
	private String regdate = null;
	private String address = null;
	private String dbcharset = null;
    private String datacharset = null;
	private String extended = null;
	
	public String getAddress() {
		return address;
	}
	public String getAuthid() {
		return authid;
	}
	public String getDbcharset() {
		return dbcharset;
	}
	public String getDbname() {
		return dbname;
	}
	public String getDbtype() {
		return dbtype;
	}
	public String getExtended() {
		return extended;
	}
	public String getHost() {
		return host;
	}
	public String getIsshare() {
		return isshare;
	}
	public String getPasswd() {
		return passwd;
	}
	public String getRegdate() {
		return regdate;
	}
	public String getUkey() {
		return ukey;
	}
	public String getUserid() {
		return userid;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public void setAuthid(String authid) {
		this.authid = authid;
	}
	public void setDbcharset(String dbcharset) {
		this.dbcharset = dbcharset;
	}
	public void setDbname(String dbname) {
		this.dbname = dbname;
	}
	public void setDbtype(String dbtype) {
		this.dbtype = dbtype;
	}
	public void setExtended(String extended) {
		this.extended = extended;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public void setIsshare(String isshare) {
		this.isshare = isshare;
	}
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
	public void setRegdate(String regdate) {
		this.regdate = regdate;
	}
	public void setUkey(String key) {
		ukey = key;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
    
    public String getDatacharset() {
        return datacharset;
    }
    public void setDatacharset(String datacharset) {
        this.datacharset = datacharset;
    }
	
	
}

package com.imoxion.sensems.web.beans;

public class ReportBean {

	private String ukey = null;
    private String gkey = null;
    private String authid = null;
    private String recid = null;
    private String item = null;
    private String subitem = null;
    private String qry = null;
    
	public String getAuthid() {
		return authid;
	}
	public String getGkey() {
		return gkey;
	}
	public String getItem() {
		return item;
	}
	public String getQry() {
		return qry;
	}
	public String getRecid() {
		return recid;
	}
	public String getSubitem() {
		return subitem;
	}
	public String getUkey() {
		return ukey;
	}
	public void setAuthid(String authid) {
		this.authid = authid;
	}
	public void setGkey(String gkey) {
		this.gkey = gkey;
	}
	public void setItem(String item) {
		this.item = item;
	}
	public void setQry(String qry) {
		this.qry = qry;
	}
	public void setRecid(String recid) {
		this.recid = recid;
	}
	public void setSubitem(String subitem) {
		this.subitem = subitem;
	}
	public void setUkey(String ukey) {
		this.ukey = ukey;
	}
}

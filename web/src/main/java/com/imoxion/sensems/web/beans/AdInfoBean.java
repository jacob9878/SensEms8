package com.imoxion.sensems.web.beans;

public class AdInfoBean {

	private String msgid = null;
	private String adid = null;
	private String ad_name = null;
	private String ret_url = null;
	private String extended = null;
	private String link_img = null;
	
	public String getAdid() {
		return adid;
	}
	public void setAdid(String adid) {
		this.adid = adid;
	}
	public String getExtended() {
		return extended;
	}
	public void setExtended(String extended) {
		this.extended = extended;
	}
	public String getLink_img() {
		return link_img;
	}
	public void setLink_img(String link_img) {
		this.link_img = link_img;
	}
	public String getMsgid() {
		return msgid;
	}
	public void setMsgid(String msgid) {
		this.msgid = msgid;
	}
	public String getRet_url() {
		return ret_url;
	}
	public void setRet_url(String ret_url) {
		this.ret_url = ret_url;
	}
	public String getAd_name() {
		return ad_name;
	}
	public void setAd_name(String ad_name) {
		this.ad_name = ad_name;
	}
}

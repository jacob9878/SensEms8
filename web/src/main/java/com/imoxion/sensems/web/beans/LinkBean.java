package com.imoxion.sensems.web.beans;

public class LinkBean {

	private String msgid = null;
	private Integer linkid;
	private String link_name = null;
	private String link_url = null;
	private String extended = null;
	private String link_img = null;
	private int count = 0;
    private String click_date;

    public String getClick_date() {
        return click_date;
    }

    public void setClick_date(String click_date) {
        this.click_date = click_date;
    }

    public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public Integer getLinkid() {
		return linkid;
	}
	public void setLinkid(Integer linkid) {
		this.linkid = linkid;
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
	public String getLink_name() {
		return link_name;
	}
	public void setLink_name(String link_name) {
		this.link_name = link_name;
	}
	public String getLink_url() {
		return link_url;
	}
	public void setLink_url(String link_url) {
		this.link_url = link_url;
	}
}

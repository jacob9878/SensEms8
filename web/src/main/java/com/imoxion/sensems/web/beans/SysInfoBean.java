package com.imoxion.sensems.web.beans;


public class SysInfoBean {

	private String language = null;
	private String sms_use = null;
	private String sms_cid = null;
	private String charset = null;
    private String multilang_use = null;
					
	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public String getSms_cid() {
		return sms_cid;
	}

	public void setSms_cid(String sms_cid) {
		this.sms_cid = sms_cid;
	}

	public String getSms_use() {
		return sms_use;
	}

	public void setSms_use(String sms_use) {
		this.sms_use = sms_use;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

    public String getMultilang_use() {
        return multilang_use;
    }

    public void setMultilang_use(String multilang_use) {
        this.multilang_use = multilang_use;
    }
}

/*
 * FileName : SmsTranBean.java
 *
 * 작성자 : Administrator
 * 이메일 : xgxong@imoxion.com
 * Company : (주)아이모션
 * 날짜 : 2007. 02. 23
 *
 * 설명:
 * 
 */
package com.imoxion.sensems.web.beans;

import java.util.Date;

public class SmsTranBean {

	private Date senddate = null;
	private String sendstat = null;
	private String msgtype = null;
	private String phone = null;
	private String callback = null;
	private String msg = null;
	private String etc1 = null;
	private String refkey = null;
	
	public String getCallback() {
		return callback;
	}
	public void setCallback(String callback) {
		if (callback != null)
			this.callback = callback;
	}
	public String getEtc1() {
		return etc1;
	}
	public void setEtc1(String etc1) {
		if (etc1 != null)
			this.etc1 = etc1;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		if (msg != null)
			this.msg = msg;
	}
	public String getMsgtype() {
		return msgtype;
	}
	public void setMsgtype(String msgtype) {
		if (msgtype != null)
			this.msgtype = msgtype;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		if (phone != null)
			this.phone = phone;
	}
	public String getRefkey() {
		return refkey;
	}
	public void setRefkey(String refkey) {
		if (refkey != null)
			this.refkey = refkey;
	}
	
	public Date getSenddate() {
		return senddate;
	}
	public void setSenddate(Date senddate) {
		if (senddate != null)
			this.senddate = senddate;
	}
	public String getSendstat() {
		return sendstat;
	}
	public void setSendstat(String sendstat) {
		if (sendstat != null)
			this.sendstat = sendstat;
	}
}

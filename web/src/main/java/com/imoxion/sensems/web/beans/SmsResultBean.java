/*
 * FileName : SmsResultBean.java
 *
 * 작성자 : Administrator
 * 이메일 : Administrator@imoxion.com
 * Company : (주)아이모션
 * 날짜 : 2007. 02. 27
 *
 * 설명:
 * 
 */
package com.imoxion.sensems.web.beans;

import java.util.Date;

public class SmsResultBean {

	private String skey = "";
	private String userid = "";
	private String send_phone = "";
	private String return_phone = "";
	private String sendmsg = "";
	private String senddate = "";
	private String resultdate = "";
	private String sendresult = "";
	private String etc1 = "";
	private String etc2 = "";
	private String result_state = "";
	
	public String getResult_state() {
		return result_state;
	}
	public void setResult_state(String result_state) {
		if (result_state != null)
			this.result_state = result_state;
	}
	public String getEtc1() {
		return etc1;
	}
	public void setEtc1(String etc1) {
		if (etc1 != null)
			this.etc1 = etc1;
	}
	public String getEtc2() {
		return etc2;
	}
	public void setEtc2(String etc2) {
		if (etc2 != null)
			this.etc2 = etc2;
	}
 
	public String getResultdate() {
		return resultdate;
	}
	public void setResultdate(String resultdate) {
		if (resultdate != null)
			this.resultdate = resultdate;
	}
	public String getSenddate() {
		return senddate;
	}
	public void setSenddate(String senddate) {
		if (senddate != null)
			this.senddate = senddate;
	}
	public String getReturn_phone() {
		return return_phone;
	}
	public void setReturn_phone(String return_phone) {
		if (return_phone != null)
			this.return_phone = return_phone;
	}
	public String getSend_phone() {
		return send_phone;
	}
	public void setSend_phone(String send_phone) {
		if (send_phone != null)
			this.send_phone = send_phone;
	}

	public String getSendmsg() {
		return sendmsg;
	}
	public void setSendmsg(String sendmsg) {
		if (sendmsg != null)
			this.sendmsg = sendmsg;
	}
	public String getSendresult() {
		return sendresult;
	}
	public void setSendresult(String sendresult) {
		if (sendresult != null)
			this.sendresult = sendresult;
	}
	public String getSkey() {
		return skey;
	}
	public void setSkey(String skey) {
		if (skey != null)
			this.skey = skey;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		if (userid != null)
			this.userid = userid;
	}
	
}

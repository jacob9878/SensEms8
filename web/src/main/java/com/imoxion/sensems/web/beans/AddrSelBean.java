package com.imoxion.sensems.web.beans;

/**
 * imb_addrsel에 매핑되는 bean
 * create by zpqdnjs 2021-03-17
 * */
public class AddrSelBean {
	/** 메시지 아이디 */
	private String msgid;
	
	/** 사용자 아이디 */
	private String userid;
	
	/** 주소록 그룹키 */
	private int gkey;
	
	/** 주소록 그룹명 */
	private String gname;

	public String getMsgid() {
		return msgid;
	}

	public void setMsgid(String msgid) {
		this.msgid = msgid;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public int getGkey() {
		return gkey;
	}

	public void setGkey(int gkey) {
		this.gkey = gkey;
	}

	public String getGname() {
		return gname;
	}

	public void setGname(String gname) {
		this.gname = gname;
	}
	
}

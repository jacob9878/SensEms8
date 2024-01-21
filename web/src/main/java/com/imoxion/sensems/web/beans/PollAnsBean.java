package com.imoxion.sensems.web.beans;

public class PollAnsBean {

	private String msgid = null;
	private String pollid = null;
	private String ansid = null;
	private String answer = null;
	private int ans_count = 0;
	private String extended =null;
	
	public int getAns_count() {
		return ans_count;
	}
	public void setAns_count(int ans_count) {
		this.ans_count = ans_count;
	}
	public String getAnsid() {
		return ansid;
	}
	public void setAnsid(String ansid) {
		this.ansid = ansid;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	public String getExtended() {
		return extended;
	}
	public void setExtended(String extended) {
		this.extended = extended;
	}
	public String getMsgid() {
		return msgid;
	}
	public void setMsgid(String msgid) {
		this.msgid = msgid;
	}
	public String getPollid() {
		return pollid;
	}
	public void setPollid(String pollid) {
		this.pollid = pollid;
	}
	
}

package com.imoxion.sensems.web.beans;

public class PollQuestBean {
	
	private String msgid = null;
	private String pollid = null;
	private String question = null;
	private String extended = null;
	
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
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	
}

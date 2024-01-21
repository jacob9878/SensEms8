package com.imoxion.sensems.server.util;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class ImSmtpSendingInfo {
	private String domain = null;
	private String mailKey = null;
	private String reserveTime = null;
	private String subject = null;
	private String charset = null;
	private String reciptKey = null;
	//private String deliverTo = null;
	private String deliverFrom = null;
	private String from = null;
	private String to = null;
	private String cc = null;
	private String userid = null;
	private String ahost = null;
	private String tbl_no = null;
	private String part_no = null;
	private String xmailer = null;
	private String senddate = null;
	private String fromIP = null;
	private String mailfrom = null;
	private String rcptto = null;
	private String traceid = null;
	private String xWebSend = null;
	private String groupKey = null;
	private String rcptKey = null;
	private String send_type = null;
	private List<String> listRcpt = null;
	
	public List<String> getListRcpt() {
		return listRcpt;
	}
}

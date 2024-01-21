/**
 * 첨부파일 Bean
 * */
package com.imoxion.sensems.web.beans;

import java.util.Date;

public class AttachBean {
	/** 고유키 */
	private String ekey;
	
	/** 메시지 아이디 */
	private String msgid;
	
	/** 파일명 */
	private String file_name;
	
	/** 파일크기 */
	private String file_size;
	
	/** 파일경로 */
	private String file_path;
	
	/** 만료일 */
	private Date expire_date;
	
	/** 등록일 */
	private Date regdate;

	/** 다운로드 횟수 */
	private int down_count;

	public int getDown_count() {
		return down_count;
	}

	public void setDown_count(int down_count) {
		this.down_count = down_count;
	}

	public String getEkey() {
		return ekey;
	}

	public void setEkey(String ekey) {
		this.ekey = ekey;
	}

	public String getMsgid() {
		return msgid;
	}

	public void setMsgid(String msgid) {
		this.msgid = msgid;
	}

	public String getFile_name() {
		return file_name;
	}

	public void setFile_name(String file_name) {
		this.file_name = file_name;
	}

	public String getFile_size() {
		return file_size;
	}

	public void setFile_size(String file_size) {
		this.file_size = file_size;
	}

	public String getFile_path() {
		return file_path;
	}

	public void setFile_path(String file_path) {
		this.file_path = file_path;
	}

	public Date getExpire_date() {
		return expire_date;
	}

	public void setExpire_date(Date expire_date) {
		this.expire_date = expire_date;
	}

	public Date getRegdate() {
		return regdate;
	}

	public void setRegdate(Date regdate) {
		this.regdate = regdate;
	}


}

package com.imoxion.sensems.web.beans;

import java.util.Date;

import org.apache.ibatis.type.Alias;

/**
 * 임시 업로드 파일 bean
 * */
public class UploadFileBean {
	private String fkey;
	
	private String filename;
	
	private String filepath;
	
	private Date regdate;
	
	private long filesize;

	public String getFkey() {
		return fkey;
	}

	public void setFkey(String fkey) {
		this.fkey = fkey;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	public Date getRegdate() {
		return regdate;
	}

	public void setRegdate(Date regdate) {
		this.regdate = regdate;
	}

	public long getFilesize() {
		return filesize;
	}

	public void setFilesize(long filesize) {
		this.filesize = filesize;
	}
	
}

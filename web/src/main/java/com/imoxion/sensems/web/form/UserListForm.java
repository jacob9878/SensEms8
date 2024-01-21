package com.imoxion.sensems.web.form;

public class UserListForm {

	private String cpage = "1";
	private String pagegroupsize = "5";
	private String srch_type = "userid";
	private String srch_keyword;
	private String permission;
	private String isStop;
	private String use_smtp;


	public String getCpage() {
		return cpage;
	}

	public void setCpage(String cpage) {
		this.cpage = cpage;
	}


	public String getPagegroupsize() {
		return pagegroupsize;
	}

	public void setPagegroupsize(String pagegroupsize) {
		this.pagegroupsize = pagegroupsize;
	}

	public String getSrch_type() {
		return srch_type;
	}

	public void setSrch_type(String srch_type) {
		this.srch_type = srch_type;
	}

	public String getSrch_keyword() {
		return srch_keyword;
	}

	public void setSrch_keyword(String srch_keyword) {
		this.srch_keyword = srch_keyword.trim();
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public String getIsStop() {
		return isStop;
	}

	public void setIsStop(String isStop) {
		this.isStop = isStop;
	}

	public String getUse_smtp() {
		return use_smtp;
	}

	public void setUse_smtp(String use_smtp) {
		this.use_smtp = use_smtp;
	}
}

package com.imoxion.sensems.web.model;

public class UserListModel {

	
	private int cpage = 1;
	private int pagesize = 15;
	private int pagegroupsize = 10;	
	private String srch_type = "userid";
	private String srch_keyword;
	private String userid = null;




	public int getCpage() {
		return cpage;
	}

	public void setCpage(int cpage) {
		this.cpage = cpage;
	}

	public int getPagesize() {
		return pagesize;
	}

	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
	}

	public int getPagegroupsize() {
		return pagegroupsize;
	}

	public void setPagegroupsize(int pagegroupsize) {
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


	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}


}

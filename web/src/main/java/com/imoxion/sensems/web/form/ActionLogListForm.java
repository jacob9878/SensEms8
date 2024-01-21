package com.imoxion.sensems.web.form;

public class ActionLogListForm {

	private String cpage = "1";
	private String page_groupsize = "5";
	private String start_date;
	private String end_date;
	private String userid = "";
	private String menu_key = "";
	private String menu = "";
	private String srch_keyword = "";

	public String getCpage() {
		return cpage;
	}

	public void setCpage(String cpage) {
		this.cpage = cpage;
	}

	public String getPage_groupsize() {
		return page_groupsize;
	}

	public void setPage_groupsize(String page_groupsize) {
		this.page_groupsize = page_groupsize;
	}

	public String getStart_date() {
		return start_date;
	}

	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}

	public String getEnd_date() {
		return end_date;
	}

	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}

	public String getUserid() { return userid; }

	public void setUserid(String userid) { this.userid = userid; }

	public String getMenu_key() { return menu_key; }

	public void setMenu_key(String menu_key) { this.menu_key = menu_key; }

	public String getMenu() { return menu; }

	public void setMenu(String menu) { this.menu = menu; }

	public String getSrch_keyword() {
		return srch_keyword;
	}

	public void setSrch_keyword(String srch_keyword) {
		this.srch_keyword = srch_keyword;
	}
}

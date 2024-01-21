package com.imoxion.sensems.web.database.domain;

import org.apache.ibatis.type.Alias;

import java.util.Date;

@Alias("categoryInfo")
public class ImbCategoryInfo {
	/** 키 */
	private String ukey;
	
	/** 카테고리 명 */
	private String name;
	
	/** 작성자 */
	private String userid;
	
	/** 등록일 */
	private Date regdate;
	

	public String getUkey() {
		return ukey;
	}
	public void setUkey(String ukey) {
		this.ukey = ukey;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public Date getRegdate() {
		return regdate;
	}
	public void setRegdate(Date regdate) {
		this.regdate = regdate;
	}
	@Override
	public String toString() {
		return "ImbCategoryInfo [ukey=" + ukey + ", name=" + name + ", userid=" + userid + ", regdate=" + regdate + "]";
	}
	
	
}
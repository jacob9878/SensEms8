/*
 * �ۼ��� ��¥: 2005. 5. 26.
 *
 */
package com.imoxion.sensems.web.beans;

import java.util.Arrays;

import javax.servlet.http.HttpSession;

import com.imoxion.sensems.web.common.SessionAttributeNames;

/**
 * <pre>
 * UserInfo 
 * id , name , mhost
 * </ore> 
 * @author imoxion
 */
public class UserInfoBean {
	
	public static  String UTYPE_NORMAL = "U"; // 사용자
	public static  String UTYPE_ADMIN = "A"; // 슈퍼 관리자
    
    private String userid = null;
    private String[] userids = null;
    private String passwd = null;
    private String name = null;
    private String dept = null;
    private String grade = null;
    private String email = null;
    private String mobile = null;
    private String tel = null;
    private String permission = null;
    private String access_right = null;
    private String use_smtp = null;
    
    private String regdate = null;
    private String isstop = null;
	private String language = "ko";
    private String approve_email = null;
    private int pagesize=15;

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String[] getUserids() {
		String[] safeArray = null;
		if (this.userids != null) {
			safeArray = new String[this.userids.length];
			for (int i = 0; i < this.userids.length ; i++) {
				safeArray[i] = this.userids[i];
			}
		}
		return safeArray;
	}

	public void setUserids(String[] userids) {
		this.userids = new String[userids.length];
		for (int i = 0; i < userids.length; ++i)
			this.userids[i] = userids[i];
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}



	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public String getAccess_right() {
		return access_right;
	}

	public void setAccess_right(String access_right) {
		this.access_right = access_right;
	}

	public String getRegdate() {
		return regdate;
	}

	public void setRegdate(String regdate) {
		this.regdate = regdate;
	}

	

	public String getIsstop() {
		return isstop;
	}

	public void setIsstop(String isstop) {
		this.isstop = isstop;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getApprove_email() {
		return approve_email;
	}

	public void setApprove_email(String approve_email) {
		this.approve_email = approve_email;
	}

	public String getDept() {
		return dept;
	}

	public void setDept(String dept) {
		this.dept = dept;
	}

	public int getPagesize() {
		return pagesize;
	}

	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
	}

	public String getUse_smtp() {
		return use_smtp;
	}

	public void setUse_smtp(String use_smtp) {
		this.use_smtp = use_smtp;
	}

	@Override
	public String toString() {
		return "UserInfoBean [userid=" + userid + ", userids=" + Arrays.toString(userids) + ", passwd=" + passwd
				+ ", name=" + name + ", dept=" + dept + ", grade=" + grade + ", email=" + email + ", mobile=" + mobile
				+ ", tel=" + tel + ", permission=" + permission + ", access_right=" + access_right + ", regdate="
				+ regdate + ", isstop=" + isstop + ", language=" + language + ", approve_email=" + approve_email + ", use_smtp=" + use_smtp + "]";
	}
	public static UserInfoBean getUserSessionInfo(HttpSession session){
		return (UserInfoBean)session.getAttribute(SessionAttributeNames.USER_SESSION_INFO);
	}

}
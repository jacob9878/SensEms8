package com.imoxion.sensems.web.form;


/**
 * @date 2021.02.01
 * @author jhpark
 *
 */
public class UserForm {

	/**
	 * 사용자 아이디
	 */
	private String userid;

	/**
	 * 아이디 중복확인 체크 여부
	 */
	private String isCheck;

	/**
	 * 이름
	 */
	private String uname;
	
	private String passwd;
	
	private String passwd_confirm;

	private String dept;
	
	private String grade;
	
	private String email;

	private String mobile;

	private String tel;

	private String permission;

	private String approve_email;

	private String access_ip;

	private String encAESKey;

	private String saltKey;

	private String isstop;

	private String use_smtp;

	/** 변경 전 이메일 */
	private String ori_email;

	/** 변경 전 이름 */
	private String ori_name;

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getIsCheck() {
		return isCheck;
	}

	public void setIsCheck(String isCheck) {
		this.isCheck = isCheck;
	}

	public String getUname() {
		return uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public String getPasswd_confirm() {
		return passwd_confirm;
	}

	public void setPasswd_confirm(String passwd_confirm) {
		this.passwd_confirm = passwd_confirm;
	}

	public String getDept() {
		return dept;
	}

	public void setDept(String dept) {
		this.dept = dept;
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

	public String getApprove_email() {
		return approve_email;
	}

	public void setApprove_email(String approve_email) {
		this.approve_email = approve_email;
	}

	public String getAccess_ip() {
		return access_ip;
	}

	public void setAccess_ip(String access_ip) {
		this.access_ip = access_ip;
	}

	public String getEncAESKey() {
		return encAESKey;
	}

	public void setEncAESKey(String encAESKey) {
		this.encAESKey = encAESKey;
	}

	public String getSaltKey() {
		return saltKey;
	}

	public void setSaltKey(String saltKey) {
		this.saltKey = saltKey;
	}

	public String getIsstop() {
		return isstop;
	}

	public void setIsstop(String isstop) {
		this.isstop = isstop;
	}

	public String getUse_smtp() {
		return use_smtp;
	}

	public void setUse_smtp(String use_smtp) {
		this.use_smtp = use_smtp;
	}

	public String getOri_email() {return ori_email; }

	public void setOri_email(String ori_email) {this.ori_email = ori_email; }

	public String getOri_name() {return ori_name; }

	public void setOri_name(String ori_name) {this.ori_name = ori_name; }
}

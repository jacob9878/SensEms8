package com.imoxion.sensems.web.database.domain;

import org.apache.ibatis.type.Alias;

import java.util.Date;

@Alias("userInfo")
public class ImbUserinfo {
	/** 사용자 아이디 */
	private String userid;
	
	/** 비밀번호 */
	private String passwd;
	
	/** 사용자 이름 */
	private String uname;
	
	/** 부서 */
	private String dept;
	
	/** 직위 */
	private String grade;
	
	/** 메일주소 */
	private String email;
	
	/** 휴대폰 */
	private String mobile;
	
	/** 전화 */
	private String tel;
	
	/** 권한(A:관리자  U:사용자 */
	private String permission;
	
	/** 등록일 */
	private Date regdate;
	
	/** 사용중지 */
	private String  isstop;
	
	/** 승인이메일 */
	private String approve_email;
	
	/** 접근 아이피 */
	private String access_ip;
	
	/** 비밀번호 암호화 타입*/
	private String pwd_type;

	/** 비밀번호 변경일 */
	private Date pwd_date;
	
	/** 계정 잠금 시각 */
	private Date fail_login_time;
	
	/** 암호 salt 값*/
	private String st_data;

	private String use_smtp;
	
	private int fail_login;
	
	public int getFail_login() {
		return fail_login;
	}

	public void setFail_login(int fail_login) {
		this.fail_login = fail_login;
	}

	public Date getPwd_date() {
		return pwd_date;
	}

	public void setPwd_date(Date pwd_date) {
		this.pwd_date = pwd_date;
	}

	public Date getFail_login_time() {
		return fail_login_time;
	}

	public void setFail_login_time(Date fail_login_time) {
		this.fail_login_time = fail_login_time;
	}

	public String getSt_data() {
		return st_data;
	}

	public void setSt_data(String st_data) {
		this.st_data = st_data;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public String getUname() {
		return uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
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

	public Date getRegdate() {
		return regdate;
	}

	public void setRegdate(Date regdate) {
		this.regdate = regdate;
	}

	public String getIsstop() {
		return isstop;
	}

	public void setIsstop(String isstop) {
		this.isstop = isstop;
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

	public String getPwd_type() {
		return pwd_type;
	}

	public void setPwd_type(String pwd_type) {
		this.pwd_type = pwd_type;
	}

	public String getUse_smtp() {
		return use_smtp;
	}

	public void setUse_smtp(String use_smtp) {
		this.use_smtp = use_smtp;
	}

	@Override
	public String toString() {
		return "ImbUserinfo [userid=" + userid + ", passwd=" + passwd + ", uname=" + uname + ", dept=" + dept
				+ ", grade=" + grade + ", email=" + email + ", mobile=" + mobile + ", tel=" + tel + ", permission="
				+ permission + ", regdate=" + regdate + ", isstop=" + isstop + ", approve_email=" + approve_email  + ", use_smtp=" + use_smtp
				+ ", access_ip=" + access_ip + ", pwd_type=" + pwd_type + "]";
	}
	
	
}
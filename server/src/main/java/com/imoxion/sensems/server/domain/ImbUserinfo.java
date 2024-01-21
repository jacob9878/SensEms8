package com.imoxion.sensems.server.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.ibatis.type.Alias;

import java.util.Date;

@Setter
@Getter
@ToString
@Alias("UserInfo")
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

	/** 실패 로그인 횟수 */
	private int fail_login;

	/** SMTP 인증권한 */
	private int use_smtp;
	
	
}
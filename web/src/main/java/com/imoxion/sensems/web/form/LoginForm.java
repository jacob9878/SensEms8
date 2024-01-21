package com.imoxion.sensems.web.form;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * @author : jhpark
 * @date : 2021. 2. 16.
 * @desc : 로그인 페이지에서 사용되는 form클래스
 *
 */
public class LoginForm implements Validator {

	private String userid = null;
	private String password = null;
	private String isSave = "0";
	private String language = "";
	private String encAESKey;
	private String mode;
	private String answer;
	
	public String getEncAESKey() {
		return encAESKey;
	}

	public void setEncAESKey(String encAESKey) {
		this.encAESKey = encAESKey;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}



	public String getIsSave() {
		return isSave;
	}

	public void setIsSave(String isSave) {
		this.isSave = isSave;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}


	@Override
	public boolean supports(Class<?> aClass) {
		return false;
	}

	public void validate(Object target, Errors errors) {
		LoginForm form = (LoginForm) target;
		// 아이디 입력 안함
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "userid", "E0053","아이디를 입력해 주세요.");

		// 패스워드 입력 안함
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "E0048","비밀번호를 입력하세요.");
	}
}

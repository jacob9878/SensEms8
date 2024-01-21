package com.imoxion.sensems.web.form;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * @author : jhpark
 * @date : 2021. 2. 16.
 * @desc : 비밀번호 변경 페이지에서 사용되는 form클래스
 * 
 */
public class UpdatePasswordForm implements Validator {

	private String password;

	private String newPassword;

	private String confirmPassword;

	private String encAESKey; // RSA 공개키로 암호화된 대칭키

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public String getEncAESKey() {
		return encAESKey;
	}

	public void setEncAESKey(String encAESKey) {
		this.encAESKey = encAESKey;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	public boolean supports(Class<?> arg0) {
	
		return false;
	}

	public void validate(Object obj, Errors errors) {

		UpdatePasswordForm form = (UpdatePasswordForm) obj;
		// ------- 비밀번호---
		// 1) 입력필수체크
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "E0048","비밀번호를 입력해 주세요.");

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "newPassword", "E0048","비밀번호를 입력해 주세요.");

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "confirmPassword", "E0081","비밀번호 확인을 입력해 주세요.");
		// 2) 새 비밀번호와 비밀번호확인의 일치 확인
		if( !form.getNewPassword().equals(form.getConfirmPassword()) ){
			errors.rejectValue("confirmPassword", "E0082","새로운 비밀번호가 일치하지 않습니다.");
		}
	}
}

package com.imoxion.sensems.server.beans;

/**
 * Created by sungg on 2017-07-17.
 */
public class ErrorCode {
	private String code = "";
	private String detail_code = "";
	private String description = "";
	private String eng_description = "";
	private String response = "";
	
	
	
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDetail_code() {
		return detail_code;
	}
	public void setDetail_code(String detail_code) {
		this.detail_code = detail_code;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getEng_description() {
		return eng_description;
	}
	public void setEng_description(String eng_description) {
		this.eng_description = eng_description;
	}
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}
	@Override
	public String toString() {
		return "ErrorCode [code=" + code + ", detail_code=" + detail_code + ", description=" + description
				+ ", eng_description=" + eng_description + ", response=" + response + "]";
	}
	
	
	
    	
}
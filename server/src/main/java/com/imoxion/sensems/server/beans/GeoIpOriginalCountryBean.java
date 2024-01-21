package com.imoxion.sensems.server.beans;
/**
 * 
 * @author : chr3125
 * @date : 2014. 12. 10.
 * geoIp mail에서의 ip 추출 관련 bean
 * 
 */
public class GeoIpOriginalCountryBean {
	
	private String originalSendIp;
	
	private String countryCode;
	
	private String countryName;

	
	public String getOriginalSendIp() {
		return originalSendIp;
	}

	public void setOriginalSendIp(String originalSendIp) {
		this.originalSendIp = originalSendIp;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	

}

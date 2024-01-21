package com.imoxion.sensems.web.database.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Dkim 정보
 * @author 
 *
 */
@Getter
@Setter
public class ImbDkimInfo {
	
	/** 도메인 */
	private String domain;
	
	/** 지정자 */
	private String selector;
	
	/** 파일명 */
	private String filename;
	
	/** 공개키 */
	private String public_key;
	
	/** 등록일 */
	private Date regdate;
	
	/** 서명사용여부 */
	private String use_sign;

	private byte[] private_key;

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getSelector() {
		return selector;
	}

	public void setSelector(String selector) {
		this.selector = selector;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getPublic_key() {
		return public_key;
	}

	public void setPublic_key(String public_key) {
		this.public_key = public_key;
	}

	public Date getRegdate() {
		return regdate;
	}

	public void setRegdate(Date regdate) {
		this.regdate = regdate;
	}

	public String getUse_sign() {
		return use_sign;
	}

	public void setUse_sign(String use_sign) {
		this.use_sign = use_sign;
	}

	public byte[] getPrivate_key() {
		byte[] safeArray = null;
		if (this.private_key != null) {
			safeArray = new byte[this.private_key.length];
			for (int i = 0; i < this.private_key.length ; i++) { safeArray[i] = this.private_key[i]; }
		}
		return safeArray;
	}

	public void setPrivate_key(byte[] private_key) {
		this.private_key = new byte[private_key.length];
		for (int i = 0; i < private_key.length; ++i)
			this.private_key[i] = private_key[i];
	}
}

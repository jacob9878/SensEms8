package com.imoxion.sensems.server.util;

/**
 * Created by sunggyu on 2015-11-08.
 */
public class MailAddress {

    private String domain;

    private String userid;

    private String name;

    private String address;
    
    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
    
}

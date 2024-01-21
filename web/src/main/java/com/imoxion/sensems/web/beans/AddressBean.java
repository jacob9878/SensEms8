package com.imoxion.sensems.web.beans;

public class AddressBean {
	
	private String gname = null;
	private String[] ukeys = null;
	private String ukey = null;
	private String userid = null;
	private String name = null;
	private String name1 = null;
	private String name2 = null;
	private String name3 = null;
	private String alias = null;
	private String email = null;
	private String company = null;
	private String dept = null;
	private String work = null;
	private String home_tel = null;
	private String office_tel = null;
	private String mobile = null;
	private String fax = null;
	private String zipcode = null;
	private String zip1 = null;
	private String zip2 = null;
	private String addr1 = null;
	private String addr2 = null;
	private String regdate = null;
	private String etc1 = null;
	private String etc2 = null;
	private String isshare = null;
	private String pref_phone = null;
	private String phone_sel = null;
	
	public String getAddr1() {
		return addr1;
	}
	public void setAddr1(String addr1) {
		this.addr1 = addr1;
	}
	public String getAddr2() {
		return addr2;
	}
	public void setAddr2(String addr2) {
		this.addr2 = addr2;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}

	public String getRegdate() {
		return regdate;
	}
	public void setRegdate(String regdate) {
		this.regdate = regdate;
	}
	public String getDept() {
		return dept;
	}
	public void setDept(String dept) {
		this.dept = dept;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getFax() {
		return fax;
	}
	public void setFax(String fax) {
		this.fax = fax;
	}
	public String getHome_tel() {
		return home_tel;
	}
	public void setHome_tel(String home_tel) {
		this.home_tel = home_tel;
	}
	public String getIsshare() {
		return isshare;
	}
	public void setIsshare(String isshare) {
		this.isshare = isshare;
	}
	
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getName1() {
		return name1;
	}
	public void setName1(String name1) {
		this.name1 = name1;
	}
	public String getName2() {
		return name2;
	}
	public void setName2(String name2) {
		this.name2 = name2;
	}
	public String getOffice_tel() {
		return office_tel;
	}
	public void setOffice_tel(String office_tel) {
		this.office_tel = office_tel;
	}
	public String getPhone_sel() {
		return phone_sel;
	}
	public void setPhone_sel(String phone_sel) {
		this.phone_sel = phone_sel;
	}
	public String getPref_phone() {
		return pref_phone;
	}
	public void setPref_phone(String pref_phone) {
		this.pref_phone = pref_phone;
	}
	public String getUkey() {
		return ukey;
	}
	public void setUkey(String ukey) {
		this.ukey = ukey;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getWork() {
		return work;
	}
	public void setWork(String work) {
		this.work = work;
	}
	public String getZip1() {
		return zip1;
	}
	public void setZip1(String zip1) {
		this.zip1 = zip1;
	}
	public String getZip2() {
		return zip2;
	}
	public void setZip2(String zip2) {
		this.zip2 = zip2;
	}
	public String getZipcode() {
		return zipcode;
	}
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}
	public String[] getUkeys() {
		String[] safeArray = null;
		if (this.ukeys != null) {
			safeArray = new String[this.ukeys.length];
			for (int i = 0; i < this.ukeys.length; i++) {
				safeArray[i] = this.ukeys[i];
			}
		}
		return safeArray;
	}

	public void setUkeys(String[] ukeys) {
		this.ukeys = new String[ukeys.length];
		for (int i = 0; i < ukeys.length; ++i)
			this.ukeys[i] = ukeys[i];
	}
	public String getGname() {
		return gname;
	}
	public void setGname(String gname) {
		this.gname = gname;
	}
	public String getName3() {
		return name3;
	}
	public void setName3(String name3) {
		this.name3 = name3;
	}
	public String getEtc1() {
		return etc1;
	}
	public void setEtc1(String etc1) {
		this.etc1 = etc1;
	}
	public String getEtc2() {
		return etc2;
	}
	public void setEtc2(String etc2) {
		this.etc2 = etc2;
	}
	
	
	
	
}

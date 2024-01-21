package com.imoxion.sensems.web.beans;

public class ImageManagerBean {
	String ukey = "";
	String userid = "";
	String image_name = "";
	int	image_weight = 0;
	int image_height = 0;
	String image_url = "";
	String reg_time = "";
	String	flag = "";
	private String[] ukeys = null;
	
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public int getImage_height() {
		return image_height;
	}
	public void setImage_height(int image_height) {
		this.image_height = image_height;
	}
	public String getImage_name() {
		return image_name;
	}
	public void setImage_name(String image_name) {
		this.image_name = image_name;
	}
	public String getImage_url() {
		return image_url;
	}
	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}
	public int getImage_weight() {
		return image_weight;
	}
	public void setImage_weight(int image_width) {
		this.image_weight = image_width;
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
	public String getReg_time() {
		return reg_time;
	}
	public void setReg_time(String reg_time) {
		this.reg_time = reg_time;
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
		
}

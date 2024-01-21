package com.imoxion.sensems.web.beans;

public class ErrorCountBean implements Comparable {

	private String type = null;
	private String code = null;
	private int count = 0;
	private String color = null;

	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getColor() { return color; }
	public void setColor(String color) { this.color = color; }
	public int compareTo(Object errorCountBean) {
		int result = 0;
        if (errorCountBean instanceof ErrorCountBean) {
        	ErrorCountBean ecBean = (ErrorCountBean)errorCountBean;
            //해당 값 비교
            if (this.getCount() > ecBean.getCount() ) {
                result = -1;
            } else if (this.getCount() == ecBean.getCount() ) {
            	result = 0;
            } else if (this.getCount() < ecBean.getCount() ) {
            	result = 1;
            }
        }
        return result;
	}
}

package com.imoxion.sensems.web.form;

public class AttachForm {
	private String key = null;

	private String name = null;

	private long size = 0;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}
	
	public String toString(){
		final String TAB = "\n";
		StringBuffer retValue = new StringBuffer();

		retValue.append("AttachInfoBean ( ").append(super.toString()).append(TAB).append("key = ").append(this.key).append(TAB).append("name = ")
				.append(this.name).append(TAB).append("size = ").append(this.size).append(TAB).append(" )");

		return retValue.toString();
	}
	
}

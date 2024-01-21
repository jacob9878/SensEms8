package com.imoxion.sensems.web.beans;

public class HostInfoBean {

	private String hostname = null;
	private int port;
	private int target;
	private int isactive;
	
	private String org_hostname = null;
	private int org_port;
	
	public String getOrg_hostname() {
		return org_hostname;
	}
	public void setOrg_hostname(String org_hostname) {
		this.org_hostname = org_hostname;
	}
	public int getOrg_port() {
		return org_port;
	}
	public void setOrg_port(int org_port) {
		this.org_port = org_port;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public int getIsactive() {
		return isactive;
	}
	public void setIsactive(int isactive) {
		this.isactive = isactive;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public int getTarget() {
		return target;
	}
	public void setTarget(int target) {
		this.target = target;
	}
}

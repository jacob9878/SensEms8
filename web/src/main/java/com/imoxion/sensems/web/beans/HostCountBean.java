package com.imoxion.sensems.web.beans;

public class HostCountBean {

	private String hostname = null;
	private int scount = 0;
	private int ecount = 0;
	private int eratio = 0;
	
	public int getEcount() {
		return ecount;
	}
	public void setEcount(int ecount) {
		this.ecount = ecount;
	}
	public int getEratio() {
		return eratio;
	}
	public void setEratio(int eratio) {
		this.eratio = eratio;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public int getScount() {
		return scount;
	}
	public void setScount(int scount) {
		this.scount = scount;
	}
}

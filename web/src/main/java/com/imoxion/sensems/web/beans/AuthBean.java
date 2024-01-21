package com.imoxion.sensems.web.beans;

public class AuthBean {
	
	private int approval;
	private int image;
	private int groupsel;
	private int receiver;
	private int singlysend;
	private int reject;	
	private int anniversary;
	private int allresult;	
	private int user;
	private int database;
	
	public AuthBean( int[] access_right ){
		this.approval = access_right[0];
		this.image = access_right[1];
		this.groupsel = access_right[2];
		this.receiver = access_right[3];
		this.singlysend = access_right[4];
		this.reject = access_right[5];
		this.anniversary = access_right[6];
		this.allresult = access_right[7];
		this.user = access_right[8];
		this.database = access_right[9];
	}
	
	public int getAllresult() {
		return allresult;
	}
	public void setAllresult(int allresult) {
		this.allresult = allresult;
	}
	public int getAnniversary() {
		return anniversary;
	}
	public void setAnniversary(int anniversary) {
		this.anniversary = anniversary;
	}
	public int getDatabase() {
		return database;
	}
	public void setDatabase(int database) {
		this.database = database;
	}
	public int getGroupsel() {
		return groupsel;
	}
	public void setGroupsel(int groupsel) {
		this.groupsel = groupsel;
	}
	public int getImage() {
		return image;
	}
	public void setImage(int image) {
		this.image = image;
	}
	public int getApproval() {
		return approval;
	}
	public void setApproval(int approval) {
		this.approval = approval;
	}
	public int getReceiver() {
		return receiver;
	}
	public void setReceiver(int receiver) {
		this.receiver = receiver;
	}
	public int getReject() {
		return reject;
	}
	public void setReject(int reject) {
		this.reject = reject;
	}
	public int getSinglysend() {
		return singlysend;
	}
	public void setSinglysend(int singlysend) {
		this.singlysend = singlysend;
	}
	public int getUser() {
		return user;
	}
	public void setUser(int user) {
		this.user = user;
	}	
}

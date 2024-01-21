package com.imoxion.sensems.web.beans;

public class ClientInfo {

	public static final int OS_WINDOW_XP = 1;
	public static final int OS_WINDOW_VISTA = 2;
	public static final int OS_WINDOW_7 = 3;
	public static final int OS_WINDOW_2000 = 4;
	public static final int OS_WINDOW_2003 = 5;
	public static final int OS_WINDOW_ME = 6;
	public static final int OS_WINDOW_98 = 7;
	public static final int OS_WINDOW_2008 = 8;
	public static final int OS_WINDOW_CE = 9;
	public static final int OS_IPHONE = 10;
	public static final int OS_OZ = 11;
	public static final int OS_MACINTOSH = 12;	
	public static final int OS_ANDROID = 13;
	public static final int OS_IPAD = 14;
		
	public static final int BROWSER_MSIE6 = 100;
	public static final int BROWSER_MSIE7 = 101;
	public static final int BROWSER_MSIE8 = 102;
	public static final int BROWSER_CHROME = 103;
	public static final int BROWSER_SAFARI = 104;
	public static final int BROWSER_FIREFOX = 105;
	public static final int BROWSER_OPERA = 106;
	public static final int BROWSER_MSIE9 = 107;
	
	private int os = -1;
	private int broswer = -1;
	
	public int getOs() {
		return os;
	}
	public void setOs(int os) {
		this.os = os;
	}
	public int getBroswer() {
		return broswer;
	}
	public void setBroswer(int broswer) {
		this.broswer = broswer;
	}
}

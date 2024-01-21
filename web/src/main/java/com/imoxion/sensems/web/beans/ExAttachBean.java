/*
 * FileName : ExAttachBean.java
 *
 * 작성자 : Administrator
 * 이메일 : Administrator@imoxion.com
 * Company : (주)아이모션
 * 날짜 : 2007. 02. 01
 *
 * 설명:
 * 
 */
package com.imoxion.sensems.web.beans;

public class ExAttachBean {
    private String ukey = null;
    private String skey = null;
    private String fname = null;
    private String fpath = null;
    
    
    public String getFname() {
        return fname;
    }
    public void setFname(String fname) {
        if (fname != null)
            this.fname = fname;
    }
    public String getFpath() {
        return fpath;
    }
    public void setFpath(String fpath) {
        if (fpath != null)
            this.fpath = fpath;
    }
    public String getSkey() {
        return skey;
    }
    public void setSkey(String skey) {
        if (skey != null)
            this.skey = skey;
    }
    public String getUkey() {
        return ukey;
    }
    public void setUkey(String ukey) {
        if (ukey != null)
            this.ukey = ukey;
    }
    
    
}

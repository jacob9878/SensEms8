/*
 * FileName : PaperBean.java
 *
 * 작성자 : Administrator
 * 이메일 : Administrator@imoxion.com
 * Company : (주)아이모션
 * 날짜 : 2007. 02. 23
 *
 * 설명:
 * 
 */
package com.imoxion.sensems.web.beans;

public class PaperBean {
    private String ukey = "";
    private String paper_name = "";
    private String paper_url = "";
    private String paper_fname = "";
    private String paper_stime = "";
    private String paper_etime = "";
    
    private String syear = "";
    private String smonth = "";
    private String sday = "";
    private String eyear = "";
    private String emonth = "";
    private String eday = "";
    
    public String getEday() {
        return eday;
    }
    public void setEday(String eday) {
        if (eday != null)
            this.eday = eday;
    }
    public String getEmonth() {
        return emonth;
    }
    public void setEmonth(String emonth) {
        if (emonth != null)
            this.emonth = emonth;
    }
    public String getEyear() {
        return eyear;
    }
    public void setEyear(String eyear) {
        if (eyear != null)
            this.eyear = eyear;
    }
    public String getSday() {
        return sday;
    }
    public void setSday(String sday) {
        if (sday != null)
            this.sday = sday;
    }
    public String getSmonth() {
        return smonth;
    }
    public void setSmonth(String smonth) {
        if (smonth != null)
            this.smonth = smonth;
    }
    public String getSyear() {
        return syear;
    }
    public void setSyear(String syear) {
        if (syear != null)
            this.syear = syear;
    }
    public String getPaper_etime() {
        return paper_etime;
    }
    public void setPaper_etime(String paper_etime) {
        if (paper_etime != null)
            this.paper_etime = paper_etime;
    }
    public String getPaper_fname() {
        return paper_fname;
    }
    public void setPaper_fname(String paper_fname) {
        if (paper_fname != null)
            this.paper_fname = paper_fname;
    }
    public String getPaper_name() {
        return paper_name;
    }
    public void setPaper_name(String paper_name) {
        if (paper_name != null)
            this.paper_name = paper_name;
    }
    public String getPaper_stime() {
        return paper_stime;
    }
    public void setPaper_stime(String paper_stime) {
        if (paper_stime != null)
            this.paper_stime = paper_stime;
    }
    public String getPaper_url() {
        return paper_url;
    }
    public void setPaper_url(String paper_url) {
        if (paper_url != null)
            this.paper_url = paper_url;
    }
    public String getUkey() {
        return ukey;
    }
    public void setUkey(String ukey) {
        if (ukey != null)
            this.ukey = ukey;
    }
    
    
}

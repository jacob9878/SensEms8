/*
 * FileName : PollBean.java
 *
 * 작성자 : realkoy
 * 이메일 : dev@imoxion.com
 * Company : (주)아이모션
 * 날짜 : 2008. 01. 04
 *
 * 설명:
 * 
 */
package com.imoxion.sensems.web.beans;

public class ImPollBean {
    private String pid = "";
    private String userid = "";
    private String ptitle = "";
    private String pexplain = "";
    private String ppage = "0";
    private String regdate = "";
    private String ptype = "0";
    private String pstate = "0";
    private String start_time = "";
    private String end_time = "";
    
    
    public String getStart_time() {
        return start_time;
    }
    public void setStart_time(String start_time) {
        if (start_time != null)
            this.start_time = start_time;
    }
    public String getEnd_time() {
        return end_time;
    }
    public void setEnd_time(String end_time) {
        if (end_time != null)
            this.end_time = end_time;
    }
    public String getPid() {
        return pid;
    }
    public void setPid(String pid) {
        if (pid != null)
            this.pid = pid;
    }
    public String getPtitle() {
        return ptitle;
    }
    public void setPtitle(String ptitle) {
        if (ptitle != null)
            this.ptitle = ptitle;
    }
    public String getPexplain() {
        return pexplain;
    }
    public void setPexplain(String pexplain) {
        if (pexplain != null)
            this.pexplain = pexplain;
    }
    public String getPpage() {
        return ppage;
    }
    public void setPpage(String ppage) {
        if (ppage != null)
            this.ppage = ppage;
    }
    public String getRegdate() {
        return regdate;
    }
    public void setRegdate(String regdate) {
        if (regdate != null)
            this.regdate = regdate;
    }
    public String getUserid() {
        return userid;
    }
    public void setUserid(String userid) {
        if (userid != null)
            this.userid = userid;
    }
    public String getPtype() {
        return ptype;
    }
    public void setPtype(String ptype) {
        if (ptype != null)
            this.ptype = ptype;
    }
    public String getPstate() {
        return pstate;
    }
    public void setPstate(String pstate) {
        if (pstate != null)
            this.pstate = pstate;
    }
    
    
}

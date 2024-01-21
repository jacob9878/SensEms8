package com.imoxion.sensems.web.beans;

public class TemplateCategoryBean {

	String cname = null;
    String userid = null;
    String sid = null;
    int count = 0;
    
    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
    }
    
    public String getCname() {
        return cname;
    }
    public void setCname(String cname) {
        this.cname = cname;
    }
    public String getSid() {
        return sid;
    }
    public void setSid(String sid) {
        this.sid = sid;
    }
    public String getUserid() {
        return userid;
    }
    public void setUserid(String userid) {
        this.userid = userid;
    }
    
    
}

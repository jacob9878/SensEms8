package com.imoxion.sensems.web.beans;

import java.util.Date;

public class ActionLogBean {
    String start_date;
    String end_date;
    String userid;
    String menu_key;
    String menu;
    String srch_keyword;
    int start;
    int end;

    private String log_key;
    private Date log_date;
    private String ip;
    private String param;


    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getMenu_key() { return menu_key; }

    public void setMenu_key(String menu_key) { this.menu_key = menu_key; }

    public String getMenu() { return menu; }

    public void setMenu(String menu) {
        this.menu = menu;
    }

    public String getSrch_keyword() {
        return srch_keyword;
    }

    public void setSrch_keyword(String srch_keyword) {
        this.srch_keyword = srch_keyword;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public String getLog_key() {
        return log_key;
    }

    public void setLog_key(String log_key) {
        this.log_key = log_key;
    }

    public Date getLog_date() {
        return log_date;
    }

    public void setLog_date(Date log_date) {
        this.log_date = log_date;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }
}

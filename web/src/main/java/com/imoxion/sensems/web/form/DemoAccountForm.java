package com.imoxion.sensems.web.form;

import java.util.Date;

public class DemoAccountForm {

    private String ukey;    //고유키
    private String email;   //이메일
    private int flag;       //기본계정여부 1:기본계정
    private String userid;  //사용자아이디
    private Date regdate;

    /* 검색 및 페이징 */
    private String cpage = "1";
    private String page_groupsize = "5";
    private String srch_keyword;


    public String getUkey() {
        return ukey;
    }

    public void setUkey(String ukey) {
        this.ukey = ukey;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getCpage() {
        return cpage;
    }

    public void setCpage(String cpage) {
        this.cpage = cpage;
    }

    public String getPage_groupsize() {
        return page_groupsize;
    }

    public void setPage_groupsize(String page_groupsize) {
        this.page_groupsize = page_groupsize;
    }

    public String getSrch_keyword() {
        return srch_keyword;
    }

    public void setSrch_keyword(String srch_keyword) {
        this.srch_keyword = srch_keyword;
    }

    public Date getRegdate() {
        return regdate;
    }

    public void setRegdate(Date regdate) {
        this.regdate = regdate;
    }
}

/*
 * FileName : PaperUserBean.java
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

public class PaperUserBean {
    private String ukey = "";
    private String paper_ukey = "";
    private String userid = "";
    
    
    public String getPaper_ukey() {
        return paper_ukey;
    }
    public void setPaper_ukey(String paper_ukey) {
        if (paper_ukey != null)
            this.paper_ukey = paper_ukey;
    }
    public String getUkey() {
        return ukey;
    }
    public void setUkey(String ukey) {
        if (ukey != null)
            this.ukey = ukey;
    }
    public String getUserid() {
        return userid;
    }
    public void setUserid(String userid) {
        if (userid != null)
            this.userid = userid;
    }
    
    
}

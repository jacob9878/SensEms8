/*
 * FileName : ExRecvBean.java
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

public class ExRecvBean {
    private String ukey = null;
    private String skey = null;
    private String from_email = null;
    private String email = null;
    private String name = null;
    private String macro1 = null;
    private String macro2 = null;
    private String macro3 = null;
    private String success = null;
    private String errcode = null;
    private String err_exp = null;
    private String send_date = null;
    private String recv_date = null;
    private String recv_count = null;
    private String tempid = null;
    
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        if (email != null)
            this.email = email;
    }
    public String getErr_exp() {
        return err_exp;
    }
    public void setErr_exp(String err_exp) {
        if (err_exp != null)
            this.err_exp = err_exp;
    }
    public String getErrcode() {
        return errcode;
    }
    public void setErrcode(String errcode) {
        if (errcode != null)
            this.errcode = errcode;
    }
    public String getFrom_email() {
        return from_email;
    }
    public void setFrom_email(String from_email) {
        if (from_email != null)
            this.from_email = from_email;
    }
    public String getMacro1() {
        return macro1;
    }
    public void setMacro1(String macro1) {
        if (macro1 != null)
            this.macro1 = macro1;
    }
    public String getMacro2() {
        return macro2;
    }
    public void setMacro2(String macro2) {
        if (macro2 != null)
            this.macro2 = macro2;
    }
    public String getMacro3() {
        return macro3;
    }
    public void setMacro3(String macro3) {
        if (macro3 != null)
            this.macro3 = macro3;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        if (name != null)
            this.name = name;
    }
    public String getRecv_count() {
        return recv_count;
    }
    public void setRecv_count(String recv_count) {
        if (recv_count != null)
            this.recv_count = recv_count;
    }
    public String getRecv_date() {
        return recv_date;
    }
    public void setRecv_date(String recv_date) {
        if (recv_date != null)
            this.recv_date = recv_date;
    }
    public String getSend_date() {
        return send_date;
    }
    public void setSend_date(String send_date) {
        if (send_date != null)
            this.send_date = send_date;
    }
    public String getSkey() {
        return skey;
    }
    public void setSkey(String skey) {
        if (skey != null)
            this.skey = skey;
    }
    public String getSuccess() {
        return success;
    }
    public void setSuccess(String success) {
        if (success != null)
            this.success = success;
    }
    public String getTempid() {
        return tempid;
    }
    public void setTempid(String tempid) {
        if (tempid != null)
            this.tempid = tempid;
    }
    public String getUkey() {
        return ukey;
    }
    public void setUkey(String ukey) {
        if (ukey != null)
            this.ukey = ukey;
    }
    
    
}

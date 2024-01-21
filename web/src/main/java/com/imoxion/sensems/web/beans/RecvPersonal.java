/*
 * FileName : RecvPersonal.java
 *
 * 작성자 : Administrator
 * 이메일 : Administrator@imoxion.com
 * Company : (주)아이모션
 * 날짜 : 2006. 11. 14
 *
 * 설명:
 * 
 */
package com.imoxion.sensems.web.beans;

public class RecvPersonal {
    private int id = 0;
    private String ukey = "";
    private String f_key = "";
    private String tempid = "";
    private String tempid2 = "";
    private String from_email = "";
    private String to_email = "";
    private String subject = "";
    private String body = "";
    private String field1 = "";
    private String field2 = "";
    private String field3 = "";
    private String field4 = "";
    private String field5 = "";
    private String field6 = "";
    private String field7 = "";
    private String field8 = "";
    private String field9 = "";
    private String field10 = "";
    private String field11 = "";
    private String field12 = "";
    private String field13 = "";
    private String field14 = "";
    private String field15 = "";
    private String field16 = "";
    private String field17 = "";
    private String field18 = "";
    private String field19 = "";
    private String field20 = "";
    private String field21 = "";
    
    private String success = "";
    private String errcode = "";
    private String err_exp = "";
    private String send_time = "";
    private String recv_time = "";
    private int recv_count = 0;
    private String state = "";
    private String charset = "";
    private int ishtml = 1; 
    private String passwd = "";
    private String utf_data = "0";
    
    public String getUtf_data() {
		return utf_data;
	}
	public void setUtf_data(String utfData) {
		if (utfData != null)
			utf_data = utfData;
	}
	public String getBody() {
        return body;
    }
    public String getPasswd() {
        return passwd;
    }
    public void setPasswd(String passwd) {
        if (passwd != null)
            this.passwd = passwd;
    }
    public void setBody(String body) {
        if (body != null)
            this.body = body;
    }
    public String getCharset() {
        return charset;
    }
    public void setCharset(String charset) {
        if (charset != null)
            this.charset = charset;
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
    public String getF_key() {
        return f_key;
    }
    public void setF_key(String f_key) {
        if (f_key != null)
            this.f_key = f_key;
    }
    public String getField1() {
        return field1;
    }
    public void setField1(String field1) {
        if (field1 != null)
            this.field1 = field1;
    }
    public String getField2() {
        return field2;
    }
    public void setField2(String field2) {
        if (field2 != null)
            this.field2 = field2;
    }
    public String getField3() {
        return field3;
    }
    public void setField3(String field3) {
        if (field3 != null)
            this.field3 = field3;
    }
    public String getFrom_email() {
        return from_email;
    }
    public void setFrom_email(String from_email) {
        if (from_email != null)
            this.from_email = from_email;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getRecv_count() {
        return recv_count;
    }
    public void setRecv_count(int recv_count) {
        this.recv_count = recv_count;
    }
    public String getRecv_time() {
        return recv_time;
    }
    public void setRecv_time(String recv_time) {
        if (recv_time != null)
            this.recv_time = recv_time;
    }
    public String getSend_time() {
        return send_time;
    }
    public void setSend_time(String send_time) {
        if (send_time != null)
            this.send_time = send_time;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        if (state != null)
            this.state = state;
    }
    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        if (subject != null)
            this.subject = subject;
    }
    public String getSuccess() {
        return success;
    }
    public void setSuccess(String success) {
        if (success != null)
            this.success = success;
    }
    public String getTo_email() {
        return to_email;
    }
    public void setTo_email(String to_email) {
        if (to_email != null)
            this.to_email = to_email;
    }
    public String getUkey() {
        return ukey;
    }
    public void setUkey(String ukey) {
        if (ukey != null)
            this.ukey = ukey;
    }
    public String getField4() {
        return field4;
    }
    public void setField4(String field4) {
        if (field4 != null)
            this.field4 = field4;
    }
    public String getField5() {
        return field5;
    }
    public void setField5(String field5) {
        if (field5 != null)
            this.field5 = field5;
    }
    public String getTempid() {
        return tempid;
    }
    public void setTempid(String tempid) {
        if (tempid != null)
            this.tempid = tempid;
    }
    public int getIshtml() {
        return ishtml;
    }
    public void setIshtml(int ishtml) {
        this.ishtml = ishtml;
    }
    public String getTempid2() {
        return tempid2;
    }
    public void setTempid2(String tempid2) {
        if (tempid2 != null)
            this.tempid2 = tempid2;
    }
    public String getField6() {
        return field6;
    }
    public void setField6(String field6) {
        if (field6 != null)
            this.field6 = field6;
    }
    public String getField7() {
        return field7;
    }
    public void setField7(String field7) {
        if (field7 != null)
            this.field7 = field7;
    }
    public String getField8() {
        return field8;
    }
    public void setField8(String field8) {
        if (field8 != null)
            this.field8 = field8;
    }
    public String getField9() {
        return field9;
    }
    public void setField9(String field9) {
        if (field9 != null)
            this.field9 = field9;
    }
    public String getField10() {
        return field10;
    }
    public void setField10(String field10) {
        if (field10 != null)
            this.field10 = field10;
    }
    public String getField11() {
        return field11;
    }
    public void setField11(String field11) {
        if (field11 != null)
            this.field11 = field11;
    }
    public String getField12() {
        return field12;
    }
    public void setField12(String field12) {
        if (field12 != null)
            this.field12 = field12;
    }
    public String getField13() {
        return field13;
    }
    public void setField13(String field13) {
        if (field13 != null)
            this.field13 = field13;
    }
    public String getField14() {
        return field14;
    }
    public void setField14(String field14) {
        if (field14 != null)
            this.field14 = field14;
    }
    public String getField15() {
        return field15;
    }
    public void setField15(String field15) {
        if (field15 != null)
            this.field15 = field15;
    }
    public String getField16() {
        return field16;
    }
    public void setField16(String field16) {
        if (field16 != null)
            this.field16 = field16;
    }
    public String getField17() {
        return field17;
    }
    public void setField17(String field17) {
        if (field17 != null)
            this.field17 = field17;
    }
    public String getField18() {
        return field18;
    }
    public void setField18(String field18) {
        if (field18 != null)
            this.field18 = field18;
    }
    public String getField19() {
        return field19;
    }
    public void setField19(String field19) {
        if (field19 != null)
            this.field19 = field19;
    }
    public String getField20() {
        return field20;
    }
    public void setField20(String field20) {
        if (field20 != null)
            this.field20 = field20;
    }
    public String getField21() {
        return field21;
    }
    public void setField21(String field21) {
        if (field21 != null)
            this.field21 = field21;
    }
    
    
}

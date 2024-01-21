package com.imoxion.sensems.web.form;

import java.util.List;

public class MailWriteForm {
	private String userid;
	
    /** 발송분류 */
    private String categoryid;

    /** 메시지 아이디 */
    private String msgid;
    
    /** 보내는사람 */
    private String mail_from;

    /** 회신주소 */
    private String replyto;

    /** 제목 */
    private String msg_name;

    /** 메일 타입 TEXT, HTML */
    private String ishtml = "1";

    /** 링크추적 */
    private String islink = "1";
    
    /** 예약시간 day : yyyymmdd*/
    private String reserv_day;
    
    /** 예약시간 hour */
    private String reserv_hour;
    
    /** 예약시간 min */
    private String reserv_min;
    
    /** 인코딩 설정 값 */
    private String charset="utf-8";

    /** 에디터 본문 */
    private String content;

    /** 발송시간 : 즉시발송, 예약발송 */
    private String is_reserve = "0";
    
    /** 수신그룹 유형  1:주소록, 3:수신그룹, 4:재발신 */
    private String rectype;
     
    /** 반응분석 day : yyyymmdd */
    private String resp_day;
    
    /** 반응분석 hour */
    private String resp_hour;
    
    /** 이메일 중복 허용 여부 */
    private String is_same_email = "0";

    /** 첨부파일 */
    private String att_keys;
    
    /** 수신그룹 아이디 */
    private String recid;
    
    /** 수신그룹 이름 */
    private String recname;
    
    /** 메일 발송 flag 0:발송, 1:임시보관, 2:사본저장후 발송 */
    private String state;

    private String resend_flag;

    private String old_msgid;

    private String dbkey;

    private String recvid;

    private String linkid;

	public String getRecname() {
		return recname;
	}

	public void setRecname(String recname) {
		this.recname = recname;
	}

	public String getMsgid() {
		return msgid;
	}

	public void setMsgid(String msgid) {
		this.msgid = msgid;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getCategoryid() {
		return categoryid;
	}

	public void setCategoryid(String categoryid) {
		this.categoryid = categoryid;
	}

	public String getMail_from() {
		return mail_from;
	}

	public void setMail_from(String mail_from) {
		this.mail_from = mail_from;
	}

	public String getReplyto() {
		return replyto;
	}

	public void setReplyto(String replyto) {
		this.replyto = replyto;
	}

	public String getMsg_name() {
		return msg_name;
	}

	public void setMsg_name(String msg_name) {
		this.msg_name = msg_name;
	}

	public String getIshtml() {
		return ishtml;
	}

	public void setIshtml(String ishtml) {
		this.ishtml = ishtml;
	}

	public String getIslink() {
		return islink;
	}

	public void setIslink(String islink) {
		this.islink = islink;
	}

	public String getReserv_day() {
		return reserv_day;
	}

	public void setReserv_day(String reserv_day) {
		this.reserv_day = reserv_day;
	}

	public String getReserv_hour() {
		return reserv_hour;
	}

	public void setReserv_hour(String reserv_hour) {
		this.reserv_hour = reserv_hour;
	}

	public String getReserv_min() {
		return reserv_min;
	}

	public void setReserv_min(String reserv_min) {
		this.reserv_min = reserv_min;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getIs_reserve() {
		return is_reserve;
	}

	public void setIs_reserve(String is_reserve) {
		this.is_reserve = is_reserve;
	}

	public String getRectype() {
		return rectype;
	}

	public void setRectype(String rectype) {
		this.rectype = rectype;
	}

	public String getResp_day() {
		return resp_day;
	}

	public void setResp_day(String resp_day) {
		this.resp_day = resp_day;
	}

	public String getResp_hour() {
		return resp_hour;
	}

	public void setResp_hour(String resp_hour) {
		this.resp_hour = resp_hour;
	}

	public String getIs_same_email() {
		return is_same_email;
	}

	public void setIs_same_email(String is_same_email) {
		this.is_same_email = is_same_email;
	}

	public String getAtt_keys() {
		return att_keys;
	}

	public void setAtt_keys(String att_keys) {
		this.att_keys = att_keys;
	}

	public String getRecid() {
		return recid;
	}

	public void setRecid(String recid) {
		this.recid = recid;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getResend_flag() { return resend_flag; }

	public void setResend_flag(String resend_flag) { this.resend_flag = resend_flag; }

	public String getOld_msgid() { return old_msgid; }

	public void setOld_msgid(String old_msgid) { this.old_msgid = old_msgid; }

	public String getDbkey() { return dbkey; }

	public void setDbkey(String dbkey) { this.dbkey = dbkey; }

	public String getRecvid() { return recvid; }

	public void setRecvid(String recvid) { this.recvid = recvid; }

	public String getLinkid() { return linkid; }

	public void setLinkid(String linkid) { this.linkid = linkid; }
}

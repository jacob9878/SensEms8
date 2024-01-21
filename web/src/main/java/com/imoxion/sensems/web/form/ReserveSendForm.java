package com.imoxion.sensems.web.form;

public class ReserveSendForm {

    private String msgid;

    private String userid;

    /** 발송분류 */
    private String categoryid;

    /** 보내는사람 */
    private String mail_from;

    /** 회신주소 */
    private String replyto;

    /** 제목 */
    private String msg_name;

    /** 반복일정 0: 매일, 1:매주, 2:매월 */
    private String rot_flag;

    /** 반복날짜 요일 :1(일) ~ 7(토) , 월 1월 ~ 12월 */
    private String rot_point;

    /** 메일 타입 TEXT, HTML */
    private String ishtml;

    /** 링크추적 */
    private String islink;

    /** 발송 등록 년월일 */
    private String start_time;

    /** 발송 종료 년월일 */
    private String end_time;

    /** 발송시간 (시분) reserv_hour_start와 reserv_minute_start를 조합한 시분임 */
    private String send_time;

    /** 시간 */
    private String reserve_hour;

    /** 분 */
    private String reserve_minute;

    /** 등록일 */
    private String regdate;

    /** 최근 발송일 */
    private String last_send;

    /** 인코딩 설정 값 */
    private String charset;

    /** 에디터 본문 */
    private String content;

    /** 수신자 목록**/
    private String recid;

    /** 업데이트 유무 확인 **/
    private String update_flag;

    /** 수신그룹 이름 **/
    private String receiver_name;

    public String getReceiver_name() {
        return receiver_name;
    }

    public void setReceiver_name(String receiver_name) {
        this.receiver_name = receiver_name;
    }

    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
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

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getSend_time() {
        return send_time;
    }

    public void setSend_time(String send_time) {
        this.send_time = send_time;
    }

    public String getRegdate() {
        return regdate;
    }

    public void setRegdate(String regdate) {
        this.regdate = regdate;
    }

    public String getLast_send() {
        return last_send;
    }

    public void setLast_send(String last_send) {
        this.last_send = last_send;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getRot_flag() {
        return rot_flag;
    }

    public void setRot_flag(String rot_flag) {
        this.rot_flag = rot_flag;
    }

    public String getRot_point() {
        return rot_point;
    }

    public void setRot_point(String rot_point) {
        this.rot_point = rot_point;
    }

    public String getReserve_hour() {
        return reserve_hour;
    }

    public void setReserve_hour(String reserve_hour) {
        this.reserve_hour = reserve_hour;
    }

    public String getReserve_minute() {
        return reserve_minute;
    }

    public void setReserve_minute(String reserve_minute) {
        this.reserve_minute = reserve_minute;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRecid() {
        return recid;
    }

    public void setRecid(String recid) {
        this.recid = recid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUpdate_flag() {
        return update_flag;
    }

    public void setUpdate_flag(String update_flag) {
        this.update_flag = update_flag;
    }
}
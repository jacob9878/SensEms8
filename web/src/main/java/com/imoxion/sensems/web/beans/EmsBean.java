package com.imoxion.sensems.web.beans;

public class EmsBean {
	/** 발송대기 */
	public static final String STATUS_WAIT_SEND = "000";
	
	/** 임시보관중 */
	public static final String STATUS_DRAFT = "007";
	
	/** 수신자 추출중 */
	public static final String STATUS_WAIT_RECEIVER_SELECT = "010";
	
	/** 발송중 */
	public static final String STATUS_SENDING = "030";
	
	/** 발송중 발송중지  */
	public static final String STATUS_STOP_AFTER_SEND = "031";
	
	/** 재전송중 */
	public static final String STATUS_RESEND = "032";
	
	/** 로그 정리중 */
	public static final String STATUS_WAIT_LOG = "040"; 
	
	/** 수신대상자 없음 */
	public static final String STATUS_NO_RECEIVER = "-00"; 
	
	/** 수신자목록 생성실패 */
	public static final String STATUS_FAIL_MAKE_RECEIVER = "-10"; 
	
	/** 수신자 추출완료 */
	public static final String STATUS_COMPLETE_RECEIVER_SELECT = "+10"; 
	
	/** 수신리스트 생성실패(제한초과) */
	public static final String STATUS_OVER_RECEIVER_COUNT = "-20";
	
	/** 발송완료 */
	public static final String STATUS_SEND_OK = "+30";
	
	/** 대기중 전송중지 */
	public static final String STATUS_STOP_BEFORE_SEND = "100";
	
	/** 승인대기중 */
	public static final String STATUS_WAIT_APPROVAL = "111";
	
	/** 수신그룹 유형 : 주소록 */
	public static final String RECE_TYPE_ADDR = "1";
	
	/** 수신그룹 유형 : 수신그룹 */
	public static final String RECE_TYPE_RECEIVE_GROUP = "3";
	
	/** 수신그룹 유형 : 재발신 */
	public static final String RECE_TYPE_RESEND = "4";
	
	
    private String categoryid = null;
    private String msgid = null;
    private String userid  = null;
    private String recid = null;
    private String recname = null;
    private String templateid = null;
    private String rectype = null;
    private String msg_name = null;
    private String query = null;
    private String reserv_time = null;
    private String regdate = null;
    private String resp_time = null;
    private String total_send = null;
    private String cur_send = null;
    private String start_time  = null;
    private String send_start_time = null;
    private String end_time = null;
    private String stop_time = null;

    /**
     * state 값 참조
     * 000: 발송대기
     * 007: 임시보관중
     * 010: 수신자 추출중
     * 030: 발송중
     * 031: 발송중지
     * 032: 재전송중
     * 040: 로그 정리중
     * -00: 수신대상자 없음
     * -10: 수신자목록 생성실패
     * +10: 수신자 추출완료
     * -20: 수신리스트 생성실패(제한초과)
     * +30: 발송완료
     * 100: 전송중지
     * 111: 승인대기중
     */
	private String state  = "000";
    private String stateValue = null;
    private String isaretmail = null;
    private String parentid = null;
    private String isattach = null;
    private String mail_from = null;
    private String replyto = null;
    private String islink = null;
    private String charset = null;
    private String contents = null;
    private String connstr = null;
    
    private int resend_num;
    private int resend_step;
    private String extended = null;
    
    private String url_attach = null;
    private String url_attach_name = null;
    
    private String temp_campid = null; 
    private int ishtml = 1;
    private int is_same_email = 0;
    
    private int error_resend = 0;
    private int cur_resend = 0;
    
    // 받는사람 이름 머지용...
    private String to_name = null;
    
    private String dbkey;

    private String msg_path = null;
    /**
     * isstop 값 설정
     * 0: 중지아님,
     * 1: 발송중지(중지된 상태),
     * 2: 발송중지 요청(아직 중지된 상태는 아님)
     *  -> 웹에서 발송중지 버튼 클릭시 STATE 컬럼값이 다음에 해당하는 경우에만 처리 가능하며 isstop을 2로 처리하면 서버에서 자동으로 1로 변경됨
     * (000: 발송대기, 010: 수신자 추출중, 030: 발송중, 032: 재전송중,+10: 수신자 추출완료)
     */
    private String isstop = "0";

    
    public String getMsg_path() {
		return msg_path;
	}

	public void setMsg_path(String msg_path) {
		this.msg_path = msg_path;
	}

	public String getDbkey() {
		return dbkey;
	}

	public void setDbkey(String dbkey) {
		this.dbkey = dbkey;
	}

	public String getIsstop() {
        return isstop;
    }

    public void setIsstop(String isstop) {
        this.isstop = isstop;
    }

    public String getStop_time() {
		return stop_time;
	}
	public void setStop_time(String stopTime) {
		stop_time = stopTime;
	}
    public String getSend_start_time() {
		return send_start_time;
	}
	public void setSend_start_time(String sendStartTime) {
		send_start_time = sendStartTime;
	}
	
	public String getTo_name() {
		return to_name;
	}
	public void setTo_name(String toName) {
		if (toName != null)
			to_name = toName;
	}
	public int getError_resend() {
		return error_resend;
	}
	public void setError_resend(int errorResend) {
		error_resend = errorResend;
	}
	public int getCur_resend() {
		return cur_resend;
	}
	public void setCur_resend(int curResend) {
		cur_resend = curResend;
	}
	public int getIs_same_email() {
        return is_same_email;
    }
    public void setIs_same_email(int is_same_email) {
        this.is_same_email = is_same_email;
    }
    public int getIshtml() {
        return ishtml;
    }
    public void setIshtml(int ishtml) {
        this.ishtml = ishtml;
    }
    public String getTemp_campid() {
        return temp_campid;
    }
    public void setTemp_campid(String temp_campid) {
        if (temp_campid != null)
            this.temp_campid = temp_campid;
    }
    public String getUrl_attach_name() {
        return url_attach_name;
    }
    public void setUrl_attach_name(String url_attach_name) {
        if (url_attach_name != null)
            this.url_attach_name = url_attach_name;
    }
    public String getCategoryid() {
        return categoryid;
    }
    public void setCategoryid(String categoryid) {
        this.categoryid = categoryid;
    }
    public String getCur_send() {
        return cur_send;
    }
    public void setCur_send(String cur_send) {
        this.cur_send = cur_send;
    }
    public String getEnd_time() {
        return end_time;
    }
    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }
    
    public String getExtended() {
        return extended;
    }
    public void setExtended(String extended) {
        this.extended = extended;
    }
    public String getIsaretmail() {
        return isaretmail;
    }
    public void setIsaretmail(String isaretmail) {
        this.isaretmail = isaretmail;
    }
    public String getMsg_name() {
        return msg_name;
    }
    public void setMsg_name(String msg_name) {
        this.msg_name = msg_name;
    }
    public String getMsgid() {
        return msgid;
    }
    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }
    public String getParentid() {
        return parentid;
    }
    public void setParentid(String parentid) {
        this.parentid = parentid;
    }
    public String getRecid() {
        return recid;
    }
    public void setRecid(String recid) {
        this.recid = recid;
    }

    public String getRegdate() {
        return regdate;
    }
    public void setRegdate(String regdate) {
        this.regdate = regdate;
    }
    public int getResend_num() {
        return resend_num;
    }
    public void setResend_num(int resend_num) {
        this.resend_num = resend_num;
    }
    public int getResend_step() {
        return resend_step;
    }
    public void setResend_step(int resend_step) {
        this.resend_step = resend_step;
    }
    public String getReserv_time() {
        return reserv_time;
    }
    public void setReserv_time(String reserv_time) {
        this.reserv_time = reserv_time;
    }
    public String getResp_time() {
        return resp_time;
    }
    public void setResp_time(String resp_time) {
        this.resp_time = resp_time;
    }
    public String getStart_time() {
        return start_time;
    }
    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
    public String getTotal_send() {
        return total_send;
    }
    public void setTotal_send(String total_send) {
        this.total_send = total_send;
    }
    public String getUserid() {
        return userid;
    }
    public void setUserid(String userid) {
        this.userid = userid;
    }
    public String getIsattach() {
        return isattach;
    }
    public void setIsattach(String isattach) {
        this.isattach = isattach;
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
    public String getRectype() {
        return rectype;
    }
    public void setRectype(String rectype) {
        this.rectype = rectype;
    }
    public String getTemplateid() {
        return templateid;
    }
    public void setTemplateid(String templateid) {
        this.templateid = templateid;
    }
    public String getIslink() {
        return islink;
    }
    public void setIslink(String islink) {
        this.islink = islink;
    }
    public String getQuery() {
        return query;
    }
    public void setQuery(String query) {
        this.query = query;
    }
    public String getStateValue() {
        return stateValue;
    }
    public void setStateValue(String stateValue) {
        this.stateValue = stateValue;
    }
    public String getCharset() {
        return charset;
    }
    public void setCharset(String charset) {
        this.charset = charset;
    }
    public String getRecname() {
        return recname;
    }
    public void setRecname(String recname) {
        this.recname = recname;
    }
    public String getConnstr() {
        return connstr;
    }
    public void setConnstr(String connstr) {
        this.connstr = connstr;
    }
    public String getContents() {
        return contents;
    }
    public void setContents(String contents) {
        this.contents = contents;
    }
    public String getUrl_attach() {
        return url_attach;
    }
    public void setUrl_attach(String url_attach) {
        if (url_attach != null)
            this.url_attach = url_attach;
    }

    
}

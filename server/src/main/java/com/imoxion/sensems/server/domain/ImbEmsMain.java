package com.imoxion.sensems.server.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Setter
@Getter
@ToString(exclude = {"contents"})
public class ImbEmsMain implements Serializable {
    public final static String RECTYPE_ADDR = "1";
    public final static String RECTYPE_RECV = "3";
    public final static String RECTYPE_RESEND = "4";

    private String msgid;
    private String categoryid;
    private String userid;
    private String mail_from;
    private String replyto;
    private String recid;
    private String recname;
    private String templateid;
    private String rectype;
    private String dbkey;
    private String query;
    private String msg_name;
    private String msg_path;
    private String reserv_time;
    private String regdate;
    private String resp_time;
    private int total_send = 0;
    private int cur_send = 0;
    private String start_time;
    private String end_time;
    private String stop_time;
    private long send_start_time = 0L;
    /**
     * 000: 발송대기
     * 007: 임시보관중
     * 010: 수신자 추출중
     * 030: 발송중
     * 031: 전송중지(발송중)
     * 032: 재전송중
     * 040: 로그 정리중
     * -00: 수신대상자 없음
     * -10: 수신자목록 생성실패
     * +10: 수신자 추출완료
     * -20: 수신리스트 생성실패(제한초과)
     * +30: 발송완료
     * 011: 전송중지(수신자추출중)
     * 100: 전송중지(대기중)
     * 111: 승인대기중
     */
    private String state = "000";
    private String parentid;
    private int resend_num = 0;
    private int resend_step = 0;
    private String isattach = "0";
    private String islink = "0";
    private String charset;
    /**
     * 999 : 에러재발신, 미수신자 재발신 등의 재발신
     */
    private String extended;
    private String temp_campid;
    private String temp_mailid;
    private int ishtml = 1;
    private int is_same_email = 0;
    private int error_resend = 0;
    private int cur_resend = 0;
    private String send_interval = "0";
    private String priority;
    /**
     * 0: 중지아님,
     * 1: 발송중지(중지된 상태),
     * 2: 발송중지 요청(아직 중지된 상태는 아님)
     *  -> 웹에서 발송중지 버튼 클릭시 STATE 컬럼값이 다음에 해당하는 경우에만 처리 가능하며,
     *  isstop을 2로 처리하면 서버에서 자동으로 1로 변경됨
     * (000: 발송대기,  010: 수신자 추출중, 030: 발송중, 032: 재전송중, +10: 수신자 추출완료)
     *
     * 재전송 처리시에는 이 값을 다시 0으로 변경해주고,
     * state 값을 100 -> 000, 011 -> 010, 031 -> 032 로 업데이트
     */
    private String isstop = "0";

    private String contents;

}

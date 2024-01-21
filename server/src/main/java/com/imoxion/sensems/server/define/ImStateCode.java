package com.imoxion.sensems.server.define;

public class ImStateCode {

    public final static String ST_WAIT_0 = "";               //  발송대기
    public final static String ST_WAIT = "000";              // 발송대기
    public final static String ST_DRAFT = "007";             // 임시보관중
    public final static String ST_EXTRACTING_RECV = "010";   // 수신자 추출중
    public final static String ST_SENDING = "030";           // 발송중
    public final static String ST_STOP_SENDING = "031";      // 전송중지(발송중)
    public final static String ST_RESENDING = "032";         // 재전송중
    public final static String ST_LOGGING = "040";               // 로그 정리중
    public final static String ST_NO_RECV = "-00";           // 수신대상자 없음
    public final static String ST_FAIL_RECV = "-10";         // 수신자목록 생성실패
    public final static String ST_FINISH_RECV = "+10";       // 수신자 추출완료
    public final static String ST_FAIL_RECV_LIMIT = "-20";   // 수신리스트 생성실패(제한초과)
    public final static String ST_FINISH_SEND = "+30";       // 발송완료
    public final static String ST_STOP_RECV = "011";         // 전송중지(수신자추출중)
    public final static String ST_STOP_WAIT = "100";         // 전송중지(대기중)
    public final static String ST_WAIT_APPROVAL = "111";     // 승인대기중
}

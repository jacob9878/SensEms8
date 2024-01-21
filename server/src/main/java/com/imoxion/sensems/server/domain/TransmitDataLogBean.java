package com.imoxion.sensems.server.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.ibatis.type.Alias;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * 메일 로그
 */
@Setter
@Getter
@ToString
@Alias("TransmitDataLogBean")
public class TransmitDataLogBean {

    /** 로그아이디 */
    private String traceId;
    /** 서버아이디 **/
    private String serverid;
    /** 일시 */
    private Date logDate;
    /** 아이디 */
    private String authid;
    /** 제목 */
    private String subject;
    /** 발신자 */
    private String mailFrom;
    /** 수신자 */
    private String rcptTo;
    /** 발신도메인 */
    private String fromDomain;
    /** 수신도메인 */
    private String rcptDomain;
    /** 발송아이피*/
    private String ip;
    /** 메일사이즈 */
    private String mailSize;
    /** 송수신구분  S:송신 / R:수신 */
    private String transmitFl;
    /** 로컬구분  */
    private String localFl;
    /** 처리결과 1: 성공 / 0:실패  */
    private String result;
    /** 이유 */
    private String description;
    /** 에러코드 */
    private int errCode;
    /** 에러메시지 */
    private String errmsg;
    /** 기타 */
    private String etc;
    /** 원래 traceid */
    private String originalTraceId;
    /** 그룹키 **/
    private String groupKey;
    /** 수신자별 고유키 **/
    private String rcptKey;
    /** T: 테스트메일, D: DB연동외부메일(WEB API이용), A: Smtp 인증, R: 릴레이연동, C:건별재발신 **/
    private String send_type;


    public void writeLogger() {
        LoggerFactory.getLogger("TRANSMIT_DATALOG").info(this.toString());
    }

}

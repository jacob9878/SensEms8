package com.imoxion.sensems.server.logger.event;

import com.imoxion.common.util.ImTimeUtil;
import com.imoxion.sensems.server.logger.ProcessLogger;
import com.imoxion.sensems.server.util.JSONUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.sf.json.JSONObject;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * 메일 송수신 로거
 */
@Getter
@Setter
@ToString
public class TransmitLogger implements ProcessLogger {

	public static final String LOGGER_NAME = "TRANSMIT";

    /**
     * 결과 : 송수신 성공
     */
    public final static int STATE_SUCCESS = 1;
    /**
     * 결과 : 송수신 실패
     */
    public final static int STATE_FAIL = 0;
    /**
     * 결과 : 송수신 처리중
     */
    public final static int STATE_ING = 2;

    private String logdate;

	private String headerFrom;

    private String from;

    private String to;
    
    private String domain;

    private String subject = "-";
    
    private String ip;

    private Long size;
    
    private String description;

    private String work;

    private boolean success = false;

    // 0:실패, 1: 성공, 2:진행중
    private int resultState = 0;

	private Object argument;
	
	private String serverid = "0";
	
	// 추적로그 항목을 추가한다.
	private String traceid = "NA";
	
	private String org_traceid = "";

	private String errmsg;

	private String authid;

	private String etc;

	private Integer errcode;

	private String groupkey;

	private String rcptkey;

    //  T: 테스트메일, D: DB연동외부메일(WEB API이용), A: Smtp 인증, R: 릴레이연동, C:건별재발신
    private String send_type;


	public void info(){
        this.logdate = ImTimeUtil.getDateFormat(new Date(),"yyyyMMddHHmmss");
        LoggerFactory.getLogger(LOGGER_NAME).info( JSONObject.fromObject(this, JSONUtils.getNullSkipJsonConfig()).toString() );
    }
}
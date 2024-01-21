package com.imoxion.sensems.server.domain;

import com.imoxion.common.util.ImStringUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.Alias;

import java.util.Date;

@Setter
@Getter
@ToString
@Alias("TransmitStatisticsData")
public class TransmitStatisticsData {
    /**
     * 송/수신 구분 : 송신 메일
     */
    public final static char TRANSMIT_SEND = 'S';
    /**
     * 송/수신 구분 : 수신 메일
     */
    public final static char TRANSMIT_RECV = 'R';
    /**
     * 송/수신 구분 : 불명확한 송수신
     */
    public final static char TRANSMIT_UNKNOWN = 'U';
    /**
     * 결과 : 송수신 성공
     */
    public final static char RESULT_SUCCESS = '1';
    /**
     * 결과 : 송수신 실패
     */
    public final static char RESULT_FAIL = '0';
    /**
     * 결과 : 송수신 처리중
     */
    public final static char RESULT_ING = '2';

    public final static char LOCAL_YES = '1';

    public final static char LOCAL_NO = '0';

    private String stats_key;
    private Date logdate;
    private String mhost;
    private String authid;
    private String mailfrom;
    private String rcptto;
    private String from_domain;
    private String rcpt_domain;
    private Long mailsize;
    private char transmit_fl;
    private char local_fl;
    private char result;
    private int errcode;
    private String errmsg;
    private String etc;
    private String subject;
    private String traceid = "NA";
    private String description;
    private String ip;
    private String serverid;
    private String args = "";
    private String org_traceid = "";
    private String groupkey;
    private String rcptkey;
    /** T: 테스트메일, D: DB연동외부메일(WEB API이용), A: Smtp 인증, R: 릴레이연동, C:건별재발신 **/
    private String send_type;

	public void setArgs(String args) {
		if(StringUtils.isNotEmpty(args)) {
			this.args = ImStringUtil.stringCutterByte(args, 190);
		}
	}
    public void setErrmsg(String errmsg) {
        if(StringUtils.isNotEmpty(errmsg)) {
            this.errmsg = ImStringUtil.stringCutterByte(errmsg, 190);
        }
    }

}

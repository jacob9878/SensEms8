package com.imoxion.sensems.server.smtp;

import com.imoxion.sensems.server.util.UUIDService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.ssl.SslContext;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.net.Socket;
import java.util.ArrayList;

@Getter
@Setter
@ToString
public class ImSmtpSession {
	public final static int SMTP_STATE_DEF = 0;
	public final static int SMTP_STATE_HELO = 1;
	public final static int SMTP_STATE_AUTH = 2;
	public final static int SMTP_STATE_MAILFROM = 3;
	public final static int SMTP_STATE_RCPTTO = 4;

	private ChannelHandlerContext channelHandlerContext;
	private Socket socket = null;
	private String traceID;
	private String msgID;
	// 연결된 client ip
	private String peerIP;
	private int peerPort;
	private String from;
	private ArrayList<String> arrRcpt = new ArrayList<String>();
	private ArrayList<String> arrRcptMhost = new ArrayList<String>();
	private String tempPath;
	private String fromDomain;
	private String fromMhost;
	private String fromUserID;
	private String fromIP;
	private boolean aliasFromUserID = false;
	private String orgFromUserIDOfAlias;
	private String logonUser;
	private String logonEmailID;

	private String logonMhost;
	private String logonDomain;
	private String heloDomain;
	private String timeStamp;
	private String connDate;
	private String connDateTime;

	private String sessionKey;
	//private ImUserInfo authUI = null;

	private int currRcpt = 0;
	private int totRcpt = 0;
	private long flags = 0;
	private long msgSize = 0;
	private long dataSize = 0;
	private int commandCount = 0;
	private int msgCount = 0;
	private boolean startTLS = false;
	private boolean relay = false;
	private boolean fromLocalUser = false;
	// 발신자가 메일링리스트(M)
	private boolean fromMlist = false;
	private int rsetCount = 0;

	private int smtpState = SMTP_STATE_DEF; //default : 0, 1 : HELO, 2 : MAIL FROM, 3 : RCPT TO

	/*
	 * Author : jungyc
	 * Date : 2007.03.15
	 * Comment : 내부 메일에서 발신된 메일과 외부에서 발신된 메일을 구분하기 위한 변수 추가
	 */
	private String localMsgid;	// 내부 메시지 아이디
	private String localDomain;	// 내부 도메인
	private String localUserid;	// 내부 사용자 아이디

	private boolean auth = false;
	private String auth_user;

	private boolean local = false;
	private boolean bulk = false;

	// 수신 허용 IP 여부
	private boolean allowIP = false;

	private String lastLoginTime;

	private boolean error = false;
	private String errorMessage;

	private String country;
	private String country_name;

	private int ipPriv  = 0; // 아이피 권한 여부.1:릴레이 IP

	// 웹에서 보낸편지함에 저장되는 메일 원본, 저널링서버로만 던지는 용도
	private boolean journalSend = false;

	private boolean connectProxy = false;

	private SslContext sslContext;

	public void addCommandCount(){
		commandCount++;
	}

	public ImSmtpSession(SslContext sslContext){
		this.sslContext = sslContext;
		initSession();
	}

	public void initSession() {
		traceID = UUIDService.getTraceID();

		resetSession();
	}

	public void resetSession() {
		fromDomain = "";
		from = "";
		orgFromUserIDOfAlias = "";
		msgID = "";
		msgSize = 0;
		arrRcpt.clear();
		currRcpt = 0;
		tempPath = "";

		if (flags == 1) {
			smtpState = SMTP_STATE_AUTH;
			return;
		}
		if (smtpState >= SMTP_STATE_HELO) {
			smtpState = SMTP_STATE_HELO;
			return;
		}
	}
}
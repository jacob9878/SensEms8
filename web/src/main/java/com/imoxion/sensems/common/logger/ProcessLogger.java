package com.imoxion.sensems.common.logger;

/**
 * Created by Administrator on 2014-12-23.
 */
public interface ProcessLogger {

    /**
     * 서비스 구분 - 웹메일
     */
    public static final String SERVICE_WEBMAIL = "WEBMAIL";

    /**
     * 서비스 구분 - 모바일웹
     */
    public static final String SERVICE_MOBILEWEB = "MOBILE";

    /**
     * 서비스 구분 - IMAP
     */
    public static final String SERVICE_IMAP = "IMAP";

    /**
     * 서비스 구분 - SMTP
     */
    public static final String SERVICE_SMTP = "SMTP";

    /**
     * 서비스 구분 - POP3
     */
    public static final String SERVICE_POP3 = "POP3";

	/**
	 * 서비스 구분- AGENT
	 */
	public static final String SERVICE_AGENT = "AGENT";


    public static final String LOGGER = "DBLOG_TRANSMIT";

    /**
     * 웹에서 메일 발송
     */
    public static final String WORK_WEB_SEND = "WEBSEND";

    /**
     * SMTP에서 메일 수신
     */
    public static final String WORK_RECEIVE = "RECEIVE";

    /**
     * SMTP에서 메일을 전달
     * 외부 SMTP서버로 메일을 전달을 의미한다.
     */
    public static final String WORK_DELIVERY = "DELIVERY";

    /**
     * SSO 로그인
     */
    public static final String AUTHTYPE_SSO = "SSO";

    /**
     * 웹메일 단독 로그인
     */
    public static final String AUTHTYPE_STANDALONE = "STANDALONE";

    /**
     * 접속 비허용국가
     */
    public static final String AUTHTYPE_NON_PERMITTED_COUNTRY = "NON_PERMITTED_COUNTRY";



    /**
     * 로거에 INFO 로그를 남긴다.
     */
    public void info();
}

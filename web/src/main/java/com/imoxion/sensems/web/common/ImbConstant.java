package com.imoxion.sensems.web.common;


import com.imoxion.common.util.ImConfLoaderEx;

public class ImbConstant {
	/**
     * SSL 서비스 사용 안함
     */
    public static final int SSL_TYPE_NONE = 0;

    /**
     * 전체 URL 에 대해서 SSL 서비스
     */
    public static final int SSL_TYPE_TOTAL = 1;

    /**
     * 특정 URL 에 대해서 SSL 서비스
     */
    public static final int SSL_TYPE_SPECIFIC = 2;
    /**
     * SSL, 일반 모두 사용
     */
    public static final int SSL_TYPE_ANY = 3;

	public final static ImConfLoaderEx ImConfLoader = ImConfLoaderEx.getInstance("sensems.home","sensems.xml");

	/**
	 * XSS 필터 사용 여부
	 */
	public final static boolean USE_XSS_FILTER = ImConfLoader.getProfileInt("filter", "xss.use", 0) == 1 ? true : false;

	public final static String DATABASE_TYPE = ImConfLoader.getProfileString("database", "dbtype");

	/** url aes key */
	public final static String URL_AES_KEY = ImConfLoader.getProfileString("url", "aes_key");
	
	/** 링크추적 url */
	public final static String AD_URL = ImConfLoader.getProfileString("url", "ad_url");
	
	/** 수신거부 url */
	public final static String REJECT_URL = ImConfLoader.getProfileString("url", "reject_url");
	
	/** 수신확인 url */
	public final static String RCPT_URL = ImConfLoader.getProfileString("url", "rcpt_url");
	
	/** 첨부파일 다운로드 url */
	public final static String DOWNLOAD_URL = ImConfLoader.getProfileString("url", "download");
	
	/** msg_path */
	public final static String MSG_PATH = ImConfLoader.getProfileString("general", "msg_path");
	
	/** 첨부파일 저장 경로 */
	public final static String ATTACH_PATH = ImConfLoader.getProfileString("attach", "path");

	/** 첨부파일 최대 업로드 개수 */
	public final static int ATTACH_MAX_COUNT = ImConfLoader.getProfileInt("attach", "max_count", 5);
	
	/** 첨부파일 최대 업로드 크기 */
	public final static int ATTACH_MAX_SIZE = ImConfLoader.getProfileInt("attach", "max_size", 10);
	
	/** 첨부파일 만료일 */
	public final static int ATTACH_EXPIRE_DAY = ImConfLoader.getProfileInt("attach", "expire_day", 14);

    /**
     * 데이터 암호화 사용여부
     */
    public final static boolean DATABASE_ENCRYPTION_USE = ImConfLoader.getProfileString("database.encryption", "use").equals("1") ? true : false;
    /**
     * 데이터베이스 관리 정보 암호화 키
     */
    public final static String DATABASE_AES_KEY = ImConfLoader.getProfileString("database.encryption", "aes_key");

	public final static String TEMPFILE_PATH = ImConfLoader.getProfileString("general", "tempfile");

	public final static String SENSDATA_PATH = ImConfLoader.getProfileString("sensdata", "path");

	public final static boolean USE_SELLANGUAGE = ImConfLoader.getProfileString("general","use_sellanguage").equals("1") ? true : false;

	public final static String LANG = ImConfLoader.getProfileString("general","lang.multiple");

	public final static String DEFAULT_LANG = ImConfLoader.getProfileString("general","lang.default");


	/**
	 * 이미지 경로
	 */
	public final static String IMAGE_SERVER = ImConfLoader.getProfileString("general","image_server");

	/**
	 * 대용량 파일 저장 기간
	 */
	public final static int BIGFILE_TERM = ImConfLoader.getProfileInt("bigfile","store" ,15);
	
	/**
	 * message mac값 생성 알고리즘
	 * MD5, SHA-1, SHA-256
	 */
	public final static String MD_ALGORITHM = ImConfLoader.getProfileString("general", "message_digest_algorithm");
	
	/**
	 * SSO 로그인시 TIMEOUT LIMIT
	 */
	public final static int SSO_TIMEOUT = ImConfLoader.getProfileInt("sso","timeout" ,10);
	public final static String PASS_SECU_TYPE = ImConfLoader.getProfileString("password","type");

	/**
	 * 로그인 실패 횟수에 따른 계정 잠금 설정 파일 불러오기
	 */
	public final static boolean USE_ACCOUNT_DENY = ImConfLoader.getProfileInt("account_deny", "use",0) == 1 ? true : false;
	
	public final static int DENY_COUNT = ImConfLoader.getProfileInt("account_deny", "deny_count",5);
	
	public final static int DENY_TIME = ImConfLoader.getProfileInt("account_deny", "deny_time",10);

	public final static boolean USE_CAPTCHA=ImConfLoader.getProfileInt("captcha","use",0) == 1 ? true : false;


	/**
	 * 패스워드 변경 강제 사용 등록
	 */
	public final static boolean USE_PASSWORD_CHANGE=ImConfLoader.getProfileInt("password","use_change_day",0) == 1 ? true : false;
	
	public final static long PASSWORD_CHANGE_PERIOD = ImConfLoader.getProfileInt("password", "change_day",90);
	public final static String PASS_USE_SALT = ImConfLoader.getProfileString("password","use_salt");
	
	/* minutes */
	public final static int SESSION_TIMEOUT = ImConfLoader.getProfileInt("general", "session.timeout", 30);
	
	public final static int SSL_TYPE = ImConfLoader.getProfileInt("ssl", "type", 0);

	private static ImbConstant constant = null;

	public static ImbConstant getInstance(){
		if( constant == null ){
			constant = new ImbConstant();
		}
		return constant;
	}


}
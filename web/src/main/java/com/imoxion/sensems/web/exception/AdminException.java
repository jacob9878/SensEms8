/**
 *
 */
package com.imoxion.sensems.web.exception;

/**
 * @author : minideji
 * @date : 2012. 10. 18.
 * @desc : 관리자관련 Exception 클래스
 *
 *
 */

public class AdminException extends CommonException {
    // TODO Exceptions 미사용 부분 처리 정리 필요
    /**
     * @param error_code
     */
    public AdminException(int error_code) {
        super(error_code);
    }

    /** Session 에러 발생 */
    public static final int EXPIRED_SESSION = 3000;

    /** 라이센스(mercertkey.key) 파일 관련 에러 */
    public static final int MERCERTKEY_FILE_ERROR = 3001;

    //	/** 파일 읽기 에러 */
    //	public static final int FILE_READ_ERROR = 3002;

    /** 그룹정보가 존재하지 않을 경우 */
    public static final int NO_GROUP_INFO = 3003;

    /** 같은 도메인이 존재 할 경우 */
    public static final int SAME_DOMAIN = 3004;

    /** 같은 가상 도메인이 존재 할 경우 */
    public static final int SAME_ALIAS_DOMAIN = 3005;

    /** 라이센스 사용자수 초과 에러 */
    public static final int USER_OVER_LICENSE_ERROR = 3006;

    /** 그룹명이 존재 할 경우 */
    public static final int SAME_GROUP_NAME = 3007;

    /** 이미 등록되어 있는 스팸 메일의 경우 */
    public static final int SAME_SPAM_EMAIL = 3008;

    /** 이미 등록되어 있는 스팸 아이피의 경우 */
    public static final int SAME_SPAM_IP = 3009;

    /** 이미 등록되어 있는 카테고리의 경우 */
    public static final int SAME_CATEGORY = 3010;

    /**
     * 같은 이름 존재함. : error 724
     */
    public static final int SAME_BOARD = 3011;

    public static final int ORDER_UPDATE_ERROR_OF_NOTICE_BOARD = 3012;

    public static final int ORDER_UPDATE_ERROR_OF_BOARD = 3013;

    /**
     * 라이센스가 부족 : error 1083
     */
    public static final int LACK_FOR_LICENSE = 3014;

    /**
     * 도메인에 할당된 사용자 수가 꽉 찼음. : error 478
     */
    public static final int DOMAIN_CURRENT_FULL = 3015;

    /**
     * 같은 아이디가 존재함. : error 724
     */
    public static final int EXIST_USERID = 3016;

    /**
     * 가입 제한 아이디에 속함. : error 1082
     */
    public static final int USERID_IS_LIMIT_ACCOUNT = 3017;

    /**
     * 용량제 그룹에서 개인에게 지급될 용량이 용량제그룹에 남은 용량을 오버하기 때문에 계정생성 못함. : error 926
     */
    public static final int LACK_FOR_PERSONAL_CAPACITY = 3018;

    public static final int EXIST_IP = 3019;

    public static final int NOT_EXIST_BOARD = 3020;

    public static final int NOT_EXIST_CATEGORY = 3021;

    public static final int EXIST_ALIAS = 3022;

    public static final int SAME_WORD = 3023;

    public static final int NO_ADD_TEAM_WEBHARD = 3024;

    public static final int NO_INPUT_ADMIN_ID = 3025;

    public static final int NO_INPUT_ADMIN_PASS = 3026;

    public static final int EXIST_ADMIN_ID = 3027;

    public static final int NO_INPUT_NEW_ADMIN_PASS = 3028;

    public static final int WRONG_ADMIN_PASS = 3029;

    public static final int EXIST_AUTH_NAME = 3030;

    public static final int EXIST_AUTH = 3031;

    /**
     * 삭제 아이디에 속함.
     */
    public static final int USERID_IS_DELETED_ACCOUNT = 3032;

    /**
     * 데이터 리턴 값이 존재하지 않음
     */
    public static final int NOT_EXIST_RETURN_VALUE = 3033;

    /** 이메일 형식에러 */
    public static final int WRONG_EMAIL_FORM = 3034;

    /** 아이디 형식에러 */
    public static final int INCORRECT_USERID = 3035;

    /** 최소 한개의 데이터는 남겨두어야함(전부삭제는 안됨!) */
    public static final int NO_DELETE_ALL = 3036;

    /** 이미 해당 목록 또는 데이터에 존재할 때 */
    public static final int LISTED_ACCOUNT = 3037;

    public static final int SAME_SPAM_CONDITION = 3038;

    /**
     * 실제 아이디가 존재하지 않음
     */
    public static final int NOT_EXIST_USERID = 3039;

    /**
     * 아이디 길이 제한을 넘김(20자)
     */
    public static final int OVER_LENGTH_USERID = 3040;

    /**
     * 설정 값이 현재 유저 수보다 적을 경우
     */
    public static final int LIMIT_LESS_THAN_CURRUSER = 3041;

    /**
     * 올바른 숫자를 입력하지 않았을 경우
     */
    public static final int INCORRECT_INTEGER_VALUE = 3042;

    /**
     * 카테고리에 동일한 항목이 존재할 경우
     */
    public static final int EXIST_ITEM_IN_CATEGORY = 3043;

    /**
     * 이미지 파일이 아닌경우
     */
    public static final int NO_IMAGE_FILE = 3044;

    /** 남은 용량 부족 */
    public static final int LACK_OF_SPACE = 3045;

    public static final int SAME_WHOST = 3046;
}
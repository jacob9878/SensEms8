/**
 *
 */
package com.imoxion.sensems.web.exception;

/**
 * 모든 기능에서 나올수 있는 에러 항목에 대해서 정의 한 Exception 여기에 정의를 하면 모든 Exception 에서 사용이 가능
 *
 * @author : sunggyu
 * @date : 2013. 1. 16.
 * @desc :
 *
 */

public class CommonException extends Exception {

    /**
     * 세션이 종료.
     */
    public static final int EXPIRE_SESSION = 451;

    /**
     * 접근 권한이 없음.
     */
    public static final int NOT_ALLOW = 405;

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /** 필수항목이 누락됨 */
    public static final int NO_REQUIRED_ITEM = 1000;

    /** 이미 존재하는 데이터 */
    public static final int EXIST_DATA = 1001;

    /** 파일을 생성하지 못함 */
    public static final int NO_WRITE_FILE = 1002;

    /** 작업중 오류 */
    public static final int WORK_ERROR = 1003;

    /** 사용자 정보 없음 */
    public static final int NO_USER_INFO = 1008;

    /** 존재하는 이메일주소 */
    public static final int EXIST_EMAIL = 1009;

    /** 선택된 항목이 없습니다. */
    public static final int NO_ITEM_SELECTED = 1010;

    /** 파일이 존재하지 않습니다. */
    public static final int NOT_EXIST_FILE = 1011;

    /** 도메인정보 없음 */
    public static final int NO_DOMAIN_INFO = 1012;

    /** 파일에러 */
    public static final int FILE_ERROR = 1013;

    public static final int EXIST_NAME = 1014;

    /**
     * 데이터를 찾을 수 없음
     */
    public static final int NO_FOUND_DATA = 1015;

    public static final int NO_MOVE_DOWN = 1016;

    public static final int NO_MOVE_UP = 1017;

    /**
     * 정의 되지 않은 에러
     */
    public static final int UNKNOW = -1000;

    /**
     * 사용할 수 없는 도메인
     */
    public static final int UNKNOW_DOMAIN = 1018;

    /**
     * 데이터베이스 쿼리 오류
     */
    public static final int DB_ERROR = 1019;

    /**
     * 날짜 형식 오류
     */
    public static final int DATE_ERROR = 1020;

    /**
     * 시작일과 종료일이 같은 오류
     */
    public static final int SAME_DATE_ERROR = 1021;

    protected int error_code;

    public CommonException(int error_code) {
        this.error_code = error_code;
    }

    /**
     * 에러코드와 에러메세지를 같이 던진다.
     *
     * @param error_code
     * @param message
     */
    public CommonException(int error_code, String message) {
        super(String.valueOf(message));
        this.error_code = error_code;
    }

    public int getErrorCode() {
        return error_code;
    }
}
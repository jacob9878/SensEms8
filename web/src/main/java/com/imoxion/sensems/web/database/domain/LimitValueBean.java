package com.imoxion.sensems.web.database.domain;

import org.apache.ibatis.type.Alias;

import com.imoxion.common.util.ImStringUtil;
import com.imoxion.sensems.web.common.ImbConstant;

/**
 *
 * @author : minideji
 * @date : 2012. 7. 25.
 * @desc : 메일 한계값 설정
 *
 */
@Alias("limitValue")
public class LimitValueBean {

    /**
     * 사용자별 최대 SMTP 시도 회수 (사용자별 최대 SMTP 메일 발송 한계값(클라이언트))
     */
    /*public static final String MAX_SMTP_CONNECT_TRY = "001";

    *//**
     * 사용자별 용량 제한(사용자별 최대 SMTP 메일 발송 한계 총용량값)
     *//*
    public static final String USER_LIMIT_SIZE = "002";

    *//**
     * 사용자 모니터링 시간
     *//*
    public static final String USER_MONITORING_TIME = "003";*/

    /**
     * 대용량 큐로 넘어가는 한계 용량값
     */
    public static final String MASS_QUEUE_SWITCH_LIMIT_VALUE = "004";

    /**
     * 대용량 큐로 넘어가는 From 한계 메일 수
     */
    public static final String MASS_QUEUE_SWITCH_FROM_LIMIT_COUNT_VALUE = "005";

    /**
     * 대용량 큐로 넘어가는 From 한계 총 용량
     */
    public static final String MASS_QUEUE_SWITCH_FROM_LIMIT_SIZE_VALUE = "006";

    /**
     * 큐 한계 값
     */
    /*public static final String QUEUE_LIMIT_VALUE = "007";

    *//**
     * 문자를 받을 관리자 휴대폰 번호
     *//*
    public static final String SMS_RECEIVER_NUMBER = "008";

    *//**
     * 문자 회신 번호
     *//*
    public static final String SMS_SENDER_NUMBER = "009";

    *//**
     * 사용자 용량 초과 알람 SMS 문자
     *//*
    public static final String USER_SIZEOVER_ALARM_SMS_MESSAGE = "010";

    *//**
     * 큐 허용 한계 값 초과 알람 문자열
     *//*
    public static final String QUEUE_LIMIT_VALUE_OVER_SMS_MESSAGE = "011";*/

    /**
     * 1회 동시 메일 발송 최대 건수(1회 발송시 최대 수신자 수)
     */
    public static final String MAX_RECEIVER_COUNT = "012";
/*
    *//**
     * 관리자 이메일 주소
     *//*
    public static final String MANAGER_EMAIL = "013";

    *//**
     * 사용자별 하루 최대 SMTP 메일 발송 한계값(클라이언트)
     *//*
    public static final String MAX_SMTP_CONNECT_TRY_PER_DAY = "014";

    *//**
     * 사용자 메일박스 용량 경고 기준값(%)
     *//*
    public static final String WARNING_MBOX_FULLED = "015";*/

    /**
     * SMTP 발송 및 수신 최대 메일 크기(MB)
     */
    public static final String SMTP_MAX_MAIL_SIZE = "016";

    /**
     * SMTP 동보메일시 최대 메일 크기(수신자 * 메일크기, MB)
     */
    public static final String SMTP_SAME_TIME_MAX_MAIL_SIZE = "017";

    public static final int LIMIT_TYPE_COLUMN_LENGTH = 3;

    public static final int LIMIT_VALUE_COLUMN_LENGTH = 255;

    public static final int DESCRIPT_COLUMN_LENGTH = 255;

    /** 제한종류(001: 회수제한, 002: 용량 제한 등) */
    private String limit_type;

    /** 제한 값 */
    private String limit_value;

    /** 설명 */
    private String descript;

    public String getLimit_type() {
        return limit_type;
    }

    public void setLimit_type(String limitType) {
        this.limit_type = ImStringUtil.stringCutterByte(limitType, LIMIT_TYPE_COLUMN_LENGTH);
    }

    public String getLimit_value() {
        return limit_value;
    }

    public void setLimit_value(String limitValue) {
        this.limit_value = ImStringUtil.stringCutterByte(limitValue, LIMIT_VALUE_COLUMN_LENGTH);
    }

    public String getDescript() {
        return descript;
    }

    public void setDescript(String descript) {
        this.descript = ImStringUtil.stringCutterByte(descript, DESCRIPT_COLUMN_LENGTH);
    }

    /**
     * Constructs a <code>String</code> with all attributes in name = value
     * format.
     *
     * @return a <code>String</code> representation of this object.
     */
    public String toString() {
        final String TAB = "\n";

        StringBuffer retValue = new StringBuffer();

        retValue.append("LimitValueBean ( ").append(super.toString()).append(TAB).append("limit_type = ").append(this.limit_type).append(TAB)
                .append("limit_value = ").append(this.limit_value).append(TAB).append("descript = ").append(this.descript).append(TAB).append(" )");

        return retValue.toString();
    }

}

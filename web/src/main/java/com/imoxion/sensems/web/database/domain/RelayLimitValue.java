package com.imoxion.sensems.web.database.domain;

public class RelayLimitValue {

    /**
     * SMTP 발송 및 수신 최대 메일 크기(MB)
     */
    public static final String SMTP_MAX_MAIL_SIZE = "016";

    /**
     * 1회 동시 메일 발송 최대 건수(1회 발송시 최대 수신자 수)
     */
    public static final String MAX_RECEIVER_COUNT = "012";


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
     * 1회 발송 시 최대 총 메일 크기(수신자수 * 메일크기, MB)
     */
    public static final String SMTP_SAME_TIME_MAX_MAIL_SIZE = "017";

    /** 제한종류(001: 회수제한, 002: 용량 제한 등) */
    private String limit_type;

    /** 제한 값 */
    private String limit_value;

    /** 설명 */
    private String descript;


    public String getLimit_type() {
        return limit_type;
    }

    public void setLimit_type(String limit_type) {
        this.limit_type = limit_type;
    }

    public String getLimit_value() {
        return limit_value;
    }

    public void setLimit_value(String limit_value) {
        this.limit_value = limit_value;
    }

    public String getDescript() {
        return descript;
    }

    public void setDescript(String descript) {
        this.descript = descript;
    }
}

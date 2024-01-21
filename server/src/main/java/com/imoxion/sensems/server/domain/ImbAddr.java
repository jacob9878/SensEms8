package com.imoxion.sensems.server.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * @author : jhpark
 * @date : 2021. 2. 5.
 */
@Setter
@Getter
@ToString
public class ImbAddr {

    /**
     * 주소록 키
     * Auto_Increment
     */
    private int ukey;

    /**
     * 그룹 키
     */
    private int gkey;

    /**
     * 이름
     */
    private String name;

    /**
     * 이메일
     */
    private String email;

    /**
     * 회사
     */
    private String company;

    /**
     * 부서
     */
    private String dept;

    /**
     * 직책
     */
    private String grade;

    /**
     * 집 전화번호
     */
    private String home_tel;

    /**
     * 회사 전화번호
     */
    private String office_tel;

    /**
     * 휴대폰번호
     */
    private String mobile;

    /**
     * Fax 번호
     */
    private String fax;

    /**
     * 우편번호
     */
    private String zipcode;

    /**
     * 주소 1, 주소2
     */
    private String addr1;
    private String addr2;

    /**
     * 기타정보 1 ~ 5
     */
    private String etc1;
    private String etc2;
    private String etc3;
    private String etc4;
    private String etc5;

    /**
     * 등록 날짜
     */
    private Date regdate;

}

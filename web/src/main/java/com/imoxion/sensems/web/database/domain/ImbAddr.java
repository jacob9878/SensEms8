package com.imoxion.sensems.web.database.domain;

import org.apache.ibatis.type.Alias;

import java.util.Date;

/**
 * @author : jhpark
 * @date : 2021. 2. 5.
 */
@Alias("addr")
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
//    private String home_tel;

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
//    private String fax;

    /**
     * 우편번호
     */
//    private String zipcode;

    /**
     * 주소 1, 주소2
     */
//    private String addr1;
//    private String addr2;

    /**
     * 기타정보 1 ~ 5
     */
    private String etc1;
    private String etc2;
//    private String etc3;
//    private String etc4;
//    private String etc5;

    /**
     * 등록 날짜
     */
    private Date regdate;

    public int getUkey() {
        return ukey;
    }

    public void setUkey(int ukey) {
        this.ukey = ukey;
    }

    public int getGkey() {
        return gkey;
    }

    public void setGkey(int gkey) {
        this.gkey = gkey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }
//
//    public String getHome_tel() {
//        return home_tel;
//    }
//
//    public void setHome_tel(String home_tel) {
//        this.home_tel = home_tel;
//    }

    public String getOffice_tel() {
        return office_tel;
    }

    public void setOffice_tel(String office_tel) {
        this.office_tel = office_tel;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

//    public String getFax() {
//        return fax;
//    }
//
//    public void setFax(String fax) {
//        this.fax = fax;
//    }
//
//    public String getZipcode() {
//        return zipcode;
//    }
//
//    public void setZipcode(String zipcode) {
//        this.zipcode = zipcode;
//    }
//
//    public String getAddr1() {
//        return addr1;
//    }
//
//    public void setAddr1(String addr1) {
//        this.addr1 = addr1;
//    }
//
//    public String getAddr2() {
//        return addr2;
//    }
//
//    public void setAddr2(String addr2) {
//        this.addr2 = addr2;
//    }

    public String getEtc1() {
        return etc1;
    }

    public void setEtc1(String etc1) {
        this.etc1 = etc1;
    }

    public String getEtc2() {
        return etc2;
    }

    public void setEtc2(String etc2) {
        this.etc2 = etc2;
    }

//    public String getEtc3() {
//        return etc3;
//    }
//
//    public void setEtc3(String etc3) {
//        this.etc3 = etc3;
//    }
//
//    public String getEtc4() {
//        return etc4;
//    }
//
//    public void setEtc4(String etc4) {
//        this.etc4 = etc4;
//    }
//
//    public String getEtc5() {
//        return etc5;
//    }
//
//    public void setEtc5(String etc5) {
//        this.etc5 = etc5;
//    }

    public Date getRegdate() {
        return regdate;
    }

    public void setRegdate(Date regdate) {
        this.regdate = regdate;
    }

    @Override
    public String toString() {
        return "ImbAddr{" +
                "ukey=" + ukey +
                ", gkey=" + gkey +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", company='" + company + '\'' +
                ", dept='" + dept + '\'' +
                ", grade='" + grade + '\'' +
//                ", home_tel='" + home_tel + '\'' +
                ", office_tel='" + office_tel + '\'' +
                ", mobile='" + mobile + '\'' +
//                ", fax='" + fax + '\'' +
//                ", zipcode='" + zipcode + '\'' +
//                ", addr1='" + addr1 + '\'' +
//                ", addr2='" + addr2 + '\'' +
                ", etc1='" + etc1 + '\'' +
                ", etc2='" + etc2 + '\'' +
//                ", etc3='" + etc3 + '\'' +
//                ", etc4='" + etc4 + '\'' +
//                ", etc5='" + etc5 + '\'' +
                ", regdate=" + regdate +
                '}';
    }
}

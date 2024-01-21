package com.imoxion.sensems.web.form;

import java.util.Date;
import java.util.Map;

/**
 * @date 2021.03.19
 * @author jhpark
 *
 */
public class AddressImportForm {

    private String fileKey;

    private String divMethod;

    private String header;

    /**
     * 고유 키
     */
    private String ukey;
    /**
     * 그룹 키
     */
    private String gkey;

    /**
     * 그룹 명
     */
    private String gname;

    /**
     * 이름
     */
    private String name;

    /**
     * 메일 주소
     */
    private String email;

    /**
     * 회사명
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
     * 휴대폰 번호
     */
    private String mobile;

    /**
     * 팩스 번호
     */
    private String fax;

    /**
     * 우편 번호
     */
    private String zipcode;

    /**
     * 주소 1
     */
    private String addr1;

    /**
     * 주소 2
     */
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
     * 등록일
     */
    private Date regdate;

    private String strColArray; // 파일 읽고 난 뒤, 셀렉트 박스 저장

    private Map<Integer, String> strColumn;

    private int nCol;

    public String getFileKey() {
        return fileKey;
    }

    public void setFileKey(String fileKey) {
        this.fileKey = fileKey;
    }

    public String getDivMethod() {
        return divMethod;
    }

    public void setDivMethod(String divMethod) {
        this.divMethod = divMethod;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getUkey() {
        return ukey;
    }

    public void setUkey(String ukey) {
        this.ukey = ukey;
    }

    public String getGkey() {
        return gkey;
    }

    public void setGkey(String gkey) {
        this.gkey = gkey;
    }

    public String getGname() {
        return gname;
    }

    public void setGname(String gname) {
        this.gname = gname;
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

    public String getHome_tel() {
        return home_tel;
    }

    public void setHome_tel(String home_tel) {
        this.home_tel = home_tel;
    }

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

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getAddr1() {
        return addr1;
    }

    public void setAddr1(String addr1) {
        this.addr1 = addr1;
    }

    public String getAddr2() {
        return addr2;
    }

    public void setAddr2(String addr2) {
        this.addr2 = addr2;
    }

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

    public String getEtc3() {
        return etc3;
    }

    public void setEtc3(String etc3) {
        this.etc3 = etc3;
    }

    public String getEtc4() {
        return etc4;
    }

    public void setEtc4(String etc4) {
        this.etc4 = etc4;
    }

    public String getEtc5() {
        return etc5;
    }

    public void setEtc5(String etc5) {
        this.etc5 = etc5;
    }

    public Date getRegdate() {
        return regdate;
    }

    public void setRegdate(Date regdate) {
        this.regdate = regdate;
    }

    public String getStrColArray() {
        return strColArray;
    }

    public void setStrColArray(String strColArray) {
        this.strColArray = strColArray;
    }

    public Map<Integer, String> getStrColumn() {
        return strColumn;
    }

    public void setStrColumn(Map<Integer, String> strColumn) {
        this.strColumn = strColumn;
    }

    public int getNCol() {
        return nCol;
    }

    public void setNCol(int nCol) {
        this.nCol = nCol;
    }
}

package com.imoxion.sensems.web.form;

import java.util.Date;
import java.util.Map;

/**
 * @date 2021.03.19
 * @author jhpark
 *
 */
public class RejectImportForm {

    private String fileKey;

    private String divMethod;

    private String header;


    /**
     * 메일 주소
     */
    private String email;



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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

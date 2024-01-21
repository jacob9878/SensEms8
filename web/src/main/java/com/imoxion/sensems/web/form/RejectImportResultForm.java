package com.imoxion.sensems.web.form;

import com.imoxion.sensems.web.database.domain.ImbAddr;
import com.imoxion.sensems.web.database.domain.ImbReject;

import java.util.List;

/**
 * 주소록 IMPORT 결과
 * @author jhpark
 * Created by Administrator on 2021-03-19.
 */
public class RejectImportResultForm {

    /**
     * 추가 시도한 수
     */
    private int importCount;

    /**
     * 성공한 수
     */
    private int successCount;

    /**
     * 헤더로 선택된 컬럼개수와 실제 데이터 개수 불일치 개수
     */
    private int columnCountNotMatch;
    /**
     * 이메일 누락,형식,중복 오류
     */
    private List<ImbReject> emailRejectErrorList;

    public int getImportCount() {
        return importCount;
    }

    public void setImportCount(int importCount) {
        this.importCount = importCount;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public int getColumnCountNotMatch() {
        return columnCountNotMatch;
    }

    public void setColumnCountNotMatch(int columnCountNotMatch) {
        this.columnCountNotMatch = columnCountNotMatch;
    }


    public List<ImbReject> getEmailAddressErrorList() {
        return emailRejectErrorList;
    }

    public void setEmailAddressErrorList(List<ImbReject> emailRejectErrorList) {
        this.emailRejectErrorList = emailRejectErrorList;
    }

}

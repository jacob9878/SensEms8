package com.imoxion.sensems.web.form;

/**
 * yeji
 * 2021. 03. 10
 * view 와 주고받는 첨부파일 확장자 관리 폼데이터
 */
public class AttachRestrictForm {

    private String restrict_ext = ""; // 첨부파일 제한 확장자

    public String getRestrict_ext() {
        return restrict_ext;
    }

    public void setRestrict_ext(String restrict_ext) {
        this.restrict_ext = restrict_ext;
    }

}


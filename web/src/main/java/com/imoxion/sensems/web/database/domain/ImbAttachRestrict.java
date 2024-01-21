package com.imoxion.sensems.web.database.domain;

import org.apache.ibatis.type.Alias;

/**
 * yeji
 * 2021. 03. 10
 * 첨부파일 확장자 관리 정보 담을 bean 객체
 */
@Alias("extInfo")
public class ImbAttachRestrict {

    private String ext ;

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }
}

package com.imoxion.sensems.web.database.domain;

import org.apache.ibatis.type.Alias;

import java.util.Date;

@Alias("templateInfo")
public class ImbTemplate {

    /** 고유키 */
    private String ukey;

    /** 유저아이디 */
    private String userid;

    /** 제목 */
    private String temp_name;

    /** 등록날짜 */
    private Date regdate;

    /** 내용 */
    private String contents;

    /** 이미지 경로 */
    private String image_path;

    /** 공용 여부
     *  01 : 공용
     *  02 : 개인
     */
    private String flag;

    /** 기타 */
    private String extended;

    /**
     * 공용 템플릿
     */
    public static final String PUBLIC_IMAGE = "01";
    /**
     * 개인 템플릿
     */
    public static final String PERSONAL_IMAGE = "02";

    public static final String UPLOAD_IMAGE_VIEW_URL = "/send/template/uploadImage/view.do";

    public static final String CONTENTS_IMAGE_VIEW_URL = "/send/template/contentsImage/view.do";

    public String getUkey() {
        return ukey;
    }

    public void setUkey(String ukey) {
        this.ukey = ukey;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getTemp_name() {
        return temp_name;
    }

    public void setTemp_name(String temp_name) {
        this.temp_name = temp_name;
    }

    public Date getRegdate() {
        return regdate;
    }

    public void setRegdate(Date regdate) {
        this.regdate = regdate;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getExtended() {
        return extended;
    }

    public void setExtended(String extended) {
        this.extended = extended;
    }
}

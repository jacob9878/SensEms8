package com.imoxion.sensems.web.database.domain;

import org.apache.ibatis.type.Alias;

import java.util.Date;

@Alias("imageInfo")
public class ImbImage {

    /** 고유키 */
    private String ukey;

    /** 유저아이디 */
    private String userid;

    /** 제목 */
    private String image_name;

    /** 가로길이(px) */
    private int image_width;

    /** 세로길이(px) */
    private int image_height;

    /** 이미지 경로 */
    private String image_path;

    /** 등록날짜 */
    private Date regdate;

    /** 공용 여부
     *  01 : 공용
     *  02 : 개인
     */
    private String flag;

    /**
     * 공용 이미지
     */
    public static final String PUBLIC_IMAGE = "01";
    /**
     * 개인이미지
     */
    public static final String PERSONAL_IMAGE = "02";

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

    public String getImage_name() {
        return image_name;
    }

    public void setImage_name(String image_name) {
        this.image_name = image_name;
    }

    public int getImage_width() {
        return image_width;
    }

    public void setImage_width(int image_width) {
        this.image_width = image_width;
    }

    public int getImage_height() {
        return image_height;
    }

    public void setImage_height(int image_height) {
        this.image_height = image_height;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public Date getRegdate() {
        return regdate;
    }

    public void setRegdate(Date regdate) {
        this.regdate = regdate;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
}

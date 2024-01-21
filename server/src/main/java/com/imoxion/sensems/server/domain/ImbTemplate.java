package com.imoxion.sensems.server.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.ibatis.type.Alias;

import java.util.Date;

@Setter
@Getter
@ToString
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

}

package com.imoxion.sensems.web.form;

import org.springframework.web.multipart.MultipartFile;

/**
 * @date 2021.02.25
 * @author jhpark
 *
 */
public class TemplateForm {


    /** 공용 여부
     * 01 : 공용
     * 02 : 개인
     */
    private String flag;

    /** 제목 */
    private String temp_name;

    /** 내용 */
    private String content;

    /** 썸네일 이미지 */
    private MultipartFile file_upload;

    /** 수정에서 사용 */

    /** 템플릿 고유 키 */
    private String ukey;

    /** 파일 삭제 여부 **/
    private String[] isDeleteImage;

    /** 수정 화면에서 이미지 존재 확인 여부 */
    private String image_path;

    /** 변경 전 제목*/
    private String ori_name;

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getTemp_name() {
        return temp_name;
    }

    public void setTemp_name(String temp_name) {
        this.temp_name = temp_name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MultipartFile getFile_upload() {
        return file_upload;
    }

    public void setFile_upload(MultipartFile file_upload) {
        this.file_upload = file_upload;
    }

    public String getUkey() {
        return ukey;
    }

    public void setUkey(String ukey) {
        this.ukey = ukey;
    }

    public String[] getIsDeleteImage() {
        String[] safeArray = null;
        if (this.isDeleteImage != null) {
            safeArray = new String[this.isDeleteImage.length];
            for (int i = 0; i < this.isDeleteImage.length; i++) { safeArray[i] = this.isDeleteImage[i]; }
        }
        return safeArray;
    }

    public void setIsDeleteImage(String[] isDeleteImage) {
        this.isDeleteImage = new String[isDeleteImage.length];
        for (int i = 0; i < isDeleteImage.length; ++i)
            this.isDeleteImage[i] = isDeleteImage[i];
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getOri_name() {
        return ori_name;
    }

    public void setOri_name(String ori_name) {
        this.ori_name = ori_name;
    }
}

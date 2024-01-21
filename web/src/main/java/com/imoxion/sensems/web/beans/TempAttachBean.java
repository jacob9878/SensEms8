/*
 * FileName : TempAttachBean.java
 *
 * 작성자 : Administrator
 * 이메일 : Administrator@imoxion.com
 * Company : (주)아이모션
 * 날짜 : 2008. 12. 17
 *
 * 설명:
 * 
 */
package com.imoxion.sensems.web.beans;

public class TempAttachBean {
    private int idx = 0;
    private String campid = null;
    private String file_name = null;
    private String file_size = null;
    
    public int getIdx() {
        return idx;
    }
    public void setIdx(int idx) {
        this.idx = idx;
    }
    public String getCampid() {
        return campid;
    }
    public void setCampid(String campid) {
        if (campid != null)
            this.campid = campid;
    }
    public String getFile_name() {
        return file_name;
    }
    public void setFile_name(String file_name) {
        if (file_name != null)
            this.file_name = file_name;
    }
    public String getFile_size() {
        return file_size;
    }
    public void setFile_size(String file_size) {
        if (file_size != null)
            this.file_size = file_size;
    }
    
    
}

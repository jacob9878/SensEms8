/*
 * FileName : IndividualCategoryBean.java
 *
 * 작성자 : Administrator
 * 이메일 : Administrator@imoxion.com
 * Company : (주)아이모션
 * 날짜 : 2007. 02. 21
 *
 * 설명:
 * 
 */
package com.imoxion.sensems.web.beans;

public class IndividualCategoryBean {
    private int id = 0;
    private String category = "";
    
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        if (category != null)
            this.category = category;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    
    
}

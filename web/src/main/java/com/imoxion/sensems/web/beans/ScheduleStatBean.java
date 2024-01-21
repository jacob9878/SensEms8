/*
 * FileName : ScheduleStatBean.java
 *
 * 작성자 : realkoy
 * 이메일 : realkoy@imoxion.com
 * Company : (주)아이모션
 * 날짜 : 2006. 4. 27.
 *
 * 설명:
 * 
 */
package com.imoxion.sensems.web.beans;

import java.util.Locale;
import java.util.ResourceBundle;

public class ScheduleStatBean {
    private String m_sType = "";
    private String m_sTypeStr = "";
    private String m_sSubject = "";
    private String m_sYear = "";
    private String m_sMonth = "";
    private String m_sDay = "";
    private String m_sUkey = "";
    

    public String getType(){
        return m_sType;
    }
    public String getTypeStr(){
        return m_sTypeStr;
    }
    public String getSubject(){
        return m_sSubject;
    }
    public String getYear(){
        return m_sYear;
    }
    public String getMonth(){
        return m_sMonth;
    }
    public String getDay(){
        return m_sDay;
    }
    public String getUkey(){
        return m_sUkey;
    }
    
    public void setType(String p_sType , String locale ){
    	ResourceBundle bundle = ResourceBundle.getBundle("resources.schedule" , new Locale( locale ) );
        m_sType = p_sType;
        if(m_sType.equals("1")){
            m_sTypeStr = bundle.getString("53");
        } else if(m_sType.equals("2")){
            m_sTypeStr = bundle.getString("54");
        } else if(m_sType.equals("3")){
            m_sTypeStr = bundle.getString("55");
        } else if(m_sType.equals("0")){
            m_sTypeStr = bundle.getString("52");
        }
    }
    public void setSubject(String p_sSubject){
        m_sSubject = p_sSubject;
    }
    public void setYear(String p_sYear){
        m_sYear = p_sYear;
    }
    public void setYear(int p_nYear){
        m_sYear = String.valueOf(p_nYear);
    }
    public void setMonth(String p_sMonth){
        m_sMonth = p_sMonth;
    }
    public void setMonth(int p_nMonth){
        m_sMonth = String.valueOf(p_nMonth);
    }
    public void setDay(String p_sDay){
        m_sDay = p_sDay;
    }
    public void setDay(int p_nDay){
        m_sDay = String.valueOf(p_nDay);
    }
    public void setUkey(String p_sUkey){
        m_sUkey = p_sUkey;
    }
}

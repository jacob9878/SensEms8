/*
 * FileName : ScheduleWeekBean.java
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

import java.util.ArrayList;

public class ScheduleWeekDayBean {
    private String m_sYear = "";
    private String m_sMonth = "";
    private String m_sDay = "";
    private String m_sHour = "";
    private String m_sMin = "";
    private String m_sWeek = "";
    private ScheduleStatBean[] m_oSSB = null;
    private ArrayList m_arrList = new ArrayList();
    
    public String getYear(){
        return m_sYear;
    }
    public String getMonth(){
        return m_sMonth;
    }
    public String getDay(){
        return m_sDay;
    }
    public String getHour(){
        return m_sHour;
    }
    public String getMin(){
        return m_sMin;
    }
    public String getWeek(){
        return m_sWeek;
    }
    public ScheduleStatBean[] getScheduleStatBeans(){
        ScheduleStatBean[] safeArray = null;
        if (this.m_oSSB != null) {
            safeArray = new ScheduleStatBean[this.m_oSSB.length];
            for (int i = 0; i < this.m_oSSB.length;i++) {
                safeArray[i] = this.m_oSSB[i];
            }
        }
        return safeArray;
    }
    public ArrayList getArrList(){
        return m_arrList;
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
    
    public void setHour(int p_nHour){
        m_sHour = String.valueOf(p_nHour);
    }
    public void setHour(String p_sHour){
        m_sHour = p_sHour;
    }
    
    public void setMin(int p_nMin){
        m_sMin = String.valueOf(p_nMin);
    }
    public void setMin(String p_sMin){
        m_sMin = p_sMin;
    }
    
    
    public void setWeek(int nWeek){
        switch (nWeek) {
        case 1:
            m_sWeek = "Sun";
            break;
        case 2:
            m_sWeek = "Mon";
            break;
        case 3:
            m_sWeek = "Tue";
            break;
        case 4:
            m_sWeek = "Wed";
            break;
        case 5:
            m_sWeek = "Thu";
            break;
        case 6:
            m_sWeek = "Fri";
            break;
        case 7:
            m_sWeek = "Sat";
            break;
        }
    }
    public void setScheduleStatBeans(ScheduleStatBean[] p_oSSB){
        this.m_oSSB = new ScheduleStatBean[p_oSSB.length];
        for (int i = 0; i < p_oSSB.length; ++i)
            this.m_oSSB[i] = p_oSSB[i];
    }
    
    public void setArrList(ArrayList p_arrList){
        m_arrList = p_arrList;
    }
}

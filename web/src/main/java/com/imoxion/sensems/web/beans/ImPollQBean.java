/*
 * FileName : PollQBean.java
 *
 * 작성자 : realkoy
 * 이메일 : dev@imoxion.com
 * Company : (주)아이모션
 * 날짜 : 2008. 01. 04
 *
 * 설명:
 * 
 */
package com.imoxion.sensems.web.beans;

import java.util.List;
import java.util.Map;

public class ImPollQBean {
    private String pid = "";
    private int qid = -1;
    private String qtitle = "";
    private String qtype = "";      // -1: 안내 멘트, 0: 라디오, 1: 체크박스, 2: 서술형, 3: 척도분석(아주좋음,좋음,보통,나쁨,아주나쁨)
    private String qsize = "50";     // 서술형답변시 입력창 크기, 기본 50
    private String qcheck = "1";    // 1: 답변 체크 필수, 0: 답변체크 안해도 넘어갈수 있음
    private String aoption = "";    // 라디오나 체크박스의 경우 답변 나열방식  v: 세로, h: 가로, h2: 가로 2, h3:가로3 ...
    
    private List arrAnsBean = null;
    private Map mapAnsCount = null;
    private int ans_count = 0;
    
    public String getPid() {
        return pid;
    }
    public void setPid(String pid) {
        if (pid != null)
            this.pid = pid;
    }
    public int getQid() {
        return qid;
    }
    public void setQid(int qid) {
        this.qid = qid;
    }
    public String getQtitle() {
        return qtitle;
    }
    public void setQtitle(String qtitle) {
        if (qtitle != null)
            this.qtitle = qtitle;
    }
    public String getQtype() {
        return qtype;
    }
    public void setQtype(String qtype) {
        if (qtype != null)
            this.qtype = qtype;
    }
    public String getQcheck() {
        return qcheck;
    }
    public void setQcheck(String qcheck) {
        if (qcheck != null)
            this.qcheck = qcheck;
    }
    public String getAoption() {
        return aoption;
    }
    public void setAoption(String aoption) {
        if (aoption != null)
            this.aoption = aoption;
    }
    public List getArrAnsBean() {
        return arrAnsBean;
    }
    public void setArrAnsBean(List arrAnsBean) {
        if (arrAnsBean != null)
            this.arrAnsBean = arrAnsBean;
    }
    public String getQsize() {
        return qsize;
    }
    public void setQsize(String qsize) {
        if (qsize != null)
            this.qsize = qsize;
    }
    public int getAns_count() {
        return ans_count;
    }
    public void setAns_count(int ans_count) {
        this.ans_count = ans_count;
    }
    public Map getMapAnsCount() {
        return mapAnsCount;
    }
    public void setMapAnsCount(Map mapAnsCount) {
        if (mapAnsCount != null)
            this.mapAnsCount = mapAnsCount;
    }
    
    
}

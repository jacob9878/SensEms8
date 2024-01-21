/*
 * FileName : PollABean.java
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

public class ImPollABean {
    private String pid = "";
    private int qid = 1;
    private int aid = 1;
    private String answer = "";
    private String atype = "0";     // 기본:0, 선택형질문의 기타답변:1, 서술형질문: 2
    private String asize = "30";     // 기타답변시 입력창 크기, 기본 30
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
    public int getAns_count() {
        return ans_count;
    }
    public void setAns_count(int ans_count) {
        this.ans_count = ans_count;
    }
    public int getAid() {
        return aid;
    }
    public void setAid(int aid) {
        this.aid = aid;
    }
    public String getAnswer() {
        return answer;
    }
    public void setAnswer(String answer) {
        if (answer != null)
            this.answer = answer;
    }
    public String getAtype() {
        return atype;
    }
    public void setAtype(String atype) {
        if (atype != null)
            this.atype = atype;
    }
    public String getAsize() {
        return asize;
    }
    public void setAsize(String asize) {
        if (asize != null)
            this.asize = asize;
    }
    
    
}

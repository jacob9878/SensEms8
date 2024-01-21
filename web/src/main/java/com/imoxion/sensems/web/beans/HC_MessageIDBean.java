package com.imoxion.sensems.web.beans;

public class HC_MessageIDBean {
    /** 도메인 */
    private String hostname;

    /** 발송건수 */
    private int scount;

    /** 실패건수 */
    private int ecount;

    /** 실패비율(%) */
    private int eration;

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getScount() {
        return scount;
    }

    public void setScount(int scount) {
        this.scount = scount;
    }

    public int getEcount() {
        return ecount;
    }

    public void setEcount(int ecount) {
        this.ecount = ecount;
    }

    public int getEration() {
        return eration;
    }

    public void setEration(int eration) {
        this.eration = eration;
    }
}

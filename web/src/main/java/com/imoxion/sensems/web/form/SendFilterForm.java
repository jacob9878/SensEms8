package com.imoxion.sensems.web.form;

public class SendFilterForm {

    private String hostname; // 발송 차단할 도메인명

    /*페이징*/
    private String cpage = "1";

    private String pagegroupsize = "5";

    /*검색*/
    private String srch_keyword;

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getCpage() {
        return cpage;
    }

    public void setCpage(String cpage) {
        this.cpage = cpage;
    }

    public String getPagegroupsize() {
        return pagegroupsize;
    }

    public void setPagegroupsize(String pagegroupsize) {
        this.pagegroupsize = pagegroupsize;
    }

    public String getSrch_keyword() {
        return srch_keyword;
    }

    public void setSrch_keyword(String srch_keyword) {
        this.srch_keyword = srch_keyword;
    }
}

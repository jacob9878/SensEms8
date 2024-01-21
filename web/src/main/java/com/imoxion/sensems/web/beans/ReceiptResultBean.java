package com.imoxion.sensems.web.beans;

import com.imoxion.sensems.web.common.ImPage;

import java.util.List;

public class ReceiptResultBean {
    private ImPage pageInfo;

    private List<ReceiptBean> resultlist;
    private String msg_name;
    private String mailfrom;

    public String getMsg_name() {
        return msg_name;
    }

    public void setMsg_name(String msg_name) {
        this.msg_name = msg_name;
    }

    public ImPage getPageInfo() {
        return pageInfo;
    }

    public List<ReceiptBean> getResultlist() {
        return resultlist;
    }

    public void setResultlist(List<ReceiptBean> resultlist) {
        this.resultlist = resultlist;
    }

    public void setPageInfo(ImPage pageInfo) {
        this.pageInfo = pageInfo;
    }

    public String getMailfrom() {return mailfrom;}

    public void setMailfrom(String mailfrom) {this.mailfrom = mailfrom;}

}

package com.imoxion.sensems.web.beans;

import com.imoxion.sensems.web.common.ImPage;
import com.imoxion.sensems.web.database.domain.ImbActionLog;

import java.util.List;

public class ActionLogResultBean {

    private ImPage pageInfo;
    private List<ImbActionLog> resultList;

    public ImPage getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(ImPage pageInfo) {
        this.pageInfo = pageInfo;
    }

    public List<ImbActionLog> getResultList() {
        return resultList;
    }

    public void setResultList(List<ImbActionLog> resultList) {
        this.resultList = resultList;
    }
}

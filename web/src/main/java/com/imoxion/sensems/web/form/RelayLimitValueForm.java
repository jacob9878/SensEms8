package com.imoxion.sensems.web.form;

import com.imoxion.sensems.web.database.domain.RelayLimitValue;

import java.util.List;
import java.util.Map;

public class RelayLimitValueForm {

    private String limit_type = null;

    private String descript = null;

    private String limit_value = null;

    private List<RelayLimitValue> limitValueList = null;

    private Map<String, String> discriptMap = null;

    public String getLimit_type() {
        return limit_type;
    }

    public void setLimit_type(String limit_type) {
        this.limit_type = limit_type;
    }

    public String getDescript() {
        return descript;
    }

    public void setDescript(String descript) {
        this.descript = descript;
    }

    public String getLimit_value() {
        return limit_value;
    }

    public void setLimit_value(String limit_value) {
        this.limit_value = limit_value;
    }

    public List<RelayLimitValue> getLimitValueList() {
        return limitValueList;
    }

    public void setLimitValueList(List<RelayLimitValue> limitValueList) {
        this.limitValueList = limitValueList;
    }

    public Map<String, String> getDiscriptMap() {
        return discriptMap;
    }

    public void setDiscriptMap(Map<String, String> discriptMap) {
        this.discriptMap = discriptMap;
    }
}

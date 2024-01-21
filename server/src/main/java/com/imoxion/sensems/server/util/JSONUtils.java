package com.imoxion.sensems.server.util;

import net.sf.json.JsonConfig;
import net.sf.json.util.PropertyFilter;

public class JSONUtils {

    /**
     * 값이 NULL 인 항목을 JSONString 에서 제외하도록 하는 JsonConfig 제공.
     * @return
     */
    public static JsonConfig getNullSkipJsonConfig(){
        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.setJsonPropertyFilter(new PropertyFilter() {
            @Override
            public boolean apply(Object source, String name, Object value) {
                return (value == null);
            }
        });
        return jsonConfig;
    }
}

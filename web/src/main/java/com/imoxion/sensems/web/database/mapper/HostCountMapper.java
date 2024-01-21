package com.imoxion.sensems.web.database.mapper;

import org.apache.ibatis.annotations.Param;
import org.mybatis.spring.annotation.MapperScan;

@MapperScan
public interface HostCountMapper {

    /**
     * hostcount 테이블을 생성한다. hc_msgid
     * @param msgid
     * */
    public void createHostCountTable(@Param("msgid") String msgid) throws Exception;
}

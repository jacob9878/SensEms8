package com.imoxion.sensems.server.repository.mapper;

import org.apache.ibatis.annotations.Param;

public interface MsgidHostCountMapper {

    void insertHostCount(@Param("tableName") String tableName, @Param("hostname") String hostname,
                         @Param("scount") int scount, @Param("ecount") int ecount, @Param("eration") int eration);

    void dropHostCount(@Param("tableName") String tableName);

    void createHostCount(@Param("msgid") String msgid);
}

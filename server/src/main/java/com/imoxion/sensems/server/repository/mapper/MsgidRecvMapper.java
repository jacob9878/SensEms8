package com.imoxion.sensems.server.repository.mapper;

import com.imoxion.sensems.server.domain.ImbDomainCount;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface MsgidRecvMapper {
    int getMaxRecvCount(@Param("tableName") String tableName, @Param("msgid") String msgid);
    int getRecvSendingCount(@Param("tableName") String tableName);
    List<ImbDomainCount> getSendCountByDomain(@Param("tableName") String tableName);
    List<Map<String, Object>> getErrorCountByErrorcode(@Param("tableName") String tableName);
    List<Map<String, Object>> getRecvTransfer(@Param("tableName") String tableName);
    void createMsgidRecvTable(@Param("extractRecvQuery") String extractRecvQuery);

    void dropRecvTable(@Param("tableName") String tableName);

    List<String> getResendTarget(@Param("msgid") String msgid);
}

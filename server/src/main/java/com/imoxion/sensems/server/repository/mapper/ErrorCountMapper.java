package com.imoxion.sensems.server.repository.mapper;

import com.imoxion.sensems.server.domain.ImbErrorCount;
import org.apache.ibatis.annotations.Param;

public interface ErrorCountMapper {
    void insertErrorCountInit(@Param("msgid") String msgid);

    void delete(@Param("msgid") String msgid);

    void updateErrorCount(ImbErrorCount errorCount);

    void updateBasicErrorCount(@Param("nEmailAddr") int nEmailAddr, @Param("nReject") int nReject,
                               @Param("nRepeat") int nRepeat, @Param("nDomain") int nDomain, @Param("nBlank") int nBlank,
                               @Param("msgid") String msgid);
}

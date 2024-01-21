package com.imoxion.sensems.server.repository.mapper;

import com.imoxion.sensems.server.domain.ImbLinkInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LinkMapper {

    void createLinkLogTable(@Param("msgid") String msgid) throws Exception;
    void insertLinkInfo(@Param("linkInfo") ImbLinkInfo linkInfo) throws Exception;

    void insertLinkCountInfo(@Param("msgid") String msgid, @Param("linkid") int linkid, @Param("link_count") int link_count) throws Exception;

    void deleteLinkCount(@Param("String") String msgid);

    void deleteLinkInfo(@Param("msgid") String msgid);

    void dropLinkLogTable(@Param("tableName") String tableName);

    List<ImbLinkInfo> getLinkList(@Param("msgid") String msgid);
}

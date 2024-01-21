package com.imoxion.sensems.server.repository.mapper;

import org.apache.ibatis.annotations.Param;

public interface ReceiptCountMapper {

    void deleteReceiptCount(@Param("msgid") String msgid);

    void insertReceiptCount(@Param("msgid") String msgid);
}

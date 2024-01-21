package com.imoxion.sensems.server.repository.mapper;

import com.imoxion.sensems.server.domain.ImbReceiver;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ReceiverMapper {
    List<ImbReceiver> getReceiverList();
    ImbReceiver getReceiverInfo(@Param("ukey") String ukey);
}

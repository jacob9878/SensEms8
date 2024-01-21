package com.imoxion.sensems.server.repository.mapper;

import com.imoxion.sensems.server.domain.ImbAddrSel;
import com.imoxion.sensems.server.domain.ImbDBInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AddressMapper {
    List<ImbAddrSel> getAddrSelInfo(@Param("userid") String userid, @Param("msgid") String msgid);
}

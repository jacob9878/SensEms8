package com.imoxion.sensems.server.repository.mapper;

import com.imoxion.sensems.server.domain.ImbDBInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DatabaseMapper {
    List<ImbDBInfo> getDBList();
    ImbDBInfo getDBInfo(@Param("ukey") String ukey);
}

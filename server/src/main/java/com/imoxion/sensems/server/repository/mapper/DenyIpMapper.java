package com.imoxion.sensems.server.repository.mapper;

import com.imoxion.sensems.server.domain.DenyIp;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DenyIpMapper {
    public List<DenyIp> getDenyIpList();
    public DenyIp getDenyIp(@Param("ip") String ip);
}

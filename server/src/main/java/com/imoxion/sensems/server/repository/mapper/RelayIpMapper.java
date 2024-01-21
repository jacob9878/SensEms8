package com.imoxion.sensems.server.repository.mapper;

import com.imoxion.sensems.server.domain.RelayIp;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RelayIpMapper {
    public List<RelayIp> getRelayIpList();
    public RelayIp getRelayIp(@Param("ip") String ip);
}

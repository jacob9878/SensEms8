package com.imoxion.sensems.server.repository.mapper;

import com.imoxion.sensems.server.domain.Dkim;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DkimMapper {
    public List<Dkim> getDkimList();
    public Dkim getDkim(@Param("domain") String domain);
}

package com.imoxion.sensems.server.repository.mapper;

import org.apache.ibatis.annotations.Param;

public interface ActionLogMapper {
    void deleteLog(@Param("delayDays") int delayDays);
}

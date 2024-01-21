package com.imoxion.sensems.web.database.mapper;

import org.apache.ibatis.annotations.Param;
import org.mybatis.spring.annotation.MapperScan;

import java.util.Date;

@MapperScan
public interface TransmitDataMapper {

    public int updateTransmitDataReaddate(@Param("traceid")String traceid, @Param("serverid")String serverid, @Param("rcptto")String rcptto, @Param("readdate") Date readdate);

    public int updateTransmitCount(@Param("traceid")String traceid, @Param("serverid")String serverid, @Param("rcptto")String rcptto);

}

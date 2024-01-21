package com.imoxion.sensems.web.database.mapper;

import com.imoxion.sensems.web.database.domain.LimitValueBean;
import com.imoxion.sensems.web.database.domain.RelayLimitValue;
import org.apache.ibatis.annotations.Param;
import org.mybatis.spring.annotation.MapperScan;

import java.util.List;

@MapperScan
public interface RelayLimitValueMapper {


    public List<RelayLimitValue> getLimitValueList();

    public RelayLimitValue getLimitValue(@Param("limit_type")String limit_type);

    public int updateLimitValue(@Param("limit")LimitValueBean limit);
}

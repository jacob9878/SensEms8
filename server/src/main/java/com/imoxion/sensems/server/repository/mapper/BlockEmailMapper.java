package com.imoxion.sensems.server.repository.mapper;

import com.imoxion.sensems.server.domain.BlockEmail;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BlockEmailMapper {
    public List<BlockEmail> getBlockEmailList();
    public int getBlockEmailCount(@Param("email") String email, @Param("domain") String domain);
    public int getBlockEmailCountEx(@Param("email") String email, @Param("domain") String domain, @Param("toemail") String toemail);
}

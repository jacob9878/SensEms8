package com.imoxion.sensems.server.repository.mapper;

import com.imoxion.sensems.server.domain.ImbUserinfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserInfoMapper {
    public List<ImbUserinfo> getUserList();
    public ImbUserinfo getUserInfo(@Param("userid") String userid);
}

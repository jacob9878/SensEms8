package com.imoxion.sensems.server.repository.mapper;

import com.imoxion.sensems.server.domain.ImbEmsAttach;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AttachMapper {
    List<ImbEmsAttach> getAttachList(@Param("msgid") String msgid);

    ImbEmsAttach getAttachInfo(@Param("ekey") String ekey, @Param("msgid") String msgid);
    List<ImbEmsAttach> getAttachListExpired(@Param("delayDays") int delayDays);

    void insertAttachInfo(ImbEmsAttach bean) throws Exception;
    void deleteAttach(@Param("ekey") String ekey);

    void deleteAttachByMsgid(@Param("msgid") String msgid);
}

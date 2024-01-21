package com.imoxion.sensems.server.repository.mapper;

import com.imoxion.sensems.server.domain.SmtpTempMain;
import com.imoxion.sensems.server.domain.SmtpTempRcpt;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SmtpTempMainMapper {
    public List<SmtpTempMain> getTempMainList();
    public List<SmtpTempRcpt> getTempRcptList(@Param("mainkey") String mainkey);
    public int deleteTempRcptByIdx(@Param("idx") long idx);
    public int deleteTempRcptByIdxList(List<SmtpTempRcpt> idxList);
    public int deleteTempRcptByMainkey(@Param("mainkey") String mainkey);
    public int deleteTempMainByMainkey(@Param("mainkey") String mainkey);
}

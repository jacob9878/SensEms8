package com.imoxion.sensems.web.database.mapper;

import com.imoxion.sensems.web.database.domain.ImbCategoryInfo;
import com.imoxion.sensems.web.database.domain.ImbReject;
import com.imoxion.sensems.web.database.domain.ImbRelay;
import org.apache.ibatis.annotations.Param;
import org.mybatis.spring.annotation.MapperScan;

import java.util.List;

@MapperScan
public interface RelayMapper {


    public List<ImbRelay> selectAllRelay();


    public int selectRelayCount(@Param("srch_keyword") String srch_keyword);

    public List<ImbRelay> selectRelayList(@Param("srch_keyword") String srch_keyword, @Param("start") int start, @Param("end") int end);

    public int getSearchRelayCount(@Param("srch_type")String srch_type, @Param("srch_keyword")String srch_keyword,
                            @Param("ip")String ip, @Param("memo")String memo) throws Exception;

    public List<ImbRelay> getRelayListForPageing(@Param("srch_type") String srch_type, @Param("srch_keyword") String srch_keyword, @Param("ip") String ip,
                                                 @Param("memo") String memo, @Param("start") int start, @Param("end") int end) throws Exception;


    public ImbRelay selectRelayByKey(@Param("ip") String ip);




    public int insertRelay(ImbRelay imbRelay);



    public int deleteRelayByKey(@Param("ip") String ip);

    public int isExistRelay(@Param("ip") String ip);



    public int selectEditRelay(@Param("ip") String ip);


    /*public int editReject(ImbReject imbReject);*/

    public int editRelay(@Param("ip") String ip, @Param("memo") String memo, @Param("ori_ip") String ori_ip);
}

















package com.imoxion.sensems.web.database.mapper;

import com.imoxion.sensems.web.database.domain.ImbBlock;
import com.imoxion.sensems.web.database.domain.ImbRelay;
import org.apache.ibatis.annotations.Param;
import org.mybatis.spring.annotation.MapperScan;

import java.util.List;

@MapperScan
public interface BlockMapper {


    public List<ImbBlock> selectAllBlock();


    public int selectBlockCount(@Param("srch_keyword") String srch_keyword);

    public List<ImbBlock> selectBlockList(@Param("srch_keyword") String srch_keyword, @Param("start") int start, @Param("end") int end);


    public ImbBlock selectBlockByKey(@Param("ip") String ip);


    public int getSearchBlockCount(@Param("srch_type")String srch_type, @Param("srch_keyword")String srch_keyword,
                                   @Param("ip")String ip, @Param("memo")String memo) throws Exception;

    public List<ImbBlock> getBlockListForPageing(@Param("srch_type") String srch_type, @Param("srch_keyword") String srch_keyword, @Param("ip") String ip,
                                                 @Param("memo") String memo, @Param("start") int start, @Param("end") int end) throws Exception;



    public int insertBlock(ImbBlock imbBlock);



    public int deleteBlockByKey(@Param("ip") String ip);

    public int isExistBlock(@Param("ip") String ip);



    public int selectEditBlock(@Param("ip") String ip);


    /*public int editReject(ImbReject imbReject);*/

    public int editBlock(@Param("ip") String ip, @Param("memo") String memo, @Param("ori_ip") String ori_ip);
}

















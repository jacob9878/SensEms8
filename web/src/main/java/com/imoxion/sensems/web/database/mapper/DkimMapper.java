package com.imoxion.sensems.web.database.mapper;

import com.imoxion.sensems.web.database.domain.ImbDkimInfo;
import com.imoxion.sensems.web.database.domain.ImbRelay;
import org.apache.ibatis.annotations.Param;
import org.mybatis.spring.annotation.MapperScan;

import java.util.List;


/**
 * 
 * @author 
 *
 */
@MapperScan
public interface DkimMapper {
	
	public List<ImbDkimInfo> getDKIMList();
	
	public int insertDKIM(ImbDkimInfo dkimInfo);
	
	public void updateUseDKIM(@Param("domain") String domain, @Param("use_sign") String use_sign);

	public int deleteDkimByKey(@Param("dki") String dki);

	public ImbDkimInfo getDKIM(@Param("domain") String domain);

	public void deleteDKIM(@Param("domain") String domain);

	public int updateDKIM(ImbDkimInfo dkimInfo);

	public int selectDkimCount(@Param("srch_keyword") String srch_keyword);

	public List<ImbDkimInfo> selectDkimList(@Param("srch_keyword") String srch_keyword, @Param("start") int start, @Param("end") int end);
	
}

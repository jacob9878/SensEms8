package com.imoxion.sensems.web.database.mapper;

import java.util.List;

import com.imoxion.sensems.web.beans.CategoryBean;
import org.apache.ibatis.annotations.Param;
import org.mybatis.spring.annotation.MapperScan;

import com.imoxion.sensems.web.database.domain.ImbCategoryInfo;


/**
 * @author zpqdnjs
 * 발송분류 관련 매퍼
 */
@MapperScan
public interface CategoryMapper {
	
	/**
	 * 발송분류 목록 개수를 구한다.(사용자)
	 * 
	 * @param srch_type
	 * @param srch_keyword
	 * @param userid
	 * @throws Exception
     */
	public int getCategoryListCount(@Param("srch_type") String srch_type, @Param("srch_keyword") String srch_keyword, @Param("userid") String userid) throws Exception;
	
	/**
	 * 발송분류 목록 개수를 구한다.(관리자)
	 * 
	 * @param srch_type
	 * @param srch_keyword
	 * @throws Exception
     */
	public int getCategoryListCountByAdmin(@Param("srch_type") String srch_type, @Param("srch_keyword") String srch_keyword) throws Exception;
	
	/**
	 * 발송분류 목록을 구한다. (페이징 O, 사용자)
	 * @param srch_type
	 * @param srch_keyword
	 * @param userid
	 * @param start
	 * @param end
	 * @throws Exception
	 * */
	public List<ImbCategoryInfo> getCategoryListForPageing(@Param("srch_type") String srch_type, @Param("srch_keyword") String srch_keyword, @Param("userid") String userid,
															@Param("start") int start, @Param("end") int end) throws Exception;
	
	/**
	 * 발송분류 목록을 구한다. (페이징 O, 관리자)
	 * @param srch_type
	 * @param srch_keyword
	 * @param start
	 * @param end
	 * @throws Exception
	 * */
	public List<ImbCategoryInfo> getCategoryListForPageingByAdmin(@Param("srch_type") String srch_type, @Param("srch_keyword") String srch_keyword, 
															@Param("start") int start, @Param("end") int end) throws Exception;
	
	/**
	 * 발송분류 추가
	 * @param category
	 * @throws Exception
	 * */
	public void addCategory(ImbCategoryInfo category) throws Exception;
	
	
	/**
	 * 카테고리 명으로 발송분류 목록 여부 확인
	 * @param name
	 * @throws Exception
	 * */
	public int checkExistCategory(@Param("userid") String userid, @Param("name") String name) throws Exception;
	
	
	/**
	 * 발송분류 수정
	 * @param name
	 * @param ukey
	 * @throws Exception
	 * */
	public void editCategory(@Param("name") String name, @Param("ukey") String ukey) throws Exception;



	public String checkDuplicateCategory(@Param("userid") String userid, @Param("ukey") String ukey) throws Exception;


	/**
	 * 발송분류 삭제
	 * @param ukey
	 * @throws Exception
	 * */
	public void deleteCategory(@Param("ukey") String ukey) throws Exception;

	/**
	 * 사용자 아이디를 통해서 해당되는 카테고리 목력을 구한다.
	 * @param userid
	 * @return
	 */
	public List<ImbCategoryInfo> getUserCategory(@Param("userid") String userid);

	/**
	 * 카테고리ID를 통해서 해당되는 카테고리를 구한다.
	 * @param categoryid
	 * @return
	 */
    CategoryBean getCategoryForgetCategoryId(@Param("categoryid") String categoryid);


}
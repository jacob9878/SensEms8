package com.imoxion.sensems.web.service;


import java.util.Date;
import java.util.List;

import com.imoxion.sensems.web.beans.CategoryBean;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.imoxion.common.util.ImUtils;
import com.imoxion.sensems.web.database.domain.ImbCategoryInfo;
import com.imoxion.sensems.web.database.mapper.CategoryMapper;


/**
 * @author zpqdnjs
 * 발송분류 관련 서비스
 */

@Service
public class CategoryService {
	@Autowired
	private CategoryMapper categoryMapper;
	
	
	/**
	 * 발송분류 목록 개수를 구한다 (사용자)
	 * 
	 * @param srch_type
	 * @param srch_keyword
	 * @param userid
	 * @throws Exception
	 * */
	public int getCategoryListCount(String srch_type, String srch_keyword, String userid) throws Exception{
		return categoryMapper.getCategoryListCount(srch_type, srch_keyword, userid);
	}
	
	/**
	 * 발송분류 목록 개수를 구한다 (관리자)
	 * 
	 * @param srch_type
	 * @param srch_keyword
	 * @param userid
	 * @throws Exception
	 * */
	public int getCategoryListCountByAdmin(String srch_type, String srch_keyword) throws Exception{
		return categoryMapper.getCategoryListCountByAdmin(srch_type, srch_keyword);
	}
		
	/**
	 * 발송분류 목록을 구한다(페이징 O, 사용자)
	 * 
	 * @param srch_type
	 * @param srch_keyword
	 * @param start
	 * @param end
	 * @throws Exception
	 * */
	public List<ImbCategoryInfo> getCategoryListForPageing(String srch_type, String srch_keyword, String userid, int start, int end) throws Exception{
		return categoryMapper.getCategoryListForPageing(srch_type, srch_keyword, userid, start, end);
	}
	
	
	/**
	 * 발송분류 목록을 구한다(페이징 O, 관리자)
	 * 
	 * @param srch_type
	 * @param srch_keyword
	 * @param start
	 * @param end
	 * @throws Exception
	 * */
	public List<ImbCategoryInfo> getCategoryListForPageingByAdmin(String srch_type, String srch_keyword, int start, int end) throws Exception{
		return categoryMapper.getCategoryListForPageingByAdmin(srch_type, srch_keyword, start, end);
	}
	
	/**
	 * 카테고리 명으로 발송분류 목록이 있는지 체크
	 * 
	 * @param name
	 * @throws Exception
	 * */
	public boolean checkExistCategory(String userid, String name) throws Exception{
		boolean isExist = false;
		int count = categoryMapper.checkExistCategory(userid, name);
		
		if(count > 0){
			isExist = true;
		}
		return isExist;
	}
	
	
	/**
	 * 발송분류 추가
	 * 
	 * @param name
	 * @param userid
	 * @throws Exception
	 * */
	public void addCategory(String name, String userid) throws Exception{
		ImbCategoryInfo category = new ImbCategoryInfo();
		category.setUkey(ImUtils.makeKeyNum(24));
		category.setName(name);
		category.setUserid(userid);
		category.setRegdate(new Date());
		
		categoryMapper.addCategory(category);
	}
	
	
	/**
	 * 발송분류 수정
	 * 
	 * @param name
	 * @param ukey
	 * @throws Exception
	 * */
	public void editCategory(String name, String ukey) throws Exception{
		categoryMapper.editCategory(name, ukey);
	}
	
	
	/**
	 * 발송분류 삭제
	 * 
	 * @param ukey
	 * @throws Exception
	 * */
	public void deleteCategory(String ukey) throws Exception{
		categoryMapper.deleteCategory(ukey);
	}

	/**
	 * 발송분류 사용자 or 관리자 권한으로 목록을 구한다.
	 * @param userid
	 */
	public List<ImbCategoryInfo> getUserCategory(String userid) throws Exception{
		return categoryMapper.getUserCategory(userid);
	}

    public CategoryBean getCategoryForgetCategoryId(String categoryid) {
		return categoryMapper.getCategoryForgetCategoryId(categoryid);
    }

    public String checkDuplicateCategory(String userid, String ukey) throws Exception{
		return categoryMapper.checkDuplicateCategory(userid, ukey);
	}
}
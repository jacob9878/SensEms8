package com.imoxion.sensems.web.database.mapper;


import java.util.Date;
import java.util.List;

import com.imoxion.sensems.web.form.UserListForm;
import com.imoxion.sensems.web.model.UserListModel;
import org.apache.ibatis.annotations.Param;
import org.junit.Test;
import org.mybatis.spring.annotation.MapperScan;

import com.imoxion.sensems.web.database.domain.ImbUserinfo;

/**
 * 사용자 관리 Mapper
 * @date 2021.02.01
 * @author jhpark
 *
 */
@MapperScan
public interface UserMapper {
	
	/**
	 * 사용자 정보를 가져온다.
	 *
	 * @param userid
     * @return
     */
	public ImbUserinfo getUserInfo(@Param("userid") String userid) throws Exception;

	/**
	 * 로그인 실패 횟수 증가
	 * @param userid
	 * @param fail_login
	 * @param fail_login_time
	 */
	public void updateFailLoginCount(@Param("userid") String userid,@Param("fail_login")int fail_login ,@Param("fail_login_time")Date fail_login_time ) throws Exception;

	/**
	 * 한 페이지에서 보여줄 사용자 목록을 가져온다.
	 * @param srch_type
	 * @param srch_keyword
	 * @param permission
	 * @param isStop
	 * @param start
	 * @param end
     * @return
     */
	public List<ImbUserinfo> getUserListForPageing(@Param("srch_type")String srch_type, @Param("srch_keyword")String srch_keyword, @Param("permission")String permission,
												   @Param("isStop")String isStop,@Param("use_smtp")String use_smtp, @Param("start") int start, @Param("end") int end) throws Exception;



	public List<ImbUserinfo> getinfoList(@Param("userid")String userid) throws Exception;

	/**
	 * 사용자 목록 총 수를 구한다.
	 * @param srch_type
	 * @param srch_keyword
	 * @param permission
	 * @param isStop
	 * @return
	 * @throws Exception
	 */
	public int getUserCount(@Param("srch_type")String srch_type, @Param("srch_keyword")String srch_keyword, @Param("permission")String permission,
							@Param("isStop")String isStop, @Param("use_smtp")String use_smtp) throws Exception;


	/**
	 * 아이디가 존재하는지 확인한다.
	 * @param userid 사용자 아이디
	 * @return
	 */
	public int isExistUser(@Param("userid") String userid) throws Exception;


	/**
	 * userinfo 테이블에 사용자 추가
	 * @param userinfo
	 */
	public void insertUser(ImbUserinfo userinfo) throws Exception;


	/**
	 * 사용자 정보 update
	 * @param userinfo
	 * @throws Exception
	 */
	public void updateUser(ImbUserinfo userinfo) throws Exception;

	public void updateInfo(ImbUserinfo userinfo) throws Exception;

	/**
	 * userinfo 테이블에서 사용자 삭제
	 * @param userid
	 */
	void deleteUser(@Param("userid")String userid) throws Exception;

	/**
	 * 사용자 로그인 실패 카운트 0으로 변경
	 * @param userid
	 * @param fail_login
	 */
    void updateFailLoginReset(@Param("userid")String userid, @Param("fail_login")int fail_login) throws Exception;

	/**
	 * 패스워드 변경페이지에서 update 수행
	 * @param userid
	 * @param newSecuPass
	 * @param pwd_date
	 */
    void updatePassword(@Param("userid")String userid, @Param("newPassword")String newSecuPass, @Param("pwd_date")Date pwd_date) throws Exception;

	public void updateUseSMTP(@Param("userid") String userid,@Param("use_smtp") String use_smtp);
}


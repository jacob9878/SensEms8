package com.imoxion.sensems.web.database.mapper;

import com.imoxion.sensems.web.database.domain.ImbAddr;
import com.imoxion.sensems.web.database.domain.ImbReject;
import com.imoxion.sensems.web.form.RejectForm;
import org.apache.ibatis.annotations.Param;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@MapperScan
public interface RejectMapper {

	/**
	 * 수신거부 목록을 가져온다.
	 * 
	 * @return
	 */
	public List<ImbReject> selectAllReject();

	/**
	 * 수신거부 목록을 가져온다.
	 * 
	 * @return
	 */
	public int selectRejectCount(@Param("srch_keyword")String srch_keyword);

	public List<ImbReject> selectRejectList( @Param("srch_keyword")String srch_keyword, @Param("start") int start, @Param("end") int end );


	/**
	 * 수신거부 정보를 가져온다.
	 * 
	 * @param email
	 *            - 수신거부 명
	 * @return
	 */
	public ImbReject selectRejectByKey(@Param("email") String email);



	/**
	 * 수신거부 등록
	 * 
	 * @param imbReject
	 */
	public int insertReject(ImbReject imbReject);


	/**
	 * 수신거부 삭제
	 * @param email
	 */
	public int deleteRejectByKey(@Param("email") String email);


	/**수신거부 중복확인
	 * 
	 * @param email
	 * @return
	 */
	public int isExistReject(@Param("email") String email);


	/**수신거부 전체 건수
	 *
	 *
	 */
	public int selectTotalRejectCount();

	public int selectEditReject(@Param("email") String email);

	public int selectRecentRejectCount(@Param("msgid") String msgid);

	/*public int editReject(ImbReject imbReject);*/

	public int editReject(@Param("email") String email ,@Param("ori_email") String ori_email);

	void insertRejectList(@Param("reject") ImbReject reject) throws Exception;
}

















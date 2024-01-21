package com.imoxion.sensems.web.database.mapper;

import org.apache.ibatis.annotations.Param;
import org.mybatis.spring.annotation.MapperScan;

import com.imoxion.sensems.web.beans.LinkBean;
import com.imoxion.sensems.web.beans.LinkLogMessageIDBean;

import java.util.List;

/**
 * 링크 관련 매퍼
 * create by zpqdnjs 2021-03-26
 * */
@MapperScan
public interface LinkMapper {
	/**
	 * 링크정보 insert
	 * @param bean
	 * */
	public void insertLinkInfo(@Param("linkInfo") LinkBean bean) throws Exception;
	
	/**
	 * 링크로그 테이블 생성
	 * @param msgid
	 * */
	public void createLinkLogTable(@Param("msgid") String msgid) throws Exception;
	
	/**
	 * 특정 링크의 클릭수를 구한다
	 * @param msgid
	 * @param adid
	 * @param userid
	 * */
	public Integer getLinkLogInfoCount(@Param("msgid") String msgid, @Param("adid") int adid, @Param("userid") int userid) throws Exception;
	
	/**
	 * linklog 테이블 카운트 업데이트
	 * @param msgid
	 * @param adid
	 * @param userid
	 * */
	public void addLinkLogCount(@Param("msgid") String msgid, @Param("adid") int adid, @Param("userid") int userid) throws Exception;
	
	/**
	 * link_count 테이블 카운트 업데이트
	 * @param msgid
	 * @param linkid
	 * */
	public void updateLinkCount(@Param("msgid") String msgid, @Param("linkid") int linkid) throws Exception;
	
	/**
	 * link_log 테이블에 데이터 update
	 * @param bean
	 * */
	public void updateLinkLogInfo(LinkLogMessageIDBean bean) throws Exception;
	
	public void insertLinkLogInfo(LinkLogMessageIDBean bean) throws Exception;
	
	/**
	 * link_count 테이블에 insert
	 * @param msgid
	 * @param linkid
	 * @param link_count
	 * */
	public void insertLinkCountInfo(@Param("msgid") String msgid, @Param("linkid") int linkid, @Param("link_count") int link_count) throws Exception;

	/**
	 * imb_emsattach 테이블의 down_count update
	 * @param ekey
	 * @param msgid
	 */
	public void updateAttachCount(@Param("ekey") String ekey,@Param("msgid") String msgid);

    /**
     * link_log 테이블 select
     * @param msgid
     * */
    public List<LinkBean> getLinkInfo(@Param("msgid") String msgid) throws Exception;

}

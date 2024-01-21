package com.imoxion.sensems.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.imoxion.sensems.web.beans.LinkBean;
import com.imoxion.sensems.web.beans.LinkLogMessageIDBean;
import com.imoxion.sensems.web.database.mapper.LinkMapper;

import java.util.List;

/**
 * 링크관련 서비스 
 * */
@Service
public class LinkService {
	@Autowired
	private LinkMapper linkMapper;
	
	/**
	 * 링크정보 insert
	 * @param bean
	 * */
	public void insertLinkInfo(LinkBean bean) throws Exception{
		linkMapper.insertLinkInfo(bean);
		linkMapper.insertLinkCountInfo(bean.getMsgid(), bean.getLinkid(), 0);
	}
	
	/**
	 * 링크로그 테이블 create
	 * @param msgid
	 * */
	public void createLinkLogTable(String msgid) throws Exception{
		linkMapper.createLinkLogTable(msgid);
	}
	
	/**
	 * linklog 테이블에서 특정 링크 카운트 조회하여 0보다 크면 true
	 * @param msgid
	 * @param adid
	 * @param userid
	 * */
	public boolean isOverLinkLogCount(String msgid, int adid, int rcode) throws Exception{
		boolean isOver = false;
		Integer count = linkMapper.getLinkLogInfoCount(msgid, adid, rcode);
		
		if(count != null && count > 0){
			isOver = true;
		}
		return isOver;
	}
	
	/**
	 * linklog 테이블 카운트 업데이트
	 * @param msgid
	 * @param adid
	 * @param rcode
	 * */
	public void addLinkLogCount(String msgid, int adid, int rcode) throws Exception{
		linkMapper.addLinkLogCount(msgid, adid, rcode);
	}
	
	/**
	 * link_count 테이블 카운트 업데이트
	 * @param msgid
	 * @param linkid
	 * */
	public void updateLinkCount(String msgid, int linkid) throws Exception{
		linkMapper.updateLinkCount(msgid, linkid);
	}
	
	/**
	 * link_log 테이블에 데이터 update
	 * @param bean
	 * */
	public void updateLinkLogInfo(LinkLogMessageIDBean bean) throws Exception{
		linkMapper.updateLinkLogInfo(bean);
	}

	public void insertLinkLogInfo(LinkLogMessageIDBean bean) throws Exception{
		linkMapper.insertLinkLogInfo(bean);
	}

	/**
	 * imb_emsattach 테이블의 down_count update
	 * @param ekey
	 * @param msgid
	 */
	public void updateAttachCount(String ekey, String msgid) throws Exception{
		linkMapper.updateAttachCount(ekey,msgid);
	}

	/**
	 * link_log 테이블 select
	 * @param msgid
	 * */
	public List<LinkBean> getLinkInfo(String msgid) throws Exception{
		return linkMapper.getLinkInfo(msgid);
	}

}

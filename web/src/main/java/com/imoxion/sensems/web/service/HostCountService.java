package com.imoxion.sensems.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.imoxion.sensems.web.database.mapper.HostCountMapper;
/**
 * 발송 도메인 통계 관련 서비스 
 * */
@Service
public class HostCountService {
	@Autowired
	private HostCountMapper hostCountMapper;

	/**
	 * hostcount 테이블을 생성한다. hc_msgid
	 * @param msgid
	 * */
	public void createHostCountTable(String msgid) throws Exception{
		hostCountMapper.createHostCountTable(msgid);
	}
}

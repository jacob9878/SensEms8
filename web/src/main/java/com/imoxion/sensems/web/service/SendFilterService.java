package com.imoxion.sensems.web.service;

import com.imoxion.sensems.web.database.domain.ImbSendFilter;
import com.imoxion.sensems.web.database.mapper.SendFilterMapper;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * yeji
 * 2021. 02. 26
 * 발송차단설정 관련 Service 클래스
 */
@Service
public class SendFilterService {

    /* 발송차단설정 DAO */
    @Autowired
    private SendFilterMapper sendFilterMapper;

    protected Logger log = LoggerFactory.getLogger(SendFilterService.class);

    /**
     * 발송차단목록 조회
     * @param srch_keyword
     * @param start
     * @param end
     * @return
     * @throws Exception
     */
    public List<ImbSendFilter> getSendFilterList(String srch_keyword, int start, int end) throws Exception{
        return sendFilterMapper.selectSendFilterList(srch_keyword, start, end);
    }

    /**
     * 전체갯수 조회
     * @param srch_keyword
     * @return
     * @throws Exception
     */
    public int getSendFilterCount(String srch_keyword) throws Exception{
        return sendFilterMapper.selectSendFilterCount(srch_keyword);
    }

    /**
     * 도메인명이 존재하는지 체크
     * @param hostname
     * @return
     * @throws Exception
     */
    public boolean checkExisSendFilter(String hostname) throws Exception{
        boolean isExist = false;
        int count = sendFilterMapper.isExistSendFilter(hostname);
        if(count > 0){
            isExist = true;
        }
        return isExist;
    }

    /**
     * 발송차단 할 도메인 추가
     * @param hostname
     * @throws Exception
     */
    public void addSendFilter(String hostname) throws Exception{
        ImbSendFilter sendFilter = new ImbSendFilter();
        Date regdate = new Date();
        sendFilter.setHostname(hostname);
        sendFilter.setRegdate(regdate);

        sendFilterMapper.insertSendFilter(sendFilter);
    }

    /**
     * 발송차단 할 도메인 삭제
     * @param hostname
     * @throws Exception
     */
    public void deleteSendFilter(String hostname) throws Exception{
        sendFilterMapper.deleteSendFilter(hostname);
    }
}

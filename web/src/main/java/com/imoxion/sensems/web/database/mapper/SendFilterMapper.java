package com.imoxion.sensems.web.database.mapper;

import com.imoxion.sensems.web.database.domain.ImbSendFilter;
import com.imoxion.sensems.web.form.SendFilterForm;
import org.apache.ibatis.annotations.Param;
import org.mybatis.spring.annotation.MapperScan;

import java.util.List;

/**
 * yeji
 * 2021. 02. 26
 * 발송차단설정 관련 mapper 클래스
 */
@MapperScan
public interface SendFilterMapper {

    /**
     * 초기화면 목록을 불러올 list 조회
     * @param srch_keyword
     * @param start
     * @param end
     * @return
     */
    public List<ImbSendFilter> selectSendFilterList(@Param("srch_keyword") String srch_keyword, @Param("start") int start, @Param("end") int end) throws Exception;

    /**
     * 페이징 처리를 위한 total 개수
     * @param srch_keyword
     * @return
     */
    public int selectSendFilterCount(@Param("srch_keyword") String srch_keyword) throws Exception;

    /**
     * 도메인 추가 시 중복체크를 위한 도메인 카운트 조회
     * @param hostname
     * @return
     */
    public int isExistSendFilter(@Param("hostname") String hostname) throws Exception;

    /**
     * 발송차단 할 도메인 추가
     * @param hostname
     * @return
     */
    public int insertSendFilter(ImbSendFilter sendFilter) throws Exception;

    /**
     * 발송차단 할 도메인 삭제
     * @param hostname
     * @return
     */
    public int deleteSendFilter(@Param("hostname") String hostname) throws Exception;
}

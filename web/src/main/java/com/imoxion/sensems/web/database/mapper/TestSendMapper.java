package com.imoxion.sensems.web.database.mapper;

import com.imoxion.sensems.web.beans.EmsBean;
import com.imoxion.sensems.web.beans.TestSendBean;
import com.imoxion.sensems.web.database.domain.ImbTransmitData;
import org.apache.ibatis.annotations.Param;
import org.mybatis.spring.annotation.MapperScan;

import java.util.List;

@MapperScan
public interface TestSendMapper {

    int insertTestSend(TestSendBean testSendBean);

    void insertTestSendBody(@Param("ukey") int ukey,@Param("content") String content);

    void insertSmtpTempMain(TestSendBean tempMain);

    void insertSmtpTempRcpt(TestSendBean tempRcpt);

    /**
     * 조건에 일치하는 테스트 발송결과 목록을 처리한다.
     * @param srch_keyword
     * @param srch_type
     * @param start
     * @param end
     * @return
     */
    List<ImbTransmitData> getTestSendList(@Param("srch_keyword") String srch_keyword, @Param("srch_type") String srch_type, @Param("start") int start, @Param("end") int end,@Param("send_type") String send_type);

    /**
     * 조건에 일치하는 테스트 발송결과 목록의 갯수를 불러온다.
     *
     * @param srch_keyword
     * @param srch_type
     * @return int
     */
    int getTestSendListCount(@Param("srch_keyword") String srch_keyword, @Param("srch_type") String srch_type, @Param("send_type") String send_type);

    /**
     * 해당되는 개별 발송결과를 삭제한다.
     *
     * @param ukey
     */
    void TestSendDelete(@Param("ukey") String ukey);

    /**
     * 해당되는 개별 발송결과 본문을 삭제한다.
     *
     * @param ukey
     */
    void TestSendBodyDelete(@Param("ukey") String ukey);

    /**
     * 조건에 일치하는 발송결과를 가져온다.
     *
     * @param ukey
     */
    TestSendBean getTestSendForUkey(@Param("ukey") String ukey);

    /**
     * 선택된 개별 발송결과 상세보기
     * @param traceid
     * @param serverid
     * @param rcptto
     * @return
     */
    ImbTransmitData getTransmitDataLog(@Param("traceid") String traceid,@Param("serverid") String serverid,@Param("rcptto") String rcptto);

    String getSendType(@Param("subject") String subject);
}

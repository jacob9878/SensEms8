package com.imoxion.sensems.server.repository.mapper;

import com.imoxion.sensems.server.domain.TransmitStatisticsData;

public interface TransmitStaticsticMapper {

    /**
     * 송수신Log 데이터를 저장한다.
     * @param transmitStatisticsData
     */
    void insertTransmitLogData(TransmitStatisticsData transmitStatisticsData);

    /**
     * 송수신Log 데이터를 삭제한다
     */
    public int deleteTransmitLogData(int saveDate);
    

}

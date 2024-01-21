package com.imoxion.sensems.web.database.mapper;

import com.imoxion.sensems.web.beans.ReserveSendBean;
import com.imoxion.sensems.web.database.domain.ImbMessage;
import com.imoxion.sensems.web.form.ReserveSendForm;
import com.imoxion.sensems.web.form.ReserveSendListForm;
import org.apache.ibatis.annotations.Param;
import org.mybatis.spring.annotation.MapperScan;

import java.util.List;


/**
 * 정기예약발송 Mapper
 * @date 2021.02.19
 * @author by moon
 *
 */
@MapperScan
public interface ReserveSendMapper {

    /**
     * 정기예약 발송 데이터 삭제
     * @param msgid
     */
    public void deleteReserveSend(@Param("msgid") String msgid);

    /**
     * 정기예약 발송 데이터 삭제
     * @param msgid
     */
    public void deleteReserveSendContent(@Param("msgid") String msgid);

    /**
     * 정기예약 발송 데이터 카운트 (리스트)
     * @param
     */
    public List<ReserveSendListForm> getReserveSendList(@Param("start") int start, @Param("end") int end, @Param("userid") String userid);


    /**
     * 정기예약 발송 테이블 데이터를 업데이트
     * @param reserveSendBean
     */
    public void modifyReserveSend(ReserveSendBean reserveSendBean);

    /**
     * 정기예약 발송 메시지를 업데이트
     * @param imbMessage
     */
    public void modifyReserveSendContent(ImbMessage imbMessage);

    /**
     * 정기예약 발송 데이터 등록 (리스트 데이터 카운트 시 사용)
     */
    public void reserveRegist(ReserveSendBean reserveSendBean);

    /**
     * 정기예약 발송 메시지 추가
     */
    public void reserveRegistMsg(ImbMessage imbMessage);

    /**
     * 정기예약 발송 목록 데이터 갯수 취득
     * userid값이 isempty라면 모든 카운트를 취득한다.
     * @param userid
     * @return
     */
    public int reserveUserSendTotalCount(@Param("userid") String userid);

    /**
     * 정기예약 발송 데이터 확인
     * @param msgid
     */
    public ReserveSendBean getReserveInfo(@Param("msgid") String msgid);

    /**
     * 정기 예약 발송 데이터 내용 확인
     * @param msgid
     */
    public String getContent(@Param("msgid") String msgid);

}

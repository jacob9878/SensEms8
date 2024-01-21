package com.imoxion.sensems.server.repository.mapper;

import com.imoxion.sensems.server.domain.ImbEmsContents;
import com.imoxion.sensems.server.domain.ImbEmsMain;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface EmsMainMapper {
    List<ImbEmsMain> getListToSend(@Param("reserv_time") String reserv_time);
    List<ImbEmsMain> getListSending(@Param("reserv_time") String reserv_time);
    List<ImbEmsMain> getListSendingToStop(@Param("reserv_time") String reserv_time);
    List<ImbEmsMain> getListToStop();

    List<ImbEmsMain> getListToDelete(String regdate);
    List<ImbEmsMain> getListToResend(@Param("interval") int interval);
    List<ImbEmsMain> getListRecentSendingMail();
    ImbEmsMain getEmsInfo(@Param("msgid") String msgid);
    ImbEmsContents getContents(@Param("msgid") String msgid);
    void updateToStop(@Param("msgid") String msgid, @Param("state") String state, @Param("stop_time") String stop_time);
    void updateStateStartTime(@Param("msgid") String msgid, @Param("state") String state, @Param("start_time") String start_time);
    void updateState(@Param("msgid") String msgid, @Param("state") String state);
    void updateStateSendStartTime(@Param("msgid") String msgid, @Param("state") String state, @Param("send_start_time") long send_start_time);
    void updateStateAndCount(ImbEmsMain emsmain);
    void updateCurSend(@Param("cur_send") int cur_send, @Param("msgid") String msgid);
    void updateToLoggingState(@Param("msgid") String msgid);
    void updateMainEndEx(@Param("msgid") String msgid, @Param("currState") String currState, @Param("currDate") String currDate);

    void updateMailResend(@Param("msgid") String msgid) throws Exception;

    void updateMailResendNum(@Param("msgid") String msgid, @Param("resend_num") int resend_num) throws Exception;

    void deleteEmsMain(@Param("msgid") String msgid);
    void deleteMsgInfo(@Param("msgid") String msgid);

    void insertMsgInfo(@Param("msgid") String msgid, @Param("contents") String contents) throws Exception;

    void insertMailData(ImbEmsMain emsMain) throws Exception;

}

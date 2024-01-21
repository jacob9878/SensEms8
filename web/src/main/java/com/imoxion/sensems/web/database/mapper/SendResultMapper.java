package com.imoxion.sensems.web.database.mapper;

import com.imoxion.sensems.web.beans.*;
import com.imoxion.sensems.web.form.LinkListForm;
import com.imoxion.sensems.web.form.StatSendForm;
import org.apache.ibatis.annotations.Param;
import org.mybatis.spring.annotation.MapperScan;

import java.util.List;

@MapperScan
public interface SendResultMapper {
    /**
     * 조건에 일치하는 발송결과 목록을 처리한다.
     *
     * @param userid
     * @return
     */
    List<EmsBean> getSendResultList(@Param("srch_keyword") String srch_keyword, @Param("userid") String userid, @Param("categoryid") String categoryid, @Param("state") String state, @Param("start")int start, @Param("end")int end);

    /**
     * 조건에 일치하는 발송결과 목록의 갯수를 불러온다.
     *
     * @param srch_keyword
     * @param userid
     * @return
     */
    int getsendResultCount(@Param("srch_keyword") String srch_keyword, @Param("userid") String userid, @Param("categoryid") String categoryid, @Param("state") String state);


    /**
     * 카테고리 이동 (업데이트 처리)
     *
     * @param msgid
     * @param categoryid
     */
    void categoryMove(@Param("msgid") String msgid, @Param("categoryid") String categoryid);

    /**
     * 조건에 일치하는 발송결과를 가져온다.
     *
     * @param msgid
     */
    EmsBean getEmsForMsgid(@Param("msgid") String msgid);

    /**
     * 해당되는 발송결과를 삭제한다.
     *
     * @param msgid
     */
    void resultDelete(@Param("msgid") String msgid);

    /**
     * 해당되는 발송결과(ims_msg_info)를 삭제한다.
     *
     * @param msgid
     */
    void msginfoDelete(@Param("msgid") String msgid);

    /**
     * 선택된 메일발송결과 값을 재발신 상태로 만든다.
     *
     * @param msgid
     */
    void doResend(@Param("msgid") String msgid);

    /**
     * 선택된 메일발송결과 값을 중지 상태로 만든다.
     *
     * @param msgid
     */
    void doStop(@Param("msgid") String msgid);

    /**
     * 수신자 카운트 테이블 정보를 가져온다.
     *
     * @param msgid
     */
    int getReceiptCountBean(@Param("msgid") String msgid);

    /**
     * 메시지 아이디를 기준으로 유동적으로 생성된 수신자 테이블의 데이터 정보를 가져온다.
     *
     * @param msgid
     */
    List<RecvMessageIDBean> getRecvMessageIDForMsgid(@Param("msgid") String msgid);

    /**
     * 메시지 아이디를 기준으로 유동적으로 생성된 링크 테이블의 데이터 정보를 가져온다.
     *
     * @param msgid
     */
    LinkLogMessageIDBean getLinkLogMessageIDForMsgid(@Param("msgid") String msgid);

    int getLinkLogCheck(@Param("tableName") String tableName);

    /**
     * 메시지 아이디를 기준으로 링크로그의 개수를 카운트한다.
     *
     * @param msgid
     */
    int getClickCount(@Param("msgid") String msgid);

    /**
     * 메시지 아이디를 기준으로 유동적으로 생성된 링크 특정 테이블의 데이터 정보를 가져온다.
     * @param msgid
     * @param linkid
     */
    List<LinkLogMessageIDBean> getLinkLogMessageForMsgid(@Param("msgid") String msgid, @Param("linkid") String linkid);

    String getLinkClickDate(@Param("msgid") String msgid, @Param("linkid") String linkid);

    /**
     * 해당되는 메시지 아이디의 에러 카운트 객체 정보를 가져온다.
     * @param msgid
     */
    ImbErrorCount getErrorCountForMsgid(@Param("msgid") String msgid);

    /**
     * 해당되는 메시지 아이디의 발송 도메인 통계 정보를 가져온다.
     * @param msgid
     * @return
     */
    List<HC_MessageIDBean> getHC_MessageIDForMsgid(@Param("msgid") String msgid);

    /**
     * 해당되는 메시지 아이디의 발송 도메인 통계 정보를 가져온다. (페이징)
     * @param msgid
     */
    List<HC_MessageIDBean> getHC_MessageIDPagingForMsgid(@Param("msgid") String msgid, @Param("start")int start, @Param("end")int end);

    /**
     * 에러메일 재발신 해당 수신확인 테이블의 발송 성공,실패 유무를 구한다.
     * @param msgid
     * @return
     */
    int getRecvMessageIDSuccessAndFailCount(@Param("msgid") String msgid, @Param("flag") String flag);

    /**
     * 해당 수신확인 테이블의 발송 성공,실패 유무를 구한다.
     * @param msgid
     * @return
     */
    int getRecvMessageIDSendSuccessAndFailCount(@Param("msgid") String msgid, @Param("flag") String flag);

    /**
     * 에러 유형의 총 합을 구한다.
     * @param msgid
     * @return
     */
    int getErrorCountForMsgidCount(@Param("msgid") String msgid);

    int getHC_MessageIDForMsgidCount(@Param("msgid") String msgid);


    /**
     * 팝업에 출력할 수신그룹목록 정보를 가져온다.
     * @param msgid
     * @return
     */
    List<RecvMessageIDBean> getReciverListPageingForMsgid(@Param("msgid") String msgid, @Param("start")int start, @Param("end")int end, @Param("srch_keyword") String srch_keyword, @Param("srch_type") String srch_type);

    /**
     * 수신그룹목록의 토탈카운트를 구한다.
     * @param msgid
     * @return
     */
    int getRecvTotalCountForMsgid(@Param("msgid") String msgid);

    /**
     * 수신그룹목록의 페이징을 위해 토탈카운트를 구한다. (검색)
     * @param msgid
     * @return
     */
    int getRecvTotalCountForMsgid2(@Param("msgid") String msgid,@Param("srch_keyword") String srch_keyword, @Param("srch_type") String srch_type);

    /**
     * 특정 결과의 수신확인 카운트를 구한다.
     * @param msgid
     * @param id
     * */
    public int getRecvCount(@Param("msgid") String msgid, @Param("id") int id) throws Exception;

    /**
     * 수신확인 카운트를 더한다.
     * @param msgid
     * @param id
     * */
    public void addRecvCount(@Param("msgid") String msgid, @Param("id") int id) throws Exception;

    /**
     * 수신확인 카운트 정보 update (일, 시간 등)
     * @param msgid
     * @param rcode
     * @param recv_time
     * @param recv_date
     * @param recv_hour
     * @throws Exception
     */
    public void updateRecvCountInfo(@Param("msgid") String msgid, @Param("rcode") String rcode, @Param("recv_time") String recv_time, @Param("recv_date") String recv_date, @Param("recv_hour") String recv_hour) throws Exception;

    public void updateRespTime(@Param("msgid") String msgid, @Param("resp_time") String resp_time);

    /**
     * 수신확인 카운트 정보
     * @param bean
     * */
    List<RecvMessageIDBean> getRecvCountIDForMsgid(@Param("msgid") String msgid);

    /**
     * 조건별 수신확인 카운트 정보
     * @param bean
     * */
    int getRecvCountForDate(@Param("msgid") String msgid, @Param("recv_date")String recv_date, @Param("srch_keyword")String srch_keyword, @Param("srch_type")String srch_type, @Param("recv_count")String recv_count);

    /**
     * 총수신, 미수신 확인 카운트 정보
     * @param msgid
     * */
    int getRecvCountForMsgid(@Param("msgid") String msgid, @Param("flag") String flag);

    /**
     * 총수신, 미수신 페이징 정보
     * @param msgid
     * @param srch_keyword
     * @param srch_type
     * @param recv_count
     * */
    int getRecvCountFlagForMsgid(@Param("msgid") String msgid, @Param("srch_keyword")String srch_keyword, @Param("srch_type")String srch_type, @Param("recv_count")String recv_count);

    /**
     * 해당 날짜에 대한 수신 확인 정보
     * @param msgid
     * @param recv_date
     * */
    List<RecvMessageIDBean> getRecvListForMsgid(@Param("msgid") String msgid, @Param("recv_date") String recv_date);

    /**
     * 해당 id에 대한 수신 확인 정보
     * @param msgid
     * @param id
     * */
    RecvMessageIDBean getRecvForMsgid(@Param("msgid") String msgid, @Param("id") String id);

    /**
     * 팝업에 출력할 수신확인 통계 목록정보를 가져온다.
     * @param msgid
     * @param start
     * @param end
     * @param srch_keyword
     * @param srch_type
     * @param flag
     * */
    List<RecvMessageIDBean> getRecvListPageingForMsgid(@Param("msgid") String msgid, @Param("start")int start, @Param("end")int end, @Param("srch_keyword") String srch_keyword, @Param("srch_type") String srch_type, @Param("recv_date") String recv_date, @Param("recv_count") String recv_count);


    List<RecvMessageIDBean> getRecvListPageingForMsgid2(@Param("msgid") String msgid, @Param("start")int start, @Param("end")int end);

    /**
     * 해당되는 메시지 아이디의 생성된 수신자 테이블의 데이터 정보를 가져온다.
     * @param msgid
     * @param flag
     * @return
     */
    List<RecvMessageIDBean> getRecvMessageIDForMsgidFlag(@Param("msgid") String msgid, @Param("flag") String flag);

    /**
     * 해당되는 메시지 아이디의 유동적으로 생성된 링크정보 테이블의 데이터 정보를 가져온다.
     * @param msgid
     * @return
     */
    List<LinkBean> getLinkMessageIDForMsgid(@Param("msgid")String msgid);

    /**
     * 해당되는 메시지 아이디의 유동적으로 생성된 링크정보 테이블의 데이터 정보를 가져온다.(페이징)
     * @param msgid
     * @param start
     * @param end
     * @return
     */
    List<LinkBean> getLinkMessageIDForMsgid2(@Param("msgid")String msgid, @Param("start") int start, @Param("end") int end);

    /**
     *  링크를 클릭한 사용자 카운트 정보.
     * @param msgid
     * @param linkid
     * @return
     */
    int getLinkClickUser(@Param("msgid") String msgid, @Param("linkid") String linkid);


    int[] getClickUser(@Param("msgid") String msgid, @Param("linkid") String linkid);

    /**
     * 해당되는 메시지 아이디의 유동적으로 생성된 링크정보 테이블의 유저 데이터 정보를 가져온다.
     * @param msgid
     * @param linkid
     * @param srch_keyword
     * @param srch_type
     * @return
     */
    List<LinkLogMessageIDBean> getLinkMessageUserForMsgid(LinkListForm form);


    /**
     * 해당되는 메시지 아이디의 유동적으로 생성된 링크정보 테이블의 유저 데이터 정보를 가져온다. (페이징)
     * @param form
     * @param start
     * @param end
     * @return
     */
    List<LinkLogMessageIDBean> getLinkMessageUserForMsgid2(@Param("msgid") String msgid,@Param("linkid") String linkid,@Param("srch_type") String srch_type,
                                                           @Param("srch_keyword") String srch_keyword,@Param("start") int start,@Param("end") int end);

    /**
     * 총수신, 미수신 페이징 정보
     * @param msgid
     * @param flag
     * @param srch_keyword
     * @param srch_type
     * @param recv_count
     * */
    /*int getLinkMessageCountForMsgid(@Param("msgid") String msgid, @Param("linkid") String linkid, @Param("srch_keyword") String srch_keyword, @Param("srch_type") String srch_type);*/
    int getLinkMessageCountForMsgid(LinkListForm form);


    /**
     * 해당 날짜에 대한 링크 클릭률 데이터를 출력한다.
     * @param msgid
     * @param click_date
     * @param linkid
     * */
    List<LinkLogMessageIDBean> getLinkListForMsgid(@Param("msgid")String msgid, @Param("click_date")String click_date, @Param("linkid") String linkid);

    List<RecvMessageIDBean> getDownloadStatErrorList (@Param("msgid") String msgid, @Param("errcode") String errcode) throws Exception;

    List<RecvMessageIDBean> getStatErrorList(@Param("msgid") String msgid, @Param("errcode") String errcode, @Param("srch_keyword") String srch_keyword, @Param("srch_type") String srch_type, @Param("start")int start, @Param("end")int end) throws Exception;

    int getStatErrorListCount(@Param("msgid") String msgid, @Param("errcode") String errcode, @Param("srch_keyword") String srch_keyword, @Param("srch_type") String srch_type) throws Exception;



    List<LinkLogMessageIDBean> getLinkListDetail(@Param("msgid")String msgid, @Param("click_date")String click_date, @Param("linkid") String linkid);

    /**
     * 링크 개수 정보를 가져온다.
     * @param msgid
     * @return
     */
    int getLinkCount(@Param("msgid") String msgid);

    int getLinkClickCount(@Param("msgid") String msgid);

    int getLinkCountForLinkid(@Param("msgid") String msgid, @Param("linkid") int linkid);

    int getLinkClickCountForLinkid(@Param("msgid") String msgid, @Param("linkid") int linkid);

    /**
     * 보낸 메일 정보를 삭제한다.
     * @param msgid
     * @return
     */
    void errorCountDelete(@Param("msgid") String msgid);
    void linkCountDelete(@Param("msgid") String msgid);
    void linkInfoDelete(@Param("msgid") String msgid);
    void receiptCountDelete(@Param("msgid") String msgid);
    void addrselDelete(@Param("msgid") String msgid);

    /**
     * 보낸 메일 테이블을 삭제한다.
     * @param msgid
     * @return
     */
    void recvDropTable(@Param("msgid") String msgid);
    void hcDropTable(@Param("msgid") String msgid);
    void linklogDropTable(@Param("msgid") String msgid);

}

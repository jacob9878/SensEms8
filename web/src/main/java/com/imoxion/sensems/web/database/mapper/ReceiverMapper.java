package com.imoxion.sensems.web.database.mapper;

import com.imoxion.sensems.web.beans.EmsBean;
import com.imoxion.sensems.web.beans.ReceiverBean;
import com.imoxion.sensems.web.beans.ReceiverInfo;
import com.imoxion.sensems.web.beans.ReserveSendBean;
import com.imoxion.sensems.web.database.domain.ImbReceiver;
import com.imoxion.sensems.web.database.domain.ImbUserinfo;
import org.apache.ibatis.annotations.Param;
import org.mybatis.spring.annotation.MapperScan;

import java.util.ArrayList;
import java.util.List;

/**
 * 수신자그룹 Mapper
 * @date 2021.02.19
 * @author by moon
 *
 */
@MapperScan
public interface ReceiverMapper {

    /**
     * 수신자 그룹 목록을 가져온다. 발송 시 수신그룹 목록을 읽을 때 사용한다.
     */
    ArrayList<ImbReceiver> getReceiverList() throws Exception;


    /**
     * 수신그룹 목록 총 수를 구한다.
     * @param srch_type
     * @param srch_keyword
     * @return
     */
    int getReceiverGroupCount(@Param("srch_type") String srch_type, @Param("srch_keyword")String srch_keyword, @Param("userid")String userid) throws Exception;

    /**
     * 수신그룹 ukey와 recid가 같은 디테일 페이지 총 수를 구한다
     * @parma recid
     */
    int getReceiverGroupDetailCount(@Param("recid") String recid) throws Exception;

    /**
     * 주소록 userid와 gkey가 같은 디테일 페이지 총 수를 구한다
     * @parma userid
     * @parma gkey
     */
    int getReceiverAddrDetailCount(@Param("userid") String userid, @Param("gkey") int gkey) throws Exception;

    /**
     * 한 페이지에서 보여줄 수신그룹 목록을 가져온다.
     * @param srch_type
     * @param srch_keyword
     * @param start
     * @param end
     * @return
     */
    List<ImbReceiver> getReceiverGroupForPageing(@Param("srch_type")String srch_type, @Param("srch_keyword")String srch_keyword, @Param("start")int start, @Param("end")int end, @Param("userid")String userid) throws Exception;

    /**
     * 수신그룹 데이터 추가
     * @param receiverInfo
     */
    void insertReceiver(ImbReceiver receiverInfo) throws Exception;

    /**
     * ukey를 이용하여 수신그룹 획득
     * @param ukey
     */
    ImbReceiver getReceiverGroup(@Param("ukey")String ukey) throws Exception;

    /**
     * receiverInfo 이용하여 수신자정보 획득
     * @param userid
     */
    List<ImbUserinfo> getReceiverUserinfo(@Param("userid")String userid) throws Exception;

    /**
     * recid를 이용하여 수신그룹 획득
     * @param recid
     */
    ImbReceiver getReceiverGroupRecid(@Param("recid")String recid) throws Exception;


    /**
     * ukey를 이용하여 수신그룹 삭제
     * @param ukey
     */
    void deleteReceiverGroup(@Param("ukey")String ukey) throws Exception;

    /**
     * 수신그룹 정보 수정
     * @param receiverInfo
     * @throws Exception
     */
    void updateReceiver(ImbReceiver receiverInfo) throws Exception;

    /**
     * ukey 값으로 dbkey 값을 구한다.
     * @param ukey
     * @return
     * */
    public String getDbKey(String ukey) throws Exception;

    /**
     * msgid로 메시지 정보를 가져온다.
     * @param msgid
     * @return
     * @throws Exception
     */
    public EmsBean getMsginfo(String msgid) throws Exception;
}

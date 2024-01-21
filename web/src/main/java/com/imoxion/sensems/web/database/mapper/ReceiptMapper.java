package com.imoxion.sensems.web.database.mapper;

import com.imoxion.sensems.web.beans.EmsBean;
import com.imoxion.sensems.web.beans.ReceiptBean;
import com.imoxion.sensems.web.beans.RecvMessageIDBean;
import com.imoxion.sensems.web.form.ReceiptForm;
import org.apache.ibatis.annotations.Param;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@MapperScan
public interface ReceiptMapper {

    /**
     * 수신 확인 msgid로 조회했을 때  count
     *
     */
    public int selectReceiptCount(ReceiptBean receiptForm);

    /**
     * 수신 확인 rcode로 조회했을 때 count
     *
     */
    public int selectReceiptRecvCount(ReceiptBean receiptForm);

    /**
     * 수신확인 호출시간 리스트
     *
     */
    public List<ReceiptBean> selectReceiptRecvList(@Param("searchKeywordMsgid")String searchKeywordMsgid, @Param("searchKeywordRcode")String searchKeywordRcode);

    public String getMsgName(@Param("msgid")String msgid) throws Exception;

    public String getMailFrom(@Param("msgid")String msgid) throws Exception;
}

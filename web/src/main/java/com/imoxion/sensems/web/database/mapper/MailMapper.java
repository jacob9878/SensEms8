package com.imoxion.sensems.web.database.mapper;

import org.apache.ibatis.annotations.Param;
import org.mybatis.spring.annotation.MapperScan;

import com.imoxion.sensems.web.beans.AttachBean;
import com.imoxion.sensems.web.beans.EmsBean;
import com.imoxion.sensems.web.beans.UploadFileBean;

import java.util.List;

/**
 * 메일 관련 매퍼 - 첨부파일, msg info 등
 * create by zpqdnjs
 * */
@MapperScan
public interface MailMapper {

	/**
	 * 메일 본문 정보 insert
	 * @param msgid
	 * @param contents
	 * */
    public void insertMsgInfo(@Param("msgid") String msgid, @Param("contents") String contents) throws Exception;
    
    /**
     * 임시 업로드 파일 정보 insert
     * @param bean
     * */
    public void insertUploadFile(UploadFileBean bean) throws Exception;
    
    /**
     * 임시 업로드 파일 정보를 가져온다.
     * @param fkey
     * */
    public UploadFileBean getUploadFileInfo(@Param("fkey") String fkey) throws Exception;
    
    /**
     * 임시 업로드 파일 삭제
     * @param fkey
     * */
    public void deleteUploadFile(@Param("fkey") String fkey) throws Exception;
    
    /**
     * 첨부파일 정보 insert
     * @param bean
     * */
    public void insertAttachInfo(AttachBean bean) throws Exception;
    
    /**
     * 메일 데이터 insert (imb_emsmain)
     * @param bean
     * */
    public void insertMailData(EmsBean bean) throws Exception;
    
    /**
	 * 메일 본문 imb_msg_info update 
	 * @param msgid
	 * @param content
	 * */
	public void updateMsgInfo(@Param("msgid") String msgid, @Param("contents") String contents) throws Exception;
	
	/**
	 * 메일 데이터 update
	 * @param bean 
	 * */
	public void updateMailData(EmsBean bean) throws Exception;
	
	/**
	 * 메일 정보를 가져온다
	 * @param msgid
	 * @param userid
	 * */
	public EmsBean getMailData(@Param("msgid") String msgid, @Param("userid") String userid) throws Exception;

	public EmsBean noUseridGetMailData(@Param("msgid") String msgid) throws Exception;

	public EmsBean getMailcontentData(@Param("msgid") String msgid) throws Exception;
	/**
	 * 첨부파일 정보를 가져온다
	 * @param ekey
	 * @param msgid
	 * */
	public AttachBean getAttachInfo(@Param("ekey") String ekey, @Param("msgid") String msgid) throws Exception;

	/**
	 * 첨부파일 정보를 모두 가져온다
	 * @param msgid
	 * */
	public List<AttachBean> getAllAttachInfo(@Param("msgid") String msgid) throws Exception;
	
	/**
	 * 반응분석 종료일을 구한다.
	 * @param msgid
	 * */
	public String getRespTime(@Param("msgid") String msgid) throws Exception;
	
	/**
	 * receipt_count 테이블의 수신확인 카운트 update
	 * @param msgid
	 * */
	public void updateReceiptCount(@Param("msgid") String msgid) throws Exception;
	
	/**
	 * receipt_count 테이블에 데이터 insert
	 * @param msgid
	 * */
	public void insertReceiptCountInfo(@Param("msgid") String msgid) throws Exception;

	/**
	 * 메일 재발송 정보 업데이트
	 * @param msgid
	 * */
	public void updateMailResend(@Param("msgid") String msgid) throws Exception;

	/**
	 * 메일 재발송 정보 업데이트
	 * @param msgid
	 * @param resend_num
	 * */
	public void updateMailResendNum(@Param("msgid") String msgid, @Param("resend_num") int resend_num) throws Exception;

}

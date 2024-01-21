package com.imoxion.sensems.web.service;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.imoxion.common.util.ImStringUtil;
import com.imoxion.common.util.ImTimeUtil;
import com.imoxion.sensems.web.beans.LinkLogMessageIDBean;
import com.imoxion.sensems.web.beans.RecvMessageIDBean;

/**
 * 링크추적, 수신확인 등 관련 서비스
 * create by zpqdnjs 2021-03-30 
 * */
@Service
public class CheckService {
	
	protected Logger log = LoggerFactory.getLogger(CheckService.class);
	
	@Autowired
	private MailService mailService;
	
	@Autowired
	private LinkService linkService;
	
	@Autowired
	private SendResultService sendResultService;
	
	/**
	 * 수신확인 정보 업데이트
	 * @param msgid
	 * @param rcode
	 * */
	public void updateReceiptInfo(String msgid, String rcode) throws Exception{
		// 이미 데이터가 있으면 수신확인 카운트만 업데이트 recv_msgid
		if(sendResultService.isOverRecvCount(rcode, msgid)){
			sendResultService.addRecvCount(rcode, msgid);
		} else{
			Date now = new Date();
			String recv_time = ImTimeUtil.getDateFormat(now, "yyyyMMddHHmmss");
			String recv_date = ImTimeUtil.getDateFormat(now, "yyyyMMdd");
			String recv_hour = ImTimeUtil.getDateFormat(now, "HH");
			
			Calendar cal = Calendar.getInstance();
			cal.setTime(now);
			//int recv_week = cal.get(Calendar.WEEK_OF_YEAR);
			
			/*RecvMessageIDBean bean = new RecvMessageIDBean();
			bean.setMsgid(msgid);
			bean.setId(rcode);
			bean.setRecv_time(recv_time);
			bean.setRecv_date(recv_date);
			bean.setRecv_hour(recv_hour);
			bean.setRecv_week(recv_week);*/
			sendResultService.updateRecvCountInfo(msgid, rcode, recv_time, recv_date, recv_hour);
		}
		
		// imb_receipt_count 테이블 업데이트
		mailService.updateReceiptCount(msgid);
	}
	
	/**
	 * 링크 클릭시 링크정보 update
	 * @param msgid
	 * @param adid
	 * @param rcode
	 * @param email
	 * 
	 * @throws Exception
	 * @return
	 * */
	public void updateLinkInfo(String msgid, String adid, String rcode, String email) throws Exception{
		int linkid = ImStringUtil.parseInt(adid);
		int userid = ImStringUtil.parseInt(rcode);
		// 카운트가 1이상이면 카운트만 ++ ( link_log )
		if(linkService.isOverLinkLogCount(msgid, linkid, userid)){	//flase면 insert, true면 update
			linkService.addLinkLogCount(msgid, linkid, userid);
		} else{
			Date now = new Date();
			String click_time = ImTimeUtil.getDateFormat(now, "yyyyMMddHHmm");
			String link_date = ImTimeUtil.getDateFormat(now, "yyyyMMdd");
			String link_hour = ImTimeUtil.getDateFormat(now, "HH");
			
			Calendar cal = Calendar.getInstance();
			cal.setTime(now);
			int link_week = cal.get(Calendar.WEEK_OF_YEAR);
			
			LinkLogMessageIDBean bean = new LinkLogMessageIDBean();
			bean.setMsgid(msgid);
			bean.setAdid(linkid);
			bean.setUserid(userid);
			bean.setClick_count(1);
			bean.setClick_time(click_time);
			bean.setLink_date(link_date);
			bean.setLink_hour(link_hour);
			bean.setLink_week(link_week);
			linkService.insertLinkLogInfo(bean);	
		}
		
		// imb_link_count 테이블에 카운트 누적
		linkService.updateLinkCount(msgid, linkid);
	}
	
	/**
	 * 현재 날짜와 반응분석 종료일 비교
	 * @param msgid
	 * @throws Exception
	 * */
	public boolean isAfterResptime(String msgid) throws Exception{
		boolean isOver = false;
		String resp_str = mailService.getRespTime(msgid);
		
		if(StringUtils.isBlank(resp_str)){
			log.info("RespTime is Null, msgid : {}", msgid);
			return isOver;
		}
		//2022 08 16 00
		Date resp_time = ImTimeUtil.getDateFromString(resp_str, "yyyyMMddHHmm");
		Date now = new Date();
		
		if(now.after(resp_time)){
			log.info("Over RespTime, resp_time : {}", resp_str);
			isOver = true;
		}
		return isOver;
	}

	/**
	 * 첨부파일 링크 클릭 시 다운로드 횟수 증가
	 * @param ekey
	 * @param msgid
	 * @throws Exception
	 */
	public void updateAttachInfo(String ekey, String msgid) throws Exception{
		linkService.updateAttachCount(ekey,msgid);
	}
}

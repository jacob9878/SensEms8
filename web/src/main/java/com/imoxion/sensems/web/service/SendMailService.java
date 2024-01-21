package com.imoxion.sensems.web.service;

import com.imoxion.common.database.ImDatabaseConnectionEx;
import com.imoxion.common.mail.ImMessage;
import com.imoxion.common.util.*;
import com.imoxion.security.ImSecurityLib;
import com.imoxion.sensems.web.beans.*;
import com.imoxion.sensems.web.common.ImbConstant;
import com.imoxion.sensems.web.database.domain.ImbAddrGrp;
import com.imoxion.sensems.web.database.domain.ImbReceiver;
import com.imoxion.sensems.web.form.MailWriteForm;
import com.imoxion.sensems.web.module.sender.SendMailData;
import com.imoxion.sensems.web.util.ImUtility;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;

import javax.mail.Session;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Date;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 웹단 메일 발송 로직 처리하기 위한 Service
 * create by zpqdnjs 2021-03-16
 * */
@Service
public class SendMailService {
	
	protected Logger log = LoggerFactory.getLogger(SendMailService.class);
	
	@Autowired
    private MessageSourceAccessor message;
	
	@Autowired
	private ReceiverService receiverService;
	
	@Autowired
	private MailService mailService;
	
	@Autowired
	private LinkService linkService;
	
	@Autowired
	private AddressService addressService;
	
	@Autowired
	private HostCountService hostCountService;

	private final String RESP_MIN = "00";
	
	/**
	 * 메일 임시보관 데이터 insert
	 * @param form
	 * */
	public boolean insertDraftMailData(MailWriteForm form) throws Exception{
		boolean isExist = false;
		EmsBean bean = new EmsBean();
		String msgid = form.getMsgid();
		if(StringUtils.isBlank(msgid)){
			msgid = ImUtils.makeKeyNum(24);
		} else{
			isExist = true;
		}
		bean.setCategoryid(form.getCategoryid());
		bean.setMsgid(msgid);
		bean.setUserid(form.getUserid());
		bean.setMail_from(form.getMail_from());
		bean.setReplyto(form.getReplyto());
		bean.setMsg_name(form.getMsg_name());
		bean.setRegdate(ImTimeUtil.getDateFormat(new Date(), "yyyyMMddHHmm"));
		bean.setRecid(form.getRecid());
		bean.setRecname(form.getRecname());
		bean.setRectype(form.getRectype());
		bean.setIsattach("0"); // 임시보관은 첨부파일 저장X
		bean.setParentid(msgid);

		// 예약시간 설정
		if("1".equals(form.getIs_reserve())){
			String reserv_time = form.getReserv_day() + form.getReserv_hour() + form.getReserv_min();
			// yyyyMMddHHmm 이 아닌경우는 false
			if(reserv_time != null && reserv_time.length() == 12){
				bean.setReserv_time(reserv_time);
			} else{
				log.info("[SENDMAIL] Incorrect ReservTime format : {}", reserv_time);
				return false;
			}
		}
		
		// 반응분석 종료일 설정 yyyyMMddHH
		String resp_time = ImUtility.changeDatesub(form.getResp_day()) + form.getResp_hour();
		if(resp_time != null && resp_time.length() == 10){
			bean.setResp_time(resp_time);
		} else{
			log.info("[SENDMAIL] Incorrect RespTime format : {}", resp_time);
			return false;
		}
		
		bean.setCharset(form.getCharset());
		bean.setIshtml(ImStringUtil.parseInt(form.getIshtml()));
		bean.setIs_same_email(ImStringUtil.parseInt(form.getIs_same_email()));
		bean.setIslink(form.getIslink());
		bean.setState(EmsBean.STATUS_DRAFT);
		
		// 기존에 저장되어 있는 경우는 업데이트
		if(isExist){
			mailService.updateMsgInfo(msgid, form.getContent());
			mailService.updateMailData(bean);
		} else{
			mailService.insertMsgInfo(msgid, form.getContent());
			mailService.insertMailData(bean);
		}
		return true;
	}
	
	/**
	 * 메일 데이터 가공하여 테이블에 insert (발송)
	 * @param form
	 * @param
	 * @return
	 * */
	public boolean insertMailData(MailWriteForm form, HttpServletRequest request) throws Exception{
		// 임시보관된 메일인지 확인
		boolean isDraft = false;
		String msgid = form.getMsgid();
		if(StringUtils.isBlank(msgid)){
			msgid = ImUtils.makeKeyNum(24);
		} else{
			isDraft = true;
		}
		String weburl = HttpRequestUtil.getWebURL(request);
		String client_ip = request.getRemoteAddr();
		String userid = form.getUserid();
		String mailFrom = form.getMail_from();
		String rectype = form.getRectype();
		String recid = form.getRecid();
		String state = form.getState();
		
		EmsBean bean = new EmsBean();
		bean.setCategoryid(form.getCategoryid());
		bean.setMsgid(msgid);
		bean.setUserid(userid);
		bean.setMail_from(mailFrom);
		bean.setReplyto(form.getReplyto());
		bean.setRectype(rectype);
		bean.setParentid(msgid);
				
		if(StringUtils.isBlank(rectype) || StringUtils.isBlank(recid)){
			log.info("[SENDMAIL] Receiver is null, rectype : {}, recid : {}", rectype, recid);
			return false;
		}
		
		// 수신그룹인경우 dbkey 세팅
		if (EmsBean.RECE_TYPE_RECEIVE_GROUP.equals(rectype)) {
			String dbkey = receiverService.getDbKey(recid);
			if (StringUtils.isNotBlank(dbkey)) {
				bean.setDbkey(dbkey);
			}
			bean.setRecid(recid);
			bean.setRecname(form.getRecname());
		}else if(EmsBean.RECE_TYPE_ADDR.equals(rectype)){
		// 주소록인 경우 imb_addrsel에 데이터 insert
			String gkeys[] = recid.split(",");
			for (int i = 0; i < gkeys.length; i++) {
				int gkey = ImStringUtil.parseInt(gkeys[i]);
				ImbAddrGrp group = addressService.getAddressGrpByGkey(userid, gkey);

				if (group != null) {
					AddrSelBean addrSelBean = new AddrSelBean();
					addrSelBean.setGkey(gkey);
					addrSelBean.setGname(group.getGname());
					addrSelBean.setMsgid(msgid);
					addrSelBean.setUserid(userid);
					addressService.insertAddrSel(addrSelBean);
				}
			}
			bean.setRecname(form.getRecname());
		}else if(EmsBean.RECE_TYPE_RESEND.equals(rectype)){
			//재발송인 경우
			String old_msgid = form.getOld_msgid();

			EmsBean emsBean = mailService.noUseridGetMailData(old_msgid);
			if(emsBean == null){
				log.info("[SENDMAIL] Old message emsbean is null, old_msgid : {}", old_msgid);
				return false;
			}

			String field = "";
			if(emsBean.getDbkey() != null){	//수신그룹이면
				ImbReceiver imbReceiver = receiverService.getReceiverGroupRecid(recid);
				if(imbReceiver == null){
					log.info("[SENDMAIL] imbReceiver is null, old_msgid : {}, recid : {}", old_msgid, recid);
					return false;
				}

				if (StringUtils.isNotBlank(imbReceiver.getDbkey())) {
					bean.setDbkey(imbReceiver.getDbkey());
				}
				bean.setRecid(recid);
				bean.setRecname(form.getRecname());
				field = this.getResendQueryColumn(old_msgid);
			}else{ //주소록이면
				String gnames[] = form.getRecname().split(",");
				for (int i = 0; i < gnames.length; i++) {
					ImbAddrGrp group = addressService.getAddressGrpByGname(userid, gnames[i]);

					if (group != null) {
						AddrSelBean addrSelBean = new AddrSelBean();
						addrSelBean.setGkey(group.getGkey());
						addrSelBean.setGname(gnames[i]);
						addrSelBean.setMsgid(msgid);
						addrSelBean.setUserid(userid);
						addressService.insertAddrSel(addrSelBean);
					}
				}
				bean.setRecname(form.getRecname());
				field = "field1, field2, field3, field4, field5, field6, field7, field8, field9";
			}

			bean.setParentid(emsBean.getParentid());
			bean.setResend_num(emsBean.getResend_num()+1);
			bean.setResend_step(emsBean.getResend_num()+1);

			if(form.getResend_flag().equals("rcpt")){
				bean.setQuery("select " + field + " from recv_" + old_msgid + " where recv_count > 0 and success = '1' order by field1");
			}else if(form.getResend_flag().equals("norcpt")){
				bean.setQuery("select " + field + " from recv_" + old_msgid + " where recv_count = 0 and success = '1' order by field1");
			}else if(form.getResend_flag().equals("link")){
				String query = "SELECT distinct " + field + " from recv_" + old_msgid + " WHERE id IN (SELECT userid from linklog_" + old_msgid;
			    if(form.getLinkid() != null && !form.getLinkid().equals("")){
			    	if(Integer.parseInt(form.getLinkid()) > -1) query += " WHERE adid = " + form.getLinkid();
                }
				query +=  ") order by field1";
			    bean.setQuery(query);
			}else if(form.getResend_flag().equals("resend")){
				bean.setQuery("select " + field + " from recv_" + old_msgid);
			}
			//extended=999, resendnum+1
			mailService.updateMailResendNum(emsBean.getParentid(), emsBean.getResend_num());
			mailService.updateMailResend(old_msgid);
		}else{
			log.info("[SENDMAIL] Incorrect rectype format : {}", rectype);
			return false;
		}
		
		bean.setMsg_name(form.getMsg_name());
		bean.setRegdate(ImTimeUtil.getDateFormat(new Date(), "yyyyMMddHHmm"));
		
		// 예약시간 설정
		if("1".equals(form.getIs_reserve())){
			String reserve = ImUtility.changeDatesub(form.getReserv_day()) + form.getReserv_hour();
			String reserv_time = reserve + form.getReserv_min();
			// yyyyMMddHHmm 이 아닌경우는 false
			if(reserv_time != null && reserv_time.length() == 12){
				bean.setReserv_time(reserv_time);
			} else{
				log.info("[SENDMAIL] Incorrect ReservTime format : {}", reserv_time);
				return false;
			}
		}

		// 반응분석 종료일 설정 yyyyMMddHH
		String resp_time = ImUtility.changeDatesub(form.getResp_day()) + form.getResp_hour() + RESP_MIN;
		if(resp_time != null && resp_time.length() == 12){ // 2023030100
			bean.setResp_time(resp_time);
		} else{
			log.info("[SENDMAIL] Incorrect RespTime format : {}", resp_time);
			return false;
		}

		String content = form.getContent();

		// 페이지 보기에선 링크 추적해도 원래 링크로 나와야 url 접속 가능해서 페이지 보기용 본문내용 변수 하나 새로 선언
		String orgcontent = form.getContent();

		String ishtml = form.getIshtml();
		
		// 첨부파일 설정
		String att_keys = form.getAtt_keys();
		bean.setIsattach("0");
		if(StringUtils.isNotBlank(att_keys)){
			String att_key[] = att_keys.split(",");
			for(int i=0; i<att_key.length; i++) {
				if (!this.setAttachments(msgid, att_key[i])) {
					return false;
				}
			}
			// 첨부파일 링크로 변환
			content = this.attachToLink(content, ishtml, weburl, att_keys, msgid);
			bean.setIsattach("1");
		}
		
		bean.setCharset(form.getCharset());
		bean.setIshtml(ImStringUtil.parseInt(ishtml));
		bean.setIs_same_email(ImStringUtil.parseInt(form.getIs_same_email()));

		// 링크추적 처리
		String islink = form.getIslink();
		bean.setIslink(islink);
	/*	if (("1".equals(islink) || StringUtils.isNotEmpty(att_keys))&& StringUtils.isNotEmpty(content)) {	
			content = this.linkParsing(msgid, content, weburl, islink);
			content = this.mapParsing(msgid, content, weburl);
		}*/
		//islink가 0이어도 첨부파일은 linklog 테이블이 생성되어야함
		if(StringUtils.isNotEmpty(content)){
			if("1".equals(islink)){
				content = this.linkParsing(msgid, content, weburl, islink);
				content = this.mapParsing(msgid, content, weburl);
			}else{
				if(StringUtils.isNotEmpty(att_keys)){
					content = this.linkParsing(msgid, content, weburl, islink);
				}
			}
		}
		

		String encrypt_msgid = ImSecurityLib.encryptAESUrlSafe(ImbConstant.URL_AES_KEY, msgid);
		if ("1".equals(ishtml) && StringUtils.isNotEmpty(content)) {
			content = ImHtmlUtil.getTidyHtmlUTF(content);
			// 수신확인 url 추가
			String rcpt_url = weburl + ImbConstant.RCPT_URL;
			String sRcptCode = "\r\n<img id='xiwmail' width='0' height='0' border='0' loading='auto' src='" + rcpt_url + "?msgid=" + encrypt_msgid
					+ "&amp;rcode=[$ID$]'>";
			content = content.concat(sRcptCode);
			mailService.insertReceiptCountInfo(msgid);
		}

		// 수신거부 링크 처리
		if (StringUtils.isNotEmpty(content)) {
			String reject_url = weburl + ImbConstant.REJECT_URL + "?msgid=" + encrypt_msgid + "&amp;e=[$EMAIL$]";
			if("1".equals(ishtml)){
				content = StringUtils.replace(content, "[/REJECT]", "</a>");
				content = StringUtils.replace(content, "[REJECT]", "<a href=\"" + reject_url + "\" target=_blank>");
			} else{
				content = StringUtils.replace(content, "[/REJECT]", message.getMessage("E0554","링크") + ": " + reject_url);
				content = StringUtils.replace(content, "[REJECT]", "");
			}
		}

		Date now = new Date();
		String year = ImTimeUtil.getDateFormat(now, "yyyy");
		String month = ImTimeUtil.getDateFormat(now, "MM");
		String day = ImTimeUtil.getDateFormat(now, "dd");
		String msg_path = File.separator + year + File.separator + month + File.separator + day;

		this.saveMail(msgid, weburl, client_ip, content, form, msg_path);
		bean.setMsg_path(msg_path + File.separator + msgid + ".eml");

		/**
		 * 발송 상태  0:발송, 1:임시보관, 2:사본저장후발송
		 * */
		if("1".equals(state)){
			bean.setState(EmsBean.STATUS_DRAFT);
			// 임시보관된 메일이면 update처리
			if(isDraft){
				mailService.updateMsgInfo(msgid, orgcontent);
				mailService.updateMailData(bean);
			} else{
				mailService.insertMsgInfo(msgid, orgcontent);
				mailService.insertMailData(bean);
			}
		} else{
			bean.setState(EmsBean.STATUS_WAIT_SEND);
			if(isDraft){
				mailService.updateMsgInfo(msgid, orgcontent);
				mailService.updateMailData(bean);
			} else{
				mailService.insertMsgInfo(msgid, orgcontent);
				mailService.insertMailData(bean);
			}
		}

		// hc_msgid 테이블 생성
		hostCountService.createHostCountTable(msgid);
		
		return true;
	}
	
	/**
	 * eml 파일 생성
	 * @param msgid
	 * @param weburl
	 * @param client_ip
	 * @param content
	 * @param form
	 * */
	private void saveMail(String msgid, String weburl, String client_ip, String content, MailWriteForm form, String msg_path) throws Exception{
		String mailFrom = form.getMail_from();
		String reply_to = form.getReplyto();
		
		String mbox_path = ImbConstant.MSG_PATH;
		String fileName = msgid + ".eml";
		
		String filePath = mbox_path + msg_path;
		File fileDir = new File(filePath);
		if(!fileDir.exists()){
			fileDir.mkdirs();
		}

		String fromName = "";
		String fromEmail = "";
		String from_domain = "";
		String[] arrFrom = mailFrom.split("<");
		if (arrFrom.length > 1) {
			fromName = arrFrom[0];
			fromEmail = arrFrom[1].substring(0, arrFrom[1].length() - 1);
		} else {
			fromEmail = mailFrom;
		}
		String tempArr[] = fromEmail.split("@");
		if(tempArr != null){
			from_domain = tempArr[1];
		}
		
		String sReplytoAddr = null;        
		String[] arrReplyto = reply_to.split("<");        
		if(arrReplyto.length > 1){
			sReplytoAddr = arrReplyto[1].substring(0, arrReplyto[1].length()-1);
		} else {
			sReplytoAddr = reply_to;
		}
		
		SendMailData sendMailData = new SendMailData();
		sendMailData.setCharset(form.getCharset());
		sendMailData.setFromName(fromName);
		sendMailData.setFromEmail(fromEmail);
		sendMailData.setReply_to(sReplytoAddr);
		sendMailData.setTo("[$TO$]");
		sendMailData.setCharset(form.getCharset());
		sendMailData.setSubject(form.getMsg_name());
		if("1".equals(form.getIshtml())){
			sendMailData.setHtmlBody(content);
		} else{
			sendMailData.setTextBody(content);
		}
        sendMailData.setHeaders("X-Originating-IP", client_ip);
        sendMailData.setHeaders("X-WebSend", ImUtils.encodeBase64(mailFrom, "utf-8"));
        sendMailData.setMessageId("<"+msgid+"@"+from_domain+">"); // <msgid@from_domain> 형식
		
        String emlPath = filePath + File.separator + fileName;
        log.info("saveMail - {}", emlPath);
       	ImMessage mimeMessage = sendMailData.getMessage();
       	this.writeMimeFile(mimeMessage, emlPath);
	}
	
	/**
	 * 첨부파일 저장
	 * @param msgid
	 * @param att_key
	 * */
	public boolean setAttachments(String msgid, String att_key) throws Exception{
		UploadFileBean uploadFileBean = mailService.getUploadFileInfo(att_key);
		if(uploadFileBean == null){
			log.info("Upload TempFile Info is Null");
			return false;
		}
		String fkey = uploadFileBean.getFkey();
		
		// tempfile 경로에 해당 파일 있는지 확인
		String tempFilePath = ImbConstant.TEMPFILE_PATH + File.separator + uploadFileBean.getFilepath();
		File tempFile = new File(tempFilePath);
		if(!tempFile.exists()){
			log.info("Upload TempFile is not exist");
			return false;
		}
		
		Date now = new Date();
		String year = ImTimeUtil.getDateFormat(now, "yyyy");
		String month = ImTimeUtil.getDateFormat(now, "MM");
		String day = ImTimeUtil.getDateFormat(now, "dd");
		
		String dayPath = year + File.separator + month + File.separator + day;
		String filePath = ImbConstant.ATTACH_PATH + File.separator + dayPath;
		
        try{
        	// 실제 저장할 경로 생성
        	File realFile = new File(filePath);
        	if(!realFile.exists()){
	        	if(!realFile.mkdirs()){
	        		log.info("Make Attach Directory Fail");
	    			return false;
	        	}
        	}
        	
        	ImFileUtil.moveFile(tempFilePath, filePath + File.separator + fkey);
        } catch (NullPointerException ne) {
			log.error("setattachments error");
		}
        catch(Exception e){
			log.error("setattachments error");
        }
        // 만료일 및 첨부파일 정보 세팅
        // 만료일 ex) 3월 15일 등록, 만료일 : 14일 => 3월 29일 23:59:59 이 만료일
        int expire_day = ImbConstant.ATTACH_EXPIRE_DAY;
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.DATE, expire_day);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        
        AttachBean attachBean = new AttachBean();
        attachBean.setEkey(fkey);
        attachBean.setMsgid(msgid);
        attachBean.setFile_name(uploadFileBean.getFilename());
        attachBean.setFile_path(dayPath + File.separator + fkey);
        attachBean.setFile_size(String.valueOf(uploadFileBean.getFilesize()));
        attachBean.setRegdate(now);
        attachBean.setExpire_date(new Date(cal.getTimeInMillis()));
        mailService.insertAttachInfo(attachBean);
        // tempfile 삭제
        mailService.deleteUploadFile(fkey);
        
        return true;
	}
	
	/**
	 * 첨부파일을 본문내 링크로 변환한다.
	 * @param content
	 * @param ishtml
	 * @param weburl
	 * */
	public String attachToLink(String content, String ishtml, String weburl, String fkey, String msgid) throws Exception{
		String down_url = weburl + ImbConstant.DOWNLOAD_URL;
		String att_key[] = fkey.split(",");
		String sHead = "";
		// 테이블 head 생성
		StringBuffer sb = new StringBuffer();

		if ("1".equals(ishtml)) {
			sHead = sb.append("<div style=\"margin:20px 0px;\">\n")
					.append("<table style=\"width:100%; font-size:12px; border-collapse:collapse;border: 1px solid #d4d4d4;\">")
					.append("<thead>\r\n")
					.append("	<tr style=\"background-color:#ededed;\">\r\n")
					.append("		<th style=\" padding-left:10px;width:50%; text-align:left; height:30px;\">")
					.append(message.getMessage("E0485", "첨부파일") + "</th>\r\n")
					.append("		<th style=\" padding-left:10px;width:50%; text-align:left; height:30px;\"></th>\r\n")
					.append("	</tr>\r\n")
					.append("</thead><tbody>").toString();
		} else {
			sHead = "\n\n====================================================================================\n";
		}
		String sBody = "";

		for(int i=0; i<att_key.length; i++) {
			AttachBean bean = mailService.getAttachInfo(att_key[i], msgid);
			/*AttachBean bean = mailService.getAttachInfo(fkey, msgid);*/
			if (bean != null) {
				String fname = bean.getFile_name();
				Date expireDate = bean.getExpire_date();

				long fsize = Long.parseLong(bean.getFile_size());

				String encrypt_msgid = ImSecurityLib.encryptAESUrlSafe(ImbConstant.URL_AES_KEY, msgid);
				String encrypt_fkey = ImSecurityLib.encryptAESUrlSafe(ImbConstant.URL_AES_KEY, att_key[i]);

				// 테이블 본문 생성
				sb.setLength(0);
				if ("1".equals(ishtml)) {
					sBody += sb
							.append("<tr><td style=\"padding-left:10px;width:50%; height:30px; border-top: 1px solid #e4e4e4;\"><a href=\"")
							.append(down_url).append("?fkey=").append(encrypt_fkey)
							.append("&msgid=").append(encrypt_msgid)
							.append("\">").append(fname).append(" (")
							.append(ImUtils.myByteFormat(fsize)).append(")").append("</a></td>\n")
							.append("<td style=\"padding-left:10px;width:50%; height:30px; border-top: 1px solid #e4e4e4; padding-right:10px;text-align:right; \">"
									+ message.getMessage("E0486", "다운로드 기간") + ": ~")
							.append(ImTimeUtil.getDateFormat(expireDate, "yyyy-MM-dd")).append("</td></tr>\n").toString();
				} else {
					sBody += "\r\n* File Name : " + fname + "("
							+ ImUtils.myByteFormat(fsize) + ") \n  Download URL : " + down_url
							+ "?fkey=" + encrypt_fkey + "&msgid=" + encrypt_msgid + "\n";
				}
			}
		}

		String sFoot = "</tbody></table></div>\n";
		if ("0".equals(ishtml)) {
			sFoot = "====================================================================================\n";
		}

		sb.setLength(0);
		content = sb.append(content).append(sHead).append(sBody).append(sFoot).toString();
		
		return content;
	}
	
	/**
	 * 본문 내의 a태그를 찾아 파싱한다.
	 * @param msgid
	 * @param content
	 * @return
	 * */
	private String linkParsing(String msgid, String content, String weburl, String islink) throws Exception{
		String adurl = weburl + ImbConstant.AD_URL;
		int link_id = 0;
		List<LinkBean> linkList = new ArrayList<>();
        StringBuffer sb = new StringBuffer();
        int prevEnd = -1; // 본문 치환시 위치를 저장하는 임시 변수
        String linkImg = "";
        
		Pattern p = Pattern.compile("<a\\s+(.*?)href\\s*=\\s*[\"|'|]?(.*?)[\"|'|>]", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(content);
		
        while (m.find()) {
        	String orgLink = m.group(2).trim();
        	
			if(StringUtils.equals(islink, "0")){	//islink가 0일 때(링크추적을 하지 않을 때) 첨부파일이 있을 경우에만 while문이 계속되도록 처리
				if(!orgLink.contains("attach.do")){
					continue;
				}
			}
        	
        	if (orgLink.length() < 1) {
                continue;
            }

            // Skip links that are just page anchors.
            if (orgLink.charAt(0) == '#') {
                continue;
            }

            // Skip mailto links.
            if (orgLink.indexOf("mailto:") != -1) {
                continue;
            }

            // Skip JavaScript links.
            if (orgLink.toLowerCase().indexOf("javascript") != -1) {
                continue;
            }

            // http:// 로 시작하는 url만..
            if (orgLink.toLowerCase().indexOf("http://") == -1 && orgLink.toLowerCase().indexOf("https://") == -1) {
                continue;
            }
            
            /*
             * 링크 url에 [#FIELD 가 포함되어 있으면 링크추적하지 않고 건너뛴다.
             * 만일, 링크추적을 하게되면 개인화된 정보를 url에 삽입할 수 없음.
             */ 
            if(orgLink.indexOf("[#FIELD") != -1){
                continue;
            }
            if(orgLink.indexOf("[$ID$]") != -1){
                continue;
            }
            if(orgLink.indexOf("[$EMAIL$]") != -1){
                continue;
            }

			// 링크(ex: www.naver.com, www.google.com)
            orgLink = orgLink.replaceAll("&amp;","&");
            
            String encrypt_msgid = ImSecurityLib.encryptAESUrlSafe(ImbConstant.URL_AES_KEY, msgid);
            String encrypt_link_id = ImSecurityLib.encryptAESUrlSafe(ImbConstant.URL_AES_KEY, String.valueOf(link_id));
            String encrypt_url = ImSecurityLib.encryptAESUrlSafe(ImbConstant.URL_AES_KEY, orgLink);

			String adLink = adurl + "?msgid="+encrypt_msgid+"&adid="+encrypt_link_id+"&rcode=[$ID$]&e=[$EMAIL$]&url="+encrypt_url;

            if(link_id == 0){
                sb.append( content.substring( 0, m.start(2)) );
            } else {
                String appendStr = content.substring( prevEnd, m.start(2));    
                if(appendStr.startsWith("\"") || appendStr.startsWith("'")){
                    appendStr =  appendStr.substring(0, 1) + " target=_blank " + appendStr.substring(1);
                } else {
                    appendStr =  " target=_blank " + appendStr;
                }
                sb.append( appendStr );
                
                try {
                	linkImg = this.linkImage(appendStr.substring(0, appendStr.toLowerCase().indexOf("</a>")));
                }catch (NullPointerException ne) {
					log.info("Fail search image tag in aTag");
					linkImg = "";
				}
                catch(Exception e){
                	log.info("Fail search image tag in aTag");
                	linkImg = "";
                }
            }
            sb.append(adLink);
            
            LinkBean bean = new LinkBean();
            bean.setMsgid(msgid);
            bean.setLinkid(link_id);
            bean.setLink_name("link"+link_id);
            bean.setLink_url(orgLink);
            if(StringUtils.isNotBlank(linkImg)){
            	bean.setLink_img(linkImg);
            }
            
            linkList.add(bean);
            
            prevEnd =  m.end(2);
            link_id ++;
        }

        if(link_id > 0){
            String appendStr = content.substring(prevEnd);
            if(appendStr.startsWith("\"") || appendStr.startsWith("'")){
                appendStr =  appendStr.substring(0, 1) + " target=_blank " + appendStr.substring(1);
            } else {
                appendStr =  " target=_blank " + appendStr;
            }
            sb.append( appendStr );
            linkImg =  this.linkImage(appendStr.substring(0, appendStr.toLowerCase().indexOf("</a>")));
            
            // 마지막 append할 때의 a태그 안에 img태그 검사후 있으면 변경처리
            if(linkList.size() > 0 && StringUtils.isNotBlank(linkImg)){
            	LinkBean tempBean = linkList.get(linkList.size()-1);
            	tempBean.setLink_img(linkImg);
            	linkList.remove(linkList.size()-1);
            	linkList.add(tempBean);
            }
            
            // link_info 테이블에 insert
            for(int i=0; i<linkList.size(); i++){
            	 linkService.insertLinkInfo(linkList.get(i));
            }
            
            linkService.createLinkLogTable(msgid);
            
            return sb.toString();
        }
        return content;
	}
	
	
	/**
	 * 본문내의 area 태그를 찾아 파싱한다.
	 * @param msgid
	 * @param content
	 * */
	private String mapParsing(String msgid, String content, String weburl) throws Exception{
		String adurl = weburl + ImbConstant.AD_URL;
		
		Pattern p = Pattern.compile("<area\\s+(.*?)href\\s*=\\s*[\"|'|]?(.*?)[\"|'|>]", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(content);
		
		int link_id = 0;
		List<LinkBean> linkList = new ArrayList<>();
        StringBuffer sb = new StringBuffer();
        int prevEnd = -1;
        
        while (m.find()) {
        	String orgLink = m.group(2).trim();
        	
        	if (orgLink.length() < 1) {
                continue;
            }

            // Skip links that are just page anchors.
            if (orgLink.charAt(0) == '#') {
                continue;
            }

            // Skip mailto links.
            if (orgLink.indexOf("mailto:") != -1) {
                continue;
            }

            // Skip JavaScript links.
            if (orgLink.toLowerCase().indexOf("javascript") != -1) {
                continue;
            }

            // http:// 로 시작하는 url만..
            if (orgLink.toLowerCase().indexOf("http://") == -1 && orgLink.toLowerCase().indexOf("https://") == -1) {
                continue;
            }
            
            /*
             * 링크 url에 [#FIELD 가 포함되어 있으면 링크추적하지 않고 건너뛴다.
             * 만일, 링크추적을 하게되면 개인화된 정보를 url에 삽입할 수 없음.
             */ 
            if(orgLink.indexOf("[#FIELD") != -1){
                continue;
            }
            
            orgLink = orgLink.replaceAll("&amp;","&");
            
            String encrypt_msgid = ImSecurityLib.encryptAESUrlSafe(ImbConstant.URL_AES_KEY, msgid);
            String encrypt_link_id = ImSecurityLib.encryptAESUrlSafe(ImbConstant.URL_AES_KEY, String.valueOf(link_id));
            String encrypt_url = ImSecurityLib.encryptAESUrlSafe(ImbConstant.URL_AES_KEY, orgLink);
            String adLink = adurl + "?msgid="+encrypt_msgid+"&adid="+encrypt_link_id+"&rcode=[$ID$]&e=[$EMAIL$]&url="+encrypt_url;
            
            if(link_id == 0){
                sb.append( content.substring( 0, m.start(2)) );
            } else {
            	sb.append( content.substring( prevEnd, m.start(2)) );
            }
            sb.append(adLink);
            
            LinkBean bean = new LinkBean();
            bean.setMsgid(msgid);
            bean.setLinkid(link_id);
            bean.setLink_name("link"+link_id);
            bean.setLink_url(orgLink);
            
            linkList.add(bean);
            
            prevEnd = m.end(2);
            link_id ++;
        }
        
        if(link_id > 0){
            sb.append( content.substring(prevEnd) );
            
            // link_info 테이블에 insert
            for(int i=0; i<linkList.size(); i++){
            	linkService.insertLinkInfo(linkList.get(i));
            }
            
            // linklog_msgid 테이블 생성
            linkService.createLinkLogTable(msgid);
            return sb.toString();
        }
		return content;
	}
	
	/**
	 * a 태그 안에서 img 태그를 찾는다.
	 * @param body
	 * @return
	 * */
	private String linkImage(String body){
		String linkImg = "";
	    
        Pattern p = Pattern.compile("<img\\s+(.*?)src\\s*=\\s*[\"|'|]?(.*?)[\"|'|>]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(body);
        
        if (m.find()) {
            linkImg = m.group(2).trim();
        }
        
        return linkImg;
	}
	
	/**
	 * eml파일 생성
	 * @param message
	 * @param emlPath
	 * */
	private void writeMimeFile(ImMessage message, String emlPath) throws Exception{
		BufferedOutputStream bos = null;
        try {
			log.info("writeMimeFile: {}", emlPath);
            //bos =  new BufferedOutputStream(ImIOCipherUtil.getFileOutputStream(emlPath),4096);
			bos =  new BufferedOutputStream(new FileOutputStream(emlPath));
			message.setContentEncoding(ImMessage.ENC_7BIT);
            message.makeMimeFile(bos);
        }finally{
            if (bos != null) {
                try {
                    bos.close();
                }catch (IOException fe) {

				}
                catch (Exception e) {
                }
            }
        }
	}

	/**
	 * 에러메일 재발신 버튼 클릭 시 메일 데이터 재가공하여 테이블에 insert (발송)
	 * @param old_msgid
	 * @param userid
	 * @return
	 * */
	public boolean insertErrorResendMailData(String old_msgid, HttpServletRequest request) throws Exception{
		String msgid = ImUtils.makeKeyNum(24);
		String encrypt_msgid = ImSecurityLib.encryptAESUrlSafe(ImbConstant.URL_AES_KEY, msgid);
		String client_ip = request.getRemoteAddr();
		MailWriteForm form = new MailWriteForm();
		String weburl = HttpRequestUtil.getWebURL(request);

		//get ims_msg_info 해서 다시 insert - 첨부파일, 링크추적, 수신확인, 수신거부...
		EmsBean emsBean = mailService.noUseridGetMailData(old_msgid);
		String old_contents = emsBean.getContents();
		String mbox_path = ImbConstant.MSG_PATH + emsBean.getMsg_path();
		String content = this.getMailBody(mbox_path);

		int index = content.indexOf("msgid=");
		if(index > 0){
			String result = content.substring(index+6, index+49);
			content = content.replace(result, encrypt_msgid);
		}

		//수신자 query
		String fetchFileds = this.getResendQueryColumn(old_msgid);
		emsBean.setQuery("select " + fetchFileds + " from recv_" + old_msgid + " where errcode IN ('902','903','904','906','910') order by field1");

		//링크추적 링크 테이블...
		//imb_link_info, linklog_msgid 가져와서 msgid만 바꾸기 insert, create / imb_link_count 새로 insert
		List<LinkBean> linkList = linkService.getLinkInfo(old_msgid);

		//첨부파일일 경우 getAttachInfo 해서 거기 있는 fkey를 암호화, link url에 넣어주기
		if(linkList != null) {
			int i = 0;
			for (LinkBean bean : linkList) {
				bean.setMsgid(msgid);
				String oldLink = bean.getLink_url();
				if (StringUtils.isNotEmpty(oldLink) && oldLink.contains(ImbConstant.DOWNLOAD_URL)) {
					//첨부파일 처리
					//fkey와 msgid로 imb_emsattach에 존재하는지 확인, 없다면 continue
					index = oldLink.indexOf("fkey=");
					String ekey = oldLink.substring(index+5, index+48);
					ekey = ImSecurityLib.decryptAES(ImbConstant.URL_AES_KEY, ekey);
					String encrypt_old_url = ImSecurityLib.encryptAESUrlSafe(ImbConstant.URL_AES_KEY, oldLink);
					AttachBean attachBean = mailService.getAttachInfo(ekey, old_msgid);
					if(attachBean == null){
						String error_url = ImSecurityLib.encryptAESUrlSafe(ImbConstant.URL_AES_KEY, weburl + "/error/no-resource.do");
						content = ImStringUtil.replace(content, encrypt_old_url, error_url);
						continue;
					}

					//파일 복제 로직, 서버에서 파일이 삭제되어 없다면 continue
					String file_path = mailService.copyAttach(attachBean);

					if(file_path == null){
						String error_url = ImSecurityLib.encryptAESUrlSafe(ImbConstant.URL_AES_KEY, weburl + "/error/no-resource.do");
						content = ImStringUtil.replace(content, encrypt_old_url, error_url);
						continue;
					}

					//파일 복제 성공했으면 imb_emsattach에 insert
					String fkey = ImUtils.makeKeyNum(24);
					attachBean.setEkey(fkey);
					attachBean.setMsgid(msgid);
					attachBean.setFile_path(file_path);
					Date now = new Date();
					attachBean.setRegdate(now);
					mailService.insertAttachInfo(attachBean);

					//content에서 이전 다운로드 url을 새로운 fkey와 msgid가 적용된 다운로드 url로 수정
					String encrypt_fkey = ImSecurityLib.encryptAESUrlSafe(ImbConstant.URL_AES_KEY, attachBean.getEkey());
					bean.setLink_url(weburl + ImbConstant.DOWNLOAD_URL + "?fkey=" + encrypt_fkey + "&msgid=" + encrypt_msgid);
					String encrypt_url = ImSecurityLib.encryptAESUrlSafe(ImbConstant.URL_AES_KEY, bean.getLink_url());

					content = ImStringUtil.replace(content, encrypt_old_url, encrypt_url);
				}
				linkService.insertLinkInfo(bean);
				i++;
			}
			//imb_link_info에 1개 이상 insert 했다면 linklog_msgid 테이블 생성
			if(i >= 1) linkService.createLinkLogTable(msgid);
		}

		//수신확인 url imb_receipt_count insert
		mailService.insertReceiptCountInfo(msgid);

		//기존 emsmain 처리
		mailService.updateMailResendNum(emsBean.getParentid(), emsBean.getResend_num());
		mailService.updateMailResend(old_msgid);


		//save mail - eml 저장
		Date now = new Date();
		String year = ImTimeUtil.getDateFormat(now, "yyyy");
		String month = ImTimeUtil.getDateFormat(now, "MM");
		String day = ImTimeUtil.getDateFormat(now, "dd");
		String msg_path = File.separator + year + File.separator + month + File.separator + day;

		form.setMail_from(emsBean.getMail_from());
		form.setReplyto(emsBean.getReplyto());
		form.setCharset(emsBean.getCharset());
		form.setMsg_name(emsBean.getMsg_name());
		form.setIshtml(Integer.toString(emsBean.getIshtml()));

		this.saveMail(msgid, weburl, client_ip, content, form, msg_path);
		emsBean.setMsg_path(msg_path + File.separator + msgid + ".eml");
		emsBean.setMsgid(msgid);
		emsBean.setRegdate(ImTimeUtil.getDateFormat(new Date(), "yyyyMMddHHmm"));
		emsBean.setRectype("4");
		emsBean.setExtended("999");
		emsBean.setState("000");
		emsBean.setResend_num( emsBean.getResend_num()+1 );
		emsBean.setResend_step( emsBean.getResend_step()+1 );

		//imb_emsmail 기존 data update & 새 data insert
		mailService.insertMsgInfo(msgid, old_contents);
		mailService.insertMailData(emsBean);

		//hc 테이블 생성
		hostCountService.createHostCountTable(msgid);

		return true;
	}

	public String getMailBody(String emlPath) throws Exception {
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props);
		ImMessage message = new ImMessage(session);
		message.setDefaultCharset("euc-kr");
		message.parseMimeFile(emlPath);

		return message.getHtml();
	}

	public String getResendQueryColumn(String msgid) throws PersistenceException, SQLException, Exception {
		SqlSession session  = null;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		String fetchFields = "";

		try {
			session = ImDatabaseConnectionEx.getConnection();
			conn = session.getConnection();
			String sql = "select * from recv_" + msgid;
			ps = conn.prepareStatement(sql);
			ps.setMaxRows(1);
			rs = ps.executeQuery();

			ResultSetMetaData rsmd = rs.getMetaData();
			int fieldCount = rsmd.getColumnCount();
			StringBuffer sbColumn = new StringBuffer();

			for (int i = 1; i <= fieldCount; i++) {
				if(rsmd.getColumnName(i).toLowerCase().startsWith("field")){
					if(sbColumn.length() > 0) sbColumn.append(",");
					sbColumn.append(rsmd.getColumnName(i));
				}
			}

			fetchFields = sbColumn.toString();
		} catch (PersistenceException | SQLException e){
			log.error("SendMailService.getResendQueryColumn error: {}", e.getMessage());
			throw e;
		} finally{

			try{ if( ps != null ) ps.close(); }catch(Exception e){}
			try{ if( conn != null ) conn.close(); }catch(Exception e){}
			try { if(session != null) session.close(); } catch(Exception ee){}
		}

		return fetchFields;
	}
}

package com.imoxion.sensems.web.service;

import java.io.*;
import java.util.Date;
import java.util.List;

import com.imoxion.common.util.ImFileUtil;
import com.imoxion.common.util.ImTimeUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.imoxion.common.util.ImUtils;
import com.imoxion.sensems.web.beans.AttachBean;
import com.imoxion.sensems.web.beans.EmsBean;
import com.imoxion.sensems.web.beans.UploadFileBean;
import com.imoxion.sensems.web.beans.UserInfoBean;
import com.imoxion.sensems.web.common.ImbConstant;
import com.imoxion.sensems.web.database.mapper.MailMapper;
import com.imoxion.sensems.web.form.MailWriteForm;
import org.xbill.DNS.NULLRecord;

/**
 * 메일 작성 관련 서비스. create by zpqdnjs 2021-03-12
 * */
@Service
public class MailService {
	protected Logger log = LoggerFactory.getLogger(MailService.class);
	
	@Autowired
	private MailMapper mailMapper;
	
	/**
	 * 메일 작성폼 생성
	 * @param userInfo
	 * @return form
	 * */
	public MailWriteForm getWriteForm(UserInfoBean userInfo, String msgid) throws Exception{
		MailWriteForm form = new MailWriteForm();
		String userid = userInfo.getUserid();
		boolean isExist = false;
		EmsBean bean = null;
		
		if(StringUtils.isNotBlank(msgid)){
			bean = this.noUseridGetMailData(msgid);
			if(bean != null){
				isExist = true;
			}
		} 
		
		if(isExist){
			form.setCategoryid(bean.getCategoryid());
			form.setMsgid(msgid);
			form.setUserid(userid);
			form.setMail_from(bean.getMail_from());
			form.setReplyto(bean.getReplyto());
			form.setMsg_name(bean.getMsg_name());
			form.setRecid(bean.getRecid());
			form.setRecname(bean.getRecname());
			form.setRectype(bean.getRectype());
			
			String reserv_time = bean.getReserv_time();
			if(StringUtils.isNotBlank(reserv_time)){
				form.setReserv_day(reserv_time.substring(0,8));
				form.setReserv_hour(reserv_time.substring(8,10));
				form.setReserv_min(reserv_time.substring(10,12));
				form.setIs_reserve("1");
			}
			
			String resp_time = bean.getResp_time();
			if(StringUtils.isNotBlank(resp_time)){
				form.setResp_day(resp_time.substring(0,8));
				form.setResp_hour(resp_time.substring(8,10));
			}
			
			form.setCharset(bean.getCharset());
			form.setIshtml(String.valueOf(bean.getIshtml()));
			form.setIs_same_email(String.valueOf(bean.getIs_same_email()));
			form.setIslink(bean.getIslink());
			form.setState("0");
			form.setContent(bean.getContents());
		} else{
			String from = userInfo.getEmail();
	
			if (StringUtils.isNotEmpty(userInfo.getName())) {
				from = userInfo.getName() + "<" + from + ">";
			}
	
			form.setMail_from(from);
			form.setReplyto(userInfo.getEmail());
		}
		return form;
    }
	
	/**
	 * 메일 본문 imb_msg_info insert
	 * @param msgid
	 * @param content
	 * */
	public void insertMsgInfo(String msgid, String content) throws Exception{
		mailMapper.insertMsgInfo(msgid, content);
	}
	
	/**
	 * 첨부파일 서버에 저장 및 테이블 insert
	 * @param file
	 * */
	public String setAttach(MultipartFile file) throws Exception{
		String ekey = ImUtils.makeKeyNum(24);
		//파일을 저장할 경로 체크 및 디렉토리 없을 경우 생성
		String tempPath = ImbConstant.TEMPFILE_PATH ;
		
		File tempDir  = new File(tempPath);
        if(!tempDir.exists()){
            tempDir.mkdirs();
        }
        
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try{
        	bis = new BufferedInputStream(file.getInputStream());
        	bos = new BufferedOutputStream(new FileOutputStream(tempPath + File.separator + ekey));
        	
        	byte[] buffer = new byte[8192];
            int read = -1;
            while ((read = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, read);
            }
        } catch (IOException ie) {
			log.error("setattach IO error");
		} catch(Exception e){
			log.error("setattach error");
        } finally{
        	if(bis!=null) bis.close();
        	if(bos!=null) bos.close();
        }
        
        UploadFileBean bean = new UploadFileBean();
        bean.setFkey(ekey);
        bean.setFilename(file.getOriginalFilename());
        bean.setFilepath(File.separator + ekey);
        bean.setRegdate(new Date());
        bean.setFilesize(file.getSize());
        mailMapper.insertUploadFile(bean);
        
        return ekey;
	}
	
	/**
	 * 임시 업로드 파일 정보를 가져온다
	 * @param fkey
	 * */
	public UploadFileBean getUploadFileInfo(String fkey) throws Exception{
		return mailMapper.getUploadFileInfo(fkey);
	}
	
	/**
	 * 임시 업로드 파일 삭제
	 * @param fkey
	 * */
	public void deleteUploadFile(String fkey) throws Exception{
		mailMapper.deleteUploadFile(fkey);
	}
	
	/**
	 * 첨부파일 정보 insert
	 * @param bean
	 * */
	public void insertAttachInfo(AttachBean bean) throws Exception{
		mailMapper.insertAttachInfo(bean);
	}
	
	/**
	 * 메일 데이터 insert ( imb_emsmain )
	 * @param bean
	 * */
	public void insertMailData(EmsBean bean) throws Exception{
		mailMapper.insertMailData(bean);
	}
	
	/**
	 * 메일 본문 imb_msg_info update 
	 * @param msgid
	 * @param content
	 * */
	public void updateMsgInfo(String msgid, String content) throws Exception{
		mailMapper.updateMsgInfo(msgid, content);
	}
	
	/**
	 * 메일 데이터 update
	 * @param bean 
	 * */
	public void updateMailData(EmsBean bean) throws Exception{
		mailMapper.updateMailData(bean);
	}
	
	/**
	 * 메일 정보를 가져온다
	 * @param msgid
	 * @param userid
	 * */
	public EmsBean getMailData(String msgid, String userid) throws Exception{
		return mailMapper.getMailData(msgid, userid);
	}

	public EmsBean noUseridGetMailData(String msgid) throws Exception{
		return mailMapper.noUseridGetMailData(msgid);
	}

	/**
	 * 메일 본문 정보를 가져온다
	 * @param msgid
	 * */
	public EmsBean getMailcontentData(String msgid) throws Exception{
		return mailMapper.getMailcontentData(msgid);
	}
	
	/**
	 * 첨부파일 정보를 가져온다
	 * @param ekey
	 * @param msgid
	 * */
	public AttachBean getAttachInfo(String ekey, String msgid) throws Exception{
		return mailMapper.getAttachInfo(ekey, msgid);
	}

	/**
	 * 첨부파일 정보를 모두 가져온다
	 * @param msgid
	 * */
	public List<AttachBean> getAllAttachInfo(String msgid) throws Exception{
		return mailMapper.getAllAttachInfo(msgid);
	}
	
	/**
	 * 반응분석 종료일을 구한다.
	 * @param msgid
	 * */
	public String getRespTime(String msgid) throws Exception{
		return mailMapper.getRespTime(msgid);
	}
	
	/**
	 * receipt_count 테이블의 수신확인 카운트 update
	 * @param msgid
	 * */
	public void updateReceiptCount(String msgid) throws Exception{
		mailMapper.updateReceiptCount(msgid);
	}
	
	/**
	 * receipt_count 테이블에 데이터 insert
	 * @param msgid
	 * */
	public void insertReceiptCountInfo(String msgid) throws Exception{
		mailMapper.insertReceiptCountInfo(msgid);
	}

	/**
	 * 메일 재발송 정보 업데이트
	 * @param msgid
	 * */
	public void updateMailResend(String msgid) throws Exception{
		mailMapper.updateMailResend(msgid);
	}

	/**
	 * 메일 재발송 num 업데이트
	 * @param msgid
	 * @param resend_num
	 * */
	public void updateMailResendNum(String msgid, int resend_num) throws Exception{
		mailMapper.updateMailResendNum(msgid, resend_num);
	}

	/**
	 * 서버의 첨부파일 복제하여 저장
	 * @param bean
	 * */
	public String copyAttach(AttachBean bean) throws Exception{
		Date now = new Date();
		String year = ImTimeUtil.getDateFormat(now, "yyyy");
		String month = ImTimeUtil.getDateFormat(now, "MM");
		String day = ImTimeUtil.getDateFormat(now, "dd");

		String dayPath = year + File.separator + month + File.separator + day;
		String fkey = ImUtils.makeKeyNum(24);
		String inFilePath = ImbConstant.ATTACH_PATH + File.separator + bean.getFile_path();
		String outFilePath = ImbConstant.ATTACH_PATH + File.separator + dayPath;

		try{
			File realFile = new File(inFilePath);
			if(realFile.exists()) {
				// 실제 저장할 경로 생성
				realFile = new File(outFilePath);
				if (!realFile.exists()) {
					if (!realFile.mkdirs()) {
						log.info("Make Attach Directory Fail");
					}
				}
				ImFileUtil.copyFile(inFilePath, outFilePath + File.separator + fkey);
			}else{
				return null;
			}
		}catch (NullPointerException ne) {
			log.error("readfile error");
		}
		catch(Exception e){
			log.error("readfile error");
		}
		return dayPath + File.separator + fkey;
	}

}

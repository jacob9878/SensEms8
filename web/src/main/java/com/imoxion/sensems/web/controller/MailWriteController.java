package com.imoxion.sensems.web.controller;


import java.util.*;

import javax.mail.Session;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.imoxion.common.mail.ImMessage;
import com.imoxion.common.util.ImFileUtil;
import com.imoxion.sensems.web.beans.EmsBean;
import com.imoxion.sensems.web.beans.RecvMessageIDBean;
import com.imoxion.sensems.web.beans.TestSendBean;
import com.imoxion.sensems.web.form.ActionLogForm;
import com.imoxion.sensems.web.service.*;
import com.imoxion.sensems.web.util.HttpRequestUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.james.mime4j.io.LimitedInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.imoxion.common.util.ImUtils;
import com.imoxion.sensems.common.logger.ErrorTraceLogger;
import com.imoxion.sensems.web.beans.UserInfoBean;
import com.imoxion.sensems.web.common.ImbConstant;
import com.imoxion.sensems.web.database.domain.ImbAttachRestrict;
import com.imoxion.sensems.web.database.domain.ImbCategoryInfo;
import com.imoxion.sensems.web.form.MailWriteForm;

import net.sf.json.JSONObject;

@Controller
@RequestMapping("mail/write")
public class MailWriteController {
	protected Logger log = LoggerFactory.getLogger(MailWriteController.class);

	@Autowired
	private CategoryService categoryService;

	@Autowired
    private MessageSourceAccessor message;

	@Autowired
	private MailService mailService;

	@Autowired
	private SendMailService sendMailService;

	@Autowired
	private AttachRestrictService attachRestrictService;

	@Autowired
	private ActionLogService actionLogService;

	@Autowired
	private SendResultService sendResultService;

	@Autowired
	private TestSendService testSendService;
	private RecvMessageIDBean recvMessageIDBean;

	@RequestMapping(value = "form.do", method = RequestMethod.GET)
	public String layoutWriteForm(HttpSession session, @ModelAttribute("MailWriteForm") MailWriteForm form,
				@RequestParam(value="msgid", required=false) String msgid, ModelMap model) throws Exception {
		UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
		String userid = null;

		String permission = userSessionInfo.getPermission();

		try{
	        form = mailService.getWriteForm(userSessionInfo, msgid);

			/** 발송분류 목록을 권한에 따라 처리 */
	        List<ImbCategoryInfo> categoryList = null;

	        if (UserInfoBean.UTYPE_NORMAL.equals(permission)) {
                userid = userSessionInfo.getUserid(); // 일반 사용자인 경우 userid가 필요하므로 userid를 처리
                categoryList = categoryService.getUserCategory(userid);
            } else {
                categoryList = categoryService.getUserCategory(userid);
            }

	        Map<String, String> hourList = new LinkedHashMap<>();
	        for (int i = 0; i <= 23; i++) {
	            String s = ImUtils.checkDigit(i);
	            hourList.put(s, message.getMessage("E0543", new Object[]{s}, "{0}시"));
	        }
	        model.addAttribute("hourList", hourList);

	        Map<String, String> minuteList = new LinkedHashMap<>();
	        for (int i = 0; i <= 11; i++) {
	            int j = i * 5; // 5분 간격 설정
	            String s = ImUtils.checkDigit(j);
	            minuteList.put(s, message.getMessage("E0544", new Object[]{s}, "{0}분"));
	        }
	        model.addAttribute("minuteList", minuteList);

	        int isDraft = 0;
	        if(StringUtils.isNotBlank(form.getMsgid())){
	        	isDraft = 1;
	        }
			ArrayList<ImbAttachRestrict> rList = new ArrayList<>();
			List<ImbAttachRestrict> restrictList = attachRestrictService.getExtInfo();
			if (restrictList != null) {
				for (int j = 0; j < restrictList.size(); j++) {
					ImbAttachRestrict restrict = restrictList.get(j);
					rList.add(restrict);
				}

			}

	        model.addAttribute("restrict", rList);
	        model.addAttribute("isDraft", isDraft);
	        model.addAttribute("MailWriteForm", form);
	        model.addAttribute("categoryList", categoryList);
	        model.addAttribute("maxAttachCount", ImbConstant.ATTACH_MAX_COUNT);
	        model.addAttribute("maxAttachSize", ImbConstant.ATTACH_MAX_SIZE);
		}catch (NullPointerException ne) {
			String errorId = ErrorTraceLogger.log(ne);
			log.error("Get Mail WriteForm ne error : {}",errorId);
		}
		catch(Exception e){
			String errorId = ErrorTraceLogger.log(e);
			log.error("Get Mail WriteForm error : {}",errorId);
		}
		return "/mail/write";
	}

	@RequestMapping(value="uploadFile.json", method=RequestMethod.POST)
	@ResponseBody
	public String uploadAttach(HttpSession session, MultipartHttpServletRequest request){
		JSONObject result = new JSONObject();
		try{
			List<MultipartFile> fileList = request.getFiles("file");

			if (fileList == null || fileList.isEmpty()) {
				result.put("result", false);
				result.put("message", message.getMessage("E0490","파일 업로드에 실패하였습니다."));
				return result.toString();
			}

			int max_count = ImbConstant.ATTACH_MAX_COUNT;
			int fileList_size = fileList.size();
			if(fileList_size> max_count){
				result.put("result", false);
				result.put("message", message.getMessage("E0762", new Object[]{max_count}, "첨부가능한 파일 개수는 {0}개 입니다."));
				return result.toString();
			}

			int max_size = ImbConstant.ATTACH_MAX_SIZE;
			long temp_max_size = max_size * 1024 * 1024;

			for(int i = 0; i < fileList_size; i++) {
				if (fileList.get(i).getSize() > temp_max_size) {
					result.put("result", false);
					result.put("message", message.getMessage("E0492", new Object[]{max_size}, "첨부가능한 최대 크기는 {0}MB 입니다."));
					return result.toString();
				}

				List<ImbAttachRestrict> restrictList = attachRestrictService.getExtInfo();
				if (restrictList != null) {
					String filename = fileList.get(i).getOriginalFilename().toLowerCase();
					for (int j = 0; j < restrictList.size(); j++) {
						ImbAttachRestrict restrict = restrictList.get(j);
						if (restrict != null && filename.indexOf(restrict.getExt()) > -1) {
							result.put("result", false);
							result.put("message", message.getMessage("E0491", "제한된 첨부파일 확장자입니다."));
							return result.toString();
						}
					}
				}
			}

			String att_keys = "";
			for(int i = 0; i < fileList_size; i++){
				String temp = mailService.setAttach(fileList.get(i));
				if(i == 0) att_keys += temp; else att_keys += ',' + temp;
			}

			/*String att_keys = mailService.setAttach(file);*/
			result.put("result", true);
			result.put("att_keys", att_keys);
		}catch (NullPointerException ne) {
			String errorId = ErrorTraceLogger.log(ne);
			log.error("Attach Upload ne Error : {}", errorId);
			result.put("result", false);
			result.put("message", message.getMessage("E0060","작업중 오류가 발생하였습니다."));
			return result.toString();
		}
		catch(Exception e){
			String errorId = ErrorTraceLogger.log(e);
			log.error("Attach Upload Error : {}", errorId);
			result.put("result", false);
			result.put("message", message.getMessage("E0060","작업중 오류가 발생하였습니다."));
            return result.toString();
		}
		return result.toString();
	}

	/**
	 * 메일 발송
	 * @param request
	 * @param session
	 * @param form
	 * */
	@RequestMapping(value = "write.json", method = RequestMethod.POST)
	@ResponseBody
	public String mailWrite(HttpServletRequest request, HttpSession session, @ModelAttribute("MailWriteForm") MailWriteForm form){
		UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
		JSONObject result = new JSONObject();

		if(form == null){
			result.put("result", false);
			result.put("message", message.getMessage("E0489","필수값이 누락되었습니다."));
			return result.toString();
		}
		/**
		 * 발송 상태  0:발송, 1:임시보관, 2:사본저장후발송	//사본 저장 후 발송은 제거
		 * */
		String state = form.getState();
		try{
			form.setUserid(userSessionInfo.getUserid());
			// 사본저장 후 발송인 경우 한건은 임시보관, 한건은 발송처리
			if("1".equals(state)){
				if(!sendMailService.insertDraftMailData(form)){
					result.put("result", false);
					result.put("message", message.getMessage("E0493","임시보관에 실패하였습니다."));
					return result.toString();
				}
			} else{
				/*if("2".equals(state)){
					if(!sendMailService.insertDraftMailData(form)){
						result.put("result", false);
						result.put("message", message.getMessage("E0494","사본저장 및 발송에 실패하였습니다."));
						return result.toString();
					}
					// 한건은 임시저장 한건은 발송처리를 위해 msgid 초기화
					form.setMsgid("");
				}*/
				if(!sendMailService.insertMailData(form, request)){
					/*if("2".equals(state)){
						result.put("result", false);
						result.put("message", message.getMessage("E0494","사본저장 및 발송에 실패하였습니다."));
						return result.toString();
					} else{*/
						result.put("result", false);
						result.put("message", message.getMessage("E0495","발송에 실패하였습니다."));
						return result.toString();
					/*}*/
				}
			}
			result.put("result", true);

			//log insert start
			ActionLogForm logForm = new ActionLogForm();
			logForm.setIp(HttpRequestUtil.getRemoteAddr(request));
			logForm.setUserid(userSessionInfo.getUserid());
			logForm.setMenu_key("B101");
			if(form.getRectype().equals("1")){
				logForm.setParam("주소록 : " + form.getRecname() + " / 제목 : " + form.getMsg_name() );
			} else if (form.getRectype().equals("3")){
				logForm.setParam("수신그룹 : " + form.getRecname() + " / 제목 : " + form.getMsg_name() );
			}
			actionLogService.insertActionLog(logForm);

		}catch (NullPointerException ne) {
			String errorId = ErrorTraceLogger.log(ne);
			log.error("Mail Write Error : {}",errorId);
			result.put("result", false);
			result.put("message", message.getMessage("E0060","작업중 오류가 발생하였습니다."));
			return result.toString();
		}
		catch(Exception e){
			String errorId = ErrorTraceLogger.log(e);
			log.error("Mail Write Error : {}",errorId);
			result.put("result", false);
			result.put("message", message.getMessage("E0060","작업중 오류가 발생하였습니다."));
            return result.toString();
		}
		return result.toString();
	}

	@RequestMapping(value = "resendform.do", method = RequestMethod.GET)
	public String layoutResendWriteForm(HttpSession session, @ModelAttribute("MailWriteForm") MailWriteForm form,
								  @RequestParam(value="msgid", required=false) String msgid, ModelMap model,
								  @RequestParam(value="flag", required=false) String flag,
                                  @RequestParam(value="linkid", required=false) String linkid) throws Exception {
		UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
		String userid = userSessionInfo.getUserid();

		String permission = userSessionInfo.getPermission();

		try{
			EmsBean bean = mailService.noUseridGetMailData(msgid);

			/** 해당 페이지를 사용할 권한이 존재하는지 확인한다. url 통해 직접 들어올 경우 위해 체크 */
			if (permission.equals(UserInfoBean.UTYPE_NORMAL)) { // 관리자인지 체크
				if (StringUtils.isNotEmpty(userid)) {
					if (!userid.equals(bean.getUserid())) {
						log.error("SendResult permissionCheck.json - Authentication ERROR : {}", userid);
						model.addAttribute("message", message.getMessage("E0760","발송 권한이 없습니다."));
						return "error/common";
					}
				} else {
					log.error("SendResult permissionCheck.json - USERID NULL ERROR");
					model.addAttribute("message", message.getMessage("E0060","작업중 오류가 발생하였습니다."));
					return "error/common";
				}
			}

			if(StringUtils.isNotEmpty(flag)){
				form = mailService.getWriteForm(userSessionInfo, msgid);
			}

			/** 발송분류 목록을 권한에 따라 처리 */
			List<ImbCategoryInfo> categoryList = null;

			if (UserInfoBean.UTYPE_NORMAL.equals(permission)) {
				categoryList = categoryService.getUserCategory(userid);
			} else {
				categoryList = categoryService.getUserCategory(null);
			}

			Map<String, String> hourList = new LinkedHashMap<>();
			for (int i = 0; i <= 23; i++) {
				String s = ImUtils.checkDigit(i);
				hourList.put(s, message.getMessage("E0543", new Object[]{s}, "{0}시"));
			}
			model.addAttribute("hourList", hourList);

			Map<String, String> minuteList = new LinkedHashMap<>();
			for (int i = 0; i <= 11; i++) {
				int j = i * 5; // 5분 간격 설정
				String s = ImUtils.checkDigit(j);
				minuteList.put(s, message.getMessage("E0544", new Object[]{s}, "{0}분"));
			}
			model.addAttribute("minuteList", minuteList);

			ArrayList<ImbAttachRestrict> rList = new ArrayList<>();
			List<ImbAttachRestrict> restrictList = attachRestrictService.getExtInfo();
			if (restrictList != null) {
				for (int j = 0; j < restrictList.size(); j++) {
					ImbAttachRestrict restrict = restrictList.get(j);
					rList.add(restrict);
				}
			}

			form.setMsgid(null);
			if(StringUtils.isNotEmpty(flag)){
				form.setRecid(bean.getRecid());
				form.setRectype("4");
				form.setResend_flag(flag);
				form.setLinkid(linkid);
				String sub = "";
				if(StringUtils.equals(flag, "rcpt")) sub = message.getMessage("E0765", "수신 확인 재발신");
				else if (StringUtils.equals(flag, "norcpt")) sub = message.getMessage("E0766", "수신 미확인 재발신");
				else if (StringUtils.equals(flag, "link")) sub = message.getMessage("E0767", "링크 클릭 재발신");
				form.setRecname(sub + "-" + bean.getMsg_name());
				form.setOld_msgid(msgid);
				form.setIs_reserve("0");
				form.setReserv_day(null);
				form.setReserv_hour(null);
				form.setReserv_min(null);
				form.setResp_day(null);
				form.setResp_hour(null);
			}else{
				String from = userSessionInfo.getEmail();
				form.setReplyto(from);
				if (StringUtils.isNotEmpty(userSessionInfo.getName())) {
					from = userSessionInfo.getName() + "<" + from + ">";
				}
				form.setMail_from(from);
				form.setMsg_name(bean.getMsg_name());
				form.setContent(bean.getContents());
			}

			model.addAttribute("restrict", rList);
			model.addAttribute("isDraft", 0);
			model.addAttribute("MailWriteForm", form);
			model.addAttribute("categoryList", categoryList);
			model.addAttribute("maxAttachCount", ImbConstant.ATTACH_MAX_COUNT);
			model.addAttribute("maxAttachSize", ImbConstant.ATTACH_MAX_SIZE);
		}catch (NullPointerException ne) {
			String errorId = ErrorTraceLogger.log(ne);
			log.error("Get Mail WriteForm error : {}",errorId);
		}
		catch(Exception e){
			String errorId = ErrorTraceLogger.log(e);
			log.error("Get Mail WriteForm error : {}",errorId);
		}
		return "/mail/write";
	}

	@RequestMapping(value = "clickresendform.do", method = RequestMethod.GET)
	public String layoutClickResendWriteForm(HttpSession session, @ModelAttribute("MailWriteForm") MailWriteForm form,
										@RequestParam(value="msgid", required=false) String msgid, ModelMap model,
										@RequestParam(value="recvid", required=false) String recvid) throws Exception {
		UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
		String emlPath = ImbConstant.MSG_PATH;
		String userid = userSessionInfo.getUserid();
		String permission = userSessionInfo.getPermission();

		try{
			EmsBean emsBean = mailService.getMailcontentData(msgid);

			/** 해당 페이지를 사용할 권한이 존재하는지 확인한다. */
			if (permission.equals(UserInfoBean.UTYPE_NORMAL)) { // 관리자인지 체크
				if (StringUtils.isNotEmpty(userid)) {
					if (!userid.equals(emsBean.getUserid())) {
						log.error("SendResult permissionCheck.json - Authentication ERROR : {}", userid);
						model.addAttribute("message", message.getMessage("E0760","발송 권한이 없습니다."));
						return "error/common";
					}
				} else {
					log.error("SendResult permissionCheck.json - USERID NULL ERROR");
					model.addAttribute("message", message.getMessage("E0060","작업중 오류가 발생하였습니다."));
					return "error/common";
				}
			}

			form = mailService.getWriteForm(userSessionInfo, msgid);

			RecvMessageIDBean recvMessageIDBean = sendResultService.getRecvForMsgid(msgid, recvid);
			String msgPath = emlPath + emsBean.getMsg_path();

			if(!ImFileUtil.isFile(msgPath)){
				log.error("Eml File does not exist - msgid : {}", msgid);
				}
			/*Properties props = new Properties();
			Session s = Session.getDefaultInstance(props);
			ImMessage message = new ImMessage(s);
			message.parseMimeFile(msgPath);
			message.getHtml();
			String contents = message.getHtml();*/
			String contents = emsBean.getContents();


			Map<Integer, String> map = new HashMap<>();
			map.put(1, recvMessageIDBean.getField1());
			map.put(2, recvMessageIDBean.getField2());
			map.put(3, recvMessageIDBean.getField3());
			map.put(4, recvMessageIDBean.getField4());
			map.put(5, recvMessageIDBean.getField5());
			map.put(6, recvMessageIDBean.getField5());
			map.put(7, recvMessageIDBean.getField7());
			map.put(8, recvMessageIDBean.getField8());
			map.put(9, recvMessageIDBean.getField9());

			String subject = testSendService.returnReplaceField("",form.getMsg_name(),0,map);
			log.info("suject : {}", subject);
			//contents = testSendService.returnReplaceField("",contents,0,map);
			log.info("contents : {}", contents);

			if(StringUtils.isNotBlank(subject)) {
				form.setMsg_name(subject);
			}
			form.setContent(contents);
			form.setMsgid(null);
			form.setRecid(emsBean.getRecid());
			form.setRectype("4");
			form.setRecname(emsBean.getRecname());
			form.setRecvid(recvid);
			form.setOld_msgid(msgid);
			form.setDbkey(emsBean.getDbkey());
			form.setIs_reserve("0");
			form.setReserv_day(null);
			form.setReserv_hour(null);
			form.setReserv_min(null);
			form.setResp_day(null);
			form.setResp_hour(null);

			model.addAttribute("mail_to",recvMessageIDBean.getField1());
			model.addAttribute("isDraft", 0);
			model.addAttribute("MailWriteForm", form);
			model.addAttribute("maxAttachCount", ImbConstant.ATTACH_MAX_COUNT);
			model.addAttribute("maxAttachSize", ImbConstant.ATTACH_MAX_SIZE);
		}catch (NullPointerException ne) {
			String errorId = ErrorTraceLogger.log(ne);
			log.error("Get Mail WriteForm ne error : {}",errorId);
			model.addAttribute("message", message.getMessage("E0761","원본 eml 파일이 삭제되어 재발송이 불가합니다."));
			return "error/common";
		}
		catch(Exception e){
			String errorId = ErrorTraceLogger.log(e);
			log.error("Get Mail WriteForm error : {}",errorId);
			model.addAttribute("message", message.getMessage("E0761","원본 eml 파일이 삭제되어 재발송이 불가합니다."));
			return "error/common";
		}
		return "/popup/mail/popup_rewrite";
	}

	/**
	 * 메일 재발송
	 * @param request
	 * @param session
	 * @param form
	 * */
	@RequestMapping(value = "rewrite.json", method = RequestMethod.POST)
	@ResponseBody
	public String mailReWrite(HttpServletRequest request, HttpSession session, @ModelAttribute("MailWriteForm") MailWriteForm form) {
		JSONObject result = new JSONObject();

		if (form == null) {
			result.put("result", false);
			result.put("message", message.getMessage("E0489", "필수값이 누락되었습니다."));
			return result.toString();
		}

		try {
			// 발송 bean 선언 및 파라메터 setting
			TestSendBean testSendBean = new TestSendBean();
			String mainkey = ImUtils.makeKeyNum(24);
			testSendBean.setMainkey(mainkey);
			testSendBean.setSubject(form.getMsg_name());
			testSendBean.setMailfrom(form.getMail_from());
			testSendBean.setIp(request.getRemoteAddr());
			testSendService.doResendMail(form, testSendBean);

		}
		catch (NullPointerException ne) {
			String errorId = ErrorTraceLogger.log(ne);
			log.error("ReWrite Error : {}", errorId);
			result.put("result", false);
			result.put("message", message.getMessage("E0060","작업중 오류가 발생하였습니다."));
			return result.toString();
		}
		catch (Exception e){
			String errorId = ErrorTraceLogger.log(e);
			log.error("ReWrite Error : {}", errorId);
			result.put("result", false);
			result.put("message", message.getMessage("E0060","작업중 오류가 발생하였습니다."));
			return result.toString();
		}
		result.put("result", true);
		result.put("message", message.getMessage("E0379","발송완료"));
		return result.toString();
	}

}

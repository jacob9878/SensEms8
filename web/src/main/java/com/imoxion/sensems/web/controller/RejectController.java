package com.imoxion.sensems.web.controller;

import com.imoxion.common.util.ImCheckUtil;
import com.imoxion.common.util.ImStringUtil;
import com.imoxion.common.util.ImUtils;
import com.imoxion.sensems.common.logger.ErrorTraceLogger;
import com.imoxion.sensems.web.beans.RejectBean;
import com.imoxion.sensems.web.beans.UserInfoBean;
import com.imoxion.sensems.web.common.CommonFile;
import com.imoxion.sensems.web.common.ImPage;
import com.imoxion.sensems.web.common.ImbConstant;
import com.imoxion.sensems.web.database.domain.ImbAddrGrp;
import com.imoxion.sensems.web.database.domain.ImbReject;
import com.imoxion.sensems.web.form.*;
import com.imoxion.sensems.web.service.ActionLogService;
import com.imoxion.sensems.web.service.AddressService;
import com.imoxion.sensems.web.service.RejectService;
import com.imoxion.sensems.web.util.AlertMessageUtil;
import com.imoxion.sensems.web.util.FileCharsetUtil;
import com.imoxion.sensems.web.util.HttpRequestUtil;
import com.imoxion.sensems.web.util.ImEncUtil;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.Date;
import java.util.List;


/**
 *
 * @author hyesue12
 *
 */
@Controller
@RequestMapping("/send/reject/")
public class RejectController {

	protected Logger log = LoggerFactory.getLogger( RejectController.class );


	@Autowired
	private RejectService rejectService;

	@Autowired
	private ActionLogService actionLogService;

	@Autowired
	private MessageSourceAccessor message;


	/**
	 * 수신거부 리스트
	 * @param rejectListForm
	 * @param model
	 * @param session
	 * @return
	 */
	@RequestMapping(value="list.do")
	public String listReject(@ModelAttribute("rejectListForm") RejectListForm rejectListForm, HttpServletRequest request, HttpSession session, ModelMap model,
                             HttpServletResponse response) throws Exception {

        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-control", "no-cache");

		//페이지 목록 개수 세션에서 획득
		UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);

		try {
			int total = rejectService.getRejectCount(rejectListForm.getSrch_keyword());
			int cpage = ImStringUtil.parseInt(rejectListForm.getCpage());
			int pageGroupSize =  ImStringUtil.parseInt(rejectListForm.getPagegroupsize());

			ImPage pageInfo = new ImPage(cpage,userSessionInfo.getPagesize(), total , pageGroupSize);
			boolean issearch = true;
			if(StringUtils.isEmpty(rejectListForm.getSrch_keyword())) {
				issearch = false;
			}
			model.addAttribute("issearch", issearch);

			List<ImbReject> rejectList = null;
			rejectList = rejectService.getRejectList(rejectListForm.getSrch_keyword(), pageInfo.getStart(),
					pageInfo.getEnd());

			model.addAttribute("pageInfo", pageInfo);
			model.addAttribute("rejectList", rejectList);

			//log insert start
			// TODO 로그정책 변경으로 삭제 (수신거부관리>조회 F601)

		}catch (NullPointerException ne) {
			String errorId = ErrorTraceLogger.log(ne);
			log.error("getRejectList ne error", errorId);
		}
		catch (Exception e) {
			String errorId = ErrorTraceLogger.log(e);
			log.error("getRejectList error", errorId);
		}

		return "/send/reject_list";
	}

	@RequestMapping(value="edit.json", method = RequestMethod.POST)
	@ResponseBody
	public String editReject(@ModelAttribute("rejectForm") RejectForm form, @RequestParam(value = "ori_email")String ori_email, HttpSession session, ModelMap model, HttpServletRequest request){
		UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);

		JSONObject result = new JSONObject();
		String email = form.getEmail();

		if (org.apache.commons.lang3.StringUtils.isBlank(form.getEmail())) {
			result.put("result", false);
			result.put("message", message.getMessage("E0054","E-MAIL을 입력해 주세요."));
			return result.toString();
		}

		try {
			form.setEmail(email);

			if(rejectService.selectEditReject(form.getEmail())>0){
				result.put("result", false);
				result.put("message", message.getMessage("E0160","이미 등록된 이메일입니다."));
				return result.toString();
			}
			//이메일 형식 체크
			if(!ImCheckUtil.isEmail(form.getEmail())){
				result.put("result", false);
				result.put("message" ,message.getMessage("E0050","E-MAIL 형식이 잘못되었습니다."));
				return result.toString();
			}



			rejectService.editReject(form, ori_email);

			result.put("result", true);
			result.put("message", message.getMessage("E0062","수정되었습니다."));

			//log insert
			ActionLogForm logForm = new ActionLogForm();
			logForm.setIp(HttpRequestUtil.getRemoteAddr(request));
			logForm.setUserid(userSessionInfo.getUserid());
			logForm.setMenu_key("F403");
			actionLogService.insertActionLog(logForm);

		}catch (NullPointerException ne) {
			String errorId = ErrorTraceLogger.log(ne);
			log.error("Edit DemoAccount Error : {}", errorId);
			result.put("result", false);
			result.put("message", message.getMessage("E0060","작업중 오류가 발생하였습니다."));
			return result.toString();
		}
		catch (Exception e){
			String errorId = ErrorTraceLogger.log(e);
			log.error("Edit DemoAccount Error : {}", errorId);
			result.put("result", false);
			result.put("message", message.getMessage("E0060","작업중 오류가 발생하였습니다."));
			return result.toString();
		}

		return result.toString();
	}
	/**
	 * 이메일 중복체크
	 * @param email
	 * @return
	 */
	@RequestMapping("emailChk.json")
	@ResponseBody
	public String jsonChkMail(@RequestParam(value="email",required = false) String email) {
		JSONObject result = new JSONObject();
		if(email == null || StringUtils.isEmpty(email)){
			result.put("result", false);
			result.put("message", message.getMessage("E0063", "필수 정보가 누락되었습니다."));
			return result.toString();
		}
		try {
			//공백제거
			email = email.replaceAll(" ", "");
			RejectForm reject = rejectService.getRejectInfo(email);
			if(reject != null && StringUtils.isNotEmpty(reject.getEmail())){
				result.put("result", false);//중복있음
			}else{
				result.put("result", true);//중복없음
			}

		}catch (NullPointerException ne) {
			String errorId = ErrorTraceLogger.log(ne);
			log.error("jsonMailChk error", errorId);
		}
		catch (Exception e) {
			String errorId = ErrorTraceLogger.log(e);
			log.error("jsonMailChk error", errorId);
		}

		return  result.toString();
	}

	/**
	 * 수신거부 추가 처리
	 * @param rejectForm
	 * @return
	 */
	@RequestMapping(value = "add.json", method = RequestMethod.POST)
	@ResponseBody
	public String addReject(@ModelAttribute("rejectForm") RejectForm rejectForm,HttpServletRequest request, HttpSession session ) {
		UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
		JSONObject result = new JSONObject();
		try {
			//이메일 형식 체크
			if(!ImCheckUtil.isEmail(rejectForm.getEmail())){
				result.put("result", false);
				result.put("message" ,message.getMessage("E0050","E-MAIL 형식이 잘못되었습니다."));
				return result.toString();
			}

			int resultAdd = rejectService.insertReject(rejectForm);
			if(resultAdd==1){
				result.put("result", true);
				result.put("message",message.getMessage("E0061","추가되었습니다."));

				//log insert start
				ActionLogForm logForm = new ActionLogForm();
				logForm.setIp(HttpRequestUtil.getRemoteAddr(request));
				logForm.setUserid(userSessionInfo.getUserid());
				logForm.setMenu_key("F602");
				logForm.setParam("수신거부 이메일 : " + rejectForm.getEmail());
				actionLogService.insertActionLog(logForm);

			}else{
				result.put("result", false);
				result.put("message",message.getMessage("E0548","실패하였습니다."));
			}
		}catch (NullPointerException ne) {
			String errorId = ErrorTraceLogger.log(ne);
			log.error("add ne error", errorId);
			result.put("result", false);
			result.put("message",message.getMessage("E0060","작업중 오류가 발생하였습니다."));
		}
		catch (Exception e) {
			String errorId = ErrorTraceLogger.log(e);
			log.error("add error", errorId);
			result.put("result", false);
			result.put("message",message.getMessage("E0060","작업중 오류가 발생하였습니다."));

		}
		return result.toString();
	}


	/**
	 * 수신거부 삭제
	 * @param emails
	 * @return
	 */
	@RequestMapping(value = "delete.json", method = RequestMethod.POST)
	@ResponseBody
	public String deleteReject(@RequestParam(value="emails[]", required = false) String[] emails,HttpServletRequest request, HttpSession session )  {
		UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
		JSONObject result = new JSONObject();
		if(emails == null ){
			result.put("result", false);
			result.put("message", message.getMessage("E0063", "필수 정보가 누락되었습니다."));
			return result.toString();
		}
		int resultDel = 0;
		try {
			resultDel = rejectService.deleteReject(request,userSessionInfo.getUserid(),emails);
			if(resultDel==1){
				result.put("result", true);
				result.put("message",message.getMessage("E0070","삭제되었습니다."));
			}else{
				result.put("result", false);
				result.put("message",message.getMessage("E0548","실패하였습니다."));
			}
		}catch (NullPointerException ne) {
			String errorId = ErrorTraceLogger.log(ne);
			log.error("delete error", errorId);
			result.put("result", false);
			result.put("message",message.getMessage("E0060","작업중 오류가 발생하였습니다."));
		}
		catch (Exception e) {
			String errorId = ErrorTraceLogger.log(e);
			log.error("delete error", errorId);
			result.put("result", false);
			result.put("message",message.getMessage("E0060","작업중 오류가 발생하였습니다."));
		}

		result.put("message",message.getMessage("E0070","삭제되었습니다."));
		return result.toString();
	}

	/**
	 * @Method Name : save
	 * @Method Comment : 수신거부 목록 저장
	 * @throws Exception
	 */
	@RequestMapping(value = "save.do" , method = RequestMethod.GET)
	public ModelAndView save( @ModelAttribute("rejectForm") RejectForm rejectForm,HttpServletRequest request, HttpSession session) {
		UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
		String tempFileName = CommonFile.getFilePath(ImbConstant.TEMPFILE_PATH, ImUtils.makeKeyNum(24) + ".xlsx");
		File file = new File(tempFileName);
	   try {
			rejectService.getDownPath(tempFileName); //엑셀만들기

		   //log insert start
		   ActionLogForm logForm = new ActionLogForm();
		   logForm.setIp(HttpRequestUtil.getRemoteAddr(request));
		   logForm.setUserid(userSessionInfo.getUserid());
		   logForm.setMenu_key("F604");
		   logForm.setParam("수신거부 목록 저장 : " + tempFileName);
		   actionLogService.insertActionLog(logForm);

		}catch (NullPointerException ne) {
		   String errorId = ErrorTraceLogger.log(ne);
		   log.error("reject/save.do error", errorId);
	   }

	   catch(Exception e){
			String errorId = ErrorTraceLogger.log(e);
			log.error("reject/save.do error", errorId);
		}
		String fileName = "reject_list.xlsx";



		return CommonFile.getDownloadView(file, fileName);
	}

	/**
	 * @return 수신거부 파일로 업로드
	 * @Method Name : upload file
	 * @Method Comment : upload  Controller
	 */

//	@RequestMapping(value = "uploadfile.json" , method = RequestMethod.POST)
//	@ResponseBody
//	public synchronized String uploadfile(@ModelAttribute("rejectForm") RejectForm rejectForm, MultipartHttpServletRequest request,HttpSession session) throws Exception {
//		UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
//
//		JSONObject result = new JSONObject();
//		int resultadd=0, success=0, fail=0;
//
//		MultipartFile file = request.getFile("file");
//		String tempPath = ImbConstant.TEMPFILE_PATH ;//파일저장경로
//		BufferedReader br = null;
//		try {
//			File dir = new File(tempPath);
//			if(!dir.exists()) dir.mkdirs();//없으면 만든다
//			String line;
//			br = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF-8"));
//			while((line=br.readLine()) != null) {
//				if (line != null && StringUtils.isNotEmpty(line)) {
//					line = line.replaceAll(" ", "");//공백제거
//					RejectForm emailChk = rejectService.getRejectInfo(line);
//					if (emailChk != null && StringUtils.isNotEmpty(emailChk.getEmail())) {// 중복존재
//						log.info("upload error: email key duplicated, {}" , line);
//						fail++;
//						continue;
//					}
//					rejectForm.setEmail(line); //email
//					try {
//						resultadd = rejectService.insertReject(rejectForm);
//					}
//					catch (FileNotFoundException fe){
//						String errorId = ErrorTraceLogger.log(fe);
//						log.error("reject loadfile fileNotFoundExceptionerror:", errorId);
//					}
//					catch (IOException ie) {
//						String errorId = ErrorTraceLogger.log(ie);
//						log.error("reject loadfile error:", errorId);
//						fail++;
//						continue;
//					}
//					catch (NullPointerException ne) {
//						String errorId = ErrorTraceLogger.log(ne);
//						log.error("reject loadfile ne error:", errorId);
//						fail++;
//						continue;
//					}
//					catch (Exception e) {
//						String errorId = ErrorTraceLogger.log(e);
//						log.error("reject loadfile error:", errorId);
//						fail++;
//						continue;
//					}
//					if (resultadd == 1) {
//						success++;
//					}
//				}
//			}
//			result.put("result", true);
//			result.put("success", success);
//			result.put("fail", fail);
//
//			//log insert start
//			ActionLogForm logForm = new ActionLogForm();
//			logForm.setIp(HttpRequestUtil.getRemoteAddr(request));
//			logForm.setUserid(userSessionInfo.getUserid());
//			logForm.setMenu_key("F605");
//			logForm.setParam("성공횟수  : " + success + " / 실패횟수 : " + fail);
//			actionLogService.insertActionLog(logForm);
//		}catch (NullPointerException ne) {
//			String errorId = ErrorTraceLogger.log(ne);
//			log.error("reject loadfile.json error", errorId);
//			result.put("result", false);
//			result.put("message",message.getMessage("E0060","작업중 오류가 발생하였습니다."));
//		}
//		catch (Exception e) {
//			String errorId = ErrorTraceLogger.log(e);
//			log.error("reject loadfile.json error", errorId);
//			result.put("result", false);
//			result.put("message",message.getMessage("E0060","작업중 오류가 발생하였습니다."));
//		}finally{
//			if(br!=null){
//				br.close();
//			}
//		}
//		return result.toString();
//		}

	/**
	 * 파일로 가져오기 1단계
	 * 팝업창 열기
	 */
	@RequestMapping(value = "doImport1.do",method = RequestMethod.GET)
	public String dbImport1(HttpSession session, ModelMap model) {

		return "/popup/send/popup_reject_import1";
	}

	/**
	 * 샘플파일 다운로드 처리를 행한다.
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "sampleDownload.do",method = RequestMethod.GET)
	public ModelAndView sampleDownload(HttpSession session, HttpServletRequest request, HttpServletResponse response, ModelMap model) {

		String savePath = ImbConstant.SENSDATA_PATH + File.separator + "user_data" + File.separator + "address" + File.separator;

		String fpath = savePath + "uploadSample.csv";

		File file = new File(fpath);
		if (!file.exists()) {
			// 파일이 존재하지 않을 경우 에러처리
			log.error("Argument is {}", file.toString());
			String msg = message.getMessage("E0430","파일이 존재하지 않습니다.");
			return AlertMessageUtil.getMessageViewOfScript("alert('"+ msg +"')");
		}

		return CommonFile.getDownloadView(file, "uploadSample.csv");
	}

	@RequestMapping(value = "doImport2.do",method = RequestMethod.POST)
	public String dbImport2(MultipartHttpServletRequest request, HttpSession session, ModelMap model) {
		UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
		String userid = userSessionInfo.getUserid();
		try{
			MultipartFile file = request.getFile("im_file");

			if(file == null || file.isEmpty()) {
				model.addAttribute("infoMessage",message.getMessage("E0113","파일을 선택해 주세요."));
				return "/popup/send/popup_reject_import1";
			}

			String orgFileName = file.getOriginalFilename();
			if(orgFileName.lastIndexOf('.')==-1){
				model.addAttribute("infoMessage",message.getMessage("E0442","파일의 확장자가 존재하지 않습니다."));
				return "/popup/send/popup_reject_import1";
			}

			//MultipartFile을 File 형식으로 변환
			String name = ImEncUtil.getInstance().replaceAll(orgFileName);
			File tempAddrFile = new File(name);
			file.transferTo(tempAddrFile);

			//확장자 체크
			String extension = ImStringUtil.substr(orgFileName,orgFileName.lastIndexOf('.'));
			extension = extension.toLowerCase();
			if(!extension.equals(".csv") && !extension.equals(".txt")){
				model.addAttribute("infoMessage",message.getMessage("E0443","추가할 수 없는 파일 확장자 입니다."));
				return "/popup/send/popup_reject_import1";
			}

			String addrFile_path = ImbConstant.TEMPFILE_PATH;

			// 저장될 고유 키 생성
			String fileKey = ImUtils.makeKeyNum(24);
			String fileName= userid + "_" + fileKey;

			File saveFile = new File(addrFile_path + File.separator + fileName);

			// 어떤 환경에서도 파일이 깨지지 않도록 UTF8 형식으로 변환한다.
			FileCharsetUtil.converterUTF8( tempAddrFile, saveFile);

			tempAddrFile.delete();
			model.addAttribute("fileKey", fileKey);
		}catch (NullPointerException ne) {
			String errorId = ErrorTraceLogger.log(ne);
			log.error("{} - Import Reject File Error", errorId);
			model.addAttribute("infoMessage",message.getMessage("E0444","파일 업로드 중 에러가 발생하였습니다."));
			return "/popup/send/popup_reject_import1";
		}
		catch (Exception e){
			String errorId = ErrorTraceLogger.log(e);
			log.error("{} - Import Reject File Error", errorId);
			model.addAttribute("infoMessage",message.getMessage("E0444","파일 업로드 중 에러가 발생하였습니다."));
			return "/popup/send/popup_reject_import1";
		}

		return "/popup/send/popup_reject_import2";
	}

	/**
	 * 파일로 가져오기 2단계의 미리 보기
	 * @param fileKey
	 * @param div - 값이 없을 시 defaultValue = 0
	 * @param header - 값이 없을 시 defaultValue = 0
	 * @param session
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "import/preview.json")
	public String doImportPreview(@RequestParam(value = "fileKey", required = false) String fileKey,
								  @RequestParam(value = "div", defaultValue = "0", required = false) String div,
								  @RequestParam(value = "header", defaultValue = "0", required = false) String header,
								  HttpSession session) {

		UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
		String userid = userSessionInfo.getUserid();
		JSONObject object = new JSONObject();

		if(StringUtils.isEmpty(fileKey)){
			object.put("result",false);
			object.put("message",message.getMessage("E0445", "파일 미리보기 중 오류가 발생하였습니다."));
			return object.toString();
		}

		try {
			String previewHtml = rejectService.importAddressFilePreview(fileKey, userid, div, header);

			object.put("result",true);
			object.put("previewHtml",previewHtml);
		}catch ( NullPointerException ne) {
			String errorId = ErrorTraceLogger.log(ne);
			log.error("{} - importRejectFilePreview ERROR",errorId);
			object.put("result",false);
			object.put("message",message.getMessage("E0445", "파일 미리보기 중 오류가 발생하였습니다."));
			return object.toString();
		}
		catch (Exception e) {
			String errorId = ErrorTraceLogger.log(e);
			log.error("{} - importRejectFilePreview ERROR",errorId);
			object.put("result",false);
			object.put("message",message.getMessage("E0445", "파일 미리보기 중 오류가 발생하였습니다."));
			return object.toString();

		}

		return object.toString();
	}

	/**
	 * 파일로 가져오기 3단계
	 * @param form
	 * @param request
	 * @param session
	 * @param model
	 */
	@RequestMapping(value = "doImport3.do",method = RequestMethod.POST)
	public String dbImport3(@ModelAttribute("importForm") RejectImportForm form, HttpServletRequest request, HttpSession session, ModelMap model) {

		UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
		String userid = userSessionInfo.getUserid();

		List<ImbReject> rejectList = null;
		try {
			form = rejectService.importAddressFileSetting(form,userid);

			// 그룹 목록 취득
			rejectList = rejectService.getRejectListAll();

		}catch (NullPointerException ne) {
			String errorId = ErrorTraceLogger.log(ne);
			log.error("{} - getRejectList ERROR",errorId);
			model.addAttribute("infoMessage",message.getMessage("E0060","작업중 오류가 발생하였습니다."));
			return "/popup/send/popup_reject_import1";
		}
		catch (Exception e) {
			String errorId = ErrorTraceLogger.log(e);
			log.error("{} - getRejectList ERROR",errorId);
			model.addAttribute("infoMessage",message.getMessage("E0060","작업중 오류가 발생하였습니다."));
			return "/popup/send/popup_reject_import1";
		}

		model.addAttribute("importForm", form);
		model.addAttribute("rejectList", rejectList);
		return "/popup/send/popup_reject_import3";
	}

	/**
	 * 파일로 가져오기 3단계에서 2단계로 다시 넘어가기 위한 Controller
	 * @param fileKey
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "doImportPrev.do",method = RequestMethod.POST)
	public String dbImportPrev(@RequestParam(value = "fileKey",required = false) String fileKey, ModelMap model) {

		if(StringUtils.isEmpty(fileKey)){
			log.error("FileKey is Not Exist - Redirect Reject Import First Page");
			model.addAttribute("infoMessage",message.getMessage("E0113","파일을 선택해 주세요."));
			return "/popup/send/popup_reject_import1";
		}

		model.addAttribute("fileKey", fileKey);
		return "/popup/send/popup_reject_import2";
	}

	/**
	 * 파일로 가져오기 마지막 단계 로직 수행
	 * @param form
	 * @param request
	 * @param session
	 * @param model
	 */
	@RequestMapping(value = "doImportFinish.do",method = RequestMethod.POST)
	public String dbImportFinish(@ModelAttribute("importForm") RejectImportForm form, HttpServletRequest request, HttpSession session, ModelMap model){
		UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
		String userid = userSessionInfo.getUserid();

		if(StringUtils.isEmpty(form.getFileKey())){
			log.error("FileKey is Not Exist - Redirect Reject Import First Page");
			model.addAttribute("infoMessage",message.getMessage("E0113","파일을 선택해 주세요."));
			return "/popup/send/popup_reject_import1";
		}

		RejectImportResultForm importResult = null;

		try {
			//새그룹 선택 시 로직
//			if(form.getGkey().equals(AddressService.NEW_GROUP)){
//				String gname = form.getGname();
//				boolean flag = true;
//				if(StringUtils.isEmpty(gname)){
//					model.addAttribute("infoMessage",message.getMessage("E0337","주소록 그룹명을 입력해 주세요."));
//					flag = false;
//				}
//				boolean isExistGname = addressService.isExistGname(gname,userid);
//				if(isExistGname){
//					model.addAttribute("infoMessage",message.getMessage("E0482", "이미 존재하는 주소록 그룹명입니다."));
//					flag = false;
//				}
//				if(!flag){
//					// 그룹 목록 취득
//					List<ImbAddrGrp> addrGrpList = addressService.getAddressGroupList(userid);
//					form = addressService.importAddressFileSetting(form,userid);
//					model.addAttribute("importForm", form);
//					model.addAttribute("addrGrpList", addrGrpList);
//
//					return "/popup/receiver/popup_address_import3";
//				}
//			}

			importResult = rejectService.importRejectForFile(form,userid);

			//log insert start
			// TODO 로그정책 변경으로 삭제 (개인주소록>가져오기 E211)

		}catch (NullPointerException ne) {
			String errorId = ErrorTraceLogger.log(ne);
			log.error("{} - dbImportFinish ne ERROR",errorId);
			model.addAttribute("errorMessage", message.getMessage("E0060","작업중 오류가 발생하였습니다."));
			return "/popup/send/popup_reject_import_finish";
		}
		catch (Exception e) {
			String errorId = ErrorTraceLogger.log(e);
			log.error("{} - dbImportFinish ERROR",errorId);
			model.addAttribute("errorMessage", message.getMessage("E0060","작업중 오류가 발생하였습니다."));
			return "/popup/send/popup_reject_import_finish";
		}
		try {
			model.addAttribute("nRowCount", importResult.getImportCount()); // 시도 횟수
			model.addAttribute("nSuccess", importResult.getSuccessCount()); //성공 횟수
			model.addAttribute("nColumnCountNotMatch", importResult.getColumnCountNotMatch()); // 파일에서 헤더 개수와 데이터의 필드 개수가 불일치한 것


			if(!importResult.getEmailAddressErrorList().isEmpty()) {
				model.addAttribute("nEmailCheckCount", importResult.getEmailAddressErrorList().size()); // 이메일 누락/형식 오류 개수
				model.addAttribute("nEmailCheckList", importResult.getEmailAddressErrorList()); // 이메일 누락/형식 오류
			}

		}
		catch (NullPointerException np){
			String errorId = ErrorTraceLogger.log(np);
			log.error("{} - dbImportFinish ERROR",errorId);
			model.addAttribute("errorMessage", message.getMessage("E0060","작업중 오류가 발생하였습니다."));
			return "/popup/send/popup_reject_import_finish";
		}
		return "/popup/send/popup_reject_import_finish";
	}

}
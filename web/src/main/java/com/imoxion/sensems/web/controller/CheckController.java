package com.imoxion.sensems.web.controller;

import com.imoxion.common.util.ImStringUtil;
import com.imoxion.security.ImSecurityLib;
import com.imoxion.sensems.common.logger.ErrorTraceLogger;
import com.imoxion.sensems.web.beans.AttachBean;
import com.imoxion.sensems.web.common.CommonFile;
import com.imoxion.sensems.web.common.ImbConstant;
import com.imoxion.sensems.web.database.domain.ImbTransmitData;
import com.imoxion.sensems.web.form.RejectForm;
import com.imoxion.sensems.web.service.*;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.Date;

/**
 * 링크추적, 수신확인 등 관련 컨트롤러
 * create by zpqdnjs 2021-03-29
 * */
@Controller
@RequestMapping(value = "check")
public class CheckController {
	protected Logger log = LoggerFactory.getLogger(CheckController.class);

	@Autowired
    private MessageSourceAccessor message;

	@Autowired
	private RejectService rejectService;

	@Autowired
	private MailService mailService;

	@Autowired
	private ImageService imageService;

	@Autowired
	private CheckService checkService;

	@Autowired
	private TestSendService testSendService;

	@Autowired
	private TransmitDataService transmitDataService;


	/**
	 * 수신확인 처리 url
	 * @param msgid
	 * @param rcode
	 * */
	@RequestMapping(value="openmail.do", method = RequestMethod.GET)
    public ModelAndView rcptCheck(@RequestParam(value="msgid", required=false) String msgid,
			@RequestParam(value="rcode", required=false) String rcode) throws Exception{

		try{
			if(StringUtils.isBlank(msgid) || StringUtils.isBlank(rcode)){
				log.info("Receipt Error - Missing required value, msgid:{}, rcode{}", msgid, rcode);
				return imageService.notFoundImage();
			}
//			String word = "imoxion";
//			String test = ImSecurityLib.makePassword("AES", word, false);
//			System.out.println(test);
//
//			String detest = ImSecurityLib.makePassword("AES", test, true);
//			System.out.println(detest);

//			ImSecurityLib.makePassword("AES", rcode, true); // 서버에서 넘어오는 암호화 방식에 따라 설정하면됨
//			ImSecurityLib.makePassword("AES", msgid, true);

			msgid = ImSecurityLib.decryptAES(ImbConstant.URL_AES_KEY, msgid);
			rcode = ImSecurityLib.decryptAES(ImbConstant.URL_AES_KEY, rcode);


			if(!checkService.isAfterResptime(msgid)){
				checkService.updateReceiptInfo(msgid, rcode);
			}

			String imagePath = ImbConstant.SENSDATA_PATH + File.separator + "images" + File.separator + "transparent.png";
			// 아웃룩에서 X 박스가 나오지 않도록 이미지 하나를 던져준다.
			File file = new File(imagePath);
			if(!file.exists()){
				return imageService.notFoundImage();
			}
	    	return CommonFile.getDownloadView(file, "transparent.png");
		}
		catch (NullPointerException ne) {
			String errorId = ErrorTraceLogger.log(ne);
			log.error("Not Found Image ne ERROR : {}", errorId);
			return imageService.notFoundImage();
		}
		catch(Exception e){
			String errorId = ErrorTraceLogger.log(e);
			log.error("Not Found Image ERROR : {}", errorId);
			return imageService.notFoundImage();
		}
    }


	/**
	 * 링크추적 url
	 * @param msgid
	 * @param adid
	 * @param rcode
	 * @param email
	 * @param url
	 * @param response
	 * */
	@RequestMapping(value="link.do", method = RequestMethod.GET)
    public String linkCheck(@RequestParam(value="msgid", required=false) String msgid, @RequestParam(value="adid", required=false) String adid,
    		@RequestParam(value="rcode", required=false) String rcode, @RequestParam(value="e", required=false) String email,
    		@RequestParam(value="url", required=false) String url, HttpServletResponse response, ModelMap model) throws Exception{

		try{
			if(StringUtils.isBlank(msgid) || StringUtils.isBlank(adid) || StringUtils.isBlank(rcode)
					|| StringUtils.isBlank(email) || StringUtils.isBlank(url)){
				model.addAttribute("message", message.getMessage("E0489","필수값이 누락되었습니다."));
				return "error/common";
			}

			msgid = ImSecurityLib.decryptAES(ImbConstant.URL_AES_KEY, msgid);
			url = ImSecurityLib.decryptAES(ImbConstant.URL_AES_KEY, url);
			// 반응분석 종료일 체크
			if(!checkService.isAfterResptime(msgid)){
				// 2022-09-07 rcode,email 암호화 되어 전달하기 때문에 주석 해제
				adid = ImSecurityLib.decryptAES(ImbConstant.URL_AES_KEY, adid);
				rcode = ImSecurityLib.decryptAES(ImbConstant.URL_AES_KEY, rcode);
				email = ImSecurityLib.decryptAES(ImbConstant.URL_AES_KEY, email);
				checkService.updateLinkInfo(msgid, adid, rcode, email);
			}
		}catch (NullPointerException ne) {
			String errorId = ErrorTraceLogger.log(ne);
			log.error("Link Check Error : {}", errorId);
			model.addAttribute("message", message.getMessage("E0060","작업중 오류가 발생하였습니다."));
			return "error/common";
		}
		catch(Exception e){
			String errorId = ErrorTraceLogger.log(e);
			log.error("Link Check Error : {}", errorId);
			model.addAttribute("message", message.getMessage("E0060","작업중 오류가 발생하였습니다."));
			return "error/common";
		}
		return "redirect:"+url;
    }

	/**
	 * 첨부파일 다운로드
	 * @param fkey
	 * @param msgid
	 * */
	@RequestMapping(value="attach.do", method = RequestMethod.GET)
    public ModelAndView attachDownload(@RequestParam(value="fkey", required=false) String fkey,
    		@RequestParam(value="msgid", required=false) String msgid) throws Exception{
		try {
			if(StringUtils.isBlank(fkey) || StringUtils.isBlank(msgid)){
				ModelAndView mav = new ModelAndView("error/common");
				mav.addObject("message", message.getMessage("E0489","필수값이 누락되었습니다."));
				return mav;
			}
			msgid = ImSecurityLib.decryptAES(ImbConstant.URL_AES_KEY, msgid);
			fkey = ImSecurityLib.decryptAES(ImbConstant.URL_AES_KEY, fkey);
			AttachBean bean = mailService.getAttachInfo(fkey, msgid);
			if (bean == null) {
				ModelAndView mav = new ModelAndView("error/common");
				mav.addObject("message", message.getMessage("E0430", "파일이 존재하지 않습니다."));
				return mav;
			}

			Date expire_date = bean.getExpire_date();
			Date now = new Date();

			if(now.after(expire_date)){
				ModelAndView mav = new ModelAndView("error/common");
				mav.addObject("message", message.getMessage("E0525","만료일이 지난 파일입니다."));
				return mav;
			}

			String filePath = ImbConstant.ATTACH_PATH + File.separator + bean.getFile_path();
			File file = new File(filePath);
			if (!file.exists()) {
				ModelAndView mav = new ModelAndView("error/common");
				mav.addObject("message", message.getMessage("E0430", "파일이 존재하지 않습니다."));
				return mav;
			}
				checkService.updateAttachInfo(fkey, msgid); // 첨부파일 다운로드 회수 증가
			return CommonFile.getDownloadView(file, bean.getFile_name());
		}catch (NullPointerException ne) {
			String errorId = ErrorTraceLogger.log(ne);
			log.error("File Download Error : {}", errorId);
			ModelAndView mav = new ModelAndView("error/common");
			return mav;
		}
		catch (Exception e) {
			String errorId = ErrorTraceLogger.log(e);
			log.error("File Download Error : {}", errorId);
			ModelAndView mav = new ModelAndView("error/common");
			return mav;
		}
    }

	/**
	 * 수신거부 처리 페이지 open
	 * @param msgid
	 * @param email
	 * @param model
	 * @throws Exception
	 * */
	@RequestMapping(value="reject.do", method = RequestMethod.GET)
    public String checkReject(@RequestParam(value="msgid", required=false) String msgid,
    		@RequestParam(value="e", required=false) String email, ModelMap model) throws Exception{
		model.addAttribute("msgid", msgid);
		model.addAttribute("email", email);
    	return "guest/check_reject";
    }

	/**
	 * 수신거부 처리
	 * @param msgid
	 * @param email
	 * @throws Exception
	 * */
	@RequestMapping(value="reject.json", method = RequestMethod.POST)
	@ResponseBody
    public String addReject(@RequestParam(value="msgid", required=false) String msgid,
    		@RequestParam(value="email", required=false) String email) throws Exception{
		JSONObject result = new JSONObject();
		try{
			if(StringUtils.isBlank(msgid) || StringUtils.isBlank(email)){
				result.put("message", message.getMessage("E0489","필수값이 누락되었습니다."));
				return result.toString();
			}
			msgid = ImSecurityLib.decryptAES(ImbConstant.URL_AES_KEY, msgid);
			email = ImSecurityLib.decryptAES(ImbConstant.URL_AES_KEY, email);
			if(!ImStringUtil.isValidEmail(email)){
				result.put("message", message.getMessage("E0050","E-MAIL 형식이 잘못되었습니다."));
				return result.toString();
			}

			if(rejectService.isExistReject(email)){
				result.put("message", message.getMessage("E0515","중복된 수신거부 데이터가 있습니다."));
				return result.toString();
			}

			RejectForm form = new RejectForm();
			form.setEmail(email);
			form.setMsgid(msgid);
			rejectService.insertReject(form);

			result.put("message", message.getMessage("E0061","추가되었습니다."));
		}catch (NullPointerException ne ) {
			String errorId = ErrorTraceLogger.log(ne);
			log.error("Add Reject ERROR : {}", errorId);
			result.put("message", message.getMessage("E0060","작업중 오류가 발생하였습니다."));
		}
		catch(Exception e){
			String errorId = ErrorTraceLogger.log(e);
			log.error("Add Reject ERROR : {}", errorId);
			result.put("message", message.getMessage("E0060","작업중 오류가 발생하였습니다."));
		}
    	return result.toString();
    }

	/**
	 * 개별발송 수신확인 처리 url
	 * @param receipt_param
	 * */
	@RequestMapping(value="openmailex.do", method = RequestMethod.GET)
	public ModelAndView exRcptCheck(@RequestParam(value="p", required=false) String receipt_param) throws Exception{

		try{
			if(StringUtils.isBlank(receipt_param)){
				log.info("Receipt Error - Missing required value, receipt_param:{}", receipt_param);
				return imageService.notFoundImage();
			}

			//파라미터로 넘어온 p 복호화
			String traceid = "";
			String serverid = "";
			String rcptto = "";
			if(StringUtils.isNotEmpty(receipt_param)) {
				receipt_param = ImStringUtil.getStringBefore(receipt_param.trim(), ".gif", false);
			}
			try{
				String decryptParameter = ImSecurityLib.decryptAriaHexString(ImbConstant.URL_AES_KEY, receipt_param, true).trim();
				log.info("decryptParameter : " + decryptParameter);

				String[] dec_param = decryptParameter.split("&");
				for (String ecn_param : dec_param) {
					String[] encParamSplit = ecn_param.split("=");
					if (encParamSplit[0].equals("tid")) {
						traceid = encParamSplit[1].trim();
					} else if (encParamSplit[0].equals("sid")) {
						serverid = encParamSplit[1].trim();
					} else if (encParamSplit[0].equals("to")) {
						rcptto = encParamSplit[1].trim();
					}
				}

				ImbTransmitData data = testSendService.getTransmitDataLog(traceid, serverid, rcptto);

				if(data != null){
					//readdate가 있다면 count만 update
					if(data.getReaddate() == null){
						transmitDataService.updateTransmitDataReaddate(traceid, serverid, rcptto);
					}else{
						transmitDataService.updateTransmitCount(traceid, serverid, rcptto);
					}
				}

			}catch (NullPointerException ne) {
				String errorId = ErrorTraceLogger.log(ne);
				log.error("{} - exRcptCheck error", errorId);
			}
			catch (Exception e){
				String errorId = ErrorTraceLogger.log(e);
				log.error("{} - exRcptCheck error", errorId);
			}

			String imagePath = ImbConstant.SENSDATA_PATH + File.separator + "image" + File.separator + "transparent.gif";
			// 아웃룩에서 X 박스가 나오지 않도록 이미지 하나를 던져준다.
			File file = new File(imagePath);
			if(!file.exists()){
				return imageService.notFoundImage();
			}
			return CommonFile.getDownloadView(file, "transparent.gif");
		}catch (NullPointerException ne) {
			String errorId = ErrorTraceLogger.log(ne);
			log.error("Not Found Image ERROR : {}", errorId);
			return imageService.notFoundImage();
		}
		catch(Exception e){
			String errorId = ErrorTraceLogger.log(e);
			log.error("Not Found Image ERROR : {}", errorId);
			return imageService.notFoundImage();
		}
	}
}

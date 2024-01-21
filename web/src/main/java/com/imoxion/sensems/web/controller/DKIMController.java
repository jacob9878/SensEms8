package com.imoxion.sensems.web.controller;

import com.imoxion.common.util.ImStringUtil;
import com.imoxion.sensems.common.logger.ErrorTraceLogger;
import com.imoxion.sensems.web.beans.UserInfoBean;
import com.imoxion.sensems.web.common.ImPage;
import com.imoxion.sensems.web.database.domain.ImbDkimInfo;
import com.imoxion.sensems.web.database.domain.ImbRelay;
import com.imoxion.sensems.web.form.ActionLogForm;
import com.imoxion.sensems.web.form.DkimListForm;
import com.imoxion.sensems.web.service.ActionLogService;

import com.imoxion.sensems.web.service.DkimService;
import com.imoxion.sensems.web.util.HttpRequestUtil;
import com.imoxion.sensems.web.util.ImUtility;
import com.imoxion.sensems.web.util.JSONResult;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.DomainValidator;
import org.cryptacular.EncodingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * dkim 관리 Controller
 *
 * @author
 *
 */
@Controller
@RequestMapping("sysman/dkim")
public class DKIMController {

	private Logger logger = LoggerFactory.getLogger(DKIMController.class);


	@Autowired
	private ActionLogService actionLogService;

	@Autowired
	private DkimService dkimService;

	@Autowired
	private MessageSourceAccessor message;

	/**
	 * dkim 목록
	 * @param session
	 * @param form
	 * @param model
	 * @return
	 */
	@RequestMapping("list.do")
	public String dkimList(HttpSession session, @ModelAttribute("dkimListForm") DkimListForm form, ModelMap model, HttpServletResponse response){
	try {

        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-control", "no-cache");

		UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);

		int total = dkimService.getDkimCount(form.getSrch_keyword());
		int cpage = ImStringUtil.parseInt(form.getCpage());
		int pageGroupSize =  ImStringUtil.parseInt(form.getPagegroupsize());
        String srch_key  = form.getSrch_keyword();
		boolean issearch = true;
		if(StringUtils.isEmpty(form.getSrch_keyword())) {
			issearch = false;
		}
		model.addAttribute("issearch", issearch);

		ImPage pageInfo = new ImPage(cpage,userSessionInfo.getPagesize(), total , pageGroupSize);

		List<ImbDkimInfo> dkimList = null;
		dkimList = dkimService.getDkimList(form.getSrch_keyword(), pageInfo.getStart(), pageInfo.getEnd());
		model.addAttribute("srch_key", srch_key);
        model.addAttribute("cpage", cpage);
		model.addAttribute("pageInfo", pageInfo);
		model.addAttribute("dkimList", dkimList);
	}catch (Exception e) {
		String errorId = ErrorTraceLogger.log(e);
		logger.error("getDkimlist error", errorId);
	}
		return "/sysman/dkim_list";
//		List<ImbDkimInfo> dkimList = dkimService.getDKIMList();
//		model.addAttribute("dkimList",dkimList);
//
//		model.addAttribute("dkimForm",form);
//		return "/sysman/dkim_list";
	}

	/**
	 * dkim 추가 화면 표시
	 *
	 * @param session
	 * @param form
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value="add.do",method = RequestMethod.GET)
	public String addFormDKIM(HttpSession session, HttpServletRequest request ,@ModelAttribute("dkimForm") DkimListForm form, ModelMap model, RedirectAttributes redirectAttributes){


			/*List<RelayDomain> domainList = relayDomainService.getPosibleDkimDomainList();


			if(domainList == null || domainList.size() ==0) {
				redirectAttributes.addFlashAttribute("infoMessage",message.getMessage("91","DKIM을 추가할 수 있는 도메인이 없습니다."));
				return "redirect:/config/dkim/list.do";
			}

			model.addAttribute("posibleDomainList",domainList);*/
			String cpage = request.getParameter("cpage");
			model.addAttribute("cpage", cpage);
			model.addAttribute("dkimForm",form);


		return "/sysman/dkimAdd";
	}

	/**
	 * dkim 추가 처리
	 * @param session
	 * @param model
	 * @param form
	 * @param bindingResult
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value="add.do",method = RequestMethod.POST)
	public String addDKIM(HttpSession session, ModelMap model, @ModelAttribute("dkimForm") DkimListForm form, BindingResult bindingResult, RedirectAttributes redirectAttributes,
						  HttpServletRequest request){

		model.addAttribute("dkimForm",form);
		UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
		String domain = form.getDomain();
		try {
			DomainValidator domainValidator = DomainValidator.getInstance(true);
			if(!domainValidator.isValid(domain)){
				model.addAttribute("infoMessage", message.getMessage("E0615","도메인 정보가 올바르지 않습니다."));
				//bindingResult.rejectValue("domain","91",message.getMessage("91","도메인 정보가 올바르지 않습니다."));
				return "/sysman/dkimAdd";
			}

			if(!ImUtility.validCharacter(domain)){
				model.addAttribute("infoMessage", message.getMessage("E0737","사용할 수 없는 특수문자가 포함되어 있습니다. (사용가능한 특수 문자 : (_) , (.) , (-) )"));
				return "/sysman/dkimAdd";
			}



			ImbDkimInfo dkimInfo = dkimService.getDKIM(domain);

			if(dkimInfo != null && StringUtils.isNotEmpty(dkimInfo.getDomain())) {
				model.addAttribute("infoMessage", message.getMessage("E0616","이미 등록되어 있는 도메인입니다."));
				//bindingResult.rejectValue("domain","82",message.getMessage("82","이미 등록되어 있는 도메인입니다."));
				return "/sysman/dkimAdd";
			}
			dkimService.addDKIMSinger(domain);

			//log insert start
			ActionLogForm logForm = new ActionLogForm();
			logForm.setIp(HttpRequestUtil.getRemoteAddr(request));
			logForm.setUserid(userSessionInfo.getUserid());
			logForm.setMenu_key("G801");
			logForm.setParam("도메인 명 : " + domain );
			actionLogService.insertActionLog(logForm);

		}catch (NullPointerException ex) {
			String errorId = ErrorTraceLogger.log(ex);
			logger.error("{} - dkim signer add error nullpoint",errorId);
			model.addAttribute("infoMessage", message.getMessage("E0060","작업 중 오류가 발생하였습니다."));
			//bindingResult.rejectValue("domain","83",message.getMessage("83","작업 중 오류가 발생하였습니다."));
			return "sysman/dkim_list";
		}catch(BadSqlGrammarException exx) {
			String errorId = ErrorTraceLogger.log(exx);
			logger.error("{} - dkim signer add error bad sql",errorId);
			model.addAttribute("infoMessage", message.getMessage("E0060","작업 중 오류가 발생하였습니다."));
			return "sysman/dkim_list";
		}
		catch (SQLException et) {
			String errorId = ErrorTraceLogger.log(et);
			logger.error("{} - dkim signer add error sql exception",errorId);
			model.addAttribute("infoMessage", message.getMessage("E0060","작업 중 오류가 발생하였습니다."));
			return "sysman/dkim_list";
		}catch (EncodingException enx) {
			String errorId = ErrorTraceLogger.log(enx);
			logger.error("{} - dkim signer add error encoding",errorId);
			model.addAttribute("infoMessage", message.getMessage("E0060","작업 중 오류가 발생하였습니다."));
			return "sysman/dkim_list";
		}
		catch (Exception e) {
			String errorId = ErrorTraceLogger.log(e);
			logger.error("{} - dkim signer add error cn",errorId);
			model.addAttribute("infoMessage", message.getMessage("E0060","작업 중 오류가 발생하였습니다."));
			return "sysman/dkim_list";
		}

		return "redirect:/sysman/dkim/view.do?domain="+domain;
	}

	/**
	 * dkim 상세 보기
	 *
	 * @param session
	 * @param domain
	 * @param model
	 * @return
	 */
	@RequestMapping("view.do")
	public String viewDKIM(HttpSession session,HttpServletRequest request ,@RequestParam("domain") String domain, ModelMap model, @ModelAttribute("dkimForm") DkimListForm form){

		ImbDkimInfo dkimInfo = dkimService.getDKIM(domain);
		String cpage = request.getParameter("cpage");
		String srch_keyword = request.getParameter("srch_keyword");

		model.addAttribute("cpage", cpage);
        model.addAttribute("srch_keyword", srch_keyword);
		model.addAttribute("dkim",dkimInfo);
		model.addAttribute("dkimForm",form);
		return "/sysman/dkimView";
	}

	/**
	 * dkim 삭제 처리
	 *
	 * @param session
	 * @param domain
	 * @return
	 */
	@RequestMapping(value = "delete.json", method = RequestMethod.POST)
	@ResponseBody
	public String deleteDkim(@RequestParam(value="dkim[]", required = false) String[] dkim, HttpServletRequest request, HttpSession session )  {
		UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
		JSONObject result = new JSONObject();
		if(dkim == null ){
			result.put("result", false);
			result.put("message", message.getMessage("E0063", "필수 정보가 누락되었습니다."));
			return result.toString();
		}
		int resultDel = 0;
		try {
			resultDel = dkimService.deleteDKIM(request,userSessionInfo.getUserid(),dkim);
			if(resultDel==1){
				result.put("result", true);
				result.put("message",message.getMessage("E0070","삭제되었습니다."));
			}else{
				result.put("result", false);
				result.put("message",message.getMessage("E0548","실패하였습니다."));
			}
		}
		catch(BadSqlGrammarException exx) {
			String errorId = ErrorTraceLogger.log(exx);
			logger.error("delete error exx", errorId);
			result.put("result", false);
			result.put("message",message.getMessage("E0060","작업중 오류가 발생하였습니다."));
		}
		catch (SQLException et) {
			String errorId = ErrorTraceLogger.log(et);
			logger.error("delete error et", errorId);
			result.put("result", false);
			result.put("message",message.getMessage("E0060","작업중 오류가 발생하였습니다."));
		}
		catch (Exception e) {
			String errorId = ErrorTraceLogger.log(e);
			logger.error("delete error", errorId);
			result.put("result", false);
			result.put("message",message.getMessage("E0060","작업중 오류가 발생하였습니다."));
		}

		result.put("message",message.getMessage("E0070","삭제되었습니다."));
		return result.toString();
	}


	/**
	 * dkim 사용여부 업데이트
	 *
	 * @param session
	 * @param domain
	 * @param use_sign
	 * @return
	 */
	@RequestMapping(value = "use.json", method = RequestMethod.GET)
	@ResponseBody
	public String activeDKIM(HttpSession session, @RequestParam("domain") String domain,
							 @RequestParam("use_sign") String use_sign,ModelMap model) {

		JSONResult result = new JSONResult();

		try {
			dkimService.updateUse(domain, use_sign);
			result.setResultCode(JSONResult.SUCCESS);
			result.setMessage(message.getMessage("E0724","변경되었습니다."));
		}
		catch (NullPointerException et) {
			String errorId = ErrorTraceLogger.log(et);
			logger.error("{} - dkim use update error", errorId);
			result.setResultCode(JSONResult.FAIL);
			result.setMessage(message.getMessage("E0605","사용여부를 변경하지 못하였습니다."));
		}
		catch (Exception e) {
			String errorId = ErrorTraceLogger.log(e);
			logger.error("{} - dkim use update error", errorId);
			result.setResultCode(JSONResult.FAIL);
			result.setMessage(message.getMessage("E0605","사용여부를 변경하지 못하였습니다."));

		}

		return result.toString();
	}

}

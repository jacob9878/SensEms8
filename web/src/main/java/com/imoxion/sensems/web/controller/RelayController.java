package com.imoxion.sensems.web.controller;

import com.imoxion.common.util.ImStringUtil;
import com.imoxion.common.util.ImUtils;
import com.imoxion.sensems.common.logger.ErrorTraceLogger;
import com.imoxion.sensems.web.beans.UserInfoBean;
import com.imoxion.sensems.web.common.CommonFile;
import com.imoxion.sensems.web.common.ImPage;
import com.imoxion.sensems.web.common.ImbConstant;
import com.imoxion.sensems.web.database.domain.ImbReject;
import com.imoxion.sensems.web.database.domain.ImbRelay;
import com.imoxion.sensems.web.database.domain.ImbUserinfo;
import com.imoxion.sensems.web.form.*;
import com.imoxion.sensems.web.service.ActionLogService;
import com.imoxion.sensems.web.service.RejectService;
import com.imoxion.sensems.web.service.RelayService;
import com.imoxion.sensems.web.util.HttpRequestUtil;
import com.imoxion.sensems.web.util.ImUtility;
import javax.servlet.http.HttpServletResponse;
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
import org.xbill.DNS.NULLRecord;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;




@Controller
@RequestMapping("/sysman/relay/")
public class RelayController {

	protected Logger log = LoggerFactory.getLogger( RelayController.class );


	@Autowired
	private RelayService relayService;

	@Autowired
	private ActionLogService actionLogService;

	@Autowired
	private MessageSourceAccessor message;


	/**
	 * 릴레이 리스트
	 * @param relayListForm
	 * @param model
	 * @param session
	 * @return
	 */
	@RequestMapping(value="list.do")
	public String listRelay(@ModelAttribute("relayListForm") RelayListForm relayListForm, HttpServletRequest request, HttpSession session, ModelMap model,
                            HttpServletResponse response) throws Exception {

        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-control", "no-cache");

		//페이지 목록 개수 세션에서 획득
		UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);

		try {
			String srch_type = relayListForm.getSrch_type();
			String srch_keyword = relayListForm.getSrch_keyword();
			int total = relayService.getRelayCount(relayListForm.getSrch_keyword());
			int cpage = ImStringUtil.parseInt(relayListForm.getCpage());
			int pageGroupSize =  ImStringUtil.parseInt(relayListForm.getPagegroupsize());
			String ip = relayListForm.getIp();
			String memo = relayListForm.getMemo();

			boolean issearch = true;
			if(StringUtils.isEmpty(relayListForm.getSrch_keyword())) {
				issearch = false;
			}
			model.addAttribute("issearch", issearch);

			int totalsize = relayService.getSearchRelayCount(srch_type,srch_keyword,ip,memo);

			ImPage pageInfo = new ImPage(cpage,userSessionInfo.getPagesize(), totalsize , pageGroupSize);

//			List<ImbRelay> relayList = null;
//			relayList = relayService.getRelayList(relayListForm.getSrch_keyword(), pageInfo.getStart(),
//					pageInfo.getEnd());

			List<ImbRelay> relayList = relayService.getRelayListForPageing(srch_type,srch_keyword,ip,memo, pageInfo.getStart(), pageInfo.getEnd());
			model.addAttribute("totalsize",totalsize);
			model.addAttribute("pageInfo", pageInfo);
			model.addAttribute("relayList", relayList);



		}catch (NullPointerException ne) {
			String errorId = ErrorTraceLogger.log(ne);
			log.error("getRelayList ne error", errorId);
		}
		catch (Exception e) {
			String errorId = ErrorTraceLogger.log(e);
			log.error("getRelayList error", errorId);
		}

		return "/sysman/relay_list";
	}

	@RequestMapping(value="edit.json", method = RequestMethod.POST)
	@ResponseBody
	public String editRelay(@ModelAttribute("relayForm") RelayForm form, @RequestParam(value = "ori_ip")String ori_ip, HttpSession session, ModelMap model, HttpServletRequest request){
		UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
		JSONObject result = new JSONObject();
		String ip = form.getIp();
		String memo = form.getMemo();

		if(!ImUtility.validCharacter(memo)){
			result.put("result", false);
			result.put("message",message.getMessage("E0737","사용할 수 없는 특수문자가 포함되어 있습니다. (사용가능한 특수 문자 : (_) , (,) , (-) )"));
			return result.toString();
		}

		if (org.apache.commons.lang3.StringUtils.isBlank(form.getIp())) {
			result.put("result", false);
			result.put("message", message.getMessage("E0567","IP를 입력해 주세요."));
			return result.toString();
		}

		try {
			form.setIp(ip);
			form.setMemo(memo);
			if(StringUtils.equals(form.getIp(),ori_ip)) {
				result.put("result", true);
				result.put("message", message.getMessage("E0062","수정되었습니다."));
				relayService.editRelay(form, ori_ip);

				//log insert
				ActionLogForm logForm = new ActionLogForm();
				logForm.setIp(HttpRequestUtil.getRemoteAddr(request));
				logForm.setUserid(userSessionInfo.getUserid());
				logForm.setMenu_key("H102");
				logForm.setParam("연동IP : " + ori_ip  + " / 설명 : " + form.getMemo());
				actionLogService.insertActionLog(logForm);

				return result.toString();

			}

			if(relayService.selectEditRelay(form.getIp())>0){
				result.put("result", false);
				result.put("message", message.getMessage("E0568","이미 등록된 IP입니다."));
				//relayService.editRelay(form, ori_ip);
				return result.toString();
			}

			relayService.editRelay(form, ori_ip);

			result.put("result", true);
			result.put("message", message.getMessage("E0062","수정되었습니다."));

			//log insert
			ActionLogForm logForm = new ActionLogForm();
			logForm.setIp(HttpRequestUtil.getRemoteAddr(request));
			logForm.setUserid(userSessionInfo.getUserid());
			logForm.setMenu_key("H102");
			logForm.setParam("이전 연동IP : " + ori_ip + " / 변경 연동IP : " + form.getIp() + " / 설명 : " + form.getMemo());
			actionLogService.insertActionLog(logForm);

		}catch (JSONException je) {
			String errorId = ErrorTraceLogger.log(je);
			log.error("Edit RelayIp json Error : {}", errorId);
			result.put("result", false);
			result.put("message", message.getMessage("E0060","작업중 오류가 발생하였습니다."));
			return result.toString();
		}catch (NullPointerException ne) {
			String errorId = ErrorTraceLogger.log(ne);
			log.error("Edit RelayIp ne Error : {}", errorId);
			result.put("result", false);
			result.put("message", message.getMessage("E0060","작업중 오류가 발생하였습니다."));
			return result.toString();
		}

		catch (Exception e){
			String errorId = ErrorTraceLogger.log(e);
			log.error("Edit RelayIp Error : {}", errorId);
			result.put("result", false);
			result.put("message", message.getMessage("E0060","작업중 오류가 발생하였습니다."));
			return result.toString();
		}

		return result.toString();
	}
	/**
	 * ip 중복체크
	 * @param ip
	 * @return
	 */
	@RequestMapping("ipChk.json")
	@ResponseBody
	public String jsonChkip(@RequestParam(value="ip",required = false) String ip) {
		JSONObject result = new JSONObject();

//		if(ip == "" || StringUtils.isEmpty(ip)){
//			result.put("result", false);
//			result.put("message", message.getMessage("E0063", "필수 정보가 누락되었습니다."));
//			return result.toString();
//		}
		try {
			//공백제거
			if(StringUtils.isNotEmpty(ip)) {
				ip = ip.replaceAll(" ", "");

				RelayForm relay = relayService.getRelayInfo(ip);
				if (relay != null && StringUtils.isNotEmpty(relay.getIp())) {
					result.put("result", false);//중복있음
				} else {
					result.put("result", true);//중복없음
				}
			}

		}catch (NullPointerException ne) {
			String errorId = ErrorTraceLogger.log(ne);
			log.error("jsonIpChk error", errorId);
		}
		catch (Exception e) {
			String errorId = ErrorTraceLogger.log(e);
			log.error("jsonIpChk error", errorId);
		}

		return  result.toString();
	}


	/**
	 * ip 추가 처리
	 * @param relayForm
	 * @return
	 */
	@RequestMapping(value = "add.json", method = RequestMethod.POST)
	@ResponseBody
	public String addRelay(@ModelAttribute("relayForm") RelayForm relayForm,HttpServletRequest request, HttpSession session ) {
		UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
		JSONObject result = new JSONObject();
		try {
			if(!ImUtility.validCharacter(relayForm.getMemo())){
				result.put("result", false);
				result.put("message",message.getMessage("E0737","사용할 수 없는 특수문자가 포함되어 있습니다. (사용가능한 특수 문자 : (_) , (,) , (-) )"));
				return result.toString();
			}

			int resultAdd = relayService.insertRelay(relayForm);
			if(resultAdd==1){
				result.put("result", true);
				result.put("message",message.getMessage("E0061","추가되었습니다."));

				//log insert start
				ActionLogForm logForm = new ActionLogForm();
				logForm.setIp(HttpRequestUtil.getRemoteAddr(request));
				logForm.setUserid(userSessionInfo.getUserid());
				logForm.setMenu_key("H101");
				logForm.setParam("연동IP : " + relayForm.getIp() + " / 설명 : "+ relayForm.getMemo());
				actionLogService.insertActionLog(logForm);

			}else{
				result.put("result", false);
				result.put("message",message.getMessage("E0548","실패하였습니다."));
			}
		}catch (JSONException je) {
			String errorId = ErrorTraceLogger.log(je);
			log.error("add json error", errorId);
			result.put("result", false);
			result.put("message",message.getMessage("E0060","작업중 오류가 발생하였습니다."));
		}
		catch (NullPointerException ne) {
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
	 * 연동 ip 삭제
	 * @param ips
	 * @return
	 */
	@RequestMapping(value = "delete.json", method = RequestMethod.POST)
	@ResponseBody
	public String deleteRelay(@RequestParam(value="ips[]", required = false) String[] ips,HttpServletRequest request, HttpSession session )  {
		UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
		JSONObject result = new JSONObject();
		if(ips == null ){
			result.put("result", false);
			result.put("message", message.getMessage("E0063", "필수 정보가 누락되었습니다."));
			return result.toString();
		}
		int resultDel = 0;
		try {
			resultDel = relayService.deleteRelay(request,userSessionInfo.getUserid(),ips);
			if(resultDel==1){
				result.put("result", true);
				result.put("message",message.getMessage("E0070","삭제되었습니다."));
			}else{
				result.put("result", false);
				result.put("message",message.getMessage("E0548","실패하였습니다."));
			}
		}catch (IOException ie) {
			String errorId = ErrorTraceLogger.log(ie);
			log.error("delete error", errorId);
			result.put("result", false);
			result.put("message",message.getMessage("E0060","작업중 오류가 발생하였습니다."));
		}
		catch (NullPointerException ne) {
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



}
package com.imoxion.sensems.web.controller;

import com.imoxion.common.util.ImStringUtil;
import com.imoxion.sensems.common.logger.ErrorTraceLogger;
import com.imoxion.sensems.web.beans.UserInfoBean;
import com.imoxion.sensems.web.common.ImPage;
import com.imoxion.sensems.web.database.domain.ImbBlock;
import com.imoxion.sensems.web.database.domain.ImbRelay;
import com.imoxion.sensems.web.form.*;
import com.imoxion.sensems.web.service.ActionLogService;
import com.imoxion.sensems.web.service.BlockService;
import com.imoxion.sensems.web.service.RelayService;
import com.imoxion.sensems.web.util.HttpRequestUtil;
import com.imoxion.sensems.web.util.ImUtility;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.jdbc.Null;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;


@Controller
@RequestMapping("/sysman/block/")
public class BlockController {

	protected Logger log = LoggerFactory.getLogger( BlockController.class );


	@Autowired
	private BlockService blockService;

	@Autowired
	private ActionLogService actionLogService;

	@Autowired
	private MessageSourceAccessor message;


	/**
	 * 차단 iP 리스트
	 * @param blockListForm
	 * @param model
	 * @param session
	 * @return
	 */
	@RequestMapping(value="list.do")
	public String listBlock(@ModelAttribute("blockListForm") BlockListForm blockListForm, HttpServletRequest request, HttpSession session, ModelMap model,
                            HttpServletResponse response) throws Exception {

        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-control", "no-cache");

		//페이지 목록 개수 세션에서 획득
		UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);

		try {
			int total = blockService.getBlockCount(blockListForm.getSrch_keyword());
			int cpage = ImStringUtil.parseInt(blockListForm.getCpage());
			int pageGroupSize =  ImStringUtil.parseInt(blockListForm.getPagegroupsize());
			String srch_type = blockListForm.getSrch_type();
			String srch_keyword = blockListForm.getSrch_keyword();
			String ip = blockListForm.getIp();
			String memo = blockListForm.getMemo();

			boolean issearch = true;
			if(StringUtils.isEmpty(blockListForm.getSrch_keyword())) {
				issearch = false;
			}
			model.addAttribute("issearch", issearch);

			int totalsize = blockService.getSearchBlockCount(srch_type,srch_keyword,ip,memo);


			ImPage pageInfo = new ImPage(cpage,userSessionInfo.getPagesize(), totalsize , pageGroupSize);

//			List<ImbBlock> blockList = null;
//			blockList = blockService.getBlockList(blockListForm.getSrch_keyword(), pageInfo.getStart(),
//					pageInfo.getEnd());

			List<ImbBlock> blockList = blockService.getBlockListForPageing(srch_type,srch_keyword,ip,memo, pageInfo.getStart(), pageInfo.getEnd());

			model.addAttribute("totalsize",totalsize);

			model.addAttribute("pageInfo", pageInfo);
			model.addAttribute("blockList", blockList);



		}catch (NullPointerException ne) {
			String errorId = ErrorTraceLogger.log(ne);
			log.error("getBlockList ne error - {}", errorId);
		}
		catch (Exception e) {
			String errorId = ErrorTraceLogger.log(e);
			log.error("getBlockList error - {}", errorId);
		}

		return "/sysman/block_list";
	}

	@RequestMapping(value="edit.json", method = RequestMethod.POST)
	@ResponseBody
	public String editBlock
			(@ModelAttribute("blockForm") BlockForm form, @RequestParam(value = "ori_ip")String ori_ip, HttpSession session, ModelMap model, HttpServletRequest request){
		UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
		JSONObject result = new JSONObject();
		String ip = form.getIp();
		String memo = form.getMemo();

		if (org.apache.commons.lang3.StringUtils.isBlank(form.getIp())) {
			result.put("result", false);
			result.put("message", message.getMessage("E0567","IP를 입력해 주세요."));
			return result.toString();
		}
		if(!ImUtility.validCharacter(memo)){
			result.put("result", false);
			result.put("message",message.getMessage("E0737","사용할 수 없는 특수문자가 포함되어 있습니다. (사용가능한 특수 문자 : (_) , (,) , (-) )"));
			return result.toString();
		}

		try {
			form.setIp(ip);
			form.setMemo(memo);
			if(StringUtils.equals(form.getIp(),ori_ip)) {
				result.put("result", true);
				result.put("message", message.getMessage("E0062","수정되었습니다."));
				blockService.editBlock(form, ori_ip);

				//log insert
				ActionLogForm logForm = new ActionLogForm();
				logForm.setIp(HttpRequestUtil.getRemoteAddr(request));
				logForm.setUserid(userSessionInfo.getUserid());
				logForm.setMenu_key("H202");
				logForm.setParam("차단IP : " + ori_ip  + " / 설명 : " + form.getMemo());
				actionLogService.insertActionLog(logForm);

				return result.toString();

			}

			if(blockService.selectEditBlock(form.getIp())>0){
				result.put("result", false);
				result.put("message", message.getMessage("E0568","이미 등록된 IP입니다."));
				//blockService.editBlock(form, ori_ip);
				return result.toString();
			}

			blockService.editBlock(form, ori_ip);

			result.put("result", true);
			result.put("message", message.getMessage("E0062","수정되었습니다."));

			//log insert
			ActionLogForm logForm = new ActionLogForm();
			logForm.setIp(HttpRequestUtil.getRemoteAddr(request));
			logForm.setUserid(userSessionInfo.getUserid());
			logForm.setMenu_key("H202");
			logForm.setParam("이전 차단IP : " + ori_ip + " / 변경 차단IP : " + form.getIp() + " / 설명 : " + form.getMemo());
			actionLogService.insertActionLog(logForm);

		}catch (NullPointerException ne) {
			String errorId = ErrorTraceLogger.log(ne);
			log.error("Edit BlockIp Error : {}", errorId);
			result.put("result", false);
			result.put("message", message.getMessage("E0060","작업중 오류가 발생하였습니다."));
			return result.toString();
		}
		catch (Exception e){
			String errorId = ErrorTraceLogger.log(e);
			log.error("Edit BlockIp Error : {}", errorId);
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

		try {
			if (StringUtils.isNotEmpty(ip)) {
			//공백제거
				ip = ip.replaceAll(" ", "");
				BlockForm block = blockService.getBlockInfo(ip);
				if (block != null && StringUtils.isNotEmpty(block.getIp())) {
					result.put("result", false);//중복있음
				} else {
					result.put("result", true);//중복없음
				}
			}

		}catch (NullPointerException ne) {
			String errorId = ErrorTraceLogger.log(ne);
			log.error("jsonIpChk error - {}", errorId);
		}
		catch (Exception e) {
			String errorId = ErrorTraceLogger.log(e);
			log.error("jsonIpChk error - {}", errorId);
		}

		return  result.toString();
	}


	/**
	 * ip 추가 처리
	 * @param blockForm
	 * @return
	 */
	@RequestMapping(value = "add.json", method = RequestMethod.POST)
	@ResponseBody
	public String addBlock(@ModelAttribute("blockForm") BlockForm blockForm,HttpServletRequest request, HttpSession session ) {
		UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
		JSONObject result = new JSONObject();
		try {
			if(!ImUtility.validCharacter(blockForm.getMemo())){
				result.put("result", false);
				result.put("message",message.getMessage("E0737","사용할 수 없는 특수문자가 포함되어 있습니다. (사용가능한 특수 문자 : (_) , (,) , (-) )"));
				return result.toString();
			}

			int resultAdd = blockService.insertBlock(blockForm);
			if(resultAdd==1){
				result.put("result", true);
				result.put("message",message.getMessage("E0061","추가되었습니다."));

				//log insert start
				ActionLogForm logForm = new ActionLogForm();
				logForm.setIp(HttpRequestUtil.getRemoteAddr(request));
				logForm.setUserid(userSessionInfo.getUserid());
				logForm.setMenu_key("H201");
				logForm.setParam("차단IP : " + blockForm.getIp() + " / 설명 : " + blockForm.getMemo());
				actionLogService.insertActionLog(logForm);

			}else{
				result.put("result", false);
				result.put("message",message.getMessage("E0548","실패하였습니다."));
			}
		}catch (NullPointerException ne) {
			String errorId = ErrorTraceLogger.log(ne);
			log.error("add error - {}", errorId);
			result.put("result", false);
			result.put("message",message.getMessage("E0060","작업중 오류가 발생하였습니다."));
		}
		catch (Exception e) {
			String errorId = ErrorTraceLogger.log(e);
			log.error("add error - {]", errorId);
			result.put("result", false);
			result.put("message",message.getMessage("E0060","작업중 오류가 발생하였습니다."));

		}
		return result.toString();
	}


	/**
	 * 차단 ip 삭제
	 * @param ips
	 * @return
	 */
	@RequestMapping(value = "delete.json", method = RequestMethod.POST)
	@ResponseBody
	public String deleteBlock(@RequestParam(value="ips[]", required = false) String[] ips,HttpServletRequest request, HttpSession session )  {
		UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
		JSONObject result = new JSONObject();
		if(ips == null ){
			result.put("result", false);
			result.put("message", message.getMessage("E0063", "필수 정보가 누락되었습니다."));
			return result.toString();
		}
		int resultDel = 0;
		try {
			resultDel = blockService.deleteBlock(request,userSessionInfo.getUserid(),ips);
			if(resultDel==1){
				result.put("result", true);
				result.put("message",message.getMessage("E0070","삭제되었습니다."));
			}else{
				result.put("result", false);
				result.put("message",message.getMessage("E0548","실패하였습니다."));
			}
		}catch (NullPointerException ne) {
			String errorId = ErrorTraceLogger.log(ne);
			log.error("delete error - {}", errorId);
			result.put("result", false);
			result.put("message",message.getMessage("E0060","작업중 오류가 발생하였습니다."));
		}
		catch (Exception e) {
			String errorId = ErrorTraceLogger.log(e);
			log.error("delete error - {}", errorId);
			result.put("result", false);
			result.put("message",message.getMessage("E0060","작업중 오류가 발생하였습니다."));
		}

		result.put("message",message.getMessage("E0070","삭제되었습니다."));
		return result.toString();
	}



}
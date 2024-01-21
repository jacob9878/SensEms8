package com.imoxion.sensems.web.controller;

import com.imoxion.sensems.common.logger.ErrorTraceLogger;
import com.imoxion.sensems.web.beans.UserInfoBean;
import com.imoxion.sensems.web.common.ImPage;
import com.imoxion.sensems.web.database.domain.ImbCategoryInfo;
import com.imoxion.sensems.web.form.ActionLogForm;
import com.imoxion.sensems.web.form.CategoryListForm;
import com.imoxion.sensems.web.service.ActionLogService;
import com.imoxion.sensems.web.service.CategoryService;

import com.imoxion.sensems.web.util.HttpRequestUtil;
import javax.servlet.http.HttpServletResponse;

import com.imoxion.sensems.web.util.ImUtility;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


/**
 * @author zpqdnjs
 * 발송분류 관련 컨트롤러
 */
@Controller
@RequestMapping("/send/category/")
public class CategoryController {

	protected Logger log = LoggerFactory.getLogger( CategoryController.class );

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private MessageSourceAccessor message;

	@Autowired
	private ActionLogService actionLogService;

	/**
	 * 발송분류 리스트
	 *
	 * @param session
	 * @param form
	 * @param model
	 * */
	@RequestMapping(value="list.do")
	public String categoryListByGet(HttpServletRequest request,HttpSession session, @ModelAttribute("categoryListForm") CategoryListForm form, ModelMap model,
                                    HttpServletResponse response){

	    response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-control", "no-cache");

		UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
		try {
			String srch_type = form.getSrch_type();
			String srch_keyword = form.getSrch_keyword();
			String userid = userSessionInfo.getUserid();
			String permission = userSessionInfo.getPermission();

			int totalSize = 0;
			int cpage = Integer.parseInt(form.getCpage());
			int pageGroupSize = Integer.parseInt(form.getPagegroupsize());
			boolean issearch = true;
			if(StringUtils.isEmpty(srch_keyword)) {
				issearch = false;
			}
			model.addAttribute("issearch", issearch);
			/**
			 * 사용자 권한(사용자, 관리자)에 따라 목록을 다르게 보여준다
			 * 사용자 : 자신이 작성한 목록만 노출
			 * 관리자 : 모든목록 노출
			 */
			if (UserInfoBean.UTYPE_NORMAL.equals(permission)) {
				totalSize = categoryService.getCategoryListCount(srch_type, srch_keyword, userid);
			} else {
				totalSize = categoryService.getCategoryListCountByAdmin(srch_type, srch_keyword);
			}

			ImPage pageInfo = new ImPage(cpage, userSessionInfo.getPagesize(), totalSize, pageGroupSize);
			model.addAttribute("pageInfo", pageInfo);

			List<ImbCategoryInfo> categoryList = null;
			if (UserInfoBean.UTYPE_NORMAL.equals(permission)) {
				categoryList = categoryService.getCategoryListForPageing(srch_type, srch_keyword, userid, pageInfo.getStart(), pageInfo.getEnd());
			} else {
				categoryList = categoryService.getCategoryListForPageingByAdmin(srch_type, srch_keyword, pageInfo.getStart(), pageInfo.getEnd());
			}

			model.addAttribute("categoryList", categoryList);

			//log insert start
			// TODO 로그정책 변경으로 삭제 (발송분류관리>조회 F101)

		}catch (NullPointerException ne) {
			String errorId = ErrorTraceLogger.log(ne);
			log.error("Category List Error : {}", errorId);
		}
		catch (Exception e) {
			String errorId = ErrorTraceLogger.log(e);
			log.error("Category List Error : {}", errorId);
		}

		return "/send/categoryList";
	}


	/**
	 * 발송분류 추가
	 *
	 * @param session
	 * @param name
	 * */
	@RequestMapping(value="add.json", method=RequestMethod.POST)
	@ResponseBody
	public String addCategory(HttpServletRequest request,HttpSession session, @RequestParam(value="name", required=false) String name){

		UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
		JSONObject result = new JSONObject();

		if (StringUtils.isBlank(name)) {
			result.put("result", false);
			result.put("message", message.getMessage("E0007","카테고리 명을 입력하세요."));
			return result.toString();
		}
		try {
			String userid = userSessionInfo.getUserid();
			// 서로 다른 계정이 같은 카테고리명 사용 가능
			if (categoryService.checkExistCategory(userid, name)) {
				result.put("result", false);
				result.put("message", message.getMessage("E0008","이미 존재하는 카테고리 명입니다."));
				return result.toString();
			}
			// 특수문자 허용하지 않도록 정규식 검사
			if(!ImUtility.validCharacter(name)){
				result.put("result", false);
				result.put("message", message.getMessage("E0737","사용할 수 없는 특수문자가 포함되어 있습니다. (사용가능한 특수 문자 : (_) , (.) , (-) )"));
				return result.toString();
			}

			categoryService.addCategory(name, userid);

			//log insert start
			ActionLogForm logForm = new ActionLogForm();
			logForm.setIp(HttpRequestUtil.getRemoteAddr(request));
			logForm.setUserid(userSessionInfo.getUserid());
			logForm.setMenu_key("F102");
			logForm.setParam("카테고리명 : " + name);
			actionLogService.insertActionLog(logForm);
		}catch (NullPointerException ne) {
			String errorId = ErrorTraceLogger.log(ne);
			log.error("Add Category Error : {}", errorId);
			result.put("result", false);
			result.put("message", message.getMessage("E0060","작업중 오류가 발생하였습니다."));
			return result.toString();
		}
		catch (Exception e) {
			String errorId = ErrorTraceLogger.log(e);
			log.error("Add Category Error : {}", errorId);
			result.put("result", false);
			result.put("message", message.getMessage("E0060","작업중 오류가 발생하였습니다."));
			return result.toString();
		}

		result.put("result", true);
		result.put("message", message.getMessage("E0061","추가되었습니다."));
		return result.toString();
	}


	/**
	 * 발송분류 수정
	 *
	 * @param session
	 * @param name
	 * */
	@RequestMapping(value="edit.json", method=RequestMethod.POST)
	@ResponseBody
	public String editCategory(HttpServletRequest request, HttpSession session, @RequestParam(value="name", required=false) String name,
								@RequestParam(value="ukey", required=false) String ukey,
								@RequestParam(value="userid", required=false) String userid,
							   @RequestParam(value="ori_name", required=false) String ori_name){
		UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);

		JSONObject result = new JSONObject();
		if (StringUtils.isBlank(ukey) || StringUtils.isBlank(userid)) {
			result.put("result", false);
			result.put("message", message.getMessage("E0063","필수 정보가 누락되었습니다."));
			return result.toString();
		}

		if (StringUtils.isBlank(name)) {
			result.put("message", message.getMessage("E0007","카테고리 명을 입력하세요."));
			return result.toString();
		}

		try {

			if (StringUtils.equals(categoryService.checkDuplicateCategory(userid, ukey),name)){

				result.put("result", false);
				result.put("message", message.getMessage("E0563","기존과 같은 이름입니다."));
				return result.toString();
			}

			if (categoryService.checkExistCategory(userid, name)) {
				result.put("result", false);
				result.put("message", message.getMessage("E0008","이미 존재하는 카테고리 명입니다."));
				return result.toString();
			}

			// 특수문자 허용하지 않도록 정규식 검사
			if(!ImUtility.validCharacter(name)){
				result.put("result", false);
				result.put("message", message.getMessage("E0737","사용할 수 없는 특수문자가 포함되어 있습니다. (사용가능한 특수 문자 : (_) , (.) , (-) )"));
				return result.toString();
			}



			categoryService.editCategory(name, ukey);

			//log insert start
			ActionLogForm logForm = new ActionLogForm();
			logForm.setIp(HttpRequestUtil.getRemoteAddr(request));
			logForm.setUserid(userSessionInfo.getUserid());
			logForm.setMenu_key("F103");
			logForm.setParam("이전 카테고리명 : " + ori_name + " / 변경 카테고리명 : " + name);
			actionLogService.insertActionLog(logForm);

		}catch (NullPointerException ne) {
			String errorId = ErrorTraceLogger.log(ne);
			log.error("Edit Category Error : {}", errorId);
			result.put("result", false);
			result.put("message", message.getMessage("E0060","작업중 오류가 발생하였습니다."));
			return result.toString();
		}
		catch (Exception e) {
			String errorId = ErrorTraceLogger.log(e);
			log.error("Edit Category Error : {}", errorId);
			result.put("result", false);
			result.put("message", message.getMessage("E0060","작업중 오류가 발생하였습니다."));
			return result.toString();
		}

		result.put("result", true);
		result.put("message", message.getMessage("E0062","수정되었습니다."));
		return result.toString();
	}


	/**
	 * 발송분류 삭제
	 *
	 * @param session
	 * @param ukeys
	 * */
	@RequestMapping(value = "delete.json", method = RequestMethod.POST)
	@ResponseBody
	public String deleteCategory(HttpServletRequest request, HttpSession session, @RequestParam(value = "ukeys[]", required = false) String[] ukeys) {
		UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
		JSONObject result = new JSONObject();
		if (ukeys == null) {
			result.put("result", false);
			result.put("message", message.getMessage("E0063","필수 정보가 누락되었습니다."));
			return result.toString();
		}

		try {
			String logParam="";
			for (int i = 0; i < ukeys.length; i++) {
				categoryService.deleteCategory(ukeys[i].split(",")[0]);
				if(i==0){
					logParam= "카테고리 명 : " + ukeys[i].split(",")[1] + " / 카테고리 key : " + ukeys[i].split(",")[0];
				}else {
					logParam += " , 카테고리 명 : " + ukeys[i].split(",")[1] + " / 카테고리 key : " + ukeys[i].split(",")[0];
				}
			}

			//log insert start
			ActionLogForm logForm = new ActionLogForm();
			logForm.setIp(HttpRequestUtil.getRemoteAddr(request));
			logForm.setUserid(userSessionInfo.getUserid());
			logForm.setMenu_key("F104");
			logForm.setParam(logParam);
			actionLogService.insertActionLog(logForm);
		}catch (NullPointerException ne) {
			String errorId = ErrorTraceLogger.log(ne);
			log.error("Delete Category Error : {}", errorId);
			result.put("result", false);
			result.put("message", message.getMessage("E0060","작업중 오류가 발생하였습니다."));
			return result.toString();
		}
		catch (Exception e) {
			String errorId = ErrorTraceLogger.log(e);
			log.error("Delete Category Error : {}", errorId);
			result.put("result", false);
			result.put("message", message.getMessage("E0060","작업중 오류가 발생하였습니다."));
			return result.toString();
		}

		result.put("result", true);
		result.put("message", message.getMessage("E0070","삭제되었습니다."));
		return result.toString();
	}
}
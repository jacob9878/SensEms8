package com.imoxion.sensems.web.controller;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
//import com.imoxion.sensems.web.common.ImaConstant;
import com.imoxion.common.util.ImIpUtil;
import com.imoxion.sensems.web.authentication.RoleService;
import com.imoxion.sensems.web.common.CommonFile;
import com.imoxion.sensems.web.form.ActionLogForm;
import com.imoxion.sensems.web.form.LoginForm;
import com.imoxion.sensems.web.form.UpdatePasswordForm;
import com.imoxion.sensems.web.form.UserForm;
import com.imoxion.sensems.web.module.passwordpolicy.PasswordPoricyService;
import com.imoxion.sensems.web.module.passwordpolicy.PasswordRequired;
import com.imoxion.sensems.web.security.sessionfixation.SessionFixationService;
import com.imoxion.sensems.web.service.ActionLogService;
import com.imoxion.sensems.web.util.AlertMessageUtil;
import com.imoxion.sensems.web.util.HttpRequestUtil;
import com.imoxion.sensems.web.util.JSONResult;
import net.sf.json.JSON;
import nl.captcha.Captcha;
import nl.captcha.backgrounds.GradiatedBackgroundProducer;
import nl.captcha.servlet.CaptchaServletUtil;
import nl.captcha.text.producer.NumbersAnswerProducer;
import nl.captcha.text.renderer.DefaultWordRenderer;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import com.imoxion.common.logger.ErrorTraceLogger;
import com.imoxion.common.util.ImCheckUtil;
import com.imoxion.common.util.ImStringUtil;
import com.imoxion.security.ImSecurityLib;
import com.imoxion.sensems.web.beans.UserInfoBean;
import com.imoxion.sensems.web.common.ImbConstant;
import com.imoxion.sensems.web.common.SessionAttributeNames;
import com.imoxion.sensems.web.database.domain.ImbUserinfo;
import com.imoxion.sensems.web.exception.ForbiddenException;
import com.imoxion.sensems.web.service.AccountService;
import com.imoxion.sensems.web.service.JCryptionService;
import com.imoxion.sensems.web.service.UserService;


/**
 *
 * @author minideji
 *
 */
@Controller
@RequestMapping("/account/")
public class AccountController {

	protected Logger log = LoggerFactory.getLogger( AccountController.class );

	@Autowired
	private AccountService accountService;

	@Autowired
	private UserService userService;

	@Autowired
	private JCryptionService jCryptionService;

	@Autowired
	private RoleService roleService;

	@Autowired
	private MessageSourceAccessor message;

	@Autowired
	private PasswordPoricyService passwordPolicyService;

	@Autowired
	private SessionFixationService sessionFixationService;

	@Autowired
	private ActionLogService actionLogService;

	/**
	 * 로그아웃
	 * @return
	 */
	@RequestMapping(value="logout.do")
	public String logout(HttpServletRequest request, HttpSession session){

		SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.setClearAuthentication(true);
        logoutHandler.setInvalidateHttpSession(true);
        logoutHandler.logout(request, null, null);

		return "redirect:/account/login.do";
	}

	/**
	 * 로그인 페이지
	 * @param loginForm
	 * @param issave
	 * @param userid
	 * @param language
	 * @param request
	 * @param model
	 * @return
	 * @throws Exception
	 */

	@RequestMapping(value="captcha.do" )
	public void getCaptcha(  HttpServletRequest request , HttpServletResponse response, ModelMap model ){
		java.util.List<Font> fontList = new ArrayList<Font>();
		fontList.add(new Font("",Font.HANGING_BASELINE,40));
		List<Color> colorList = new ArrayList<Color>();
		colorList.add(Color.black);
		colorList.add(Color.blue);
		colorList.add(Color.lightGray);
		Captcha captcha = new Captcha.Builder(250, 49).addText( new NumbersAnswerProducer(6), new DefaultWordRenderer(colorList, fontList)).gimp().addNoise()
				.addBorder().addBackground(new GradiatedBackgroundProducer()).build();
		request.getSession().setAttribute(Captcha.NAME, captcha);
		CaptchaServletUtil.writeImage(response,captcha.getImage());

	}

	@RequestMapping(value="login.do",method = RequestMethod.GET )
	public String login(@ModelAttribute("LoginForm") LoginForm loginForm, @CookieValue(value = "issave", required = false) String issave,
						@CookieValue(value = "ems_userid", required = false) String userid, @CookieValue(value = "ems_lang", required = false) String language,
						HttpServletRequest request , ModelMap model ) throws Exception {

		// 접근가능한 IP 대역에서만 로그인페이지가 나오도록 수정
		String requestIp = request.getRemoteAddr();
		boolean allow = accountService.isAllow(requestIp);
		if( !allow ){
			throw new ForbiddenException();
		}
		model.addAttribute("lang", ImbConstant.LANG);


		if( StringUtils.isNotEmpty(language) ){
			loginForm.setLanguage(language);
		}else{
			language = request.getLocale().getLanguage();
			if(!"ko".equals(language) && !"en".equals(language) && !"ja".equals(language) && !"zh".equals(language)){
				language = "en";
			}
			loginForm.setLanguage(language);
		}

		HttpSession session = request.getSession();
		if (session != null) {
            session.setAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, new Locale(language));
            UserInfoBean UserInfo = (UserInfoBean)session.getAttribute("UserInfo");
    		if (UserInfo != null && ImCheckUtil.isNotEmpty(UserInfo.getUserid())) {
                // 세션이 존재 할 경우, 메일목록으로
                return "redirect:/mail/result/list.do";
            }
        }
		// 아이디저장이 되어있을 경우 화면에 표시한다.
		if (ImCheckUtil.isNotEmpty(issave) && issave.equals("1")) {
			userid = ImStringUtil.getSafeString(userid);
			loginForm.setUserid(userid);
			loginForm.setIsSave("1");
		}

		model.addAttribute("isSelectLanguage", ImbConstant.USE_SELLANGUAGE );
		model.addAttribute("LoginForm",loginForm);

		if(ImbConstant.USE_CAPTCHA){
			model.addAttribute("useCaptcha",ImbConstant.USE_CAPTCHA );
		}

		return "/jsp/account/login";
	}

	/**
	 * 로그인 프로세스
	 * @param loginForm
	 * @param bindingResult
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value="login.do",method = RequestMethod.POST )
	public String login(@ModelAttribute("LoginForm") LoginForm loginForm , BindingResult bindingResult,
						HttpServletRequest request , HttpServletResponse response , ModelMap model){

		try {
			// 접근가능한 IP 대역에서만 로그인페이지가 나오도록 수정
			String requestIp = request.getRemoteAddr();
			boolean allow = accountService.isAllow(requestIp);
			if( !allow ){
				throw new ForbiddenException();
			}

			HttpSession session = request.getSession();
			if( session == null ){
				return "redirect:login.do";
			}

			if(ImbConstant.USE_CAPTCHA){
				model.addAttribute("useCaptcha",ImbConstant.USE_CAPTCHA );
			}

			// 세션체크
	        if (roleService.hasRole("ROLE_USER") || roleService.hasRole("ROLE_ADMIN")) {
	            return "redirect:/mail/result/list.do";
	        }

	        boolean secureType = false;
            if (ImbConstant.getInstance().SSL_TYPE == ImbConstant.SSL_TYPE_TOTAL) {
                secureType = true;
            }

			// ###############################################################################################
			// # Language 관련 부분 처리 - START
			// ###############################################################################################
			// 쿠키에 언어정보가 있을 경우 그 언어가 사용할 수 있는 언어인지 확인 한 뒤
			// 사용할 수 없는 언어일 경우 도메인에 설정된 기본 언어로 설정해준다.
			if(ImbConstant.LANG.equals("0")) {
				loginForm.setLanguage(ImbConstant.DEFAULT_LANG);
			}
			String language = loginForm.getLanguage();
			Cookie cookie = null;
			cookie = new Cookie("ems_lang",language);
			cookie.setMaxAge(60 * 60 * 24 * 365);
			cookie.setPath("/");
			cookie.setSecure(secureType);
			response.addCookie(cookie);
			session.setAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, new Locale(language));

			model.addAttribute("LoginForm",loginForm);

			// 언어 변경 모드일 경우 로그인 페이지로 이동한다.
			if ("LANGUAGE".equals(loginForm.getMode())) {
				return "/jsp/account/login";
			}



			// ###############################################################################################
			// # Language 관련 부분 처리 - END
			// ###############################################################################################

			// 아이디, 패스워드 null 체크
			loginForm.validate(loginForm,bindingResult);
			if (bindingResult.hasErrors()) {
				return "/jsp/account/login";
			}

			if(ImbConstant.USE_CAPTCHA){
				//캡챠 체크
				Captcha captcha = (Captcha)session.getAttribute(Captcha.NAME);
				String cap_ans = captcha.getAnswer();
				String answer = loginForm.getAnswer();

				loginForm.setAnswer(StringUtils.EMPTY);
				if(StringUtils.isEmpty(answer) || !answer.equals(cap_ans)){
//					model.addAttribute("error", message.getMessage("674") );
					bindingResult.rejectValue("answer","E0156",  "올바른 숫자를 입력해 주세요.");
					return "/jsp/account/login";
				}
			}

			// ###############################################################################################
			// # ID / PW 체크 - START
			// # AccessIP / 사용중지 / 계정잠금 체크 - START
			// ###############################################################################################
			String userid = ImStringUtil.allTrim(loginForm.getUserid());
			boolean checkId = userService.existUser(userid);

			if( checkId ){
				bindingResult.rejectValue("userid", "E0056", "해당 계정은 존재하지 않습니다.");
				return "/jsp/account/login";
			}


			ImbUserinfo userInfo = userService.getUserInfo(userid);


			//Access IP 체크
			//설정했을 경우에는 check 실시, 설정하지 않았을 때나 0.0.0.0으로 설정 시 모든 IP 허용
			boolean isAccessIP = false;
			if(StringUtils.isNotEmpty(userInfo.getAccess_ip())){
				String[] ips = userInfo.getAccess_ip().split(",");
				for(String access_ip : ips){
					if (access_ip.equals("0.0.0.0") || ImIpUtil.matchIPbyCIDR(access_ip, requestIp)) {
						isAccessIP = true;
						break;
					}else {
						isAccessIP = false;
					}
				}
			}else {
				isAccessIP = true;
			}

			if(!isAccessIP){
				bindingResult.rejectValue("userid", "E0077", "허가되지 않은 IP입니다.");
				return "/jsp/account/login";
			}

			//사용중지 계정인지 체크
			if("1".equals(userInfo.getIsstop())){
				bindingResult.rejectValue("userid", "loE0078", "사용 중지된 계정입니다. 관리자에게 문의하세요.");
				return "/jsp/account/login";
			}

			//패스워드 오류로 인한 계정 잠금 확인
			if(ImbConstant.USE_ACCOUNT_DENY){
				if(userInfo.getFail_login()>=ImbConstant.DENY_COUNT){
					bindingResult.rejectValue("userid", "E0079",new Object[]{ImbConstant.DENY_COUNT}, "비밀번호 "+ImbConstant.DENY_COUNT+"회 불일치하여 계정이 잠겼습니다.</br> 관리자에게 패스워드 변경을 요청하세요.");
					return "/jsp/account/login";
				}

			}

			String sCryptPasswd = ImStringUtil.allTrim(loginForm.getPassword());// 스크립트 암호화 된 패스워드
			String privateKey = session.getAttribute("privateKey").toString();
			String password = jCryptionService.deCrypt(privateKey, loginForm.getEncAESKey(), sCryptPasswd);//평문 패스워드
			String DBPasswd = userInfo.getPasswd();// DB 에서 password 를 가져온다.

			String passwordSalt="";
			// 패스워드를 salt키를 가지고 조합하여 암호화 한다.
			if(ImbConstant.PASS_USE_SALT.equals("1") && StringUtils.isNotEmpty(userInfo.getSt_data())) {
				passwordSalt = userInfo.getSt_data() + password;
			}else{
				passwordSalt = password;
			}

			String pwd_type = userInfo.getPwd_type();

			// 패스워드를 설정된 암호화 타입으로 암호화 하여 값 생성
			password = ImSecurityLib.makePassword(pwd_type,passwordSalt,false);

			// 설정된 값으로 암호화한 Password를 비교한다.
			if( !password.equals(DBPasswd)){
				if(ImbConstant.USE_ACCOUNT_DENY){
					int failcount = userInfo.getFail_login()+1; // +1을 해줘야 현재 틀린 시점과 동일하므로 +1을 처리한다.
					userService.updateFailLoginCount(userInfo);
					bindingResult.rejectValue("password", "E0080",new Object[]{failcount++,ImbConstant.DENY_COUNT}, "사용자 패스워드 "+failcount+"회 실패하였습니다.</br> "+ImbConstant.DENY_COUNT+"회 잘못 입력시 계정이 잠깁니다.");

				}else {
					bindingResult.rejectValue("password","E0076",  "아이디 또는 비밀번호를 확인하세요.");
				}
				return "/jsp/account/login";
			}else {
				//로그인 성공시 사용자의 fail_login 초기화한다.
				userService.updateFailLoginReset(userInfo);
			}
			// ###############################################################################################
			// # ID / PW 체크 - END
			// # AccessIP / 사용중지 / 계정잠금 체크 - END
			// ###############################################################################################


			//로그인 쿠키 관련 설정
			String isSave = loginForm.getIsSave();
			if (ImCheckUtil.isNotEmpty(isSave) && isSave.equals("1")) {
				cookie = new Cookie("ems_userid", userid);
                cookie.setMaxAge(60 * 60 * 24 * 365);
				cookie.setPath("/");
				cookie.setSecure(secureType);
				response.addCookie(cookie);
				cookie = new Cookie("issave", isSave);
                cookie.setMaxAge(60 * 60 * 24 * 365);
				cookie.setPath("/");
                cookie.setSecure(secureType);
				response.addCookie(cookie);
			} else {
				cookie = new Cookie("ems_userid", "");
                cookie.setMaxAge(60 * 60 * 24 * 365);
				cookie.setPath("/");
                cookie.setSecure(secureType);
				response.addCookie(cookie);
				cookie = new Cookie("issave", isSave);
                cookie.setMaxAge(60 * 60 * 24 * 365);
				cookie.setPath("/");
                cookie.setSecure(secureType);
				response.addCookie(cookie);
			}


			//세션관련 객체생성
			UserInfoBean userSessionInfo = userService.getUserSessionInfo(userInfo,language);

			session.setAttribute(SessionAttributeNames.USER_SESSION_INFO,userSessionInfo);
			session.setMaxInactiveInterval( ImbConstant.SESSION_TIMEOUT * 60 ); // 60분

			//JSessionID를 통안 로그인 방지 - IP, userAgent를 비교할 수 있도록 조합하여 암호화 실시
			sessionFixationService.generationToken(request, response);

//			roleService.removeAuthorites("ROLE_SIMPLE");

			if(userInfo.getPermission().equals(UserInfoBean.UTYPE_ADMIN)){
				roleService.addAuthorites("ROLE_ADMIN");
			}else{
				roleService.addAuthorites("ROLE_USER");
			}

			//log insert start
			ActionLogForm logForm = new ActionLogForm();
			logForm.setIp(HttpRequestUtil.getRemoteAddr(request));
			logForm.setUserid(userSessionInfo.getUserid());
			logForm.setMenu_key("A101");
			logForm.setParam(message.getMessage("E0707","Userid :") + userInfo.getUserid() + " /" + message.getMessage("E0708","접근IP :") + HttpRequestUtil.getRemoteAddr(request) + " /" + message.getMessage("E0709","Permission :") + userInfo.getPermission());
			actionLogService.insertActionLog(logForm);

			//패스워드 변경 필요 여부 확인
			if(ImbConstant.USE_PASSWORD_CHANGE){
				if(accountService.checkRequirePasswordChange(userInfo)){
					Map<String, PasswordRequired> passwordRequiredMap = passwordPolicyService.getPasswordRequired(language);
					//비밀번호 변경 페이지로 이동
					model.addAttribute("UpdatePasswordForm",new UpdatePasswordForm());
					model.addAttribute("passwordRequired",passwordRequiredMap.values());
					return "/jsp/account/password_update";
				}
			}

			session.removeAttribute(Captcha.NAME);
		}catch (NullPointerException ne) {
			String errorId = ErrorTraceLogger.log(ne);
			log.error("{} - LOGIN ERROR",errorId);
			return "redirect:/error/server-error.do";
		}
		catch(Exception e){
			String errorId = ErrorTraceLogger.log(e);
			log.error("{} - LOGIN ERROR",errorId);
			return "redirect:/error/server-error.do";
		}
		model.remove("useCaptcha"); // 캡챠 값은 다 사용하였으므로 파라메터 노출되지 않도록 제거


		return "redirect:/mail/result/list.do";
	}


	/**
	 * 패스워드 변경주기 기능 사용 시, 패스워드 변경하기
	 * @param form
	 * @param bindingResult
	 * @param request
	 * @param session
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "updatePassword.do", method = RequestMethod.POST)
	public String updatePassword(@ModelAttribute("UpdatePasswordForm") UpdatePasswordForm form, BindingResult bindingResult, HttpServletRequest request, HttpSession session,
								 HttpServletResponse response, ModelMap model){

		UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
		String userid = userSessionInfo.getUserid();
		String language = userSessionInfo.getLanguage();

		Map<String, PasswordRequired> passwordRequiredMap = passwordPolicyService.getPasswordRequired(language);
		boolean flag = true; // 패스워드 정책 검사를 위한 flag 값

		form.validate(form,bindingResult);
		if(bindingResult.hasErrors()){
			model.addAttribute("passwordRequired",passwordRequiredMap.values());
			return "/jsp/account/password_update";
		}
		try{
			ImbUserinfo userInfo = userService.getUserInfo(userid);

			//스크립트에서 암호화된 패스워드
			String oldPassword = form.getPassword();
			String newPassword = form.getNewPassword();

			//평문 패스워드로 복호화 실시
			String privateKey = session.getAttribute("privateKey").toString();
			oldPassword = jCryptionService.deCrypt(privateKey, form.getEncAESKey(), oldPassword);
			newPassword = jCryptionService.deCrypt(privateKey, form.getEncAESKey(), newPassword);

			String oldPasswordSalt="";
			String newPasswordSalt="";

			//salt 사용여부에 따른 패스워드값 처리
			if(ImbConstant.PASS_USE_SALT.equals("1")) {
				String salt = userInfo.getSt_data();
				newPasswordSalt = salt + newPassword;
				oldPasswordSalt = salt + oldPassword;
			}else{
				newPasswordSalt = newPassword;
				oldPasswordSalt = oldPassword;
			}
			//암호화 실시한 패스워드
			String newSecuPass = ImSecurityLib.makePassword(userInfo.getPwd_type(), newPasswordSalt, false);
			String oldSecuPass = ImSecurityLib.makePassword(userInfo.getPwd_type(), oldPasswordSalt, false);

			//HISTORY_VIOLATION
			Map<String,PasswordRequired> historyViolationMessage = null;
			if(userInfo.getPasswd().equals(newSecuPass)){
				historyViolationMessage = passwordPolicyService.getRequiredBean("HISTORY_VIOLATION",language);
			}

			Map<String,PasswordRequired> messageList = passwordPolicyService.isValidPassword(userid,newPassword,language);

			if(messageList != null){
				for(Map.Entry<String,PasswordRequired> passwordRequiredEntry : messageList.entrySet() ){
					passwordRequiredMap.put( passwordRequiredEntry.getKey(), passwordRequiredEntry.getValue() );
				}
				flag=false;
			}
			if(historyViolationMessage != null){
				passwordRequiredMap.putAll(historyViolationMessage);
				flag = false;
			}
			if(!userInfo.getPasswd().equals(oldSecuPass)){
				bindingResult.rejectValue("password", "E0083","기존 비밀번호가 일치하지 않습니다.");
				flag=false;
			}
			if(!flag){
				model.addAttribute("passwordRequired",passwordRequiredMap.values());
				return "/jsp/account/password_update";
			}
			accountService.updatePassword(userid,newSecuPass);
		}catch (NullPointerException ne) {
			String errorId = ErrorTraceLogger.log(ne);
			log.error("{} - ChangePassword ERROR",errorId);
			return "redirect:/error/server-error.do";
		}
		catch (Exception e){
			String errorId = ErrorTraceLogger.log(e);
			log.error("{} - ChangePassword ERROR",errorId);
			return "redirect:/error/server-error.do";
		}
		return "redirect:/mail/result/list.do";
	}

	/**
	 * 패스워드 다음에 변경하기 시, redirect 실시
	 * @param form
	 * @param bindingResult
	 * @param request
	 * @param session
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "updatePasswordNext.do")
	public String updatePasswordNext(@ModelAttribute("updatePasswordForm") UpdatePasswordForm form, BindingResult bindingResult, HttpServletRequest request, HttpSession session,
								  HttpServletResponse response, ModelMap model) {

		return "redirect:/mail/result/list.do";
	}





	@RequestMapping(value="infoedit.do",method = RequestMethod.GET)
	public String infoEditForm(HttpSession session ,@ModelAttribute("UserForm") UserForm form,
							   @RequestParam(value = "userid",required = false) String userid, ModelMap model){

		UserInfoBean userInfo = UserInfoBean.getUserSessionInfo(session);
		userid = userInfo.getUserid();
		try{
			ImbUserinfo userinfo = userService.getUserInfo(userid);

			if(userinfo ==null){
				model.addAttribute("infoMessage","아이디 정보가 없습니다.");
			}
			Map<String, PasswordRequired> passwordRequiredMap = passwordPolicyService.getPasswordRequired(userInfo.getLanguage());
			model.addAttribute("passwordRequired",passwordRequiredMap.values());

			if(userinfo != null){
				form = userService.userInfoToForm(userinfo);
				model.addAttribute("UserForm", form);
			}
		}catch (NullPointerException ne) {
			String errorId = com.imoxion.sensems.common.logger.ErrorTraceLogger.log(ne);
			log.error("{} - Get User Edit Page ERROR", errorId);
		}
		catch (Exception e){
			String errorId = com.imoxion.sensems.common.logger.ErrorTraceLogger.log(e);
			log.error("{} - Get User Edit Page ERROR", errorId);
		}
		return "/popup/account/popup_userinfo_edit";
	}



	@RequestMapping(value="infoedit.do",method = RequestMethod.POST)
	@ResponseBody
	public String userEdit(HttpServletRequest request, HttpSession session , @ModelAttribute("UserForm") UserForm form, ModelMap model){
		UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);

		boolean flag = true;

		JSONResult result = new JSONResult();
		try{
			//데이터 형식 검사 실시
			flag = userService.isValidate(form,model,false,null,null);

			if(!flag){
				result.setResultCode(JSONResult.FAIL);
				result.setMessage(message.getMessage("",(String) model.getAttribute("infoMessage")));
				return result.toString();
			}

			//사용자 수정
			userService.modifyUser(form);
			result.setResultCode(JSONResult.SUCCESS);

			//log insert start
			ActionLogForm logForm = new ActionLogForm();
			logForm.setIp(HttpRequestUtil.getRemoteAddr(request));
			logForm.setUserid(userSessionInfo.getUserid());
			logForm.setMenu_key("G103");
			logForm.setParam("수정한 아이디  : " + form.getUserid() + " / 이름 : " + form.getUname() + " / E-MAIL : " + form.getEmail());
			actionLogService.insertActionLog(logForm);

			userSessionInfo.setName(form.getUname());
			userSessionInfo.setDept(form.getDept());
			userSessionInfo.setGrade(form.getGrade());
			userSessionInfo.setEmail(form.getEmail());
			userSessionInfo.setMobile(form.getMobile());

		}catch ( NullPointerException ne ) {
			String errorId = com.imoxion.sensems.common.logger.ErrorTraceLogger.log(ne);
			log.error("{} - INFO EDIT ne ERROR",errorId);

			model.addAttribute("UserForm", form);
			return "/popup/account/popup_userinfo_edit";
		}
		catch (Exception e){
			String errorId = com.imoxion.sensems.common.logger.ErrorTraceLogger.log(e);
			log.error("{} - INFO EDIT ERROR",errorId);

			model.addAttribute("UserForm", form);
			return "/popup/account/popup_userinfo_edit";

		}

		return result.toString();


	}


	@RequestMapping(value="changePwd.json",method = RequestMethod.POST)
	@ResponseBody
	public String changePassword(HttpServletRequest request, HttpSession session , @RequestParam(value = "userid", required = false) String userid,
								 @RequestParam(value = "passwd",required = false) String passwd,
								 @RequestParam(value = "passwd_confirm",required = false) String passwd_confirm,
								 @RequestParam(value = "encAESKey",required = false) String encAESKey, ModelMap model){

		UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
		String language = userSessionInfo.getLanguage();
		JSONResult result = new JSONResult();


		try{
			if(StringUtils.isEmpty(userid)){
				log.info("USERID is Empty");
				result.setResultCode(JSONResult.FAIL);
				result.setMessage(message.getMessage("E0057","선택한 사용자가 없습니다."));
				return result.toString();
			}

			if(StringUtils.isNotEmpty(passwd) && StringUtils.isNotEmpty(passwd_confirm) && StringUtils.isNotEmpty(encAESKey)){
				String privateKey = session.getAttribute("privateKey").toString();
				passwd = jCryptionService.deCrypt(privateKey, encAESKey, passwd);
				passwd_confirm = jCryptionService.deCrypt(privateKey, encAESKey, passwd_confirm);
			}



			ImbUserinfo userInfo = userService.getUserInfo(userid);

			if(userInfo == null) {
				log.debug("Not Exist User - {}", userid);
				result.setResultCode(JSONResult.FAIL);
				result.setMessage(message.getMessage("E0042","등록된 사용자가 없습니다."));
				return result.toString();
			}
			String newPasswordSalt="";
			//salt 사용여부에 따른 패스워드값 처리
			if(ImbConstant.PASS_USE_SALT.equals("1")) {
				String salt = userInfo.getSt_data();
				newPasswordSalt = salt + passwd;
			}else{
				newPasswordSalt = passwd;
			}
			//암호화 실시한 패스워드
			String newSecuPass = ImSecurityLib.makePassword(userInfo.getPwd_type(), newPasswordSalt, false);


			//패스워드 검사
			if( StringUtils.isEmpty(passwd) || StringUtils.isEmpty(passwd_confirm) ) {
				result.setResultCode(JSONResult.FAIL);
				result.setMessage(message.getMessage("E0048","비밀번호를 입력해주세요."));
				return result.toString();
			}else if( !passwd.equals(passwd_confirm)) {
				result.setResultCode(JSONResult.FAIL);
				result.setMessage(message.getMessage("E0049","비밀번호가 일치하지 않습니다."));
				return result.toString();
			}else{
				//HISTORY_VIOLATION
				Map<String,PasswordRequired> historyViolationMessage = null;
				if(userInfo.getPasswd().equals(newSecuPass)){
					historyViolationMessage = passwordPolicyService.getRequiredBean("HISTORY_VIOLATION",language);
				}

				Map<String,PasswordRequired> messageList = passwordPolicyService.isValidPassword(userid,passwd,language);

				if(messageList != null || historyViolationMessage != null){
					result.setResultCode(JSONResult.FAIL);
					result.setMessage(message.getMessage("E0085","패스워드 정책에 맞지 않습니다."));
					return result.toString();
				}
			}

			userService.updatePassword(userInfo,passwd);

			//log insert start
			ActionLogForm logForm = new ActionLogForm();
			logForm.setIp(HttpRequestUtil.getRemoteAddr(request));
			logForm.setUserid(userSessionInfo.getUserid());
			logForm.setMenu_key("G105");
			logForm.setParam("패스워드 변경 아이디  : " + userid);
			actionLogService.insertActionLog(logForm);

			result.setMessage(message.getMessage("E0058","비밀번호가 변경되었습니다."));
			result.setResultCode(JSONResult.SUCCESS);
			return result.toString();

		}catch (NullPointerException ne) {
			String errorId = com.imoxion.sensems.common.logger.ErrorTraceLogger.log(ne);
			log.error("{} - changePwd ne ERROR", errorId);
			result.setResultCode(JSONResult.FAIL);
			result.setMessage(message.getMessage("E0060","작업중 오류가 발생하였습니다."));
			return result.toString();
		}
		catch (Exception e){
			String errorId = com.imoxion.sensems.common.logger.ErrorTraceLogger.log(e);
			log.error("{} - changePwd ERROR", errorId);
			result.setResultCode(JSONResult.FAIL);
			result.setMessage(message.getMessage("E0060","작업중 오류가 발생하였습니다."));
			return result.toString();
		}
	}
	@RequestMapping(value = "json/idCheck.json",method = RequestMethod.GET)
	@ResponseBody
	public String idCheck(HttpSession session, @RequestParam(value = "userid", required = false) String userid) {
		JSONResult result = new JSONResult();
		try{
			if(StringUtils.isEmpty(userid)){
				result.setResultCode(JSONResult.FAIL);
				result.setMessage(message.getMessage("E0053", "아이디를 입력해 주세요."));
				return result.toString();
			}
			if (userService.idCheck(userid)) {
				result.setResultCode(JSONResult.SUCCESS);
			} else {
				result.setResultCode(JSONResult.FAIL);
				result.setMessage(message.getMessage("E0038", "이미 등록되어있거나 사용하실 수 없는 아이디입니다."));
			}
		}catch (NullPointerException ne) {
			String errorId = com.imoxion.sensems.common.logger.ErrorTraceLogger.log(ne);
			log.error("{} - IdCheck ne ERROR", errorId);
			result.setResultCode(JSONResult.FAIL);
			result.setMessage(message.getMessage("E0060","작업중 오류가 발생하였습니다."));
			return result.toString();
		}
		catch (Exception e){
			String errorId = com.imoxion.sensems.common.logger.ErrorTraceLogger.log(e);
			log.error("{} - IdCheck ERROR", errorId);
			result.setResultCode(JSONResult.FAIL);
			result.setMessage(message.getMessage("E0060","작업중 오류가 발생하였습니다."));
			return result.toString();
		}
		return result.toString();
	}

	/**
	 * 샘플파일 다운로드 처리를 행한다.
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "helpDownload.do",method = RequestMethod.GET)
	public ModelAndView sampleDownload(HttpSession session, HttpServletRequest request, HttpServletResponse response, ModelMap model) {

		String savePath = ImbConstant.SENSDATA_PATH + File.separator + "user_data" + File.separator ;

		String fpath = savePath + "SensEMS_v8_Help.pdf";

		File file = new File(fpath);
		if (!file.exists()) {
			// 파일이 존재하지 않을 경우 에러처리
			log.error("Argument is {}", file.toString());
			String msg = message.getMessage("E0430","파일이 존재하지 않습니다.");
			return AlertMessageUtil.getMessageViewOfScript("alert('"+ msg +"')");
		}

		return CommonFile.getDownloadView(file, "SensEMS_v8_Help.pdf");
	}
}
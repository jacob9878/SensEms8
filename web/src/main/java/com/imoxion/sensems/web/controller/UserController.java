package com.imoxion.sensems.web.controller;


import com.imoxion.common.util.ImStringUtil;
import com.imoxion.common.util.ImUtils;
import com.imoxion.security.ImSecurityLib;
import com.imoxion.sensems.common.logger.ErrorTraceLogger;
import com.imoxion.sensems.web.beans.UserInfoBean;
import com.imoxion.sensems.web.common.ImPage;
import com.imoxion.sensems.web.common.ImbConstant;

import com.imoxion.sensems.web.database.domain.ImbUserinfo;
import com.imoxion.sensems.web.form.ActionLogForm;
import com.imoxion.sensems.web.form.UserForm;
import com.imoxion.sensems.web.form.UserListForm;
import com.imoxion.sensems.web.module.passwordpolicy.PasswordPoricyService;
import com.imoxion.sensems.web.module.passwordpolicy.PasswordRequired;
import com.imoxion.sensems.web.service.ActionLogService;
import com.imoxion.sensems.web.service.ImageService;
import com.imoxion.sensems.web.service.JCryptionService;
import com.imoxion.sensems.web.service.TemplateService;
import com.imoxion.sensems.web.service.UserService;
import com.imoxion.sensems.web.util.HttpRequestUtil;
import com.imoxion.sensems.web.util.ImUtility;
import com.imoxion.sensems.web.util.JSONResult;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.xbill.DNS.NULLRecord;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 시스템 관리 > 사용자 관리 컨트롤러
 * @date 2021.02.01
 * @author jhpark
 *
 */
@Controller
@RequestMapping("sysman/user")
public class UserController {

    protected Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private MessageSourceAccessor message;

    @Autowired
    private JCryptionService jCryptionService;

    @Autowired
    private PasswordPoricyService passwordPolicyService;

    @Autowired
    private ActionLogService actionLogService;

    @Autowired
    private ImageService imageService;

    /**
     * 사용자 검색 및 목록 획득
     * @param form
     * @param session
     * @param model
     * @return
     */
    @RequestMapping(value="list.do")
    public String userList(@ModelAttribute("UserListForm") UserListForm form, HttpServletRequest request, HttpSession session, ModelMap model,
                           HttpServletResponse response){

        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-control", "no-cache");

        //페이지 목록 개수 세션에서 획득
        UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);

        try{
            String userid = userSessionInfo.getUserid();

            String srch_type = form.getSrch_type();
            String srch_keyword = form.getSrch_keyword();
            String permission = form.getPermission();
            String isStop = form.getIsStop();
            String use_smtp = form.getUse_smtp();


             int totalsize =0;
            int cpage = ImStringUtil.parseInt(form.getCpage());
            int pageGroupSize =  ImStringUtil.parseInt(form.getPagegroupsize());
            model.addAttribute("cpage", cpage);
            boolean issearch = true;
            if(StringUtils.isEmpty(srch_keyword)) {
                issearch = false;
            }
            model.addAttribute("issearch", issearch);


            //검색 결과 사용자 수
            totalsize = userService.getUserCount(srch_type,srch_keyword,permission,isStop,use_smtp);
            //페이지 정보
            ImPage pageInfo = new ImPage(cpage, userSessionInfo.getPagesize(), totalsize, pageGroupSize);
            //유저정보 획득
            List<ImbUserinfo> userList = userService.getUserListForPageing(srch_type,srch_keyword,permission,isStop,use_smtp, pageInfo.getStart(), pageInfo.getEnd());

            model.addAttribute("srch_key", srch_keyword);
            model.addAttribute("srch_type", srch_type);
            model.addAttribute("totalsize",totalsize);
            model.addAttribute("pageInfo", pageInfo);
            model.addAttribute("userList", userList);

            //log insert start
            // TODO 로그정책 변경으로 삭제 (사용자관리>조회 G101)

        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("{} - USER LIST ne ERROR", errorId);
        }
        catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - USER LIST ERROR", errorId);
        }
        model.addAttribute("UserListForm",form);

        return "/sysman/user_list";
    }

    /**
     * 사용자 추가 페이지로 이동
     * @param session
     * @param form
     * @param model
     * @return
     */
    @RequestMapping(value="add.do",method = RequestMethod.GET)
    public String userAddForm(HttpSession session ,HttpServletRequest request ,@ModelAttribute("UserForm") UserForm form, ModelMap model){
        UserInfoBean userInfo = UserInfoBean.getUserSessionInfo(session);
        Map<String, PasswordRequired> passwordRequiredMap = passwordPolicyService.getPasswordRequired(userInfo.getLanguage());
        String cpage = request.getParameter("cpage");
        model.addAttribute("cpage", cpage);
        model.addAttribute("passwordRequired",passwordRequiredMap.values());

        model.addAttribute("UserForm", form);

        return "/sysman/user_add";
    }

    /**
     * 사용자 추가 동작
     * @param session
     * @param form
     * @param model
     * @return
     */
    @RequestMapping(value="add.do",method = RequestMethod.POST)
    public String userAdd(HttpServletRequest request, HttpSession session , @ModelAttribute("UserForm") UserForm form,
                          BindingResult bindingResult, ModelMap model){
        UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
        String language = userSessionInfo.getLanguage();
        boolean flag = false; // 데이터 형식 검사 결과값
        Map<String,PasswordRequired> passwordRequiredMap = passwordPolicyService.getPasswordRequired(language);
        try{
            if(StringUtils.isNotEmpty(form.getPasswd()) && StringUtils.isNotEmpty(form.getPasswd_confirm()) && StringUtils.isNotEmpty(form.getEmail()) && StringUtils.isNotEmpty(form.getEncAESKey())){
                String password = form.getPasswd();
                String password_confirm = form.getPasswd_confirm();
                String email = form.getEmail();
                String privateKey = session.getAttribute("privateKey").toString();
                password = jCryptionService.deCrypt(privateKey, form.getEncAESKey(), password);
                password_confirm = jCryptionService.deCrypt(privateKey, form.getEncAESKey(), password_confirm);
                email = jCryptionService.deCrypt(privateKey, form.getEncAESKey(), email);
                form.setPasswd(password);
                form.setPasswd_confirm(password_confirm);
                form.setEmail(email);
                form.setUse_smtp(form.getUse_smtp());

                if(StringUtils.isNotEmpty(form.getTel())){ // 전화번호가 null이 아니면 복호화 진행
                    String tel = jCryptionService.deCrypt(privateKey, form.getEncAESKey(), form.getTel());
                    form.setTel(tel);
                }
                if(StringUtils.isNotEmpty(form.getMobile())){ // 휴대폰번호가 null이 아니면 복호화 진행
                    String mobile = jCryptionService.deCrypt(privateKey, form.getEncAESKey(), form.getMobile());
                    form.setMobile(mobile);
                }
                if(StringUtils.isNotEmpty(form.getApprove_email())){ // 승인메일이 null이 아니면 복호화 진행
                    String apmail = jCryptionService.deCrypt(privateKey, form.getEncAESKey(), form.getApprove_email());
                    form.setApprove_email(apmail);
                }
            }


            //데이터 형식 검사 실시
            flag = userService.isValidate(form,model,true,passwordRequiredMap,language);

            if(!flag){
                model.addAttribute("UserForm", form);
                //비밀번호 변경 페이지로 이동
                model.addAttribute("passwordRequired",passwordRequiredMap.values());
                return "/sysman/user_add";
            }

            String passwordSalt="";
            if(ImbConstant.PASS_USE_SALT.equals("1")) {
                String saltKey = ImUtils.makeKeyNum(32);
                log.debug("Make Salt Key - {}",saltKey);
                passwordSalt = saltKey + form.getPasswd();
                form.setSaltKey(saltKey);
                form.setPasswd(passwordSalt);
            }else{
                passwordSalt = form.getPasswd();
            }
            //사용자 추가 실시
            userService.addUser(form);

            //log insert start
            ActionLogForm logForm = new ActionLogForm();
            logForm.setIp(HttpRequestUtil.getRemoteAddr(request));
            logForm.setUserid(userSessionInfo.getUserid());
            logForm.setMenu_key("G102");
            logForm.setParam("추가한 아이디  : " + form.getUserid() + " / 이름 : " + form.getUname() + " / E-MAIL : " + form.getEmail());
            actionLogService.insertActionLog(logForm);

        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("{} - USER ADD ne ERROR",errorId);
        }
        catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - USER ADD ERROR",errorId);

            model.addAttribute("UserForm", form);
            return "/sysman/user_add";
        }


        return "redirect:/sysman/user/list.do";
    }

    /**
     * 사용자 추가 시 아이디 중복 체크 로직
     * @param session
     * @param userid
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "json/idCheck.json",method = RequestMethod.GET)
    @ResponseBody
    public String idCheck(HttpSession session, @RequestParam(value = "userid", required = false) String userid) {
        JSONResult result = new JSONResult();
        try{
            if(!userService.idCheck(userid)){
                result.setResultCode(JSONResult.FAIL);
                result.setMessage(message.getMessage("E0722", "이미 등록된 아이디입니다."));
                return result.toString();
            }else if(!ImUtility.validCharacter2(userid)){
                result.setResultCode(JSONResult.FAIL);
                result.setMessage(message.getMessage("E0738", "특수문자는 (_) , (,) , (-) 만 허용합니다."));
                return result.toString();
            } else if(!ImUtility.validFormatChar(userid)){
                result.setResultCode(JSONResult.FAIL);
                result.setMessage(message.getMessage("E0034", "15자까지 영문,숫자만 가능합니다."));
                return result.toString();
            }
            result.setResultCode(JSONResult.SUCCESS);
        }catch (NullPointerException | BadSqlGrammarException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("{} - IdCheck ERROR", errorId);
            result.setResultCode(JSONResult.FAIL);
            result.setMessage(message.getMessage("E0060","작업중 오류가 발생하였습니다."));
            return result.toString();
        }
        catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - IdCheck ERROR", errorId);
            result.setResultCode(JSONResult.FAIL);
            result.setMessage(message.getMessage("E0060","작업중 오류가 발생하였습니다."));
            return result.toString();
        }
        return result.toString();
    }


    /**
     * 사용자 수정 페이지 이동
     * @param session
     * @param form
     * @param userid
     * @param model
     * @return
     */
    @RequestMapping(value="edit.do",method = RequestMethod.GET)
    public String userEditForm(HttpSession session, HttpServletRequest request ,@ModelAttribute("UserForm") UserForm form,
                               @RequestParam(value = "userid",required = false) String userid, ModelMap model){

        UserInfoBean userInfo = UserInfoBean.getUserSessionInfo(session);
        if(StringUtils.isEmpty(userid)){
            return "redirect:/sysman/user/list.do";
        }

        try{
            ImbUserinfo userinfo = userService.getUserInfo(userid);
            String cpage = request.getParameter("cpage");
            String srch_keyword = request.getParameter("srch_keyword");
            String srch_type = request.getParameter("srch_type");
            model.addAttribute("cpage", cpage);
            model.addAttribute("srch_keyword", srch_keyword);
            model.addAttribute("srch_type", srch_type);

            if(userinfo ==null){
                return "redirect:/sysman/user/list.do";
            }
            Map<String, PasswordRequired> passwordRequiredMap = passwordPolicyService.getPasswordRequired(userInfo.getLanguage());
            model.addAttribute("passwordRequired",passwordRequiredMap.values());

            form = userService.userInfoToForm(userinfo);
            model.addAttribute("UserForm", form);
        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("{} - Get User Edit Page ne ERROR", errorId);
        }
        catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - Get User Edit Page ERROR", errorId);
        }
        return "/sysman/user_edit";
    }

    /**
     * 사용자 정보 수정 동작
     * @param session
     * @param form
     * @param model
     * @return
     */
    @RequestMapping(value="edit.do",method = RequestMethod.POST)
    public String userEdit(HttpServletRequest request, HttpSession session , @ModelAttribute("UserForm") UserForm form, ModelMap model){
        UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
        boolean flag = true;

        try{
            if(StringUtils.isNotEmpty(form.getEmail())){
                String email = form.getEmail();
                String ori_email = form.getOri_email(); //변경 전 이메일
                String privateKey = session.getAttribute("privateKey").toString();
                email = jCryptionService.deCrypt(privateKey, form.getEncAESKey(), email);
                ori_email = jCryptionService.deCrypt(privateKey, form.getEncAESKey(), ori_email.trim()); // 변경 전 이메일 복호화 시 trim 해주지 않으면 에러발생
                form.setEmail(email);
                form.setOri_email(ori_email);

                if(StringUtils.isNotEmpty(form.getTel())){ // 전화번호가 null이 아니면 복호화 진행
                    String tel = jCryptionService.deCrypt(privateKey, form.getEncAESKey(), form.getTel());
                    form.setTel(tel);
                }
                if(StringUtils.isNotEmpty(form.getMobile())){ // 휴대폰번호가 null이 아니면 복호화 진행
                    String mobile = jCryptionService.deCrypt(privateKey, form.getEncAESKey(), form.getMobile());
                    form.setMobile(mobile);
                }
                if(StringUtils.isNotEmpty(form.getApprove_email())){ // 승인메일이 null이 아니면 복호화 진행
                    String apmail = jCryptionService.deCrypt(privateKey, form.getEncAESKey(), form.getApprove_email());
                    form.setApprove_email(apmail);
                }
            }



            //데이터 형식 검사 실시
            flag = userService.isValidate(form,model,false,null,null);

            if(!flag){
                model.addAttribute("UserForm", form);
                return "/sysman/user_edit";
            }

            //사용자 수정
            userService.modifyUser(form);

            //log insert start
            ActionLogForm logForm = new ActionLogForm();
            logForm.setIp(HttpRequestUtil.getRemoteAddr(request));
            logForm.setUserid(userSessionInfo.getUserid());
            logForm.setMenu_key("G103");
            String param = "";
            if(StringUtils.equals(form.getOri_name(), form.getUname())){
                param = "수정한 아이디  : " + form.getUserid() + " / 이름 : " + form.getUname();
            }else{
                param = "수정한 아이디  : " + form.getUserid() + " / 이전 이름 : " + form.getOri_name() + " / 변경 이름 : " + form.getUname();
            }
            if(StringUtils.equals(form.getOri_email(),form.getEmail())){
                param += " / E-MAIL : " + form.getEmail();
            }else {
                param += " / 이전 E-MAIL : " + form.getOri_email() + " / 변경 E-MAIL : " + form.getEmail();
            }
            logForm.setParam(param);
            actionLogService.insertActionLog(logForm);

        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("{} - USER ADD ne ERROR",errorId);

            model.addAttribute("UserForm", form);
            return "/sysman/user_edit";
        }
        catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - USER ADD ERROR",errorId);

            model.addAttribute("UserForm", form);
            return "/sysman/user_edit";
        }

        return "redirect:/sysman/user/list.do";
    }

    /**
     * 사용자 수정 > 비밀번호 변경
     * @param session
     * @param userid
     * @param passwd
     * @param passwd_confirm
     * @param encAESKey
     * @param model
     * @return
     */
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

        }catch (BadSqlGrammarException | NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("{} - changePwd ne ERROR", errorId);
            result.setResultCode(JSONResult.FAIL);
            result.setMessage(message.getMessage("E0060","작업중 오류가 발생하였습니다."));
            return result.toString();
        }
        catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - changePwd ERROR", errorId);
            result.setResultCode(JSONResult.FAIL);
            result.setMessage(message.getMessage("E0060","작업중 오류가 발생하였습니다."));
            return result.toString();
        }
    }

    /**
     * 사용자 목록 > 선택한 사용자 삭제 동작
     * @param session
     * @param userids
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "deleteUsers.json", method = RequestMethod.POST)
    @ResponseBody
    public String userDelete(HttpServletRequest request, HttpSession session, @RequestParam(value = "userid", required = false) String[] userids, ModelMap model) {
        UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
        JSONResult result = new JSONResult();
        try {
            if(userids == null || userids.length==0){
                result.setResultCode(JSONResult.FAIL);
                result.setMessage(message.getMessage("E0059","삭제할 사용자를 선택해주세요."));
                return result.toString();
            }

            for (int i = 0; i < userids.length; i++) {
                imageService.deleteImageUkey(request, userids[i]);
            }
            userService.deleteUserList(request,userSessionInfo.getUserid(),userids);


            result.setResultCode(JSONResult.SUCCESS);
            result.setMessage(message.getMessage("E0070","삭제되었습니다."));
        }catch (NullPointerException | BadSqlGrammarException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("{} - USER Delete ne ERROR", errorId);
            result.setResultCode(JSONResult.FAIL);
            result.setMessage(message.getMessage("E0060","작업중 오류가 발생하였습니다."));
            return result.toString();
        }
        catch (Exception e) {
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - USER Delete ERROR", errorId);
            result.setResultCode(JSONResult.FAIL);
            result.setMessage(message.getMessage("E0060","작업중 오류가 발생하였습니다."));
            return result.toString();
        }
        return result.toString();
    }

//    @RequestMapping(value = "use.json", method = RequestMethod.GET)
//    @ResponseBody
//    public String activeDKIM(HttpSession session, @RequestParam("userid") String userid,
//                             @RequestParam("use_smtp") int use_smtp,ModelMap model) {
//
//        JSONResult result = new JSONResult();
//
//        try {
//            userService.updateUse(userid, use_smtp);
//
//            if ("1".equals(use_smtp)) {
//                result.setResultCode(JSONResult.SUCCESS);
//                result.setMessage(message.getMessage("E0057","사용으로 변경하였습니다."));
//            } else {
//                result.setResultCode(JSONResult.SUCCESS);
//                result.setMessage(message.getMessage("E0057","사용안함으로 변경하였습니다."));
//
//            }
//
//        } catch (Exception e) {
//            String errorId = ErrorTraceLogger.log(e);
//            log.error("{} - dkim use update error", errorId);
//            result.setResultCode(JSONResult.FAIL);
//            result.setMessage(message.getMessage("E0057","사용여부를 변경하지 못하였습니다."));
//
//        }
//
//        return result.toString();
//    }



}

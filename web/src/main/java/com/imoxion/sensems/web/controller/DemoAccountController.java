package com.imoxion.sensems.web.controller;


import com.imoxion.common.util.ImCheckUtil;
import com.imoxion.common.util.ImStringUtil;
import com.imoxion.sensems.common.logger.ErrorTraceLogger;
import com.imoxion.sensems.web.beans.UserInfoBean;
import com.imoxion.sensems.web.common.ImPage;
import com.imoxion.sensems.web.database.domain.ImbDemoAccount;
import com.imoxion.sensems.web.form.ActionLogForm;
import com.imoxion.sensems.web.form.DemoAccountForm;
import com.imoxion.sensems.web.service.ActionLogService;
import com.imoxion.sensems.web.service.DemoAccountService;
import com.imoxion.sensems.web.util.HttpRequestUtil;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("send/demoaccount")
public class DemoAccountController {

    protected Logger log = LoggerFactory.getLogger( DemoAccountController.class );

    @Autowired
    private DemoAccountService demoAccountService;

    @Autowired
    private ActionLogService actionLogService;

    @Autowired
    private MessageSourceAccessor message;


    /**
     * 테스트 계정 리스트
     * @param session
     * @param form
     * @param model
     * @return
     */
    @RequestMapping(value = "list.do")
    public String listDemoAccont(HttpSession session, @ModelAttribute("demoAccountForm") DemoAccountForm form, ModelMap model, HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {

        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-control", "no-cache");

        UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);

        try{
            boolean issearch = true;
            if(StringUtils.isEmpty(form.getSrch_keyword())) {
                issearch = false;
            }
            model.addAttribute("issearch", issearch);


            int total = demoAccountService.getDemoAccountCount(userSessionInfo.getUserid(), form.getSrch_keyword());

            ImPage pageinfo = new ImPage(ImStringUtil.parseInt(form.getCpage()), userSessionInfo.getPagesize(),
                    total, ImStringUtil.parseInt(form.getPage_groupsize()));

            List<ImbDemoAccount> demoAccountList = null;
            demoAccountList = demoAccountService.getDemoAccountList(userSessionInfo.getUserid(), form.getSrch_keyword(), pageinfo.getStart(), pageinfo.getEnd());

            model.addAttribute("pageInfo", pageinfo);
            model.addAttribute("demoAccountList", demoAccountList);

            //log insert
            // TODO 로그정책 변경으로 삭제 (테스트계정관리>조회 F401)

        }
        catch (NullPointerException | BadSqlGrammarException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("{} - DEMOACCOUNT LIST ERROR", errorId);
        } catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - DEMOACCOUNT LIST ERROR", errorId);
        }

        return "/send/demoAccount_list";

    }

    /**
     * 테스트 계정 추가
     * @param session
     * @param form
     * @param model
     * @return
     */
    @RequestMapping(value="add.json", method= RequestMethod.POST)
    @ResponseBody
    public String addDemoAccount(@ModelAttribute("demoAccountForm") DemoAccountForm form, HttpSession session, ModelMap model, HttpServletRequest request){
        UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);

        JSONObject result = new JSONObject();
        if (StringUtils.isBlank(form.getEmail())) {
            result.put("result", false);
            result.put("message", message.getMessage("E0054","E-MAIL을 입력해 주세요."));
            return result.toString();
        }

        try {
            form.setUserid(userSessionInfo.getUserid());
            if(demoAccountService.getDemoAccountByEmail(form.getEmail(), form.getUserid())>0){
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

            demoAccountService.addDemoAccount(form);

            result.put("result", true);
            result.put("message", message.getMessage("E0061","추가되었습니다."));

            //log insert
            ActionLogForm logForm = new ActionLogForm();
            logForm.setIp(HttpRequestUtil.getRemoteAddr(request));
            logForm.setUserid(userSessionInfo.getUserid());
            logForm.setMenu_key("F402");
            logForm.setParam("추가 email : " + form.getEmail() + " / flag : " + form.getFlag());
            actionLogService.insertActionLog(logForm);

        }catch (NullPointerException | BadSqlGrammarException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("Add DemoAccount Error : {}", errorId);
            result.put("result", false);
            result.put("message", message.getMessage("E0060","작업중 오류가 발생하였습니다."));
            return result.toString();
        } catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("Add DemoAccount Error : {}", errorId);
            result.put("result", false);
            result.put("message", message.getMessage("E0060","작업중 오류가 발생하였습니다."));
            return result.toString();
        }

        return result.toString();
    }


    /**
     * 테스트 계정 수정
     * @param session
     * @param form
     * @param model
     * @return
     */
    @RequestMapping(value="edit.json", method = RequestMethod.POST)
    @ResponseBody
    public String editDemoAccount(@ModelAttribute("demoAccountForm") DemoAccountForm form, HttpSession session, ModelMap model, HttpServletRequest request,
                                  @RequestParam(value = "ori_email", required = false) String ori_email){
        UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
        JSONObject result = new JSONObject();
        String userid = userSessionInfo.getUserid();

        if (StringUtils.isBlank(form.getUkey())) {
            result.put("result", false);
            result.put("message", message.getMessage("E0063","필수 정보가 누락되었습니다."));
            return result.toString();
        }

        if (StringUtils.isBlank(form.getEmail())) {
            result.put("result", false);
            result.put("message", message.getMessage("E0054","E-MAIL을 입력해 주세요."));
            return result.toString();
        }

        try {
            form.setUserid(userid);
            if(demoAccountService.selectEditDemoAccount(form.getUkey(),form.getEmail())>0){
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

            demoAccountService.editDemoAccount(form);

            result.put("result", true);
            result.put("message", message.getMessage("E0062","수정되었습니다."));

            //log insert
            ActionLogForm logForm = new ActionLogForm();
            logForm.setIp(HttpRequestUtil.getRemoteAddr(request));
            logForm.setUserid(userSessionInfo.getUserid());
            logForm.setMenu_key("F403");
            if(StringUtils.equals(form.getEmail(),ori_email)){
                logForm.setParam("수정 email : " + form.getEmail() + " / flag : " + form.getFlag());
            }else{
                logForm.setParam("이전 email : " + ori_email + "/ 변경 email : "+ form.getEmail() +  " / flag : " + form.getFlag());
            }

            actionLogService.insertActionLog(logForm);

        }
        catch (NullPointerException | BadSqlGrammarException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("Edit DemoAccount Error : {}", errorId);
            result.put("result", false);
            result.put("message", message.getMessage("E0060","작업중 오류가 발생하였습니다."));
            return result.toString();
        } catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("Edit DemoAccount Error : {}", errorId);
            result.put("result", false);
            result.put("message", message.getMessage("E0060","작업중 오류가 발생하였습니다."));
            return result.toString();
        }

        return result.toString();
    }


    /**
     * 테스트 계정 삭제
     * @param session
     * @param ukeys
     * @return
     */
    @RequestMapping(value="delete.json", method = RequestMethod.POST)
    @ResponseBody
    public String deleteDemoAccount(HttpSession session, @RequestParam(value = "ukeys[]", required = false) String[] ukeys, HttpServletRequest request){
        UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);

        JSONObject result = new JSONObject();
        if (ukeys == null) {
            result.put("result", false);
            result.put("message", message.getMessage("E0063","필수 정보가 누락되었습니다."));
            return result.toString();
        }

        try {
            String param = "";
            for (int i = 0; i < ukeys.length; i++) {
                demoAccountService.deleteDemoAccount(ukeys[i].split(",")[0]);
                if(i==0){
                    param= "삭제한 email : " + ukeys[i].split(",")[1] + " / 삭제한 ukey : " + ukeys[i].split(",")[0];
                }else {
                    param += " , 삭제한 email : " + ukeys[i].split(",")[1] + " / 삭제한 ukey : " + ukeys[i].split(",")[0];
                }
            }

            result.put("result", true);
            result.put("message", message.getMessage("E0070","삭제되었습니다."));

            //log insert start
            ActionLogForm logForm = new ActionLogForm();
            logForm.setIp(HttpRequestUtil.getRemoteAddr(request));
            logForm.setUserid(userSessionInfo.getUserid());
            logForm.setMenu_key("F404");
            logForm.setParam(param);
            actionLogService.insertActionLog(logForm);

        }catch (JSONException je) {
            String errorId = ErrorTraceLogger.log(je);
            log.error("JSON Object Error : {}", errorId);
            result.put("result", false);
            result.put("message", message.getMessage("E0060","작업중 오류가 발생하였습니다."));
            return result.toString();
        }
        catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("NullPoint Error : {}", errorId);
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

        return result.toString();
    }


    /**
     * 테스트 계정 추가
     * @param session
     * @param form
     * @param model
     * @return
     */
    @RequestMapping(value="list.json", method= RequestMethod.GET)
    @ResponseBody
    public String DemoAccountList(@ModelAttribute("demoAccountForm") DemoAccountForm form, HttpSession session, ModelMap model){
        UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);

        JSONObject result = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();

        try {
            List<ImbDemoAccount> demoAccountList = null;
            demoAccountList = demoAccountService.getDemoAccountListForUserid(userSessionInfo.getUserid());

            int size = demoAccountList.size();

            for(int i=0; i < size; i++){
                jsonObject.clear();
                jsonObject.put("email", demoAccountList.get(i).getEmail());
                jsonObject.put("flag", demoAccountList.get(i).getFlag());

                jsonArray.add(jsonObject);
            }

            result.put("result", true);
            result.put("demoAccountList", jsonArray);

        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error(" DemoAccount Get List  Error : {}", errorId);
            result.put("result", false);
            result.put("message", message.getMessage("E0060","작업중 오류가 발생하였습니다."));
            return result.toString();
        }
        catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error(" DemoAccount Get List  Error : {}", errorId);
            result.put("result", false);
            result.put("message", message.getMessage("E0060","작업중 오류가 발생하였습니다."));
            return result.toString();
        }

        return result.toString();

    }


}

package com.imoxion.sensems.web.controller;

import com.imoxion.common.util.ImStringUtil;
import com.imoxion.sensems.common.logger.ErrorTraceLogger;
import com.imoxion.sensems.web.beans.UserInfoBean;
import com.imoxion.sensems.web.common.ImPage;
import com.imoxion.sensems.web.database.domain.ImbSendFilter;
import com.imoxion.sensems.web.form.ActionLogForm;
import com.imoxion.sensems.web.form.SendFilterForm;
import com.imoxion.sensems.web.service.ActionLogService;
import com.imoxion.sensems.web.service.SendFilterService;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * yeji
 * 2021. 02. 26
 * 발송차단 설정 관리 Controller
 */
@Controller
@RequestMapping("/sysman/sendfilter/")
public class SendFilterController {

    protected Logger log = LoggerFactory.getLogger( SendFilterController.class );

    /* 발송차단설정 관련 DAO */
    @Autowired
    private SendFilterService sendFilterService;


    @Autowired
    private MessageSourceAccessor message;

    @Autowired
    private ActionLogService actionLogService;

    /**
     * 초기화면 및 페이징, 검색 목록 조회
     * @param session
     * @param form
     * @param model
     * @return
     */
    @RequestMapping(value="list.do")
    public String list(HttpSession session, @ModelAttribute("sendFilterForm") SendFilterForm form, ModelMap model, HttpServletRequest request,
                       HttpServletResponse response){

        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-control", "no-cache");

        UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
        try{
            String srch_keyword = form.getSrch_keyword();
            if(StringUtils.isNotEmpty(srch_keyword)){
                srch_keyword = srch_keyword.trim();
            }
            int totalSize = 0;
            Integer cpage = ImStringUtil.parseInt(form.getCpage());
            Integer pageGroupSize = ImStringUtil.parseInt(form.getPagegroupsize());

            boolean issearch = true;
            if(StringUtils.isEmpty(srch_keyword)) {
                issearch = false;
            }
            model.addAttribute("issearch", issearch);

            // 페이징 처리를 위한 총 갯수 가져오기
            totalSize = sendFilterService.getSendFilterCount(srch_keyword);

            ImPage pageInfo = new ImPage(cpage, userSessionInfo.getPagesize(), totalSize, pageGroupSize);
            model.addAttribute("pageInfo", pageInfo);

            List<ImbSendFilter> sendFilterList = null;
            sendFilterList = sendFilterService.getSendFilterList(srch_keyword, pageInfo.getStart(), pageInfo.getEnd());
            model.addAttribute("sendFilterList", sendFilterList);

            //log insert start
            // TODO 로그정책 변경으로 삭제 (발송차단설정>조회 G201)

        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("SendFilter List ne Error : {}", errorId);
        }
        catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("SendFilter List Error : {}", errorId);
        }
        return "/sysman/sendfilter_list";
    }

    /**
     * 발송차단 할 도메인 추가
     * @param session
     * @param hostname
     * @return
     */
    @ResponseBody
    @RequestMapping(value="add.json", method=RequestMethod.POST)
    public String addSendFilter(@RequestParam(value = "hostname", required = false) String hostname, HttpSession session, HttpServletRequest request){
        JSONObject jsonResult = new JSONObject();
        //1.null체크
        if (StringUtils.isEmpty(hostname)) {
            jsonResult.put("result", false);
            jsonResult.put("message", message.getMessage("E0111","발송차단할 도메인을 입력해주세요."));
            return jsonResult.toString();
        }
        try {
            //2.중복체크
            if (sendFilterService.checkExisSendFilter(hostname)) {
                jsonResult.put("result", false);
                jsonResult.put("message", message.getMessage("E0112","이미 존재하는 도메인입니다."));
                return jsonResult.toString();
            }

            if(!ImUtility.validCharacter(hostname)){
                jsonResult.put("result", false);
                jsonResult.put("message", message.getMessage("E0737","사용할 수 없는 특수문자가 포함되어 있습니다. (사용가능한 특수 문자 : (_) , (.) , (-) )"));
                return jsonResult.toString();
            }

            //3.추가 수행
            sendFilterService.addSendFilter(hostname);
            log.debug("hostname - {}", hostname);

            // TODO : ActionLog 추가
            ActionLogForm logForm = new ActionLogForm();
            UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
            logForm.setIp(HttpRequestUtil.getRemoteAddr(request));
            logForm.setUserid(userSessionInfo.getUserid());
            logForm.setMenu_key("G202");
            logForm.setParam("추가 도메인 : " + hostname);
            actionLogService.insertActionLog(logForm);

        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("Add SendFilter ne Error : {}", errorId);
            jsonResult.put("result", false);
            jsonResult.put("message", message.getMessage("E0060","작업중 오류가 발생하였습니다."));
            return jsonResult.toString();
        }
        catch (Exception e) {
            String errorId = ErrorTraceLogger.log(e);
            log.error("Add SendFilter Error : {}", errorId);
            jsonResult.put("result", false);
            jsonResult.put("message", message.getMessage("E0060","작업중 오류가 발생하였습니다."));
            return jsonResult.toString();
        }

        jsonResult.put("result", true);
        jsonResult.put("message", message.getMessage("E0061","추가되었습니다."));
        return jsonResult.toString();
    }

    /**
     * 발송차단 할 도메인 삭제
     * @param hostnames
     * @return
     */
    @ResponseBody
    @RequestMapping(value="delete.json", method=RequestMethod.POST)
    public String delSendFilter( @RequestParam(value = "hostnames[]", required = false) String[] hostnames, HttpSession session, HttpServletRequest request){
        JSONObject result = new JSONObject();
        try {
            String param = "";
            if(hostnames != null && hostnames.length > 0){
                for (int i = 0; i < hostnames.length; i++) {
                    sendFilterService.deleteSendFilter(hostnames[i]);
                    param += hostnames[i];
                    if(i == hostnames.length-1) break;
                    param += ", ";
                }
            }

            ActionLogForm logForm = new ActionLogForm();
            UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
            logForm.setIp(HttpRequestUtil.getRemoteAddr(request));
            logForm.setUserid(userSessionInfo.getUserid());
            logForm.setMenu_key("G203");
            logForm.setParam("삭제한 도메인명 : " + param);
            actionLogService.insertActionLog(logForm);

        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("Delete SendFilter ne Error : {}", errorId);
            result.put("result", false);
            result.put("message", message.getMessage("E0060","작업중 오류가 발생하였습니다."));
            return result.toString();
        }
        catch (Exception e) {
            String errorId = ErrorTraceLogger.log(e);
            log.error("Delete SendFilter Error : {}", errorId);
            result.put("result", false);
            result.put("message", message.getMessage("E0060","작업중 오류가 발생하였습니다."));
            return result.toString();
        }

        result.put("result", true);
        result.put("message", message.getMessage("E0070","삭제되었습니다."));
        return result.toString();
    }

}

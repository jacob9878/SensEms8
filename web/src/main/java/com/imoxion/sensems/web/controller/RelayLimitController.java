
package com.imoxion.sensems.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.imoxion.sensems.web.beans.UserInfoBean;
import com.imoxion.sensems.web.exception.message.LimitErrorMessage;
import com.imoxion.sensems.web.form.ActionLogForm;
import com.imoxion.sensems.web.service.ActionLogService;
import com.imoxion.sensems.web.util.HttpRequestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import com.imoxion.sensems.common.logger.ErrorTraceLogger;
import com.imoxion.sensems.web.form.RelayLimitValueForm;
import com.imoxion.sensems.web.service.RelayLimitValueService;

import net.sf.json.JSONObject;



/**
 *송신제한 설정 Controller
 *
 * @author minideji
 *
 */

@Controller
@RequestMapping("sysman/limit")
public class RelayLimitController {

    private Logger log = LoggerFactory.getLogger(RelayLimitController.class);


    @Autowired
    private RelayLimitValueService relayLimitValueService;

    @Autowired
    private ActionLogService actionLogService;

    @Autowired
    private MessageSourceAccessor message;

    @Autowired
    private LimitErrorMessage limitErrorMessage;

/**
     * 송신제한 설정 목록
     *
     * @param session
     * @param request
     * @param response
     * @param form
     * @param model
     * @return
     * @throws Exception
     */

    @RequestMapping(value="list.do",method = {RequestMethod.GET ,RequestMethod.POST})
    public String getSystemLimitValue(HttpSession session, HttpServletRequest request, HttpServletResponse response,
                                      @ModelAttribute("relayLimitValueForm") RelayLimitValueForm form, ModelMap model) throws Exception{

        try {
            form = relayLimitValueService.getLimitValueList();
        }catch (NullPointerException ne) {
            log.error("getSystemLimitValue ne error");
        }
        catch (Exception e) {
            log.error("getSystemLimitValue error");

        }

        model.addAttribute("relayLimitValueForm", form);

        return "/sysman/limitValueList";
    }

/**
     * 송신제한 설정 데이터 수정 처리
     */

    @ResponseBody
    @RequestMapping(value = "edit.do", method = RequestMethod.POST)
    public String edit(HttpServletRequest request,HttpSession session, @RequestParam(value="limit_value", required = false) String limit_value,
                       @RequestParam(value="limit_type", required = false) String limit_type, @RequestParam(value="ord_limit_value", required = false) String ord_limit_value,
                       @RequestParam(value="descript", required = false) String descript) throws Exception {
        UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
        JSONObject result = new JSONObject();
        try {
            if(limit_value == null || limit_value.isEmpty()){
                result.put("result", false);
                result.put("message", message.getMessage("156", "한계값을 입력하세요."));
            }else if(!relayLimitValueService.isnumber(limit_value)){
                result.put("result", false);
                result.put("message", message.getMessage("157", "한계값은 유효한 숫자만 가능합니다."));
            }else{
                relayLimitValueService.updateLimitValue(limit_value,limit_type);
                result.put("result", true);

                //log insert
                ActionLogForm logForm = new ActionLogForm();
                logForm.setIp(HttpRequestUtil.getRemoteAddr(request));
                logForm.setUserid(userSessionInfo.getUserid());
                logForm.setMenu_key("H301");
                logForm.setParam("설명 : " + descript + " / 이전 한계값 : " + ord_limit_value + " / 변경 한계값 : " + limit_value);
                actionLogService.insertActionLog(logForm);
            }

        }catch (NullPointerException ne) {
            log.error("LIMIT VALUE NULL ERROR");
        }
        catch (Exception e) {
            log.error("INCORRECT LIMIT VALUE ERROR");
            result.put("message", limitErrorMessage.getMessage(limit_type));
        }
        return result.toString();
    }

}


package com.imoxion.sensems.web.controller;

import com.imoxion.sensems.common.logger.ErrorTraceLogger;
import com.imoxion.sensems.web.beans.UserInfoBean;
import com.imoxion.sensems.web.common.SessionAttributeNames;
import com.imoxion.sensems.web.util.JSONResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("common")
public class CommonController {

    protected Logger log = LoggerFactory.getLogger(CommonController.class);

    @Autowired
    private MessageSourceAccessor message;

    @RequestMapping(value = "changePagesize.json",method = RequestMethod.GET)
    @ResponseBody
    public String changePagesize(HttpSession session, @RequestParam("pagesize") int pagesize) {
        JSONResult result = new JSONResult();

        try{
            UserInfoBean userInfo = UserInfoBean.getUserSessionInfo(session);
            userInfo.setPagesize(pagesize);

            session.removeAttribute(SessionAttributeNames.USER_SESSION_INFO);
            session.setAttribute(SessionAttributeNames.USER_SESSION_INFO,userInfo);

            result.setResultCode(JSONResult.SUCCESS);
        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("{} - changePagesize ERROR", errorId);
            result.setResultCode(JSONResult.FAIL);
            result.setMessage(message.getMessage("E0060","작업 중 오류가 발생하였습니다."));
            return result.toString();
        }
        catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - changePagesize ERROR", errorId);
            result.setResultCode(JSONResult.FAIL);
            result.setMessage(message.getMessage("E0060","작업 중 오류가 발생하였습니다."));
            return result.toString();
        }

        return result.toString();
    }
}

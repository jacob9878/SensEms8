package com.imoxion.sensems.web.controller;

import com.imoxion.sensems.web.beans.UserInfoBean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("calendar")
public class CalendarController {


    @RequestMapping(value="schedule.do",method = RequestMethod.GET)
    public String scheduleList(HttpSession session, ModelMap model){

        //페이지 목록 개수 세션에서 획득
        UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);

        return "/calendar/calendar_schedule";
    }

}

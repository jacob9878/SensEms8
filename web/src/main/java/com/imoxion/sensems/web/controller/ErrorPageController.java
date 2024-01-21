package com.imoxion.sensems.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Create by zpqdnjs on 2021-02-16
 * 공통 에러페이지 Controller
 */
@Controller
@RequestMapping("error")
public class ErrorPageController {

    @RequestMapping("no-resource.do")
    public String notFoundPage(){
        return "error/404";
    }

    @RequestMapping("server-error.do")
    public String serverError(){
        return "error/500";
    }

    @RequestMapping("session-expire.do")
    public String sessionExpire(){
        return "error/expire";
    }

    @RequestMapping("forbidden.do")
    public String forbidden(){
        return "error/noauth";
    }
}
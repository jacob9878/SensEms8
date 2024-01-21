package com.imoxion.sensems.web.controller;

import com.imoxion.common.util.ImStringUtil;
import com.imoxion.common.util.ImUtils;
import com.imoxion.sensems.common.logger.ErrorTraceLogger;
import com.imoxion.sensems.web.beans.ActionLogResultBean;
import com.imoxion.sensems.web.beans.UserInfoBean;
import com.imoxion.sensems.web.common.CommonFile;
import com.imoxion.sensems.web.common.ImPage;
import com.imoxion.sensems.web.common.ImbConstant;
import com.imoxion.sensems.web.database.domain.ImbActionLog;
import com.imoxion.sensems.web.database.domain.ImbActionMenu;
import com.imoxion.sensems.web.database.mapper.ActionLogMapper;
import com.imoxion.sensems.web.form.ActionLogForm;
import com.imoxion.sensems.web.form.ActionLogListForm;
import com.imoxion.sensems.web.service.ActionLogService;
import com.imoxion.sensems.web.util.HttpRequestUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

@Controller
@RequestMapping("sysman/actionlog")
public class ActionLogController {

    protected Logger log = LoggerFactory.getLogger( ActionLogController.class );

    @Autowired
    private ActionLogService actionLogService;

    @Autowired
    ActionLogMapper actionLogMapper;

    @RequestMapping(value = "list.do", method = RequestMethod.GET)
    public String listActionLogForm(HttpSession session, @ModelAttribute("actionLogListForm") ActionLogListForm form, ModelMap model, HttpServletRequest request) throws Exception {
        List<ImbActionMenu> actionMenuList = actionLogService.selectActionMenu();
        ImPage pageInfo = null;
        UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
        int totalsize = 0;
        int cpage = ImStringUtil.parseInt(form.getCpage());
        int pageGroupSize = ImStringUtil.parseInt(10);
        int type = 1; //페이징 처리 필요한 목록일 경우 type=1, 전체 목록이 필요한 경우 type=2
        ActionLogResultBean resultInfo = actionLogService.search(form, userSessionInfo.getPagesize(), type);
        pageInfo = new ImPage(cpage, userSessionInfo.getPagesize(), totalsize, pageGroupSize);
        List<ImbActionLog> logData = resultInfo.getResultList();


        model.addAttribute("logData", logData);
        model.addAttribute("pageInfo", pageInfo);
        model.addAttribute("actionMenuList", actionMenuList);
        return "/sysman/actionlog_list";
    }

    @RequestMapping(value = "search.json", method = RequestMethod.POST)
    @ResponseBody
    public String listActionLog(HttpSession session, @ModelAttribute("actionLogListForm") ActionLogListForm form, HttpServletRequest request, ModelMap model) throws Exception {
        UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
        JSONObject result = new JSONObject();
        int type = 1; //페이징 처리 필요한 목록일 경우 type=1, 전체 목록이 필요한 경우 type=2

        try {
            ActionLogResultBean resultInfo = actionLogService.search(form, userSessionInfo.getPagesize(), type);

            ImPage pageInfo = resultInfo.getPageInfo();
            List<ImbActionLog> logData = resultInfo.getResultList();

            JSONArray searchResult = new JSONArray();
            if(logData != null){
                for(Object o : logData){
                    JSONObject data = JSONObject.fromObject(o);
                    searchResult.add(data);
                }
            }

            result.put("pageInfo", pageInfo);
            result.put("result", searchResult);

        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("{} - LOG SEARCH ERROR", errorId);
        }
        catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - LOG SEARCH ERROR", errorId);
        }
        return result.toString();
    }

    @RequestMapping(value = "listDownload.do", method = RequestMethod.GET)
    public synchronized ModelAndView save(HttpSession session, HttpServletRequest request,
         @ModelAttribute("actionLogListForm") ActionLogListForm form, ModelMap model) throws Exception {
        UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);

        String tempFileName = ImbConstant.TEMPFILE_PATH + File.separator + ImUtils.makeKeyNum(24) + ".xls";
        File file = new File(tempFileName);
        String fName = "ActionLog_List.xls";
        int type = 1; // 현재 페이지만 다운로드 시 1, 전체목록 다운로드 시 2

        try {
            if(file.exists()){
                file.delete();
            }

            actionLogService.getFileDownloadActionLog(form, tempFileName, userSessionInfo.getPagesize(), type);

            //log insert start
            ActionLogForm logForm = new ActionLogForm();
            logForm.setIp(HttpRequestUtil.getRemoteAddr(request));
            logForm.setUserid(userSessionInfo.getUserid());
            logForm.setMenu_key("G501");
            logForm.setParam("검색기간 : " + form.getStart_date() + "~" + form.getEnd_date() + " / 메뉴 : " + form.getMenu()
                    + " / 아이디 : " + form.getUserid() + " / 내용 : " + form.getSrch_keyword());
            actionLogService.insertActionLog(logForm);
        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("{} - LOG DOWNLOAD ERROR", errorId);
        }
        catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - LOG DOWNLOAD ERROR", errorId);
        }

        return CommonFile.getDownloadView(file, fName);
    }

    @RequestMapping(value = "allListDownload.do", method = RequestMethod.GET)
    public synchronized ModelAndView saveAll(HttpSession session, HttpServletRequest request,
                                          @ModelAttribute("actionLogListForm") ActionLogListForm form, ModelMap model) throws Exception {
        UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);

        String tempFileName = ImbConstant.TEMPFILE_PATH + File.separator + ImUtils.makeKeyNum(24) + ".xls";
        File file = new File(tempFileName);
        String fName = "ActionLog_List.xls";
        int type = 2; // 현재 페이지만 다운로드 시 1, 전체목록 다운로드 시 2

        try {
            if(file.exists()){
                file.delete();
            }

            actionLogService.getFileDownloadActionLog(form, tempFileName, userSessionInfo.getPagesize(), type);

            //log insert start
            ActionLogForm logForm = new ActionLogForm();
            logForm.setIp(HttpRequestUtil.getRemoteAddr(request));
            logForm.setUserid(userSessionInfo.getUserid());
            logForm.setMenu_key("G501");
            logForm.setParam("검색기간 : " + form.getStart_date() + "~" + form.getEnd_date() + " / 메뉴 : " + form.getMenu()
                    + " / 아이디 : " + form.getUserid() + " / 내용 : " + form.getSrch_keyword());
            actionLogService.insertActionLog(logForm);
        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("{} - LOG DOWNLOAD ERROR", errorId);
        }
        catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - LOG DOWNLOAD ERROR", errorId);
        }

        return CommonFile.getDownloadView(file, fName);
    }

}

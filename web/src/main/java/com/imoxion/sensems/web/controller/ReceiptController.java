package com.imoxion.sensems.web.controller;


import com.imoxion.common.util.ImTimeUtil;
import com.imoxion.security.ImSecurityLib;
import com.imoxion.sensems.common.logger.ErrorTraceLogger;
import com.imoxion.sensems.web.beans.*;
import com.imoxion.sensems.web.common.ImPage;
import com.imoxion.sensems.web.common.ImbConstant;
import com.imoxion.sensems.web.form.ActionLogForm;
import com.imoxion.sensems.web.form.ReceiptForm;
import com.imoxion.sensems.web.service.ActionLogService;
import com.imoxion.sensems.web.service.ReceiptService;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("sysman/receipt")
public class ReceiptController {

    protected Logger log = LoggerFactory.getLogger( ReceiptController.class );
    @Autowired
    private ReceiptService receiptService;

    @Autowired
    private ActionLogService actionLogService;

    /**
     * 수신확인 조회 목록
     * @param session
     * @param model
     * @param form
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "list.do")
    public String receiptList( HttpSession session, ModelMap model,
                               @ModelAttribute("ReceiptForm") ReceiptForm form ) throws Exception {


        String curdate = ImTimeUtil.getDateFormat(new Date(), "yyyy-MM-dd");
        form.setRecv_date(curdate);

        return "/sysman/receipt_search";
    }

    /**
     * 수신확인 코드 조회 > 검색
     *
     * @param request
     * @param session
     * @param model
     * @param form
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "search.json", method = RequestMethod.POST)
    @ResponseBody
    public String searchReceipt(HttpServletRequest request, HttpSession session, ModelMap model,
                                @ModelAttribute("ReceiptForm") ReceiptForm form ) throws Exception {

        UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
        JSONObject result = new JSONObject();

        try {
            String msgid = ImSecurityLib.decryptAES(ImbConstant.URL_AES_KEY, form.getSearchKeywordMsgid());
            String rcode = ImSecurityLib.decryptAES(ImbConstant.URL_AES_KEY, form.getSearchKeywordRcode());

            //log insert start
            ActionLogForm logForm = new ActionLogForm();
            logForm.setIp(HttpRequestUtil.getRemoteAddr(request));
            logForm.setUserid(userSessionInfo.getUserid());
            logForm.setMenu_key("G701");
            logForm.setParam("조회 msgid : " + form.getSearchKeywordMsgid() + " / rcode : " + form.getSearchKeywordRcode());
            actionLogService.insertActionLog(logForm);

            form.setSearchKeywordRcode(rcode);
            form.setSearchKeywordMsgid(msgid);
            ReceiptResultBean resultinfo = receiptService.search(form, userSessionInfo.getPagesize());
            ImPage pageInfo = resultinfo.getPageInfo();
            List<ReceiptBean> resultData = resultinfo.getResultlist();
            String msg_name = resultinfo.getMsg_name();
            String mailfrom = resultinfo.getMailfrom();


            JSONArray searchResult = new JSONArray();
            if(resultData != null) {
                searchResult.addAll(JSONArray.fromObject(resultData));
            }

            //pageInfo = new ImPage(form.getCpage(), form.getPagesize(), searchResult.size(), 10);

            result.put("pageInfo", pageInfo);
            result.put("result", searchResult);
            result.put("msg_name", msg_name);
            result.put("mailfrom", mailfrom);


        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("{} - LOG SEARCH ne ERROR", errorId);
        }
        catch (Exception e) {
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - LOG SEARCH ERROR", errorId);
        }
        return result.toString();
    }

}

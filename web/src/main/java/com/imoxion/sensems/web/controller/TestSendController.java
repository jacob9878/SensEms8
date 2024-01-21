package com.imoxion.sensems.web.controller;

import com.imoxion.common.util.ImUtils;
import com.imoxion.sensems.common.logger.ErrorTraceLogger;
import com.imoxion.sensems.web.beans.TestSendBean;
import com.imoxion.sensems.web.beans.UserInfoBean;
import com.imoxion.sensems.web.form.ActionLogForm;
import com.imoxion.sensems.web.form.DemoAccountForm;
import com.imoxion.sensems.web.service.ActionLogService;
import com.imoxion.sensems.web.service.SendMailService;
import com.imoxion.sensems.web.service.TestSendService;
import com.imoxion.sensems.web.util.HttpRequestUtil;
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

@Controller
@RequestMapping("send/test")
public class TestSendController {

    protected Logger log = LoggerFactory.getLogger(TestSendController.class);

    @Autowired
    private TestSendService testSendService;

    @Autowired
    private MessageSourceAccessor message;

    @Autowired
    private ActionLogService actionLogService;

    @Autowired
    private SendMailService sendMailService;

    /**
     * 테스트 발송
     * @param session
     * @param model
     * @param from_email
     * @param to_emails
     * @param subject
     * @param charset
     * @param ishtml
     * @param content
     * @param isAttach
     * @return
     */
    @RequestMapping(value="testSend.json", method= RequestMethod.POST)
    @ResponseBody
    public String testSendMailSaveForReserve(HttpSession session, ModelMap model,
                                   @RequestParam(value = "from_email",required = false) String from_email ,
                                   @RequestParam(value = "to_emails[]",required = false) String[] to_emails,
                                   @RequestParam(value = "subject",required = false) String subject,
                                   @RequestParam(value = "charset",required = false) String charset ,
                                   @RequestParam(value = "ishtml",required = false) String ishtml,
                                   @RequestParam(value = "content",required = false) String content,
                                   @RequestParam(value = "isAttach",required = false, defaultValue = "0") String isAttach,
                                   @RequestParam(value = "recid", required = false) String recid,
                                   @RequestParam(value = "rectype", required = false) String rectype,
                                   @RequestParam(value = "att_keys", required = false) String att_keys,
                                   HttpServletRequest request) {
        UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);

        JSONObject result = new JSONObject();
        try {
            // 첨부파일 첨부 후 링크로 변환
            if(StringUtils.equals(isAttach,"1")){
                String weburl = HttpRequestUtil.getWebUrl(request);
                String msgid = ImUtils.makeKeyNum(24);
                if(StringUtils.isNotEmpty(att_keys)){
                    String att_key[] = att_keys.split(",");
                    for(int i=0; i<att_key.length; i++){
                        sendMailService.setAttachments(msgid, att_key[i]);
                    }
                    content = sendMailService.attachToLink(content, ishtml, weburl, att_keys, msgid);
                }
            }

            // 테스트 발송 bean 선언 및 파라메터 setting
            TestSendBean testSendBean = new TestSendBean();
            String mainkey = ImUtils.makeKeyNum(24);
            testSendBean.setMainkey(mainkey);
            testSendBean.setSubject(subject);
            testSendBean.setMailfrom(from_email);
            testSendBean.setIp(request.getRemoteAddr());

            // 테스트 발송 객체를 갖고 서비스단에서 제목, 첨부파일여부, HTML 여부, 필드에 따른 내용 치환 서비스단 처리 to email은 배열로 전달하여 건건히 가공하여 발송처리
            testSendService.doTestSendMailForReserveSend(to_emails, testSendBean, content, recid, rectype, userSessionInfo.getUserid());

            //log insert start
            ActionLogForm logForm = new ActionLogForm();
            logForm.setIp(HttpRequestUtil.getRemoteAddr(request));
            logForm.setUserid(userSessionInfo.getUserid());
            logForm.setMenu_key("B102");
            String param = "";
            for(int i=0; i<to_emails.length; i++){
                param += to_emails[i];
                if(i == to_emails.length-1) break;
                param += ", ";
            }
            logForm.setParam("from email : " + from_email + " / 제목 : " + subject + " / to email : " + param);
            actionLogService.insertActionLog(logForm);


        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("Add testSend ne Error : {}", errorId);
            result.put("result", false);
            result.put("message", message.getMessage("E0060","작업중 오류가 발생하였습니다."));
            return result.toString();
        }
        catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("Add testSend Error : {}", errorId);
            result.put("result", false);
            result.put("message", message.getMessage("E0060","작업중 오류가 발생하였습니다."));
            return result.toString();
        }
        result.put("result", true);
        result.put("message", message.getMessage("E0304","테스트 발송을 완료하였습니다."));
        return result.toString();
    }


}

package com.imoxion.sensems.web.controller;

import com.imoxion.common.util.ImStringUtil;
import com.imoxion.common.util.ImUtils;
import com.imoxion.security.ImSecurityLib;
import com.imoxion.sensems.common.logger.ErrorTraceLogger;
import com.imoxion.sensems.web.beans.*;
import com.imoxion.sensems.web.common.CommonFile;
import com.imoxion.sensems.web.common.ImPage;
import com.imoxion.sensems.web.common.ImParam;
import com.imoxion.sensems.web.common.ImbConstant;
import com.imoxion.sensems.web.database.domain.ImbCategoryInfo;
import com.imoxion.sensems.web.database.domain.ImbTransmitData;
import com.imoxion.sensems.web.form.*;
import com.imoxion.sensems.web.service.*;
import com.imoxion.sensems.web.util.HttpRequestUtil;
import com.imoxion.sensems.web.util.ImUtility;
import com.imoxion.sensems.web.util.JSONResult;
import javassist.NotFoundException;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("mail/result")
public class SendResultController {

    protected Logger log = LoggerFactory.getLogger(SendResultService.class);

    @Autowired
    private RejectService rejectService;

    @Autowired
    private MessageSourceAccessor message;

    @Autowired
    SendResultService sendResultService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ActionLogService actionLogService;

    @Autowired
    private MailService mailService;

    @Autowired
    private TestSendService testSendService;

    @Autowired
    private SendMailService sendMailService;


    /**
     * 발송결과 리스트 출력을 위한 처리를 행한다.
     * @param session
     * @param form
     * @return
     */
    @RequestMapping(value = "list.do")
    public String resultList(HttpSession session, @ModelAttribute("emsListForm") EmsListForm form, ModelMap model, HttpServletRequest request,
                             HttpServletResponse response) {

        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-control", "no-cache");
        UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
        String permission = userSessionInfo.getPermission();
        String userid = userSessionInfo.getUserid();
        String srch_keyword = form.getSrch_keyword();
        String categoryid = form.getCategoryid();
        String state = form.getState();

        boolean issearch = true;

        if(StringUtils.isEmpty(srch_keyword)) {
            issearch = false;
        }
        model.addAttribute("issearch", issearch);
        ImPage pageInfo = null;

        if (StringUtils.isNotEmpty(state)) {
            if (state.equals("1")) { // 발송 완료
                state = "+30";
            } else if (state.equals("2")) { // 대기 중
                state = "000";
            } else if (state.equals("3")) { // 발송 중
                state = "030";
            } else if (state.equals("4")) { // 임시보관중
                state = "007";
            }
            else if (state.equals("0")) { // 전체선택
                state = "";
            }
        }

        int totalsize = 0;
        int cpage = ImStringUtil.parseInt(form.getCpage());
        int pageGroupSize = ImStringUtil.parseInt(form.getPagegroupsize());

        try {
            List<EmsBean> emsList = null;
            List<EmsListForm> formList = new ArrayList<>();
            List<ImbCategoryInfo> categoryList = null;

            // 발송결과 처리 일반 사용자와 관리자 권한에 따라서 페이지 결과를 처리
            if (UserInfoBean.UTYPE_NORMAL.equals(permission)) { // 일반 사용자와 관리자 권한 구분
                totalsize = sendResultService.getsendResultCount(srch_keyword, userid, categoryid, state);
                pageInfo = new ImPage(cpage, userSessionInfo.getPagesize(), totalsize, pageGroupSize);
                emsList = sendResultService.getSendResultList(srch_keyword, userid, categoryid, state, pageInfo.getStart(), pageInfo.getEnd());
                categoryList = categoryService.getUserCategory(userid);


            } else { // 관리자는 userid 조건을 걸지않고 모든 발송결과를 확인할 권한이 있으므로 파라메터를 전달하지 않음
                totalsize = sendResultService.getsendResultCount(srch_keyword, null, categoryid, state);
                pageInfo = new ImPage(cpage, userSessionInfo.getPagesize(), totalsize, pageGroupSize);
                emsList = sendResultService.getSendResultList(srch_keyword, null, categoryid, state, pageInfo.getStart(), pageInfo.getEnd());
                categoryList = categoryService.getUserCategory(null);


            }

            model.addAttribute("cpage", cpage);
            model.addAttribute("srch_key", srch_keyword);
            model.addAttribute("categoryList", categoryList);
            model.addAttribute("pageInfo", pageInfo);
            model.addAttribute("emsList", emsList);
            model.addAttribute("emsListForm", form);

            //log insert start
            // TODO 로그정책 변경으로 삭제 (메일발송결과>조회)


        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error(" getSendResult List ne error  :{}", errorId);
        }
        catch (Exception e) {
            String errorId = ErrorTraceLogger.log(e);
            log.error(" getSendResult List error  :{}", errorId);
        }

        return "/result/result_list";
    }

    /**
     * 카테고리 이동을 수행한다.
     * @param session
     * @param msgids
     * @param categoryid
     * @return
     */
    @RequestMapping(value = "categoryMove.json", method = RequestMethod.POST)
    @ResponseBody
    public String categoryMove(HttpSession session, @RequestParam(value = "msgids", required = false) String[] msgids, @RequestParam(value = "categoryid", required = false) String categoryid,
                               HttpServletResponse response) {

        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-control", "no-cache");

        UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
        String permission = userSessionInfo.getPermission();
        JSONObject result = new JSONObject();

        try {
            if (msgids != null && categoryid != null) {
                int length = msgids.length;

                for (int i = 0; i < length; i++) {
                    EmsBean emsBean = sendResultService.getEmsForMsgid(msgids[i].split(",")[0]);

                    if (emsBean != null) {
                        /**사용자 권한이 존재하는지 확인한다. */
                        if (permission.equals(UserInfoBean.UTYPE_NORMAL)) { // 일반 사용자라면 세션과 등록자 유무를 체크한다.
                            if (StringUtils.isNotEmpty(userSessionInfo.getUserid()) && StringUtils.isNotEmpty(emsBean.getUserid())) {
                                if (!userSessionInfo.getUserid().equals(emsBean.getUserid())) { // userid가 다르다면 리스트 페이지로 이동한다.
                                    result.put("result", false);
                                    result.put("message", message.getMessage("E0060", "작업중 오류가 발생하였습니다."));
                                    log.error("categoryMove.json Authentication ERROR : {}", userSessionInfo.getUserid());
                                    return result.toString();
                                }
                            } else {
                                log.error("categoryMove.json ERR - USERID NULL ERROR : {}", userSessionInfo.getUserid());
                                result.put("result", false);
                                result.put("message", message.getMessage("E0060", "작업중 오류가 발생하였습니다."));
                                return result.toString();
                            }
                        }
                        sendResultService.categoryMove(msgids[i].split(",")[0], categoryid);
                    }
                }
                result.put("result", true);
                result.put("message", message.getMessage("E0549", "카테고리 이동을 완료하였습니다."));
            } else {
                result.put("result", false);
                result.put("message", message.getMessage("E0060", "작업중 오류가 발생하였습니다."));
                return result.toString();
            }
        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error(" ctegoryMove ne ERROR  :{}", errorId);
            result.put("result", false);
            result.put("message", message.getMessage("E0060", "작업중 오류가 발생하였습니다."));
        }
        catch (Exception e) {
            String errorId = ErrorTraceLogger.log(e);
            log.error(" ctegoryMove ERROR  :{}", errorId);
            result.put("result", false);
            result.put("message", message.getMessage("E0060", "작업중 오류가 발생하였습니다."));
        }

        return result.toString();
    }

    /**
     * 발송결과를 삭제한다.
     * @param session
     * @param msgids
     * @return
     */
    @RequestMapping(value = "delete.json", method = RequestMethod.POST)
    @ResponseBody
    public String resultDelete(HttpSession session, @RequestParam(value = "msgids", required = false) String[] msgids, HttpServletRequest request) {

        JSONResult result = new JSONResult();
        try {

            if (msgids == null || msgids.length == 0) {
                result.setResultCode(JSONResult.FAIL);
                result.setMessage(message.getMessage("E0550", "선택된 항목이 없습니다."));
                return result.toString();
            }

            UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);

            boolean checker = sendResultService.deleteResultList(msgids, userSessionInfo); // 권한이 있는 사용자인지 아닌지를 체크한다.

            if (checker) {
                result.setResultCode(JSONResult.SUCCESS);
                result.setMessage(message.getMessage("E0070", "삭제되었습니다."));

                //log insert start
                ActionLogForm logForm = new ActionLogForm();
                logForm.setIp(HttpRequestUtil.getRemoteAddr(request));
                logForm.setUserid(userSessionInfo.getUserid());
                logForm.setMenu_key("C102");
                String param = "";
                for(int i=0; i<msgids.length; i++){
                    if(i==0){
                        param= "메일 제목 : " + msgids[i].split(",")[1] + " / 삭제 mgsid : " + msgids[i].split(",")[0];
                    }else {
                        param += " , 메일 제목 : " + msgids[i].split(",")[1] + " / 삭제 mgsid : " + msgids[i].split(",")[0];
                    }
                }
                logForm.setParam(param);
                actionLogService.insertActionLog(logForm);

            } else {
                result.setResultCode(JSONResult.FAIL);
                result.setMessage(message.getMessage("E0060", "작업중 오류가 발생하였습니다."));
            }

        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("reserve Delete ne ERROR : {}", errorId);
            result.setResultCode(JSONResult.FAIL);
            result.setMessage(message.getMessage("E0060", "작업중 오류가 발생하였습니다."));
            return result.toString();
        }
        catch (Exception e) {
            String errorId = ErrorTraceLogger.log(e);
            log.error("reserve Delete ERROR : {}", errorId);
            result.setResultCode(JSONResult.FAIL);
            result.setMessage(message.getMessage("E0060", "작업중 오류가 발생하였습니다."));
            return result.toString();
        }

        return result.toString();
    }

    /**
     * 재발송을 시도한다.
     * @param session
     * @param msgid
     * @return
     */
    @RequestMapping(value = "resend.json", method = RequestMethod.POST)
    @ResponseBody
    public String doResend(HttpSession session, @RequestParam(value = "msgid", required = false) String msgid,
                           @RequestParam(value = "msg_name", required = false) String msg_name ,HttpServletRequest request) {
        JSONResult result = new JSONResult();

        try {
            UserInfoBean userInfoBean = UserInfoBean.getUserSessionInfo(session);
            String permission = userInfoBean.getPermission();

            if (msgid == null) {
                result.setResultCode(JSONResult.FAIL);
                result.setMessage(message.getMessage("E0550", "선택된 항목이 없습니다."));
                return result.toString();
            }

            /** 해당 페이지를 사용할 권한이 존재하는지 확인한다. */
            if (permission.equals(UserInfoBean.UTYPE_NORMAL)) { // 관리자인지 체크
                if (StringUtils.isNotEmpty(userInfoBean.getUserid())) {
                    if (!userInfoBean.getUserid().equals(userInfoBean.getUserid())) {
                        log.error("SendResult resend.json - Authentication ERROR : {}", userInfoBean.getUserid());
                        result.setResultCode(JSONResult.FAIL);
                        result.setMessage(message.getMessage("E0060", "작업중 오류가 발생하였습니다."));
                        return result.toString();
                    }
                } else {
                    log.error("SendResult resend.json - USERID NULL ERROR : {}", userInfoBean.getUserid());
                    result.setResultCode(JSONResult.FAIL);
                    result.setMessage(message.getMessage("E0060", "작업중 오류가 발생하였습니다."));
                    return result.toString();
                }
            }

            /** 발송중지를 시도한다. */
            EmsBean emsBean = sendResultService.getEmsForMsgid(msgid);

            if (emsBean.getIsstop().equals("1")) { // 스톱상태이라면 2분 후 발송을 할수 있도록 텀을 체크한다.
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

                Date stop_time = sdf.parse(emsBean.getStop_time()); // 중지시간을 시간으로 환산한다.
                Date now = calendar.getTime(); //현재시간

                calendar.setTime(stop_time);
                calendar.add(Calendar.MINUTE, 2); // 중지시간에서 +2분한다.
                stop_time = calendar.getTime();

                int compare = now.compareTo(stop_time);
                /** 중지시간과 현재시간을 비교하여 현재가 중지된 시점보다 더 크다면 발송을 수행 */
                if (compare > 0) { // now > stop_time
                    sendResultService.doResend(msgid);
                    result.setResultCode(JSONResult.SUCCESS);
                    result.setMessage(message.getMessage("E0551", "재발송 설정을 완료하였습니다."));
                } else { // now < stop_time
                    result.setResultCode(JSONResult.FAIL);
                    result.setMessage(message.getMessage("E0552", "재전송은 중지 후 2분이상 경과후에 가능합니다."));
                    return result.toString();
                }
            } else { // 스톱상태가 아니라면 업데이트 수행
                sendResultService.doResend(msgid);
                result.setResultCode(JSONResult.SUCCESS);
                result.setMessage(message.getMessage("E0551", "재발송 설정을 완료하였습니다."));
            }

            //log insert start
            ActionLogForm logForm = new ActionLogForm();
            logForm.setIp(HttpRequestUtil.getRemoteAddr(request));
            logForm.setUserid(userInfoBean.getUserid());
            logForm.setMenu_key("C103");
            logForm.setParam("재발신 메일 제목 : " + msg_name + " / 재발신 mgsid : " + msgid);
            actionLogService.insertActionLog(logForm);

        }catch (NotFoundException nf) {
            String errorId = ErrorTraceLogger.log(nf);
            log.error(" SendResult resend.json not found ERROR :{}", errorId);
            result.setResultCode(JSONResult.FAIL);
            result.setMessage(message.getMessage("E0060", "작업중 오류가 발생하였습니다."));
            return result.toString();
        }
        catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error(" SendResult resend.json ne ERROR :{}", errorId);
            result.setResultCode(JSONResult.FAIL);
            result.setMessage(message.getMessage("E0060", "작업중 오류가 발생하였습니다."));
            return result.toString();
        }
        catch (Exception e) {
            String errorId = ErrorTraceLogger.log(e);
            log.error(" SendResult resend.json ERROR :{}", errorId);
            result.setResultCode(JSONResult.FAIL);
            result.setMessage(message.getMessage("E0060", "작업중 오류가 발생하였습니다."));
            return result.toString();
        }

        return result.toString();
    }

    /**
     * 발송중지를 시도한다.
     *
     * @param session
     * @param msgid
     * @return
     */
    @RequestMapping(value = "stop.json", method = RequestMethod.POST)
    @ResponseBody
    public String doStop(HttpSession session, @RequestParam(value = "msgid", required = false) String msgid, HttpServletRequest request) {
        JSONResult result = new JSONResult();

        try {
            UserInfoBean userInfoBean = UserInfoBean.getUserSessionInfo(session);
            String permission = userInfoBean.getPermission();

            if (msgid == null) {
                result.setResultCode(JSONResult.FAIL);
                result.setMessage(message.getMessage("E0550", "선택된 항목이 없습니다."));
                return result.toString();
            }

            /** 해당 페이지를 사용할 권한이 존재하는지 확인한다. */
            if (permission.equals(UserInfoBean.UTYPE_NORMAL)) { // 관리자인지 체크
                if (StringUtils.isNotEmpty(userInfoBean.getUserid())) {
                    if (!userInfoBean.getUserid().equals(userInfoBean.getUserid())) {
                        log.error("SendResult stop.json - Authentication ERROR : {}", userInfoBean.getUserid());
                        result.setResultCode(JSONResult.FAIL);
                        result.setMessage(message.getMessage("E0060", "작업중 오류가 발생하였습니다."));
                        return result.toString();
                    }
                } else {
                    log.error("SendResult stop.json - USERID NULL ERROR : {}", userInfoBean.getUserid());
                    result.setResultCode(JSONResult.FAIL);
                    result.setMessage(message.getMessage("E0060", "작업중 오류가 발생하였습니다."));
                    return result.toString();
                }
            }

            sendResultService.doStop(msgid.split(",")[0]);
            result.setResultCode(JSONResult.SUCCESS);
            result.setMessage(message.getMessage("E0553", "전송중지 설정을 완료하였습니다."));

            //log insert start
            ActionLogForm logForm = new ActionLogForm();
            logForm.setIp(HttpRequestUtil.getRemoteAddr(request));
            logForm.setUserid(userInfoBean.getUserid());
            logForm.setMenu_key("C104");
            logForm.setParam("발송중지 메일 제목 : " + msgid.split(",")[1] +  " / 발송중지 mgsid : " + msgid.split(",")[0]);
            actionLogService.insertActionLog(logForm);

        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error(" SendResult resend.json ne ERROR :{}", errorId);
            result.setResultCode(JSONResult.FAIL);
            result.setMessage(message.getMessage("E0060", "작업중 오류가 발생하였습니다."));
            return result.toString();
        }
        catch (Exception e) {
            String errorId = ErrorTraceLogger.log(e);
            log.error(" SendResult resend.json ERROR :{}", errorId);
            result.setResultCode(JSONResult.FAIL);
            result.setMessage(message.getMessage("E0060", "작업중 오류가 발생하였습니다."));
            return result.toString();
        }

        return result.toString();
    }

    /**
     * 해당되는 발송결과 페이지를 리포팅 페이지를 출력한다.
     * @param session
     * @param request
     * @param model
     * @return
     */
    @RequestMapping(value = "reporting.do", method = RequestMethod.GET)
    public String report(HttpSession session, HttpServletRequest request, ModelMap model, @RequestParam(value = "msgid", required = false) String msgid) {
        try {
            UserInfoBean userInfoBean = UserInfoBean.getUserSessionInfo(session);
            String permission = userInfoBean.getPermission();

            EmsBean emsBean = sendResultService.getEmsForMsgid(msgid);

            /** 해당 페이지를 사용할 권한이 존재하는지 확인한다. */
            if(permission.equals(UserInfoBean.UTYPE_NORMAL)){
                if(StringUtils.isNotEmpty(userInfoBean.getUserid())){
                    if(!userInfoBean.getUserid().equals(emsBean.getUserid())){ // userid가 다르다면 리스트 페이지로 이동한다.
                        log.error("reporting view - Authentication ERROR : {}", userInfoBean.getUserid());
                        return "redirect:/error/forbidden.do";
                    }
                }else{
                    log.error("reporting view  - USERID NULL ERROR : {}", userInfoBean.getUserid());
                    return "redirect:/error/forbidden.do";
                }
            }

            //IMB_RECEIPT_COUNT
            int receiptCount= sendResultService.getReceiptCountBean(msgid);

            // RECV_메시지아이디 , 성공개수, 실패개수 구하기
            int success_count = 0;
            int fail_count = 0;
            int total_count = 0;

            try {
                success_count = sendResultService.getRecvMessageIDSendSuccessAndFailCount(msgid, "1");
                fail_count = sendResultService.getRecvMessageIDSendSuccessAndFailCount(msgid, "0");
                total_count = sendResultService.getRecvMessageIDSendSuccessAndFailCount(msgid, null);
            }catch (NullPointerException ne) {
                String errorId = ErrorTraceLogger.log(ne);
                log.error("reporting RECV_messageID table ne ERROR :{}", errorId);
                success_count = 0;
                fail_count = 0;
                total_count = 0;
            }
            catch (Exception e){
                String errorId = ErrorTraceLogger.log(e);
                log.error("reporting RECV_messageID table ERROR :{}", errorId);
                success_count = 0;
                fail_count = 0;
                total_count = 0;
            }
            int clickCount = 0;
            //링크 클릭 로그   2023-02-02 리포팅페이지에 불필요한 함수로 주석처리
            // LinkLogMessageIDBean linkLogMessageIDBean = null;
//            try{
            int checkTB = 0;
            checkTB = sendResultService.getLinkLogCheck(msgid);
            if(checkTB == 1) {
                //linkLogMessageIDBean = sendResultService.getLinkLogMessageIDForMsgid(msgid);
                clickCount = sendResultService.getClickCount(msgid);
                model.addAttribute("clickCount", clickCount);
            }else {
                clickCount = 0;
                model.addAttribute("clickCount", clickCount);
            }

//            }catch (Exception e){
//                String errorId = ErrorTraceLogger.log(e);
//                log.error("reporting linklog_messageID table ERROR :{}", errorId);
//            }





            // IB_ERROR_COUNT = 메일별 에러 카운트 통계

            LinkedHashMap<String, Integer> errDataMap = null;
            int total_error_count = 0;
            try {
                ImbErrorCount imbErrorCount = sendResultService.getErrorCountForMsgid(msgid);
                total_error_count = sendResultService.getErrorCountForMsgidCount(msgid);
                errDataMap = sendResultService.setErrorData(imbErrorCount);

            }catch (NullPointerException ne) {
                String errorId = ErrorTraceLogger.log(ne);
                log.error("reporting errDataMap sorting ne ERROR :{}", errorId);
            }
            catch (Exception e){
                String errorId = ErrorTraceLogger.log(e);
                log.error("reporting errDataMap sorting ERROR :{}", errorId);
            }

            //발송 도메인 통계
            List<HC_MessageIDBean> hc_messageIDBeanList = null;
            try {
                hc_messageIDBeanList = sendResultService.getHC_MessageIDForMsgid(msgid);
            }catch (NullPointerException ne) {
                String errorId = ErrorTraceLogger.log(ne);
                log.error("reporting hc_messageID table ne ERROR :{}", errorId);
            }
            catch (Exception e){
                String errorId = ErrorTraceLogger.log(e);
                log.error("reporting hc_messageID table ERROR :{}", errorId);
            }

            //발송 도메인 통계는 6개까지 출력하므로 6개까지만 데이터를 출력한다.
            //기타도메인 갯수도 넣는다.
            if(hc_messageIDBeanList != null && hc_messageIDBeanList.size() > 6) {
                /*List<HC_MessageIDBean> hcList = new ArrayList<>();

                for(int i=0; i < 6; i++){
                    hcList.add(hc_messageIDBeanList.get(i));
                }*/
                model.addAttribute("hcList", hc_messageIDBeanList.subList(0, 6));
                model.addAttribute("hcCount", hc_messageIDBeanList.size()-6);
            }else{
                model.addAttribute("hcList", hc_messageIDBeanList);
                model.addAttribute("hcCount", 0);
            }

            //카테고리
            String categoryName = sendResultService.getCategoryName(emsBean.getCategoryid());

            // 시간 설정
            if(StringUtils.isNotEmpty(emsBean.getRegdate())){
                emsBean.setRegdate(emsBean.getRegdate().substring(0, 4) + "-" + emsBean.getRegdate().substring(4, 6) + "-" + emsBean.getRegdate().substring(6, 8)+" "+ emsBean.getRegdate().substring(8, 10) + ":" + emsBean.getRegdate().substring(10, 12));
            }

            if(StringUtils.isNotEmpty(emsBean.getStart_time())){
                emsBean.setStart_time(emsBean.getStart_time().substring(0, 4) + "-" + emsBean.getStart_time().substring(4, 6) + "-" + emsBean.getStart_time().substring(6, 8) + " "+ emsBean.getStart_time().substring(8, 10) + ":" + emsBean.getStart_time().substring(10, 12));
            }

            if(StringUtils.isNotEmpty(emsBean.getEnd_time())){
                emsBean.setEnd_time(emsBean.getEnd_time().substring(0, 4) + "-" + emsBean.getEnd_time().substring(4, 6) + "-" + emsBean.getEnd_time().substring(6, 8) + " "+ emsBean.getEnd_time().substring(8, 10) + ":" + emsBean.getEnd_time().substring(10, 12));
            }

            // 시작시간과 종료시간이 있는 경우만 처리
            // 시간이 둘다 없는 경우 시간을 비교할 수 없으므로 데이터 0을 내보낸다. ( 0인 경우 출력을 안하도록 설정 )
            float time_diff = 0;

            if(StringUtils.isNotEmpty(emsBean.getStart_time()) && StringUtils.isNotEmpty(emsBean.getEnd_time()) ){
                // 소요시간 계산
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date start = sdf.parse(emsBean.getStart_time());
                Date end = sdf.parse(emsBean.getEnd_time());
                float diff = end.getTime() - start.getTime();
//                if ( emsBean.getEnd_time() == null ) {
//                    time_diff = -1;
//                }else {
                time_diff = diff / 1000 / 60;
                //}
            }

            // 클릭율, 개봉율을 계산
            String displayPattern = "0.##";
            DecimalFormat form = new DecimalFormat(displayPattern);
            double clickRate = 0;
            double openRate = 0;

            int cursend = ImStringUtil.parseInt(emsBean.getCur_send());
            if(receiptCount != 0){
                openRate = ((double)receiptCount/(double)cursend) * 100;
                if(Double.isNaN(openRate) || Double.isInfinite(openRate)){
                    openRate = 0;
                }
            }else{
                receiptCount = 0;
            }


            clickRate = (double)((double)clickCount/(double)cursend) * 100;
            if(Double.isNaN(clickRate) || Double.isInfinite(clickRate)){
                openRate = 0;
            }


            // 에러 카운트 별 퍼센테이지 계산
            model.addAttribute("emsBean", emsBean);
            //model.addAttribute("recv_count", recv_count);
            model.addAttribute("time_diff", time_diff);
            model.addAttribute("categoryName", categoryName);
            model.addAttribute("receiptCount", receiptCount);
            model.addAttribute("success_count", success_count);
            model.addAttribute("fail_count", fail_count);
            model.addAttribute("total_count", total_count);
            model.addAttribute("total_error_count", total_error_count);
            model.addAttribute("openRate", form.format(openRate)); // 개봉율
            model.addAttribute("clickRate", form.format(clickRate)); // 클릭율
            //model.addAttribute("linkLog", linkLogMessageIDBean);
            model.addAttribute("errDataMap", errDataMap);

            //log insert start
            // TODO 로그정책 수정으로 삭제 (메일발송결과>리포팅조회 C105)

        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("SendResult reporting.do ne ERROR :{}", errorId);
        }
        catch (Exception e) {
            String errorId = ErrorTraceLogger.log(e);
            log.error("SendResult reporting.do ERROR :{}", errorId);
        }

        return "/popup/result/popup_resultReport";
    }


    /**
     * 해당되는 수신자 목록 페이지를 팝업한다.
     * @param session
     * @param request
     * @param model
     * @return
     */
    @RequestMapping(value = "receiverList.do")
    public String receiverListPopup(@ModelAttribute("receiverListForm") ReceiverListForm form, HttpSession session, HttpServletRequest request, ModelMap model, @RequestParam(value = "msgid", required = false) String msgid,
                                    HttpServletResponse response) {

        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-control", "no-cache");

        UserInfoBean userInfoBean = UserInfoBean.getUserSessionInfo(session);
        String permission = userInfoBean.getPermission();

        int totalsize = 0;
        int cpage = ImStringUtil.parseInt(form.getCpage());
        int pageGroupSize = ImStringUtil.parseInt(form.getPagegroupsize());
        String srch_type = form.getSrch_type();
        String srch_keyword = form.getSrch_keyword();
        ImPage pageInfo = null;

        try {
            List<ImbCategoryInfo> categoryList = null;

            EmsBean emsBean = sendResultService.getEmsForMsgid(msgid);

            /** 해당 페이지를 사용할 권한이 존재하는지 확인한다. */
            if (permission.equals(UserInfoBean.UTYPE_NORMAL)) {
                if (StringUtils.isNotEmpty(userInfoBean.getUserid())) {
                    if (!userInfoBean.getUserid().equals(emsBean.getUserid())) { // userid가 다르다면 리스트 페이지로 이동한다.
                        log.error("ResultList ReceiverListPopup - Authentication ERROR : {}", userInfoBean.getUserid());
                        return "redirect:/error/forbidden.do";
                    }
                } else {
                    log.error("ResultList ReceiverListPopup - USERID NULL ERROR : {}", userInfoBean.getUserid());
                    return "redirect:/error/forbidden.do";
                }
            }

            List<RecvMessageIDBean> recvList = null;

            // E-mail로 검색 시 srch_keyword를 암호화 하여 암호화 된 DB 데이터에서 검색
            if(StringUtils.isNotEmpty(srch_type) && "01".equals(srch_type) && StringUtils.isNotEmpty(srch_keyword)){
                if(ImbConstant.DATABASE_ENCRYPTION_USE){
                    String secret_key = ImbConstant.DATABASE_AES_KEY;
                    srch_keyword = ImSecurityLib.encryptAES256(secret_key,srch_keyword);
                }
            }

            boolean issearch = true;

            if(StringUtils.isEmpty(srch_keyword)) {
                issearch = false;
            }
            model.addAttribute("issearch", issearch);

            try {
                totalsize = sendResultService.getRecvTotalCountForMsgid2(msgid,srch_keyword,srch_type);
                pageInfo = new ImPage(cpage, userInfoBean.getPagesize(), totalsize, pageGroupSize);
                recvList = sendResultService.getReciverListPageingForMsgid(msgid, pageInfo.getStart(), pageInfo.getEnd(), srch_keyword, srch_type);
            }catch (NullPointerException ne) {
                log.error("ResultList recvList ne - ERROR ");
            }
            catch (Exception e){
                log.error("ResultList recvList - ERROR ");
            }

            model.addAttribute("recvList", recvList);
            model.addAttribute("form", form);
            model.addAttribute("pageInfo", pageInfo);

            //log insert start
            // TODO 로그정책 변경으로 삭제 (메일발송결과>수신자목록보기 C107)

        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("ResultList ReceiverListPopup - receiverList.do ne ERROR :{}", errorId);
        }
        catch (Exception e) {
            String errorId = ErrorTraceLogger.log(e);
            log.error("ResultList ReceiverListPopup - receiverList.do ERROR :{}", errorId);
        }

        return "/popup/result/popup_recvList";
    }

    @RequestMapping(value = "htmlDownload.json", method = RequestMethod.POST)
    public ModelAndView downloadHtml(HttpSession session, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
        ImParam param = new ImParam( request );
        String html = param.get("html");
        // 파일로 선언하여 model을 retrun 한다.
        CommonFile downloadFile = new CommonFile();

        /** URL을 설정 START */
        String weburl;

        if(request.isSecure()){ // http or https 체크
            weburl = "https://"+ request.getRemoteHost();
        }else{
            weburl = "http://"+ request.getRemoteHost();
        }

        int serverPort = request.getServerPort();

        if(serverPort != 80 && serverPort != 443){ // 서버 포트가 80이나 443이 아니라면 일반적인 포트가 아니므로 해당 포트가 올바르게 처리되도록 예외처리
            weburl = weburl +":"+ serverPort;
        }
        /** URL을 설정 END */

        File file = new File("download");
        downloadFile.setFile(file);

        String fileName = "reporting.html";

        String strscript = "\r\n<script language='JavaScript'>\r\n var len = document.all.length;\r\n for(i=0;i<len;i++){\r\n if(document.all[i].tagName == 'A') {\r\n document.all[i].style.display = 'none'\r\n }\r\n }\r\n </script>";

        String strcss = "<html><head><LINK href='"+weburl+"/sens-static/css/style.css' rel=STYLESHEET title='text spacing' type=text/css>"+
                "<LINK href='"+weburl+"/sens-static/css/skin.css' rel=STYLESHEET title='text spacing' type=text/css>"
                +"</head>\r\n<body>\r\n";
        String html_src = strcss+"<table width=1000 border=0><tr><td>\n"+html+"\r\n</td></tr></table>\r\n</body></html>"+strscript;

        String sCharset = "UTF-8";
        FileOutputStream fos = null;
        try{
            fos = new FileOutputStream(file);
            fos.write(html_src.getBytes(sCharset));
        } catch(IOException ie){
            String errorId = ErrorTraceLogger.log(ie);
            log.error("SendResult reporting download log IO error :{}", errorId);
        } catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("SendResult reporting download log error :{}", errorId);
        } finally {
            try {if(fos != null)fos.close();} catch (IOException e) {}
        }

        ModelAndView mav = new ModelAndView();
        mav.setViewName("download");
        mav.addObject("downloadFile", downloadFile);

        try{
            //log insert start
            //TODO 로그정책 변경으로 삭제 (메일발송결과 > 리포딩 다운로드 C106)
        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("SendResult reporting download log ne ERROR :{}", errorId);
        }
        catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("SendResult reporting download log ERROR :{}", errorId);
        }

        return CommonFile.getDownloadView(file, "reporting.html");
    }

    //    @RequestMapping(value = "staticSend.do", method = RequestMethod.GET)
    @RequestMapping(value = "staticSend.do")
    public String staticsSend(@ModelAttribute("StatSendForm") StatSendForm form, HttpSession httpSession,HttpServletRequest request,
                              ModelMap model, @RequestParam(value = "msgid", required = false) String msgid,
                              @RequestParam(value = "srch_key", required = false) String srch_key,
                              @RequestParam(value = "listcpage", required = false) String listcpage) {
        try {
            UserInfoBean userInfoBean = UserInfoBean.getUserSessionInfo(httpSession);
            String permission = userInfoBean.getPermission();
            EmsBean emsBean = sendResultService.getEmsForMsgid(msgid);
            int reject_totalcount = rejectService.getTotalRejectCount();
            // 수신거부 테이블에 포함된 현재 보낸 발송 통계 데이터중에 수신거부 건수를 구한다.
            int reject_recentcount = rejectService.getRecentRejectCount(msgid);


            /** 해당 페이지를 사용할 권한이 존재하는지 확인한다. */
            if (permission.equals(UserInfoBean.UTYPE_NORMAL)) {
                if (StringUtils.isNotEmpty(userInfoBean.getUserid())) {
                    if (!userInfoBean.getUserid().equals(emsBean.getUserid())) { // userid가 다르다면 리스트 페이지로 이동한다.
                        log.error("reporting view - Authentication ERROR : {}", userInfoBean.getUserid());
                        return "redirect:/error/forbidden.do";
                    }
                } else {
                    log.error("reporting view  - USERID NULL ERROR : {}", userInfoBean.getUserid());
                    return "redirect:/error/forbidden.do";
                }
            }

            // RECV_메시지아이디 , 성공개수, 실패개수 구하기
            int success_count = 0;
            int fail_count = 0;
            int total_count = 0;


            try {
                success_count = sendResultService.getRecvMessageIDSendSuccessAndFailCount(msgid, "1");
                fail_count = sendResultService.getRecvMessageIDSendSuccessAndFailCount(msgid, "0");
                total_count = sendResultService.getRecvMessageIDSendSuccessAndFailCount(msgid, null);
            }catch (NullPointerException ne) {
                String errorId = ErrorTraceLogger.log(ne);
                log.error("statSend RECV_messageID table ne ERROR :{}", errorId);
                success_count = 0;
                fail_count = 0;
                total_count = 0;
            }catch (Exception e) {
                String errorId = ErrorTraceLogger.log(e);
                log.error("statSend RECV_messageID table ERROR :{}", errorId);
                success_count = 0;
                fail_count = 0;
                total_count = 0;
            }

            // 반응분석일 시, 분 표시
            Map<String, String> hourList = new LinkedHashMap<>();
            for (int i = 0; i <= 23; i++) {
                String s = ImUtils.checkDigit(i);
                hourList.put(s, message.getMessage("M0830", new Object[]{s}, "{0}시"));
            }
            model.addAttribute("hourList", hourList);

            Map<String, String> minuteList = new LinkedHashMap<>();
            for (int i = 0; i <= 11; i++) {
                int j = i * 5; // 5분 간격 설정
                String s = ImUtils.checkDigit(j);
                minuteList.put(s, message.getMessage("M0831", new Object[]{s}, "{0}분"));
            }
            model.addAttribute("minuteList", minuteList);


            // 발송 성공률 원형 그래프 구현
            LinkedHashMap<String, Integer> EmsDateMap = null;
            try {
                // ImbErrorCount imbErrorCount = sendResultService.getErrorCountForMsgid(msgid);
                // total_error_count = sendResultService.getErrorCountForMsgidCount(msgid);
                EmsDateMap = sendResultService.setEmsDate(msgid);
            }catch (NullPointerException ne) {
                String errorId = ErrorTraceLogger.log(ne);
                log.error("reporting errDataMap sorting ne ERROR :{}", errorId);
            }
            catch (Exception e){
                String errorId = ErrorTraceLogger.log(e);
                log.error("reporting errDataMap sorting ERROR :{}", errorId);
            }

            //발송 도메인 통계
            List<HC_MessageIDBean> hc_messageIDBeanList = null;
            LinkedHashMap<String, Integer> hcCountMap = null;
            ImPage pageInfo = null;
            int totalsize = 0;
            int cpage = ImStringUtil.parseInt(form.getCpage());
            listcpage =request.getParameter("listcpage");
            if(listcpage == null){
                listcpage = String.valueOf(cpage);
            }
            int pageGroupsize = ImStringUtil.parseInt(form.getPagegroupsize());

            //카테고리
            String categoryName = sendResultService.getCategoryName(emsBean.getCategoryid());

            try {
                /*hc_messageIDBeanList = sendResultService.getHC_MessageIDForMsgid(msgid);*/
                totalsize = sendResultService.getHC_MessageIDForMsgidCount(msgid);
                pageInfo = new ImPage(cpage, 10, totalsize, pageGroupsize);
                hc_messageIDBeanList = sendResultService.getHC_MessageIDPagingForMsgid(msgid,pageInfo.getStart(),pageInfo.getEnd());
            }catch (NullPointerException ne) {
                String errorId = ErrorTraceLogger.log(ne);
                log.error("statSend hc_messageID table ne ERROR :{}", errorId);
            }
            catch (Exception e) {
                String errorId = ErrorTraceLogger.log(e);
                log.error("statSend hc_messageID table ERROR :{}", errorId);
            }
            if(emsBean.getResp_time().length() < 12) {
                String resptime =  emsBean.getResp_time() + "00";
                emsBean.setResp_time(resptime);
                model.addAttribute("resp_min", ImUtility.stroToMM(emsBean.getResp_time()));
            }else {
                model.addAttribute("resp_min", ImUtility.stroToMM(emsBean.getResp_time()));
            }

            //log insert start
            ActionLogForm logForm = new ActionLogForm();
            logForm.setIp(HttpRequestUtil.getRemoteAddr(request));
            logForm.setUserid(userInfoBean.getUserid());
            logForm.setMenu_key("C101");
            logForm.setParam("메일 제목 : " + emsBean.getMsg_name() + " / msgid : " + msgid);
            actionLogService.insertActionLog(logForm);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");

            String beforeDate = ImUtility.changeDate(emsBean.getResp_time());
            model.addAttribute("response_time", beforeDate);
            String afterDate = "";

            try {
                Date date = dateFormat.parse(beforeDate); // 기존 string을 date 클래스로 변환
                model.addAttribute("resp_time", ImUtility.getDateFormat(date, "yyyy-MM-dd"));
            }catch (ParseException pe) {
                String errorId = ErrorTraceLogger.log(pe);
                log.error("getStaticSend dateFormat pe error :{}", errorId);
            }
            catch (Exception e) {
                String errorId = ErrorTraceLogger.log(e);
                log.error("getStaticSend dateFormat error :{}", errorId);
            }

            model.addAttribute("cpage", cpage);
            model.addAttribute("listcpage", listcpage);
            model.addAttribute("srch_key", srch_key);
            model.addAttribute("on", "send");
            model.addAttribute("hcList", hc_messageIDBeanList);
            model.addAttribute("emsbean", emsBean);
            model.addAttribute("success_count", success_count);
            model.addAttribute("fail_count", fail_count);
            model.addAttribute("total_count", total_count);
            model.addAttribute("reject_totalcount", reject_totalcount);
            model.addAttribute("reject_recentcount", reject_recentcount);
            model.addAttribute("emsMap", EmsDateMap);
            model.addAttribute("pageInfo", pageInfo);
            model.addAttribute("response_time", ImUtility.changeDate(emsBean.getResp_time()));
            model.addAttribute("totalsize", totalsize);
            model.addAttribute("statSendForm", form);
            model.addAttribute("resp_hour", ImUtility.stroToHH(emsBean.getResp_time()));
            model.addAttribute("categoryName", categoryName);
//            String from =  ImUtility.changeDate(emsBean.getResp_time());
//            SimpleDateFormat fDate = new SimpleDateFormat("yyyy-MM-dd");
//            Date nDate = fDate.parse(from);
//            System.out.println("~~~~~~~~~~~~~~~~~~~" + nDate);


        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.debug("error content : " +ne);
            log.error("SendResult staticsSend.do ne ERROR :{}", errorId);
        }
        catch (Exception e) {
            String errorId = ErrorTraceLogger.log(e);
            log.debug("error content : " + e);
            log.error("SendResult staticsSend.do ERROR :{}", errorId);
        }
        return "/result_send/stat_send";
    }

    /**
     *
     * 반응분석일을 수정한다.
     *
     *
     */
    @ResponseBody
    @RequestMapping(value = "resptimeUpdate.json", method = RequestMethod.POST)
    public String resptimeUpdate(HttpServletRequest request, HttpSession session, ModelMap model,@RequestParam("msgid")String msgid, @RequestParam("resp_date")String resp_date, @RequestParam("resp_hour")String resp_hour, @RequestParam("resp_min") String resp_min){
        JSONResult result = new JSONResult();
        ImParam param = new ImParam(request);
        EmsBean emsBean = sendResultService.getEmsForMsgid(msgid);
        try {
            String resptime = mailService.getRespTime(msgid);
            if(resptime.length() < 12) {
                resptime = resptime + "00";
            }
            String resp_time = resp_date + resp_hour + resp_min;
            sendResultService.updateRespTime(msgid, resp_time);
            //emsBean.setResp_time( resp_time );
            //session.setAttribute("emsbean", emsBean);

            result.setMessage(message.getMessage("E0590","반응분석 종료일이 변경되었습니다."));
            result.setResultCode(JSONResult.SUCCESS);
        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("{} - respTimeUpdate ADD ne ERROR",errorId);
            result.setResultCode(JSONResult.FAIL);
            result.setMessage(message.getMessage("E0060","작업중 오류가 발생하였습니다."));
            return result.toString();
        }
        catch (Exception e) {
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - respTimeUpdate ADD ERROR",errorId);
            result.setResultCode(JSONResult.FAIL);
            result.setMessage(message.getMessage("E0060","작업중 오류가 발생하였습니다."));
            return result.toString();
            //  model.addAttribute("StatSendForm", form);
        }
        return result.toString();
    }

    /**
     *
     * 발송 도메인 성공률 그래프를 그린다.
     *
     *
     * @param session
     * @param request
     * @param response
     * @param model
     */
/*    @RequestMapping(value = "staticSend.do", method = RequestMethod.GET)
    public staticHcData(@RequestParam(value = "msgid", required = false) String msgid, HttpSession session, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
        JSONObject result = new JSONObject();

        try {
            UserInfoBean userInfoBean = UserInfoBean.getUserSessionInfo(session);
            if (msgid == null) {
                result.setResultCode(JSONResult.FAIL);
                result.setMessage(message.getMessage("E0550", "선택된 항목이 없습니다."));
                return result.toString();
            }

        }catch (Exception e) {
            e.getMessage();
        }


    }*/

    /**
     * 개별발송결과 리스트 출력을 위한 처리를 행한다.
     * @param session
     * @param form
     * @return
     */
    @RequestMapping(value = "sendList.do")
    public String sendResultList(HttpSession session, @ModelAttribute("testSendListForm") TestSendListForm form, ModelMap model, HttpServletRequest request,
                                 HttpServletResponse response) {

        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-control", "no-cache");

        UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
        String permission = userSessionInfo.getPermission();
        String userid = userSessionInfo.getUserid();
        String srch_type = form.getSrch_type();
        String srch_keyword = form.getSrch_keyword();
        String send_type =form.getSend_type();
        boolean issearch = true;
        if(StringUtils.isEmpty(srch_keyword)) {
            issearch = false;
        }
        model.addAttribute("issearch", issearch);
        ImPage pageInfo = null;

        if (StringUtils.isNotEmpty(send_type)) {
            if (send_type.equals("0")) { // 테스트 발송
                send_type = "T";
            } else if (send_type.equals("1")) { // DB연동
                send_type = "D";
            } else if (send_type.equals("2")) { // SMTP 인증
                send_type = "A";
            } else if (send_type.equals("3")) { // 릴레이
                send_type = "R";
            } else if (send_type.equals("4")) { // 전체 선택
                send_type ="";
            }
        }


        int totalsize = 0;
        int cpage = ImStringUtil.parseInt(form.getCpage());
        int pageGroupSize = ImStringUtil.parseInt(form.getPagegroupsize());

        try {
            List<ImbTransmitData> imbTransmitData = null;
            // String sendtype = null;

            // 발송결과 처리 일반 사용자와 관리자 권한에 따라서 페이지 결과를 처리
            /*
            if (UserInfoBean.UTYPE_NORMAL.equals(permission)) { // 일반 사용자와 관리자 권한 구분
                totalsize = testSendService.getTestSendListCount(srch_keyword, srch_type, userid);
                pageInfo = new ImPage(cpage, userSessionInfo.getPagesize(), totalsize, pageGroupSize);
                imbTransmitData = testSendService.getTestSendList(srch_keyword, srch_type, userid, pageInfo.getStart(), pageInfo.getEnd());
            } else { // 관리자는 userid 조건을 걸지않고 모든 발송결과를 확인할 권한이 있으므로 파라메터를 전달하지 않음
                totalsize = testSendService.getTestSendListCount(srch_keyword, srch_type,null);
                pageInfo = new ImPage(cpage, userSessionInfo.getPagesize(), totalsize, pageGroupSize);
                imbTransmitData = testSendService.getTestSendList(srch_keyword, srch_type, null, pageInfo.getStart(), pageInfo.getEnd());
            }*/

            totalsize = testSendService.getTestSendListCount(srch_keyword, srch_type, send_type);
            pageInfo = new ImPage(cpage, userSessionInfo.getPagesize(), totalsize, pageGroupSize);
            imbTransmitData = testSendService.getTestSendList(srch_keyword, srch_type, pageInfo.getStart(), pageInfo.getEnd(),send_type);

            int count = imbTransmitData.size();

            model.addAttribute("pageInfo", pageInfo);
            model.addAttribute("testSendList", imbTransmitData);
            model.addAttribute("testSendListForm", form);


        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error(" getTestSend List ne error  :{}", errorId);
        }
        catch (Exception e) {
            String errorId = ErrorTraceLogger.log(e);
            log.error(" getTestSend List error  :{}", errorId);
        }

        return "/result/testSend_list";
    }

    /**
     * 개별 발송결과를 삭제한다.
     * @param session
     * @param ukeys
     * @return
     */
    @RequestMapping(value = "testDelete.json", method = RequestMethod.POST)
    @ResponseBody
    public String testSendDelete(HttpSession session, @RequestParam(value = "ukeys", required = false) String[] ukeys, HttpServletRequest request) {

        JSONResult result = new JSONResult();
        try {

            if (ukeys == null || ukeys.length == 0) {
                result.setResultCode(JSONResult.FAIL);
                result.setMessage(message.getMessage("E0550", "선택된 항목이 없습니다."));
                return result.toString();
            }

            UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);

            boolean checker = testSendService.deleteTestSendList(ukeys, userSessionInfo); // 권한이 있는 사용자인지 아닌지를 체크한다.

            if (checker) {
                result.setResultCode(JSONResult.SUCCESS);
                result.setMessage(message.getMessage("E0070", "삭제되었습니다."));

                //log insert start
                ActionLogForm logForm = new ActionLogForm();
                logForm.setIp(HttpRequestUtil.getRemoteAddr(request));
                logForm.setUserid(userSessionInfo.getUserid());
                logForm.setMenu_key("C1021");
                String param = "";
                for(int i=0; i<ukeys.length; i++){
                    if(i==0){
                        param= "메일 제목 : " + ukeys[i].split(",")[1] + " / 삭제 mgsid : " + ukeys[i].split(",")[0];
                    }else {
                        param += " , 메일 제목 : " + ukeys[i].split(",")[1] + " / 삭제 mgsid : " + ukeys[i].split(",")[0];
                    }
                }
                logForm.setParam(param);
                actionLogService.insertActionLog(logForm);

            } else {
                result.setResultCode(JSONResult.FAIL);
                result.setMessage(message.getMessage("E0606", "개별발송 목록은 관리자만 삭제할 수 있습니다."));
            }

        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("testSend Delete ERROR : {}", errorId);
            result.setResultCode(JSONResult.FAIL);
            result.setMessage(message.getMessage("E0060", "작업중 오류가 발생하였습니다."));
            return result.toString();
        }
        catch (Exception e) {
            String errorId = ErrorTraceLogger.log(e);
            log.error("testSend Delete ERROR : {}", errorId);
            result.setResultCode(JSONResult.FAIL);
            result.setMessage(message.getMessage("E0060", "작업중 오류가 발생하였습니다."));
            return result.toString();
        }

        return result.toString();
    }

    /**
     * 개별 발송결과 상세보기
     * @param request
     * @param response
     * @param session
     * @param traceid
     * @param serverid
     * @param rcptto
     * @return
     * @throws Exception
     */
    @RequestMapping(value="testSend.json" , method = RequestMethod.GET)
    @ResponseBody
    public String view( HttpServletRequest request , HttpServletResponse response,HttpSession session,@RequestParam("traceid")String traceid,
                        @RequestParam("serverid")String serverid,	@RequestParam("rcptto")String rcptto) throws Exception{

        JSONObject result = new JSONObject();

        ImbTransmitData data = testSendService.getTransmitDataLog(traceid, serverid, rcptto);
        result.put("transmitData",data);


        if("1".equals(data.getResult())) {
            result.put("msg",message.getMessage(data.getDescription(), "성공 - 외부로 메일 전송 완료"));
        }else if("2".equals(data.getResult())){
            result.put("msg",message.getMessage("E0412", "대기중"));
        }else {
            String[] args ={String.valueOf(data.getErrcode())};
            result.put("msg",message.getMessage(data.getDescription(),args , "오류 - 외부로 메일 발송 실패 "));
            result.put("Errmsg",data.getErrmsg());
        }

        return result.toString();
    }

    @RequestMapping(value = "staticReceipt.do", method = RequestMethod.GET)
    public String staticsReceipt(HttpSession session, ModelMap model, @RequestParam(value = "msgid", required = false) String msgid,
                                 @RequestParam(value = "srch_key", required = false) String srch_key,@RequestParam(value = "listcpage", required = false) String listcpage) {

        try {
            UserInfoBean userInfoBean = UserInfoBean.getUserSessionInfo(session);
            String permission = userInfoBean.getPermission();


            EmsBean emsBean = sendResultService.getEmsForMsgid(msgid);
            /** 해당 페이지를 사용할 권한이 존재하는지 확인한다. */
            if(permission.equals(UserInfoBean.UTYPE_NORMAL)){
                if(StringUtils.isNotEmpty(userInfoBean.getUserid())){
                    if(!userInfoBean.getUserid().equals(emsBean.getUserid())){ // userid가 다르다면 리스트 페이지로 이동한다.
                        log.error("reporting view - Authentication ERROR : {}", userInfoBean.getUserid());
                        return "redirect:/error/forbidden.do";
                    }
                }else{
                    log.error("reporting view  - USERID NULL ERROR : {}", userInfoBean.getUserid());
                    return "redirect:/error/forbidden.do";
                }
            }

            try {  // recv_(msgid) 테이블의 존재 유무 체크
                int recvCheck = sendResultService.getRecvTotalCountForMsgid(msgid);
                if(recvCheck == 0){
                    String categoryName = sendResultService.getCategoryName(emsBean.getCategoryid());
                    model.addAttribute("listcpage", listcpage);
                    model.addAttribute("srch_key", srch_key);
                    model.addAttribute("on", "receipt");
                    model.addAttribute("emsbean", emsBean);
                    model.addAttribute("categoryName", categoryName);
                    model.addAttribute("message", message.getMessage("E0586" , "데이터가 없습니다."));

                    return "/result_send/stat_receipt";
                }
            }catch (NullPointerException ne) {
                String categoryName = sendResultService.getCategoryName(emsBean.getCategoryid());
                model.addAttribute("listcpage", listcpage);
                model.addAttribute("srch_key", srch_key);
                model.addAttribute("on", "receipt");
                model.addAttribute("emsbean", emsBean);
                model.addAttribute("categoryName", categoryName);
                model.addAttribute("message", message.getMessage("E0586" , "데이터가 없습니다."));

                return "/result_send/stat_receipt";
            }
            catch (Exception e){
                // recv_(msgid) 테이블이 없다면 composer_area에 필요 한 데이터만 전송
                String categoryName = sendResultService.getCategoryName(emsBean.getCategoryid());

                model.addAttribute("listcpage", listcpage);
                model.addAttribute("srch_key", srch_key);
                model.addAttribute("on", "receipt");
                model.addAttribute("emsbean", emsBean);
                model.addAttribute("categoryName", categoryName);
                model.addAttribute("message", message.getMessage("E0586" , "데이터가 없습니다."));

                return "/result_send/stat_receipt";
            }

            int receipt = 0;
            int unreceipt = 0;
            int totalcount = 0;

            //IMB_RECEIPT_COUNT
            List<RecvMessageIDBean> recvMessageIDBean = null;
            try {
                recvMessageIDBean = sendResultService.getRecvCountIDForMsgid(msgid);
                if(recvMessageIDBean != null) {
                    totalcount = sendResultService.getRecvTotalCountForMsgid(msgid);
                    receipt = sendResultService.getRecvCountForMsgid(msgid, "1");
                    unreceipt = sendResultService.getRecvCountForMsgid(msgid, "0");
                }
            }catch (Exception e) {
                receipt = 0;
                unreceipt = 0;
                totalcount = 0;
                String errorId = ErrorTraceLogger.log(e);
                log.error("SendResult staticsReceipt.do ne ERROR :{}", errorId);
                throw new RuntimeException(e);
            }


            //카테고리
            String categoryName = sendResultService.getCategoryName(emsBean.getCategoryid());

            model.addAttribute("listcpage", listcpage);
            model.addAttribute("srch_key", srch_key);
            model.addAttribute("on", "receipt");
            model.addAttribute("emsbean", emsBean);
            model.addAttribute("recvMessageIDBean", recvMessageIDBean);
            model.addAttribute("receipt", receipt);
            model.addAttribute("msgid", msgid);
            model.addAttribute("unreceipt", unreceipt);
            model.addAttribute("totalcount", totalcount);
            model.addAttribute("categoryName", categoryName);

            //log insert start
            // TODO 로그정책 수정으로 삭제 (메일발송결과>리포팅조회 C105)

        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("SendResult staticsReceipt.do ne ERROR :{}", errorId);
        }
        catch (Exception e) {
            String errorId = ErrorTraceLogger.log(e);
            log.error("SendResult staticsReceipt.do ERROR :{}", errorId);
        }
        return "/result_send/stat_receipt";
    }

    @RequestMapping(value = "statisticsReceiptDetail.do", method = RequestMethod.GET)
    public String statisticsReceiptDetail(HttpSession session, ModelMap model, @RequestParam(value="msgid", required=false) String msgid,
                                          @RequestParam(value="recv_date", required = false) String recv_date,
                                          @RequestParam(value="receipt", required = false) Integer receipt) {
        try {
            UserInfoBean userInfoBean = UserInfoBean.getUserSessionInfo(session);
            String permission = userInfoBean.getPermission();


            EmsBean emsBean = sendResultService.getEmsForMsgid(msgid);
            /** 해당 페이지를 사용할 권한이 존재하는지 확인한다. */
            if (permission.equals(UserInfoBean.UTYPE_NORMAL)) {
                if (StringUtils.isNotEmpty(userInfoBean.getUserid())) {
                    if (!userInfoBean.getUserid().equals(emsBean.getUserid())) { // userid가 다르다면 리스트 페이지로 이동한다.
                        log.error("reporting view - Authentication ERROR : {}", userInfoBean.getUserid());
                        return "redirect:/error/forbidden.do";
                    }
                } else {
                    log.error("reporting view  - USERID NULL ERROR : {}", userInfoBean.getUserid());
                    return "redirect:/error/forbidden.do";
                }
            }

            if(receipt == null) {
                receipt = 0;
            }
            int unreceipt = 0;


            // 수신률 원형 그래프 구현
            LinkedHashMap<String, Integer> EmsDateMap = null;
            int[] data = new int[12];
            try {
                EmsDateMap = sendResultService.setReceiptData(msgid,recv_date,receipt,unreceipt);
                data = sendResultService.setReceiptData2(msgid,recv_date,receipt,unreceipt);
            }catch (NullPointerException ne) {
                String errorId = ErrorTraceLogger.log(ne);
                log.error("reporting errDataMap sorting ne ERROR :{}", errorId);
            }
            catch (Exception e){
                String errorId = ErrorTraceLogger.log(e);
                log.error("reporting errDataMap sorting ERROR :{}", errorId);
            }
            model.addAttribute("recvDate", recv_date);
            model.addAttribute("emsMap", EmsDateMap);
            model.addAttribute("data", data);




        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("SendResult statisticsReceiptDetail.do ERROR :{}", errorId);
        }
        catch (Exception e) {
            String errorId = ErrorTraceLogger.log(e);
            log.error("SendResult statisticsReceiptDetail.do ERROR :{}", errorId);
        }
        return "/popup/result/popup_receiptDetail";

    }

    @RequestMapping(value="statisticsReceiptList.do")
    public String statisticsReceiptList(@ModelAttribute("receiverListForm") ReceiverListForm form, HttpSession session, ModelMap model,
                                        @RequestParam(value="msgid", required=false) String msgid,
                                        @RequestParam(value="recv_date", required = false) String recv_date,
                                        @RequestParam(value="receipt", required=false) Integer receipt,
                                        @RequestParam(value="unreceipt", required=false) Integer unreceipt,
                                        HttpServletResponse response) throws Exception {

        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-control", "no-cache");

        UserInfoBean userInfoBean = UserInfoBean.getUserSessionInfo(session);
        String permission = userInfoBean.getPermission();

        ImPage pageInfo = null;
        int totalsize = 0;
        int cpage = ImStringUtil.parseInt(form.getCpage());
        int pageGroupSize = ImStringUtil.parseInt(form.getPagegroupsize());
        String srch_type = form.getSrch_type();
        String srch_keyword = form.getSrch_keyword();
        String recv_count = form.getRecv_count();

        boolean issearch = true;

        if(StringUtils.isEmpty(srch_keyword)) {
            issearch = false;
        }
        model.addAttribute("issearch", issearch);

        if(StringUtils.isNotEmpty(srch_type) && "01".equals(srch_type.toLowerCase()) && StringUtils.isNotEmpty(srch_keyword)){
            if(ImbConstant.DATABASE_ENCRYPTION_USE){
                String secret_key = ImbConstant.DATABASE_AES_KEY;
                srch_keyword = ImSecurityLib.encryptAES256(secret_key,srch_keyword);
            }
        }
        try {

            EmsBean emsBean = sendResultService.getEmsForMsgid(msgid);
            /** 해당 페이지를 사용할 권한이 존재하는지 확인한다. */
            if (permission.equals(UserInfoBean.UTYPE_NORMAL)) {
                if (StringUtils.isNotEmpty(userInfoBean.getUserid())) {
                    if (!userInfoBean.getUserid().equals(emsBean.getUserid())) { // userid가 다르다면 리스트 페이지로 이동한다.
                        log.error("reporting view - Authentication ERROR : {}", userInfoBean.getUserid());
                        return "redirect:/error/forbidden.do";
                    }
                } else {
                    log.error("reporting view  - USERID NULL ERROR : {}", userInfoBean.getUserid());
                    return "redirect:/error/forbidden.do";
                }
            }
            if(form.getRecv_date() != null){
                recv_date = form.getRecv_date();
            }
            String flag = null;
            List<RecvMessageIDBean> recvMessageIDBean = null;
            try{
                if(recv_date != null) {
                    totalsize = sendResultService.getRecvCountForDate(msgid, recv_date, srch_keyword, srch_type, recv_count);
                    pageInfo = new ImPage(cpage, userInfoBean.getPagesize(), totalsize, pageGroupSize);
                }else if(receipt != null){
                    totalsize = sendResultService.getRecvCountFlagForMsgid(msgid, srch_keyword, srch_type, recv_count);
                    pageInfo = new ImPage(cpage, userInfoBean.getPagesize(), totalsize, pageGroupSize);
                }else{
                    totalsize = sendResultService.getRecvCountFlagForMsgid(msgid, srch_keyword, srch_type, recv_count);
                    pageInfo = new ImPage(cpage, userInfoBean.getPagesize(), totalsize, pageGroupSize);
                }

//                if(receipt != null) {
//                    recvMessageIDBean = sendResultService.getRecvListPageingForMsgid(msgid, pageInfo.getStart(), pageInfo.getEnd(), srch_keyword, srch_type, recv_date, recv_count);
//                }else if (unreceipt != null){
//                    recvMessageIDBean = sendResultService.getRecvListPageingForMsgid(msgid, pageInfo.getStart(), pageInfo.getEnd(), srch_keyword, srch_type, recv_date, recv_count);
//                }else{
//                    recvMessageIDBean = sendResultService.getRecvListPageingForMsgid(msgid, pageInfo.getStart(), pageInfo.getEnd(), srch_keyword, srch_type, recv_date, recv_count);
//
//
//                }

                recvMessageIDBean = sendResultService.getRecvListPageingForMsgid(msgid, pageInfo.getStart(), pageInfo.getEnd(), srch_keyword, srch_type, recv_date, recv_count);


            }catch (NullPointerException ne) {
                totalsize = 0;
                String errorId = ErrorTraceLogger.log(ne);
                log.error("SendResult statisticsReceiptList.do ne ERROR :{}", errorId);
            }
            catch(Exception e){
                totalsize = 0;
                String errorId = ErrorTraceLogger.log(e);
                log.error("SendResult statisticsReceiptList.do ERROR :{}", errorId);
            }


            model.addAttribute("recvMessageIDBean", recvMessageIDBean);
            model.addAttribute("recvDate", recv_date);
            model.addAttribute("msgid", msgid);
            model.addAttribute("receipt", receipt);
            model.addAttribute("unreceipt", unreceipt);
            model.addAttribute("form", form);
            model.addAttribute("pageInfo", pageInfo);

        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("SendResult statisticsReceiptList.do ne ERROR :{}", errorId);
        }
        catch (Exception e) {
            String errorId = ErrorTraceLogger.log(e);
            log.error("SendResult statisticsReceiptList.do ERROR :{}", errorId);
        }

        return "/popup/result/popup_receiptList";
    }
    /**
     * 수신자 목록 다운로드
     * @param form
     * @param
     * @return
     */
    @RequestMapping(value ="downReceiptList.do", method = RequestMethod.GET)
    public ModelAndView downReceiptList(@ModelAttribute("ReceiverListForm") ReceiverListForm form, @RequestParam(value="msgid",required=false) String msgid,
                                        @RequestParam(value="recvdate", required = false) String recv_date) {

        String tempFileName = CommonFile.getFilePath(ImbConstant.TEMPFILE_PATH, ImUtils.makeKeyNum(24) + ".xlsx");
        if(form.getMsgid() != null && form.getRecv_date() != null) {
            msgid = form.getMsgid();
            recv_date = form.getRecv_date();
        }
        String fileName = recv_date + "_List.xlsx";

        File file = new File(tempFileName);

        try{

            List<RecvMessageIDBean> recvMessageIDBean = sendResultService.getRecvListForMsgid(msgid,recv_date);

            sendResultService.getXlsxDownload(recvMessageIDBean, tempFileName);


        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("Receipt List Download ne Error", errorId);
        }
        catch(Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("Receipt List Download Error", errorId);
        }
        return CommonFile.getDownloadView(file, fileName);
    }

    /**
     * 수신확인, 미확인 목록 다운로드
     * @param
     * @param session
     * @return
     */
    @RequestMapping(value ="listDownReceiptList.do", method = RequestMethod.GET)
    public ModelAndView listDownReceiptList(@RequestParam(value="msgid",required=false) String msgid, @RequestParam(value="receipt", required = false) Integer receipt, HttpSession session) {
        String tempFileName = CommonFile.getFilePath(ImbConstant.TEMPFILE_PATH, ImUtils.makeKeyNum(24) + ".xlsx");
        String fileName = "";

        File file = new File(tempFileName);

        try{
            List<RecvMessageIDBean> recvMessageIDBean = null;
            if(receipt != null) {
                fileName = "수신확인" + "_List.xlsx";
                recvMessageIDBean = sendResultService.getRecvMessageIDForMsgidFlag(msgid,"1");
            }else {
                fileName = "수신미확인" + "_List.xlsx";
                recvMessageIDBean = sendResultService.getRecvMessageIDForMsgidFlag(msgid,"0");
            }
            sendResultService.getXlsxDownload(recvMessageIDBean, tempFileName);


        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("Receipt List Download ne Error", errorId);
        }
        catch(Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("Receipt List Download Error", errorId);
        }
        return CommonFile.getDownloadView(file, fileName);
    }


    @RequestMapping(value ="listDownlinkList.do", method = RequestMethod.GET)
    public ModelAndView listDownLinkList(@ModelAttribute("linkListForm")LinkListForm form, @RequestParam(value="msgid",required=false) String msgid, Integer receipt, HttpSession session) {
        String tempFileName = CommonFile.getFilePath(ImbConstant.TEMPFILE_PATH, ImUtils.makeKeyNum(24) + ".xlsx");
        String fileName = "";

        File file = new File(tempFileName);

        try{
            List<LinkLogMessageIDBean> linkLogMessageIDBeans = null;
            fileName = "linkClick" + "_List.xlsx";
            linkLogMessageIDBeans = sendResultService.getLinkMessageUserForMsgid(form);

            sendResultService.getXlsxlinkDownload(linkLogMessageIDBeans, tempFileName);


        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("Receipt List Download ne Error", errorId);
        }
        catch(Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("Receipt List Download Error", errorId);
        }
        return CommonFile.getDownloadView(file, fileName);
    }


    @RequestMapping(value="staticLink.do")
    public String staticsLink(@ModelAttribute("linkListForm")LinkListForm form,HttpSession session, ModelMap model, @RequestParam(value = "msgid", required = false) String msgid,
                              @RequestParam(value = "srch_key", required = false) String srch_key, @RequestParam(value = "listcpage", required = false) String listcpage,
                              HttpServletResponse response, HttpServletRequest request) {
        try {

            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-control", "no-cache");

            UserInfoBean userInfoBean = UserInfoBean.getUserSessionInfo(session);
            String permission = userInfoBean.getPermission();

            int cpage = ImStringUtil.parseInt(form.getCpage());
            listcpage =request.getParameter("listcpage");
            if(listcpage == null){
                listcpage = String.valueOf(cpage);
            }
            EmsBean emsBean = sendResultService.getEmsForMsgid(msgid);
            /** 해당 페이지를 사용할 권한이 존재하는지 확인한다. */
            if(permission.equals(UserInfoBean.UTYPE_NORMAL)){
                if(StringUtils.isNotEmpty(userInfoBean.getUserid())){
                    if(!userInfoBean.getUserid().equals(emsBean.getUserid())){ // userid가 다르다면 리스트 페이지로 이동한다.
                        log.error("reporting view - Authentication ERROR : {}", userInfoBean.getUserid());
                        return "redirect:/error/forbidden.do";
                    }
                }else{
                    log.error("reporting view  - USERID NULL ERROR : {}", userInfoBean.getUserid());
                    return "redirect:/error/forbidden.do";
                }
            }
            //imb_link_info 에서 링크가 존재하는지 체크
            int count = sendResultService.getLinkCount(msgid);

            if(count == 0){ //imb_link_info에 값이 없다면 composer_area에 필요 한 데이터만 전송

                String categoryName = sendResultService.getCategoryName(emsBean.getCategoryid());

                model.addAttribute("listcpage", listcpage);
                model.addAttribute("srch_key", srch_key);
                model.addAttribute("categoryName", categoryName);
                model.addAttribute("on", "link");
                model.addAttribute("emsbean", emsBean);
                model.addAttribute("message", message.getMessage("E0586" , "데이터가 없습니다."));

                return "/result_send/stat_link";
            }

            //IMB_LINK_INFO
            List<LinkBean> linkMessageIDBean = sendResultService.getLinkMessageIDForMsgid(msgid);

            int pageGroupSize = ImStringUtil.parseInt(form.getPagegroupsize());
            int totalsize = linkMessageIDBean.size();

            ImPage pageInfo = new ImPage(cpage, 10, totalsize, pageGroupSize);

            List<LinkBean> linkMessageIDBean2 = sendResultService.getLinkMessageIDForMsgid2(msgid, pageInfo.getStart(), pageInfo.getEnd());


            //카테고리
            String categoryName = sendResultService.getCategoryName(emsBean.getCategoryid());

            model.addAttribute("cpage", cpage);
            model.addAttribute("listcpage", listcpage);
            model.addAttribute("srch_key", srch_key);
            model.addAttribute("on", "link");
            model.addAttribute("emsbean", emsBean);
            model.addAttribute("pageInfo", pageInfo);
            model.addAttribute("linkMessageIDBean", linkMessageIDBean2);
            model.addAttribute("categoryName", categoryName);

            //log insert start
            // TODO 로그정책 수정으로 삭제 (메일발송결과>리포팅조회 C105)

        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("SendResult staticLink.do ne ERROR :{}", errorId);
        }
        catch (Exception e) {
            String errorId = ErrorTraceLogger.log(e);
            log.error("SendResult staticLink.do ERROR :{}", errorId);
        }

        return "/result_send/stat_link";
    }

    @RequestMapping(value="statisticsLinkDetailList.do", method=RequestMethod.GET)
    public String statisticsLinkDetailList(HttpSession session, ModelMap model, @RequestParam(value="msgid", required=false) String msgid,
                                           @RequestParam(value="linkid", required = false) String linkid, @RequestParam(value="click_date", required = false) String click_date,
                                           @RequestParam(value="cpage", required = false) String cpage, @RequestParam(value="srch_key", required = false) String srch_key) {
        UserInfoBean userInfoBean = UserInfoBean.getUserSessionInfo(session);
        String permission = userInfoBean.getPermission();

        try {
            EmsBean emsBean = sendResultService.getEmsForMsgid(msgid);
            /** 해당 페이지를 사용할 권한이 존재하는지 확인한다. */
            if (permission.equals(UserInfoBean.UTYPE_NORMAL)) {
                if (StringUtils.isNotEmpty(userInfoBean.getUserid())) {
                    if (!userInfoBean.getUserid().equals(emsBean.getUserid())) { // userid가 다르다면 리스트 페이지로 이동한다.
                        log.error("reporting view - Authentication ERROR : {}", userInfoBean.getUserid());
                        return "redirect:/error/forbidden.do";
                    }
                } else {
                    log.error("reporting view  - USERID NULL ERROR : {}", userInfoBean.getUserid());
                    return "redirect:/error/forbidden.do";
                }
            }

            List<LinkLogMessageIDBean> linkLogMessageIDBean = null;

            int totalcount = 0;
            int linkClickUser = 0;
            List<LinkLogMessageIDBean> click_data = null;


            try{
                linkLogMessageIDBean = sendResultService.getLinkLogMessageForMsgid(msgid, linkid);
                totalcount = sendResultService.getRecvTotalCountForMsgid(msgid);
                linkClickUser = sendResultService.getLinkClickUser(msgid, linkid);

            }catch (NullPointerException ne) {
                String errorId = ErrorTraceLogger.log(ne);
                log.error("SendResult statisticsLinkDetailList.do ne ERROR :{}", errorId);
            }
            catch(Exception e){
                String errorId = ErrorTraceLogger.log(e);
                log.error("SendResult statisticsLinkDetailList.do ERROR :{}", errorId);
            }

            model.addAttribute("cpage", cpage);
            model.addAttribute("srch_key", srch_key);
            model.addAttribute("msgid", msgid);
            model.addAttribute("linkid", linkid);
            model.addAttribute("totalcount", totalcount);
            model.addAttribute("LinkClickUser", linkClickUser);
            model.addAttribute("linkLogMessageIDBean", linkLogMessageIDBean);



        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("SendResult statisticsLinkDetailList.do ne ERROR :{}", errorId);
        }
        catch (Exception e) {
            String errorId = ErrorTraceLogger.log(e);
            log.error("SendResult statisticsLinkDetailList.do ERROR :{}", errorId);
        }


        return "/popup/result/popup_linkDetailList";
    }

    @RequestMapping(value="statisticsLinkDetail.do", method=RequestMethod.GET)
    public String statisticsLinkDetail(HttpSession session, ModelMap model, @RequestParam(value="msgid", required=false) String msgid,
                                       @RequestParam(value="click_date", required = false) String click_date, @RequestParam(value="linkid") String linkid) {
        try {
            UserInfoBean userInfoBean = UserInfoBean.getUserSessionInfo(session);
            String permission = userInfoBean.getPermission();
            String cdate = "";

            EmsBean emsBean = sendResultService.getEmsForMsgid(msgid);
            /** 해당 페이지를 사용할 권한이 존재하는지 확인한다. */
            if (permission.equals(UserInfoBean.UTYPE_NORMAL)) {
                if (StringUtils.isNotEmpty(userInfoBean.getUserid())) {
                    if (!userInfoBean.getUserid().equals(emsBean.getUserid())) { // userid가 다르다면 리스트 페이지로 이동한다.
                        log.error("reporting view - Authentication ERROR : {}", userInfoBean.getUserid());
                        return "redirect:/error/forbidden.do";
                    }
                } else {
                    log.error("reporting view  - USERID NULL ERROR : {}", userInfoBean.getUserid());
                    return "redirect:/error/forbidden.do";
                }
            }


            // 링크 클릭률 원형 그래프 구현
            LinkedHashMap<String, Integer> EmsDateMap = null;
            //jsp에서 데이터테이블을 합침으로써 13시이후의 데이터값을 받을 배열 추가
            int[] data = new int[12];
            try {
                EmsDateMap = sendResultService.setLinkData(msgid, click_date, linkid);
                data = sendResultService.setLinkData2(msgid, click_date, linkid);
                cdate = click_date;

            }catch (NullPointerException ne) {
                String errorId = ErrorTraceLogger.log(ne);
                log.error("reporting errDataMap sorting ne ERROR :{}", errorId);
            }
            catch (Exception e){
                String errorId = ErrorTraceLogger.log(e);
                log.error("reporting errDataMap sorting ERROR :{}", errorId);
            }
            model.addAttribute("emsMap", EmsDateMap);
            model.addAttribute("data", data);
            model.addAttribute("click_date", cdate);



        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("SendResult statisticsLinkDetail.do ne ERROR :{}", errorId);
        }
        catch (Exception e) {
            String errorId = ErrorTraceLogger.log(e);
            log.error("SendResult statisticsLinkDetail.do ERROR :{}", errorId);
        }

        return "/popup/result/popup_linkDetail";
    }

    @RequestMapping(value="statisticsLinkList.do")
    public String statisticsLinkList(@ModelAttribute("linkListForm")LinkListForm form, HttpSession session, ModelMap model, @RequestParam(value="msgid", required=false) String msgid,
                                     HttpServletResponse response) throws Exception {

        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-control", "no-cache");

        UserInfoBean userInfoBean = UserInfoBean.getUserSessionInfo(session);
        String permission = userInfoBean.getPermission();

        ImPage pageInfo = null;
        int totalsize = 0;
        int cpage = ImStringUtil.parseInt(form.getCpage());
        int pageGroupSize = ImStringUtil.parseInt(form.getPagegroupsize());
        String srch_type = form.getSrch_type();
        String srch_keyword = form.getSrch_keyword();

        boolean issearch = true;

        if(StringUtils.isEmpty(srch_keyword)) {
            issearch = false;
        }
        model.addAttribute("issearch", issearch);

        if(StringUtils.isNotEmpty(srch_type) && "01".equals(srch_type.toLowerCase()) && StringUtils.isNotEmpty(srch_keyword)){
            if(ImbConstant.DATABASE_ENCRYPTION_USE){
                String secret_key = ImbConstant.DATABASE_AES_KEY;
                srch_keyword = ImSecurityLib.encryptAES256(secret_key,srch_keyword);
            }
        }




        try {

            EmsBean emsBean = sendResultService.getEmsForMsgid(msgid);
            /** 해당 페이지를 사용할 권한이 존재하는지 확인한다. */
            if (permission.equals(UserInfoBean.UTYPE_NORMAL)) {
                if (StringUtils.isNotEmpty(userInfoBean.getUserid())) {
                    if (!userInfoBean.getUserid().equals(emsBean.getUserid())) { // userid가 다르다면 리스트 페이지로 이동한다.
                        log.error("reporting view - Authentication ERROR : {}", userInfoBean.getUserid());
                        return "redirect:/error/forbidden.do";
                    }
                } else {
                    log.error("reporting view  - USERID NULL ERROR : {}", userInfoBean.getUserid());
                    return "redirect:/error/forbidden.do";
                }
            }

            List<LinkLogMessageIDBean> linkListUserForm = null;
            try{
                totalsize = sendResultService.getLinkMessageCountForMsgid(form);
                pageInfo = new ImPage(cpage, userInfoBean.getPagesize(), totalsize, pageGroupSize);
                linkListUserForm = sendResultService.getLinkMessageUserForMsgid2(form.getMsgid(),form.getLinkid(),srch_type, srch_keyword, pageInfo.getStart(), pageInfo.getEnd());
                /*int count[] = new int[totalsize];
                for(int i=0; i<totalsize; i++){
                    count[i] = i;
                }*/
            }catch (NullPointerException ne) {
                String errorId = ErrorTraceLogger.log(ne);
                log.error("SendResult statisticsLinkList.do ne ERROR :{}", errorId);
            }
            catch(Exception e){
                String errorId = ErrorTraceLogger.log(e);
                log.error("SendResult statisticsLinkList.do ERROR :{}", errorId);
            }


            model.addAttribute("msgid", msgid);
            model.addAttribute("pageInfo", pageInfo);
            model.addAttribute("linkid", form.getLinkid());
            model.addAttribute("linkListUserForm", linkListUserForm);

        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("SendResult statisticsLinkList.do ne ERROR :{}", errorId);
        }
        catch (Exception e) {
            String errorId = ErrorTraceLogger.log(e);
            log.error("SendResult statisticsLinkList.do ERROR :{}", errorId);
        }

        return "/popup/result/popup_linkList";

    }
    /**
     * 링크 클릭 목록 다운로드
     * @param form
     * @param
     * @return
     */
    @RequestMapping(value ="downLinkClickList.do", method = RequestMethod.GET)
    public ModelAndView downLinkClickList(@ModelAttribute("linkListForm")LinkListForm form, @RequestParam(value="msgid",required=false) String msgid) {


        String srch_type = form.getSrch_type();
        String srch_keyword = form.getSrch_keyword();
        String tempFileName = CommonFile.getFilePath(ImbConstant.TEMPFILE_PATH, ImUtils.makeKeyNum(24) + ".xlsx");
        String fileName = "";

        File file = new File(tempFileName);

        try{
            fileName = "링크클릭" + form.getLinkid() + "_List.xlsx";
            List<LinkLogMessageIDBean> linkLogMessageIdBean = sendResultService.getLinkMessageUserForMsgid(form);

            sendResultService.getlinkXlsxDownload(linkLogMessageIdBean, tempFileName);


        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("Receipt List Download Error", errorId);
        }
        catch(Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("Receipt List Download Error", errorId);
        }
        return CommonFile.getDownloadView(file, fileName);
    }



    @RequestMapping(value="staticPage.do")
    public String EmsContents(@ModelAttribute("emsListForm") EmsListForm form, HttpServletRequest request,
                              ModelMap model, @RequestParam("msgid") String msgid,@RequestParam("listcpage") String listcpage,
                              @RequestParam("srch_key") String srch_key) throws Exception {

        try {
            //String msgid = form.getMsgid();
            EmsBean emsBean = mailService.getMailcontentData(msgid);
            log.info("contents : {}", emsBean.getContents());
            form.setContents(emsBean.getContents());

            //카테고리
            String categoryName = sendResultService.getCategoryName(emsBean.getCategoryid());

            model.addAttribute("listcpage", listcpage);
            model.addAttribute("srch_key", srch_key);
            model.addAttribute("on", "page");
            model.addAttribute("emsbean", emsBean);
            model.addAttribute("emsListForm", form);
            model.addAttribute("categoryName", categoryName);

        }catch (NullPointerException ne ) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("getEmsPage ne error", errorId);
        }
        catch (Exception e) {
            String errorId = ErrorTraceLogger.log(e);
            log.error("getEmsPage error", errorId);
        }

        return "/result_send/stat_page";
    }


    /**
     *
     * 에러통계 탭 그래프 출력
     *
     *
     * @param session
     * @param request
     * @param response
     * @param model
     */
    @RequestMapping(value = "staticError.do", method = RequestMethod.GET)
    public String statisticsError(@RequestParam(value = "msgid", required = false) String msgid,@RequestParam(value = "srch_key", required = false) String srch_key,
                                  @RequestParam(value = "listcpage", required = false) String listcpage, HttpSession session, HttpServletRequest request, HttpServletResponse response, ModelMap model) {

        try {
            EmsBean emsBean = sendResultService.getEmsForMsgid(msgid);
            ImbErrorCount imbErrorCount = sendResultService.getErrorCountForMsgid(msgid);

            //카테고리
            String categoryName = sendResultService.getCategoryName(emsBean.getCategoryid());

            if(imbErrorCount == null){ //imb_error_count에 값이 없다면 composer_area에 필요 한 데이터만 전송
                model.addAttribute("listcpage", listcpage);
                model.addAttribute("srch_key", srch_key);
                model.addAttribute("emsbean", emsBean);
                model.addAttribute("on", "error");
                model.addAttribute("msgid", msgid);
                model.addAttribute("categoryName", categoryName);
                model.addAttribute("message", message.getMessage("E0586" , "데이터가 없습니다."));

                return "/result_send/stat_error";
            }

            ArrayList<ErrorCountBean> errorlist = sendResultService.setErrorList(imbErrorCount);

            model.addAttribute("listcpage", listcpage);
            model.addAttribute("srch_key", srch_key);
            model.addAttribute("emsbean", emsBean);
            model.addAttribute("on", "error");
            model.addAttribute("msgid", msgid);
            model.addAttribute("errDataMap", errorlist);
            model.addAttribute("categoryName", categoryName);

        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("getStaticError List ne error :{}", errorId);
        }
        catch (Exception e) {
            String errorId = ErrorTraceLogger.log(e);
            log.error("getStaticError List error :{}", errorId);
        }

        return "/result_send/stat_error";
    }


    /**
     *
     * 에러통계 탭 목록보기 팝업
     *
     *
     * @param session
     * @param request
     * @param response
     * @param model
     */
    @RequestMapping(value = "statErrorList.do")
    public String statisticsErrorList(@RequestParam(value = "msgid", required = false) String msgid, @RequestParam(value = "errcode", required = false) String code,
                                      @ModelAttribute("StatErrorForm") StatErrorForm form, HttpSession session, HttpServletRequest request, HttpServletResponse response, ModelMap model) throws Exception {

        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-control", "no-cache");

        ImPage pageInfo = null;
        int totalsize = 0;
        int cpage = ImStringUtil.parseInt(form.getCpage());
        int pageGroupSize = ImStringUtil.parseInt(form.getPagegroupsize());
        String srch_type = form.getSrch_type();
        String srch_keyword = form.getSrch_keyword();
        List<RecvMessageIDBean> list = null;

        boolean issearch = true;

        if(StringUtils.isEmpty(srch_keyword)) {
            issearch = false;
        }
        model.addAttribute("issearch", issearch);

        if(StringUtils.isNotEmpty(srch_type) && "01".equals(srch_type.toLowerCase()) && StringUtils.isNotEmpty(srch_keyword)){
            if(ImbConstant.DATABASE_ENCRYPTION_USE){
                String secret_key = ImbConstant.DATABASE_AES_KEY;
                srch_keyword = ImSecurityLib.encryptAES256(secret_key,srch_keyword);
            }
        }

        try {
            totalsize = sendResultService.getStatErrorListCount(msgid, code, srch_keyword, srch_type);
            pageInfo = new ImPage(cpage, 10, totalsize, pageGroupSize);
            list = sendResultService.getStatErrorList(msgid, code, srch_keyword, srch_type, pageInfo.getStart(), pageInfo.getEnd());

            model.addAttribute("errorList", list);
            model.addAttribute("pageInfo", pageInfo);
            model.addAttribute("msgid", msgid);

        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("statErrorList ne error :{}", errorId);
        }
        catch (Exception e) {
            String errorId = ErrorTraceLogger.log(e);
            log.error("statErrorList error :{}", errorId);
        }

        return "/popup/result/popup_statErrList";
    }


    /**
     *
     * 에러통계 탭 에러목록 다운로드
     *
     *
     * @param session
     * @param request
     * @param response
     * @param model
     */
    @RequestMapping(value = "statErrorDownload.do", method = RequestMethod.GET)
    public ModelAndView statisticsErrorListDownload(@RequestParam(value = "msgid", required = false) String msgid, @RequestParam(value = "code", required = false) String code, HttpSession session, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
        String tempFileName = CommonFile.getFilePath(ImbConstant.TEMPFILE_PATH, ImUtils.makeKeyNum(24) + ".xlsx");
        String fileName = msgid + "_error_List.xlsx";

        File file = new File(tempFileName);

        try {
            List<RecvMessageIDBean> list = sendResultService.getDownloadStatErrorList(msgid, code);
            sendResultService.getXlsxDownload(list, tempFileName, msgid);

        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("Error List Download ne Error", errorId);
        }
        catch (Exception e) {
            String errorId = ErrorTraceLogger.log(e);
            log.error("Error List Download Error", errorId);
        }

        return CommonFile.getDownloadView(file, fileName);
    }

    /**
     *
     * 에러메일 재발송 버튼 클릭 시
     *
     *
     */
    @ResponseBody
    @RequestMapping(value = "errorResend.json", method = RequestMethod.POST)
    public String errorResend(HttpServletRequest request, HttpSession session, ModelMap model,@RequestParam("msgid")String msgid ) throws Exception {
        UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
        String userid = userSessionInfo.getUserid();
        JSONResult result = new JSONResult();
        String permission = userSessionInfo.getPermission();

        if(StringUtils.isEmpty(msgid)) {
            result.setResultCode(JSONResult.FAIL);
            result.setMessage(message.getMessage("E0060", "작업중 오류가 발생하였습니다."));
            return result.toString();
        }

        msgid = msgid.split(",")[0];
        EmsBean bean = mailService.noUseridGetMailData(msgid);

        /** 해당 페이지를 사용할 권한이 존재하는지 확인한다. */
        if (permission.equals(UserInfoBean.UTYPE_NORMAL)) { // 관리자인지 체크
            if (StringUtils.isNotEmpty(userid)) {
                if (!userid.equals(bean.getUserid())) {
                    log.error("SendResult permissionCheck.json - Authentication ERROR : {}", userid);
                    result.setResultCode(JSONResult.FAIL);
                    result.setMessage(message.getMessage("E0760", "발송 권한이 없습니다."));
                    return result.toString();
                }
            } else {
                log.error("SendResult permissionCheck.json - USERID NULL ERROR");
                result.setResultCode(JSONResult.FAIL);
                result.setMessage(message.getMessage("E0060", "작업중 오류가 발생하였습니다."));
                return result.toString();
            }
        }

        try {
            int fail_count = sendResultService.getRecvMessageIDSuccessAndFailCount(msgid, "0");
            if(fail_count > 0) {
                sendMailService.insertErrorResendMailData(msgid, request);
                result.setMessage(message.getMessage("E0379", "발송완료"));
            }else {
                result.setResultCode(JSONResult.FAIL);
                result.setMessage(message.getMessage("E0630","에러 데이터가 존재하지 않습니다."));
                return result.toString();
            }
        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("{} - errorResend ne ERROR",errorId);
            result.setResultCode(JSONResult.FAIL);
            result.setMessage(message.getMessage("E0060","작업중 오류가 발생하였습니다."));
            return result.toString();
        }
        catch (Exception e) {
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - errorResend ERROR",errorId);
            result.setResultCode(JSONResult.FAIL);
            result.setMessage(message.getMessage("E0060","작업중 오류가 발생하였습니다."));
            return result.toString();
        }
        return result.toString();
    }

    @ResponseBody
    @RequestMapping(value = "clickResend.json", method = RequestMethod.POST)
    public String rcptResend(HttpServletRequest request, HttpSession session, ModelMap model,
                             @RequestParam("msgid")String msgid, @RequestParam("flag")String flag, @RequestParam(value = "linkid", required = false)String linkid){
        JSONResult result = new JSONResult();
        UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);

        if(StringUtils.isEmpty(msgid)) {
            result.setResultCode(JSONResult.FAIL);
            result.setMessage(message.getMessage("E0060", "작업중 오류가 발생하였습니다."));
            return result.toString();
        }

        try {
            String userid = userSessionInfo.getUserid();
            String permission = userSessionInfo.getPermission();
            EmsBean bean = mailService.noUseridGetMailData(msgid);

            /** 해당 페이지를 사용할 권한이 존재하는지 확인한다. */
            if (permission.equals(UserInfoBean.UTYPE_NORMAL)) { // 관리자인지 체크
                if (StringUtils.isNotEmpty(userid)) {
                    if (!userid.equals(bean.getUserid())) {
                        log.error("SendResult permissionCheck.json - Authentication ERROR : {}", userid);
                        result.setResultCode(JSONResult.FAIL);
                        result.setMessage(message.getMessage("E0760", "발송 권한이 없습니다."));
                        return result.toString();
                    }
                } else {
                    log.error("SendResult permissionCheck.json - USERID NULL ERROR");
                    result.setResultCode(JSONResult.FAIL);
                    result.setMessage(message.getMessage("E0060", "작업중 오류가 발생하였습니다."));
                    return result.toString();
                }
            }

            int count = 0;
            int num = 0;

            if(StringUtils.equals(flag, "rcpt") || StringUtils.equals(flag,"norcpt")){
                count = sendResultService.getRecvCountForMsgid(msgid, flag.equals("rcpt") ? "1" : "0");
            }else if(flag.equals("link")){
                count = sendResultService.getLinkCountData(msgid, linkid);
                if(count == 0) num++;
            }

            if(count > 0){
                result.setResultCode(JSONResult.SUCCESS);
            }else{
                result.setResultCode(JSONResult.FAIL);
                if(num > 0){
                    result.setMessage(message.getMessage("E0586","데이터가 없습니다."));
                }else{
                    result.setMessage(message.getMessage("E0629","수신자가 존재하지 않습니다."));
                }
            }

        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("{} - clickResend ne ERROR",errorId);
            result.setResultCode(JSONResult.FAIL);
            result.setMessage(message.getMessage("E0060","작업중 오류가 발생하였습니다."));
            return result.toString();
        }
        catch (Exception e) {
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - clickResend ERROR",errorId);
            result.setResultCode(JSONResult.FAIL);
            result.setMessage(message.getMessage("E0060","작업중 오류가 발생하였습니다."));
            return result.toString();
        }
        return result.toString();
    }

    @ResponseBody
    @RequestMapping(value = "permissionCheck.json", method = RequestMethod.POST)
    public String permissionCheck(HttpServletRequest request, HttpSession session, ModelMap model,@RequestParam("msgid")String msgid) throws Exception {
        UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);

        String userid = userSessionInfo.getUserid();
        String permission = userSessionInfo.getPermission();
        JSONResult result = new JSONResult();

        if(StringUtils.isEmpty(msgid)) {
            result.setResultCode(JSONResult.FAIL);
            result.setMessage(message.getMessage("E0060", "작업중 오류가 발생하였습니다."));
            return result.toString();
        }

        msgid = msgid.split(",")[0];

        EmsBean bean = mailService.noUseridGetMailData(msgid);

        /** 해당 페이지를 사용할 권한이 존재하는지 확인한다. */
        if (permission.equals(UserInfoBean.UTYPE_NORMAL)) { // 관리자인지 체크
            if (StringUtils.isNotEmpty(userid)) {
                if (!userid.equals(bean.getUserid())) {
                    log.error("SendResult permissionCheck.json - Authentication ERROR : {}", userid);
                    result.setResultCode(JSONResult.FAIL);
                    result.setMessage(message.getMessage("E0760", "발송 권한이 없습니다."));
                    return result.toString();
                }
            } else {
                log.error("SendResult permissionCheck.json - USERID NULL ERROR");
                result.setResultCode(JSONResult.FAIL);
                result.setMessage(message.getMessage("E0060", "작업중 오류가 발생하였습니다."));
                return result.toString();
            }
        }

        result.setResultCode(JSONResult.SUCCESS);
        return result.toString();
    }

}

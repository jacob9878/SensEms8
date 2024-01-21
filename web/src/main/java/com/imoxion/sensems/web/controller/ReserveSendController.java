package com.imoxion.sensems.web.controller;


import com.imoxion.common.util.ImStringUtil;
import com.imoxion.common.util.ImUtils;
import com.imoxion.sensems.common.logger.ErrorTraceLogger;
import com.imoxion.sensems.web.beans.ReceiverBean;
import com.imoxion.sensems.web.beans.ReserveSendBean;
import com.imoxion.sensems.web.beans.UserInfoBean;
import com.imoxion.sensems.web.common.ImPage;
import com.imoxion.sensems.web.database.domain.ImbCategoryInfo;
import com.imoxion.sensems.web.database.domain.ImbReceiver;
import com.imoxion.sensems.web.form.ActionLogForm;
import com.imoxion.sensems.web.form.ReserveSendForm;
import com.imoxion.sensems.web.form.ReserveSendListForm;
import com.imoxion.sensems.web.service.ActionLogService;
import com.imoxion.sensems.web.service.CategoryService;
import com.imoxion.sensems.web.service.ReceiverService;
import com.imoxion.sensems.web.service.ReserveSendService;
import com.imoxion.sensems.web.util.HttpRequestUtil;
import com.imoxion.sensems.web.util.JSONResult;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("send/reserve")
public class ReserveSendController {

    protected Logger log = LoggerFactory.getLogger(ReserveSendController.class);

    @Autowired
    private ReserveSendService reserveSendService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private MessageSourceAccessor message;

    @Autowired
    private ReceiverService receiverService;

    @Autowired
    private ActionLogService actionLogService;

    /**
     * 정기예약메일 발송 메일의 등록된 내용을 화면에 출력
     * @param request
     * @param response
     * @param session
     * @param model
     * @param msgid
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "view.do", method = RequestMethod.GET)
    public String reserveView(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model, @RequestParam(value = "msgid", required = false) String msgid) throws Exception {
        try {
            UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
            String userid = null;
            String permission = userSessionInfo.getPermission();
            ReserveSendBean reserveSendBean = reserveSendService.getReserveInfo(msgid);

            /** 해당 페이지를 사용할 권한이 존재하는지 확인한다. */
            if(permission.equals(UserInfoBean.UTYPE_NORMAL)){ // 관리자인지 체크
                if(StringUtils.isNotEmpty(userSessionInfo.getUserid())){
                    if(!userSessionInfo.getUserid().equals(reserveSendBean.getUserid())){ // userid가 다르다면 리스트 페이지로 이동한다.
                        log.error("Reserve view - Authentication Err : {}", userSessionInfo.getUserid());
                        return "redirect:/send/reserve/list.do";
                    }
                }else{
                    log.error("Reserve view - userid Null Err : {}", userSessionInfo.getUserid());
                    return "redirect:/send/reserve/list.do";
                }
            }

            String content = reserveSendService.getContent(msgid);
            ReserveSendForm reserveSendForm = reserveSendService.convertForm(reserveSendBean, content);

            model.addAttribute("reserveSendForm", reserveSendForm);

            //log insert start
            // TODO 로그정책 변경으로 삭제 (정기예약발송관리>조회 F501)

        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("Reserve VIEW ne ERROR : {}", errorId);
        }
        catch (Exception e) {
            String errorId = ErrorTraceLogger.log(e);
            log.error("Reserve VIEW ERROR : {}", errorId);
        }
        return "/reserve/reserve_view";
    }

    /**
     * 리스트에서 수정 페이지 출력한다.
     * @param request
     * @param response
     * @param session
     * @param model
     * @param msgid
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "modify.do", method = RequestMethod.GET)
    public String reserveModifyPage(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model, @RequestParam(value = "msgid", required = true) String msgid) throws Exception {
        try {
            UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
            String userid = null;
            String permission = userSessionInfo.getPermission();

            /** 수정할 메시지의 원문을 불러온다 */
            ReserveSendBean reserveSendBean = reserveSendService.getReserveInfo(msgid);
            String content = reserveSendService.getContent(msgid);

            /** 해당 페이지를 사용할 권한이 존재하는지 확인한다. */
            if(permission.equals(UserInfoBean.UTYPE_NORMAL)){ // 관리자인지 체크
                if(StringUtils.isNotEmpty(userSessionInfo.getUserid())){
                    if(!userSessionInfo.getUserid().equals(reserveSendBean.getUserid())){ // userid가 다르다면 리스트 페이지로 이동한다.
                        log.error("Reserve view - Authentication Err : {}", userSessionInfo.getUserid());
                        return "redirect:/send/reserve/list.do";
                    }
                }else{
                    log.error("Reserve modify - userid Null Err : {}", userSessionInfo.getUserid());
                    return "redirect:/send/reserve/list.do";
                }
            }

            /** 수정할 메시지의 원문을 Form 형태로 전환한다. */
            ReserveSendForm reserveSendForm = reserveSendService.convertForm(reserveSendBean, content);

            /** 시간 값들을 세팅 start */
            Map<String, String> hourList = new LinkedHashMap<>();
            for (int i = 0; i <= 23; i++) {
                String s = ImUtils.checkDigit(i);
                hourList.put(s, message.getMessage("E0543", new Object[]{s}, "{0}시"));
            }
            model.addAttribute("hourList", hourList);

            Map<String, String> minuteList = new LinkedHashMap<>();
            for (int i = 0; i <= 11; i++) {
                int j = i * 5; // 5분 간격 설정
                String s = ImUtils.checkDigit(j);
                minuteList.put(s, message.getMessage("E0544", new Object[]{s}, "{0}분"));
            }
            model.addAttribute("minuteList", minuteList);
            /** 시간 값들을 세팅 end */

            /** 발송분류 목록을 권한에 따라 처리  start */
            List<ImbCategoryInfo> categoryList = null;

            try {
                if (UserInfoBean.UTYPE_NORMAL.equals(permission)) {
                    userid = userSessionInfo.getUserid(); // 권한이 일반 사용자인 경우 userid를 기준으로 조회를 하므로 userid를 갱신
                    categoryList = categoryService.getUserCategory(userid);
                } else {// 권한이 관리자인 경우 userid = null
                    categoryList = categoryService.getUserCategory(userid);
                }
            }catch (NullPointerException ne) {
                String errorId = ErrorTraceLogger.log(ne);
                log.error("ReserveWrite - Category List ne Error : {}", errorId);
            }
            catch (Exception e) {
                String errorId = ErrorTraceLogger.log(e);
                log.error("ReserveWrite - Category List Error : {}", errorId);
            }
            /** 발송분류 목록을 권한에 따라 처리  end */

            /** 수신그룹 이름을 가져온다 **/
            ImbReceiver receiverBean = receiverService.getReceiverGroup(reserveSendBean.getRecid());
            String receiver_name = receiverBean.getRecv_name();

            model.addAttribute("reserveSendForm", reserveSendForm);
            model.addAttribute("recv_name", receiver_name);
            model.addAttribute("categoryList", categoryList);
            model.addAttribute("userInfo", userSessionInfo);

        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("ReserveWrite - Update Send Form Setting ne Error : {}", errorId);
        }
        catch (Exception e) {
            String errorId = ErrorTraceLogger.log(e);
            log.error("ReserveWrite - Update Send Form Setting Error : {}", errorId);
        }

        return "/reserve/reserve_write";
    }

    /**
     * 정기예약발송 리스트를 출력한다.
     * @param reserveSendListForm
     * @param session
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "list.do", method = RequestMethod.GET)
    public String reserveList(@ModelAttribute("reserveSendListForm") ReserveSendListForm reserveSendListForm, HttpSession session, ModelMap model) throws Exception {
        try {
            UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
            String permission = userSessionInfo.getPermission();
            String cpage = reserveSendListForm.getCpage();
            String userid = null;
            String pageGroupSize = reserveSendListForm.getPagegroupsize();

            List<ReserveSendListForm> reserveSendListFormList = null;
            ImPage pageInfo = null;
            int total = 0;

            // 일반 사용자와 관리자 권한 구분
            if (UserInfoBean.UTYPE_NORMAL.equals(permission)) {
                userid = userSessionInfo.getUserid();
                total = reserveSendService.reserveUserSendTotalCount(userid);
                pageInfo = new ImPage(ImStringUtil.parseInt(cpage), userSessionInfo.getPagesize(), total, ImStringUtil.parseInt(pageGroupSize));
                reserveSendListFormList = reserveSendService.getReserveSendList(pageInfo.getStart(), pageInfo.getEnd(), userid);
            } else {
                total = reserveSendService.reserveUserSendTotalCount(userid);
                pageInfo = new ImPage(ImStringUtil.parseInt(cpage), userSessionInfo.getPagesize(), total, ImStringUtil.parseInt(pageGroupSize));
                reserveSendListFormList = reserveSendService.getReserveSendList(pageInfo.getStart(), pageInfo.getEnd(), userid);
            }

            model.addAttribute("pageInfo", pageInfo);
            model.addAttribute("reserveList", reserveSendListFormList);

        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("getReserveSendList ne error : {}", errorId);
        }
        catch (Exception e) {
            String errorId = ErrorTraceLogger.log(e);
            log.error("getReserveSendList error : {}", errorId);
        }

        return "/reserve/reserve_list";

    }

    /**
     * 정기예약발송목록에서 삭제 요청을 처리한다.
     * @param session
     * @param ukeys
     * @param model
     * @return
     */
    @RequestMapping(value = "reserveDelete.json", method = RequestMethod.POST)
    @ResponseBody
    public String reserveDelte(HttpSession session, @RequestParam(value = "ukeys", required = false) String[] ukeys, ModelMap model, HttpServletRequest request) {

        JSONResult result = new JSONResult();
        try {
            if (ukeys == null || ukeys.length == 0) {
                result.setResultCode(JSONResult.FAIL);
                result.setMessage(message.getMessage("E0303", "삭제할 정기예약메일을 선택해주세요."));
                return result.toString();
            }
            UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);

            boolean checker = reserveSendService.deleteReserveList(ukeys, userSessionInfo); // 권한이 있는 사용자인지 아닌지를 체크한다.

            if(checker){
                result.setResultCode(JSONResult.SUCCESS);
                result.setMessage(message.getMessage("E0070", "삭제되었습니다."));
            }else{
                result.setResultCode(JSONResult.FAIL);
                result.setMessage(message.getMessage("E0060", "작업중 오류가 발생하였습니다."));
            }

            //log insert start
            ActionLogForm logForm = new ActionLogForm();
            logForm.setIp(HttpRequestUtil.getRemoteAddr(request));
            logForm.setUserid(userSessionInfo.getUserid());
            logForm.setMenu_key("F504");
            String param = "";
            for(int i=0; i<ukeys.length; i++){
                param += ukeys[i];
                if(i == ukeys.length-1) break;
                param += ", ";
            }
            logForm.setParam("삭제된 ukey : " + param);
            actionLogService.insertActionLog(logForm);

        } catch (NullPointerException ne) {
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
     * 정기예약 메일 등록 페이지를 출력한다.
     * @param request
     * @param response
     * @param reserveSendForm
     * @param session
     * @param model
     * @return
     */
    @RequestMapping(value = "write.do", method = RequestMethod.GET)
    public String reserveWrite(HttpServletRequest request, HttpServletResponse response, @ModelAttribute("reserveSendForm") ReserveSendForm reserveSendForm, HttpSession session, ModelMap model) {
        UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
        String userid = null;
        String permission = userSessionInfo.getPermission();

        Map<String, String> hourList = new LinkedHashMap<>();
        for (int i = 0; i <= 23; i++) {
            String s = ImUtils.checkDigit(i);
            hourList.put(s, message.getMessage("E0543", new Object[]{s}, "{0}시"));
        }
        model.addAttribute("hourList", hourList);

        Map<String, String> minuteList = new LinkedHashMap<>();
        for (int i = 0; i <= 11; i++) {
            int j = i * 5; // 5분 간격 설정
            String s = ImUtils.checkDigit(j);
            minuteList.put(s, message.getMessage("E0544", new Object[]{s}, "{0}분"));
        }
        model.addAttribute("minuteList", minuteList);

        /** 발송분류 목록을 권한에 따라 처리 */
        List<ImbCategoryInfo> categoryList = null;

        try {
            if (UserInfoBean.UTYPE_NORMAL.equals(permission)) { // 권한 체크
                userid = userSessionInfo.getUserid();// 일반 사용자의 경우 사용자에 해당되는 카테고리만 노출되어야하므로 userid를 get 처리
                categoryList = categoryService.getUserCategory(userid);
            } else {
                categoryList = categoryService.getUserCategory(userid);
            }
        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("ReserveWrite - Category List ne Error : {}", errorId);
        }
        catch (Exception e) {
            String errorId = ErrorTraceLogger.log(e);
            log.error("ReserveWrite - Category List Error : {}", errorId);
        }

        reserveSendForm = reserveSendService.getWriteForm(userSessionInfo);

        model.addAttribute("reserveSendForm", reserveSendForm);
        model.addAttribute("categoryList", categoryList);
        model.addAttribute("userInfo", userSessionInfo);

        return "/reserve/reserve_write";
    }

    /**
     * 정기예약 발송메일을 등록한다.
     * @param request
     * @param response
     * @param reserveSendForm
     * @param session
     * @param model
     * @return
     */
    @RequestMapping(value = "write.do", method = RequestMethod.POST)
    public String reserveRegist(HttpServletRequest request, HttpServletResponse response, @ModelAttribute("reserveSendForm") ReserveSendForm reserveSendForm, HttpSession session, ModelMap model) {
        try {
            UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
            reserveSendForm.setUserid(userSessionInfo.getUserid());
            reserveSendService.reserveRegist(reserveSendForm);

            //log insert start
            ActionLogForm logForm = new ActionLogForm();
            logForm.setIp(HttpRequestUtil.getRemoteAddr(request));
            logForm.setUserid(userSessionInfo.getUserid());
            logForm.setMenu_key("F502");
            logForm.setParam("mgsid : " + reserveSendForm.getMsgid() + " / 제목 : " + reserveSendForm.getMsg_name());
            actionLogService.insertActionLog(logForm);

        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("Reserve Send ne error : {}", errorId);
            return "redirect:/send/reserve/list.do";
        }
        catch (Exception e) {
            String errorId = ErrorTraceLogger.log(e);
            log.error("Reserve Send error : {}", errorId);
            return "redirect:/send/reserve/list.do";
        }

        return "redirect:/send/reserve/list.do";
    }

    /**
     * 정기예약 발송페이지의 수정을 수행한다.
     * @param request
     * @param response
     * @param session
     * @param model
     * @param reserveSendForm
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "modify.do", method = RequestMethod.POST)
    public String reserveModify(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model, @ModelAttribute("reserveSendForm") ReserveSendForm reserveSendForm) throws Exception {
        try {
            UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
            String permission = userSessionInfo.getPermission();

            /** 해당 페이지를 사용할 권한이 존재하는지 확인한다. */
            if(permission.equals(UserInfoBean.UTYPE_NORMAL)){ // 관리자인지 체크
                if(StringUtils.isNotEmpty(userSessionInfo.getUserid())){
                    if(!userSessionInfo.getUserid().equals(reserveSendForm.getUserid())){
                        log.error("Reserve view - Authentication ERROR : {}", userSessionInfo.getUserid());
                        return "redirect:/reserve/reserve_list";
                    }
                }else{
                    log.error("Reserve modify - userid Null ERROR : {}", userSessionInfo.getUserid());
                    return "redirect:/send/reserve/list.do";
                }
            }

            reserveSendForm.setUserid(userSessionInfo.getUserid());
            reserveSendService.reserveModify(reserveSendForm);

            //log insert start
            ActionLogForm logForm = new ActionLogForm();
            logForm.setIp(HttpRequestUtil.getRemoteAddr(request));
            logForm.setUserid(userSessionInfo.getUserid());
            logForm.setMenu_key("F503");
            logForm.setParam("mgsid : " + reserveSendForm.getMsgid() + " / 제목 : " + reserveSendForm.getMsg_name());
            actionLogService.insertActionLog(logForm);


        } catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("Reserve Send ne error : {}", errorId);
            return "redirect:/send/reserve/list.do";
        }
        catch (Exception e) {
            String errorId = ErrorTraceLogger.log(e);
            log.error("Reserve Send error : {}", errorId);
            return "redirect:/send/reserve/list.do";
        }

        return "redirect:/send/reserve/list.do";
    }


}

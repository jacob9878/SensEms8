package com.imoxion.sensems.web.controller;

import com.imoxion.common.util.ImStringUtil;
import com.imoxion.sensems.common.logger.ErrorTraceLogger;
import com.imoxion.sensems.web.beans.AttachBean;
import com.imoxion.sensems.web.beans.UserInfoBean;
import com.imoxion.sensems.web.common.CommonFile;
import com.imoxion.sensems.web.common.ImPage;
import com.imoxion.sensems.web.common.ImbConstant;
import com.imoxion.sensems.web.database.domain.ImbAttachRestrict;
import com.imoxion.sensems.web.form.ActionLogForm;
import com.imoxion.sensems.web.form.AttachListForm;
import com.imoxion.sensems.web.form.AttachRestrictForm;
import com.imoxion.sensems.web.service.ActionLogService;
import com.imoxion.sensems.web.service.AttachListService;
import com.imoxion.sensems.web.service.AttachRestrictService;
import com.imoxion.sensems.web.util.FileDownloadErrorUtil;
import com.imoxion.sensems.web.util.HttpRequestUtil;
import com.imoxion.sensems.web.util.JSONResult;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * yeji
 * 2021. 03. 10
 * 첨부파일 확장자 관리 Controller
 */
@Controller
@RequestMapping("sysman/attach")
public class AttachRestrictController {

    private final Logger log = LoggerFactory.getLogger(AttachRestrictController.class);

    @Autowired
    private AttachRestrictService attachRestrictService;

    @Autowired
    private AttachListService attachListService;

    @Autowired
    private MessageSourceAccessor message;

    @Autowired
    private ActionLogService actionLogService;


    /**
     * @method : 첨부파일  관리 화면 표시를 위한 Controller
     * @param form : AttachListForm 폼데이터
     * @param model
     * @return
     */

    @RequestMapping(value="list.do")
    public String attachListSettingForm(@ModelAttribute("attachlistForm")AttachListForm form, HttpSession session, ModelMap model,
                                        HttpServletResponse response) {

        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-control", "no-cache");

        String searchText = form.getSearchText();
        int listCount = -1;
        ImPage imPage = null;
        UserInfoBean userInfo = UserInfoBean.getUserSessionInfo(session);
        UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);

     //   String mhost = userInfo.getMhost();
       // String userid = userInfo.getUserid();

        try {
            listCount = attachListService.getAttachCount(searchText);
            // 1.첨부파일 목록을 불러온다.
            imPage = new ImPage(ImStringUtil.parseInt(form.getCurrentPage()), userSessionInfo.getPagesize(), listCount, 10);
            form.setFileList(attachListService.getAttachInfoList(searchText, imPage.getStart(), imPage.getEnd()));

        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("attachList Error :{}", errorId);
            model.addAttribute("errorMessage", ne.getMessage());
            return "/sysman/attach_list";
        }
        catch (Exception e) {
            String errorId = ErrorTraceLogger.log(e);
            log.error("attachList Error :{}", errorId);
            model.addAttribute("errorMessage", e.getMessage());
            return "/sysman/attach_list";
        }

        model.addAttribute("imPage", imPage);
        model.addAttribute("fileHistory", form);
        return "/sysman/attach_list";
    }

    /**
     * @method : 첨부파일 삭제 관련 컨트롤러
     * @param ekey : 삭제 첨부파일 데이터
     * @return
     */

    @RequestMapping(value="attachDel.do", method = RequestMethod.POST)
    @ResponseBody
    public String attachDel(@RequestParam(value = "ekey", required = false) String[] ekey, HttpServletRequest request, HttpSession session) throws Exception {
        UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
        JSONResult result = new JSONResult();
        try {
            attachListService.deleteAttachInfoByEkey(request,userSessionInfo.getUserid(),ekey);
            result.setResultCode(JSONResult.SUCCESS);
            result.setMessage(message.getMessage("O0342", "삭제가 완료 되었습니다."));
        }catch (NullPointerException ne) {
            result.setResultCode(JSONResult.FAIL);
            return result.toString();
        }
        catch (Exception e) {
            result.setResultCode(JSONResult.FAIL);
            return result.toString();
        }

        return result.toString();
    }


    /**
     * @method : 첨부파일 확장자 관리 화면 표시를 위한 Controller
     * @param form : attachRestrictForm 폼데이터
     * @param model
     * @return
     */
    @RequestMapping(value="restrict.do")
    public String attachRestrictSettingForm(@ModelAttribute("attachRestrictForm") AttachRestrictForm form, ModelMap model) throws Exception {

        List<ImbAttachRestrict> extList = null;
        String restrict_ext = "";

        try{
            //1.확장자 정보를 리스트에 담아 가져온다.
            extList = attachRestrictService.getExtInfo();

            //2.리스트에서 확장자 뽑아 배열에 담는다.
            if(extList != null && extList.size() > 0){ // null 체크
                String[] ext = new String[extList.size()];

                for (int i = 0; i < extList.size(); i++){
                    ext[i] = extList.get(i).getExt();
                    //log.info("ext[{}]-{}", i, ext[i]); // 확장자
                }

                //3.각각의 확장자를 ', ' 구분자로 -> 문자열 합치기(join)
                restrict_ext = StringUtils.join(ext, ", ");
                log.debug("restrict_ext result -{}", restrict_ext);
            }

            //4.form에 set
            form.setRestrict_ext(restrict_ext);
            //log.debug("form.getRestrict_ext - {}", form.getRestrict_ext());

        }catch (NullPointerException ne ) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("extList Error :{}", errorId);
        }
        catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("extList Error :{}", errorId);
        }

        model.addAttribute("attachRestrictForm", form);
        return "/sysman/attach_restrict";
    }



    /**
     * @comment : param 값 restrict_ext 공백 허용함으로 공백체크는 별도로 하지 않음.
     * @param restrict_ext
     * @param model
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value = "restrict.json", method = RequestMethod.POST)
    public String attachRestrictSave(@RequestParam(value="restrict_ext", required=false) String restrict_ext, ModelMap model, HttpSession session, HttpServletRequest request) throws Exception{
        //log.debug("param data(restrict_ext) is -{}", restrict_ext); // 파라미터값 확인

        JSONObject jsonResult = new JSONObject();
        List<String> extList = new ArrayList();

        try{
            if(StringUtils.isNotEmpty(restrict_ext)){ //restrict_ext 가 비어있지 않을때 tokenizer 수행
                //1.자르기
                StringTokenizer tokens = new StringTokenizer(restrict_ext, ", ");
                while (tokens.hasMoreTokens()){
                    String extToken= tokens.nextToken().trim().toLowerCase();
                    //log.info("ext token -{}",extToken);
                    if(!StringUtils.startsWith(extToken, ".")) { // 쩜(.) 으로 시작하지 않으면... 쩜(.)을 붙여준다
                        extToken = "." + extToken;
                    }
                    extList.add(extToken); // 리스트에 저장
                }

                //2.중복체크 : 리스트에 저장된 extToken 들을 비교(중복 입력된 확장자 확인하기 위해)
                for(int i = 0; i < extList.size(); i++){
                    for(int j = i+1; j < extList.size(); j++){
                        if(StringUtils.equalsIgnoreCase(extList.get(i), extList.get(j))){
                            log.debug("[{}]-{} , [{}]-{}", i, extList.get(i), j, extList.get(j));
                            jsonResult.put("result", false);
                            jsonResult.put("message", message.getMessage("E0229","중복된 확장자가 포함되어 있습니다."));
                            return jsonResult.toString();
                        }
                    }
                }

                //3.서버단 유효성체크(한글입력불가)
                String regex = ".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*";
                Pattern pattern = Pattern.compile(regex); // compile : 주어진 regex로 패턴을 만듦
                Matcher matcher = pattern.matcher(restrict_ext);
                boolean hangleCheck = matcher.matches(); //matches : 한글포함되어 있으면 true
                if(hangleCheck){
                    jsonResult.put("result", false);
                    jsonResult.put("message", message.getMessage("E0237","한글은 입력할 수 없습니다."));
                    return jsonResult.toString();
                }
            }
        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("attach restrict ext Error : {}", errorId);
            jsonResult.put("result", false);
            jsonResult.put("message", message.getMessage("E0060","작업중 오류가 발생하였습니다."));
            return jsonResult.toString();
        }
        catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("attach restrict ext Error : {}", errorId);
            jsonResult.put("result", false);
            jsonResult.put("message", message.getMessage("E0060","작업중 오류가 발생하였습니다."));
            return jsonResult.toString();
        }

        try{
            //4.초기화(delete) 후, 저장(insert) 진행
            ImbAttachRestrict imbAttachRestrict = new ImbAttachRestrict();

            //현재 ext 초기화
            attachRestrictService.deleteExtAll();
            log.info("restrict ext is all deleted");

            //ext 저장
            if(extList != null && extList.size() > 0){
                for(String ext : extList) {
                    imbAttachRestrict.setExt(ext);
                    attachRestrictService.insertExt(imbAttachRestrict);
                    //log.debug("imbAttachRestrict.getExt -{}", imbAttachRestrict.getExt());
                }

                //log insert start
                UserInfoBean userInfoBean = UserInfoBean.getUserSessionInfo(session);
                ActionLogForm logForm = new ActionLogForm();
                logForm.setIp(HttpRequestUtil.getRemoteAddr(request));
                logForm.setUserid(userInfoBean.getUserid());
                logForm.setMenu_key("G401");
                logForm.setParam("제한 확장자 : " + restrict_ext);
                actionLogService.insertActionLog(logForm);
            }
            log.info("restrict ext save success!");
        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("attach restrict ext [Save] ne Error : {}", errorId);
            jsonResult.put("result", false);
            jsonResult.put("message", message.getMessage("E0060","작업중 오류가 발생하였습니다."));
            return jsonResult.toString();
        }
        catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("attach restrict ext [Save] Error : {}", errorId);
            jsonResult.put("result", false);
            jsonResult.put("message", message.getMessage("E0060","작업중 오류가 발생하였습니다."));
            return jsonResult.toString();
        }

        jsonResult.put("result", true);
        jsonResult.put("message", message.getMessage("E0339","저장되었습니다."));
        return jsonResult.toString();
    }

    @RequestMapping(value="attachDownload.do", method = RequestMethod.GET)
    public ModelAndView attachDownload(@RequestParam(value = "ekey") String ekey, ModelMap model) throws Exception {

        try {
            if(StringUtils.isBlank(ekey)){
                String resultMsg = message.getMessage("E0489",  "필수값이 누락되었습니다.");
                return FileDownloadErrorUtil.getMessage(resultMsg);
            }
            AttachBean bean = attachListService.getFileInfo(ekey);
            if (bean == null) {
                String resultMsg = message.getMessage("E0430",  "파일이 존재하지 않습니다.");
                return FileDownloadErrorUtil.getMessage(resultMsg);
            }

            Date expire_date = bean.getExpire_date();
            Date now = new Date();

            if(now.after(expire_date)){
                String resultMsg = message.getMessage("E0525",  "만료일이 지난 파일입니다.");
                return FileDownloadErrorUtil.getMessage(resultMsg);
            }

            String filePath = ImbConstant.ATTACH_PATH + File.separator + bean.getFile_path();
            File file = new File(filePath);
            if (!file.exists()) {
                String resultMsg = message.getMessage("E0430",  "파일이 존재하지 않습니다.");
                return FileDownloadErrorUtil.getMessage(resultMsg);
            }

            return CommonFile.getDownloadView(file, bean.getFile_name());

        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("File Download Error : {}", errorId);
            return FileDownloadErrorUtil.getMessage(ne.getMessage());
        }
        catch (Exception e) {
            String errorId = ErrorTraceLogger.log(e);
            log.error("File Download Error : {}", errorId);
            return FileDownloadErrorUtil.getMessage(e.getMessage());

        }


    }


    /**
     * @method : 첨부파일 삭제 관련 컨트롤러
     * @param ekey : 첨부파일 데이터
     * @param date : 만료일
     * @return
     */

    @RequestMapping(value="expireDateUpdate.json", method = RequestMethod.POST)
    @ResponseBody
    public String expireDateUpdate(@RequestParam(value = "ekey", required = false) String ekey,
                                  @RequestParam(value = "date", required = false) String date) throws Exception {
        JSONResult result = new JSONResult();
        SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            String expire_date = date + " 23:59:59";
            Date expireDate = dateParser.parse(expire_date);

            attachListService.expireDateUpdate(ekey, expireDate);
            result.setResultCode(JSONResult.SUCCESS);
            result.setMessage(message.getMessage("E0729", "만료일이 수정 되었습니다."));
        }catch (NullPointerException ne) {
            result.setResultCode(JSONResult.FAIL);
            return result.toString();
        }
        catch (Exception e) {
            result.setResultCode(JSONResult.FAIL);
            return result.toString();
        }
        return result.toString();
    }

}

package com.imoxion.sensems.web.controller;

import com.imoxion.common.util.ImFileUtil;
import com.imoxion.common.util.ImStringUtil;
import com.imoxion.common.util.ImUtils;
import com.imoxion.sensems.common.logger.ErrorTraceLogger;
import com.imoxion.sensems.web.beans.UserInfoBean;
import com.imoxion.sensems.web.common.CommonFile;
import com.imoxion.sensems.web.common.ImPage;
import com.imoxion.sensems.web.common.ImbConstant;
import com.imoxion.sensems.web.database.domain.ImbTemplate;
import com.imoxion.sensems.web.form.ActionLogForm;
import com.imoxion.sensems.web.form.TemplateForm;
import com.imoxion.sensems.web.form.TemplateListForm;
import com.imoxion.sensems.web.service.ActionLogService;
import com.imoxion.sensems.web.service.ImageService;
import com.imoxion.sensems.web.service.TemplateService;
import com.imoxion.sensems.web.util.*;
import com.nhncorp.lucy.security.xss.XssPreventer;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 발송관리 > 템플릿 관리 컨트롤러
 * @date 2021.02.23
 * @author jhpark
 */
@Controller
@RequestMapping("send/template")
public class TemplateController {

    @Autowired
    private TemplateService templateService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private MessageSourceAccessor message;

    @Autowired
    private ActionLogService actionLogService;

    protected Logger log = LoggerFactory.getLogger(TemplateController.class);


    /**
     * 템플릿 검색 및 목록 획득
     * @param form
     * @param session
     * @param model
     * @return
     */
    @RequestMapping(value="list.do")
    public String templateList(@ModelAttribute("TemplateListForm") TemplateListForm form,HttpServletRequest request, HttpSession session, ModelMap model,
                               HttpServletResponse response){

        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-control", "no-cache");

        //페이지 목록 개수 세션에서 획득
        UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
        String userid = userSessionInfo.getUserid();
        String srch_type = form.getSrch_type();
        String srch_keyword = form.getSrch_keyword();

        int totalsize=0;
        int cpage = ImStringUtil.parseInt(form.getCpage());
        model.addAttribute("cpage", cpage);
        int pageGroupSize =  ImStringUtil.parseInt(form.getPagegroupsize());

        try{

            boolean issearch = true;
            if(StringUtils.isEmpty(srch_keyword)) {
                issearch = false;
            }
            model.addAttribute("issearch", issearch);


            totalsize = templateService.getTemplateCount(srch_type, srch_keyword,userid);
            //페이지 정보
            ImPage pageInfo = new ImPage(cpage, userSessionInfo.getPagesize(), totalsize, pageGroupSize);
            //템플릿정보 획득
            List<ImbTemplate> templateList = templateService.getTemplateListForPageing(srch_type, srch_keyword, userid, pageInfo.getStart(), pageInfo.getEnd());

            model.addAttribute("srch_type", srch_type);
            model.addAttribute("srch_key", srch_keyword);
            model.addAttribute("pageInfo", pageInfo);
            model.addAttribute("templateList", templateList);

            model.addAttribute("TemplateListForm",form);

            //log insert start
            // TODO 로그정책 변경으로 삭제 (템플릿관리>조회 F301)

        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("{} - Get Template List ne Error", errorId);
        }
        catch (Exception e) {
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - Get Template List Error", errorId);
        }
        return "/send/template_list";
    }

    /**
     * 템플릿 추가 페이지 이동
     * @param session
     * @param form
     * @param model
     * @return
     */
    @RequestMapping(value="add.do",method = RequestMethod.GET)
    public String templateAddForm (HttpSession session , HttpServletRequest request, @ModelAttribute("TemplateForm") TemplateForm form,
                                   @RequestParam(value = "srch_keyword",required = false)String srch_keyword,@RequestParam(value = "srch_type",required = false)String srch_type,
                                   ModelMap model){
        String cpage = request.getParameter("cpage");
        model.addAttribute("cpage", cpage);
        model.addAttribute("srch_keyword", srch_keyword);
        model.addAttribute("srch_type", srch_type);

        model.addAttribute("TemplateForm",form);

        return "/send/template_add";
    }

    /**
     * 템플릿 추가 실시
     * @param session
     * @param form
     * @param model
     * @return
     */
    @RequestMapping(value="add.do",method = RequestMethod.POST)
    public String templateAdd (HttpServletRequest request, HttpSession session , @ModelAttribute("TemplateForm") TemplateForm form,
                               ModelMap model){

        UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);

        String template_dir = ImbConstant.SENSDATA_PATH + File.separator + "template";
        String flag = form.getFlag();
        String temp_name = form.getTemp_name();
        String contents = form.getContent();
        //MultipartFile file_upload = form.getFile_upload();

        model.addAttribute("TemplateForm",form);

        try{
            //데이터 유효성 체크
            if(StringUtils.isEmpty(flag)){
                model.addAttribute("infoMessage",message.getMessage("E0115","분류를 선택해 주세요."));
                return "/send/template_add";
            }else if (StringUtils.isEmpty(temp_name)){
                model.addAttribute("infoMessage",message.getMessage("E0114","제목을 입력해 주세요."));
                return "/send/template_add";
            }else if(!ImUtility.validCharacter(temp_name)){
                model.addAttribute("infoMessage",message.getMessage("E0737","사용할 수 없는 특수문자가 포함되어 있습니다. (사용가능한 특수 문자 : (_) , (,) , (-) )"));
                return "/send/template_add";
            }

            ImbTemplate templateInfo = new ImbTemplate();

            // 저장될 고유 키 생성
            String ukey= ImUtils.makeKeyNum(24);
            String userid = userSessionInfo.getUserid();

            templateInfo.setUkey(ukey);
            templateInfo.setUserid(userid);
            templateInfo.setTemp_name(temp_name);
            templateInfo.setRegdate(new Date());
            templateInfo.setFlag(flag);


            /** 템플릿 본문 이미지 데이터 처리 */
            if(StringUtils.contains(contents,ImbTemplate.UPLOAD_IMAGE_VIEW_URL)){
                String checkContents = contents;

                int index = StringUtils.indexOf(checkContents,ImbTemplate.UPLOAD_IMAGE_VIEW_URL);
                int url_length = ImbTemplate.UPLOAD_IMAGE_VIEW_URL.length()+5;

                while(index>-1){
                    String key = StringUtils.substring(checkContents,index+url_length,index+url_length+24);

                    File contentsImageFile = CommonFile.getFile(ImbConstant.TEMPFILE_PATH, key);

                    if (!contentsImageFile.exists() || !contentsImageFile.isFile()) {
                        checkContents= StringUtils.substring(checkContents,index+url_length+24);
                        index = StringUtils.indexOf(checkContents,ImbTemplate.UPLOAD_IMAGE_VIEW_URL);
                        continue;
                    }
                    ImFileUtil.moveFile(contentsImageFile.getAbsolutePath(),ImbConstant.SENSDATA_PATH + File.separator + "template" + File.separator + key);

                    checkContents= StringUtils.substring(checkContents,index+url_length+24);
                    index = StringUtils.indexOf(checkContents,ImbTemplate.UPLOAD_IMAGE_VIEW_URL);
                }

                contents = contents.replaceAll(ImbTemplate.UPLOAD_IMAGE_VIEW_URL,ImbTemplate.CONTENTS_IMAGE_VIEW_URL);

            }

            templateInfo.setContents(contents);


            /** 템플릿 미리보기 이미지 데이터 처리
                미리보기 이미지 등록 삭제로 주석처리
             **/
            /*byte[] data;
            if(file_upload == null || file_upload.isEmpty()){
                templateInfo.setImage_path("");
            }else{
                templateInfo.setImage_path(File.separator + userid + "_" + ukey);
                //템플릿 미리보기 이미지 경로 획득
                FileOutputStream fos = null;
                try{
                    data = file_upload.getBytes();

                    //형식 체크 실시
                    File tempImageFile = new File(file_upload.getOriginalFilename());
                    file_upload.transferTo(tempImageFile);

                    boolean isImage = MimeDetectUtil.getInstance().isImage(tempImageFile);

                    if(!isImage){
                        model.addAttribute("infoMessage",message.getMessage("E0116","이미지 파일만 등록할 수 있습니다."));
                        return "/send/template_add";
                    }else {
                        String extension = MimeDetectUtil.getInstance().getFileExtForMimeType(tempImageFile);
                        extension = extension.toLowerCase();
                        if(!extension.equals(".jpg") && !extension.equals(".jpeg") && !extension.equals(".gif")){
                            model.addAttribute("infoMessage",message.getMessage("E0110","이미지파일은 JPG 또는 GIF 확장자만 등록가능합니다."));
                            return "/send/template_add";
                        }
                    }
                    tempImageFile.delete();
                    //실제 파일 서버에 업로드 실시
                    fos = new FileOutputStream(template_dir + File.separator + userid + "_" + ukey);
                    fos.write(data);
                }catch (IOException ie) {
                    String errorId = ErrorTraceLogger.log(ie);
                    log.error("{} - File MIMEType Check ie Error", errorId);
                }
                catch (Exception e){
                    String errorId = ErrorTraceLogger.log(e);
                    log.error("{} - File MIMEType Check Error", errorId);
                }finally {
                    try { if (fos != null) fos.close(); }catch (IOException ie) {} catch (Exception e) { }
                }
            }*/

            //템플릿 정보 추가
            templateService.insertTemplateInfo(templateInfo);

            //log insert start
            ActionLogForm logForm = new ActionLogForm();
            logForm.setIp(HttpRequestUtil.getRemoteAddr(request));
            logForm.setUserid(userSessionInfo.getUserid());
            logForm.setMenu_key("F302");
            logForm.setParam("공용여부(01:공용,02:개인) : " + flag + " / 템플릿명 : " + form.getTemp_name());
            actionLogService.insertActionLog(logForm);

        }catch (NullPointerException ne ) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("{} - Add Template ne Error", errorId);
        }
        catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - Add Template Error", errorId);
        }

        /** flag가 공용 이미지인 경우 현재탭 유지 시키기 */
        if(flag.equals("01")) {
            model.addAttribute("srch_type", flag);
        }

        return "redirect:/send/template/list.do";
    }

    /**
     * Template 수정 페이지로 이동
     * @param session
     * @param ukey
     * @param model
     * @return
     */
    @RequestMapping(value="edit.do",method = RequestMethod.GET)
    public String templateEditForm (HttpSession session , HttpServletRequest request, @RequestParam(value = "ukey",required = false)String ukey,
                                    @RequestParam(value = "srch_keyword",required = false)String srch_keyword,@RequestParam(value = "srch_type",required = false)String srch_type,
                                    ModelMap model){

        if(StringUtils.isEmpty(ukey)){
            return "redirect:/send/template/list.do";
        }

        try{
            ImbTemplate templateInfo = templateService.getTemplate(ukey);

            if(templateInfo == null){
                return "redirect:/send/template/list.do";
            }
            String cpage = request.getParameter("cpage");
            model.addAttribute("cpage", cpage);
            model.addAttribute("srch_keyword", srch_keyword);
            model.addAttribute("srch_type", srch_type);

            TemplateForm form = templateService.templateInfoToForm(templateInfo);
            model.addAttribute("TemplateForm",form);
        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("{} - Get Template Edit Page ne Error", errorId);
        }
        catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - Get Template Edit Page Error", errorId);
        }

        return "/send/template_edit";
    }

    /**
     * Template 정보 수정
     * @param session
     * @param form
     * @param model
     * @return
     */
    @RequestMapping(value="edit.do",method = RequestMethod.POST)
    public synchronized String templateEdit (HttpServletRequest request, HttpSession session , @ModelAttribute("TemplateForm") TemplateForm form , ModelMap model){

        UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
        String chk_pm = userSessionInfo.getPermission(); // 권한 확인 A:관리자, U:사용자
        String chk_id = userSessionInfo.getUserid(); // 로그인 유저 id 확인

        if(StringUtils.isEmpty(form.getUkey())){
            return "redirect:/send/template/list.do";
        }

        String template_dir = ImbConstant.SENSDATA_PATH + File.separator + "template";
        String flag = form.getFlag();
        String temp_name = form.getTemp_name();
        String contents = form.getContent();
        MultipartFile file_upload = form.getFile_upload();
        String ukey = form.getUkey();
        String[] isDeleteImage = form.getIsDeleteImage();


        model.addAttribute("TemplateForm",form);

        try{
            //유효성 체크
            if(StringUtils.isEmpty(flag)){
                model.addAttribute("infoMessage",message.getMessage("E0115","분류를 선택해 주세요."));
                return "/send/template_edit";
            }else if (StringUtils.isEmpty(temp_name)){
                model.addAttribute("infoMessage",message.getMessage("E0114","제목을 입력해 주세요."));
                return "/send/template_edit";
            }else if(!ImUtility.validCharacter(temp_name)){
                model.addAttribute("infoMessage",message.getMessage("E0737","사용할 수 없는 특수문자가 포함되어 있습니다. (사용가능한 특수 문자 : (_) , (,) , (-) )"));
                return "/send/template_add";
            }

            ImbTemplate templateInfo = templateService.getTemplate(ukey);


            if(templateInfo == null){
                model.addAttribute("infoMessage",message.getMessage("E0294","수정할 템플릿이 존재하지 않습니다."));
                return "/send/template_edit";
            }

            templateInfo.setUkey(ukey);
            templateInfo.setTemp_name(temp_name);

            templateInfo.setFlag(flag);

            /** 템플릿 원래 본문에 있던 이미지 삭제 건 처리  */
            String original_contents = templateInfo.getContents();
            //원래 본문에 contentsImage/view.do가 존재할 때 확인 실시
            if(StringUtils.contains(original_contents,ImbTemplate.CONTENTS_IMAGE_VIEW_URL)){
                //원래 본문에 존재하는 이미지 key List
                List<String> keyList = new ArrayList<String>();
                String original_checkContents = original_contents;
                int index = StringUtils.indexOf(original_checkContents,ImbTemplate.CONTENTS_IMAGE_VIEW_URL);
                int url_length = ImbTemplate.CONTENTS_IMAGE_VIEW_URL.length()+5;

                while (index > -1){
                    String key = StringUtils.substring(original_checkContents,index+url_length,index+url_length+24);
                    keyList.add(key);

                    original_checkContents= StringUtils.substring(original_checkContents,index+url_length+24);
                    index = StringUtils.indexOf(original_checkContents,ImbTemplate.CONTENTS_IMAGE_VIEW_URL);
                }

                // key List가 존재할 때 실시
                if(keyList.size() > 0) {
                    String template_path = ImbConstant.SENSDATA_PATH + File.separator + "template" + File.separator;
                    for(String file_key : keyList ){
                        //수정된 본문에 원래 있던 key가 없을 경우 이미지 파일 삭제 실시
                        if(!StringUtils.contains(contents, file_key)){
                            try{
                                ImFileUtil.deleteFile(template_path + file_key);
                            }catch (NullPointerException ne) {
                                log.debug("Template Contents Image Delete ne Error - key : {} ", file_key);
                                continue;
                            }
                            catch (Exception e){
                                log.debug("Template Contents Image Delete Error - key : {} ", file_key);
                                continue;
                            }
                        }
                    }
                }

            }

            /** 템플릿 본문 이미지 데이터 추가 건 처리 */
            if(StringUtils.contains(contents,ImbTemplate.UPLOAD_IMAGE_VIEW_URL)){
                String checkContents = contents;

                int index = StringUtils.indexOf(checkContents,ImbTemplate.UPLOAD_IMAGE_VIEW_URL);
                int url_length = ImbTemplate.UPLOAD_IMAGE_VIEW_URL.length()+5;

                while(index>-1){
                    String key = StringUtils.substring(checkContents,index+url_length,index+url_length+24);

                    File contentsImageFile = CommonFile.getFile(ImbConstant.TEMPFILE_PATH, key);

                    if (!contentsImageFile.exists() || !contentsImageFile.isFile()) {
                        checkContents= StringUtils.substring(checkContents,index+url_length+24);
                        index = StringUtils.indexOf(checkContents,ImbTemplate.UPLOAD_IMAGE_VIEW_URL);
                        continue;
                    }
                    ImFileUtil.moveFile(contentsImageFile.getAbsolutePath(),ImbConstant.SENSDATA_PATH + File.separator + "template" + File.separator + key);

                    checkContents= StringUtils.substring(checkContents,index+url_length+24);
                    index = StringUtils.indexOf(checkContents,ImbTemplate.UPLOAD_IMAGE_VIEW_URL);
                }

                contents = contents.replaceAll(ImbTemplate.UPLOAD_IMAGE_VIEW_URL,ImbTemplate.CONTENTS_IMAGE_VIEW_URL);

            }

            templateInfo.setContents(contents);

            /** 기존 미리보기 이미지 파일을 삭제할때만 동작 함. */
            if(isDeleteImage != null && isDeleteImage.length > 0){
                if(isDeleteImage[0].equals("1")){
                    templateService.deleteTemplateImage(templateInfo.getImage_path());
                    templateInfo.setImage_path("");
                }
            }

            /** 기존 미리보기 이미지 파일을 변경할 때 동작 */
            byte[] data;
            if(file_upload != null && !file_upload.isEmpty()){
                FileOutputStream fos = null;
                try{
                    data = file_upload.getBytes();

                    //형식 체크 실시
                    File tempImageFile = new File(file_upload.getOriginalFilename());
                    file_upload.transferTo(tempImageFile);

                    boolean isImage = MimeDetectUtil.getInstance().isImage(tempImageFile);

                    if(!isImage){
                        model.addAttribute("infoMessage",message.getMessage("E0116","이미지 파일만 등록할 수 있습니다."));
                        return "/send/template_edit";
                    }else {
                        String extension = MimeDetectUtil.getInstance().getFileExtForMimeType(tempImageFile);
                        extension = extension.toLowerCase();
                        if(!extension.equals(".jpg") && !extension.equals(".jpeg") && !extension.equals(".gif")){
                            model.addAttribute("infoMessage",message.getMessage("E0632","이미지파일은 JPG, GIF 확장자만 등록가능합니다."));
                            return "/send/template_edit";
                        }
                    }
                    tempImageFile.delete();

                    //기존 이미지 있을 경우 삭제 후 업로드 실시
                    if(StringUtils.isNotEmpty(templateInfo.getImage_path())){
                        templateService.deleteTemplateImage(templateInfo.getImage_path());
                    }else{ // 기존 이미지 없을 경우 Image_path 세팅
                        templateInfo.setImage_path(File.separator + templateInfo.getUserid() + "_" + ukey);
                    }

                    //서버에 새로운 이미지 업로드
                    fos = new FileOutputStream(template_dir + templateInfo.getImage_path());
                    fos.write(data);
                }catch (IOException ie) {
                    String errorId = ErrorTraceLogger.log(ie);
                    log.error("{} - File MIMEType Check ie Error", errorId);
                }
                catch (Exception e){
                    String errorId = ErrorTraceLogger.log(e);
                    log.error("{} - File MIMEType Check Error", errorId);
                }finally {
                    try { if (fos != null) fos.close(); }catch (IOException ie) {} catch (Exception e) { }
                }
            }

            //템플릿 정보 업데이트 실시
            templateService.updateTemplateInfo(templateInfo);

            //log insert start
            ActionLogForm logForm = new ActionLogForm();
            logForm.setIp(HttpRequestUtil.getRemoteAddr(request));
            logForm.setUserid(userSessionInfo.getUserid());
            logForm.setMenu_key("F303");
            if(StringUtils.equals(form.getOri_name(),form.getTemp_name())){
                logForm.setParam("공용여부(01:공용,02:개인) : " + flag + " / 템플릿명 : " + form.getTemp_name());
            }else{
                logForm.setParam("공용여부(01:공용,02:개인) : " + flag + " / 이전 템플릿명 : " + form.getOri_name() + " / 변경 템플릿명 : " + form.getTemp_name());
            }
            actionLogService.insertActionLog(logForm);

        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("{} - Edit Template ne Error", errorId);
        }
        catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - Edit Template Error", errorId);
        }

        /** flag가 공용 이미지인 경우 현재탭 유지 시키기 */
        if(flag.equals("01")) {
            model.addAttribute("srch_type", flag);
        }
        return "redirect:/send/template/list.do";
    }

    /**
     * 이미지 보여주기
     * @param session
     * @param request
     * @param ukey
     * @return
     */
    @RequestMapping(value = "view.do",method = RequestMethod.GET)
    public ModelAndView viewTemplateImage(HttpSession session, HttpServletRequest request, @RequestParam (value = "ukey", required = false) String ukey) {

        String sensdataPath = ImbConstant.SENSDATA_PATH;
        String imagePath = "";
        String filename = "";
        File imageFile = null;

        try{
            //유효성 체크 실시
            if(StringUtils.isEmpty(ukey)){
                return new ModelAndView("redirect:/error/no-resource");
            }
            ImbTemplate template = templateService.getTemplate(ukey);

            if(template != null){
                //이미지를 보는 것은 ukey를 알고 있다면 모든 사람들에게 허용
                imagePath = sensdataPath + File.separator + "template" + template.getImage_path();
                filename = template.getTemp_name();
            }else{
                return imageService.notFoundImage();
            }

            try {
                imageFile = new File(imagePath);
            }catch (NullPointerException ne) {
                String errorId = ErrorTraceLogger.log(ne);
                log.error("{} - Not Found Template Preview Image File ne ERROR", errorId);
                return imageService.notFoundImage();
            }
            catch (Exception e) {
                String errorId = ErrorTraceLogger.log(e);
                log.error("{} - Not Found Template Preview Image File ERROR", errorId);
                return imageService.notFoundImage();
            }

            //MIME TYPE 및 확장자 부여
            boolean isImage = MimeDetectUtil.getInstance().isImage(imageFile);
            if (!isImage) {
                return imageService.notFoundImage();
            }
            String extension = MimeDetectUtil.getInstance().getFileExtForMimeType(imageFile);
            filename = filename + extension;
        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("{} - Get Template  Preview Image View ne ERROR - ukey({})", errorId,ukey);
        }
        catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - Get Template  Preview Image View ERROR - ukey({})", errorId,ukey);
        }
        return CommonFile.getDownloadView(imageFile, filename);
    }


    /**
     * Template 목록 > 선택한 Template 삭제 동작
     * @param session
     * @param ukeys
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "deleteTemplates.json", method = RequestMethod.POST)
    @ResponseBody
    public String templateDelete(HttpServletRequest request, HttpSession session, @RequestParam(value = "ukeys", required = false) String[] ukeys, ModelMap model)  {
        UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
        JSONResult result = new JSONResult();
        try {
            if(ukeys == null || ukeys.length==0){
                result.setResultCode(JSONResult.FAIL);
                result.setMessage(message.getMessage("E0125","삭제할 템플릿을 선택해주세요."));
                return result.toString();
            }
            int fail_count = templateService.deleteTemplateList(request,userSessionInfo.getUserid(),ukeys);
            if(fail_count > 0){
                result.setResultCode(JSONResult.FAIL);
                result.setMessage(message.getMessage("E0168",new Object[]{ukeys.length,fail_count},"선택하신 {0}건 중 {1}건 삭제에 실패하였습니다."));
                return result.toString();
            }

        }catch (BadSqlGrammarException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("{} - Template Delete Bad Grammer ERROR", errorId);
            result.setResultCode(JSONResult.FAIL);
            result.setMessage(message.getMessage("E0060","작업중 오류가 발생하였습니다."));
            return result.toString();
        }
        catch (Exception e) {
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - Template Delete ERROR", errorId);
            result.setResultCode(JSONResult.FAIL);
            result.setMessage(message.getMessage("E0060","작업중 오류가 발생하였습니다."));
            return result.toString();
        }
        result.setResultCode(JSONResult.SUCCESS);
        result.setMessage(message.getMessage("E0070","삭제되었습니다."));
        return result.toString();
    }

    /**
     * Template 목록 > 선택한 Template 삭제 동작 (공용 템플릿)
     * @param session
     * @param ukeys
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "checkdeleteTemplates.json", method = RequestMethod.POST)
    @ResponseBody
    public String checktemplateDelete(HttpServletRequest request, HttpSession session, @RequestParam(value = "ukeys", required = false) String[] ukeys, ModelMap model)  {
        UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
        JSONResult result = new JSONResult();
        String chk_pm = userSessionInfo.getPermission();

        try {
            if(ukeys == null || ukeys.length==0){
                result.setResultCode(JSONResult.FAIL);
                result.setMessage(message.getMessage("E0125","삭제할 템플릿을 선택해주세요."));
                return result.toString();
            }

            // 체크한 템플릿 삭제를 위해 작성자 체크 및 권한 체크
            for (int i = 0; i < ukeys.length; i++) {
                ImbTemplate template= templateService.getTemplate(ukeys[i].split(",")[0]);
                if(!template.getUserid().equals(userSessionInfo.getUserid())){ // 로그인한 유저와 해당 템플릿 작성자의 아이디가 일치하는지 확인
                    if(chk_pm.equals("U")) { // 일치하지 않는다면 관리자 권한인지 확인
                        result.setResultCode(JSONResult.FAIL);
                        result.setMessage(message.getMessage("E0588","공용템플릿은 관리자 또는 작성자만 삭제할 수 있습니다."));
                        return result.toString();
                    }
                }
            }


            int fail_count = templateService.deleteTemplateList(request,userSessionInfo.getUserid(),ukeys);
            if(fail_count > 0){
                result.setResultCode(JSONResult.FAIL);
                result.setMessage(message.getMessage("E0168",new Object[]{ukeys.length,fail_count},"선택하신 {0}건 중 {1}건 삭제에 실패하였습니다."));
                return result.toString();
            }

        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("{} - Template Delete ne ERROR", errorId);
            result.setResultCode(JSONResult.FAIL);
            result.setMessage(message.getMessage("E0060","작업중 오류가 발생하였습니다."));
            return result.toString();
        }
        catch (Exception e) {
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - Template Delete ERROR", errorId);
            result.setResultCode(JSONResult.FAIL);
            result.setMessage(message.getMessage("E0060","작업중 오류가 발생하였습니다."));
            return result.toString();
        }
        result.setResultCode(JSONResult.SUCCESS);
        result.setMessage(message.getMessage("E0070","삭제되었습니다."));
        return result.toString();
    }

    @RequestMapping(value ="templateAdd.json", method = RequestMethod.POST)
    @ResponseBody
    public String templateJsonAdd(HttpServletRequest request, HttpSession session,
                                  @RequestParam(value="subject" , required = false) String temp_name,
                                  @RequestParam(value="flag" , required = false) String flag,
                                  @RequestParam(value="contents" , required = false) String contents, ModelMap model){
        JSONObject result = new JSONObject();

        try{
            UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);

            ImbTemplate templateInfo = new ImbTemplate();

            // 저장될 고유 키 생성
            String ukey= ImUtils.makeKeyNum(24);
            String userid = userSessionInfo.getUserid();

            templateInfo.setUkey(ukey);
            templateInfo.setUserid(userid);
            templateInfo.setTemp_name(temp_name);
            templateInfo.setRegdate(new Date());
            templateInfo.setContents(contents);
            templateInfo.setFlag(flag);

            templateService.insertTemplateInfo(templateInfo);

            //log insert start
            ActionLogForm logForm = new ActionLogForm();
            logForm.setIp(HttpRequestUtil.getRemoteAddr(request));
            logForm.setUserid(userSessionInfo.getUserid());
            logForm.setMenu_key("F302");
            logForm.setParam("공용여부(01:공용,02:개인) : " + flag + " / 템플릿명 : " + temp_name);
            actionLogService.insertActionLog(logForm);

            result.put("result", true);
            result.put("message",message.getMessage("E0061","추가되었습니다."));

        }catch (NullPointerException | BadSqlGrammarException ne) {
            result.put("result", false);
            result.put("message",message.getMessage("E0548","실패하였습니다."));
        }
        catch (Exception e){
            result.put("result", false);
            result.put("message",message.getMessage("E0548","실패하였습니다."));
        }
        return result.toString();
    }

    @RequestMapping(value = "uploadImage.do",method = RequestMethod.POST)
    public ModelAndView uploadImage(HttpSession session, MultipartHttpServletRequest request, HttpServletResponse response) {

        UserInfoBean userInfo = UserInfoBean.getUserSessionInfo(session);

        MultipartFile upload_file = request.getFile("upload");
        String weburl = HttpRequestUtil.getWebUrl(request);

        String type = request.getParameter("type") != null ? request.getParameter("type") : "";
        String CKEditorFuncNum = XssPreventer.escape(request.getParameter("CKEditorFuncNum"));

        String returnValue = null;
        File tempImageFile = null;
        FileOutputStream fos = null;

        try{
            byte[] data = upload_file.getBytes();
            tempImageFile = new File(upload_file.getOriginalFilename());
            upload_file.transferTo(tempImageFile);
            String extension = MimeDetectUtil.getInstance().getFileExtForMimeType(tempImageFile);
            tempImageFile.delete();

            String key = ImUtils.makeKeyNum(24);
            fos = new FileOutputStream(ImbConstant.TEMPFILE_PATH + File.separator + key);  //여기임
            fos.write(data);

            String img_url = weburl + "/send/template/uploadImage/view.do?key=" + key;

            if ("quick".equals(type)) {
                JSONObject ret = new JSONObject();
                ret.put("uploaded", 1);
                ret.put("fileName", key + extension);
                ret.put("url", img_url);
                returnValue = ret.toString();
            } else {
                returnValue = "try{window.parent.CKEDITOR.tools.callFunction(" + CKEditorFuncNum + ", '" + img_url + "');}catch(e){}";
            }
        }catch (IOException ne) {
            log.debug("CKEditor UploadImage MIME TYPE ne Exception - userid : {}", userInfo.getUserid());
            tempImageFile.delete();
        }
        catch (Exception e){
            log.debug("CKEditor UploadImage MIME TYPE Exception - userid : {}", userInfo.getUserid());
            tempImageFile.delete();
        }finally {
            try { if (fos != null) fos.close(); }catch (IOException ie) {} catch (Exception e) { }
        }
        log.debug("returnValue is {}", returnValue);
        if ("quick".equals(type)) return AlertMessageUtil.getMessage(returnValue);
        else return AlertMessageUtil.getMessageViewOfScript(returnValue);
    }

    @RequestMapping(value = "uploadImage/view.do",method = RequestMethod.GET)
    public ModelAndView uploadImageView(@RequestParam("key") String key) {

        File uploadImageFile = CommonFile.getFile(ImbConstant.TEMPFILE_PATH, key);

        if (!uploadImageFile.exists() || !uploadImageFile.isFile()) {
            return null;
        }

        boolean isImage = MimeDetectUtil.getInstance().isImage(uploadImageFile);
        if (!isImage) {
            return null;
        }
        String extension = MimeDetectUtil.getInstance().getFileExtForMimeType(uploadImageFile);
        extension = extension.toLowerCase();
        String filename = "uploadImage"+extension;
        return CommonFile.getDownloadView(uploadImageFile, filename);
    }

    @RequestMapping(value = "contentsImage/view.do", method = RequestMethod.GET)
    public ModelAndView contentsImageView(@RequestParam("key") String key) {

        File contentsImageFile = CommonFile.getFile(ImbConstant.SENSDATA_PATH + File.separator + "template", key);

        if (!contentsImageFile.exists() || !contentsImageFile.isFile()) {
            return null;
        }

        boolean isImage = MimeDetectUtil.getInstance().isImage(contentsImageFile);
        if (!isImage) {
            return null;
        }
        String extension = MimeDetectUtil.getInstance().getFileExtForMimeType(contentsImageFile);
        extension = extension.toLowerCase();
        String filename = "contentsImage"+extension;
        return CommonFile.getDownloadView(contentsImageFile, filename);
    }

    @RequestMapping(value = "checkPermission.json", method = RequestMethod.POST)
    @ResponseBody
    public String checkPermission(HttpSession session, @RequestParam(value = "ukey", required = false) String ukey){
        UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
        JSONResult result = new JSONResult();
        String chk_pm = userSessionInfo.getPermission(); // 권한 확인 A:관리자, U:사용자
        String chk_id = userSessionInfo.getUserid(); // 로그인 유저 id 확인

        try{
            // 수정 페이지 접근을 위해 작성자 체크 및 권한 체크
            ImbTemplate templateInfo = templateService.getTemplate(ukey);

            if(!templateInfo.getUserid().equals(chk_id)){ // 로그인한 유저와 해당 템플릿 작성자의 아이디가 일치하는지 확인
                if(chk_pm.equals("U")){ // 일치 하지않으면 로그인 유저의 권한 확인
                    result.setResultCode(JSONResult.FAIL);
                    result.setMessage(message.getMessage("E0589","공용템플릿은 관리자 또는 작성자만 수정할 수 있습니다."));
                    return result.toString();
                }
            }

        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("{} - Permission Check ne ERROR", errorId);
            result.setResultCode(JSONResult.FAIL);
            result.setMessage(message.getMessage("E0060","작업중 오류가 발생하였습니다."));
            return result.toString();
        }
        catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - Permission Check ERROR", errorId);
            result.setResultCode(JSONResult.FAIL);
            result.setMessage(message.getMessage("E0060","작업중 오류가 발생하였습니다."));
            return result.toString();
        }

        result.setResultCode(JSONResult.SUCCESS);
        return result.toString();
    }

    @RequestMapping(value = "preview.do", method = RequestMethod.GET)
    public String template_content(HttpServletRequest request, HttpSession session, @RequestParam("ukey") String ukey, ModelMap model) throws Exception {

        try {
            ImbTemplate template = templateService.getTemplate(ukey);

            String content = null;
            String subject = null;
            String image = null;
            if (template != null) {
                content = template.getContents();
                content = templateService.documentToContent(content);
                subject = template.getTemp_name();
                image = template.getImage_path();
            }
            model.addAttribute("content", content);
            model.addAttribute("subject", subject);
            model.addAttribute("image", image);

        }catch (NullPointerException ne) {
            model.addAttribute("message", message.getMessage("E0631","템플릿 정보를 찾지 못했습니다."));
            String errorId = ErrorTraceLogger.log(ne);
            log.error("{} - TEMPLATE CONTENT GET ne ERROR", errorId);
            return "/popup/send/popup_templatePreview";
        }
        catch (Exception e) {
            model.addAttribute("message", message.getMessage("E0631","템플릿 정보를 찾지 못했습니다."));
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - TEMPLATE CONTENT GET ERROR", errorId);
            return "/popup/send/popup_templatePreview";
        }

        return "/popup/send/popup_templatePreview";
    }

}

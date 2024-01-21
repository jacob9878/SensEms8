package com.imoxion.sensems.web.controller;

import com.imoxion.common.util.ImStringUtil;
import com.imoxion.common.util.ImUtils;
import com.imoxion.sensems.common.logger.ErrorTraceLogger;
import com.imoxion.sensems.web.beans.UserInfoBean;
import com.imoxion.sensems.web.common.CommonFile;
import com.imoxion.sensems.web.common.ImPage;
import com.imoxion.sensems.web.common.ImbConstant;
import com.imoxion.sensems.web.database.domain.ImbImage;
import com.imoxion.sensems.web.form.ActionLogForm;
import com.imoxion.sensems.web.form.ImageListForm;
import com.imoxion.sensems.web.service.ActionLogService;
import com.imoxion.sensems.web.service.ImageService;
import com.imoxion.sensems.web.util.*;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSON;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.File;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 발송관리 > 이미지 관리 컨트롤러
 * @date 2021.02.19
 * @author jhpark
 */
@Controller
@RequestMapping("/send/image/")
public class ImageController {


    @Autowired
    private ImageService imageService;

    @Autowired
    private MessageSourceAccessor message;

    @Autowired
    private ActionLogService actionLogService;

    protected Logger log = LoggerFactory.getLogger(ImageController.class);
    /**
     * 이미지 검색 및 목록 획득
     * @param form
     * @param session
     * @param model
     * @return
     */
    @RequestMapping(value="list.do")
    public String imageList(@ModelAttribute("ImageListForm") ImageListForm form, HttpServletRequest request, HttpSession session, ModelMap model,
                            HttpServletResponse response){

        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-control", "no-cache");

        //페이지 목록 개수 세션에서 획득
        UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
        String userid = userSessionInfo.getUserid();
        String srch_type = form.getSrch_type();
        String srch_keyword = form.getSrch_keyword();
        int cpage = ImStringUtil.parseInt(form.getCpage());
        int pageGroupSize =  ImStringUtil.parseInt(form.getPagegroupsize());
        int totalsize=0;

        try{

            boolean issearch = true;
            if(StringUtils.isEmpty(srch_keyword)) {
                issearch = false;
            }
            model.addAttribute("issearch", issearch);

            totalsize = imageService.getImageCount(srch_type, srch_keyword,userid);
            //페이지 정보
            ImPage pageInfo = new ImPage(cpage, userSessionInfo.getPagesize(), totalsize, pageGroupSize);
            //이미지정보 획득
            List<ImbImage> imageList = imageService.getImageListForPageing(srch_type, srch_keyword, userid, pageInfo.getStart(), pageInfo.getEnd());

            model.addAttribute("pageInfo", pageInfo);
            model.addAttribute("imageList", imageList);

            model.addAttribute("ImageListForm",form);

            //log insert start
            // TODO 로그정책 변경으로 삭제 (이미지관리>조회 F201)

        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("{} - Get Image List Error nullpoint", errorId);
        }

        catch (Exception e) {
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - Get Image List Error", errorId);
        }
        return "/send/image_list";
    }

    /**
     * 이미지 추가
     * @param session
     * @param request
     * @param flag
     * @param image_name
     * @param model
     * @return
     */
    @RequestMapping(value = "add.json", method = RequestMethod.POST)
    @ResponseBody
    public synchronized String imageAdd(HttpSession session, MultipartHttpServletRequest request,
                           @RequestParam(value = "flag",required = false) String flag,
                           @RequestParam(value = "image_name",required = false) String image_name,
                           ModelMap model){

        UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
        JSONResult result = new JSONResult();

        try{
            // 유효성 체크 실시
            MultipartFile file = request.getFile("file");
            if(file == null || file.isEmpty()) {
                result.setResultCode(JSONResult.FAIL);
                result.setMessage(message.getMessage("E0113", "파일을 선택해 주세요."));
                return result.toString();
            }else if(StringUtils.isEmpty(image_name)){
                result.setResultCode(JSONResult.FAIL);
                result.setMessage(message.getMessage("E0114", "제목을 입력해 주세요."));
                return result.toString();
            }else if (StringUtils.isEmpty(flag)){
                result.setResultCode(JSONResult.FAIL);
                result.setMessage(message.getMessage("E0115", "분류를 선택해 주세요."));
                return result.toString();
            }else if(!ImUtility.validCharacter(image_name)){
                result.setResultCode(JSONResult.FAIL);
                result.setMessage(message.getMessage("E0737", "사용할 수 없는 특수문자가 포함되어 있습니다. (사용가능한 특수 문자 : (_) , (,) , (-) )"));
                return result.toString();
            }

            //파일을 저장할 경로 체크 및 디렉토리 없을 경우 생성
            String sensdataPath = ImbConstant.SENSDATA_PATH;
            Date today = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("/yyyy/MM/dd");
            String image_dir = simpleDateFormat.format(today);
            String sensImagePath = sensdataPath + File.separator + "user_data" + File.separator + "images" + image_dir;

            File tempDir  = new File(sensImagePath);
            if(!tempDir.exists()){
                tempDir.mkdirs();
            }
            //파일 데이터 획득
            byte[] data = file.getBytes();
            BufferedImage image = ImageIO.read(file.getInputStream());

            //업로드 파일 MIME TYPE 및 확장자 체크
            //임시파일 생성
            File tempImageFile=null;
                //MultipartFile을 File 형식으로 변환
                String name = ImEncUtil.getInstance().replaceAll(file.getOriginalFilename());
                tempImageFile = new File(name);
                file.transferTo(tempImageFile);

                boolean isImage = MimeDetectUtil.getInstance().isImage(tempImageFile);
                if(!isImage){ // 이미지 파일인지 체크
                    result.setResultCode(JSONResult.FAIL);
                    result.setMessage(message.getMessage("E0116", "이미지 파일만 등록할 수 있습니다."));
                    return result.toString();
                }else { // 이미지 파일일 경우 확장자 체크
                    String extension = MimeDetectUtil.getInstance().getFileExtForMimeType(tempImageFile);
                    extension = extension.toLowerCase();
                    if(!extension.equals(".jpg") && !extension.equals(".jpeg") && !extension.equals(".gif") && !extension.equals(".png") && !extension.equals(".bmp")){
                        result.setResultCode(JSONResult.FAIL);
                        result.setMessage(message.getMessage("E0110", "이미지파일은 JPG, GIF, PNG, BMP 확장자만 등록가능합니다."));
                        return result.toString();
                    }
                }
                //임시파일 삭제
                tempImageFile.delete();
            // 저장될 고유 키 생성
            String ukey= ImUtils.makeKeyNum(24);
            String userid = userSessionInfo.getUserid();

            //실제 파일 서버에 업로드 실시
            FileOutputStream fos = new FileOutputStream(sensImagePath + File.separator + ukey);
            fos.write(data);
            fos.close();

            //데이터베이스에 해당 이미지 정보 저장
            ImbImage imageInfo = new ImbImage();
            imageInfo.setUkey(ukey);
            imageInfo.setUserid(userid);
            imageInfo.setImage_name(image_name);
            imageInfo.setImage_path(image_dir + File.separator + ukey);
            imageInfo.setRegdate(new Date());
            imageInfo.setFlag(flag);

            //이미지 가로 세로 길이 구하기
            if(image != null){
                imageInfo.setImage_width(image.getWidth());
                imageInfo.setImage_height(image.getHeight());
            }else{
                imageInfo.setImage_width(0);
                imageInfo.setImage_height(0);
            }

            imageService.insertImageInfo(imageInfo);

            //log insert start
            ActionLogForm logForm = new ActionLogForm();
            logForm.setIp(HttpRequestUtil.getRemoteAddr(request));
            logForm.setUserid(userSessionInfo.getUserid());
            logForm.setMenu_key("F202");
            logForm.setParam("공용여부(01:공용,02:개인) : " + flag + " / 이미지명 : " + image_name);
            actionLogService.insertActionLog(logForm);

        }
        catch (FileNotFoundException fe){
            String errorId = ErrorTraceLogger.log(fe);
            log.error("{} - File MIMEType Check Error fileNotFoundException", errorId);
        }
        catch (IOException Ie){
            String errorId = ErrorTraceLogger.log(Ie);
            log.error("{} - File MIMEType Check Error IOException", errorId);
        }
        catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("{} - File MIMEType Check Error nullpoint", errorId);
            result.setResultCode(JSONResult.FAIL);
            result.setMessage(message.getMessage("E0117", "MIME TYPE 체크 중 오류가 발생하였습니다."));
            return result.toString();
        }
        catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - File MIMEType Check Error", errorId);
            result.setResultCode(JSONResult.FAIL);
            result.setMessage(message.getMessage("E0117", "MIME TYPE 체크 중 오류가 발생하였습니다."));
            return result.toString();
        }
        result.setResultCode(JSONResult.SUCCESS);
        result.setMessage(message.getMessage("E0061", "추가 되었습니다."));

        return result.toString();
    }

    /**
     * 이미지 목록 > 선택한 이미지 삭제 동작
     * @param session
     * @param ukeys
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "deleteImages.json", method = RequestMethod.POST)
    @ResponseBody
    public String imageDelete(HttpServletRequest request, HttpSession session, @RequestParam(value = "ukeys", required = false) String[] ukeys, ModelMap model)  {
        UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);

        JSONResult result = new JSONResult();
        try {
            if(ukeys == null || ukeys.length==0){
                result.setResultCode(JSONResult.FAIL);
                result.setMessage(message.getMessage("E0124","삭제할 이미지를 선택해주세요."));
                return result.toString();
            }



            int fail_count = imageService.deleteImageList(request,userSessionInfo.getUserid(),ukeys);

            if(fail_count > 0){
                result.setResultCode(JSONResult.FAIL);
                result.setMessage(message.getMessage("E0168",new Object[]{ukeys.length,fail_count},"선택하신 {0}건 중 {1}건 삭제에 실패하였습니다."));
                return result.toString();
            }

        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("{} - Image Delete ERROR np", errorId);
            result.setResultCode(JSONResult.FAIL);
            result.setMessage(message.getMessage("E0060","작업중 오류가 발생하였습니다."));
            return result.toString();
        }

        catch (Exception e) {
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - Image Delete ERROR", errorId);
            result.setResultCode(JSONResult.FAIL);
            result.setMessage(message.getMessage("E0060","작업중 오류가 발생하였습니다."));
            return result.toString();
        }
        result.setResultCode(JSONResult.SUCCESS);
        result.setMessage(message.getMessage("E0070","삭제되었습니다."));
        return result.toString();
    }


    /**
     * 이미지 목록 > 선택한 이미지 삭제 동작(공용이미지)
     * @param session
     * @param ukeys
     * @param model
     * @return
     * @throws Exception
     */

    @RequestMapping(value = "checkdeleteImages.json", method = RequestMethod.POST)
    @ResponseBody
    public String checkimageDelete(HttpServletRequest request, HttpSession session, @RequestParam(value = "ukeys", required = false) String[] ukeys, ModelMap model)  {
        UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);

        JSONResult result = new JSONResult();
        String chk_pm = userSessionInfo.getPermission();
        try {
            if(ukeys == null || ukeys.length==0){
                result.setResultCode(JSONResult.FAIL);
                result.setMessage(message.getMessage("E0124","삭제할 이미지를 선택해주세요."));
                return result.toString();
            }

            // 체크한 이미지 삭제를 위해 작성자 체크 및 권한 체크
            for (int i = 0; i < ukeys.length; i++) {
                ImbImage imbImage = imageService.getImage(ukeys[i].split(",")[0]);
                if(!imbImage.getUserid().equals(userSessionInfo.getUserid())){ // 로그인한 유저와 해당 이미지 작성자의 아이디가 일치하는지 확인
                    if(chk_pm.equals("U")) { // 일치하지 않는다면 관리자 권한인지 확인
                        result.setResultCode(JSONResult.FAIL);
                        result.setMessage(message.getMessage("E0587","공용이미지는 관리자 또는 작성자만 삭제할 수 있습니다."));
                        return result.toString();
                    }
                }
            }


            int fail_count = imageService.deleteImageList(request,userSessionInfo.getUserid(),ukeys);

            if(fail_count > 0){
                result.setResultCode(JSONResult.FAIL);
                result.setMessage(message.getMessage("E0168",new Object[]{ukeys.length,fail_count},"선택하신 {0}건 중 {1}건 삭제에 실패하였습니다."));
                return result.toString();
            }

        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("{} - Image Delete ERROR", errorId);
            result.setResultCode(JSONResult.FAIL);
            result.setMessage(message.getMessage("E0060","작업중 오류가 발생하였습니다."));
            return result.toString();
        }
        catch (Exception e) {
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - Image Delete ERROR", errorId);
            result.setResultCode(JSONResult.FAIL);
            result.setMessage(message.getMessage("E0060","작업중 오류가 발생하였습니다."));
            return result.toString();
        }



        result.setResultCode(JSONResult.SUCCESS);
        result.setMessage(message.getMessage("E0070","삭제되었습니다."));
        return result.toString();
    }

    /**
     * 이미지 보여주기
     * @param session
     * @param request
     * @param ukey
     * @return
     */
    @RequestMapping(value = "view.do",method = RequestMethod.GET)
    public ModelAndView viewImage(HttpSession session, HttpServletRequest request, @RequestParam(value = "ukey",required = false) String ukey) {

        String sensdataPath = ImbConstant.SENSDATA_PATH;
        String imagePath = "";
        String filename = "";
        File imageFile = null;

        try{
            //유효성 체크 실시
            if(StringUtils.isEmpty(ukey)){
                return new ModelAndView("redirect:/error/no-resource.do");
            }
            ImbImage image = imageService.getImage(ukey);

            if(image != null){
                //이미지를 보는 것은 ukey를 알고 있다면 모든 사람들에게 허용
                imagePath = sensdataPath + File.separator + "user_data" + File.separator + "images" + image.getImage_path();
                filename = image.getImage_name();
            }else{
                imageService.notFoundImage();
            }
            try {
                imageFile = new File(imagePath);
            }catch (NullPointerException ne) {
                String errorId = ErrorTraceLogger.log(ne);
                log.error("{} - Not Found Image File ERROR", errorId);
                return imageService.notFoundImage();
            }
            catch (Exception e) {
                String errorId = ErrorTraceLogger.log(e);
                log.error("{} - Not Found Image File ERROR", errorId);
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
            log.error("{} - Get Image View null ERROR - ukey({})", errorId,ukey);
        }
        catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - Get Image View ERROR - ukey({})", errorId,ukey);
        }
        return CommonFile.getDownloadView(imageFile, filename);
    }

    /**
     * 미리보기 페이지로 이동
     * @param session
     * @param request
     * @param model
     * @param ukey
     * @return
     */
    @RequestMapping(value = "preview.do",method = RequestMethod.GET)
    public String previewImage(HttpSession session, HttpServletRequest request,ModelMap model,
                               @RequestParam(value = "ukey",required = false) String ukey,
                               @RequestParam(value = "type",required = false) String type) {

        if(StringUtils.isEmpty(ukey) || StringUtils.isEmpty(type)){
            return "redirect:/error/no-resource.do";
        }
        model.addAttribute("ukey",ukey);
        model.addAttribute("type",type);
        return "/jsp/send/image_preview";
    }
}

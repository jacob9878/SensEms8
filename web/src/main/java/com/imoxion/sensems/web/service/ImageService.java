package com.imoxion.sensems.web.service;

import com.imoxion.common.util.ImStringUtil;
import com.imoxion.sensems.common.logger.ErrorTraceLogger;
import com.imoxion.sensems.web.common.CommonFile;
import com.imoxion.sensems.web.common.ImPage;
import com.imoxion.sensems.web.common.ImbConstant;
import com.imoxion.sensems.web.database.domain.ImbImage;
import com.imoxion.sensems.web.database.mapper.ImageMapper;
import com.imoxion.sensems.web.form.ActionLogForm;
import com.imoxion.sensems.web.util.HttpRequestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 이미지 관리 Service
 * @date 2021.02.19
 * @author jhpark
 *
 */
@Service
public class ImageService {

    protected Logger log = LoggerFactory.getLogger(ImageService.class);

    @Autowired
    private ImageMapper imageMapper;

    @Autowired
    private ActionLogService actionLogService;

    /**
     * 검색에 따른 결과 총 개수 획득
     * @param srch_type
     * @param srch_keyword
     * @param userid
     * @return
     */
    public int getImageCount(String srch_type, String srch_keyword, String userid) throws Exception {
        return imageMapper.getImageCount(srch_type,srch_keyword,userid);
    }

    /**
     * 검색에 따른 Image 목록 정보 획득
     * @param srch_type
     * @param srch_keyword
     * @param userid
     * @param start
     * @param end
     * @return
     */
    public List<ImbImage> getImageListForPageing(String srch_type, String srch_keyword, String userid, int start, int end) throws Exception{
        return imageMapper.getImageListForPageing(srch_type,srch_keyword,userid,start,end);
    }

    /**
     * ukey를 이용하여 Image 정보 획득
     * @param ukey
     * @return
     */
    public ImbImage getImage(String ukey) throws Exception {
        return imageMapper.getImage(ukey);
    }

    /**
     * 이미지 파일을 찾을 수 없는 경우 나올 이미지 경로
     * @return
     */
    public String getNotFoundImagePath() {
        String sensdataPath = ImbConstant.SENSDATA_PATH;
        return sensdataPath + File.separator + "images" + File.separator + "notfound_image.jpg";
    }

    /**
     * NOTFOUND_IMAGE 다운로드 실시
     * @return
     */
    public ModelAndView notFoundImage() throws Exception {
        FileInputStream is = null;
        try {
            is = new FileInputStream(new File(getNotFoundImagePath()));
        } catch (FileNotFoundException e) {
            log.error("Error is {}", e);
        }
        return CommonFile.getDownloadView(is, "notfound_image.jpg");
    }

    /**
     * 선택한 IMAGE 삭제
     * 동기화를 넣은 이유 : 시큐어 코딩에서 TOCTOU경쟁조건 취약점이 발생하여, 동기화 구문을 사용하여 하나의 스레드만 접근가능하도록 조치
     * @param request
     * @param userid
     * @param ukeys
     */
    public synchronized int deleteImageList(HttpServletRequest request, String userid, String[] ukeys) throws Exception{
        String sensdataPath = ImbConstant.SENSDATA_PATH;
        String dir_path = sensdataPath + File.separator + "user_data" + File.separator + "images";
        int fail_count = 0;
        String logParam = "";
        for(int i=0;i<ukeys.length;i++){

            ImbImage image = getImage(ukeys[i].split(",")[0]);
            if(image == null){
                continue;
            }

            try {
                //실제 파일 삭제 및 DB에서 정보 삭제
                File delFile = new File(dir_path + image.getImage_path());
                if (delFile.exists() && delFile.isFile()) {
                    boolean deleteOK = delFile.delete();
                    if(deleteOK){
                        //파일이 존재하여 삭제 했을 경우
                        log.debug("Is Delete OK - ukey : {}",ukeys[i].split(",")[0]);
                    }else {
                        //파일이 존재하지만 삭제에 실패했을 경우
                        log.debug("Image File Delete False - ukey : {}",ukeys[i].split(",")[0]);
                        fail_count++;
                        continue;
                    }
                }else{
                    //파일이 존재하지 않을 경우
                    log.debug("File is Not Exist - ukey : {}",ukeys[i].split(",")[0]);
                }
            }catch ( NullPointerException ne) {
                String errorid = ErrorTraceLogger.log(ne);
                log.error("delFile ne Error : {}", errorid);
            }
            catch (Exception e){
                String errorid = ErrorTraceLogger.log(e);
                log.error("delFile Error : {}", errorid);
            }

            imageMapper.deleteImage(ukeys[i].split(",")[0]);
            if(i==0){
                logParam= "삭제한 이미지명 : " + ukeys[i].split(",")[1] + " / 삭제한 이미지 key : " + ukeys[i].split(",")[0];
            }else {
                logParam += " , 삭제한 이미지명 : " + ukeys[i].split(",")[1] + " / 삭제한 이미지 key : " + ukeys[i].split(",")[0];
            }

        }
        //log insert start
        ActionLogForm logForm = new ActionLogForm();
        logForm.setIp(HttpRequestUtil.getRemoteAddr(request));
        logForm.setUserid(userid);
        logForm.setMenu_key("F203");
        logForm.setParam(logParam);
        actionLogService.insertActionLog(logForm);
        return fail_count;
    }

    /**
     * Image 정보를 추가한다.
     * @param imageInfo
     * @throws Exception
     */
    public void insertImageInfo(ImbImage imageInfo) throws Exception {
        imageMapper.insertImage(imageInfo);
    }

    /**
     * @Method Name : getTemplateList
     * @Method Comment : 템플릿 목록 호출(페이징)
     *
     * @param userid
     * @return
     * @throws Exception
     */
    public Map<String, Object> getTemplateList(String userid, String category_key, String currentPage, String pageSize) throws Exception {
        String search_text = null;

        //템플릿 개수 취득
        int totalCount = imageMapper.getImageCount(category_key, search_text, userid);

        //페이지 정보 설정
        int cpage = ImStringUtil.parseInt(currentPage);
        int tSize = ImStringUtil.parseInt(pageSize);
        ImPage imPage = new ImPage(cpage, tSize, totalCount, 2);

        //템플릿 목록취득
        List<ImbImage> templateList = imageMapper.getImageListForPageing(category_key,search_text,userid,imPage.getStart(),imPage.getEnd());

        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("TEMPLATE_LIST", templateList);
        resultMap.put("IMPAGE", imPage);

        return resultMap;
    }

    public Map<String, Object> getImageList(String userid, String category_key, String currentPage, String pageSize) throws Exception{
        String search_text = null;

        //템플릿 개수 취득
        int totalCount = imageMapper.getImageCount(category_key, search_text, userid);

        //페이지 정보 설정
        int cpage = ImStringUtil.parseInt(currentPage);
        int tSize = ImStringUtil.parseInt(pageSize);
        ImPage imPage = new ImPage(cpage, tSize, totalCount, 2);

        //템플릿 목록취득
        List<ImbImage> templateList = imageMapper.getImageListForPageing(category_key,search_text,userid,imPage.getStart(),imPage.getEnd());

        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("IMAGE_LIST", templateList);
        resultMap.put("IMPAGE", imPage);

        return resultMap;
    }

    public synchronized int deleteImageUkey(HttpServletRequest request, String userids) throws Exception{

        String sensdataPath = ImbConstant.SENSDATA_PATH;
        String dir_path = sensdataPath + File.separator + "user_data" + File.separator + "images";
        int fail_count = 0;
        String logParam = "";

        List<ImbImage> images = imageMapper.deleteImageList(userids);
        int size = images.size();

        for(int i=0; i < size; i++){
            try{
                //실제 파일 삭제 및 DB에서 정보 삭제
                File delFile = new File(dir_path + images.get(i).getImage_path());
                if(delFile.exists() && delFile.isFile()){
                    boolean deleteOK = delFile.delete();
                    if(deleteOK){
                        //파일이 존재하여 삭제 했을 경우
                        log.debug("Image Delete OK - ukey : {}",images.get(i).getUkey());
                    }else{
                        //파일이 존재하지만 삭제에 실패했을 경우
                        log.debug("Image File Delete False - ukey : {}",images.get(i).getUkey());
                        fail_count++;
                        continue;
                    }
                }else{
                    //파일이 존재하지 않을 경우
                    log.debug("File is Not Exist - ukey : {}",images.get(i).getUkey());
                }
            }catch (NullPointerException ne){
                String errorid = ErrorTraceLogger.log(ne);
                log.error("delImageFile ne Error : {}", errorid);
            }catch (Exception e){
                String errorid = ErrorTraceLogger.log(e);
                log.error("delImageFile  Error : {}", errorid);
            }

            imageMapper.deleteImage(images.get(i).getUkey());

            if(i==0){
                logParam= "삭제한 이미지명 : " + images.get(i).getImage_name() + " / 삭제한 이미지 key : " + images.get(i).getUkey();
            }else {
                logParam += " , 삭제한 이미지명 : " + images.get(i).getImage_name() + " / 삭제한 이미지 key : " + images.get(i).getUkey();
            }
        }

        //log insert start
        ActionLogForm logForm = new ActionLogForm();
        logForm.setIp(HttpRequestUtil.getRemoteAddr(request));
        logForm.setUserid(userids);
        logForm.setMenu_key("F203");
        logForm.setParam(logParam);
        actionLogService.insertActionLog(logForm);
        return fail_count;
    }
}

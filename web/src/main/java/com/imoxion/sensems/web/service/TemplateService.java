package com.imoxion.sensems.web.service;


import com.imoxion.common.util.ImStringUtil;
import com.imoxion.sensems.common.logger.ErrorTraceLogger;
import com.imoxion.sensems.web.common.ImPage;
import com.imoxion.common.util.ImFileUtil;
import com.imoxion.sensems.web.common.ImbConstant;
import com.imoxion.sensems.web.database.domain.ImbTemplate;
import com.imoxion.sensems.web.database.mapper.TemplateMapper;
import com.imoxion.sensems.web.form.ActionLogForm;
import com.imoxion.sensems.web.form.TemplateForm;
import com.imoxion.sensems.web.util.HttpRequestUtil;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 템플릿 관리 Service
 * @date 2021.02.23
 * @author jhpark
 *
 */
@Service
public class TemplateService {

    protected Logger log = LoggerFactory.getLogger(TemplateService.class);

    @Autowired
    private TemplateMapper templateMapper;

    @Autowired
    private ActionLogService actionLogService;

    @Autowired
    private TemplateService templateService;

    /**
     * 검색에 따른 결과 총 개수 획득
     * @param srch_type
     * @param srch_keyword
     * @param userid
     * @return
     */
    public int getTemplateCount(String srch_type, String srch_keyword, String userid) throws Exception {
        return templateMapper.getTemplateCount(srch_type,srch_keyword,userid);
    }

    /**
     * 검색에 따른 Template 목록 정보 획득
     * @param srch_type
     * @param srch_keyword
     * @param userid
     * @param start
     * @param end
     * @return
     */
    public List<ImbTemplate> getTemplateListForPageing(String srch_type, String srch_keyword, String userid, int start, int end) throws Exception {
        return templateMapper.getTemplateListForPageing(srch_type,srch_keyword,userid,start,end);
    }

    /**
     * ukey를 이용하여 Template 정보 획득
     * @param ukey
     * @return
     */
    public ImbTemplate getTemplate(String ukey) throws Exception {
        return templateMapper.getTemplate(ukey);
    }

    /**
     * 선택한 Template 삭제
     * @param ukeys
     */
    public int deleteTemplateList(HttpServletRequest request, String userid, String[] ukeys) throws Exception {
        int fail_count = 0;
        String logParam="";
        for(int i=0;i<ukeys.length;i++){

            ImbTemplate template = getTemplate(ukeys[i].split(",")[0]);
            if(template == null){
                continue;
            }

            /** 템플릿 원래 본문에 있던 이미지 삭제 건 처리  */
            String contents = template.getContents();
            //원래 본문에 contentsImage/view.do가 존재할 때 확인 실시
            if(StringUtils.contains(contents,ImbTemplate.CONTENTS_IMAGE_VIEW_URL)){
                //원래 본문에 존재하는 이미지 key List
                List<String> keyList = new ArrayList<String>();
                int index = StringUtils.indexOf(contents,ImbTemplate.CONTENTS_IMAGE_VIEW_URL);
                int url_length = ImbTemplate.CONTENTS_IMAGE_VIEW_URL.length()+5;

                while (index > -1){
                    String key = StringUtils.substring(contents,index+url_length,index+url_length+24);
                    keyList.add(key);

                    contents= StringUtils.substring(contents,index+url_length+24);
                    index = StringUtils.indexOf(contents,ImbTemplate.CONTENTS_IMAGE_VIEW_URL);
                }

                // key List가 존재할 때 실시
                if(keyList.size() > 0) {
                    String template_path = ImbConstant.SENSDATA_PATH + File.separator + "template" + File.separator;
                    for(String file_key : keyList ){
                        try{
                            ImFileUtil.deleteFile(template_path + file_key);
                        }catch (NullPointerException ne) {
                            log.debug("Template Contents Image Delete ne False - key : {} ", file_key);
                            fail_count++;
                            continue;
                        }
                        catch (Exception e){
                            log.debug("Template Contents Image Delete False - key : {} ", file_key);
                            fail_count++;
                            continue;
                        }
                    }
                }

            }

            /** 미리보기 이미지 삭제 */
            if(StringUtils.isNotEmpty(template.getImage_path())){
                boolean deleteOK = deleteTemplateImage(template.getImage_path());
                if(!deleteOK){
                    log.debug("Template Preview Image Delete False - key : {} ", template.getImage_path());
                    fail_count++;
                    continue;
                }
            }
            //DB에서 정보 삭제
            templateMapper.deleteTemplate(ukeys[i].split(",")[0]);
            if(i==0){
                logParam= "삭제한 템플릿명 : " + ukeys[i].split(",")[1] + " / 삭제한 템플릿 key : " + ukeys[i].split(",")[0];
            }else {
                logParam += " , 삭제한 템플릿명 : " + ukeys[i].split(",")[1] + " / 삭제한 템플릿 key : " + ukeys[i].split(",")[0];
            }
        }
        //log insert start
        ActionLogForm logForm = new ActionLogForm();
        logForm.setIp(HttpRequestUtil.getRemoteAddr(request));
        logForm.setUserid(userid);
        logForm.setMenu_key("F304");
        logForm.setParam(logParam);
        actionLogService.insertActionLog(logForm);

        return fail_count;
    }


    /**
     * Template 정보 추가
     * @param templateInfo
     * @throws Exception
     */
    public void insertTemplateInfo(ImbTemplate templateInfo) throws Exception {
        templateMapper.insertTemplate(templateInfo);
    }

    /**
     * template 객체를 TemplateForm으로 맵핑
     * @param templateInfo
     */
    public TemplateForm templateInfoToForm(ImbTemplate templateInfo) {
        TemplateForm form = new TemplateForm();
        form.setUkey(templateInfo.getUkey());
        form.setFlag(templateInfo.getFlag());
        form.setTemp_name(templateInfo.getTemp_name());
        form.setContent(templateInfo.getContents());
        form.setImage_path(templateInfo.getImage_path());

        return form;

    }

    /**
     * template 썸네일 이미지 삭제
     * @param image_path
     */
    public synchronized boolean deleteTemplateImage(String image_path) throws Exception{
        boolean deleteOK = false;
        String sensdataPath = ImbConstant.SENSDATA_PATH;
        String dir_path = sensdataPath + File.separator + "template";
        try{
            File delFile = new File(dir_path + image_path);
            if (delFile.exists() && delFile.isFile()) {
                deleteOK = delFile.delete();
            }
        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("deleteOK is ne error : {}",errorId);
        }
        catch (Exception e) {
            String errorId = ErrorTraceLogger.log(e);
            log.error("deleteOK is error : {}",errorId);
        }
        return deleteOK;
    }

    /**
     * Template 정보 수정
     * @param templateInfo
     */
    public void updateTemplateInfo(ImbTemplate templateInfo) throws Exception {
        templateMapper.updateTemplate(templateInfo);
    }

    /**
     * template 사용자 및 공용 템플릿을 불러온다.
     */
    public List<ImbTemplate> getTemplateList(String userid) throws Exception{
     return templateMapper.getTemplateList(userid);
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
        int totalCount = templateMapper.getTemplateCount(category_key, search_text, userid);

        //페이지 정보 설정
        int cpage = ImStringUtil.parseInt(currentPage);
        int tSize = ImStringUtil.parseInt(pageSize);
        ImPage imPage = new ImPage(cpage, tSize, totalCount, 2);

        //템플릿 목록취득
        List<ImbTemplate> templateList = templateMapper.getTemplateListForPageing(category_key,search_text,userid,imPage.getStart(),imPage.getEnd());

        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("TEMPLATE_LIST", templateList);
        resultMap.put("IMPAGE", imPage);

        return resultMap;
    }


    public String documentToContent(String content) {
        Document document = Jsoup.parse(content);
        content = document.body().html();
        return content;
    }
}

package com.imoxion.sensems.web.controller;

import com.imoxion.common.util.HttpRequestUtil;
import com.imoxion.sensems.common.logger.ErrorTraceLogger;
import com.imoxion.sensems.web.beans.TemplateCategoryBean;
import com.imoxion.sensems.web.beans.UserInfoBean;
import com.imoxion.sensems.web.database.domain.ImbTemplate;
import com.imoxion.sensems.web.service.ImageService;
import com.imoxion.sensems.web.service.TemplateService;
import com.imoxion.sensems.web.util.JSONResult;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("editor")
public class EditorPluginController {

    @Autowired
    private TemplateService templateService;

    @Autowired
    private ImageService imageService;

    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * 템플릿 페이지를 호출한다.
     * @param session
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "template.do", method = RequestMethod.GET)
    public String template(HttpSession session, ModelMap model) throws Exception{
        UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
        return "jsp/editor/template";
    }

    /**
     * UI에서 사용자가 선택한 템플릿 값을 editor에 세팅하기 위한 템플릿 데이터를 ukey 파라메터를 통해 리턴한다.
     * @param request
     * @param session
     * @param categoryId
     * @param ukey
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "json/template/content.do", method = RequestMethod.POST)
    @ResponseBody
    public String template_content(HttpServletRequest request, HttpSession session,
                                   @RequestParam(value = "templateCategory", defaultValue = "1", required = false) String categoryId,
                                   @RequestParam("ukey") String ukey, ModelMap model) throws Exception {

        UserInfoBean userInfo = UserInfoBean.getUserSessionInfo(session);
        String userid = userInfo.getUserid();
        JSONResult result = new JSONResult();
        String weburl = HttpRequestUtil.getWebURL(request);
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
            JSONObject data = new JSONObject();
            data.put("content", content);
            data.put("subject", subject);
            data.put("image", image);


            result.setResultCode(JSONResult.SUCCESS);
            result.setData(data);

        }catch (NullPointerException | ParseException ne) {
            result.setResultCode(JSONResult.FAIL);
            result.setMessage("템플릿 정보를 못찾았습니다.");
            String errorId = ErrorTraceLogger.log(ne);
            log.error("{} - TEMPLATE CONTENT GET ERROR", errorId);
        } catch (Exception e) {
            result.setResultCode(JSONResult.FAIL);
            result.setMessage("템플릿 정보를 못찾았습니다.");
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - TEMPLATE CONTENT GET ERROR", errorId);
        }

        return result.toString();
    }

    /**
     * 템플릿 목록 취득 메소드
     * @param session
     * @param categoryId
     * @param currentPage
     * @return
     */
    @RequestMapping(value = "json/templateList.do", method = RequestMethod.POST)
    @ResponseBody
    public ModelAndView template_list(HttpSession session,
                                      @RequestParam(value = "templateText", required = false) String templateText,
                                      @RequestParam(value = "templateCategory", defaultValue = "0", required = false) String categoryId,
                                      @RequestParam(value = "currentPage", defaultValue = "1", required = false) String currentPage,
                                      @RequestParam(value = "templateSort", defaultValue = "0", required = false) String sort) throws Exception {

        ModelAndView mav = new ModelAndView();
        UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
        String userid = userSessionInfo.getUserid();

        String pageSize = "5";

        Map<String, Object> resultMap = null;

        try {

            resultMap = templateService.getTemplateList(userid, categoryId, currentPage, pageSize);
            mav.addObject("imPage", resultMap.get("IMPAGE"));
            mav.addObject("templateList", resultMap.get("TEMPLATE_LIST"));

        }catch (NullPointerException ne) {
            log.error("TEMPLATE LIST ERROR");

        }
        catch (Exception e) {
            log.error("TEMPLATE LIST ERROR");
        }

        mav.setViewName("jsonView");
        return mav;
    }

    /**
     * 이미지목록 페이지를 호출한다.
     * @param session
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "image.do", method = RequestMethod.GET)
    public String paper(HttpSession session, ModelMap model) throws Exception{
        return "jsp/editor/image";
    }

    /**
     * 메일작성에서 이미지 삽입을 클릭하였을경우 실행되는 메소드
     * @param request
     * @param response
     * @param session
     * @param model
     * @param categoryId
     * @param currentPage
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "json/imageList.do", method = RequestMethod.POST)
    @ResponseBody
    public ModelAndView image_list(HttpServletRequest request, HttpServletResponse response, HttpSession session,
                                   ModelMap model,
                                   @RequestParam(value = "imageCategory", defaultValue = "0", required = false) String categoryId,
                                   @RequestParam(value = "currentPage", defaultValue = "1", required = false) String currentPage)
            throws Exception {

        ModelAndView mav = new ModelAndView();
        UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);

        String userid = userSessionInfo.getUserid();
        String pageSize = "6";

        Map<String, Object> resultMap = null;

        try {
            resultMap = imageService.getImageList(userid, categoryId, currentPage, pageSize);

            mav.addObject("imPage", resultMap.get("IMPAGE"));
            mav.addObject("imageList", resultMap.get("IMAGE_LIST"));
            mav.addObject("weburl", HttpRequestUtil.getWebURL(request));

        }catch (NullPointerException ne ) {
            log.error("IMAGELIST GET ERROR");

        }
        catch (Exception e) {
            log.error("IMAGELIST GET ERROR");

        }

        mav.setViewName("jsonView");
        return mav;
    }
}
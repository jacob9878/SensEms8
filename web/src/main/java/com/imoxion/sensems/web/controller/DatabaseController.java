package com.imoxion.sensems.web.controller;

import com.imoxion.common.util.ImStringUtil;
import com.imoxion.sensems.common.logger.ErrorTraceLogger;
import com.imoxion.sensems.web.beans.UserInfoBean;
import com.imoxion.sensems.web.common.ImPage;
import com.imoxion.sensems.web.database.domain.ImbDBInfo;
import com.imoxion.sensems.web.form.ActionLogForm;
import com.imoxion.sensems.web.form.DatabaseForm;
import com.imoxion.sensems.web.service.ActionLogService;
import com.imoxion.sensems.web.service.DatabaseService;
import com.imoxion.sensems.web.service.JCryptionService;
import com.imoxion.sensems.web.util.HttpRequestUtil;
import javassist.NotFoundException;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.Null;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.List;

/**
 * yeji
 * 2021. 03. 03
 * 데이터베이스관리 관련 Controller
 */
@Controller
@RequestMapping("/sysman/database/")
public class DatabaseController {

    protected Logger log = LoggerFactory.getLogger( DatabaseController.class );

    @Autowired
    private DatabaseService databaseService;

    @Autowired
    private MessageSourceAccessor message;

    @Autowired
    private ActionLogService actionLogService;

    @Autowired
    private JCryptionService jCryptionService;

    /**
     * 초기화면 및 페이징
     * @param session
     * @param form
     * @param model
     * @return
     */
    @RequestMapping(value="list.do")
    public String list(HttpSession session, @ModelAttribute("databaseForm") DatabaseForm form, ModelMap model,
                       HttpServletResponse response){

        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-control", "no-cache");

        UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
        try{
            int totalSize = 0;
            Integer cpage = ImStringUtil.parseInt(form.getCpage());
            Integer pageGroupSize = ImStringUtil.parseInt(form.getPagegroupsize());
            totalSize = databaseService.getListCount();

            ImPage pageInfo = new ImPage(cpage, userSessionInfo.getPagesize(), totalSize, pageGroupSize);
            model.addAttribute("pageInfo", pageInfo);

            List<ImbDBInfo> dbInfoList = null;
            dbInfoList = databaseService.getDBInfoListForPaging(pageInfo.getStart(), pageInfo.getEnd()); // 페이징 위한 리스트
            model.addAttribute("dbInfoList", dbInfoList);
            model.addAttribute("cpage", cpage);
        }catch (NullPointerException np) {
            String errorId = ErrorTraceLogger.log(np);
            log.error("dbinfo getList Error np : {}", errorId);
        }
        catch (BadSqlGrammarException be) {
            String errorId = ErrorTraceLogger.log(be);
            log.error("dbinfo getList Error be : {}", errorId);
        }
        catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("dbinfo getList Error : {}", errorId);
        }
        return "/sysman/database_list";
    }

    /**
     * 데이터베이스 추가 Form
     * @param form
     * @param model
     * @return
     */
    @RequestMapping(value="add.do", method=RequestMethod.GET)
    public String addForm(@ModelAttribute("databaseForm") DatabaseForm form, HttpServletRequest request, ModelMap model){
        log.debug("database add get mapping url - /sysman/database/add.do");
        String cpage = request.getParameter("cpage");
        model.addAttribute("cpage", cpage);
        model.addAttribute("databaseForm", form);
        return "/sysman/database_add";
    }

    /**
     * 데이터베이스 추가
     * @param
     * @return
     */
    @RequestMapping(value="add.do", method=RequestMethod.POST)
    public String add(HttpSession session, @ModelAttribute("databaseForm") DatabaseForm form, ModelMap model, BindingResult bindingResult, HttpServletRequest request) throws Exception{
        log.debug("database add post mapping url - /sysman/database/add.do");
        UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
        String userid = userSessionInfo.getUserid();

        String dbtype = form.getDbtype();
        String privateKey = session.getAttribute("privateKey").toString();

        // DatabaseForm 복호화 진행
        if(dbtype.equals("mysql") || dbtype.equals("mssql") || dbtype.equals("tibero")){
            String host = jCryptionService.deCrypt(privateKey, form.getEncAESKey(), form.getDbhost());//평문 호스트
            String real_dbname = jCryptionService.deCrypt(privateKey, form.getEncAESKey(), form.getReal_dbname());//평문 DB네임
            form.setDbhost(host);
            form.setReal_dbname(real_dbname);
        } else if(dbtype.equals("oracle")){
            String orahost = jCryptionService.deCrypt(privateKey, form.getEncAESKey(), form.getOracle_svc());//평문 호스트(오라클)
            form.setOracle_svc(orahost);
        }

        String user = jCryptionService.deCrypt(privateKey, form.getEncAESKey(), form.getDbuser());//평문 유저
        String pass = jCryptionService.deCrypt(privateKey, form.getEncAESKey(), form.getDbpasswd());//평문 패스워드

        form.setDbuser(user);
        form.setDbpasswd(pass);

        //userid 체크
        if(form == null || StringUtils.isEmpty(userid)){
            log.error("Database Edit Required Parameter Is Null - DatabaseForm or userid or ukey");
            return "/sysman/database_add";
        }

        try {
            if (databaseService.checkDBExist(userid, form.getDbname())) { // db명 중복확인 체크( true 이면 중복)
                form.setExistDB(true); // ExistDB 값 default false 세팅
                log.info("database name is already exist - dbname : {}", form.getDbname());
            }
            //1.유효성 체크
            form.databaseValidator(form, bindingResult);
            if (bindingResult.hasErrors()) { //BindingResult 의 hasErrors : 검증 결과에 에러가 있다면 true 리턴
                List<FieldError> errors = bindingResult.getFieldErrors();
                for (FieldError error : errors) {
                    log.error("database add validator error info : {},{},{}", error.getField(), error.getCode(), message.getMessage(error));
                }
                model.addAttribute("databaseForm", form);
                return "/sysman/database_add";
            }
            //2.추가
            databaseService.databaseAdd(userid, form);

            //log insert start
            ActionLogForm logForm = new ActionLogForm();
            logForm.setIp(HttpRequestUtil.getRemoteAddr(request));
            logForm.setUserid(userSessionInfo.getUserid());
            logForm.setMenu_key("G301");
            logForm.setParam("DB Name : " + form.getDbname() + " / DB Type : " + form.getDbtype());
            actionLogService.insertActionLog(logForm);

        } catch (NoSuchMessageException e) {
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - database add error", errorId);
            return "/sysman/database_add";
        }
        catch (BadSqlGrammarException be) {
            String errorId = ErrorTraceLogger.log(be);
            log.error("{} - database add error", errorId);
            return "/sysman/database_add";
        }
        catch (Exception e) {
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - database add error", errorId);
            return "/sysman/database_add";
        }
        return "redirect:/sysman/database/list.do";
    }

    /**
     * @comment 데이터베이스 연결테스트 수행
     * @param session
     * @param form
     * @return
     * @throws Exception
     */
    @RequestMapping(value="connectTest.json", method=RequestMethod.POST)
    @ResponseBody
    public String ConnectCheck(HttpSession session, @ModelAttribute("databaseForm") DatabaseForm form) throws Exception{
        log.debug("database connect test url mapping - /sysman/database/connectTest.json");
        JSONObject jsonResult = new JSONObject();
        UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
        String userid = userSessionInfo.getUserid();
        //1.유효성 체크
        try{
            JSONObject validResult = databaseService.validateForConnect(form, jsonResult);
            if(validResult != null && validResult.size() != 0){
                log.error("database connect test validate result - {}", validResult.toString());
                return validResult.toString();
            }
        }
        catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("database connect validator error ne : {}", errorId);
            jsonResult.put("result", false);
            jsonResult.put("message", message.getMessage("E0060","작업중 오류가 발생하였습니다."));
            return jsonResult.toString();
        }

        catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("database connect validator error : {}", errorId);
            jsonResult.put("result", false);
            jsonResult.put("message", message.getMessage("E0060","작업중 오류가 발생하였습니다."));
            return jsonResult.toString();
        }
        //2.연결테스트
        try{
            if(databaseService.connectTest(form)){
                jsonResult.put("result", true);
                jsonResult.put("message", message.getMessage("E0189","데이터베이스 연결에 성공했습니다. "));
                return jsonResult.toString();
            }else {
                jsonResult.put("result", false);
                jsonResult.put("message", message.getMessage("E0190","데이터베이스 연결에 실패하였습니다."));
                return jsonResult.toString();
            }
        }catch (NullPointerException ne2) {
            String errorId = ErrorTraceLogger.log(ne2);
            log.error("database connect test error de : {}", errorId);
            jsonResult.put("result", false);
            jsonResult.put("message", message.getMessage("E0060","작업중 오류가 발생하였습니다."));
            return jsonResult.toString();
        } catch(Exception e2) {
            String errorId = ErrorTraceLogger.log(e2);
            log.error("database connect test error : {}", errorId);
            jsonResult.put("result", false);
            jsonResult.put("message", message.getMessage("E0060","작업중 오류가 발생하였습니다."));
            return jsonResult.toString();
        }
    }

    /**
     * 데이터베이스 정보 수정 페이지
     * @param form
     * @param model
     * @return
     */
    @RequestMapping(value="edit.do", method=RequestMethod.GET)
    public String editForm(@ModelAttribute("databaseForm") DatabaseForm form, HttpServletRequest request, @RequestParam(value = "ukey",required = false) String ukey, ModelMap model) throws Exception{
        log.debug("database editForm get mapping url - /sysman/database/edit.do");
        // ukey 체크
        if(StringUtils.isEmpty(ukey)){
            log.error("Database Edit Required Parameter Is Null - ukey");
            return "redirect:/sysman/database/list.do";
        }
        try{
            ImbDBInfo dbInfo = databaseService.getDBInfoByUkey(ukey);
            String cpage = request.getParameter("cpage");
            model.addAttribute("cpage", cpage);

            if(dbInfo != null){
                form = databaseService.dbInfoToForm(dbInfo, ukey);
                model.addAttribute("databaseForm", form);
            }else{
                return "redirect:/sysman/database/list.do";
            }
        }
        catch (NullPointerException | DataAccessException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("{} - database editForm view error", errorId);
            return "redirect:/sysman/database/list.do";
        } catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - database editForm view error", errorId);
            return "redirect:/sysman/database/list.do";
        }
        return "/sysman/database_edit";
    }

    /**
     * 데이터베이스 수정 로직 수행
     * @param session
     * @param form
     * @return
     */
    @RequestMapping(value="edit.do", method=RequestMethod.POST)
    public String edit(HttpSession session, @ModelAttribute("databaseForm") DatabaseForm form, BindingResult bindingResult, ModelMap model, HttpServletRequest request) throws Exception {
        log.debug("database edit post mapping url - /sysman/database/edit.do");
        UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
        String userid = userSessionInfo.getUserid();

        String dbtype = form.getDbtype();
        String privateKey = session.getAttribute("privateKey").toString();

        // DatabaseForm 복호화 진행
        if(dbtype.equals("mysql") || dbtype.equals("mssql") || dbtype.equals("tibero")){
            String host = jCryptionService.deCrypt(privateKey, form.getEncAESKey(), form.getDbhost());//평문 호스트
            String real_dbname = jCryptionService.deCrypt(privateKey, form.getEncAESKey(), form.getReal_dbname());//평문 DB네임
            form.setDbhost(host);
            form.setReal_dbname(real_dbname);
        } else if(dbtype.equals("oracle")){
            String orahost = jCryptionService.deCrypt(privateKey, form.getEncAESKey(), form.getOracle_svc());//평문 호스트(오라클)
            form.setOracle_svc(orahost);
        }

        String user = jCryptionService.deCrypt(privateKey, form.getEncAESKey(), form.getDbuser());//평문 유저
        String pass = jCryptionService.deCrypt(privateKey, form.getEncAESKey(), form.getDbpasswd());//평문 패스워드

        form.setDbuser(user);
        form.setDbpasswd(pass);

        //userid 체크
        if(form == null || StringUtils.isEmpty(userid) || StringUtils.isEmpty(form.getUkey())){
            log.error("Database Edit Required Parameter Is Null - DatabaseForm or userid or ukey");
            return "/sysman/database_edit";
        }
        try{
            String old_dbname = databaseService.getDBInfoByUkey(form.getUkey()).getDbname();
            if(!StringUtils.equalsIgnoreCase(old_dbname,form.getDbname())){ // 기존 dbname이랑 비교하여 다른 경우에만 중복체크 수행
                if(databaseService.checkDBExist(userid, form.getDbname())){
                    form.setExistDB(true); // ExistDB 값 default false 세팅
                    log.info("database name is already exist - dbname:{}", form.getDbname() );
                }
            }
            //1.유효성 체크
            form.databaseValidator(form, bindingResult);
            if (bindingResult.hasErrors()) { //BindingResult 의 hasErrors : 검증 결과에 에러가 있다면 true 리턴
                List<FieldError> errors = bindingResult.getFieldErrors();
                for (FieldError error : errors) {
                    log.error("database edit validator error info : {},{},{}", error.getField(), error.getCode(), message.getMessage(error));
                }
                model.addAttribute("databaseForm", form);
                return "/sysman/database_edit";
            }
            //2.수정
            databaseService.databaseEdit(userid, form);

            //log insert start
            ActionLogForm logForm = new ActionLogForm();
            String param = "";
            logForm.setIp(HttpRequestUtil.getRemoteAddr(request));
            logForm.setUserid(userSessionInfo.getUserid());
            logForm.setMenu_key("G302");
            if(StringUtils.equals(form.getOri_name(),form.getDbname())){
                param = "DB명 : " + form.getDbname();
            } else {
                param = "이전 DB명 : " + form.getOri_name() + " / 변경 DB명 : " + form.getDbname();
            }
            if(StringUtils.equals(form.getOri_dbtype(),form.getDbtype())){
                param += " / DB타입 : " + form.getDbtype();
            }else {
                param += " / 이전 DB타입 : " + form.getOri_dbtype() + " / 변경 DB타입 : " + form.getDbtype();
            }
            logForm.setParam(param);
            actionLogService.insertActionLog(logForm);

        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("{} - database edit error np", errorId);
            return "/sysman/database_edit";
        }catch (BadSqlGrammarException be) {
            String errorId = ErrorTraceLogger.log(be);
            log.error("{} - database edit error be", errorId);
            return "/sysman/database_edit";
        }

        catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - database edit error", errorId);
            return "/sysman/database_edit";
        }
        return "redirect:/sysman/database/list.do";
    }

    /**
     * 데이터베이스 삭제
     * @param
     * @return
     */
    @RequestMapping(value="delete.json", method=RequestMethod.POST)
    @ResponseBody
    public String deleteDBInfo(@RequestParam(value = "ukeys[]", required = false) String[] ukeys, HttpServletRequest request, HttpSession session){
        JSONObject jsonResult = new JSONObject();
        try {
            if(ukeys != null && ukeys.length > 0){
                String param = "";
                for (int i = 0; i < ukeys.length; i++) {
                    databaseService.deleteDBInfo(ukeys[i].split(",")[0]);
                    if(i==0){
                        param= "삭제한 DB명 : " + ukeys[i].split(",")[1] + " / 삭제한 ukey : " + ukeys[i].split(",")[0];
                    }else {
                        param += " , 삭제한 DB명 : " + ukeys[i].split(",")[1] + " / 삭제한 ukey : " + ukeys[i].split(",")[0];
                    }
                }

                //log insert start
                UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
                ActionLogForm logForm = new ActionLogForm();
                logForm.setIp(HttpRequestUtil.getRemoteAddr(request));
                logForm.setUserid(userSessionInfo.getUserid());
                logForm.setMenu_key("G303");
                logForm.setParam(param);
                actionLogService.insertActionLog(logForm);
            }


        }catch (DataAccessException ex) {
            String errorId = ErrorTraceLogger.log(ex);
            log.error("database delete error da : {}", errorId);
            jsonResult.put("result", false);
            jsonResult.put("message", message.getMessage("E0060","작업중 오류가 발생하였습니다."));
            return jsonResult.toString();
        }
        catch (NullPointerException np) {
            String errorId = ErrorTraceLogger.log(np);
            log.error("database delete error np : {}", errorId);
            jsonResult.put("result", false);
            jsonResult.put("message", message.getMessage("E0060","작업중 오류가 발생하였습니다."));
            return jsonResult.toString();
        }

        catch (Exception e) {
            String errorId = ErrorTraceLogger.log(e);
            log.error("database delete error : {}", errorId);
            jsonResult.put("result", false);
            jsonResult.put("message", message.getMessage("E0060","작업중 오류가 발생하였습니다."));
            return jsonResult.toString();
        }
        jsonResult.put("result", true);
        jsonResult.put("message", message.getMessage("E0070","삭제되었습니다."));
        return jsonResult.toString();
    }
}

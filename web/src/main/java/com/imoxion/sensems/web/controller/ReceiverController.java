package com.imoxion.sensems.web.controller;

import com.imoxion.common.util.ImStringUtil;
import com.imoxion.common.util.ImUtils;
import com.imoxion.security.ImSecurityLib;
import com.imoxion.sensems.common.logger.ErrorTraceLogger;
import com.imoxion.sensems.web.beans.*;
import com.imoxion.sensems.web.common.ImPage;
import com.imoxion.sensems.web.common.ImbConstant;
import com.imoxion.sensems.web.database.domain.*;
import com.imoxion.sensems.web.form.*;
import com.imoxion.sensems.web.service.*;
import com.imoxion.sensems.web.util.HttpRequestUtil;
import com.imoxion.sensems.web.util.ImUtility;
import com.imoxion.sensems.web.util.JSONResult;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.*;
import java.util.*;
import java.util.Date;

@Controller
@RequestMapping("receiver/group")
public class ReceiverController {

    protected Logger log = LoggerFactory.getLogger( ReceiverController.class );

    @Autowired
    private ReceiverService receiverService;

    @Autowired
    private DatabaseService databaseService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private MessageSourceAccessor message;

    @Autowired
    private ActionLogService actionLogService;

    @Autowired
    private SendResultService sendResultService;

    @Autowired
    private UserService userService;

    /**
     * 수신그룹 검색 및 목록 획득
     * @param form
     * @param session
     * @param model
     * @return
     */
    @RequestMapping(value="list.do")
    public String receiverGroupList(@ModelAttribute("ReceiverGroupListForm") ReceiverGroupListForm form,HttpServletRequest request, HttpSession session, ModelMap model,
                                    HttpServletResponse response){

        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-control", "no-cache");

        //페이지 목록 개수 세션에서 획득
        UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);

        try{


            String srch_type = form.getSrch_type();
            String srch_keyword = form.getSrch_keyword();
            String userid = null;
            if(userSessionInfo.getPermission().equals("U")){
                userid = userSessionInfo.getUserid();
            }

            int totalsize =0;
            int cpage = ImStringUtil.parseInt(form.getCpage());
            model.addAttribute("cpage", cpage);
            int pageGroupSize =  ImStringUtil.parseInt(form.getPagegroupsize());

            boolean issearch = true;
            if(StringUtils.isEmpty(srch_keyword)) {
                issearch = false;
            }
            model.addAttribute("issearch", issearch);

            //검색 결과 수신그룹 수
            totalsize = receiverService.getReceiverGroupCount(srch_type,srch_keyword,userid);
            //페이지 정보
            ImPage pageInfo = new ImPage(cpage, userSessionInfo.getPagesize(), totalsize, pageGroupSize);
            //수신그룹정보 획득
            List<ImbReceiver> receiverGroupList = receiverService.getReceiverGroupForPageing(srch_type,srch_keyword,pageInfo.getStart(), pageInfo.getEnd(),userid);

            model.addAttribute("srch_type", srch_type);
            model.addAttribute("srch_key", srch_keyword);
            model.addAttribute("totalsize",totalsize);
            model.addAttribute("pageInfo", pageInfo);
            model.addAttribute("receiverGroupList", receiverGroupList);
            model.addAttribute("permission", userSessionInfo.getPermission());

            //log insert start
            // TODO 로그정책 변경으로 삭제 (수신그룹관리>조회 E101)

        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("{} - ReceiverGroup LIST ne ERROR", errorId);
        }
        catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - ReceiverGroup LIST ERROR", errorId);
        }
        model.addAttribute("ReceiverGroupListForm",form);

        return "/receiver/receiver_list";
    }

    /**
     * 추가 페이지 이동
     * @param session
     * @param model
     * @param form
     * @return
     */
    @RequestMapping(value="add.do",method = RequestMethod.GET)
    public String receiverGroupAddForm(HttpSession session ,ModelMap model,HttpServletRequest request , @ModelAttribute("ReceiverGroupForm") ReceiverGroupForm form,
                                       @RequestParam(value = "srch_key",required = false)String srch_key,@RequestParam(value = "srch_type",required = false)String srch_type){

        try{
            List<ImbDBInfo> dbInfoList = databaseService.getDBInfoList();
            model.addAttribute("DBList",dbInfoList);
            String cpage = request.getParameter("cpage");
            model.addAttribute("cpage", cpage);
            model.addAttribute("srch_key", srch_key);
            model.addAttribute("srch_type", srch_type);
        }catch (NullPointerException ne){
            String errorId = ErrorTraceLogger.log(ne);
            log.error("{} - GET DBInfo LIST ne ERROR", errorId);
        }
        catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - GET DBInfo LIST ERROR", errorId);
        }
        model.addAttribute("ReceiverGroupForm",form);
        return "/receiver/receiver_add";
    }

    /**
     * 수신그룹 정보 추가 동작
     * @param session
     * @param model
     * @param form
     * @return
     */
    @RequestMapping(value="add.do",method = RequestMethod.POST)
    @ResponseBody
    public String receiverGroupAdd(HttpSession session ,HttpServletRequest request, ModelMap model, @ModelAttribute("ReceiverGroupForm") ReceiverGroupForm form ){

        UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
        JSONResult result = new JSONResult();

        try{
            String recv_name = form.getRecv_name();
            String dbkey = form.getDbkey();
            String query = form.getQuery();

            //유효성 체크
            boolean isValid = true;
            if(StringUtils.isEmpty(recv_name)){
                result.setResultCode(JSONResult.FAIL);
                result.setMessage(message.getMessage("E0176","수신그룹명을 입력해 주세요."));
                isValid = false;
            }else if(!ImUtility.validCharacter(recv_name)){
                result.setResultCode(JSONResult.FAIL);
                result.setMessage(message.getMessage("E0737","사용할 수 없는 특수문자가 포함되어 있습니다. (사용가능한 특수 문자 : (_) , (,) , (-) )"));
                isValid = false;
            }else if(StringUtils.isEmpty(dbkey)) {
                result.setResultCode(JSONResult.FAIL);
                result.setMessage(message.getMessage("E0177","데이터베이스를 선택해 주세요."));
                isValid = false;
            }else if(StringUtils.isEmpty(query)){
                result.setResultCode(JSONResult.FAIL);
                result.setMessage(message.getMessage("E0178","쿼리를 입력해 주세요."));
                isValid = false;
            }
            //유효하지 않은 값이 들어왔을 때 추가 페이지로 다시 이동
            if(!isValid){
                return receiverGroup2(request,form,model,true, result);
            }
            ImbDBInfo dbInfo = databaseService.getDBInfoByUkey(dbkey);
            if(dbInfo == null){
                result.setResultCode(JSONResult.FAIL);
                result.setMessage(message.getMessage("E0200","데이터 베이스 정보가 없습니다."));
                return receiverGroup2(request,form,model,true, result);
            }
            boolean sqlOK = databaseService.sqlValidationCheck(dbInfo,query);
            if(!sqlOK){
                result.setResultCode(JSONResult.FAIL);
                result.setMessage(message.getMessage("E0298","데이터베이스와 연결이 되지 않거나 올바르지 않은 쿼리 입니다."));
                return receiverGroup2(request,form,model,true, result);
            }
            result.setResultCode(JSONResult.SUCCESS);

            ImbReceiver receiverInfo = new ImbReceiver();
            String ukey= ImUtils.makeKeyNum(24);
            String userid = userSessionInfo.getUserid();

            receiverInfo.setUkey(ukey);
            receiverInfo.setUserid(userid);
            receiverInfo.setDbkey(dbkey);
            receiverInfo.setRecv_name(recv_name);
            receiverInfo.setRegdate(new Date());
            receiverInfo.setExtended("");

            if(ImbConstant.DATABASE_ENCRYPTION_USE){
                query = ImSecurityLib.encryptAES256(ImbConstant.DATABASE_AES_KEY, query);
            }
            receiverInfo.setQuery(query);

            receiverService.insertReceiverInfo(receiverInfo);

            //log insert start
            ActionLogForm logForm = new ActionLogForm();
            logForm.setIp(HttpRequestUtil.getRemoteAddr(request));
            logForm.setUserid(userSessionInfo.getUserid());
            logForm.setMenu_key("E102");
            logForm.setParam("수신그룹명 : " + form.getRecv_name() + " / 선택 DB Key : " + form.getDbkey());
            actionLogService.insertActionLog(logForm);

        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("{} - Add Receiver Group ne ERROR", errorId);
        }
        catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - Add Receiver Group ERROR", errorId);
        }

        return result.toString();
    }
    /**
     * 수정 페이지 이동
     * @param session
     * @param model
     * @param
     * @return
     */
    @RequestMapping(value="edit.do",method = RequestMethod.GET)
    public String receiverGroupEditForm(HttpSession session ,ModelMap model,HttpServletRequest request ,@RequestParam(value = "ukey",required = false)String ukey,
                                        @RequestParam(value = "srch_key",required = false)String srch_key,@RequestParam(value = "srch_type",required = false)String srch_type){

        if(StringUtils.isEmpty(ukey)){
            return "redirect:/receiver/group/list.do";
        }
        try{
            ImbReceiver receiverInfo = receiverService.getReceiverGroup(ukey);
            if(receiverInfo == null){
                return "redirect:/receiver/group/list.do";
            }

            ReceiverGroupForm form = receiverService.receiverInfoToForm(receiverInfo);
            List<ImbDBInfo> dbInfoList = databaseService.getDBInfoList();
            String cpage = request.getParameter("cpage");
            model.addAttribute("cpage", cpage);
            model.addAttribute("srch_key", srch_key);
            model.addAttribute("srch_type", srch_type);

            model.addAttribute("DBList",dbInfoList);
            model.addAttribute("ReceiverGroupForm",form);
        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("{} - Edit Page ERROR", errorId);
        }
        catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - Edit Page ERROR", errorId);
        }
        return "/receiver/receiver_edit";
    }
    /**
     * 수신그룹 정보 수정 동작
     * @param session
     * @param model
     * @param form
     * @return
     */
    @RequestMapping(value="edit.do",method = RequestMethod.POST)
    @ResponseBody
    public String receiverGroupEditProc(HttpSession session , HttpServletRequest request,
                                       ModelMap model, @ModelAttribute("ReceiverGroupForm") ReceiverGroupForm form ){
        UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
        JSONResult result = new JSONResult();

        String ukey = form.getUkey();
        if(StringUtils.isEmpty(ukey)){
            return "redirect:/receiver/group/list.do";
        }
        try{
            String recv_name = form.getRecv_name();
            String dbkey = form.getDbkey();
            String query = form.getQuery();

            //유효성 체크
            boolean isValid = true;
            if(StringUtils.isEmpty(recv_name)){
                result.setResultCode(JSONResult.FAIL);
                result.setMessage(message.getMessage("E0176","수신그룹명을 입력해 주세요."));
                isValid = false;
            }else if(!ImUtility.validCharacter(recv_name)){
                result.setResultCode(JSONResult.FAIL);
                result.setMessage(message.getMessage("E0737","사용할 수 없는 특수문자가 포함되어 있습니다. (사용가능한 특수 문자 : (_) , (.) , (-) )"));
                isValid = false;
            }else if(StringUtils.isEmpty(dbkey)) {
                result.setResultCode(JSONResult.FAIL);
                result.setMessage(message.getMessage("E0177","데이터베이스를 선택해 주세요."));
                isValid = false;
            }else if(StringUtils.isEmpty(query)){
                result.setResultCode(JSONResult.FAIL);
                result.setMessage(message.getMessage("E0178","쿼리를 입력해 주세요."));
                isValid = false;
            }
            //유효하지 않은 값이 들어왔을 때 추가 페이지로 다시 이동
            if(!isValid){
                return receiverGroup2(request,form,model,false, result);
            }
            ImbDBInfo dbInfo = databaseService.getDBInfoByUkey(dbkey);
            if(dbInfo == null){
                result.setResultCode(JSONResult.FAIL);
                result.setMessage(message.getMessage("E0200","데이터 베이스 정보가 없습니다."));
                return receiverGroup2(request,form,model,false, result);
            }

            boolean sqlOK = databaseService.sqlValidationCheck(dbInfo,query);
            if(!sqlOK){
                result.setResultCode(JSONResult.FAIL);
                result.setMessage(message.getMessage("E0298","데이터베이스와 연결이 되지 않거나 올바르지 않은 쿼리 입니다."));
                return receiverGroup2(request,form,model,false, result);
            }

            ImbReceiver receiverInfo = receiverService.getReceiverGroup(ukey);
            if(receiverInfo == null){
                result.setResultCode(JSONResult.FAIL);
                result.setMessage(message.getMessage("E0295","수정할 수신 그룹 정보가 없습니다."));
                return receiverGroup2(request,form,model,false, result);
            }
            result.setResultCode(JSONResult.SUCCESS);

            ImbReceiver newReceiverInfo = new ImbReceiver();
            newReceiverInfo.setUkey(ukey);
            newReceiverInfo.setRecv_name(recv_name);
            newReceiverInfo.setDbkey(dbkey);

            if(ImbConstant.DATABASE_ENCRYPTION_USE){
                query = ImSecurityLib.encryptAES256(ImbConstant.DATABASE_AES_KEY, query);
            }
            newReceiverInfo.setQuery(query);

            receiverService.updateReceiverInfo(newReceiverInfo);

            if(StringUtils.equals(form.getRecv_name(),form.getOri_name())){
                ActionLogForm logForm = new ActionLogForm();
                logForm.setIp(HttpRequestUtil.getRemoteAddr(request));
                logForm.setUserid(userSessionInfo.getUserid());
                logForm.setMenu_key("E103");
                logForm.setParam("수신그룹명 : " + form.getRecv_name() + " / 선택 DB Key : " + form.getDbkey());
                actionLogService.insertActionLog(logForm);
            }else {
                //log insert start
                ActionLogForm logForm = new ActionLogForm();
                logForm.setIp(HttpRequestUtil.getRemoteAddr(request));
                logForm.setUserid(userSessionInfo.getUserid());
                logForm.setMenu_key("E103");
                logForm.setParam("이전 수신그룹명 : " + form.getOri_name() + " / 변경 수신그룹명 : " + form.getRecv_name() + " / 선택 DB Key : " + form.getDbkey());
                actionLogService.insertActionLog(logForm);
            }

        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("{} - Edit Receiver Group ne ERROR", errorId);
        }
        catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - Edit Receiver Group ERROR", errorId);
        }

        return result.toString();
    }

    /**
     * 수신그룹 삭제 동작
     * @param session
     * @param ukeys
     * @param model
     * @return
     */
    @RequestMapping(value = "deleteReceiverGroups.json", method = RequestMethod.POST)
    @ResponseBody
    public String receiverGroupDelete(HttpServletRequest request, HttpSession session, @RequestParam(value = "ukeys", required = false) String[] ukeys, ModelMap model)  {
        UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
        String chk_pm = userSessionInfo.getPermission(); // 권한 확인 A:관리자, U:사용자
        String chk_id = userSessionInfo.getUserid(); // 로그인 유저 id 확인

        JSONResult result = new JSONResult();
        try{
            if(ukeys == null || ukeys.length==0){
                result.setResultCode(JSONResult.FAIL);
                result.setMessage(message.getMessage("E0550","선택된 항목이 없습니다."));
                return result.toString();
            }

            // 체크한 수신그룹 삭제를 위한 작성자 체크 및 권한 체크
            for (int i = 0; i < ukeys.length; i++) {
                ImbReceiver receiverInfo = receiverService.getReceiverGroup(ukeys[i].split(",")[0]);
                if(!receiverInfo.getUserid().equals(chk_id)){ // 로그인한 유저와 해당 수신그룹 작성자의 아이디가 일치하는지 확인
                    if(chk_pm.equals("U")) { // 일치하지 않는다면 관리자 권한인지 확인
                        result.setResultCode(JSONResult.FAIL);
                        result.setMessage(message.getMessage("E0592","수신그룹은 관리자 또는 작성자만 삭제할 수 있습니다."));
                        return result.toString();
                    }
                }
            }

           receiverService.deleteReceiverGroupList(ukeys);

            //log insert start
            String logParam = "";
            for(int i=0; i<ukeys.length;i++){
                if(i==0){
                    logParam= "삭제한 수신그룹명 : " + ukeys[i].split(",")[1] + " / 삭제한 수신그룹 Key : " + ukeys[i].split(",")[0];
                }else {
                    logParam += " , 삭제한 수신그룹명 : " + ukeys[i].split(",")[1] + " / 삭제한 수신그룹 Key : " + ukeys[i].split(",")[0];
                }
            }
            ActionLogForm logForm = new ActionLogForm();
            logForm.setIp(HttpRequestUtil.getRemoteAddr(request));
            logForm.setUserid(userSessionInfo.getUserid());
            logForm.setMenu_key("E104");
            logForm.setParam(logParam);
            actionLogService.insertActionLog(logForm);

        }catch (SQLException ex) {
            String errorId = ErrorTraceLogger.log(ex);
            log.error("{} - Receiver Group Delete ERROR", errorId);
            result.setResultCode(JSONResult.FAIL);
            result.setMessage(message.getMessage("E0060","작업중 오류가 발생하였습니다."));
            return result.toString();
        }
        catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - Receiver Group Delete ERROR", errorId);
            result.setResultCode(JSONResult.FAIL);
            result.setMessage(message.getMessage("E0060","작업중 오류가 발생하였습니다."));
            return result.toString();
        }
        result.setResultCode(JSONResult.SUCCESS);
        result.setMessage(message.getMessage("E0070","삭제되었습니다."));
        return result.toString();
    }

    /**
     * 필드 추가 시 테이블 데이터 획득
     * @param session
     * @param dbkey
     * @param model
     * @return
     */
    @RequestMapping(value = "getTables.json", method = RequestMethod.POST)
    @ResponseBody
    public String getTablesByDbkey(HttpSession session, @RequestParam(value = "dbkey", required = false) String dbkey, ModelMap model){

        JSONObject object = new JSONObject();
        try{
            if(StringUtils.isEmpty(dbkey)){
                object.put("result",false);
                object.put("message",message.getMessage("E0177", "데이터베이스를 선택해 주세요."));
                return object.toString();
            }
            ImStringUtil.parseInt("test");

            ImbDBInfo dbInfo = databaseService.getDBInfoByUkey(dbkey);

            if(dbInfo == null) {
                object.put("result",false);
                object.put("message",message.getMessage("E0200", "데이터 베이스 정보가 없습니다."));
                return object.toString();
            }



            String dbtype = dbInfo.getDbtype();
            String address = dbInfo.getAddress();
            String dbUser = dbInfo.getDbuser();
            String dbPasswd = dbInfo.getDbpasswd();
            Connection con = null;

            JSONArray tableList = new JSONArray();
            try{
                con = databaseService.getDBConnection(dbtype,address,dbUser,dbPasswd);
                ResultSet rs = con.getMetaData().getTables(null,null,null,new String[]{"TABLE"});

                while(rs.next()){
                    JSONObject table = new JSONObject();

                    String tableName = rs.getString("TABLE_NAME");
                    table.put("tableName",tableName);

                    tableList.add(table);
                }
            }catch (SQLException ex) {
                String errorId = ErrorTraceLogger.log(ex);
                log.error("{} - DataBase Connection sql ERROR", errorId);
                object.put("result",false);
                object.put("message",message.getMessage("E0190", "데이터베이스 연결에 실패하였습니다."));
                return object.toString();
            }
            catch (Exception e){
                String errorId = ErrorTraceLogger.log(e);
                log.error("{} - DataBase Connection ERROR", errorId);
                object.put("result",false);
                object.put("message",message.getMessage("E0190", "데이터베이스 연결에 실패하였습니다."));
                return object.toString();
            }finally {
                if(con != null) con.close();
            }

            object.put("result",true);
            object.put("tableList",tableList);
        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("{} - Open Field Popup ne ERROR", errorId);
            object.put("result",false);
            object.put("message",message.getMessage("E0060", "작업중 오류가 발생하였습니다."));
            return object.toString();
        }
        catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - Open Field Popup ERROR", errorId);
            object.put("result",false);
            object.put("message",message.getMessage("E0060", "작업중 오류가 발생하였습니다."));
            return object.toString();
        }

        return object.toString();
    }

    /**
     * 추가 / 수정 페이지에서 테이블 선택에 따른 필드 목록 데이터 획득
     * @param session
     * @param dbkey
     * @param tableName
     * @param model
     * @return
     */
    @RequestMapping(value = "getFieldsByTableName.json", method = RequestMethod.POST)
    @ResponseBody
    public String getFieldsByTableName(HttpSession session, @RequestParam(value = "dbkey", required = false) String dbkey,
                                       @RequestParam(value = "tableName", required = false) String tableName,ModelMap model) {

        JSONObject object = new JSONObject();
        try{
            if(StringUtils.isEmpty(dbkey) || StringUtils.isEmpty(tableName)){
                object.put("result",false);
                object.put("message",message.getMessage("E0199", "데이터베이스 혹은 테이블명이 없습니다."));
                return object.toString();
            }

            ImbDBInfo dbInfo = databaseService.getDBInfoByUkey(dbkey);

            if(dbInfo == null) {
                object.put("result",false);
                object.put("message",message.getMessage("E0200", "데이터 베이스 정보가 없습니다."));
                return object.toString();
            }

            String dbtype = dbInfo.getDbtype();
            String address = dbInfo.getAddress();
            String dbUser = dbInfo.getDbuser();
            String dbPasswd = dbInfo.getDbpasswd();
            Connection con = null;

            try {
                con = databaseService.getDBConnection(dbtype,address,dbUser,dbPasswd);

                ResultSet rs = con.getMetaData().getColumns(null,null,tableName,null);
                JSONArray columnList = new JSONArray();
                while(rs.next()){

                    JSONObject column = new JSONObject();

                    String columnName = rs.getString("COLUMN_NAME");

                    column.put("columnName",StringUtils.lowerCase(columnName));

                    columnList.add(column);
                }
                object.put("result",true);
                object.put("columnList",columnList);
            }catch (NullPointerException ne) {
                String errorId = ErrorTraceLogger.log(ne);
                log.error("{} - DataBase Connection ne ERROR", errorId);
                object.put("result",false);
                object.put("message",message.getMessage("E0190", "데이터베이스 연결에 실패하였습니다."));
            }
            catch (Exception e){
                String errorId = ErrorTraceLogger.log(e);
                log.error("{} - DataBase Connection ERROR", errorId);
                object.put("result",false);
                object.put("message",message.getMessage("E0190", "데이터베이스 연결에 실패하였습니다."));
            }finally {
                if(con != null) con.close();
            }

        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("{} - Get Fields By TableName ne ERROR", errorId);
            object.put("result",false);
            object.put("message",message.getMessage("E0060", "작업중 오류가 발생하였습니다."));
            return object.toString();
        }
        catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - Get Fields By TableName ERROR", errorId);
            object.put("result",false);
            object.put("message",message.getMessage("E0060", "작업중 오류가 발생하였습니다."));
            return object.toString();
        }
        return object.toString();
    }

    /**
     * 메일 작성 페이지에서 수신그룹 선택 시, 필드 목록 데이터 획득
     * @param session
     * @param ukey
     * @param model
     * @return
     */
    @RequestMapping(value = "getFieldsByUkey.json", method = RequestMethod.POST)
    @ResponseBody
    public String getFieldsByUkey(HttpSession session, @RequestParam(value = "ukey", required = false) String ukey, ModelMap model) {

        JSONObject object = new JSONObject();

        try{

            if(StringUtils.isEmpty(ukey)){
                object.put("result",false);
                object.put("message",message.getMessage("E0545", "선택한 수신그룹이 없습니다."));
                return object.toString();
            }

            ImbReceiver receiverInfo = receiverService.getReceiverGroup(ukey);

            if(receiverInfo == null){
                object.put("result",false);
                object.put("message",message.getMessage("E0546", "수신그룹 정보가 없습니다."));
                return object.toString();
            }

            if(StringUtils.isEmpty(receiverInfo.getDbkey())){
                object.put("result",false);
                object.put("message",message.getMessage("E0199", "데이터베이스 혹은 테이블명이 없습니다."));
                return object.toString();
            }
            if(StringUtils.isEmpty(receiverInfo.getQuery())){
                object.put("result",false);
                object.put("message",message.getMessage("E0547", "실행할 쿼리가 없습니다."));
                return object.toString();
            }

            ImbDBInfo dbInfo = databaseService.getDBInfoByUkey(receiverInfo.getDbkey());

            if(dbInfo == null) {
                object.put("result",false);
                object.put("message",message.getMessage("E0200", "데이터 베이스 정보가 없습니다."));
                return object.toString();
            }


            String dbtype = dbInfo.getDbtype();
            String address = dbInfo.getAddress();
            String dbUser = dbInfo.getDbuser();
            String dbPasswd = dbInfo.getDbpasswd();

            Connection con = null;
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            JSONArray fieldList = new JSONArray();

            try{
                con = databaseService.getDBConnection(dbtype,address,dbUser,dbPasswd);
                pstmt = con.prepareStatement(receiverInfo.getQuery());
                rs = pstmt.executeQuery();

                //쿼리 실행 후, 메타데이터 획득
                ResultSetMetaData rsmd = rs.getMetaData();
                int fieldCount = rsmd.getColumnCount();

                for(int i=1;i<=fieldCount;i++){
                    JSONObject field = new JSONObject();

                    String fieldName = rsmd.getColumnName(i);

                    field.put("fieldName",fieldName);
                    fieldList.add(field);
                }

            }catch (NullPointerException ne) {
                object.put("result",false);
                object.put("message",message.getMessage("E0298","데이터베이스와 연결이 되지 않거나 올바르지 않은 쿼리 입니다."));
                return object.toString();
            }
            catch (Exception e){
                object.put("result",false);
                object.put("message",message.getMessage("E0298","데이터베이스와 연결이 되지 않거나 올바르지 않은 쿼리 입니다."));
                return object.toString();
            }finally {
                if(rs != null) rs.close();
                if(pstmt != null) try { pstmt.close(); } catch (Exception e) {}
                if(con !=null) con.close();
            }
            object.put("result",true);
            object.put("fieldList",fieldList);
        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("{} - Get Fields By Receiver Ukey ne ERROR", errorId);
            object.put("result",false);
            object.put("message",message.getMessage("E0060", "작업중 오류가 발생하였습니다."));
            return object.toString();
        }
        catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - Get Fields By Receiver Ukey ERROR", errorId);
            object.put("result",false);
            object.put("message",message.getMessage("E0060", "작업중 오류가 발생하였습니다."));
            return object.toString();
        }
        return object.toString();
    }
    /**
     * 추가창에서 SQL 결과 미리보기
     * @param session
     * @param request
     * @param form
     * @param model
     * @return
     */
    @RequestMapping(value = "previewQuery.do", method = RequestMethod.POST)
    public String previewQueryForReceiverGroupForm(HttpSession session, HttpServletRequest request,
                                                   @ModelAttribute("ReceiverGroupForm") ReceiverGroupForm form, ModelMap model){

        String dbkey = form.getDbkey();
        String query = form.getQuery();

        try{
            //유효성 체크
            boolean isValid = true;
            if(StringUtils.isEmpty(dbkey)){
                model.addAttribute("infoMessage",message.getMessage("E0177","데이터베이스를 선택해 주세요."));
                isValid = false;
            }
            if(StringUtils.isEmpty(query)){
                model.addAttribute("infoMessage",message.getMessage("E0178","쿼리를 입력해 주세요."));
                isValid = false;
            }

            if(!isValid){
                return receiverGroup(request,form,model,true);
            }
            ImbDBInfo dbInfo = databaseService.getDBInfoByUkey(dbkey);
            if(dbInfo == null) {
                model.addAttribute("infoMessage",message.getMessage("E0200","데이터 베이스 정보가 없습니다."));
                return receiverGroup(request,form,model,true);
            }

            String dbtype = dbInfo.getDbtype();
            String address = dbInfo.getAddress();
            String dbUser = dbInfo.getDbuser();
            String dbPasswd = dbInfo.getDbpasswd();
            Connection con = null;
            try{
                con = databaseService.getDBConnection(dbtype,address,dbUser,dbPasswd);
                databaseService.excuteQuery(con,query,model);
            }catch (BadSqlGrammarException be) {
                String errorId = ErrorTraceLogger.log(be);
                log.error("{} - Excute Preview Query be ERROR", errorId);
                model.addAttribute("ErrorMessage",message.getMessage("E0298","데이터베이스와 연결이 되지 않거나 올바르지 않은 쿼리 입니다."));
                if(con !=null){
                    con.close();
                }
            }
            catch (Exception e){
                String errorId = ErrorTraceLogger.log(e);
                log.error("{} - Excute Preview Query ERROR", errorId);
                model.addAttribute("ErrorMessage",message.getMessage("E0298","데이터베이스와 연결이 되지 않거나 올바르지 않은 쿼리 입니다."));
                if(con !=null){
                    con.close();
                }
            }
            con.close();
        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("{} - Preview Query ne ERROR", errorId);
        }
        catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - Preview Query ERROR", errorId);
        }
        return "/popup/receiver/popup_receiverPreview";
    }


    /**
     * 수신그룹 추가/수정 페이지를 표시하는데 꼭 필요한 데이터 GET/POST
     * @param request
     * @param form
     * @param model
     * @param isAdd
     * @return
     */
    public String receiverGroup(HttpServletRequest request, ReceiverGroupForm form, ModelMap model, boolean isAdd){

        try{
            List<ImbDBInfo> dbInfoList = databaseService.getDBInfoList();
            model.addAttribute("DBList",dbInfoList);
        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("{} - Get DBInfo List ERROR", errorId);
        }
        catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - Get DBInfo List ERROR", errorId);
        }

        model.addAttribute("ReceiverGroupForm",form);
        if(isAdd){
            return "/receiver/receiver_add";
        }else {
            return "/receiver/receiver_edit";
        }

    }

    /**
     *  수신그룹 추가/수정을 ajax로 처리하기 위해 생성
     * @param request
     * @param form
     * @param model
     * @param isAdd
     * @return
     */
    public String receiverGroup2(HttpServletRequest request, ReceiverGroupForm form, ModelMap model, boolean isAdd, JSONResult result){

        try{
            List<ImbDBInfo> dbInfoList = databaseService.getDBInfoList();
            model.addAttribute("DBList",dbInfoList);
        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("{} - Get DBInfo List ne ERROR", errorId);
        }
        catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - Get DBInfo List ERROR", errorId);
        }

        model.addAttribute("ReceiverGroupForm",form);
        return result.toString();

    }

    /**
     * 팝업창에 수신그룹을 불러온다.
     * @param model
     * @param type
     * @return
     */
    @RequestMapping(value = "popupReceiver.do")
    public String receiverGroupList(@ModelAttribute("ReceiverGroupListForm") ReceiverGroupListForm form, ModelMap model,
                                    @RequestParam(value="type", required=false) String type, HttpSession session,
                                    HttpServletResponse response){

        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-control", "no-cache");

        //페이지 목록 개수 세션에서 획득
        UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);

        if(StringUtils.isEmpty(type)){
            type = "0";
        }

        try{
            String userid = null;
            if(userSessionInfo.getPermission().equals("U")){
                userid = userSessionInfo.getUserid();
            }
            String srch_type = form.getSrch_type();
            String srch_keyword = form.getSrch_keyword();

            int totalsize =0;
            int cpage = ImStringUtil.parseInt(form.getCpage());
            int pageGroupSize =  ImStringUtil.parseInt(form.getPagegroupsize());

            //검색 결과 수신그룹 수
            totalsize = receiverService.getReceiverGroupCount(srch_type,srch_keyword,userid);
            //페이지 정보
            ImPage pageInfo = new ImPage(cpage, userSessionInfo.getPagesize(), totalsize, pageGroupSize);
            //수신그룹정보 획득
            List<ImbReceiver> receiverGroupList = receiverService.getReceiverGroupForPageing(srch_type,srch_keyword,pageInfo.getStart(), pageInfo.getEnd(),userid);

            /** 수신그룹 관리 팝업은 수신그룹이름, 등록자, 등록일자가 필요함 */
            model.addAttribute("totalsize",totalsize);
            model.addAttribute("pageInfo", pageInfo);
            model.addAttribute("receiverGroupList", receiverGroupList);
            model.addAttribute("opener_type", type);
        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("{} - Get DBInfo List ERROR", errorId);
        }
        catch (Exception e) {
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - Get DBInfo List ERROR", errorId);
        }

        return "/popup/receiver/popup_receiverList";
    }

    /**
     * 팝업창에 주소록 그룹 목록을 불러온다
     * */
    @RequestMapping(value = "popupAddr.do", method = RequestMethod.GET)
    public String addrGroupList(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model){
    	UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
    	String userid = userSessionInfo.getUserid();
    	List<AddrGroupBean> groupList = new ArrayList<>();


    	try{
    		List<ImbAddrGrp> tempGroupList = addressService.getAddressGroupList(userid);

            for(ImbAddrGrp addrGrp : tempGroupList){
                AddrGroupBean addrGroupBean = new AddrGroupBean();
                int addrCount = addressService.getAddressCountByGkey(userid,addrGrp.getGkey()); // 각 그룹 주소록 갯수
                addrGroupBean.setCount(addrCount);
                addrGroupBean.setGname(addrGrp.getGname());
                addrGroupBean.setGkey(addrGrp.getGkey());

                groupList.add(addrGroupBean);
            }

            model.addAttribute("groupList", groupList);
    	}catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("{} - Get Address Group List ne Error", errorId);
        }
    	catch(Exception e){
    		String errorId = ErrorTraceLogger.log(e);
            log.error("{} - Get Address Group List Error", errorId);
    	}
        return "/popup/receiver/popup_addressList";
    }




    /**
     * ukey를 이용한 수신그룹 멤버 페이지 오픈
     * @param request
     * @param session
     * @param model
     * @param ukey
     * @return
     */
    @RequestMapping(value = "preview.do", method = RequestMethod.GET)
    public String receiverGroupPreview(HttpServletRequest request,HttpSession session, ModelMap model, @RequestParam(value = "ukey",required = false) String ukey){

        if(StringUtils.isEmpty(ukey)){
            return "redirect:/error/no-resource.do";
        }

        try{
            ImbReceiver receiverInfo = receiverService.getReceiverGroup(ukey);

            if(receiverInfo == null){
                //TODO 커스텀 에러페이지로 이동, 메세지 추가 필요
                return "redirect:/error/no-resource.do";
            }
            String dbkey = receiverInfo.getDbkey();
            String query = receiverInfo.getQuery();
            ImbDBInfo dbInfo = databaseService.getDBInfoByUkey(dbkey);
            if(dbInfo == null) {
                //TODO 커스텀 에러페이지로 이동, 메세지 추가 필요
                return "redirect:/error/no-resource.do";
            }
            String dbtype = dbInfo.getDbtype();
            String address = dbInfo.getAddress();
            String dbUser = dbInfo.getDbuser();
            String dbPasswd = dbInfo.getDbpasswd();
            Connection con = null;
            try{
                con = databaseService.getDBConnection(dbtype,address,dbUser,dbPasswd);
                databaseService.excuteQuery(con,query,model);
            }catch (NullPointerException ne) {
                String errorId = ErrorTraceLogger.log(ne);
                log.error("{} - Excute Query ne ERROR", errorId);
                model.addAttribute("ErrorMessage",message.getMessage("E0298","데이터베이스와 연결이 되지 않거나 올바르지 않은 쿼리 입니다."));
                if(con !=null){
                    con.close();
                }
            }
            catch (Exception e){
                String errorId = ErrorTraceLogger.log(e);
                log.error("{} - Excute Query ERROR", errorId);
                model.addAttribute("ErrorMessage",message.getMessage("E0298","데이터베이스와 연결이 되지 않거나 올바르지 않은 쿼리 입니다."));
                if(con !=null){
                    con.close();
                }
            }finally {
                if(con !=null){
                    con.close();
                }
            }

        }catch (NullPointerException nee) {
            String errorId = ErrorTraceLogger.log(nee);
            log.error("{} - Receiver Group List Preview Query ne ERROR", errorId);
        }
        catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - Receiver Group List Preview Query ERROR", errorId);
        }

        return "/popup/receiver/popup_receiverPreview";
    }

    /**
     * recid를 이용한 수신그룹 멤버 페이지 오픈
     * @param request
     * @param session
     * @param model
     * @param recid
     * @return
     */
    @RequestMapping(value = "groupDetail.do")
    public String receiverGroupDetail(@ModelAttribute("ReceiverGroupListForm") ReceiverGroupListForm form, HttpServletRequest request,HttpSession session, ModelMap model, @RequestParam(value ="recid" ,required = false) String recid,
                                      HttpServletResponse response) {

        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-control", "no-cache");

        //페이지 목록 개수 세션에서 획득
       UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);

        try{
            ImbReceiver receiverInfo = receiverService.getReceiverGroup(recid);

            if(receiverInfo == null){
                //TODO 커스텀 에러페이지로 이동, 메세지 추가 필요
                return "redirect:/error/no-resource.do";
            }

            String dbkey = receiverInfo.getDbkey();
            String query = receiverInfo.getQuery();
            ImbDBInfo dbInfo = databaseService.getDBInfoByUkey(dbkey);
            if(dbInfo == null) {
                //TODO 커스텀 에러페이지로 이동, 메세지 추가 필요
                return "redirect:/error/no-resource.do";
            }
            String dbtype = dbInfo.getDbtype();
            String address = dbInfo.getAddress();
            String dbUser = dbInfo.getDbuser();
            String dbPasswd = dbInfo.getDbpasswd();
            Connection con = null;
            try{
                con = databaseService.getDBConnection(dbtype,address,dbUser,dbPasswd);
                databaseService.excuteQuery(con,query,model);
            }catch (NullPointerException ne) {
                String errorId = ErrorTraceLogger.log(ne);
                log.error("{} - Excute Query ne ERROR", errorId);
                model.addAttribute("ErrorMessage",message.getMessage("E0298","데이터베이스와 연결이 되지 않거나 올바르지 않은 쿼리 입니다."));
                if(con !=null){
                    con.close();
                }
            }
            catch (Exception e){
                String errorId = ErrorTraceLogger.log(e);
                log.error("{} - Excute Query ERROR", errorId);
                model.addAttribute("ErrorMessage",message.getMessage("E0298","데이터베이스와 연결이 되지 않거나 올바르지 않은 쿼리 입니다."));
                if(con !=null){
                    con.close();
                }
            }finally {
                if(con!= null){
                    con.close();
                }
            }

        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("{} - Receiver Group List Preview Query ne ERROR", errorId);
        }
        catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - Receiver Group List Preview Query ERROR", errorId);
        }
        return "/popup/receiver/popup_receiverDetail";
    }

    @RequestMapping(value="addresslist.do", method= RequestMethod.GET)
    @ResponseBody
    public String AddressGroupList(@ModelAttribute("addressListForm") AddressListForm form, HttpServletRequest request, HttpSession session, ModelMap model){
        UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
        String userid = userSessionInfo.getUserid();

         JSONObject result = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();


        try {
            List<ImbAddrGrp> groupList = null;
            groupList = addressService.getAddressGroupList(userid);

            int size = groupList.size();

            for(int i=0; i < size; i++){
                jsonObject.clear();
                //int addrCount = addressService.getAddressCountByGkey(userid,groupList.get(i).getGkey());
                jsonObject.put("gkey", groupList.get(i).getGkey());
                jsonObject.put("gname", groupList.get(i).getGname());
                jsonObject.put("count", groupList.get(i).getGrpcount());
                jsonArray.add(jsonObject);
            }
            result.put("result", true);
            result.put("groupList", jsonArray);


        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error(" addressList Get List ne Error : {}", errorId);
            result.put("result", false);
            result.put("message", message.getMessage("E0060","작업중 오류가 발생하였습니다."));
            return result.toString();
        }
        catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error(" addressList Get List  Error : {}", errorId);
            result.put("result", false);
            result.put("message", message.getMessage("E0060","작업중 오류가 발생하였습니다."));
            return result.toString();
        }

        return result.toString();

    }



    @RequestMapping(value="grouplist.do", method= RequestMethod.GET)
    @ResponseBody
    public String GroupList(@ModelAttribute("ReceiverGroupListForm") ReceiverGroupListForm form, HttpServletRequest request, HttpSession session, ModelMap model,
                            @RequestParam(value="type", required=false) String type){
        UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
        //String userid = userSessionInfo.getUserid();

        JSONObject result = new JSONObject();


        try {
            String userid = null;
            if(userSessionInfo.getPermission().equals("U")){
                userid = userSessionInfo.getUserid();
            }
            String srch_type = form.getSrch_type();
            String srch_keyword = form.getSrch_keyword();

            int totalsize =0;
            int cpage = ImStringUtil.parseInt(form.getCpage());
            int pageGroupSize =  ImStringUtil.parseInt(form.getPagegroupsize());

            //검색 결과 수신그룹 수
            totalsize = receiverService.getReceiverGroupCount(srch_type,srch_keyword,userid);
            //페이지 정보
             ImPage pageInfo = new ImPage(cpage, userSessionInfo.getPagesize(), totalsize, pageGroupSize);
            //수신그룹정보 획득
            List<ImbReceiver> receiverGroupList = receiverService.getReceiverGroupForPageing(srch_type,srch_keyword,pageInfo.getStart(), pageInfo.getEnd(),userid);
            List<ImbReceiver> receiverGrpList = receiverService.getReceiverList();
            /** 수신그룹 관리 팝업은 수신그룹이름, 등록자, 등록일자가 필요함 */
            result.put("result", true);

            result.put("totalsize",totalsize);
            result.put("pageInfo", pageInfo);
            result.put("receiverGroupList", receiverGroupList);
            result.put("opener_type", type);


            model.addAttribute("totalsize", totalsize);
            model.addAttribute("pageInfo", pageInfo);
            model.addAttribute("receiverGroupList", receiverGroupList);
            model.addAttribute("opener_type", type);



        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error(" addrgroupList Get List ne Error : {}", errorId);
            result.put("result", false);
            result.put("message", message.getMessage("E0060","작업중 오류가 발생하였습니다."));
            return result.toString();
        }
        catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error(" addrgroupList Get List  Error : {}", errorId);
            result.put("result", false);
            result.put("message", message.getMessage("E0060","작업중 오류가 발생하였습니다."));
            return result.toString();
        }

        return result.toString();

    }


    /**
     * msgid를 이용한 주소록 멤버 페이지 오픈
     * @param request
     * @param session
     * @param model
     * @param msgid
     * @return
     */
    @RequestMapping(value = "addrDetail.do")
    public String receiverAddrDetail(@ModelAttribute("ReceiverAddrListForm") ReceiverAddrListForm form, HttpServletRequest request, HttpSession session, ModelMap model, @RequestParam(value ="msgid" ,required = false) String msgid,
                                     HttpServletResponse response) {

        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-control", "no-cache");

        //페이지 목록 개수 세션에서 획득
        UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);

        if(StringUtils.isEmpty(msgid)){
            return "redirect:/error/no-resource.do";
        }
        try {
            int total =0;
            int cpage = ImStringUtil.parseInt(form.getCpage());
            int pageGroupSize =  ImStringUtil.parseInt(form.getPagegroupsize());

            EmsBean emsBean = receiverService.getMsginfo(msgid); // msgid에 해당하는 데이터 호출
//             List<ImbAddrGrp> imbAddrGrp = addressService.getAddressGrpByGname(emsBean.getUserid(),emsBean.getRecname());

             List<RecvMessageIDBean> recvList = sendResultService.getRecvMessageIDForMsgid(msgid);

            // 총 수
            total = recvList.size();

            //페이지 정보
            ImPage pageInfo = new ImPage(cpage, userSessionInfo.getPagesize(), total, pageGroupSize);
            List<RecvMessageIDBean> recvMessageIDBean = null;
            recvMessageIDBean = sendResultService.getRecvListPageingForMsgid2(msgid, pageInfo.getStart(), pageInfo.getEnd());


            if(recvList == null){
                //TODO 커스텀 에러페이지로 이동, 메세지 추가 필요
                return "redirect:/error/no-resource.do";
            }

//            model.addAttribute("imbAddrList", imbAddrList);
            model.addAttribute("recvMessageIDBean", recvMessageIDBean);
            model.addAttribute("totalsize",total);
            model.addAttribute("pageInfo", pageInfo);
            model.addAttribute("msgid", msgid);
        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("{} - Receiver Addr List ne ERROR", errorId);
        }
        catch (Exception e) {
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - Receiver Addr List ERROR", errorId);
        }
        return "/popup/receiver/popup_receiverAddr";
    }

    /**
     * ukey를 이용한 수정페이지 접근 사용자 권한 체크
     * @param session
     * @param ukey
     * @return
     */
    @RequestMapping(value = "checkPermission.json", method = RequestMethod.POST)
    @ResponseBody
    public String checkPermission(HttpSession session, @RequestParam(value = "ukey", required = false) String ukey){
        UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
        JSONResult result = new JSONResult();
        String chk_pm = userSessionInfo.getPermission(); // 권한 확인 A:관리자, U:사용자
        String chk_id = userSessionInfo.getUserid(); // 로그인 유저 id 확인

        try{
            // 수정 페이지 접근을 위해 작성자 체크 및 권한 체크
            ImbReceiver receiverInfo = receiverService.getReceiverGroup(ukey);

            if(!receiverInfo.getUserid().equals(chk_id)){ // 로그인한 유저와 해당 수신그룹 작성자의 아이디가 일치하는지 확인
                if(chk_pm.equals("U")){ // 일치 하지않으면 로그인 유저의 권한 확인
                    result.setResultCode(JSONResult.FAIL);
                    result.setMessage(message.getMessage("E0591","수신그룹은 관리자 또는 작성자만 수정할 수 있습니다."));
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

    /**
     *  ukey를 이용한 컬럼의 개수를 확인
     * @param request
     * @param session
     * @param model
     * @param ukey
     * @return
     */
    @RequestMapping(value = "columnCheck.json", method = RequestMethod.POST)
    @ResponseBody
    public String ColumnCountCheck(HttpServletRequest request,HttpSession session, ModelMap model, @RequestParam(value = "ukey",required = false) String ukey){

        JSONResult result = new JSONResult();

        try {
            ImbReceiver receiverInfo = receiverService.getReceiverGroup(ukey);

            if(receiverInfo == null){
                return "redirect:/error/no-resource.do";
            }
            String dbkey = receiverInfo.getDbkey();
            String query = receiverInfo.getQuery();
            ImbDBInfo dbInfo = databaseService.getDBInfoByUkey(dbkey);
            int count = 0;

            if(dbInfo == null) {
                return "redirect:/error/no-resource.do";
            }
            String dbtype = dbInfo.getDbtype();
            String address = dbInfo.getAddress();
            String dbUser = dbInfo.getDbuser();
            String dbPasswd = dbInfo.getDbpasswd();
            Connection con = null;
            try {
                con = databaseService.getDBConnection(dbtype,address,dbUser,dbPasswd);
                count = databaseService.checkCoulumn(con,query);
                JSONObject data = new JSONObject();
                data.put("count", count);
                result.setResultCode(JSONResult.SUCCESS);
                result.setData(data);

            }catch (NullPointerException ne) {
                String errorId = ErrorTraceLogger.log(ne);
                log.error("{} - Excute Query ne ERROR", errorId);
            }
            catch (Exception e){
                String errorId = ErrorTraceLogger.log(e);
                log.error("{} - Excute Query ERROR", errorId);
            }

        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("{} - Receiver Group List Preview Query Check Column Count ne ERROR", errorId);
        }
        catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - Receiver Group List Preview Query Check Column Count ERROR", errorId);
        }

        return result.toString();
    }

}

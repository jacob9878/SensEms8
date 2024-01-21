package com.imoxion.sensems.web.controller;

import com.imoxion.common.util.ImStringUtil;
import com.imoxion.common.util.ImUtils;
import com.imoxion.security.ImSecurityLib;
import com.imoxion.sensems.common.logger.ErrorTraceLogger;
import com.imoxion.sensems.web.beans.AddrGroupBean;
import com.imoxion.sensems.web.beans.UserInfoBean;
import com.imoxion.sensems.web.common.CommonFile;
import com.imoxion.sensems.web.common.ImPage;
import com.imoxion.sensems.web.common.ImbConstant;
import com.imoxion.sensems.web.database.domain.ImbAddr;
import com.imoxion.sensems.web.database.domain.ImbAddrGrp;
import com.imoxion.sensems.web.form.AddressForm;
import com.imoxion.sensems.web.form.AddressImportForm;
import com.imoxion.sensems.web.form.AddressImportResultForm;
import com.imoxion.sensems.web.form.AddressListForm;
import com.imoxion.sensems.web.service.ActionLogService;
import com.imoxion.sensems.web.service.AddressService;
import com.imoxion.sensems.web.util.*;
import net.sf.json.JSONObject;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 개인 주소록 Controller
 * @date 2021.03.11
 * @author jhpark
 *
 */
@Controller
@RequestMapping("receiver/address")
public class AddressController {

    protected Logger log = LoggerFactory.getLogger(AddressController.class);

    @Autowired
    private MessageSourceAccessor message;

    @Autowired
    private AddressService addressService;

    @Autowired
    private ActionLogService actionLogService;


    /**
     * 사용자 검색 및 목록 획득
     * @param form
     * @param session
     * @param model
     * @return
     */
    @RequestMapping(value="list.do")
    public String addrList(@ModelAttribute("AddressListForm") AddressListForm form,HttpServletRequest request, HttpSession session, ModelMap model,
                           HttpServletResponse response){

        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-control", "no-cache");
        //페이지 목록 개수 세션에서 획득
        UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
        List<AddrGroupBean> grpBeanList = new ArrayList<>();

        try{
            String userid = userSessionInfo.getUserid();
            String srch_type = form.getSrch_type();
            String srch_keyword = form.getSrch_keyword();

            int gkey = ImStringUtil.parseInt(form.getGkey());
            int searchCount =0;
            int cpage = ImStringUtil.parseInt(form.getCpage());
            model.addAttribute("cpage", cpage);
            int pageGroupSize =  ImStringUtil.parseInt(form.getPagegroupsize());

            // AddressListForm에 수정된 값으로 넣어줘야 함
            form.setGkey(String.valueOf(gkey));
            form.setCpage(String.valueOf(cpage));
            form.setPagegroupsize(String.valueOf(pageGroupSize));

            boolean issearch = true;
            if(StringUtils.isEmpty(srch_keyword)) {
                issearch = false;
            }
            model.addAttribute("issearch", issearch);


            if(StringUtils.isNotEmpty(srch_type) && "email".equals(srch_type.toLowerCase()) && StringUtils.isNotEmpty(srch_keyword)){
                if(ImbConstant.DATABASE_ENCRYPTION_USE){
                    String secret_key = ImbConstant.DATABASE_AES_KEY;
                    srch_keyword = ImSecurityLib.encryptAES256(secret_key,srch_keyword);
                }
            }

            //검색 결과 사용자 수
            searchCount = addressService.getAddressCountForSearch(srch_type,srch_keyword, userid, gkey);
            //페이지 정보
            ImPage pageInfo = new ImPage(cpage, userSessionInfo.getPagesize(), searchCount, pageGroupSize);
            //유저정보 획득
            List<ImbAddr> addrList = addressService.getAddressListForPageing(srch_type,srch_keyword,userid,gkey,pageInfo.getStart(), pageInfo.getEnd());

            List<ImbAddrGrp> addrGrpList = addressService.getAddressGroupList(userid);
            String gname = addressService.getGname(userid, gkey);

            int totalCount = addressService.getAddressCountByGkey(userid, -1);// 전체 주소록 갯수
            int defaultCount = addressService.getAddressCountByGkey(userid, 0);// 미분류 주소록 갯수

            for(ImbAddrGrp addrGrp : addrGrpList){
                AddrGroupBean addrGroupBean = new AddrGroupBean();
                //int addrCount = addressService.getAddressCountByGkey(userid,addrGrp.getGkey()); // 각 그룹 주소록 갯수
                addrGroupBean.setCount(addrGrp.getGrpcount());
                addrGroupBean.setGname(addrGrp.getGname());
                addrGroupBean.setGkey(addrGrp.getGkey());

                grpBeanList.add(addrGroupBean);
            }
            model.addAttribute("gname", gname);
            model.addAttribute("form", form);
            model.addAttribute("totalCount", totalCount);
            model.addAttribute("defaultCount", defaultCount);
            model.addAttribute("pageInfo", pageInfo);
            model.addAttribute("addrList", addrList);
            model.addAttribute("addrGrpList",grpBeanList);

            //log insert start
            // TODO 로그정책 변경으로 삭제 (개인주소록>조회 E201)

        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("{} - Address LIST ERROR", errorId);
        }
        catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - Address LIST ERROR", errorId);
        }
        model.addAttribute("AddressListForm",form);

        return "/receiver/address_list";
    }


    /**
     * 주소록 추가 페이지로 이동
     * @param form
     * @param session
     * @param model
     * @return
     */
    @RequestMapping(value="add.do",method = RequestMethod.GET)
    public String addrAddPage(@ModelAttribute("AddressForm") AddressForm form,HttpServletRequest request ,HttpSession session, ModelMap model){

        try{
            UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
            String userid = userSessionInfo.getUserid();
            String gkey = form.getGkey();
            String cpage = request.getParameter("cpage");
            model.addAttribute("cpage", cpage);

            List<ImbAddrGrp> addrGrpList = addressService.getAddressGroupList(userid);

            form.setGkey(gkey); // 주소록 추가 시 주소록 그룹 기본 값 지정

            model.addAttribute("AddressForm",form);
            model.addAttribute("addrGrpList",addrGrpList);

        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("{} - Address Add Page ERROR", errorId);
        }
        catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - Address Add Page ERROR", errorId);
        }

        return "/receiver/address_add";
    }

    /**
     * 주소록 추가 동작
     * @param form
     * @param session
     * @param model
     * @return
     */
    @RequestMapping(value="add.do",method = RequestMethod.POST)
    public String addrAdd(@ModelAttribute("AddressForm") AddressForm form,HttpServletRequest request, HttpSession session, ModelMap model){

        try{
            UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
            String userid = userSessionInfo.getUserid();

            model.addAttribute("AddressForm",form);
            //유효성 체크 실시
            boolean isValid = addressService.isValidate(form,model,userid,true);
            if(!isValid){
                List<ImbAddrGrp> addrGrpList = addressService.getAddressGroupList(userid);
                model.addAttribute("addrGrpList",addrGrpList);
                return "/receiver/address_add";
            }

            ImbAddr addr = addressService.convertToImbAddr(form);

            addr.setRegdate(new Date());

            if(ImbConstant.DATABASE_ENCRYPTION_USE){
                addressService.encryptAddr(addr);
            }

            addressService.insertAddress(addr,userid);

            //log insert start
            // TODO 로그정책 변경으로 삭제 (개인주소록>추가 E202)

        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("{} - Address Add ERROR", errorId);
        }
        catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - Address Add ERROR", errorId);
        }
        String gkey = form.getGkey();
        String url = "/receiver/address/list.do?cpage=1&gkey=" + gkey + "&srch_type=name&srch_keyword=";

        return "redirect:" + url;
    }

    /**
     * 주소록 수정 페이지로 이동
     * @param ukey
     * @param session
     * @param model
     * @return
     */
    @RequestMapping(value="edit.do",method = RequestMethod.GET)
    public String addrEditPage(@RequestParam("ukey")String ukey,HttpServletRequest request, HttpSession session, ModelMap model){

        if(StringUtils.isEmpty(ukey)){
            return "redirect:/receiver/address/list.do";
        }
        int key = ImStringUtil.parseInt(ukey);
        try{
            UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
            String userid = userSessionInfo.getUserid();

            ImbAddr addr = addressService.getAddressByUkey(userid,key);

            AddressForm form = addressService.convertToAddressForm(addr);

            List<ImbAddrGrp> addrGrpList = addressService.getAddressGroupList(userid);
            String cpage = request.getParameter("cpage");
            model.addAttribute("cpage", cpage);

//            form.setCpage(cpage);

            model.addAttribute("AddressForm",form);
            model.addAttribute("addrGrpList",addrGrpList);


        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("{} - Address Edit Page ERROR", errorId);
        }
        catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - Address Edit Page ERROR", errorId);
        }

        return "/receiver/address_edit";
    }

    /**
     * 주소록 수정 동작
     * @param form
     * @param session
     * @param model
     * @return
     */
    @RequestMapping(value="edit.do",method = RequestMethod.POST)
    public String addrEdit(@ModelAttribute("AddressForm") AddressForm form,HttpServletRequest request, HttpSession session, ModelMap model){

        try{
            UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
            String userid = userSessionInfo.getUserid();

            model.addAttribute("AddressForm",form);
            //유효성 체크 실시
            //boolean isValid = addressService.isValidate(form,model,userid,false);
            boolean isValid = addressService.editIsValidate(form,model,userid,false);
            if(!isValid){
                List<ImbAddrGrp> addrGrpList = addressService.getAddressGroupList(userid);
                model.addAttribute("addrGrpList",addrGrpList);
                return "/receiver/address_edit";
            }

            ImbAddr addr = addressService.convertToImbAddr(form);

            if(ImbConstant.DATABASE_ENCRYPTION_USE) {
                addressService.encryptAddr(addr);
            }
            addressService.updateAddress(addr,userid);

            //log insert start
            // TODO 로그정책 변경으로 삭제 (개인주소록>수정 E203)

        }catch ( NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("{} - Address Edit ERROR", errorId);
        }
        catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - Address Edit ERROR", errorId);
        }
        ImbAddr addr = addressService.convertToImbAddr(form);
        int gkey =addr.getGkey();
        String url =   "/receiver/address/list.do?cpage="+form.getCpage()+"&gkey="+gkey+"&srch_type=name&srch_keyword=";


        return "redirect:"+url;
    }


    /**
     * 그룹 추가/수정 팝업 열때 주소록 데이터 획득
     * @param session
     * @param gkey
     * @param model
     * @return
     */
    @RequestMapping(value = "addrGrpPopupData.json", method = RequestMethod.POST)
    @ResponseBody
    public String addrGrpPopupData(HttpSession session, @RequestParam(value = "gkey", required = false) String gkey, ModelMap model){

        JSONObject object = new JSONObject();
        try{
            UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
            String userid = userSessionInfo.getUserid();

            //수정일 경우 gkey 값이 존재한다.
            if(StringUtils.isNotEmpty(gkey)){
                //gkey에 해당하는 주소록 데이터 획득
                int group_key = ImStringUtil.parseInt(gkey);
                ImbAddrGrp grpInfo = addressService.getAddressGrpByGkey(userid,group_key);
                if(grpInfo == null){
                    object.put("result",false);
                    object.put("message",message.getMessage("E0323", "해당하는 주소록 그룹 정보가 없습니다."));
                    return object.toString();
                }
                object.put("grpInfo",grpInfo);
            }

        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("{} - Open Address Group Popup ERROR", errorId);
            object.put("result",false);
            object.put("message",message.getMessage("E0060", "작업중 오류가 발생하였습니다."));
            return object.toString();
        }
        catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - Open Address Group Popup ERROR", errorId);
            object.put("result",false);
            object.put("message",message.getMessage("E0060", "작업중 오류가 발생하였습니다."));
            return object.toString();
        }

        object.put("result",true);
        return object.toString();
    }


    /**
     * 그룹 정보 추가 / 수정
     * @param session
     * @param gkey - null이면 추가 / 있으면 수정
     * @param gname
     * @param memo
     * @param model
     * @return
     */
    @RequestMapping(value = "saveAddressGrpInfo.json", method = RequestMethod.POST)
    @ResponseBody
    public String saveAddressGrpInfo(HttpServletRequest request, HttpSession session, @RequestParam(value = "gkey", required = false) String gkey,
                                     @RequestParam(value = "gname", required = false) String gname,
                                     @RequestParam(value = "memo", required = false) String memo, ModelMap model){

        JSONObject object = new JSONObject();

        // gkey가 있을 경우 수정동작, 없을 경우 추가 동작
        try{
            UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
            String userid = userSessionInfo.getUserid();

            if(StringUtils.isEmpty(gname)){
                object.put("result",false);
                object.put("message",message.getMessage("E0337", "주소록 그룹명을 입력해 주세요."));
                return object.toString();
            }

            //주소록명 특수문자 체크
            boolean check = ImUtility.validCharacter(gname);
            if(!check){
                object.put("result",false);
                object.put("message",message.getMessage("E0737", "사용할 수 없는 특수문자가 포함되어 있습니다. (사용가능한 특수 문자 : (_) , (,) , (-) )"));
                return object.toString();
            }

            //설명(메모) 특수문자 체크
            check = ImUtility.validCharacter(memo);
            if(!check){
                object.put("result",false);
                object.put("message",message.getMessage("E0737", "사용할 수 없는 특수문자가 포함되어 있습니다. (사용가능한 특수 문자 : (_) , (,) , (-) )"));
                return object.toString();
            }

            //주소록명 중복 체크
            boolean isExistGname = addressService.isExistGname(gname,userid);

            if(StringUtils.isEmpty(gkey)){ // 추가 동작
                if(isExistGname){
                    object.put("result",false);
                    object.put("message",message.getMessage("E0482", "이미 존재하는 주소록 그룹명입니다."));
                    return object.toString();
                }

                ImbAddrGrp addrGrp = new ImbAddrGrp();

                addrGrp.setGname(gname);
                addrGrp.setMemo(memo);

                addressService.insertAddressGrp(addrGrp,userid);

                //log insert start
                // TODO 로그정책 변경으로 삭제 (개인주소록>그룹추가 E206)

            }else{ //수정 동작
                int group_key = ImStringUtil.parseInt(gkey);
                ImbAddrGrp addrGrp = addressService.getAddressGrpByGkey(userid,group_key);
                if(addrGrp == null){
                    object.put("result",false);
                    object.put("message",message.getMessage("E0323", "해당하는 주소록 그룹 정보가 없습니다."));
                    return object.toString();
                }
                if(!gname.equalsIgnoreCase(addrGrp.getGname()) && isExistGname){ //기존 주소록명과 동일하지 않고 이미 중복하는 이름 일때 수정할 수 없음
                    object.put("result",false);
                    object.put("message",message.getMessage("E0482", "이미 존재하는 주소록 그룹명입니다."));
                    return object.toString();
                }
                addrGrp.setGname(gname);
                addrGrp.setMemo(memo);
                addressService.updateAddressGrp(addrGrp,userid);

                //log insert start
                // TODO 로그정책 변경으로 삭제 (개인주소록>그룹수정 E207)

            }

        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("{} - Save Address Group ERROR", errorId);
            object.put("result",false);
            object.put("message",message.getMessage("E0060", "작업중 오류가 발생하였습니다."));
            return object.toString();
        }
        catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - Save Address Group ERROR", errorId);
            object.put("result",false);
            object.put("message",message.getMessage("E0060", "작업중 오류가 발생하였습니다."));
            return object.toString();
        }

        object.put("result",true);
        object.put("message",message.getMessage("E0339", "저장되었습니다."));
        return object.toString();
    }

    /**
     * 삭제 옵션에 따라 그룹을 삭제한다.
     * @param session
     * @param gkey
     * @param delOpt
     * @param model
     * @return
     */
    @RequestMapping(value = "deleteAddrGrp.json", method = RequestMethod.POST)
    @ResponseBody
    public String saveAddressGrpInfo(HttpServletRequest request, HttpSession session, @RequestParam(value = "gkey", required = false) String gkey,
                                     @RequestParam(value = "delOpt", required = false) String delOpt, ModelMap model){
        JSONObject object = new JSONObject();

        if(StringUtils.isEmpty(gkey)){
            object.put("result",false);
            object.put("message",message.getMessage("E0340", "주소록 그룹을 선택해 주세요."));
            return object.toString();
        }
        if(StringUtils.isEmpty(delOpt)){
            object.put("result",false);
            object.put("message",message.getMessage("E0341", "삭제 옵션을 선택해 주세요."));
            return object.toString();
        }

        try{
            UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
            String userid = userSessionInfo.getUserid();
            int group_key = ImStringUtil.parseInt(gkey);

            List<ImbAddr> addrList = null;
            if(group_key == -1) {
                //전체 주소록 데이터 획득
                addrList = addressService.getAllAddressList(userid);
            }else {
                //선택한 주소록 데이터 획득
                addrList = addressService.getAddressListByGkey(userid,group_key);
            }


            if(addrList == null){
                object.put("result",false);
                object.put("message",message.getMessage("E0323", "해당하는 주소록 그룹 정보가 없습니다."));
                return object.toString();
            }

            if(delOpt.equals(ImbAddrGrp.DEL_ONLY_GROUP)){
                // 그룹만 삭제 선택 시, 데이터는 미분류로 이동
                for(ImbAddr addr : addrList){
                    addressService.encryptAddr(addr);
                    addr.setGkey(0);
                    addressService.updateAddress(addr,userid);
                }
                //그룹 삭제 실시
                addressService.deleteAddrGrpByGkey(userid,group_key);
            }else if(delOpt.equals(ImbAddrGrp.DEL_ONLY_ADDR)){
                //데이터만 삭제 선택 시 동작
                addressService.deleteAddrByGkey(userid,group_key);
            }else if(delOpt.equals(ImbAddrGrp.DEL_GROUP_ADDR)){
                //데이터 삭제
                addressService.deleteAddrByGkey(userid,group_key);
                //그룹 삭제
                addressService.deleteAddrGrpByGkey(userid,group_key);
            }

            //log insert start
            // TODO 로그정책 변경으로 삭제 (개인주소록>그룹삭제 E208)


        }catch ( NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("{} - Delete Address Group ERROR", errorId);
            object.put("result",false);
            object.put("message",message.getMessage("E0060", "작업중 오류가 발생하였습니다."));
            return object.toString();
        }
        catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - Delete Address Group ERROR", errorId);
            object.put("result",false);
            object.put("message",message.getMessage("E0060", "작업중 오류가 발생하였습니다."));
            return object.toString();
        }
        object.put("result",true);
        object.put("message",message.getMessage("E0070", "삭제되었습니다."));
        return object.toString();

    }

    /**
     * 주소록 삭제 동작
     * @param session
     * @param ukeys
     * @param model
     * @return
     */
    @RequestMapping(value = "deleteAddress.json", method = RequestMethod.POST)
    @ResponseBody
    public String AddressDelete(HttpServletRequest request, HttpSession session, @RequestParam(value = "ukeys", required = false) String[] ukeys, ModelMap model)  {
        JSONResult result = new JSONResult();
        UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
        String userid = userSessionInfo.getUserid();
        try{
            if(ukeys == null || ukeys.length==0){
                result.setResultCode(JSONResult.FAIL);
                result.setMessage(message.getMessage("E0360","삭제할 주소록을 선택해주세요."));
                return result.toString();
            }

            String logParam = "";
            for(int i=0;i<ukeys.length;i++){
                int ukey = ImStringUtil.parseInt(ukeys[i]);
                addressService.deleteAddrByUkey(userid,ukey);


                if(i==0){
                    logParam= ukeys[i];
                }else{
                    logParam += ", "+ukeys[i];
                }
            }
            //log insert start
            // TODO 로그정책 변경으로 삭제 (개인주소록>삭제 E204)

        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("{} - Address Delete ERROR", errorId);
            result.setResultCode(JSONResult.FAIL);
            result.setMessage(message.getMessage("E0060","작업중 오류가 발생하였습니다."));
            return result.toString();
        }
        catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - Address Delete ERROR", errorId);
            result.setResultCode(JSONResult.FAIL);
            result.setMessage(message.getMessage("E0060","작업중 오류가 발생하였습니다."));
            return result.toString();
        }
        result.setResultCode(JSONResult.SUCCESS);
        result.setMessage(message.getMessage("E0070","삭제되었습니다."));
        return result.toString();
    }

    /**
     * 주소록 이동 동작
     * @param session
     * @param ukeys
     * @param model
     * @return
     */
    @RequestMapping(value = "moveAddress.json", method = RequestMethod.POST)
    @ResponseBody
    public String AddressMove(HttpServletRequest request, HttpSession session, @RequestParam(value = "ukeys", required = false) String[] ukeys,
                              @RequestParam(value = "gkey", required = false) String gkey, ModelMap model)  {

        JSONResult result = new JSONResult();
        UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
        String userid = userSessionInfo.getUserid();
        int fail_count = 0;
        try{
            if(ukeys == null || ukeys.length==0){
                result.setResultCode(JSONResult.FAIL);
                result.setMessage(message.getMessage("E0483","이동시킬 주소록을 선택해주세요."));
                return result.toString();
            }
            if(StringUtils.isEmpty(gkey)){
                result.setResultCode(JSONResult.FAIL);
                result.setMessage(message.getMessage("E0340","주소록 그룹을 선택해 주세요."));
                return result.toString();
            }
            String logParam = "";
            for(int i=0;i<ukeys.length;i++){
                int ukey = ImStringUtil.parseInt(ukeys[i]);
                int group_key = ImStringUtil.parseInt(gkey);
                ImbAddr addr = addressService.getAddressByUkey(userid,ukey);
                if(addr == null){
                    fail_count++;
                    continue;
                }else {
                    //본인 그룹에 중복된 이메일이 존재하는 경우 이동 불가
                    String tempEmail = addr.getEmail();
                    if(ImbConstant.DATABASE_ENCRYPTION_USE){
                        String secret_key = ImbConstant.DATABASE_AES_KEY;
                        tempEmail = ImSecurityLib.encryptAES256(secret_key,tempEmail);
                    }
                    int count = addressService.getAddressCountByEmailAndGkey(tempEmail,group_key,userid);
                    if(count > 0){
                        fail_count++;
                        continue;
                    }
                }
                addr.setGkey(group_key);

                if(ImbConstant.DATABASE_ENCRYPTION_USE) {
                    addressService.encryptAddr(addr);
                }
                addressService.updateAddress(addr,userid);
                if(logParam.isEmpty()){
                    logParam = ukeys[i];
                }else{
                    logParam += ", " + ukeys[i];
                }
            }
            //log insert start
            // TODO 로그정책 변경으로 삭제 (개인주소록>이동 E205)

        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("{} - Address Move ERROR", errorId);
            result.setResultCode(JSONResult.FAIL);
            result.setMessage(message.getMessage("E0060","작업중 오류가 발생하였습니다."));
            return result.toString();
        }
        catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - Address Move ERROR", errorId);
            result.setResultCode(JSONResult.FAIL);
            result.setMessage(message.getMessage("E0060","작업중 오류가 발생하였습니다."));
            return result.toString();
        }
        result.setResultCode(JSONResult.SUCCESS);
        if(ukeys.length==fail_count){
            result.setMessage(message.getMessage("E0512","이동불가합니다."));
        }else if(fail_count > 0){
            result.setMessage(message.getMessage("E0511",new Object[]{ukeys.length,ukeys.length-fail_count},"이동 불가한 주소록 제외 후 {0}건 중 {1}건 이동되었습니다."));
        }else{
            result.setMessage(message.getMessage("E0484","이동되었습니다."));
        }
        return result.toString();
    }

    /**
     * 현재 페이지 주소록 목록 다운로드
     * @param form
     * @param session
     * @return
     */
    @RequestMapping(value = "saveList.do" , method = RequestMethod.GET)
    public ModelAndView saveAddrList(@ModelAttribute("AddressListForm") AddressListForm form,HttpServletRequest request, HttpSession session) {
        UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
        String userid = userSessionInfo.getUserid();

        String srch_type = form.getSrch_type();
        String srch_keyword = form.getSrch_keyword();

        int gkey = ImStringUtil.parseInt(form.getGkey());
        int searchCount =0;
        int cpage = ImStringUtil.parseInt(form.getCpage());
        int pageGroupSize =  ImStringUtil.parseInt(form.getPagegroupsize());

        String tempFileName = CommonFile.getFilePath(ImbConstant.TEMPFILE_PATH, ImUtils.makeKeyNum(24) + ".xlsx");
        String fileName = userid + "_Address_List.xlsx";

        File file = new File(tempFileName);

        try {
            if(StringUtils.isNotEmpty(srch_type) && "email".equals(srch_type.toLowerCase()) && StringUtils.isNotEmpty(srch_keyword)){
                if(ImbConstant.DATABASE_ENCRYPTION_USE){
                    String secret_key = ImbConstant.DATABASE_AES_KEY;
                    srch_keyword = ImSecurityLib.encryptAES256(secret_key,srch_keyword);
                }
            }

            //검색 결과 사용자 수
            searchCount = addressService.getAddressCountForSearch(srch_type,srch_keyword, userid, gkey);
            //페이지 정보
            ImPage pageInfo = new ImPage(cpage, userSessionInfo.getPagesize(), searchCount, pageGroupSize);
            //유저정보 획득
            List<ImbAddr> addrList = addressService.getAddressListForPageing(srch_type,srch_keyword,userid,gkey,pageInfo.getStart(), searchCount);

            addressService.getXlsxDownload(addrList, tempFileName, userid);

            //log insert start
            // TODO 로그정책 변경으로 삭제 (개인주소록>목록저장 E209)

        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("Address List Download ne Error", errorId);
        }
        catch(Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("Address List Download Error", errorId);
        }

        return CommonFile.getDownloadView(file, fileName);
    }

    /**
     * 주소록 그룹 멤버 다운로드
     * @param gkey
     * @param session
     * @return
     */
    @RequestMapping(value = "saveGrp.do" , method = RequestMethod.GET)
    public ModelAndView saveAddrGrp(@RequestParam("gkey") String gkey,HttpServletRequest request, HttpSession session) {
        UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
        String userid = userSessionInfo.getUserid();
        if(StringUtils.isEmpty(gkey)){
            String msg = message.getMessage("E0340","주소록 그룹을 선택해 주세요.");
            return AlertMessageUtil.getMessageViewOfScript("alert('"+ msg +"')");
        }
        String tempFileName = CommonFile.getFilePath(ImbConstant.TEMPFILE_PATH, ImUtils.makeKeyNum(24) + ".xlsx");
        String fileName = userid + "_Address_List.xlsx";

        File file = new File(tempFileName);



        try {
            int group_key = ImStringUtil.parseInt(gkey);
            List<ImbAddr> addrList = null;
            if(group_key == -1){
                //전체 주소록 목록 획득
                addrList = addressService.getAllAddressList(userid);
            }else {
                //선택된 주소록 목록 획득
                addrList = addressService.getAddressListByGkey(userid,group_key);
            }

            addressService.getXlsxDownload(addrList, tempFileName, userid);

            //log insert start
            // TODO 로그정책 변경으로 삭제 (개인주소록>그룹저장 E210)

        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("Address Grp Download Error", errorId);
        }
        catch(Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("Address Grp Download Error", errorId);
        }

        return CommonFile.getDownloadView(file, fileName);
    }


    /**
     * 파일로 가져오기 1단계
     * 팝업창 열기
     */
    @RequestMapping(value = "doImport1.do",method = RequestMethod.GET)
    public String dbImport1(HttpSession session, ModelMap model) {

        return "/popup/receiver/popup_address_import1";
    }

    /**
     * 샘플파일 다운로드 처리를 행한다.
     * @param request
     * @param response
     */
    @RequestMapping(value = "sampleDownload.do",method = RequestMethod.GET)
    public ModelAndView sampleDownload(HttpSession session, HttpServletRequest request, HttpServletResponse response, ModelMap model) {

        String savePath = ImbConstant.SENSDATA_PATH + File.separator + "user_data" + File.separator + "address" + File.separator;

        String fpath = savePath + "uploadSample.csv";

        File file = new File(fpath);
        if (!file.exists()) {
            // 파일이 존재하지 않을 경우 에러처리
            log.error("Argument is {}", file.toString());
            String msg = message.getMessage("E0430","파일이 존재하지 않습니다.");
            return AlertMessageUtil.getMessageViewOfScript("alert('"+ msg +"')");
        }

        return CommonFile.getDownloadView(file, "uploadSample.csv");
    }
    /**
     * @Method Comment : 파일로 가져오기 1단계에서 파일 업로드 시 실행
     * @return
     */
    @RequestMapping(value = "doImport2.do",method = RequestMethod.POST)
    public String dbImport2(MultipartHttpServletRequest request, HttpSession session, ModelMap model) {
        UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
        String userid = userSessionInfo.getUserid();
        try{
            MultipartFile file = request.getFile("im_file");

            if(file == null || file.isEmpty()) {
                model.addAttribute("infoMessage",message.getMessage("E0113","파일을 선택해 주세요."));
                return "/popup/receiver/popup_address_import1";
            }

            String orgFileName = file.getOriginalFilename();
            if(orgFileName.lastIndexOf('.')==-1){
                model.addAttribute("infoMessage",message.getMessage("E0442","파일의 확장자가 존재하지 않습니다."));
                return "/popup/receiver/popup_address_import1";
            }

            //MultipartFile을 File 형식으로 변환
            String name = ImEncUtil.getInstance().replaceAll(orgFileName);
            File tempAddrFile = new File(name);
            file.transferTo(tempAddrFile);

            //확장자 체크
            String extension = ImStringUtil.substr(orgFileName,orgFileName.lastIndexOf('.'));
            extension = extension.toLowerCase();
            if(!extension.equals(".csv") && !extension.equals(".txt")){
                model.addAttribute("infoMessage",message.getMessage("E0443","추가할 수 없는 파일 확장자 입니다."));
                return "/popup/receiver/popup_address_import1";
            }

            String addrFile_path = ImbConstant.TEMPFILE_PATH;

            // 저장될 고유 키 생성
            String fileKey = ImUtils.makeKeyNum(24);
            String fileName= userid + "_" + fileKey;

            File saveFile = new File(addrFile_path + File.separator + fileName);

            // 어떤 환경에서도 파일이 깨지지 않도록 UTF8 형식으로 변환한다.
            FileCharsetUtil.converterUTF8( tempAddrFile, saveFile);

            tempAddrFile.delete();
            model.addAttribute("fileKey", fileKey);
        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("{} - Import Address File Error", errorId);
            model.addAttribute("infoMessage",message.getMessage("E0444","파일 업로드 중 에러가 발생하였습니다."));
            return "/popup/receiver/popup_address_import1";
        }
        catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - Import Address File Error", errorId);
            model.addAttribute("infoMessage",message.getMessage("E0444","파일 업로드 중 에러가 발생하였습니다."));
            return "/popup/receiver/popup_address_import1";
        }

        return "/popup/receiver/popup_address_import2";
    }

    /**
     * 파일로 가져오기 2단계의 미리 보기
     * @param fileKey
     * @param div - 값이 없을 시 defaultValue = 0
     * @param header - 값이 없을 시 defaultValue = 0
     * @param session
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "import/preview.json")
    public String doImportPreview(@RequestParam(value = "fileKey", required = false) String fileKey,
                                  @RequestParam(value = "div", defaultValue = "0", required = false) String div,
                                  @RequestParam(value = "header", defaultValue = "0", required = false) String header,
                                  HttpSession session) {

        UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
        String userid = userSessionInfo.getUserid();
        JSONObject object = new JSONObject();

        if(StringUtils.isEmpty(fileKey)){
            object.put("result",false);
            object.put("message",message.getMessage("E0445", "파일 미리보기 중 오류가 발생하였습니다."));
            return object.toString();
        }

        try {
            String previewHtml = addressService.importAddressFilePreview(fileKey, userid, div, header);

            object.put("result",true);
            object.put("previewHtml",previewHtml);
        }catch ( NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("{} - importAddressFilePreview ERROR",errorId);
            object.put("result",false);
            object.put("message",message.getMessage("E0445", "파일 미리보기 중 오류가 발생하였습니다."));
            return object.toString();
        }
        catch (Exception e) {
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - importAddressFilePreview ERROR",errorId);
            object.put("result",false);
            object.put("message",message.getMessage("E0445", "파일 미리보기 중 오류가 발생하였습니다."));
            return object.toString();

        }

        return object.toString();
    }

    /**
     * 파일로 가져오기 3단계
     * @param form
     * @param request
     * @param session
     * @param model
     */
    @RequestMapping(value = "doImport3.do",method = RequestMethod.POST)
    public String dbImport3(@ModelAttribute("importForm") AddressImportForm form, HttpServletRequest request, HttpSession session, ModelMap model) {

       UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
       String userid = userSessionInfo.getUserid();

        List<ImbAddrGrp> addrGrpList = null;
        try {
            form = addressService.importAddressFileSetting(form,userid);

            // 그룹 목록 취득
            addrGrpList = addressService.getAddressGroupList(userid);

        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("{} - getAddressGroupList ERROR",errorId);
            model.addAttribute("infoMessage",message.getMessage("E0060","작업중 오류가 발생하였습니다."));
            return "/popup/receiver/popup_address_import1";
        }
        catch (Exception e) {
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - getAddressGroupList ERROR",errorId);
            model.addAttribute("infoMessage",message.getMessage("E0060","작업중 오류가 발생하였습니다."));
            return "/popup/receiver/popup_address_import1";
        }

        model.addAttribute("importForm", form);
        model.addAttribute("addrGrpList", addrGrpList);
        return "/popup/receiver/popup_address_import3";
    }

    /**
     * 파일로 가져오기 3단계에서 2단계로 다시 넘어가기 위한 Controller
     * @param fileKey
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "doImportPrev.do",method = RequestMethod.POST)
    public String dbImportPrev(@RequestParam(value = "fileKey",required = false) String fileKey, ModelMap model) {

        if(StringUtils.isEmpty(fileKey)){
            log.error("FileKey is Not Exist - Redirect Addr Import First Page");
            model.addAttribute("infoMessage",message.getMessage("E0113","파일을 선택해 주세요."));
            return "/popup/receiver/popup_address_import1";
        }

        model.addAttribute("fileKey", fileKey);
        return "/popup/receiver/popup_address_import2";
    }

    /**
     * 파일로 가져오기 마지막 단계 로직 수행
     * @param form
     * @param request
     * @param session
     * @param model
     */
    @RequestMapping(value = "doImportFinish.do",method = RequestMethod.POST)
    public String dbImportFinish(@ModelAttribute("importForm") AddressImportForm form, HttpServletRequest request, HttpSession session, ModelMap model){
        UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
        String userid = userSessionInfo.getUserid();

        if(StringUtils.isEmpty(form.getFileKey())){
            log.error("FileKey is Not Exist - Redirect Addr Import First Page");
            model.addAttribute("infoMessage",message.getMessage("E0113","파일을 선택해 주세요."));
            return "/popup/receiver/popup_address_import1";
        }

        AddressImportResultForm importResult = null;

        try {
            //새그룹 선택 시 로직
            if(form.getGkey().equals(AddressService.NEW_GROUP)){
                String gname = form.getGname();
                boolean flag = true;
                if(StringUtils.isEmpty(gname)){
                    model.addAttribute("infoMessage",message.getMessage("E0337","주소록 그룹명을 입력해 주세요."));
                    flag = false;
                }
                boolean isExistGname = addressService.isExistGname(gname,userid);
                if(isExistGname){
                    model.addAttribute("infoMessage",message.getMessage("E0482", "이미 존재하는 주소록 그룹명입니다."));
                    flag = false;
                }
                if(!flag){
                    // 그룹 목록 취득
                    List<ImbAddrGrp> addrGrpList = addressService.getAddressGroupList(userid);
                    form = addressService.importAddressFileSetting(form,userid);
                    model.addAttribute("importForm", form);
                    model.addAttribute("addrGrpList", addrGrpList);

                    return "/popup/receiver/popup_address_import3";
                }
            }

            importResult = addressService.importAddressForFile(form,userid);

            //log insert start
            // TODO 로그정책 변경으로 삭제 (개인주소록>가져오기 E211)

        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("{} - dbImportFinish ERROR",errorId);
            model.addAttribute("errorMessage", message.getMessage("E0060","작업중 오류가 발생하였습니다."));
            return "/popup/receiver/popup_address_import_finish";
        }
        catch (Exception e) {
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - dbImportFinish ERROR",errorId);
            model.addAttribute("errorMessage", message.getMessage("E0060","작업중 오류가 발생하였습니다."));
            return "/popup/receiver/popup_address_import_finish";
        }
        try {
            model.addAttribute("nRowCount", importResult.getImportCount()); // 시도 횟수
            model.addAttribute("nSuccess", importResult.getSuccessCount()); //성공 횟수
            model.addAttribute("nColumnCountNotMatch", importResult.getColumnCountNotMatch()); // 파일에서 헤더 개수와 데이터의 필드 개수가 불일치한 것

            if(!importResult.getBlankNameList().isEmpty()) {
                model.addAttribute("nBlankCount", importResult.getBlankNameList().size());//이름 누락 개수
                model.addAttribute("nBlankList", importResult.getBlankNameList());//이름 누락
            }
            if(!importResult.getEmailAddressErrorList().isEmpty()) {
                model.addAttribute("nEmailCheckCount", importResult.getEmailAddressErrorList().size()); // 이메일 누락/형식 오류 개수
                model.addAttribute("nEmailCheckList", importResult.getEmailAddressErrorList()); // 이메일 누락/형식 오류
            }
            if(!importResult.getNumberErrorList().isEmpty()){
                model.addAttribute("nNumberErrorCount", importResult.getNumberErrorList().size()); // 번호 형식 오류 개수
                model.addAttribute("nNumberErrorList", importResult.getNumberErrorList()); // 번호 형식 오류
            }
        }
        catch (NullPointerException np){
            String errorId = ErrorTraceLogger.log(np);
            log.error("{} - dbImportFinish ERROR",errorId);
            model.addAttribute("errorMessage", message.getMessage("E0060","작업중 오류가 발생하였습니다."));
            return "/popup/receiver/popup_address_import_finish";
        }
        return "/popup/receiver/popup_address_import_finish";
    }
    /**
     * 주소록 수정 페이지로 이동
     * @param ukey
     * @param session
     * @param model
     * @return
     */
    @RequestMapping(value = "addressSetting.json",method = RequestMethod.POST)
    @ResponseBody
    public String addressSetting(HttpServletRequest request, HttpSession session, ModelMap model){

        JSONObject object = new JSONObject();
        try{

            UserInfoBean userSessionInfo = UserInfoBean.getUserSessionInfo(session);
            String userid = userSessionInfo.getUserid();

            addressService.updateAddressCount(userid);

        }catch ( NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("{} - Address Setting ERROR", errorId);
            object.put("result", false);
            object.put("message",message.getMessage("E0060", "작업중 오류가 발생하였습니다."));
        }
        catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - Address Setting ERROR", errorId);
            object.put("result", false);
            object.put("message",message.getMessage("E0060", "작업중 오류가 발생하였습니다."));
        }
        object.put("result", true);
        object.put("message",message.getMessage("E0725", "주소록 그룹 갯수가 복원 되었습니다."));
        return object.toString();
    }
}

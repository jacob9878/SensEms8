package com.imoxion.sensems.web.service;

import com.csvreader.CsvReader;
import com.imoxion.common.util.ImCheckUtil;
import com.imoxion.common.util.ImStringUtil;
import com.imoxion.security.ImSecurityLib;
import com.imoxion.sensems.common.logger.ErrorTraceLogger;
import com.imoxion.sensems.web.beans.AddrSelBean;
import com.imoxion.sensems.web.common.ImbConstant;
import com.imoxion.sensems.web.database.domain.ImbAddr;
import com.imoxion.sensems.web.database.domain.ImbAddrGrp;
import com.imoxion.sensems.web.database.mapper.AddressMapper;
import com.imoxion.sensems.web.form.AddressForm;

import com.imoxion.sensems.web.form.AddressImportForm;
import com.imoxion.sensems.web.form.AddressImportResultForm;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import ucar.nc2.util.IO;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 개인 주소록 Service
 * @date 2021.03.11
 * @author jhpark
 *
 */
@Service
public class AddressService {

    protected Logger log = LoggerFactory.getLogger(AddressService.class);

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private MessageSourceAccessor message;

    /**
     * 파일로 가져오기 : 데이터 나누는 기준 - 콤마(쉼표)
     */
    public static final String DIV_COMMA = "0";

    /**
     * 파일로 가져오기 : 데이터 나누는 기준 - 세미콜론
     */
    public static final String DIV_SEMICOLON = "1";

    /**
     * 파일로 가져오기 : 헤더 유무
     */
    public static final String USE_HEADER = "1";

    /**
     * 값이 존재하지 않음
     */
    public static final String NO_DATA = "-1";

    /**
     * 새 그룹 지정
     */
    public static final String NEW_GROUP = "-1";


    /**
     * 검색 결과 수를 구한다.
     * @param srch_type
     * @param srch_keyword
     * @param userid
     * @param gkey
     * @return
     * @throws Exception
     */
    public int getAddressCountForSearch(String srch_type, String srch_keyword, String userid, int gkey) throws Exception{
        return addressMapper.getAddressCountForSearch(srch_type,srch_keyword,userid,gkey);
    }

    /**
     * 검색 결과 목록을 획득한다.
     * @param srch_type
     * @param srch_keyword
     * @param userid
     * @param gkey
     * @param start
     * @param end
     * @return
     */
    public List<ImbAddr> getAddressListForPageing(String srch_type, String srch_keyword, String userid, int gkey, int start, int end) throws Exception {
        List<ImbAddr> addrList =addressMapper.getAddressListForPageing(srch_type,srch_keyword,userid,gkey,start,end);

        if(ImbConstant.DATABASE_ENCRYPTION_USE) {
            for (ImbAddr addr : addrList) {
                decryptAddr(addr);
            }
        }
        return addrList;
    }

    /**
     * 주소록 그룹 목록 획득
     * @param userid
     * @return
     * @throws Exception
     */
    public List<ImbAddrGrp> getAddressGroupList(String userid) throws Exception {
        return addressMapper.getAddressGroupList(userid);
    }

    public String getGname(String userid, int gkey) throws Exception {
        return addressMapper.getGname(userid,gkey);
    }

    /**
     * 주소록 그룹 목록 획득 (페이징)
     * @param userid
     * @param start
     * @param end
     * @return
     * @throws Exception
     */
    public List<ImbAddrGrp> getAddressGroupListForPaging(String userid,int start, int end) throws Exception {
        return addressMapper.getAddressGroupListForPaging(userid,start,end);
    }

    /**
     * 주소록 그룹 목록 갯수 획득
     * @param userid
     * @return
     * @throws Exception
     */
    public int getAddressGroupListCount(String userid) throws Exception {
        return addressMapper.getAddressGroupListCount(userid);
    }

    /**
     * 주소록 테이블에서 그룹에 속한 정보 개수 획득
     * @param userid
     * @param gkey
     * @return
     * @throws Exception
     */
    public int getAddressCountByGkey(String userid, int gkey) throws Exception {
        return addressMapper.getAddressCountByGkey(userid,gkey);
    }

    /**
     * 주소록 모든 데이터 획득
     * @return
     */
    public List<ImbAddr> getAllAddressList(String userid) throws Exception {

        List<ImbAddr> addrList = addressMapper.getAllAddressList(userid);
        if(ImbConstant.DATABASE_ENCRYPTION_USE) {
            for (ImbAddr addr : addrList) {
                decryptAddr(addr);
            }
        }
        return addrList;
    }
    /**
     * 그룹 키를 이용한 주소록 데이터 획득
     * @return
     */
    public List<ImbAddr> getAddressListByGkey(String userid, int gkey) throws Exception {
        List<ImbAddr> addrList = addressMapper.getAddressListByGkey(userid,gkey);
        if(ImbConstant.DATABASE_ENCRYPTION_USE) {
            for (ImbAddr addr : addrList) {
                decryptAddr(addr);
            }
        }
        return addrList;
    }

    /**
     * 그룹 키를 이용한 주소록 데이터 획득 (페이징)
     */
    public List<ImbAddr> getAddressListByGkey2(String userid, int gkey, int start, int end) throws Exception {
        List<ImbAddr> addrList = addressMapper.getAddressListByGkey2(userid,gkey,start,end);
        if(ImbConstant.DATABASE_ENCRYPTION_USE) {
            for (ImbAddr addr : addrList) {
                decryptAddr(addr);
            }
        }
        return addrList;
    }

    /**
     * 그룹 키를 이용한 주소록 그룹 정보 획득
     * @param userid
     * @param gkey
     * @return
     * @throws Exception
     */
    public ImbAddrGrp getAddressGrpByGkey(String userid, int gkey) throws Exception {
        return addressMapper.getAddressGrpByGkey(userid,gkey);
    }

    /**
     * 그룹이름을 이용한 주소록 그룹 정보 획득
     * @param userid
     * @param gname
     * @return
     * @throws Exception
     */
    public ImbAddrGrp getAddressGrpByGname(String userid, String gname) throws Exception {
        return addressMapper.getAddressGrpByGname(userid,gname);
    }

    /**
     * 주소록 그룹 팝업에서 검색을 통한 주소록 정보 획득
     * @param userid
     * @param type
     * @param keyword
     * @return
     */
    public List<ImbAddr> getAddressListByKeyword(String userid, String type, String keyword) throws Exception {
        List<ImbAddr> addrList = addressMapper.getAddressListByKeyword(userid,type,keyword);
        if(ImbConstant.DATABASE_ENCRYPTION_USE) {
            for (ImbAddr addr : addrList) {
                decryptAddr(addr);
            }
        }
        return addrList;
    }

    /**
     * 주소록 그룹 정보 추가
     * @param addrGrp
     * @param userid
     * @throws Exception
     */
    public void insertAddressGrp(ImbAddrGrp addrGrp, String userid) throws Exception {
        addressMapper.insertAddressGrp(addrGrp,userid);
    }

    /**
     * 그룹 키를 이용하여 주소록 데이터를 삭제한다.
     * @param gkey
     */
    public void deleteAddrByGkey(String userid, int gkey) throws Exception {
        addressMapper.deleteAddrByGkey(userid, gkey);
    }

    /**
     * ukey를 이용하여 주소록 데이터를 획득
     * @param userid
     * @param ukey
     * @return
     */
    public ImbAddr getAddressByUkey(String userid, int ukey) throws Exception {
        ImbAddr addr = addressMapper.getAddressByUkey(userid,ukey);
        if(ImbConstant.DATABASE_ENCRYPTION_USE) {
            decryptAddr(addr);
        }
        return addr;
    }
    /**
     * 주소록 데이터 암호화 실시
     * @param addr
     */
    public void encryptAddr(ImbAddr addr) throws Exception {
        String secret_key = ImbConstant.DATABASE_AES_KEY;

        addr.setEmail(ImSecurityLib.encryptAES256(secret_key,addr.getEmail()));

//        if(StringUtils.isNotEmpty(addr.getHome_tel())){
//            addr.setHome_tel(ImSecurityLib.encryptAES256(secret_key,addr.getHome_tel()));
//        }
        if(StringUtils.isNotEmpty(addr.getOffice_tel())){
            addr.setOffice_tel(ImSecurityLib.encryptAES256(secret_key,addr.getOffice_tel()));
        }
        if(StringUtils.isNotEmpty(addr.getMobile())){
            addr.setMobile(ImSecurityLib.encryptAES256(secret_key,addr.getMobile()));
        }
//        if(StringUtils.isNotEmpty(addr.getFax())){
//            addr.setFax(ImSecurityLib.encryptAES256(secret_key,addr.getFax()));
//        }
        if(StringUtils.isNotEmpty(addr.getEtc1())){
            addr.setEtc1(ImSecurityLib.encryptAES256(secret_key,addr.getEtc1()));
        }
        if(StringUtils.isNotEmpty(addr.getEtc2())){
            addr.setEtc2(ImSecurityLib.encryptAES256(secret_key,addr.getEtc2()));
        }
//        if(StringUtils.isNotEmpty(addr.getEtc3())){
//            addr.setEtc3(ImSecurityLib.encryptAES256(secret_key,addr.getEtc3()));
//        }
//        if(StringUtils.isNotEmpty(addr.getEtc4())){
//            addr.setEtc4(ImSecurityLib.encryptAES256(secret_key,addr.getEtc4()));
//        }
//        if(StringUtils.isNotEmpty(addr.getEtc5())){
//            addr.setEtc5(ImSecurityLib.encryptAES256(secret_key,addr.getEtc5()));
//        }

    }
    /**
     * 주소록 데이터 복호화 실시
     * @param addr
     */
    public void decryptAddr(ImbAddr addr) throws Exception {
        String secret_key = ImbConstant.DATABASE_AES_KEY;

        addr.setEmail(ImSecurityLib.decryptAES256(secret_key,addr.getEmail()));

//        if(StringUtils.isNotEmpty(addr.getHome_tel())){{
//            addr.setHome_tel(ImSecurityLib.decryptAES256(secret_key,addr.getHome_tel()));
//        }}
        if(StringUtils.isNotEmpty(addr.getOffice_tel())){
            addr.setOffice_tel(ImSecurityLib.decryptAES256(secret_key,addr.getOffice_tel()));
        }
        if(StringUtils.isNotEmpty(addr.getMobile())){
            addr.setMobile(ImSecurityLib.decryptAES256(secret_key,addr.getMobile()));
        }
//        if(StringUtils.isNotEmpty(addr.getFax())){
//            addr.setFax(ImSecurityLib.decryptAES256(secret_key,addr.getFax()));
//        }
        if(StringUtils.isNotEmpty(addr.getEtc1())){
            String etc1 = ImSecurityLib.decryptAES256(secret_key,addr.getEtc1());
            addr.setEtc1(etc1);
        }
        if(StringUtils.isNotEmpty(addr.getEtc2())){
            addr.setEtc2(ImSecurityLib.decryptAES256(secret_key,addr.getEtc2()));
        }
//        if(StringUtils.isNotEmpty(addr.getEtc3())){
//            addr.setEtc3(ImSecurityLib.decryptAES256(secret_key,addr.getEtc3()));
//        }
//        if(StringUtils.isNotEmpty(addr.getEtc4())){
//            addr.setEtc4(ImSecurityLib.decryptAES256(secret_key,addr.getEtc4()));
//        }
//        if(StringUtils.isNotEmpty(addr.getEtc5())){
//            addr.setEtc5(ImSecurityLib.decryptAES256(secret_key,addr.getEtc5()));
//        }

    }


    /**
     * 주소록 데이터 추가
     * @param addrInfo
     * @param userid
     */
    public void insertAddress(ImbAddr addrInfo, String userid) throws Exception {
        addressMapper.insertAddress(addrInfo,userid);
    }

    /**
     * ukey를 이용한 삭제 동작
     * @param userid
     * @param ukey
     */
    public void deleteAddrByUkey(String userid, int ukey) throws Exception {
        addressMapper.deleteaddrByUkey(userid, ukey);
    }


    /**
     * 주소록 데이터 수정
     * @param addr
     */
    public void updateAddress(ImbAddr addr,String userid) throws Exception {
        addressMapper.updateAddress(addr,userid);
    }

    /**
     * 그룹 키를 이용하여 주소록 그룹 삭제
     * @param userid
     * @param gkey
     */
    public void deleteAddrGrpByGkey(String userid, int gkey) throws Exception {
        addressMapper.deleteAddrGrpByGkey(userid,gkey);
    }

    /**
     * 주소록 그룹 데이터 수정
     * @param addrGrp
     * @param userid
     */
    public void updateAddressGrp(ImbAddrGrp addrGrp, String userid) throws Exception{
        addressMapper.updateAddressGrp(addrGrp,userid);
    }

    /**
     * ImbAddr 객체를 Form 데이터로 변환
     * @param addr
     * @return
     */
    public AddressForm convertToAddressForm(ImbAddr addr) {
        AddressForm form = new AddressForm();

        String ukey = Integer.toString(addr.getUkey());
        String gkey = Integer.toString(addr.getGkey());

        form.setUkey(ukey);
        form.setGkey(gkey);
        form.setName(addr.getName());
        form.setEmail(addr.getEmail());
        form.setCompany(addr.getCompany());
        form.setDept(addr.getDept());
        form.setGrade(addr.getGrade());
//        form.setHome_tel(addr.getHome_tel());
        form.setOffice_tel(addr.getOffice_tel());
        form.setMobile(addr.getMobile());
//        form.setFax(addr.getFax());
//        form.setZipcode(addr.getZipcode());
//        form.setAddr1(addr.getAddr1());
//        form.setAddr2(addr.getAddr2());
        form.setEtc1(addr.getEtc1());
        form.setEtc2(addr.getEtc2());
//        form.setEtc3(addr.getEtc3());
//        form.setEtc4(addr.getEtc4());
//        form.setEtc5(addr.getEtc5());
        form.setRegdate(addr.getRegdate());

        return form;
    }

    /**
     * 추가 시 유효성 검사
     * @param form
     * @param model
     */
    public boolean isValidate(AddressForm form, ModelMap model, String userid, boolean isAdd) throws Exception {
        boolean result = false;

        String name = form.getName();
        String gkey = form.getGkey();
        String email = form.getEmail();
//        String home_tel = form.getHome_tel();
        String office_tel = form.getOffice_tel();
        String mobile = form.getMobile();
//        String fax = form.getFax();
//        String zipcode = form.getZipcode();
        //필수값 null 체크

        if(!isAdd){
            String ukey = form.getUkey();
            if(StringUtils.isEmpty(ukey)){
                model.addAttribute("infoMessage" ,message.getMessage("E0359","수정할 주소록 데이터가 없습니다."));
                return result;
            }
        }

        if(StringUtils.isEmpty(name)){
            model.addAttribute("infoMessage" ,message.getMessage("E0354","이름을 입력해 주세요."));
            return result;
        }
        if(StringUtils.isEmpty(gkey)){
            model.addAttribute("infoMessage" ,message.getMessage("E0340","주소록 그룹을 선택해 주세요."));
            return result;
        }
        if(StringUtils.isEmpty(email)){
            model.addAttribute("infoMessage" ,message.getMessage("E0054","E-MAIL을 입력해 주세요."));
            return result;
        }

        //이메일 형식 체크
        if(!ImCheckUtil.isEmail(email)){
            model.addAttribute("infoMessage" ,message.getMessage("E0050","E-MAIL 형식이 잘못되었습니다."));
            return result;
        }

        String regex = "^[a-zA-Z0-9ㄱ-ㅎ가-힣-._\\s]*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher;
        boolean check;

        matcher = pattern.matcher(name);
        check = matcher.matches();
        if(!check) {model.addAttribute("infoMessage" ,message.getMessage("E0737","사용할 수 없는 특수문자가 포함되어 있습니다. (사용가능한 특수 문자 : (_) , (,) , (-) )")); return false;};

        matcher = pattern.matcher(form.getCompany());
        check = matcher.matches();
        if(!check) {model.addAttribute("infoMessage" ,message.getMessage("E0737","사용할 수 없는 특수문자가 포함되어 있습니다. (사용가능한 특수 문자 : (_) , (,) , (-) )")); return false;};

        matcher = pattern.matcher(form.getDept());
        check = matcher.matches();
        if(!check) {model.addAttribute("infoMessage" ,message.getMessage("E0737","사용할 수 없는 특수문자가 포함되어 있습니다. (사용가능한 특수 문자 : (_) , (,) , (-) )")); return false;};

        matcher = pattern.matcher(form.getGrade());
        check = matcher.matches();
        if(!check) {model.addAttribute("infoMessage" ,message.getMessage("E0737","사용할 수 없는 특수문자가 포함되어 있습니다. (사용가능한 특수 문자 : (_) , (,) , (-) )")); return false;};

        matcher = pattern.matcher(form.getOffice_tel());
        check = matcher.matches();
        if(!check) {model.addAttribute("infoMessage" ,message.getMessage("E0737","사용할 수 없는 특수문자가 포함되어 있습니다. (사용가능한 특수 문자 : (_) , (,) , (-) )")); return false;};

        matcher = pattern.matcher(form.getMobile());
        check = matcher.matches();
        if(!check) {model.addAttribute("infoMessage" ,message.getMessage("E0737","사용할 수 없는 특수문자가 포함되어 있습니다. (사용가능한 특수 문자 : (_) , (,) , (-) )")); return false;};

        matcher = pattern.matcher(form.getEtc1());
        check = matcher.matches();
        if(!check) {model.addAttribute("infoMessage" ,message.getMessage("E0737","사용할 수 없는 특수문자가 포함되어 있습니다. (사용가능한 특수 문자 : (_) , (,) , (-) )")); return false;};

        matcher = pattern.matcher(form.getEtc1());
        check = matcher.matches();
        if(!check) {model.addAttribute("infoMessage" ,message.getMessage("E0737","사용할 수 없는 특수문자가 포함되어 있습니다. (사용가능한 특수 문자 : (_) , (,) , (-) )")); return false;};


        //이메일 중복 체크
        if(ImbConstant.DATABASE_ENCRYPTION_USE){
            String secret_key = ImbConstant.DATABASE_AES_KEY;
            email = ImSecurityLib.encryptAES256(secret_key,email);
        }

        List<Integer> gkeyList = addressMapper.getGkeyListByGkey(email,userid, gkey);
            if(gkeyList.size()>=1){
                // 이메일의 그룹키가 다를 경우 중복으로 인한 에러 발생
                    model.addAttribute("infoMessage" ,message.getMessage("E0488","다른 그룹 내의 E-Mail 중복만 허용합니다."));
                    return false;

        }
        if(StringUtils.isNotEmpty(office_tel)){
            if(!ImCheckUtil.isPhone(office_tel)){
                model.addAttribute("infoMessage",message.getMessage("E0356","회사전화 형식이 잘못되었습니다."));
                return result;
            }
        }
        if(StringUtils.isNotEmpty(mobile)){
            if(!ImCheckUtil.isPhone(mobile)){
                model.addAttribute("infoMessage",message.getMessage("E0299","휴대폰번호 형식이 잘못되었습니다."));
                return result;
            }
        }
        result = true;
        return result;
    }

    /**
     * 수정 시 유효성 검사
     * @param form
     * @param model
     */
    public boolean editIsValidate(AddressForm form, ModelMap model, String userid, boolean isAdd) throws Exception {
        boolean result = false;

        String name = form.getName();
        String gkey = form.getGkey();
        String email = form.getEmail();
//        String home_tel = form.getHome_tel();
        String ukey = form.getUkey();
        String office_tel = form.getOffice_tel();
        String mobile = form.getMobile();
//        String fax = form.getFax();
//        String zipcode = form.getZipcode();
        //필수값 null 체크

        if(!isAdd){
            if(StringUtils.isEmpty(ukey)){
                model.addAttribute("infoMessage" ,message.getMessage("E0359","수정할 주소록 데이터가 없습니다."));
                return result;
            }
        }

        if(StringUtils.isEmpty(name)){
            model.addAttribute("infoMessage" ,message.getMessage("E0354","이름을 입력해 주세요."));
            return result;
        }
        if(StringUtils.isEmpty(gkey)){
            model.addAttribute("infoMessage" ,message.getMessage("E0340","주소록 그룹을 선택해 주세요."));
            return result;
        }
        if(StringUtils.isEmpty(email)){
            model.addAttribute("infoMessage" ,message.getMessage("E0054","E-MAIL을 입력해 주세요."));
            return result;
        }

        //이메일 형식 체크
        if(!ImCheckUtil.isEmail(email)){
            model.addAttribute("infoMessage" ,message.getMessage("E0050","E-MAIL 형식이 잘못되었습니다."));
            return result;
        }

        String regex = "^[a-zA-Z0-9ㄱ-ㅎ가-힣-._\\s]*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher;
        boolean check;

        matcher = pattern.matcher(name);
        check = matcher.matches();
        if(!check) {model.addAttribute("infoMessage" ,message.getMessage("E0737","사용할 수 없는 특수문자가 포함되어 있습니다. (사용가능한 특수 문자 : (_) , (,) , (-) )")); return false;};

        matcher = pattern.matcher(form.getCompany());
        check = matcher.matches();
        if(!check) {model.addAttribute("infoMessage" ,message.getMessage("E0737","사용할 수 없는 특수문자가 포함되어 있습니다. (사용가능한 특수 문자 : (_) , (,) , (-) )")); return false;};

        matcher = pattern.matcher(form.getDept());
        check = matcher.matches();
        if(!check) {model.addAttribute("infoMessage" ,message.getMessage("E0737","사용할 수 없는 특수문자가 포함되어 있습니다. (사용가능한 특수 문자 : (_) , (,) , (-) )")); return false;};

        matcher = pattern.matcher(form.getGrade());
        check = matcher.matches();
        if(!check) {model.addAttribute("infoMessage" ,message.getMessage("E0737","사용할 수 없는 특수문자가 포함되어 있습니다. (사용가능한 특수 문자 : (_) , (,) , (-) )")); return false;};

        matcher = pattern.matcher(form.getOffice_tel());
        check = matcher.matches();
        if(!check) {model.addAttribute("infoMessage" ,message.getMessage("E0737","사용할 수 없는 특수문자가 포함되어 있습니다. (사용가능한 특수 문자 : (_) , (,) , (-) )")); return false;};

        matcher = pattern.matcher(form.getMobile());
        check = matcher.matches();
        if(!check) {model.addAttribute("infoMessage" ,message.getMessage("E0737","사용할 수 없는 특수문자가 포함되어 있습니다. (사용가능한 특수 문자 : (_) , (,) , (-) )")); return false;};

        matcher = pattern.matcher(form.getEtc1());
        check = matcher.matches();
        if(!check) {model.addAttribute("infoMessage" ,message.getMessage("E0737","사용할 수 없는 특수문자가 포함되어 있습니다. (사용가능한 특수 문자 : (_) , (,) , (-) )")); return false;};

        matcher = pattern.matcher(form.getEtc1());
        check = matcher.matches();
        if(!check) {model.addAttribute("infoMessage" ,message.getMessage("E0737","사용할 수 없는 특수문자가 포함되어 있습니다. (사용가능한 특수 문자 : (_) , (,) , (-) )")); return false;};

        //이메일 중복 체크
        if(ImbConstant.DATABASE_ENCRYPTION_USE){
            String secret_key = ImbConstant.DATABASE_AES_KEY;
            email = ImSecurityLib.encryptAES256(secret_key,email);
        }

        List<Integer> gkeyList = addressMapper.getGkeyListByGkeyForEdit(email,userid, gkey, ukey);
        if(gkeyList.size()>=1){
            // 이메일의 그룹키가 다를 경우 중복으로 인한 에러 발생
            model.addAttribute("infoMessage" ,message.getMessage("E0488","다른 그룹 내의 E-Mail 중복만 허용합니다"));
            return false;

        }
        if(StringUtils.isNotEmpty(office_tel)){
            if(!ImCheckUtil.isPhone(office_tel)){
                model.addAttribute("infoMessage",message.getMessage("E0356","회사전화 형식이 잘못되었습니다."));
                return result;
            }
        }
        if(StringUtils.isNotEmpty(mobile)){
            if(!ImCheckUtil.isPhone(mobile)){
                model.addAttribute("infoMessage",message.getMessage("E0299","휴대폰번호 형식이 잘못되었습니다."));
                return result;
            }
        }
        result = true;
        return result;
    }

    /**
     * Form데이터를 ImbAddr로 변환
     * @param form
     * @return
     */
    public ImbAddr convertToImbAddr(AddressForm form) {
        ImbAddr addr = new ImbAddr();

        if(StringUtils.isNotEmpty(form.getUkey())){
            int ukey = ImStringUtil.parseInt(form.getUkey());
            addr.setUkey(ukey);
        }
        int gkey = ImStringUtil.parseInt(form.getGkey());
        addr.setGkey(gkey);


        addr.setName(form.getName());
        addr.setEmail(form.getEmail());
        addr.setCompany(form.getCompany());
        addr.setDept(form.getDept());
        addr.setGrade(form.getGrade());
//        addr.setHome_tel(form.getHome_tel());
        addr.setOffice_tel(form.getOffice_tel());
        addr.setMobile(form.getMobile());
//        addr.setFax(form.getFax());
//        addr.setZipcode(form.getZipcode());
//        addr.setAddr1(form.getAddr1());
//        addr.setAddr2(form.getAddr2());
        addr.setEtc1(form.getEtc1());
        addr.setEtc2(form.getEtc2());
//        addr.setEtc3(form.getEtc3());
//        addr.setEtc4(form.getEtc4());
//        addr.setEtc5(form.getEtc5());

        return addr;
    }


    /**
     * 주소록 목록 엑셀 파일 생성 및 다운로드
     * @param addrList
     * @param tempFileName
     * @param userid
     * @throws Exception
     */
    public void getXlsxDownload(List<ImbAddr> addrList,String tempFileName,String userid) throws Exception {
        FileOutputStream fileoutputstream = null;
        XSSFWorkbook workbook = new XSSFWorkbook();

        //2차는 sheet생성
        XSSFSheet sheet = workbook.createSheet(userid + "_Address_List");
        //엑셀의 행
        XSSFRow row = null;
        //엑셀의 셀
        XSSFCell cell = null;

        row = sheet.createRow(0);
        //타이틀(첫행)
        String name = message.getMessage("E0018","이름");
        String email = message.getMessage("E0022","E-MAIL");
        String company = message.getMessage("E0312","회사");;
        String dept = message.getMessage("E0313","부서");;
        String grade = message.getMessage("E0021","직책");;
//        String home_tel = message.getMessage("E0344","집전화번호");;
        String office_tel = message.getMessage("E0314","회사전화");;
        String mobile = message.getMessage("E0037","휴대폰번호");;
//        String fax = message.getMessage("E0345","FAX");;
//        String zipcode = message.getMessage("E0346","우편번호");;
//        String addr1 = message.getMessage("E0347","주소1");;
//        String addr2 = message.getMessage("E0348","주소2");;
        String etc1 = message.getMessage("E0316","기타정보1");;
        String etc2 = message.getMessage("E0349","기타정보2");;
//        String etc3 = message.getMessage("E0350","기타정보3");;
//        String etc4 = message.getMessage("E0351","기타정보4");;
//        String etc5 = message.getMessage("E0352","기타정보5");;
        String[] columns = {name,email,company,dept,grade,office_tel,mobile,etc1,etc2};
        for (int i = 0; i < columns.length; i++) {
            cell = row.createCell(i);
            cell.setCellValue(columns[i]);
        }

        int i = 1;
        for (ImbAddr addr : addrList) {
            row = sheet.createRow((Integer) i);
            int j = 0;
            //이름 추가
            cell = row.createCell(j);
            cell.setCellValue(addr.getName());
            j++;
            //이메일 추가
            cell = row.createCell(j);
            cell.setCellValue(addr.getEmail());
            j++;
            //회사 추가
            cell = row.createCell(j);
            cell.setCellValue(addr.getCompany());
            j++;
            //부서 추가
            cell = row.createCell(j);
            cell.setCellValue(addr.getDept());
            j++;
            //직책 추가
            cell = row.createCell(j);
            cell.setCellValue(addr.getGrade());
            j++;
            //집전화번호 추가
//            cell = row.createCell(j);
//            cell.setCellValue(addr.getHome_tel());
//            j++;
            //회사전화 추가
            cell = row.createCell(j);
            cell.setCellValue(addr.getOffice_tel());
            j++;
            //휴대폰번호 추가
            cell = row.createCell(j);
            cell.setCellValue(addr.getMobile());
            j++;
            //fax 추가
//            cell = row.createCell(j);
//            cell.setCellValue(addr.getFax());
//            j++;
//            //우편번호 추가
//            cell = row.createCell(j);
//            cell.setCellValue(addr.getZipcode());
//            j++;
//            //주소1 추가
//            cell = row.createCell(j);
//            cell.setCellValue(addr.getAddr1());
//            j++;
//            //주소2 추가
//            cell = row.createCell(j);
//            cell.setCellValue(addr.getAddr2());
//            j++;
            //기타정보1 추가
            cell = row.createCell(j);
            cell.setCellValue(addr.getEtc1());
            j++;
            //기타정보2 추가
            cell = row.createCell(j);
            cell.setCellValue(addr.getEtc2());
            j++;
            //기타정보3 추가
//            cell = row.createCell(j);
//            cell.setCellValue(addr.getEtc3());
//            j++;
//            //기타정보4 추가
//            cell = row.createCell(j);
//            cell.setCellValue(addr.getEtc4());
//            j++;
//            //기타정보5 추가
//            cell = row.createCell(j);
//            cell.setCellValue(addr.getEtc5());
//            j++;

            i++;
        }

        fileoutputstream = new FileOutputStream(tempFileName);
        //파일을 쓴다
        workbook.write(fileoutputstream);

        if (fileoutputstream != null) fileoutputstream.close();
    }

    /**
     * imb_addrsel 테이블에 수신그룹 insert
     * @param bean
     * */
    public void insertAddrSel(AddrSelBean bean) throws Exception{
    	addressMapper.insertAddrSel(bean);
    }

    /**
     * 파일로 가져오기 미리보기 페이지 설정
     * @param fileKey
     * @param div
     * @param header
     * @return
     * @throws Exception
     */
    public String importAddressFilePreview(String fileKey,String userid, String div, String header) throws Exception {

        char divstr = ',';
        if (DIV_SEMICOLON.equals(div)) {
            divstr = ';';
        }

        String addrFile_path = ImbConstant.TEMPFILE_PATH;
        File addrFile = new File(addrFile_path + File.separator + userid + "_" + fileKey);
        if( !addrFile.exists() ){
//            throw new Exception();
            log.error("Argument is file = {}, file.exists() = {}", addrFile, addrFile.exists());
        }

        String sCharset = "UTF-8";

        String retStr = "<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"1\" class=\"table\">";

        BufferedReader in = null;
        CsvReader csvReader = null;
        try {
            int f_count;
            int checkCnt = 0;
            in = new BufferedReader(new InputStreamReader(new FileInputStream(addrFile),sCharset));
            csvReader = new CsvReader(in, divstr);
            csvReader.setEscapeMode(1);
            while (csvReader.readRecord()) {
                f_count = csvReader.getColumnCount();
                if (checkCnt > 9)
                    break;

                if(checkCnt == 0 ){
                    if(!USE_HEADER.equals(header)){
                        retStr += "<thead>";
                        for (int i = 0; i < f_count; i++) {
                            retStr += "<th>" + csvReader.get(i) + "</th>";
                        }
                        retStr += "</thead>";
                    }
                }else {
                    retStr += "<tr>";
                    for (int i = 0; i < f_count; i++) {
                        retStr += "<td class=\"over_text\">" + csvReader.get(i) + "</td>";
                    }
                    retStr += "</tr>";
                }
                checkCnt++;
            }
        } finally {
            try{ if (csvReader != null) csvReader.close(); }catch (NullPointerException ne){}
            catch(Exception e){}
            try{ if (in != null) in.close(); } catch (NullPointerException ne){}
            catch(Exception e){}
        }

        return retStr + "</table>";
    }

    /**
     * 파일로 가져오기 설정
     * 동기화를 넣은 이유 : 시큐어 코딩에서 TOCTOU경쟁조건 취약점이 발생하여, 동기화 구문을 사용하여 하나의 스레드만 접근가능하도록 조치
     * @param form
     * @param userid
     * @return
     * @throws Exception
     */
    public synchronized AddressImportForm importAddressFileSetting(AddressImportForm form,String userid) throws Exception{

        Map<Integer, String> strColumn = new HashMap<Integer, String>();

        String strColArray = "";
        String strdiv = ",";
        int nCol = 0;
        if (DIV_COMMA.equals(form.getDivMethod())) {
            strdiv = ",";
        } else if (DIV_SEMICOLON.equals(form.getDivMethod())) {
            strdiv = ";";
        } else {
            strdiv = "";
        }

        String addrFile_path = ImbConstant.TEMPFILE_PATH;
        File addrFile = new File(addrFile_path + File.separator + userid + "_" + form.getFileKey());

        String sCharset = "UTF-8";

        List<String> arrLineStr = new ArrayList<String>();
        if (addrFile.exists()) {
            BufferedReader in = null;
            try {
                StringBuffer sb = new StringBuffer();
                in = new BufferedReader(new InputStreamReader(new FileInputStream(addrFile), sCharset));
                String readString = null;
                while ((readString = in.readLine()) != null) {
                    sb.append(readString);
                    arrLineStr.add(readString);
                }
            }catch (FileNotFoundException fe){
                String errorId = ErrorTraceLogger.log(fe);
                log.error("FileNotException fe error : {}",errorId);
            }catch (Exception e){
                String errorId = ErrorTraceLogger.log(e);
                log.error("FileNotException error : {}",errorId);
            }finally {
                if (in != null) in.close();
            }
        } else {
            log.error("Arguement is file = {}, file.isFile() = {}", addrFile, addrFile.isFile());
            form.setNCol(0);
            form.setStrColArray(strColArray);
        }
        nCol = arrLineStr.size();


        /** 1번째 행만 나오도록 option 설정 및 배열 설정 Start */
        StringTokenizer sToken = new StringTokenizer(arrLineStr.get(0).toString(), strdiv);
        List<String> arrFileStr = new ArrayList<String>();

        while (sToken.hasMoreTokens()) {
            arrFileStr.add(sToken.nextToken());
        }

        if (strdiv.equals("")) {
            for (int c = 0; c < arrFileStr.size(); c++) {
                if (USE_HEADER.equals(form.getHeader())) {
                    strColumn.put(c, arrFileStr.get(c));
                    strColArray += "arrcolname[" + c + "]='" + arrFileStr.get(c) + "';\n";
                } else {
                    strColumn.put(c, "col" + c);
                    strColArray += "arrcolname[" + c + "]='col" + c + "';\n";
                }
                strColArray += "arrcolsize[" + c + "]='100';\n";
                strColArray += "arrcoltype[" + c + "]='0';\n";
            }
        } else {
            for (int c = 0; c < arrFileStr.size(); c++) {
                strColumn.put(c, arrFileStr.get(c));
                strColArray += "arrcolname[" + c + "]='" + arrFileStr.get(c) + "';\n";
                strColArray += "arrcolsize[" + c + "]='100';\n";
                strColArray += "arrcoltype[" + c + "]='0';\n";
            }
        }
        /** 1번째 행만 나오도록 option 설정 및 배열 설정 End */

        form.setNCol(nCol);
        form.setStrColumn(strColumn);
        form.setStrColArray(strColArray);
        return form;
    }

    /**
     * 추가된 .csv 또는 .txt 파일을 읽어드려 List 형태로 변형
     * @param form
     * @param userid
     * @return
     * @throws Exception
     */
    public AddressImportResultForm importAddressForFile(AddressImportForm form, String userid) throws Exception {
        char strdiv = ',';
        int columnCountNotMatch = 0;
        String divmethod = form.getDivMethod();
        if (DIV_COMMA.equals(divmethod)) {
            strdiv = ',';
        } else if (DIV_SEMICOLON.equals(divmethod)) {
            strdiv = ';';
        } else {
            strdiv = ',';
        }

        String addrFile_path = ImbConstant.TEMPFILE_PATH;
        File addrFile = new File(addrFile_path + File.separator + userid + "_" + form.getFileKey());
        String sCharset = "UTF-8";

        if (!addrFile.exists()) {
            log.error("Argument is file = {}, file.exists() = {}", addrFile, addrFile.exists());
        }

        int columnLength = 0;
        if (NO_DATA.equals(form.getName())) {
            log.error("Argument is NAME = {}", form.getName());

        }

        if (NO_DATA.equals(form.getEmail())) {
            log.error("Argument is E-MAIL = {}", form.getEmail());

        }

        String gkey = form.getGkey();
        String gname = form.getGname();

        List<ImbAddr> addressList = new ArrayList<ImbAddr>();

        // 배열화 된 목록을 차례대로 구분자로 나눠준다.
        // 업로드한 파일을 배열화한다.

        int nCount = 0;
        FileInputStream fis = null;
        BufferedReader in = null;

        fis = new FileInputStream(addrFile);
        in = new BufferedReader(new InputStreamReader(fis, sCharset));

        int f_count = 0;
        CsvReader csvReader = new CsvReader(in, strdiv);
        csvReader.setEscapeMode(1);

        while (csvReader.readRecord()) {
            f_count = csvReader.getColumnCount();

            String sName = null;
            String sEmail = null;
            String sCompany = null;
            String sDept = null;
            String sGrade = null;
            String sHome_tel = null;
            String sOffice_tel = null;
            String sMobile = null;
            String sFax = null;
            String sZipcode = null;
            String sAddr1 = null;
            String sAddr2 = null;
            String sEtc1 = null;
            String sEtc2 = null;
            String sEtc3 = null;
            String sEtc4 = null;
            String sEtc5 = null;

            if (nCount == 0)
                columnLength = f_count;

            if (columnLength > f_count) {
                log.error("Argument is columnLength = {}", columnLength);
                log.error("Argument is f_count = {}", f_count);
                columnCountNotMatch++;
                continue;
            }

            if (USE_HEADER.equals(form.getHeader()) && nCount == 0) {
                nCount++;
                continue;
            }

            if (StringUtils.isNotEmpty(form.getName()) && !NO_DATA.equals(form.getName())) {
                sName = ImStringUtil.trim(csvReader.get(ImStringUtil.parseInt(form.getName())));
            }

            sName = ImStringUtil.replace(sName, "\"", "");

            sEmail = csvReader.get(ImStringUtil.parseInt(form.getEmail()));
            sEmail = ImStringUtil.replace(sEmail, "\"", "");

            // 주소록에 메일 계정을 등록한다.
            if (!NO_DATA.equals(form.getCompany())) {
                sCompany = csvReader.get(ImStringUtil.parseInt(form.getCompany()));
            }

            if (!NO_DATA.equals(form.getDept())) {
                sDept = csvReader.get(ImStringUtil.parseInt(form.getDept()));
            }

            if (!NO_DATA.equals(form.getGrade())) {
                sGrade = csvReader.get(ImStringUtil.parseInt(form.getGrade()));
            }

            if (!NO_DATA.equals(form.getHome_tel())) {
                sHome_tel = csvReader.get(ImStringUtil.parseInt(form.getHome_tel()));
            }

            if (!NO_DATA.equals(form.getOffice_tel())) {
                sOffice_tel = csvReader.get(ImStringUtil.parseInt(form.getOffice_tel()));
            }

            if (!NO_DATA.equals(form.getMobile())) {
                sMobile = csvReader.get(ImStringUtil.parseInt(form.getMobile()));
            }

            if (!NO_DATA.equals(form.getFax())) {
                sFax = csvReader.get(ImStringUtil.parseInt(form.getFax()));
            }

            if (!NO_DATA.equals(form.getZipcode())) {
                sZipcode = csvReader.get(ImStringUtil.parseInt(form.getZipcode()));
            }

            if (!NO_DATA.equals(form.getAddr1())) {
                sAddr1 = csvReader.get(ImStringUtil.parseInt(form.getAddr1()));
            }

            if (!NO_DATA.equals(form.getAddr2())) {
                sAddr2 = csvReader.get(ImStringUtil.parseInt(form.getAddr2()));
            }

            if (!NO_DATA.equals(form.getEtc1())) {
                sEtc1 = csvReader.get(ImStringUtil.parseInt(form.getEtc1()));
            }
            if (!NO_DATA.equals(form.getEtc2())) {
                sEtc2 = csvReader.get(ImStringUtil.parseInt(form.getEtc2()));
            }
            if (!NO_DATA.equals(form.getEtc1())) {
                sEtc3 = csvReader.get(ImStringUtil.parseInt(form.getEtc3()));
            }
            if (!NO_DATA.equals(form.getEtc1())) {
                sEtc4 = csvReader.get(ImStringUtil.parseInt(form.getEtc4()));
            }
            if (!NO_DATA.equals(form.getEtc1())) {
                sEtc5 = csvReader.get(ImStringUtil.parseInt(form.getEtc5()));
            }

            ImbAddr address = new ImbAddr();
            address.setName(sName);

            address.setEmail(sEmail);
            address.setCompany(ImStringUtil.replace(sCompany, "\"", ""));
            address.setDept(ImStringUtil.replace(sDept, "\"", ""));
            address.setGrade(ImStringUtil.replace(sGrade, "\"", ""));
//            address.setHome_tel(ImStringUtil.replace(sHome_tel, "\"", ""));
            address.setOffice_tel(ImStringUtil.replace(sOffice_tel, "\"", ""));
            address.setMobile(ImStringUtil.replace(sMobile, "\"", ""));
//            address.setFax(ImStringUtil.replace(sFax, "\"", ""));
//            address.setZipcode(ImStringUtil.replace(sZipcode, "\"", ""));
//            address.setAddr1(ImStringUtil.replace(sAddr1, "\"", ""));
//            address.setAddr2(ImStringUtil.replace(sAddr2, "\"", ""));
            address.setEtc1(ImStringUtil.replace(sEtc1, "\"", ""));
            address.setEtc2(ImStringUtil.replace(sEtc2, "\"", ""));
//            address.setEtc3(ImStringUtil.replace(sEtc3, "\"", ""));
//            address.setEtc4(ImStringUtil.replace(sEtc4, "\"", ""));
//            address.setEtc5(ImStringUtil.replace(sEtc5, "\"", ""));

            addressList.add( address );
        }
        if (in != null) in.close();
        if (fis != null) fis.close();

        AddressImportResultForm resultForm = importAddress(userid,gkey,gname,addressList);
        if(columnLength !=0) {
            resultForm.setColumnCountNotMatch(columnCountNotMatch);
        }
        return resultForm;
    }

    /**
     * List에 포함된 주소록 데이터를 insert하여 결과를 반환
     * @param userid
     * @param gkey
     * @param gname
     * @param addressList
     * @return
     * @throws Exception
     */
    private AddressImportResultForm importAddress(String userid, String gkey, String gname, List<ImbAddr> addressList) throws Exception {
        AddressImportResultForm resultForm = new AddressImportResultForm();

        int importCount = 0; // 처리한 주소 수

        int successCount = 0; // 등록 성공한 주소 수

        // 이름 누락 목록
        List<ImbAddr> blankNameList = new ArrayList<ImbAddr>();

        // 이메일 누락 / 형식 오류
        List<ImbAddr> emailAddressErrorList = new ArrayList<ImbAddr>();

        // 숫자 오류
        List<ImbAddr> numberErrorList = new ArrayList<ImbAddr>();

        ImbAddrGrp addrGrp = null;
        int group_key = 0;
        //새그룹 선택 시, 주소록 그룹을 만든다.
        if ( AddressService.NEW_GROUP.equals( gkey ) ){
            addrGrp = new ImbAddrGrp();
            addrGrp.setGname(gname);
            addrGrp.setMemo("");


            insertAddressGrp(addrGrp,userid);
            group_key = addrGrp.getGkey();
        }else {
            //선택한 주소록 그룹 키 세팅
            group_key = ImStringUtil.parseInt(gkey);
        }

        if( addressList == null ){
            return null;
        }

        for(ImbAddr addr : addressList){
            addr.setGkey(group_key);
            addr.setRegdate(new Date());
            //이름이 누락된 경우
            if(StringUtils.isEmpty(addr.getName())){
                blankNameList.add(addr);
                importCount++;
                continue;
            }
            //이메일이 누락된 경우
            if(StringUtils.isEmpty(addr.getEmail())){
                emailAddressErrorList.add(addr);
                importCount++;
                continue;
            }
            //이메일 형식이 맞지 않을 경우
            if (!ImCheckUtil.isEmail(addr.getEmail())) {
                emailAddressErrorList.add(addr);
                importCount++;
                continue;
            }

            //이메일 중복 체크
            String tempEmail = addr.getEmail();
            if(ImbConstant.DATABASE_ENCRYPTION_USE){
                String secret_key = ImbConstant.DATABASE_AES_KEY;
                tempEmail = ImSecurityLib.encryptAES256(secret_key,tempEmail);
            }

            // 해당 이메일을 보유하고 있는 gkeyList를 획득한다.
            // select DISTINCT gkey 를 통해 검색함으로 0과 1이 나와야만 한다. (0일 경우 추가, 1일 경우 gkey 값 비교(같으면 추가, 다르면 중복))
            // 1 보다 클 경우 이미 중복 이메일 존재함으로 에러
            List<Integer> gkeyList = addressMapper.getGkeyListByGkey(tempEmail,userid, String.valueOf(group_key));

                if(gkeyList.size()>=1){
                            emailAddressErrorList.add(addr);
                            importCount++;
                            continue;
                }

            //번호 형식 체크
//            if(StringUtils.isNotEmpty(addr.getHome_tel())){
//                if(!ImCheckUtil.isPhone(addr.getHome_tel())){
//                    numberErrorList.add(addr);
//                    importCount++;
//                    continue;
//                }
//            }
            if(StringUtils.isNotEmpty(addr.getOffice_tel())){
                if(!ImCheckUtil.isPhone(addr.getOffice_tel())){
                    numberErrorList.add(addr);
                    importCount++;
                    continue;
                }
            }
            if(StringUtils.isNotEmpty(addr.getMobile())){
                if(!ImCheckUtil.isPhone(addr.getMobile())){
                    numberErrorList.add(addr);
                    importCount++;
                    continue;
                }
            }
//            if(StringUtils.isNotEmpty(addr.getFax())){
//                if(!ImCheckUtil.isPhone(addr.getFax())){
//                    numberErrorList.add(addr);
//                    importCount++;
//                    continue;
//                }
//            }
//            if(StringUtils.isNotEmpty(addr.getZipcode())){
//                if(!ImCheckUtil.isPhone(addr.getZipcode())){
//                    numberErrorList.add(addr);
//                    importCount++;
//                    continue;
//                }
//            }

            //데이터 insert 실시
            try{
                if(ImbConstant.DATABASE_ENCRYPTION_USE){
                    encryptAddr(addr);
                }
                insertAddress(addr,userid);
                importCount++;
                successCount++;
            }catch (NullPointerException ne) {
                String errorId = ErrorTraceLogger.log(ne);
                log.error("{} - ADDRESS IMPORT INSERT ne ERROR - {}", errorId, addr.getName());
                importCount++;
                continue;
            }
            catch (Exception e){
                String errorId = ErrorTraceLogger.log(e);
                log.error("{} - ADDRESS IMPORT INSERT ERROR - {}", errorId, addr.getName());
                importCount++;
                continue;
            }
        }
        resultForm.setImportCount( importCount );
        resultForm.setSuccessCount( successCount );
        resultForm.setBlankNameList( blankNameList );
        resultForm.setEmailAddressErrorList( emailAddressErrorList );
        resultForm.setNumberErrorList(numberErrorList);
        return  resultForm;
    }



    /**
     * 주소록 그룹명 존재하는지 확인
     * @param gname
     * @param userid
     * @return
     */
    public boolean isExistGname(String gname, String userid) throws Exception {
        boolean isExist = false;
        int rs = addressMapper.isExistGname(gname,userid);
        if(rs > 0){
            isExist=true;
        }
        return isExist;
    }

    /**
     * 한 그룹에 중복 이메일 개수 획득
     * @param email
     * @param gkey
     * @param userid
     * @return
     */
    public int getAddressCountByEmailAndGkey(String email, int gkey, String userid) {
        return addressMapper.getAddressCountByEmailAndGkey(email,gkey,userid);
    }

    /**
     * 개인주소록 정보 복구
     * @param userid
     * @return
     */
    public void updateAddressCount(String userid){

        List<ImbAddrGrp> addressCountAll;
        try {

            addressCountAll = addressMapper.getAddressCountAll(userid);
            for( ImbAddrGrp addrGrp: addressCountAll){
                addressMapper.updateAddressCount(addrGrp.getGkey(), addrGrp.getGrpcount(), userid);
            }
        }catch(NullPointerException ne){
            String errorId = ErrorTraceLogger.log(ne);
            log.error("{} - Address Setting ERROR", errorId);
        }catch (Exception e) {
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - Address Setting ERROR", errorId);
        }
    }
}

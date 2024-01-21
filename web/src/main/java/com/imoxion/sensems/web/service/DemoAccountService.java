package com.imoxion.sensems.web.service;

import com.imoxion.common.util.ImUtils;
import com.imoxion.sensems.web.database.domain.ImbDemoAccount;
import com.imoxion.sensems.web.database.mapper.DemoAccountMapper;
import com.imoxion.sensems.web.form.DemoAccountForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DemoAccountService {

    @Autowired
    private DemoAccountMapper demoAccountMapper;

    /**
     * 테스트계정 목록 개수
     *
     * @param userid
     * @param srch_keyword
     * @throws Exception
     * */
    public int getDemoAccountCount(String userid, String srch_keyword){
        return demoAccountMapper.selectDemoAccountCount(userid, srch_keyword);
    }

    /**
     * 테스트계정 목록
     *
     * @param userid
     * @param srch_keyword
     * @param start
     * @param end
     * @throws Exception
     * */
    public List<ImbDemoAccount> getDemoAccountList(String userid, String srch_keyword, int start, int end){
        return demoAccountMapper.selectDemoAccountList(userid, srch_keyword, start, end);
    }

    /**
     * email, userid로 이미 등록된 계정이 있는지 체크
     *
     * @param email
     * @param userid
     * @throws Exception
     * */
    public int getDemoAccountByEmail(String email, String userid){
        return demoAccountMapper.selectDemoAccountByEmail(email, userid);
    }

    /**
     * 테스트 계정 추가
     *
     * @param form
     * @throws Exception
     * */
    public int addDemoAccount(DemoAccountForm form) throws Exception{
        ImbDemoAccount imbDemoAccount = new ImbDemoAccount();
        String ukey= ImUtils.makeKeyNum(24);
        imbDemoAccount.setUkey(ukey);
        imbDemoAccount.setEmail(form.getEmail().toLowerCase());
        imbDemoAccount.setFlag(form.getFlag());
        imbDemoAccount.setUserid(form.getUserid());
        return demoAccountMapper.addDemoAccount(imbDemoAccount);
    }

    /**
     * ukey, email로 현재 선택한 ukey 외에 중복된 email이 존재하는지 체크
     *
     * @param ukey
     * @param email
     * @throws Exception
     * */
    public int selectEditDemoAccount(String ukey, String email) throws Exception {
        return demoAccountMapper.selectEditDemoAccount(ukey, email);
    }

    /**
     * 테스트 계정 수정
     *
     * @param form
     * @throws Exception
     * */
    public int editDemoAccount(DemoAccountForm form) throws Exception {
        ImbDemoAccount imbDemoAccount = new ImbDemoAccount();
        imbDemoAccount.setUkey(form.getUkey());
        imbDemoAccount.setUserid(form.getUserid());
        imbDemoAccount.setEmail(form.getEmail().toLowerCase());
        imbDemoAccount.setFlag(form.getFlag());
        return  demoAccountMapper.editDemoAccount(imbDemoAccount);
    }

    /**
     * 테스트 계정 삭제
     *
     * @param ukey
     * @throws Exception
     * */
    public void deleteDemoAccount(String ukey) throws Exception {
        demoAccountMapper.deleteDemoAccount(ukey);
    }

    public List<ImbDemoAccount> getDemoAccountListForUserid(String userid){
        return demoAccountMapper.getDemoAccountList(userid);
    }

}

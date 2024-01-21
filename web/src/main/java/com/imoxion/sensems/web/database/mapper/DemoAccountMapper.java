package com.imoxion.sensems.web.database.mapper;

import com.imoxion.sensems.web.database.domain.ImbDemoAccount;
import org.apache.ibatis.annotations.Param;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.security.core.parameters.P;

import java.util.List;

@MapperScan
public interface DemoAccountMapper {

    /**
     * 테스트계정 목록 개수
     *
     * @param userid
     * @param srch_keyword
     * @throws Exception
     * */
    public int selectDemoAccountCount(@Param("userid") String userid, @Param("srch_keyword") String srch_keyword);

    /**
     * 테스트계정 목록
     *
     * @param userid
     * @param srch_keyword
     * @param start
     * @param end
     * @throws Exception
     * */
    public List<ImbDemoAccount> selectDemoAccountList(@Param("userid") String userid, @Param("srch_keyword") String srch_keyword, @Param("start") int start, @Param("end") int end);


    public List<ImbDemoAccount> getDemoAccountList(@Param("userid") String userid);

    /**
     * email, userid로 이미 등록된 계정이 있는지 체크
     *
     * @param email
     * @param userid
     * @throws Exception
     * */
    public int selectDemoAccountByEmail(@Param("email") String email, @Param("userid") String userid);

    /**
     * 테스트 계정 추가
     *
     * @param imbDemoAccount
     * @throws Exception
     * */
    public int addDemoAccount(ImbDemoAccount imbDemoAccount);

    /**
     * ukey, email로 현재 선택한 ukey 외에 중복된 email이 존재하는지 체크
     *
     * @param ukey
     * @param email
     * @throws Exception
     * */
    public int selectEditDemoAccount(@Param("ukey") String ukey, @Param("email") String email);

    /**
     * 테스트 계정 수정
     *
     * @param imbDemoAccount
     * @throws Exception
     * */
    public int editDemoAccount(ImbDemoAccount imbDemoAccount);

    /**
     * 테스트 계정 삭제
     *
     * @param ukey
     * @throws Exception
     * */
    public void deleteDemoAccount(@Param("ukey") String ukey);


}

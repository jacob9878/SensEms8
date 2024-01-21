package com.imoxion.sensems.web.database.mapper;

import com.imoxion.sensems.web.beans.AddrSelBean;
import com.imoxion.sensems.web.database.domain.ImbAddr;
import com.imoxion.sensems.web.database.domain.ImbAddrGrp;
import org.apache.ibatis.annotations.Param;
import org.mybatis.spring.annotation.MapperScan;

import java.util.List;


/**
 * 주소록 Mapper
 * @date 2021.02.01
 * @author jhpark
 *
 */
@MapperScan
public interface AddressMapper {


    /**
     * 개인 주소록 테이블 생성
     * @param userid
     */
    public void createAddrTable(@Param("userid") String userid) throws Exception;

    /**
     * 개인 주소록 그룹 테이블 생성
     * @param userid
     */
    public void createAddrGrpTable(@Param("userid")String userid) throws Exception;

    /**
     * 개인 주소록 테이블 삭제
     * @param userid
     */
    void dropAddrTable(@Param("userid")String userid) throws Exception;

    /**
     * 개인 주소록 그룹 테이블 삭제
     * @param userid
     */
    void dropAddrGrpTable(@Param("userid")String userid) throws Exception;

    /**
     * 발송 분류 데이터 삭제
     * @param userid
     */
    void deleteCategory(@Param("userid")String userid) throws Exception;

    /**
     * 발송 분류 데이터 삭제
     * @param userid
     */
    void deleteReceiver(@Param("userid")String userid) throws Exception;

    /**
     * 테스트 계정 삭제
     * @param userid
     */
    void deleteTestAccount(@Param("userid")String userid) throws Exception;

    /**
     * 추가한 데이터베이스 정보 삭제
     * @param userid
     */
    void deleteDbinfo(@Param("userid")String userid) throws Exception;

    /**
     * 템플릿 정보 삭제
     * @param userid
     */
    void deleteTemplate(@Param("userid")String userid) throws Exception;

    /**
     * 주소록 검색 목록 총 개수를 구한다.
     * @param srch_type
     * @param srch_keyword
     * @param gkey
     * @return
     */
    int getAddressCountForSearch(@Param("srch_type")String srch_type, @Param("srch_keyword")String srch_keyword,
                              @Param("userid")String userid, @Param("gkey")int gkey) throws Exception;

    /**
     * 주소록 검색 목록 정보를 획득한다.
     * @param srch_type
     * @param srch_keyword
     * @param userid
     * @param gkey
     * @param start
     * @param end
     * @return
     * @throws Exception
     */
    List<ImbAddr> getAddressListForPageing(@Param("srch_type")String srch_type, @Param("srch_keyword")String srch_keyword,
                                           @Param("userid")String userid, @Param("gkey")int gkey,
                                           @Param("start")int start, @Param("end")int end) throws Exception;

    /**
     * 주소록 그룹 목록 획득
     * @return
     * @throws Exception
     */
    List<ImbAddrGrp> getAddressGroupList(@Param("userid")String userid) throws Exception;

    /**
     * 주소록 그룹 목록 획득 (페이징)
     * @param userid
     * @param start
     * @param end
     * @return
     */
    List<ImbAddrGrp> getAddressGroupListForPaging(@Param("userid")String userid,@Param("start")int start, @Param("end")int end);

    /**
     * 주소록 그룹 목록 개수 획득
     * @param userid
     * @return
     */
    int getAddressGroupListCount(@Param("userid")String userid);

    /**
     * 주소록 테이블에서 그룹에 속한 정보 개수 획득
     * @param userid
     * @param gkey
     * @return
     * @throws Exception
     */
    int getAddressCountByGkey(@Param("userid")String userid, @Param("gkey")int gkey) throws Exception;

    String getGname(@Param("userid")String userid, @Param("gkey")int gkey) throws Exception;

    /**
     * 모든 주소록 데이터 획득
     * @param userid
     * @return
     * @throws Exception
     */
    List<ImbAddr> getAllAddressList(@Param("userid")String userid) throws Exception;

    /**
     * 그룹키를 이용한 주소록 데이터 획득
     * @param userid
     * @return
     * @throws Exception
     */
    List<ImbAddr> getAddressListByGkey(@Param("userid")String userid, @Param("gkey")int gkey) throws Exception;

    /**
     * 그룹키를 이용한 주소록 데이터 획득 (페이징)
     * @param userid
     * @return
     * @throws Exception
     */
    List<ImbAddr> getAddressListByGkey2(@Param("userid")String userid, @Param("gkey")int gkey,@Param("start")int start, @Param("end")int end) throws Exception;

    /**
     * 그룹 키를 이용한 주소록 그룹 정보 획득
     * @param userid
     * @param gkey
     * @return
     */
    ImbAddrGrp getAddressGrpByGkey(@Param("userid")String userid, @Param("gkey")int gkey) throws Exception;

    /**
     * 그룹 이름을 이용한 주소록 그룹 정보 획득
     * @param userid
     * @param gname
     * @return
     */
    ImbAddrGrp getAddressGrpByGname(@Param("userid")String userid, @Param("gname")String gname) throws Exception;

    /**
     * 주소록 그룹 팝업에서 검색을 통한 주소록 정보 획득
     * @param userid
     * @param type
     * @param keyword
     * @return
     * @throws Exception
     */
    List<ImbAddr> getAddressListByKeyword(@Param("userid")String userid, @Param("type")String type, @Param("keyword")String keyword) throws Exception;

    /**
     * 주소록 그룹 정보 추가
     * @param addrGrp
     * @param userid
     */
    void insertAddressGrp(@Param("addrGrp")ImbAddrGrp addrGrp, @Param("userid")String userid) throws Exception;

    /**
     * 그룹 키를 이용하여 주소록 데이터 삭제
     * @param gkey
     * @throws Exception
     */
    void deleteAddrByGkey(@Param("userid")String userid, @Param("gkey")int gkey) throws Exception;

    /**
     * ukey를 이용하여 주소록 데이터 획득
     * @param userid
     * @param ukey
     * @throws Exception
     */
    ImbAddr getAddressByUkey(@Param("userid")String userid, @Param("ukey")int ukey) throws Exception;

    /**
     * 주소록 데이터 추가
     * @param addrInfo
     * @param userid
     */
    void insertAddress(@Param("addr")ImbAddr addrInfo, @Param("userid")String userid) throws Exception;

    /**
     * ukey를 이용하여 주소록 데이터 삭제
     * @param userid
     * @param ukey
     * @throws Exception
     */
    void deleteaddrByUkey(@Param("userid")String userid, @Param("ukey")int ukey) throws Exception;

    /**
     * 주소록 데이터 수정 실시
     * @param addr
     * @throws Exception
     */
    void updateAddress(@Param("addr")ImbAddr addr,@Param("userid")String userid) throws Exception;

    /**
     * 주소록 그룹 삭제
     * @param userid
     * @param gkey
     * @throws Exception
     */
    void deleteAddrGrpByGkey(@Param("userid")String userid,@Param("gkey")int gkey) throws Exception;

    /**
     * 주소록 그룹 데이터 수정 실시
     * @param addrGrp
     * @param userid
     */
    void updateAddressGrp(@Param("addrgrp")ImbAddrGrp addrGrp, @Param("userid")String userid)throws Exception;

    /**
     * imb_addrsel 테이블에 데이터 insert
     * @param bean
     * */
    public void insertAddrSel(@Param("addrSelBean") AddrSelBean bean) throws Exception;

    /**
     * 주소록 그룹명 존재하는지 체크
     * @param gname
     * @param userid
     * @return
     */
    int isExistGname(@Param("gname")String gname, @Param("userid")String userid) throws Exception;

    /**
     * E-MAIL을 이용한 주소록 데이터 획득
     * @param email
     * @param userid
     * @return
     * @throws Exception
     */
    List<Integer> getGkeyListByEmail(@Param("email")String email, @Param("userid")String userid) throws Exception;

    List<Integer> getGkeyListByGkey(@Param("email")String email, @Param("userid")String userid, @Param("gkey")String gkey) throws Exception;

    List<Integer> getGkeyListByGkeyForEdit(@Param("email")String email, @Param("userid")String userid, @Param("gkey")String gkey, @Param("ukey")String ukey) throws Exception;

    List<ImbAddrGrp> getGkeyByuserid(@Param("userid")String userid) throws Exception;

    String getEmailByGkey(@Param("userid")String userid, @Param("gkey")String gkey) throws Exception;

    /**
     * 그룹에 속한 Email 주소 갯수 획득
     * @param email
     * @param gkey
     * @param userid
     * @return
     */
    int getAddressCountByEmailAndGkey(@Param("email")String email, @Param("gkey")int gkey, @Param("userid")String userid);

    public void createTagInsertTrigger(@Param("userid") String userid);

    public void createTagDeleteTrigger(@Param("userid") String userid);

    public void createTagUpdateTrigger(@Param("userid") String userid);

    void updateAddressCount(@Param("gkey")int gkey, @Param("grpcount")int grpcount, @Param("userid")String userid);

    List<ImbAddrGrp> getAddressCountAll(@Param("userid") String userid) throws Exception;

}

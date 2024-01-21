package com.imoxion.sensems.web.database.mapper;


import com.imoxion.sensems.web.beans.AttachBean;
import java.util.Date;
import org.apache.ibatis.annotations.Param;
import org.mybatis.spring.annotation.MapperScan;

import java.util.List;

/**
 * jooyoung
 * 2022. 03. 29
 * 첨부파일 확장자 관리 Mapper 클래스
 */
@MapperScan
public interface AttachMapper {
    /**
     * 모든 첨부파일 목록을 가져온다.
     *
     */
    public List<AttachBean> getAttachInfoList(@Param("searchText") String searchText, @Param("start")int start, @Param("end") int end) throws Exception;

    /**
     * 첨부파일 목록 수를 제공한다.
     *
     */
    public int getAttachCount(@Param("searchText") String searchText) throws  Exception;

    /**
     * 특정 파일의 정보를 취득
     *
     * @Method Name : getFileInfo
     * @Method Comment :
     *
     * @param ukey
     * @return
     */
    public AttachBean getFileInfo(@Param("ekey") String ukey);

    public int deleteFileInfo(@Param("ekey") String ukeys);

    public List<AttachBean> getAttachList(@Param("msgid") String msgid);

    public void expireDateUpdate(@Param("ekey") String ekey, @Param("expireDate") Date expireDate);

}

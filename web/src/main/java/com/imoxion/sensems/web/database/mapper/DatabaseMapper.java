package com.imoxion.sensems.web.database.mapper;

import com.imoxion.sensems.web.database.domain.ImbDBInfo;
import org.apache.ibatis.annotations.Param;
import org.mybatis.spring.annotation.MapperScan;

import java.util.List;

/**
 * yeji
 * 2021. 03. 03
 * 데이터베이스관리 관련 mapper 클래스
 */
@MapperScan
public interface DatabaseMapper {

    /**
     * 페이징 위한 전체 갯수
     * @return
     * @throws Exception
     */
    public int getListCount() throws Exception;

    /**
     * 페이징 및 초기 목록화면
     * @param start
     * @param end
     * @return
     * @throws Exception
     */
    public List<ImbDBInfo> getDBInfoListForPaging(@Param("start") int start, @Param("end") int end) throws Exception;

    /**
     * 데이터베이스 리스트 가져오기
     * @return
     * @throws Exception
     */
    public List<ImbDBInfo> getDBInfoList() throws Exception;

    /**
     * 데이터베이스 이름 중복 확인
     * @param userid
     * @param dbname
     * @return
     * @throws Exception
     */
    public int getCheckDBExist(@Param("userid") String userid, @Param("dbname") String dbname) throws Exception;

    /**
     * 데이터베이스 추가
     * @param dbInfo
     * @return
     * @throws Exception
     */
    public int insertDBInfo(ImbDBInfo dbInfo) throws Exception;

    /**
     * 특정 ukey 에 해당하는 데이터베이스 정보 가져오기
     * @param ukey
     * @return
     * @throws Exception
     */
    public ImbDBInfo getDBInfoByUkey(@Param("ukey") String ukey) throws Exception;

    /**
     * 데이터베이스 수정
     * @param dbInfo
     * @return
     * @throws Exception
     */
    public int updateDBInfo(ImbDBInfo dbInfo) throws Exception;

    /**
     * 데이터베이스 삭제
     * @param ukey
     * @return
     * @throws Exception
     */
    public int deleteDBInfo (@Param("ukey") String ukey) throws Exception;

}

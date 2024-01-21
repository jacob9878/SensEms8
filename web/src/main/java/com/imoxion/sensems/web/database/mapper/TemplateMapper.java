package com.imoxion.sensems.web.database.mapper;

import com.imoxion.sensems.web.database.domain.ImbTemplate;
import org.apache.ibatis.annotations.Param;
import org.mybatis.spring.annotation.MapperScan;

import java.util.List;

/**
 * 템플릿 관리 Mapper
 * @date 2021.02.23
 * @author jhpark
 *
 */
@MapperScan
public interface TemplateMapper {

    /**
     * 검색조건에 맞는 Template 개수를 구한다.
     * @param srch_type
     * @param srch_keyword
     * @param userid
     * @return
     */
    int getTemplateCount(@Param("srch_type")String srch_type, @Param("srch_keyword")String srch_keyword, @Param("userid")String userid) throws Exception;


    /**
     * 검색 조건에 맞는 Template 정보 목록을 구한다.
     * @param srch_type
     * @param srch_keyword
     * @param userid
     * @param start
     * @param end
     * @return
     */
    List<ImbTemplate> getTemplateListForPageing(@Param("srch_type")String srch_type, @Param("srch_keyword")String srch_keyword,@Param("userid")String userid, @Param("start")int start, @Param("end")int end) throws Exception;

    /**
     * ukey에 맞는 Template 정보를 구한다.
     * @param ukey
     * @return
     */
    ImbTemplate getTemplate(@Param("ukey")String ukey) throws Exception;

    /**
     * ukey를 이용하여 Template 정보를 삭제한다.
     * @param ukey
     */
    void deleteTemplate(@Param("ukey")String ukey) throws Exception;

    /**
     * Template 정보 추가
     * @param templateInfo
     */
    void insertTemplate(ImbTemplate templateInfo) throws Exception;

    /**
     * 템플릿 정보 수정
     * @param templateInfo
     */
    void updateTemplate(ImbTemplate templateInfo) throws Exception;

    List<ImbTemplate> getTemplateList(@Param("userid") String userid) throws Exception;
}

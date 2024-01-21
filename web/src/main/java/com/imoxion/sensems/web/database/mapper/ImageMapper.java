package com.imoxion.sensems.web.database.mapper;

import com.imoxion.sensems.web.database.domain.ImbImage;

import org.apache.ibatis.annotations.Param;
import org.mybatis.spring.annotation.MapperScan;

import java.util.List;

/**
 * 이미지 관리 Mapper
 * @date 2021.02.19
 * @author jhpark
 *
 */
@MapperScan
public interface ImageMapper {

    /**
     * 검색조건에 맞는 이미지 개수를 구한다.
     * @param srch_type
     * @param srch_keyword
     * @param userid
     * @return
     */
    int getImageCount(@Param("srch_type")String srch_type, @Param("srch_keyword")String srch_keyword, @Param("userid")String userid) throws Exception;

    /**
     * 검색 조건에 맞는 이미지 정보 목록을 구한다.
     * @param srch_type
     * @param srch_keyword
     * @param userid
     * @param start
     * @param end
     * @return
     */
    List<ImbImage> getImageListForPageing(@Param("srch_type")String srch_type, @Param("srch_keyword")String srch_keyword,@Param("userid")String userid, @Param("start")int start, @Param("end")int end) throws Exception;

    List<ImbImage> deleteImageList(@Param("userid")String userid) throws Exception;

    /**
     * ukey에 맞는 이미지 정보를 구한다.
     * @param ukey
     * @return
     */
    ImbImage getImage(@Param("ukey") String ukey) throws Exception;

    /**
     * ukey를 이용하여 이미지 정보를 삭제한다.
     * @param ukey
     */
    void deleteImage(@Param("ukey")String ukey) throws Exception;

    /**
     * Image 정보를 추가한다.
     * @param imageInfo
     */
    void insertImage(ImbImage imageInfo) throws Exception;
}

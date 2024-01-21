package com.imoxion.sensems.web.database.mapper;

import com.imoxion.sensems.web.database.domain.ImbAttachRestrict;
import org.mybatis.spring.annotation.MapperScan;

import java.util.List;

/**
 * yeji
 * 2021. 03. 10
 * 첨부파일 확장자 관리 Mapper 클래스
 */
@MapperScan
public interface AttachRestrictMapper {

    /**
     * 모든 ext 조회
     * @return
     * @throws Exception
     */
    public List<ImbAttachRestrict> getExtInfo() throws Exception;

    /**
     * ext 저장
     * @param attachRestrictBean
     * @return
     * @throws Exception
     */
    public int insertExt(ImbAttachRestrict attachRestrictBean) throws Exception;


    /**
     * ext 초기화
     * @return
     * @throws Exception
     */
    public int deleteExtAll() throws Exception;

}

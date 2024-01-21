package com.imoxion.sensems.web.service;

import com.imoxion.sensems.web.database.domain.ImbAttachRestrict;
import com.imoxion.sensems.web.database.mapper.AttachRestrictMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * yeji
 * 2021. 03. 10
 * 첨부파일 확장자 관리 Service
 */
@Service
public class AttachRestrictService {

    private final Logger log = LoggerFactory.getLogger(AttachRestrictService.class);

    @Autowired
    private AttachRestrictMapper attachRestrictMapper;

    /**
     * 모든 ext 조회
     * @return
     * @throws Exception
     */
    public List<ImbAttachRestrict> getExtInfo() throws Exception{
        return attachRestrictMapper.getExtInfo();
    }

    /**
     * ext 저장
     * @param attachRestrictBean
     * @return
     * @throws Exception
     */
    public int insertExt(ImbAttachRestrict attachRestrictBean) throws Exception{
        return attachRestrictMapper.insertExt(attachRestrictBean);
    }

    /**
     * ext 초기화
     * @return
     * @throws Exception
     */
    public int deleteExtAll() throws Exception{
        return attachRestrictMapper.deleteExtAll();
    }

}

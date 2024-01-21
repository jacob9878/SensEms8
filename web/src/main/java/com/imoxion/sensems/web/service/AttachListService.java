package com.imoxion.sensems.web.service;

import com.imoxion.sensems.web.beans.AttachBean;
import com.imoxion.sensems.web.database.mapper.AttachMapper;
import java.util.Date;

import com.imoxion.sensems.web.form.ActionLogForm;
import com.imoxion.sensems.web.util.HttpRequestUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class AttachListService {

    private final Logger log = LoggerFactory.getLogger(AttachListService.class);

    @Autowired
    private AttachMapper attachMapper;

    @Autowired
    private ActionLogService actionLogService;

    /**
     * 모든 첨부파일 조회
     * @return
     * @throws Exception
     */
    public List<AttachBean> getAttachInfoList(String searchText, int start, int end) throws Exception{
        return attachMapper.getAttachInfoList(searchText, start, end);
    }

    /**
     * 첨부파일 목록 수를 제공한다.
     *
     */
    public int getAttachCount(String searchText) throws Exception{
        return attachMapper.getAttachCount(searchText);
    }

    /**
     * 첨부파일 ekey를 가지고 삭제한다.
     *
     *
     */
    public void deleteAttachInfoByEkey(HttpServletRequest request,String userid, String[] ekeys) throws Exception{
        if( ekeys == null ){
            return;
        }

        for (int i = 0; i < ekeys.length; i++) {
            //파일 삭제
            AttachBean attachinfo = getFileInfo(ekeys[i]);
            if(attachinfo == null) continue;
            attachMapper.deleteFileInfo(ekeys[i]);

            //log insert start
            ActionLogForm logForm = new ActionLogForm();
            logForm.setIp(HttpRequestUtil.getRemoteAddr(request));
            logForm.setUserid(userid);
            logForm.setMenu_key("G601");
            logForm.setParam("삭제 첨부파일 명 : " + attachinfo.getFile_name() + " / 삭제 첨부파일 키 : " + ekeys[i]);
            actionLogService.insertActionLog(logForm);
        }

    }

    /**
     * 파일 정보를 제공한다.
     *
     * @Method Name : getFileInfo
     * @Method Comment :
     *
     * @param ekey
     * @return
     * @throws Exception
     */
    public AttachBean getFileInfo(String ekey) throws Exception {

        if (StringUtils.isEmpty(ekey)) {
            log.error("getFileInfo error = {}",ekey);
        }
        return attachMapper.getFileInfo(ekey);
    }

    /**
     * 첨부파일 만료일을 수정한다.
     *
     */
    public void expireDateUpdate(String ekey, Date expireDate) throws Exception{
        attachMapper.expireDateUpdate(ekey, expireDate);
    }


}

package com.imoxion.sensems.web.service;

import com.imoxion.security.ImSecurityLib;

import com.imoxion.sensems.web.beans.EmsBean;
import com.imoxion.sensems.web.common.ImbConstant;
import com.imoxion.sensems.web.database.domain.ImbReceiver;
import com.imoxion.sensems.web.database.domain.ImbUserinfo;
import com.imoxion.sensems.web.database.mapper.ReceiverMapper;
import com.imoxion.sensems.web.form.ReceiverGroupForm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReceiverService {

    protected Logger log = LoggerFactory.getLogger(ReceiverService.class);


    @Autowired
    private ReceiverMapper receiverMapper;

    public ArrayList<ImbReceiver> getReceiverList() throws Exception {
        return receiverMapper.getReceiverList();
    }

    /**
     * 수신그룹 목록 총 수 획득
     * @param srch_type
     * @param srch_keyword
     * @return
     */
    public int getReceiverGroupCount(String srch_type, String srch_keyword, String userid) throws Exception {
        return receiverMapper.getReceiverGroupCount(srch_type,srch_keyword,userid);
    }

    /**
     * 수신그룹 ukey와 recid가 같은 디테일 페이지 총 수를 구한다
     * @parma recid
     */
    public int getReceiverGroupDetailCount(String recid) throws Exception {
        return receiverMapper.getReceiverGroupDetailCount(recid);
    }

    /**
     * 주소록 userid와 gkey가 같은 디테일 페이지 총 수를 구한다
     * @parma userid
     * @parma gkey
     */
    public int getReceiverAddrDetailCount(String userid, int gkey) throws Exception {
        return receiverMapper.getReceiverAddrDetailCount(userid,gkey);
    }

    /**
     * 수신그룹 목록 페이징해서 가져온다.
     * @param srch_type
     * @param srch_keyword
     * @param start
     * @param end
     * @return
     */
    public List<ImbReceiver> getReceiverGroupForPageing(String srch_type, String srch_keyword, int start, int end, String userid) throws Exception{
        return receiverMapper.getReceiverGroupForPageing(srch_type,srch_keyword,start,end,userid);
    }

    /**
     * 수신그룹 데이터 추가
     * @param receiverInfo
     * @throws Exception
     */
    public void insertReceiverInfo(ImbReceiver receiverInfo) throws Exception {
        receiverMapper.insertReceiver(receiverInfo);
    }

    /**
     * ukey를 이용하여 수신그룹 정보 획득
     * @param ukey
     * @return
     * @throws Exception
     */
    public ImbReceiver getReceiverGroup(String ukey) throws Exception {
        ImbReceiver receiverInfo = receiverMapper.getReceiverGroup(ukey);
        if(ImbConstant.DATABASE_ENCRYPTION_USE){
            receiverInfo.setQuery(ImSecurityLib.decryptAES256(ImbConstant.DATABASE_AES_KEY, receiverInfo.getQuery()));
        }
        return receiverInfo;
    }

    public List<ImbUserinfo> getReceiverUserinfo(String userid) throws Exception {
        List<ImbUserinfo> userInfo = receiverMapper.getReceiverUserinfo(userid);
        return userInfo;
    }

    public ImbReceiver getReceiverGroupRecid(String recid) throws Exception {
        ImbReceiver receiverInfo = receiverMapper.getReceiverGroupRecid(recid);
        return receiverInfo;
    }


    /**
     * 선택한 수신그룹 삭제
     * @param ukeys
     * @return
     */
    public void deleteReceiverGroupList(String[] ukeys) throws Exception {
        for(int i=0;i<ukeys.length;i++) {
            receiverMapper.deleteReceiverGroup(ukeys[i].split(",")[0]);
        }
    }

    /**
     * ReceiverGroup 객체를 ReceiverGroupForm으로 맵핑
     * @param receiverInfo
     * @return
     */
    public ReceiverGroupForm receiverInfoToForm(ImbReceiver receiverInfo) throws Exception {
        ReceiverGroupForm form = new ReceiverGroupForm();

        form.setUkey(receiverInfo.getUkey());
        form.setDbkey(receiverInfo.getDbkey());
        form.setQuery(receiverInfo.getQuery());
        form.setRecv_name(receiverInfo.getRecv_name());

        return form;
    }

    /**
     * 수신그룹 정보 수정
     * @param receiverInfo
     * @throws Exception
     */
    public void updateReceiverInfo(ImbReceiver receiverInfo) throws Exception{
        receiverMapper.updateReceiver(receiverInfo);
    }

    /**
     * 수신그룹명 사용가능한 특수문자 체크
     * @param recv_name
     * @return
     */
    public boolean validCharacter(String recv_name) {
        boolean isValid = true;
        if (recv_name.indexOf("'") != -1 || recv_name.indexOf('"') != -1 || recv_name.indexOf('@') != -1 || recv_name.indexOf(',') != -1
                || recv_name.indexOf('?') != -1 || recv_name.indexOf('<') != -1 || recv_name.indexOf('>') != -1 || recv_name.indexOf(';') != -1
                || recv_name.indexOf(':') != -1 || recv_name.indexOf('/') != -1 || recv_name.indexOf('(') != -1 || recv_name.indexOf(')') != -1
                || recv_name.indexOf('+') != -1 || recv_name.indexOf('|') != -1 || recv_name.indexOf('\\') != -1 || recv_name.indexOf('*') != -1
                || recv_name.indexOf('&') != -1 || recv_name.indexOf('^') != -1 || recv_name.indexOf('%') != -1 || recv_name.indexOf('$') != -1
                || recv_name.indexOf('!') != -1 || recv_name.indexOf('~') != -1 || recv_name.indexOf('#') != -1 || recv_name.indexOf('=') != -1
                || recv_name.indexOf('`') != -1 || recv_name.indexOf('{') != -1 || recv_name.indexOf('}') != -1 || recv_name.indexOf('[') != -1
                || recv_name.indexOf(']') != -1)  {
            isValid = false;
        }
        return isValid;
    }

    /**
     * ukey 값으로 dbkey 값을 구한다.
     * @param ukey
     * @return
     * */
    public String getDbKey(String ukey) throws Exception{
    	return receiverMapper.getDbKey(ukey);
    }

    /**
     * msgid로 메시지 정보를 가져온다.
     * @param msgid
     * @return
     * @throws Exception
     */
    public EmsBean getMsginfo(String msgid) throws Exception{
        EmsBean emsBean = receiverMapper.getMsginfo(msgid);
        return emsBean;
    }
}

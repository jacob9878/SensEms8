package com.imoxion.sensems.web.service;

import com.imoxion.common.util.ImConfLoaderEx;
import com.imoxion.common.util.ImStringUtil;
import com.imoxion.security.ImSecurityLib;
import com.imoxion.sensems.common.logger.ErrorTraceLogger;
import com.imoxion.sensems.web.beans.*;
import com.imoxion.sensems.web.common.ImbConstant;
import com.imoxion.sensems.web.database.mapper.AttachMapper;
import com.imoxion.sensems.web.database.mapper.SendResultMapper;
import com.imoxion.sensems.web.form.LinkListForm;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.sql.SQLException;
import java.util.*;


@Service
public class SendResultService {
    protected Logger log = LoggerFactory.getLogger(SendResultService.class);

    ImConfLoaderEx conf = ImConfLoaderEx.getInstance("sensems.home","sensems.xml");

    @Autowired
    private MessageSourceAccessor message;

    @Autowired
    SendResultMapper sendResultMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    AttachMapper attachMapper;

    /**
     * 조건에 맞는 발송결과 목록을 가져온다.
     *
     * @param userid
     * @return
     */
    public List<EmsBean> getSendResultList(String srch_keyword, String userid, String categoryid, String state, int start, int end) throws Exception {
        return sendResultMapper.getSendResultList(srch_keyword, userid, categoryid, state, start, end);
    }

    /**
     * 조건에 맞는발송결과 개수를 가져온다.
     *
     * @param srch_keyword
     * @param userid
     * @param categoryid
     * @return
     */
    public int getsendResultCount(String srch_keyword, String userid, String categoryid, String state) {
        int count = 0;
        try {
            count = sendResultMapper.getsendResultCount(srch_keyword, userid, categoryid, state);
        } catch(Exception e){
            count = 0;
        }
        return count;
    }

    /**
     * 카테고리 이동 (조건에 해당되는 카테고리로 업데이트한다.)
     *
     * @param msgid
     * @param categoryid
     */
    public void categoryMove(String msgid, String categoryid) {
        sendResultMapper.categoryMove(msgid, categoryid);
    }

    /**
     * 조건에 일치하는 발송결과를 가져온다.
     * @param msgid
     * @return
     */
    public EmsBean getEmsForMsgid(String msgid) {
        return sendResultMapper.getEmsForMsgid(msgid);
    }

    /**
     * 선택된 발송결과들을 제거한다.
     * @param msgids
     * @param userInfoBean
     * @return
     */
    public synchronized boolean deleteResultList(String[] msgids, UserInfoBean userInfoBean){
        boolean checker = false;

        String emlPath = conf.getProfileString("general", "msg_path");
        String filePath = conf.getProfileString("attach", "path");

        String permission = userInfoBean.getPermission();
        String userid = userInfoBean.getUserid();

        for (int i = 0; i < msgids.length; i++) {
            EmsBean emsBean = sendResultMapper.getEmsForMsgid(msgids[i].split(",")[0]);
            List<AttachBean> attachBean = attachMapper.getAttachList(msgids[i].split(",")[0]);


            if (UserInfoBean.UTYPE_ADMIN.equals(permission)) { // 삭제 권한이 존재하는 사용자인지 권한 체크
                try {
                    File eml = new File(emlPath + emsBean.getMsg_path());
                    if (eml.exists() && eml.isFile()) {
                        boolean deleteOK = eml.delete();
                        if(deleteOK){
                            log.info("Eml Delete OK - eml : {}",eml);
                        }else {
                            log.info("Eml File Delete False - eml : {}",eml);
                            continue;
                        }
                    }
                }catch (NullPointerException e){
                    String errorid = ErrorTraceLogger.log(e);
                    log.error("deleteOK NuLLPointerError : {}",errorid);
                }catch (Exception e1){
                    String errorid = ErrorTraceLogger.log(e1);
                    log.error("deleteOK Checkerror : {}",errorid);
                }
                sendResultMapper.resultDelete(msgids[i].split(",")[0]);   // 메시지 및 본문을 삭제한다.
                sendResultMapper.msginfoDelete(msgids[i].split(",")[0]); // imb_msg_info 데이터 삭제

                for(AttachBean attach : attachBean){
                    File file = new File(filePath + File.separator + attach.getFile_path());
                    if( file.exists() && file.isFile() ){
                        boolean deleteOK = file.delete();
                        if(deleteOK){
                            log.info("File Delete OK - file : {}",file);
                        }else {
                            //파일이 존재하지만 삭제에 실패했을 경우
                            log.info("File File Delete False - file : {}",file);
                            continue;
                        }
                    }
                    attachMapper.deleteFileInfo(attach.getEkey());
                }

                //delete teble
                sendResultMapper.errorCountDelete(msgids[i].split(",")[0]);
                sendResultMapper.linkCountDelete(msgids[i].split(",")[0]);
                sendResultMapper.linkInfoDelete(msgids[i].split(",")[0]);
                sendResultMapper.receiptCountDelete(msgids[i].split(",")[0]);
                sendResultMapper.addrselDelete(msgids[i].split(",")[0]);
                //drop table
                sendResultMapper.recvDropTable(msgids[i].split(",")[0]);
                sendResultMapper.hcDropTable(msgids[i].split(",")[0]);
                sendResultMapper.linklogDropTable(msgids[i].split(",")[0]);

                checker = true;
            } else { // 삭제권한이 없다면 게시자 본인인지 체크

                if (emsBean != null) {
                    if (emsBean.getUserid().equals(userid)) { // 등록된 값과 시도하는 사용자 아이디가 같다면 권한이 있는 사용자로 판단
                        try{
                            File eml = new File(emlPath + emsBean.getMsg_path());
                            if (eml.exists() && eml.isFile()) {
                                boolean deleteOK = eml.delete();
                                if(deleteOK){
                                    //파일이 존재하여 삭제 했을 경우
                                    log.info("eml Delete OK - eml : {}",eml);
                                }else {
                                    //파일이 존재하지만 삭제에 실패했을 경우
                                    log.info("eml File Delete False - eml : {}",eml);
                                    continue;
                                }
                            }else{
                                //파일이 존재하지 않을 경우
                                log.info("eml File is Not Exist - eml : {}",eml);
                            }
                        }catch (NullPointerException ne){
                            String errorId = ErrorTraceLogger.log(ne);
                            log.error("deleteOK Checkerror : {}",errorId);
                        }catch (Exception e){
                            String errorId = ErrorTraceLogger.log(e);
                            log.error("deleteOK Checkerror : {}",errorId);
                        }

                        sendResultMapper.resultDelete(msgids[i].split(",")[0]);
                        sendResultMapper.msginfoDelete(msgids[i].split(",")[0]); // imb_msg_info 데이터 삭제

                        for(AttachBean attach : attachBean){
                            File file = new File(filePath + File.separator + attach.getFile_path());
                            if( file.exists() && file.isFile() ){
                                boolean deleteOK = file.delete();
                                if(deleteOK){
                                    log.info("File Delete OK - file : {}",file);
                                }else {
                                    //파일이 존재하지만 삭제에 실패했을 경우
                                    log.info("File File Delete False - file : {}",file);
                                    continue;
                                }
                            }
                            attachMapper.deleteFileInfo(attach.getEkey());
                        }

                        //delete teble
                        sendResultMapper.errorCountDelete(msgids[i].split(",")[0]);
                        sendResultMapper.linkCountDelete(msgids[i].split(",")[0]);
                        sendResultMapper.linkInfoDelete(msgids[i].split(",")[0]);
                        sendResultMapper.receiptCountDelete(msgids[i].split(",")[0]);
                        sendResultMapper.addrselDelete(msgids[i].split(",")[0]);

                        //drop table
                        sendResultMapper.recvDropTable(msgids[i].split(",")[0]);
                        sendResultMapper.hcDropTable(msgids[i].split(",")[0]);
                        sendResultMapper.linklogDropTable(msgids[i].split(",")[0]);

                        checker = true;
                    }
                } else { // 예외로 null 인 경우이므로 false로 리턴
                    checker = false;
                    return checker;
                }
            } // else end
        } // if end

        return checker;
    }

    /**
     * 선택된 메일발송결과 값을 재발신 상태로 만든다.
     * @param msgid
     */
    public void doResend(String msgid) {
        sendResultMapper.doResend(msgid);
    }

    /**
     * 선택된 메일발송결과 값을 중지 상태로 만든다.
     * @param msgid
     */
    public void doStop(String msgid) {
        sendResultMapper.doStop(msgid);
    }

    /**
     * 수신자 카운트 테이블 정보를 가져온다.
     * @param msgid
     * @return
     */
    public int getReceiptCountBean(String msgid) {
        int count = 0;
        try {
            count = sendResultMapper.getReceiptCountBean(msgid);
        } catch(Exception e){
            count = 0;
        }
       return count;
    }

    /**
     * 해당되는 메시지 아이디의 생성된 수신자 테이블의 데이터 정보를 가져온다.
     * @param msgid
     * @return
     */
    public List<RecvMessageIDBean> getRecvMessageIDForMsgid(String msgid) throws Exception {
        List<RecvMessageIDBean> list = sendResultMapper.getRecvMessageIDForMsgid(msgid);
        return getDectyptFiled(list);
    }

    /**
     * 해당되는 메시지 아이디의 생성된 수신자 테이블의 데이터 정보를 가져온다.
     * @param msgid
     * @return
     */
    public List<RecvMessageIDBean> getRecvMessageIDForMsgidFlag(String msgid, String flag) {
        return sendResultMapper.getRecvMessageIDForMsgidFlag(msgid,flag);
    }

    /**
     * 해당되는 메시지 아이디의 생성된 수신자 데이터 정보를 가져온다.
     * @param msgid
     * @return
     */
    public List<RecvMessageIDBean> getRecvListForMsgid(String msgid, String recv_date) throws Exception {
        List<RecvMessageIDBean> recvList = sendResultMapper.getRecvListForMsgid(msgid, recv_date);
        return getDectyptFiled(recvList);
    }

    public RecvMessageIDBean getRecvForMsgid(String msgid, String id) throws Exception {
        RecvMessageIDBean recvMessageIDBean = sendResultMapper.getRecvForMsgid(msgid, id);
        recvMessageIDBean.setField1(ImSecurityLib.decryptAES256(ImbConstant.DATABASE_AES_KEY, recvMessageIDBean.getField1()));
        return recvMessageIDBean;
    }

    /**
     * 링크 개수 정보를 가져온다.
     * @param msgid
     * @return
     */
    public int getLinkCount(String msgid){
        int count = 0;
        try {
            count = sendResultMapper.getLinkCount(msgid);
        } catch(Exception e){
            count = 0;
        }
        return count;
    }

    public int getLinkCountForLinkid(String msgid, int linkid){
        int count = 0;
        try {
            count = sendResultMapper.getLinkCountForLinkid(msgid, linkid);
        } catch(Exception e){
            count = 0;
        }
        return count;
    }

    /**
     * 해당되는 메시지 아이디의 생성된 수신자 데이터 정보를 가져온다.
     * @param msgid
     * @return
     */
    public List<RecvMessageIDBean> getRecvListgetLinkLogMessageIDForMsgidForMsgid(String msgid, String recv_date) {
        return sendResultMapper.getRecvListForMsgid(msgid, recv_date);
    }

    /**
     * 해당되는 메시지 아이디의 생성된 수신자 테이블의 수신확인 일자 데이터 정보를 가져온다.
     * @param msgid
     * @return
     */
    public List<RecvMessageIDBean> getRecvCountIDForMsgid(String msgid) {
        try {
            return sendResultMapper.getRecvCountIDForMsgid(msgid);
        } catch (Exception e){
            return null;
        }
    }

    /**
     * 해당되는 메시지 아이디의 유동적으로 생성된 링크 테이블의 데이터 정보를 가져온다.
     * @param msgid
     * @return
     */
    public LinkLogMessageIDBean getLinkLogMessageIDForMsgid(String msgid) {
        return sendResultMapper.getLinkLogMessageIDForMsgid(msgid);
    }
  /**
   * 테이블 존재 여부 체크
   **/
    public int getLinkLogCheck(String msgid) {
        String tableName = "linklog_"+msgid;
        int count = 0;
        try {
            count = sendResultMapper.getLinkLogCheck(tableName);
        } catch(Exception e){
            count = 0;
        }
        return count;
    }


    /**
     * 링크클릭 수를 가져온다
     * @param msgid
     * @return
     */
    public int getClickCount(String msgid) {
        int count = 0;
        try {
            count = sendResultMapper.getClickCount(msgid);
        } catch(Exception e){
            count = 0;
        }
        return count;
    }

    /**
     * 해당되는 메시지 아이디의 유동적으로 생성된 링크 특정 테이블의 데이터 정보를 가져온다.
     * @param msgid
     * @return
     */
    public List<LinkLogMessageIDBean> getLinkLogMessageForMsgid(String msgid,String linkid) {
        return sendResultMapper.getLinkLogMessageForMsgid(msgid, linkid);
    }

    public String getLinkClickDate(String msgid,String linkid) {
        return sendResultMapper. getLinkClickDate(msgid, linkid);
    }

    /**
     * 해당되는 메시지 아이디의 유동적으로 생성된 링크정보 테이블의 데이터 정보를 가져온다.
     * @param msgid
     * @return
     */
    public List<LinkBean> getLinkMessageIDForMsgid(String msgid) {
        return sendResultMapper.getLinkMessageIDForMsgid(msgid);
    }

    /**
     * 해당되는 메시지 아이디의 유동적으로 생성된 링크정보 테이블의 데이터 정보를 가져온다.(페이징)
     * @param msgid
     * @param start
     * @param end
     * @return
     */
    public List<LinkBean> getLinkMessageIDForMsgid2(String msgid, int start, int end) {
        return sendResultMapper.getLinkMessageIDForMsgid2(msgid,start,end);
    }

    /**
     * 해당되는 메시지 아이디의 유동적으로 생성된 링크정보 테이블의 유저 데이터 정보를 가져온다.
     * @param form
     * @return
     */
    public List<LinkLogMessageIDBean> getLinkMessageUserForMsgid(LinkListForm form) throws Exception {
        List<LinkLogMessageIDBean> linkLogMessageIDBeans = sendResultMapper.getLinkMessageUserForMsgid(form);
        return getDectyptFiledForLinkLog(linkLogMessageIDBeans);
    }

    /**
     * 해당되는 메시지 아이디의 유동적으로 생성된 링크정보 테이블의 유저 데이터 정보를 가져온다.(페이징)
     * @param msgid
     * @param linkid
     * @param srch_type
     * @param srch_keyword
     * @param start
     * @param end
     * @return
     */
    public List<LinkLogMessageIDBean> getLinkMessageUserForMsgid2(String msgid, String linkid, String srch_type, String srch_keyword, int start, int end) throws Exception {
        try {
            List<LinkLogMessageIDBean> linkLogMessageIDBeans = sendResultMapper.getLinkMessageUserForMsgid2(msgid, linkid, srch_type, srch_keyword, start, end);
            if (ImbConstant.DATABASE_ENCRYPTION_USE) {
                for (LinkLogMessageIDBean bean : linkLogMessageIDBeans) {
                    decryptField1(bean);
                }
            }
            return linkLogMessageIDBeans;
        } catch(Exception e){
           // throw new NullPointerException("List<LinkLogMessageIDBean> is null");
            return null;
        }



//        return sendResultMapper.getLinkMessageUserForMsgid2(msgid, linkid, srch_type, srch_keyword, start, end);
    }

    /**
     * 해당되는 메시지 아이디의 메일별 에러 카운트 통계 정보를 가져온다.
     * @param msgid
     * @return
     */
    public ImbErrorCount getErrorCountForMsgid(String msgid) {
        return sendResultMapper.getErrorCountForMsgid(msgid);
    }

    /**
     * 해당되는 메시지 아이디의 발송 도메인 통계 정보를 가져온다.
     * @param msgid
     * @return
     */
    public List<HC_MessageIDBean> getHC_MessageIDForMsgid(String msgid) {
        return sendResultMapper.getHC_MessageIDForMsgid(msgid);
    }

    /**
     * 해당되는 메시지 아이디의 발송 도메인 통계 정보를 가져온다. (페이징)
     * @param msgid
     * @return
     */
    public List<HC_MessageIDBean> getHC_MessageIDPagingForMsgid(String msgid, int start, int end) {
        return sendResultMapper.getHC_MessageIDPagingForMsgid(msgid,start,end);
    }

    public int getHC_MessageIDForMsgidCount(String msgid) {
        int count = 0;
        try {
            count = sendResultMapper.getHC_MessageIDForMsgidCount(msgid);
        } catch(Exception e){
            count = 0;
        }
        return count;
    }

    /**
     * 발송 도메인 통계를 막대 그래프로 가져온다.
     * @param HC_MessageIDBean
     *
     */
    /* public LinkedHashMap<String, Integer>setHcDate(String msgid, HC_MessageIDBean hcData) {
        LinkedHashMap<String, Integer> setHcDate = new LinkedHashMap<String,Integer>();
        setHcDate.put("hcEration", getHC_MessageIDForMsgid(msgid).get);

        return  setHcDate;
    }*/

    /**
     * 에러메일 재발신 관련 해당 수신확인 테이블의 발송 성공 개수를 구한다.
     * @param msgid
     * @return
     */
    public int getRecvMessageIDSuccessAndFailCount(String msgid, String flag) {
        int count = 0;
        try {
            count = sendResultMapper.getRecvMessageIDSuccessAndFailCount(msgid, flag);
        } catch(Exception e){
            count = 0;
        }
        return count;
    }
    /**
     * 해당 수신확인 테이블의 발송 성공 개수를 구한다.
     * @param msgid
     * @return
     */
    public int getRecvMessageIDSendSuccessAndFailCount(String msgid, String flag) {
        int count = 0;
        try {
            count = sendResultMapper.getRecvMessageIDSendSuccessAndFailCount(msgid, flag);
        } catch(Exception e){
            count = 0;
        }
        return count;
    }

    /**
     * 에러 개수의 총 합을 구한다.
     * @param msgid
     * @return
     */
    public int getErrorCountForMsgidCount(String msgid) {
        int count = 0;
        try {
            count = sendResultMapper.getErrorCountForMsgidCount(msgid);
        } catch(Exception e){
            count = 0;
        }
        return count;
    }

    /**
     * 에러데이터를 내림차순으로 6개 항목을 동적으로 처리할 수 있도록 소팅처리
     * @param imbErrorCount
     * @return
     */
    public LinkedHashMap<String, Integer> setErrorData(ImbErrorCount imbErrorCount) throws Exception {
        HashMap<String,Integer> map = new HashMap<String,Integer>();
        LinkedHashMap<String,Integer> errMap = new LinkedHashMap<String,Integer>();
        ArrayList<HashMap<String,Integer>> list = new ArrayList<>();

        if(imbErrorCount == null) return errMap;

        map.put(message.getMessage("E0496","이메일주소 공백"),imbErrorCount.getBlankemail_error() );
        map.put(message.getMessage("E0497","불확실한 이메일주소"),imbErrorCount.getUserunknown() );
        map.put(message.getMessage("E0498","서버연결 에러"),imbErrorCount.getConnect_error() );
        map.put(message.getMessage("E0499","DNS 에러"),imbErrorCount.getDns_error() );
        map.put(message.getMessage("E0500","차단 도메인"),imbErrorCount.getDomain_error() );
        map.put(message.getMessage("E0501","이메일형식 에러"),imbErrorCount.getEmailaddr_error() );
        map.put(message.getMessage("E0502","기타"),imbErrorCount.getEtc_error() );
        map.put(message.getMessage("E0503","메일박스 FULL"),imbErrorCount.getMboxfull() );
        map.put(message.getMessage("E0504","네트워크 에러"),imbErrorCount.getNetwork_error() );
        map.put(message.getMessage("E0071","수신거부"),imbErrorCount.getReject_error() );
        map.put(message.getMessage("E0506","중복에러"),imbErrorCount.getRepeat_error() );
        map.put(message.getMessage("E0507","서버 에러"),imbErrorCount.getServer_error() );
        map.put(message.getMessage("E0508","명령어 에러"),imbErrorCount.getSyntax_error() );
        map.put(message.getMessage("E0509","시스템 에러"),imbErrorCount.getSystem_error() );
        map.put(message.getMessage("E0676","불확실한 도메인"),imbErrorCount.getUnknownhost());

        Iterator it = sortByValue(map).iterator();
        int checker = 0;
        while(it.hasNext()){
            String temp = (String) it.next();
            if(checker < 6){
                errMap.put(temp , map.get(temp));
            }else{
                break;
            }
            checker++;
        }
        return errMap;
    }

    public LinkedHashMap<String, Integer> setEmsDate(String msgid) throws SQLException {
        LinkedHashMap<String,Integer> emsMap = new LinkedHashMap<String,Integer>();
      //  emsMap.put("발송메일수", getRecvTotalCountForMsgid(msgid));
        emsMap.put(message.getMessage("E0677","실패수"), getRecvMessageIDSendSuccessAndFailCount(msgid,"0"));
        emsMap.put(message.getMessage("E0678","성공수"), getRecvMessageIDSendSuccessAndFailCount(msgid, "1"));

        return emsMap;
    }

    public LinkedHashMap<String, Integer> setReceiptData(String msgid, String recv_date, int receipt, int unrecipt) {
        LinkedHashMap<String,Integer> emsMap = new LinkedHashMap<String,Integer>();
        try {
            List<RecvMessageIDBean> recvHour = null;

            if (receipt != 0) {
                recvHour = sendResultMapper.getRecvMessageIDForMsgidFlag(msgid, "1");
            } else if (unrecipt != 0) {
                recvHour = sendResultMapper.getRecvMessageIDForMsgidFlag(msgid, "0");
            } else {
                recvHour = sendResultMapper.getRecvListForMsgid(msgid, recv_date);
            }

            int[] time = new int[24];

            for (RecvMessageIDBean item : recvHour) {
                String hour = item.getRecv_hour();
                if (hour.equals("01")) {
                    time[0]++;
                } else if (hour.equals("02")) {
                    time[1]++;
                } else if (hour.equals("03")) {
                    time[3]++;
                } else if (hour.equals("04")) {
                    time[3]++;
                } else if (hour.equals("05")) {
                    time[4]++;
                } else if (hour.equals("06")) {
                    time[5]++;
                } else if (hour.equals("07")) {
                    time[6]++;
                } else if (hour.equals("08")) {
                    time[7]++;
                } else if (hour.equals("09")) {
                    time[8]++;
                } else if (hour.equals("10")) {
                    time[9]++;
                } else if (hour.equals("11")) {
                    time[10]++;
                } else if (hour.equals("12")) {
                    time[11]++;
                } else if (hour.equals("13")) {
                    time[12]++;
                } else if (hour.equals("14")) {
                    time[13]++;
                } else if (hour.equals("15")) {
                    time[14]++;
                } else if (hour.equals("16")) {
                    time[15]++;
                } else if (hour.equals("17")) {
                    time[16]++;
                } else if (hour.equals("18")) {
                    time[17]++;
                } else if (hour.equals("19")) {
                    time[18]++;
                } else if (hour.equals("20")) {
                    time[19]++;
                } else if (hour.equals("21")) {
                    time[20]++;
                } else if (hour.equals("22")) {
                    time[21]++;
                } else if (hour.equals("23")) {
                    time[22]++;
                } else if (hour.equals("24")) {
                    time[23]++;
                }
            }


            emsMap.put(message.getMessage("E0679", "1시"), Integer.valueOf(time[0]));
            emsMap.put(message.getMessage("E0680", "2시"), Integer.valueOf(time[1]));
            emsMap.put(message.getMessage("E0681", "3시"), Integer.valueOf(time[2]));
            emsMap.put(message.getMessage("E0682", "4시"), Integer.valueOf(time[3]));
            emsMap.put(message.getMessage("E0683", "5시"), Integer.valueOf(time[4]));
            emsMap.put(message.getMessage("E0684", "6시"), Integer.valueOf(time[5]));
            emsMap.put(message.getMessage("E0685", "7시"), Integer.valueOf(time[6]));
            emsMap.put(message.getMessage("E0686", "8시"), Integer.valueOf(time[7]));
            emsMap.put(message.getMessage("E0687", "9시"), Integer.valueOf(time[8]));
            emsMap.put(message.getMessage("E0688", "10시"), Integer.valueOf(time[9]));
            emsMap.put(message.getMessage("E0689", "11시"), Integer.valueOf(time[10]));
            emsMap.put(message.getMessage("E0690", "12시"), Integer.valueOf(time[11]));
            emsMap.put(message.getMessage("E0691", "13시"), Integer.valueOf(time[12]));
            emsMap.put(message.getMessage("E0692", "14시"), Integer.valueOf(time[13]));
            emsMap.put(message.getMessage("E0693", "15시"), Integer.valueOf(time[14]));
            emsMap.put(message.getMessage("E0694", "16시"), Integer.valueOf(time[15]));
            emsMap.put(message.getMessage("E0695", "17시"), Integer.valueOf(time[16]));
            emsMap.put(message.getMessage("E0696", "18시"), Integer.valueOf(time[17]));
            emsMap.put(message.getMessage("E0697", "19시"), Integer.valueOf(time[18]));
            emsMap.put(message.getMessage("E0698", "20시"), Integer.valueOf(time[19]));
            emsMap.put(message.getMessage("E0699", "21시"), Integer.valueOf(time[20]));
            emsMap.put(message.getMessage("E0700", "22시"), Integer.valueOf(time[21]));
            emsMap.put(message.getMessage("E0701", "23시"), Integer.valueOf(time[22]));
            emsMap.put(message.getMessage("E0702", "24시"), Integer.valueOf(time[23]));
        }catch(Exception e){}


        return emsMap;
    }

    public int[] setReceiptData2(String msgid, String recv_date, int receipt, int unrecipt) {

        try {
            List<RecvMessageIDBean> recvHour = null;

            if (receipt != 0) {
                recvHour = sendResultMapper.getRecvMessageIDForMsgidFlag(msgid, "1");
            } else if (unrecipt != 0) {
                recvHour = sendResultMapper.getRecvMessageIDForMsgidFlag(msgid, "0");
            } else {
                recvHour = sendResultMapper.getRecvListForMsgid(msgid, recv_date);
            }

            int[] time = new int[12];

            for (RecvMessageIDBean item : recvHour) {
                String hour = item.getRecv_hour();
                if (hour.equals("13")) {
                    time[0]++;
                } else if (hour.equals("14")) {
                    time[1]++;
                } else if (hour.equals("15")) {
                    time[2]++;
                } else if (hour.equals("16")) {
                    time[3]++;
                } else if (hour.equals("17")) {
                    time[4]++;
                } else if (hour.equals("18")) {
                    time[5]++;
                } else if (hour.equals("19")) {
                    time[6]++;
                } else if (hour.equals("20")) {
                    time[7]++;
                } else if (hour.equals("21")) {
                    time[8]++;
                } else if (hour.equals("22")) {
                    time[9]++;
                } else if (hour.equals("23")) {
                    time[10]++;
                } else if (hour.equals("24")) {
                    time[11]++;
                }
            }


            return time;
        }catch(Exception e){
            return null;
        }
    }

    /**
     * map 데이터를 내림차순으로 정렬한다.
     * @param map
     * @return
     */
    public List sortByValue(final Map map){
        List<String> list = new ArrayList();
        list.addAll(map.keySet());

        Collections.sort(list,new Comparator(){

            public int compare(Object o1,Object o2){
                Object v1 = map.get(o1);
                Object v2 = map.get(o2);

                return ((Comparable) v1).compareTo(v2);
            }

        });
        Collections.reverse(list); // 주석시 오름차순 처리
        return list;
    }

    /**
     * 팝업에 출력할 수신그룹목록 정보를 가져온다.
     * @param msgid
     * @return
     */
    public List<RecvMessageIDBean> getReciverListPageingForMsgid(String msgid, int start, int end, String srch_keyword, String srch_type) throws Exception {
        List<RecvMessageIDBean> recvList = sendResultMapper.getReciverListPageingForMsgid(msgid, start, end, srch_keyword, srch_type);
        return getDectyptFiled(recvList);
    }

    /**
     * 팝업에 출력할 수신확인 통계 목록정보를 가져온다.
     * @param msgid
     * @return
     */
    public List<RecvMessageIDBean> getRecvListPageingForMsgid(String msgid, int start, int end, String srch_keyword, String srch_type, String recv_date, String recv_count) throws Exception {
        try {
            List<RecvMessageIDBean> recvList = sendResultMapper.getRecvListPageingForMsgid(msgid, start, end, srch_keyword, srch_type, recv_date, recv_count);
            return getDectyptFiled(recvList);
        }catch(Exception e){
            return null;
        }
    }

    public List<RecvMessageIDBean> getRecvListPageingForMsgid2(String msgid, int start, int end) throws Exception {
        try {
            List<RecvMessageIDBean> recvList = sendResultMapper.getRecvListPageingForMsgid2(msgid, start, end);
            return getDectyptFiled(recvList);
        }catch(Exception e){
            //throw e;
            return null;
        }
    }

    /**
     * 팝업에 출력할 수신확인 통계 카운트정보를 가져온다.
     * @param msgid
     * @param recv_date
     * @param srch_keyword
     * @param srch_type
     * @param recv_count
     * @return
     */
    public int getRecvCountForDate(String msgid, String recv_date, String srch_keyword, String srch_type, String recv_count) {
        int count = 0;
        try {
            count = sendResultMapper.getRecvCountForDate(msgid, recv_date, srch_keyword, srch_type, recv_count);
        } catch(Exception e){
            count = 0;
        }
        return count;
    }

    /**
     * 총수신, 미수신 확인 카운트 정보
     * @param msgid
     * @param flag
     * */
    public int getRecvCountForMsgid(String msgid, String flag) {
        int count = 0;
        try {
            count = sendResultMapper.getRecvCountForMsgid(msgid, flag);
        } catch(Exception e){
            count = 0;
        }
        return count;
    }

    /**
     * 총수신, 미수신 페이징 정보
     * @param msgid

     * @param srch_keyword
     * @param srch_type
     * @param recv_count
     * */
    public int getRecvCountFlagForMsgid(String msgid,String srch_keyword, String srch_type, String recv_count) {
        int count = 0;
        try {
            count = sendResultMapper.getRecvCountFlagForMsgid(msgid, srch_keyword, srch_type, recv_count);
        } catch(Exception e){
            count = 0;
        }
        return count;
    }

    public int getRecvTotalCountForMsgid(String msgid) {
        int count = 0;
        try {
            count = sendResultMapper.getRecvTotalCountForMsgid(msgid);
        } catch(Exception e){
            count = 0;
        }
        return count;
    }

    /**
     * 수신그룹목록의 페이징을 위해 토탈카운트를 구한다. (검색)
     * @param msgid
     * @param srch_keyword
     * @param srch_type
     * @return
     */
    public int getRecvTotalCountForMsgid2(String msgid,String srch_keyword, String srch_type) {
        int count = 0;
        try {
            count = sendResultMapper.getRecvTotalCountForMsgid2(msgid,srch_keyword,srch_type);
        } catch(Exception e){
            count = 0;
        }
        return count;
    }

    /**
     * 특정 발송결과의 수신확인 카운트가 0보다 큰경우 true
     * @param id
     * @param msgid
     * */
    public boolean isOverRecvCount(String id, String msgid) throws Exception{
    	boolean isOver = false;
    	int rcode = ImStringUtil.parseInt(id);

    	int count = sendResultMapper.getRecvCount(msgid, rcode);
    	if(count > 0){
    		isOver = true;
    	}
    	return isOver;
    }

    /**
     * 수신확인 카운트를 더한다.
     * @param msgid
     * @param id
     * */
    public void addRecvCount(String id, String msgid) throws Exception{
    	int rcode = ImStringUtil.parseInt(id);
    	sendResultMapper.addRecvCount(msgid, rcode);
    }

    /**
     * 수신확인 카운트 정보 update (일, 시간 등)
     * @param msgid
     * @param rcode
     * @param recv_time
     * @param recv_date
     * @param recv_hour
     * @throws Exception
     */
    public void updateRecvCountInfo(String msgid, String rcode, String recv_time, String recv_date, String recv_hour) throws Exception{
    	sendResultMapper.updateRecvCountInfo(msgid, rcode, recv_time, recv_date, recv_hour);
    }

    /**
    *
    * 반응분석일을 수정한다.
    * */
    public void updateRespTime(String msgid, String resp_time) throws Exception{
        sendResultMapper.updateRespTime(msgid, resp_time);
    }

    /**
     *
     *  암호화 된 이메일을 복호화시키기 위해서 불러낸 메서드
     * */

    public String getDecryptString(String encStr) throws Exception {
        ImbConstant emsConfig = ImbConstant.getInstance();
       /* if(!emsConfig.isUseEncryptDB()){
            return encStr;
        }*/
        String secret_key = emsConfig.DATABASE_AES_KEY;
        String result = encStr;

        try {
            result = ImSecurityLib.decryptAES256(secret_key, encStr);
        }catch (InvalidKeyException ie) {
            String errorId = ErrorTraceLogger.log(ie);
            log.error("{} - Decrypt Invalid key error", errorId);
        }
        catch (Exception e) {
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - Decrypt error", errorId);
        }

        return result;
    }


    public void getXlsxDownload(List<RecvMessageIDBean> recvMessageIDBean, String tempFileName) throws Exception{
        FileOutputStream fileoutputstream = null;
        try {
            XSSFWorkbook workbook = new XSSFWorkbook();

            //2차는 sheet생성
            XSSFSheet sheet = workbook.createSheet("_List");
            //엑셀의 행
            XSSFRow row = null;
            //엑셀의 셀
            XSSFCell cell = null;

            row = sheet.createRow(0);

            //타이틀(첫행)
            String field1 = message.getMessage("O0007","E-MAIL");
            String field2 = message.getMessage("O0003", "field2");
            String field3 = message.getMessage("E0704", "field3");
            String field4 = message.getMessage("E0705","field4");
            String recv_time = message.getMessage("E0529", "확인 시간");
            String recv_count = message.getMessage("E0593", "확인 횟수");

            String[] columns = {field1, field2, field3, field4, recv_time, recv_count};

            for (int i = 0; i < columns.length; i++) {
                cell = row.createCell(i);
                cell.setCellValue(columns[i]);
            }

            int i = 1;
            for (RecvMessageIDBean recvList : recvMessageIDBean) {
                row = sheet.createRow((Integer) i);
                int j = 0;
                //메일 추가
                cell = row.createCell(j);
                cell.setCellValue(getDecryptString(recvList.getField1()));
                j++;
                //이름 추가
                cell = row.createCell(j);
                cell.setCellValue(recvList.getField2());
                j++;
                //filed3 추가
                cell = row.createCell(j);
                cell.setCellValue(recvList.getField3());
                j++;
                //filed4 추가
                cell = row.createCell(j);
                cell.setCellValue(recvList.getField4());
                j++;
                //recv_time 추가
                cell = row.createCell(j);
                cell.setCellValue(recvList.getRecv_time());
                j++;
                //recv_count 추가
                cell = row.createCell(j);
                cell.setCellValue(recvList.getRecv_count());
                j++;

                i++;
            }

            fileoutputstream = new FileOutputStream(tempFileName);
            //파일을 쓴다
            workbook.write(fileoutputstream);
        }catch (IOException ie) {
            log.error("list download error");

        }
        catch (Exception e) {
            log.error("list download error");

        }
        if (fileoutputstream != null)
            fileoutputstream.close();
    }

  /* 링크통계 목록보기 목록저장 */
    public void getXlsxlinkDownload(List<LinkLogMessageIDBean> linkLogMessageIDBeans, String tempFileName) throws Exception{
        FileOutputStream fileoutputstream = null;
        try {
            XSSFWorkbook workbook = new XSSFWorkbook();

            //2차는 sheet생성
            XSSFSheet sheet = workbook.createSheet("_List");
            //엑셀의 행
            XSSFRow row = null;
            //엑셀의 셀
            XSSFCell cell = null;

            row = sheet.createRow(0);

            //타이틀(첫행)
            String field1 = message.getMessage("O0007","E-MAIL");
            String field2 = message.getMessage("O0003", "field2");
            String field3 = message.getMessage("E0704", "field3");
            String field4 = message.getMessage("E0705", "field4");
            String click_time = message.getMessage("E0662","클릭 시간");
            String click_count = message.getMessage("E0663", "클릭 횟수");

            String[] columns = {field1, field2, field3, field4, click_time, click_count};

            for (int i = 0; i < columns.length; i++) {
                cell = row.createCell(i);
                cell.setCellValue(columns[i]);
            }

            int i = 1;
            for (LinkLogMessageIDBean linkList : linkLogMessageIDBeans) {
                row = sheet.createRow((Integer) i);
                int j = 0;

                //메일 추가
                cell = row.createCell(j);
                cell.setCellValue(linkList.getField1());
                j++;
                //이름 추가
                cell = row.createCell(j);
                cell.setCellValue(linkList.getField2());
                j++;
                //filed3 추가
                cell = row.createCell(j);
                cell.setCellValue(linkList.getField3());
                j++;
                //filed4 추가
                cell = row.createCell(j);
                cell.setCellValue(linkList.getField4());
                j++;
                //recv_time 추가
                cell = row.createCell(j);
                cell.setCellValue(linkList.getClick_time());
                j++;
                //recv_count 추가
                cell = row.createCell(j);
                cell.setCellValue(linkList.getClick_count());
                j++;

                i++;
            }

            fileoutputstream = new FileOutputStream(tempFileName);
            //파일을 쓴다
            workbook.write(fileoutputstream);
        }catch (IOException ie) {
            log.error("getXlsxlinkDownload error");

        }
        catch (Exception e) {
            log.error("getXlsxlinkDownload error");
        }
        if (fileoutputstream != null)
            fileoutputstream.close();
    }



    public int getLinkClickUser(String msgid, String linkid) {
        int count = 0;
        try {
            count = sendResultMapper.getLinkClickUser(msgid, linkid);
        } catch(Exception e){
            count = 0;
        }
        return count;
    }

    public int[] getClickUser(String msgid, String linkid) {
        return sendResultMapper.getClickUser(msgid, linkid);
    }

    public void getlinkXlsxDownload(List<LinkLogMessageIDBean> linkLogMessageIdBean, String tempFileName) throws Exception{
        FileOutputStream fileoutputstream = null;
        try {
            XSSFWorkbook workbook = new XSSFWorkbook();

            //2차는 sheet생성
            XSSFSheet sheet = workbook.createSheet("_List");
            //엑셀의 행
            XSSFRow row = null;
            //엑셀의 셀
            XSSFCell cell = null;

            row = sheet.createRow(0);

            //타이틀(첫행)
            String field1 = message.getMessage("O0007", "field1");
            String field2 = message.getMessage("O0003", "field2");
            String field3 = message.getMessage("E0704", "field3");
            String field4 = message.getMessage("E0705", "field4");
            String click_time = message.getMessage("E0662", "클릭 시간");
            String click_count = message.getMessage("E0706", "클릭 카운트");

            String[] columns = {field1, field2, field3, field4, click_time, click_count};

            for (int i = 0; i < columns.length; i++) {
                cell = row.createCell(i);
                cell.setCellValue(columns[i]);
            }

            int i = 1;
            for (LinkLogMessageIDBean linkList : linkLogMessageIdBean) {
                row = sheet.createRow((Integer) i);
                int j = 0;
                //메일 추가
                cell = row.createCell(j);
                cell.setCellValue(linkList.getField1());
                j++;
                //이름 추가
                cell = row.createCell(j);
                cell.setCellValue(linkList.getField2());
                j++;
                //filed3 추가
                cell = row.createCell(j);
                cell.setCellValue(linkList.getField3());
                j++;
                //filed4 추가
                cell = row.createCell(j);
                cell.setCellValue(linkList.getField4());
                j++;
                //recv_time 추가
                cell = row.createCell(j);
                cell.setCellValue(linkList.getClick_time());
                j++;
                //recv_count 추가
                cell = row.createCell(j);
                cell.setCellValue(linkList.getClick_count());
                j++;

                i++;
            }

            fileoutputstream = new FileOutputStream(tempFileName);
            //파일을 쓴다
            workbook.write(fileoutputstream);
        }catch (IOException ie) {
            log.error("getlinkXlsxDownload error");

        }
        catch (Exception e) {
            log.error("getlinkXlsxDownload error");

        }
        if (fileoutputstream != null)
            fileoutputstream.close();
    }

    public int getLinkMessageCountForMsgid(LinkListForm form) {
        int count = 0;
        try {
            count = sendResultMapper.getLinkMessageCountForMsgid(form);
        } catch(Exception e){
            count = 0;
        }
        return count;
    }

    public LinkedHashMap<String, Integer> setLinkData(String msgid, String click_date, String linkid) {

        List<LinkLogMessageIDBean> linkHour = null;
        linkHour = sendResultMapper.getLinkListForMsgid(msgid, click_date, linkid);

        int [] time = new int[24];

        for(LinkLogMessageIDBean item : linkHour){
            String hour = item.getLink_hour();
            if(hour.equals("01")){
                time[0] ++;
            }else if(hour.equals("02")){
                time[1] ++;
            }else if(hour.equals("03")){
                time[3] ++;
            }else if(hour.equals("04")){
                time[3] ++;
            }else if(hour.equals("05")){
                time[4] ++;
            }else if(hour.equals("06")){
                time[5] ++;
            }else if(hour.equals("07")){
                time[6] ++;
            }else if(hour.equals("08")){
                time[7] ++;
            }else if(hour.equals("09")){
                time[8] ++;
            }else if(hour.equals("10")){
                time[9] ++;
            }else if(hour.equals("11")){
                time[10] ++;
            }else if(hour.equals("12")){
                time[11] ++;
            }else if(hour.equals("13")){
                time[12] ++;
            }else if(hour.equals("14")){
                time[13] ++;
            }else if(hour.equals("15")){
                time[14] ++;
            }else if(hour.equals("16")){
                time[15] ++;
            }else if(hour.equals("17")){
                time[16] ++;
            }else if(hour.equals("18")){
                time[17] ++;
            }else if(hour.equals("19")){
                time[18] ++;
            }else if(hour.equals("20")){
                time[19] ++;
            }else if(hour.equals("21")){
                time[20] ++;
            }else if(hour.equals("22")){
                time[21] ++;
            }else if(hour.equals("23")){
                time[22] ++;
            }else if(hour.equals("24")){
                time[23] ++;
            }
        }

        LinkedHashMap<String,Integer> emsMap = new LinkedHashMap<String,Integer>();
        emsMap.put(message.getMessage("E0679","1시"), Integer.valueOf(time[0]));
        emsMap.put(message.getMessage("E0680","2시"), Integer.valueOf(time[1]));
        emsMap.put(message.getMessage("E0681","3시"), Integer.valueOf(time[2]));
        emsMap.put(message.getMessage("E0682","4시"), Integer.valueOf(time[3]));
        emsMap.put(message.getMessage("E0683","5시"), Integer.valueOf(time[4]));
        emsMap.put(message.getMessage("E0684","6시"), Integer.valueOf(time[5]));
        emsMap.put(message.getMessage("E0685","7시"), Integer.valueOf(time[6]));
        emsMap.put(message.getMessage("E0686","8시"), Integer.valueOf(time[7]));
        emsMap.put(message.getMessage("E0687","9시"), Integer.valueOf(time[8]));
        emsMap.put(message.getMessage("E0688","10시"), Integer.valueOf(time[9]));
        emsMap.put(message.getMessage("E0689","11시"), Integer.valueOf(time[10]));
        emsMap.put(message.getMessage("E0690","12시"), Integer.valueOf(time[11]));
        emsMap.put(message.getMessage("E0691","13시"), Integer.valueOf(time[12]));
        emsMap.put(message.getMessage("E0692","14시"), Integer.valueOf(time[13]));
        emsMap.put(message.getMessage("E0693","15시"), Integer.valueOf(time[14]));
        emsMap.put(message.getMessage("E0694","16시"), Integer.valueOf(time[15]));
        emsMap.put(message.getMessage("E0695","17시"), Integer.valueOf(time[16]));
        emsMap.put(message.getMessage("E0696","18시"), Integer.valueOf(time[17]));
        emsMap.put(message.getMessage("E0697","19시"), Integer.valueOf(time[18]));
        emsMap.put(message.getMessage("E0698","20시"), Integer.valueOf(time[19]));
        emsMap.put(message.getMessage("E0699","21시"), Integer.valueOf(time[20]));
        emsMap.put(message.getMessage("E0700","22시"), Integer.valueOf(time[21]));
        emsMap.put(message.getMessage("E0701","23시"), Integer.valueOf(time[22]));
        emsMap.put(message.getMessage("E0702","24시"), Integer.valueOf(time[23]));


        return emsMap;
    }



//    public LinkedHashMap<String, Integer> setLinkgraphData(String msgid, String click_date, String linkid) {
//
//        List<LinkLogMessageIDBean> g_data = null;
//        g_data = sendResultMapper.getLinkListDetail(msgid,click_date, linkid);
//
//
//
//        for(LinkLogMessageIDBean item : g_data){
//            LinkedHashMap<String,Integer> emsMap = new LinkedHashMap<String,Integer>();
//            emsMap.put(item.getLink_date(), item.getClick_count());
//            return emsMap;
//        }
//
//      return ;
//
//
//    }





    public int[] setLinkData2(String msgid, String click_date, String linkid) {

        List<LinkLogMessageIDBean> linkHour = null;
        linkHour = sendResultMapper.getLinkListForMsgid(msgid, click_date, linkid);

        int [] time = new int[12];

        for(LinkLogMessageIDBean item : linkHour){
            String hour = item.getLink_hour();
            if(hour.equals("13")){
                time[0] ++;
            }else if(hour.equals("14")){
                time[1] ++;
            }else if(hour.equals("15")){
                time[2] ++;
            }else if(hour.equals("16")){
                time[3] ++;
            }else if(hour.equals("17")){
                time[4] ++;
            }else if(hour.equals("18")){
                time[5] ++;
            }else if(hour.equals("19")){
                time[6] ++;
            }else if(hour.equals("20")){
                time[7] ++;
            }else if(hour.equals("21")){
                time[8] ++;
            }else if(hour.equals("22")){
                time[9] ++;
            }else if(hour.equals("23")){
                time[10] ++;
            }else if(hour.equals("24")){
                time[11] ++;
            }
        }


        return time;
    }





    public ArrayList<ErrorCountBean> setErrorList(ImbErrorCount imbErrorCount) throws Exception{

        ArrayList<ErrorCountBean> errorlist = new ArrayList<>();

        String[] field = new String[15];

        field[0] = "901:"+message.getMessage("E0676","불확실한 도메인")+":#304bab:" + imbErrorCount.getUnknownhost();
        field[1] = "902:"+message.getMessage("E0498","서버연결 에러")+":#5e7be7:" + imbErrorCount.getConnect_error();
        field[2] = "903:"+message.getMessage("E0499","DNS 에러")+":#1f52ed:" + imbErrorCount.getDns_error() ;
        field[3] = "904:"+message.getMessage("E0504","네트워크 에러")+":#6ce3f3:" + imbErrorCount.getNetwork_error();
        field[4] = "905:"+message.getMessage("E0509","시스템 에러")+":#ff953f:" + imbErrorCount.getSystem_error();
        field[5] = "906:"+message.getMessage("E0507","서버 에러")+":#c4e3fe:" + imbErrorCount.getServer_error();
        field[6] = "907:"+message.getMessage("E0508","명령어 에러")+":#4c4c4c:" + imbErrorCount.getSyntax_error();
        field[7] = "908:"+message.getMessage("E0497","불확실한 이메일주소")+":#ffceb6:" + imbErrorCount.getUserunknown();
        field[8] = "909:"+message.getMessage("E0503","메일박스 FULL")+":#7a3726:" + imbErrorCount.getMboxfull();
        field[9] = "910:"+message.getMessage("E0502","기타")+":#ff6941:" + imbErrorCount.getEtc_error();
        field[10] = "911:"+message.getMessage("E0501","이메일형식 에러")+":#f7c849:" + imbErrorCount.getEmailaddr_error();
        field[11] = "912:"+message.getMessage("E0071","수신거부")+":#fc4c50:" + imbErrorCount.getReject_error();
        field[12] = "913:"+message.getMessage("E0506","중복에러")+":#cf865e:" + imbErrorCount.getRepeat_error();
        field[13] = "914:"+message.getMessage("E0500","차단 도메인")+":#7b7b7b:" + imbErrorCount.getDomain_error();
        field[14] = "915:"+message.getMessage("E0556","이메일주소 공백")+":#0ba035:" + imbErrorCount.getBlankemail_error();


        for(int i = 0 ; i < field.length ; i++ ){
            String[] type = field[i].split(":");
            ErrorCountBean errorCountBean = new ErrorCountBean();
            errorCountBean.setCode( type[0] );
            errorCountBean.setType( type[1] );
            errorCountBean.setColor( type[2] );
            errorCountBean.setCount( Integer.parseInt(type[3]) );
            errorlist.add( errorCountBean );
        }

        return errorlist;
    }

    public List<RecvMessageIDBean> getDownloadStatErrorList(String msgid, String errcode) throws Exception{
        List<RecvMessageIDBean> list = sendResultMapper.getDownloadStatErrorList(msgid, errcode);
        return getDectyptFiled(list);
    }

    public List<RecvMessageIDBean> getStatErrorList(String msgid, String errcode, String srch_keyword, String srch_type, int start, int end) throws Exception{
        List<RecvMessageIDBean> list = sendResultMapper.getStatErrorList(msgid, errcode, srch_keyword, srch_type, start, end);
        return getDectyptFiled(list);
    }

    public List<LinkLogMessageIDBean> setLinkDatadetail(String msgid, String click_date,String linkid) {

        return sendResultMapper.getLinkListDetail(msgid,click_date, linkid);

}

    public List<RecvMessageIDBean> getDectyptFiled(List<RecvMessageIDBean> list) throws Exception{
        for(int i=0; i<list.size(); i++){
            RecvMessageIDBean bean = list.get(i);
            String temp = ImSecurityLib.decryptAES256(ImbConstant.DATABASE_AES_KEY, list.get(i).getField1());
            list.set(i, bean).setField1(temp);
        }
        return list;
    }

    public List<LinkLogMessageIDBean> getDectyptFiledForLinkLog(List<LinkLogMessageIDBean> list) throws Exception{
        for(int i=0; i<list.size(); i++){
            LinkLogMessageIDBean bean = list.get(i);
            String temp = ImSecurityLib.decryptAES256(ImbConstant.DATABASE_AES_KEY, list.get(i).getField1());
            list.set(i, bean).setField1(temp);
        }
        return list;
    }

    public int getStatErrorListCount(String msgid, String errcode, String srch_keyword, String srch_type) throws Exception{
        int count = 0;
        try {
            count = sendResultMapper.getStatErrorListCount(msgid, errcode, srch_keyword, srch_type);
        } catch(Exception e){
            count = 0;
        }
        return count;
    }


    public void getXlsxDownload(List<RecvMessageIDBean> errorList, String tempFileName, String msgid) throws Exception {

        FileOutputStream fileoutputstream = null;

        XSSFWorkbook workbook = new XSSFWorkbook();

        //2차는 sheet생성
        XSSFSheet sheet = workbook.createSheet("reject");
        //엑셀의 행
        XSSFRow row = null;
        //엑셀의 셀
        XSSFCell cell = null;

        row = sheet.createRow(0);
        //타이틀(첫행)
        String str = "ID,도메인,성공여부,에러코드,에러내용,발신시간,수신시간,field1,field2,field3,field4,is_resend";

        String[] headerArr = str.split(",");
        for (int i = 0; i < headerArr.length; i++) {
            cell = row.createCell(i);
            cell.setCellValue(headerArr[i]);
        }

        int i = 1;
        for (RecvMessageIDBean error : errorList) {
            row = sheet.createRow((Integer) i);
            int j = 0;

            //ID
            cell = row.createCell(j);
            cell.setCellValue(error.getId());
            j++;

            //도메인
            cell = row.createCell(j);
            cell.setCellValue(error.getDomain());
            j++;

            //성공여부
            cell = row.createCell(j);
            cell.setCellValue(error.getSuccess());
            j++;

            //에러코드
            cell = row.createCell(j);
            cell.setCellValue(error.getErrcode());
            j++;

            //에러내용
            cell = row.createCell(j);
            cell.setCellValue(error.getErr_exp());
            j++;

            //발신시간
            cell = row.createCell(j);
            cell.setCellValue(error.getSend_time());
            j++;

            //수신시간
            cell = row.createCell(j);
            cell.setCellValue(error.getRecv_time());
            j++;

            //Field1
            cell = row.createCell(j);
            cell.setCellValue(error.getField1());
            j++;

            //Field2
            cell = row.createCell(j);
            cell.setCellValue(error.getField2());
            j++;

            //Field3
            cell = row.createCell(j);
            cell.setCellValue(error.getField3());
            j++;

            //Field4
            cell = row.createCell(j);
            cell.setCellValue(error.getField4());
            j++;

            //is_resend
            cell = row.createCell(j);
            cell.setCellValue(error.getIs_resend());
            j++;

            i++;

        }
        fileoutputstream = new FileOutputStream(tempFileName);
        //파일을 쓴다
        workbook.write(fileoutputstream);

        if (fileoutputstream != null) fileoutputstream.close();

    }

    public void decryptField1(LinkLogMessageIDBean bean) throws Exception {
        String secret_key = ImbConstant.DATABASE_AES_KEY;

        bean.setField1(ImSecurityLib.decryptAES256(secret_key,bean.getField1()));

    }

    public String getCategoryName(String categoryid){

        CategoryBean categoryBean = categoryService.getCategoryForgetCategoryId(categoryid);
        String categoryName;

        if(categoryBean != null){
            categoryName = categoryBean.getName();
        }else{
            categoryName = "";
        }

        return categoryName;
    }

    public int getLinkClickCount(String msgid){
        int count = 0;
        try {
            count = sendResultMapper.getLinkClickCount(msgid);
        } catch(Exception e){
            count = 0;
        }
        return count;
    }

    public int getLinkClickCountForLinkid(String msgid, int linkid){
        int count = 0;
        try {
            count = sendResultMapper.getLinkClickCountForLinkid(msgid, linkid);
        } catch(Exception e){
            count = 0;
        }
        return count;
    }

    public int getLinkCountData(String msgid, String linkid){
        int count = 0;
        if(linkid != null){
            int id = Integer.parseInt(linkid);
            if(id > -1){
                count = this.getLinkCountForLinkid(msgid, id);
            }
            if(count > 0){
                count = this.getLinkClickCountForLinkid(msgid, id);
            }
        }else{
            count = this.getLinkCount(msgid);
            if(count > 0){
                count = this.getLinkClickCount(msgid);
            }
        }
        return count;
    }



}

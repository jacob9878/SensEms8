package com.imoxion.sensems.web.service;

import com.imoxion.common.mail.ImMessage;
import com.imoxion.common.util.ImTimeUtil;
import com.imoxion.security.ImSecurityLib;
import com.imoxion.sensems.common.logger.ErrorTraceLogger;
import com.imoxion.sensems.web.beans.EmsBean;
import com.imoxion.sensems.web.beans.RecvMessageIDBean;
import com.imoxion.sensems.web.beans.TestSendBean;
import com.imoxion.sensems.web.beans.UserInfoBean;
import com.imoxion.sensems.web.common.ImbConstant;
import com.imoxion.sensems.web.database.domain.ImbAddr;
import com.imoxion.sensems.web.database.domain.ImbDBInfo;
import com.imoxion.sensems.web.database.domain.ImbReceiver;
import com.imoxion.sensems.web.database.domain.ImbTransmitData;
import com.imoxion.sensems.web.database.mapper.TestSendMapper;
import com.imoxion.sensems.web.form.MailWriteForm;
import com.imoxion.sensems.web.util.ImMessageUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TestSendService {
    private Logger log = LoggerFactory.getLogger(AccountService.class);

    @Autowired
    TestSendMapper testSendMapper;

    @Autowired
    DatabaseService databaseService;

    @Autowired
    ReceiverService receiverService;

    @Autowired
    AddressService addressService;

    @Autowired
    private MessageSourceAccessor message;

    @Autowired
    private SendResultService sendResultService;



    // 테스트 발송 객체를 갖고 서비스단에서 제목, 첨부파일여부, HTML 여부, 필드에 따른 내용 치환 서비스단 처리를 행함 to email은 배열로 전달하여 건건히 가공하여 발송조치한다.

    /**
     *  발신주소 = from_email
     *  수신주소 = to_email
     *  제목 = subject - [테스트] + subject
     *  발송상태 = state - 0: 기본, 1: 진행중, 2:완료
     *  메일언어셋 = charset
     *  HTML메일여부 = ishtml 0 = text , 1= html
     *  첨부파일 유무 = isattach 0=  없음
     *  등록시간 = regdate
     * @throws Exception
     */
    public void doTestSendMailForReserveSend(String[] to_emails, TestSendBean testSendBean, String content, String recid, String rectype, String userid) throws Exception{
        // 제목에 테스트발송 설정
        testSendBean.setSubject("["+message.getMessage("E0305","테스트")+"] " + testSendBean.getSubject());

        /** 제목이나 본문에 필드 기능을 사용하는지 유무를 체크**/
        String check = "[#FIELD";
        int subject_count = StringUtils.countMatches(testSendBean.getSubject(), check);
        int content_count = StringUtils.countMatches(content, check);
        String subject = testSendBean.getSubject();

        /** 제목이나 본문중 필드 기능을 사용하는 경우 DB에서 수신자 필드 값을 추출한다. **/
        if(subject_count > 0 || content_count > 0 ){

            /** 수신자 필드에 따른 데이터 치환 처리  **/
            if(rectype.equals("2")){
                // 수신그룹 값을 가져와서 수신그룹 값을 기준으로 필드정보를 추출
                ImbReceiver imbReceiver = receiverService.getReceiverGroup(recid);
                String query = imbReceiver.getQuery();
                String dbkey = imbReceiver.getDbkey();

                ImbDBInfo dbInfo = databaseService.getDBInfoByUkey(dbkey);

                if(dbInfo != null) {
                    Connection con = null;
                    try {
                        con = databaseService.getDBConnection(dbInfo.getDbtype(),dbInfo.getAddress(),dbInfo.getDbuser(),dbInfo.getDbpasswd());

                        Map<Integer, String> map = databaseService.testSendExcuteQuery(con, query);

                        if (subject_count > 0  && !map.isEmpty())  {
                            subject = returnReplaceField(query, subject, subject_count, map);
                            testSendBean.setSubject(subject);
                        }
                        if (content_count > 0 && !map.isEmpty()) {
                            content = returnReplaceField(query, content, content_count, map);
                        }
                    }catch (NullPointerException ne) {
                        String errorId = ErrorTraceLogger.log(ne);
                        log.error("{} - doTestSendMailForReserveSend NPE error", errorId);
                    }
                    catch (Exception e){
                        String errorId = ErrorTraceLogger.log(e);
                        log.error("{} - doTestSendMailForReserveSend error", errorId);
                    }finally {
                        if(con != null) con.close();
                    }
                }
            }else{ //주소록
                if(recid.contains(",")){
                    String recids[] = recid.split(",");
                    recid = recids[0];
                }

                List<ImbAddr> imbAddrList = addressService.getAddressListByGkey(userid, Integer.parseInt(recid));
                ImbAddr imbAddr = imbAddrList.get(0);

                Map<Integer, String> map = new HashMap<Integer, String>();
                map.put(1, imbAddr.getEmail());
                map.put(2, imbAddr.getName());
                map.put(3, imbAddr.getCompany());
                map.put(4, imbAddr.getOffice_tel());
                map.put(5, imbAddr.getDept());
                map.put(6, imbAddr.getMobile());
                map.put(7, imbAddr.getEtc1());
                map.put(8, imbAddr.getEtc2());

                if (subject_count > 0) {
                    subject = returnReplaceField("", subject, subject_count, map);
                    testSendBean.setSubject(subject);
                }

                if (content_count > 0) {
                    content = returnReplaceField("", content, content_count, map);
                }
            }
        }

        int length = to_emails.length;

        // 수신자의 수만큼 데이터를 주입
         for(int i=0; i < length; i++){
             // 수신자별 이메일을 설정
            testSendBean.setRcptto(to_emails[i]);
            testSendMapper.insertSmtpTempRcpt(testSendBean);
        }
         testSendBean.setRegdate(new Date());
         testSendBean.setBody(content);
         testSendMapper.insertSmtpTempMain(testSendBean);
    }

    public String returnReplaceField(String query, String txt, int count, Map<Integer, String> map) throws Exception {
            int mapSize = map.size();
            String filed_value;
            String start_field = "[#FIELD";
            String end_field = "#]";
            /*for(int i=0; i < mapSize; i++){ //8
                //txt 변수가 필드를 사용하고 필드의 값이 현재 반복문의 i값이 제목의 필드갯수 보다 작은경우 필드값을 사용하는 것으로 판단하여 치환을 수행
                if(count > 0 && i <= count ){
                    // map에서 필드에 해당하는 값을 구한다.
                    filed_value = "";
                    filed_value  = map.get(i+1);
                    txt = txt.replace(start_field+(i+1)+end_field, filed_value);

                }
            }*/
            for(int i=0; i < mapSize; i++){
                filed_value = "";
                filed_value  = map.get(i+1) == null ? "" : map.get(i+1);
                txt = txt.replace(start_field+(i+1)+end_field, filed_value);
            }

        return txt;
    }

    /**
     * 조건에 맞는 발송결과 목록을 가져온다.
     * @param srch_keyword
     * @param srch_type
     * @param start
     * @param end
     * @return
     */
    public List<ImbTransmitData> getTestSendList(String srch_keyword, String srch_type, int start, int end, String send_type){
        return testSendMapper.getTestSendList(srch_keyword, srch_type, start, end, send_type);
    }

    /**
     * 조건에 맞는발송결과 개수를 가져온다.
     *
     * @param srch_keyword
     * @param srch_type
     * @return
     */
    public int getTestSendListCount(String srch_keyword, String srch_type, String send_type) {
        return testSendMapper.getTestSendListCount(srch_keyword, srch_type, send_type);
    }

    /**
     * 선택된 개별 발송결과들을 제거한다.
     * @param ukeys
     * @param userInfoBean
     * @return
     */
    public boolean deleteTestSendList(String[] ukeys, UserInfoBean userInfoBean) {
        boolean checker = false;

        String permission = userInfoBean.getPermission();
        String userid = userInfoBean.getUserid();

        for (int i = 0; i < ukeys.length; i++) {
            if (UserInfoBean.UTYPE_ADMIN.equals(permission)) { // 삭제 권한이 존재하는 사용자인지 권한 체크
                checker = true;
                testSendMapper.TestSendDelete(ukeys[i].split(",")[0]);   // 메시지를 삭제한다.
            } else { // 삭제권한이 없다면 게시자 본인인지 체크
                checker = false;
            } // else end
        } // if end

        return checker;
    }

    /**
     * 선택된 개별 발송결과 상세보기
     * @param traceid
     * @param serverid
     * @param rcptto
     * @return
     */
    public ImbTransmitData getTransmitDataLog(String traceid, String serverid, String rcptto) {
        return testSendMapper.getTransmitDataLog(traceid,serverid,rcptto);
    }

    /**
     * 발송 구분 값을 제목과 userid로 구한다.
     * @param
     * @param subject
     * */
    public String getSendType(String subject) { return testSendMapper.getSendType(subject); }

    public void doResendMail(MailWriteForm form, TestSendBean testSendBean) throws Exception{
        testSendBean.setSubject(form.getMsg_name());

        /** 제목이나 본문에 필드 기능을 사용하는지 유무를 체크**/
        String content = form.getContent();
        String subject = testSendBean.getSubject();
        String check = "[#FIELD";
        int subject_count = StringUtils.countMatches(subject, check);
        int content_count = StringUtils.countMatches(content, check);

        /** 제목이나 본문중 필드 기능을 사용하는 경우 DB에서 수신자 필드 값을 추출한다. **/
        if(subject_count > 0 || content_count > 0 ){

            /** 수신자 필드에 따른 데이터 치환 처리  **/
            //recvid로 recv 테이블에ㅓ서 가져와서 필드 넣기...
            RecvMessageIDBean recvMessageIDBean = sendResultService.getRecvForMsgid(form.getOld_msgid(), form.getRecvid());
            Map<Integer, String> map = new HashMap<Integer, String>();
            map.put(1, recvMessageIDBean.getField1());
            map.put(2, recvMessageIDBean.getField2());
            map.put(3, recvMessageIDBean.getField3());
            map.put(4, recvMessageIDBean.getField4());
            map.put(5, recvMessageIDBean.getField5());
            map.put(6, recvMessageIDBean.getField6());
            map.put(7, recvMessageIDBean.getField7());
            map.put(8, recvMessageIDBean.getField8());
            map.put(9, recvMessageIDBean.getField9());

            if (subject_count > 0) {
                subject = returnReplaceField("", subject, subject_count, map);
                testSendBean.setSubject(subject);
            }

            if (content_count > 0) {
                content = returnReplaceField("", content, content_count, map);
            }
        }

        //본문 내 링크 처리
        Pattern p = Pattern.compile("<a\\s+(.*?)href\\s*=\\s*[\"|'|]?(.*?)[\"|'|>]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(content);
        int link_id = 0;
        StringBuffer sb = new StringBuffer();
        int prevEnd = -1; // 본문 치환시 위치를 저장하는 임시 변수
        String temp = "";

        while (m.find()) {
            String orgLink = m.group(2).trim();

            if(orgLink.contains(ImbConstant.AD_URL)){
                int idx = orgLink.indexOf("url=");
                orgLink = ImSecurityLib.decryptAES(ImbConstant.URL_AES_KEY, orgLink.substring(idx+4));

                if(link_id == 0){
                    sb.append(content.substring( 0, m.start(2)));
                }else{
                    String appendStr = content.substring( prevEnd, m.start(2));
                    if(appendStr.startsWith("\"") || appendStr.startsWith("'")){
                        appendStr =  appendStr.substring(0, 1) + " target=_blank " + appendStr.substring(1);
                    } else {
                        appendStr =  " target=_blank " + appendStr;
                    }
                    sb.append( appendStr );
                }
            }
            sb.append(orgLink);
            prevEnd =  m.end(2);
            link_id ++;
        }

        if(link_id > 0){
            String appendStr = content.substring(prevEnd);
            if(appendStr.startsWith("\"") || appendStr.startsWith("'")){
                appendStr =  appendStr.substring(0, 1) + " target=_blank " + appendStr.substring(1);
            } else {
                appendStr =  " target=_blank " + appendStr;
            }
            sb.append( appendStr );

            content = sb.toString();
        }

        //이전 openmail.do 제거
        p = Pattern.compile("<img\\s+(.*?)src\\s*=\\s*[\"|'|]?(.*?)[\"|'|>]", Pattern.CASE_INSENSITIVE);
        m = p.matcher(content);
        String url = "";

        while (m.find()) {
            url = m.group(1).trim();

           if(url.contains(ImbConstant.RCPT_URL)){
               content = content.replaceAll(url + ">","");
           }
        }

        // 수신자 이메일을 설정
        testSendBean.setRcptto(form.getRecname());
        testSendMapper.insertSmtpTempRcpt(testSendBean);

        testSendBean.setRegdate(new Date());
        testSendBean.setBody(content);
        testSendMapper.insertSmtpTempMain(testSendBean);
    }
}

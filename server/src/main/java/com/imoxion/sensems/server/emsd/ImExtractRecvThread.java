package com.imoxion.sensems.server.emsd;

import com.imoxion.common.util.ImStringUtil;
import com.imoxion.sensems.server.ImEmsServer;
import com.imoxion.sensems.server.beans.ImEMSQueryData;
import com.imoxion.sensems.server.beans.ImRecvRecordData;
import com.imoxion.sensems.server.define.ImStateCode;
import com.imoxion.sensems.server.domain.ImbDBInfo;
import com.imoxion.sensems.server.domain.ImbEmsMain;
import com.imoxion.sensems.server.exception.ImEmailException;
import com.imoxion.sensems.server.logger.ErrorTraceLogger;
import com.imoxion.sensems.server.service.*;
import com.imoxion.sensems.server.util.ImEmsUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 수신자 추출 쿼리를 이용하여 타겟 수신자 데이텨를 뽑아서 recv_[msgid] 테이블에 인서트한다.
 */
public class ImExtractRecvThread implements Runnable {
    private static Logger logger = LoggerFactory.getLogger("EMSD");

    private ImbEmsMain emsMain;
    private static List<String> listReject;
    private static List<String> listFilterDomain;

    public ImExtractRecvThread(ImbEmsMain emsMain) {
        this.emsMain = emsMain;
    }

    public String getMsgid(){
        return this.emsMain.getMsgid();
    }

    private List<String> getListReject(){
        RejectService rejectService = RejectService.getInstance();
        return rejectService.getRejectList();
    }

    private List<String> getListFilterDomain(){
        FilterDomainService filterDomainService = FilterDomainService.getInstance();
        return filterDomainService.getListFilter();
    }

    public static boolean isExistRejectList(String email){
//logger.info("isExistRejectList listReject size: {}", listReject.size());
        if(listReject.contains(email.toLowerCase())){
            return true;
        } else {
            return false;
        }
    }
    public static boolean isExistFilterDomainList(String domain){
        if(listFilterDomain.contains(domain.toLowerCase())){
            return true;
        } else {
            return false;
        }
    }

    /**
     * 수신그룹을 이용하여 메일을 발송하기 위해 수신 대상자를 추출한다.
     */
    public void extractReceiver(ImbEmsMain emsMain, ImbDBInfo dbInfo) throws Exception {
        ReceiverService receiverService = ReceiverService.getInstance();
        EmsMainService emsMainService = EmsMainService.getInstance();
        DatabaseService databaseService = DatabaseService.getInstance();
        ErrorCountService errorCountService = ErrorCountService.getInstance();
        AddressService addressService = AddressService.getInstance();

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String extractQuery = "";
        String createRecvQuery = "";
        String insertRecvQuery = "";

        int fieldCount = 0;
        int alreadyExtractedCount = -1;
        int recordNum = 0;
        int curIdNum = 0;
        boolean bResend = false;
        String prevEmail = "";
        int nMaxRecv = 10;
        String email = "";
        List<ImEMSQueryData> listQryData = new ArrayList<>();
        int nEmailAddr = 0;
        int nReject = 0;
        int nDomain = 0;
        int nRepeat = 0;
        int nBlank = 0;
        int nSendCount = 0;
        int nIdNum = 0;

        try {
            if (StringUtils.isBlank(emsMain.getQuery())) {
                emsMain.setState(ImStateCode.ST_FAIL_RECV);
                emsMain.setTotal_send(0);
                // 수신자 생성 실패
                emsMainService.updateStateAndCount(emsMain);
                // imb_error_count 에 업데이트
                errorCountService.insertErrorCountInit(emsMain.getMsgid());
                return;
            }

            // extended: 999 재발신(에러재발신, 미수신자 재발신 등)
            if ("999".equals(emsMain.getExtended())) {
                bResend = true;
            }

            // 혹시 기존에 추출하던 정보가 있는지 확인한다.
            // 기존에 뽑던 데이터가 있으면 중지후 재전송이 되겟죠
            // 없으면 -1
            try {
                alreadyExtractedCount = receiverService.getAlreadyExtractedCount(emsMain.getMsgid());
            }catch(Exception e){}

            logger.info("======== emsMain: {}", emsMain.toString());

            // 주소록인지 수신그룹인지 재발신인지
            if (ImbEmsMain.RECTYPE_ADDR.equals(emsMain.getRectype()) || ImbEmsMain.RECTYPE_RESEND.equals(emsMain.getRectype())) {
                logger.debug("Addr");
                conn = databaseService.getLocalDBConn();
//                extractQuery = ImEmsUtil.getDecryptString(emsMain.getQuery());
                extractQuery = emsMain.getQuery();
            } else if (ImbEmsMain.RECTYPE_RECV.equals(emsMain.getRectype())) {
                logger.debug("Recv");
                conn = receiverService.getRecvDBConn(dbInfo);
//                extractQuery = ImEmsUtil.getDecryptString(emsMain.getQuery());
//                ImbReceiver receiverInfo = receiverService.getReceiverInfo(emsMain.getRecid());
//                extractQuery = ImEmsUtil.getDecryptString(receiverInfo.getQuery());
                extractQuery = emsMain.getQuery();
                logger.debug("Recv - {}", extractQuery);
            } /*else if( emsMain.getRectype() == ImbEmsMain.RECTYPE_RESEND){
                conn = databaseService.getLocalDBConn();
                //extractQuery = ImEmsUtil.getDecryptString(emsMain.getQuery());
                extractQuery = emsMain.getQuery();
            }*/

            logger.debug("executeQuery: {}", extractQuery);

            ps = conn.prepareStatement(extractQuery);
            ps.setFetchSize(100);
            rs = ps.executeQuery();

            // 추출된 데이터의 기본 정보를 뽑는다
            if (rs != null) {
//                logger.debug("1111");
                SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
                ResultSetMetaData rsMetaData = rs.getMetaData();
                fieldCount = rsMetaData.getColumnCount();
//                logger.debug("22222");
                // 처음 실행되기 때문에 recv_[msgid] 테이블생성, imb_error_count 에 초기값 세팅
                if (alreadyExtractedCount < 0) {
                    createRecvQuery = receiverService.getCreateRecvQuery(emsMain.getMsgid(), rsMetaData);
                    receiverService.createExtractRecvQuery(createRecvQuery);
                    errorCountService.insertErrorCountInit(emsMain.getMsgid());
                }
//                logger.debug("33333");
                while (rs.next() && ImEmsServer.isAlive) {
//                    logger.debug("44444");
                    if (Thread.interrupted()) {
                        logger.info("<" + Thread.currentThread().getName() + ", " + emsMain.getMsgid() + "> Interrupted & Stopped");
                        break;
                    }

                    String strCurrDate = df.format(new Date(System.currentTimeMillis()));

                    ImRecvRecordData recvRecordData = new ImRecvRecordData();
//                    logger.debug("5555");
                    // 이전에 추출중이었으면 추출된 데이타 이후부터 뽑는다.
                    if (alreadyExtractedCount > recordNum++) continue;
//                    logger.debug("6666");
                    // recv_[msgid] 테이블 인서트 쿼리
                    insertRecvQuery = "INSERT INTO recv_" + emsMain.getMsgid() + " VALUES(?,?,?,?,?,?,'',0";
                    for (int i = 1; i <= fieldCount; i++) {
                        if(i == 1 && ImbEmsMain.RECTYPE_RECV.equals(emsMain.getRectype())) {
                            recvRecordData.addRecord("FIELD" + i, ImEmsUtil.getEncryptString(rs.getString(i)));
                        } else {
                            recvRecordData.addRecord("FIELD" + i, rs.getString(i));
                        }
                        insertRecvQuery += ",?";
                    }
                    insertRecvQuery += ",0,NULL, NULL, NULL)";
//                    logger.debug("7777 - {}", insertRecvQuery);
                    if (bResend) {
                        nIdNum = rs.getInt("id");
                    } else {
                        nIdNum = recordNum;
                    }
//                    logger.debug("88888 - {}", nIdNum);
                    try {
                        if (ImbEmsMain.RECTYPE_ADDR.equals(emsMain.getRectype()) || ImbEmsMain.RECTYPE_RESEND.equals(emsMain.getRectype())) {
                            email = ImEmsUtil.getDecryptString(rs.getString(1));
                        }else if (ImbEmsMain.RECTYPE_RECV.equals(emsMain.getRectype())) {
                            email = rs.getString(1);
                        }
                        email = ImStringUtil.getSafeString(email).toLowerCase().trim();
//logger.debug("insertRecvQuery : {} - {}", email, insertRecvQuery);
                        // 이메일주소 값 없음
                        if (StringUtils.isEmpty(email)) {
                            nBlank++;
                            // String sID, String sCurrTime, String sErrorCode, String sErrorStr, String sSuccess, String sDomain, ImRecvRecordData rData
                            ImEMSQueryData qData = new ImEMSQueryData(String.valueOf(nIdNum), strCurrDate,
                                    "915", "Empty Email Address",
                                    "0", "", recvRecordData);
                            listQryData.add(qData);
                            throw new ImEmailException();
                        }

                        String domain = StringUtils.substringAfter(email, "@");

                        // 이메일 형식 오류
                        if (!EmailValidator.getInstance().isValid(email)) {
                            nEmailAddr++;
                            ImEMSQueryData qData = new ImEMSQueryData(String.valueOf(nIdNum), strCurrDate,
                                    "911", "Invalid Email Address",
                                    "0", domain, recvRecordData);
                            listQryData.add(qData);

                            throw new ImEmailException();
                        }

                        // 수신거부 이메일주소
                        if (ImExtractRecvThread.isExistRejectList(email)) {
                            nReject++;

                            ImEMSQueryData qData = new ImEMSQueryData(String.valueOf(nIdNum), strCurrDate,
                                    "912", "Reject Email Address",
                                    "0", domain, recvRecordData);
                            listQryData.add(qData);

                            throw new ImEmailException();
                        }

                        // 필터링 도메인
                        if (ImExtractRecvThread.isExistFilterDomainList(domain)) {
                            nDomain++;

                            ImEMSQueryData qData = new ImEMSQueryData(String.valueOf(nIdNum), strCurrDate,
                                    "914", "Filtered Domain Address",
                                    "0", domain, recvRecordData);
                            listQryData.add(qData);

                            throw new ImEmailException();
                        }

                        // 이메일 중복여부 체크( 1== 중복허용, 0== 중복 오류)
                        if (emsMain.getIs_same_email() == 0) {
                            if (!email.equals(prevEmail)) {
                                nSendCount++;
                                ImEMSQueryData qData = new ImEMSQueryData(String.valueOf(nIdNum), strCurrDate,
                                        "", "",
                                        "2", domain, recvRecordData);
                                listQryData.add(qData);

                                // - 수신자 수가 적을 때는 덩어리로 발송하는 갯수를 작게(10 > 20 > 30)
                                /*if (nSendCount > 100) {
                                    nMaxRecv = 20;
                                } else if (nSendCount > 1000) {
                                    nMaxRecv = ImEmsConfig.getInstance().getMaxRecvCount();
                                }

                                if ((nSendCount % nMaxRecv) == 0) {
                                    Thread.sleep(100);
                                    receiverService.executeSQLUpdate(insertRecvQuery, listQryData);
                                }*/

                                prevEmail = email;
                                curIdNum = nIdNum;
                            } else {
                                nRepeat++;
                                ImEMSQueryData qData = new ImEMSQueryData(String.valueOf(nIdNum), strCurrDate,
                                        "913", "Duplicated Email Address",
                                        "0", domain, recvRecordData);
                                listQryData.add(qData);

                               /* if (listQryData.size() > 100) {
                                    receiverService.executeSQLUpdate(insertRecvQuery, listQryData);
                                }*/
                            }
                        } else {
                            nSendCount++;
                            ImEMSQueryData qData = new ImEMSQueryData(String.valueOf(nIdNum), strCurrDate,
                                    "", "",
                                    "2", domain, recvRecordData);
                            listQryData.add(qData);

                            /*// - 수신자 수가 적을 때는 덩어리로 발송하는 갯수를 작게(10 > 20 > 30)
                            if (nSendCount > 100) {
                                nMaxRecv = 20;
                            } else if (nSendCount > 1000) {
                                nMaxRecv = ImEmsConfig.getInstance().getMaxRecvCount();
                            }

                            if ((nSendCount % nMaxRecv) == 0) {
                                Thread.sleep(100);
                                receiverService.executeSQLUpdate(insertRecvQuery, listQryData);
                            }*/
                        }

                    }catch(ImEmailException ex){
                       /* if(listQryData.size() > 100){
                            receiverService.executeSQLUpdate(insertRecvQuery, listQryData);
                        }*/
                        prevEmail = email;
                    }

                    if(listQryData.size() > 100){
                        receiverService.excuteInsert(insertRecvQuery, listQryData);
                    }
                }
            }

            if (listQryData.size() > 0) {
                receiverService.excuteInsert(insertRecvQuery, listQryData);
            }

            logger.info("Record :" + recordNum + " send : " + nSendCount);
            if (nSendCount == 0 && alreadyExtractedCount <= 0) {
                emsMain.setState(ImStateCode.ST_NO_RECV);
                emsMain.setTotal_send(recordNum);
                emsMainService.updateStateAndCount(emsMain);
                try {
                    errorCountService.updateBasicErrorCount(nEmailAddr, nReject, nRepeat, nDomain, nBlank, emsMain.getMsgid());
                    logger.info("ExtractRecv No Recv: {} - {}", emsMain.getMsgid(), emsMain.getMsg_name());
                }catch(Exception e){}
            } else if (nSendCount == -1) {
                if (recordNum <= 0) {
                    emsMain.setState(ImStateCode.ST_FAIL_RECV);
                    emsMain.setTotal_send(recordNum);
                    emsMainService.updateStateAndCount(emsMain);
                    try {
                        errorCountService.updateBasicErrorCount(nEmailAddr, nReject, nRepeat, nDomain, nBlank, emsMain.getMsgid());
                        logger.info("ExtractRecv Fail Recv: {} - {}", emsMain.getMsgid(), emsMain.getMsg_name());
                    }catch(Exception e){}
                }
            } else {
                emsMain.setState(ImStateCode.ST_FINISH_RECV);
                emsMain.setTotal_send(recordNum);
                emsMainService.updateStateAndCount(emsMain);
                try {
                    errorCountService.updateBasicErrorCount(nEmailAddr, nReject, nRepeat, nDomain, nBlank, emsMain.getMsgid());
                    logger.info("ExtractRecv Finish Recv: {} - {}", emsMain.getMsgid(), emsMain.getMsg_name());
                }catch(Exception e){}
//                if(recordNum > 1000000) System.gc();
            }
        }catch(Exception ex){
            nSendCount = -1;
            emsMain.setState(ImStateCode.ST_FAIL_RECV);
            emsMain.setTotal_send(recordNum);
            emsMainService.updateStateAndCount(emsMain);

            logger.info("recv main :"+ ex+" curremail:"+email+" record : " +nIdNum);
            String errorId = ErrorTraceLogger.log(ex);
            logger.error("{} - ImExtractRecvThread.extractReceiver Error - {}", errorId, emsMain.getMsgid() );
            throw ex;
        }finally {
            try { if(rs != null) rs.close(); }catch(Exception e){}
            try { if(ps != null) ps.close(); }catch(Exception e){}
            try { if(conn != null) conn.close(); }catch(Exception e){}

        }
    }

    @Override
    public void run()  {
        String currentThreadName = Thread.currentThread().getName();

        if(ImEmsServer.isAlive){
            ReceiverService receiverService = ReceiverService.getInstance();
            DatabaseService databaseService = DatabaseService.getInstance();
            try {
                if (emsMain == null) {
                    throw new Exception("EmsMain & msgid is null");
                }

                logger.debug("ImExtractRecvThread.run 0 : {}", emsMain.getMsgid());
                // 수신거부 목록
                listReject = getListReject();
                // 필터링도메인
                listFilterDomain = getListFilterDomain();
                logger.debug("ImExtractRecvThread.run 1 : {}", emsMain.getMsgid());

                // 수신자를 뽑아서 recv_[msgid] 테이블에 넣는다.
                String rectype = emsMain.getRectype();
                logger.debug("ImExtractRecvThread.run 2 : {} - rectype: {}", emsMain.getMsgid(), rectype);

                // 주소록 or 재발신
                if (ImbEmsMain.RECTYPE_ADDR.equals(rectype) || ImbEmsMain.RECTYPE_RESEND.equals(rectype)) {
                    extractReceiver(emsMain, null);
                    // 수신그룹
                } else if (ImbEmsMain.RECTYPE_RECV.equals(rectype)) {
                    logger.debug("ImExtractRecvThread.run 3 : {} - rectype: {}, dbkey: {}", emsMain.getMsgid(), rectype, emsMain.getDbkey());
                    String db_key = emsMain.getDbkey();
                    // imb_dbinfo에서 조회
                    ImbDBInfo dbInfo = databaseService.getDBInfoByUkey(db_key);

                    extractReceiver(emsMain, dbInfo);
                }

                if (Thread.interrupted()) {
                    logger.info("<" + currentThreadName + ", " + emsMain.getMsgid() + "> Interrupted & Stopped");
                }
            }catch(InterruptedException ex) {
                    //ex.printStackTrace();
                logger.info("<" + currentThreadName + ", " + emsMain.getMsgid() + "> Interrupted & Stopped.");
            }catch(Exception ex) {
                String errorId = ErrorTraceLogger.log(ex);
                logger.error("{} - ImbExtractRecvThread Error - {}", errorId, emsMain.getMsgid() );
            }finally {
                if(ImEmsMainThread.taskExtractMap.containsKey(emsMain.getMsgid())) {
                    ImEmsMainThread.taskExtractMap.remove(emsMain.getMsgid());
                }
            }
        }
        logger.info("ImbExtractRecvThread Shutdown - {}", emsMain.getMsgid());
    }
}

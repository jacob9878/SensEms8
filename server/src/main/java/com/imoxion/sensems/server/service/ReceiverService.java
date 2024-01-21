package com.imoxion.sensems.server.service;

import com.imoxion.sensems.server.beans.ImEMSQueryData;
import com.imoxion.sensems.server.define.ImJdbcDriver;
import com.imoxion.sensems.server.domain.ImbDBInfo;
import com.imoxion.sensems.server.domain.ImbDomainCount;
import com.imoxion.sensems.server.domain.ImbErrorCount;
import com.imoxion.sensems.server.domain.ImbReceiver;
import com.imoxion.sensems.server.logger.ErrorTraceLogger;
import com.imoxion.sensems.server.repository.ErrorCountRepository;
import com.imoxion.sensems.server.repository.MsgidHostCountRepository;
import com.imoxion.sensems.server.repository.MsgidRecvRepository;
import com.imoxion.sensems.server.repository.ReceiverRepository;
import com.imoxion.sensems.server.util.ImEmsUtil;
import org.apache.ibatis.exceptions.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.List;

public class ReceiverService {
    private Logger logger = LoggerFactory.getLogger( "EMSD" );
    //private Logger emsLog = LoggerFactory.getLogger("EMSD");
    private Logger senderLog = LoggerFactory.getLogger("SENDER");

    private static final ReceiverService receiverService = new ReceiverService();
    public static ReceiverService getInstance() {
        return receiverService;
    }
    private ReceiverService() {}
    ///////////////////

    public List<ImbReceiver> getReceiverList() throws Exception {
        ReceiverRepository receiverRepository = ReceiverRepository.getInstance();

        return receiverRepository.getReceiverList();
    }

    /**
     * 특정 수신자 정보 추출(쿼리 복호화 처리)
     */
    public ImbReceiver getReceiverInfo(String ukey) throws Exception {
        ReceiverRepository receiverRepository = ReceiverRepository.getInstance();
        ImbReceiver receiver = receiverRepository.getReceiverInfo(ukey);

//        logger.debug("enc query: {}", receiver.getQuery());
        String deQuery =  ImEmsUtil.getDecryptString(receiver.getQuery());
        receiver.setQuery(deQuery);
//        logger.debug("dec query: {}", deQuery);

        return receiver;
    }

    /**
     * 수신자 추출용 DB에 연결
     */
    public Connection getRecvDBConn(ImbDBInfo dbInfo) throws Exception {
        String jdbc_url = dbInfo.getAddress();
        String jdbcDriver = ImJdbcDriver.getDriver(dbInfo.getDbtype());
        String db_uid = dbInfo.getDbuser();
        String db_pwd = dbInfo.getDbpasswd();

        // DriverManager.getConnection(url,userid,password);
        Class.forName(jdbcDriver);
        Connection conn = DriverManager.getConnection(jdbc_url, db_uid, db_pwd);

        return conn;
    }

    /**
     * 혹시 기존에 추출하던 정보가 있는지 확인한다.
     * 기존에 뽑던 데이터가 있으면 중지후 재전송이므로 뽑아진 이후부터 뽑는다.
     */
    public int getAlreadyExtractedCount(String msgid) {
        int maxRecvCount = -1;
        MsgidRecvRepository msgidRecvRepository = MsgidRecvRepository.getInstance();
        try {
            maxRecvCount = msgidRecvRepository.getMaxRecvCount(msgid);
        } catch (PersistenceException | SQLException ex) {
            logger.error("getAlreadyExtractedCount is -1");
        } catch (Exception ex) {
            logger.error("getAlreadyExtractedCount is -1");
        }

        return maxRecvCount;
    }

    /**
     * recv_[msgid] 테이블 생성
     */
    public void createExtractRecvQuery(String extractRecvQuery){
        MsgidRecvRepository msgidRecvRepository = MsgidRecvRepository.getInstance();
        try {
            msgidRecvRepository.createExtractRecvQuery(extractRecvQuery);
        } catch (PersistenceException | SQLException e) {
            String errorId = ErrorTraceLogger.log(e);
            logger.error("{} - createExtractRecvQuery error: {}", errorId, extractRecvQuery);
        } catch (Exception ex) {
            String errorId = ErrorTraceLogger.log(ex);
            logger.error("createExtractRecvQuery error: {}", errorId, extractRecvQuery);
        }
    }

    /**
     * recv_[msgid] 테이블 생성 쿼리를 만든다.
     */
    public String getCreateRecvQuery(String msgid, ResultSetMetaData metaData){
        String query = "";
        try {
            query = "create table recv_" + msgid +
                    " (id int not null, domain varchar(100), success char(1) default '2', errcode char(3), err_exp varchar(200), send_time varchar(14)," +
                    " recv_time varchar(14), recv_count int default 0";
            int fieldCount = metaData.getColumnCount();
            for(int i=1; i<=fieldCount; i++){
                query += ", field" + i+" ";
                switch(metaData.getColumnType(i)){
                    case Types.CHAR:     // 1
                    case Types.VARCHAR:    // 12
                        if(metaData.getColumnDisplaySize(i)> 4000){
                            query += "varchar(4000)";
                        }else{
                            query += "varchar("+ metaData.getColumnDisplaySize(i) +")";
                        }
                        break;
                    case Types.LONGVARCHAR:     // -1
                        query += "text";
                        break;
                    case Types.NUMERIC:
                        query += "int";
                        break;
                    default:
                        query += "varchar(30)";
                        break;
                }
            }
            query += ",is_resend tinyint default 0, up_date datetime, recv_date varchar(8), recv_hour varchar(2), primary key(id), key idx_field1(field1), key idx_success(success) ) ENGINE=MyISAM";
        } catch(Exception e){
            String errorId = ErrorTraceLogger.log(e);
            logger.error("{} - getCreateRecvQuery error: {}", errorId, query);
        }

        return query;
    }

    public void excuteInsert(String insertRecvQuery, List<ImEMSQueryData> listQryData){
        MsgidRecvRepository msgidRecvRepository = MsgidRecvRepository.getInstance();
        try {
            msgidRecvRepository.excuteInsert(insertRecvQuery, listQryData);
        } catch (Exception e) {
            String errorId = ErrorTraceLogger.log(e);
            logger.error("{} - createExtractRecvQuery error: {}", errorId, insertRecvQuery);
        } finally {
            listQryData.clear();
        }
    }

    public int getRecvSendingCount(String msgid){
        MsgidRecvRepository msgidRecvRepository = MsgidRecvRepository.getInstance();
        int sendingCount = -1;
        try {
            sendingCount = msgidRecvRepository.getRecvSendingCount(msgid);
        } catch (PersistenceException | SQLException ex) {
            logger.error("getRecvSendingCount is -1");
        } catch (Exception ex) {
            logger.error("getRecvSendingCount is -1");
        }

        return sendingCount;
    }

    public List<ImbDomainCount> getSendCountByDomain(String msgid) {
        MsgidRecvRepository msgidRecvRepository = MsgidRecvRepository.getInstance();
        List<ImbDomainCount> imbDomainCountList = null;
        try {
            imbDomainCountList = msgidRecvRepository.getSendCountByDomain(msgid);
        } catch (PersistenceException | SQLException ex) {
            logger.error("getSendCountByDomain error: {}", ex.getMessage());
        } catch (Exception ex) {
            logger.error("getSendCountByDomain error: {}", ex.getMessage());
        }

        return imbDomainCountList;
    }

    /*public List<ImbErrorCount> getErrorCountByErrorcode(String msgid) {
        MsgidRecvRepository msgidRecvRepository = MsgidRecvRepository.getInstance();

        try {
            imbDomainCountList = msgidRecvRepository.getErrorCountByErrorcode(msgid);
        } catch (PersistenceException | SQLException ex) {
            logger.error("getSendCountByDomain error: {}", ex.getMessage());
        } catch (Exception ex) {
            logger.error("getSendCountByDomain error: {}", ex.getMessage());
        }

        return imbDomainCountList;
    }*/



    public void insertHostCountByDomain(String msgid) {
        MsgidRecvRepository msgidRecvRepository = MsgidRecvRepository.getInstance();
        MsgidHostCountRepository msgidHostCountRepository = MsgidHostCountRepository.getInstance();
        try {
            List<ImbDomainCount> domainCountList = msgidRecvRepository.getSendCountByDomain(msgid);
            msgidHostCountRepository.insertHostCount(msgid, domainCountList);
        }catch (PersistenceException | SQLException ex) {
                logger.error("insertHostCountByDomain error: {}", ex.getMessage());
        }catch (Exception ex) {
            logger.error("insertHostCountByDomain error: {}", ex.getMessage());
        }
    }

    public void createHostCount(String msgid) {
        MsgidHostCountRepository msgidHostCountRepository = MsgidHostCountRepository.getInstance();
        try {
            msgidHostCountRepository.createHostCount(msgid);
        }catch (PersistenceException | SQLException ex) {
            logger.error("createHostCount error: {}", ex.getMessage());
        }catch (Exception ex) {
            logger.error("createHostCount error: {}", ex.getMessage());
        }
    }

    public void updateErrorCountByDomain(String msgid) {
        //emsLog.info("updateErrorCountByDomain: 111");
        MsgidRecvRepository msgidRecvRepository = MsgidRecvRepository.getInstance();
        ErrorCountRepository errorCountRepository = ErrorCountRepository.getInstance();
        try {
            //emsLog.info("updateErrorCountByDomain: 222");
            //int[] arrErrors = msgidRecvRepository.getErrorCountByErrorcode(msgid);
            ImbErrorCount errorCount = msgidRecvRepository.getErrorCountByErrorcode(msgid);
            //emsLog.info("updateErrorCountByDomain: {}", errorCount.toString());
            errorCountRepository.updateErrorCount(errorCount);
        }catch (PersistenceException | SQLException ex) {
            logger.error("updateErrorCountByDomain error: {}", ex.getMessage());
        }catch (Exception ex) {
            logger.error("updateErrorCountByDomain error: {}", ex.getMessage());
        }
    }

    public String getResendQueryColumn(String msgid) {
        MsgidRecvRepository msgidRecvRepository = MsgidRecvRepository.getInstance();
        String result = "";
        try {
            result = msgidRecvRepository.getResendQueryColumn(msgid);
        }catch (PersistenceException | SQLException ex) {
            logger.error("getResendQueryColumn error: {}", ex.getMessage());
        }catch (Exception ex) {
            logger.error("getResendQueryColumn error: {}", ex.getMessage());
        }
        return result;
    }

    public boolean existResendTarget(String msgid) {
        MsgidRecvRepository msgidRecvRepository = MsgidRecvRepository.getInstance();

        try {
            List<String> listTarget = msgidRecvRepository.getResendTarget(msgid);
            if(listTarget != null && listTarget.size() > 0){
                return true;
            }
        }catch (PersistenceException | SQLException ex) {
            logger.error("getResendQueryColumn error: {}", ex.getMessage());
        }catch (Exception ex) {
            logger.error("getResendQueryColumn error: {}", ex.getMessage());
        }
        return false;
    }




    /**
     * 수신그룹을 이용하여 메일을 발송하기 위해 수신 대상자를 추출한다.
     */
    /*public boolean extractReceiver(ImbEmsMain emsMain, ImbDBInfo dbInfo) {
        EmsMainService emsMainService = EmsMainService.getInstance();
        DatabaseService databaseService = DatabaseService.getInstance();
        ErrorCountService errorCountService = ErrorCountService.getInstance();

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String extractQuery = "";
        String insertRecvQuery = "";

        int fieldCount = 0;
        int alreadyExtractedCount = -1;
        int recordNum = 0;
        int curIdNum = 0;
        boolean bResend = false;
        String prevEmail = "";
        int nMaxRecvCount = 0;
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
            if(StringUtils.isBlank(emsMain.getQuery())){
                emsMain.setState(ImStateCode.ST_FAIL_RECV);
                emsMain.setTotal_send(0);
                // 수신자 생성 실패
                emsMainService.updateStateAndCount(emsMain);
                // imb_error_count 에 업데이트
                errorCountService.insertErrorCountInit(emsMain.getMsgid());

                return true;
            }

            // extended: 999 재발신(에러재발신, 미수신자 재발신 등)
            if("999".equals(emsMain.getExtended())){
                bResend = true;
            }

            // 혹시 기존에 추출하던 정보가 있는지 확인한다.
            // 기존에 뽑던 데이터가 있으면 중지후 재전송이 되겟죠
            alreadyExtractedCount = getAlreadyExtractedCount(emsMain.getMsgid());

            // 주소록인지 수신그룹인지 재발신인지
            if(emsMain.getRectype() == ImbEmsMain.RECTYPE_ADDR || emsMain.getRectype() == ImbEmsMain.RECTYPE_RESEND){
                conn = databaseService.getLocalDBConn();
                extractQuery = ImEmsUtil.getDecryptString(emsMain.getQuery());
            } else if(emsMain.getRectype() == ImbEmsMain.RECTYPE_RECV) {
                conn = getRecvDBConn(dbInfo);
                extractQuery = ImEmsUtil.getDecryptString(emsMain.getQuery());
                //ImbReceiver receiverInfo = getReceiverInfo(emsMain.getRecid());
            }

            ps = conn.prepareStatement(extractQuery);
            ps.setFetchSize(100);
            rs = ps.executeQuery();

            // 추출된 데이터의 기본 정보를 뽑는다
            if(rs != null){

                ResultSetMetaData rsMetaData = rs.getMetaData();
                fieldCount = rsMetaData.getColumnCount();

                // 처음 실행되기 때문에 recv_[msgid] 테이블생성, imb_error_count 에 초기값 세팅
                if(alreadyExtractedCount < 0){
                    extractQuery = extractRecvQuery(emsMain.getMsgid(), rsMetaData);
                    createExtractRecvQuery(extractQuery);
                    errorCountService.insertErrorCountInit(emsMain.getMsgid());
                }

                SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");

                while(rs.next() && ImEmsServer.bIsAlive){
                    String strCurrDate = df.format(new Date(System.currentTimeMillis()));

                    // 이전에 추출중이었으면 추출된 데이타 이후부터 뽑는다.
                    ImRecvRecordData recvRecordData = new ImRecvRecordData();

                    if(alreadyExtractedCount > recordNum++) continue;

                    // recv_[msgid] 테이블 인서트 쿼리
                    insertRecvQuery = "INSERT INTO recv_"+ emsMain.getMsgid() +" VALUES(?,?,?,?,?,?,'',0";
                    for(int i=1;i<=fieldCount;i++){
                        recvRecordData.addRecord("FIELD"+i, rs.getString(i));
                        insertRecvQuery += ",?";
                    }
                    insertRecvQuery += ",0,NULL)";

                    if(bResend){
                        nIdNum = rs.getInt("id");
                    }else{
                        nIdNum = recordNum;
                    }

                    email = ImStringUtil.getSafeString(rs.getString(1)).toLowerCase().trim();

                    // 이메일주소 값 없음
                    if(StringUtils.isEmpty(email)){
                        nBlank++;
                        // String sID, String sCurrTime, String sErrorCode, String sErrorStr, String sSuccess, String sDomain, ImRecvRecordData rData
                        ImEMSQueryData qData = new ImEMSQueryData(String.valueOf(nIdNum), strCurrDate,
                                "915","Empty Email Address",
                                "0", "", recvRecordData);
                        listQryData.add(qData);
                        throw new ImEmailException();
                    }

                    // 이메일 형식 오류
                    if(!EmailValidator.getInstance().isValid(email)){
                        nEmailAddr++;
                        ImEMSQueryData qData = new ImEMSQueryData(String.valueOf(nIdNum), strCurrDate,
                                "911","Invalid Email Address",
                                "0", "", recvRecordData);
                        listQryData.add(qData);

                        throw new ImEmailException();
                    }

                    String domain = StringUtils.substringAfter(email, "@");

                    // 수신거부 이메일주소
                    if(ImExtractRecvThread.isExistRejectList(email)){
                        nReject++;

                        ImEMSQueryData qData = new ImEMSQueryData(String.valueOf(nIdNum), strCurrDate,
                                "912","Reject Email Address",
                                "0", domain, recvRecordData);
                        listQryData.add(qData);

                        throw new ImEmailException();;
                    }

                    // 필터링 도메인
                    if(ImExtractRecvThread.isExistFilterDomainList(domain)){
                        nDomain++;

                        ImEMSQueryData qData = new ImEMSQueryData(String.valueOf(nIdNum), strCurrDate,
                                "914","Filtered Domain Address",
                                "0", domain, recvRecordData);
                        listQryData.add(qData);

                        throw new ImEmailException();;
                    }

                    // 이메일 중복여부 체크( 1== 중복허용, 0== 중복 오류)
                    if(emsMain.getIs_same_email() == 0){
                        if(!email.equals(prevEmail)){
                            nSendCount++;
                            ImEMSQueryData qData = new ImEMSQueryData(String.valueOf(nIdNum), strCurrDate,
                                    "","",
                                    "2", domain, recvRecordData);
                            listQryData.add(qData);

                            // - 수신자 수가 적을 때는 덩어리로 발송하는 갯수를 작게(10 > 20 > 30)
                            if(nSendCount > 100) {
                                nMaxRecv = 20;
                            } else if(nSendCount > 1000) {
                                nMaxRecv = ImEmsConfig.getInstance().getMaxRecvCount();
                            }

                            if((nSendCount % nMaxRecv) == 0){
                                Thread.sleep(100);
                                executeSQLUpdate(insertRecvQuery, listQryData);
                            }

                            prevEmail = email;
                            curIdNum = nIdNum;
                        } else {
                            nRepeat++;
                            ImEMSQueryData qData = new ImEMSQueryData(String.valueOf(nIdNum), strCurrDate,
                                    "913","Duplicated Email Address",
                                    "0", domain, recvRecordData);
                            listQryData.add(qData);

                            if(listQryData.size() > 100){
                                executeSQLUpdate(insertRecvQuery, listQryData);
                            }
                        }
                    } else {
                        nSendCount++;
                        ImEMSQueryData qData = new ImEMSQueryData(String.valueOf(nIdNum), strCurrDate,
                                "","",
                                "2", domain, recvRecordData);
                        listQryData.add(qData);

                        // - 수신자 수가 적을 때는 덩어리로 발송하는 갯수를 작게(10 > 20 > 30)
                        if(nSendCount > 100) {
                            nMaxRecv = 20;
                        } else if(nSendCount > 1000) {
                            nMaxRecv = ImEmsConfig.getInstance().getMaxRecvCount();
                        }

                        if((nSendCount % nMaxRecv) == 0){
                            Thread.sleep(100);
                            executeSQLUpdate(insertRecvQuery, listQryData);
                        }
                    }
                }
            }

            if(listQryData.size() > 0){
                executeSQLUpdate(insertRecvQuery, listQryData);
            }

            logger.info("Record :"+recordNum+" send : "+nSendCount);
            if(nSendCount == 0){
                emsMain.setState(ImStateCode.ST_NO_RECV);
                emsMain.setTotal_send(recordNum);
                emsMainService.updateStateAndCount(emsMain);
                errorCountService.updateBasicErrorCount(nEmailAddr, nReject, nRepeat, nDomain, nBlank, emsMain.getMsgid());
                //dao.updateMainStateEx("-00", String.valueOf(lRecordNum), oEmsData.getCategoryId(), oEmsData.getMsgid());
                //dao.updateError(lEmailAddr, lReject, lRepeat, lDomain, lBlank, oEmsData.getMsgid());
            }else if(nSendCount == -1){
                if(recordNum <= 0){
                    emsMain.setState(ImStateCode.ST_FAIL_RECV);
                    emsMain.setTotal_send(recordNum);
                    emsMainService.updateStateAndCount(emsMain);
                    errorCountService.updateBasicErrorCount(nEmailAddr, nReject, nRepeat, nDomain, nBlank, emsMain.getMsgid());
                    //dao.updateMainStateEx("-10", String.valueOf(lRecordNum), oEmsData.getCategoryId(), oEmsData.getMsgid());
                    //dao.updateError(lEmailAddr, lReject, lRepeat, lDomain, lBlank, oEmsData.getMsgid());
                }
            }else{
                if(bIsAlive){
                    dao.updateMainStateEx("+10", String.valueOf(lRecordNum), oEmsData.getCategoryId(), oEmsData.getMsgid());
                    dao.updateError(lEmailAddr, lReject, lRepeat, lDomain, lBlank, oEmsData.getMsgid());
                    if(lRecordNum > 1000000)
                        System.gc();
                }
            }
        }catch(Exception ex){
            nSendCount = -1;
            logger.info("recv main :"+ ex+" curremail:"+email+" record : " +nIdNum);
            String errorId = ErrorTraceLogger.log(ex);
            logger.error("{} - ReceiverService.extractReceiver Error - {}", errorId, emsMain.getMsgid() );
        }finally {
            try { if(rs != null) rs.close(); }catch(Exception e){}
            try { if(ps != null) ps.close(); }catch(Exception e){}
            try { if(conn != null) conn.close(); }catch(Exception e){}
        }



        return true;
    }*/
}

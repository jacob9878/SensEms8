package com.imoxion.sensems.server.repository;

import com.imoxion.common.database.ImDatabaseConnectionEx;
import com.imoxion.common.util.ImStringUtil;
import com.imoxion.sensems.server.beans.ImEMSQueryData;
import com.imoxion.sensems.server.beans.ImSenderQueryData;
import com.imoxion.sensems.server.domain.ImbDomainCount;
import com.imoxion.sensems.server.domain.ImbErrorCount;
import com.imoxion.sensems.server.repository.mapper.MsgidRecvMapper;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MsgidRecvRepository {
    private Logger logger = LoggerFactory.getLogger("EMSD");
    private Logger senderLog = LoggerFactory.getLogger("SENDER");

    private static final MsgidRecvRepository msgidRecvRepository = new MsgidRecvRepository();

    public static MsgidRecvRepository getInstance(){
        return msgidRecvRepository;
    }

    private MsgidRecvRepository() {}

    ////////////////////////////////

    public String getResendQueryColumn(String msgid) throws PersistenceException, SQLException, Exception {
        SqlSession session  = null;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String fetchFields = "";
        try {
            session = ImDatabaseConnectionEx.getConnection();
            conn = session.getConnection();
            String sql = "select * from recv_" + msgid;
            ps = conn.prepareStatement(sql);
            ps.setMaxRows(1);
            rs = ps.executeQuery();

            ResultSetMetaData rsmd = rs.getMetaData();
            int fieldCount = rsmd.getColumnCount();
            StringBuffer sbColumn = new StringBuffer();

            for (int i = 1; i <= fieldCount; i++) {
                if(rsmd.getColumnName(i).toLowerCase().startsWith("field")){
                    if(sbColumn.length() > 0) sbColumn.append(",");
                    sbColumn.append(rsmd.getColumnName(i));
                }
            }

            fetchFields = sbColumn.toString();
        } catch (PersistenceException | SQLException e){
            logger.error("MsgidRecvRepository.getResendQueryColumn error: {}", e.getMessage());
            throw e;
        } finally{
            try{ if( ps != null ) ps.close(); }catch(Exception e){}
            try{ if( conn != null ) conn.close(); }catch(Exception e){}
            try { if(session != null) session.close(); } catch(Exception ee){}
        }

        return fetchFields;
    }

    public List<String> getResendTarget(String msgid) throws PersistenceException, SQLException, Exception {
        try(SqlSession session = ImDatabaseConnectionEx.getConnection(); ){
            MsgidRecvMapper mapper = session.getMapper(MsgidRecvMapper.class);

            return mapper.getResendTarget(msgid);
        } catch (PersistenceException | SQLException se){
            //logger.error("MsgidRecvRepository.getMaxRecvCount error: {}", se.getMessage());
            throw se;
        } catch (Exception e){
            //logger.error("MsgidRecvRepository.getMaxRecvCount error: {}", e.getMessage());
            throw e;
        }
    }

    public int getMaxRecvCount(String msgid) throws PersistenceException, SQLException, Exception {
        try(SqlSession session = ImDatabaseConnectionEx.getConnection(); ){
            MsgidRecvMapper mapper = session.getMapper(MsgidRecvMapper.class);
            String tableName = "recv_" + msgid;
            return mapper.getMaxRecvCount(tableName, msgid);
        } catch (PersistenceException | SQLException se){
            //logger.error("MsgidRecvRepository.getMaxRecvCount error: {}", se.getMessage());
            throw se;
        } catch (Exception e){
            //logger.error("MsgidRecvRepository.getMaxRecvCount error: {}", e.getMessage());
            throw e;
        }
    }

    public List<ImbDomainCount> getSendCountByDomain(String msgid) throws PersistenceException, SQLException, Exception {
        try(SqlSession session = ImDatabaseConnectionEx.getConnection(); ){
            MsgidRecvMapper mapper = session.getMapper(MsgidRecvMapper.class);
            String tableName = "recv_" + msgid;
            return mapper.getSendCountByDomain(tableName);
        } catch (PersistenceException | SQLException se){
//            logger.error("MsgidRecvRepository.getSendCountByDomain error: {}", se.getMessage());
//            throw se;
        } catch (Exception e){
//            logger.error("MsgidRecvRepository.getSendCountByDomain error: {}", e.getMessage());
//            throw e;
        }
        return null;
    }

    /*public int[] getErrorCountByErrorcode(String msgid) throws PersistenceException, SQLException, Exception {
        int[] nErrs = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};

        try(SqlSession session = ImDatabaseConnectionEx.getConnection(); ){
            MsgidRecvMapper mapper = session.getMapper(MsgidRecvMapper.class);
            String tableName = "recv_" + msgid;
            List<Map<String, Object>> errorMapList = mapper.getErrorCountByErrorcode(tableName);

//            ImbErrorCount errorCount = new ImbErrorCount(msgid);

            for(Map<String, Object> errorMap : errorMapList){
                for( Map.Entry<String, Object> elem : errorMap.entrySet() ){
                    String errorCode = elem.getKey();
                    int count = (int) elem.getValue();

                    int nIdx = Integer.parseInt( errorCode ) - 901;
                    nErrs[nIdx] = count;
                }
            }
        } catch (PersistenceException | SQLException se){
            logger.error("MsgidRecvRepository.getErrorCountByErrorcode error: {}", se.getMessage());
            throw se;
        } catch (Exception e){
            logger.error("MsgidRecvRepository.getErrorCountByErrorcode error: {}", e.getMessage());
            throw e;
        }

        return nErrs;
    }*/

    public ImbErrorCount getErrorCountByErrorcode(String msgid) throws PersistenceException, SQLException, Exception {
        ImbErrorCount errorCount = new ImbErrorCount(msgid);
        try(SqlSession session = ImDatabaseConnectionEx.getConnection(); ){
            MsgidRecvMapper mapper = session.getMapper(MsgidRecvMapper.class);
            String tableName = "recv_" + msgid;
logger.info("getErrorCountByErrorcode: msgid:{}", msgid);
            List<Map<String, Object>> errorMapList = mapper.getErrorCountByErrorcode(tableName);

            for(Map<String, Object> errorMap : errorMapList){
                String errorCode = errorMap.get("errcode").toString();
                int count = ImStringUtil.parseInt(errorMap.get("cnt").toString());
                logger.info("getErrorCountByErrorcode: errorCode:{}, count:{}", errorCode, count);
                errorCount.setErrorCount(errorCode, count);
                /*for( Map.Entry<String, Object> elem : errorMap.entrySet() ){

                    String errorCode = elem.getKey();
                    int count = ImStringUtil.parseInt(elem.getValue());
logger.info("getErrorCountByErrorcode: errorCode:{}, count:{}", errorCode, count);
                    errorCount.setErrorCount(errorCode, count);
                }*/
            }
        } catch (PersistenceException | SQLException se){
            logger.error("MsgidRecvRepository.getErrorCountByErrorcode error: {}", se.getMessage());
            throw se;
        } catch (Exception e){
            logger.error("MsgidRecvRepository.getErrorCountByErrorcode error: {}", e.getMessage());
            throw e;
        }

        return errorCount;
    }


    public int getRecvSendingCount(String msgid) throws PersistenceException, SQLException, Exception {
        try(SqlSession session = ImDatabaseConnectionEx.getConnection(); ){
            MsgidRecvMapper mapper = session.getMapper(MsgidRecvMapper.class);
            String tableName = "recv_" + msgid;
            return mapper.getRecvSendingCount(tableName);
        } catch (PersistenceException | SQLException se){
            logger.error("MsgidRecvRepository.getRecvSendingCount error: {}", se.getMessage());
            throw se;
        } catch (Exception e){
            logger.error("MsgidRecvRepository.getRecvSendingCount error: {}", e.getMessage());
            throw e;
        }
    }

    public List<Map<String, Object>> getRecvTransfer(String msgid) throws PersistenceException, SQLException, Exception {
        try(SqlSession session = ImDatabaseConnectionEx.getConnection(); ){
            MsgidRecvMapper mapper = session.getMapper(MsgidRecvMapper.class);
            String tableName = "recv_" + msgid;
            return mapper.getRecvTransfer(tableName);
        } catch (PersistenceException | SQLException se){
            logger.error("MsgidRecvRepository.getRecvTransfer error: {}", se.getMessage());
            throw se;
        } catch (Exception e){
            throw e;
        }
    }

    public void createExtractRecvQuery(String extractRecvQuery) throws PersistenceException, SQLException, Exception {
        try(SqlSession session = ImDatabaseConnectionEx.getConnection(); ){
            MsgidRecvMapper mapper = session.getMapper(MsgidRecvMapper.class);

            mapper.createMsgidRecvTable(extractRecvQuery);
        } catch (PersistenceException | SQLException se){
            logger.error("MsgidRecvRepository.createExtractRecvQuery error: {}", se.getMessage());
            throw se;
        } catch (Exception e){
            throw e;
        }
    }

    public void dropRecvTable(String msgid) throws PersistenceException, SQLException, Exception {
        try(SqlSession session = ImDatabaseConnectionEx.getConnection(); ){
            MsgidRecvMapper mapper = session.getMapper(MsgidRecvMapper.class);
            String tableName = "recv_" + msgid;
            mapper.dropRecvTable(tableName);
        } catch (PersistenceException | SQLException se){
            logger.error("MsgidRecvRepository.createExtractRecvQuery error: {}", se.getMessage());
            throw se;
        } catch (Exception e){
            throw e;
        }
    }

    public void excuteInsert(String insertRecvQuery, List<ImEMSQueryData>  listQryData) throws PersistenceException, SQLException, Exception {
        SqlSession session  = null;
        Connection conn = null;
        PreparedStatement ps = null;
        try {
//            logger.info("excuteInsert 000");
            session = ImDatabaseConnectionEx.getConnection();
            conn = session.getConnection();
            ps = conn.prepareStatement(insertRecvQuery);
//            logger.info("excuteInsert 111");
            for(ImEMSQueryData queryData : listQryData){
//                logger.info("excuteInsert -- {}", queryData.toString());
                ps.setString(1, queryData.getId());
                ps.setString(2, queryData.getDomain());
                ps.setString(3, queryData.getSuccess());
                ps.setString(4, queryData.getErrorCode());
                ps.setString(5, queryData.getErrorStr());
                ps.setString(6, queryData.getCurrTime());

                for(int j=0; j<queryData.getRData().size(); j++){
                    ps.setString(j+7, queryData.getRData().getValue(j));
                }

                ps.addBatch();
            }
            ps.executeBatch();
        } catch (PersistenceException | SQLException e){
            logger.error("MsgidRecvRepository.excuteInsert error: {}", e.getMessage());
            throw e;
        } finally{
            try{ if( ps != null ) ps.close(); }catch(Exception e){}
            try{ if( conn != null ) conn.close(); }catch(Exception e){}
            try { if(session != null) session.close(); } catch(Exception ee){}
        }
    }

    public void excuteUpdateResult(List<ImSenderQueryData> listQryData) throws PersistenceException, SQLException {
        SqlSession session  = null;
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            if(listQryData == null) {
                senderLog.error("MsgidRecvRepository.excuteUpdateResult: listQryData is null");
                return;
            }

            session = ImDatabaseConnectionEx.getConnection();
            conn = session.getConnection();

            SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
            String strCurrDate = df.format(new Date());

            ImSenderQueryData qData = listQryData.get(0);

            // "update recv_" + emsMain.getMsgid() + " set success=?, errcode=?, err_exp=?, send_time=? where id=?";
            ps = conn.prepareStatement(qData.getQuery());
            senderLog.info("excuteUpdateResult : {}", qData.getQuery());
            int i = 1;
            for(ImSenderQueryData queryData : listQryData){
                ps.setString(1,Integer.toString(queryData.getSuccess()));
                ps.setString(2,Integer.toString(queryData.getReponse()));
                ps.setString(3, ImStringUtil.stringCutterByte(queryData.getErrStr(), 199));
                ps.setString(4,strCurrDate);
                ps.setInt(5,ImStringUtil.parseInt(queryData.getId()));
                ps.addBatch();
                if(i % 1000 == 0) ps.executeBatch();
                i++;
            }
            ps.executeBatch();
        } catch (PersistenceException | SQLException e){
            senderLog.error("MsgidRecvRepository.excuteUpdateResult error: {}", e.getMessage());
            throw e;
        } finally{
            try{ if( ps != null ) ps.close(); }catch(Exception e){}
            try{ if( conn != null ) conn.close(); }catch(Exception e){}
            try { if(session != null) session.close(); } catch(Exception ee){}
        }
    }


}

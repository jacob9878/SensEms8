package com.imoxion.sensems.server.repository;

import com.imoxion.common.database.ImDatabaseConnectionEx;
import com.imoxion.sensems.server.domain.*;
import com.imoxion.sensems.server.repository.mapper.*;
import org.apache.ibatis.session.SqlSession;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * smtp 계정, 도메인, 릴레이아이피, 거부 아이피 정보를 db에서 뽑아서 체크
 */
public class SmtpRepository {
    private static final SmtpRepository smtpDatabaseService = new SmtpRepository();

    public static SmtpRepository getInstance() {
        return smtpDatabaseService;
    }
    private SmtpRepository(){}
    //------------------------

    public List<Dkim> getDkimList() throws Exception{
        try(SqlSession session = ImDatabaseConnectionEx.getConnection()){
            DkimMapper dkimMapper = session.getMapper(DkimMapper.class);
            return dkimMapper.getDkimList();
        }
    }

    public List<RelayIp> getRelayIpList() throws Exception {
        try (SqlSession session = ImDatabaseConnectionEx.getConnection()) {
            RelayIpMapper relayIpMapper = session.getMapper(RelayIpMapper.class);
            return relayIpMapper.getRelayIpList();
        }
    }

    public RelayIp getRelayIp(String ip) throws Exception {
        try (SqlSession session = ImDatabaseConnectionEx.getConnection()) {
            RelayIpMapper relayIpMapper = session.getMapper(RelayIpMapper.class);
            return relayIpMapper.getRelayIp(ip);
        }
    }

    public List<DenyIp> getDenyIpList() throws Exception {
        try (SqlSession session = ImDatabaseConnectionEx.getConnection()) {
            DenyIpMapper denyIpMapper = session.getMapper(DenyIpMapper.class);
            return denyIpMapper.getDenyIpList();
        }
    }

    public ImbUserinfo getUserInfo(String userid) throws Exception {
        try (SqlSession session = ImDatabaseConnectionEx.getConnection()) {
            UserInfoMapper userInfoMapper = session.getMapper(UserInfoMapper.class);
            return userInfoMapper.getUserInfo(userid);
        }
    }

    /**
     *
     * @param email
     * @param domain : *@domain
     * @return
     */
    public int getBlockEmailCount(String email, String domain) throws Exception {
        try (SqlSession session = ImDatabaseConnectionEx.getConnection()) {
            BlockEmailMapper blockEmailMapper = session.getMapper(BlockEmailMapper.class);
            return blockEmailMapper.getBlockEmailCount(email, domain);
        }
    }

    /**
     *
     * @param email
     * @param domain  : *@domain
     * @param toemail
     * @return
     * @throws Exception
     */
    public int getBlockEmailCountEx(String email, String domain, String toemail) throws Exception {
        try (SqlSession session = ImDatabaseConnectionEx.getConnection()) {
            BlockEmailMapper blockEmailMapper = session.getMapper(BlockEmailMapper.class);
            return blockEmailMapper.getBlockEmailCountEx(email, domain, toemail);
        }
    }

    /**
     *
     * @Method Name  : getLimitInfo
     * @Method Comment : 서버 한계값 정보를 가져온다.(서버 설정 정보)
     *
     * @return 서버 한계값 Map 객체
     */
    public ConcurrentHashMap<String, String> getLimitInfo() throws SQLException {
        try (SqlSession session = ImDatabaseConnectionEx.getConnection()) {
            LimitInfoMapper limitInfoMapper = session.getMapper(LimitInfoMapper.class);
            List<LimitInfo> ret = limitInfoMapper.getLimitInfoList();
            ConcurrentHashMap<String, String> mapConf = new ConcurrentHashMap<String, String>();
            for(LimitInfo limit : ret ){
                mapConf.put(limit.getLimit_type(),limit.getLimit_value());
            }
            return mapConf;
        }
    }

    /**
     * 송수신내역 삭제
     * @param saveDate
     * @throws Exception
     */
    public void deleteTransmitLogData(int saveDate) throws Exception {
        try (SqlSession session = ImDatabaseConnectionEx.getConnection()) {
            TransmitStaticsticMapper transmitLogMapper = session.getMapper(TransmitStaticsticMapper.class);
            transmitLogMapper.deleteTransmitLogData(saveDate);
        }
    }

    public List<SmtpTempMain> getTempMainList() throws Exception {
        try (SqlSession session = ImDatabaseConnectionEx.getConnection()) {
            SmtpTempMainMapper tempMainMapper = session.getMapper(SmtpTempMainMapper.class);
            return tempMainMapper.getTempMainList();
        }
    }

    public List<SmtpTempRcpt> getTempRcptList(String mainkey) throws Exception {
        try (SqlSession session = ImDatabaseConnectionEx.getConnection()) {
            SmtpTempMainMapper tempMainMapper = session.getMapper(SmtpTempMainMapper.class);
            return tempMainMapper.getTempRcptList(mainkey);
        }
    }

    public void deleteTempRcptByIdx(long idx) throws Exception {
        try (SqlSession session = ImDatabaseConnectionEx.getConnection()) {
            SmtpTempMainMapper tempMainMapper = session.getMapper(SmtpTempMainMapper.class);
            tempMainMapper.deleteTempRcptByIdx(idx);
        }
    }

    public void deleteTempRcptByIdxListOld(List<SmtpTempRcpt> listRcpt) throws Exception {
        SqlSession session  = null;
        try {
            session = ImDatabaseConnectionEx.getConnection(false);
            SmtpTempMainMapper tempMainMapper = session.getMapper(SmtpTempMainMapper.class);

            for (SmtpTempRcpt tempRcpt : listRcpt) {
                tempMainMapper.deleteTempRcptByIdx(tempRcpt.getIdx());
            }
            session.commit();
        }catch(Exception e){
            if(session != null) session.rollback();
            throw e;
        }finally{
            if(session != null) session.close();
        }
    }

    public void deleteTempRcptByIdxList(List<SmtpTempRcpt> listRcpt) throws Exception {
        try (SqlSession session = ImDatabaseConnectionEx.getConnection()) {
            SmtpTempMainMapper tempMainMapper = session.getMapper(SmtpTempMainMapper.class);
            tempMainMapper.deleteTempRcptByIdxList(listRcpt);
        }
    }

    public void deleteTempRcptByMainkey(String mainkey) throws Exception {
        try (SqlSession session = ImDatabaseConnectionEx.getConnection()) {
            SmtpTempMainMapper tempMainMapper = session.getMapper(SmtpTempMainMapper.class);
            tempMainMapper.deleteTempRcptByMainkey(mainkey);
        }
    }

    public void deleteTempMainByMainkey(String mainkey) throws Exception {
        try (SqlSession session = ImDatabaseConnectionEx.getConnection()) {
            SmtpTempMainMapper tempMainMapper = session.getMapper(SmtpTempMainMapper.class);
            tempMainMapper.deleteTempMainByMainkey(mainkey);
        }
    }

    public void insertTransmitLogData(TransmitStatisticsData transmitStatisticData) throws Exception {
        try (SqlSession session = ImDatabaseConnectionEx.getConnection()) {
            TransmitStaticsticMapper transmitStaticsticMapper = session.getMapper(TransmitStaticsticMapper.class);
            transmitStaticsticMapper.insertTransmitLogData(transmitStatisticData);
        }
    }

}
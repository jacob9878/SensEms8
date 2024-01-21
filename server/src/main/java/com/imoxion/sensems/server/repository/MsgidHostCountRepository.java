package com.imoxion.sensems.server.repository;

import com.imoxion.common.database.ImDatabaseConnectionEx;
import com.imoxion.sensems.server.domain.ImbDomainCount;
import com.imoxion.sensems.server.domain.ImbMsgidHostCount;
import com.imoxion.sensems.server.repository.mapper.MsgidHostCountMapper;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

public class MsgidHostCountRepository {
    private Logger logger = LoggerFactory.getLogger("EMSD");
    private Logger senderLog = LoggerFactory.getLogger("SENDER");

    private static final MsgidHostCountRepository msgidHostCountRepository = new MsgidHostCountRepository();

    public static MsgidHostCountRepository getInstance(){
        return msgidHostCountRepository;
    }

    private MsgidHostCountRepository() {}

    ////////////////////////////////
    public void insertHostCount(String msgid, List<ImbDomainCount> domainCountList) throws PersistenceException, SQLException {
        try (SqlSession session = ImDatabaseConnectionEx.getConnection();) {
            MsgidHostCountMapper mapper = session.getMapper(MsgidHostCountMapper.class);
            String tableName = "hc_" + msgid;

            for(ImbDomainCount domainCount : domainCountList) {
                String domain = domainCount.getDomain();
                int nTotal = domainCount.getSuccessCount() + domainCount.getFailCount();
                int nError = domainCount.getFailCount();
                int nRatio = (int)(((float)(nError)/(float)(nTotal)) * 100);

                ImbMsgidHostCount msgidHostCount = new ImbMsgidHostCount(domain, nTotal, nError, nRatio);

                mapper.insertHostCount(tableName, msgidHostCount.getHostname(), msgidHostCount.getScount(), msgidHostCount.getEcount(), msgidHostCount.getEration());
            }
        } catch (PersistenceException | SQLException se){
            throw se;
        } catch (Exception e){
            throw e;
        }
    }


    public void dropHostCount(String msgid) throws PersistenceException, SQLException {
        try (SqlSession session = ImDatabaseConnectionEx.getConnection();) {
            MsgidHostCountMapper mapper = session.getMapper(MsgidHostCountMapper.class);
            String tableName = "hc_" + msgid;
            mapper.dropHostCount(tableName);
        } catch (PersistenceException | SQLException se){
            throw se;
        } catch (Exception e){
            throw e;
        }
    }

    public void createHostCount(String msgid) throws PersistenceException, SQLException {
        try (SqlSession session = ImDatabaseConnectionEx.getConnection();) {
            MsgidHostCountMapper mapper = session.getMapper(MsgidHostCountMapper.class);
            mapper.createHostCount(msgid);
        } catch (PersistenceException | SQLException se){
            throw se;
        } catch (Exception e){
            throw e;
        }
    }



}

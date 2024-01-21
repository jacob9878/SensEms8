package com.imoxion.sensems.server.repository;

import com.imoxion.common.database.ImDatabaseConnectionEx;
import com.imoxion.sensems.server.domain.ImbReceiver;
import com.imoxion.sensems.server.repository.mapper.ReceiverMapper;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

public class ReceiverRepository {
    private Logger logger = LoggerFactory.getLogger("EMSD");

    private static final ReceiverRepository receiverRepository = new ReceiverRepository();

    public static ReceiverRepository getInstance(){
        return receiverRepository;
    }

    private ReceiverRepository() {}

    ////////////////////////////////

    /**
     * 수신자 목록 추출
     */
    public List<ImbReceiver> getReceiverList() throws PersistenceException, SQLException, Exception {
        try( SqlSession session = ImDatabaseConnectionEx.getConnection(); ){
            ReceiverMapper mapper = session.getMapper(ReceiverMapper.class);
            return mapper.getReceiverList();
        } catch (PersistenceException | SQLException se){
            throw se;
        } catch (Exception e){
            throw e;
        }
    }

    /**
     * 특정 ukey 의 수신자 정보 추출
     */
    public ImbReceiver getReceiverInfo(String ukey) throws PersistenceException, SQLException, Exception {
        try (SqlSession session = ImDatabaseConnectionEx.getConnection();) {
            ReceiverMapper mapper = session.getMapper(ReceiverMapper.class);
            return mapper.getReceiverInfo(ukey);
        } catch (PersistenceException | SQLException se){
            throw se;
        } catch (Exception e){
            throw e;
        }
    }



}

package com.imoxion.sensems.server.repository;

import com.imoxion.common.database.ImDatabaseConnectionEx;
import com.imoxion.sensems.server.repository.mapper.RejectMapper;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

public class RejectRepository {
    private Logger logger = LoggerFactory.getLogger("EMSD");

    private static final RejectRepository rejectRepository = new RejectRepository();

    public static RejectRepository getInstance(){
        return rejectRepository;
    }

    private RejectRepository() {}

    ////////////////////////////////


    public List<String> getRejectList() throws PersistenceException, SQLException, Exception {
        try(SqlSession session = ImDatabaseConnectionEx.getConnection(); ){
            RejectMapper mapper = session.getMapper(RejectMapper.class);
            return mapper.getRejectList();
        } catch (PersistenceException | SQLException se){
            throw se;
        } catch (Exception e){
            throw e;
        }
    }
}

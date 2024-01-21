package com.imoxion.sensems.server.repository;

import com.imoxion.common.database.ImDatabaseConnectionEx;
import com.imoxion.sensems.server.repository.mapper.ActionLogMapper;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class ActionLogRepository {
    private Logger logger = LoggerFactory.getLogger("DAEMON");

    private static final ActionLogRepository actionLogRepository = new ActionLogRepository();

    public static ActionLogRepository getInstance(){
        return actionLogRepository;
    }

    private ActionLogRepository() {}

    ////////////////////////////////


    public void deleteLog(int deleteDelayDays) throws PersistenceException, SQLException, Exception {
        try(SqlSession session = ImDatabaseConnectionEx.getConnection(); ){
            ActionLogMapper actionLogMapper = session.getMapper(ActionLogMapper.class);

            actionLogMapper.deleteLog(deleteDelayDays);
        } catch (PersistenceException | SQLException se){
            throw se;
        } catch (Exception e){
            throw e;
        }
    }


}

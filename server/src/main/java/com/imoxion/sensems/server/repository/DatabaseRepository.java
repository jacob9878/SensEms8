package com.imoxion.sensems.server.repository;

import com.imoxion.common.database.ImDatabaseConnectionEx;
import com.imoxion.sensems.server.domain.ImbDBInfo;
import com.imoxion.sensems.server.repository.mapper.DatabaseMapper;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

public class DatabaseRepository {
    private Logger logger = LoggerFactory.getLogger("EMSD");

    private static final DatabaseRepository databaseRepository = new DatabaseRepository();

    public static DatabaseRepository getInstance(){
        return databaseRepository;
    }

    private DatabaseRepository() {}

    ////////////////////////////////

    /**
     * 데이타베이스 목록 추출
     */
    public List<ImbDBInfo> getDBList() throws PersistenceException, SQLException, Exception {
        try( SqlSession session = ImDatabaseConnectionEx.getConnection(); ){
            DatabaseMapper mapper = session.getMapper(DatabaseMapper.class);
            return  mapper.getDBList();
        } catch (PersistenceException | SQLException se){
            throw se;
        } catch (Exception e){
            throw e;
        }
    }

    /**
     * 특정 ukey 의 데이터베이스 정보 추출
     */
    public ImbDBInfo getDBInfo(String ukey) throws SQLException, Exception {
        try (SqlSession session = ImDatabaseConnectionEx.getConnection();) {
            DatabaseMapper mapper = session.getMapper(DatabaseMapper.class);
            return mapper.getDBInfo(ukey);
        } catch (SQLException se){
            throw se;
        } catch (Exception e){
            throw e;
        }
    }



}

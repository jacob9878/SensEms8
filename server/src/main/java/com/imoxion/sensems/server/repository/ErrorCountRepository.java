package com.imoxion.sensems.server.repository;

import com.imoxion.common.database.ImDatabaseConnectionEx;
import com.imoxion.sensems.server.domain.ImbErrorCount;
import com.imoxion.sensems.server.repository.mapper.ErrorCountMapper;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class ErrorCountRepository {
    private Logger logger = LoggerFactory.getLogger("EMSD");

    private static final ErrorCountRepository errorCountRepository = new ErrorCountRepository();

    public static ErrorCountRepository getInstance(){
        return errorCountRepository;
    }

    private ErrorCountRepository() {}

    ////////////////////////////////

    /**
     * imb_error_count 테이블에 msgid 를 입력
     */
    public void insertErrorCountInit(String msgid) throws SQLException, Exception {
        try(SqlSession session = ImDatabaseConnectionEx.getConnection(); ){
            ErrorCountMapper errorCountMapper = session.getMapper(ErrorCountMapper.class);

            errorCountMapper.insertErrorCountInit(msgid);
        } catch (PersistenceException | SQLException se){
            throw se;
        } catch (Exception e){
            throw e;
        }
    }

    public void delete(String msgid) throws SQLException, Exception {
        try(SqlSession session = ImDatabaseConnectionEx.getConnection(); ){
            ErrorCountMapper errorCountMapper = session.getMapper(ErrorCountMapper.class);

            errorCountMapper.delete(msgid);
        } catch (PersistenceException | SQLException se){
            throw se;
        } catch (Exception e){
            throw e;
        }
    }

    /*public void updateErrorCount(int[] errors, String msgid) throws SQLException, Exception {
        try(SqlSession session = ImDatabaseConnectionEx.getConnection(); ){
            ErrorCountMapper errorCountMapper = session.getMapper(ErrorCountMapper.class);

            errorCountMapper.updateErrorCount(errors, msgid);
        } catch (PersistenceException | SQLException se){
            throw se;
        } catch (Exception e){
            throw e;
        }
    }*/
    public void updateErrorCount(ImbErrorCount errorCount) throws SQLException, Exception {
        try(SqlSession session = ImDatabaseConnectionEx.getConnection(); ){
            ErrorCountMapper errorCountMapper = session.getMapper(ErrorCountMapper.class);

            errorCountMapper.updateErrorCount(errorCount);
        } catch (PersistenceException | SQLException se){
            throw se;
        } catch (Exception e){
            throw e;
        }
    }

    /**
     * 수신자 추출 과정에서 발생하는 기본 오류들
     */
    public void updateBasicErrorCount(int nEmailAddr, int nReject, int nRepeat, int nDomain, int nBlank, String msgid) throws SQLException, Exception {
        try(SqlSession session = ImDatabaseConnectionEx.getConnection(); ){
            ErrorCountMapper errorCountMapper = session.getMapper(ErrorCountMapper.class);

            errorCountMapper.updateBasicErrorCount(nEmailAddr, nReject, nRepeat, nDomain, nBlank, msgid);
        } catch (PersistenceException | SQLException se){
            throw se;
        } catch (Exception e){
            throw e;
        }
    }
}

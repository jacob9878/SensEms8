package com.imoxion.sensems.server.repository;

import com.imoxion.common.database.ImDatabaseConnectionEx;
import com.imoxion.sensems.server.repository.mapper.ReceiptCountMapper;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class ReceiptCountRepository {
    private Logger logger = LoggerFactory.getLogger("EMSD");

    private static final ReceiptCountRepository receiptCountRepository = new ReceiptCountRepository();

    public static ReceiptCountRepository getInstance(){
        return receiptCountRepository;
    }

    private ReceiptCountRepository() {}

    ////////////////////////////////

    public void delete(String msgid) throws SQLException, Exception {
        try(SqlSession session = ImDatabaseConnectionEx.getConnection(); ){
            ReceiptCountMapper receiptCountMapper = session.getMapper(ReceiptCountMapper.class);

            receiptCountMapper.deleteReceiptCount(msgid);
        } catch (PersistenceException | SQLException se){
            throw se;
        } catch (Exception e){
            throw e;
        }
    }

    public void insertReceiptCount(String msgid) throws SQLException, Exception {
        try(SqlSession session = ImDatabaseConnectionEx.getConnection(); ){
            ReceiptCountMapper receiptCountMapper = session.getMapper(ReceiptCountMapper.class);

            receiptCountMapper.insertReceiptCount(msgid);
        } catch (PersistenceException | SQLException se){
            throw se;
        } catch (Exception e){
            throw e;
        }
    }

}

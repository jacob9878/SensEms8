package com.imoxion.sensems.server.repository;

import com.imoxion.common.database.ImDatabaseConnectionEx;
import com.imoxion.sensems.server.domain.ImbUploadFile;
import com.imoxion.sensems.server.repository.mapper.UploadFileMapper;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

public class UploadFileRepository {
    private Logger logger = LoggerFactory.getLogger("DAEMON");

    private static final UploadFileRepository uploadFileRepository = new UploadFileRepository();

    public static UploadFileRepository getInstance(){
        return uploadFileRepository;
    }

    private UploadFileRepository() {}

    ////////////////////////////////

    public List<ImbUploadFile> getUploadFileListToDelete() throws PersistenceException, SQLException, Exception {
        List<ImbUploadFile> listUploadFile = null;
        try( SqlSession session = ImDatabaseConnectionEx.getConnection(); ){
            UploadFileMapper mapper = session.getMapper(UploadFileMapper.class);
            listUploadFile = mapper.getUploadFileListToDelete();
        } catch (PersistenceException | SQLException se){
            throw se;
        } catch (Exception e){
            throw e;
        }

        return listUploadFile;
    }

    public void deleteUploadFileLazy() throws PersistenceException, SQLException, Exception {
        try( SqlSession session = ImDatabaseConnectionEx.getConnection(); ){
            UploadFileMapper mapper = session.getMapper(UploadFileMapper.class);
            mapper.deleteUploadFileLazy();
        } catch (PersistenceException | SQLException se){
            throw se;
        } catch (Exception e){
            throw e;
        }
    }


}

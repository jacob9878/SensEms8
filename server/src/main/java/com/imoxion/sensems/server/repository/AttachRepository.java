package com.imoxion.sensems.server.repository;

import com.imoxion.common.database.ImDatabaseConnectionEx;
import com.imoxion.sensems.server.domain.ImbEmsAttach;
import com.imoxion.sensems.server.repository.mapper.AttachMapper;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

public class AttachRepository {
    private Logger logger = LoggerFactory.getLogger("DAEMON");

    private static final AttachRepository attachRepository = new AttachRepository();

    public static AttachRepository getInstance(){
        return attachRepository;
    }

    private AttachRepository() {}

    ////////////////////////////////

    public List<ImbEmsAttach> getAttachList(String msgid) throws PersistenceException, SQLException, Exception {
        List<ImbEmsAttach> listAttach = null;
        try( SqlSession session = ImDatabaseConnectionEx.getConnection(); ){
            AttachMapper mapper = session.getMapper(AttachMapper.class);
            listAttach = mapper.getAttachList(msgid);
        } catch (PersistenceException | SQLException se){
            throw se;
        } catch (Exception e){
            throw e;
        }

        return listAttach;
    }

    public ImbEmsAttach getAttachInfo(String ekey, String msgid) throws PersistenceException, SQLException, Exception {
        ImbEmsAttach emsAttach = null;
        try( SqlSession session = ImDatabaseConnectionEx.getConnection(); ){
            AttachMapper mapper = session.getMapper(AttachMapper.class);
            emsAttach = mapper.getAttachInfo(ekey, msgid);
        } catch (PersistenceException | SQLException se){
            throw se;
        } catch (Exception e){
            throw e;
        }

        return emsAttach;
    }

    public void insertAttach(ImbEmsAttach emsAttach) throws PersistenceException, SQLException, Exception {

        try( SqlSession session = ImDatabaseConnectionEx.getConnection(); ){
            AttachMapper mapper = session.getMapper(AttachMapper.class);
            mapper.insertAttachInfo(emsAttach);
        } catch (PersistenceException | SQLException se){
            throw se;
        } catch (Exception e){
            throw e;
        }
    }

    public List<ImbEmsAttach> getAttachListExpired(int deleteDelayDays) throws PersistenceException, SQLException, Exception {
        List<ImbEmsAttach> listAttach = null;
        try( SqlSession session = ImDatabaseConnectionEx.getConnection(); ){
            AttachMapper mapper = session.getMapper(AttachMapper.class);
            listAttach = mapper.getAttachListExpired(deleteDelayDays);
        } catch (PersistenceException | SQLException se){
            throw se;
        } catch (Exception e){
            throw e;
        }

        return listAttach;
    }

    public void deleteAttach(List<ImbEmsAttach> listAttach) throws PersistenceException, SQLException, Exception {
        SqlSession session  = null;
        try {
            session = ImDatabaseConnectionEx.getConnection(false);
            AttachMapper mapper = session.getMapper(AttachMapper.class);

            for (ImbEmsAttach attach : listAttach) {
                mapper.deleteAttach(attach.getEkey());
            }
            session.commit();
        }catch(Exception e){
            if(session != null) session.rollback();
            throw e;
        }finally{
            if(session != null) session.close();
        }
    }

    public void deleteAttachByMsgid(String msgid) throws PersistenceException, SQLException, Exception {

        try( SqlSession session = ImDatabaseConnectionEx.getConnection(); ){
            AttachMapper mapper = session.getMapper(AttachMapper.class);
            mapper.deleteAttachByMsgid(msgid);
        } catch (PersistenceException | SQLException se){
            throw se;
        } catch (Exception e){
            throw e;
        }
    }

}

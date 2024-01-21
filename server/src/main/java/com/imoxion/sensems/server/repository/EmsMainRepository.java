package com.imoxion.sensems.server.repository;

import com.imoxion.common.database.ImDatabaseConnectionEx;
import com.imoxion.sensems.server.domain.ImbEmsContents;
import com.imoxion.sensems.server.domain.ImbEmsMain;
import com.imoxion.sensems.server.repository.mapper.EmsMainMapper;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class EmsMainRepository {
    private Logger logger = LoggerFactory.getLogger("EMSD");

    private static final EmsMainRepository emsMainRepository = new EmsMainRepository();

    public static EmsMainRepository getInstance(){
        return emsMainRepository;
    }

    private EmsMainRepository() {}

    ////////////////////////////////

    public void deleteEmsMain(String msgid) throws Exception {
        try( SqlSession session = ImDatabaseConnectionEx.getConnection(); ){
            EmsMainMapper mapper = session.getMapper(EmsMainMapper.class);

            mapper.deleteEmsMain(msgid);
        } catch (PersistenceException | SQLException se){
            throw se;
        } catch (Exception e){
            throw e;
        }
    }
    public void deleteMsgInfo(String msgid) throws Exception {
        try( SqlSession session = ImDatabaseConnectionEx.getConnection(); ){
            EmsMainMapper mapper = session.getMapper(EmsMainMapper.class);

            mapper.deleteMsgInfo(msgid);
        } catch (PersistenceException | SQLException se){
            throw se;
        } catch (Exception e){
            throw e;
        }
    }

    /**
     * 발송 대상 목록 추출
     */
    public List<ImbEmsMain> getListToSend() throws Exception {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
        String sCurrDate = df.format(new Date());

        try( SqlSession session = ImDatabaseConnectionEx.getConnection(); ){
            EmsMainMapper mapper = session.getMapper(EmsMainMapper.class);
            return  mapper.getListToSend(sCurrDate);
        } catch (PersistenceException | SQLException se){
            throw se;
        } catch (Exception e){
            throw e;
        }
    }

    public List<ImbEmsMain> getListToDelete(String deleteDate) throws Exception {

        try( SqlSession session = ImDatabaseConnectionEx.getConnection(); ){
            EmsMainMapper mapper = session.getMapper(EmsMainMapper.class);
            return  mapper.getListToDelete(deleteDate);
        } catch (PersistenceException | SQLException se){
            throw se;
        } catch (Exception e){
            throw e;
        }
    }

    public List<ImbEmsMain> getListSending() throws Exception {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
        String sCurrDate = df.format(new Date());

        try( SqlSession session = ImDatabaseConnectionEx.getConnection(); ){
            EmsMainMapper mapper = session.getMapper(EmsMainMapper.class);
            return  mapper.getListSending(sCurrDate);
        } catch (PersistenceException | SQLException se){
            throw se;
        } catch (Exception e){
            throw e;
        }
    }

    /**
     * ems 서비스가 기동될때 발송중인 메일을 중지시키기 위함
     * @return
     * @throws Exception
     */
    public List<ImbEmsMain> getListSendingToStop() throws Exception {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
        String sCurrDate = df.format(new Date());

        try( SqlSession session = ImDatabaseConnectionEx.getConnection(); ){
            EmsMainMapper mapper = session.getMapper(EmsMainMapper.class);
            return  mapper.getListSendingToStop(sCurrDate);
        } catch (PersistenceException | SQLException se){
            throw se;
        } catch (Exception e){
            throw e;
        }
    }

    /**
     * 최근 30일 이내 발신 메일 중 아직 종료가 아닌 메일
     * @return
     * @throws SQLException
     * @throws Exception
     */
    public List<ImbEmsMain> getListRecentSendingMail() throws Exception {
        try( SqlSession session = ImDatabaseConnectionEx.getConnection(); ){
            EmsMainMapper mapper = session.getMapper(EmsMainMapper.class);
            return mapper.getListRecentSendingMail();
        } catch (PersistenceException | SQLException se){
            throw se;
        } catch (Exception e){
            throw e;
        }
    }


    /**
     * 특정 msgid 의 메일 정보 추출
     */
    public ImbEmsMain getEmsInfo(String msgid) throws Exception {
        try (SqlSession session = ImDatabaseConnectionEx.getConnection();) {
            EmsMainMapper mapper = session.getMapper(EmsMainMapper.class);
            return mapper.getEmsInfo(msgid);
        } catch (PersistenceException | SQLException se){
            throw se;
        } catch (Exception e){
            throw e;
        }
    }

    /**
     * 특정 msgid 의 메일 본문 추출
     */
    public ImbEmsContents getContents(String msgid) throws Exception {
        try (SqlSession session = ImDatabaseConnectionEx.getConnection();) {
            EmsMainMapper mapper = session.getMapper(EmsMainMapper.class);
            return mapper.getContents(msgid);
        } catch (PersistenceException | SQLException se){
            throw se;
        } catch (Exception e){
            throw e;
        }
    }

    /**
     * 자동 에러 재발신 항목 추출(발송종료 후 최대 3일 이내인 것만)
     */
    public List<ImbEmsMain> getListToResend(int interval) throws Exception {
        try( SqlSession session = ImDatabaseConnectionEx.getConnection(); ){
            EmsMainMapper mapper = session.getMapper(EmsMainMapper.class);
            return  mapper.getListToResend(interval);
        } catch (PersistenceException | SQLException se){
            throw se;
        } catch (Exception e){
            throw e;
        }
    }

    /**
     * 중지된 메일 목록 추출
     */
    public List<ImbEmsMain> getListToStop() throws Exception {
        try( SqlSession session = ImDatabaseConnectionEx.getConnection(); ){
            EmsMainMapper mapper = session.getMapper(EmsMainMapper.class);
            return  mapper.getListToStop();
        } catch (PersistenceException | SQLException se){
            throw se;
        } catch (Exception e){
            throw e;
        }
    }

    /**
     * 메일발송 중지 처리(isstop 값을 2로 변경)
     */
    public void stopSendEms(String msgid, String state) throws Exception {
        try( SqlSession session = ImDatabaseConnectionEx.getConnection(); ){
            EmsMainMapper mapper = session.getMapper(EmsMainMapper.class);

            SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
            String sCurrentTime = df.format(new Date());

            mapper.updateToStop(msgid, state, sCurrentTime);
        } catch (PersistenceException | SQLException se){
            throw se;
        } catch (Exception e){
            throw e;
        }
    }

    public void updateStateStartTime(String msgid, String state, String currentTime) throws Exception {
        try( SqlSession session = ImDatabaseConnectionEx.getConnection(); ){
            EmsMainMapper mapper = session.getMapper(EmsMainMapper.class);

            mapper.updateStateStartTime(msgid, state, currentTime);
        } catch (PersistenceException | SQLException se){
            throw se;
        } catch (Exception e){
            throw e;
        }
    }

    public void updateState(String msgid, String state) throws Exception {
        try( SqlSession session = ImDatabaseConnectionEx.getConnection(); ){
            EmsMainMapper mapper = session.getMapper(EmsMainMapper.class);

            mapper.updateState(msgid, state);
        } catch (PersistenceException | SQLException se){
            throw se;
        } catch (Exception e){
            throw e;
        }
    }

    public void updateStateSendStartTime(String msgid, String state, long currentTime) throws Exception {
        try( SqlSession session = ImDatabaseConnectionEx.getConnection(); ){
            EmsMainMapper mapper = session.getMapper(EmsMainMapper.class);

            mapper.updateStateSendStartTime(msgid, state, currentTime);
        } catch (PersistenceException | SQLException se){
            throw se;
        } catch (Exception e){
            throw e;
        }
    }

    public void updateStateAndCount(ImbEmsMain emsMain) throws Exception {
        try( SqlSession session = ImDatabaseConnectionEx.getConnection(); ){
            EmsMainMapper mapper = session.getMapper(EmsMainMapper.class);

            mapper.updateStateAndCount(emsMain);
        } catch (PersistenceException | SQLException se){
            throw se;
        } catch (Exception e){
            throw e;
        }
    }

    public void updateCurSend(int curSend, String msgid) throws Exception {
        try( SqlSession session = ImDatabaseConnectionEx.getConnection(); ){
            EmsMainMapper mapper = session.getMapper(EmsMainMapper.class);

            mapper.updateCurSend(curSend, msgid);
        } catch (PersistenceException | SQLException se){
            throw se;
        } catch (Exception e){
            throw e;
        }
    }

    public  void updateToLoggingState(String msgid) throws Exception {
        try( SqlSession session = ImDatabaseConnectionEx.getConnection(); ){
            EmsMainMapper mapper = session.getMapper(EmsMainMapper.class);

            mapper.updateToLoggingState(msgid);
        } catch (PersistenceException | SQLException se){
            throw se;
        } catch (Exception e){
            throw e;
        }
    }

    public void updateMainEndEx(String msgid, String currState, String currDate) throws Exception {
        try( SqlSession session = ImDatabaseConnectionEx.getConnection(); ){
            EmsMainMapper mapper = session.getMapper(EmsMainMapper.class);

            mapper.updateMainEndEx(msgid, currState, currDate);
        } catch (PersistenceException | SQLException se){
            throw se;
        } catch (Exception e){
            throw e;
        }
    }

    public void updateMailResend(String msgid) throws Exception {
        try( SqlSession session = ImDatabaseConnectionEx.getConnection(); ){
            EmsMainMapper mapper = session.getMapper(EmsMainMapper.class);

            mapper.updateMailResend(msgid);
        } catch (PersistenceException | SQLException se){
            throw se;
        } catch (Exception e){
            throw e;
        }
    }

    public void updateMailResendNum(String msgid, int resend_num) throws Exception {
        try( SqlSession session = ImDatabaseConnectionEx.getConnection(); ){
            EmsMainMapper mapper = session.getMapper(EmsMainMapper.class);

            mapper.updateMailResendNum(msgid, resend_num);
        } catch (PersistenceException | SQLException se){
            throw se;
        } catch (Exception e){
            throw e;
        }
    }

    public void insertMsgInfo(String msgid, String content) throws Exception {
        try( SqlSession session = ImDatabaseConnectionEx.getConnection(); ){
            EmsMainMapper mapper = session.getMapper(EmsMainMapper.class);

            mapper.insertMsgInfo(msgid, content);
        } catch (PersistenceException | SQLException se){
            throw se;
        } catch (Exception e){
            throw e;
        }
    }

    public void insertMailData(ImbEmsMain emsMain) throws Exception {
        try( SqlSession session = ImDatabaseConnectionEx.getConnection(); ){
            EmsMainMapper mapper = session.getMapper(EmsMainMapper.class);

            mapper.insertMailData(emsMain);
        } catch (PersistenceException | SQLException se){
            throw se;
        } catch (Exception e){
            throw e;
        }
    }
}

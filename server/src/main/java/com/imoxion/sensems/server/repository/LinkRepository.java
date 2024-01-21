package com.imoxion.sensems.server.repository;

import com.imoxion.common.database.ImDatabaseConnectionEx;
import com.imoxion.sensems.server.domain.ImbLinkInfo;
import com.imoxion.sensems.server.repository.mapper.LinkMapper;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

public class LinkRepository {
    private Logger logger = LoggerFactory.getLogger("EMSD");

    private static final LinkRepository linkRepository = new LinkRepository();

    public static LinkRepository getInstance(){
        return linkRepository;
    }

    private LinkRepository() {}

    ////////////////////////////////

    public void createLinkLogTable(String msgid) throws SQLException, Exception {
        try(SqlSession session = ImDatabaseConnectionEx.getConnection(); ){
            LinkMapper linkMapper = session.getMapper(LinkMapper.class);

            linkMapper.createLinkLogTable(msgid);
        } catch (PersistenceException | SQLException se){
            throw se;
        } catch (Exception e){
            throw e;
        }
    }

    public void insertLinkInfo(ImbLinkInfo linkInfo) throws SQLException, Exception {
        try(SqlSession session = ImDatabaseConnectionEx.getConnection(); ){
            LinkMapper linkMapper = session.getMapper(LinkMapper.class);

            linkMapper.insertLinkInfo(linkInfo);
        } catch (PersistenceException | SQLException se){
            throw se;
        } catch (Exception e){
            throw e;
        }
    }
    public void insertLinkCountInfo(String msgid, int linkid, int count) throws SQLException, Exception {
        try(SqlSession session = ImDatabaseConnectionEx.getConnection(); ){
            LinkMapper linkMapper = session.getMapper(LinkMapper.class);

            linkMapper.insertLinkCountInfo(msgid, linkid, count);
        } catch (PersistenceException | SQLException se){
            throw se;
        } catch (Exception e){
            throw e;
        }
    }


    public void deleteLinkCount(String msgid) throws SQLException, Exception {
        try(SqlSession session = ImDatabaseConnectionEx.getConnection(); ){
            LinkMapper linkMapper = session.getMapper(LinkMapper.class);

            linkMapper.deleteLinkCount(msgid);
        } catch (PersistenceException | SQLException se){
            throw se;
        } catch (Exception e){
            throw e;
        }
    }
    public void deleteLinkInfo(String msgid) throws SQLException, Exception {
        try(SqlSession session = ImDatabaseConnectionEx.getConnection(); ){
            LinkMapper linkMapper = session.getMapper(LinkMapper.class);

            linkMapper.deleteLinkInfo(msgid);
        } catch (PersistenceException | SQLException se){
            throw se;
        } catch (Exception e){
            throw e;
        }
    }

    public void dropLinkLogTable(String msgid) throws SQLException, Exception {
        try(SqlSession session = ImDatabaseConnectionEx.getConnection(); ){
            LinkMapper linkMapper = session.getMapper(LinkMapper.class);

            String tableName = "hc_" + msgid;
            linkMapper.deleteLinkInfo(tableName);
        } catch (PersistenceException | SQLException se){
            throw se;
        } catch (Exception e){
            throw e;
        }
    }


    public List<ImbLinkInfo> getLinkList(String msgid) throws PersistenceException, SQLException, Exception {
        try( SqlSession session = ImDatabaseConnectionEx.getConnection(); ){
            LinkMapper mapper = session.getMapper(LinkMapper.class);
            return mapper.getLinkList(msgid);
        } catch (PersistenceException | SQLException se){
            throw se;
        } catch (Exception e){
            throw e;
        }
    }
}
package com.imoxion.sensems.server.repository;

import com.imoxion.common.database.ImDatabaseConnectionEx;
import com.imoxion.sensems.server.domain.ImbFilterDomain;
import com.imoxion.sensems.server.repository.mapper.FilterDomainMapper;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

public class FilterDomainRepository {
    private Logger logger = LoggerFactory.getLogger("EMSD");

    private static final FilterDomainRepository filterDomainRepository = new FilterDomainRepository();

    public static FilterDomainRepository getInstance(){
        return filterDomainRepository;
    }

    private FilterDomainRepository() {}

    ////////////////////////////////


    public List<String> getFilterList() throws PersistenceException, SQLException, Exception {
        try(SqlSession session = ImDatabaseConnectionEx.getConnection(); ){
            FilterDomainMapper mapper = session.getMapper(FilterDomainMapper.class);
            return mapper.getFilterList();
        } catch (PersistenceException | SQLException se){
            throw se;
        } catch (Exception e){
            throw e;
        }
    }
}

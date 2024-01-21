package com.imoxion.sensems.server.repository;

import com.imoxion.common.database.ImDatabaseConnectionEx;
import com.imoxion.sensems.server.domain.ImbAddrSel;
import com.imoxion.sensems.server.repository.mapper.AddressMapper;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

public class AddressRepository {
    private Logger logger = LoggerFactory.getLogger("EMSD");

    private static final AddressRepository addressRepository = new AddressRepository();

    public static AddressRepository getInstance(){
        return addressRepository;
    }

    private AddressRepository() {}

    ////////////////////////////////

    /**
     * 주소록을 이용한 수신그룹 쿼리 추출
     */
    public String getRecvQueryAddr(String userid, String msgid) throws PersistenceException, SQLException, Exception {
        String recvQuery = "";
        try( SqlSession session = ImDatabaseConnectionEx.getConnection(); ){
            AddressMapper mapper = session.getMapper(AddressMapper.class);
            List<ImbAddrSel> listAddrSel = mapper.getAddrSelInfo(userid, msgid);

            String tableAddr = "imb_addr_" + userid;

            recvQuery = "select distinct email,name,company,dept,grade,office_tel,mobile,etc1,etc2 from " + tableAddr;
            for(int i=0; i<listAddrSel.size(); i++){
                String gkey = listAddrSel.get(i).getGkey();
                if(i == 0) {
                    recvQuery += " where gkey = '" + gkey+ "'";
                } else {
                    recvQuery += " or gkey = '" + gkey + "'";
                }
            }
            recvQuery += " order by email";
        } catch (PersistenceException | SQLException se){
            throw se;
        } catch (Exception e){
            throw e;
        }

        return recvQuery;
    }


    /*public ImbDBInfo getDBInfo(String ukey) throws SQLException, Exception {
        try (SqlSession session = ImDatabaseConnectionEx.getConnection();) {
            DatabaseMapper mapper = session.getMapper(DatabaseMapper.class);
            return mapper.getDBInfo(ukey);
        } catch (SQLException se){
            throw se;
        } catch (Exception e){
            throw e;
        }
    }*/



}

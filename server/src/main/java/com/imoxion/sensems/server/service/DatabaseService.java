package com.imoxion.sensems.server.service;

import com.imoxion.common.database.ImDatabaseConnectionEx;
import com.imoxion.security.ImSecurityLib;
import com.imoxion.sensems.server.config.ImEmsConfig;
import com.imoxion.sensems.server.define.ImJdbcDriver;
import com.imoxion.sensems.server.domain.ImbDBInfo;
import com.imoxion.sensems.server.repository.DatabaseRepository;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseService {
    private Logger log = LoggerFactory.getLogger( DatabaseService.class );

    private static final DatabaseService databaseService = new DatabaseService();
    public static DatabaseService getInstance() {
        return databaseService;
    }
    private DatabaseService() {}
    ////////////////////////////////////

    /**
     * 특정 ukey 데이터베이스 정보 가져오기
     * (복호화해서 가져옴)
     */
    public ImbDBInfo getDBInfoByUkey(String ukey) throws Exception{
        DatabaseRepository databaseRepository = DatabaseRepository.getInstance();
        ImbDBInfo dbInfo = databaseRepository.getDBInfo(ukey);
//        log.info("ukey: {}, dbInfo: {}", ukey, dbInfo !=null ? dbInfo.toString(): null);
        return decryptDBInfo(dbInfo);
    }

    public ImbDBInfo decryptDBInfo(ImbDBInfo dbInfo) throws Exception {
        ImEmsConfig emsConfig = ImEmsConfig.getInstance();
        String secret_key = emsConfig.getAesKey();

//        log.info("secret_key: {}", secret_key);

        try {
           // String deUserid = ImSecurityLib.decryptAES256(secret_key, dbInfo.getUserid());
            String deDbhost = ImSecurityLib.decryptAES256(secret_key, dbInfo.getDbhost());
            String deDbuser = ImSecurityLib.decryptAES256(secret_key, dbInfo.getDbuser());
            String deDbpasswd = ImSecurityLib.decryptAES256(secret_key, dbInfo.getDbpasswd());
            String deAddress = ImSecurityLib.decryptAES256(secret_key, dbInfo.getAddress());
//            log.info("deDbhost: {}", deDbhost);
//            log.info("deDbuser: {}", deDbuser);
//            log.info("deDbpasswd: {}", deDbpasswd);
//            log.info("deAddress: {}", deAddress);

           // dbInfo.setUserid(deUserid);
            dbInfo.setDbhost(deDbhost);
            dbInfo.setDbuser(deDbuser);
            dbInfo.setDbpasswd(deDbpasswd);
            dbInfo.setAddress(deAddress);
        }catch(Exception e){
            //e.printStackTrace();
        }

//        log.info("decryptDBInfo: {}", dbInfo.toString());
        return dbInfo;
    }


    /**
     * 수신자 추출용 DB에 연결
     */
    public Connection getRecvDBConn(ImbDBInfo dbInfo) throws Exception {
        String jdbc_url = dbInfo.getAddress();
        String jdbcDriver = ImJdbcDriver.getDriver(dbInfo.getDbtype());
        String db_uid = dbInfo.getUserid();
        String db_pwd = dbInfo.getDbpasswd();

        // DriverManager.getConnection(url,userid,password);
        Class.forName(jdbcDriver);
        Connection conn = DriverManager.getConnection(jdbc_url, db_uid, db_pwd);

        return conn;
    }

    /**
     * 수신자 추출용 DB에 연결(주소록, 재발신)
     */
    public Connection getLocalDBConn() throws Exception {
        SqlSession session = ImDatabaseConnectionEx.getConnection();
        Connection conn = session.getConnection();

        return conn;
    }




}

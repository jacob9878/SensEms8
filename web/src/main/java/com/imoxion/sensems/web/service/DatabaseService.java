package com.imoxion.sensems.web.service;

import com.imoxion.common.util.ImUtils;
import com.imoxion.common.util.NumberUtils;
import com.imoxion.security.ImSecurityLib;
import com.imoxion.sensems.common.logger.ErrorTraceLogger;
import com.imoxion.sensems.web.common.ImbConstant;
import com.imoxion.sensems.web.database.domain.ImbDBInfo;
import com.imoxion.sensems.web.database.mapper.DatabaseMapper;
import com.imoxion.sensems.web.form.DatabaseForm;
import javassist.NotFoundException;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.jdbc.Null;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import java.io.FileNotFoundException;
import java.net.ConnectException;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * yeji
 * 2021. 03. 03
 * 데이터베이스관리 관련 Service 클래스
 */
@Service
public class DatabaseService {

    @Autowired
    private DatabaseMapper databaseMapper;

    @Autowired
    private MessageSourceAccessor message;

    protected Logger log = LoggerFactory.getLogger( DatabaseService.class );

    public static final String DRIVER_CLASS__MYSQL = "com.mysql.jdbc.Driver";

    public static final String DRIVER_CLASS__ORACLE = "oracle.jdbc.driver.OracleDriver";

    public static final String DRIVER_CLASS__MSSQL = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

    public static final String DRIVER_CLASS_TIBERO = "com.tmax.tibero.jdbc.TbDriver";

    /**
     * @comment : 페이징 처리를 위한 total 개수
     * @return
     * @throws Exception
     */
    public int getListCount() throws Exception{
       return databaseMapper.getListCount();
    }

    /**
     * @comment : 초기화면 목록을 불러올 list 조회
     * @param start
     * @param end
     * @return
     * @throws Exception
     */
    public List<ImbDBInfo> getDBInfoListForPaging(int start, int end) throws Exception{
        return databaseMapper.getDBInfoListForPaging(start, end);
    }

    /**
     * 데이터베이스 리스트 가져오기
     * @return
     * @throws Exception
     */
    public List<ImbDBInfo> getDBInfoList() throws Exception{
        return databaseMapper.getDBInfoList();
    }

    /**
     * @comment : 데이터베이스 이름 중복 확인
     * @param userid
     * @param dbname
     * @return
     * @throws Exception
     */
    public boolean checkDBExist(String userid, String dbname) throws Exception{
        boolean isExist = false;
        if(ImbConstant.DATABASE_ENCRYPTION_USE) {
            userid = ImSecurityLib.encryptAES256(ImbConstant.DATABASE_AES_KEY, userid);
        }
        int count = databaseMapper.getCheckDBExist(userid, dbname);
        if(count > 0){
            isExist = true;
        }
        return isExist;
    }

    /**
     * @comment : ImbDBInfo 에 form 넣고 insertDBInfo 호출
     * @Method Name : databaseAdd
     * @Method Comment : 데이터베이스 추가 로직 수행
     * @param userid : 사용자아이디
     * @param form : 데이터베이스 추가 페이지에서 넘어온 폼데이터
     */
    public void databaseAdd(String userid, DatabaseForm form) throws Exception{
        ImbDBInfo dbInfoBean = new ImbDBInfo();

        //1.고유키 생성
        dbInfoBean.setUkey(ImUtils.makeKeyNum(24));

        if (ImbConstant.DATABASE_ENCRYPTION_USE) {
            /* 암호화 대상 : dbhost , dbuser , dbpasswd , address */
            String secret_key = ImbConstant.DATABASE_AES_KEY;

            //폼데이터 암호화 실시
            encryptDBInfo(form);
            // 2023-02-21 userid의 암호화는 불필요하여 주석처리
            //userid = ImSecurityLib.encryptAES256(secret_key, userid);

        }


        //2.userid, dbname, dbtype
        dbInfoBean.setUserid(userid);
        dbInfoBean.setDbname(form.getDbname());
        dbInfoBean.setDbtype(form.getDbtype());

        //3.dbhost, dbport: db 유형에 따른 서버주소 및 포트값 세팅
        /**
         * 오라클인 경우 : [oracle_svc] , [oracle_port]
         * 이외 유형인 경우 : [dbhost] , [dbport]
         */
        if(StringUtils.equalsIgnoreCase("oracle", form.getDbtype())){
            dbInfoBean.setDbhost(form.getOracle_svc());
            dbInfoBean.setDbport(form.getOracle_port());
        }else{
            dbInfoBean.setDbhost(form.getDbhost());
            dbInfoBean.setDbport(form.getDbport());
        }

        //4.dbuser , dbpasswd , dbcharset , datacharset
        dbInfoBean.setDbuser(form.getDbuser());
        dbInfoBean.setDbpasswd(form.getDbpasswd());
        dbInfoBean.setDbcharset(form.getDbcharset());
        dbInfoBean.setDatacharset(form.getDatacharset());

        //5.address(jdbcurl)
        //String jdbcUrl = getJDBCUrl(form);
        dbInfoBean.setAddress(form.getAddress());

        //6.regdate
        dbInfoBean.setRegdate(new Date());

        // insert
        databaseMapper.insertDBInfo(dbInfoBean);
    }

    /**
     * 데이터베이스 연결 테스트
     * @param form
     * @return : boolean
     */
    public boolean connectTest(DatabaseForm form){
        String driverClass = getJDBCDriverClass(form.getDbtype());
        String jdbcurl = this.getJDBCUrl(form);
        String username = form.getDbuser();
        String passwd = form.getDbpasswd();
        boolean result = true;

        Connection conn = null;
        try{
            Class.forName( driverClass );
            conn = DriverManager.getConnection(jdbcurl, username, passwd);
            log.info("db connect test success");
        }catch (DataAccessException ae) {
            String errorId = ErrorTraceLogger.log(ae);
            log.error("{} - db connect test fail error", errorId);
            return false;
        }
        catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - db connect test fail error", errorId);
            result = false;
        }finally {
            if(conn != null){ try { conn.close(); }catch (NullPointerException ne) {} catch (Exception e){} }
            return result;
        }
    }

    /**
     * DB 드라이버클래스 생성
     * @param dbtype
     * @return
     */
    public String getJDBCDriverClass(String dbtype){
        if(dbtype.equalsIgnoreCase(ImbDBInfo.DB_TYPE__MYSQL)){ // dbtype = mysql 이면
            return DatabaseService.DRIVER_CLASS__MYSQL;
        }else if(dbtype.equalsIgnoreCase(ImbDBInfo.DB_TYPE__ORACLE)){
            return DatabaseService.DRIVER_CLASS__ORACLE;
        }else if(dbtype.equalsIgnoreCase(ImbDBInfo.DB_TYPE__MSSQL)){
            return DatabaseService.DRIVER_CLASS__MSSQL;
        }else if(dbtype.equalsIgnoreCase(ImbDBInfo.DB_TYPE__TIBERO)){
            return DatabaseService.DRIVER_CLASS_TIBERO;
        }
        return null;
    }

    /**
     * @method Name : getJDBCUrl
     * @comment 데이터베이스 jdbc url 생성
     * @param form : 데이터베이스 폼
     * @return : String
     */
    public String getJDBCUrl(DatabaseForm form){
        String real_dbname = form.getReal_dbname();
        String dbtype = form.getDbtype();
        String dbhost = form.getDbhost();
        String dbport = form.getDbport();
        String oracle_svc = form.getOracle_svc();
        String oracle_port = form.getOracle_port();
        String oracle_sid = form.getOracle_sid();
        String dbcharset = form.getDbcharset();

        if( dbtype.equalsIgnoreCase(ImbDBInfo.DB_TYPE__MYSQL)){
            return "jdbc:mysql://"+ dbhost +":"+ dbport +"/"+ real_dbname +"?unicode=true&characterEncoding=" + dbcharset;
        }else if( dbtype.equalsIgnoreCase(ImbDBInfo.DB_TYPE__ORACLE)){
            return "jdbc:oracle:thin:@"+ oracle_svc +":"+ oracle_port +":"+ oracle_sid;
        }else if( dbtype.equalsIgnoreCase(ImbDBInfo.DB_TYPE__MSSQL)){
            return "jdbc:sqlserver://"+ dbhost +":"+ dbport +";DatabaseName="+ real_dbname;
        }else if( dbtype.equalsIgnoreCase(ImbDBInfo.DB_TYPE__TIBERO)){
            return "jdbc:tibero:thin:@"+ dbhost +":"+ dbport +":"+ real_dbname;
        }else{
            return "jdbc:"+ dbtype +"://"+ dbhost +":"+ dbport +"/"+ real_dbname;
        }
    }

    /**
     * 특정 ukey 데이터베이스 정보 가져오기
     * @param ukey
     * @return
     * @throws Exception
     */
    public ImbDBInfo getDBInfoByUkey(String ukey) throws Exception{
        ImbDBInfo dbInfo = databaseMapper.getDBInfoByUkey(ukey);
        if(ImbConstant.DATABASE_ENCRYPTION_USE){
            decryptDBInfo(dbInfo);
        }
        return dbInfo;
    }

    /**
     * model 에 담아줄 form 세팅
     * ImbDBInfo -> DatabaseForm 객체 매핑
     * @return
     */
    public DatabaseForm dbInfoToForm(ImbDBInfo dbInfo, String ukey) throws Exception{ // 복호화 된 form 객체가 넘어옴.
        DatabaseForm form = new DatabaseForm();

        String dbname = dbInfo.getDbname();
        String dbtype = dbInfo.getDbtype();
        form.setDbtype(dbtype);

        String dbport = dbInfo.getDbport();
        String dehost = dbInfo.getDbhost();
        //String real_dbname = dbInfo.
        String dbcharset = dbInfo.getDbcharset();
        String datacharset = dbInfo.getDatacharset();
        String dedbuser = dbInfo.getDbuser();
        String dedbpasswd = dbInfo.getDbpasswd();
        String address = dbInfo.getAddress();


        if(StringUtils.equalsIgnoreCase("oracle", dbtype)){
            String oracle_sid = getAddressInfo(form, address);  // address 에서 oracle_sid 추출
            form.setOracle_svc(dehost); // 오라클 host(svc)
            form.setOracle_port(dbport); //오라클 포트
            form.setOracle_sid(oracle_sid); // 오라클 sid
        }else {
            String real_dbname = getAddressInfo(form, address);  // address 에서 real_dbname 추출
            form.setDbhost(dehost);
            form.setDbport(dbport);
            form.setReal_dbname(real_dbname);
        }
        form.setUkey(ukey);
        form.setDbname(dbname);
        form.setDbuser(dedbuser);
        form.setDbpasswd(dedbpasswd);
        form.setDbcharset(dbcharset);
        form.setDatacharset(datacharset);
        return form;
    }

    /**
     * @comment : jdbcurl 에서 SID or real_dbname(DB명) 추출
     * @param form
     * @param jdbcurl
     * @return
     */
    public String getAddressInfo(DatabaseForm form, String jdbcurl){
        String dbtype = form.getDbtype();
        String returnValue = "";
        String[] tempUrl = null;

        if( dbtype.equalsIgnoreCase(ImbDBInfo.DB_TYPE__MYSQL)){
            tempUrl = jdbcurl.split(form.getDbport() + "/");
            int nPos = tempUrl[1].indexOf("?");
            returnValue = tempUrl[1].substring(0, nPos);         // mysql real_dbname 추출
            log.info("mysql real_dbname - {}" , returnValue);
        }else if( dbtype.equalsIgnoreCase(ImbDBInfo.DB_TYPE__ORACLE)){
            tempUrl = jdbcurl.split("@");
            if( tempUrl != null ){
                int nPos = tempUrl[1].lastIndexOf(":");
                returnValue = tempUrl[1].substring(nPos+1);      // oracle_sid 추출
                log.info("oracle_sid - {}" , returnValue);
            }
        }else if( dbtype.equalsIgnoreCase(ImbDBInfo.DB_TYPE__MSSQL)){
            tempUrl = jdbcurl.split(";DatabaseName=");
            if( tempUrl != null ){
                returnValue = tempUrl[1];      // mssql real_dbname 추출
                log.info("mssql real_dbname - {}" , returnValue);
            }
        }else if( dbtype.equalsIgnoreCase(ImbDBInfo.DB_TYPE__TIBERO)){
            tempUrl = jdbcurl.split("@");
            if( tempUrl != null ){
                int nPos = tempUrl[1].lastIndexOf(":");
                returnValue = tempUrl[1].substring(nPos+1);     // tibero real_dbname 추출
                log.info("tibero real_dbname - {}" , returnValue);
            }
        }
        return returnValue;
    }

    /**
     * @comment : 데이터베이스 폼을 ImbDBInfo 에 넣고 updateDBInfo 호출
     * @Method Name : databaseEdit
     * @Method Comment : 데이터베이스 수정 로직 수행
     * @param userid : 사용자아이디
     * @param form : 데이터베이스 수정 페이지에서 넘어온 폼데이터
     */
    public void databaseEdit(String userid, DatabaseForm form) throws Exception{
        ImbDBInfo dbInfoBean = new ImbDBInfo();

        //1.ukey
        dbInfoBean.setUkey(form.getUkey());

        if(ImbConstant.DATABASE_ENCRYPTION_USE){
            /* 암호화 대상 :  dbhost , dbuser , dbpasswd , address */
            String secret_key = ImbConstant.DATABASE_AES_KEY;

            // 폼데이터 암호화 실시
            encryptDBInfo(form);
            // 2023-02-21 userid의 암호화는 불필요하여 주석처리
            //userid = ImSecurityLib.encryptAES256(secret_key, userid);
        }

        //2.userid, dbname, dbtype
        dbInfoBean.setUserid(userid);
        dbInfoBean.setDbname(form.getDbname());
        dbInfoBean.setDbtype(form.getDbtype());

        //3.dbhost, dbport: db 유형에 따른 서버주소 및 포트값 세팅
        /**
         * 오라클인 경우 : [oracle_svc] , [oracle_port]
         * 이외 유형인 경우 : [dbhost] , [dbport]
         */
        if(StringUtils.equalsIgnoreCase("oracle", form.getDbtype())){
            dbInfoBean.setDbhost(form.getOracle_svc());
            dbInfoBean.setDbport(form.getOracle_port());
        }else{
            dbInfoBean.setDbhost(form.getDbhost());
            dbInfoBean.setDbport(form.getDbport());
        }

        //4.dbuser , dbpasswd , dbcharset , datacharset
        dbInfoBean.setDbuser(form.getDbuser());
        dbInfoBean.setDbpasswd(form.getDbpasswd());
        dbInfoBean.setDbcharset(form.getDbcharset());
        dbInfoBean.setDatacharset(form.getDatacharset());

        //5.address(jdbcurl)
        //log.debug("Database jdbc url Is {}", jdbcUrl);
        dbInfoBean.setAddress(form.getAddress());

        // update
        databaseMapper.updateDBInfo(dbInfoBean);
    }

    /**
     * 데이터베이스 삭제 처리
     * @param ukey
     * @throws Exception
     */
    public void deleteDBInfo(String ukey) throws Exception{
        databaseMapper.deleteDBInfo(ukey);
    }

    /**
     * DBForm 암호화 실시
     * @param form
     * @return
     * @throws Exception
     */
    public DatabaseForm encryptDBInfo(DatabaseForm form) throws Exception{
        //암호화 대상 - dbhost, dbuser, dbpasswd, address
        //폼에 없는게 userid, address
        String secret_key = ImbConstant.DATABASE_AES_KEY;
        String address = getJDBCUrl(form);
        String enAddress = ImSecurityLib.encryptAES256(secret_key, address);
        form.setAddress(enAddress);

        String enDbuser = ImSecurityLib.encryptAES256(secret_key, form.getDbuser());
        String enDbpasswd = ImSecurityLib.encryptAES256(secret_key, form.getDbpasswd());

        String enDbhost = "";
        String enOracle_svc = "";
        if(StringUtils.equalsIgnoreCase("oracle", form.getDbtype())){
            enOracle_svc = ImSecurityLib.encryptAES256(secret_key, form.getOracle_svc());
            form.setOracle_svc(enOracle_svc);
        }else{
            enDbhost = ImSecurityLib.encryptAES256(secret_key, form.getDbhost());
            form.setDbhost(enDbhost);
        }

        form.setDbuser(enDbuser);
        form.setDbpasswd(enDbpasswd);

        return form;
    }

    /**
     * DBInfo 복호화 실시
     * @param dbInfo
     * @return
     * @throws Exception
     */
    public void decryptDBInfo(ImbDBInfo dbInfo) throws Exception {
        String secret_key = ImbConstant.DATABASE_AES_KEY;

        //String deUserid = ImSecurityLib.decryptAES256(secret_key,dbInfo.getUserid());
        String deDbhost = ImSecurityLib.decryptAES256(secret_key,dbInfo.getDbhost());
        String deDbuser = ImSecurityLib.decryptAES256(secret_key,dbInfo.getDbuser());
        String deDbpasswd = ImSecurityLib.decryptAES256(secret_key,dbInfo.getDbpasswd());
        String deAddress = ImSecurityLib.decryptAES256(secret_key,dbInfo.getAddress());

        //dbInfo.setUserid(deUserid);
        dbInfo.setDbhost(deDbhost);
        dbInfo.setDbuser(deDbuser);
        dbInfo.setDbpasswd(deDbpasswd);
        dbInfo.setAddress(deAddress);

    }


    /**
     * 폼데이터 유효성 체크 - db connection 시도할 때
     * @param form
     * @param jsonResult
     * @return
     */
    public JSONObject validateForConnect(DatabaseForm form, JSONObject jsonResult){
        if (StringUtils.isEmpty(form.getDbname())) {
            jsonResult.put("result", false);
            jsonResult.put("message", message.getMessage("E0149","데이터베이스 이름을 입력해주세요."));
            return jsonResult;
        }
        if(StringUtils.isEmpty(form.getDbtype())){
            jsonResult.put("result", false);
            jsonResult.put("message", message.getMessage("E0191","DB 유형 값을 확인해주세요."));
            return jsonResult;
        }else if(!(StringUtils.equalsIgnoreCase("mysql", form.getDbtype()) || StringUtils.equalsIgnoreCase("oracle", form.getDbtype())
                || StringUtils.equalsIgnoreCase("mssql", form.getDbtype()) || StringUtils.equalsIgnoreCase("tibero", form.getDbtype()))){
            jsonResult.put("result", false);
            jsonResult.put("message", message.getMessage("E0151","지원하지 않는 DB 유형입니다."));
            return jsonResult;
        }
        // DB 접속 ip 체크
        String regexIPv4 = "^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
        Pattern pattern = Pattern.compile(regexIPv4);
        Matcher matcher;
        if(StringUtils.equalsIgnoreCase("oracle", form.getDbtype())){
            // 오라클인 경우
            // 오라클 svc, port, sid null 체크
            if(StringUtils.isEmpty(form.getOracle_svc())){
                jsonResult.put("result", false);
                jsonResult.put("message", message.getMessage("E0153","접속 호스트를 입력해주세요."));
                return jsonResult;
            }else if(!StringUtils.equalsIgnoreCase("localhost", form.getOracle_svc())){
                // oracle_svc 형식 체크
                matcher = pattern.matcher(form.getOracle_svc());
                boolean hostCheck = matcher.matches();
                if(!hostCheck){
                    jsonResult.put("result", false);
                    jsonResult.put("message", message.getMessage("E0152","잘못된 호스트 형식입니다."));
                    return jsonResult;
                }
            }else if(StringUtils.isEmpty(form.getOracle_port())){
                jsonResult.put("result", false);
                jsonResult.put("message", message.getMessage("E0154","포트번호를 입력해주세요."));
                return jsonResult;
            }else if(StringUtils.isEmpty(form.getOracle_sid())){
                jsonResult.put("result", false);
                jsonResult.put("message", message.getMessage("E0155","오라클 SID 를 입력해주세요."));
                return jsonResult;
            }
            // 포트번호 숫자만 입력 가능하도록 체크
            if (!NumberUtils.isNumber(form.getOracle_port().replaceAll("-", ""))) {
                jsonResult.put("result", false);
                jsonResult.put("message", message.getMessage("E0156","올바른 숫자를 입력해 주세요."));
                return jsonResult;
            }
        } else{
            // 이외 DB 유형인 경우
            // dbport , dbhost 체크
            if (StringUtils.isEmpty(form.getDbhost())) {
                jsonResult.put("result", false);
                jsonResult.put("message", message.getMessage("E0153","접속 호스트를 입력해주세요."));
                return jsonResult;
            }else if(!StringUtils.equalsIgnoreCase("localhost", form.getDbhost())){
                // oracle_svc 형식 체크
                matcher = pattern.matcher(form.getDbhost());
                boolean hostCheck = matcher.matches();
                if(!hostCheck){
                    jsonResult.put("result", false);
                    jsonResult.put("message", message.getMessage("E0152","잘못된 호스트 형식입니다."));
                    return jsonResult;
                }
            }
            // 포트번호
            if (StringUtils.isEmpty(form.getDbport())) {
                jsonResult.put("result", false);
                jsonResult.put("message", message.getMessage("E0154","포트번호를 입력해주세요."));
                return jsonResult;
            }else {
                if(!NumberUtils.isNumber(form.getDbport().replaceAll("-", ""))){
                    jsonResult.put("result", false);
                    jsonResult.put("message", message.getMessage("E0156","올바른 숫자를 입력해 주세요."));
                    return jsonResult;
                }
            }
            // DB명
            if (StringUtils.isEmpty(form.getReal_dbname())) {
                jsonResult.put("result", false);
                jsonResult.put("message", message.getMessage("E0197","DB명을 입력해주세요."));
                return jsonResult;
            }
        }
        if (StringUtils.isEmpty(form.getDbuser())) {
            jsonResult.put("result", false);
            jsonResult.put("message", message.getMessage("E0157","접속 아이디를 입력해주세요."));
            return jsonResult;
        }
        if (StringUtils.isEmpty(form.getDbpasswd())) {
            jsonResult.put("result", false);
            jsonResult.put("message", message.getMessage("E0158","접속 비밀번호를 입력해주세요."));
            return jsonResult;
        }
        if (StringUtils.isEmpty(form.getDbcharset())) {
            jsonResult.put("result", false);
            jsonResult.put("message", message.getMessage("E0159","DB의 캐릭터셋을 지정해 주세요."));
            return jsonResult;
        }
        return null;
    }


    /**
     * SQL 실행 테스트
     * @param dbInfo
     * @param query
     * @return
     */
    public boolean sqlValidationCheck(ImbDBInfo dbInfo, String query) {
        boolean queryOK = false;
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            String dbtype = dbInfo.getDbtype();
            String address = dbInfo.getAddress();
            String dbUser = dbInfo.getDbuser();
            String dbPasswd = dbInfo.getDbpasswd();

            con = getDBConnection(dbtype,address,dbUser,dbPasswd);
            pstmt = con.prepareStatement(query);
            pstmt.executeQuery();

            queryOK = true;

        } catch (DataAccessException ae) {
            String errorId = ErrorTraceLogger.log(ae);
            log.error("{} - CAN NOT EXCUTE QUERY or CONNECT DATABASE - DataAccess error - query : {}", errorId, query);
        } catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("{} - CAN NOT EXCUTE QUERY or CONNECT DATABASE - query : {}", errorId, query);
        } finally {
            if(pstmt != null) try { pstmt.close(); } catch (SQLException e) {} catch (Exception e){}
            if(con != null) {  try {  con.close(); } catch (SQLException e) {} catch (Exception e){}

            }
            return queryOK;
        }
    }

    /**
     * 데이터베이스 접속
     * @param dbtype
     * @param address
     * @param dbUser
     * @param dbPasswd
     * @return
     * @throws Exception
     */
    public Connection getDBConnection(String dbtype, String address, String dbUser, String dbPasswd ) throws Exception{

        String driverClass = getJDBCDriverClass(dbtype);
        Class.forName(driverClass);
        Connection con = DriverManager.getConnection(address,dbUser,dbPasswd);

        return con;
    }

    /**
     * 쿼리 실행하여 model객체에 담는다.
     * @param con
     * @param query
     * @param model
     * @throws Exception
     */
    public void excuteQuery(Connection con, String query, ModelMap model) throws Exception {

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = con.prepareStatement(query);
            rs = pstmt.executeQuery();

            //컬럼 데이터 추출
            ResultSetMetaData rsmd = rs.getMetaData();
            int fieldCount = rsmd.getColumnCount();
            List<String> fieldInfoList = new ArrayList<String>();

            for(int i=1;i<=fieldCount;i++){
                String fieldName = rsmd.getColumnName(i);
                fieldInfoList.add(fieldName);
            }

            //실제 데이터 추출
            List resultList = new ArrayList();
            int totalCount = 0;
            while (rs.next()){
                totalCount++;
                if(resultList.size()>=100){
                    continue;
                }
                List<String> resultRow = new ArrayList<>();
                for(int i=1;i<=fieldCount;i++){
                    String result = rs.getString(i);
                    resultRow.add(result);
                }
                resultList.add(resultRow);
            }
            model.addAttribute("fieldInfoList",fieldInfoList);
            model.addAttribute("totalCount",totalCount);
            model.addAttribute("resultList",resultList);
        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("fieldInfoList ne Error - {}", errorId );
        }
        catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("fieldInfoList ne Error - {}", errorId );
        }finally {
            if(rs != null){ try { rs.close(); }catch (NullPointerException ne)  {}catch (Exception e){} }
            if(pstmt != null) try { pstmt.close(); }catch (NullPointerException ne) {} catch (Exception e) {}
        }

    }

    public Map<Integer, String> testSendExcuteQuery(Connection con, String query) throws Exception {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Map resultMap = null;

        try {
            pstmt = con.prepareStatement(query);
            rs = pstmt.executeQuery();

            //컬럼 데이터 추출
            ResultSetMetaData rsmd = rs.getMetaData();
            int fieldCount = rsmd.getColumnCount();
            List<String> fieldInfoList = new ArrayList<String>();

            for(int i=1;i<=fieldCount;i++){
                String fieldName = rsmd.getColumnName(i);
                fieldInfoList.add(fieldName);
            }

            //실제 데이터 추출
            List resultList = new ArrayList();
            while (rs.next()){
                if(resultList.size()>=100){
                    continue;
                }
                resultMap = new HashMap();
                for(int i=0;i<=fieldCount-1;i++){
                    String fieldName = fieldInfoList.get(i);
                    String result = rs.getString(fieldName);
                    resultMap.put(i+1,result); // 필드 1 부터 랭스까지 대체할 값이 들어감
                }
            }
        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("testSendExcuteQuery ne Error - {}", errorId );
        }
        catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("testSendExcuteQuery Error - {}", errorId );

        }finally {
           if(pstmt != null) pstmt.close();
           if(rs != null) rs.close();
        }

        return resultMap;
    }

    /**
     * 쿼리 실행하여 컬럼 개수를 체크한다.
     * @param con
     * @param query
     * @return
     * @throws Exception
     */
    public int checkCoulumn (Connection con, String query) throws Exception{

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ResultSetMetaData rsmd = null;
        int fieldCount = 0;

        try {
            pstmt = con.prepareStatement(query);
            rs = pstmt.executeQuery();
            //컬럼 카운트 확인
            if(rs != null) {
                rsmd = rs.getMetaData();
                fieldCount = rsmd.getColumnCount();
            }
        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            log.error("Column count ne error : {}",errorId);
        }
        catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            log.error("Column count error : {}",errorId);
        }finally {
            if(pstmt != null){ try { pstmt.close(); }catch (NullPointerException ne) {}catch (Exception e){} }
            if(rs != null){ try { rs.close(); }catch (NullPointerException ne) {} catch (Exception e){} }
        }
        return fieldCount;
    }
}

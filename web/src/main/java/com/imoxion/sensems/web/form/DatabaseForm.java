package com.imoxion.sensems.web.form;

import com.imoxion.common.util.NumberUtils;
import com.imoxion.sensems.web.util.ImUtility;
import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * yeji
 * 2021. 03. 03
 * 데이터베이스관리 관련 Form 객체
 */
public class DatabaseForm {

    private String cpage = "1";

    private String pagegroupsize = "5";

    private String ukey;

    private String dbname; // 데이터베이스 이름 (리스트에서 보이는 제목)

    private String real_dbname; // jdbcurl 생성을 위한 DB명  ex) jdbc:sqlserver://dbhost:dbport;DatabaseName=[real_dbname]

    private String dbtype = "mysql"; // 데이터베이스 유형 (default : 1 : mysql)

    private String dbhost; // DB 호스트

    private String dbport = "3306"; // 포트 (default :3306)

    private String dbuser; // 접속 아이디

    private String dbpasswd; // 접속 비밀번호

    private String dbcharset; // DB CHARSET

    private String datacharset; // DATA CHARSET

    private String oracle_svc; // 오라클 ip

    private String oracle_port = "1521"; // 오라클 포트 (default : 1521)

    private String oracle_sid; // 오라클 SID

    private boolean existDB = false;

    private String address; // jdbcurl

    private String encAESKey = "";

    private String ori_name;

    private String ori_dbtype;

    public String getEncAESKey() {
        return encAESKey;
    }

    public void setEncAESKey(String encAESKey) {
        this.encAESKey = encAESKey;
    }

    public String getReal_dbname() {return real_dbname;}

    public void setReal_dbname(String real_dbname) {
        this.real_dbname = real_dbname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isExistDB() {
        return existDB;
    }

    public void setExistDB(boolean existDB) {
        this.existDB = existDB;
    }

    public String getCpage() {
        return cpage;
    }

    public void setCpage(String cpage) {
        this.cpage = cpage;
    }

    public String getPagegroupsize() {
        return pagegroupsize;
    }

    public void setPagegroupsize(String pagegroupsize) {
        this.pagegroupsize = pagegroupsize;
    }

    public String getUkey() {
        return ukey;
    }

    public void setUkey(String ukey) {
        this.ukey = ukey;
    }

    public String getDbname() {
        return dbname;
    }

    public void setDbname(String dbname) {
        this.dbname = dbname;
    }

    public String getDbtype() {
        return dbtype;
    }

    public void setDbtype(String dbtype) {
        this.dbtype = dbtype;
    }

    public String getDbhost() {
        return dbhost;
    }

    public void setDbhost(String dbhost) {
        this.dbhost = dbhost;
    }

    public String getDbport() {
        return dbport;
    }

    public void setDbport(String dbport) {
        this.dbport = dbport;
    }

    public String getDbuser() {
        return dbuser;
    }

    public void setDbuser(String dbuser) {
        this.dbuser = dbuser;
    }

    public String getDbpasswd() {
        return dbpasswd;
    }

    public void setDbpasswd(String dbpasswd) {
        this.dbpasswd = dbpasswd;
    }

    public String getDbcharset() {
        return dbcharset;
    }

    public void setDbcharset(String dbcharset) {
        this.dbcharset = dbcharset;
    }

    public String getDatacharset() {
        return datacharset;
    }

    public void setDatacharset(String datacharset) {
        this.datacharset = datacharset;
    }

    public String getOracle_svc() {
        return oracle_svc;
    }

    public void setOracle_svc(String oracle_svc) {
        this.oracle_svc = oracle_svc;
    }

    public String getOracle_port() {
        return oracle_port;
    }

    public void setOracle_port(String oracle_port) {
        this.oracle_port = oracle_port;
    }

    public String getOracle_sid() {
        return oracle_sid;
    }

    public void setOracle_sid(String oracle_sid) {
        this.oracle_sid = oracle_sid;
    }

    public String getOri_name() {return ori_name;}

    public void setOri_name(String ori_name) {this.ori_name = ori_name;}

    public String getOri_dbtype() {return ori_dbtype;}

    public void setOri_dbtype(String ori_dbtype) {this.ori_dbtype = ori_dbtype;}

    @Override
    public String toString() {
        return "DatabaseForm{" +
                "cpage:" + cpage +
                ", pagegroupsize:" + pagegroupsize +
                ", ukey:" + ukey +
                ", dbname:" + dbname +
                ", dbtype:" + dbtype +
                ", dbhost:" + dbhost +
                ", dbport:" + dbport +
                ", real_dbname:" + real_dbname +
                ", dbuser:" + dbuser +
                ", dbpasswd:" + dbpasswd +
                ", dbcharset:" + dbcharset +
                ", datacharset:" + datacharset +
                ", oracle_svc:" + oracle_svc +
                ", oracle_port:" + oracle_port +
                ", oracle_sid:" + oracle_sid +
                ", existDB:" + existDB +
                ", address:" + address +
                ", encAESKey: " + encAESKey +
                "}";
    }

    // target : 검증할 객체, errors : 검증객체가 올바르지 않을 경우의 에러정보 저장
    public void databaseValidator(Object target, Errors errors){
        // 객체 검사 전 타입변환
        DatabaseForm form = (DatabaseForm) target;

        // ValidationUtils 클래스 메소드로 검증 결과를 errors 에 저장
        // 데이터베이스 이름 체크
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "dbname", "E0149", "데이터베이스 이름을 입력해주세요.");
        // 데이터베이스 이름 특수문자 체크
        if(!ImUtility.validCharacter(form.getDbname())){
            errors.rejectValue("dbname","E0737","사용할 수 없는 특수문자가 포함되어 있습니다. (사용가능한 특수 문자 : (_) , (,) , (-) )");
        }

        // 데이터베이스 이름 중복체크
        if (StringUtils.isNotEmpty(form.getDbname()) && form.isExistDB()) { // isExistDB default is false
            errors.rejectValue("dbname", "E0150", "이미 존재하는 데이터베이스 이름입니다.");
        }

        // DB 유형 체크 (지원하지 않는 유형인 경우 체크)
        if(StringUtils.isNotEmpty(form.getDbtype()) && !(StringUtils.equalsIgnoreCase("mysql", form.getDbtype()) || StringUtils.equalsIgnoreCase("oracle", form.getDbtype())
                || StringUtils.equalsIgnoreCase("mssql", form.getDbtype()) || StringUtils.equalsIgnoreCase("tibero", form.getDbtype()))){
            errors.rejectValue("dbtype", "E0151", "지원하지 않는 DB 유형입니다.");
        }

        // DB 접속 ip(dbhost) 체크
        String regexIPv4 = "^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
        Pattern pattern = Pattern.compile(regexIPv4);
        Matcher matcher;
        if(StringUtils.equalsIgnoreCase("oracle", form.getDbtype())){  // 오라클인 경우
            // 오라클 svc(host), port, sid 체크
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "oracle_svc", "E0153", "접속 호스트를 입력해주세요.");
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "oracle_port", "E0154", "포트번호를 입력해주세요.");
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "oracle_sid", "E0155", "오라클 SID를 입력해주세요.");
            if(StringUtils.isNotEmpty(form.getOracle_svc()) || !StringUtils.equalsIgnoreCase("localhost", form.getOracle_svc())){ // host 가 localhost 인 경우
                matcher = pattern.matcher(form.getOracle_svc());
                boolean hostCheck = matcher.matches();
                if(!hostCheck){
                    errors.rejectValue("oracle_svc", "E0152", "잘못된 호스트 형식입니다.");
                }
            }
            if (StringUtils.isNotEmpty(form.getOracle_port()) && !NumberUtils.isNumber(form.getOracle_port().replaceAll("-", ""))) {
                errors.rejectValue("oracle_port", "E0156", "올바른 숫자를 입력해 주세요.");
            }
        } else{ // 이외 DB 유형인 경우
            // dbport , dbhost 체크
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "dbhost", "E0153", "접속 호스트를 입력해주세요.");
            if(StringUtils.isNotEmpty(form.getDbhost()) || !StringUtils.equalsIgnoreCase("localhost", form.getDbhost())){
                matcher = pattern.matcher(form.getDbhost());
                boolean hostCheck = matcher.matches();
                if(!hostCheck){
                    errors.rejectValue("dbhost", "E0152", "잘못된 호스트 형식입니다.");
                }
            }
            // 포트번호
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "dbport", "E0154", "포트번호를 입력해주세요.");
            if (StringUtils.isNotEmpty(form.getDbport()) && !NumberUtils.isNumber(form.getDbport().replaceAll("-", ""))) {
                errors.rejectValue("dbport", "E0156", "올바른 숫자를 입력해 주세요.");
            }
            // DB명
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "real_dbname", "E0197", "DB명을 입력해주세요.");
        }

        // 접속 아이디
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "dbuser", "E0157", "접속 아이디를 입력해주세요.");

        // 접속 비밀번호
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "dbpasswd", "E0158", "접속 비밀번호를 입력해주세요.");

        // DB CHARSET
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "dbcharset", "E0159", "DB의 캐릭터셋을 지정해 주세요.");
    }

}

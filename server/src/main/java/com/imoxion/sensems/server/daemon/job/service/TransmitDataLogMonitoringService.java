package com.imoxion.sensems.server.daemon.job.service;

import ch.qos.logback.classic.Level;
import com.imoxion.common.database.ImDatabaseConnectionEx;
import com.imoxion.common.util.ImStringUtil;
import com.imoxion.common.util.ImTimeUtil;
import com.imoxion.common.util.StopWatch;
import com.imoxion.sensems.server.domain.TransmitStatisticsData;
import com.imoxion.sensems.server.environment.SensEmsEnvironment;
import com.imoxion.sensems.server.logger.ErrorTraceLogger;
import com.imoxion.sensems.server.logger.event.TransmitLogger;
import com.imoxion.sensems.server.repository.mapper.TransmitStaticsticMapper;
import com.imoxion.sensems.server.util.MailAddress;
import com.imoxion.sensems.server.util.MailAddressUtil;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JSON 형식의 로그 파일을 읽어서 모니터링 자료를 생산한다.
 */
public class TransmitDataLogMonitoringService {

    private Logger logger = LoggerFactory.getLogger("DAEMON");

    private SimpleDateFormat logDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    private final String LAST_CHECK_DATE_PROP = "last_check_date";

    private final String LAST_CHECK_DATE_PATTERN = "yyyyMMddHHmmss";
    
    private final String LAST_LOG_FILE = "last_logfile_date";
    
    private PropertiesConfiguration transmitMonitoringProp;
    
    public static void main(String[] args) throws Exception{
    	SensEmsEnvironment.init();

//    	LoggerLoader.initLog("sensems-daemon-log.xml");
//        ImDatabaseConnectionEx.init("sensems.home", "mybatis-config.xml");

        TransmitDataLogMonitoringService service = new TransmitDataLogMonitoringService();
        service.dataAggregation();
    }

    public void dataAggregation() throws Exception{
        logger.info("transmit_dataAggregation start");
        Map<String,String> localDomainSet = new HashMap<>();

        Date now = new Date();
        
        // 최근 작업 로그 파일
        // 최근 수집한 로그 날짜가 현재 날짜보다 느린경우
        File mailActionProperties = new File(SensEmsEnvironment.getSensEmsServerHome() + "/conf/" + "transmitmonitoring.properties");
        if (!mailActionProperties.exists()) {
            if (mailActionProperties.createNewFile()) {
                logger.info("transmitmonitoring.properties file create");
            }
        }
        
        transmitMonitoringProp = new PropertiesConfiguration(mailActionProperties);
        transmitMonitoringProp.setAutoSave(true);
        
        Date lastCollectionDate;
        String last_logfile_date = transmitMonitoringProp.getString(LAST_LOG_FILE);
        if (StringUtils.isEmpty(last_logfile_date)) {
            Calendar n = Calendar.getInstance();
            n.add(Calendar.DATE, -1);
            lastCollectionDate = n.getTime();
        } else {
            lastCollectionDate = ImTimeUtil.getDateFromString(last_logfile_date, "yyyy-MM-dd");
        }

        // 최종 수집 날짜
        Calendar lastCollectionCal = Calendar.getInstance();
        lastCollectionCal.setTime(lastCollectionDate);
        
        
        String logFilePath;
        while (lastCollectionCal.getTime().compareTo(now) <= 0) {
        	last_logfile_date = ImTimeUtil.getDateFormat(lastCollectionCal.getTime(), "yyyy-MM-dd");
            logger.info("transmitmonitoring date:{}", last_logfile_date);
            
            logFilePath = SensEmsEnvironment.getSensEmsServerHome() + "/log/" + "transmit.log." + last_logfile_date;
            
            File logFile = new File(logFilePath);
            logger.info("targetLog add: {}",logFile.getName());
            logToDatabase(logFile,localDomainSet);
            // propeties 에 최근 작업한 파일 날짜를 저장한다.
            transmitMonitoringProp.setProperty(LAST_LOG_FILE, last_logfile_date);
            // 현재 날짜에다가 +1일 한다.
            lastCollectionCal.add(Calendar.DAY_OF_MONTH, 1);
        }
        logger.info("transmitdata log monitoring complete");
    }

    private void logToDatabase(File file, Map<String,String> localDomainSet){

        logger.info("transmitdata_logToDatabase - file:{}",file.getAbsolutePath());
        String sLastCheckDate = transmitMonitoringProp.getString(LAST_CHECK_DATE_PROP);
        BufferedReader reader = null;
        SqlSession sqlSession = null;
        Date lastCheckDate;
        logger.info("transmitdata_last_check_date:{}",sLastCheckDate);
        
        if(!file.exists()) {
        	logger.info("transmit log file does not exist: {}", file.getAbsolutePath());
        	return;
        }
        
        if(StringUtils.isNotEmpty( sLastCheckDate ) ){
            lastCheckDate = ImTimeUtil.getDateFromString(sLastCheckDate,LAST_CHECK_DATE_PATTERN);
        }else{
            Calendar n = Calendar.getInstance();
            n.set(Calendar.DAY_OF_MONTH,1);
            lastCheckDate =  n.getTime();
        }
        
        try{
        	StopWatch sw = new StopWatch();
            sw.start();
            reader = new BufferedReader(new FileReader(file));
            String row;
            TransmitLogger log;

            int maxErrorCount = 10;
            int errorCount = 0;
            
            Date eventDate = null;

            JsonConfig config = new JsonConfig();
            //org.apache.log4j.Logger.getLogger(JSONObject.class).setLevel(Level.INFO);
            Level level = Level.INFO;
            
            sqlSession = ImDatabaseConnectionEx.getBatchConnection("sensems");

            TransmitStaticsticMapper transmitStaticsticMapper = sqlSession.getMapper(TransmitStaticsticMapper.class);

            int updateCount = 0;
            while( (row = reader.readLine())!=null){
                if( errorCount > maxErrorCount ){
                    break;
                }
                try {
                    log = (TransmitLogger)JSONObject.toBean(JSONObject.fromObject(row,config),TransmitLogger.class);
                    if( log != null ) {
                        if (StringUtils.isEmpty(log.getLogdate())) {
                            Date logDate = ImTimeUtil.getDateFromString(FilenameUtils.getExtension(file.getName()), "yyyy-MM-dd");
                            log.setLogdate(ImTimeUtil.getDateFormat(logDate, "yyyyMMddHHmmss"));
                        }
                    }
                }catch(Exception e){
                    logger.error("transmitdata_log parse error({}) - log:{}",e.getMessage(), row);
                    errorCount++;
                    continue;
                }
                if( log == null){
                    continue;
                }
//                if( !TransmitLogger.WORK_DELIVERY.equalsIgnoreCase(log.getWork()) && !TransmitLogger.WORK_RECEIVE.equalsIgnoreCase(log.getWork()) ){
//                    continue;
//                }

//                TransmitStatisticsData[] transmitStatisticsData;
                try {
                	
                	SimpleDateFormat simple = new SimpleDateFormat("yyyyMMddHHmmss");
                	SimpleDateFormat simple2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                	Date formatLogDate = simple.parse(log.getLogdate());
                    String strLogDate = simple2.format(formatLogDate);
//logger.info("strLogDate: " + strLogDate + " / lastCheckDate: " + sLastCheckDate);
                	eventDate = ImTimeUtil.getDateFromString(strLogDate,"yyyy-MM-dd HH:mm:ss");
                    if( lastCheckDate != null ) {
//                    	logger.info("+++++++++++++ compare result: " + lastCheckDate.compareTo(eventDate));
                        if (lastCheckDate.compareTo(eventDate) > 0){
//                        	logger.info("_________________________ date is Bigger");
                        	continue;
                        }
                    }
                    
                    TransmitStatisticsData transmitStatisticData = parseTransmitLog(log, localDomainSet);
                    
                    if( transmitStatisticData != null ){
//                    	logger.debug("transmitStatisticData: {}",transmitStatisticData);
                    	transmitStaticsticMapper.insertTransmitLogData(transmitStatisticData);
                    	updateCount++;
                    }
                    
                    if( eventDate != null ) {
                        String sCheckDate = ImTimeUtil.getDateFormat(eventDate, LAST_CHECK_DATE_PATTERN);
                        transmitMonitoringProp.setProperty(LAST_CHECK_DATE_PROP, sCheckDate);
//                        logger.debug("transmitdata_checkDate:{}",eventDate);
                    }
                }catch(Exception e){
                    String errorId = ErrorTraceLogger.log(e);
                    logger.error("{} -parse transmitdata log error - log:{}", errorId, log.toString());
                    errorCount++;
                    continue;
                }
                if( updateCount % 500 == 0){
                    sqlSession.commit();
                }
            }
            sqlSession.commit();
            sw.stop();
            logger.info(sw.toString());
        }catch (Exception e){
            String errorId = ErrorTraceLogger.log(e);
            logger.error("{} - transmitdata_logToDatabase error",errorId);
        }finally {
            try{ if( sqlSession != null ) sqlSession.close(); }catch (Exception e){}
            try{ if( reader != null ) reader.close(); }catch (Exception e){}
        }
    }

    /**
     * TransmitLog 에서 모니터링 자료로 데이터를 추출한다.
     * @param log
     * @param localDomainMap
     * @return
     */
    public TransmitStatisticsData parseTransmitLog(TransmitLogger log, Map<String,String> localDomainMap) throws Exception{

        char result = TransmitStatisticsData.RESULT_FAIL; // 메일 발송 성공 여부 판단.
        String fromDomain = null;
        String fromMailid = null;
        String rcptDomain = null;
        String rcptMailid = null;
        String fromMhost = null;
        String rcptMhost = null;
        String groupKey = null;
        String rcptKey = null;

        if( StringUtils.isNotEmpty(log.getFrom() )) {
            MailAddress fromAddressInfo = MailAddressUtil.getMailAddress(log.getFrom());
            fromDomain = StringUtils.lowerCase(fromAddressInfo.getDomain());
            fromMailid = StringUtils.lowerCase(fromAddressInfo.getUserid());
            // FROM 주소가 10.6 버전 이전에 (헤더주소)가 포함되어있어 파싱이 안되어서 이와같이 처리함.
            if( StringUtils.isEmpty(fromDomain) ){
                String fromStr = ImStringUtil.getStringBetween( log.getFrom() , "(",")");
                fromAddressInfo = MailAddressUtil.getMailAddress(fromStr);
                fromDomain = StringUtils.lowerCase(fromAddressInfo.getDomain());
                fromMailid = StringUtils.lowerCase(fromAddressInfo.getUserid());
            }
        }

        if( StringUtils.isNotEmpty(log.getTo())){
            MailAddress rcpttoAddressInfo = MailAddressUtil.getMailAddress( log.getTo() );
            rcptDomain = StringUtils.lowerCase(rcpttoAddressInfo.getDomain());
            rcptMailid = StringUtils.lowerCase(rcpttoAddressInfo.getUserid());
        }

        if( localDomainMap.containsKey(fromDomain) ){
            fromMhost = localDomainMap.get(fromDomain);
        }

        if( localDomainMap.containsKey(rcptDomain) ){
            rcptMhost = localDomainMap.get(rcptDomain);
        }

        Date logDate = null;
        try{
            logDate = logDateFormat.parse(log.getLogdate());
        }catch(Exception e){
            e.printStackTrace();
        }
   
        TransmitStatisticsData[] transmitStatisticsDatas;

        String mhost = null;
        String mailid = null;
        char transmit_fl; // 송/수신/내부간 구분

        if(log.getResultState() == TransmitLogger.STATE_ING){
            result = TransmitStatisticsData.RESULT_ING;
        } else if(log.getResultState() == TransmitLogger.STATE_SUCCESS){
            result = TransmitStatisticsData.RESULT_SUCCESS;
        } else {
            result = TransmitStatisticsData.RESULT_FAIL;
        }
        /*
			송수신 구분 로직
            1) 수신 및 발신자의 메일 도메인이 같은 경우 내부 메일(L)
            2) 수신자 도메인이 외부인경우 송신메일(S)
            3) 송신자 도메인이 외부인경우 수신메일(R)
        */
        if( log.isSuccess() ){
            result = TransmitStatisticsData.RESULT_SUCCESS;
        }else {
        	if ("delivery.success".equalsIgnoreCase(log.getDescription()) 
            		|| "delivery.remote.send.success".equalsIgnoreCase(log.getDescription())
            		|| "delivery.remote.gateway.send.success".equalsIgnoreCase(log.getDescription())
            		|| "delivery.remote.gateway.savefile.success".equalsIgnoreCase(log.getDescription())
            		|| "delivery.proxy.send.success".equalsIgnoreCase(log.getDescription())
            		) {
                result = TransmitStatisticsData.RESULT_SUCCESS;
            }
        }

        if( StringUtils.isNotEmpty(fromMhost) ) { // 송수신자 MHOST가 있는 경우 송신메일로 처리한다.

            transmit_fl = TransmitStatisticsData.TRANSMIT_SEND;
            mhost = fromMhost;
            mailid = fromMailid;

        }else if( StringUtils.isNotEmpty(rcptMhost) ) {

            transmit_fl = TransmitStatisticsData.TRANSMIT_RECV;
            mhost = rcptMhost;
            mailid = rcptMailid;

        }else{

            transmit_fl = TransmitStatisticsData.TRANSMIT_UNKNOWN;

        }

//        logger.info(">>>>>>>> log.getOrg_traceid(): " + log.getOrg_traceid());

        String errMsg = log.getErrmsg();
        if(StringUtils.isEmpty(errMsg)) errMsg = "";

//        logger.info(">>>>>>>> log.getErrmsg(): " + errMsg);
       // transmitStatisticsDatas = new TransmitStatisticsData[1];
        TransmitStatisticsData transmitStatisticsData = new TransmitStatisticsData();
        transmitStatisticsData.setTraceid( log.getTraceid() ); // 로그 ID
        transmitStatisticsData.setLogdate( logDate ); // 수신 시간
        transmitStatisticsData.setMhost( mhost );
        transmitStatisticsData.setAuthid( log.getAuthid() );
        transmitStatisticsData.setFrom_domain( fromDomain ); // 송신자 도메인
        transmitStatisticsData.setRcpt_domain( rcptDomain ); // 수신자 도메인
        transmitStatisticsData.setMailfrom( log.getFrom() ); // 송신자 이메일주소
        transmitStatisticsData.setRcptto( log.getTo() ); // 수신자 이메일주소
        transmitStatisticsData.setIp( log.getIp() ); // 송신자 IP
        //transmitStatisticsData.setSubject( log.getSubject() ); // 제목
        String subject = ImStringUtil.stringCutterByte( ImStringUtil.removeEmoticonCharacter( log.getSubject()), 200 ); // 제목(mysql입력불가 문자포함시 제거)
        transmitStatisticsData.setSubject( subject ); 
        transmitStatisticsData.setTransmit_fl( transmit_fl ); // 송/수신/내부간 구분
        transmitStatisticsData.setLocal_fl( TransmitStatisticsData.LOCAL_NO );
        transmitStatisticsData.setMailsize( log.getSize() ); // 메일 크기
        transmitStatisticsData.setResult( result ); // 메일 발송 성공 여부 판단.
        transmitStatisticsData.setDescription( log.getDescription() ); // 설명
        if( TransmitStatisticsData.RESULT_FAIL == result ){
            if(log.getErrcode() == null) {
                log.setErrcode(0);
            }
            //transmitStatisticsData.setErrcode(getErrorCode( log.getArgument() ) );
            transmitStatisticsData.setErrcode(log.getErrcode());
        }
        transmitStatisticsData.setErrmsg(errMsg);
        transmitStatisticsData.setServerid(log.getServerid());
        if(log.getArgument() != null) transmitStatisticsData.setArgs(log.getArgument().toString());
        transmitStatisticsData.setOrg_traceid(log.getOrg_traceid());
        transmitStatisticsData.setGroupkey(log.getGroupkey());
        transmitStatisticsData.setRcptkey(log.getRcptkey());
        //transmitStatisticsDatas[0] = transmitStatisticsData;
        transmitStatisticsData.setSend_type(log.getSend_type());

        return transmitStatisticsData;
    }

    private int getErrorCode(Object argument){
        if(argument == null) return 0;
        String arg = argument.toString();
        if(StringUtils.isEmpty(arg)) return 0;
    	else return ImStringUtil.parseInt(argument);
    }
}

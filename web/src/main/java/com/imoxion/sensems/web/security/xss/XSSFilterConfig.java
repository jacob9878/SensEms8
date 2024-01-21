package com.imoxion.sensems.web.security.xss;

import com.imoxion.common.util.StopWatch;
import com.imoxion.sensems.common.logger.ErrorTraceLogger;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.event.ConfigurationEvent;
import org.apache.commons.configuration.event.ConfigurationListener;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

/**
 * XSS 핕터 설정 Class
 * Created by sunggyu on 2015-04-28.
 */
public class XSSFilterConfig {

    private static Logger logger = LoggerFactory.getLogger(XSSFilterConfig.class);

    private static Map<String, Set<String>> exceptionUriMap = new HashMap<String, Set<String>>();
    /**
     * Parameter 예외처리 : 모든 URL에 대해 정의된 파라미터는 예외처리함.
     */
    private static Set<String> allExceptionParameter = new HashSet<String>();
    /**
     * URL 예외처리 : 해당 URL 은 예외처리함
     */
    private static Set<String> ignoreURI = new HashSet<>();

    private static XMLConfiguration configuration;

    private static XSSFilterConfig xssFilterConfig = null;

    public static void init(String config_file) {
        try {
            if (xssFilterConfig == null) {
                xssFilterConfig = new XSSFilterConfig(config_file);
            }
        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            logger.error("XSS FILTER CONFIG ne ERROR", errorId);
        }
        catch (Exception e) {
            String errorId = ErrorTraceLogger.log(e);
            logger.error("XSS FILTER CONFIG ERROR", errorId);
        }
    }

    private XSSFilterConfig(String config_file) throws Exception {
        if (StringUtils.isEmpty(config_file)) {
            logger.error("XSS FILTER CONFIG FILE SETTING PLEASES.");
        } else {
            logger.info("XSS FILTER CONFIG FILE INIT : {} ", config_file);

            File configFile = new File(config_file);
            if (!configFile.exists()) {
                logger.error("XSS FILTER CONFIG FILE NOT EXIST : {}", config_file);
                return;
            }
            configuration = new XMLConfiguration(configFile);
            FileChangedReloadingStrategy fileChangedReloadingStrategy = new FileChangedReloadingStrategy();
            configuration.setReloadingStrategy(fileChangedReloadingStrategy);
            configuration.addConfigurationListener(new ConfigurationListener() {
                @Override
                public void configurationChanged(ConfigurationEvent event) {
                    if (event.isBeforeUpdate()) {
                        try {
                            loadConfigurationFile();
                            logger.info("XSSFILTER CONFIG LOAD OK");
                        }catch (NullPointerException ne) {
                            logger.error("XSSFilter Config Reload ne Error");
                        }
                        catch (Exception e) {
                            logger.error("XSSFilter Config Reload Error");
                        }
                    }
                }
            });
            loadConfigurationFile();
        }
    }

    /**
     * 설정 파일에서 설정값을 읽어서 메모리에 담는다.
     *
     * @throws Exception
     */
    private void loadConfigurationFile() throws Exception {
        List<HierarchicalConfiguration> fields = configuration.configurationsAt("xss.exception.url");
        exceptionUriMap.clear();
        allExceptionParameter.clear();
        for (HierarchicalConfiguration urlElement : fields) {
            String uri = urlElement.getString("[@uri]");
            String[] params = urlElement.getStringArray("param");
            boolean ignore = urlElement.getBoolean("[@ignore]", false);
            if (ignore) {
                logger.info("XSS FILTER IGNORE URI - {}", uri);
                ignoreURI.add(uri);
                continue;
            }
            if (uri.equals("*")) {
                for (String param : params) {
                    allExceptionParameter.add(param);
                    logger.info("XSSFILTER ALL EXCEPTION REGIST - {}", param);
                }
            } else {
                Set<String> parameters = new HashSet<String>();
                for (String param : params) {
                    parameters.add(param);
                    logger.info("XSSFILTER URI REGIST - {}, Param:{}", uri, param);
                }
                exceptionUriMap.put(uri, parameters);
            }
        }
        logger.info("XSS FILTER URI REGIST SUCCESS");
    }

    public static boolean isIgnoreURL(String uri){
        if( ignoreURI == null ){
            return false;
        }
        // 예외 URL
        if (ignoreURI.contains(uri)) {
            logger.debug("XSS IGNORE URL : {}", uri);
            return true;
        }
        return false;
    }
    /**
     * XSS 차단 예외 파라미터를 체크한다.
     *
     * @param uri
     * @param parameter
     * @return
     */
    public static boolean isExceptionUri(String uri, String parameter) {

        StopWatch sw = new StopWatch();
        if (exceptionUriMap == null) {
            logger.error("XSSFilterConfig don't call init method");
            return false;
        }
        boolean result = false;
        if (allExceptionParameter.contains(parameter)) {
            logger.debug("XSS EXCEPTION Parameter : {}", parameter);
            result = true;
        } else if (exceptionUriMap.containsKey(uri)) {
            Set<String> parameters = exceptionUriMap.get(uri);
            if (parameters.contains(parameter)) {
                logger.debug("XSS EXCEPTION URI- {}:{}", uri, parameter);
                result = true;
            }
        }
        sw.stop();
        //logger.debug( sw.toString() );
        return result;
    }
}
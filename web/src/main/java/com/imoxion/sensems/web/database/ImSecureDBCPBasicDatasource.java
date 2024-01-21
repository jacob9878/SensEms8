package com.imoxion.sensems.web.database;

import com.imoxion.security.ImSecurityLib;
import com.imoxion.sensems.common.logger.ErrorTraceLogger;
import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import java.security.InvalidKeyException;

/**
 * 
 * @author Administrator
 * 
 * db.properties에 아이디/비밀번호가 암호화 되어 있을때
 * datasource.xml 의 <bean id="dataSource" class="org.apache.commons.dbcp.BasicDataSource"> 에서
 * class="com.imoxion.sensems.web.database.ImSecureDBCPBasicDatasource" 로 바꿔서 사용
 *
 */

public class ImSecureDBCPBasicDatasource extends BasicDataSource {
	public static Logger log = LoggerFactory.getLogger(ImSecureDBCPBasicDatasource.class);
	
	public void setUsername(String username){
		String decUsername = username;
		try {
			decUsername = ImSecurityLib.makePassword("AES", username, true);
		}catch (BadPaddingException i) {
			String errorId = ErrorTraceLogger.log(i);
			log.error("{} - setUsername BadPaddingException error", errorId);
		}catch(Exception e){
			String errorId = ErrorTraceLogger.log(e);
			log.error("{} - setUsername error", errorId);
		}
		super.setUsername(decUsername);
	}
	
	public void setPassword(String password) {
		String decPassword = password;
		try {
			decPassword = ImSecurityLib.makePassword("AES", password, true);
		}catch (BadPaddingException i) {
			String errorId = ErrorTraceLogger.log(i);
			log.error("{} - setPassword BadPaddingException error", errorId);
		}catch(Exception e){
			String errorId = ErrorTraceLogger.log(e);
			log.error("{} - setPassword error", errorId);
		}
		super.setPassword(decPassword);
	}
}

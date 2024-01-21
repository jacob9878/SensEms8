package com.imoxion.sensems.server.config;

import com.imoxion.security.ImSecurityLib;
import org.apache.commons.dbcp.BasicDataSource;

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
	
	public void setUsername(String username){
		String decUsername = username;
		try {
			decUsername = ImSecurityLib.makePassword("AES", username, true);
		}catch(Exception e){}
		super.setUsername(decUsername);
	}
	
	public void setPassword(String password) {
		String decPassword = password;
		try {
			decPassword = ImSecurityLib.makePassword("AES", password, true);
		}catch(Exception e){}
		super.setPassword(decPassword);
	}
}

package com.imoxion.sensems.common.logger.event;

import com.imoxion.sensems.common.logger.SensLoggerFactory;
import com.imoxion.sensems.common.logger.ProcessLogger;

import java.util.Date;

/**
 * 메일 송수신 로거
 */
public class ActionLogger implements ProcessLogger {

    private Date timestamp;

    private String userid;
    
    private String code;
    
    private String domain;

    private String content;

    private String usertype;

	public String getUsertype() {
		return usertype;
	}

	public void setUsertype(String usertype) {
		this.usertype = usertype;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
    
    @Override
	public String toString() {
		return "ActionLogger [timestamp=" + timestamp + ", userid=" + userid
				+ ", code=" + code + ", domain=" + domain + ", content="
				+ content + "]";
	}

	public void info(){
        SensLoggerFactory.getLogger("DBLOG_ACTION").info( this );
    }
}

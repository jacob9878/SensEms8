package com.imoxion.sensems.web.service;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.imoxion.common.logger.ErrorTraceLogger;
import org.xbill.DNS.NULLRecord;

@Service
public class VelocityService {
	private Logger log = LoggerFactory.getLogger(VelocityService.class);

	/** 템플릿관련 */
	@Autowired
	protected VelocityEngine velocityEngine;

	/**
	 * 
	 * @Method Name : getTemplate
	 * @Method Comment :
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public String getTemplate(Map<String, Object> map, String templateName) {
		try {
			StringWriter writer = new StringWriter();
			VelocityContext context = new VelocityContext(map);
			velocityEngine.mergeTemplate(templateName,"UTF-8",context,writer);
			return writer.toString();
//			return velocityEngine.mergeTemplateIntoString(velocityEngineFactory.createVelocityEngine(), templateName, "UTF-8", map);
		}catch (NullPointerException ne) {
			String errorId = ErrorTraceLogger.log(ne);
			log.error("{} VelocityEngineUtils ne error - templateName={}", errorId, templateName);
			return "";
		}
		catch (Exception e) {
			String errorId = ErrorTraceLogger.log(e);
			log.error("{} VelocityEngineUtils error - templateName={}", errorId, templateName);
			return "";
		}
	}

    public String getTemplate(String templateName){
		return getTemplate(new HashMap<>(),templateName);
    }
}
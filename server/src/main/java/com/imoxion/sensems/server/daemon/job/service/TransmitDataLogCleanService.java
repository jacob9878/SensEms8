package com.imoxion.sensems.server.daemon.job.service;

import com.imoxion.common.database.ImDatabaseConnectionEx;
import com.imoxion.common.util.ImConfLoaderEx;
import com.imoxion.sensems.server.environment.SensEmsEnvironment;
import com.imoxion.sensems.server.logger.ErrorTraceLogger;
import com.imoxion.sensems.server.logger.LoggerLoader;
import com.imoxion.sensems.server.repository.SmtpRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 개별발송 로그 데이터를 삭제하는 Task Service
 */
public class TransmitDataLogCleanService {

	private Logger logger = LoggerFactory.getLogger("DAEMON");

	public void run() {
		ImConfLoaderEx conf = ImConfLoaderEx.getInstance("sensems.home","sensems.xml");
		
		try {
			int saveDate = conf.getProfileInt("general.transmit_log_delete_day", 7);
			logger.info("transmit log delete. save date :{}",saveDate);
			SmtpRepository smtpRepository = SmtpRepository.getInstance();
			// 기간이 지난 로그를 일괄삭제.
			smtpRepository.deleteTransmitLogData(saveDate);
		} catch (Exception e) {
			String errorId = ErrorTraceLogger.log(e);
			logger.error("{} - transmitDataLogClean Error:{}", errorId, e.getMessage());
		}
	}
	
	public static void main(String[] args) {
        SensEmsEnvironment.init();

        LoggerLoader.initLog("sensems-daemon-log.xml");
        ImDatabaseConnectionEx.init("sensems.home","mybatis-config.xml");

        TransmitDataLogCleanService transmitDataLogCleanService = new TransmitDataLogCleanService();
		transmitDataLogCleanService.run();
	}
}

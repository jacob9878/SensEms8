package com.imoxion.sensems.server.daemon.job.service;

import com.imoxion.common.util.ImConfLoaderEx;
import com.imoxion.sensems.server.logger.ErrorTraceLogger;
import com.imoxion.sensems.server.repository.ActionLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 사용자 활동로그 (imb_user_action_log)를 삭제하는 Task Service
 */
public class ActionLogCleanService {

	private Logger logger = LoggerFactory.getLogger("DAEMON");

	public void run() {
		ImConfLoaderEx conf = ImConfLoaderEx.getInstance("sensems.home","sensems.xml");
		
		try {
			int delayDays = conf.getProfileInt("general.action_log_delete_day", 30);
			logger.info("action log delete. delay days :{}", delayDays);
			ActionLogRepository actionLogRepository = ActionLogRepository.getInstance();
			// 기간이 지난 로그를 일괄삭제.
			actionLogRepository.deleteLog(delayDays);
		} catch (Exception e) {
			String errorId = ErrorTraceLogger.log(e);
			logger.error("{} - ActionLogCleanService Error:{}", errorId, e.getMessage());
		}
	}
	
	/*public static void main(String[] args) {
        SensEmsEnvironment.init();

        LoggerLoader.initLog("sensems-daemon-log.xml");
        ImDatabaseConnectionEx.init("sensems.home","mybatis-config.xml");

        ActionLogCleanService transmitDataLogCleanService = new ActionLogCleanService();
		transmitDataLogCleanService.run();
	}*/
}

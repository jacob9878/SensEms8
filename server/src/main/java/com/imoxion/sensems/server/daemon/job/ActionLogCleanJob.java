package com.imoxion.sensems.server.daemon.job;

import com.imoxion.sensems.server.daemon.job.service.ActionLogCleanService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActionLogCleanJob implements Job {
	private Logger logger = LoggerFactory.getLogger("DAEMON");

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		logger.info("ActionLogCleanJob Execute");
		ActionLogCleanService actionLogCleanService = new ActionLogCleanService();
		actionLogCleanService.run();
		logger.info("ActionLogCleanJob Finish");
	}
	
}

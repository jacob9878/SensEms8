package com.imoxion.sensems.server.daemon.job;

import com.imoxion.sensems.server.daemon.job.service.EmsResultDataCleanService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmsMainDataCleanJob implements Job {
	private Logger logger = LoggerFactory.getLogger("DAEMON");

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		logger.info("EmsMainDataCleanJob Execute");
		EmsResultDataCleanService service = new EmsResultDataCleanService();
		service.run();
		logger.info("EmsMainDataCleanJob Finish");
	}
	
}

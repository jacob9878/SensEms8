package com.imoxion.sensems.server.daemon.job;

import com.imoxion.sensems.server.daemon.job.service.TransmitDataLogCleanService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransmitDataLogCleanJob implements Job {
	private Logger logger = LoggerFactory.getLogger("DAEMON");

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		logger.info("TransmitDataLogCleanJob Execute");
		TransmitDataLogCleanService service = new TransmitDataLogCleanService();
		service.run();
		logger.info("TransmitDataLogCleanJob Finish");
	}
	
}

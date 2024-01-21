package com.imoxion.sensems.server.daemon.job;

import com.imoxion.sensems.server.daemon.job.service.TransmitDataLogMonitoringService;
import com.imoxion.sensems.server.logger.ErrorTraceLogger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 송수신 Log 모니터링 Job
 */
public class TransmitDataLogMonitoringJob implements Job {

	private Logger logger = LoggerFactory.getLogger("DAEMON");
	
	@Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.info("TransmitDataLogMonitoringJob Execute");

        TransmitDataLogMonitoringService transmitDataLogMonitoringService = new TransmitDataLogMonitoringService();
        
        try {
        	transmitDataLogMonitoringService.dataAggregation();
        } catch (Exception e) {
            String errorId = ErrorTraceLogger.log(e);
            logger.error("{} - transmitDataLogMonitoring aggregation error",errorId);
        }
        logger.info("TransmitDataLogMonitoringJob Finish");
    }
}


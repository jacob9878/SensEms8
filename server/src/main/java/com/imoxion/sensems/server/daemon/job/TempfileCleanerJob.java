package com.imoxion.sensems.server.daemon.job;

import com.imoxion.sensems.server.daemon.job.service.TempfileCleanerService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 임시파일 삭제 작업을 실행한다.
 * Created by sunggyu on 2014-12-16.
 */
public class TempfileCleanerJob implements Job {
    private Logger logger = LoggerFactory.getLogger(TempfileCleanerJob.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        logger.info("TempfileCleanerJob execute");

        TempfileCleanerService service = TempfileCleanerService.getInstance();
        service.clean();

        logger.info("TempfileCleanerJob Finish");
    }
}

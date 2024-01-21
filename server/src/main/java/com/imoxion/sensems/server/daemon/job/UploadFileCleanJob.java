package com.imoxion.sensems.server.daemon.job;

import com.imoxion.sensems.server.daemon.job.service.UploadFileCleanService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 등록한지 2일이 지난 업로드 파일 정보를 삭제한다.
 * Created by sunggyu on 2016-07-04.
 */
public class UploadFileCleanJob implements Job {
    private Logger logger = LoggerFactory.getLogger("DAEMON");

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.info("UploadFileCleanJob execute");

        UploadFileCleanService uploadFileCleanService = new UploadFileCleanService();
        uploadFileCleanService.run();

        logger.info("UploadFileDeleteJob Finish");
    }
}

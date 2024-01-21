package com.imoxion.sensems.server.daemon.job.service;

import com.imoxion.common.util.ImConfLoaderEx;
import com.imoxion.sensems.server.logger.ErrorTraceLogger;
import com.imoxion.sensems.server.repository.UploadFileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UploadFileCleanService {
    private Logger logger = LoggerFactory.getLogger("DAEMON");

    public void run() {
        ImConfLoaderEx conf = ImConfLoaderEx.getInstance("sensems.home","sensems.xml");

        try {
            UploadFileRepository uploadFileRepository = UploadFileRepository.getInstance();

            // db 테이블 삭제
            uploadFileRepository.deleteUploadFileLazy();

            // file 삭제
            // tempfile cleaner job에서 자동 삭제됨

        } catch (Exception e) {
            String errorId = ErrorTraceLogger.log(e);
            logger.error("{} - UploadFileCleanService Error:{}", errorId, e.getMessage());
        }
    }
}

package com.imoxion.sensems.server.daemon.job.service;

import com.imoxion.common.util.ImConfLoaderEx;
import com.imoxion.sensems.server.domain.ImbEmsAttach;
import com.imoxion.sensems.server.logger.ErrorTraceLogger;
import com.imoxion.sensems.server.repository.AttachRepository;
import com.imoxion.sensems.server.service.FileDeleteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

public class AttachFileCleanService {
    private Logger logger = LoggerFactory.getLogger("DAEMON");

    public void run() {
        ImConfLoaderEx conf = ImConfLoaderEx.getInstance("sensems.home","sensems.xml");

        try {
            String attachPath = conf.getProfileString("attach", "path");
            int deleteDelayDays = conf.getProfileInt("attach", "delete_delay_day", 0);

            AttachRepository attachRepository = AttachRepository.getInstance();

            List<ImbEmsAttach> listAttach = attachRepository.getAttachListExpired(deleteDelayDays);

            // db 테이블 삭제
            attachRepository.deleteAttach(listAttach);

            // file 삭제
            for(ImbEmsAttach attach : listAttach){
                String fullPath = attachPath + File.separator + attach.getFile_path();
                FileDeleteService.fileDelete(fullPath);
                logger.info("DELETE FILE: {}", fullPath);
            }

            logger.info("Delete File Count: {}", listAttach.size());
        } catch (Exception e) {
            String errorId = ErrorTraceLogger.log(e);
            logger.error("{} - AttachFileCleanService Error:{}", errorId, e.getMessage());
        }
    }
}

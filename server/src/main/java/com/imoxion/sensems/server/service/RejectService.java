package com.imoxion.sensems.server.service;

import com.imoxion.sensems.server.logger.ErrorTraceLogger;
import com.imoxion.sensems.server.repository.RejectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class RejectService {
    private Logger logger = LoggerFactory.getLogger( ReceiverService.class );

    private static final RejectService rejectService = new RejectService();
    public static RejectService getInstance() {
        return rejectService;
    }
    private RejectService() {}
    ///////////////////

    public List<String> getRejectList() {
        RejectRepository rejectRepository = RejectRepository.getInstance();

        List<String> listReject = new ArrayList<>();
        try {
            listReject = rejectRepository.getRejectList();
        } catch (Exception e) {
            String errorId = ErrorTraceLogger.log(e);
            logger.error("{} - getRejectList error", errorId);
        }

        return listReject;
    }
}

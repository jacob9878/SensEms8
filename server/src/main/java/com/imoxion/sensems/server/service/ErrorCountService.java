package com.imoxion.sensems.server.service;

import com.imoxion.common.util.ImStringUtil;
import com.imoxion.sensems.server.ImEmsServer;
import com.imoxion.sensems.server.beans.ImEMSQueryData;
import com.imoxion.sensems.server.beans.ImRecvRecordData;
import com.imoxion.sensems.server.config.ImEmsConfig;
import com.imoxion.sensems.server.define.ImJdbcDriver;
import com.imoxion.sensems.server.define.ImStateCode;
import com.imoxion.sensems.server.domain.ImbDBInfo;
import com.imoxion.sensems.server.domain.ImbEmsMain;
import com.imoxion.sensems.server.domain.ImbReceiver;
import com.imoxion.sensems.server.emsd.ImExtractRecvThread;
import com.imoxion.sensems.server.exception.ImEmailException;
import com.imoxion.sensems.server.logger.ErrorTraceLogger;
import com.imoxion.sensems.server.repository.ErrorCountRepository;
import com.imoxion.sensems.server.repository.MsgidRecvRepository;
import com.imoxion.sensems.server.repository.ReceiverRepository;
import com.imoxion.sensems.server.util.ImEmsUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ErrorCountService {
    private Logger logger = LoggerFactory.getLogger( ErrorCountService.class );

    private static final ErrorCountService errorCountService = new ErrorCountService();
    public static ErrorCountService getInstance() {
        return errorCountService;
    }
    private ErrorCountService() {}
    ///////////////////


    public void insertErrorCountInit(String msgid){
        ErrorCountRepository errorCountRepository = ErrorCountRepository.getInstance();
        try {
            errorCountRepository.insertErrorCountInit(msgid);
        } catch (Exception ex) {
            String errorId = ErrorTraceLogger.log(ex);
            logger.error("{} - insertErrorCount error", errorId);
        }
    }

    /**
     * 수신자 추출 도중 발생하는 수신자 오류
     */
    public void updateBasicErrorCount(int nEmailAddr, int nReject, int nRepeat, int nDomain, int nBlank, String msgid){
        ErrorCountRepository errorCountRepository = ErrorCountRepository.getInstance();
        try {
            errorCountRepository.updateBasicErrorCount(nEmailAddr, nReject, nRepeat, nDomain, nBlank, msgid);
        } catch (Exception ex) {
            String errorId = ErrorTraceLogger.log(ex);
            logger.error("{} - updateBasicErrorCount error", errorId);
        }
    }



}

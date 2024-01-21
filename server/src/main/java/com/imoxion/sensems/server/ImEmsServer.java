package com.imoxion.sensems.server;

import com.imoxion.common.database.ImDatabaseConnectionEx;
import com.imoxion.sensems.server.config.ImEmsConfig;
import com.imoxion.sensems.server.emsd.ImEmsDistThread;
import com.imoxion.sensems.server.emsd.ImEmsLogThread;
import com.imoxion.sensems.server.emsd.ImEmsMainThread;
import com.imoxion.sensems.server.logger.ErrorTraceLogger;
import com.imoxion.sensems.server.logger.LoggerLoader;
import com.imoxion.sensems.server.service.EmsMainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;

public class ImEmsServer {
    private static Logger logger = LoggerFactory.getLogger("EMSD");

    public static boolean isAlive = true;

    public static void main(String[] args)  {
        // System.out.println 로그를 logback 의 CONSOLE 로그에 찍히도록 수정
        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();

        LoggerLoader.initLog("emslog.xml");
        ImDatabaseConnectionEx.init("sensems.home", "mybatis-config.xml");

        ImEmsConfig emsConfig = ImEmsConfig.getInstance();

        // 발송중 서비스가 재시작되거나 서버가 재부팅되었을때
        // 발송중인 메일은 모두 중지시킨다.
        EmsMainService emsMainService = EmsMainService.getInstance();
        emsMainService.stopAllSending();

        // send server host 정보 추출

        // sender 서버로 데이터를 전달
        ImEmsDistThread th = new ImEmsDistThread();
        th.setName("DistTh");
        th.start();

        ImEmsLogThread logThread = new ImEmsLogThread();
        logThread.setName("LogTh");
        logThread.start();

        // ImEmsMainThread Start
        try {
            ImEmsMainThread emsMainThread = new ImEmsMainThread();
            emsMainThread.setName("EmsMainTh");
            emsMainThread.start();
            emsMainThread.join();
        }catch(Exception e){
            String errorId = ErrorTraceLogger.log(e);
            logger.error("{} ImbEmsServer error", errorId);
        }

    }
}

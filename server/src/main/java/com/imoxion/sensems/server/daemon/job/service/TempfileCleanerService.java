package com.imoxion.sensems.server.daemon.job.service;

import com.imoxion.common.util.ImConfLoaderEx;
import com.imoxion.common.util.ImTimeUtil;
import com.imoxion.sensems.server.config.ImEmsConfig;
import com.imoxion.sensems.server.service.FileDeleteService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Calendar;

/**
 * Created by Administrator on 2014-12-16.
 */
public class TempfileCleanerService {

    private Logger logger = LoggerFactory.getLogger(TempfileCleanerService.class);

    private static TempfileCleanerService tempfileCleanerService;

    public static TempfileCleanerService getInstance(){
        if( tempfileCleanerService == null ){
            tempfileCleanerService = new TempfileCleanerService();
        }
        return tempfileCleanerService;
    }

    private TempfileCleanerService(){}

    public void clean(){

        ImConfLoaderEx conf = ImConfLoaderEx.getInstance("sensems.home","sensems.xml");

        String tempfilePath = ImEmsConfig.getInstance().getTempfile();

        // Cleaner 데몬은 보통 자정을 넘겨서 실행하기 때문에 체크 날짜를 하루 전날 0시 0분 0초를 기준으로 이전 파일을 삭제한다.
        Calendar checkDay = Calendar.getInstance();
        checkDay.add(Calendar.DAY_OF_MONTH, -1);
        checkDay.set(Calendar.HOUR_OF_DAY, 0 );
        checkDay.set(Calendar.MINUTE, 0);
        checkDay.set(Calendar.SECOND, 0);

        long checkDayTime = checkDay.getTimeInMillis();

        // 만약 tempfile 의 경로를 잘못 입력했을 경우를 방지한다.
        String sensHomePath = System.getProperty("sensems.home");
        // 센스메일 홈과 tempfile 경로가 같을 경우 실행하지 않는다.
        if(StringUtils.isNotEmpty(sensHomePath) && sensHomePath.equalsIgnoreCase( tempfilePath ) ){
            return;
        }
        deleteFile(tempfilePath, checkDayTime);
    }

    private void deleteFile(String tempfilePath , long checkDayTime ){
        File tempFile = new File( tempfilePath );
        if( tempFile.exists() && tempFile.isDirectory() ){
            File[] files = tempFile.listFiles();
            if( files != null ) {
                for (File f : files) {
                    // 체크시간 이전의 파일만 삭제한다.
                    if (checkDayTime > f.lastModified()) {
                        try {
                            Calendar c = Calendar.getInstance();
                            c.setTimeInMillis(f.lastModified());
                            
                            if (f.isDirectory()) {
                            	FileUtils.deleteDirectory(f);
                            } else {
                                FileDeleteService.fileDelete(f);
                            }
                            logger.debug("tempfile delete - filename:{} , createTime:{}",f.getName(), ImTimeUtil.getDateFormat(c.getTime(),"yyyy-MM-dd HH:mm:ss"));
                        } catch (Exception e) {
                            logger.error("tempfile delete error - {}",e.getMessage());
                        }
                    }
                }
            }
        }
    }
}

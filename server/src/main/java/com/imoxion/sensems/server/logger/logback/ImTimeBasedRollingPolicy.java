package com.imoxion.sensems.server.logger.logback;

import ch.qos.logback.core.rolling.RolloverFailure;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;

import com.imoxion.sensems.server.util.ImFilePermission;
import org.apache.commons.io.FileUtils;

import java.io.File;

public class ImTimeBasedRollingPolicy<E> extends TimeBasedRollingPolicy<E> {
    private static boolean doRolling = true;

    /*@Override
    public boolean isTriggeringEvent(File activeFile, E event) {
        boolean result = super.isTriggeringEvent(activeFile, event);

        // 로그파일 소유권 처리(sensmail.xml 의 system.user,group,permission)
        try {
            *//*if(!activeFile.exists()){
                System.out.println("Log ActiveFileName Not Exist(isTriggeringEvent): " + activeFile.getAbsolutePath());
            }*//*

            ImServerUtils.setFilePermission(activeFile.getAbsolutePath());
        }catch(Exception e){}

        return result;
    }*/

    @Override
    public void start() {
        super.start();

        try {
            // 파일이 없으면 생성
            FileUtils.touch(new File(this.getActiveFileName()));
            // 로그파일 소유권 처리(sensmail.xml 의 system.user,group,permission)
            ImFilePermission.setFilePermission(this.getActiveFileName());
            //System.out.println("Log ActiveFileName(start): " + this.getActiveFileName() + " - " + new File(this.getActiveFileName()).exists());
        }catch(Exception e){}
    }

    @Override
    public void rollover() throws RolloverFailure {
        super.rollover();
        try {
            // 파일이 없으면 생성
            FileUtils.touch(new File(this.getActiveFileName()));
            // 로그파일 소유권 처리(sensmail.xml 의 system.user,group,permission)
            ImFilePermission.setFilePermission(this.getActiveFileName());
            //System.out.println("Log ActiveFileName(rollover): " + this.getActiveFileName() + " - " + new File(this.getActiveFileName()).exists());
        }catch(Exception e){}
    }
}
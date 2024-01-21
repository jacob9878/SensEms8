package com.imoxion.sensems.common.logger.logback;

import java.io.File;

import com.imoxion.sensems.web.service.SendResultService;
import org.apache.commons.io.FileUtils;

import com.imoxion.sensems.web.common.util.ImServerUtils;

import ch.qos.logback.core.rolling.RolloverFailure;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImTimeBasedRollingPolicy<E> extends TimeBasedRollingPolicy<E> {
    private static boolean doRolling = true;
    protected Logger log = LoggerFactory.getLogger(ImTimeBasedRollingPolicy.class);
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
            ImServerUtils.setFilePermission(this.getActiveFileName());
            //System.out.println("Log ActiveFileName(start): " + this.getActiveFileName() + " - " + new File(this.getActiveFileName()).exists());
        }catch (NullPointerException ne) {
            log.error("start error");
        }
        catch (RuntimeException re) {
            log.error("start error");
        }
        catch(Exception e){

            log.error("start error");
        }
    }

    @Override
    public void rollover() throws RolloverFailure {
        super.rollover();
        try {
            // 파일이 없으면 생성
            FileUtils.touch(new File(this.getActiveFileName()));
            // 로그파일 소유권 처리(sensmail.xml 의 system.user,group,permission)
            ImServerUtils.setFilePermission(this.getActiveFileName());
            //System.out.println("Log ActiveFileName(rollover): " + this.getActiveFileName() + " - " + new File(this.getActiveFileName()).exists());
        }catch (NullPointerException ne) {
            log.error("rollover error");
        }
        catch (RuntimeException re) {
            log.error("rollover error");
        }catch(Exception e){
            log.error("rollover error");
        }
    }
}

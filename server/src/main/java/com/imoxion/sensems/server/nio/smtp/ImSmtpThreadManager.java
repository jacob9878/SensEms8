package com.imoxion.sensems.server.nio.smtp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ImSmtpThreadManager {

    private Logger logger = LoggerFactory.getLogger(ImSmtpThreadManager.class);

    public final static int BULK = 0;
    public final static int SEND = 1;
    public final static int RESEND = 2;
    public final static int RESERV = 3;
    public final static int ETC = 4;

    private static ImSmtpThreadManager smtpThreadManager;
    public static ImSmtpThreadManager getInstance(){
        if( smtpThreadManager == null ){
            smtpThreadManager = new ImSmtpThreadManager();
        }
        return smtpThreadManager;
    }
    public static boolean isLoad(){
        return smtpThreadManager != null;
    }

    private List<Thread> bulkThreadList = new ArrayList();
    private List<Thread> sendThreadList = new ArrayList();
    private List<Thread> resendThreadList = new ArrayList();
    private List<Thread> reservThreadList = new ArrayList();

    /**
     * 발송 쓰레드 외 쓰레드 목록
     */
    private List<Thread> etcThreadList = new ArrayList<>();

    public List<Thread> getBulkThreadList() {
        return bulkThreadList;
    }

    public List<Thread> getSendThreadList() {
        return sendThreadList;
    }

    public List<Thread> getResendThreadList() {
        return resendThreadList;
    }

    public List<Thread> getReservThreadList() {
        return reservThreadList;
    }

    public void addThread(int type,Thread thread){
        if( type == BULK ){
            bulkThreadList.add(thread);
        }else if(type == SEND ){
            sendThreadList.add(thread);
        }else if(type == RESEND ){
            resendThreadList.add(thread);
        }else if(type == RESERV ){
            reservThreadList.add(thread);
        }else if(type == ETC ){
            etcThreadList.add(thread);
        }
    }

    public void interrupt(){
        for(Thread th : sendThreadList){
            th.interrupt();
        }
        for(Thread th : bulkThreadList){
            th.interrupt();
        }
        for(Thread th : resendThreadList){
            th.interrupt();
        }
        for(Thread th : reservThreadList){
            th.interrupt();
        }
        for(Thread th : etcThreadList){
            th.interrupt();
        }
    }

    public void clear(){
        smtpThreadManager = null;
    }
}
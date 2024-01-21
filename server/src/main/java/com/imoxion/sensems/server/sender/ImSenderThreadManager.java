package com.imoxion.sensems.server.sender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ImSenderThreadManager {

    private Logger logger = LoggerFactory.getLogger(ImSenderThreadManager.class);

//    public final static int BULK = 0;
    public final static int SEND = 1;
//    public final static int RESEND = 2;
//    public final static int RESERV = 3;
    public final static int ETC = 4;

    private static ImSenderThreadManager senderThreadManager;
    public static ImSenderThreadManager getInstance(){
        if( senderThreadManager == null ){
            senderThreadManager = new ImSenderThreadManager();
        }
        return senderThreadManager;
    }
    public static boolean isLoad(){
        return senderThreadManager != null;
    }

    private List<Thread> sendThreadList = new ArrayList();
    private List<Thread> etcThreadList = new ArrayList<>();


    public void addThread(int type, Thread thread){
        if(type == SEND ){
            sendThreadList.add(thread);
        }else if(type == ETC ){
            etcThreadList.add(thread);
        }
    }

    public void interrupt(){
        for(Thread th : sendThreadList){
            th.interrupt();
        }

        for(Thread th : etcThreadList){
            th.interrupt();
        }
    }

    public void clear(){
        senderThreadManager = null;
    }
}
package com.imoxion.sensems.server.service;

import com.imoxion.common.thread.ImBlockingQueue;
import com.imoxion.common.util.ImUtils;
import com.imoxion.sensems.server.beans.ImQueueObj;
import com.imoxion.sensems.server.config.ImSmtpConfig;
import com.imoxion.sensems.server.logger.ErrorTraceLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class MessageQueueService {

    private Logger smtpLogger = LoggerFactory.getLogger("SMTP");

    private List<ImQueueObj> m_arrQueue = new ArrayList();

    private ImBlockingQueue m_bqBulkMsg = new ImBlockingQueue();

    private int queueSize;

    private static MessageQueueService messageQueueService;
    public synchronized static MessageQueueService getInstance(){
        if( messageQueueService == null ){
            messageQueueService = new MessageQueueService();
        }
        return messageQueueService;
    }

    public int getQueueSize() {
        return queueSize;
    }

    private MessageQueueService(){
        this.queueSize = ImSmtpConfig.getInstance().getQueueSize();
    }

    public void addQueue(ImQueueObj queue){
        this.m_arrQueue.add(queue);
    }

    public ImQueueObj getQueue(int idx){
        return m_arrQueue.get(idx);
    }

    public List<ImQueueObj> getQueueList(){
        return m_arrQueue;
    }
    /**
     *
     * @Method Name    : addMessageQueue
     * @Method Comment : 로드 밸런싱을 해서 멀티큐에 메시지를 넣는다.
     *
     * @param queuePath 메시지 경로
     */
    public synchronized void addMessageQueue(String queuePath){
        int limit = 1000000;
        ImQueueObj inQueue = null;
        int minSize = Integer.MAX_VALUE;
       
        for(int i=0;i<m_arrQueue.size();i++){
            ImQueueObj queue = m_arrQueue.get(i);
            int currQueueSize = queue.getQueue().getSize();
            if( currQueueSize > limit){
                // 큐에 메일 수가 지정된 것보다 많을때. 실제 큐에서 제거하고 대기큐에 넣는다.
                if(queue.isAlive()){
                    queue.setAlive(false);
                }
            }

            if(queue.isAlive() && currQueueSize < minSize){
                minSize = currQueueSize;
                inQueue = queue;
            }
        }

        if(inQueue != null){
            inQueue.getQueue().enqueue(queuePath);
        }else{
            // 만약 유효한 큐가 없으면 첫번째 큐에다 넣는다.
            m_arrQueue.get(0).getQueue().enqueue(queuePath);
        }
//		m_bqLocalMsg.enqueue(queuePath);
    }

    /**
     *
     * @Method Name    : addMessageQueue
     * @Method Comment : 큐에 직접 지정해서 메시지를 넣는다.
     *
     * @param queuePath 큐 경로를 넣는다
     * @param queueIndex 멀티 큐 인덱스
     */
    public synchronized void addMessageQueue(String queuePath,int queueIndex){
        ((ImQueueObj)m_arrQueue.get(queueIndex)).getQueue().enqueue(queuePath);
    }

    /**
     *
     * @Method Name    : getMessageQueue
     * @Method Comment : 멀티큐를 로드밸런싱을 해서 추가 가능한 큐개수를 가져온다.
     *
     * @return 큐 객체
     */
    public synchronized ImQueueObj getMessageQueue(){
        int limit = 1000000;
        ImQueueObj inQueue = null;
        int minSize = Integer.MAX_VALUE;
       
        for(int i=0;i<m_arrQueue.size();i++){
            ImQueueObj queue = (ImQueueObj)m_arrQueue.get(i);
            int currQueueSize = queue.getQueue().getSize();
//			smtpLogger.debug( "Queue : "+queue.getIndex()+" is "+currQueueSize);
            
            if(queue.isAlive() && currQueueSize < minSize){
                minSize = currQueueSize;
                inQueue = queue;
            }
        }

        if(inQueue == null){
            // 만약 유효한 큐가 없으면 랜덤하게 큐에다 넣는다.
            int nQueue = ImUtils.getRandom(0,m_arrQueue.size()-1);
            inQueue = m_arrQueue.get(nQueue);
        }

//		smtpLogger.debug( "In Queue : "+inQueue.getIndex());


        return inQueue;
//		m_bqLocalMsg.enqueue(queuePath);
    }

    public String extractMessageQueue(int n){
        String sQueuePath = null;
        ImQueueObj queue = m_arrQueue.get(n);
        try {
            if (queue != null) {
                sQueuePath = (String) queue.getQueue().dequeue();
            }
        } catch(InterruptedException ie){
            smtpLogger.debug("normal queue-{} interrupt",n);
        } catch(Exception e){
            String errorId = ErrorTraceLogger.log(e);
            smtpLogger.error("{} - [SmtpServer] ", errorId );
        }

        return sQueuePath;
    }

    public void addBulkMessageQueue(String queuePath){
        m_bqBulkMsg.enqueue(queuePath);
    }

    public String extractInBulkMessageQueue(){
        String sQueuePath = null;
        try {
            sQueuePath = (String)m_bqBulkMsg.dequeue();
        } catch(InterruptedException ie){
            smtpLogger.debug("bulk queue interrupt");
        } catch(Exception e){
            String errorId = ErrorTraceLogger.log(e);
            smtpLogger.error("{} - [SmtpServer] ", errorId );
        }

        return sQueuePath;
    }

    /**
     * 현재 등록되어있는 큐 개수를 제공.
     * @return
     */
    public int getNormalQueueCount(){
        int queueCount = 0;
        for(int i = 0 ; i < queueSize ; i++ ){
            ImQueueObj queue = (ImQueueObj)m_arrQueue.get(i);
            if(queue != null) {
                queueCount += queue.getQueue().getSize();
            }
        }
        return queueCount;
    }

    /**
     * 현재 벌크 큐 개수를 제공
     * @return
     */
    public int getBulkQueueCount(){
        return m_bqBulkMsg.getSize();
    }
}

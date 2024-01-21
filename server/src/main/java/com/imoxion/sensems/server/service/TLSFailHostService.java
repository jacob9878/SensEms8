package com.imoxion.sensems.server.service;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TLS연결 실패한 아이피를 보관하면서 24시간이 지나면 삭제
 * @author Administrator
 *
 */
public class TLSFailHostService {

    private Map<Long,String> mapFailHost = new ConcurrentHashMap<Long,String>();

    private static TLSFailHostService failTLSHostService = new TLSFailHostService();
    public static TLSFailHostService getInstance(){
        return failTLSHostService;
    }

    private TLSFailHostService() {}

    /**
     * ip 가 목록에 있는지 체크
     * @param ip
     * @return
     */
    public boolean hasValue(String ip){
        return mapFailHost.containsValue(ip);
    }

    /**
     * ip를 목록에 저장
     * @param ip
     */
    public void push(String ip){
    	mapFailHost.put(System.currentTimeMillis(), ip);
    }

    /**
     * 보관기간이 지난 host를 메모리에서 제거한다.
     */
    public void cleanMemory(){
        this.flush( 1440 );
    }
    
    /**
     * min(분)이 지난 데이터는 메모리에서 정리한다.
     * @param min
     */
    private void flush(int min){
        Iterator<Long> hostValueCreateTimes = mapFailHost.keySet().iterator();
        while( hostValueCreateTimes.hasNext() ){
            long ipValueCreateTime = hostValueCreateTimes.next();
            // ip가 등록된 시간이 min(분) 지난 경우 삭제한다.
            long nowTime = System.currentTimeMillis();
            long checkTime = nowTime - (min * 60 * 1000);
            // 등록된 시간이 현재보다 1일 이전이면 삭제한다.
            if( ipValueCreateTime <  checkTime ){
            	mapFailHost.remove(ipValueCreateTime);
            }
        }
    }
}
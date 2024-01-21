package com.imoxion.sensems.server.service;

import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ConnectErrorDomainService {
    private static Logger senderLogger = LoggerFactory.getLogger("SENDER");

    public static final int MIN_ERROR_COUNT = 3;

    private static ConnectErrorDomainService connectErrorDomainService;
    // 메일 발송시 연결에 문제가 있는 도메인을 10분간 map에 넣고
    // 여기에 있으면 10분내에는 다시 발송시도 하지 않음
    private static Map<String, Integer> connectErrorDomainMap;

    public static ConnectErrorDomainService getInstance() {
        if( connectErrorDomainService == null ){
            connectErrorDomainService = new ConnectErrorDomainService();
        }
        return connectErrorDomainService;
    }

    private ConnectErrorDomainService(){
        connectErrorDomainMap = ExpiringMap.builder()
                .maxSize(100000)
                .expiration(10, TimeUnit.MINUTES)
                //.expiration(20, TimeUnit.SECONDS)
                .expirationPolicy(ExpirationPolicy.CREATED)
                .expirationListener((key, value) -> senderLogger.info("Removed from connectErrorDomainMap: {}", key) )
                .build();
    }

    // domain이 존재하면 0보다 큰값, 아니면 0
    public int getDomainCount(String domain){
        if(StringUtils.isEmpty(domain)) return -1;
        domain = domain.toLowerCase();
        if(connectErrorDomainMap.containsKey(domain)) {
            return connectErrorDomainMap.get(domain);
        }

        return 0;
    }

    public boolean isErrorDomain(String domain){
        if(StringUtils.isEmpty(domain)) return false;
        domain = domain.toLowerCase();
        // count가 3이상이면 연결시도 안함
        if(connectErrorDomainMap.containsKey(domain)) {
            if(connectErrorDomainMap.get(domain) >= ConnectErrorDomainService.MIN_ERROR_COUNT) {
                return true;
            }
        }

        return false;
    }

    // connectErrorDomainMap에 추가: 해당 도메인으로 연결이 안되어 추가
    public int putDomainAndCount(String domain){
        domain = domain.toLowerCase();
        Integer num = connectErrorDomainMap.compute(domain, (k, v) -> v == null ? 1 : v + 1 );
        if(num >= MIN_ERROR_COUNT) senderLogger.info("Put to connectErrorDomainMap: {} / {}", domain, num);
        return num;
    }

    // connectErrorDomainMap에서 제거: 연결이 잘 되기 때문에 제거
    public void removeDomain(String domain){
        domain = domain.toLowerCase();
        connectErrorDomainMap.remove(domain);
    }

//    public int removeDomainAndCount(String domain){
//        domain = domain.toLowerCase();
//
//        Integer num = connectErrorDomainMap.compute(domain, (k, v) -> v == null ? 0 : v - 1 );
//        if( num <= 0) connectErrorDomainMap.remove(domain);
//
//        return num;
//    }

    // 전체 초기화
    public void removeAll(){
        connectErrorDomainMap.clear();
    }
}

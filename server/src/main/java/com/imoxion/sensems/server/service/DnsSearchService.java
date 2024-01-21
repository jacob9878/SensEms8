package com.imoxion.sensems.server.service;

import com.imoxion.common.net.ImDNSServer;
import com.imoxion.sensems.server.config.ImEmsConfig;

public class DnsSearchService extends ImDNSServer {

    private static DnsSearchService dnsSearchService;

    public static DnsSearchService getInstance(){
        String dnsServers = ImEmsConfig.getInstance().getDnsServer();
        return getInstance(dnsServers);
    }

    public static DnsSearchService getInstance(String p_sDNSServers){
        if( dnsSearchService == null ){
            dnsSearchService = new DnsSearchService(p_sDNSServers);
        }
        return dnsSearchService;
    }

    public DnsSearchService(String p_sDNSServers) {
        super(p_sDNSServers);
    }
}
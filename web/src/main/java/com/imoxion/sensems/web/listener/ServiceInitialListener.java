package com.imoxion.sensems.web.listener;

import com.imoxion.common.database.ImDatabaseConnectionEx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceInitialListener implements StartupListener{

    Logger log = LoggerFactory.getLogger(ServiceInitialListener.class);

    @Override
    public void activate() {
        ImDatabaseConnectionEx.init("sensems.home", "mybatis-config.xml");
    }
}

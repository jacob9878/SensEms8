package com.imoxion.sensems.server.service;

import com.imoxion.security.ImSecurityLib;
import com.imoxion.sensems.server.config.ImEmsConfig;
import com.imoxion.sensems.server.define.ImJdbcDriver;
import com.imoxion.sensems.server.domain.ImbDBInfo;
import com.imoxion.sensems.server.repository.AddressRepository;
import com.imoxion.sensems.server.repository.DatabaseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;

public class AddressService {
    private Logger log = LoggerFactory.getLogger( AddressService.class );

    private static final AddressService addressService = new AddressService();
    public static AddressService getInstance() {
        return addressService;
    }
    private AddressService() {}
    ////////////////////////////////////


    public String getRecvQueryAddr(String userid, String msgid) throws Exception {
        AddressRepository addressRepository = AddressRepository.getInstance();

        String recvQueryAddr = addressRepository.getRecvQueryAddr(userid, msgid);
        return recvQueryAddr;
    }




}

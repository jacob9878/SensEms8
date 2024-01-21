package com.imoxion.sensems.server.service;

import com.imoxion.sensems.server.beans.ImFromMonBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.concurrent.ConcurrentHashMap;

public class FromMonService {
    private static Logger smtpLogger = LoggerFactory.getLogger("SMTP");
    private static final FromMonService fromMonService = new FromMonService();
    private ConcurrentHashMap<String, ImFromMonBean> mapFromMon = new ConcurrentHashMap<>();

    public static FromMonService getInstance() {
        return fromMonService;
    }
    private FromMonService(){}

    public ConcurrentHashMap<String, ImFromMonBean> getMapFromMon() {
        return mapFromMon;
    }

    public void setMapFromMon(ConcurrentHashMap<String, ImFromMonBean> mapFromMon) {
        this.mapFromMon = mapFromMon;
    }

    public void addFromMon(String fromEmail,long size){
        String key = fromEmail;
        if(mapFromMon == null)
            return;

        // 만약 이전에 설정된 From 주소가 없으면 새로운 객체를 만든다.
        ImFromMonBean uBean = new ImFromMonBean();
        uBean.setKey(fromEmail);
        uBean.setCount(1);
        uBean.setSize(size);
        uBean.setTimestamp(Calendar.getInstance().getTimeInMillis());

        ImFromMonBean uBeanIn = mapFromMon.putIfAbsent(fromEmail, uBean);
        // 만약 기본에 존재하는 경우에
        if(uBeanIn != null){
            // 만약 이전에 설정된 From 주소가 있으면 값만 추가.
            uBeanIn.addCount(1);
            uBeanIn.addSize(size);
            uBeanIn.setTimestamp(Calendar.getInstance().getTimeInMillis());
            //smtpLogger.info("uBean == " + uBeanIn.toString());
        }
    }

}

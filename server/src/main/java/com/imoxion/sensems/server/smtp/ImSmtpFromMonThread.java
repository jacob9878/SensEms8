package com.imoxion.sensems.server.smtp;

import com.imoxion.sensems.server.beans.ImFromMonBean;
import com.imoxion.sensems.server.config.ImServerPolicyConfig;
import com.imoxion.sensems.server.logger.ErrorTraceLogger;
import com.imoxion.sensems.server.service.FromMonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

public class ImSmtpFromMonThread extends Thread{

	public Logger smtpLogger = LoggerFactory.getLogger("SMTP");
	@Override
	public void run() {
		int runTime = 0;
		Calendar calStart = Calendar.getInstance();
		SimpleDateFormat sdfStart = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		ImServerPolicyConfig policyConfig = ImServerPolicyConfig.getInstance();
		try{
			smtpLogger.info("ImSmtpFromMonThread start");
			while( !isInterrupted() ){
				Calendar cal = Calendar.getInstance();
				long currTimestamp = cal.getTimeInMillis();

				// 제한 설정값을 가져온다.
		    	String limitCount = policyConfig.get(ImServerPolicyConfig.LIMIT_BULK_MAIL_COUNT);
		    	String limitSize = policyConfig.get(ImServerPolicyConfig.LIMIT_BULK_MAIL_TOTAL_SIZE);


		    	long lLimitSize = 0;
		    	long lLimitCount = 0;
		    	int nMonTime = 3600;
		    	try{
		    		lLimitCount = Long.parseLong(limitCount);
		    		// 단위가 MB 이기 때문에 1000을 곱한다.
		    		lLimitSize = Long.parseLong(limitSize) * 1024 * 1024;

		    		nMonTime = 3600;

		    	}catch(NumberFormatException ex){
					smtpLogger.warn("parseLong or parseInt error - limitCount:{}, limitSize:{}, monTime:{}",limitCount,limitSize, nMonTime);
				}

				for( Map.Entry<String, ImFromMonBean> entry : FromMonService.getInstance().getMapFromMon().entrySet()){
		    		String sKey = entry.getKey();
		    		ImFromMonBean uBean = entry.getValue();
		    		if(uBean != null){
		    			long userSize = uBean.getSize();
		    			long userCount = uBean.getCount();

						smtpLogger.info("ImSmtpFromMonThread ImFromMonBean - from {} / count {} / size {}", uBean.getKey(), uBean.getCount(), uBean.getSize());

		    			if(lLimitCount > 0 && lLimitCount < userCount){
		    				// 한계수를 넘었기 때문에 대용량 큐로 처리
		    				uBean.setBulk(true);
							FromMonService.getInstance().getMapFromMon().put(sKey, uBean);
		    				smtpLogger.info( "Sender From count, bulk queue started ("+sKey+") : Limit value ==>"+lLimitCount+", Sender value ==>"+userCount);
		    				continue;
		    			}

		    			if(lLimitSize > 0 && lLimitSize < userSize){
		    				// 한계 용량을 넘었기 때문에 대용량 큐로 처리.
		    				uBean.setBulk(true);
							FromMonService.getInstance().getMapFromMon().put(sKey, uBean);
		    				smtpLogger.info( "Sender From size, bulk queue started ("+sKey+") : Limit value ==>"+lLimitSize+", Sender value ==>"+userSize);
		    				continue;
		    			}
		    		}
		    	}

				// 한시간에 한번씩만 초기화
				if(runTime >= nMonTime){
					// 사용자 모니터 객체를 초기화한다.
					FromMonService.getInstance().getMapFromMon().clear();
					runTime = 0;

					smtpLogger.info("ImSmtpFromMonThread reset mapFromMon");
				}

				// 30초 대기
				sleep(30000);
				runTime +=30;

				// 15초 대기
//				sleep(15000);
			}

		}catch(InterruptedException ie){
			smtpLogger.debug("ImSmtpFromMonThread Interrupted");
		}catch(Exception ex){
    		String errorId = ErrorTraceLogger.log(ex);
    		smtpLogger.error("{} - [SMTP]  ImSmtpFromMonThread " , errorId );

		}
	}

}

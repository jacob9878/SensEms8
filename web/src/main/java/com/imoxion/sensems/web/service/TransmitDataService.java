package com.imoxion.sensems.web.service;

import com.imoxion.sensems.web.database.mapper.TransmitDataMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * TransmitData
 * */
@Service
public class TransmitDataService {

	@Autowired
	private TransmitDataMapper transmitDataMapper;

	public int updateTransmitDataReaddate(String traceid, String serverid, String rctpto) {
		Date readDate = new Date();
		return transmitDataMapper.updateTransmitDataReaddate(traceid, serverid, rctpto, readDate);
	}

	public int updateTransmitCount(String traceid, String serverid, String rctpto) {
		return transmitDataMapper.updateTransmitCount(traceid, serverid, rctpto);
	}


}

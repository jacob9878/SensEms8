package com.imoxion.sensems.web.service;


import java.util.Date;

import com.imoxion.sensems.web.database.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.imoxion.common.util.ImConfLoaderEx;
import com.imoxion.common.util.ImIpUtil;
import com.imoxion.sensems.web.common.ImbConstant;
import com.imoxion.sensems.web.database.domain.ImbUserinfo;

@Service
public class AccountService {
	
	private Logger log = LoggerFactory.getLogger(AccountService.class);

	@Autowired
	private UserMapper userMapper;

	/**
	 * 허용IP인지 확인 실시
	 * @param requestIp
	 * @return
	 */
    public boolean isAllow(String requestIp) {
        boolean allow = false;

        ImConfLoaderEx allowIpConfig = ImConfLoaderEx.getInstance("sensems.home","allow.properties", true);
        String[] allowips = allowIpConfig.getProfileStringArray("allow.ip");

        if (allowips != null) {
            for (String allowip : allowips) {
            	log.debug("allowip : requestIp - {} : {}", allowip, requestIp);
                if (ImIpUtil.matchIP(allowip, requestIp)) {
                    allow = true;
                    break;
                }
            }
        }
        return allow;
    }

	/**
	 * 패스워드 변경 시점 확인
	 * @param userInfo
	 * @return
	 */
	public boolean checkRequirePasswordChange(ImbUserinfo userInfo) {

		Date pwdDate = userInfo.getPwd_date(); //비밀번호 변경 날짜
		if(pwdDate == null){
			return false;
		}
		/* 날짜 : 타임스탬프 기준 계산 (1970-01-01 00:00:00)
		 * 1초  = 1,000 ms ( 1 * 1000)
		 * 1분 = 60,000 ms  ( 60 * 1000)
		 * 1시간 = 3,600,000 ms ( 60 * 60 * 1000 )
		 * 1일 = 86,400,000 (24 * 60 * 60 * 1000 )
		 * */
		long checkDate = ImbConstant.PASSWORD_CHANGE_PERIOD; // 비밀번호 변경 주기
		Long lPassTime = pwdDate.getTime(); //비밀번호 변경날짜 기준으로 계산된 지난 시간
		Long chkTime = lPassTime + (checkDate * 24 * 60 * 60 * 1000); // 제한일자 = 지난시간 + (비밀번호변경기간  * 1일)
		Long nowTime = System.currentTimeMillis(); // 현재시간 (currentTimeMillis(): 1/1000초의 값 long 형으로 리턴)

		// 제한일자와 현재 일자를 비교하여 비밀번호를 변경해야하는지 결정
		if (nowTime > chkTime) {
			return true;
		}
		return false;
    }

	/**
	 * 패스워드 변경 페이지에서 변경 처리
	 * @param userid
	 * @param newSecuPass
	 */
	public void updatePassword(String userid, String newSecuPass) throws Exception {
		userMapper.updatePassword(userid,newSecuPass,new Date());
	}
}
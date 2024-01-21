package com.imoxion.sensems.web.service;

import com.imoxion.common.util.ImStringUtil;
import com.imoxion.sensems.web.common.ImbConstant;
import com.imoxion.sensems.web.database.domain.ImbReject;
import com.imoxion.sensems.web.database.domain.ImbRelay;
import com.imoxion.sensems.web.database.domain.ImbUserinfo;
import com.imoxion.sensems.web.database.mapper.RejectMapper;
import com.imoxion.sensems.web.database.mapper.RelayMapper;
import com.imoxion.sensems.web.form.ActionLogForm;
import com.imoxion.sensems.web.form.RejectForm;
import com.imoxion.sensems.web.form.RelayForm;
import com.imoxion.sensems.web.util.HttpRequestUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.List;

@Service
public class RelayService {

	@Autowired
	private RelayMapper relayMapper;

	@Autowired
	private ActionLogService actionLogService;
	
	protected Logger log = LoggerFactory.getLogger(RelayService.class);

	
	// 전체 수신거부 목록 취득
	public List<ImbRelay> getRelayListAll() throws Exception{
		return relayMapper.selectAllRelay();
	}

	public int getRelayCount(String srch_keyword){
		return  relayMapper.selectRelayCount(srch_keyword);
	}	

	
	public List<ImbRelay> getRelayList(String srch_keyword, int start, int end){
		return relayMapper.selectRelayList(srch_keyword, start, end);
	}
	public int getSearchRelayCount( String srch_type, String srch_keyword, String ip, String memo) throws Exception{
		return relayMapper.getSearchRelayCount(srch_type,srch_keyword,ip,memo);
	}

	public List<ImbRelay> getRelayListForPageing(String srch_type, String srch_keyword, String ip, String memo, int start , int end ) throws Exception{

		return relayMapper.getRelayListForPageing(srch_type, srch_keyword, ip, memo, start, end);
	}

	/**
	 * 중복 ip 데이터 확인
	 * @param ip
	 * */
	public boolean isExistRelay(String ip) throws Exception{
		int count = relayMapper.isExistRelay(ip);
		if(count > 0){
			return true;
		}
		return false;
	}
	
	/**
	 * ip 정보를 가져온다.
	 * 
	 * @param ip
	 * @return
	 */
	public RelayForm getRelayInfo(String ip) throws Exception{
		ImbRelay imbRelay = relayMapper.selectRelayByKey(ip);
		if(imbRelay == null){
			return null;
		}
		RelayForm relayForm = new RelayForm();
		relayForm.setIp(imbRelay.getIp());
		relayForm.setMemo(imbRelay.getMemo());
		relayForm.setRegdate(imbRelay.getRegdate());

		return relayForm;
	}

	public int selectEditRelay(String ip) throws Exception {
		return relayMapper.selectEditRelay(ip);
	}

	/**
	 * 수신거부 계정 수정
	 *
	 * @param form
	 * @throws Exception
	 * */
	public int editRelay(RelayForm form, String ori_ip) throws Exception {
		return relayMapper.editRelay(form.getIp() ,form.getMemo(), ori_ip);
	}

	/**
	 * ip 등록
	 * @param relayForm
	 * @throws Exception
	 */
	public int insertRelay(RelayForm relayForm) throws Exception{
        
    	//ip 정보  Bean setting
		ImbRelay imbRelay = new ImbRelay();

		imbRelay.setIp(relayForm.getIp());
		if(StringUtils.isEmpty(relayForm.getMemo())){
			imbRelay.setMemo(null);
		}else {
			imbRelay.setMemo(relayForm.getMemo());
		}

		return relayMapper.insertRelay(imbRelay);
    }
	
	/**
	 * 연동IP 삭제
	 * @param ips
	 * @throws Exception 
	 */
	public int deleteRelay(HttpServletRequest request, String userid, String[] ips) throws Exception{
		int result =0;
		String ip="";
		String logParam = "";
		for (int i = 0; i < ips.length; i++) {
			String[] userinfo = ips[i].split(";");
			ip = userinfo[0].split(",")[0];
			int lenth = ips[i].split(",").length;
			result = relayMapper.deleteRelayByKey(ip);    //  연동IP 삭제
			if(i==0){
				if(lenth == 2){
					logParam= "삭제한 연동ip : " + ips[i].split(",")[0] + " / 설명 : " + ips[i].split(",")[1];
				}else{
					logParam= "삭제한 연동ip : " + ips[i].split(",")[0];
				}
			}else {
				if(lenth == 2){
					logParam += " , 삭제한 연동ip : " + ips[i].split(",")[0] + " / 설명 : " + ips[i].split(",")[1];
				} else {
					logParam += " , 삭제한 연동ip : " + ips[i].split(",")[0];
				}

			}
		}
		//log insert start
		ActionLogForm logForm = new ActionLogForm();
		logForm.setIp(HttpRequestUtil.getRemoteAddr(request));
		logForm.setUserid(userid);
		logForm.setMenu_key("H103");
		logForm.setParam(logParam);
		actionLogService.insertActionLog(logForm);
		return result;
	}



}
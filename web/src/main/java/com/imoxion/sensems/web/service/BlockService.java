package com.imoxion.sensems.web.service;

import com.imoxion.sensems.web.database.domain.ImbBlock;
import com.imoxion.sensems.web.database.domain.ImbRelay;
import com.imoxion.sensems.web.database.mapper.BlockMapper;
import com.imoxion.sensems.web.database.mapper.RelayMapper;
import com.imoxion.sensems.web.form.ActionLogForm;
import com.imoxion.sensems.web.form.BlockForm;
import com.imoxion.sensems.web.form.RelayForm;
import com.imoxion.sensems.web.util.HttpRequestUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class BlockService {

	@Autowired
	private BlockMapper blockMapper;

	@Autowired
	private ActionLogService actionLogService;
	
	protected Logger log = LoggerFactory.getLogger(BlockService.class);

	
	// 전체 수신거부 목록 취득
	public List<ImbBlock> getBlockListAll() throws Exception{
		return blockMapper.selectAllBlock();
	}

	public int getBlockCount(String srch_keyword){
		return  blockMapper.selectBlockCount(srch_keyword);
	}	

	
	public List<ImbBlock> getBlockList(String srch_keyword, int start, int end){
		return blockMapper.selectBlockList(srch_keyword, start, end);
	}

	/**
	 * 중복 ip 데이터 확인
	 * @param ip
	 * */
	public boolean isExistBlock(String ip) throws Exception{
		int count = blockMapper.isExistBlock(ip);
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
	public BlockForm getBlockInfo(String ip) throws Exception{
		ImbBlock imbBlock = blockMapper.selectBlockByKey(ip);
		if(imbBlock == null){
			return null;
		}
		BlockForm blockForm = new BlockForm();
		blockForm.setIp(imbBlock.getIp());
		blockForm.setMemo(imbBlock.getMemo());
		blockForm.setRegdate(imbBlock.getRegdate());

		return blockForm;
	}

	public int selectEditBlock(String ip) throws Exception {
		return blockMapper.selectEditBlock(ip);
	}

	/**
	 * 수신거부 계정 수정
	 *
	 * @param form
	 * @throws Exception
	 * */
	public int editBlock(BlockForm form, String ori_ip) throws Exception {
		return blockMapper.editBlock(form.getIp() ,form.getMemo(), ori_ip);
	}

	/**
	 * ip 등록
	 * @param blockForm
	 * @throws Exception
	 */
	public int insertBlock(BlockForm blockForm) throws Exception{
        
    	//ip 정보  Bean setting
		ImbBlock imbBlock = new ImbBlock();

		imbBlock.setIp(blockForm.getIp());
		if(StringUtils.isEmpty(blockForm.getMemo())){
			imbBlock.setMemo(null);
		}else {
			imbBlock.setMemo(blockForm.getMemo());
		}

		return blockMapper.insertBlock(imbBlock);
    }
	
	/**
	 * 차단IP 삭제
	 * @param ips
	 * @throws Exception 
	 */
	public int deleteBlock(HttpServletRequest request, String userid, String[] ips) throws Exception{
		int result =0;
		String ip="";
		String logParam = "";
		for (int i = 0; i < ips.length; i++) {
			String[] userinfo = ips[i].split(";");
			ip = userinfo[0].split(",")[0];
			int lenth = ips[i].split(",").length;
			result = blockMapper.deleteBlockByKey(ip);    // 차단IP 삭제
			if(i==0){
				if(lenth == 2){
					logParam= "삭제한 차단ip : " + ips[i].split(",")[0] + " / 설명 : " + ips[i].split(",")[1];
				}else{
					logParam= "삭제한 차단ip : " + ips[i].split(",")[0];
				}
			}else {
				if(lenth == 2){
					logParam += " , 삭제한 차단ip : " + ips[i].split(",")[0] + " / 설명 : " + ips[i].split(",")[1];
				} else{
					logParam += " , 삭제한 차단ip : " + ips[i].split(",")[0];
				}
			}
		}
		//log insert start
		ActionLogForm logForm = new ActionLogForm();
		logForm.setIp(HttpRequestUtil.getRemoteAddr(request));
		logForm.setUserid(userid);
		logForm.setMenu_key("H203");
		logForm.setParam(logParam);
		actionLogService.insertActionLog(logForm);
		return result;
	}

	public int getSearchBlockCount( String srch_type, String srch_keyword, String ip, String memo) throws Exception{
		return blockMapper.getSearchBlockCount(srch_type,srch_keyword,ip,memo);
	}

	public List<ImbBlock> getBlockListForPageing(String srch_type, String srch_keyword, String ip, String memo, int start , int end ) throws Exception{

		return blockMapper.getBlockListForPageing(srch_type, srch_keyword, ip, memo, start, end);
	}



	}
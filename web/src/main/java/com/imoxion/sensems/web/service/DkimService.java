package com.imoxion.sensems.web.service;

import com.imoxion.common.util.ImConfLoaderEx;
import com.imoxion.common.util.ImTimeUtil;
import com.imoxion.sensems.web.database.domain.ImbDkimInfo;
import com.imoxion.sensems.web.database.mapper.DkimMapper;
import com.imoxion.sensems.web.form.ActionLogForm;
import com.imoxion.sensems.web.util.HttpRequestUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;
import java.util.Date;
import java.util.List;

/**
 * DKIM 관리 서비스 
 * 
 * @author minideji
 *
 */
@Service
public class DkimService {
	
	private Logger logger = LoggerFactory.getLogger(DkimService.class);
	
	@Autowired
	private DkimMapper dkimMapper;

	@Autowired
	private ActionLogService actionLogService;
	
	/**
	 * dkim 목록 취득
	 * 
	 * @return
	 */
	public List<ImbDkimInfo>  getDKIMList(){
		return dkimMapper.getDKIMList();
	}

	public List<ImbDkimInfo> getDkimList(String srch_keyword, int start, int end){
		return dkimMapper.selectDkimList(srch_keyword, start, end);
	}
	public int getDkimCount(String srch_keyword){
		return  dkimMapper.selectDkimCount(srch_keyword);
	}


	/**
	 * 등록된 dkim 삭제
	 *
	 * @param domain
	 */
//	public void deleteDKIM(String domain) {
//		dkimMapper.deleteDKIM(domain);
//	}

	public int deleteDKIM(HttpServletRequest request, String userid, String[] dkim) throws Exception{
		int result =0;
		String dki="";
		String logParam = "";
		for (int i = 0; i < dkim.length; i++) {
			String[] userinfo = dkim[i].split(";");
			dki = userinfo[0];
			result = dkimMapper.deleteDkimByKey(dki);    // dkim 삭제
			if(i==0){
				logParam=dkim[i];
			}else {
				logParam += ", " + dkim[i];
			}
		}
		//log insert start
		ActionLogForm logForm = new ActionLogForm();
		logForm.setIp(HttpRequestUtil.getRemoteAddr(request));
		logForm.setUserid(userid);
		logForm.setMenu_key("G802");
		logForm.setParam("도메인 명 : " + logParam);
		actionLogService.insertActionLog(logForm);
		return result;
	}
	
	/**
	 * dkim 정보 취득
	 * 
	 * @param domain
	 * @return
	 */
	public ImbDkimInfo getDKIM(String domain) {
		return dkimMapper.getDKIM(domain);
	}
	
	/**
	 * dkim 생성 
	 * 
	 * @param domain
	 * @return
	 * @throws Exception
	 */
	public ImbDkimInfo addDKIMSinger(String domain) throws Exception {
		ImConfLoaderEx confEms = new ImConfLoaderEx("sensems.home", "sensems.xml");
		Date regdate = new Date();
		String selector = "s"+ ImTimeUtil.getDateFormat(regdate,"yyyyMMdd");
		String filename = domain + ".private.key.der";
//		File dkimPath = new File(confEms.getProfileString("sensdata", "path"));
//		if( !dkimPath.exists() ){
//			dkimPath.mkdirs();
//		}
//		Path targetFile = Paths.get(confEms.getProfileString("sensdata", "path") + File.separator + filename );
		KeyPairGenerator kpg;
		kpg = KeyPairGenerator.getInstance("RSA");
		kpg.initialize(1024);
		KeyPair kp = kpg.generateKeyPair();
		// der 파일 생성
		byte[] der = kp.getPrivate().getEncoded();
//		Files.write(targetFile, der);

		String public_key = Base64.getMimeEncoder().encodeToString(kp.getPublic().getEncoded());
		public_key = StringUtils.replace(public_key,"\r\n","");
		logger.debug(public_key);
		ImbDkimInfo dkim = new ImbDkimInfo();
		dkim.setDomain(domain);
		dkim.setSelector(selector);
		dkim.setFilename(filename);
		dkim.setPublic_key(public_key);
		dkim.setRegdate(regdate);
		dkim.setPrivate_key(der);
		dkimMapper.insertDKIM(dkim);
		return dkim;
	}
	
	
	/**
	 * DKIM 사용여부 변경.
	 * @param domain
	 * @param useSign
	 */
	public void updateUse(String domain,String useSign){
		dkimMapper.updateUseDKIM(domain,useSign);
	}

}

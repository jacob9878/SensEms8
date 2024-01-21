package com.imoxion.sensems.server.util;

import com.imoxion.common.api.beans.ImDmarcData;
import com.imoxion.common.net.ImDNSServer;
import com.imoxion.common.util.ImStringUtil;
import com.imoxion.sensems.server.smtp.ImSmtpSession;
import org.apache.commons.lang.StringUtils;
import org.apache.james.jdkim.DKIMVerifier;
import org.apache.james.jdkim.api.SignatureRecord;
import org.apache.james.jspf.executor.SPFResult;
import org.apache.james.jspf.impl.DefaultSPF;
import org.apache.james.jspf.impl.SPF;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.util.List;

public class ImSecurityFilter {
	private boolean isSPF = false;
	private boolean isDKIM = false;
	private boolean isDMARC = false;
	
	private String dkimDomain = "";

	private String dkimStr = "";
	private String spfStr = "";
	private String spfResult = "";
	private String dmarcStr = "";
	
	private String dmarcPolicy = "";
			
	private String authResult = "";
	private String defaultDomain = "";
	
	public ImSecurityFilter(){
		
	}
	
	public boolean isSPF() {
		return isSPF;
	}

	public void setSPF(boolean isSPF) {
		this.isSPF = isSPF;
	}

	public boolean isDKIM() {
		return isDKIM;
	}

	public void setDKIM(boolean isDKIM) {
		this.isDKIM = isDKIM;
	}

	public boolean isDMARC() {
		return isDMARC;
	}

	public void setDMARC(boolean isDMARC) {
		this.isDMARC = isDMARC;
	}

	public String getDkimDomain() {
		return dkimDomain;
	}

	public void setDkimDomain(String dkimDomain) {
		this.dkimDomain = dkimDomain;
	}

	public String getDkimStr() {
		return dkimStr;
	}

	public void setDkimStr(String dkimStr) {
		this.dkimStr = dkimStr;
	}

	public String getSpfStr() {
		return spfStr;
	}
	public String getspfResult(){
		return spfResult;
	}

	public void setSpfStr(String spfStr) {
		this.spfStr = spfStr;
	}

	public String getDmarcStr() {
		return dmarcStr;
	}

	public void setDmarcStr(String dmarcStr) {
		this.dmarcStr = dmarcStr;
	}

	public String getDmarcPolicy() {
		return dmarcPolicy;
	}

	public void setDmarcPolicy(String dmarcPolicy) {
		this.dmarcPolicy = dmarcPolicy;
	}

	public void setAuthResult(String authResult) {
		this.authResult = authResult;
	}

	public String getDefaultDomain() {
		return defaultDomain;
	}

	public void setDefaultDomain(String defaultDomain) {
		this.defaultDomain = defaultDomain;
	}

	public String getAuthResult(){
		String res = "";

		if(StringUtils.isEmpty(spfStr) && StringUtils.isEmpty(dkimStr) && StringUtils.isEmpty(dmarcStr)){
			return res;
		}

		res = "Authentication-Results: "+defaultDomain;
		
		if(spfStr != null && !spfStr.equals("")){
			res += ";\r\n\t"+spfStr;
		}

		if(dkimStr != null && !dkimStr.equals("")){
			res += ";\r\n\t"+dkimStr;
		}
		
		if(dmarcStr != null && !dmarcStr.equals("")){
			res += ";\r\n\t"+dmarcStr;
		}
		
		authResult = res;
		
		return res;
	}
	
	/**
	 * DMARC 레크드를 조회한다.
	 * 
	 * @param ids DNS 조회 서버 객체
	 * @param domain 조회 도메인 명
	 * @return DMARC 정보 객체
	 * @throws Exception
	 */
	public ImDmarcData lookupDmarc(ImDNSServer ids, String domain) throws Exception{
		return ids.lookupDMARC(domain);
	}

	/**
	 * SPF 조회를 한다.
	 * fromEmail은 smtp.mailfrom
	 */
	public String verifySPF(String fromIP,String fromEmail){
		SPF spf = null;
		String res = null;
		Level lv = Level.INFO;
		try {
			//Log4JLogger log4jlogger = new Log4JLogger(logger);
			lv = Logger.getRootLogger().getLevel();
			//System.out.println("level: " + lv);
			Logger.getRootLogger().setLevel(Level.ERROR);
			spf = new DefaultSPF();

			String sDomain = "";
			try {
				sDomain = fromEmail.substring(fromEmail.indexOf("@") + 1);
			} catch (Exception e){
				sDomain = fromEmail;
			}

			// From IP 정보가 있으면 그걸 이용하고 없으면 Peer IP 정보를 이용한다.
			SPFResult spfResult = null;
			if(fromIP != null && !fromIP.equals("")){
				//res = spf.checkSPF(fromIP, fromEmail, sDomain).getHeaderText();
				spfResult = spf.checkSPF(fromIP, fromEmail, sDomain);
				res = spfResult.getResult();
				this.spfResult = res;
			}

			//ImLoggerEx.info("SMTP", "verifySPF: {} - {} / {} / {}", res, fromIP, fromEmail, sDomain);
			//System.out.println("verifySPF: "+ res+" / "+ fromIP+" / "+ fromEmail+" / "+ sDomain);

			if(res != null){
				if("pass".equals(res)) {
					res = "spf=" + res + " (spfCheck: domain of " + fromEmail + " designates " + fromIP + " as permitted sender) smtp.mailfrom=" + fromEmail;
				}else if("fail".equals(res)) {
					res = "spf=" + res + " (spfCheck: domain of " + fromEmail + " does not designate " + fromIP + " as permitted sender)";
				}else if("softfail".equals(res)) {
					res = "spf=" + res + " (spfCheck: transitioning domain of " + fromEmail + " does not designate " + fromIP + " as permitted sender)";
				}else if("permerror".equals(res)) {
					res = "spf=" + res + " (spfCheck: Error in processing SPF Record)";
				}else if("temperror".equals(res)) {
					res = "spf=" + res + " (spfCheck: Error in retrieving data from DNS)";
				}else{
					res = "spf=" + res + " (spfCheck: " + fromIP + " is neither permitted nor denied by domain of " + fromEmail + ") smtp.mailfrom="+fromEmail;
				}
			}else{
				res = "spf=none (spfCheck: " + fromIP + " is neither permitted nor denied by domain of " + fromEmail + ") smtp.mailfrom="+fromEmail;
			}
		} catch(Exception e){
			e.printStackTrace();
			res = "spf=none (spfCheck: " + fromIP + " is neither permitted nor denied by domain of " + fromEmail + ") smtp.mailfrom="+fromEmail;
		}finally{
			Logger.getRootLogger().setLevel(lv);
		}

		spfStr = res; 
		
		return res;
	}


	/**
	 * DKIM 정보를 조회한다.
	 * 
	 * @param msgPath 메시지 경로
	 * @return DKIM 조회 결과 문자열
	 * @throws Exception
	 */
	public String verifyDKIM(String msgPath) throws Exception{
		String rootDomain = null;
		FileInputStream fis = null;
		String ret = "";

		try{
			DKIMVerifier ver = new DKIMVerifier();

			fis= new FileInputStream(msgPath);
			List<SignatureRecord> listSR = ver.verify(fis);

			if(listSR != null){
				for(SignatureRecord sr : listSR){
					rootDomain = sr.getDToken().toString();
					dkimDomain = rootDomain;
				}
			}
			
			if(rootDomain != null && !rootDomain.equals("")){
				ret = "dkim=pass (good signature) header.d="+rootDomain;
			}else{
				ret = "dkim=none header.d="+rootDomain;
			}
		}catch(Exception ex){
			ret = "dkim=fail header.d="+rootDomain;
			throw ex;
		}finally{
			if(fis != null)
				fis.close();
		}

		dkimStr = ret;
		
		return ret;
	}
	
	/**
	 * DMARC 내용을 체크한다.
	 * 
	 * @param ids DNS 서버 조회 객체
	 * @param headerFrom 헤더의 from 이메일 주소
	 * @param sd SMTP 전송 세션 객체
	 * @param msgPath 메시지 경로
	 * @throws Exception
	 */
	public void verifyDMARC(ImDNSServer ids, String headerFrom, ImSmtpSendingInfo sd, String msgPath) throws Exception{
		String headerFromDomain = "";
		String mailFromDomain = "";
		String dkimDomain = "";
//		boolean isDKIM = false;
//		boolean isSPF = false;
		
//		boolean isDMARC = false;
		boolean isDMARCtoDKIM = false;
		boolean isDMARCtoSPF = false;
		
		// 헤더 from 도메인을 뽑아온다.
    	// <>를 제거한다.
    	String emailTemp = ImStringUtil.getStringBetween(headerFrom, "<", ">");
		String[] arrFrom = ImStringUtil.getTokenizedString(emailTemp, "@");
		if(arrFrom != null && arrFrom.length > 1){
			headerFromDomain = arrFrom[1];
			defaultDomain = headerFromDomain;
		}
		
    	String tempMailFrom = ImStringUtil.getStringBetween(sd.getFrom(), "<", ">");
		String[] arrMailFrom = ImStringUtil.getTokenizedString(tempMailFrom, "@");
		if(arrMailFrom != null && arrMailFrom.length > 1){
			mailFromDomain = arrMailFrom[1];
		}
		// DMARC 레코드를 조회한다.
		ImDmarcData dd = null;
		
		try{
			dd= ids.lookupDMARC(headerFromDomain);
		}catch(Exception e){}
		
		if(dd != null){
			dmarcPolicy = dd.getPolicy();
		}else{
			// 조회 실패
			dmarcStr = "dmarc=none header.from="+headerFromDomain;
		}
		
		// DKIM 유효성을 조회한다.
		String retDKIM = verifyDKIM(msgPath);	
		
		if(retDKIM.startsWith("dkim=pass") ){
			// DKIM 성공
			isDKIM = true;
			int n = retDKIM.lastIndexOf("header.d=");
			if(n >= 0){
				dkimDomain = retDKIM.substring(n+9);
			}
			
		}
		
		// SPF 유효성 체크
		String retSPF = verifySPF(sd.getFromIP(),tempMailFrom);
		if(retSPF.startsWith("spf=pass") ){
			// DKIM 성공
			isSPF = true;
		}
		
		if(dd != null && isDKIM && isSPF){
			if(dd.getAignmentDKIM().equalsIgnoreCase("R")){
				// DKIM 이 relax mode 일때  결과 값에 도메인이 포함되어 있으면 성공
				if(dkimDomain.contains(headerFromDomain)){
					// 성공
					isDMARCtoDKIM = true;
				}
			}else{
				// DKIM 이 relax mode 일때  결과 값에 도메인이 일치해야 성공
				if(dkimDomain.equalsIgnoreCase(headerFromDomain)){
					// 성공
					isDMARCtoDKIM = true;
				}
				
			}
			
			if(dd.getAlignmentSPF().equalsIgnoreCase("R")){
				// SPF 가 relax mode 일때
				if(mailFromDomain.contains(headerFromDomain)){
					isDMARCtoSPF = true;
				}
			}else{
				if(mailFromDomain.equalsIgnoreCase(headerFromDomain)){
					isDMARCtoSPF = true;
				}
			}
			
		}
		if(dd != null){
			// DMARC 판정
			if( isDMARCtoDKIM && isDMARCtoSPF){
				isDMARC = true;
				dmarcStr = "dmarc=pass (p="+dd.getPolicy()+") header.from="+headerFromDomain;
			}else{
				dmarcStr = "dmarc=fail (p="+dd.getPolicy()+") header.from="+headerFromDomain;
			}
			
		}
	}

	public void verifyDMARC(ImDNSServer ids, String headerFrom, ImSmtpSendingInfo sd, String msgPath, ImSmtpSession smtps) throws Exception{
		String headerFromDomain = "";
		String mailFromDomain = "";
		String dkimDomain = "";
//		boolean isDKIM = false;
//		boolean isSPF = false;

//		boolean isDMARC = false;
		boolean isDMARCtoDKIM = false;
		boolean isDMARCtoSPF = false;

		// 헤더 from 도메인을 뽑아온다.
		// <>를 제거한다.
		String emailTemp = ImStringUtil.getStringBetween(headerFrom, "<", ">");
		String[] arrFrom = ImStringUtil.getTokenizedString(emailTemp, "@");
		if(arrFrom != null && arrFrom.length > 1){
			headerFromDomain = arrFrom[1];
			defaultDomain = headerFromDomain;
		}

		String tempMailFrom = ImStringUtil.getStringBetween(sd.getFrom(), "<", ">");
		String[] arrMailFrom = ImStringUtil.getTokenizedString(tempMailFrom, "@");
		if(arrMailFrom != null && arrMailFrom.length > 1){
			mailFromDomain = arrMailFrom[1];
		}
		// DMARC 레코드를 조회한다.
		ImDmarcData dd = null;

		try{
			dd= ids.lookupDMARC(headerFromDomain);
		}catch(Exception e){}

		if(dd != null){
			dmarcPolicy = dd.getPolicy();
		}else{
			// 조회 실패
			dmarcStr = "dmarc=none header.from="+headerFromDomain;
		}

		// DKIM 유효성을 조회한다.
		String retDKIM = verifyDKIM(msgPath);

		if(retDKIM.startsWith("dkim=pass") ){
			// DKIM 성공
			isDKIM = true;
			int n = retDKIM.lastIndexOf("header.d=");
			if(n >= 0){
				dkimDomain = retDKIM.substring(n+9);
			}

		}

		// SPF 유효성 체크
		// gmail은 smtp.mailfrom 을 기준으로 하고 있음
		String retSPF = verifySPF(sd.getFromIP(), smtps.getFrom());
		if(retSPF.startsWith("spf=pass") ){
			// DKIM 성공
			isSPF = true;
		}

		if(dd != null && isDKIM && isSPF){
			if(dd.getAignmentDKIM().equalsIgnoreCase("R")){
				// DKIM 이 relax mode 일때  결과 값에 도메인이 포함되어 있으면 성공
				if(dkimDomain.contains(headerFromDomain)){
					// 성공
					isDMARCtoDKIM = true;
				}
			}else{
				// DKIM 이 relax mode 일때  결과 값에 도메인이 일치해야 성공
				if(dkimDomain.equalsIgnoreCase(headerFromDomain)){
					// 성공
					isDMARCtoDKIM = true;
				}

			}

			if(dd.getAlignmentSPF().equalsIgnoreCase("R")){
				// SPF 가 relax mode 일때
				if(mailFromDomain.contains(headerFromDomain)){
					isDMARCtoSPF = true;
				}
			}else{
				if(mailFromDomain.equalsIgnoreCase(headerFromDomain)){
					isDMARCtoSPF = true;
				}
			}

		}
		if(dd != null){
			// DMARC 판정
			if( isDMARCtoDKIM && isDMARCtoSPF){
				isDMARC = true;
				dmarcStr = "dmarc=pass (p="+dd.getPolicy()+") header.from="+headerFromDomain;
			}else{
				dmarcStr = "dmarc=fail (p="+dd.getPolicy()+") header.from="+headerFromDomain;
			}

		}
	}

	public void verifyDMARC(ImDNSServer ids, String headerFrom, ImSmtpSendData sd, String msgPath) throws Exception{
		String headerFromDomain = "";
		String mailFromDomain = "";
		String dkimDomain = "";
//		boolean isDKIM = false;
//		boolean isSPF = false;

//		boolean isDMARC = false;
		boolean isDMARCtoDKIM = false;
		boolean isDMARCtoSPF = false;

		// 헤더 from 도메인을 뽑아온다.
		// <>를 제거한다.
		String emailTemp = ImStringUtil.getStringBetween(headerFrom, "<", ">");
		String[] arrFrom = ImStringUtil.getTokenizedString(emailTemp, "@");
		if(arrFrom != null && arrFrom.length > 1){
			headerFromDomain = arrFrom[1];
			defaultDomain = headerFromDomain;
		}

		String tempMailFrom = ImStringUtil.getStringBetween(sd.getFrom(), "<", ">");
		String[] arrMailFrom = ImStringUtil.getTokenizedString(tempMailFrom, "@");
		if(arrMailFrom != null && arrMailFrom.length > 1){
			mailFromDomain = arrMailFrom[1];
		}
		// DMARC 레코드를 조회한다.
		ImDmarcData dd = null;

		try{
			dd= ids.lookupDMARC(headerFromDomain);
		}catch(Exception e){}

		if(dd != null){
			dmarcPolicy = dd.getPolicy();
		}else{
			// 조회 실패
			dmarcStr = "dmarc=none header.from="+headerFromDomain;
		}

		// DKIM 유효성을 조회한다.
		String retDKIM = verifyDKIM(msgPath);

		if(retDKIM.startsWith("dkim=pass") ){
			// DKIM 성공
			isDKIM = true;
			int n = retDKIM.lastIndexOf("header.d=");
			if(n >= 0){
				dkimDomain = retDKIM.substring(n+9);
			}

		}

		// SPF 유효성 체크
		String retSPF = verifySPF(sd.getFromIP(),tempMailFrom);
		if(retSPF.startsWith("spf=pass") ){
			// DKIM 성공
			isSPF = true;
		}

		if(dd != null && isDKIM && isSPF){
			if(dd.getAignmentDKIM().equalsIgnoreCase("R")){
				// DKIM 이 relax mode 일때  결과 값에 도메인이 포함되어 있으면 성공
				if(dkimDomain.contains(headerFromDomain)){
					// 성공
					isDMARCtoDKIM = true;
				}
			}else{
				// DKIM 이 relax mode 일때  결과 값에 도메인이 일치해야 성공
				if(dkimDomain.equalsIgnoreCase(headerFromDomain)){
					// 성공
					isDMARCtoDKIM = true;
				}

			}

			if(dd.getAlignmentSPF().equalsIgnoreCase("R")){
				// SPF 가 relax mode 일때
				if(mailFromDomain.contains(headerFromDomain)){
					isDMARCtoSPF = true;
				}
			}else{
				if(mailFromDomain.equalsIgnoreCase(headerFromDomain)){
					isDMARCtoSPF = true;
				}
			}

		}
		if(dd != null){
			// DMARC 판정
			if( isDMARCtoDKIM && isDMARCtoSPF){
				isDMARC = true;
				dmarcStr = "dmarc=pass (p="+dd.getPolicy()+") header.from="+headerFromDomain;
			}else{
				dmarcStr = "dmarc=fail (p="+dd.getPolicy()+") header.from="+headerFromDomain;
			}

		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ImSecurityFilter sf = new ImSecurityFilter();
		try{
			ImDNSServer ids = new ImDNSServer("8.8.8.8");
			sf.setDefaultDomain("mx.imoxion.com");
			ImSmtpSendData sd = new ImSmtpSendData();
			sd.setFrom("jungyc@gmail.com");
			sd.setPeerIP("209.85.161.176");
//			System.out.println(sf.verifyDKIM("D:\\apps\\test\\test.eml"));
			System.out.println(sf.getAuthResult());
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

}

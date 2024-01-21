package com.imoxion.sensems.server.util;

import java.util.ArrayList;
import java.util.List;

import com.imoxion.sensems.server.beans.ErrorCode;
import com.imoxion.sensems.server.environment.SensEmsEnvironment;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 에러 스트링에서 코드를 취득하여 반송사유 및 해결방안이 담긴 데이터를 제공한다.
 *
 * Created by sunggyu on 2015-10-29.
 */
public class ReturnMailCodeMatcher {
	private static Logger smailLogger = LoggerFactory.getLogger("SMAIL");
	private static Logger smtpLogger = LoggerFactory.getLogger("SMTP");

	private static ReturnMailCodeMatcher returnMailCodeMatcher = null;
	private List<ErrorCode> errorCodeList = null;

	public static ReturnMailCodeMatcher getInstance(){
		if( returnMailCodeMatcher == null ){
			returnMailCodeMatcher = new ReturnMailCodeMatcher();
		}
		return returnMailCodeMatcher;
	}

	private ReturnMailCodeMatcher(){
		String errcodeListPath = SensEmsEnvironment.getSensEmsServerHome()+"/conf/errorcode.xml";
		try {
			this.errorCodeList = this.loadErrorCodeList(errcodeListPath);
		}catch(Exception e) {
			smailLogger.error("loadErrorCodeList error : {}" , e );
		}
	}

	private List<ErrorCode> loadErrorCodeList(String confFilePath) throws Exception{
        XMLConfiguration configuration = new XMLConfiguration(confFilePath);
        List<ErrorCode> errorCodeList = new ArrayList<>();
        List<HierarchicalConfiguration> errorList = configuration.configurationsAt("errors.error");
        for(HierarchicalConfiguration error : errorList){
            String code = (String) error.getProperty("code");
            String detail_code = (String) error.getProperty("detail_code");
            String description = (String) error.getProperty("description");
            String eng_description = (String) error.getProperty("eng_description");
            String response = (String) error.getProperty("response");
            ErrorCode errCode = new ErrorCode();
            errCode.setCode(code);
            errCode.setDetail_code(detail_code);
            errCode.setDescription(description);
            errCode.setEng_description(eng_description);
            errCode.setResponse(response);
            
            errorCodeList.add(errCode);
            smailLogger.debug("loadErrorCodeList : {} {}" , errCode.getCode(), errCode.getDetail_code()  );
        }
        return errorCodeList;
    }
	
	/**
	 * 반송사유 에러 스트링에서 코드를 비교하여 반송사유,해결방안을 제공한다.
	 * @param sErrString
	 * @return
	 */
	public ErrorCode matcher(String sErrString){
		//smailLogger.debug("matcher sErrString : {}" , sErrString);
		if( StringUtils.isNotEmpty(sErrString) ){
			boolean thisCode = false;
			if (this.errorCodeList != null) {
				//smailLogger.debug("errorCodeList is not null");
				for (ErrorCode code : errorCodeList) {
					//smailLogger.debug("match code? {} - {}", code.getCode(), sErrString.indexOf(code.getCode()));
					if (sErrString.indexOf(code.getCode()) > -1) {
						//smailLogger.debug("matcher code : {}" , code.getCode() );
						String errorCodeDetail = code.getDetail_code();
						
						// 상세코드가 없는 리터메일 데이터는 항상 마지막이기 때문에 오면 바로 해당값을 보여준다.
						if (StringUtils.isNotEmpty(errorCodeDetail)) {
							//smailLogger.debug("matcher code_detail : {}" , errorCodeDetail  );
							if (sErrString.indexOf(errorCodeDetail) >= 0) {
								if(StringUtils.isNotEmpty(code.getResponse())) {
									String[] responses = code.getResponse().split(",");
									for (String reponse : responses) {
										if (sErrString.toLowerCase().indexOf(reponse.toLowerCase()) > -1) {
											return code;
										}
									}
								} else {
									return code;
								}
							}
						} else {
							if(StringUtils.isNotEmpty(code.getResponse())) {
								String[] responses = code.getResponse().split(",");
								for (String reponse : responses) {
									//smailLogger.debug("matcher response : {}" , reponse  );
									if (sErrString.toLowerCase().indexOf(reponse.toLowerCase()) > -1) {
										return code;
									}
								}
							} else {
								return code;
							}
						}
					}
				}				
			} else {
				//smailLogger.debug("errorCodeList is null");
			}
		}
		//smailLogger.debug("matcher not match sErrString : {}" , sErrString  );
		// 검색된 코드가 없을 경우 일반적인 실패메세지를 보여준다.
		ErrorCode code = new ErrorCode();
		code.setDescription("아래와 같은 이유로 메일 발송에 실패했습니다. 수신자 이메일주소가 정확한지 확인 후 다시 시도해보세요.");
		
		return code;
	}
}

package com.imoxion.sensems.web.module.passwordpolicy;

import org.passay.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.*;

@Service
public class PasswordPoricyService {

	private final Logger log = LoggerFactory.getLogger(getClass());
	
	public Map<String,PasswordRequired> isValidPassword(String mailid, String password, String language){

		ResourceBundle resourceBundle = ResourceBundle.getBundle("messages.password_rule",new Locale(language) );
		
		MessageResolver resolver = new ResourceBundleMessageResolver(resourceBundle);

		List<Rule> ruleList = new ArrayList<>();
		
		ruleList.add(new LengthRule(8,30));
		ruleList.add(new HistoryRule());
		ruleList.add(new UsernameRule());
		ruleList.add(new RepeatCharacterRegexRule(4)); //반복적인 문자
		ruleList.add(new WhitespaceRule()); //공백 문자 방지
		
		CharacterCharacteristicsRule characterCharacteristicsRule = new CharacterCharacteristicsRule();
		characterCharacteristicsRule.getRules().add(new CharacterRule(EnglishCharacterData.Alphabetical, 1)); // 알파멧 1개이상 포함
		characterCharacteristicsRule.getRules().add(new CharacterRule(EnglishCharacterData.Special, 1)); // 특수문자 1개이상 포함
		characterCharacteristicsRule.getRules().add(new CharacterRule(EnglishCharacterData.Digit, 1)); // 최소 숫자 1개이상 포함
		
		if(10 <= password.length()){
			characterCharacteristicsRule.setNumberOfCharacteristics(2);
		}else{
			characterCharacteristicsRule.setNumberOfCharacteristics(3);
		}
		
		ruleList.add(characterCharacteristicsRule);
		PasswordValidator validator = new PasswordValidator(ruleList);
		
		PasswordData passwordData = new PasswordData(password);
		
		passwordData.setUsername(mailid);
		
		RuleResult result = validator.validate(passwordData);
		
		if( !result.isValid() ){
			Map<String,PasswordRequired> passwordRequiredMap = new HashMap<>();
			for(RuleResultDetail detail : result.getDetails()){
				if( detail.getErrorCode().equalsIgnoreCase("INSUFFICIENT_CHARACTERISTICS") ||
					detail.getErrorCode().equalsIgnoreCase("ILLEGAL_USERNAME") ||
					detail.getErrorCode().equalsIgnoreCase("ILLEGAL_WHITESPACE")) {
					passwordRequiredMap.put(detail.getErrorCode(), new PasswordRequired( true, resolver.resolve(detail) ) );
				}else if(detail.getErrorCode().equalsIgnoreCase("ILLEGAL_MATCH")){
					Map<String,Object> paramMap = detail.getParameters();
					paramMap.put("match", 4);
					detail = new RuleResultDetail(detail.getErrorCode(), paramMap);
					passwordRequiredMap.put(detail.getErrorCode(), new PasswordRequired( true, resolver.resolve(detail) ) );
				}else if(detail.getErrorCode().equalsIgnoreCase("TOO_LONG")||detail.getErrorCode().equalsIgnoreCase("TOO_SHORT")){
					
					passwordRequiredMap.put("INSUFFICIENT_CHARACTERISTICS", new PasswordRequired(true, MessageFormat.format( resourceBundle.getString("INSUFFICIENT_CHARACTERISTICS"),null )));
				}
			}
			return passwordRequiredMap;
		}
		return null;
	}
	
	public Map<String,PasswordRequired> getPasswordRequired(String language){

		ResourceBundle resourceBundle = ResourceBundle.getBundle("messages.password_rule",new Locale(language) );
		Map<String,PasswordRequired> passwordRequiredList = new LinkedHashMap<>();

		passwordRequiredList.put("INSUFFICIENT_CHARACTERISTICS", new PasswordRequired(false,MessageFormat.format( resourceBundle.getString("INSUFFICIENT_CHARACTERISTICS"),null ) ));
        passwordRequiredList.put("ILLEGAL_USERNAME", new PasswordRequired(false, MessageFormat.format( resourceBundle.getString("ILLEGAL_USERNAME"),null )));
	    passwordRequiredList.put("HISTORY_VIOLATION", new PasswordRequired(false, MessageFormat.format( resourceBundle.getString("HISTORY_VIOLATION"),null )));
		passwordRequiredList.put("ILLEGAL_MATCH", new PasswordRequired(false,MessageFormat.format( resourceBundle.getString("ILLEGAL_MATCH"),4 ) ));
		passwordRequiredList.put("ILLEGAL_WHITESPACE", new PasswordRequired(false, MessageFormat.format(resourceBundle.getString("ILLEGAL_WHITESPACE"),null )));
		
		
		
		return passwordRequiredList;
	}
	
	public Map<String,PasswordRequired> getRequiredBean(String key,String language){
		ResourceBundle resourceBundle = ResourceBundle.getBundle("messages.password_rule",new Locale(language) );
		Map<String,PasswordRequired> passwordRequiredList = new LinkedHashMap<>();
		passwordRequiredList.put(key, new PasswordRequired(true,MessageFormat.format( resourceBundle.getString(key),null ) ));
		return passwordRequiredList;
	}
	
}

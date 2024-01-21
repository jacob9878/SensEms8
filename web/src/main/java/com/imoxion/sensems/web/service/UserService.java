package com.imoxion.sensems.web.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.imoxion.common.util.ImCheckUtil;
import com.imoxion.common.util.ImUtils;
import com.imoxion.security.ImSecurityLib;
import com.imoxion.sensems.web.beans.UserInfoBean;
import com.imoxion.sensems.web.common.ImbConstant;
import com.imoxion.sensems.web.database.mapper.AddressMapper;
import com.imoxion.sensems.web.form.ActionLogForm;
import com.imoxion.sensems.web.form.UserForm;
import com.imoxion.sensems.web.module.passwordpolicy.PasswordPoricyService;
import com.imoxion.sensems.web.module.passwordpolicy.PasswordRequired;
import com.imoxion.sensems.web.util.HttpRequestUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;

import com.imoxion.sensems.web.database.domain.ImbUserinfo;
import com.imoxion.sensems.web.database.mapper.UserMapper;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

import javax.servlet.http.HttpServletRequest;


/**
 * 사용자 관리 Service
 * @date 2021.02.01
 * @author jhpark
 *
 */
@Service
public class UserService {

	protected Logger log = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private PasswordPoricyService passwordPolicyService;

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private AddressMapper addressMapper;

	@Autowired
	private MessageSourceAccessor message;

	@Autowired
	private ActionLogService actionLogService;

	/**
	 * userid를 이용한 사용자 정보 획득
	 * @param userId
	 * @return
	 */
	public ImbUserinfo getUserInfo(String userId) throws Exception {
		ImbUserinfo userInfo = userMapper.getUserInfo(userId);
		if(ImbConstant.DATABASE_ENCRYPTION_USE){
			decryptUserinfo(userInfo);
		}

		return userInfo;
	}

	/**
	 * 로그인 실패 횟수 증가
	 * @param userInfo
	 */
	public void updateFailLoginCount(ImbUserinfo userInfo ) throws Exception {
		int currentFailCount = userInfo.getFail_login();
		//패스워드 검증 실패응 하면 실패 카운트 증가
		userMapper.updateFailLoginCount( userInfo.getUserid(),currentFailCount+1, new Date() );
	}

	/**
	 * 사용자 리스트 페이징해서 가져온다.
	 * @param srch_type
	 * @param srch_keyword
	 * @param permission
	 * @param isStop
	 * @param start
	 * @param end
	 * @return
	 * @throws Exception
	 */
	public List<ImbUserinfo> getUserListForPageing( String srch_type, String srch_keyword, String permission, String isStop,String use_smtp, int start , int end ) throws Exception{
		List<ImbUserinfo> userinfoList = userMapper.getUserListForPageing(srch_type,srch_keyword,permission,isStop,use_smtp, start, end);

		if(ImbConstant.DATABASE_ENCRYPTION_USE) {
			for (ImbUserinfo userinfo : userinfoList) {
				decryptUserinfo(userinfo);
			}
		}
		return userinfoList;
	}

	/**
	 * 사용자 목록 총 수를 구한다.
	 * @param srch_type
	 * @param srch_keyword
	 * @param permission
	 * @param isStop
	 * @return
	 * @throws Exception
	 */
	public int getUserCount( String srch_type, String srch_keyword, String permission, String isStop , String use_smtp) throws Exception{
		return userMapper.getUserCount(srch_type,srch_keyword,permission,isStop, use_smtp);
	}


	/**
	 * 세션에 저장할 UserInfoBean 획득
	 * @param userinfo
	 * @param language
	 * @return
	 */
	public UserInfoBean getUserSessionInfo(ImbUserinfo userinfo,String language){
		UserInfoBean userSessionInfo = new UserInfoBean();
		userSessionInfo.setUserid(userinfo.getUserid());
		userSessionInfo.setLanguage(language);
		userSessionInfo.setEmail(userinfo.getEmail());
		userSessionInfo.setApprove_email(userinfo.getApprove_email());
		userSessionInfo.setPermission(userinfo.getPermission());
		userSessionInfo.setName(userinfo.getUname());


		return userSessionInfo;
	}
	/**
	 * 아이디 중복 체크
	 * 2023-01-31 아이디 특수문자 체크와 중복체크 분리
	 * 특수문자 체크는 ImUtility.validCharacter()로 진행
	 * @param userid
	 * @return
	 */
    public boolean idCheck(String userid) throws Exception{
		/*if (!isValidId(userid)) {
			return false;
		}*/

		if(userMapper.isExistUser(userid) > 0){
			return false;
		}
		return true;
    }

    public boolean existUser(String userid) throws Exception {
		if(userMapper.isExistUser(userid) > 0){
			return false;
		}
		return true;
	}

	/**
	 * 웹페이지로부터 받은 값 UserInfo에 담는 처리
	 * @param form
	 * @throws Exception
	 */
    @Transactional(isolation = Isolation.READ_COMMITTED)
	public void addUser(UserForm form) throws Exception {
		String userid = form.getUserid();
		// 필수 항목 암호화 대상 : password , email
		// 필수 항목이 아닌 암호화 대상 : tel , mobile , approve_email
		String secret_key = ImbConstant.DATABASE_AES_KEY;
		String password = ImSecurityLib.makePassword(ImbConstant.PASS_SECU_TYPE,form.getPasswd(),false);
		String email = ImSecurityLib.encryptAES256(secret_key, form.getEmail());

		ImbUserinfo userinfo = new ImbUserinfo();
		userinfo.setUserid(userid);
		userinfo.setPasswd(password);
		userinfo.setPwd_type(ImbConstant.PASS_SECU_TYPE);
		userinfo.setUname(form.getUname());
		userinfo.setEmail(email);
		userinfo.setDept(form.getDept());
		userinfo.setGrade(form.getGrade());
		userinfo.setTel(form.getTel());
		userinfo.setMobile(form.getMobile());
		userinfo.setPermission(form.getPermission());
		userinfo.setAccess_ip(form.getAccess_ip());
		userinfo.setApprove_email(form.getApprove_email());
		userinfo.setUse_smtp(form.getUse_smtp());

		if(StringUtils.isNotEmpty(form.getTel())){ // 전화번호가 null이 아니면 암호화 진행
			String enTel = ImSecurityLib.encryptAES256(secret_key, form.getTel());
			userinfo.setTel(enTel);
		}
		if(StringUtils.isNotEmpty(form.getMobile())){ // 휴대폰번호가 null이 아니면 암호화 진행
			String enMobile = ImSecurityLib.encryptAES256(secret_key, form.getMobile());
			userinfo.setMobile(enMobile);
		}
		if(StringUtils.isNotEmpty(form.getApprove_email())){ // 승인메일이 null이 아니면 암호화 진행
			String enApmail = ImSecurityLib.encryptAES256(secret_key, form.getApprove_email());
			userinfo.setApprove_email(enApmail);
		}

		insertUser(userinfo);
		addressMapper.createAddrGrpTable(userinfo.getUserid());
		addressMapper.createAddrTable(userinfo.getUserid());
		addressMapper.createTagInsertTrigger(userinfo.getUserid());
        addressMapper.createTagDeleteTrigger(userinfo.getUserid());
        addressMapper.createTagUpdateTrigger(userinfo.getUserid());
		log.debug("Insert UserInfo userid - {} / uname - {}",userid,form.getUname());

	}

	/**
	 * 전달 받은 UserInfo의 기본값 세팅 및 추가 실시
	 * @param userinfo
	 * @throws Exception
	 */
    public void insertUser(ImbUserinfo userinfo) throws Exception {

		userinfo.setIsstop("0");
		userMapper.insertUser(userinfo);

	}
	/**
     * 아이디 유효성 검사
	 * @param userid 해당 아이디
	 * @return
	 */
	public static boolean isValidId(String userid) {
		char cUser;
		int i = 0;
		int nFlag = 0;

		if (StringUtils.isEmpty(userid)) {
			return false;
		}

		for (i = 0; i < userid.length(); i++) {
			cUser = userid.charAt(i);
			if (!((cUser >= 48 && cUser <= 57) || (cUser >= 41 && cUser <= 90) || (cUser >= 97 && cUser <= 122) || cUser == 45 || cUser == 95)) {
				nFlag = 1;
			}
		}

		if (userid.indexOf("'") != -1 || userid.indexOf('"') != -1 || userid.indexOf('@') != -1 || userid.indexOf(',') != -1
				|| userid.indexOf('?') != -1 || userid.indexOf('<') != -1 || userid.indexOf('>') != -1 || userid.indexOf(';') != -1
				|| userid.indexOf(':') != -1 || userid.indexOf('/') != -1 || userid.indexOf('(') != -1 || userid.indexOf(')') != -1
				|| userid.indexOf('+') != -1 || userid.indexOf('|') != -1 || userid.indexOf('\\') != -1 || userid.indexOf('*') != -1
				|| userid.indexOf('&') != -1 || userid.indexOf('^') != -1 || userid.indexOf('%') != -1 || userid.indexOf('$') != -1
				|| userid.indexOf('!') != -1 || userid.indexOf('~') != -1 || userid.indexOf('#') != -1 || userid.indexOf('=') != -1
				|| userid.indexOf('`') != -1) {
			nFlag = 1;
		}

		if (nFlag == 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 폼데이터 유효성 체크
	 * @param form
	 * @param model
	 * @param isAdd 추가일 경우 true, 수정일 경우 false
	 * @return
	 */
	public boolean isValidate(UserForm form, ModelMap model,boolean isAdd,
							  Map<String,PasswordRequired> passwordRequiredMap,String language) {
		//아이디 검사
		if(StringUtils.isEmpty(form.getUserid())){
			model.addAttribute("infoMessage" ,message.getMessage("E0053","아이디를 입력해 주세요."));
			return false;
		}

		if(isAdd){
			//패스워드 검사
			if( StringUtils.isEmpty(form.getPasswd()) || StringUtils.isEmpty(form.getPasswd_confirm()) ) {
				model.addAttribute("infoMessage" ,message.getMessage("E0048","비밀번호를 입력해주세요."));
				return false;
			}else if( !form.getPasswd().equals(form.getPasswd_confirm()) ) {
				model.addAttribute("infoMessage" ,message.getMessage("E0049","비밀번호가 일치하지 않습니다."));
				return false;
			}else{
				Map<String, PasswordRequired> messageList = passwordPolicyService.isValidPassword(form.getUserid(),form.getPasswd(),language);
				if(messageList != null){
					for(Map.Entry<String,PasswordRequired> passwordRequiredEntry : messageList.entrySet() ){
						passwordRequiredMap.put( passwordRequiredEntry.getKey(), passwordRequiredEntry.getValue() );
					}
					model.addAttribute("passwordRequired",passwordRequiredMap.values());
					return false;
				}
			}
		}
		if(StringUtils.isEmpty(form.getUname())){
			model.addAttribute("infoMessage",message.getMessage("E0354","이름을 입력해 주세요."));
			return false;
		}
		//특수문자 검사
		String regex = "^[a-zA-Z0-9ㄱ-ㅎ가-힣-._\\s]*$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher;
		boolean check;

		matcher = pattern.matcher(form.getUname());
		check = matcher.matches();
		if(!check) {model.addAttribute("infoMessage" ,message.getMessage("E0737","사용할 수 없는 특수문자가 포함되어 있습니다. (사용가능한 특수 문자 : (_) , (,) , (-) )")); return false;};

		if(StringUtils.isNotEmpty(form.getDept())){
			matcher = pattern.matcher(form.getDept());
			check = matcher.matches();
			if(!check) {model.addAttribute("infoMessage" ,message.getMessage("E0737","사용할 수 없는 특수문자가 포함되어 있습니다. (사용가능한 특수 문자 : (_) , (,) , (-) )")); return false;};
		}
		if(StringUtils.isNotEmpty(form.getGrade())){
			matcher = pattern.matcher(form.getGrade());
			check = matcher.matches();
			if(!check) {model.addAttribute("infoMessage" ,message.getMessage("E0737","사용할 수 없는 특수문자가 포함되어 있습니다. (사용가능한 특수 문자 : (_) , (,) , (-) )")); return false;};
		}

		if(StringUtils.isNotEmpty(form.getTel())){
			matcher = pattern.matcher(form.getTel());
			check = matcher.matches();
			if(!check) {model.addAttribute("infoMessage" ,message.getMessage("E0737","사용할 수 없는 특수문자가 포함되어 있습니다. (사용가능한 특수 문자 : (_) , (,) , (-) )")); return false;};
		}

		if(StringUtils.isNotEmpty(form.getMobile())){
			matcher = pattern.matcher(form.getMobile());
			check = matcher.matches();
			if(!check) {model.addAttribute("infoMessage" ,message.getMessage("E0737","사용할 수 없는 특수문자가 포함되어 있습니다. (사용가능한 특수 문자 : (_) , (,) , (-) )")); return false;};
		}

		//이메일 형식 검사
		if(StringUtils.isEmpty(form.getEmail())){
			model.addAttribute("infoMessage",message.getMessage("E0054","E-MAIL을 입력해 주세요."));
			return false;
		}else{
			if(!ImCheckUtil.isEmail(form.getEmail())){
				model.addAttribute("infoMessage",message.getMessage("E0050","E-MAIL 형식이 잘못되었습니다."));
				return false;
			}
		}
		// 승인 메일 형식 검사
		if(StringUtils.isNotEmpty(form.getApprove_email())){
			if(!ImCheckUtil.isEmail(form.getApprove_email())){
				model.addAttribute("infoMessage",message.getMessage("E0300","승인메일 주소 형식이 잘못되었습니다."));
				return false;
			}
		}
		//전화번호 형식 검사
		if(StringUtils.isNotEmpty(form.getTel())){
			if(!ImCheckUtil.isPhone(form.getTel())){
				model.addAttribute("infoMessage",message.getMessage("E0052","전화번호 형식이 잘못되었습니다."));
				return false;
			}
		}
		//휴대폰번호 형식 검사
		if(StringUtils.isNotEmpty(form.getMobile())){
			if(!ImCheckUtil.isPhone(form.getMobile())){
				model.addAttribute("infoMessage",message.getMessage("E0299","휴대폰번호 형식이 잘못되었습니다."));
				return false;
			}
		}
		//IP 형식 검사
		if(StringUtils.isNotEmpty(form.getAccess_ip())){
			String accessIPs = form.getAccess_ip();
			String regexIPv4Cidr = "^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)(\\/(3[0-2]|[012]?[0-9]))?$";

			if (accessIPs.indexOf(",") > -1) {
				String[] IPs = StringUtils.split(accessIPs,',');
				for(String ip : IPs){
					//TODO IP 형식 검사 실시 > IMCheckUtil 에 만들어야 할 것으로 예상됨.
					ip = StringUtils.trim(ip);
					Pattern p = Pattern.compile(regexIPv4Cidr);
					Matcher m = p.matcher(ip);
					boolean ipCheck = m.matches();
					if(!ipCheck){
						model.addAttribute("infoMessage",message.getMessage("E0095","잘못된 IP 주소 형식입니다."));
						return false;
					}
				}
			}else {
				accessIPs = StringUtils.trim(accessIPs);
				Pattern p = Pattern.compile(regexIPv4Cidr);
				Matcher m = p.matcher(accessIPs);
				boolean ipCheck = m.matches();
				if(!ipCheck){
					model.addAttribute("infoMessage",message.getMessage("E0095","잘못된 IP 주소 형식입니다."));
					return false;
				}
			}

		}
		return true;
	}


	/**
	 * userInfo 객체를 UserForm으로 맵핑
	 * @param userinfo
	 * @return
	 */
	public UserForm userInfoToForm(ImbUserinfo userinfo) {
		UserForm form = new UserForm();
		form.setUserid(userinfo.getUserid());
		form.setUname(userinfo.getUname());
		form.setEmail(userinfo.getEmail());
		form.setDept(userinfo.getDept());
		form.setGrade(userinfo.getGrade());
		form.setPermission(userinfo.getPermission());
		form.setApprove_email(userinfo.getApprove_email());
		form.setAccess_ip(userinfo.getAccess_ip());
		form.setTel(userinfo.getTel());
		form.setMobile(userinfo.getMobile());
		form.setIsstop(userinfo.getIsstop());
		form.setUse_smtp(userinfo.getUse_smtp());
		return form;
	}

	/**
	 * 사용자 정보 수정
	 * @param form
	 * @throws Exception
	 */
	public void modifyUser(UserForm form) throws Exception{

		String userid = form.getUserid();
		// password 경우 별도로 변경 기능 존재  updatePassword()
		// 필수 항목 암호화 대상 : email
		// 필수 항목이 아닌 암호화 대상 : tel , mobile , approve_email
		String secret_key = ImbConstant.DATABASE_AES_KEY;
		String email = ImSecurityLib.encryptAES256(secret_key, form.getEmail());

		ImbUserinfo userinfo = new ImbUserinfo();
		userinfo.setUserid(userid);
		userinfo.setUname(form.getUname());
		userinfo.setEmail(email);
		userinfo.setDept(form.getDept());
		userinfo.setGrade(form.getGrade());
		userinfo.setTel(form.getTel());
		userinfo.setMobile(form.getMobile());
		userinfo.setPermission(form.getPermission());
		userinfo.setAccess_ip(form.getAccess_ip());
		userinfo.setApprove_email(form.getApprove_email());
		userinfo.setIsstop(form.getIsstop());
		userinfo.setUse_smtp(form.getUse_smtp());

		if(StringUtils.isNotEmpty(form.getTel())){ // 전화번호가 null이 아니면 암호화 진행
			String enTel = ImSecurityLib.encryptAES256(secret_key, form.getTel());
			userinfo.setTel(enTel);
		}
		if(StringUtils.isNotEmpty(form.getMobile())){ // 휴대폰번호가 null이 아니면 암호화 진행
			String enMobile = ImSecurityLib.encryptAES256(secret_key, form.getMobile());
			userinfo.setMobile(enMobile);
		}
		if(StringUtils.isNotEmpty(form.getApprove_email())){ // 승인메일이 null이 아니면 암호화 진행
			String enApmail = ImSecurityLib.encryptAES256(secret_key, form.getApprove_email());
			userinfo.setApprove_email(enApmail);
		}

		userMapper.updateUser(userinfo);
		log.debug("Update UserInfo userid - {} / uname - {}",userid,form.getUname());

	}

	/**
	 * 패스워드 변경
	 * @param userinfo
	 * @param newPasswd
	 * @throws Exception
	 */
	public void updatePassword(ImbUserinfo userinfo, String newPasswd) throws Exception {

		String secret_key = ImbConstant.DATABASE_AES_KEY;

		String passwordSalt="";
		if(ImbConstant.PASS_USE_SALT.equals("1")) {
			String saltKey = ImUtils.makeKeyNum(32);
			passwordSalt = saltKey + newPasswd;
			userinfo.setSt_data(saltKey);
		}else{
			passwordSalt = newPasswd;
		}

		String email = ImSecurityLib.encryptAES256(secret_key, userinfo.getEmail());
		String password = ImSecurityLib.makePassword(ImbConstant.PASS_SECU_TYPE,passwordSalt,false);
		userinfo.setEmail(email);
		userinfo.setPasswd(password);
		userinfo.setPwd_date(new Date());
		userinfo.setFail_login(0);

		if(StringUtils.isNotEmpty(userinfo.getTel())){ // 전화번호가 null이 아니면 암호화 진행
			String enTel = ImSecurityLib.encryptAES256(secret_key, userinfo.getTel());
			userinfo.setTel(enTel);
		}
		if(StringUtils.isNotEmpty(userinfo.getMobile())){ // 휴대폰번호가 null이 아니면 암호화 진행
			String enMobile = ImSecurityLib.encryptAES256(secret_key, userinfo.getMobile());
			userinfo.setMobile(enMobile);
		}
		if(StringUtils.isNotEmpty(userinfo.getApprove_email())){ // 승인메일이 null이 아니면 암호화 진행
			String enApmail = ImSecurityLib.encryptAES256(secret_key, userinfo.getApprove_email());
			userinfo.setApprove_email(enApmail);
		}

		userMapper.updateUser(userinfo);
	}

	/**
	 * 사용자 목록 삭제
	 * @param request
	 * @param userid
	 * @param userids
	 * @throws Exception
	 */
	public void deleteUserList(HttpServletRequest request, String userid, String[] userids) throws Exception {
		String logParam="";
		for(int i=0;i<userids.length;i++){
			ImbUserinfo userinfo = getUserInfo(userids[i]);
			if(userinfo == null){
				continue;
			}

			addressMapper.dropAddrTable(userinfo.getUserid());
			addressMapper.dropAddrGrpTable(userinfo.getUserid());
			addressMapper.deleteCategory(userinfo.getUserid());
			addressMapper.deleteReceiver(userinfo.getUserid());
            addressMapper.deleteTestAccount(userinfo.getUserid());
            addressMapper.deleteDbinfo(userinfo.getUserid());
            addressMapper.deleteTemplate(userinfo.getUserid());

			deleteUser(userinfo.getUserid());
			if(StringUtils.isEmpty(logParam)){
				logParam = userids[i];
			}else{
				logParam += ", " + userids[i];
			}
		}
		//log insert start
		ActionLogForm logForm = new ActionLogForm();
		logForm.setIp(HttpRequestUtil.getRemoteAddr(request));
		logForm.setUserid(userid);
		logForm.setMenu_key("G104");
		logForm.setParam("삭제 아이디  : " + logParam);
		actionLogService.insertActionLog(logForm);
	}

	/**
	 * userinfo 테이블에서 해당 사용자 삭제
	 * @param userid
	 */
	private void deleteUser(String userid) throws Exception {
		userMapper.deleteUser(userid);
	}

	/**
	 * 로그인 성공 시 fail_login 초기화
	 * @param userInfo
	 */
    public void updateFailLoginReset(ImbUserinfo userInfo) throws Exception {
		userMapper.updateFailLoginReset(userInfo.getUserid(),0);
    }

	/**
	 *  사용자 데이터 복호화 실시
	 */
	public void decryptUserinfo(ImbUserinfo userinfo) throws Exception {
		String secret_key = ImbConstant.DATABASE_AES_KEY;

		userinfo.setEmail(ImSecurityLib.decryptAES256(secret_key,userinfo.getEmail()));

		if (StringUtils.isNotEmpty(userinfo.getMobile())){
			userinfo.setMobile(ImSecurityLib.decryptAES256(secret_key,userinfo.getMobile()));
		}
		if (StringUtils.isNotEmpty(userinfo.getTel())){
			userinfo.setTel(ImSecurityLib.decryptAES256(secret_key,userinfo.getTel()));
		}
		if (StringUtils.isNotEmpty(userinfo.getApprove_email())){
			userinfo.setApprove_email(ImSecurityLib.decryptAES256(secret_key,userinfo.getApprove_email()));
		}
	}

	public void updateUse(String userid, String use_smtp){
		userMapper.updateUseSMTP(userid, use_smtp);
	}

}
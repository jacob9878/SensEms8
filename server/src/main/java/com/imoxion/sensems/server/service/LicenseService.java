package com.imoxion.sensems.server.service;

import com.imoxion.common.logger.ErrorTraceLogger;
import com.imoxion.common.util.ImTimeUtil;
import com.imoxion.security.ImSecurityLib;
import com.imoxion.sensems.server.exception.LicenseException;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.NetworkInterface;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Enumeration;

/**
 * 라이센스 파일 생성 및 체크 서비스
 */
public class LicenseService {
	private Logger logger = LoggerFactory.getLogger(LicenseService.class);

	private static LicenseService licenseService;

	private final static String licenseFileName = "sensems.lic";

	private static String encryptKey;

	public static LicenseService getInstance() {
		if (licenseService == null) {
			licenseService = new LicenseService();
		}
		return licenseService;
	}
	private LicenseService(){
		try {
			encryptKey = ImSecurityLib.makePassword("AES", "mDPkg2B+55TN7FWQkgvV1Q==", true);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public JSONObject getLicenseInfo() throws LicenseException {

		JSONObject licenseInfo = null;
		String homePath = System.getProperty("sensems.home");

		if (StringUtils.isEmpty(homePath)) {
			homePath = LicenseService.class.getProtectionDomain().getCodeSource().getLocation().getPath()
					+ File.separator + "..";
		}
		String path = homePath + File.separator + "conf" + File.separator + licenseFileName;

		logger.debug("License check path:{}",path);

		File file = new File(path);
		try {

			if (!file.exists()) {
				// 라이센스 파일이 존재하지 않으면 에러
				logger.error("License File is not found");
				throw new LicenseException(LicenseException.NOT_FOUND);
			}

			String readString = FileUtils.readFileToString(file);

			licenseInfo = getLincenseInfoJson(readString);
		} catch (LicenseException e){
			throw e;
		} catch (Exception e) {
			String errorId = ErrorTraceLogger.log(e);
			logger.error("{} - license file check error", errorId);
			throw new LicenseException(LicenseException.NOT_AVAILABLE);
		}

		return licenseInfo;
	}

	public JSONObject getLincenseInfoJson(String readString) throws LicenseException {
		JSONObject licenseInfo;
		if (StringUtils.isEmpty(readString)) {
			logger.error("License File Is incorrect");
			licenseInfo = new JSONObject();
			throw new LicenseException(LicenseException.NOT_AVAILABLE);
		} else {
			try {
				String decyprtString = ImSecurityLib.decryptAES(encryptKey, readString);
				licenseInfo = JSONObject.fromObject(JSONSerializer.toJSON(decyprtString));
			} catch (Exception e) {
				String errorId = ErrorTraceLogger.log(e);
				logger.error("{} - decyprtString error", errorId);
				throw new LicenseException(LicenseException.NOT_AVAILABLE);
			}
			String chkData = "";
			try {
				chkData = licenseInfo.getString("chkdate");
			} catch (JSONException e) {}

			if( StringUtils.isNotEmpty(chkData)) {
				LocalDate finishDate = LocalDate.parse(chkData);
				LocalDate currentDate = LocalDate.now();

				long diffDays =  ChronoUnit.DAYS.between(currentDate, finishDate);

				if (diffDays < 0) { // 종료일이 현재날짜보다 이전인 경우
					logger.debug("license expired");
					licenseInfo.put("available", "0");
				} else {
					licenseInfo.put("available", "1");
				}
			} else {
				try {
					boolean available = false;
					JSONArray licenseMacAddrs = licenseInfo.getJSONArray("macaddrs");

					Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
					while (networkInterfaces.hasMoreElements()) {
						NetworkInterface ni = networkInterfaces.nextElement();
						byte[] hardwareAddress = ni.getHardwareAddress();
						if (hardwareAddress != null) {
							String[] hexadecimalFormat = new String[hardwareAddress.length];
							for (int i = 0; i < hardwareAddress.length; i++) {
								String ha = String.format("%02X", hardwareAddress[i]);
								if("00:00:00:00:00:00".equals(ha)) {
									continue;
								}
								hexadecimalFormat[i] = ha;
							}
							String macAddr = String.join("-", hexadecimalFormat);

							if( licenseMacAddrs.contains(macAddr.toUpperCase()) ) {
								available = true;
								break;
							}
						}
					}

					if( available ) {
						logger.debug("license unlimited");
						licenseInfo.put("available", "1");
					} else {
						logger.debug("License is unavailable with invalid MAC address");
						licenseInfo.put("available", "0");
					}
				} catch( Exception ex ) {
					licenseInfo.put("available", "0");
					String errorId = ErrorTraceLogger.log(ex);
					logger.error("{} - license file check error(mac addr)", errorId);
					throw new LicenseException(LicenseException.NOT_AVAILABLE);
				}
			}
		}
		return licenseInfo;
	}

	public void licenseCheck() throws LicenseException{
		JSONObject licenseInfo = this.getLicenseInfo();
		//System.out.println("licenseInfo = " + licenseInfo.toString());

		// 라이센스 파일이 존재하지 않음
		if ( licenseInfo == null) {
			throw new LicenseException(LicenseException.NOT_FOUND, "License key file not found");
		}

		// 라이센스 파일은 존재하나 정보가 정상적이지 않음
		if( !licenseInfo.containsKey("product") ) {
			throw new LicenseException(LicenseException.NOT_AVAILABLE, "License is invalid");
		}

		String available = licenseInfo.getString("available");

		if( StringUtils.isNotEmpty(available) && "0".equals(available) ) {
			throw new LicenseException(LicenseException.EXPIRE, "License is unavailable or expired");
		}
	}

	public void licenseCheck(String lic) throws LicenseException{
		JSONObject licenseInfo = getLincenseInfoJson(lic);

		// 라이센스 파일이 존재하지 않음
		if ( licenseInfo == null) {
			throw new LicenseException(LicenseException.NOT_FOUND, "License key file not found");
		}

		// 라이센스 파일은 존재하나 정보가 정상적이지 않음
		if( !licenseInfo.containsKey("product") ) {
			throw new LicenseException(LicenseException.NOT_AVAILABLE, "License is invalid");
		}

		String available = licenseInfo.getString("available");
		if( StringUtils.isNotEmpty(available) && "0".equals(available) ) {
			throw new LicenseException(LicenseException.EXPIRE, "License is unavailable or expired");
		}
	}

	public boolean licenseAvailable() throws LicenseException{
		boolean ret =false;
		JSONObject licenseInfo = this.getLicenseInfo();

		if ( licenseInfo != null && licenseInfo.containsKey("available") ) {
			String available = licenseInfo.getString("available");
			if( StringUtils.isNotEmpty(available) && "1".equals(available) ) {
				ret = true;
			}
		}

		return ret;
	}
	public boolean licenseAvailable(String lic) throws Exception {
		boolean ret =false;
		JSONObject licenseInfo = getLincenseInfoJson(lic);

		if ( licenseInfo != null && licenseInfo.containsKey("available") ) {
			String available = licenseInfo.getString("available");
			if( StringUtils.isNotEmpty(available) && "1".equals(available) ) {
				ret = true;
			}
		}

		return ret;
	}

	/**
	 * 라이센스 파일을 생성한다.
	 * @param macAddrs	// 정식버전 라이센스에서 사용가능한 MAC Adress String 배열
	 * @param expireMonth	// 0:무제한, 데모기간 : 1~3
	 * @param expireDate // 만료일 지정(yyyy-MM-dd)
	 * @return
	 * @throws Exception
	 */
	public String createLicense(String[] macAddrs, int expireMonth, String expireDate) throws Exception{
		JSONObject obj = new JSONObject();
		obj.put("product", "SensEms" );

		String chkdate = null;
		if( StringUtils.isNotEmpty(expireDate) ) {
			if( ImTimeUtil.getDateFromString(expireDate, "yyyy-MM-dd") != null ) {
				chkdate = expireDate;
				obj.put("chkdate", chkdate );
			}
		} else {
			if(expireMonth > 0){
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.MONTH, expireMonth);
				chkdate = ImTimeUtil.getDateFormat2(cal.getTimeInMillis(), "yyyy-MM-dd");
				obj.put("chkdate", chkdate );
			}
		}

		if( macAddrs != null ) {
			JSONArray regMacAddr = new JSONArray();
			for( String macAddr : macAddrs ) {
				if( macAddr!=null ) {
					regMacAddr.add(macAddr.toUpperCase().replace(":", "-"));
				}
			}
			obj.put("macaddrs", regMacAddr );
		}

		String licenseKey = ImSecurityLib.encryptAES(encryptKey, obj.toString());
		//System.out.println("obj.toString() = " + obj.toString());
		return licenseKey;
	}


	public static void main(String[] args) {

		try {
			LicenseService licenseService = LicenseService.getInstance();

			String[] macaddrs = {"52:54:00:2b:ca:a3", "BC-EE-7B-8A-BE-EA"};
			//File file = licenseService.createLicenseFile("MEL-FD4F80-2A925CC-204B21-000064", "0", macaddrs, 5, "2021-08-09");
			//String license = licenseService.createLicense(macaddrs, 3, null);
			String license = licenseService.createLicense(macaddrs, 0, null);
			System.out.println("created license key : "+ license);
			System.out.println(licenseService.getLincenseInfoJson(license));
			System.out.println(licenseService.licenseAvailable(license));
			licenseService.licenseCheck(license);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
/**
 * 국가별 IP 검색 서비스를 로딩한다.
 * sensmail.xml의 geoip.database2에 데이타파일 경로 설정(ex: /apps/sensmail/sensdata/geoip/GeoLite2-Country.mmdb)
 */
package com.imoxion.sensems.server.util;

import com.imoxion.common.api.beans.ImGeoCountry;
import com.imoxion.common.util.ImIpUtil;
import com.imoxion.sensems.server.beans.GeoIpOriginalCountryBean;
import com.imoxion.sensems.server.config.ImSmtpConfig;
import com.imoxion.sensems.server.environment.SensData;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CountryResponse;
import com.maxmind.geoip2.record.Continent;
import com.maxmind.geoip2.record.Country;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.InetAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author : Administrator
 * @date : 2013. 1. 24.
 * @desc :
 * GeoLite2-Country.mmdb 파일을 이용
 * (http://dev.maxmind.com/geoip/geoip2/geolite2/)
 * ImGeoIPLookup2 geo = ImGeoIPLookup2.getInstance();<br/>
try {<br/>
Country country = geo.lookup("아이피 정보");<br/>
System.out.println(country.getIsoCode());<br/>
System.out.println(country.getName());<br/>
System.out.println(country.getNames().get("zh-CN"));<br/>
} catch(Exception e){<br/>
e.printStackTrace();<br/>
}<br/>
 *
 */
public class ImGeoIPLookup2 {

	private static ImGeoIPLookup2 geoIpLookup = null;
	private static DatabaseReader databaseReader;

	private Logger log = LoggerFactory.getLogger(ImGeoIPLookup2.class);

	/**
	 * GEOIP DATA FILE 경로
	 */
	private String _GEOIP2_DATA_PATH;

	/**
	 * ImGeoIPLookup2 싱글턴 객체를 생성한다. 국가별 IP 검색 서비스를 로딩한다.
	 * 설정파일 경로는 sensmail.xml 의 geoip2.database 를 자동 참조한다.
	 * Country country = ImGeoIPLookup2.getInstance().getCountry(아이피);
	 * country.getIsoCode() : 국가코드(KR, US, JP, CN ...)
	 * country.getName() : 국가명 (Korea, Republic of, United States, Japan, China ...)
	 */
	public static synchronized ImGeoIPLookup2 getInstance() {
		//String geoipDataPath = ImSmtpConfig.getInstance().getGeoipPath() + "/" + ImSmtpConfig.getInstance().getGeoipDatabase();
		String geoipDataPath = SensData.getPath(SensData.GEOIP) + File.separator + ImSmtpConfig.getInstance().getGeoipDatabase();
		return getInstance(geoipDataPath);
	}

	public static synchronized ImGeoIPLookup2 getInstance(String geoIpDataPath) {
		if(geoIpLookup == null){
			geoIpLookup = new ImGeoIPLookup2(geoIpDataPath);
		}
		return geoIpLookup;
	}

	public static synchronized ImGeoIPLookup2 reloadInstance(String geoIpDataPath) {
		geoIpLookup = new ImGeoIPLookup2(geoIpDataPath);

		return geoIpLookup;
	}

	public ImGeoIPLookup2(String geoIpPath){
		this._GEOIP2_DATA_PATH = geoIpPath;
		databaseReader = getDatabaseReader(geoIpPath);
	}

	private DatabaseReader getDatabaseReader(String geoIpPath) {
		DatabaseReader dbReader = null;
		try {
			// A File object pointing to your GeoIP2 or GeoLite2 database
			File database = new File(geoIpPath);

			// This creates the DatabaseReader object, which should be reused across
			// lookups.
			dbReader = new DatabaseReader.Builder(database).build();
		} catch (Exception e) {
			log.error("ImGeoIPLookup2.getDatabaseReader error: {}", e.toString());
		}
		return dbReader;
	}


	/**
	 * lookup 메소드와 동일(alias), Country 객체를 반환한다.
	 * @param ip
	 * @return
	 * @throws Exception
	 */
	public Country getCountry(String ip){
		Country country = null;
		try {
			country = this.lookup(ip);
		} catch(Exception e){
			if(e.toString().toLowerCase().indexOf("addressnotfoundexception") < 0) {
				log.error("ImGeoIPLookup2.getCountry error and reload: {}", e.toString());
				synchronized(this) {
					reloadInstance(_GEOIP2_DATA_PATH);
				}

				country = null;
				try {
					country = this.lookup(ip);
					if(country != null) {
						log.info("after reload, ip:{}, country: {}", ip, country.getIsoCode());
					}else {
						log.info("after reload, ip:{} not found", ip);
					}
				} catch (Exception ee) {
					//ee.printStackTrace();
					log.error("ImGeoIPLookup2.getCountry reload error: {}", ee.toString());
				}
			}
		}

		return country;
	}

	/**
	 * 아이피를 이용하여 국가정보를 추출, Country객체반환
	 * @param ip
	 * @return
	 * @throws Exception
	 */
	public Country lookup(String ip) throws Exception {
		// 아이피 형태가 아닌경우 InetAddress.getByName(ip)에서 오래걸릴수 있음
		String[] ipArr = getIP(ip);
		if(ipArr == null || ipArr.length <= 0) {
			return null;
		}

		if(StringUtils.isEmpty(ipArr[0])) {
			return null;
		}

		CountryResponse cr = databaseReader.country(InetAddress.getByName(ip));
		Country country = cr.getCountry();

		if(country.getIsoCode() == null){
			Continent ct = cr.getContinent();
			ImGeoCountry icountry = new ImGeoCountry();
			icountry.setIsoCode(ct.getCode());
			icountry.setName(ct.getName());
			country = (Country)icountry;
		}

		/*System.out.println(country.getIsoCode());
		System.out.println(country.getName());
		System.out.println(country.getNames().get("zh-CN"));
		System.out.println("Continent : " + ct.getCode());
		System.out.println("Continent : " + ct.getName());
		System.out.println("Continent : " + ct.getGeoNameId());
		*/

		return country;
	}


	private String[] getIP(String str){
		Pattern p =
				Pattern.compile("((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[0-9]{1,2})\\.){3}(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[0-9]{1,2})");
		Matcher m = p.matcher(str);

		StringBuffer sb = new StringBuffer();
		while(m.find()){
			sb.append(m.group()+ " ");
		}

		return m.reset().find() ? sb.toString().split(" ") : new String[0];
	}

	/*public static void main (String[] args){
		ImGeoIPLookup2 geoip = ImGeoIPLookup2.getInstance("C:/Temp/GeoLite2-Country.mmdb");
		if(geoip != null) {
			try {
				Country country = null;
				Country country = geoip.lookup("192.168.123.80");
				System.out.println(country.getIsoCode());
				System.out.println(country.getName());

				// locales
				de – German
				en – English names may still include accented characters if that is the accepted spelling in English. In other words, English does not mean ASCII.
				es – Spanish
				fr – French
				ja – Japanese
				pt-BR – Brazilian Portuguese
				ru – Russian
				zh-CN – Simplified Chinese.

				System.out.println(country.getNames().get("zh-CN"));

				country = geoip.getCountry("47.88.147.42");
				if(country != null) {
					System.out.println(country.getIsoCode());
					System.out.println(country.getName());
					System.out.println(country.getNames().get("ja"));
				} else {
					System.out.println("unkown ip-country");
				}
			} catch(Exception e){
				e.printStackTrace();
			}
		} else {
			System.out.println("geoip is null");
		}
	}*/

	public GeoIpOriginalCountryBean getMailHeaderSendIp(String[] received){

		if( geoIpLookup == null ){
			return null;
		}

		GeoIpOriginalCountryBean geoIpCountry = new GeoIpOriginalCountryBean();

		String countryCode = "";
		String countryName = "";

		try{
			if(received != null){

				int receivedIpLength = received.length;

				for(int i=0; i<receivedIpLength; i++){
					String receivedIp = received[i];
					String[] recvIPs = getIP(receivedIp);

					String checkIP = "";
					if(recvIPs != null && recvIPs.length > 0){
						checkIP = recvIPs[0];
					}

					// 내부 ip , 스팸 ip Skep
					if(StringUtils.isEmpty(checkIP)) continue;
					if(!ImIpUtil.isPublicIP(checkIP)){
						continue;
					}

					if( ImSmtpConfig.getInstance().getSpamServerList().contains(checkIP) ) continue;

					Country country = getCountry(checkIP);
					if (country == null) {
						countryCode = "-";
						countryName = "-";
					} else {
						countryCode = country.getIsoCode();
						countryName = country.getName();
					}
					log.debug("getReceiveIp :{} countryCode :{} countryName :{}",checkIP,countryCode,countryName);

					//국가 이름이 널일 경우
					if(!countryName.equals("N/A")){
						geoIpCountry.setOriginalSendIp(checkIP);
						geoIpCountry.setCountryCode(countryCode.toLowerCase());//국기 이미지를 정상적으로 가져오기 위하여 소문자 변환
						geoIpCountry.setCountryName(countryName);
						break;
					}

				}
			}
			return geoIpCountry;
		}catch(Exception e){
			log.error("GeoIpService.getMailHeaderSendIp Error - {}" , e.getMessage());
			return null;
		}
	}
}

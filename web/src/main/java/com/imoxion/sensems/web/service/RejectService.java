package com.imoxion.sensems.web.service;

import com.csvreader.CsvReader;
import com.imoxion.common.util.ImCheckUtil;
import com.imoxion.common.util.ImStringUtil;
import com.imoxion.sensems.common.logger.ErrorTraceLogger;
import com.imoxion.sensems.web.common.ImbConstant;
import com.imoxion.sensems.web.database.domain.ImbReject;
import com.imoxion.sensems.web.database.mapper.RejectMapper;
import com.imoxion.sensems.web.form.ActionLogForm;
import com.imoxion.sensems.web.form.RejectForm;
import com.imoxion.sensems.web.form.RejectImportForm;
import com.imoxion.sensems.web.form.RejectImportResultForm;
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
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class RejectService {

	@Autowired
	private RejectMapper rejectMapper;

	@Autowired
	private ActionLogService actionLogService;
	
	protected Logger log = LoggerFactory.getLogger(RejectService.class);

	public static final String DIV_COMMA = "0";

	/**
	 * 파일로 가져오기 : 데이터 나누는 기준 - 세미콜론
	 */
	public static final String DIV_SEMICOLON = "1";

	/**
	 * 파일로 가져오기 : 헤더 유무
	 */
	public static final String USE_HEADER = "1";

	/**
	 * 값이 존재하지 않음
	 */
	public static final String NO_DATA = "-1";



	// 전체 수신거부 목록 취득
	public List<ImbReject> getRejectListAll() throws Exception{
		return rejectMapper.selectAllReject();
	}

	public int getRejectCount(String srch_keyword){
		return  rejectMapper.selectRejectCount(srch_keyword);
	}

	/**
	 * 메일 발송 결과 상세페이지에서 특정 메일에 등록된 수신거부 건수(현재건수)
	 * @param msgid
	 * @return int
	 */
	public int getRecentRejectCount(String msgid){
		return rejectMapper.selectRecentRejectCount(msgid);
	}

	/**
	 * 모든 수신거부 건수
	 * @return int
	 */
	public int getTotalRejectCount(){
		return rejectMapper.selectTotalRejectCount();
	}
	
	public List<ImbReject> getRejectList(String srch_keyword, int start, int end){
		return rejectMapper.selectRejectList(srch_keyword, start, end);
	}

	/**
	 * 중복 수신거부 데이터 확인
	 * @param email
	 * */
	public boolean isExistReject(String email) throws Exception{
		int count = rejectMapper.isExistReject(email);
		if(count > 0){
			return true;
		}
		return false;
	}
	
	/**
	 * 수신거부 정보를 가져온다.
	 * 
	 * @param email
	 * @return
	 */
	public RejectForm getRejectInfo(String email) throws Exception{
		ImbReject imbReject = rejectMapper.selectRejectByKey(email);
		if(imbReject == null){
			return null;
		}
		RejectForm rejectForm = new RejectForm();
		rejectForm.setEmail(imbReject.getEmail());
		rejectForm.setMsgid(imbReject.getMsgid());
		rejectForm.setRegdate(imbReject.getRegdate());

		return rejectForm;
	}

	public int selectEditReject(String email) throws Exception {
		return rejectMapper.selectEditReject(email);
	}

	/**
	 * 수신거부 계정 수정
	 * @param form
	 * @param ori_email
	 * @return
	 * @throws Exception
	 */
	public int editReject(RejectForm form, String ori_email) throws Exception {
		return rejectMapper.editReject(form.getEmail().toLowerCase() ,ori_email);
	}

	/**
	 * 수신거부 등록
	 * @param rejectForm
	 * @throws Exception
	 */
	public int insertReject(RejectForm rejectForm) throws Exception{
        
    	//수신거부 정보  Bean setting
		ImbReject imbReject = new ImbReject();

		imbReject.setEmail(rejectForm.getEmail().toLowerCase());
		if(StringUtils.isEmpty(rejectForm.getMsgid())){
			imbReject.setMsgid(null);
		}else {
			imbReject.setMsgid(rejectForm.getMsgid());
		}

		return rejectMapper.insertReject(imbReject);
    }
	
	/**
	 * 수신거부 삭제
	 * @param emails
	 * @throws Exception 
	 */
	public int deleteReject(HttpServletRequest request, String userid, String[] emails) throws Exception{
		int result =0;
		String email="";
		String logParam = "";
		for (int i = 0; i < emails.length; i++) {
			String[] userinfo = emails[i].split(";");
			email = userinfo[0];
			result = rejectMapper.deleteRejectByKey(email);    // 수신거부 삭제
			if(i==0){
				logParam=emails[i];
			}else {
				logParam += ", " + emails[i];
			}
		}
		//log insert start
		ActionLogForm logForm = new ActionLogForm();
		logForm.setIp(HttpRequestUtil.getRemoteAddr(request));
		logForm.setUserid(userid);
		logForm.setMenu_key("F603");
		logForm.setParam("수신거부 삭제 키 : " + logParam);
		actionLogService.insertActionLog(logForm);
		return result;
	}


	public void getDownPath( String tempFileName) throws Exception {
		FileOutputStream fileoutputstream = null;
		try {
			XSSFWorkbook workbook = new XSSFWorkbook();

			//2차는 sheet생성
			XSSFSheet sheet = workbook.createSheet("reject");
			//엑셀의 행
			XSSFRow row = null;
			//엑셀의 셀
			XSSFCell cell = null;

			row = sheet.createRow(0);
			//타이틀(첫행)
			String str = "수신거부메일 , 등록일 ";
			String[] headerArr = str.split(",");
			for (int i = 0; i < headerArr.length; i++) {
				cell = row.createCell(i);
				cell.setCellValue(headerArr[i]);
			}
			//수신거부 목록
			List<ImbReject> rejectList = this.getRejectListAll();
			int i = 1;
			for (ImbReject imbReject : rejectList) {

				row = sheet.createRow((Integer) i);
				int j = 0;
				cell = row.createCell(j);
				cell.setCellValue(imbReject.getEmail()); //이메일
				j++;

				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

				cell = row.createCell(j);
				if(imbReject.getRegdate() != null) {
					String regdate = ImStringUtil.getSafeString(dateFormat.format(imbReject.getRegdate())); //등록일
					cell.setCellValue(regdate);
				}else{cell.setCellValue("");}
				i++;
			}

			fileoutputstream = new FileOutputStream(tempFileName);
			//파일을 쓴다
			workbook.write(fileoutputstream);

		}catch (NullPointerException ne) {
			log.error("delete reject error");
		}
		catch (Exception e) {
			log.error("delete reject error");
		} finally {
			try {
				if (fileoutputstream != null) fileoutputstream.close();
			}catch (IOException ie) {}
			catch (Exception e) {
			}
		}
	}

	/**
	 * 파일로 가져오기 미리보기 페이지 설정
	 * @param fileKey
	 * @param div
	 * @param header
	 * @return
	 * @throws Exception
	 */
	public String importAddressFilePreview(String fileKey,String userid, String div, String header) throws Exception {

		char divstr = ',';
		if (DIV_SEMICOLON.equals(div)) {
			divstr = ';';
		}

		String addrFile_path = ImbConstant.TEMPFILE_PATH;
		File addrFile = new File(addrFile_path + File.separator + userid + "_" + fileKey);
		if( !addrFile.exists() ){
//            throw new Exception();
			log.error("Argument is file = {}, file.exists() = {}", addrFile, addrFile.exists());
		}

		String sCharset = "UTF-8";

		String retStr = "<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"1\" class=\"table\">";

		BufferedReader in = null;
		CsvReader csvReader = null;
		try {
			int f_count;
			int checkCnt = 0;
			in = new BufferedReader(new InputStreamReader(new FileInputStream(addrFile),sCharset));
			csvReader = new CsvReader(in, divstr);
			csvReader.setEscapeMode(1);
			while (csvReader.readRecord()) {
				f_count = csvReader.getColumnCount();
				if (checkCnt > 9)
					break;

				if(checkCnt == 0 ){
					if(!USE_HEADER.equals(header)){
						retStr += "<thead>";
						for (int i = 0; i < f_count; i++) {
							retStr += "<th>" + csvReader.get(i) + "</th>";
						}
						retStr += "</thead>";
					}
				}else {
					retStr += "<tr>";
					for (int i = 0; i < f_count; i++) {
						retStr += "<td class=\"over_text\">" + csvReader.get(i) + "</td>";
					}
					retStr += "</tr>";
				}
				checkCnt++;
			}
		} finally {
			try{ if (csvReader != null) csvReader.close(); }catch (NullPointerException ne){}
			catch(Exception e){}
			try{ if (in != null) in.close(); } catch (NullPointerException ne){}
			catch(Exception e){}
		}

		return retStr + "</table>";
	}

	/**
	 * 파일로 가져오기 설정
	 * 동기화를 넣은 이유 : 시큐어 코딩에서 TOCTOU경쟁조건 취약점이 발생하여, 동기화 구문을 사용하여 하나의 스레드만 접근가능하도록 조치
	 * @param form
	 * @param userid
	 * @return
	 * @throws Exception
	 */
	public synchronized RejectImportForm importAddressFileSetting(RejectImportForm form, String userid) throws Exception{

		Map<Integer, String> strColumn = new HashMap<Integer, String>();

		String strColArray = "";
		String strdiv = ",";
		int nCol = 0;
		if (DIV_COMMA.equals(form.getDivMethod())) {
			strdiv = ",";
		} else if (DIV_SEMICOLON.equals(form.getDivMethod())) {
			strdiv = ";";
		} else {
			strdiv = "";
		}

		String addrFile_path = ImbConstant.TEMPFILE_PATH;
		File addrFile = new File(addrFile_path + File.separator + userid + "_" + form.getFileKey());

		String sCharset = "UTF-8";

		List<String> arrLineStr = new ArrayList<String>();
		if (addrFile.exists()) {
			BufferedReader in = null;
			try {
				StringBuffer sb = new StringBuffer();
				in = new BufferedReader(new InputStreamReader(new FileInputStream(addrFile), sCharset));
				String readString = null;
				while ((readString = in.readLine()) != null) {
					sb.append(readString);
					arrLineStr.add(readString);
				}
			}catch (FileNotFoundException fe){
				String errorId = ErrorTraceLogger.log(fe);
				log.error("FileNotException fe error : {}",errorId);
			}catch (Exception e){
				String errorId = ErrorTraceLogger.log(e);
				log.error("FileNotException error : {}",errorId);
			}finally {
				if (in != null) in.close();
			}
		} else {
			log.error("Arguement is file = {}, file.isFile() = {}", addrFile, addrFile.isFile());
			form.setNCol(0);
			form.setStrColArray(strColArray);
		}
		nCol = arrLineStr.size();


		/** 1번째 행만 나오도록 option 설정 및 배열 설정 Start */
		StringTokenizer sToken = new StringTokenizer(arrLineStr.get(0).toString(), strdiv);
		List<String> arrFileStr = new ArrayList<String>();

		while (sToken.hasMoreTokens()) {
			arrFileStr.add(sToken.nextToken());
		}

		if (strdiv.equals("")) {
			for (int c = 0; c < arrFileStr.size(); c++) {
				if (USE_HEADER.equals(form.getHeader())) {
					strColumn.put(c, arrFileStr.get(c));
					strColArray += "arrcolname[" + c + "]='" + arrFileStr.get(c) + "';\n";
				} else {
					strColumn.put(c, "col" + c);
					strColArray += "arrcolname[" + c + "]='col" + c + "';\n";
				}
				strColArray += "arrcolsize[" + c + "]='100';\n";
				strColArray += "arrcoltype[" + c + "]='0';\n";
			}
		} else {
			for (int c = 0; c < arrFileStr.size(); c++) {
				strColumn.put(c, arrFileStr.get(c));
				strColArray += "arrcolname[" + c + "]='" + arrFileStr.get(c) + "';\n";
				strColArray += "arrcolsize[" + c + "]='100';\n";
				strColArray += "arrcoltype[" + c + "]='0';\n";
			}
		}
		/** 1번째 행만 나오도록 option 설정 및 배열 설정 End */

		form.setNCol(nCol);
		form.setStrColumn(strColumn);
		form.setStrColArray(strColArray);
		return form;
	}

	/**
	 * 추가된 .csv 또는 .txt 파일을 읽어드려 List 형태로 변형
	 * @param form
	 * @param userid
	 * @return
	 * @throws Exception
	 */
	public RejectImportResultForm importRejectForFile(RejectImportForm form, String userid) throws Exception {
		char strdiv = ',';
		int columnCountNotMatch = 0;
		String divmethod = form.getDivMethod();
		if (DIV_COMMA.equals(divmethod)) {
			strdiv = ',';
		} else if (DIV_SEMICOLON.equals(divmethod)) {
			strdiv = ';';
		} else {
			strdiv = ',';
		}

		String addrFile_path = ImbConstant.TEMPFILE_PATH;
		File addrFile = new File(addrFile_path + File.separator + userid + "_" + form.getFileKey());
		String sCharset = "UTF-8";

		if (!addrFile.exists()) {
			log.error("Argument is file = {}, file.exists() = {}", addrFile, addrFile.exists());
		}

		int columnLength = 0;

		if (NO_DATA.equals(form.getEmail())) {
			log.error("Argument is E-MAIL = {}", form.getEmail());

		}


		List<ImbReject> rejectList = new ArrayList<ImbReject>();

		// 배열화 된 목록을 차례대로 구분자로 나눠준다.
		// 업로드한 파일을 배열화한다.

		int nCount = 0;
		FileInputStream fis = null;
		BufferedReader in = null;

		fis = new FileInputStream(addrFile);
		in = new BufferedReader(new InputStreamReader(fis, sCharset));

		int f_count = 0;
		CsvReader csvReader = new CsvReader(in, strdiv);
		csvReader.setEscapeMode(1);

		while (csvReader.readRecord()) {
			f_count = csvReader.getColumnCount();

			String sEmail = null;

			if (nCount == 0)
				columnLength = f_count;

			if (columnLength > f_count) {
				log.error("Argument is columnLength = {}", columnLength);
				log.error("Argument is f_count = {}", f_count);
				columnCountNotMatch++;
				continue;
			}

			if (USE_HEADER.equals(form.getHeader()) && nCount == 0) {
				nCount++;
				continue;
			}



			sEmail = csvReader.get(ImStringUtil.parseInt(form.getEmail()));
			sEmail = ImStringUtil.replace(sEmail, "\"", "");



			ImbReject reject = new ImbReject();
			reject.setEmail(sEmail.toLowerCase());
			rejectList.add( reject );
		}
		if (in != null) in.close();
		if (fis != null) fis.close();

		RejectImportResultForm resultForm = importReject(userid,rejectList);
		if(columnLength !=0) {
			resultForm.setColumnCountNotMatch(columnCountNotMatch);
		}
		return resultForm;
	}
	/**
	 * List에 포함된 수신거부 데이터를 insert하여 결과를 반환
	 * @param userid
	 * @param rejectList
	 * @return
	 * @throws Exception
	 */
	private RejectImportResultForm importReject(String userid, List<ImbReject> rejectList) throws Exception {
		RejectImportResultForm resultForm = new RejectImportResultForm();

		int importCount = 0; // 처리한 주소 수

		int successCount = 0; // 등록 성공한 주소 수


		// 이메일 누락 / 형식 오류
		List<ImbReject> emailAddressErrorList = new ArrayList<ImbReject>();

		// 숫자 오류

		ImbReject imbReject = null;
		//새그룹 선택 시, 주소록 그룹을 만든다.
//		if ( AddressService.NEW_GROUP.equals( gkey ) ){
//			addrGrp = new ImbAddrGrp();
//			addrGrp.setGname(gname);
//			addrGrp.setMemo("");
//
//
//			insertReject(\,userid);
//			group_key = addrGrp.getGkey();
//		}else {
//			//선택한 주소록 그룹 키 세팅
//			group_key = ImStringUtil.parseInt(gkey);
//		}

		if( rejectList == null ){
			return null;
		}

		for(ImbReject reject : rejectList){
			reject.setRegdate(new Date());
			//이름이 누락된 경우
			//이메일이 누락된 경우
			if(StringUtils.isEmpty(reject.getEmail())){
				emailAddressErrorList.add(reject);
				importCount++;
				continue;
			}
			//이메일 형식이 맞지 않을 경우
			if (!ImCheckUtil.isEmail(reject.getEmail())) {
				emailAddressErrorList.add(reject);
				importCount++;
				continue;
			}

			//이메일 중복 체크
//			String tempEmail = reject.getEmail();
//			if(ImbConstant.DATABASE_ENCRYPTION_USE){
//				String secret_key = ImbConstant.DATABASE_AES_KEY;
//				tempEmail = ImSecurityLib.encryptAES256(secret_key,tempEmail);
//			}

			// 해당 이메일을 보유하고 있는 gkeyList를 획득한다.
			// select DISTINCT gkey 를 통해 검색함으로 0과 1이 나와야만 한다. (0일 경우 추가, 1일 경우 gkey 값 비교(같으면 추가, 다르면 중복))
			// 1 보다 클 경우 이미 중복 이메일 존재함으로 에러
//			List<Integer> gkeyList = addressMapper.getGkeyListByGkey(tempEmail,userid, String.valueOf(group_key));
//
//			if(gkeyList.size()>=1){
//				emailAddressErrorList.add(addr);
//				importCount++;
//				continue;
//			}

			//번호 형식 체크
//            if(StringUtils.isNotEmpty(addr.getHome_tel())){
//                if(!ImCheckUtil.isPhone(addr.getHome_tel())){
//                    numberErrorList.add(addr);
//                    importCount++;
//                    continue;
//                }
//            }
//			if(StringUtils.isNotEmpty(addr.getOffice_tel())){
//				if(!ImCheckUtil.isPhone(addr.getOffice_tel())){
//					numberErrorList.add(addr);
//					importCount++;
//					continue;
//				}
//			}
//			if(StringUtils.isNotEmpty(addr.getMobile())){
//				if(!ImCheckUtil.isPhone(addr.getMobile())){
//					numberErrorList.add(addr);
//					importCount++;
//					continue;
//				}
//			}
//            if(StringUtils.isNotEmpty(addr.getFax())){
//                if(!ImCheckUtil.isPhone(addr.getFax())){
//                    numberErrorList.add(addr);
//                    importCount++;
//                    continue;
//                }
//            }
//            if(StringUtils.isNotEmpty(addr.getZipcode())){
//                if(!ImCheckUtil.isPhone(addr.getZipcode())){
//                    numberErrorList.add(addr);
//                    importCount++;
//                    continue;
//                }
//            }

			//데이터 insert 실시
			try{

				insertRejectList(reject);
				importCount++;
				successCount++;
			}catch (NullPointerException ne) {
				String errorId = ErrorTraceLogger.log(ne);
				log.error("{} - reject IMPORT INSERT ne ERROR - {}", errorId, reject.getEmail());
				importCount++;
				continue;
			}
			catch (Exception e){
				String errorId = ErrorTraceLogger.log(e);
				log.error("{} - reject IMPORT INSERT ERROR - {}", errorId, reject.getEmail());
				importCount++;
				continue;
			}
		}
		resultForm.setImportCount( importCount );
		resultForm.setSuccessCount( successCount );
		resultForm.setEmailAddressErrorList( emailAddressErrorList );
		return  resultForm;
	}
	public void insertRejectList(ImbReject reject) throws Exception {
		rejectMapper.insertRejectList(reject);
	}

}
package com.imoxion.sensems.web.service;


import com.imoxion.common.util.ImStringUtil;
import com.imoxion.common.util.ImTimeUtil;
import com.imoxion.common.util.ImUtils;
import com.imoxion.sensems.common.logger.ErrorTraceLogger;
import com.imoxion.sensems.web.beans.ActionLogBean;
import com.imoxion.sensems.web.beans.ActionLogResultBean;
import com.imoxion.sensems.web.common.ImPage;
import com.imoxion.sensems.web.database.domain.ImbActionLog;
import com.imoxion.sensems.web.database.domain.ImbActionMenu;
import com.imoxion.sensems.web.database.mapper.ActionLogMapper;
import com.imoxion.sensems.web.form.ActionLogForm;
import com.imoxion.sensems.web.form.ActionLogListForm;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;
import ucar.nc2.util.IO;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class ActionLogService {
	
	private Logger log = LoggerFactory.getLogger(ActionLogService.class);

	@Autowired
	private MessageSourceAccessor message;

	@Autowired
	private ActionLogMapper actionLogMapper;

	public void insertActionLog(ActionLogForm form) throws Exception {

		ImbActionLog imbActionLog = new ImbActionLog();
		String logkey = ImUtils.makeKeyNum(24);
		imbActionLog.setLog_key(logkey);
		imbActionLog.setLog_date(new Date());
		imbActionLog.setIp(form.getIp());
		imbActionLog.setUserid(form.getUserid());
		imbActionLog.setMenu_key(form.getMenu_key());
		imbActionLog.setParam(form.getParam());

		actionLogMapper.insertActionLog(imbActionLog);

	}

	public List<ImbActionMenu> selectActionMenu() throws Exception {
		return actionLogMapper.selectActionMenu();
	}

	public ActionLogResultBean search(ActionLogListForm form, int pagesize, int type) throws Exception {
		ActionLogResultBean result = new ActionLogResultBean();
		ActionLogBean actionLogBean = new ActionLogBean();
		actionLogBean.setStart_date(form.getStart_date());
		actionLogBean.setEnd_date(form.getEnd_date());
		actionLogBean.setUserid(form.getUserid());
		actionLogBean.setMenu_key(form.getMenu_key());
		actionLogBean.setSrch_keyword(form.getSrch_keyword());

		int total = actionLogMapper.selectActionLogCount(actionLogBean);

		ImPage pageInfo = new ImPage(ImStringUtil.parseInt(form.getCpage()), pagesize,
				total, ImStringUtil.parseInt(form.getPage_groupsize()));

		actionLogBean.setStart(pageInfo.getStart());
		actionLogBean.setEnd(pageInfo.getEnd());

		if (type == 1){ // 목록에 페이징이 필요한 경우 ex) 검색 , 검색결과 다운로드(현재 페이지만)
			List<ImbActionLog> resultList = actionLogMapper.selectActionLogList(actionLogBean);
			result.setResultList(resultList);
		} else { // 전체 검색결과 다운로드
			List<ImbActionLog> resultList = actionLogMapper.selectAllActionLogList(actionLogBean);
			result.setResultList(resultList);
		}

		result.setPageInfo(pageInfo);


		return result;
	}

	public void getFileDownloadActionLog(ActionLogListForm form, String tempFileName, int pagesize, int type) throws Exception {

		ActionLogResultBean logResult = this.search(form, pagesize, type);
		ImPage pageInfo = logResult.getPageInfo();
		List<ImbActionLog> list = logResult.getResultList();

		FileOutputStream fileoutputstream = null;

		HSSFWorkbook workbook = new HSSFWorkbook();

		//2차는 sheet생성
		HSSFSheet sheet = workbook.createSheet("log");
		//엑셀의 행
		HSSFRow row = null;
		//엑셀의 셀
		HSSFCell cell = null;

		row = sheet.createRow(0);
		//타이틀
		String str = message.getMessage("E0409","로그시간,IP,아이디,메뉴,상세내용");
		String[] headerArr = str.split(",");
		for (int i = 0; i < headerArr.length; i++) {
			cell = row.createCell(i);
			cell.setCellValue(headerArr[i]);
		}

		int i = 1;
		for (ImbActionLog log : list) {
			row = sheet.createRow((short) i);
			int j = 0;

			cell = row.createCell(j);
			cell.setCellValue(ImTimeUtil.getDate(log.getLog_date()));
			j++;

			cell = row.createCell(j);
			cell.setCellValue(ImStringUtil.getSafeString(log.getIp()));
			j++;

			cell = row.createCell(j);
			cell.setCellValue(ImStringUtil.getSafeString(log.getUserid()));
			j++;

			cell = row.createCell(j);
			String menu = ImStringUtil.getSafeString(log.getImbActionMenu().getMenu());
			cell.setCellValue(menu);
			j++;

			cell = row.createCell(j);
			cell.setCellValue(ImStringUtil.getSafeString(log.getParam()));

			i++;
		}
		try {
			fileoutputstream = new FileOutputStream(tempFileName);
			//파일을 쓴다
			workbook.write(fileoutputstream);
		} catch (IOException ie) {
			String errorId = ErrorTraceLogger.log(ie);
			log.error("{} - getFileDownloadActionLog IO error", errorId);
		} catch(Exception e){
			String errorId = ErrorTraceLogger.log(e);
			log.error("{} - getFileDownloadActionLog error", errorId);
		} finally {
			try{ if( fileoutputstream != null ) fileoutputstream.close(); }catch (IOException iee){} catch (Exception ex){}
		}
	}

}
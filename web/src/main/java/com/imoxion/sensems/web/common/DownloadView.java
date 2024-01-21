package com.imoxion.sensems.web.common;

import com.imoxion.common.util.ImClientUtil;
import com.imoxion.common.util.ImUtils;
import com.imoxion.common.util.client.ClientInfo;
import com.imoxion.sensems.common.logger.ErrorTraceLogger;
import org.apache.ibatis.jdbc.Null;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.servlet.view.AbstractView;
import org.xbill.DNS.CAARecord;
import ucar.nc2.util.IO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Map;

public class DownloadView extends AbstractView {

	private Logger log = LoggerFactory.getLogger(getClass());

	public void Download() {
		setContentType("application/download; utf-8");
	}

	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {

		String userAgent = request.getHeader("User-Agent") != null ? request.getHeader("User-Agent") : "";
		userAgent = userAgent.replaceAll("[\\r\\n]", "");
		String language = request.getLocale().getLanguage();
		if(!"ko".equals(language) && !"en".equals(language) && !"ja".equals(language) && !"zh".equals(language)){
			language = "en";
		}

		CommonFile commonFile = (CommonFile) model.get("downloadFile");

		File file = commonFile.getFile();
		InputStream is = null;
		if (file == null) {
			is = commonFile.getStream();
		}
		String fileName = commonFile.getFileName();

		long fileSize = -1;
		if (commonFile.getFileSize() > 0) {
			fileSize = commonFile.getFileSize();
		}

		// 파일이름 처리
		ClientInfo clientInfo = ClientInfo.getClientInfo(userAgent);
		String downloadFileName = ImClientUtil.getDownloadFileName(fileName, clientInfo, language);
		OutputStream out = response.getOutputStream();

		try {
			if (file != null) { // File				
				fileSize = file.length();
				setHeader(response, downloadFileName, fileSize, userAgent, language);
				FileInputStream fis = null;
				try {
					fis = new FileInputStream(file);
					FileCopyUtils.copy(fis, out);
				} catch (FileNotFoundException fe) {
					String errorId = ErrorTraceLogger.log(fe);
					log.error("{} - filecopy file not found error", errorId);
				} catch (NullPointerException ne) {
					String errorId = ErrorTraceLogger.log(ne);
					log.error("{} - filecopy null error", errorId);
				} catch (Exception e) {
					String errorId = ErrorTraceLogger.log(e);
					log.error("{} - filecopy error", errorId);
				} finally {
					if (fis != null) {
						try { fis.close(); }
						catch (IOException ie) {
							String errorId = ErrorTraceLogger.log(ie);
							log.error("{} - file not close IO error", errorId);
						} catch (Exception e) {
							String errorId = ErrorTraceLogger.log(e);
							log.error("{} - file not close error", errorId);
						}
					}
				}
			} else {
				// inputStream 은 사이즈를 모르니깐..0으로 설정				
				setHeader(response, downloadFileName, fileSize, userAgent, language);
				try {
					FileCopyUtils.copy(is, out);
				} catch (IOException ie) {
					String errorId = ErrorTraceLogger.log(ie);
					log.error("{} - file copy IO error", errorId);
				} catch (Exception e) {
					String errorId = ErrorTraceLogger.log(e);
					log.error("{} - file copy error", errorId);
				} finally {
					if (is != null) {
						try { is.close(); }
						catch (IOException ie) {
							String errorId = ErrorTraceLogger.log(ie);
							log.error("{} - file not close IO error", errorId);
						} catch (Exception e) {
							String errorId = ErrorTraceLogger.log(e);
							log.error("{} - file not close error", errorId);
						}
					}
				}
			}
		} catch (FileNotFoundException | NullPointerException fe) {
			String errorId = ErrorTraceLogger.log(fe);
			log.error("{} - Download OutputStream write notfound/null error", errorId);
		} catch (Exception ex) {
			String errorId = ErrorTraceLogger.log(ex);
			log.error("{} - Download OutputStream write error", errorId);
		} finally {
			if(out != null) {
				out.flush();
				out.close();
			}
		}
	}

	/**
	 * 파일 다운로드 헤더 설정
	 * 
	 * @Method Name : setHeader
	 * @Method Comment :
	 * 
	 * @param response
	 * @param fileName
	 * @param fileSize
	 * @param agentstr
	 * @param lang
	 * @throws Exception
	 */
	private void setHeader(HttpServletResponse response, String fileName, long fileSize, String agentstr, String lang) throws Exception {
		response.reset();

		String mimeType = ImUtils.getMimeType(fileName);
		response.setContentType(mimeType);
		response.setHeader("Content-Transfer-Encoding", "binary;");
		if (fileSize > 0) {
			response.setHeader("Content-Length", Long.toString(fileSize));
		}
		//response.setHeader("Cache-Control", "private");
		
		// ie8 이하에서 제거
		//response.setHeader("Pragma", "no-cache");
		response.setHeader("Expires", "0");

		if (agentstr.indexOf("MSIE 5.5") > -1) {
			response.setHeader("Content-Disposition", "filename=" + fileName);
		} else {
			response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
		}
	}
}
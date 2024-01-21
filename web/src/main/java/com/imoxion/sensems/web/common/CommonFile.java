package com.imoxion.sensems.web.common;

import com.imoxion.common.util.ImFileUtil;
import com.imoxion.sensems.common.logger.ErrorTraceLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.InputStream;


public class CommonFile {

	private static Logger log = LoggerFactory.getLogger(CommonFile.class);

	private String fileName = null;

	private File file = null;

	private long fileSize = 0;

	private InputStream stream = null;

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName
	 *            the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return the file
	 */
	public File getFile() {
		return file;
	}

	/**
	 * @param file
	 *            the file to set
	 */
	public void setFile(File file) {
		this.file = file;
	}

	/**
	 * @return the fileSize
	 */
	public long getFileSize() {
		return fileSize;
	}

	/**
	 * @param fileSize
	 *            the fileSize to set
	 */
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	/**
	 * @return the stream
	 */
	public InputStream getStream() {
		return stream;
	}

	/**
	 * @param stream
	 *            the stream to set
	 */
	public void setStream(InputStream stream) {
		this.stream = stream;
	}

	/**
	 * 다운로드할 수 있는 ModelAndView 객체를 제공한다.
	 * 
	 * @param downloadFile
	 * @return
	 */
	public static ModelAndView getDownloadView(CommonFile downloadFile) {

		ModelAndView mav = new ModelAndView();
		mav.setViewName("download");
		mav.addObject("downloadFile", downloadFile);
		return mav;
	}

	/**
	 * 다운로드할 수 있는 ModelAndView 객체를 제공한다.
	 * 
	 * @param file
	 * @param fileName
	 * @return
	 */
	public static ModelAndView getDownloadView(File file, String fileName) {

		CommonFile downloadFile = new CommonFile();
		downloadFile.setFile(file);
		downloadFile.setFileName(fileName);

		ModelAndView mav = new ModelAndView();
		mav.setViewName("download");
		mav.addObject("downloadFile", downloadFile);
		return mav;
	}

	/**
	 * 다운로드할 수 있는 ModelAndView 객체를 제공한다.
	 * 
	 * @param inputStream
	 * @param fileName
	 * @return
	 */
	public static ModelAndView getDownloadView(InputStream inputStream, String fileName) throws Exception {
		ModelAndView mav = new ModelAndView();

		try{
			CommonFile downloadFile = new CommonFile();
			downloadFile.setStream(inputStream);
			downloadFile.setFileName(fileName);

			mav.setViewName("download");
			mav.addObject("downloadFile", downloadFile);
		}catch (NullPointerException ne) {
			String errorId = ErrorTraceLogger.log(ne);
			log.error("{} - getDownloadView ne error", errorId);
		}
		catch (Exception e){
			String errorId = ErrorTraceLogger.log(e);
			log.error("{} - getDownloadView error", errorId);
		}
		finally {
			if(inputStream != null) inputStream.close();
			return mav;
		}
	}

	public static ModelAndView getDownloadView(InputStream inputStream, String fileName, long fileSize) {

		CommonFile downloadFile = new CommonFile();
		downloadFile.setStream(inputStream);
		downloadFile.setFileName(fileName);
		downloadFile.setFileSize(fileSize);

		ModelAndView mav = new ModelAndView();
		mav.setViewName("download");
		mav.addObject("downloadFile", downloadFile);
		return mav;
	}


	/**
	 * 다운로드취약점 대응하는 파일 경로를 생성
	 * @param parentPath 부모 경로
	 * @param fileName 파일이름
	 * @return
	 */
	public static String getFilePath(String parentPath , String fileName){
		return parentPath + File.separator + ImFileUtil.getSafetyFileName(fileName);
	}

	/**
	 * 다운로드취약점 대응하는 파일 경로의 파일 객체 제공
	 * @param parentPath 부모 경로
	 * @param fileName 파일이름
	 * @return
	 */
	public static File getFile(String parentPath , String fileName){
		return new File(getFilePath(parentPath,fileName));
	}
}

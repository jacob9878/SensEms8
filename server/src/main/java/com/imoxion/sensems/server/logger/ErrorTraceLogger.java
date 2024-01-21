package com.imoxion.sensems.server.logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imoxion.common.util.ImUtils;

/**
 * Trace Log 를 별도의 파일에 저장한다. 이때 해당 Log의 ErrorId를 관리하여 시스템 로그파일에 에러 ID 를 비교하여 해당
 * 에러에 대한 Trace를 쉽게 찾을 수 있도록 한다.
 * 
 * @author sunggyu
 * 
 */
public class ErrorTraceLogger {

	static Logger log = LoggerFactory.getLogger("ERRORTRACE");

	/**
	 * errorId 를 입력받아 Error Trace 에 Trace 로그를 남긴다.
	 * 
	 * @param errorId
	 * @param e
	 */
	public static void log(String errorId, Throwable e) {
		Writer result = new StringWriter();
		PrintWriter printWriter = new PrintWriter(result);
		e.printStackTrace(printWriter);
		log.error("ERROR ID : {}\r\n{}", errorId, result.toString());
		printWriter.close();
	}

	/**
	 * ErrorTraceLogger 에 Trace Logger 를 찍고 ErrorId 를 반환한다.
	 * 
	 * @param e
	 * @return
	 */
	public static String log(Throwable e) {
		String errorId = ErrorTraceLogger.getId();
		log(errorId, e);
		return errorId;
	}

	/**
	 * 에러 ID를 생성한다.
	 * 
	 * @return
	 */
	public static synchronized String getId() {
		return "ERR-" + ImUtils.getRandomNum(5);
	}
}
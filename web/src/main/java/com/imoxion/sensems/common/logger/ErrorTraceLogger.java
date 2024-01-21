package com.imoxion.sensems.common.logger;

import com.imoxion.common.util.ImUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Trace Log 를 별도의 파일에 저장한다. 이때 해당 Log의 ErrorId를 관리하여 시스템 로그파일에 에러 ID 를 비교하여 해당
 * 에러에 대한 Trace를 쉽게 찾을 수 있도록 한다.
 * 
 * @author sunggyu
 * 
 */
public class ErrorTraceLogger {

	static Logger log = LoggerFactory.getLogger("ERRORTRACE");

    private static final int CLIENT_CODE_STACK_INDEX;

    /**
     * StackTrace 에서 자신의 메서드 이름으 몇번째 행에 나오는지 구한다.
     * 이는 JDK 버전에 따라 다르게 나오기 때문에 처음에 이를 구하여 depth 를 구해놓는다.
     */
    static {
        int i = 0;
        for (StackTraceElement ste: Thread.currentThread().getStackTrace())
        {
            i++;
            if (ste.getClassName().equals(ErrorTraceLogger.class.getName()))
            {
                break;
            }
        }
        CLIENT_CODE_STACK_INDEX = i;
        log.info("GET METHODNAME LINE NUMBER : {}", CLIENT_CODE_STACK_INDEX);
    }

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
		String errorId = ErrorTraceLogger.log(e);
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

    /**
     * 에러가 난 메서드 이름을 구한다.
     * @param e
     * @return
     */
    public static String getMethodName(Exception e){
		try{
			return e.getStackTrace()[CLIENT_CODE_STACK_INDEX].getMethodName();
		}catch (NullPointerException ne) {
			return "ne UNKNOW METHOD NAME";
		}
		catch (Exception ex) {
			return "UNKNOW METHOD NAME";
		}
	}

}
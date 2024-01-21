/**
 * 
 * �ۼ��� ��¥: 2005. 2. 16.
 * @author: OYoung, Kwon<realkoy@imoxion.com>
 *
 * SmtpServer
 */
package com.imoxion.sensems.server.smtp;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.imoxion.sensems.server.util.ImSmtpSendData;
import org.apache.commons.lang.StringUtils;

import com.imoxion.common.net.ImSmtp;
import com.imoxion.common.util.ImStringUtil;

/**
 * 
 * TODO
 */
public class ImSmtpSLog {
	/** Debug */
	public static boolean isLog = true;
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("EEE,dd MMM yyyy HH:mm:ss Z", Locale.US);

	public static synchronized void log(String sPath, String s)
			throws IOException {
		if (!isLog)
			return;
		if (sPath == null || sPath.length() <= 0) {
			return;
		}

		FileWriter aWriter = new FileWriter(sPath, true);
		try {
			aWriter.write(s + "\r\n");
		} catch (Exception ex) {
		} finally {
			try {
				aWriter.close();
			} catch (Exception e) {
			}
		}
	}

	public static void doPeekLog(String sPath) {
		try {
			String sTimeStamp = Long.toString(System.currentTimeMillis());

			String sCurrDate = dateFormat.format(new Date());
			String s = "[PeekTime] " + sTimeStamp + " : " + sCurrDate;

			log(sPath, s);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	public static void doPeekLog(String sPath, ImSmtpSendData sd) {
		try {
			String sTimeStamp = Long.toString(System.currentTimeMillis());
			String sCurrDate = dateFormat.format(new Date());
			String s = "[PeekTime] " + sTimeStamp + " : " + sCurrDate;

			log(sPath, s);
			sd.setLastTryTime(sTimeStamp);
			sd.increaseTryNumbers();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	// smtp
	public static void doSmtpErrorLog(String sPath, ImSmtp smtp, String sHost, ImSmtpSendData sd) {
		try {
			String sCurrDate = dateFormat.format(new Date());
			String sErr = smtp.getResponse();
			if(StringUtils.isEmpty(sErr) ){
				sErr = smtp.getError();
			}
			String sTimeStamp = Long.toString(System.currentTimeMillis());

			StringBuffer sbErr = new StringBuffer();
			sbErr.append("<<\r\n" + "ErrCode\t= ")
					.append(smtp.getErrorCode())
					.append("\r\nErrString = ");
			if(smtp.getErrorCode() > 900){
				sbErr.append(smtp.getErrorCode()).append(" ");
			}
			sbErr.append(sErr)
					.append(" (Tried: ")
					.append(sCurrDate)
					.append(")")
					.append("\r\nSMAIL SMTP-Send MX = \"")
					.append(sHost)
					.append("\" From = \"")
					.append(sd.getFrom())
					.append("\" To = \"")
					.append(sd.getRcptto())
					.append("\" Failed!\r\n")
					.append(sTimeStamp)
					.append("\r\n>>");
		
			String sErrString = sbErr.toString();


			log(sPath, sErrString);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	public static void doSmailErrorLog(String sPath, ImSmtpSendData sd,String sErr) {
		try {
			String sTimeStamp = Long.toString(System.currentTimeMillis());
			String sCurrDate = dateFormat.format(new Date());

			StringBuffer sbErr = new StringBuffer();
			String sErrString = sbErr.append("<<\r\n" )
					.append("ErrString = ")
					.append(sErr)
					.append(" (Tried: ")
					.append(sCurrDate)
					.append(")")
					.append("\r\n")
					.append("From = \"")
					.append(sd.getFrom())
					.append("\" To = \"")
					.append(sd.getRcptto())
					.append("\" Failed!\r\n")
					.append(sTimeStamp)
					.append("\r\n>>")
					.toString();

			log(sPath, sErrString);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	public static void doSmtpErrorLog(String sPath, String sErrString) {
		try {
			log(sPath, sErrString);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	public static void doSmtpLocalErrorLog(String sPath, String sHost, int nErrCode, String sErr, String sFrom, String sRcpt) {
		try {
			StringBuffer sbErr = new StringBuffer();
			ImStringUtil.replace(sErr, "\r\n", " ");
			ImStringUtil.replace(sErr, "\n", " ");
			String sCurrDate = dateFormat.format(new Date());
			sbErr.append("<<\r\n" + "ErrCode\t= ")
					.append(nErrCode)
					.append("\r\nErrString = ");
			if(nErrCode> 900){
				sbErr.append(nErrCode).append(" ");
			}
			sbErr.append(sErr)
					.append(" (Tried: ")
					.append(sCurrDate)
					.append(")")
					.append("\r\nSMAIL SMTP-Send Local = \"")
					.append(sHost)
					.append("\" From = \"")
					.append(sFrom)
					.append("\" To = \"")
					.append(sRcpt)
					.append("\" Failed!\r\n>>");
		
			String sErrString = sbErr.toString();

			log(sPath, sErrString);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	/**  on|off */
	public static void on() {
		isLog = true;
	}

	public static void off() {
		isLog = false;
	}

}
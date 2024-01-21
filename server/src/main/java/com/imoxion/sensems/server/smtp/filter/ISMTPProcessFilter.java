package com.imoxion.sensems.server.smtp.filter;

import com.imoxion.sensems.server.smtp.ImSmtpSession;

import java.net.Socket;

public interface ISMTPProcessFilter {
	boolean doProcess(ImSmtpSession smtps);
	boolean doProcess(ImSmtpSession smtps, String sCommand);
	void	setParam(String p_sName, String p_sValue);
	void setConfigFile(String p_sConfPath);
	void sendClient(Socket socket, String strMsg);
	String	getError();
	int	getErrorCode();
}

package com.imoxion.sensems.server.util;

import com.imoxion.common.util.ImConfLoaderEx;
import com.imoxion.common.util.ImIpUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class HAProxyService {

	private static Logger logger = LoggerFactory.getLogger(HAProxyService.class);

	private List<String> proxyServers = new ArrayList<>();

	private static HAProxyService proxyService;

	public synchronized static HAProxyService getInstance(){
		if( proxyService == null ){
			proxyService = new HAProxyService();
		}
		return proxyService;
	}

	private HAProxyService(){
		setProxyServers();
	}

	public void setProxyServers(){
		String[] proxyServerList = ImConfLoaderEx.getInstance("sensems.home","sensems.xml").getProfileStringArray("proxy.servers.server");
		if( proxyServerList != null ){
			for(String server : proxyServerList){
				proxyServers.add(server);
				logger.info("proxy server registry : {}",server);
			}
		}
	}

	public boolean isProxyServer(String clientIP){
		boolean isProxyServer = false;
		for(String proxyServerIP : proxyServers) {
			if (ImIpUtil.matchIP(proxyServerIP, clientIP)) {
				 isProxyServer = true;
				break;
			}
		}
		return isProxyServer;
	}

	public boolean isProxyCommand(String command){
		return command.startsWith("PROXY");
	}

	public String getClientIP(String sMsg){
		String[] commands = StringUtils.split(sMsg, " ");
		if (commands.length == 6) {
			return commands[2];
		}else{
			return "";
		}
	}
}
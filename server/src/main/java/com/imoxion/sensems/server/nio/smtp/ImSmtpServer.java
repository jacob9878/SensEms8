package com.imoxion.sensems.server.nio.smtp;


import com.imoxion.sensems.server.beans.ImQueueObj;
import com.imoxion.sensems.server.config.ImServerPolicyConfig;
import com.imoxion.sensems.server.config.ImSmtpConfig;
import com.imoxion.sensems.server.logger.ErrorTraceLogger;
import com.imoxion.sensems.server.nio.define.ServiceType;
import com.imoxion.sensems.server.nio.smtp.initializer.ImSmtpInitializier;
import com.imoxion.sensems.server.nio.smtp.initializer.ImSmtpIspInitializier;
import com.imoxion.sensems.server.nio.smtp.initializer.ImSmtpSslInitializier;
import com.imoxion.sensems.server.nio.ssl.ImSSLContext;
import com.imoxion.sensems.server.service.DnsSearchService;
import com.imoxion.sensems.server.service.ImDkimSigner;
import com.imoxion.sensems.server.service.MessageQueueService;
import com.imoxion.sensems.server.service.TLSFailHostService;
import com.imoxion.sensems.server.smtp.*;
import com.imoxion.sensems.server.util.ImMessQueue;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CancellationException;

@Slf4j
@RequiredArgsConstructor
public class ImSmtpServer {
    private static Logger smtpLogger = LoggerFactory.getLogger("SMTP");
    private static Logger smailLogger = LoggerFactory.getLogger("SMAIL");

    private ChannelFuture closeFuture;
    private final ImSSLContext imSslContext;

    public static String VERSION = "8.0";
    public static String VERSION_DETAIL = "8.0";
    public static String BUILD_DATE = "";

    private static ImSmtpServer sensEmsSmtpServer;

    public synchronized static ImSmtpServer getInstance(){
        if( sensEmsSmtpServer == null ){
            ImSSLContext sslContext = new ImSSLContext();
            sensEmsSmtpServer = new ImSmtpServer(sslContext);
        }
        return sensEmsSmtpServer;
    }

    public synchronized static ImSmtpServer getInstance(ImSSLContext sslContext){
        if( sensEmsSmtpServer == null ){
            sensEmsSmtpServer = new ImSmtpServer(sslContext);
        }
        return sensEmsSmtpServer;
    }

    /**
     * option
     */
    public void start(ServiceType svcType) throws IOException, GeneralSecurityException {

        ImSmtpConfig smtpConfig = ImSmtpConfig.getInstance();

        int defaultConnection = smtpConfig.getDefaultConnection();
        int maxConnection = smtpConfig.getMaxConnection();
        int maxSendTh = smtpConfig.getMaxSendTh();
        int blukThread = smtpConfig.getMaxBlukThread();
        int reSendTh = smtpConfig.getReSendTh();
        int port = ImSmtpConfig.getInstance().getSmtpPortIn();
        //int sslPort = ImSmtpConfig.getInstance().getSslPort();

        if(svcType == ServiceType.SVC_DEFAULT) {
            ImSmtpThreadManager imSmtpThreadManager = ImSmtpThreadManager.getInstance();

        	 // dkim, limit_info, tls_fail_host reload
 			ReloadConfThread rcth = new ReloadConfThread();
 			rcth.setName("ReloadConfTh");
 			rcth.start();
 			imSmtpThreadManager.addThread(ImSmtpThreadManager.ETC, rcth);

            // 송수신제한설정값
            initLimitConfigure(true);
            // DKIM load()는 하지 않음(getInstance할때 한번 load 함)
            ImDkimSigner.getInstance();
           // setConfigurationWatchService();
            //ImDkimSignerByFile.getInstance();

            // dns 서버 초기화
            DnsSearchService.getInstance();

            // 큐 읽어오기
            loadQueue();
            
            // Inbound bulk 발송 쓰레드 수
            MessageQueueService messageQueueService = MessageQueueService.getInstance();
            for (ImQueueObj obj : messageQueueService.getQueueList()) {
                if (obj != null) {
                    int queueIndex = obj.getIndex();
                    for (int j = 0; j < maxSendTh; j++) {
                        Thread th = new ImSendMailThread(queueIndex, obj.getQueue());
                        th.setName("SendTh-" + queueIndex + "-" + j);
                        th.start();
                        imSmtpThreadManager.addThread(ImSmtpThreadManager.SEND, th);
                        smailLogger.info("{} Started", th.getName());
                    }
                }
            }
            
			//smtpLogger.debug("send {} thread init", messageQueueService.getQueueSize());
			
            for (int i = 0; i < blukThread; i++) {
                Thread th = new ImSendMailThread(true);
                th.setName("BulkTh-" + i);
                th.start();
                imSmtpThreadManager.addThread(ImSmtpThreadManager.BULK, th);
                smailLogger.info("{} Started", th.getName());
            }

            for (int i = 0; i < reSendTh; i++) {
                Thread th = new Thread(new ImResendMailThread());
                th.setName("ResendTh-" + i);
                th.start();
                imSmtpThreadManager.addThread(ImSmtpThreadManager.RESEND, th);
                smailLogger.info("{} Started", th.getName());
            }
         	
 			// 망연계용 스풀 디렉토리가 설정되어야 쓰레드를 띄운다.
 			if(StringUtils.isNotEmpty(ImSmtpConfig.getInstance().getNetLinkSpoolPath())) {
 				ImNetlinkSpoolThread nlth = new ImNetlinkSpoolThread(ImSmtpConfig.getInstance().getNetLinkSpoolPath());
 				nlth.setName("NetlinkSpoolTh");
 				nlth.start();
 				imSmtpThreadManager.addThread(ImSmtpThreadManager.ETC, nlth);
 			}

            // From 주소를 체크하는 모니터링 쓰레드를 띄운다.
            ImSmtpFromMonThread smtpFromMonThread = new ImSmtpFromMonThread();
            smtpFromMonThread.start();
            smtpFromMonThread.setName("SmtpFromMonTh");
            imSmtpThreadManager.addThread(ImSmtpThreadManager.ETC, smtpFromMonThread);

            // spool/local 을 체크하는 쓰레드
            ImLocalSpoolThread localSpoolThread = new ImLocalSpoolThread(ImSmtpConfig.getInstance().getQueuePath());
            localSpoolThread.start();
            imSmtpThreadManager.addThread(ImSmtpThreadManager.ETC, localSpoolThread);

            smailLogger.info("Load Thread OK");
        }


//        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
//        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        EventLoopGroup bossGroup;
        EventLoopGroup workerGroup;
        Class clazz;
//        boolean isLinux = false;
//        String os = System.getProperty("os.name");
//        if(os.toLowerCase().indexOf("nux") > -1) {
//            isLinux = true;
//        }
        // workerGroup 쓰레드 수는 지정하지 않으면 cpu core * 2 가 기본
        int thCount = Runtime.getRuntime().availableProcessors() * 2;
        if (Epoll.isAvailable()) {
            smtpLogger.info("Use Epoll");
            bossGroup = new EpollEventLoopGroup(1, new DefaultThreadFactory("bossSmtpGrp", true));
            workerGroup = new EpollEventLoopGroup(thCount, new DefaultThreadFactory("wrkrSmtpGrp", true));

//            bossGroup = new EpollEventLoopGroup(1);
//            workerGroup = new EpollEventLoopGroup();
            clazz = EpollServerSocketChannel.class;
        } else {
            smtpLogger.info("Use Nio");
            bossGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("bossSmtpGrp", true));
            workerGroup = new NioEventLoopGroup(thCount, new DefaultThreadFactory("wrkrSmtpGrp", true));

//            bossGroup = new NioEventLoopGroup(1);
//            workerGroup = new NioEventLoopGroup();
            clazz = NioServerSocketChannel.class;
        }

        // ssl 서비스로 시작하는 경우에는 startTls 옵션이 false임
        SslContext sslContext = null;
        if(svcType == ServiceType.SVC_SSL) {
            sslContext = imSslContext.get(false);
            port = ImSmtpConfig.getInstance().getSslPort();
        } else if(svcType == ServiceType.SVC_ISP) {
            sslContext = imSslContext.get(true);
            port = ImSmtpConfig.getInstance().getIspPort();
        } else {
            sslContext = imSslContext.get(true);
        }

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(clazz)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.SO_BACKLOG, 50)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_LINGER, 0)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
            if(svcType == ServiceType.SVC_SSL) {
                serverBootstrap.childHandler(new ImSmtpSslInitializier(sslContext));
            } else if(svcType == ServiceType.SVC_ISP) {
                serverBootstrap.childHandler(new ImSmtpIspInitializier(sslContext));
            } else {
                serverBootstrap.childHandler(new ImSmtpInitializier(sslContext));
            }

        smtpLogger.info("SMTP Service Start: {}({})", svcType, port);
        try {
            //closeFuture = serverBootstrap.bind("0.0.0.0", port).sync().channel().closeFuture();
            closeFuture = serverBootstrap.bind(port).sync().channel().closeFuture();
            closeFuture.sync();
        } catch(InterruptedException ie){
            String errorId = ErrorTraceLogger.log(ie);
            smtpLogger.error("[{}] - ImSensSmtpServer InterruptedException", errorId);
        } catch(CancellationException ce){
            String errorId = ErrorTraceLogger.log(ce);
            smtpLogger.error("[{}] - ImSensSmtpServer CancellationException", errorId);
        } catch(Exception e){
            String errorId = ErrorTraceLogger.log(e);
            smtpLogger.error("[{}] - ImSensSmtpServer Exception", errorId);
        } finally {
            smtpLogger.info("ImSensSmtpServer shutdown gracefully");
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public void stop(){
        Optional.ofNullable(closeFuture).ifPresent(
            channelFuture -> {
                try {
                    ImSmtpThreadManager imSmtpThreadManager = ImSmtpThreadManager.getInstance();
                    imSmtpThreadManager.interrupt();
                    imSmtpThreadManager.clear();

                    channelFuture.channel().close().sync();

                    smtpLogger.info("SMTP Service Shutdown OK");
                } catch (InterruptedException ie) {
                    String errorId = ErrorTraceLogger.log(ie);
                    smtpLogger.error("[{}] - ImSensSmtpServer stop InterruptedException", errorId);
                }
            }
        );
    }

    public synchronized boolean initLimitConfigure(boolean isFirst){
        boolean bRet = false;
        try{
            ImServerPolicyConfig policyConfig = ImServerPolicyConfig.getInstance();
            if(!isFirst) policyConfig.reload();

            // 1회 발송 최대 수신자수
            int maxRcpts = policyConfig.get("012") != null ? policyConfig.getInt("012") : -1;

            // 메일 한통의 최대 크기
            long maxMsgSize = policyConfig.get("016") != null ? policyConfig.getLong("016") * 1024 * 1024 : -1;
            if(maxMsgSize == 0) maxMsgSize = Integer.MAX_VALUE;

            // maxMsgSize * 수신자 수
            long totMaxMsgSize = policyConfig.get("017") != null ? policyConfig.getLong("017") * 1024 * 1024 : -1;

            ImSmtpConfig m_config = ImSmtpConfig.getInstance();
            if(maxRcpts > 0) m_config.setMaxRcpt(maxRcpts);
            if(maxMsgSize > 0)m_config.setMaxMsgSize(maxMsgSize);
            if(totMaxMsgSize > 0)m_config.setTotMaxMsgSize(totMaxMsgSize);

            //smtpAuthIpFailCount = mapLimitConf.get("018") != null ? Integer.parseInt(mapLimitConf.get("018")) : -1;
            smtpLogger.info("LimitConfigure Reload");
            bRet = true;
        }catch(Exception ex){
            String errorId = ErrorTraceLogger.log(ex);
            smtpLogger.error("{} - [initLimitConfigure] ", errorId );
        }
        return bRet;
    }

    /*public void closeConnection(ChannelHandlerContext ctx, ImSmtpSession smtps){
        String ip = null;

        if(smtps == null) {
            InetSocketAddress isa = (InetSocketAddress) ctx.channel().remoteAddress();
            ip = isa.getAddress().getHostAddress();
        } else {
            ip = smtps.getPeerIP();
        }
        delSmtpConnectServer(ip);

        ctx.close();
    }*/



    
    /**
     *
     * @Method Name  : loadQueue
     * @Method Comment : 메일 큐에 이전에 쌓였던 메일을 올린다.
     *
     */
    private void loadQueue(){
        smailLogger.info( "Load QUEUE");
        ImSmtpConfig m_config = ImSmtpConfig.getInstance();

        String tempPath = m_config.getQueuePath() + File.separator + "temp";
        File tempFile = new File(tempPath);
        if(!tempFile.exists()){
            tempFile.mkdirs();
        }

        MessageQueueService messageQueueService = MessageQueueService.getInstance();
        for(int i=0;i<messageQueueService.getQueueSize();i++){
            ArrayList<?> vLocalMsg = ImMessQueue.loadQueue(m_config.getQueuePath()+ File.separator+"queue"+i);
            ImQueueObj obj = new ImQueueObj();
            obj.setIndex(i);
            messageQueueService.addQueue(obj);

            if(vLocalMsg != null){
                for(int n =0;n<vLocalMsg.size();n++){
                    String svMsg = (String)vLocalMsg.get(n);
                    if(svMsg.indexOf(".body") > 0){
                        continue;
                    }
                    //ImSmtpSendData sd = new ImSmtpSendData(svMsg);
                    //m_bqmsg.enqueue(sd);
                    System.out.println("Load Queue:"+svMsg);
                    obj.getQueue().enqueue(svMsg);

                }

                vLocalMsg.clear();
                vLocalMsg = null;

            }

            // 로컬 재발송 큐 로딩
            ArrayList vMsg = ImMessQueue.loadRsndQueue(m_config.getQueuePath()+ File.separator+"queue"+i);
            if(vMsg != null){
                for(int j =0;j<vMsg.size();j++){
                    String svMsg = (String)vMsg.get(j);
                    if(svMsg.indexOf(".body") > 0){
                        continue;
                    }
                    //ImSmtpSendData sd_rsnd = new ImSmtpSendData(svMsg);
                    //ImResendMailThread.addResendMailQueue(sd_rsnd);
                    ImResendMailThread.addResendMailQueue(svMsg);
                }
                vMsg.clear();
                vMsg = null;
            }
        }
        smailLogger.info( "Load QUEUE OK");

        // SSL 전송이 아닐때만 큐의 메일을 올린다.
        // 대용량 큐 로딩
        ArrayList<?> vInBulkMsg = ImMessQueue.loadQueue(m_config.getQueuePath()+ File.separator+"bulk");
        if(vInBulkMsg != null){
            for(int i =0;i<vInBulkMsg.size();i++){
                String svMsg = (String)vInBulkMsg.get(i);
                if(svMsg.indexOf(".body") > 0){
                    continue;
                }
                //ImSmtpSendData sd = new ImSmtpSendData(svMsg);
                //m_bqmsg.enqueue(sd);
                messageQueueService.addBulkMessageQueue(svMsg);
            }

            vInBulkMsg.clear();
        }
        smailLogger.info( "Load Bulk QUEUE OK");
    }

    private static List<String> loadRelayIpList(String confFilePath) throws Exception{
        XMLConfiguration configuration = new XMLConfiguration(confFilePath);
        List<String> relayIpList = new ArrayList<String>();
        List<HierarchicalConfiguration> serverList = configuration.configurationsAt("relay.server");
        for(HierarchicalConfiguration server : serverList){
            String name = (String)server.getProperty("name");
            String ip = (String) server.getProperty("ip");
            
            relayIpList.add(ip);
        }
        return relayIpList;
    }
	
	private static List<String> loadDenyIpList(String confFilePath) throws Exception{
        XMLConfiguration configuration = new XMLConfiguration(confFilePath);
        List<String> denyIpList = new ArrayList<String>();
        List<HierarchicalConfiguration> serverList = configuration.configurationsAt("deny.server");
        for(HierarchicalConfiguration server : serverList){
            String name = (String)server.getProperty("name");
            String ip = (String) server.getProperty("ip");
            
            denyIpList.add(ip);
        }
        return denyIpList;
    }
    
    private static class ReloadConfThread extends Thread {	

    	public ReloadConfThread(){
    		setDaemon(true);
    	}

    	
    	public void run(){
    		try {
    			int cnt = 0;
    			while(!isInterrupted()) {
                    // 1분간격 체크
                    Thread.sleep(30000);

    			    // imp_limit_info 에서 갱신
    			    ImSmtpServer.getInstance().initLimitConfigure(false);

                    ImDkimSigner.getInstance().load();

    			    // proxydomain
    				/*String proxyPath = SensProxyEnvironment.getSensProxyServerHome()+"/conf/proxydomain.xml";
    				File pfile = new File(proxyPath);
    				if(pfile.exists()) {
    					long ptime = pfile.lastModified();
	    				if(ImSmtpServer.m_lProxyConfTime != ptime) {
	    					ImSmtpConfig.getInstance().setProxyDomainList(ProxyDomainBean.loadProxyDomainList(proxyPath));
	    					smtpLogger.debug("Reload ProxyDomain List: {}", ImSmtpConfig.getInstance().getProxyDomainList().size());
	    					ImSmtpServer.m_lProxyConfTime = ptime;
	    				}
    				}
    				
    				// 사용자(smtp 인증용): 형식 ==> 아이디:AES비번 (pwd_enc.sh로 생성) 
    				Map<String, String> userMap = new HashMap<String, String>();
    				String userPath = SensProxyEnvironment.getSensProxyServerHome()+"/conf/users";
    				File ufile = new File(userPath);
    				if(ufile.exists()) {
	    				long utime = ufile.lastModified();
	    				if(ImSmtpServer.m_lUsersConfTime != utime) {
	    					List<String> listUsers = ImSensProxyUtils.readFileByLine(userPath);
	        				for(String line: listUsers) {
	        					String[] arrUser = line.split(":");
	        					if(arrUser.length > 1) {
	        						userMap.put(arrUser[0].trim(), arrUser[1].trim());
	        					}
	        				}
	    					
	        				ImSmtpConfig.getInstance().setUserMap(userMap);
	    					smtpLogger.debug("Reload User list: {}", userMap.size());
	    					ImSmtpServer.m_lUsersConfTime = utime;
	    				}
    				} else {
    					ufile.createNewFile();
    					ImSmtpConfig.getInstance().setUserMap(userMap);
    					smtpLogger.debug("Reload User list: {}", userMap.size());
    					long utime = ufile.lastModified();
    					ImSmtpServer.m_lUsersConfTime = utime;
    				}
    				
    				// 릴레이 아이피
    				String relayipPath = SensProxyEnvironment.getSensProxyServerHome()+"/conf/relayip.xml";
    				File rfile = new File(relayipPath);
    				long rtime = rfile.lastModified();
    				if(ImSmtpServer.m_lRelayIpConfTime != rtime) {
    					ImSmtpConfig.getInstance().setRelayIpList(loadRelayIpList(relayipPath));
    					smtpLogger.debug("Reload Relay IP list: {}", ImSmtpConfig.getInstance().getRelayIpList().toString());
    					ImSmtpServer.m_lRelayIpConfTime = rtime;
    				}
    				    				
    				// 거부 아이피
    				String denyipPath = SensProxyEnvironment.getSensProxyServerHome()+"/conf/denyip.xml";
    				File dfile = new File(denyipPath);
    				long dtime = dfile.lastModified();
    				if(ImSmtpServer.m_lDenyIpConfTime != dtime) {
    					ImSmtpConfig.getInstance().setDenyIpList(loadDenyIpList(denyipPath));
    					smtpLogger.debug("Reload Denied IP list: {}", ImSmtpConfig.getInstance().getDenyIpList().toString());
    					ImSmtpServer.m_lDenyIpConfTime = dtime;
    				} */
    				
    				// TLS 연결 실패 아이피 목록 관리도 여기서...
    				// 하루 이상 보관중인 아이피는 삭제처리
    				if(cnt > 120) {
	    				TLSFailHostService failTLSHostService = TLSFailHostService.getInstance();
	    				failTLSHostService.cleanMemory();
	    				cnt = 0;
    				}

    				cnt++;
    				// 1분간격 체크
    				//Thread.sleep(60000);
    			}
    					
    		}catch(Exception e) {
    			
    		}
    	}
    	
	}
}

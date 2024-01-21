package com.imoxion.sensems.server.nio.smtp.handler;

import com.imoxion.common.util.ImIpUtil;
import com.imoxion.sensems.server.config.ImSmtpConfig;
import com.imoxion.sensems.server.exception.ImSmtpCloseException;
import com.imoxion.sensems.server.exception.ImSmtpException;
import com.imoxion.sensems.server.logger.ErrorTraceLogger;
import com.imoxion.sensems.server.nio.ImSensSmtpApplication;
import com.imoxion.sensems.server.nio.smtp.ImSmtpProcCmd;
import com.imoxion.sensems.server.service.SmtpConnectService;
import com.imoxion.sensems.server.smtp.ImSmtpSession;
import com.imoxion.sensems.server.util.HAProxyService;
import com.imoxion.sensems.server.util.ImGeoIPLookup2;
import com.imoxion.sensems.server.util.ImSmtpUtil;
import com.imoxion.sensems.server.util.UUIDService;
import com.maxmind.geoip2.record.Country;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.haproxy.HAProxyCommand;
import io.netty.handler.codec.haproxy.HAProxyMessage;
import io.netty.handler.codec.haproxy.HAProxyProxiedProtocol;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.WriteTimeoutException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

@ChannelHandler.Sharable
public class ImSmtpServerHandler extends ChannelInboundHandlerAdapter {
    private Logger smtpLogger = LoggerFactory.getLogger("SMTP");
    private ImSmtpSession smtps;
    private ImSmtpProcCmd processCmd;
    protected static SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
    protected static SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmm");
    private boolean checkConnectedIP = false;

    public ImSmtpSession getSmtpSession(){
        return smtps;
    }

    protected void initSmtpSession(ChannelHandlerContext ctx){
        try{
            smtps.setChannelHandlerContext(ctx);

            InetSocketAddress isa = (InetSocketAddress)ctx.channel().remoteAddress();
            InetSocketAddress isaLoc = (InetSocketAddress)ctx.channel().localAddress();
            String ip = isa.getAddress().getHostAddress();
            //int port = isa.getPort();
            int port = isaLoc.getPort();

            smtps.setPeerIP(ip);
            smtps.setPeerPort(port);

            if(StringUtils.isEmpty(smtps.getTraceID())) {
                smtps.setTraceID(UUIDService.getTraceID());
            }
            smtps.setSmtpState(ImSmtpSession.SMTP_STATE_DEF);
            smtps.setCurrRcpt(0);

            smtps.setTimeStamp("<"+System.currentTimeMillis()+"@"+ ImSmtpConfig.getInstance().getRootDomain()+">");
            Date dt = new Date();
            smtps.setConnDate(sdf.format(dt));
            smtps.setConnDateTime(sdf2.format(dt));
            smtps.setSessionKey(UUIDService.getUID());

            if( StringUtils.isNotEmpty(ip) ){
                try {
                    Country con = ImGeoIPLookup2.getInstance().getCountry(ip);
                    if(con != null) {
                        smtps.setCountry(con.getIsoCode());
                        smtps.setCountry_name(con.getName());
                    } else {
                        if (ImIpUtil.isPublicIP(ip)) {
                            smtps.setCountry("UNKNOWN");
                            smtps.setCountry_name("UNKNOWN");
                        } else {
                            smtps.setCountry("--");
                            smtps.setCountry_name("--");
                        }
                    }
                }catch(Exception e){
                    if (ImIpUtil.isPublicIP(ip)) {
                        smtps.setCountry("UNKNOWN");
                        smtps.setCountry_name("UNKNOWN");
                    } else {
                        smtps.setCountry("--");
                        smtps.setCountry_name("--");
                    }
                }

            }

        }catch(Exception ex){
            String errorId = ErrorTraceLogger.log(ex);
            smtpLogger.error("[{}] {} - [SMTP] InitSmtpSession " , smtps.getTraceID(), errorId );
        }
    }


    public ImSmtpServerHandler(ImSmtpSession smtpSession) {
        this.smtps = smtpSession;
    }

    protected void sendClient(ChannelHandlerContext ctx, String msg){
        ctx.writeAndFlush(msg + "\r\n");
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        /*int maxConnection = ImSmtpConfig.getInstance().getMaxConnection();
        if (ImSmtpServer.activeConnections.get() < maxConnection) {
//            smtpLogger.info("activeConnections.get1() = " + activeConnections.get());
            ImSmtpServer.activeConnections.incrementAndGet();
//            smtpLogger.trace("activeConnections.get2() = " + activeConnections.get());
        }else {
            ImSmtpServer.activeConnections.incrementAndGet();
            ctx.close();
        }
        smtpLogger.info("activeConnections Connected = " + ImSmtpServer.activeConnections.get());*/

       // super.channelRegistered(ctx);

        smtpLogger.trace("[{}] ImSmtpServerHandler channelRegistered", smtps.getTraceID());
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        /*ImSmtpServer.activeConnections.decrementAndGet();
        smtpLogger.info("activeConnections Disconnected = " + ImSmtpServer.activeConnections.get());*/

// 동시접속 수 체크
//        if(ImSmtpConfig.getInstance().getMaxConcurrentConnect() > 0){
//            ImSmtpServer.delSmtpConnectServer(smtps.getPeerIP());
//        }
        SmtpConnectService.getInstance().delSmtpConnectServer(smtps);

        //super.channelUnregistered(ctx);
        smtpLogger.trace("[{}] ImSmtpServerHandler channelUnregistered", smtps.getTraceID());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        System.out.println("ImSmtpServerHandler.channelActive");
        // smtp session 초기화
        initSmtpSession(ctx);
//        System.out.println("ImSmtpServerHandler.channelActive 1");
// 아이피 차단 등... 처리해야 함
        // 동시접속 수 체크
//        if(ImSmtpConfig.getInstance().getMaxConcurrentConnect() > 0){
//            if(!ImSmtpServer.addSmtpConnectServer(smtps.getPeerIP())){
//                //Thread.sleep(ImSmtpConfig.getInstance().getUseTarpit());  // 여긴 바로 끊기
//                smtpLogger.info( "[{}] Too many connections from your IP ({})",smtps.getTraceID(), smtps.getPeerIP());
//                throw new ImSmtpCloseException("421 4.3.0");
//            }
//        }

		//smtps.setAllowIP(true);
        HAProxyService proxyService = HAProxyService.getInstance();
        boolean isHealthCheck = proxyService.isProxyServer(smtps.getPeerIP());
        if(!isHealthCheck){
            smtpLogger.info("[{}] channelActive Connection init {}:{}", smtps.getTraceID(), smtps.getPeerIP(), smtps.getPeerPort());
        }

        String dateNow = sdf.format(new Date());
        String sIntro = "220 "+ ImSmtpConfig.getInstance().getHeloHost()+" ESMTP "+ ImSensSmtpApplication.SMTP_SERVER_VERSION + "; " + dateNow;
        sendClient(ctx, sIntro);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        // 연결 해제
        ctx.close();
        smtpLogger.trace("[{}] Connection closed {}:{}", smtps.getTraceID(), smtps.getPeerIP(), smtps.getPeerPort());
    }

    private void checkConnectIP() throws InterruptedException, SQLException {
        if (this.checkConnectedIP) return;
        this.checkConnectedIP = true;

        if(!SmtpConnectService.getInstance().addSmtpConnectServer(smtps)){
            //Thread.sleep(ImSmtpConfig.getInstance().getUseTarpit());  // 여긴 바로 끊기
            //smtpLogger.info( "[{}] Too many connections from your IP ({})",smtps.getTraceID(), smtps.getPeerIP());
            throw new ImSmtpCloseException("421 4.3.0");
        }

        // 먼저 허용 IP 인지 체크한다.
//        boolean isAllowIP = checkRelayIP();
//        if(!isAllowIP) {
//            Thread.sleep(ImSmtpConfig.getInstance().getUseTarpit());
//            smtpLogger.info( "[{}] Not Allowed Smtp IP ({}).", smtps.getTraceID(), smtps.getPeerIP());
//            throw new ImSmtpCloseException("421 4.3.3", new String[]{smtps.getPeerIP()});
//        }

        // 수신거부 아이피
        if(ImSmtpUtil.isDenyIp(smtps)) {
            // 수신 차단 IP
            Thread.sleep(3000);
            smtpLogger.info( "[{}] Connection refused ({})", smtps.getTraceID(), smtps.getPeerIP());
            throw new ImSmtpCloseException("421 4.3.3", new String[]{smtps.getPeerIP()});
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HAProxyMessage) {
            HAProxyMessage proxyMessage = (HAProxyMessage) msg;

            if(StringUtils.isEmpty(smtps.getTraceID())){
                //initSmtpSession(ctx);
                throw new ImSmtpCloseException("Wrong access attempt");
            }

            String clientIP = smtps.getPeerIP();
            String proxyIP = smtps.getPeerIP();
            if (proxyMessage.command() == HAProxyCommand.PROXY) {
                if (proxyMessage.proxiedProtocol() == HAProxyProxiedProtocol.UNKNOWN) {
                    clientIP = smtps.getPeerIP();
                } else {
                    clientIP = proxyMessage.sourceAddress();
                }
            }

            // String clientIP = proxyMessage.sourceAddress();
            if (StringUtils.isNotEmpty(clientIP)) {
                if (smtps.getPeerPort() == 0) {
                    smtpLogger.trace("[{}][{}:{}] channelRead Health Check IP: {}", smtps.getTraceID(), smtps.getPeerIP(), smtps.getPeerPort(), clientIP);
                    ctx.fireChannelUnregistered();
                    ctx.close();
                    return;
                }

                smtps.setConnectProxy(true);
                smtps.setPeerIP(clientIP);
                //imaps.setPeerPort(message.sourcePort());
                Map<String, String> countryMap = ImSmtpUtil.getIpCountryMap(clientIP);
                smtps.setCountry(countryMap.get("country"));
                smtps.setCountry_name(countryMap.get("country_name"));

                //smtpLogger.info("[{}][{}:{}][{}] channelRead Proxy Client IP: {}", smtps.getTraceID(), proxyIP, smtps.getPeerPort(),  smtps.getCountry(), clientIP);
                smtpLogger.info("[{}] channelRead Proxy Client IP: {}:{}({}) / {}", smtps.getTraceID(), clientIP, smtps.getPeerPort(), smtps.getCountry(), proxyIP);
            }

            //imapLogger.info("HAProxyMessage: {}", clientIP);
            HAProxyService proxyService = HAProxyService.getInstance();
            boolean isHealthCheck = false;

            // proxy ip 와 client ip 가 같은 경우 healthcheck 이다.
            isHealthCheck = proxyService.isProxyServer(clientIP);

            if (isHealthCheck) {
                smtpLogger.debug("[{}] channelRead Health Check IP: {}", smtps.getTraceID(), clientIP);
                ctx.fireChannelInactive();
            }

            checkConnectIP();

            ctx.fireChannelRead(msg);
            //this.channelRead(ctx, msg);
        } else {
            if (!this.checkConnectedIP) checkConnectIP();
            //여기서 명령을 받아서 처리해야 한다.
            byte[] byteCmd = (byte[]) msg;
            //String cmdLine = "";
            String cmdLine = new String(byteCmd);
            //String cmdLine = (String) msg;

            // 명령어 처리해야 함
            ImSmtpProcCmd processCommand = ImSmtpProcCmd.getInstance();
            processCommand.doProcessCommand(ctx, smtps, cmdLine);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
//        super.userEventTriggered(ctx, evt);
        smtpLogger.trace("[{}] ImSmtpServerHandler userEventTriggered", smtps.getTraceID());
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.READER_IDLE ) {
                sendClient(ctx, "Timeout Connection Closed");
                smtpLogger.debug("[{}][{}:{}] userEventTriggered IdleState ReadTimeout Connection closed", smtps.getTraceID(), smtps.getPeerIP(), smtps.getPeerPort());
                ctx.close();
            } else if (e.state() == IdleState.WRITER_IDLE) {
                sendClient(ctx, "Timeout Connection Closed");
                smtpLogger.debug("[{}][{}:{}] userEventTriggered IdleState WriteTimeout Connection closed", smtps.getTraceID(), smtps.getPeerIP(), smtps.getPeerPort());
                ctx.close();
            } else if (e.state() == IdleState.ALL_IDLE) {
                sendClient(ctx, "Timeout Connection Closed");
                smtpLogger.debug("[{}][{}:{}] userEventTriggered IdleState Read/WriteTimeout Connection closed", smtps.getTraceID(), smtps.getPeerIP(), smtps.getPeerPort());
                ctx.close();
            }
        }
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        super.channelWritabilityChanged(ctx);
        smtpLogger.trace("[{}] ImSmtpServerHandler channelWritabilityChanged", smtps.getTraceID());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if( cause instanceof IOException) {
            InetSocketAddress isa = (InetSocketAddress)ctx.channel().remoteAddress();
            String ip = isa.getAddress().getHostAddress();
            HAProxyService proxyService = HAProxyService.getInstance();
            // healthcheck 아이피가 아니면 에러를 기록한다
            if(!proxyService.isProxyServer(ip)){
                String errorId = ErrorTraceLogger.log(cause);
                smtpLogger.error("[{}] {} IOException", smtps.getTraceID(), errorId);
            } else {
                smtpLogger.trace("[{}] IOException - {} ", smtps.getTraceID(), cause.getMessage());
            }
//            String errorId = ErrorTraceLogger.log(cause);
//            smtpLogger.error("[{}] {} IOException", smtps.getTraceID(), errorId);
        } else if( cause instanceof ReadTimeoutException) {
            String errorId = ErrorTraceLogger.log(cause);
            smtpLogger.error("[{}] {} ReadTimeoutException", smtps.getTraceID(), errorId);
            ctx.close();
//            this.channelUnregistered(ctx);
        } else if( cause instanceof WriteTimeoutException) {
            String errorId = ErrorTraceLogger.log(cause);
            smtpLogger.error("[{}] {} WriteTimeoutException", smtps.getTraceID(), errorId);
            ctx.close();
//            this.channelUnregistered(ctx);
        } else if( cause instanceof ImSmtpCloseException) {
            smtpLogger.error("[{}] ImSmtpCloseException, error code : {}, message : {}", smtps.getTraceID(),
                    ( (ImSmtpException) cause ).getErrorCode(), cause.getMessage());
            sendClient(ctx, ((ImSmtpException) cause).getMessage());
            ctx.close();
//            this.channelUnregistered(ctx);
        } else if( cause instanceof ImSmtpException) {
            smtpLogger.error("[{}] ImSmtpException, error code : {}, message : {}", smtps.getTraceID(),
                    ( (ImSmtpException) cause ).getErrorCode(), cause.getMessage());
            sendClient(ctx, ((ImSmtpException) cause).getMessage());
        } else {
            String errorId = ErrorTraceLogger.log(cause);
            smtpLogger.error("[{}] {} exception: {}", smtps.getTraceID(), errorId, cause.getMessage());
            ctx.close();
//            this.channelUnregistered(ctx);
        }
    }
}

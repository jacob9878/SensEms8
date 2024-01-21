package com.imoxion.sensems.server.nio.smtp.handler;

import com.imoxion.sensems.server.config.ImSmtpConfig;
import com.imoxion.sensems.server.nio.smtp.ImSmtpServer;
import com.imoxion.sensems.server.smtp.ImSmtpSession;
import com.imoxion.sensems.server.util.HAProxyService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.ssl.SslHandler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class ImSmtpSslServerHandler extends ImSmtpServerHandler {
    private static Logger smtpLogger = LoggerFactory.getLogger("SMTP");
    private static Logger smailLogger = LoggerFactory.getLogger("SMAIL");

    public ImSmtpSslServerHandler(ImSmtpSession smtpSession) {
        super(smtpSession);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.pipeline().get(SslHandler.class).handshakeFuture().addListener(
                future -> {
                    // smtp session 초기화
                    if(StringUtils.isEmpty(super.getSmtpSession().getTraceID()) || StringUtils.isEmpty(super.getSmtpSession().getPeerIP())) {
                        initSmtpSession(ctx);
                    }

                    // 아이피 차단 등... 처리해야 함

                    // 동시접속 수 체크
//                    if(!ImSmtpServer.addSmtpConnectServer(super.getSmtpSession())){
//                        //Thread.sleep(ImSmtpConfig.getInstance().getUseTarpit());  // 여긴 바로 끊기
//                        //smtpLogger.info( "[{}] Too many connections from your IP ({})",super.getSmtpSession().getTraceID(), super.getSmtpSession().getPeerIP());
//                        throw new ImSmtpCloseException("421 4.3.0");
//                    }
                    /*if(ImSmtpConfig.getInstance().getMaxConcurrentConnect() > 0){
                        if(!ImSmtpServer.addSmtpConnectServer(super.getSmtpSession().getPeerIP())){
                            //Thread.sleep(ImSmtpConfig.getInstance().getUseTarpit());  // 여긴 바로 끊기
                            smtpLogger.info( "[{}] Too many connections from your IP ({})",super.getSmtpSession().getTraceID(), super.getSmtpSession().getPeerIP());
                            sendClient(ctx, new ImSmtpCloseException("421 4.3.0").getMessage());
                            ctx.close();
                        }
                    }*/

                 // 수신거부 아이피
//            		if(ImSmtpUtil.isDenyIp(super.getSmtpSession())) {
//            			// 수신 차단 IP
//            			Thread.sleep(3000);
//            			smtpLogger.info( "Connection refused ("+super.getSmtpSession().getPeerIP()+")");
//                 		throw new ImSmtpCloseException("421 4.3.2", new String[]{super.getSmtpSession().getPeerIP()});
//                 	}
//
//            		super.getSmtpSession().setAllowIP(true);

                    HAProxyService proxyService = HAProxyService.getInstance();
                    boolean isHealthCheck = proxyService.isProxyServer(super.getSmtpSession().getPeerIP());
                    if(!isHealthCheck) {
                        smtpLogger.info("[{}] Connection init {}:{}", super.getSmtpSession().getTraceID(), super.getSmtpSession().getPeerIP(), super.getSmtpSession().getPeerPort());
                    }

                    String dateNow = sdf.format(new Date());
                    String sIntro = "220 "+ ImSmtpConfig.getInstance().getHeloHost()+" ESMTP "+ImSmtpConfig.getInstance().getProductName()+" EmsSmtpServer "+ ImSmtpServer.VERSION + "; " + dateNow;
                    sendClient(ctx, sIntro);

                    super.getSmtpSession().setStartTLS(true);
                }
        );
    }
}

package com.imoxion.sensems.server.nio.smtp.handler;

import com.imoxion.security.ImSecurityLib;
import com.imoxion.sensems.server.domain.ImbUserinfo;
import com.imoxion.sensems.server.exception.ImSmtpException;
import com.imoxion.sensems.server.logger.ErrorTraceLogger;
import com.imoxion.sensems.server.logger.event.TransmitLogger;
import com.imoxion.sensems.server.repository.SmtpRepository;
import com.imoxion.sensems.server.smtp.ImSmtpSession;
import com.imoxion.sensems.server.util.ImSmtpUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.WriteTimeoutException;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@RequiredArgsConstructor
public class ImSmtpAuthHandler extends ChannelInboundHandlerAdapter {
    public static Logger smtpLogger = LoggerFactory.getLogger("SMTP");
    @NonNull @Getter
    private final ImSmtpSession smtps;
    @NonNull @Getter
    private final ChannelHandler channelHandler;
    private boolean isChannelInactive = false;

    protected void revertBaseHandler(ChannelHandlerContext ctx){
        ChannelPipeline cp = ctx.pipeline();
        cp.replace("authhandler", "basehandler", channelHandler);
    }

    private void sendClient(ChannelHandlerContext ctx, Object msg){
        ctx.writeAndFlush((String)msg + "\r\n");
    }
    private void sendClient(ChannelHandlerContext ctx, String msg){
        ctx.writeAndFlush(msg + "\r\n");
    }
    private void sendClient(ChannelHandlerContext ctx, ImSmtpException e){
        sendClient(ctx, e.getMessage());
    }

    protected boolean doAuth(String userid, String passwd){
        TransmitLogger transmitLogger = new TransmitLogger();
    	if(userid.trim().length() <= 0 || passwd.length() <= 0){
    		smtpLogger.error("[{}] [SMTP]  Auth - error (userid or password is empty) " , smtps.getTraceID() );
			return false;
		}
    	
        try{
            SmtpRepository smtpRepository = SmtpRepository.getInstance();
            ImbUserinfo userInfo = smtpRepository.getUserInfo(userid);

            // 사용자가 존재하지 않음
			if(userInfo == null){
				smtpLogger.info("[{}] [SMTP] : Auth - error (userid does not exist)", smtps.getTraceID());
                transmitLogger.setDescription("error.auth_no_user");
                transmitLogger.setAuthid(userid);
                transmitLogger.setTo("-");
                transmitLogger.setResultState(TransmitLogger.STATE_FAIL);
                ImSmtpUtil.doTransmitLog(transmitLogger, smtps);
				return false;
			}

            // smtp 인증권한 없음
            if(userInfo.getUse_smtp() != 1){
                smtpLogger.info("[{}] [SMTP] : Auth - error (userid has no smtp auth right)", smtps.getTraceID());
                transmitLogger.setDescription("error.auth_no_right");
                transmitLogger.setAuthid(userid);
                transmitLogger.setTo("-");
                transmitLogger.setResultState(TransmitLogger.STATE_FAIL);
                ImSmtpUtil.doTransmitLog(transmitLogger, smtps);
                return false;
            }

			String salt = userInfo.getSt_data();
			if(StringUtils.isNotEmpty(salt)){
			    passwd = salt + passwd;
            }

            String pwdType = userInfo.getPwd_type();
			if(StringUtils.isEmpty(pwdType)) pwdType = "sha-256";

            String inPasswd = passwd;
//smtpLogger.info("CMD : Auth - in plain: {}", inPasswd);
            inPasswd = ImSecurityLib.makePassword(pwdType, inPasswd, false);
//smtpLogger.info("CMD : Auth - in: {} / db: {}", inPasswd, smtpAccount.getPasswd());
            // 비밀번호가 맞는지 체크
            if(!inPasswd.equals(userInfo.getPasswd())){
                smtpLogger.info("[{}] [SMTP] : Auth - error (password does not match)", smtps.getTraceID());
                transmitLogger.setDescription("error.auth_incorrect_pwd");
                transmitLogger.setAuthid(userid);
                transmitLogger.setTo("-");
                transmitLogger.setResultState(TransmitLogger.STATE_FAIL);
                ImSmtpUtil.doTransmitLog(transmitLogger, smtps);
                return false;
            }
			
			// 로그인도메인
			smtps.setLogonUser(userid);
			smtps.setLocalUserid(userid);
			smtps.setRelay(true);
			smtps.setLocal(true);
		}catch(Exception ex){    		
    		String errorId = ErrorTraceLogger.log(ex);
    		smtpLogger.error("[{}] {} - [SMTP]  Auth - processing error (" +userid+ ") " , smtps.getTraceID(), errorId );
            return false;
		}

        return true;
    }


    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        smtpLogger.trace("[{}] ImSmtpAuthHandler channelRegistered", smtps.getTraceID());
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        smtpLogger.trace("[{}] ImSmtpAuthHandler channelUnregistered", smtps.getTraceID());
        revertBaseHandler(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        smtpLogger.trace("[{}] ImSmtpAuthHandler channelActive", smtps.getTraceID());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        smtpLogger.trace("[{}] ImSmtpAuthHandler channelInactive", smtps.getTraceID());
        isChannelInactive = true;
    }


    /*@Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        smtpLogger.trace("[{}] ImSmtpAuthHandler channelReadComplete", smtps.getTraceID());
        ctx.flush();
    }*/

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        smtpLogger.trace("[{}] ImSmtpAuthHandler userEventTriggered", smtps.getTraceID());
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
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        smtpLogger.info("[{}] ImSmtpAuthHandler handlerRemoved", smtps.getTraceID());
        // 비정상 종료되었을 경우
        if(isChannelInactive){
            ctx.fireChannelUnregistered();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if( cause instanceof IOException) {
            String errorId = ErrorTraceLogger.log(cause);
            smtpLogger.error("[{}] {} IOException", smtps.getTraceID(), errorId);
            this.channelUnregistered(ctx);
        } else if( cause instanceof ReadTimeoutException) {
            String errorId = ErrorTraceLogger.log(cause);
            smtpLogger.error("[{}] {} ReadTimeoutException", smtps.getTraceID(), errorId);
            this.channelUnregistered(ctx);
            //ImSmtpServer.getInstance().closeConnection(ctx, smtps);
        } else if( cause instanceof WriteTimeoutException) {
            String errorId = ErrorTraceLogger.log(cause);
            smtpLogger.error("[{}] {} WriteTimeoutException", smtps.getTraceID(), errorId);
            this.channelUnregistered(ctx);
            //ImSmtpServer.getInstance().closeConnection(ctx, smtps);
        } else if( cause instanceof ImSmtpException) {
            smtpLogger.error("[{}] ImSmtpException, error code : {}, message : {}", smtps.getTraceID(),
                    ( (ImSmtpException) cause ).getErrorCode(), cause.getMessage());
            sendClient(ctx, ((ImSmtpException) cause).getMessage());
            this.channelUnregistered(ctx);
        } else {
            String errorId = ErrorTraceLogger.log(cause);
            smtpLogger.error("[{}] {} exception: {}", smtps.getTraceID(), errorId, cause.getMessage());
            this.channelUnregistered(ctx);
            //ImSmtpServer.getInstance().closeConnection(ctx, smtps);
        }

    }

}
